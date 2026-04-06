package com.locapin.mobile.data.auth

import com.locapin.mobile.domain.model.AuthRole

data class MockUser(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val role: AuthRole
)
