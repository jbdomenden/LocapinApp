package com.locapin.mobile.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun EulaScreen(onBack: () -> Unit) {
    LegalDocumentScreen(
        title = "End User License Agreement",
        content = EULA_CONTENT,
        onBack = onBack
    )
}

@Composable
fun TermsScreen(onBack: () -> Unit) {
    LegalDocumentScreen(
        title = "Terms and Agreement",
        content = TERMS_CONTENT,
        onBack = onBack
    )
}

@Composable
private fun LegalDocumentScreen(
    title: String,
    content: List<LegalSection>,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(title) })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Effective date: April 2, 2026",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            items(content.size) { index ->
                val section = content[index]
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = section.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(text = section.body, style = MaterialTheme.typography.bodyMedium)
                }
            }
            item {
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Text("Back")
                }
            }
        }
    }
}

private data class LegalSection(val title: String, val body: String)

private val EULA_CONTENT = listOf(
    LegalSection(
        title = "1. License Grant",
        body = "LocaPin grants you a limited, non-exclusive, revocable license to install and use the app for personal, non-commercial travel discovery and navigation support."
    ),
    LegalSection(
        title = "2. Location Services",
        body = "LocaPin uses your device location to calculate attraction distance and provide in-app directions. You may disable location permission, but distance calculations and routing quality may be limited or unavailable."
    ),
    LegalSection(
        title = "3. Internet Requirement",
        body = "LocaPin requires internet connectivity to fetch backend-driven attraction data, account status, map overlays, and route information. Offline behavior may be restricted."
    ),
    LegalSection(
        title = "4. Account and Usage Data",
        body = "To provide core functionality, LocaPin may process account identifiers, authentication tokens, destination interactions, and app usage events related to your session and saved preferences."
    ),
    LegalSection(
        title = "5. Restrictions",
        body = "You may not reverse engineer, misuse, disrupt, or attempt unauthorized access to LocaPin services, APIs, or related systems."
    ),
    LegalSection(
        title = "6. Disclaimer and Liability",
        body = "LocaPin is provided as-is. Map routes, travel times, and attraction details are best-effort and may change. Use judgment when traveling and follow local laws and safety guidance."
    )
)

private val TERMS_CONTENT = listOf(
    LegalSection(
        title = "1. Service Scope",
        body = "LocaPin helps users discover attractions, view map information, and navigate with in-app routing features driven by backend-managed content."
    ),
    LegalSection(
        title = "2. Location and Directions Disclosure",
        body = "By using LocaPin, you acknowledge that location access is used to compute real-time distance and provide in-app directions to attractions."
    ),
    LegalSection(
        title = "3. Connectivity and Backend Data",
        body = "Internet access is required for authentication, profile retrieval, destination listings, map content, and other backend-supported features."
    ),
    LegalSection(
        title = "4. Data Processing",
        body = "LocaPin may process data relevant to account security, login sessions, attraction history, and app usage to deliver and improve service functionality."
    ),
    LegalSection(
        title = "5. User Responsibilities",
        body = "You are responsible for accurate account information, secure credentials, and lawful use of location and map features."
    ),
    LegalSection(
        title = "6. Changes and Termination",
        body = "These terms may be updated over time. Continued use after updates means acceptance of revised terms. Access may be suspended for policy or security violations."
    )
)
