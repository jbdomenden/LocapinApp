package com.locapin.mobile.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.outlined.Facebook
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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

private val AuthSpacing = 14.dp
private val OuterGradient = listOf(Color(0xFF9F325D), Color(0xFFFF7A87), Color(0xFF9F325D))
private val SurfaceColor = Color(0xFFF5F0E8)
private val CardColor = Color(0xFFE7B6C3)
private val BorderPink = Color(0xFFD87795)
private val AccentGold = Color(0xFFC1882E)
private val AccentPink = Color(0xFFFA2D9D)
private val AccentOrange = Color(0xFFF6C846)
private val InputColor = Color(0xFFF6F1EA)

@Composable
fun LoginScreen(
    onRegister: () -> Unit,
    onForgotPassword: () -> Unit,
    onSuccess: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    if (state.isAuthenticated) onSuccess()
    AuthScreenContent(
        state = state,
        title = "Login / Sign-Up",
        subtitle = "Welcome Back!",
        onUsernameChange = vm::onUsernameChange,
        onPasswordChange = vm::onPasswordChange,
        onClearUsername = vm::clearUsername,
        onTogglePassword = vm::togglePasswordVisibility,
        onForgotPassword = {
            vm.forgotPassword()
            onForgotPassword()
        },
        onPrimaryAction = vm::login,
        onGoogleClick = { vm.socialLogin("Google") },
        onFacebookClick = { vm.socialLogin("Facebook") },
        onPhoneClick = { vm.socialLogin("Phone") },
        actionLabel = "Login",
        footerAction = {
            Text(
                text = "Create account",
                color = BorderPink,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(onClick = onRegister)
                    .padding(vertical = 6.dp, horizontal = 10.dp)
            )
        }
    )
}

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    if (state.isAuthenticated) onSuccess()
    AuthScreenContent(
        state = state,
        title = "Create Account",
        subtitle = "Join LocaPin",
        onUsernameChange = vm::onUsernameChange,
        onPasswordChange = vm::onPasswordChange,
        onClearUsername = vm::clearUsername,
        onTogglePassword = vm::togglePasswordVisibility,
        onForgotPassword = onBack,
        onPrimaryAction = vm::register,
        onGoogleClick = { vm.socialLogin("Google") },
        onFacebookClick = { vm.socialLogin("Facebook") },
        onPhoneClick = { vm.socialLogin("Phone") },
        actionLabel = "Sign Up",
        footerAction = {
            Text(
                text = "Back to login",
                color = BorderPink,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(onClick = onBack)
                    .padding(vertical = 6.dp, horizontal = 10.dp)
            )
        }
    )
}

@Composable
fun ForgotPasswordScreen(onBack: () -> Unit, vm: AuthViewModel = hiltViewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    AuthScreenContent(
        state = state,
        title = "Forgot Password",
        subtitle = "Recover your account",
        onUsernameChange = vm::onUsernameChange,
        onPasswordChange = vm::onPasswordChange,
        onClearUsername = vm::clearUsername,
        onTogglePassword = vm::togglePasswordVisibility,
        onForgotPassword = onBack,
        onPrimaryAction = vm::forgotPassword,
        onGoogleClick = { vm.socialLogin("Google") },
        onFacebookClick = { vm.socialLogin("Facebook") },
        onPhoneClick = { vm.socialLogin("Phone") },
        actionLabel = "Send Link",
        footerAction = {
            Text(
                text = "Back",
                color = BorderPink,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(onClick = onBack)
                    .padding(vertical = 6.dp, horizontal = 10.dp)
            )
        }
    )
}

@Composable
private fun AuthScreenContent(
    state: AuthUiState,
    title: String,
    subtitle: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onClearUsername: () -> Unit,
    onTogglePassword: () -> Unit,
    onForgotPassword: () -> Unit,
    onPrimaryAction: () -> Unit,
    onGoogleClick: () -> Unit,
    onFacebookClick: () -> Unit,
    onPhoneClick: () -> Unit,
    actionLabel: String,
    footerAction: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.horizontalGradient(OuterGradient))
            .padding(AuthSpacing)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(2.dp))
                .background(SurfaceColor)
                .padding(AuthSpacing)
        ) {
            TopOverlay(modifier = Modifier.align(Alignment.TopEnd))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(4.dp, BorderPink, RoundedCornerShape(64.dp))
                        .clip(RoundedCornerShape(64.dp))
                        .background(SurfaceColor)
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        LoginHeader()
                        Spacer(Modifier.height(28.dp))
                        GradientDivider()
                        Spacer(Modifier.height(20.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(38.dp))
                                .background(CardColor)
                                .border(3.dp, BorderPink, RoundedCornerShape(38.dp))
                                .shadow(8.dp, RoundedCornerShape(38.dp), clip = false)
                                .padding(horizontal = 26.dp, vertical = 28.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    title,
                                    style = TextStyle(
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFFCE3F0),
                                        shadow = androidx.compose.ui.graphics.Shadow(
                                            color = BorderPink,
                                            blurRadius = 2f
                                        )
                                    )
                                )
                                Text(
                                    subtitle,
                                    style = TextStyle(
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentGold
                                    )
                                )
                                Spacer(Modifier.height(26.dp))
                                RoundedInputField(
                                    value = state.username,
                                    placeholder = "Username",
                                    onValueChange = onUsernameChange,
                                    trailing = {
                                        IconButton(onClick = onClearUsername) {
                                            Icon(Icons.Default.Close, contentDescription = "Clear username")
                                        }
                                    }
                                )
                                Spacer(Modifier.height(12.dp))
                                PasswordInputField(
                                    value = state.password,
                                    isVisible = state.isPasswordVisible,
                                    onValueChange = onPasswordChange,
                                    onToggle = onTogglePassword
                                )
                                Text(
                                    text = "Forgot Password?",
                                    color = AccentGold,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .align(Alignment.Start)
                                        .padding(start = 8.dp, top = 8.dp)
                                        .clickable(onClick = onForgotPassword)
                                )
                                Spacer(Modifier.height(28.dp))
                                LoginButton(
                                    label = actionLabel,
                                    isLoading = state.isLoading,
                                    onClick = onPrimaryAction
                                )
                                state.errorMessage?.let {
                                    Spacer(Modifier.height(10.dp))
                                    Text(text = it, color = Color(0xFFB02645), textAlign = TextAlign.Center)
                                }
                                Spacer(Modifier.height(26.dp))
                                GradientDivider("Login with")
                                Spacer(Modifier.height(12.dp))
                                SocialLoginRow(
                                    onGoogleClick = onGoogleClick,
                                    onFacebookClick = onFacebookClick,
                                    onPhoneClick = onPhoneClick
                                )
                                Spacer(Modifier.height(12.dp))
                                footerAction()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoginHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Locapin",
            style = TextStyle(
                color = Color(0xFFE7B6C3),
                fontSize = 68.sp,
                fontWeight = FontWeight.ExtraBold,
                shadow = androidx.compose.ui.graphics.Shadow(color = Color(0xFF6B3C50), blurRadius = 2f)
            )
        )
        Text(
            "A new chapter for San Juan exploration",
            color = AccentGold,
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun GradientDivider(text: String? = null) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(Brush.horizontalGradient(listOf(AccentPink, Color(0xFFFF8B8B), AccentOrange)))
        )
        if (text != null) {
            Text(
                text = text,
                color = Color(0xFFFCE3F0),
                style = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 22.sp),
                modifier = Modifier
                    .background(CardColor)
                    .padding(horizontal = 12.dp)
            )
        }
    }
}

@Composable
fun RoundedInputField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(InputColor)
            .padding(start = 18.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            visualTransformation = visualTransformation,
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD785A4)
            ),
            cursorBrush = SolidColor(BorderPink),
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                if (value.isBlank()) {
                    Text(placeholder, color = Color(0xFFDDA4BA), fontWeight = FontWeight.Bold)
                }
                inner()
            }
        )
        trailing()
    }
}

@Composable
fun PasswordInputField(
    value: String,
    isVisible: Boolean,
    onValueChange: (String) -> Unit,
    onToggle: () -> Unit
) {
    RoundedInputField(
        value = value,
        placeholder = "Password",
        onValueChange = onValueChange,
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailing = {
            IconButton(onClick = onToggle) {
                Icon(
                    if (isVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = "Toggle password visibility"
                )
            }
        }
    )
}

@Composable
fun LoginButton(label: String, isLoading: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(190.dp)
            .height(72.dp)
            .shadow(10.dp, RoundedCornerShape(32.dp))
            .clip(RoundedCornerShape(32.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFFFF9AA0), Color(0xFFFFB57C))))
            .clickable(
                enabled = !isLoading,
                interactionSource = MutableInteractionSource(),
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
        else Text(label, color = Color(0xFFF6E6F2), fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
    }
}

@Composable
fun SocialLoginRow(onGoogleClick: () -> Unit, onFacebookClick: () -> Unit, onPhoneClick: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
        SocialCircle("G", Color(0xFF9B4BFF), onGoogleClick)
        SocialCircle("f", Color(0xFF3559C7), onFacebookClick, icon = Icons.Outlined.Facebook)
        SocialCircle("☎", Color(0xFF1A1A1A), onPhoneClick, icon = Icons.Default.Call)
    }
}

@Composable
private fun SocialCircle(text: String, tint: Color, onClick: () -> Unit, icon: androidx.compose.ui.graphics.vector.ImageVector? = null) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = text, tint = tint, modifier = Modifier.size(28.dp))
        } else {
            Text(text = text, color = tint, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun TopOverlay(modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.QrCode, contentDescription = "QR placeholder", tint = BorderPink)
        }
        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF7A2A4)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Map, contentDescription = "App icon placeholder", tint = Color.White)
        }
    }
}

@Preview(name = "Login - Pixel style", widthDp = 412, heightDp = 915, showBackground = true)
@Composable
private fun LoginScreenPreview() {
    AuthScreenContent(
        state = AuthUiState(username = "", password = ""),
        title = "Login / Sign-Up",
        subtitle = "Welcome Back!",
        onUsernameChange = {},
        onPasswordChange = {},
        onClearUsername = {},
        onTogglePassword = {},
        onForgotPassword = {},
        onPrimaryAction = {},
        onGoogleClick = {},
        onFacebookClick = {},
        onPhoneClick = {},
        actionLabel = "Login",
        footerAction = {}
    )
}

@Preview(name = "Login - Dark", widthDp = 412, heightDp = 915, showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LoginScreenDarkPreview() = LoginScreenPreview()

@Preview(name = "Login - Small height", widthDp = 412, heightDp = 640, showBackground = true)
@Composable
private fun LoginScreenSmallPreview() = LoginScreenPreview()
