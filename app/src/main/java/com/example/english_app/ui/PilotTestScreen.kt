package com.example.english_app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                            total = pilotTestQuestions.size
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
                color = Color.Gray
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
                    color = Color.Black,
                    modifier = Modifier.padding(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            question.options.forEachIndexed { index, option ->
                val isSelected = selectedOption == index
                val isCorrectOption = index == question.correctIndex
                val showFeedback = selectedOption != null

                val containerColor = when {
                    !showFeedback -> Color.White
                    isCorrectOption -> Color(0xFFC8E6C9)
                    isSelected && !isCorrectOption -> Color(0xFFFFCDD2)
                    else -> Color.White
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = containerColor),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    onClick = {
                        if (selectedOption == null) {
                            selectedOption = index
                            if (isCorrectOption) score++
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (showFeedback && isCorrectOption) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Correct",
                                tint = Color(0xFF388E3C),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(text = option, fontSize = 15.sp, color = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
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
