# ============================================================
# ComposeTemplate ProGuard / R8 Rules
# ============================================================

# -----------------------------------------------------------
# Debuggability: Keep line numbers for readable crash traces
# -----------------------------------------------------------
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# -----------------------------------------------------------
# Kotlinx Serialization: Keep generated serializer classes
# Navigation3 and Retrofit deserialize typed models and routes.
# -----------------------------------------------------------
-keep,includedescriptorclasses class com.ytapps.composetemplate.**$$serializer { *; }
-keepclassmembers class com.ytapps.composetemplate.** {
    *** Companion;
}
-keep,includedescriptorclasses class * extends kotlinx.serialization.KSerializer { *; }

# -----------------------------------------------------------
# Retrofit: Keep service interface method signatures
# -----------------------------------------------------------
-keep,allowobfuscation,allowshrinking interface com.ytapps.composetemplate.feature.auth.data.remote.AuthService { *; }
