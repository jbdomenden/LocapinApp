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

data class SignUpUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val registeredRole: UserRole? = null
)

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SignUpUiState())
    val state: StateFlow<SignUpUiState> = _state.asStateFlow()

    fun clearRegisteredRole() {
        _state.update { it.copy(registeredRole = null) }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        agreeEula: Boolean,
        agreeTerms: Boolean,
        agreePrivacy: Boolean
    ) {
        if (_state.value.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (
                val result = authRepository.register(
                    name = name,
                    email = email,
                    password = password,
                    agreedToEula = agreeEula,
                    agreedToTerms = agreeTerms,
                    agreedToPrivacy = agreePrivacy
                )
            ) {
                is LocaPinResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = null,
                            registeredRole = result.data.role
                        )
                    }
                }
                is LocaPinResult.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                LocaPinResult.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}
