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
import com.example.english_app.data.UserProgressRepository
import android.content.Intent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarouselScreen(
    category: Category,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    animationSettings: AnimationSettings = AnimationSettings(),
    onStartQuiz: (String) -> Unit = {}
) {
    var currentWordIndex by remember { mutableIntStateOf(0) }
    var isFavorite by remember { mutableStateOf(false) }
    var isBookmarked by remember { mutableStateOf(false) }
    var difficultyRating by remember { mutableIntStateOf(0) }
    var showDetails by remember { mutableStateOf(false) }
    
    val words = category.words
    val currentWord = words[currentWordIndex]

    // Load this word's saved favorite/bookmark/difficulty state from Firestore
    LaunchedEffect(currentWordIndex, category.id) {
        UserProgressRepository.loadWordProgress(category.id, currentWord.word) { favorite, bookmarked, difficulty ->
            isFavorite = favorite
            isBookmarked = bookmarked
            difficultyRating = difficulty
        }
    }
    
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

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Navigation options appear above the main FAB
                if (showNavigationOptions) {
                    // Next button
                    if (currentWordIndex < words.size - 1) {
                        FloatingActionButton(
                            onClick = {
                                currentWordIndex++
                                isFavorite = false
                                difficultyRating = 0
                                showDetails = false
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

                    // Previous button
                    if (currentWordIndex > 0) {
                        FloatingActionButton(
                            onClick = {
                                currentWordIndex--
                                isFavorite = false
                                difficultyRating = 0
                                showDetails = false
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
                                    showDetails = false
                                }
                            } else {
                                // Swipe left - go to next
                                if (currentWordIndex < words.size - 1) {
                                    currentWordIndex++
                                    isFavorite = false
                                    difficultyRating = 0
                                    showDetails = false
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
                
                // Word card with enhanced features
                WordCard(
                    word = currentWord,
                    isFavorite = isFavorite,
                    onFavoriteToggle = {
                        isFavorite = !isFavorite
                        UserProgressRepository.setFavorite(category.id, currentWord.word, isFavorite)
                    },
                    difficultyRating = difficultyRating,
                    onDifficultyChange = {
                        difficultyRating = it
                        UserProgressRepository.setDifficulty(category.id, currentWord.word, it)
                    },
                    onSpeakWord = { text ->
                        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
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
                        onClick = { onStartQuiz(category.id) },
                        color = VibrantBlue
                    )
                    ActionButton(
                        icon = Icons.Default.Share,
                        text = "Share",
                        onClick = {
                            val shareText = "${currentWord.word} (${currentWord.pronunciation})\n" +
                                "${currentWord.definition}\n" +
                                "Example: ${currentWord.example}\n\n" +
                                "Learning English vocabulary with ENGLISH_APP!"
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share this word"))
                        },
                        color = VibrantOrange
                    )
                    ActionButton(
                        icon = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        text = if (isBookmarked) "Saved" else "Save",
                        onClick = {
                            isBookmarked = !isBookmarked
                            UserProgressRepository.setBookmarked(category.id, currentWord.word, isBookmarked)
                        },
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
                        showDetails = false
                    }
                },
                onNext = {
                    if (currentWordIndex < words.size - 1) {
                        currentWordIndex++
                        isFavorite = false
                        difficultyRating = 0
                        showDetails = false
                    }
                }
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
    onSpeakWord: (String) -> Unit,
    categoryId: String,
    useRandomDirections: Boolean,
    animationStyle: Int
) {
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
                    .height(340.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                if (word.imageUrl.isBlank()) {
                    // No image assigned yet — show a gray placeholder box
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFE0E0E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "No image yet for ${word.word}",
                            tint = Color(0xFFBDBDBD),
                            modifier = Modifier.size(56.dp)
                        )
                    }
                } else {
                    Image(
                        painter = painterResource(
                            id = getImageResId(word.imageUrl)
                        ),
                        contentDescription = "Image for ${word.word}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            // Word details
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
                                    key = "word-${word.word}"
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
                                onClick = { onSpeakWord(word.word) },
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Definition",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                                IconButton(
                                    onClick = { onSpeakWord(word.definition) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Pronounce Definition",
                                        tint = VibrantGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            if (animationStyle == 2) {
                                TypewriterText(
                                    text = word.definition,
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    lineHeight = 24.sp,
                                    key = "def-${word.word}"
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Example",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1976D2)
                                )
                                IconButton(
                                    onClick = { onSpeakWord(word.example) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Pronounce Example",
                                        tint = VibrantGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            if (animationStyle == 2) {
                                TypewriterText(
                                    text = word.example,
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    lineHeight = 24.sp,
                                    fontStyle = FontStyle.Italic,
                                    key = "ex-${word.word}"
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

// Helper to get drawable resource id from name
@Composable
fun getImageResId(imageName: String): Int {
    return when (imageName) {
        "abstract_img" -> R.drawable.abstract_img
        "alight" -> R.drawable.alight
        "altercation" -> R.drawable.altercation
        "always" -> R.drawable.always
        "ambel" -> R.drawable.ambel
        "amiable" -> R.drawable.amiable
        "anchor" -> R.drawable.anchor
        "angry" -> R.drawable.angry
        "annotate" -> R.drawable.annotate
        "appraise" -> R.drawable.appraise
        "apprensive" -> R.drawable.apprensive
        "asafoetida" -> R.drawable.asafoetida
        "bad" -> R.drawable.bad
        "bank" -> R.drawable.bank
        "bark" -> R.drawable.bark
        "bat" -> R.drawable.bat
        "bay_leaves" -> R.drawable.bay_leaves
        "bestow" -> R.drawable.bestow
        "big" -> R.drawable.big
        "biopic" -> R.drawable.biopic
        "black_gram" -> R.drawable.black_gram
        "blanch" -> R.drawable.blanch
        "blink" -> R.drawable.blink
        "blog" -> R.drawable.blog
        "blow_your_nose" -> R.drawable.blow_your_nose
        "bolt" -> R.drawable.bolt
        "bow" -> R.drawable.bow
        "brick_klins" -> R.drawable.brick_klins
        "bright" -> R.drawable.bright
        "broiling" -> R.drawable.broiling
        "brunch" -> R.drawable.brunch
        "burp" -> R.drawable.burp
        "busy" -> R.drawable.busy
        "camcorder" -> R.drawable.camcorder
        "cardamom" -> R.drawable.cardamom
        "carelessness" -> R.drawable.carelessness
        "casserole" -> R.drawable.casserole
        "charge" -> R.drawable.charge
        "chew" -> R.drawable.chew
        "chillax" -> R.drawable.chillax
        "chortle" -> R.drawable.chortle
        "cinnamon" -> R.drawable.cinnamon
        "clandestine" -> R.drawable.clandestine
        "clear" -> R.drawable.clear
        "clg" -> R.drawable.clg
        "climb" -> R.drawable.climb
        "clip" -> R.drawable.clip
        "cloves" -> R.drawable.cloves
        "cogitate" -> R.drawable.cogitate
        "colander" -> R.drawable.colander
        "collasal" -> R.drawable.collasal
        "conclave" -> R.drawable.conclave
        "conflate" -> R.drawable.conflate
        "confused" -> R.drawable.confused
        "consecration_ceremony" -> R.drawable.consecration_ceremony
        "console" -> R.drawable.console
        "convive" -> R.drawable.convive
        "cower" -> R.drawable.cower
        "cozy" -> R.drawable.cozy
        "craft" -> R.drawable.craft
        "crawl" -> R.drawable.crawl
        "cremains" -> R.drawable.cremains
        "croon" -> R.drawable.croon
        "crouch" -> R.drawable.crouch
        "crumple" -> R.drawable.crumple
        "cumin" -> R.drawable.cumin
        "cup_ear" -> R.drawable.cup_ear
        "current" -> R.drawable.current
        "defenestrate" -> R.drawable.defenestrate
        "degustation_menu" -> R.drawable.degustation_menu
        "deplorable" -> R.drawable.deplorable
        "devour" -> R.drawable.devour
        "diktat" -> R.drawable.diktat
        "dine" -> R.drawable.dine
        "discourteous" -> R.drawable.discourteous
        "disgruntled" -> R.drawable.disgruntled
        "dissipate" -> R.drawable.dissipate
        "doc1_halcyon" -> R.drawable.doc1_halcyon
        "doc1_jubilant" -> R.drawable.doc1_jubilant
        "doc1_poignant" -> R.drawable.doc1_poignant
        "doc4_blog" -> R.drawable.doc4_blog
        "doc4_brunch" -> R.drawable.doc4_brunch
        "doc4_chillax" -> R.drawable.doc4_chillax
        "doc4_chortle" -> R.drawable.doc4_chortle
        "doc4_frenemy" -> R.drawable.doc4_frenemy
        "doc4_motel" -> R.drawable.doc4_motel
        "doc4_smog" -> R.drawable.doc4_smog
        "doc5_blanch" -> R.drawable.doc5_blanch
        "doc5_grate" -> R.drawable.doc5_grate
        "doc5_knead" -> R.drawable.doc5_knead
        "doc5_ladle" -> R.drawable.doc5_ladle
        "doc5_mash" -> R.drawable.doc5_mash
        "doc5_mortar_pestle" -> R.drawable.doc5_mortar_pestle
        "doc5_tongs" -> R.drawable.doc5_tongs
        "doc5_whisk" -> R.drawable.doc5_whisk
        "domicile" -> R.drawable.domicile
        "dramedy" -> R.drawable.dramedy
        "dredging" -> R.drawable.dredging
        "drowsy" -> R.drawable.drowsy
        "eat" -> R.drawable.eat
        "ecstatic" -> R.drawable.ecstatic
        "edutainment" -> R.drawable.edutainment
        "electocute" -> R.drawable.electocute
        "elegant" -> R.drawable.elegant
        "embarrass" -> R.drawable.embarrass
        "ember" -> R.drawable.ember
        "emoticon" -> R.drawable.emoticon
        "enervate" -> R.drawable.enervate
        "engrossed" -> R.drawable.engrossed
        "ephemeral" -> R.drawable.ephemeral
        "equanimity" -> R.drawable.equanimity
        "evade" -> R.drawable.evade
        "exaggerate" -> R.drawable.exaggerate
        "facepalm" -> R.drawable.facepalm
        "fair" -> R.drawable.fair
        "fennel_seeds" -> R.drawable.fennel_seeds
        "fenugreek_seeds" -> R.drawable.fenugreek_seeds
        "fight" -> R.drawable.fight
        "flexitarian" -> R.drawable.flexitarian
        "flick" -> R.drawable.flick
        "flinch" -> R.drawable.flinch
        "flutter" -> R.drawable.flutter
        "fortnight" -> R.drawable.fortnight
        "foxtail" -> R.drawable.foxtail
        "frenemy" -> R.drawable.frenemy
        "gargle" -> R.drawable.gargle
        "giggle" -> R.drawable.giggle
        "glance" -> R.drawable.glance
        "grab" -> R.drawable.grab
        "grappling" -> R.drawable.grappling
        "grate" -> R.drawable.grate
        "green_gram" -> R.drawable.green_gram
        "grimance" -> R.drawable.grimance
        "grime" -> R.drawable.grime
        "groan" -> R.drawable.groan
        "guess" -> R.drawable.guess
        "guesstimate" -> R.drawable.guesstimate
        "halycon" -> R.drawable.halycon
        "happy" -> R.drawable.happy
        "henchman" -> R.drawable.henchman
        "hiccup" -> R.drawable.hiccup
        "hoax" -> R.drawable.hoax
        "hop" -> R.drawable.hop
        "idea" -> R.drawable.idea
        "imminently" -> R.drawable.imminently
        "incensed" -> R.drawable.incensed
        "ineffable" -> R.drawable.ineffable
        "infomercial" -> R.drawable.infomercial
        "infotainment" -> R.drawable.infotainment
        "interpol" -> R.drawable.interpol
        "jam" -> R.drawable.jam
        "jeggings" -> R.drawable.jeggings
        "jubilant" -> R.drawable.jubilant
        "knead" -> R.drawable.knead
        "ladle" -> R.drawable.ladle
        "late" -> R.drawable.late
        "lead" -> R.drawable.lead
        "lean" -> R.drawable.lean
        "leap" -> R.drawable.leap
        "lentils" -> R.drawable.lentils
        "limerance" -> R.drawable.limerance
        "linner" -> R.drawable.linner
        "listicle" -> R.drawable.listicle
        "little" -> R.drawable.little
        "lucid" -> R.drawable.lucid
        "make" -> R.drawable.make
        "mander" -> R.drawable.mander
        "many" -> R.drawable.many
        "mash" -> R.drawable.mash
        "match" -> R.drawable.match
        "maverick" -> R.drawable.maverick
        "meager" -> R.drawable.meager
        "minute" -> R.drawable.minute
        "miscreants" -> R.drawable.miscreants
        "mockumentary" -> R.drawable.mockumentary
        "monitor" -> R.drawable.monitor
        "mortar_and_pestle" -> R.drawable.mortar_and_pestle
        "motel" -> R.drawable.motel
        "mumble" -> R.drawable.mumble
        "mustard_seeds" -> R.drawable.mustard_seeds
        "negligence" -> R.drawable.negligence
        "nervous" -> R.drawable.nervous
        "netiquette" -> R.drawable.netiquette
        "new_img" -> R.drawable.new_img
        "niblings" -> R.drawable.niblings
        "nice" -> R.drawable.nice
        "niftastic" -> R.drawable.niftastic
        "nod" -> R.drawable.nod
        "notion" -> R.drawable.notion
        "obsient" -> R.drawable.obsient
        "obsolete" -> R.drawable.obsolete
        "often" -> R.drawable.often
        "old" -> R.drawable.old
        "parsley" -> R.drawable.parsley
        "peer" -> R.drawable.peer
        "perpetually" -> R.drawable.perpetually
        "perplexing" -> R.drawable.perplexing
        "persuade" -> R.drawable.persuade
        "petrified" -> R.drawable.petrified
        "phablet" -> R.drawable.phablet
        "pioneer" -> R.drawable.pioneer
        "pitch" -> R.drawable.pitch
        "pixel" -> R.drawable.pixel
        "plot" -> R.drawable.plot
        "plunge" -> R.drawable.plunge
        "podcast" -> R.drawable.podcast
        "poignant" -> R.drawable.poignant
        "poke" -> R.drawable.poke
        "ponder" -> R.drawable.ponder
        "poppy_seeds" -> R.drawable.poppy_seeds
        "pound" -> R.drawable.pound
        "pout" -> R.drawable.pout
        "pristine" -> R.drawable.pristine
        "querencia" -> R.drawable.querencia
        "racket" -> R.drawable.racket
        "recurrently" -> R.drawable.recurrently
        "reluctant" -> R.drawable.reluctant
        "resilient" -> R.drawable.resilient
        "reticent" -> R.drawable.reticent
        "rude" -> R.drawable.rude
        "saute" -> R.drawable.saute
        "scale" -> R.drawable.scale
        "scared" -> R.drawable.scared
        "serendipity" -> R.drawable.serendipity
        "sesame" -> R.drawable.sesame
        "shopaholic" -> R.drawable.shopaholic
        "shred" -> R.drawable.shred
        "shrugs" -> R.drawable.shrugs
        "sieve" -> R.drawable.sieve
        "sigh" -> R.drawable.sigh
        "silent" -> R.drawable.silent
        "sitcom" -> R.drawable.sitcom
        "slam" -> R.drawable.slam
        "slap" -> R.drawable.slap
        "slouch" -> R.drawable.slouch
        "sludge" -> R.drawable.sludge
        "slurp" -> R.drawable.slurp
        "slury" -> R.drawable.slury
        "slush" -> R.drawable.slush
        "smog" -> R.drawable.smog
        "snap_fingers" -> R.drawable.snap_fingers
        "snatch" -> R.drawable.snatch
        "sneeze" -> R.drawable.sneeze
        "sniff" -> R.drawable.sniff
        "snollygoster" -> R.drawable.snollygoster
        "sometimes" -> R.drawable.sometimes
        "soon" -> R.drawable.soon
        "spill" -> R.drawable.spill
        "spit" -> R.drawable.spit
        "splendid" -> R.drawable.splendid
        "sporadically" -> R.drawable.sporadically
        "spork" -> R.drawable.spork
        "spring" -> R.drawable.spring
        "squash" -> R.drawable.squash
        "squeeze" -> R.drawable.squeeze
        "stare" -> R.drawable.stare
        "staycation" -> R.drawable.staycation
        "stubborn" -> R.drawable.stubborn
        "stutter" -> R.drawable.stutter
        "surmise" -> R.drawable.surmise
        "suspend" -> R.drawable.suspend
        "swift" -> R.drawable.swift
        "tardy" -> R.drawable.tardy
        "tenuous" -> R.drawable.tenuous
        "think" -> R.drawable.think
        "tickle" -> R.drawable.tickle
        "timid" -> R.drawable.timid
        "tire" -> R.drawable.tire
        "tongs" -> R.drawable.tongs
        "trailblazer" -> R.drawable.trailblazer
        "tresspass" -> R.drawable.tresspass
        "trouble" -> R.drawable.trouble
        "truce" -> R.drawable.truce
        "turmoil" -> R.drawable.turmoil
        "tweak" -> R.drawable.tweak
        "vault" -> R.drawable.vault
        "vellichor" -> R.drawable.vellichor
        "venerable" -> R.drawable.venerable
        "vivid" -> R.drawable.vivid
        "walk" -> R.drawable.walk
        "wander" -> R.drawable.wander
        "weak" -> R.drawable.weak
        "whiff" -> R.drawable.whiff
        "whisk" -> R.drawable.whisk
        "winch" -> R.drawable.winch
        "wink" -> R.drawable.wink
        "wok" -> R.drawable.wok
        "word1" -> R.drawable.word1
        "word10" -> R.drawable.word10
        "word11" -> R.drawable.word11
        "word12" -> R.drawable.word12
        "word13" -> R.drawable.word13
        "word14" -> R.drawable.word14
        "word15" -> R.drawable.word15
        "word16" -> R.drawable.word16
        "word17" -> R.drawable.word17
        "word18" -> R.drawable.word18
        "word19" -> R.drawable.word19
        "word2" -> R.drawable.word2
        "word20" -> R.drawable.word20
        "word3" -> R.drawable.word3
        "word4" -> R.drawable.word4
        "word5" -> R.drawable.word5
        "word6" -> R.drawable.word6
        "word7" -> R.drawable.word7
        "word8" -> R.drawable.word8
        "word9" -> R.drawable.word9
        "workaholic" -> R.drawable.workaholic
        "wring" -> R.drawable.wring
        "yank" -> R.drawable.yank
        "yell" -> R.drawable.yell
        else -> R.drawable.ic_placeholder_word
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