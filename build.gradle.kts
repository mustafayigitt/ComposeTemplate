plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose.compiler) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.dagger.hilt) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false
    // Resolved here but not applied, so that composetemplate.perf can apply it by id only when
    // the generator project exists. Without this line the plugin would not be on the plugin
    // classpath at all once :app stops declaring it.
    alias(libs.plugins.baselineprofile) apply false
    id("composetemplate.validate.secrets")
    id("composetemplate.scaffold.feature")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
