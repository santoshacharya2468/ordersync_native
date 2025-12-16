package com.orderpush.app.features.auth.presentation.view
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.orderpush.app.R
import com.orderpush.app.core.router.LocalNavigation
import com.orderpush.app.core.router.Screen
import com.orderpush.app.features.auth.presentation.viewmodel.AuthState
import com.orderpush.app.features.auth.presentation.viewmodel.AuthViewModel

@Composable
fun LoginScreen() {
        val viewModel = hiltViewModel<AuthViewModel>()
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        val loginState by viewModel.loginState.collectAsState()
        val navigator = LocalNavigation.current
        val context = LocalContext.current
        val infiniteTransition = rememberInfiniteTransition(label = "background")
        val animatedAlpha by infiniteTransition.animateFloat(
            initialValue = 0.7f,
            targetValue = 0.7f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alpha"
        )

        LaunchedEffect(loginState) {
            when (loginState) {
                is AuthState.Error -> {
                    Toast.makeText(
                        context,
                        (loginState as AuthState.Error).message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                AuthState.Idle -> {}
                AuthState.Loading -> {}
                AuthState.LogoutSuccess ->{}
                is AuthState.Success -> {

                    navigator.replaceAll(Screen.DashboardSelection)
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF060900).copy(alpha = animatedAlpha),
                            Color(0xFF695656).copy(alpha = animatedAlpha),
                            Color(0xFF1C0B0B).copy(alpha = animatedAlpha)
                        )
                    )
                )
        ) {
            // Decorative circles
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .offset(x = (-50).dp, y = (-50).dp)
                    .alpha(0.1f)
                    .background(
                        MaterialTheme.colorScheme.background,
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .size(150.dp)
                    .offset(x = 250.dp, y = 100.dp)
                    .alpha(0.1f)
                    .background(
                        Color.White,
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()

                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // App Logo/Icon placeholder
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.9f),
                    shadowElevation = 8.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "OP",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Welcome Text
                Text(
                    text = "Welcome Back!",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Sign in to continue to OrderPush",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                Card(
                    modifier = Modifier

                       .widthIn(max=800.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(

                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp) ,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Login",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.SemiBold,

                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Email Field
                        StyledTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email or Phone",
                            icon = Icons.Default.Email,
                            placeholder = "Enter your email or phone"
                        )

                        // Password Field
                        StyledTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Password",
                            icon = Icons.Default.Lock,
                            placeholder = "Enter your password",
                            isPassword = true,
                            passwordVisible = passwordVisible,
                            onPasswordVisibilityChange = { passwordVisible = !passwordVisible }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Login Button
                        when (loginState) {
                            is AuthState.Loading -> {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color(0xFF667eea)
                                    )
                                }
                            }
                            else -> {
                                Button(
                                    onClick = { viewModel.login(email, password) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF667eea)
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(
                                        defaultElevation = 4.dp,
                                        pressedElevation = 2.dp
                                    ),
                                    enabled = email.isNotBlank() && password.isNotBlank()
                                ) {
                                    Text(
                                        text = "Sign In",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // Forgot Password
                        TextButton(
                            onClick = { /* Handle forgot password */ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Forgot Password?",
                                color = Color(0xFF667eea),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Footer
                Text(
                    text = "Don't have an account?",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium
                )

                TextButton(
                    onClick = { /* Handle sign up */ }
                ) {
                    Text(
                        text = "Sign Up",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }


            }
        }
    }

@Composable
private fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    placeholder: String,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityChange: (() -> Unit)? = null
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,

                    )
            },
            trailingIcon = if (isPassword) {
                {
                    IconButton(
                        onClick = { onPasswordVisibilityChange?.invoke() }
                    ) {
                        Icon(
                            painter = painterResource(id = if (passwordVisible) R.drawable.outline_visibility else R.drawable.outline_visibility_off),
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = Color(0xFF9CA3AF)
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            shape = RoundedCornerShape(12.dp),

            colors = OutlinedTextFieldDefaults.colors(

            ),
            singleLine = true
        )

    }
}