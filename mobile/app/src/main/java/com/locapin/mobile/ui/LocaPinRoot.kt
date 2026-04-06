package com.locapin.mobile.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Attractions
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.locapin.mobile.core.navigation.Routes
import com.locapin.mobile.domain.model.UserRole
import com.locapin.mobile.feature.auth.LoginScreen
import com.locapin.mobile.feature.common.ComingSoonScreen
import kotlinx.coroutines.launch

private data class ModuleItem(val route: String, val title: String, val description: String, val icon: ImageVector)

@Composable
fun LocaPinRoot(
    hasLocationPermission: Boolean,
    requestLocationPermission: () -> Unit,
    vm: RootViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val session by vm.session.collectAsStateWithLifecycle()
    val isReady by vm.isReady.collectAsStateWithLifecycle()

    NavHost(navController = navController, startDestination = Routes.SessionCheck, modifier = Modifier.fillMaxSize()) {
        composable(Routes.SessionCheck) {
            if (!isReady) return@composable
            when (session?.role) {
                UserRole.ADMIN -> navController.navigate(Routes.AdminDashboard) {
                    popUpTo(Routes.SessionCheck) { inclusive = true }
                }
                UserRole.TOURIST -> navController.navigate(Routes.TouristDashboard) {
                    popUpTo(Routes.SessionCheck) { inclusive = true }
                }
                null -> navController.navigate(Routes.Login) {
                    popUpTo(Routes.SessionCheck) { inclusive = true }
                }
            }
        }

        composable(Routes.Login) {
            LoginScreen(
                onForgotPassword = { navController.navigate(Routes.ForgotPassword) },
                onSignUp = { navController.navigate(Routes.SignUp) },
                onRoleResolved = { role ->
                    val destination = if (role == UserRole.ADMIN) Routes.AdminDashboard else Routes.TouristDashboard
                    navController.navigate(destination) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.SignUp) {
            ComingSoonScreen(
                title = "Sign Up",
                description = "Sign up will be enabled after backend account creation is finalized.",
                onBack = navController::popBackStack
            )
        }
        composable(Routes.ForgotPassword) {
            ComingSoonScreen(
                title = "Forgot Password",
                description = "Password reset flow will be connected when auth backend is ready.",
                onBack = navController::popBackStack
            )
        }

        composable(Routes.AdminDashboard) {
            AdminShell(navController = navController, onLogout = {
                vm.logout()
                navController.navigate(Routes.Login) { popUpTo(0) }
            })
        }
        composable(Routes.TouristDashboard) {
            TouristShell(navController = navController, onLogout = {
                vm.logout()
                navController.navigate(Routes.Login) { popUpTo(0) }
            })
        }

        composable(Routes.AdminAttractions) { ComingSoonScreen("Manage Attractions", "Create and manage attractions content from this module.", navController::popBackStack) }
        composable(Routes.AdminCategories) { ComingSoonScreen("Manage Categories", "Maintain attraction categories with clean taxonomy.", navController::popBackStack) }
        composable(Routes.AdminMapAreas) { ComingSoonScreen("Manage Map Areas", "Configure map zones and area boundaries.", navController::popBackStack) }
        composable(Routes.AdminReports) { ComingSoonScreen("Reports", "Review engagement and operational reports here.", navController::popBackStack) }
        composable(Routes.AdminProfile) { ComingSoonScreen("Admin Profile", "Manage profile and contact settings.", navController::popBackStack) }
        composable(Routes.AdminSettings) { ComingSoonScreen("Admin Settings", "Manage app preferences and admin-level options.", navController::popBackStack) }

        composable(Routes.TouristMap) { ComingSoonScreen("Map", "Browse interactive map experiences here.", navController::popBackStack) }
        composable(Routes.TouristAttractions) { ComingSoonScreen("Attractions", "Discover attractions curated for tourists.", navController::popBackStack) }
        composable(Routes.TouristFavorites) { ComingSoonScreen("Favorites", "Review your saved places and quick picks.", navController::popBackStack) }
        composable(Routes.TouristProfile) { ComingSoonScreen("Profile", "Update your personal and travel preferences.", navController::popBackStack) }
        composable(Routes.TouristAbout) { ComingSoonScreen("About", "Learn more about LocaPin mobile experience.", navController::popBackStack) }
        composable(Routes.TouristSettings) { ComingSoonScreen("Settings", "Customize notification and app behavior.", navController::popBackStack) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminShell(navController: NavHostController, onLogout: () -> Unit) {
    val modules = listOf(
        ModuleItem(Routes.AdminDashboard, "Dashboard", "Overview of mobile admin operations.", Icons.Default.Home),
        ModuleItem(Routes.AdminAttractions, "Manage Attractions", "Create and update attractions.", Icons.Default.Attractions),
        ModuleItem(Routes.AdminCategories, "Manage Categories", "Maintain category taxonomy.", Icons.Default.Category),
        ModuleItem(Routes.AdminMapAreas, "Manage Map Areas", "Configure map zones.", Icons.Default.Map),
        ModuleItem(Routes.AdminReports, "Reports", "Review activity and trends.", Icons.Default.Analytics),
        ModuleItem(Routes.AdminProfile, "Profile", "View and edit admin profile.", Icons.Default.Person),
        ModuleItem(Routes.AdminSettings, "Settings", "System and app settings.", Icons.Default.Settings)
    )
    DashboardShell(
        title = "Admin Dashboard",
        modules = modules,
        navController = navController,
        onLogout = onLogout
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TouristShell(navController: NavHostController, onLogout: () -> Unit) {
    val modules = listOf(
        ModuleItem(Routes.TouristMap, "Map", "Explore destinations on map.", Icons.Default.Map),
        ModuleItem(Routes.TouristAttractions, "Attractions", "Browse nearby places.", Icons.Default.Explore),
        ModuleItem(Routes.TouristFavorites, "Favorites", "Access your saved attractions.", Icons.Default.Favorite),
        ModuleItem(Routes.TouristProfile, "Profile", "Manage tourist profile.", Icons.Default.Person),
        ModuleItem(Routes.TouristAbout, "About", "Know more about LocaPin.", Icons.Default.Info),
        ModuleItem(Routes.TouristSettings, "Settings", "App preferences and controls.", Icons.Default.Settings)
    )
    DashboardShell(
        title = "Tourist Dashboard",
        modules = modules,
        navController = navController,
        onLogout = onLogout
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardShell(
    title: String,
    modules: List<ModuleItem>,
    navController: NavHostController,
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
                modules.forEach { module ->
                    NavigationDrawerItem(
                        label = { Text(module.title) },
                        selected = currentRoute == module.route,
                        icon = { Icon(module.icon, contentDescription = module.title) },
                        onClick = {
                            if (module.route != Routes.AdminDashboard && module.route != Routes.TouristDashboard) {
                                navController.navigate(module.route)
                            }
                            scope.launch { drawerState.close() }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Divider(modifier = Modifier.padding(horizontal = 12.dp))
                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    icon = { Icon(Icons.Default.Logout, contentDescription = "Logout") },
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Open menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Change Password") },
                                onClick = {
                                    showMenu = false
                                    navController.navigate(Routes.ForgotPassword)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Logout") },
                                onClick = {
                                    showMenu = false
                                    onLogout()
                                }
                            )
                        }
                    }
                )
            }
        ) { padding ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(modules) { module ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(144.dp)
                            .clickable {
                                if (module.route != Routes.AdminDashboard && module.route != Routes.TouristDashboard) {
                                    navController.navigate(module.route)
                                }
                            },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(module.icon, contentDescription = module.title, tint = MaterialTheme.colorScheme.primary)
                            Text(module.title, style = MaterialTheme.typography.titleMedium)
                            Text(module.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
