# ============================================================
# ComposeTemplate ProGuard / R8 Rules
# ============================================================

# -----------------------------------------------------------
# Debuggability: Keep line numbers for readable crash traces
# -----------------------------------------------------------
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# -----------------------------------------------------------
# Gson: Keep all network model / DTO classes
# R8 full-mode with proguard-android-optimize.txt can strip
# synthetic constructors that Gson relies on via Unsafe
# -----------------------------------------------------------
-keep class com.ytapps.composetemplate.feature.auth.data.model.** { *; }

# -----------------------------------------------------------
# Kotlinx Serialization: Keep generated serializer classes
# Navigation3 deserializes routes at runtime across module
# boundaries. The compiler plugin adds @Keep to serializers
# but multi-module builds need explicit rules as a safety net.
# -----------------------------------------------------------
-keep,includedescriptorclasses class com.ytapps.composetemplate.**$$serializer { *; }
-keepclassmembers class com.ytapps.composetemplate.** {
    *** Companion;
}
-keep,includedescriptorclasses class * extends kotlinx.serialization.KSerializer { *; }

# -----------------------------------------------------------
# Retrofit: Keep service interface method signatures
# Consumer rules already cover @retrofit2.http.* annotations;
# explicit rule prevents shrinking when interfaces are internal
# -----------------------------------------------------------
-keep,allowobfuscation,allowshrinking interface com.ytapps.composetemplate.feature.auth.data.remote.AuthService { *; }
