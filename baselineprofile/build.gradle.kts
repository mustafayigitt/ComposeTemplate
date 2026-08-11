plugins {
    id("composetemplate.baseline.profile.generator")
}

android {
    namespace = "com.ytapps.composetemplate.baselineprofile"

    buildTypes {
        create("benchmark") {
            initWith(getByName("debug"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
        }
    }

    defaultConfig {
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "EMULATOR"
    }
}
