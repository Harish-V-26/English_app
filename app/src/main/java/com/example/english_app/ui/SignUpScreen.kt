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
<<<<<<< HEAD
=======
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import com.example.english_app.R
import com.example.english_app.data.UserProgressRepository

private const val ALLOWED_DOMAIN = "@srcas.ac.in"

<<<<<<< HEAD
=======
@androidx.compose.material3.ExperimentalMaterial3Api
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
@Composable
fun SignUpScreen(
    onSignUpClick: (String, String, String, Boolean) -> Unit = { _, _, _, _ -> },
    onBackToLogin: (() -> Unit)? = null,
    onGoogleSignUp: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var rollNo by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
<<<<<<< HEAD
=======
    var departmentDropdownExpanded by remember { mutableStateOf(false) }
    val departmentOptions = listOf(
        "Computer Science",
        "Computer Applications",
        "English",
        "Commerce",
        "Business Administration",
        "Mathematics",
        "Physics",
        "Chemistry",
        "Botany",
        "Zoology",
        "Visual Communication",
        "Tamil",
        "Economics",
        "History"
    )
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
    var secretKey by remember { mutableStateOf("") }
    var isTeacher by remember { mutableStateOf(false) }
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

    fun isEmailDomainValid(email: String): Boolean {
        return email.lowercase().endsWith(ALLOWED_DOMAIN)
    }

    val isFormValid = name.isNotBlank() && 
                     email.isNotBlank() && 
                     password.isNotBlank() && 
                     confirmPassword.isNotBlank() && 
                     (isTeacher || rollNo.isNotBlank()) &&
                     department.isNotBlank() &&
                     isEmailValid(email) && 
                     isEmailDomainValid(email) &&
                     password == confirmPassword && 
                     password.length >= 6 &&
                     agreeToTerms &&
                     (!isTeacher || secretKey == "srcas@tec#123")

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Animated background gradient — matching Login screen theme
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
                .size(120.dp)
                .offset(
                    x = (300 + floatAnimation * -25).dp,
                    y = (80 + floatAnimation * 20).dp
                )
                .background(
                    color = VibrantYellow.copy(alpha = 0.3f),
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
                    color = VibrantPink.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(45)
                )
        )

        Box(
            modifier = Modifier
                .size(70.dp)
                .offset(
                    x = (200 + floatAnimation * 10).dp,
                    y = (650 + floatAnimation * -15).dp
                )
                .background(
                    color = VibrantPurple.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(35)
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

<<<<<<< HEAD
            // College logo and name at the top
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(8.dp, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.clg),
                        contentDescription = "SRCAS Shield Logo",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "Sri Ramakrishna College\nof Arts & Science",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // App Logo
            Image(
                painter = painterResource(id = R.drawable.wordwhiz_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "WORDWhiz",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "VOCABULARY LEARNING APP",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        
            Spacer(modifier = Modifier.height(16.dp))
        
            Text(
                text = "Join Our Community!",
                style = MaterialTheme.typography.titleLarge,
=======
            // College name at the top — matching Login screen
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
            
            // College logo
            Card(
                modifier = Modifier
                    .size(150.dp)
                    .shadow(6.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.clg),
                    contentDescription = "SRCAS Shield Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Join Our Community!",
                style = MaterialTheme.typography.headlineLarge,
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
<<<<<<< HEAD
        
            Text(
                text = "Create your account to start learning English",
                style = MaterialTheme.typography.bodyMedium,
=======
            
            Text(
                text = "Create your account to start learning English",
                style = MaterialTheme.typography.bodyLarge,
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Sign up form card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
<<<<<<< HEAD
                    .shadow(20.dp, RoundedCornerShape(20)),
=======
                    .shadow(6.dp, RoundedCornerShape(20)),
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Sign Up",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Domain restriction notice
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = VibrantBlue.copy(alpha = 0.08f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = VibrantBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Only @srcas.ac.in emails are allowed",
                                color = VibrantBlue,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Role Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { isTeacher = false },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isTeacher) VibrantBlue else Color.Transparent,
                                contentColor = if (!isTeacher) Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(8.dp),
                            elevation = null
                        ) {
                            Text("Student", fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { isTeacher = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isTeacher) VibrantBlue else Color.Transparent,
                                contentColor = if (isTeacher) Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(8.dp),
                            elevation = null
                        ) {
                            Text("Teacher", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    
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
                                tint = VibrantBlue
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "nameField" },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VibrantBlue,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                            focusedLabelColor = VibrantBlue,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isTeacher) {
                        // Roll No field
                        OutlinedTextField(
                            value = rollNo,
                            onValueChange = {
                                rollNo = it
                                if (error.isNotEmpty()) error = ""
                            },
                            label = { Text("Roll Number") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Roll Number",
                                    tint = VibrantBlue
                                )
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { contentDescription = "rollNoField" },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VibrantBlue,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                                focusedLabelColor = VibrantBlue,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.primary,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    } else {
                        // Teacher Secret Key field
                        OutlinedTextField(
                            value = secretKey,
                            onValueChange = {
                                secretKey = it
                                if (error.isNotEmpty()) error = ""
                            },
                            label = { Text("Teacher Secret Key") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Secret Key",
                                    tint = VibrantBlue
                                )
                            },
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VibrantBlue,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                                focusedLabelColor = VibrantBlue,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.primary,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }

<<<<<<< HEAD
                    // Department field
                    OutlinedTextField(
                        value = department,
                        onValueChange = {
                            department = it
                            if (error.isNotEmpty()) error = ""
                        },
                        label = { Text("Department") },
                        placeholder = { Text("e.g. Computer Science") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "Department",
                                tint = VibrantBlue
                            )
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { contentDescription = "departmentField" },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VibrantBlue,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                            focusedLabelColor = VibrantBlue,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
=======
                    // Department field — fixed dropdown so every student's department
                    // is stored with the exact same spelling/casing (e.g. always
                    // "Computer Science"), which is required for the Admin Panel's
                    // department filter to group students correctly.
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = department,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Department") },
                            placeholder = { Text("Select your department") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = "Department",
                                    tint = VibrantBlue
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = if (departmentDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = VibrantBlue
                                )
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { contentDescription = "departmentField" },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VibrantBlue,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                                focusedLabelColor = VibrantBlue,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.primary,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { departmentDropdownExpanded = !departmentDropdownExpanded }
                        )
                        DropdownMenu(
                            expanded = departmentDropdownExpanded,
                            onDismissRequest = { departmentDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            departmentOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, fontSize = 15.sp) },
                                    onClick = {
                                        department = option
                                        departmentDropdownExpanded = false
                                        if (error.isNotEmpty()) error = ""
                                    }
                                )
                            }
                        }
                    }
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69

                    // Space between Department and Email fields
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Email field
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (error.isNotEmpty()) error = ""
                        },
                        label = { Text("College Email") },
                        placeholder = { Text("yourname@srcas.ac.in") },
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
                        isError = email.isNotBlank() && (!isEmailValid(email) || !isEmailDomainValid(email)),
                        supportingText = {
                            when {
                                email.isNotBlank() && !isEmailValid(email) -> {
                                    Text("Invalid email format", color = VibrantRed)
                                }
                                email.isNotBlank() && isEmailValid(email) && !isEmailDomainValid(email) -> {
                                    Text("Only @srcas.ac.in emails are allowed", color = VibrantRed)
                                }
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
                            imeAction = ImeAction.Next
                        ),
                        isError = password.isNotBlank() && password.length < 6,
                        supportingText = {
                            if (password.isNotBlank() && password.length < 6) {
                                Text("Password must be at least 6 characters", color = VibrantRed)
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
                                tint = VibrantBlue
                            )
                        },
                        singleLine = true,
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                            val desc = if (confirmPasswordVisible) "Hide password" else "Show password"
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(imageVector = icon, contentDescription = desc, tint = VibrantBlue)
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
                                Text("Passwords don't match", color = VibrantRed)
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
                                checkedColor = VibrantBlue,
                                uncheckedColor = Color.Gray
                            )
                        )
                        Text(
                            "I agree to the Terms and Conditions",
                            color = MaterialTheme.colorScheme.onSurface,
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
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        loading = false
                                        if (task.isSuccessful) {
                                            // Save profile data to Firestore
                                            UserProgressRepository.saveUserProfile(
                                                name = name,
                                                rollNo = rollNo,
                                                department = department,
<<<<<<< HEAD
                                                role = if (isTeacher) "admin" else "student",
=======
                                                role = if (isTeacher) "teacher" else "student",
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
                                                email = email
                                            )
                                            onSignUpClick(name, email, password, agreeToTerms)
                                        } else {
                                            error = task.exception?.localizedMessage ?: "Sign up failed."
                                        }
                                    }
                            } else if (email.isNotBlank() && !isEmailDomainValid(email)) {
                                error = "Only @srcas.ac.in emails are allowed."
                            } else {
                                error = "Please fill all fields correctly and agree to terms."
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .semantics { contentDescription = "signUpButton" },
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
                                "Create Account",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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

                    // Google Sign-Up button
                    OutlinedButton(
                        onClick = { 
                            if (isTeacher && secretKey != "srcas@tec#123") {
                                error = "Invalid Teacher Secret Key"
                            } else {
                                onGoogleSignUp?.invoke() 
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .semantics { contentDescription = "googleSignUpButton" },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, Color.Gray.copy(alpha = 0.4f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_google),
                                contentDescription = "Google",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Sign up with Google",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Google domain hint
                    Text(
                        text = "Use your @srcas.ac.in Google account",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(28.dp))
                    
                    // Back to login link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Already have an account? ",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Login",
                            color = VibrantBlue,
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .clickable { onBackToLogin?.invoke() }
                                .semantics { contentDescription = "backToLogin" }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
<<<<<<< HEAD
=======
@androidx.compose.material3.ExperimentalMaterial3Api
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
@Composable
fun SignUpScreenPreview() {
    SignUpScreen()
} 