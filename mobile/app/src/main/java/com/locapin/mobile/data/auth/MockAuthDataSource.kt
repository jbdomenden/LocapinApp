package com.locapin.mobile.data.auth

import com.locapin.mobile.domain.model.AuthRole
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockAuthDataSource @Inject constructor() {
    private val users = listOf(
        MockUser(id = "admin-1", name = "Admin One", email = "admin@locapin.app", password = "Admin123!", role = AuthRole.ADMIN),
        MockUser(id = "tourist-1", name = "Tourist One", email = "tourist@locapin.app", password = "Tourist123!", role = AuthRole.TOURIST),
        MockUser(id = "admin-2", name = "Admin Two", email = "admin2@locapin.app", password = "Admin123!", role = AuthRole.ADMIN),
        MockUser(id = "tourist-2", name = "Tourist Two", email = "tourist2@locapin.app", password = "Tourist123!", role = AuthRole.TOURIST)
    )

    fun findUser(email: String, password: String): MockUser? {
        val normalizedEmail = email.trim().lowercase()
        return users.firstOrNull { it.email == normalizedEmail && it.password == password }
    }
}
