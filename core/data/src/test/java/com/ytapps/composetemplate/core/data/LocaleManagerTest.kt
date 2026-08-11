package com.ytapps.composetemplate.core.data

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.ytapps.composetemplate.core.common.Language
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class LocaleManagerTest {
    private val preferencesManager = mockk<IPreferencesManager>(relaxed = true)
    private val languageCodeFlow = MutableStateFlow<String?>(null)

    private lateinit var localeManager: LocaleManager

    @Before
    fun setUp() {
        every { preferencesManager.languageCodeFlow } returns languageCodeFlow

        mockkStatic(AppCompatDelegate::class)
        mockkStatic(LocaleListCompat::class)
        every { AppCompatDelegate.setApplicationLocales(any()) } just Runs

        localeManager = LocaleManager(preferencesManager)
    }

    @After
    fun tearDown() {
        unmockkStatic(AppCompatDelegate::class)
        unmockkStatic(LocaleListCompat::class)
    }

    @Test
    fun `given a language when applyLanguage called then application locales are set`() {
        val localeList = mockk<LocaleListCompat>()
        every { LocaleListCompat.forLanguageTags(Language.TURKISH.code) } returns localeList

        localeManager.applyLanguage(Language.TURKISH)

        verify(exactly = 1) { AppCompatDelegate.setApplicationLocales(localeList) }
    }

    @Test
    fun `given a saved language code when restoreSavedLanguage called then that language is applied`() {
        val localeList = mockk<LocaleListCompat>()
        every { LocaleListCompat.forLanguageTags(Language.TURKISH.code) } returns localeList
        languageCodeFlow.value = Language.TURKISH.code

        localeManager.restoreSavedLanguage()

        verify(exactly = 1) { AppCompatDelegate.setApplicationLocales(localeList) }
    }

    @Test
    fun `given no saved language code when restoreSavedLanguage called then no locale is applied`() {
        languageCodeFlow.value = null

        localeManager.restoreSavedLanguage()

        verify(exactly = 0) { AppCompatDelegate.setApplicationLocales(any()) }
    }
}
