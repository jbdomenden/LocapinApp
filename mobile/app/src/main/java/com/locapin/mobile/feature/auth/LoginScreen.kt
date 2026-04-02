package com.locapin.mobile.feature.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Facebook
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.locapin.mobile.BuildConfig
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

private val LoginBackground = Color(0xFFFFF7F1)
private val LoginFrame = Color(0xFFF4D8DD)
private val LoginCardOuter = Color(0xFFF2CED8)
private val LoginCardInner = Color(0xFFFFF7F1)
private val LoginCardBorder = Color(0x66D795AA)
private val LoginTextPrimary = Color(0xFF532F3E)
private val LoginTextSecondary = Color(0xFF8A6A78)
private val LoginAccent = Color(0xFFD06384)
private val LoginFieldBackground = Color(0xFFFFFBF8)
private val LoginFieldFocused = Color(0xFFD47A96)
private val LoginFieldUnfocused = Color(0xFFDDB7C3)
private val LoginFieldPlaceholder = Color(0xFFA77F8D)

@Composable
fun LoginScreen(
    onRegister: () -> Unit,
    onForgotPassword: () -> Unit,
    onSuccess: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context.findActivity()
    val googleServerClientId = BuildConfig.GOOGLE_SERVER_CLIENT_ID
    val callbackManager = remember { CallbackManager.Factory.create() }

    val googleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        runCatching { task.result }.onSuccess { account ->
            val idToken = account.idToken
            if (idToken.isNullOrBlank()) vm.onSocialAuthError("Google sign-in did not return an ID token.")
            else vm.socialLogin(provider = "google", idToken = idToken)
        }.onFailure {
            vm.onSocialAuthError(it.message ?: "Google sign-in failed.")
        }
    }

    DisposableEffect(callbackManager) {
        FacebookAuthBridge.setCallbackManager(callbackManager)
        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onCancel() = vm.onSocialAuthError("Facebook sign-in was cancelled.")
                override fun onError(error: FacebookException) {
                    vm.onSocialAuthError(error.message ?: "Facebook sign-in failed.")
                }

                override fun onSuccess(result: LoginResult) {
                    val token = result.accessToken?.token
                    if (token.isNullOrBlank()) vm.onSocialAuthError("Facebook sign-in returned no access token.")
                    else vm.socialLogin(provider = "facebook", accessToken = token)
                }
            }
        )
        onDispose {
            FacebookAuthBridge.clearCallbackManager(callbackManager)
        }
    }

    if (state.isAuthenticated) onSuccess()
    LoginScreenContent(
        state = state,
        onIdentifierChange = vm::onLoginIdentifierChange,
        onPasswordChange = vm::onLoginPasswordChange,
        onClearIdentifier = vm::clearLoginIdentifier,
        onTogglePassword = vm::toggleLoginPasswordVisibility,
        onForgotPassword = {
            vm.forgotPassword()
            onForgotPassword()
        },
        onPrimaryAction = vm::login,
        onRegister = onRegister,
        onGoogleLoginClick = {
            if (googleServerClientId.isBlank()) {
                vm.onSocialAuthError("Google sign-in is not configured.")
                return@LoginScreenContent
            }
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(googleServerClientId)
                .build()
            googleLauncher.launch(GoogleSignIn.getClient(context, options).signInIntent)
        },
        onFacebookLoginClick = {
            if (activity == null) {
                vm.onSocialAuthError("Facebook sign-in requires an activity context.")
                return@LoginScreenContent
            }
            LoginManager.getInstance().logInWithReadPermissions(activity, callbackManager, listOf("email", "public_profile"))
        }
    )
}

@Composable
private fun LoginScreenContent(
    state: AuthUiState,
    onIdentifierChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onClearIdentifier: () -> Unit,
    onTogglePassword: () -> Unit,
    onForgotPassword: () -> Unit,
    onPrimaryAction: () -> Unit,
    onRegister: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(LoginBackground, Color(0xFFFFEFE8))
                )
            )
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(0.92f)
                        .height(220.dp)
                        .graphicsLayer(alpha = 0.45f)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFFFFDCE4), Color.Transparent)
                            ),
                            shape = RoundedCornerShape(44.dp)
                        )
                )
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = LoginFrame,
                    shape = RoundedCornerShape(36.dp),
                    shadowElevation = 10.dp
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "LocaPin",
                            style = MaterialTheme.typography.displaySmall.copy(letterSpacing = 1.2.sp),
                            color = LoginTextPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Discover your next San Juan moment.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LoginTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Spacer(Modifier.height(24.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = LoginCardOuter,
                            shape = RoundedCornerShape(32.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LoginCardBorder),
                            shadowElevation = 12.dp
                        ) {
                            Surface(
                                modifier = Modifier
                                    .padding(1.dp)
                                    .fillMaxWidth(),
                                color = LoginCardInner,
                                shape = RoundedCornerShape(31.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x26FFFFFF))
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Welcome back",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = LoginTextPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Sign in to continue your exploration.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = LoginTextSecondary,
                                        modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                                    )

                                AuthPillField(
                                    value = state.loginIdentifier,
                                    placeholder = "Email or username",
                                    onValueChange = onIdentifierChange,
                                    enabled = !state.isLoading,
                                    trailing = {
                                        if (state.loginIdentifier.isNotBlank()) {
                                            IconButton(onClick = onClearIdentifier) {
                                                Icon(
                                                    Icons.Default.Close,
                                                    contentDescription = "Clear username",
                                                    tint = LoginTextSecondary
                                                )
                                            }
                                        }
                                    }
                                )
                                Spacer(Modifier.height(14.dp))
                                AuthPillField(
                                    value = state.loginPassword,
                                    placeholder = "Password",
                                    onValueChange = onPasswordChange,
                                    enabled = !state.isLoading,
                                    isPassword = true,
                                    isPasswordVisible = state.isLoginPasswordVisible,
                                    onTogglePassword = onTogglePassword
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = onForgotPassword) {
                                        Text(
                                            "Forgot Password?",
                                            color = LoginAccent,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Button(
                                    onClick = onPrimaryAction,
                                    enabled = !state.isLoading,
                                    shape = RoundedCornerShape(24.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = LoginAccent,
                                        contentColor = Color(0xFFFFF7F3),
                                        disabledContainerColor = LoginAccent.copy(alpha = 0.6f),
                                        disabledContentColor = Color(0xFFFFF7F3).copy(alpha = 0.9f)
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(
                                        defaultElevation = 6.dp,
                                        pressedElevation = 2.dp,
                                        disabledElevation = 0.dp
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                ) {
                                    if (state.isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = Color(0xFFFFF7F3),
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text(
                                            text = "Login",
                                            color = Color(0xFFFFF7F3),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                state.errorMessage?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFAF3D4D),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top = 12.dp)
                                    )
                                }

                                Spacer(Modifier.height(16.dp))
                                SocialAuthRow(
                                    onGoogleClick = onGoogleLoginClick,
                                    onFacebookClick = onFacebookLoginClick,
                                    enabled = !state.isLoading && state.socialLoadingProvider == null,
                                    loadingProvider = state.socialLoadingProvider
                                )

                                }
                            }
                        }
                    }
                }
            }

            TextButton(
                onClick = onRegister,
                modifier = Modifier.padding(top = 22.dp)
            ) {
                Text(
                    text = "Create account",
                    color = LoginAccent,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall,
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                )
            }
        }
    }
}

@Composable
private fun AuthPillField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp),
        shape = RoundedCornerShape(26.dp),
        singleLine = true,
        placeholder = {
            Text(
                text = placeholder,
                color = LoginFieldPlaceholder,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        textStyle = TextStyle(
            color = LoginTextPrimary,
            fontWeight = FontWeight.Medium
        ),
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            when {
                isPassword && onTogglePassword != null -> {
                    IconButton(onClick = onTogglePassword) {
                        Icon(
                            if (isPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = "Toggle password visibility",
                            tint = LoginTextSecondary
                        )
                    }
                }
                trailing != null -> trailing()
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = LoginFieldBackground,
            unfocusedContainerColor = LoginFieldBackground,
            disabledContainerColor = LoginFieldBackground,
            focusedBorderColor = LoginFieldFocused,
            unfocusedBorderColor = LoginFieldUnfocused,
            focusedTrailingIconColor = LoginAccent,
            unfocusedTrailingIconColor = LoginTextSecondary,
            cursorColor = LoginAccent
        ),
        supportingText = {}
    )
}

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    onOpenEula: () -> Unit,
    onOpenTerms: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context.findActivity()
    val googleServerClientId = BuildConfig.GOOGLE_SERVER_CLIENT_ID
    val callbackManager = remember { CallbackManager.Factory.create() }

    val googleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        runCatching { task.result }.onSuccess { account ->
            val idToken = account.idToken
            if (idToken.isNullOrBlank()) vm.onSocialAuthError("Google sign-in did not return an ID token.")
            else vm.socialLogin(provider = "google", idToken = idToken)
        }.onFailure {
            vm.onSocialAuthError(it.message ?: "Google sign-in failed.")
        }
    }

    DisposableEffect(callbackManager) {
        FacebookAuthBridge.setCallbackManager(callbackManager)
        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onCancel() = vm.onSocialAuthError("Facebook sign-in was cancelled.")
                override fun onError(error: FacebookException) {
                    vm.onSocialAuthError(error.message ?: "Facebook sign-in failed.")
                }

                override fun onSuccess(result: LoginResult) {
                    val token = result.accessToken?.token
                    if (token.isNullOrBlank()) vm.onSocialAuthError("Facebook sign-in returned no access token.")
                    else vm.socialLogin(provider = "facebook", accessToken = token)
                }
            }
        )
        onDispose {
            FacebookAuthBridge.clearCallbackManager(callbackManager)
        }
    }

    if (state.isAuthenticated) onSuccess()
    RegisterScreenContent(
        state = state,
        onUsernameChange = vm::onSignupUsernameChange,
        onEmailChange = vm::onSignupEmailChange,
        onPasswordChange = vm::onSignupPasswordChange,
        onConfirmPasswordChange = vm::onSignupConfirmPasswordChange,
        onClearUsername = vm::clearSignupUsername,
        onClearEmail = vm::clearSignupEmail,
        onTogglePassword = vm::toggleSignupPasswordVisibility,
        onToggleConfirmPassword = vm::toggleSignupConfirmPasswordVisibility,
        onTermsChange = vm::toggleTermsAcceptance,
        onPrimaryAction = vm::register,
        onBack = onBack,
        onOpenEula = onOpenEula,
        onOpenTerms = onOpenTerms,
        onGoogleLoginClick = {
            if (googleServerClientId.isBlank()) {
                vm.onSocialAuthError("Google sign-in is not configured.")
                return@RegisterScreenContent
            }
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(googleServerClientId)
                .build()
            googleLauncher.launch(GoogleSignIn.getClient(context, options).signInIntent)
        },
        onFacebookLoginClick = {
            if (activity == null) {
                vm.onSocialAuthError("Facebook sign-in requires an activity context.")
                return@RegisterScreenContent
            }
            LoginManager.getInstance().logInWithReadPermissions(activity, callbackManager, listOf("email", "public_profile"))
        }
    )
}

@Composable
private fun RegisterScreenContent(
    state: AuthUiState,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onClearUsername: () -> Unit,
    onClearEmail: () -> Unit,
    onTogglePassword: () -> Unit,
    onToggleConfirmPassword: () -> Unit,
    onTermsChange: () -> Unit,
    onPrimaryAction: () -> Unit,
    onBack: () -> Unit,
    onOpenEula: () -> Unit,
    onOpenTerms: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onFacebookLoginClick: () -> Unit
) {
    val signupError = state.errorMessage

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(LoginBackground, Color(0xFFFFEFE8))
                )
            )
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(0.92f)
                        .height(220.dp)
                        .graphicsLayer(alpha = 0.45f)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFFFFDCE4), Color.Transparent)
                            ),
                            shape = RoundedCornerShape(44.dp)
                        )
                )
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = LoginFrame,
                    shape = RoundedCornerShape(36.dp),
                    shadowElevation = 10.dp
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "LocaPin",
                            style = MaterialTheme.typography.displaySmall.copy(letterSpacing = 1.2.sp),
                            color = LoginTextPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Discover your next San Juan moment.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LoginTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Spacer(Modifier.height(24.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = LoginCardOuter,
                            shape = RoundedCornerShape(32.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LoginCardBorder),
                            shadowElevation = 12.dp
                        ) {
                            Surface(
                                modifier = Modifier
                                    .padding(1.dp)
                                    .fillMaxWidth(),
                                color = LoginCardInner,
                                shape = RoundedCornerShape(31.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x26FFFFFF))
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Create account",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = LoginTextPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Join LocaPin and start exploring.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = LoginTextSecondary,
                                        modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                                    )

                                    AuthPillField(
                                        value = state.signupUsername,
                                        placeholder = "Username",
                                        enabled = !state.isLoading,
                                        onValueChange = onUsernameChange,
                                        trailing = {
                                            if (state.signupUsername.isNotBlank()) {
                                                IconButton(onClick = onClearUsername) {
                                                    Icon(
                                                        Icons.Default.Close,
                                                        contentDescription = "Clear username",
                                                        tint = LoginTextSecondary
                                                    )
                                                }
                                            }
                                        }
                                    )
                                    Spacer(Modifier.height(14.dp))
                                    AuthPillField(
                                        value = state.signupEmail,
                                        placeholder = "Email",
                                        enabled = !state.isLoading,
                                        onValueChange = onEmailChange,
                                        trailing = {
                                            if (state.signupEmail.isNotBlank()) {
                                                IconButton(onClick = onClearEmail) {
                                                    Icon(
                                                        Icons.Default.Close,
                                                        contentDescription = "Clear email",
                                                        tint = LoginTextSecondary
                                                    )
                                                }
                                            }
                                        }
                                    )
                                    Spacer(Modifier.height(14.dp))
                                    AuthPillField(
                                        value = state.signupPassword,
                                        placeholder = "Password",
                                        enabled = !state.isLoading,
                                        onValueChange = onPasswordChange,
                                        isPassword = true,
                                        isPasswordVisible = state.isSignupPasswordVisible,
                                        onTogglePassword = onTogglePassword
                                    )
                                    Spacer(Modifier.height(14.dp))
                                    AuthPillField(
                                        value = state.signupConfirmPassword,
                                        placeholder = "Confirm password",
                                        enabled = !state.isLoading,
                                        onValueChange = onConfirmPasswordChange,
                                        isPassword = true,
                                        isPasswordVisible = state.isSignupConfirmPasswordVisible,
                                        onTogglePassword = onToggleConfirmPassword
                                    )

                                    Spacer(Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(enabled = !state.isLoading, onClick = onTermsChange),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = state.hasAcceptedTerms,
                                            onCheckedChange = { onTermsChange() },
                                            enabled = !state.isLoading
                                        )
                                        Text(
                                            text = "I agree to the EULA and Terms & Agreement.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = LoginTextSecondary
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(onClick = onOpenEula, enabled = !state.isLoading) {
                                            Text("View EULA", color = LoginAccent)
                                        }
                                        TextButton(onClick = onOpenTerms, enabled = !state.isLoading) {
                                            Text("View Terms", color = LoginAccent)
                                        }
                                    }

                                    Spacer(Modifier.height(10.dp))
                                    Button(
                                        onClick = onPrimaryAction,
                                        enabled = !state.isLoading && state.hasAcceptedTerms,
                                        shape = RoundedCornerShape(24.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = LoginAccent,
                                            contentColor = Color(0xFFFFF7F3),
                                            disabledContainerColor = LoginAccent.copy(alpha = 0.6f),
                                            disabledContentColor = Color(0xFFFFF7F3).copy(alpha = 0.9f)
                                        ),
                                        elevation = ButtonDefaults.buttonElevation(
                                            defaultElevation = 6.dp,
                                            pressedElevation = 2.dp,
                                            disabledElevation = 0.dp
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp)
                                    ) {
                                        if (state.isLoading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                color = Color(0xFFFFF7F3),
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Text(
                                                text = "Sign Up",
                                                color = Color(0xFFFFF7F3),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    signupError?.let {
                                        Text(
                                            text = it,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFFAF3D4D),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(top = 12.dp)
                                        )
                                    }

                                    Spacer(Modifier.height(16.dp))
                                    SocialAuthRow(
                                        onGoogleClick = onGoogleLoginClick,
                                        onFacebookClick = onFacebookLoginClick,
                                        enabled = !state.isLoading && state.socialLoadingProvider == null,
                                        loadingProvider = state.socialLoadingProvider
                                    )

                                }
                            }
                        }
                    }
                }
            }

            TextButton(
                onClick = onBack,
                modifier = Modifier.padding(top = 22.dp)
            ) {
                Text(
                    text = "Back to login",
                    color = LoginAccent,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall,
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                )
            }
        }
    }
}

@Composable
private fun SocialAuthRow(
    onGoogleClick: () -> Unit,
    onFacebookClick: () -> Unit,
    enabled: Boolean,
    loadingProvider: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = onGoogleClick,
            enabled = enabled,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3EEE9), contentColor = LoginTextPrimary)
        ) {
            if (loadingProvider == "google") CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            else Text("Google")
        }
        Button(
            onClick = onFacebookClick,
            enabled = enabled,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3EEE9), contentColor = LoginTextPrimary)
        ) {
            if (loadingProvider == "facebook") CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            else {
                Icon(Icons.Outlined.Facebook, contentDescription = null, tint = Color(0xFF3B5998))
                Spacer(Modifier.size(6.dp))
                Text("Facebook")
            }
        }
    }
}

@Composable
fun ForgotPasswordScreen(onBack: () -> Unit, vm: AuthViewModel = hiltViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    ForgotPasswordScreenContent(
        state = state,
        onEmailChange = vm::onLoginIdentifierChange,
        onClearUsername = vm::clearLoginIdentifier,
        onPrimaryAction = vm::forgotPassword,
        onBack = onBack
    )
}

@Composable
private fun ForgotPasswordScreenContent(
    state: AuthUiState,
    onEmailChange: (String) -> Unit,
    onClearUsername: () -> Unit,
    onPrimaryAction: () -> Unit,
    onBack: () -> Unit
) {
    val isSuccessMessage = state.errorMessage?.contains("reset link sent", ignoreCase = true) == true
    val visibleMessage = state.errorMessage?.takeUnless { it.contains("not yet connected", ignoreCase = true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(LoginBackground, Color(0xFFFFEFE8))
                )
            )
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(0.92f)
                        .height(220.dp)
                        .graphicsLayer(alpha = 0.45f)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFFFFDCE4), Color.Transparent)
                            ),
                            shape = RoundedCornerShape(44.dp)
                        )
                )
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = LoginFrame,
                    shape = RoundedCornerShape(36.dp),
                    shadowElevation = 10.dp
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "LocaPin",
                            style = MaterialTheme.typography.displaySmall.copy(letterSpacing = 1.2.sp),
                            color = LoginTextPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Discover your next San Juan moment.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LoginTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Spacer(Modifier.height(24.dp))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = LoginCardOuter,
                            shape = RoundedCornerShape(32.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LoginCardBorder),
                            shadowElevation = 12.dp
                        ) {
                            Surface(
                                modifier = Modifier
                                    .padding(1.dp)
                                    .fillMaxWidth(),
                                color = LoginCardInner,
                                shape = RoundedCornerShape(31.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x26FFFFFF))
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Reset password",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = LoginTextPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Enter your email and we'll send a reset link.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = LoginTextSecondary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                                    )

                                    AuthPillField(
                                        value = state.loginIdentifier,
                                        placeholder = "Email",
                                        onValueChange = onEmailChange,
                                        enabled = !state.isLoading,
                                        trailing = {
                                            if (state.loginIdentifier.isNotBlank()) {
                                                IconButton(onClick = onClearUsername) {
                                                    Icon(
                                                        Icons.Default.Close,
                                                        contentDescription = "Clear email",
                                                        tint = LoginTextSecondary
                                                    )
                                                }
                                            }
                                        }
                                    )

                                    Spacer(Modifier.height(14.dp))
                                    Button(
                                        onClick = onPrimaryAction,
                                        enabled = !state.isLoading,
                                        shape = RoundedCornerShape(24.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = LoginAccent,
                                            contentColor = Color(0xFFFFF7F3),
                                            disabledContainerColor = LoginAccent.copy(alpha = 0.6f),
                                            disabledContentColor = Color(0xFFFFF7F3).copy(alpha = 0.9f)
                                        ),
                                        elevation = ButtonDefaults.buttonElevation(
                                            defaultElevation = 6.dp,
                                            pressedElevation = 2.dp,
                                            disabledElevation = 0.dp
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp)
                                    ) {
                                        if (state.isLoading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                color = Color(0xFFFFF7F3),
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Text(
                                                text = "Send reset link",
                                                color = Color(0xFFFFF7F3),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    androidx.compose.animation.AnimatedVisibility(visible = visibleMessage != null) {
                                        Text(
                                            text = visibleMessage.orEmpty(),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isSuccessMessage) Color(0xFF2E7D5B) else Color(0xFFAF3D4D),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(top = 12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            TextButton(
                onClick = onBack,
                modifier = Modifier.padding(top = 22.dp)
            ) {
                Text(
                    text = "Back to login",
                    color = LoginAccent,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall,
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                )
            }
        }
    }
}

@Preview(name = "Login - Pixel style", widthDp = 412, heightDp = 915, showBackground = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreenContent(
        state = AuthUiState(loginIdentifier = "", loginPassword = ""),
        onIdentifierChange = {},
        onPasswordChange = {},
        onClearIdentifier = {},
        onTogglePassword = {},
        onForgotPassword = {},
        onPrimaryAction = {},
        onRegister = {}
    )
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Preview(name = "Login - Dark", widthDp = 412, heightDp = 915, showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LoginScreenDarkPreview() = LoginScreenPreview()

@Preview(name = "Login - Small height", widthDp = 412, heightDp = 640, showBackground = true)
@Composable
private fun LoginScreenSmallPreview() = LoginScreenPreview()
