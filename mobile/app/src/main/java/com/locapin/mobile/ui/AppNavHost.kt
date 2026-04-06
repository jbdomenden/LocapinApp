package com.locapin.mobile.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.locapin.mobile.core.navigation.AppDestinations
import com.locapin.mobile.core.navigation.RoleResolver
import com.locapin.mobile.feature.auth.LoginScreen
import com.locapin.mobile.feature.common.ComingSoonScreen
import com.locapin.mobile.feature.home.DashboardScreen

@Suppress("UNUSED_PARAMETER")
@Composable
fun AppNavHost(
    hasLocationPermission: Boolean,
    requestLocationPermission: () -> Unit,
    vm: RootViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val roleResolver = remember { RoleResolver() }
    val session = vm.session.collectAsStateWithLifecycle().value
    val isReady = vm.isReady.collectAsStateWithLifecycle().value

    NavHost(
        navController = navController,
        startDestination = AppDestinations.SessionCheck,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(AppDestinations.SessionCheck) {
            LaunchedEffect(isReady, session) {
                if (!isReady) return@LaunchedEffect
                navController.navigate(roleResolver.resolveDestination(session)) {
                    popUpTo(AppDestinations.SessionCheck) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

        composable(AppDestinations.Auth) {
            LoginScreen(
                onForgotPassword = { navController.navigate(AppDestinations.ForgotPassword) },
                onSignUp = { navController.navigate(AppDestinations.SignUp) },
                onRoleResolved = { role ->
                    navController.navigate(roleResolver.resolveDestination(role)) {
                        popUpTo(AppDestinations.Auth) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppDestinations.ForgotPassword) {
            ComingSoonScreen(
                title = "Forgot Password",
                description = "Password reset flow will be connected when auth backend is ready.",
                onBack = navController::popBackStack
            )
        }

        composable(AppDestinations.SignUp) {
            ComingSoonScreen(
                title = "Sign Up",
                description = "Sign up will be enabled after backend account creation is finalized.",
                onBack = navController::popBackStack
            )
        }

        composable(AppDestinations.AdminEntry) {
            ComingSoonScreen(
                title = "Admin Entry",
                description = "Admin dashboard is not part of Phase 4 and will be added later.",
                onBack = {
                    vm.logout()
                    navController.navigate(AppDestinations.Auth) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(AppDestinations.TouristEntry) {
            DashboardScreen(
                onMap = {},
                onHistory = {},
                onHelp = {}
            )
        }
    }
}
