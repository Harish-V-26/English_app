package com.example.english_app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.english_app.ui.theme.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import android.content.SharedPreferences
import coil.compose.AsyncImage
import com.example.english_app.data.UserProgressRepository
import com.example.english_app.data.UserProfile
import com.google.firebase.auth.FirebaseAuth

// Data class for animation settings
data class AnimationSettings(
    val useRandomDirections: Boolean = true,
    val animationStyle: Int = 0 // 0: Slide, 1: Fade, 2: Typewriter, 3: Scale
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit, 
    darkTheme: Boolean, 
    onToggleTheme: () -> Unit,
    animationSettings: AnimationSettings = AnimationSettings(),
    onAnimationSettingsChange: (AnimationSettings) -> Unit = {},
    userName: String = "User",
    userEmail: String = "",
    userPhotoUrl: String? = null,
    onSignOut: () -> Unit = {},
    fontSizeScale: Float = 1.0f,
    onFontSizeChange: (Float) -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("animation_settings", Context.MODE_PRIVATE) }
    
    var useRandomDirections by remember { 
        mutableStateOf(prefs.getBoolean("use_random_directions", true)) 
    }
    var animationStyle by remember { 
        mutableIntStateOf(prefs.getInt("animation_style", 0)) 
    }
    var currentFontScale by remember { mutableFloatStateOf(fontSizeScale) }

    // Load user profile
    var userProfile by remember { mutableStateOf(UserProfile()) }
    LaunchedEffect(Unit) {
        UserProgressRepository.loadUserProfile { profile ->
            userProfile = profile
        }
    }
    
    // Save settings when they change
    LaunchedEffect(useRandomDirections, animationStyle) {
        prefs.edit()
            .putBoolean("use_random_directions", useRandomDirections)
            .putInt("animation_style", animationStyle)
            .apply()
        
        onAnimationSettingsChange(AnimationSettings(useRandomDirections, animationStyle))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = if (darkTheme) MaterialTheme.colorScheme.onSurface else Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (darkTheme) MaterialTheme.colorScheme.onSurface else Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (darkTheme) MaterialTheme.colorScheme.surface else VibrantGreen
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Settings", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground)
            }

            // Profile Section
            item {
                SettingsSection(title = "Profile") {
                    ProfileCard(
                        userName = userName,
                        userEmail = userEmail,
                        userPhotoUrl = userPhotoUrl,
                        department = userProfile.department,
                        rollNo = userProfile.rollNo
                    )
                    
                    var editDepartment by remember(userProfile.department) { mutableStateOf(userProfile.department) }
                    var editDepartmentExpanded by remember { mutableStateOf(false) }
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
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = editDepartment,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Update Department") },
                                trailingIcon = {
                                    Icon(
                                        imageVector = if (editDepartmentExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown",
                                        tint = VibrantGreen
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VibrantGreen,
                                    focusedLabelColor = VibrantGreen
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { editDepartmentExpanded = !editDepartmentExpanded }
                            )
                            DropdownMenu(
                                expanded = editDepartmentExpanded,
                                onDismissRequest = { editDepartmentExpanded = false }
                            ) {
                                departmentOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            editDepartment = option
                                            editDepartmentExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                UserProgressRepository.saveUserProfile(
                                    name = userProfile.name,
                                    rollNo = userProfile.rollNo,
                                    department = editDepartment,
                                    role = userProfile.role,
                                    email = userProfile.email
                                )
                                userProfile = userProfile.copy(department = editDepartment)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VibrantGreen)
                        ) {
                            Text("Save", color = Color.White)
                        }
                    }
                }
            }
            
            item {
                SettingsSection(title = "Appearance") {
                    SettingsItem(
                        icon = Icons.Default.Brightness6,
                        title = "Dark Mode",
                        subtitle = "Switch between light and dark themes"
                    ) {
                        Switch(
                            checked = darkTheme, 
                            onCheckedChange = { onToggleTheme() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = VibrantGreen,
                                checkedTrackColor = VibrantGreen.copy(alpha = 0.5f)
                            )
                        )
                    }

                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))

                    // Font Size Adjustment
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = "Font Size",
                                tint = VibrantGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Font Size",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Adjust app text size (${(currentFontScale * 100).toInt()}%)",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("A", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Slider(
                                value = currentFontScale,
                                onValueChange = {
                                    currentFontScale = it
                                    onFontSizeChange(it)
                                },
                                valueRange = 0.8f..1.4f,
                                steps = 5,
                                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                                colors = SliderDefaults.colors(
                                    thumbColor = VibrantGreen,
                                    activeTrackColor = VibrantGreen
                                )
                            )
                            Text("A", fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                        }
                        // Preview text
                        Text(
                            text = "Preview: The quick brown fox jumps over the lazy dog.",
                            fontSize = (14 * currentFontScale).sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
            
            item {
                SettingsSection(title = "Animation Preferences") {
                    SettingsItem(
                        icon = Icons.Default.Animation,
                        title = "Direction Mode",
                        subtitle = if (useRandomDirections) "Random directions" else "Per-category order"
                    ) {
                        Switch(
                            checked = useRandomDirections,
                            onCheckedChange = { useRandomDirections = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = VibrantGreen,
                                checkedTrackColor = VibrantGreen.copy(alpha = 0.5f)
                            )
                        )
                    }
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Animation Style",
                                tint = VibrantGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Animation Style",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = when (animationStyle) {
                                        0 -> "Slide from sides"
                                        1 -> "Fade in/out"
                                        2 -> "Typewriter effect"
                                        3 -> "Scale animation"
                                        else -> "Slide from sides"
                                    },
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Slide" to 0, "Fade" to 1, "Type" to 2, "Scale" to 3).forEach { (label, value) ->
                                AssistChip(
                                    onClick = { animationStyle = value },
                                    label = { Text(label, fontSize = 12.sp) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = if (animationStyle == value) 
                                            VibrantGreen.copy(alpha = 0.15f) else Color.Transparent
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
            
            item {
                SettingsSection(title = "About") {
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "Version",
                        subtitle = "1.0.0"
                    ) { }
                    
                    SettingsItem(
                        icon = Icons.Default.Email,
                        title = "Contact Developer",
                        subtitle = "Get in touch for support"
                    ) { }
                }
            }

            // Sign Out Button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        onSignOut()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VibrantRed
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Sign Out",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Sign Out",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ProfileCard(
    userName: String,
    userEmail: String,
    userPhotoUrl: String?,
    department: String,
    rollNo: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Card(
                modifier = Modifier.size(60.dp),
                colors = CardDefaults.cardColors(containerColor = VibrantGreen),
                shape = RoundedCornerShape(30.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (userPhotoUrl != null && userPhotoUrl.isNotBlank()) {
                        AsyncImage(
                            model = userPhotoUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(30.dp))
                        )
                    } else {
                        Text(
                            text = userName.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = userName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = userEmail,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (department.isNotBlank() || rollNo.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(12.dp))

            if (rollNo.isNotBlank()) {
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Roll No",
                        tint = VibrantBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Roll No: $rollNo", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }
            if (department.isNotBlank()) {
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "Department",
                        tint = VibrantOrange,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Department: $department", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
fun UserProfileCard(userName: String, userEmail: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.size(60.dp),
                colors = CardDefaults.cardColors(containerColor = VibrantGreen),
                shape = RoundedCornerShape(30.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = userName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = userEmail,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = VibrantGreen,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        trailing()
    }
} 