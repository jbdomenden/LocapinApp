package com.locapin.mobile.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberDrawerState
import androidx.navigation.compose.rememberNavController
import com.locapin.mobile.core.navigation.Routes
import com.locapin.mobile.feature.auth.EulaScreen
import com.locapin.mobile.feature.auth.ForgotPasswordScreen
import com.locapin.mobile.feature.auth.LoginScreen
import com.locapin.mobile.feature.auth.RegisterScreen
import com.locapin.mobile.feature.auth.TermsScreen
import com.locapin.mobile.feature.destination.DestinationDetailsScreen
import com.locapin.mobile.feature.home.DashboardScreen
import com.locapin.mobile.feature.home.HelpScreen
import com.locapin.mobile.feature.home.HistoryScreen
import com.locapin.mobile.feature.home.OnboardingScreen
import com.locapin.mobile.feature.home.SplashScreen
import com.locapin.mobile.feature.map.MapScreen
import kotlinx.coroutines.launch

private data class DrawerItem(val route: String, val label: String, val icon: ImageVector)

@Composable
fun LocaPinRoot(
    hasLocationPermission: Boolean,
    requestLocationPermission: () -> Unit,
    vm: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val state by vm.state.collectAsStateWithLifecycle()
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentRoute = currentDestination?.route

    LaunchedEffect(state.loading, state.onboarded, state.authed, currentRoute) {
        if (!state.loading) {
            val route = when {
                !state.onboarded -> Routes.Onboarding
                !state.authed -> Routes.Login
                else -> Routes.Dashboard
            }

            if (currentRoute == Routes.Splash) {
                navController.navigate(route) {
                    popUpTo(Routes.Splash) { inclusive = true }
                }
            } else if (currentRoute != route && route == Routes.Dashboard) {
                navController.navigate(route) {
                    popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                }
            } else if (
                currentRoute != route &&
                route != Routes.Dashboard &&
                currentRoute !in setOf(Routes.Register, Routes.ForgotPassword, Routes.Eula, Routes.Terms)
            ) {
                navController.navigate(route) {
                    popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                }
            }
        }
    }

    val shellRoutes = setOf(
        Routes.Dashboard, Routes.Map, Routes.History, Routes.Help
    )
    val showShell = currentRoute in shellRoutes
    val drawerItems = listOf(
        DrawerItem(Routes.Dashboard, "Dashboard", Icons.Default.Home),
        DrawerItem(Routes.Map, "Map", Icons.Default.Map),
        DrawerItem(Routes.History, "History", Icons.Default.History),
        DrawerItem(Routes.Help, "Help", Icons.Default.Help)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showShell,
        drawerContent = {
            if (showShell) {
                ModalDrawerSheet {
                    Text(
                        text = "LocaPin",
                        modifier = Modifier.padding(16.dp)
                    )
                    drawerItems.forEach { item ->
                        NavigationDrawerItem(
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    launchSingleTop = true
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                }
                                scope.launch { drawerState.close() }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) }
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    NavigationDrawerItem(
                        label = { Text("Logout") },
                        selected = false,
                        onClick = {
                            vm.logout()
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Default.Logout, contentDescription = "Logout") }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (showShell) {
                    TopAppBar(
                        title = { Text(drawerItems.firstOrNull { it.route == currentRoute }?.label ?: "LocaPin") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Open menu")
                            }
                        }
                    )
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = Routes.Splash,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                composable(Routes.Splash) { SplashScreen() }
                composable(Routes.Onboarding) {
                    OnboardingScreen(
                        onDone = { vm.completeOnboarding() },
                        onSkip = { vm.completeOnboarding() }
                    )
                }
                composable(Routes.Login) {
                    LoginScreen(
                        onRegister = { navController.navigate(Routes.Register) },
                        onForgotPassword = { navController.navigate(Routes.ForgotPassword) },
                        onSuccess = { navController.navigate(Routes.Dashboard) }
                    )
                }
                composable(Routes.Register) {
                    RegisterScreen(
                        onBack = { navController.popBackStack() },
                        onSuccess = { navController.navigate(Routes.Dashboard) },
                        onOpenEula = { navController.navigate(Routes.Eula) },
                        onOpenTerms = { navController.navigate(Routes.Terms) }
                    )
                }
                composable(Routes.Eula) { EulaScreen(onBack = { navController.popBackStack() }) }
                composable(Routes.Terms) { TermsScreen(onBack = { navController.popBackStack() }) }
                composable(Routes.ForgotPassword) {
                    ForgotPasswordScreen(onBack = { navController.popBackStack() })
                }

                composable(Routes.Dashboard) {
                    DashboardScreen(
                        onMap = { navController.navigate(Routes.Map) },
                        onHistory = { navController.navigate(Routes.History) },
                        onHelp = { navController.navigate(Routes.Help) }
                    )
                }
                composable(Routes.Map) {
                    MapScreen(
                        hasLocationPermission = hasLocationPermission,
                        requestPermission = requestLocationPermission,
                        onDetails = { navController.navigate("${Routes.DestinationDetailsBase}/$it") }
                    )
                }
                composable(Routes.History) { HistoryScreen() }
                composable(Routes.Help) { HelpScreen() }
                composable(Routes.DestinationDetails) { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id").orEmpty()
                    DestinationDetailsScreen(vm, destinationId = id, onBack = { navController.popBackStack() })
                }
            }
        }
    }
}
