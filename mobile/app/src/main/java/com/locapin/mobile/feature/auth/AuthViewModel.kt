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

    fun onLoginIdentifierChange(value: String) {
        _state.update { it.copy(loginIdentifier = value, errorMessage = null) }
    }

    fun onLoginPasswordChange(value: String) {
        _state.update { it.copy(loginPassword = value, errorMessage = null) }
    }

    fun toggleLoginPasswordVisibility() {
        _state.update { it.copy(isLoginPasswordVisible = !it.isLoginPasswordVisible) }
    }

    fun clearLoginIdentifier() {
        _state.update { it.copy(loginIdentifier = "") }
    }

    fun onSignupUsernameChange(value: String) {
        _state.update { it.copy(signupUsername = value, errorMessage = null) }
    }

    fun clearSignupUsername() {
        _state.update { it.copy(signupUsername = "", errorMessage = null) }
    }

    fun onSignupEmailChange(value: String) {
        _state.update { it.copy(signupEmail = value, errorMessage = null) }
    }

    fun clearSignupEmail() {
        _state.update { it.copy(signupEmail = "", errorMessage = null) }
    }

    fun onSignupPasswordChange(value: String) {
        _state.update { it.copy(signupPassword = value, errorMessage = null) }
    }

    fun onSignupConfirmPasswordChange(value: String) {
        _state.update { it.copy(signupConfirmPassword = value, errorMessage = null) }
    }

    fun toggleSignupPasswordVisibility() {
        _state.update { it.copy(isSignupPasswordVisible = !it.isSignupPasswordVisible) }
    }

    fun toggleSignupConfirmPasswordVisibility() {
        _state.update { it.copy(isSignupConfirmPasswordVisible = !it.isSignupConfirmPasswordVisible) }
    }

    fun toggleTermsAcceptance() {
        _state.update { it.copy(hasAcceptedTerms = !it.hasAcceptedTerms, errorMessage = null) }
    }

    fun socialLogin(provider: String, idToken: String? = null, accessToken: String? = null) {
        if (idToken.isNullOrBlank() && accessToken.isNullOrBlank()) {
            _state.update { it.copy(errorMessage = "$provider sign-in returned no usable token.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(socialLoadingProvider = provider, errorMessage = null) }
            val result = authRepository.socialLogin(provider, idToken = idToken, accessToken = accessToken)
            when (result) {
                is LocaPinResult.Success -> {
                    _state.update { it.copy(socialLoadingProvider = null, isAuthenticated = true) }
                }
                is LocaPinResult.Error -> {
                    _state.update { it.copy(socialLoadingProvider = null, errorMessage = result.message) }
                }
                else -> _state.update { it.copy(socialLoadingProvider = null) }
            }
        }
    }

    fun onSocialAuthError(message: String) {
        _state.update { it.copy(errorMessage = message, socialLoadingProvider = null) }
    }

    fun login() {
        val current = _state.value
        if (current.loginIdentifier.isBlank() || current.loginPassword.isBlank()) {
            _state.update { it.copy(errorMessage = "Username/email and password are required.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.login(current.loginIdentifier.trim(), current.loginPassword)
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
        val validationError = validateSignup(current)
        if (validationError != null) {
            _state.update { it.copy(errorMessage = validationError) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.register(
                name = current.signupUsername.trim(),
                email = current.signupEmail.trim(),
                password = current.signupPassword
            )
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
        val email = _state.value.loginIdentifier
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

    private fun validateSignup(state: AuthUiState): String? {
        val email = state.signupEmail.trim()
        return when {
            state.signupUsername.isBlank() -> "Username is required."
            email.isBlank() -> "Email is required."
            !EMAIL_REGEX.matches(email) -> "Please enter a valid email address."
            state.signupPassword.length < 8 -> "Password must be at least 8 characters."
            state.signupConfirmPassword != state.signupPassword -> "Passwords do not match."
            !state.hasAcceptedTerms -> "You must accept the EULA and Terms & Agreement."
            else -> null
        }
    }

    private companion object {
        val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    }
}
