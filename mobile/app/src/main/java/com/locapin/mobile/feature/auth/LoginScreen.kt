package com.locapin.mobile.feature.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
    val context = LocalContext.current
    val callbackManager = remember { CallbackManager.Factory.create() }

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

                val activity = context as? Activity
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                ) {
                    Button(
                        onClick = {
                            googleSignInClient.signOut().addOnCompleteListener {
                                googleLoginLauncher.launch(googleSignInClient.signInIntent)
                            }
                        },
                        enabled = !state.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AuthFieldBackground,
                            contentColor = AuthPrimaryText
                        )
                    ) {
                        Text("Google")
                    }
                    Button(
                        onClick = {
                            if (activity == null) {
                                vm.onSocialLoginError("Facebook sign-in is unavailable in this context.")
                            } else {
                                LoginManager.getInstance().logInWithReadPermissions(
                                    activity,
                                    listOf("email", "public_profile")
                                )
                            }
                        },
                        enabled = !state.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AuthFieldBackground,
                            contentColor = AuthPrimaryText
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
            }
        }
    }
}
