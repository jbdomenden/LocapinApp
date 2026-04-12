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
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun onEmailChange(value: String) {
        _state.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _state.update { it.copy(password = value, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onRememberMeChange(value: Boolean) {
        _state.update { it.copy(rememberMe = value) }
    }

    fun forgotPassword() {
        val email = _state.value.email.trim()
        if (email.isBlank()) {
            _state.update { it.copy(errorMessage = "Please enter your email to reset password.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.forgotPassword(email)) {
                is LocaPinResult.Success -> {
                    _state.update { it.copy(isLoading = false, errorMessage = "Reset link sent to your email.") }
                }
                is LocaPinResult.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                else -> {}
            }
        }
    }

    fun consumeLoginResult() {
        _state.update { it.copy(loggedInRole = null) }
    }

    fun loginWithGoogle(idToken: String?) {
        loginWithSocialProvider(provider = "google", idToken = idToken, accessToken = null)
    }

    fun loginWithFacebook(accessToken: String?) {
        loginWithSocialProvider(provider = "facebook", idToken = null, accessToken = accessToken)
    }

    fun onSocialLoginError(message: String) {
        _state.update { it.copy(isLoading = false, errorMessage = message) }
    }

    fun login() {
        val current = _state.value
        if (current.isLoading) return

        val trimmedEmail = current.email.trim()
        val trimmedPassword = current.password.trim()
        if (trimmedEmail.isBlank() && trimmedPassword.isBlank()) {
            _state.update { it.copy(errorMessage = "Please enter your email and password.") }
            return
        }
        if (trimmedEmail.isBlank()) {
            _state.update { it.copy(errorMessage = "Please enter your email.") }
            return
        }
        if (trimmedPassword.isBlank()) {
            _state.update { it.copy(errorMessage = "Please enter your password.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = authRepository.login(trimmedEmail, trimmedPassword)) {
                is LocaPinResult.Success -> {
                    _state.update {
                        it.copy(
                            email = trimmedEmail,
                            isLoading = false,
                            errorMessage = null,
                            loggedInRole = result.data.role
                        )
                    }
                }
                is LocaPinResult.Error -> {
                    _state.update {
                        it.copy(
                            email = trimmedEmail,
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                LocaPinResult.Loading -> {
                    _state.update { it.copy(email = trimmedEmail, isLoading = true) }
                }
            }
        }
    }

    private fun loginWithSocialProvider(provider: String, idToken: String?, accessToken: String?) {
        if (_state.value.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = authRepository.socialLogin(provider, idToken, accessToken)) {
                is LocaPinResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = null,
                            loggedInRole = result.data.role
                        )
                    }
                }
                is LocaPinResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                LocaPinResult.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}
