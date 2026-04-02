package com.locapin.mobile.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.History
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
                items(cards) { card ->
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
fun HistoryScreen() {
    CenterModuleText(title = "History", body = "Your visited attractions will appear here.")
}

@Composable
fun HelpScreen() {
    CenterModuleText(title = "Help", body = "Read quick guides and FAQs for using LocaPin.")
}

@Composable
private fun CenterModuleText(title: String, body: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF7F1))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(body, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF8A6A78))
        }
    }
}
