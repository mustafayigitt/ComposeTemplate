#include <jni.h>
#include <string>
#include <vector>

/**
 * Decrypts data using the dynamic XOR mask passed from Gradle.
 */
std::string decrypt(std::string data) {
    std::string key = XOR_MASK;
    std::string output = data;
    for (size_t i = 0; i < data.size(); i++) {
        output[i] = data[i] ^ key[i % key.size()];
    }
    return output;
}

/**
 * Robustly checks the app signature hash using JNI.
 */
bool isSignatureValid(JNIEnv* env, jobject context) {
    jclass contextClass = env->GetObjectClass(context);

    // context.getPackageManager()
    jmethodID getPM = env->GetMethodID(contextClass, "getPackageManager", "()Landroid/content/pm/PackageManager;");
    jobject pm = env->CallObjectMethod(context, getPM);

    // context.getPackageName()
    jmethodID getPackageName = env->GetMethodID(contextClass, "getPackageName", "()Ljava/lang/String;");
    jstring packageName = (jstring)env->CallObjectMethod(context, getPackageName);

    // pm.getPackageInfo(packageName, GET_SIGNATURES)
    jclass pmClass = env->GetObjectClass(pm);
    jmethodID getPI = env->GetMethodID(pmClass, "getPackageInfo", "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    jobject pi = env->CallObjectMethod(pm, getPI, packageName, 64); // 64 = GET_SIGNATURES

    jclass piClass = env->GetObjectClass(pi);
    jfieldID signaturesField = env->GetFieldID(piClass, "signatures", "[Landroid/content/pm/Signature;");
    jobjectArray signatures = (jobjectArray)env->GetObjectField(pi, signaturesField);
    jobject firstSignature = env->GetObjectArrayElement(signatures, 0);

    // signature.toByteArray()
    jclass sigClass = env->GetObjectClass(firstSignature);
    jmethodID toByteArray = env->GetMethodID(sigClass, "toByteArray", "()[B");
    jbyteArray certBytes = (jbyteArray)env->CallObjectMethod(firstSignature, toByteArray);

    // MessageDigest.getInstance("SHA-256")
    jclass mdClass = env->FindClass("java/security/MessageDigest");
    jmethodID getInstance = env->GetStaticMethodID(mdClass, "getInstance", "(Ljava/lang/String;)Ljava/security/MessageDigest;");
    jobject md = env->CallStaticObjectMethod(mdClass, getInstance, env->NewStringUTF("SHA-256"));

    jmethodID update = env->GetMethodID(mdClass, "update", "([B)V");
    env->CallVoidMethod(md, update, certBytes);

    jmethodID digest = env->GetMethodID(mdClass, "digest", "()[B");
    jbyteArray hashBytes = (jbyteArray)env->CallObjectMethod(md, digest);

    // Convert to Hex
    jsize length = env->GetArrayLength(hashBytes);
    jbyte* buffer = env->GetByteArrayElements(hashBytes, nullptr);
    char hex[length * 2 + 1];
    for (int i = 0; i < length; i++) {
        sprintf(&hex[i * 2], "%02X", (unsigned char)buffer[i]);
    }
    env->ReleaseByteArrayElements(hashBytes, buffer, JNI_ABORT);

    return (std::string(hex) == EXPECTED_SIGNATURE_HASH);
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_ytapps_composetemplate_core_util_SecretManager_getApiKeyNative(
        JNIEnv* env,
        jobject thiz,
        jobject context,
        jboolean isDebug) {

    // Release modunda imza hash kontrolü yap
    if (!isDebug && !isSignatureValid(env, context)) {
        return env->NewStringUTF("UNAUTHORIZED_ACCESS");
    }

    std::string encrypted_key = isDebug ? API_KEY_DEBUG : API_KEY_RELEASE;
    return env->NewStringUTF(decrypt(encrypted_key).c_str());
}
