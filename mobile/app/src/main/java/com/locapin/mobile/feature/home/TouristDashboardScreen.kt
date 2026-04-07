package com.locapin.mobile.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.locapin.mobile.feature.common.ComingSoonScreen
import kotlinx.coroutines.launch

private data class TouristModuleItem(
    val route: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TouristDashboardScreen(
    touristName: String?,
    currentRoute: String,
    mapRoute: String,
    attractionsRoute: String,
    favoritesRoute: String,
    profileRoute: String,
    aboutRoute: String,
    settingsRoute: String,
    changePasswordRoute: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    val modules = listOf(
        TouristModuleItem(mapRoute, "Map", "Explore San Juan City on an interactive map.", Icons.Default.Map),
        TouristModuleItem(attractionsRoute, "Attractions", "Browse curated destinations nearby.", Icons.Default.Explore),
        TouristModuleItem(favoritesRoute, "Favorites", "Access your saved places instantly.", Icons.Default.Favorite),
        TouristModuleItem(profileRoute, "Profile", "View your account and travel profile.", Icons.Default.Person),
        TouristModuleItem(aboutRoute, "About", "Learn more about the LocaPin app.", Icons.Default.Info),
        TouristModuleItem(settingsRoute, "Settings", "Adjust app preferences and controls.", Icons.Default.Settings)
    )
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Tourist Modules",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)
                )
                Text(
                    text = "LocaPin Explorer",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                )
                modules.forEach { module ->
                    NavigationDrawerItem(
                        label = { Text(module.title) },
                        selected = currentRoute == module.route,
                        icon = { Icon(module.icon, contentDescription = module.title) },
                        onClick = {
                            onNavigate(module.route)
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Divider(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    icon = { Icon(Icons.Default.Logout, contentDescription = "Logout") },
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Tourist Dashboard") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Open tourist menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Dashboard actions")
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Change Password") },
                                onClick = {
                                    showMenu = false
                                    onNavigate(changePasswordRoute)
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Welcome${touristName?.let { ", $it" } ?: ""}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = "Explore San Juan City",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(modules) { module ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(156.dp)
                                .clickable { onNavigate(module.route) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = module.icon,
                                    contentDescription = module.title,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(text = module.title, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    text = module.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TouristAboutScreen(onBack: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("About") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "LocaPin", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(
                text = "LocaPin is a San Juan City tourism mobile app.",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Use the map and attractions modules to discover places, save favorites, and plan local trips.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Back",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = onBack)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordPlaceholderScreen(onBack: () -> Unit) {
    ComingSoonScreen(
        title = "Change Password",
        description = "Secure password updates will be available once backend account management is finalized.",
        onBack = onBack
    )
}
