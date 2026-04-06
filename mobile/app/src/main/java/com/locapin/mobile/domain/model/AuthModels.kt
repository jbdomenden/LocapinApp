package com.locapin.mobile.domain.model

enum class UserRole {
    ADMIN,
    TOURIST
}

data class AuthSession(
    val isLoggedIn: Boolean,
    val userId: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val token: String
)
