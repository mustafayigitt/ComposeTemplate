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

            fun toHex(data: String): String {
                return data.toByteArray().joinToString("") { byte -> "%02x".format(byte) }
            }

            fun encryptToHex(data: String): String {
                val input = data.replace("\"", "")
                val bytes = input.toByteArray()
                val maskBytes = mask.toByteArray()
                val encrypted = bytes.mapIndexed { i, byte ->
                    (byte.toInt() xor maskBytes[i % maskBytes.size].toInt()).toByte()
                }
                return encrypted.joinToString("") { byte -> "%02x".format(byte) }
            }

            val apiKeyDebugHex = encryptToHex(secrets.getProperty("API_KEY_DEBUG") ?: "")
            val apiKeyReleaseHex = encryptToHex(secrets.getProperty("API_KEY_RELEASE") ?: "")
            val baseUrlDebugHex = encryptToHex(secrets.getProperty("BASE_URL_DEBUG") ?: "")
            val baseUrlReleaseHex = encryptToHex(secrets.getProperty("BASE_URL_RELEASE") ?: "")
            val maskHex = toHex(mask)
            val expectedSignatureHex = toHex(expectedSignature)

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
                                "-DAPI_KEY_DEBUG=\"$apiKeyDebugHex\"",
                                "-DAPI_KEY_RELEASE=\"$apiKeyReleaseHex\"",
                                "-DBASE_URL_DEBUG=\"$baseUrlDebugHex\"",
                                "-DBASE_URL_RELEASE=\"$baseUrlReleaseHex\"",
                                "-DXOR_MASK=\"$maskHex\"",
                                "-DEXPECTED_SIGNATURE_HASH=\"$expectedSignatureHex\""
                            )
                        }
                    }
                }
            }
        }
    }
}
