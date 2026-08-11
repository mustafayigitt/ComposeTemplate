package com.ytapps.composetemplate.core.common

/**
 * Enum representing supported languages in the application.
 */
enum class Language(
    val code: String,
    val displayName: String,
) {
    ENGLISH("en", "English"),
    TURKISH("tr", "Türkçe"),
    ;

    companion object {
        fun fromCode(code: String?): Language = entries.find { it.code == code } ?: ENGLISH
    }
}
