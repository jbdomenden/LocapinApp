package com.locapin.mobile.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.locapin.mobile.domain.model.UserRole

private val AuthScreenBackground = Color(0xFFF3E9E3)
private val AuthCardBackground = Color(0xFFEAB7C2)
private val AuthBorder = Color(0xFFD57C94)
private val AuthFieldBackground = Color(0xFFF4ECE7)
private val AuthPrimaryButton = Color(0xFFF6A39B)
private val AuthPrimaryText = Color(0xFF8E3551)
private val AuthAccentText = Color(0xFFC78D2C)
private val AuthDivider = Color(0xFFD86B7A)
private val AuthGradientStart = Color(0xFFE71589)
private val AuthGradientEnd = Color(0xFFF5CA45)

@Composable
fun LoginScreen(
    onForgotPassword: () -> Unit,
    onSignUp: () -> Unit,
    onRoleResolved: (UserRole) -> Unit,
    vm: LoginViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.loggedInRole) {
        state.loggedInRole?.let {
            onRoleResolved(it)
            vm.consumeLoginResult()
        }
    }

    Scaffold(
        containerColor = AuthScreenBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
                .border(width = 5.dp, color = AuthBorder, shape = RoundedCornerShape(40.dp))
                .background(AuthScreenBackground, RoundedCornerShape(40.dp))
                .padding(horizontal = 20.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "LocaPin",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = AuthPrimaryText,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "A new chapter for San Juan exploration",
                style = MaterialTheme.typography.titleMedium,
                color = AuthAccentText,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AuthCardBackground, RoundedCornerShape(34.dp))
                    .border(width = 2.dp, color = AuthBorder, shape = RoundedCornerShape(34.dp))
                    .padding(horizontal = 16.dp, vertical = 22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(AuthGradientStart, AuthGradientEnd)
                            ),
                            shape = RoundedCornerShape(100)
                        )
                )

                Text(
                    text = "Login | Sign-Up",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = AuthPrimaryText,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "Welcome Back!",
                    style = MaterialTheme.typography.titleMedium,
                    color = AuthAccentText,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                OutlinedTextField(
                    value = state.email,
                    onValueChange = vm::onEmailChange,
                    label = { Text("Username", color = AuthPrimaryText) },
                    singleLine = true,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp)
                )
                OutlinedTextField(
                    value = state.password,
                    onValueChange = vm::onPasswordChange,
                    label = { Text("Password", color = AuthPrimaryText) },
                    singleLine = true,
                    enabled = !state.isLoading,
                    visualTransformation = if (state.isPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = vm::togglePasswordVisibility, enabled = !state.isLoading) {
                            Icon(
                                imageVector = if (state.isPasswordVisible) {
                                    Icons.Outlined.VisibilityOff
                                } else {
                                    Icons.Outlined.Visibility
                                },
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp)
                )

                TextButton(
                    onClick = onForgotPassword,
                    enabled = !state.isLoading,
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Text("Forgot Password?", color = AuthAccentText)
                }

                state.errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = vm::login,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(220.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    enabled = !state.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AuthPrimaryButton,
                        contentColor = AuthPrimaryText
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.height(18.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Signing in...")
                    } else {
                        Text("Login", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(modifier = Modifier.weight(1f), color = AuthDivider)
                    Text(
                        text = "Login with",
                        style = MaterialTheme.typography.titleMedium,
                        color = AuthPrimaryText,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                    Divider(modifier = Modifier.weight(1f), color = AuthDivider)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                ) {
                    Button(
                        onClick = {},
                        enabled = false,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AuthFieldBackground,
                            disabledContainerColor = AuthFieldBackground,
                            disabledContentColor = AuthPrimaryText
                        )
                    ) {
                        Text("Google")
                    }
                    Button(
                        onClick = {},
                        enabled = false,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AuthFieldBackground,
                            disabledContainerColor = AuthFieldBackground,
                            disabledContentColor = AuthPrimaryText
                        )
                    ) {
                        Text("Facebook")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onSignUp, enabled = !state.isLoading) {
                        Text("Create account", color = AuthPrimaryText)
                    }
                }

                QuickAccountsBlock(
                    onAdmin = { vm.applyQuickAccount(UserRole.ADMIN) },
                    onTourist = { vm.applyQuickAccount(UserRole.TOURIST) },
                    enabled = !state.isLoading
                )
            }
        }
    }
}

@Composable
private fun QuickAccountsBlock(
    onAdmin: () -> Unit,
    onTourist: () -> Unit,
    enabled: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(AuthFieldBackground, RoundedCornerShape(20.dp))
            .padding(12.dp)
    ) {
        Text("Test Accounts", style = MaterialTheme.typography.titleSmall, color = AuthPrimaryText)
        QuickAccountRow(
            label = "Admin",
            email = "admin@locapin.app",
            password = " / Admin123!",
            onUse = onAdmin,
            enabled = enabled
        )
        QuickAccountRow(
            label = "Tourist",
            email = "tourist@locapin.app",
            password = " / Tourist123!",
            onUse = onTourist,
            enabled = enabled
        )
    }
}

@Composable
private fun QuickAccountRow(
    label: String,
    email: String,
    password: String,
    onUse: () -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.62f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(label, style = MaterialTheme.typography.labelLarge, color = AuthPrimaryText)
            Text(email, style = MaterialTheme.typography.bodySmall)
            Text(password, style = MaterialTheme.typography.bodySmall)
        }
        TextButton(onClick = onUse, enabled = enabled) { Text("Use", color = AuthPrimaryText) }
    }
}
