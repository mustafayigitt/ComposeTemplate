package com.ytapps.composetemplate.convention

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryNativeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val mask = secrets.getProperty("XOR_MASK")?.replace("\"", "") ?: "DEFAULT_MASK"
            val expectedSignature = secrets.getProperty("EXPECTED_SIGNATURE_HASH")?.replace("\"", "") ?: ""

            fun encrypt(data: String): String {
                val input = data.replace("\"", "")
                val output = StringBuilder()
                for (i in input.indices) {
                    output.append((input[i].code xor mask[i % mask.length].code).toChar())
                }
                return output.toString()
            }

            val apiKeyDebug = encrypt(secrets.getProperty("API_KEY_DEBUG") ?: "")
            val apiKeyRelease = encrypt(secrets.getProperty("API_KEY_RELEASE") ?: "")

            extensions.configure<LibraryExtension> {
                ndkVersion = libs.findVersion("ndk").get().toString()

                externalNativeBuild {
                    cmake {
                        path = file("src/main/cpp/CMakeLists.txt")
                    }
                }

                defaultConfig {
                    externalNativeBuild {
                        cmake {
                            cppFlags("")
                            arguments(
                                "-DAPI_KEY_DEBUG=$apiKeyDebug",
                                "-DAPI_KEY_RELEASE=$apiKeyRelease",
                                "-DXOR_MASK=$mask",
                                "-DEXPECTED_SIGNATURE_HASH=$expectedSignature"
                            )
                        }
                    }
                }
            }
        }
    }
}
