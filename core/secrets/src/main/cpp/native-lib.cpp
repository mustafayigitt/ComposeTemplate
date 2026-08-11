#include <jni.h>
#include <string>
#include <vector>
#include <cstdlib>
#include <algorithm>
#include <cctype>
#include <fstream>
#include <android/log.h>
#include <unistd.h>
#include <sys/system_properties.h>
#include "secrets_generated.h"

#define LOG_TAG "SecretManager-Native"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

/**
 * Checks if a debugger is attached to the process.
 */
bool isDebuggerAttached() {
    std::ifstream statusFile("/proc/self/status");
    std::string line;
    while (std::getline(statusFile, line)) {
        const std::string prefix = "TracerPid:";
        if (line.rfind(prefix, 0) == 0) {
            const std::string tracerPid = line.substr(prefix.length());
            return tracerPid.find_first_not_of(" \t0") != std::string::npos;
        }
    }
    return false;
}

/**
 * Checks if the app is running on an emulator.
 */
bool isEmulator() {
    char model[PROP_VALUE_MAX];
    char hardware[PROP_VALUE_MAX];
    __system_property_get("ro.product.model", model);
    __system_property_get("ro.hardware", hardware);

    std::string sModel(model);
    std::string sHardware(hardware);

    if (sModel.find("sdk") != std::string::npos ||
        sModel.find("Emulator") != std::string::npos ||
        sHardware.find("goldfish") != std::string::npos ||
        sHardware.find("ranchu") != std::string::npos) {
        return true;
    }
    return false;
}

int getSdkInt(JNIEnv* env) {
    jclass versionClass = env->FindClass("android/os/Build$VERSION");
    jfieldID sdkIntField = env->GetStaticFieldID(versionClass, "SDK_INT", "I");
    return env->GetStaticIntField(versionClass, sdkIntField);
}

std::string normalizeHash(std::string hash) {
    hash.erase(std::remove(hash.begin(), hash.end(), ':'), hash.end());
    std::transform(hash.begin(), hash.end(), hash.begin(), ::toupper);
    return hash;
}

/**
 * Decrypts data using the combined mask (STATIC_MASK + CM_PART + runtimeMaskPart).
 */
std::string decrypt(const unsigned char* data, int dataLen, const std::string& runtimeMaskPart) {
    // Reconstruct full mask
    std::string fullMask;
    for(int i=0; i < STATIC_MASK_LEN; i++) fullMask.push_back((char)STATIC_MASK[i]);
    fullMask += CM_PART; // From CMake
    fullMask += runtimeMaskPart;   // From Kotlin

    std::string output;
    for (int i = 0; i < dataLen; i++) {
        output.push_back(data[i] ^ fullMask[i % fullMask.size()]);
    }
    return output;
}

/**
 * Reads app signing certificates using SigningInfo on modern Android and falls back to signatures.
 */
jobjectArray getAppSignatures(JNIEnv* env, jobject context) {
    jclass contextClass = env->GetObjectClass(context);
    jmethodID getPM = env->GetMethodID(contextClass, "getPackageManager", "()Landroid/content/pm/PackageManager;");
    jobject pm = env->CallObjectMethod(context, getPM);

    jmethodID getPackageName = env->GetMethodID(contextClass, "getPackageName", "()Ljava/lang/String;");
    jstring packageName = (jstring)env->CallObjectMethod(context, getPackageName);

    jclass pmClass = env->GetObjectClass(pm);
    jmethodID getPI = env->GetMethodID(pmClass, "getPackageInfo", "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    int flags = getSdkInt(env) >= 28 ? 0x08000000 : 64; // GET_SIGNING_CERTIFICATES : GET_SIGNATURES
    jobject pi = env->CallObjectMethod(pm, getPI, packageName, flags);

    jclass piClass = env->GetObjectClass(pi);

    if (getSdkInt(env) >= 28) {
        jfieldID signingInfoField = env->GetFieldID(piClass, "signingInfo", "Landroid/content/pm/SigningInfo;");
        jobject signingInfo = env->GetObjectField(pi, signingInfoField);
        if (signingInfo != nullptr) {
            jclass signingInfoClass = env->GetObjectClass(signingInfo);
            jmethodID hasMultipleSigners = env->GetMethodID(signingInfoClass, "hasMultipleSigners", "()Z");
            bool multipleSigners = env->CallBooleanMethod(signingInfo, hasMultipleSigners);
            const char* methodName = multipleSigners ? "getApkContentsSigners" : "getSigningCertificateHistory";
            jmethodID getSigners = env->GetMethodID(signingInfoClass, methodName, "()[Landroid/content/pm/Signature;");
            return (jobjectArray)env->CallObjectMethod(signingInfo, getSigners);
        }
    }

    jfieldID signaturesField = env->GetFieldID(piClass, "signatures", "[Landroid/content/pm/Signature;");
    return (jobjectArray)env->GetObjectField(pi, signaturesField);
}

std::string hashSignature(JNIEnv* env, jobject signature) {
    jclass sigClass = env->GetObjectClass(signature);
    jmethodID toByteArray = env->GetMethodID(sigClass, "toByteArray", "()[B");
    jbyteArray certBytes = (jbyteArray)env->CallObjectMethod(signature, toByteArray);


    jclass mdClass = env->FindClass("java/security/MessageDigest");
    jmethodID getInstance = env->GetStaticMethodID(mdClass, "getInstance", "(Ljava/lang/String;)Ljava/security/MessageDigest;");
    jobject md = env->CallStaticObjectMethod(mdClass, getInstance, env->NewStringUTF("SHA-256"));

    jmethodID update = env->GetMethodID(mdClass, "update", "([B)V");
    env->CallVoidMethod(md, update, certBytes);

    jmethodID digest = env->GetMethodID(mdClass, "digest", "()[B");
    jbyteArray hashBytes = (jbyteArray)env->CallObjectMethod(md, digest);

    jsize length = env->GetArrayLength(hashBytes);
    jbyte* buffer = env->GetByteArrayElements(hashBytes, nullptr);
    static const char hexDigits[] = "0123456789ABCDEF";
    std::string hex;
    hex.reserve(length * 2);
    for (int i = 0; i < length; i++) {
        unsigned char byte = (unsigned char)buffer[i];
        hex.push_back(hexDigits[(byte >> 4) & 0x0F]);
        hex.push_back(hexDigits[byte & 0x0F]);
    }
    env->ReleaseByteArrayElements(hashBytes, buffer, JNI_ABORT);
    return hex;
}

/**
 * Robustly checks the app signature hash using JNI.
 */
bool isSignatureValid(JNIEnv* env, jobject context) {
    std::string expectedHash;
    for(int i=0; i < EXPECTED_SIGNATURE_HASH_LEN; i++) expectedHash.push_back((char)EXPECTED_SIGNATURE_HASH[i]);
    expectedHash = normalizeHash(expectedHash);

    jobjectArray signatures = getAppSignatures(env, context);
    if (signatures == nullptr || env->GetArrayLength(signatures) == 0) {
        LOGE("Signature validation failed: no signatures available.");
        return false;
    }

    jsize signatureCount = env->GetArrayLength(signatures);
    for (int i = 0; i < signatureCount; i++) {
        jobject signature = env->GetObjectArrayElement(signatures, i);
        std::string actualHash = normalizeHash(hashSignature(env, signature));
        if (actualHash == expectedHash) {
            return true;
        }
    }

    LOGE("Signature validation failed!");
    return false;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_ytapps_composetemplate_core_secrets_SecretManager_getApiKeyNative(
        JNIEnv* env,
        jobject thiz,
        jobject context,
        jboolean isDebug,
        jstring runtime_mask) {

    const char* mask_part = env->GetStringUTFChars(runtime_mask, nullptr);
    std::string sRuntimeMask(mask_part);
    env->ReleaseStringUTFChars(runtime_mask, mask_part);

    // Production environment checks
    if (!isDebug && NATIVE_RUNTIME_CHECKS_ENABLED) {
        if (isDebuggerAttached() || isEmulator() || !isSignatureValid(env, context)) {
            return env->NewStringUTF("UNAUTHORIZED_ACCESS");
        }
    }

    if (isDebug) {
        return env->NewStringUTF(decrypt(API_KEY_DEBUG, API_KEY_DEBUG_LEN, sRuntimeMask).c_str());
    } else {
        return env->NewStringUTF(decrypt(API_KEY_RELEASE, API_KEY_RELEASE_LEN, sRuntimeMask).c_str());
    }
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_ytapps_composetemplate_core_secrets_SecretManager_getBaseUrlNative(
        JNIEnv* env,
        jobject thiz,
        jobject context,
        jboolean isDebug,
        jstring runtime_mask) {

    const char* mask_part = env->GetStringUTFChars(runtime_mask, nullptr);
    std::string sRuntimeMask(mask_part);
    env->ReleaseStringUTFChars(runtime_mask, mask_part);

    if (!isDebug && NATIVE_RUNTIME_CHECKS_ENABLED) {
        if (isDebuggerAttached() || isEmulator() || !isSignatureValid(env, context)) {
            return env->NewStringUTF("UNAUTHORIZED_ACCESS");
        }
    }

    if (isDebug) {
        return env->NewStringUTF(decrypt(BASE_URL_DEBUG, BASE_URL_DEBUG_LEN, sRuntimeMask).c_str());
    } else {
        return env->NewStringUTF(decrypt(BASE_URL_RELEASE, BASE_URL_RELEASE_LEN, sRuntimeMask).c_str());
    }
}
