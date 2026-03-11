package com.lhacenmed.budget.ui.page.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state = _state.asStateFlow()

    fun login(email: String, password: String) = viewModelScope.launch {
        _state.value = AuthUiState(isLoading = true)
        runCatching {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
        }.onFailure { _state.value = AuthUiState(error = it.message?.cleanAuthError()) }
            .onSuccess  { _state.value = AuthUiState() }
    }

    fun register(email: String, password: String, displayName: String) = viewModelScope.launch {
        _state.value = AuthUiState(isLoading = true)
        runCatching {
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = kotlinx.serialization.json.buildJsonObject {
                    put("display_name", kotlinx.serialization.json.JsonPrimitive(displayName))
                }
            }
        }.onFailure { _state.value = AuthUiState(error = it.message?.cleanAuthError()) }
            .onSuccess  { _state.value = AuthUiState() }
    }

    fun signOut() = viewModelScope.launch {
        runCatching { supabase.auth.signOut() }
    }

    fun clearError() { _state.value = _state.value.copy(error = null) }

    // Supabase errors are verbose — extract the human-readable part
    private fun String.cleanAuthError(): String = when {
        contains("Invalid login credentials") -> "Invalid email or password."
        contains("User already registered")   -> "This email is already registered."
        contains("Password should be")        -> "Password must be at least 6 characters."
        contains("Unable to validate email")  -> "Please enter a valid email."
        else -> "Something went wrong. Please try again."
    }
}
