package com.example.english_app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.english_app.ui.theme.*
import androidx.compose.animation.core.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import coil.compose.AsyncImage
import android.speech.tts.TextToSpeech
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Offset
import kotlin.math.abs
import java.util.Locale
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import kotlinx.coroutines.delay
import kotlin.random.Random
import androidx.compose.ui.text.font.FontStyle
import com.example.english_app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarouselScreen(
    category: Category,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    animationSettings: AnimationSettings = AnimationSettings()
) {
    var currentWordIndex by remember { mutableIntStateOf(0) }
    var isFavorite by remember { mutableStateOf(false) }
    var difficultyRating by remember { mutableIntStateOf(0) }
    var showPracticeMode by remember { mutableStateOf(false) }
    
    val words = category.words
    val currentWord = words[currentWordIndex]
    
    // Text-to-Speech setup
    val context = LocalContext.current
    val tts = remember { 
        TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Set language after initialization
            }
        }
    }
    
    // Set language after TTS is initialized
    LaunchedEffect(tts) {
        tts.language = Locale.US
    }
    
    // Cleanup TTS on dispose
    DisposableEffect(Unit) {
        onDispose {
            tts.shutdown()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category.title, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VibrantGreen
                )
            )
        },
        floatingActionButton = {
            var showNavigationOptions by remember { mutableStateOf(false) }
            
            Box {
                // Navigation options
                if (showNavigationOptions) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Previous button
                        if (currentWordIndex > 0) {
                            FloatingActionButton(
                                onClick = {
                                    currentWordIndex--
                                    isFavorite = false
                                    difficultyRating = 0
                                    showNavigationOptions = false
                                },
                                containerColor = VibrantGreen,
                                contentColor = Color.White,
                                modifier = Modifier.size(56.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.NavigateBefore,
                                    contentDescription = "Previous Word",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        
                        // Next button
                        if (currentWordIndex < words.size - 1) {
                            FloatingActionButton(
                                onClick = {
                                    currentWordIndex++
                                    isFavorite = false
                                    difficultyRating = 0
                                    showNavigationOptions = false
                                },
                                containerColor = VibrantGreen,
                                contentColor = Color.White,
                                modifier = Modifier.size(56.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                                    contentDescription = "Next Word",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
                
                // Main FAB
                FloatingActionButton(
                    onClick = { showNavigationOptions = !showNavigationOptions },
                    containerColor = VibrantGreen,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = if (showNavigationOptions) Icons.Default.Close else Icons.Default.Navigation,
                        contentDescription = if (showNavigationOptions) "Close Navigation" else "Navigation Options"
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val (x, y) = dragAmount
                        if (abs(x) > abs(y) && abs(x) > 50f) {
                            if (x > 0) {
                                // Swipe right - go to previous
                                if (currentWordIndex > 0) {
                                    currentWordIndex--
                                    isFavorite = false
                                    difficultyRating = 0
                                }
                            } else {
                                // Swipe left - go to next
                                if (currentWordIndex < words.size - 1) {
                                    currentWordIndex++
                                    isFavorite = false
                                    difficultyRating = 0
                                }
                            }
                        }
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Scrollable content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .padding(bottom = 80.dp), // Add padding for FAB
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Progress indicator
                LinearProgressIndicator(
                    progress = { (currentWordIndex + 1).toFloat() / words.size.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    color = VibrantGreen,
                    trackColor = VibrantGreen.copy(alpha = 0.3f)
                )
                
                // Scroll indicator
                Text(
                    text = "📜 Scroll to see more content",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Word card with enhanced features
                WordCard(
                    word = currentWord,
                    isFavorite = isFavorite,
                    onFavoriteToggle = { isFavorite = !isFavorite },
                    difficultyRating = difficultyRating,
                    onDifficultyChange = { difficultyRating = it },
                    onSpeakWord = { 
                        tts.speak(currentWord.word, TextToSpeech.QUEUE_FLUSH, null, null)
                    },
                    categoryId = category.id,
                    useRandomDirections = animationSettings.useRandomDirections,
                    animationStyle = animationSettings.animationStyle
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ActionButton(
                        icon = Icons.Default.PlayArrow,
                        text = "Practice",
                        onClick = { showPracticeMode = true },
                        color = VibrantBlue
                    )
                    ActionButton(
                        icon = Icons.Default.Share,
                        text = "Share",
                        onClick = { /* TODO: Share word */ },
                        color = VibrantOrange
                    )
                    ActionButton(
                        icon = Icons.Default.Bookmark,
                        text = "Save",
                        onClick = { /* TODO: Save to notes */ },
                        color = VibrantPink
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Additional navigation info
                Text(
                    text = "💡 Swipe left/right or use buttons to navigate",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Navigation controls (outside scrollable area)
            NavigationControls(
                currentIndex = currentWordIndex,
                totalWords = words.size,
                onPrevious = {
                    if (currentWordIndex > 0) {
                        currentWordIndex--
                        isFavorite = false
                        difficultyRating = 0
                    }
                },
                onNext = {
                    if (currentWordIndex < words.size - 1) {
                        currentWordIndex++
                        isFavorite = false
                        difficultyRating = 0
                    }
                }
            )
        }
        
        // Practice mode dialog
        if (showPracticeMode) {
            PracticeModeDialog(
                word = currentWord,
                onDismiss = { showPracticeMode = false }
            )
        }
    }
}

@Composable
fun TypewriterText(
    text: String,
    fontSize: androidx.compose.ui.unit.TextUnit,
    fontWeight: FontWeight? = null,
    color: Color = Color.Black,
    lineHeight: androidx.compose.ui.unit.TextUnit? = null,
    fontStyle: FontStyle? = null,
    key: String
) {
    var visibleCharacters by remember(key) { mutableIntStateOf(0) }

    LaunchedEffect(key1 = key) {
        visibleCharacters = 0
        val total = text.length
        while (visibleCharacters < total) {
            visibleCharacters++
            delay(18)
        }
    }

    val shown = if (visibleCharacters <= text.length) text.take(visibleCharacters) else text
    Text(
        text = shown,
        fontSize = fontSize,
        fontWeight = fontWeight ?: FontWeight.Normal,
        color = color,
        lineHeight = lineHeight ?: 20.sp,
        fontStyle = fontStyle
    )
}

@Composable
fun WordCard(
    word: Word,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    difficultyRating: Int,
    onDifficultyChange: (Int) -> Unit,
    onSpeakWord: () -> Unit,
    categoryId: String,
    useRandomDirections: Boolean,
    animationStyle: Int
) {
    var showDetails by remember { mutableStateOf(false) }
    var directionIndex by remember { mutableIntStateOf(0) }
    var chosenDirection by remember { mutableIntStateOf(0) } // 0: left, 1: right, 2: top, 3: bottom
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(12.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // Word image (no animations)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { 
                        showDetails = !showDetails
                        if (useRandomDirections) {
                            chosenDirection = Random.nextInt(0, 4)
                        } else {
                            // Deterministic per-category order
                            val order = when (categoryId) {
                                "words1" -> listOf(0, 3, 1, 2) // left, bottom, right, top
                                "words2" -> listOf(1, 2, 0, 3) // right, top, left, bottom
                                "words3" -> listOf(2, 0, 3, 1) // top, left, bottom, right
                                else -> listOf(0, 1, 2, 3)
                            }
                            chosenDirection = order[directionIndex]
                            directionIndex = (directionIndex + 1) % order.size
                        }
                    }
            ) {
                Image(
                    painter = painterResource(
                        id = getImageResId(word.imageUrl)
                    ),
                    contentDescription = "Image for ${word.word}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Click indicator overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (showDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (showDetails) "Hide details" else "Show details",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        
                        // Animation type indicator for text
                        if (showDetails) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = when (animationStyle) {
                                    0 -> when (chosenDirection) {
                                        0 -> "⬅️ Slide From Left"
                                        1 -> "➡️ Slide From Right"
                                        2 -> "⬆️ Slide From Top"
                                        else -> "⬇️ Slide From Bottom"
                                    }
                                    1 -> "✨ Fade"
                                    2 -> "⌨️ Typewriter"
                                    3 -> "🔍 Scale"
                                    else -> "✨ Fade"
                                },
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            // Word details (animated visibility)
            val enterTransition = when (animationStyle) {
                0 -> when (chosenDirection) { // Slide
                    0 -> slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
                    1 -> slideInHorizontally(initialOffsetX = { it }) + fadeIn()
                    2 -> slideInVertically(initialOffsetY = { -it }) + fadeIn()
                    else -> slideInVertically(initialOffsetY = { it }) + fadeIn()
                }
                1 -> fadeIn() // Fade only
                2 -> fadeIn() // Typewriter inside content
                3 -> scaleIn(initialScale = 0.92f) + fadeIn() // Scale + fade
                else -> fadeIn()
            }
            val exitTransition = when (animationStyle) {
                0 -> when (chosenDirection) { // Slide
                    0 -> slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                    1 -> slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                    2 -> slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                    else -> slideOutVertically(targetOffsetY = { it }) + fadeOut()
                }
                1 -> fadeOut()
                2 -> fadeOut()
                3 -> scaleOut(targetScale = 0.92f) + fadeOut()
                else -> fadeOut()
            }
            AnimatedVisibility(
                visible = showDetails,
                enter = enterTransition,
                exit = exitTransition
            ) {
                Column {
                    // Header with word and favorite button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (animationStyle == 2) {
                                TypewriterText(
                                    text = word.word,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    key = "word-${word.word}-${showDetails}"
                                )
                            } else {
                                Text(
                                    text = word.word,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            IconButton(
                                onClick = { onSpeakWord() },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Pronounce",
                                    tint = VibrantGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        IconButton(
                            onClick = onFavoriteToggle,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) VibrantPink else Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    // Pronunciation display
                    Text(
                        text = word.pronunciation,
                        fontSize = 16.sp,
                        color = Color.Gray,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier
                            .padding(start = 2.dp, top = 2.dp, bottom = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Definition section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Definition",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            if (animationStyle == 2) {
                                TypewriterText(
                                    text = word.definition,
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    lineHeight = 24.sp,
                                    key = "def-${word.word}-${showDetails}"
                                )
                            } else {
                                Text(
                                    text = word.definition,
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    lineHeight = 24.sp,
                                    modifier = Modifier
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Example section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE3F2FD)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Example",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1976D2)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            if (animationStyle == 2) {
                                TypewriterText(
                                    text = word.example,
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    lineHeight = 24.sp,
                                    fontStyle = FontStyle.Italic,
                                    key = "ex-${word.word}-${showDetails}"
                                )
                            } else {
                                Text(
                                    text = word.example,
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    lineHeight = 24.sp,
                                    fontStyle = FontStyle.Italic,
                                    modifier = Modifier
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Difficulty rating
                    DifficultyRating(
                        rating = difficultyRating,
                        onRatingChange = onDifficultyChange
                    )
                }
            }
        }
    }
}

// Helper to get drawable resource id from name
@Composable
fun getImageResId(imageName: String): Int {
    return when (imageName) {
        // Basic Words - Food, Transport, Electronics, Apartment, Ocean
        "word1" -> R.drawable.word1 // Food - keep existing or will be replaced
        "word2" -> R.drawable.word2 // Transport - keep existing
        "word3" -> R.drawable.word3 // Electronics - keep existing
        "word4" -> R.drawable.word4 // Apartment - keep existing
        "word5" -> R.drawable.word5 // Ocean - keep existing
        
        // Advanced Words - Complex vocabulary
        "word6" -> R.drawable.word6 // Clandestine - keep existing
        "word7" -> R.drawable.word7 // Disgruntled - keep existing
        "word8" -> R.drawable.word8 // Grappling - keep existing
        "word9" -> R.drawable.word9 // Coracle - keep existing
        "word10" -> R.drawable.word10 // Trailblazer - keep existing
        "word11" -> R.drawable.word11 // Whiff - keep existing
        "word12" -> R.drawable.word12 // Tweak - keep existing
        "word13" -> R.drawable.word13 // Ember - keep existing
        "word14" -> R.drawable.word14 // Maverick - keep existing
        "word15" -> R.drawable.word15 // Domicile - keep existing
        
        // Homographs - Multiple meanings
        "word16" -> R.drawable.word16 // Bank (financial) - keep existing
        "word17" -> R.drawable.word17 // Bank (slope) - keep existing
        "word18" -> R.drawable.word18 // Bat (mammal) - keep existing
        "word19" -> R.drawable.word19 // Bat (sports) - keep existing
        "word20" -> R.drawable.word20 // Cloud - keep existing
        else -> R.drawable.word1 // fallback
    }
}

@Composable
fun DifficultyRating(rating: Int, onRatingChange: (Int) -> Unit) {
    Column {
        Text(
            text = "How difficult was this word?",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            (1..5).forEach { star ->
                IconButton(
                    onClick = { onRatingChange(star) }
                ) {
                    Icon(
                        imageVector = if (star <= rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = "Rate $star",
                        tint = if (star <= rating) VibrantOrange else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    color: Color
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.size(100.dp, 50.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = text,
                fontSize = 10.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun NavigationControls(
    currentIndex: Int,
    totalWords: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress indicator
        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / totalWords.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            color = VibrantGreen,
            trackColor = VibrantGreen.copy(alpha = 0.3f)
        )
        
        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onPrevious,
                enabled = currentIndex > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentIndex > 0) VibrantGreen else Color.Gray.copy(alpha = 0.6f),
                    disabledContainerColor = Color.Gray.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .height(56.dp)
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.NavigateBefore,
                    contentDescription = "Previous",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Previous", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            Card(
                modifier = Modifier.padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = VibrantGreen.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "${currentIndex + 1} / $totalWords",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = VibrantGreen,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }
            
            Button(
                onClick = onNext,
                enabled = currentIndex < totalWords - 1,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentIndex < totalWords - 1) VibrantGreen else Color.Gray.copy(alpha = 0.6f),
                    disabledContainerColor = Color.Gray.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .height(56.dp)
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text("Next", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        // Navigation hint
        Text(
            text = "💡 Swipe left/right or use Previous/Next buttons",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun PracticeModeDialog(word: Word, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Practice Mode",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Test your knowledge of '${word.word}'",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Definition:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = word.definition,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Example:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = word.example,
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = VibrantGreen)
            ) {
                Text("Got it!")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}