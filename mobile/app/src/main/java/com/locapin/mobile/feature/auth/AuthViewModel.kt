package com.locapin.mobile.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locapin.mobile.core.common.LocaPinResult
import com.locapin.mobile.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
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
        val current = _state.value
        if (current.username.isBlank() || current.password.isBlank()) {
            _state.update { it.copy(errorMessage = "Email and password are required.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.login(current.username, current.password)
            when (result) {
                is LocaPinResult.Success -> {
                    _state.update { it.copy(isLoading = false, isAuthenticated = true) }
                }
                is LocaPinResult.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                else -> {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun register() {
        val current = _state.value
        if (current.username.isBlank() || current.password.isBlank()) {
            _state.update { it.copy(errorMessage = "Email and password are required.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            // Using username as both name and email for registration in this simple flow
            val result = authRepository.register(current.username.split("@")[0], current.username, current.password)
            when (result) {
                is LocaPinResult.Success -> {
                    _state.update { it.copy(isLoading = false, isAuthenticated = true) }
                }
                is LocaPinResult.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                else -> {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun forgotPassword() {
        val email = _state.value.username
        if (email.isBlank()) {
            _state.update { it.copy(errorMessage = "Please enter your email address.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.forgotPassword(email)
            when (result) {
                is LocaPinResult.Success -> {
                    _state.update { it.copy(isLoading = false, errorMessage = "Reset link sent to your email.") }
                }
                is LocaPinResult.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                else -> {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun socialLogin(provider: String) {
        _state.update { it.copy(errorMessage = "$provider login is not yet connected.") }
    }
}
