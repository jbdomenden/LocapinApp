package com.locapin.mobile.feature.auth

data class AuthUiState(
    val loginIdentifier: String = "",
    val loginPassword: String = "",
    val isLoginPasswordVisible: Boolean = false,
    val signupUsername: String = "",
    val signupEmail: String = "",
    val signupPassword: String = "",
    val signupConfirmPassword: String = "",
    val isSignupPasswordVisible: Boolean = false,
    val isSignupConfirmPasswordVisible: Boolean = false,
    val hasAcceptedTerms: Boolean = false,
    val isLoading: Boolean = false,
    val socialLoadingProvider: String? = null,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false
)
