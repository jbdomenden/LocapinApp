package com.locapin.mobile.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
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
import com.locapin.mobile.feature.explore.ExploreScreen
import com.locapin.mobile.feature.favorites.FavoritesScreen
import com.locapin.mobile.feature.home.ChangePasswordPlaceholderScreen
import com.locapin.mobile.feature.home.TouristAboutScreen
import com.locapin.mobile.feature.home.TouristDashboardScreen
import com.locapin.mobile.feature.map.MapScreen
import com.locapin.mobile.feature.profile.ProfileScreen
import com.locapin.mobile.feature.settings.SettingsScreen

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

    fun logoutAndGoToAuth() {
        vm.logout()
        navController.navigate(AppDestinations.Auth) {
            popUpTo(0)
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
            ComingSoonScreen(
                title = "Admin Entry",
                description = "Admin dashboard is not part of Phase 6 and remains as implemented in Phase 5.",
                onBack = ::logoutAndGoToAuth
            )
        }

        touristGraph(
            navController = navController,
            touristName = session?.name,
            hasLocationPermission = hasLocationPermission,
            requestLocationPermission = requestLocationPermission,
            onLogout = ::logoutAndGoToAuth
        )
    }
}

private fun androidx.navigation.NavGraphBuilder.touristGraph(
    navController: NavHostController,
    touristName: String?,
    hasLocationPermission: Boolean,
    requestLocationPermission: () -> Unit,
    onLogout: () -> Unit
) {
    composable(AppDestinations.TouristEntry) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            ?: AppDestinations.TouristDashboard

        TouristDashboardScreen(
            touristName = touristName,
            currentRoute = currentRoute,
            mapRoute = AppDestinations.TouristMap,
            attractionsRoute = AppDestinations.TouristAttractions,
            favoritesRoute = AppDestinations.TouristFavorites,
            profileRoute = AppDestinations.TouristProfile,
            aboutRoute = AppDestinations.TouristAbout,
            settingsRoute = AppDestinations.TouristSettings,
            changePasswordRoute = AppDestinations.TouristChangePassword,
            onNavigate = { route -> navController.navigate(route) { launchSingleTop = true } },
            onLogout = onLogout
        )
    }

    composable(AppDestinations.TouristMap) {
        MapScreen(
            hasLocationPermission = hasLocationPermission,
            requestPermission = requestLocationPermission
        )
    }
    composable(AppDestinations.TouristAttractions) {
        ExploreScreen(vm = hiltViewModel<MainViewModel>(), onDetails = {})
    }
    composable(AppDestinations.TouristFavorites) {
        FavoritesScreen(vm = hiltViewModel<MainViewModel>(), onDetails = {})
    }
    composable(AppDestinations.TouristProfile) {
        ProfileScreen(
            vm = hiltViewModel<MainViewModel>(),
            onSettings = { navController.navigate(AppDestinations.TouristSettings) }
        )
    }
    composable(AppDestinations.TouristAbout) {
        TouristAboutScreen(onBack = navController::popBackStack)
    }
    composable(AppDestinations.TouristSettings) {
        SettingsScreen()
    }
    composable(AppDestinations.TouristChangePassword) {
        ChangePasswordPlaceholderScreen(onBack = navController::popBackStack)
    }
}
