package com.locapin.mobile.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.locapin.mobile.domain.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onBack: () -> Unit,
    onOpenEula: () -> Unit,
    onOpenTerms: () -> Unit,
    onOpenPrivacyConsent: () -> Unit,
    onRegistered: (UserRole) -> Unit,
    vm: SignUpViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var agreeEula by remember { mutableStateOf(false) }
    var agreeTerms by remember { mutableStateOf(false) }
    var agreePrivacy by remember { mutableStateOf(false) }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var validationMessage by remember { mutableStateOf<String?>(null) }

    val allConsentsChecked = agreeEula && agreeTerms && agreePrivacy
    val uiState by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.registeredRole) {
        uiState.registeredRole?.let { role ->
            onRegistered(role)
            vm.clearRegisteredRole()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Account") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Set up your LocaPin account",
                style = MaterialTheme.typography.headlineSmall
            )

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    validationMessage = null
                },
                label = { Text("Name") },
                singleLine = true,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    validationMessage = null
                },
                label = { Text("Email") },
                singleLine = true,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    validationMessage = null
                },
                label = { Text("Password") },
                singleLine = true,
                enabled = !uiState.isLoading,
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }, enabled = !uiState.isLoading) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = "Toggle password visibility"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    validationMessage = null
                },
                label = { Text("Confirm Password") },
                singleLine = true,
                enabled = !uiState.isLoading,
                visualTransformation = if (confirmPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }, enabled = !uiState.isLoading) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = "Toggle confirm password visibility"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Consent",
                style = MaterialTheme.typography.titleMedium
            )
            ConsentRow(
                checked = agreeEula,
                label = "I agree to the EULA",
                linkLabel = "Read EULA",
                onCheckedChange = {
                    agreeEula = it
                    validationMessage = null
                },
                onOpenDocument = onOpenEula
            )
            ConsentRow(
                checked = agreeTerms,
                label = "I agree to the Terms and Conditions",
                linkLabel = "Read Terms",
                onCheckedChange = {
                    agreeTerms = it
                    validationMessage = null
                },
                onOpenDocument = onOpenTerms
            )
            ConsentRow(
                checked = agreePrivacy,
                label = "I agree to the Privacy and Location Consent",
                linkLabel = "Read Privacy",
                onCheckedChange = {
                    agreePrivacy = it
                    validationMessage = null
                },
                onOpenDocument = onOpenPrivacyConsent
            )

            (validationMessage ?: uiState.errorMessage)?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    when {
                        name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                            validationMessage = "Please complete all required fields."
                        }

                        password != confirmPassword -> {
                            validationMessage = "Passwords do not match. Please try again."
                        }

                        else -> {
                            validationMessage = null
                            vm.register(
                                name = name.trim(),
                                email = email.trim(),
                                password = password,
                                agreeEula = agreeEula,
                                agreeTerms = agreeTerms,
                                agreePrivacy = agreePrivacy
                            )
                        }
                    }
                },
                enabled = allConsentsChecked && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.height(18.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Creating account...")
                } else {
                    Text("Create Account")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ConsentRow(
    checked: Boolean,
    label: String,
    linkLabel: String,
    onCheckedChange: (Boolean) -> Unit,
    onOpenDocument: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        TextButton(onClick = onOpenDocument) {
            Text(linkLabel)
        }
    }
}
