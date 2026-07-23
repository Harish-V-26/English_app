package com.example.english_app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.english_app.data.UserProgressRepository
import com.example.english_app.data.QuizAnswerDetail
import com.example.english_app.ui.theme.*
import androidx.compose.foundation.BorderStroke

data class QuizQuestion(
    val word: Word,
    val options: List<String>,
    val correctIndex: Int
)

/** Builds multiple-choice questions: word -> pick the correct definition. */
fun buildQuizQuestions(category: Category, questionCount: Int = 8): List<QuizQuestion> {
    val words = category.words
    if (words.size < 4) {
        // Not enough words for 4 options — fall back to using all words as the pool
        return words.shuffled().take(minOf(questionCount, words.size)).map { word ->
            val distractors = (words - word).shuffled().take(minOf(3, words.size - 1))
            val options = (distractors.map { it.definition } + word.definition).shuffled()
            QuizQuestion(word, options, options.indexOf(word.definition))
        }
    }
    return words.shuffled().take(minOf(questionCount, words.size)).map { word ->
        val distractors = (words - word).shuffled().take(3)
        val options = (distractors.map { it.definition } + word.definition).shuffled()
        QuizQuestion(word, options, options.indexOf(word.definition))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    category: Category,
    onBack: () -> Unit
) {
    var questions by remember(category.id) { mutableStateOf(buildQuizQuestions(category)) }
    var currentIndex by remember(category.id) { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var score by remember(category.id) { mutableIntStateOf(0) }
    var isFinished by remember(category.id) { mutableStateOf(false) }
    var resultSaved by remember(category.id) { mutableStateOf(false) }
    val userAnswers = remember(category.id) { mutableListOf<QuizAnswerDetail>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz: ${category.title}", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = category.color)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (questions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Not enough words in this category to build a quiz yet.")
                }
                return@Scaffold
            }

            if (isFinished) {
                LaunchedEffect(Unit) {
                    if (!resultSaved) {
                        UserProgressRepository.recordQuizResult(
                            categoryId = category.id,
                            categoryTitle = category.title,
                            score = score,
                            total = questions.size,
                            answers = userAnswers.toList()
                        )
                        resultSaved = true
                        // Prevent showing correct answers
                    }
                }
                QuizResultView(
                    score = score,
                    total = questions.size,
                    color = category.color,
                    onRetry = {
                        questions = buildQuizQuestions(category)
                        currentIndex = 0
                        score = 0
                        selectedOption = null
                        isFinished = false
                        userAnswers.clear()
                        resultSaved = false
                    },
                    onBack = onBack
                )
                return@Scaffold
            }

            val question = questions[currentIndex]

            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / questions.size.toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = category.color,
                trackColor = category.color.copy(alpha = 0.25f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Question ${currentIndex + 1} of ${questions.size}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = category.color.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "What does this word mean?",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = question.word.word,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            question.options.forEachIndexed { index, option ->
                val isSelected = selectedOption == index
                
                val containerColor by androidx.compose.animation.animateColorAsState(
                    targetValue = if (isSelected) category.color.copy(alpha = 0.15f) else Color.White,
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

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = containerColor),
                    border = if (borderWidth > 0.dp) BorderStroke(borderWidth, category.color) else null,
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = elevation),
                    onClick = {
                        // Allow users to deselect the chosen answer by tapping it again
                        selectedOption = if (selectedOption == index) null else index
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Custom animated radio button / check circle indicator
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    color = if (isSelected) category.color else Color.Transparent,
                                    shape = androidx.compose.foundation.shape.CircleShape
                                )
                                .border(
                                    width = 2.dp,
                                    color = if (isSelected) category.color else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
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
                        
                        Text(text = option, fontSize = 15.sp, color = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    selectedOption?.let { selIndex ->
                        val isCorrect = selIndex == question.correctIndex
                        if (isCorrect) score++
                        userAnswers.add(
                            QuizAnswerDetail(
                                word = question.word.word,
                                correctAnswer = question.options[question.correctIndex],
                                userAnswer = question.options[selIndex],
                                isCorrect = isCorrect
                            )
                        )
                    }

                    if (currentIndex < questions.size - 1) {
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
                colors = ButtonDefaults.buttonColors(containerColor = category.color),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (currentIndex < questions.size - 1) "Next" else "Finish",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun QuizResultView(
    score: Int,
    total: Int,
    color: Color,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    val percent = if (total > 0) (score * 100 / total) else 0
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val isPerfectScore = score == total && total > 0
        if (isPerfectScore) {
            val composition by com.airbnb.lottie.compose.rememberLottieComposition(
                com.airbnb.lottie.compose.LottieCompositionSpec.Url("https://assets9.lottiefiles.com/packages/lf20_obhph3sh.json")
            )
            com.airbnb.lottie.compose.LottieAnimation(
                composition = composition,
                iterations = 1,
                modifier = Modifier.size(150.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Result",
                tint = color,
                modifier = Modifier.size(64.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "You scored $score / $total",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "$percent% correct",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = color),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(0.7f).height(48.dp)
        ) {
            Text("Try Again", color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = onBack,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(0.7f).height(48.dp)
        ) {
            Text("Back to Words")
        }
    }
}
