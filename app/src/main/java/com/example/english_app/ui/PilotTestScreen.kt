package com.example.english_app.ui

<<<<<<< HEAD
=======
import androidx.compose.foundation.background
import androidx.compose.foundation.border
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
<<<<<<< HEAD
import androidx.compose.material.icons.filled.CheckCircle
=======
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
<<<<<<< HEAD
=======
import com.example.english_app.data.QuizAnswerDetail
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
import com.example.english_app.data.UserProgressRepository
import com.example.english_app.ui.theme.VibrantPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PilotTestScreen(onBack: () -> Unit) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }
    var resultSaved by remember { mutableStateOf(false) }
<<<<<<< HEAD
=======
    val userAnswers = remember { mutableStateListOf<QuizAnswerDetail>() }
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(PILOT_TEST_TITLE, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VibrantPurple)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (isFinished) {
                LaunchedEffect(Unit) {
                    if (!resultSaved) {
                        UserProgressRepository.recordQuizResult(
                            categoryId = PILOT_TEST_CATEGORY_ID,
                            categoryTitle = PILOT_TEST_TITLE,
                            score = score,
<<<<<<< HEAD
                            total = pilotTestQuestions.size
=======
                            total = pilotTestQuestions.size,
                            answers = userAnswers
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
                        )
                        resultSaved = true
                    }
                }
                QuizResultView(
                    score = score,
                    total = pilotTestQuestions.size,
                    color = VibrantPurple,
                    onRetry = {
                        currentIndex = 0
                        score = 0
                        selectedOption = null
                        isFinished = false
                        resultSaved = false
<<<<<<< HEAD
=======
                        userAnswers.clear()
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
                    },
                    onBack = onBack
                )
                return@Scaffold
            }

            val question = pilotTestQuestions[currentIndex]

            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / pilotTestQuestions.size.toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = VibrantPurple,
                trackColor = VibrantPurple.copy(alpha = 0.25f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Question ${currentIndex + 1} of ${pilotTestQuestions.size}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = VibrantPurple.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = question.prompt,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            question.options.forEachIndexed { index, option ->
                val isSelected = selectedOption == index
<<<<<<< HEAD
                val isCorrectOption = index == question.correctIndex
                val showFeedback = selectedOption != null

                val containerColor = when {
                    !showFeedback -> MaterialTheme.colorScheme.surface
                    isCorrectOption -> Color(0xFFC8E6C9)
                    isSelected && !isCorrectOption -> Color(0xFFFFCDD2)
                    else -> MaterialTheme.colorScheme.surface
                }
=======
                
                val containerColor by androidx.compose.animation.animateColorAsState(
                    targetValue = if (isSelected) VibrantPurple.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                    label = "colorAnim"
                )
                
                val borderWidth by androidx.compose.animation.core.animateDpAsState(
                    targetValue = if (isSelected) 2.dp else 0.dp,
                    label = "borderAnim"
                )
                
                val elevation by androidx.compose.animation.core.animateDpAsState(
                    targetValue = if (isSelected) 6.dp else 2.dp,
                    label = "elevationAnim"
                )
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = containerColor),
<<<<<<< HEAD
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    onClick = {
                        if (selectedOption == null) {
                            selectedOption = index
                            if (isCorrectOption) score++
                        }
=======
                    border = if (borderWidth > 0.dp) androidx.compose.foundation.BorderStroke(borderWidth, VibrantPurple) else null,
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = elevation),
                    onClick = {
                        // Allow users to deselect the chosen answer by tapping it again
                        selectedOption = if (selectedOption == index) null else index
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
<<<<<<< HEAD
                        if (showFeedback && isCorrectOption) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Correct",
                                tint = Color(0xFF388E3C),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
=======
                        // Custom animated radio button / check circle indicator
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    color = if (isSelected) VibrantPurple else Color.Transparent,
                                    shape = androidx.compose.foundation.shape.CircleShape
                                )
                                .border(
                                    width = 2.dp,
                                    color = if (isSelected) VibrantPurple else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    shape = androidx.compose.foundation.shape.CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
                        Text(text = option, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
<<<<<<< HEAD
=======
                    selectedOption?.let { selIndex ->
                        val isCorrect = selIndex == question.correctIndex
                        if (isCorrect) {
                            score++
                        }
                        userAnswers.add(
                            QuizAnswerDetail(
                                word = question.prompt, // For Pilot test, prompt is the sentence/question
                                correctAnswer = question.options[question.correctIndex],
                                userAnswer = question.options[selIndex],
                                isCorrect = isCorrect
                            )
                        )
                    }

>>>>>>> 73d420b5c198105f2a9f3f976511c9aad67dfa69
                    if (currentIndex < pilotTestQuestions.size - 1) {
                        currentIndex++
                        selectedOption = null
                    } else {
                        isFinished = true
                    }
                },
                enabled = selectedOption != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VibrantPurple),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (currentIndex < pilotTestQuestions.size - 1) "Next" else "Finish",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}
