package com.ytapps.composetemplate.feature.profile.presentation

import com.google.common.truth.Truth.assertThat
import com.ytapps.composetemplate.core.common.Language
import com.ytapps.composetemplate.core.data.LocaleManager
import com.ytapps.composetemplate.feature.profile.domain.GetLanguageUseCase
import com.ytapps.composetemplate.feature.profile.domain.GetThemeUseCase
import com.ytapps.composetemplate.feature.profile.domain.IProfileRepository
import com.ytapps.composetemplate.feature.profile.domain.LogoutUseCase
import com.ytapps.composetemplate.feature.profile.domain.UpdateLanguageUseCase
import com.ytapps.composetemplate.feature.profile.domain.UpdateThemeUseCase
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class ProfileViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeProfileRepository
    private lateinit var localeManager: LocaleManager

    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeProfileRepository()
        localeManager = mockk(relaxed = true)

        viewModel =
            ProfileViewModel(
                updateTheme = UpdateThemeUseCase(repository),
                getTheme = GetThemeUseCase(repository),
                updateLanguage = UpdateLanguageUseCase(repository),
                getLanguage = GetLanguageUseCase(repository),
                logout = LogoutUseCase(repository),
                localeManager = localeManager,
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given logout succeeds when logout called then navigate to login event sent`() =
        runTest(testDispatcher) {
            viewModel.logout()
            advanceUntilIdle()

            val event = viewModel.events.first()

            assertThat(event).isEqualTo(ProfileEvent.NavigateToLogin)
            assertThat(repository.clearAuthCalled).isTrue()
        }

    @Test
    fun `given language selected when onLanguageSelected called then locale applied`() =
        runTest(testDispatcher) {
            viewModel.onLanguageSelected(Language.TURKISH)
            advanceUntilIdle()

            verify(exactly = 1) { localeManager.applyLanguage(Language.TURKISH) }
        }

    private class FakeProfileRepository : IProfileRepository {
        override val isDarkModeFlow = MutableStateFlow(false)
        override val languageCodeFlow: Flow<String> = MutableStateFlow(Language.ENGLISH.code)
        var clearAuthCalled = false

        override suspend fun setDarkMode(isDarkMode: Boolean) {
            isDarkModeFlow.value = isDarkMode
        }

        override suspend fun setLanguageCode(languageCode: String) = Unit

        override suspend fun clearAuth() {
            clearAuthCalled = true
        }
    }
}
