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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.locapin.mobile.core.navigation.AppDestinations
import com.locapin.mobile.core.navigation.RoleResolver
import com.locapin.mobile.feature.admin.AdminDashboardScreen
import com.locapin.mobile.feature.admin.AdminModulePlaceholderScreen
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

    fun logoutToAuth() {
        vm.logout()
        navController.navigate(AppDestinations.Auth) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

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
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route.orEmpty()
            AdminDashboardScreen(
                adminName = session?.name,
                currentRoute = currentRoute,
                onOpenModule = { route ->
                    if (route != AppDestinations.AdminEntry) {
                        navController.navigate(route) { launchSingleTop = true }
                    }
                },
                onChangePassword = { navController.navigate(AppDestinations.AdminChangePassword) },
                onLogout = ::logoutToAuth
            )
        }

        composable(AppDestinations.AdminAttractions) {
            AdminModulePlaceholderScreen(
                title = "Manage Attractions",
                description = "Manage tourist attraction records, content, and visibility.",
                onBack = navController::popBackStack
            )
        }

        composable(AppDestinations.AdminCategories) {
            AdminModulePlaceholderScreen(
                title = "Manage Categories",
                description = "Manage attraction categories and content grouping.",
                onBack = navController::popBackStack
            )
        }

        composable(AppDestinations.AdminMapAreas) {
            AdminModulePlaceholderScreen(
                title = "Manage Map Areas",
                description = "Manage San Juan map areas and attraction mapping.",
                onBack = navController::popBackStack
            )
        }

        composable(AppDestinations.AdminReports) {
            AdminModulePlaceholderScreen(
                title = "Reports",
                description = "View summary insights and future analytics for the mobile admin side.",
                onBack = navController::popBackStack
            )
        }

        composable(AppDestinations.AdminProfile) {
            AdminModulePlaceholderScreen(
                title = "Profile",
                description = "View and manage the current admin account details.",
                onBack = navController::popBackStack
            )
        }

        composable(AppDestinations.AdminSettings) {
            AdminModulePlaceholderScreen(
                title = "Settings",
                description = "Access admin app settings and future configuration options.",
                onBack = navController::popBackStack
            )
        }

        composable(AppDestinations.AdminChangePassword) {
            AdminModulePlaceholderScreen(
                title = "Change Password",
                description = "Securely update admin credentials from this screen in a future phase.",
                onBack = navController::popBackStack
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
