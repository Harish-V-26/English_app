package com.example.english_app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.platform.LocalFocusManager
import java.util.regex.Pattern
import androidx.compose.animation.core.*
import androidx.compose.ui.unit.sp
import com.example.english_app.ui.theme.*
import androidx.compose.ui.draw.clip
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun SignUpScreen(
    onSignUpClick: (String, String, String, Boolean) -> Unit = { _, _, _, _ -> },
    onBackToLogin: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var agreeToTerms by remember { mutableStateOf(false) }
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

    val isFormValid = name.isNotBlank() && 
                     email.isNotBlank() && 
                     password.isNotBlank() && 
                     confirmPassword.isNotBlank() && 
                     isEmailValid(email) && 
                     password == confirmPassword && 
                     password.length >= 6 &&
                     agreeToTerms

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
                            GradientGreenStart,
                            GradientGreenEnd,
                            VibrantTeal,
                            VibrantBlue
                        )
                    )
                )
        )

        // Floating decorative elements
        Box(
            modifier = Modifier
                .size(120.dp)
                .offset(
                    x = (300 + floatAnimation * -25).dp,
                    y = (80 + floatAnimation * 20).dp
                )
                .background(
                    color = VibrantOrange.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(60)
                )
        )

        Box(
            modifier = Modifier
                .size(90.dp)
                .offset(
                    x = (30 + floatAnimation * 15).dp,
                    y = (250 + floatAnimation * -20).dp
                )
                .background(
                    color = VibrantPurple.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(45)
                )
        )

        // Main content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(32.dp)
                .offset(y = (20 + floatAnimation * 10).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // App logo/title
            Card(
                modifier = Modifier
                    .size(100.dp)
                    .shadow(20.dp, RoundedCornerShape(50)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(50)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "EN",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = VibrantGreen
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = "Join Our Community!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Create your account to start learning English",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Character/avatar above the sign-up form
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(VibrantGreen)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                val avatarLetter = when {
                    name.isNotBlank() -> name.take(1).uppercase()
                    email.isNotBlank() -> email.take(1).uppercase()
                    else -> ""
                }
                if (avatarLetter.isNotBlank()) {
                    Text(
                        text = avatarLetter,
                        color = Color.White,
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
            
            // Sign up form card
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
                        text = "Sign Up",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Name field
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            if (error.isNotEmpty()) error = ""
                        },
                        label = { Text("Full Name") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Name",
                                tint = VibrantGreen
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "nameField" },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VibrantGreen,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                            focusedLabelColor = VibrantGreen,
                            unfocusedLabelColor = PrimaryText,
                            cursorColor = PrimaryText,
                            focusedTextColor = PrimaryText,
                            unfocusedTextColor = PrimaryText
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
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
                                tint = VibrantGreen
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
                                Text("Invalid email format", color = Color.Black)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VibrantGreen,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                            focusedLabelColor = VibrantGreen,
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
                                tint = VibrantGreen
                            )
                        },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                            val desc = if (passwordVisible) "Hide password" else "Show password"
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = icon, contentDescription = desc, tint = VibrantGreen)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "passwordField" },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        isError = password.isNotBlank() && password.length < 6,
                        supportingText = {
                            if (password.isNotBlank() && password.length < 6) {
                                Text("Password must be at least 6 characters", color = Color.Black)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VibrantGreen,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                            focusedLabelColor = VibrantGreen,
                            unfocusedLabelColor = Color.Black,
                            cursorColor = Color.Black,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Confirm Password field
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            if (error.isNotEmpty()) error = ""
                        },
                        label = { Text("Confirm Password") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Confirm Password",
                                tint = VibrantGreen
                            )
                        },
                        singleLine = true,
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                            val desc = if (confirmPasswordVisible) "Hide password" else "Show password"
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(imageVector = icon, contentDescription = desc, tint = VibrantGreen)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "confirmPasswordField" },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        isError = confirmPassword.isNotBlank() && password != confirmPassword,
                        supportingText = {
                            if (confirmPassword.isNotBlank() && password != confirmPassword) {
                                Text("Passwords don't match", color = Color.Black)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VibrantGreen,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                            focusedLabelColor = VibrantGreen,
                            unfocusedLabelColor = Color.Black,
                            cursorColor = Color.Black,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black // <-- dark color for confirm password
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Terms and conditions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = agreeToTerms,
                            onCheckedChange = { agreeToTerms = it },
                            modifier = Modifier.semantics { contentDescription = "agreeToTermsCheckbox" },
                            colors = CheckboxDefaults.colors(
                                checkedColor = VibrantGreen,
                                uncheckedColor = Color.Gray
                            )
                        )
                        Text(
                            "I agree to the Terms and Conditions",
                            color = PrimaryText,
                            fontSize = 14.sp
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
                    
                    // Sign up button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            if (isFormValid) {
                                loading = true
                                error = ""
                                println("DEBUG: Attempting signup with email: $email")
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        loading = false
                                        if (task.isSuccessful) {
                                            println("DEBUG: Signup successful")
                                            onSignUpClick(name, email, password, agreeToTerms)
                                        } else {
                                            println("DEBUG: Signup failed: ${task.exception?.message}")
                                            error = task.exception?.localizedMessage ?: "Sign up failed."
                                        }
                                    }
                            } else {
                                println("DEBUG: Form validation failed")
                                error = "Please fill all fields correctly and agree to terms."
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .semantics { contentDescription = "signUpButton" },
                        enabled = isFormValid && !loading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VibrantGreen,
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
                                "Create Account",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Back to login link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Already have an account? ",
                            color = SecondaryText,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Login",
                            color = VibrantGreen,
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .clickable { onBackToLogin?.invoke() }
                                .semantics { contentDescription = "backToLogin" }
                        )
                    }

                    // Add Sign In button below the login link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedButton(
                            onClick = { onBackToLogin?.invoke() },
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(48.dp)
                                .semantics { contentDescription = "signInButton" },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, VibrantGreen),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = VibrantGreen)
                        ) {
                            Text("Sign In", fontWeight = FontWeight.Bold, color = VibrantGreen)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    SignUpScreen()
} 