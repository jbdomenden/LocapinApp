package com.locapin.mobile.core.navigation

import com.locapin.mobile.domain.model.AuthSession
import com.locapin.mobile.domain.model.UserRole
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoleResolver @Inject constructor() {

    fun resolveDestination(session: AuthSession?): String = when (session?.role) {
        UserRole.ADMIN -> AppDestinations.AdminEntry
        UserRole.TOURIST -> AppDestinations.TouristEntry
        null -> AppDestinations.Auth
    }

    fun resolveDestination(role: UserRole): String = when (role) {
        UserRole.ADMIN -> AppDestinations.AdminEntry
        UserRole.TOURIST -> AppDestinations.TouristEntry
    }
}
