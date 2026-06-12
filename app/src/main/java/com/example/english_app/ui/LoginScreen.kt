package com.example.english_app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import java.util.regex.Pattern
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.shadow
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import com.example.english_app.ui.theme.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

import com.example.english_app.R
import androidx.compose.ui.layout.ContentScale

@Composable
fun LoginScreen(
    onLoginClick: (String, String, Boolean) -> Unit = { _, _, _ -> },
    onForgotPassword: (() -> Unit)? = null,
    onSignUp: (() -> Unit)? = null,
    onGoogleLogin: (() -> Unit)? = null,
    onFacebookLogin: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val auth = remember { FirebaseAuth.getInstance() }

    // Animation for floating elements
    val infiniteTransition = rememberInfiniteTransition()
    val floatAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    fun isEmailValid(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        )
        return emailPattern.matcher(email).matches()
    }

    val isFormValid = email.isNotBlank() && password.isNotBlank() && isEmailValid(email)

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Animated background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GradientStart,
                            GradientEnd,
                            GradientOrangeStart,
                            GradientOrangeEnd
                        )
                    )
                )
        )

        // Floating decorative elements
        Box(
            modifier = Modifier
                .size(100.dp)
                .offset(
                    x = (20 + floatAnimation * 20).dp,
                    y = (100 + floatAnimation * 30).dp
                )
                .background(
                    color = VibrantYellow.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(50)
                )
        )

        Box(
            modifier = Modifier
                .size(80.dp)
                .offset(
                    x = (300 + floatAnimation * -15).dp,
                    y = (200 + floatAnimation * 25).dp
                )
                .background(
                    color = VibrantPink.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(40)
                )
        )

        // Main content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .offset(y = (20 + floatAnimation * 10).dp),
            contentAlignment = Alignment.Center
        ) {
            // Make the login form scrollable
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                // College name at the top
                Text(
                    text = "Sri Ramakrishna College of Arts & Science",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                // Character/avatar above the login form
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(40.dp))
                        .background(VibrantBlue)
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    if (email.isNotBlank()) {
                        Text(
                            text = email.take(1).uppercase(),
                            color = Color.White, // Always white on VibrantBlue background
                            fontWeight = FontWeight.Bold,
                            fontSize = 36.sp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            
                // App logo/title
                Image(
                    painter = painterResource(id = R.drawable.ic_app_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .shadow(20.dp, RoundedCornerShape(60)),
                    contentScale = ContentScale.Fit
                )
            
                Spacer(modifier = Modifier.height(24.dp))
            
                Text(
                    text = "Welcome Back!",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            
                Text(
                    text = "Sign in to continue your English journey",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
            
                Spacer(modifier = Modifier.height(40.dp))
            
                // Login form card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(20.dp, RoundedCornerShape(20)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Login",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Email field
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                if (error.isNotEmpty()) error = ""
                            },
                            label = { Text("Email") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email",
                                    tint = VibrantBlue
                                )
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { contentDescription = "emailField" },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            isError = email.isNotBlank() && !isEmailValid(email),
                            supportingText = {
                                if (email.isNotBlank() && !isEmailValid(email)) {
                                    Text("Invalid email format", color = VibrantRed)
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VibrantBlue,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                                focusedLabelColor = VibrantBlue,
                                unfocusedLabelColor = Color.Black,
                                cursorColor = Color.Black,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Password field
                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                if (error.isNotEmpty()) error = ""
                            },
                            label = { Text("Password") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Password",
                                    tint = VibrantBlue
                                )
                            },
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                                val desc = if (passwordVisible) "Hide password" else "Show password"
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = icon, contentDescription = desc, tint = VibrantBlue)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { contentDescription = "passwordField" },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    if (isFormValid) {
                                        loading = true
                                        error = ""
                                        auth.signInWithEmailAndPassword(email, password)
                                            .addOnCompleteListener { task ->
                                                loading = false
                                                if (task.isSuccessful) {
                                                    onLoginClick(email, password, rememberMe)
                                                } else {
                                                    error = task.exception?.localizedMessage ?: "Login failed."
                                                }
                                            }
                                    } else {
                                        error = "Please enter valid credentials."
                                    }
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VibrantBlue,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                                focusedLabelColor = VibrantBlue,
                                unfocusedLabelColor = Color.Black,
                                cursorColor = Color.Black,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Remember me and forgot password
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                modifier = Modifier.semantics { contentDescription = "rememberMeCheckbox" },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = VibrantBlue,
                                    uncheckedColor = Color.Gray
                                )
                            )
                            Text(
                                "Remember Me",
                                color = PrimaryText,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "Forgot Password?",
                                color = VibrantBlue,
                                textDecoration = TextDecoration.Underline,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .clickable { onForgotPassword?.invoke() }
                                    .semantics { contentDescription = "forgotPassword" }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (error.isNotEmpty()) {
                            Text(
                                text = error,
                                color = VibrantRed,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        
                        // Login button
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                if (isFormValid) {
                                    loading = true
                                    error = ""
                                    println("DEBUG: Attempting login with email: $email")
                                    auth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { task ->
                                            loading = false
                                            if (task.isSuccessful) {
                                                println("DEBUG: Login successful")
                                                onLoginClick(email, password, rememberMe)
                                            } else {
                                                println("DEBUG: Login failed: ${task.exception?.message}")
                                                error = task.exception?.localizedMessage ?: "Login failed."
                                            }
                                        }
                                } else {
                                    println("DEBUG: Form validation failed")
                                    error = "Please enter valid credentials."
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .semantics { contentDescription = "loginButton" },
                            enabled = isFormValid && !loading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VibrantBlue,
                                disabledContainerColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            if (loading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "Login",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Test login button (for development/testing)
                        OutlinedButton(
                            onClick = {
                                println("DEBUG: Test login clicked")
                                onLoginClick("test@example.com", "password", false)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = VibrantGreen
                            ),
                            border = BorderStroke(1.dp, VibrantGreen),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                "Test Login (Skip Authentication)",
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Divider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(Color.Gray.copy(alpha = 0.3f))
                            )
                            Text(
                                "OR",
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = SecondaryText,
                                fontSize = 14.sp
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(Color.Gray.copy(alpha = 0.3f))
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Social login buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Google button (classic)
                            Button(
                                onClick = { onGoogleLogin?.invoke() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = VibrantBlue
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(2.dp, VibrantBlue)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = android.R.drawable.ic_dialog_email),
                                        contentDescription = "Google",
                                        tint = VibrantBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Sign in with Google",
                                        color = VibrantBlue,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            // Facebook button (unchanged)
                            Button(
                                onClick = { onFacebookLogin?.invoke() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1877F2)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        "Facebook",
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        // Sign up link
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Don't have an account? ",
                                color = SecondaryText,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Sign Up",
                                color = VibrantBlue,
                                textDecoration = TextDecoration.Underline,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .clickable { onSignUp?.invoke() }
                                    .semantics { contentDescription = "signUp" }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}