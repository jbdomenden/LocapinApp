package com.locapin.mobile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SectorBottomSheet(
    sectorName: String,
    attractionsCount: Int,
    attractions: List<String>,
    onViewAttractions: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = sectorName, style = MaterialTheme.typography.headlineSmall)
        Text(
            text = if (attractionsCount == 1) "1 attraction available" else "$attractionsCount attractions available",
            style = MaterialTheme.typography.bodyMedium
        )

        if (attractions.isEmpty()) {
            Text(
                text = "Attractions for this sector will appear here soon.",
                style = MaterialTheme.typography.bodySmall
            )
        } else {
            attractions.take(4).forEach { item ->
                Text(text = "• $item", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Button(onClick = onViewAttractions, modifier = Modifier.fillMaxWidth()) {
            Text("View Attractions")
        }
    }
}
