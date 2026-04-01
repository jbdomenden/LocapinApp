package com.locapin.mobile.feature.auth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun onUsernameChange(value: String) {
        _state.update { it.copy(username = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _state.update { it.copy(password = value, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun clearUsername() {
        _state.update { it.copy(username = "") }
    }

    fun login() {
        submitAuth()
    }

    fun register() {
        submitAuth()
    }

    fun forgotPassword() {
        _state.update { it.copy(errorMessage = "Forgot password flow is not yet connected.") }
    }

    fun socialLogin(provider: String) {
        _state.update { it.copy(errorMessage = "$provider login is not yet connected.") }
    }

    private fun submitAuth() {
        val current = _state.value
        if (current.username.isBlank() || current.password.isBlank()) {
            _state.update { it.copy(errorMessage = "Username and password are required.") }
            return
        }
        _state.update { it.copy(isLoading = false, isAuthenticated = true, errorMessage = null) }
    }
}
