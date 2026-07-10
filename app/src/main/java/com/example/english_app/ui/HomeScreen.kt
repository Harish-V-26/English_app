package com.example.english_app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.english_app.ui.theme.*
import com.example.english_app.ui.Word
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import coil.compose.AsyncImage
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.Switch
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import android.speech.tts.TextToSpeech
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
import java.util.Locale
import androidx.compose.material.icons.filled.CompareArrows

// --- Vocabulary Data Models and Sets ---
data class Category(
    val id: String,
    val title: String,
    val description: String,
    val color: Color,
    val icon: ImageVector,
    val words: List<Word>
)

val words1 = listOf(
    Word("Food", "fuːd", "Any nutritious substance that people or animals eat or drink.", "Rice is a staple food in many countries.", "word1"),
    Word("Transport", "ˈtrænspɔːrt", "A system or means of conveying people or goods from place to place.", "Public transport in the city is very efficient.", "word2"),
    Word("Electronics", "ɪˌlɛkˈtrɒnɪks", "Devices or systems that operate using the flow of electrons, especially in computers and other devices.", "He studies electronics at university.", "word3"),
    Word("Apartment", "əˈpɑːrtmənt", "A set of rooms for someone to live in on one level of a building or house.", "She moved into a new apartment downtown.", "word4"),
    Word("Ocean", "ˈoʊʃən", "A very large area of sea.", "The Pacific Ocean is the largest ocean on Earth.", "word5")
)

val words2 = listOf(
    Word("Clandestine", "klænˈdestɪn", "Kept or done in secret, often in order to conceal an illicit or improper purpose.", "They held a clandestine meeting at night.", "word6"),
    Word("Disgruntled", "dɪsˈɡrʌntld", "Unhappy, annoyed, and disappointed about something.", "He was disgruntled by the new rules.", "word7"),
    Word("Grappling", "ˈɡræplɪŋ", "To get hold of somebody/something and fight with or try to control.", "She was grappling with a tough decision.", "word8"),
    Word("Coracle", "ˈkɒrəkəl", "A small, round boat that is made by stretching animal skin over a wooden frame.", "The fisherman crossed the river in his tiny coracle, paddling swiftly.", "word9"),
    Word("Trailblazer / Pioneer", "ˈtreɪlbleɪzər / ˌpaɪəˈnɪər", "The first person to do something or go somewhere, who shows that it is possible for others too.", "She became a trailblazer in the tech industry.", "word10"),
    Word("Whiff", "wɪf", "A slight smell, carried on a current of air.", "I caught a whiff of freshly baked bread.", "word11"),
    Word("Tweak", "twiːk", "To change something slightly, especially to make it more correct, effective, or suitable.", "She tweaked her essay before submitting it.", "word12"),
    Word("Ember", "ˈembər", "A piece of wood or coal, etc. which continues to burn after a fire has no more flames.", "We watched the last ember glow before bed.", "word13"),
    Word("Maverick", "ˈmævərɪk", "A person who does not behave or think like everyone else, but who has independent, unusual opinions.", "The maverick questioned outdated traditions.", "word14"),
    Word("Domicile", "ˈdɒmɪsaɪl", "A person's permanent, fixed, or principal home and legal residence.", "He changed his domicile after moving abroad.", "word15")
)

val words3 = listOf(
    Word("Bank (financial institution)", "bæŋk", "A financial institution that accepts deposits and makes loans.", "I went to the bank to deposit my paycheck.", "word16"),
    Word("Bank (slope or incline)", "bæŋk", "A slope or incline, especially of a river or road.", "The car skidded on the bank of the river.", "word17"),
    Word("Bat (flying mammal)", "bæt", "A nocturnal flying mammal with leathery wings.", "Bats are important for controlling insect populations.", "word18"),
    Word("Bat (sports equipment)", "bæt", "A wooden or metal club used in sports like baseball or cricket.", "He swung the bat and hit a home run.", "word19"),
    Word("Cloud (weather phenomenon)", "klaʊd", "A visible mass of condensed water vapor floating in the atmosphere.", "Dark clouds gathered before the storm.", "word20")
)

val categories = listOf(
    Category(
        id = "words1",
        title = "Basic words",
        description = "Common everyday vocabulary",
        color = VibrantYellow,
        icon = Icons.Default.Restaurant, // Food icon for basic words
        words = words1
    ),
    Category(
        id = "words2",
        title = "Advanced words",
        description = "Complex vocabulary and expressions",
        color = VibrantBlue,
        icon = Icons.Default.School, // Education icon for advanced words
        words = words2
    ),
    Category(
        id = "words3",
        title = "Homographs Words",
        description = "Words that are spelled the same but have different meanings",
        color = VibrantGreen,
        icon = Icons.Default.CompareArrows, // Changed from AutoMirrored to Default
        words = words3
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCategorySelected: (Category) -> Unit,
    onDashboard: () -> Unit,
    onSettings: () -> Unit,
    onContact: () -> Unit,
    onHome: () -> Unit,
    onLogin: () -> Unit = {},
    onLogout: () -> Unit = {},
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    userName: String = "User",
    userPhotoUrl: String? = null,
    isLoggedIn: Boolean = false
) {
    var searchQuery by remember { mutableStateOf("") }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Text-to-Speech setup
    val context = LocalContext.current
    var isTtsInitialized by remember { mutableStateOf(false) }
    val tts = remember { 
        TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsInitialized = true
            }
        }
    }
    
    // Set language after TTS is initialized
    LaunchedEffect(isTtsInitialized) {
        if (isTtsInitialized) {
            tts.language = Locale.US
        }
    }
    
    // Cleanup TTS on dispose
    DisposableEffect(Unit) {
        onDispose {
            tts.shutdown()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Avatar at the top
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (userPhotoUrl != null && userPhotoUrl.isNotBlank()) {
                        AsyncImage(
                            model = userPhotoUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(32.dp))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .background(VibrantGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp
                            )
                        }
                    }
                }
                Text(userName, modifier = Modifier.align(Alignment.CenterHorizontally), fontWeight = FontWeight.Bold)
                Text("Menu", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onHome() 
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") }
                )
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onSettings() 
                    },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") }
                )
                NavigationDrawerItem(
                    label = { Text("Contact Developer") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onContact() 
                    },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Contact") }
                )
                if (isLoggedIn) {
                    NavigationDrawerItem(
                        label = { Text("Logout") },
                        selected = false,
                        onClick = { 
                            scope.launch { drawerState.close() }
                            onLogout() 
                        },
                        icon = { Icon(Icons.Default.Logout, contentDescription = "Logout") }
                    )
                } else {
                    NavigationDrawerItem(
                        label = { Text("Login") },
                        selected = false,
                        onClick = { 
                            scope.launch { drawerState.close() }
                            onLogin() 
                        },
                        icon = { Icon(Icons.Default.Login, contentDescription = "Login") }
                    )
                }
                Row(
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Brightness6, contentDescription = "Theme", tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Dark Mode", modifier = Modifier.weight(1f))
                    Switch(checked = darkTheme, onCheckedChange = { onToggleTheme() })
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Vocabulary Explorer", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { 
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = onDashboard) {
                            Icon(Icons.Default.Dashboard, contentDescription = "Dashboard", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = VibrantGreen
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search categories...", color = Color.Gray) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = VibrantGreen
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VibrantGreen,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color.Black
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Categories with navigation
                val filteredCategories = categories.filter { it.title.contains(searchQuery, ignoreCase = true) }
                if (filteredCategories.isNotEmpty()) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredCategories) { category ->
                            EnhancedCategoryCard(
                                category = category,
                                onCategorySelected = onCategorySelected,
                                onSpeakCategory = { 
                                    tts.speak(category.title, TextToSpeech.QUEUE_FLUSH, null, null)
                                }
                            )
                        }
                    }
                } else {
                    // No categories found
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No categories found",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedCategoryCard(
    category: Category,
    onCategorySelected: (Category) -> Unit,
    onSpeakCategory: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCategorySelected(category) }
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon and progress
            Box(
                modifier = Modifier.size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.title,
                    tint = category.color,
                    modifier = Modifier.size(32.dp)
                )
                CircularProgressIndicator(
                    progress = { 0.6f },
                    modifier = Modifier.size(60.dp),
                    color = category.color.copy(alpha = 0.3f),
                    strokeWidth = 4.dp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Category info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = category.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { onSpeakCategory() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Pronounce category",
                            tint = category.color,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    text = category.description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${category.words.size} words",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "60% completed",
                        fontSize = 12.sp,
                        color = category.color,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Arrow
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Go to category",
                tint = category.color,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ProgressCard(
    title: String,
    value: String,
    total: String,
    color: Color,
    icon: ImageVector
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = color
            )
            Text(
                text = "/ $total",
                fontSize = 10.sp,
                color = SecondaryText
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: Category,
    onClick: () -> Unit
) {
    val emoji = when (category.id) {
        "words1" -> "📝"
        "words2" -> "🎯"
        "words3" -> "🔄"
        else -> "📚"
    }
    Box(
        modifier = Modifier
            .width(110.dp)
            .height(170.dp)
            .shadow(12.dp, RoundedCornerShape(20.dp))
            .background(
                color = category.color.copy(alpha = 0.18f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = emoji,
                fontSize = 32.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Icon(
                imageVector = category.icon,
                contentDescription = category.title,
                tint = category.color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = category.color,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = category.description,
                fontSize = 12.sp,
                color = PrimaryText,
                textAlign = TextAlign.Center
            )
        }
        // Playful arrow (emoji)
        Text(
            text = "➡️",
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
        )
    }
}

@Composable
fun LearningModuleCard(
    module: LearningModule,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(120.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = module.icon,
                contentDescription = module.title,
                tint = module.color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = module.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = PrimaryText,
                textAlign = TextAlign.Center
            )
            Text(
                text = module.description,
                fontSize = 10.sp,
                color = SecondaryText,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun QuickActionCard(
    action: QuickAction,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(80.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = action.color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.title,
                tint = action.color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = action.title,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = action.color,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Data classes for learning modules and quick actions
data class LearningModule(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

data class QuickAction(
    val title: String,
    val icon: ImageVector,
    val color: Color
)

// Sample data
val learningModules = listOf(
    LearningModule("Grammar", "Learn English grammar", Icons.Default.School, VibrantBlue),
    LearningModule("Vocabulary", "Expand your vocabulary", Icons.Default.Book, VibrantGreen),
    LearningModule("Speaking", "Practice pronunciation", Icons.Default.Mic, VibrantOrange),
    LearningModule("Listening", "Improve listening skills", Icons.AutoMirrored.Filled.VolumeUp, VibrantPurple),
    LearningModule("Writing", "Enhance writing skills", Icons.Default.Edit, VibrantPink)
)

val quickActions = listOf(
    QuickAction("Daily Quiz", Icons.Default.QuestionAnswer, VibrantBlue),
    QuickAction("Flashcards", Icons.Default.Style, VibrantGreen),
    QuickAction("Progress", Icons.Default.ShowChart, VibrantPurple)
)

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        onCategorySelected = {},
        onDashboard = {},
        onSettings = {},
        onContact = {},
        onHome = {},
        darkTheme = false,
        onToggleTheme = {},
        userName = "Test User",
        userPhotoUrl = "https://via.placeholder.com/150"
    )
}