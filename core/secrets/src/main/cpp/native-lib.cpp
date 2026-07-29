#include <jni.h>
#include <string>
#include <vector>
#include <cstdlib>
#include <android/log.h>
#include <sys/ptrace.h>
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
    if (ptrace(PTRACE_TRACEME, 0, 1, 0) < 0) {
        return true;
    }
    ptrace(PTRACE_DETACH, 0, 1, 0);
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
 * Robustly checks the app signature hash using JNI.
 */
bool isSignatureValid(JNIEnv* env, jobject context) {
    jclass contextClass = env->GetObjectClass(context);
    jmethodID getPM = env->GetMethodID(contextClass, "getPackageManager", "()Landroid/content/pm/PackageManager;");
    jobject pm = env->CallObjectMethod(context, getPM);

    jmethodID getPackageName = env->GetMethodID(contextClass, "getPackageName", "()Ljava/lang/String;");
    jstring packageName = (jstring)env->CallObjectMethod(context, getPackageName);

    jclass pmClass = env->GetObjectClass(pm);
    jmethodID getPI = env->GetMethodID(pmClass, "getPackageInfo", "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    jobject pi = env->CallObjectMethod(pm, getPI, packageName, 64);

    jclass piClass = env->GetObjectClass(pi);
    jfieldID signaturesField = env->GetFieldID(piClass, "signatures", "[Landroid/content/pm/Signature;");
    jobjectArray signatures = (jobjectArray)env->GetObjectField(pi, signaturesField);
    jobject firstSignature = env->GetObjectArrayElement(signatures, 0);

    jclass sigClass = env->GetObjectClass(firstSignature);
    jmethodID toByteArray = env->GetMethodID(sigClass, "toByteArray", "()[B");
    jbyteArray certBytes = (jbyteArray)env->CallObjectMethod(firstSignature, toByteArray);

    jclass mdClass = env->FindClass("java/security/MessageDigest");
    jmethodID getInstance = env->GetStaticMethodID(mdClass, "getInstance", "(Ljava/lang/String;)Ljava/security/MessageDigest;");
    jobject md = env->CallStaticObjectMethod(mdClass, getInstance, env->NewStringUTF("SHA-256"));

    jmethodID update = env->GetMethodID(mdClass, "update", "([B)V");
    env->CallVoidMethod(md, update, certBytes);

    jmethodID digest = env->GetMethodID(mdClass, "digest", "()[B");
    jbyteArray hashBytes = (jbyteArray)env->CallObjectMethod(md, digest);

    jsize length = env->GetArrayLength(hashBytes);
    jbyte* buffer = env->GetByteArrayElements(hashBytes, nullptr);
    char hex[length * 2 + 1];
    for (int i = 0; i < length; i++) {
        sprintf(&hex[i * 2], "%02X", (unsigned char)buffer[i]);
    }
    env->ReleaseByteArrayElements(hashBytes, buffer, JNI_ABORT);

    std::string actualHash(hex);

    std::string expectedHash;
    for(int i=0; i < EXPECTED_SIGNATURE_HASH_LEN; i++) expectedHash.push_back((char)EXPECTED_SIGNATURE_HASH[i]);

    if (actualHash != expectedHash) {
        LOGE("Signature validation failed!");
        return false;
    }

    return true;
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
    if (!isDebug) {
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

    if (!isDebug) {
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
