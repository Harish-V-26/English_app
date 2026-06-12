package com.example.english_app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.english_app.ui.theme.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import android.content.SharedPreferences

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
    onAnimationSettingsChange: (AnimationSettings) -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("animation_settings", Context.MODE_PRIVATE) }
    
    var useRandomDirections by remember { 
        mutableStateOf(prefs.getBoolean("use_random_directions", true)) 
    }
    var animationStyle by remember { 
        mutableIntStateOf(prefs.getInt("animation_style", 0)) 
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
                title = { Text("Settings", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VibrantGreen
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
                Text("Settings", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.Black)
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
                    
                    SettingsItem(
                        icon = Icons.Default.Palette,
                        title = "Animation Style",
                        subtitle = when (animationStyle) {
                            0 -> "Slide from sides"
                            1 -> "Fade in/out"
                            2 -> "Typewriter effect"
                            3 -> "Scale animation"
                            else -> "Slide from sides"
                        }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Slide" to 0, "Fade" to 1, "Type" to 2, "Scale" to 3).forEach { (label, value) ->
                                AssistChip(
                                    onClick = { animationStyle = value },
                                    label = { Text(label, fontSize = 12.sp) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = if (animationStyle == value) 
                                            VibrantGreen.copy(alpha = 0.15f) else Color.Transparent
                                    )
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
        }
    }
}

@Composable
fun UserProfileCard(userName: String, userEmail: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
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
                    color = Color.Black
                )
                Text(
                    text = userEmail,
                    fontSize = 14.sp,
                    color = Color.Gray
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
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
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
                color = Color.Black
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        
        trailing()
    }
} 