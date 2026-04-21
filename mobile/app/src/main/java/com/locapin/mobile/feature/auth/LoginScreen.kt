package com.locapin.mobile.feature.auth

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.locapin.mobile.BuildConfig
import com.locapin.mobile.domain.model.UserRole

private val AuthScreenBackground = Color(0xFFF7F0E9)
private val AuthCardBackground = Color(0xFFF9A7B3)
private val AuthBorder = Color(0xFFE88C9D)
private val AuthFieldBackground = Color(0xFFFDF1F3)
private val AuthPrimaryButton = Color(0xFFFFA3B1)
private val AuthPrimaryText = Color(0xFF8B3A4D)
private val AuthAccentText = Color(0xFFE58B9C)
private val AuthWhite = Color.White

@Composable
fun LoginScreen(
    onForgotPassword: () -> Unit,
    onSignUp: () -> Unit,
    onRoleResolved: (UserRole) -> Unit,
    onOpenEula: () -> Unit = {},
    onOpenTerms: () -> Unit = {},
    onOpenPrivacyConsent: () -> Unit = {},
    vm: LoginViewModel = hiltViewModel(),
    signUpVm: SignUpViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val signUpState by signUpVm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var isSignUpMode by rememberSaveable { mutableStateOf(false) }
    var signUpName by rememberSaveable { mutableStateOf("") }
    var signUpEmail by rememberSaveable { mutableStateOf("") }
    var signUpPassword by rememberSaveable { mutableStateOf("") }
    var signUpConfirmPassword by rememberSaveable { mutableStateOf("") }
    var signUpPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var signUpConfirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var signUpValidationMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var agreeEula by rememberSaveable { mutableStateOf(false) }
    var agreeTerms by rememberSaveable { mutableStateOf(false) }
    var agreePrivacy by rememberSaveable { mutableStateOf(false) }

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
        if (result.resultCode == Activity.RESULT_CANCELED) {
            vm.onSocialLoginError("Google sign-in was cancelled.")
            return@rememberLauncherForActivityResult
        }

        if (result.resultCode != Activity.RESULT_OK) {
            vm.onSocialLoginError("Google sign-in failed (Result: ${result.resultCode}).")
            return@rememberLauncherForActivityResult
        }

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            vm.loginWithGoogle(account.idToken)
        } catch (e: ApiException) {
            val errorMessage = when (e.statusCode) {
                12501 -> "Google sign-in was cancelled."
                12500 -> "Google sign-in failed. Please ensure your device has Google Play Services and a valid account."
                10 -> "Google sign-in developer error (10). This usually means the SHA-1 or package name is mismatched in the Google Cloud Console, or the google-services.json is missing the correct client configuration."
                else -> "Google sign-in failed (Status Code: ${e.statusCode}): ${e.message}"
            }
            vm.onSocialLoginError(errorMessage)
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(signUpState.errorMessage) {
        signUpState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AuthScreenBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Section
            LocaPinLogo()
            
            Text(
                text = "A new chapter for San Juan exploration",
                style = TextStyle(
                    fontFamily = FontFamily.Cursive,
                    fontSize = 18.sp,
                    color = AuthPrimaryText,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Main Card
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .background(AuthCardBackground, RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp, bottomStart = 40.dp, bottomEnd = 40.dp))
                    .border(width = 4.dp, color = AuthBorder, shape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp, bottomStart = 40.dp, bottomEnd = 40.dp))
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Gradient Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFFE91E63), Color(0xFFFFEB3B))
                            )
                        )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Login",
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = if (!isSignUpMode) AuthWhite else AuthWhite.copy(alpha = 0.6f),
                            shadow = Shadow(color = AuthPrimaryText.copy(alpha = 0.5f), offset = Offset(2f, 2f), blurRadius = 2f)
                        ),
                        modifier = Modifier.clickable { isSignUpMode = false }
                    )
                    Text(
                        text = " | ",
                        style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Black, color = AuthWhite)
                    )
                    Text(
                        text = "Sign-Up",
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isSignUpMode) AuthWhite else AuthWhite.copy(alpha = 0.6f),
                            shadow = Shadow(color = AuthPrimaryText.copy(alpha = 0.5f), offset = Offset(2f, 2f), blurRadius = 2f)
                        ),
                        modifier = Modifier.clickable { isSignUpMode = true }
                    )
                }

                Text(
                    text = if (isSignUpMode) "Join us today!" else "Welcome Back!",
                    style = TextStyle(
                        fontFamily = FontFamily.Cursive,
                        fontSize = 20.sp,
                        color = AuthWhite,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))

                if (isSignUpMode) {
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
                        agreeEula = agreeEula,
                        agreeTerms = agreeTerms,
                        agreePrivacy = agreePrivacy,
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
                        onAgreeEulaChange = { agreeEula = it },
                        onAgreeTermsChange = { agreeTerms = it },
                        onAgreePrivacyChange = { agreePrivacy = it },
                        onOpenEula = {
                            onOpenEula()
                        },
                        onOpenTerms = {
                            onOpenTerms()
                        },
                        onOpenPrivacyConsent = {
                            onOpenPrivacyConsent()
                        },
                        onCreateAccount = {
                            val passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$".toRegex()
                            when {
                                signUpName.isBlank() || signUpEmail.isBlank() ||
                                    signUpPassword.isBlank() || signUpConfirmPassword.isBlank() -> {
                                    signUpValidationMessage = "Please complete all required fields."
                                }
                                signUpPassword.length < 8 || !passwordRegex.matches(signUpPassword) -> {
                                    signUpValidationMessage = "Password must be at least 8 alphanumeric characters."
                                }
                                signUpPassword != signUpConfirmPassword -> {
                                    signUpValidationMessage = "Passwords do not match."
                                }
                                !agreeEula || !agreeTerms || !agreePrivacy -> {
                                    signUpValidationMessage = "You must agree to all legal consents."
                                }
                                else -> {
                                    signUpValidationMessage = null
                                    signUpVm.register(
                                        name = signUpName.trim(),
                                        email = signUpEmail.trim(),
                                        password = signUpPassword,
                                        agreeEula = agreeEula,
                                        agreeTerms = agreeTerms,
                                        agreePrivacy = agreePrivacy
                                    )
                                }
                            }
                        }
                    )
                } else {
                    LoginContent(
                        state = state,
                        onEmailChange = vm::onEmailChange,
                        onPasswordChange = vm::onPasswordChange,
                        onTogglePassword = vm::togglePasswordVisibility,
                        onRememberMeChange = vm::onRememberMeChange,
                        onForgotPassword = vm::forgotPassword,
                        onLogin = vm::login,
                        onGoogleLogin = {
                            googleLoginLauncher.launch(googleSignInClient.signInIntent)
                        },
                        onFacebookLogin = {
                            Toast.makeText(
                                context,
                                "Facebook login is coming soon. Please try other login methods.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LocaPinLogo() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // "LocaP" with outline effect
        Text(
            text = "LocaP",
            style = TextStyle(
                fontSize = 58.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AuthCardBackground,
                shadow = Shadow(color = Color.White, offset = Offset(0f, 0f), blurRadius = 8f)
            )
        )
        
        // Pin icon as 'i'
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 2.dp)) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color(0xFFE91E63),
                modifier = Modifier.size(54.dp)
            )
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color.White, CircleShape)
                    .align(Alignment.Center)
                    .offset(y = (-6).dp)
            )
        }

        // "n"
        Text(
            text = "n",
            style = TextStyle(
                fontSize = 58.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AuthCardBackground,
                shadow = Shadow(color = Color.White, offset = Offset(0f, 0f), blurRadius = 8f)
            )
        )
    }
}

@Composable
private fun ColumnScope.LoginContent(
    state: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onRememberMeChange: (Boolean) -> Unit,
    onForgotPassword: () -> Unit,
    onLogin: () -> Unit,
    onGoogleLogin: () -> Unit,
    onFacebookLogin: () -> Unit
) {
    StyledTextField(
        value = state.email,
        onValueChange = onEmailChange,
        label = "Email Address",
        trailingIcon = { 
            if (state.email.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Close, 
                    contentDescription = null, 
                    tint = AuthPrimaryText, 
                    modifier = Modifier.size(20.dp).clickable { onEmailChange("") }
                ) 
            }
        }
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    StyledTextField(
        value = state.password,
        onValueChange = onPasswordChange,
        label = "Password",
        isPassword = true,
        passwordVisible = state.isPasswordVisible,
        onTogglePassword = onTogglePassword
    )

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onRememberMeChange(!state.rememberMe) }
        ) {
            Checkbox(
                checked = state.rememberMe,
                onCheckedChange = onRememberMeChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = AuthPrimaryText,
                    uncheckedColor = AuthPrimaryText,
                    checkmarkColor = AuthWhite
                )
            )
            Text(
                text = "Remember me",
                style = TextStyle(fontSize = 14.sp, color = AuthPrimaryText, fontWeight = FontWeight.Bold)
            )
        }

        Text(
            text = "Forgot Password?",
            style = TextStyle(fontFamily = FontFamily.Cursive, fontSize = 14.sp, color = AuthPrimaryText, fontWeight = FontWeight.Bold),
            modifier = Modifier
                .padding(end = 12.dp)
                .clickable { onForgotPassword() }
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onLogin,
        modifier = Modifier
            .width(180.dp)
            .height(54.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = AuthPrimaryButton),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(color = AuthWhite, modifier = Modifier.size(24.dp))
        } else {
            Text(
                "Login",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Black, color = AuthWhite, shadow = Shadow(color = AuthPrimaryText.copy(alpha = 0.3f), offset = Offset(1f, 1f), blurRadius = 1f))
            )
        }
    }

    Spacer(modifier = Modifier.height(30.dp))

    SocialDivider()

    Spacer(modifier = Modifier.height(20.dp))

    SocialButtons(onGoogleLogin, onFacebookLogin)
}

@Composable
fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: () -> Unit = {},
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label, color = AuthPrimaryText.copy(alpha = 0.6f), style = TextStyle(fontWeight = FontWeight.Bold)) },
        singleLine = true,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = null,
                        tint = AuthPrimaryText
                    )
                }
            } else {
                trailingIcon?.invoke()
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = CircleShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = AuthFieldBackground,
            unfocusedContainerColor = AuthFieldBackground,
            focusedBorderColor = AuthWhite,
            unfocusedBorderColor = AuthWhite,
            cursorColor = AuthPrimaryText,
            focusedTextColor = AuthPrimaryText,
            unfocusedTextColor = AuthPrimaryText
        )
    )
}

@Composable
fun SocialDivider() {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = AuthPrimaryText, thickness = 2.dp)
        Text(
            text = "Login with",
            style = TextStyle(fontWeight = FontWeight.Bold, color = AuthWhite, fontSize = 16.sp),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = AuthPrimaryText, thickness = 2.dp)
    }
}

@Composable
fun SocialButtons(onGoogleClick: () -> Unit, onFacebookClick: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        // Google Icon
        Surface(
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 2.dp,
            onClick = onGoogleClick
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(10.dp)) {
                GoogleIcon()
            }
        }

        // Facebook Icon
        Surface(
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            color = Color(0xFF1877F2),
            shadowElevation = 2.dp,
            onClick = onFacebookClick
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(10.dp)) {
                FacebookIcon()
            }
        }
    }
}

@Composable
fun FacebookIcon() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val scale = size.width / 48f
        val white = Color.White

        val path = Path().apply {
            moveTo(48f * scale, 24f * scale)
            cubicTo(48f * scale, 10.75f * scale, 37.25f * scale, 0f * scale, 24f * scale, 0f * scale)
            cubicTo(10.75f * scale, 0f * scale, 0f * scale, 10.75f * scale, 0f * scale, 24f * scale)
            cubicTo(0f * scale, 35.97f * scale, 8.75f * scale, 45.89f * scale, 20.25f * scale, 47.69f * scale)
            lineTo(20.25f * scale, 30.94f * scale)
            lineTo(14.16f * scale, 30.94f * scale)
            lineTo(14.16f * scale, 24f * scale)
            lineTo(20.25f * scale, 24f * scale)
            lineTo(20.25f * scale, 18.71f * scale)
            cubicTo(20.25f * scale, 12.7f * scale, 23.83f * scale, 9.38f * scale, 29.3f * scale, 9.38f * scale)
            cubicTo(31.92f * scale, 9.38f * scale, 34.69f * scale, 9.84f * scale, 34.69f * scale, 9.84f * scale)
            lineTo(34.69f * scale, 15.75f * scale)
            lineTo(31.66f * scale, 15.75f * scale)
            cubicTo(28.68f * scale, 15.75f * scale, 27.75f * scale, 17.6f * scale, 27.75f * scale, 19.5f * scale)
            lineTo(27.75f * scale, 24f * scale)
            lineTo(34.41f * scale, 24f * scale)
            lineTo(33.34f * scale, 30.94f * scale)
            lineTo(27.75f * scale, 30.94f * scale)
            lineTo(27.75f * scale, 47.94f * scale)
            cubicTo(39.25f * scale, 46.14f * scale, 48f * scale, 36.14f * scale, 48f * scale, 24f * scale)
        }
        drawPath(path, white)
    }
}

@Composable
fun GoogleIcon() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val scale = size.width / 48f
        
        val red = Color(0xFFEA4335)
        val yellow = Color(0xFFFBBC05)
        val green = Color(0xFF34A853)
        val blue = Color(0xFF4285F4)

        // Blue part
        val bluePath = Path().apply {
            moveTo(46.59f * scale, 24.51f * scale)
            cubicTo(46.59f * scale, 22.96f * scale, 46.45f * scale, 21.46f * scale, 46.2f * scale, 20f * scale)
            lineTo(24f * scale, 20f * scale)
            lineTo(24f * scale, 29.03f * scale)
            lineTo(36.76f * scale, 29.03f * scale)
            cubicTo(36.21f * scale, 31.9f * scale, 34.54f * scale, 34.4f * scale, 32.13f * scale, 36.06f * scale)
            lineTo(39.86f * scale, 42.06f * scale)
            cubicTo(44.37f * scale, 37.88f * scale, 46.98f * scale, 31.73f * scale, 46.59f * scale, 24.51f * scale)
        }
        drawPath(bluePath, blue)

        // Green part
        val greenPath = Path().apply {
            moveTo(24f * scale, 48f * scale)
            cubicTo(30.48f * scale, 48f * scale, 35.93f * scale, 45.87f * scale, 39.89f * scale, 42.19f * scale)
            lineTo(32.16f * scale, 36.19f * scale)
            cubicTo(30.01f * scale, 37.64f * scale, 27.24f * scale, 38.49f * scale, 24f * scale, 38.49f * scale)
            cubicTo(17.74f * scale, 38.49f * scale, 12.43f * scale, 34.27f * scale, 10.53f * scale, 28.58f * scale)
            lineTo(2.33f * scale, 34.93f * scale)
            cubicTo(6.51f * scale, 42.62f * scale, 14.62f * scale, 48f * scale, 24f * scale, 48f * scale)
        }
        drawPath(greenPath, green)

        // Yellow part
        val yellowPath = Path().apply {
            moveTo(10.53f * scale, 28.59f * scale)
            cubicTo(10.05f * scale, 27.14f * scale, 9.77f * scale, 25.6f * scale, 9.77f * scale, 24f * scale)
            cubicTo(9.77f * scale, 22.4f * scale, 10.04f * scale, 20.86f * scale, 10.53f * scale, 19.41f * scale)
            lineTo(2.56f * scale, 13.22f * scale)
            cubicTo(0.92f * scale, 16.46f * scale, 0f * scale, 20.12f * scale, 0f * scale, 24f * scale)
            cubicTo(0f * scale, 27.88f * scale, 0.92f * scale, 31.54f * scale, 2.56f * scale, 34.78f * scale)
            lineTo(10.53f * scale, 28.59f * scale)
        }
        drawPath(yellowPath, yellow)

        // Red part
        val redPath = Path().apply {
            moveTo(24f * scale, 9.5f * scale)
            cubicTo(27.54f * scale, 9.5f * scale, 30.71f * scale, 10.72f * scale, 33.21f * scale, 13.1f * scale)
            lineTo(40.06f * scale, 6.25f * scale)
            cubicTo(35.9f * scale, 2.38f * scale, 30.47f * scale, 0f * scale, 24f * scale, 0f * scale)
            cubicTo(14.62f * scale, 0f * scale, 6.51f * scale, 5.38f * scale, 2.56f * scale, 13.22f * scale)
            lineTo(10.54f * scale, 19.41f * scale)
            cubicTo(12.43f * scale, 13.72f * scale, 17.74f * scale, 9.5f * scale, 24f * scale, 9.5f * scale)
        }
        drawPath(redPath, red)
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
    agreeEula: Boolean,
    agreeTerms: Boolean,
    agreePrivacy: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onToggleConfirmPassword: () -> Unit,
    onAgreeEulaChange: (Boolean) -> Unit,
    onAgreeTermsChange: (Boolean) -> Unit,
    onAgreePrivacyChange: (Boolean) -> Unit,
    onOpenEula: () -> Unit,
    onOpenTerms: () -> Unit,
    onOpenPrivacyConsent: () -> Unit,
    onCreateAccount: () -> Unit
) {
    StyledTextField(value = name, onValueChange = onNameChange, label = "Name")
    Spacer(modifier = Modifier.height(12.dp))
    StyledTextField(value = email, onValueChange = onEmailChange, label = "Email")
    Spacer(modifier = Modifier.height(12.dp))
    StyledTextField(value = password, onValueChange = onPasswordChange, label = "Password", isPassword = true, passwordVisible = passwordVisible, onTogglePassword = onTogglePassword)
    Spacer(modifier = Modifier.height(12.dp))
    StyledTextField(value = confirmPassword, onValueChange = onConfirmPasswordChange, label = "Confirm Password", isPassword = true, passwordVisible = confirmPasswordVisible, onTogglePassword = onToggleConfirmPassword)

    Spacer(modifier = Modifier.height(8.dp))

    // EULA Consent
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clickable { onAgreeEulaChange(!agreeEula) }
    ) {
        Checkbox(
            checked = agreeEula,
            onCheckedChange = onAgreeEulaChange,
            colors = CheckboxDefaults.colors(
                checkedColor = AuthPrimaryText,
                uncheckedColor = AuthWhite,
                checkmarkColor = AuthWhite
            )
        )
        Text(
            text = "I agree to the ",
            style = TextStyle(fontSize = 14.sp, color = AuthWhite, fontWeight = FontWeight.Bold)
        )
        Text(
            text = "EULA",
            style = TextStyle(fontSize = 14.sp, color = AuthWhite, fontWeight = FontWeight.Black, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline),
            modifier = Modifier.clickable { onOpenEula() }
        )
    }

    // Terms Consent
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clickable { onAgreeTermsChange(!agreeTerms) }
    ) {
        Checkbox(
            checked = agreeTerms,
            onCheckedChange = onAgreeTermsChange,
            colors = CheckboxDefaults.colors(
                checkedColor = AuthPrimaryText,
                uncheckedColor = AuthWhite,
                checkmarkColor = AuthWhite
            )
        )
        Text(
            text = "I agree to the ",
            style = TextStyle(fontSize = 14.sp, color = AuthWhite, fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Terms and Conditions",
            style = TextStyle(fontSize = 14.sp, color = AuthWhite, fontWeight = FontWeight.Black, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline),
            modifier = Modifier.clickable { onOpenTerms() }
        )
    }

    // Privacy Consent
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clickable { onAgreePrivacyChange(!agreePrivacy) }
    ) {
        Checkbox(
            checked = agreePrivacy,
            onCheckedChange = onAgreePrivacyChange,
            colors = CheckboxDefaults.colors(
                checkedColor = AuthPrimaryText,
                uncheckedColor = AuthWhite,
                checkmarkColor = AuthWhite
            )
        )
        Text(
            text = "I agree to the ",
            style = TextStyle(fontSize = 14.sp, color = AuthWhite, fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Privacy and Location Consent",
            style = TextStyle(fontSize = 14.sp, color = AuthWhite, fontWeight = FontWeight.Black, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline),
            modifier = Modifier.clickable { onOpenPrivacyConsent() }
        )
    }

    (validationMessage ?: backendError)?.let {
        Text(text = it, color = Color.Red, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = onCreateAccount,
        modifier = Modifier.width(200.dp).height(54.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = AuthPrimaryButton),
        enabled = !isLoading && agreeEula && agreeTerms && agreePrivacy,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = AuthWhite, modifier = Modifier.size(24.dp))
        } else {
            Text("Create Account", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Black, color = AuthWhite))
        }
    }
}
