package com.locapin.mobile.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    onOpenEula: () -> Unit = {},
    onOpenTermsConditions: () -> Unit = {},
    onOpenPrivacyConsent: () -> Unit = {}
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)
        Text("Map + location preferences")

        Text("Legal", style = MaterialTheme.typography.titleMedium)
        TextButton(onClick = onOpenEula) { Text("End User License Agreement") }
        TextButton(onClick = onOpenTermsConditions) { Text("Terms and Conditions") }
        TextButton(onClick = onOpenPrivacyConsent) { Text("Privacy and Location Consent") }
    }
}
