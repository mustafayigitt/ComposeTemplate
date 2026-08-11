package com.ytapps.composetemplate.convention

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jlleitschuh.gradle.ktlint.KtlintExtension

class StaticAnalysisConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.gitlab.arturbosch.detekt")
            pluginManager.apply("org.jlleitschuh.gradle.ktlint")

            extensions.configure<DetektExtension> {
                toolVersion = libs.findVersion("detekt").get().toString()
                source.setFrom(files("src/main/java", "src/main/kotlin"))
                config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
                buildUponDefaultConfig = true
                allRules = false
                disableDefaultRuleSets = false
                debug = false
                ignoreFailures = false
                autoCorrect = true
            }

            extensions.configure<KtlintExtension> {
                android.set(true)
                ignoreFailures.set(false)
                enableExperimentalRules.set(true)
                reporters {
                    reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
                    reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
                }
            }

            tasks.withType<Detekt>().configureEach {
                reports {
                    html.required.set(true)
                    xml.required.set(true)
                    txt.required.set(true)
                    sarif.required.set(true)
                }
            }
        }
    }
}
