package com.locapin.mobile.feature.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AttractionDetailSheetContent(
    name: String,
    description: String?,
    knownFor: String,
    category: String?,
    area: String?,
    distanceText: String,
    onGo: () -> Unit,
    onRefreshDistance: () -> Unit,
    showPermissionAction: Boolean,
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(name, style = MaterialTheme.typography.titleLarge)
        Text(description ?: "No description available.", style = MaterialTheme.typography.bodyMedium)
        category?.takeIf { it.isNotBlank() }?.let {
            Text("Category: $it", style = MaterialTheme.typography.bodySmall)
        }
        area?.takeIf { it.isNotBlank() }?.let {
            Text("Area: $it", style = MaterialTheme.typography.bodySmall)
        }
        Text("Known For", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text(knownFor)
        Text(distanceText, style = MaterialTheme.typography.bodyMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = onGo) { Text("Go") }
            if (showPermissionAction) {
                Button(onClick = onRequestPermission) { Text("Allow GPS") }
            }
            Button(onClick = onRefreshDistance) { Text("Refresh distance") }
        }
    }
}
