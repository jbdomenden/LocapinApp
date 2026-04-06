package com.locapin.mobile.domain.model

enum class AuthRole {
    ADMIN,
    TOURIST
}

typealias UserRole = AuthRole

data class AuthSession(
    val userId: String,
    val name: String,
    val email: String,
    val role: AuthRole,
    val isLoggedIn: Boolean
)
