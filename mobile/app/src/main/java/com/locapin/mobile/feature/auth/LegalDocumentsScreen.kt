package com.locapin.mobile.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
        sections = EULA_CONTENT,
        onBack = onBack
    )
}

@Composable
fun TermsConditionsScreen(onBack: () -> Unit) {
    LegalDocumentScreen(
        title = "Terms and Conditions",
        sections = TERMS_CONTENT,
        onBack = onBack
    )
}

@Composable
fun PrivacyLocationConsentScreen(onBack: () -> Unit) {
    LegalDocumentScreen(
        title = "Privacy and Location Consent",
        sections = PRIVACY_CONTENT,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LegalDocumentScreen(
    title: String,
    sections: List<LegalSection>,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sections.size) { index ->
                val section = sections[index]
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = section.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = section.body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private data class LegalSection(val title: String, val body: String)

private val EULA_CONTENT = listOf(
    LegalSection(
        title = "About LocaPin",
        body = "LocaPin is a San Juan City tourism mobile application designed to help visitors discover attractions and nearby points of interest."
    ),
    LegalSection(
        title = "Location and Connectivity",
        body = "The app may use device location for nearby attraction context, distance estimates, and route guidance. LocaPin requires internet access and may use GPS or network-based location services for key features."
    ),
    LegalSection(
        title = "User Responsibilities",
        body = "You are responsible for lawful and appropriate use of the app, including how location and route guidance are used while traveling."
    ),
    LegalSection(
        title = "Service Changes",
        body = "App content, attractions, and feature behavior may change over time. Services may be updated, interrupted, or temporarily unavailable during maintenance or technical issues."
    ),
    LegalSection(
        title = "Deployment Disclaimer",
        body = "To the extent appropriate for a student or capstone-style deployment, LocaPin is provided on an as-is basis without guaranteed uninterrupted performance."
    )
)

private val TERMS_CONTENT = listOf(
    LegalSection(
        title = "Eligibility and Responsible Use",
        body = "You agree to use LocaPin responsibly and in compliance with applicable laws and platform policies."
    ),
    LegalSection(
        title = "Account Responsibility",
        body = "You are responsible for your account credentials, profile information, and any activity performed through your account."
    ),
    LegalSection(
        title = "Accuracy and Limitations",
        body = "Attraction details, route guidance, and location-based information are provided on a best-effort basis and may not always be complete, current, or fully accurate."
    ),
    LegalSection(
        title = "Prohibited Misuse",
        body = "You may not misuse the app by attempting unauthorized access, disrupting services, or using content and features for unlawful purposes."
    ),
    LegalSection(
        title = "Third-Party Dependencies",
        body = "Some features may rely on third-party services such as map providers or social login providers. Their availability can affect app behavior."
    ),
    LegalSection(
        title = "Content Management and Updates",
        body = "Administrative users are responsible for content they manage when applicable. Features and terms may be updated in later releases."
    )
)

private val PRIVACY_CONTENT = listOf(
    LegalSection(
        title = "Account Data Use",
        body = "LocaPin may collect and use account details needed for authentication and profile-related features."
    ),
    LegalSection(
        title = "Location Data Use",
        body = "The app may access your current location to calculate distance and support navigation-related features for attractions in and around San Juan City."
    ),
    LegalSection(
        title = "How Location Works",
        body = "GPS, network connectivity, and third-party map services may be used to provide map display, direction guidance, and nearby context."
    ),
    LegalSection(
        title = "Permission Control",
        body = "Location permission is controlled at the device level. You can allow, deny, or revoke access in system settings at any time."
    ),
    LegalSection(
        title = "Feature Impact",
        body = "If location access is denied, some distance, map, and navigation features may not work properly."
    ),
    LegalSection(
        title = "Scope Commitment",
        body = "This consent reflects current app scope and does not claim data collection beyond planned authentication, profile, and tourism navigation use cases."
    )
)
