# Keep NDK Bridge
-keepclassmembers class **.SecretManager {
    private static native <methods>;
}

# Preserve JNI method names for signature verification
-keep class com.ytapps.composetemplate.core.util.SecretManager {
    native <methods>;
}

# Keep Hilt / Dagger
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# General Compose / Kotlin Serialization rules
-keepattributes *Annotation*, Signature, InnerClasses
-keep @kotlinx.serialization.Serializable class * { *; }
