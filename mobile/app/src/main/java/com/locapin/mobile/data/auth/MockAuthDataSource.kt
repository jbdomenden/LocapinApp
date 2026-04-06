package com.locapin.mobile.data.auth

import com.locapin.mobile.domain.model.UserRole
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockAuthDataSource @Inject constructor() {
    private val accounts = listOf(
        MockAccount("admin-1", "Admin One", "admin@locapin.app", "Admin123!", UserRole.ADMIN),
        MockAccount("tourist-1", "Tourist One", "tourist@locapin.app", "Tourist123!", UserRole.TOURIST),
        MockAccount("admin-2", "Admin Two", "admin2@locapin.app", "Admin123!", UserRole.ADMIN),
        MockAccount("tourist-2", "Tourist Two", "tourist2@locapin.app", "Tourist123!", UserRole.TOURIST)
    )

    fun findAccount(email: String, password: String): MockAccount? {
        val normalized = email.trim().lowercase()
        return accounts.firstOrNull { it.email == normalized && it.password == password }
    }

    data class MockAccount(
        val userId: String,
        val name: String,
        val email: String,
        val password: String,
        val role: UserRole
    )
}
