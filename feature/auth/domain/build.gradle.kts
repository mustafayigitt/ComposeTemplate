plugins {
    id("composetemplate.feature.domain")
}

android {
    namespace = "com.ytapps.composetemplate.feature.auth.domain"
}

dependencies {
    // TEMPORARY probe dependency, reverted in the next commit. A feature may name another
    // feature's navigation module, because a route contract is what a feature publishes.
    // Its domain layer is private, so this edge must be rejected.
    implementation(project(":feature:home:domain"))
}
