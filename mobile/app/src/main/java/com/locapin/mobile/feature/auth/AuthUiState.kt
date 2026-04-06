package com.locapin.mobile.feature.auth

import com.locapin.mobile.domain.model.UserRole

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loggedInRole: UserRole? = null
)
