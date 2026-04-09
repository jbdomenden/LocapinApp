package com.locapin.mobile.feature.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.locapin.mobile.BuildConfig
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
    vm: LoginViewModel = hiltViewModel(),
    signUpVm: SignUpViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val signUpState by signUpVm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val callbackManager = remember { CallbackManager.Factory.create() }

    var isSignUpMode by rememberSaveable { mutableStateOf(false) }
    var signUpName by rememberSaveable { mutableStateOf("") }
    var signUpEmail by rememberSaveable { mutableStateOf("") }
    var signUpPassword by rememberSaveable { mutableStateOf("") }
    var signUpConfirmPassword by rememberSaveable { mutableStateOf("") }
    var signUpPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var signUpConfirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var signUpValidationMessage by rememberSaveable { mutableStateOf<String?>(null) }

    val googleSignInOptions = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .apply {
                if (BuildConfig.GOOGLE_SERVER_CLIENT_ID.isNotBlank()) {
                    requestIdToken(BuildConfig.GOOGLE_SERVER_CLIENT_ID)
                }
            }
            .build()
    }
    val googleSignInClient = remember(context) {
        GoogleSignIn.getClient(context, googleSignInOptions)
    }

    val googleLoginLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) {
            vm.onSocialLoginError("Google sign-in was cancelled.")
            return@rememberLauncherForActivityResult
        }

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            vm.loginWithGoogle(account.idToken)
        } catch (_: ApiException) {
            vm.onSocialLoginError("Google sign-in failed. Please try again.")
        }
    }

    DisposableEffect(callbackManager) {
        FacebookAuthBridge.setCallbackManager(callbackManager)
        val loginManager = LoginManager.getInstance()

        val callback = object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                vm.loginWithFacebook(result.accessToken?.token)
            }

            override fun onCancel() {
                vm.onSocialLoginError("Facebook sign-in was cancelled.")
            }

            override fun onError(error: FacebookException) {
                vm.onSocialLoginError(error.message ?: "Facebook sign-in failed.")
            }
        }

        loginManager.registerCallback(callbackManager, callback)

        onDispose {
            FacebookAuthBridge.clearCallbackManager(callbackManager)
            loginManager.unregisterCallback(callbackManager)
        }
    }

    LaunchedEffect(state.loggedInRole) {
        state.loggedInRole?.let {
            onRoleResolved(it)
            vm.consumeLoginResult()
        }
    }

    LaunchedEffect(signUpState.registeredRole) {
        signUpState.registeredRole?.let {
            onRoleResolved(it)
            signUpVm.clearRegisteredRole()
        }
    }

    Scaffold(containerColor = AuthScreenBackground) { padding ->
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

                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (!isSignUpMode) AuthPrimaryText else AuthPrimaryText.copy(alpha = 0.55f),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { isSignUpMode = false }
                    )
                    Text(
                        text = "|",
                        style = MaterialTheme.typography.headlineSmall,
                        color = AuthPrimaryText
                    )
                    Text(
                        text = "Sign-Up",
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (isSignUpMode) AuthPrimaryText else AuthPrimaryText.copy(alpha = 0.55f),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { isSignUpMode = true }
                    )
                }

                Text(
                    text = if (isSignUpMode) "Create your account" else "Welcome Back!",
                    style = MaterialTheme.typography.titleMedium,
                    color = AuthAccentText,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                if (!isSignUpMode) {
                    LoginContent(
                        state = state,
                        onEmailChange = vm::onEmailChange,
                        onPasswordChange = vm::onPasswordChange,
                        onTogglePassword = vm::togglePasswordVisibility,
                        onForgotPassword = onForgotPassword,
                        onLogin = vm::login,
                        onGoogleLogin = {
                            googleSignInClient.signOut().addOnCompleteListener {
                                googleLoginLauncher.launch(googleSignInClient.signInIntent)
                            }
                        },
                        onFacebookLogin = {
                            val activity = context as? Activity
                            if (activity == null) {
                                vm.onSocialLoginError("Facebook sign-in is unavailable in this context.")
                            } else {
                                LoginManager.getInstance().logInWithReadPermissions(
                                    activity,
                                    listOf("email", "public_profile")
                                )
                            }
                        },
                        onOpenSignUpScreen = onSignUp
                    )
                } else {
                    SignUpInlineContent(
                        isLoading = signUpState.isLoading,
                        backendError = signUpState.errorMessage,
                        name = signUpName,
                        email = signUpEmail,
                        password = signUpPassword,
                        confirmPassword = signUpConfirmPassword,
                        passwordVisible = signUpPasswordVisible,
                        confirmPasswordVisible = signUpConfirmPasswordVisible,
                        validationMessage = signUpValidationMessage,
                        onNameChange = {
                            signUpName = it
                            signUpValidationMessage = null
                        },
                        onEmailChange = {
                            signUpEmail = it
                            signUpValidationMessage = null
                        },
                        onPasswordChange = {
                            signUpPassword = it
                            signUpValidationMessage = null
                        },
                        onConfirmPasswordChange = {
                            signUpConfirmPassword = it
                            signUpValidationMessage = null
                        },
                        onTogglePassword = { signUpPasswordVisible = !signUpPasswordVisible },
                        onToggleConfirmPassword = { signUpConfirmPasswordVisible = !signUpConfirmPasswordVisible },
                        onCreateAccount = {
                            when {
                                signUpName.isBlank() || signUpEmail.isBlank() ||
                                    signUpPassword.isBlank() || signUpConfirmPassword.isBlank() -> {
                                    signUpValidationMessage = "Please complete all required fields."
                                }

                                signUpPassword != signUpConfirmPassword -> {
                                    signUpValidationMessage = "Passwords do not match."
                                }

                                else -> {
                                    signUpValidationMessage = null
                                    signUpVm.register(
                                        name = signUpName.trim(),
                                        email = signUpEmail.trim(),
                                        password = signUpPassword
                                    )
                                }
                            }
                        },
                        onSwitchToLogin = { isSignUpMode = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.LoginContent(
    state: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onForgotPassword: () -> Unit,
    onLogin: () -> Unit,
    onGoogleLogin: () -> Unit,
    onFacebookLogin: () -> Unit,
    onOpenSignUpScreen: () -> Unit
) {
    OutlinedTextField(
        value = state.email,
        onValueChange = onEmailChange,
        label = { Text("Username", color = AuthPrimaryText) },
        singleLine = true,
        enabled = !state.isLoading,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp)
    )
    OutlinedTextField(
        value = state.password,
        onValueChange = onPasswordChange,
        label = { Text("Password", color = AuthPrimaryText) },
        singleLine = true,
        enabled = !state.isLoading,
        visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onTogglePassword, enabled = !state.isLoading) {
                Icon(
                    imageVector = if (state.isPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
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
        Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
    }

    Button(
        onClick = onLogin,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .width(220.dp)
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        enabled = !state.isLoading,
        colors = ButtonDefaults.buttonColors(containerColor = AuthPrimaryButton, contentColor = AuthPrimaryText)
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
            onClick = onGoogleLogin,
            enabled = !state.isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = AuthFieldBackground, contentColor = AuthPrimaryText)
        ) {
            Text("Google")
        }
        Button(
            onClick = onFacebookLogin,
            enabled = !state.isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = AuthFieldBackground, contentColor = AuthPrimaryText)
        ) {
            Text("Facebook")
        }
    }

    TextButton(onClick = onOpenSignUpScreen, enabled = !state.isLoading) {
        Text("Open full sign-up form", color = AuthPrimaryText)
    }
}

@Composable
private fun ColumnScope.SignUpInlineContent(
    isLoading: Boolean,
    backendError: String?,
    name: String,
    email: String,
    password: String,
    confirmPassword: String,
    passwordVisible: Boolean,
    confirmPasswordVisible: Boolean,
    validationMessage: String?,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onToggleConfirmPassword: () -> Unit,
    onCreateAccount: () -> Unit,
    onSwitchToLogin: () -> Unit
) {
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Name", color = AuthPrimaryText) },
        singleLine = true,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp)
    )
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("Email", color = AuthPrimaryText) },
        singleLine = true,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp)
    )
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("Password", color = AuthPrimaryText) },
        singleLine = true,
        enabled = !isLoading,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onTogglePassword, enabled = !isLoading) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = "Toggle password visibility"
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp)
    )
    OutlinedTextField(
        value = confirmPassword,
        onValueChange = onConfirmPasswordChange,
        label = { Text("Confirm Password", color = AuthPrimaryText) },
        singleLine = true,
        enabled = !isLoading,
        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggleConfirmPassword, enabled = !isLoading) {
                Icon(
                    imageVector = if (confirmPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = "Toggle confirm password visibility"
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp)
    )

    (validationMessage ?: backendError)?.let {
        Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
    }

    Button(
        onClick = onCreateAccount,
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .width(220.dp)
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(containerColor = AuthPrimaryButton, contentColor = AuthPrimaryText)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.height(18.dp), strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Creating...")
        } else {
            Text("Create account", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }

    TextButton(onClick = onSwitchToLogin, enabled = !isLoading, modifier = Modifier.align(Alignment.CenterHorizontally)) {
        Text("Already have an account? Login", color = AuthPrimaryText)
    }
}
