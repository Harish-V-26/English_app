package com.example.english_app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.english_app.ui.theme.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.vector.ImageVector
import coil.compose.AsyncImage
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import kotlinx.coroutines.launch
import com.example.english_app.data.UserProgressRepository
import com.example.english_app.data.DashboardStats
import com.example.english_app.data.DetailedQuizResult
import com.example.english_app.data.QuizAnswerDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userName: String,
    userEmail: String,
    userPhotoUrl: String?,
    onNavigateToHome: () -> Unit,
    isTestActive: Boolean = false, // New parameter to check if test is active
    onTestScheduled: () -> Unit = {}, // Callback for when test is scheduled
    onLogout: () -> Unit,
    onBack: () -> Unit,
    onSettings: () -> Unit,
    onContact: () -> Unit
) {
    var stats by remember { mutableStateOf(DashboardStats()) }
    var detailedResults by remember { mutableStateOf<List<DetailedQuizResult>>(emptyList()) }

    DisposableEffect(Unit) {
        val listener = UserProgressRepository.observeDashboardStats { loaded ->
            stats = loaded
        }
        onDispose {
            listener.remove()
        }
    }

    LaunchedEffect(Unit) {
        UserProgressRepository.loadDetailedQuizResults { results ->
            detailedResults = results
        }
    }

    val infiniteTransition = rememberInfiniteTransition()
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menu", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
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
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Dashboard", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToHome) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = onLogout) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Logout",
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
            if (isTestActive) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Access to learning section is blocked during the test.",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // User Profile Section
                    item {
                        UserProfileSection(userName, userEmail, userPhotoUrl)
                    }

                    // Progress Stats
                    item {
                        ProgressStats(stats)
                    }

                    // Today's Challenge
                    item {
                        TodaysChallenge(floatingOffset, stats)
                    }

                    // Achievement Badges
                    item {
                        AchievementBadges(stats)
                    }

                    // Recent Quiz Activity
                    item {
                        RecentQuizActivity(stats)
                    }

                    // Test Analyzer
                    item {
                        TestAnalyzerSection(detailedResults)
                    }
                }
            }
        }
    }
}


@Composable
fun TodaysChallenge(floatingOffset: Float, stats: DashboardStats) {
    val wordsGoal = 5
    val wordsLearned = stats.wordsRated.coerceAtMost(wordsGoal)
    val progress = if (wordsGoal > 0) wordsLearned.toFloat() / wordsGoal else 0f
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = floatingOffset.dp)
            .shadow(3.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE0B2))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎯 Today's Challenge",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE65100)
                )
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Challenge",
                    tint = Color(0xFFE65100),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Rate $wordsGoal words today!",
                fontSize = 14.sp,
                color = Color(0xFFE65100)
            )
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFE65100),
                trackColor = Color(0xFFE65100).copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$wordsLearned/$wordsGoal completed",
                fontSize = 12.sp,
                color = Color(0xFFE65100),
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun AchievementBadges(stats: DashboardStats) {
    val hasFirstQuiz = stats.quizzesTaken >= 1
    val hasPerfectScore = stats.quizAccuracy >= 1.0f
    val hasFavorites = stats.favoriteCount >= 1
    val hasRated = stats.wordsRated >= 10
    Column {
        Text(
            text = "🏆 Achievements",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AchievementBadge("First Quiz", "🌟", hasFirstQuiz)
            AchievementBadge("Favorited", "❤️", hasFavorites)
            AchievementBadge("10 Words Rated", "👑", hasRated)
            AchievementBadge("Perfect Score", "💯", hasPerfectScore)
        }
    }
}

@Composable
fun AchievementBadge(title: String, emoji: String, unlocked: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(80.dp)
            .background(
                color = if (unlocked) VibrantGreen else Color.Gray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(8.dp)
    ) {
        Text(
            text = emoji,
            fontSize = 24.sp
        )
        Text(
            text = title,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            color = if (unlocked) Color.White else Color.Gray
        )
    }
}

@Composable
fun RecentQuizActivity(stats: DashboardStats) {
    if (stats.quizzesTaken == 0) return
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📚 Your Quiz Summary",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            ActivityItem(
                title = "Quizzes Taken: ${stats.quizzesTaken}",
                subtitle = "Keep it up!",
                icon = "📝"
            )
            ActivityItem(
                title = "Overall Accuracy: ${(stats.quizAccuracy * 100).toInt()}%",
                subtitle = if (stats.quizAccuracy >= 0.7f) "Great job! 🎉" else "Keep practicing!",
                icon = "🎯"
            )
            if (stats.favoriteCount > 0) {
                ActivityItem(
                    title = "Favorited ${stats.favoriteCount} word(s)",
                    subtitle = "Your personal word list is growing!",
                    icon = "⭐"
                )
            }
            if (stats.wordsRated > 0) {
                ActivityItem(
                    title = "Rated ${stats.wordsRated} word(s)",
                    subtitle = "Difficulty ratings help you focus!",
                    icon = "🏷️"
                )
            }
        }
    }
}

@Composable
fun ActivityItem(title: String, subtitle: String, icon: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ProgressBarWidget(label: String, progress: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = color, fontWeight = FontWeight.Bold)
            Text("${(progress * 100).toInt()}%", color = color)
        }
        LinearProgressIndicator(
            progress = { progress },
            color = color,
            trackColor = color.copy(alpha = 0.2f),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        )
    }
}

@Composable
fun UserProfileSection(userName: String, userEmail: String, userPhotoUrl: String?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
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
                    text = "Welcome back, $userName!",
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
fun ProgressStats(stats: DashboardStats) {
    val totalWordsInApp = categories.sumOf { it.words.size }
    val ratedProgress = if (totalWordsInApp > 0) stats.wordsRated.toFloat() / totalWordsInApp else 0f
    val quizProgress = stats.quizAccuracy.coerceIn(0f, 1f)
    val favoriteProgress = if (totalWordsInApp > 0) stats.favoriteCount.toFloat() / totalWordsInApp else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📊 Your Progress",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProgressPieChart(progress = ratedProgress, color = VibrantBlue, label = "Words Rated")
                ProgressPieChart(progress = quizProgress, color = VibrantGreen, label = "Quiz Accuracy")
                ProgressPieChart(progress = favoriteProgress, color = VibrantOrange, label = "Favorites")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProgressStat("Rated", "${stats.wordsRated}", "$totalWordsInApp", ratedProgress, VibrantBlue)
                ProgressStat("Quizzes", "${stats.quizzesTaken}", "taken", quizProgress, VibrantGreen)
                ProgressStat("Favorites", "${stats.favoriteCount}", "$totalWordsInApp", favoriteProgress, VibrantOrange)
            }
        }
    }
}

@Composable
fun ProgressStat(title: String, current: String, total: String, progress: Float, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "$current/$total",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .width(60.dp)
                .height(4.dp),
            color = color,
            trackColor = color.copy(alpha = 0.3f)
        )
    }
}

@Composable
fun QuickActions(onNavigateToHome: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "⚡ Quick Actions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionButton(
                    icon = Icons.Default.MenuBook,
                    text = "Categories",
                    onClick = onNavigateToHome,
                    color = VibrantBlue
                )
                QuickActionButton(
                    icon = Icons.Default.Add,
                    text = "Add Word",
                    onClick = { /* TODO */ },
                    color = VibrantGreen
                )
                QuickActionButton(
                    icon = Icons.Default.Star,
                    text = "Favorites",
                    onClick = { /* TODO */ },
                    color = VibrantOrange
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    color: Color
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.size(100.dp, 80.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun WordOfTheDay() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        )
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
                    text = "🌟 Word of the Day",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Word of the Day",
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Serendipity",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "The occurrence of events by chance in a happy or beneficial way.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            Text(
                text = "Example: Finding the book was pure serendipity.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

@Composable
fun ProgressPieChart(progress: Float, color: Color, label: String) {
    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(120.dp)) {
            drawArc(
                color = color.copy(alpha = 0.2f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 18f, cap = StrokeCap.Round)
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 18f, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${(progress * 100).toInt()}%", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = color)
            Text(label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun TestAnalyzerSection(results: List<DetailedQuizResult>) {
    if (results.isEmpty()) return
    Column {
        Text(
            text = "🔬 Test Analyzer",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        results.forEach { result ->
            TestAnalyzerItem(result)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TestAnalyzerItem(result: DetailedQuizResult) {
    var expanded by remember { mutableStateOf(false) }
    val percent = if (result.total > 0) (result.score * 100 / result.total) else 0
    val dateStr = remember(result.timestamp) {
        val sdf = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault())
        sdf.format(java.util.Date(result.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = result.categoryTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = dateStr,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // Score badge
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (percent >= 70) Color(0xFFC8E6C9) else if (percent >= 40) Color(0xFFFFF9C4) else Color(0xFFFFCDD2)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${result.score}/${result.total} ($percent%)",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (percent >= 70) Color(0xFF2E7D32) else if (percent >= 40) Color(0xFFF57F17) else Color(0xFFC62828)
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Expanded detail view
            if (expanded && result.answers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(8.dp))

                // Summary bar
                val correctCount = result.answers.count { it.isCorrect }
                val wrongCount = result.answers.size - correctCount
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Correct",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("$correctCount Correct", fontSize = 13.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Medium)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Wrong",
                            tint = Color(0xFFF44336),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("$wrongCount Wrong", fontSize = 13.sp, color = Color(0xFFF44336), fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                result.answers.forEachIndexed { index, answer ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(
                                color = if (answer.isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = if (answer.isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = if (answer.isCorrect) "Correct" else "Wrong",
                            tint = if (answer.isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Q${index + 1}: ${answer.word}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "Your answer: ${answer.userAnswer}",
                                fontSize = 12.sp,
                                color = if (answer.isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                            if (!answer.isCorrect) {
                                Text(
                                    text = "Correct: ${answer.correctAnswer}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            } else if (expanded && result.answers.isEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Detailed breakdown not available for this older test.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}