package com.locapin.mobile.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private data class DashboardCard(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun DashboardScreen(
    onMap: () -> Unit,
    onHistory: () -> Unit,
    onHelp: () -> Unit
) {
    val cards = listOf(
        DashboardCard("Map", "Open nearby attractions and routing.", Icons.Default.Map, onMap),
        DashboardCard("History", "Review your recently visited attractions.", Icons.Default.History, onHistory),
        DashboardCard("Help", "Learn how to use LocaPin effectively.", Icons.Default.Help, onHelp)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF7F1))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Quick access to your core modules.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF8A6A78)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                gridItems(cards) { card ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        tonalElevation = 2.dp,
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = card.onClick)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(card.icon, contentDescription = card.title, tint = Color(0xFFD06384))
                            Text(card.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(card.subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF7A6670))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HelpScreen() {
    val sections = listOf(
        HelpSection(
            title = "Dashboard",
            icon = Icons.Default.Home,
            points = listOf(
                "After signing in, you land on Dashboard.",
                "Use the cards to jump quickly to Map, History, or Help.",
                "Dashboard is the main post-login starting point."
            )
        ),
        HelpSection(
            title = "Hamburger menu and sidebar",
            icon = Icons.Default.List,
            points = listOf(
                "Tap the hamburger icon in the top-left to open the sidebar.",
                "Use sidebar modules to move between Dashboard, Map, History, and Help.",
                "Logout is at the bottom of the sidebar."
            )
        ),
        HelpSection(
            title = "Map module",
            icon = Icons.Default.Map,
            points = listOf(
                "Open Map from Dashboard card or sidebar.",
                "The map first shows San Juan zones without attraction pins.",
                "Tap a zone to reveal attractions only for that area."
            )
        ),
        HelpSection(
            title = "Selecting areas and pins",
            icon = Icons.Default.TouchApp,
            points = listOf(
                "Tap a highlighted area to filter attractions by that zone.",
                "Tap an attraction pin to open its details sheet.",
                "Use Refresh distance to update distance from your current GPS location."
            )
        ),
        HelpSection(
            title = "Attraction details and Go",
            icon = Icons.Default.Navigation,
            points = listOf(
                "Attraction details show name, description, known-for info, and distance.",
                "Tap Go to start in-app direction routing on the map.",
                "The route is drawn inside LocaPin so you stay in the app."
            )
        ),
        HelpSection(
            title = "History module",
            icon = Icons.Default.History,
            points = listOf(
                "History lists attractions you visited through map interactions.",
                "Tap a history item to reopen the same attraction detail layout.",
                "Use it to quickly revisit places you previously explored."
            )
        ),
        HelpSection(
            title = "Logout",
            icon = Icons.Default.Help,
            points = listOf(
                "Open the sidebar and tap Logout at the bottom.",
                "Your session is cleared and you return to the auth flow."
            )
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF7F1))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "LocaPin User Guide",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                "This guide covers app usage after successful login.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF8A6A78),
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
        }
        items(sections) { section ->
            Surface(shape = RoundedCornerShape(16.dp), tonalElevation = 2.dp) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(section.icon, contentDescription = section.title, tint = Color(0xFFD06384))
                        Text(section.title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                    }
                    section.points.forEach { point ->
                        Text("• $point", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF6E5963))
                    }
                }
            }
        }
    }
}

private data class HelpSection(
    val title: String,
    val icon: ImageVector,
    val points: List<String>
)
