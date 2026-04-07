package com.locapin.mobile.feature.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Home
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
import com.locapin.mobile.core.navigation.AppDestinations
import kotlinx.coroutines.launch

private data class AdminModuleItem(
    val route: String,
    val title: String,
    val description: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    adminName: String?,
    currentRoute: String,
    onOpenModule: (String) -> Unit,
    onChangePassword: () -> Unit,
    onLogout: () -> Unit
) {
    val modules = remember {
        listOf(
            AdminModuleItem(
                route = AppDestinations.AdminEntry,
                title = "Dashboard",
                description = "Overview of mobile admin operations.",
                icon = Icons.Default.Home
            ),
            AdminModuleItem(
                route = AppDestinations.AdminAttractions,
                title = "Manage Attractions",
                description = "Manage tourist attraction records and content.",
                icon = Icons.Default.Attractions
            ),
            AdminModuleItem(
                route = AppDestinations.AdminCategories,
                title = "Manage Categories",
                description = "Manage attraction categories and grouping.",
                icon = Icons.Default.Category
            ),
            AdminModuleItem(
                route = AppDestinations.AdminMapAreas,
                title = "Manage Map Areas",
                description = "Manage San Juan map areas and mapping.",
                icon = Icons.Default.Map
            ),
            AdminModuleItem(
                route = AppDestinations.AdminReports,
                title = "Reports",
                description = "View summary insights and future analytics.",
                icon = Icons.Default.Analytics
            ),
            AdminModuleItem(
                route = AppDestinations.AdminProfile,
                title = "Profile",
                description = "View and manage the admin account.",
                icon = Icons.Default.Person
            ),
            AdminModuleItem(
                route = AppDestinations.AdminSettings,
                title = "Settings",
                description = "Access admin settings and configuration.",
                icon = Icons.Default.Settings
            )
        )
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Admin Modules",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)
                )
                Text(
                    text = "LocaPin Control Panel",
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
                            onOpenModule(module.route)
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
                    title = { Text("Admin Dashboard") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Open admin menu")
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
                                    onChangePassword()
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
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Welcome${adminName?.let { ", $it" } ?: ""}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Mobile admin control center.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(modules) { module ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(156.dp)
                                .clickable { onOpenModule(module.route) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row {
                                    Icon(
                                        imageVector = module.icon,
                                        contentDescription = module.title,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(text = module.title, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        text = module.description,
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
}
