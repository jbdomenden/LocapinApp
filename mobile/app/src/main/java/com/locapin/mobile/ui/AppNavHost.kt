package com.locapin.mobile.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.locapin.mobile.core.navigation.AppDestinations
import com.locapin.mobile.domain.model.AuthSession
import com.locapin.mobile.core.navigation.RoleResolver
import com.locapin.mobile.feature.admin.AdminDashboardScreen
import com.locapin.mobile.feature.admin.AdminAttractionFormScreen
import com.locapin.mobile.feature.admin.AdminAttractionsListScreen
import com.locapin.mobile.feature.admin.AdminCategoriesListScreen
import com.locapin.mobile.feature.admin.AdminCategoryFormScreen
import com.locapin.mobile.feature.admin.AdminModulePlaceholderScreen
import com.locapin.mobile.feature.admin.AdminMapAreaFormScreen
import com.locapin.mobile.feature.admin.AdminMapAreasListScreen
import com.locapin.mobile.feature.auth.EulaScreen
import com.locapin.mobile.feature.auth.LoginScreen
import com.locapin.mobile.feature.auth.PrivacyLocationConsentScreen
import com.locapin.mobile.feature.auth.SignUpScreen
import com.locapin.mobile.feature.auth.TermsConditionsScreen
import com.locapin.mobile.feature.common.ComingSoonScreen
import com.locapin.mobile.feature.destination.DestinationDetailsScreen
import com.locapin.mobile.feature.explore.ExploreScreen
import com.locapin.mobile.feature.favorites.FavoritesScreen
import com.locapin.mobile.feature.home.TouristAboutScreen
import com.locapin.mobile.feature.home.TouristDashboardScreen
import com.locapin.mobile.feature.map.LocationPermissionUiState
import com.locapin.mobile.feature.map.MapScreen
import com.locapin.mobile.feature.profile.ChangePasswordScreen
import com.locapin.mobile.feature.profile.ProfileScreen
import com.locapin.mobile.feature.settings.SettingsScreen

@Suppress("UNUSED_PARAMETER")
@Composable
fun AppNavHost(
    locationPermissionUiState: LocationPermissionUiState,
    requestLocationPermission: () -> Unit,
    openAppSettings: () -> Unit,
    vm: RootViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val roleResolver = remember { RoleResolver() }
    val session = vm.session.collectAsStateWithLifecycle().value
    val isReady = vm.isReady.collectAsStateWithLifecycle().value

    fun logoutAndGoToAuth() {
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
            SessionCheckScreen()

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
                        popUpTo(navController.graph.id) { inclusive = true }
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
            SignUpScreen(
                onBack = navController::popBackStack,
                onOpenEula = { navController.navigate(AppDestinations.Eula) },
                onOpenTerms = { navController.navigate(AppDestinations.TermsConditions) },
                onOpenPrivacyConsent = { navController.navigate(AppDestinations.PrivacyLocationConsent) }
            )
        }

        composable(AppDestinations.Eula) {
            EulaScreen(onBack = navController::popBackStack)
        }

        composable(AppDestinations.TermsConditions) {
            TermsConditionsScreen(onBack = navController::popBackStack)
        }

        composable(AppDestinations.PrivacyLocationConsent) {
            PrivacyLocationConsentScreen(onBack = navController::popBackStack)
        }

        adminGraph(
            navController = navController,
            adminName = session?.name,
            session = session,
            onLogout = ::logoutAndGoToAuth
        )

        touristGraph(
            navController = navController,
            touristName = session?.name,
            session = session,
            locationPermissionUiState = locationPermissionUiState,
            requestLocationPermission = requestLocationPermission,
            openAppSettings = openAppSettings,
            onLogout = ::logoutAndGoToAuth
        )
    }
}

@Composable
private fun SessionCheckScreen() {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Text(
                text = "Checking session…",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun NavGraphBuilder.adminGraph(
    navController: NavHostController,
    adminName: String?,
    session: AuthSession?,
    onLogout: () -> Unit
) {
    composable(AppDestinations.AdminEntry) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            ?: AppDestinations.AdminEntry

        AdminDashboardScreen(
            adminName = adminName,
            currentRoute = currentRoute,
            onOpenModule = { route -> navController.navigate(route) { launchSingleTop = true } },
            onChangePassword = { navController.navigate(AppDestinations.AdminChangePassword) },
            onLogout = onLogout
        )
    }
    composable(AppDestinations.AdminAttractions) {
        AdminAttractionsListScreen(
            onBack = navController::popBackStack,
            onCreateAttraction = { navController.navigate(AppDestinations.AdminAttractionCreate) },
            onEditAttraction = { id -> navController.navigate(AppDestinations.adminAttractionEdit(id)) }
        )
    }
    composable(AppDestinations.AdminAttractionCreate) {
        AdminAttractionFormScreen(onBack = navController::popBackStack)
    }
    composable(
        route = AppDestinations.AdminAttractionEdit,
        arguments = listOf(navArgument("attractionId") { type = NavType.StringType })
    ) {
        AdminAttractionFormScreen(onBack = navController::popBackStack)
    }
    composable(AppDestinations.AdminCategories) {
        AdminCategoriesListScreen(
            onBack = navController::popBackStack,
            onCreateCategory = { navController.navigate(AppDestinations.AdminCategoryCreate) },
            onEditCategory = { id -> navController.navigate(AppDestinations.adminCategoryEdit(id)) }
        )
    }
    composable(AppDestinations.AdminCategoryCreate) {
        AdminCategoryFormScreen(onBack = navController::popBackStack)
    }
    composable(
        route = AppDestinations.AdminCategoryEdit,
        arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
    ) {
        AdminCategoryFormScreen(onBack = navController::popBackStack)
    }
    composable(AppDestinations.AdminMapAreas) {
        AdminMapAreasListScreen(
            onBack = navController::popBackStack,
            onCreateMapArea = { navController.navigate(AppDestinations.AdminMapAreaCreate) },
            onEditMapArea = { id -> navController.navigate(AppDestinations.adminMapAreaEdit(id)) }
        )
    }
    composable(AppDestinations.AdminMapAreaCreate) {
        AdminMapAreaFormScreen(onBack = navController::popBackStack)
    }
    composable(
        route = AppDestinations.AdminMapAreaEdit,
        arguments = listOf(navArgument("mapAreaId") { type = NavType.StringType })
    ) {
        AdminMapAreaFormScreen(onBack = navController::popBackStack)
    }
    composable(AppDestinations.AdminReports) {
        AdminModulePlaceholderScreen(
            title = "Reports",
            description = "Operational analytics and summary reports are coming soon.",
            onBack = navController::popBackStack
        )
    }
    composable(AppDestinations.AdminProfile) {
        ProfileScreen(
            name = session?.name ?: "Guest",
            email = session?.email ?: "No email available",
            role = session?.role?.name ?: "Unknown",
            onBack = navController::popBackStack
        )
    }
    composable(AppDestinations.AdminSettings) {
        AdminModulePlaceholderScreen(
            title = "Settings",
            description = "Admin configuration settings are not yet connected in mock mode.",
            onBack = navController::popBackStack
        )
    }
    composable(AppDestinations.AdminChangePassword) {
        ChangePasswordScreen(onBack = navController::popBackStack)
    }
}

private fun NavGraphBuilder.touristGraph(
    navController: NavHostController,
    touristName: String?,
    session: AuthSession?,
    locationPermissionUiState: LocationPermissionUiState,
    requestLocationPermission: () -> Unit,
    openAppSettings: () -> Unit,
    onLogout: () -> Unit
) {
    composable(AppDestinations.TouristEntry) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            ?: AppDestinations.TouristEntry

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
            permissionUiState = locationPermissionUiState,
            requestPermission = requestLocationPermission,
            openAppSettings = openAppSettings
        )
    }
    composable(AppDestinations.TouristAttractions) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(AppDestinations.TouristEntry)
        }
        val mainViewModel = hiltViewModel<MainViewModel>(parentEntry)
        ExploreScreen(vm = mainViewModel, onDetails = {})
    }
    composable(AppDestinations.TouristFavorites) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(AppDestinations.TouristEntry)
        }
        val mainViewModel = hiltViewModel<MainViewModel>(parentEntry)
        FavoritesScreen(
            vm = mainViewModel,
            onDetails = { attractionId ->
                navController.navigate(AppDestinations.touristAttractionDetails(attractionId))
            }
        )
    }
    composable(
        route = AppDestinations.TouristAttractionDetails,
        arguments = listOf(navArgument("attractionId") { type = NavType.StringType })
    ) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            navController.getBackStackEntry(AppDestinations.TouristEntry)
        }
        val mainViewModel = hiltViewModel<MainViewModel>(parentEntry)
        val attractionId = backStackEntry.arguments?.getString("attractionId").orEmpty()
        DestinationDetailsScreen(
            vm = mainViewModel,
            destinationId = attractionId,
            onBack = navController::popBackStack
        )
    }
    composable(AppDestinations.TouristProfile) {
        ProfileScreen(
            name = touristName ?: "Guest",
            email = session?.email ?: "No email available",
            role = session?.role?.name ?: "Unknown",
            onBack = navController::popBackStack
        )
    }
    composable(AppDestinations.TouristAbout) {
        TouristAboutScreen(onBack = navController::popBackStack)
    }
    composable(AppDestinations.TouristSettings) {
        SettingsScreen(
            onOpenEula = { navController.navigate(AppDestinations.Eula) },
            onOpenTermsConditions = { navController.navigate(AppDestinations.TermsConditions) },
            onOpenPrivacyConsent = { navController.navigate(AppDestinations.PrivacyLocationConsent) }
        )
    }
    composable(AppDestinations.TouristChangePassword) {
        ChangePasswordScreen(onBack = navController::popBackStack)
    }
}
