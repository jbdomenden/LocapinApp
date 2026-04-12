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
        title = "1. License Grant",
        body = "LocaPin grants you a revocable, non-exclusive, non-transferable, limited license to download, install and use the Application strictly in accordance with the terms of this Agreement."
    ),
    LegalSection(
        title = "2. Restrictions",
        body = "You agree not to, and you will not permit others to license, sell, rent, lease, assign, distribute, transmit, host, outsource, disclose or otherwise commercially exploit the Application or make the Application available to any third party."
    ),
    LegalSection(
        title = "3. Intellectual Property",
        body = "The Application, including without limitation all copyrights, patents, trademarks, trade secrets and other intellectual property rights are, and shall remain, the sole and exclusive property of LocaPin."
    ),
    LegalSection(
        title = "4. Your Suggestions",
        body = "Any feedback, comments, ideas, improvements or suggestions provided by you to LocaPin with respect to the Application shall remain the sole and exclusive property of LocaPin."
    ),
    LegalSection(
        title = "5. Modifications to Application",
        body = "LocaPin reserves the right to modify, suspend or discontinue, temporarily or permanently, the Application or any service to which it connects, with or without notice and without liability to you."
    ),
    LegalSection(
        title = "6. Term and Termination",
        body = "This Agreement shall remain in effect until terminated by you or LocaPin. LocaPin may, in its sole discretion, at any time and for any or no reason, suspend or terminate this Agreement with or without prior notice."
    )
)

private val TERMS_CONTENT = listOf(
    LegalSection(
        title = "1. Agreement to Terms",
        body = "By accessing or using LocaPin, you agree to be bound by these Terms and Conditions. If you disagree with any part of the terms, then you may not access the service."
    ),
    LegalSection(
        title = "2. User Accounts",
        body = "When you create an account with us, you must provide information that is accurate, complete, and current at all times. Failure to do so constitutes a breach of the Terms, which may result in immediate termination of your account."
    ),
    LegalSection(
        title = "3. Links To Other Web Sites",
        body = "Our Service may contain links to third-party web sites or services that are not owned or controlled by LocaPin. LocaPin has no control over, and assumes no responsibility for, the content, privacy policies, or practices of any third party web sites or services."
    ),
    LegalSection(
        title = "4. Limitation of Liability",
        body = "In no event shall LocaPin, nor its directors, employees, partners, agents, suppliers, or affiliates, be liable for any indirect, incidental, special, consequential or punitive damages, including without limitation, loss of profits, data, use, goodwill, or other intangible losses."
    ),
    LegalSection(
        title = "5. Governing Law",
        body = "These Terms shall be governed and construed in accordance with the laws of the Philippines, without regard to its conflict of law provisions."
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
