package com.locapin.mobile.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locapin.mobile.core.common.LocaPinResult
import com.locapin.mobile.domain.model.UserRole
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

    fun applyQuickAccount(role: UserRole) {
        when (role) {
            UserRole.ADMIN -> _state.update {
                it.copy(email = "admin@locapin.app", password = "Admin123!", errorMessage = null)
            }
            UserRole.TOURIST -> _state.update {
                it.copy(email = "tourist@locapin.app", password = "Tourist123!", errorMessage = null)
            }
        }
    }

    fun consumeLoginResult() {
        _state.update { it.copy(loggedInRole = null) }
    }

    fun login() {
        val current = _state.value
        if (current.isLoading) return

        val trimmedEmail = current.email.trim()
        if (trimmedEmail.isBlank() || current.password.isBlank()) {
            _state.update { it.copy(errorMessage = "Email and password are required.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = authRepository.login(trimmedEmail, current.password)) {
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
}
