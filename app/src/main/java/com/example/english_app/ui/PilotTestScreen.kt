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
    var isCheckingAccess by remember { mutableStateOf(true) }
    var isRestricted by remember { mutableStateOf(false) }
    var initialAttempts by remember { mutableIntStateOf(0) }
    var highestPastScore by remember { mutableIntStateOf(0) }
    var pastScoresList by remember { mutableStateOf<List<Int>>(emptyList()) }
    var currentAttemptNumber by remember { mutableIntStateOf(1) }

    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }
    var resultSaved by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        UserProgressRepository.checkPilotTestAccess { restricted, attempts, highest, scores ->
            isRestricted = restricted
            initialAttempts = attempts
            highestPastScore = highest
            pastScoresList = scores
            currentAttemptNumber = attempts + 1
            isCheckingAccess = false
        }
    }

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
        if (isCheckingAccess) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = VibrantPurple)
            }
            return@Scaffold
        }

        // Check if user has already exhausted all 2 attempts
        if (isRestricted && initialAttempts >= 2 && !isFinished) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = VibrantPurple.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed",
                            tint = VibrantPurple,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Pilot Test Completed",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = VibrantPurple,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "You have completed all 2 allowed attempts for the Pilot Test.",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(14.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Your Highest Score",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "$highestPastScore / ${pilotTestQuestions.size}",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VibrantPurple
                                )
                                val percent = if (pilotTestQuestions.isNotEmpty()) highestPastScore * 100 / pilotTestQuestions.size else 0
                                Text(
                                    text = "$percent% Accuracy",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF2E7D32)
                                )
                                if (pastScoresList.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Attempts: " + pastScoresList.mapIndexed { idx, s -> "Attempt ${idx + 1}: $s/${pilotTestQuestions.size}" }.joinToString(" • "),
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VibrantPurple),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Back to Practice", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            return@Scaffold
        }

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

                val bestScore = maxOf(score, highestPastScore)
                val percent = if (pilotTestQuestions.isNotEmpty()) score * 100 / pilotTestQuestions.size else 0

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Result",
                        tint = VibrantPurple,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "You scored $score / ${pilotTestQuestions.size}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$percent% correct",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (isRestricted) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = VibrantPurple.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (currentAttemptNumber >= 2) "All 2 Attempts Completed" else "Attempt 1 of 2 Completed",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = VibrantPurple
                                )
                                Text(
                                    text = "Highest Score Recorded: $bestScore / ${pilotTestQuestions.size}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    if (isRestricted && currentAttemptNumber < 2) {
                        Button(
                            onClick = {
                                currentIndex = 0
                                score = 0
                                selectedOption = null
                                isFinished = false
                                resultSaved = false
                                currentAttemptNumber = 2
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VibrantPurple),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth(0.75f).height(48.dp)
                        ) {
                            Text("Try Again (Final Attempt)", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    } else if (!isRestricted) {
                        Button(
                            onClick = {
                                currentIndex = 0
                                score = 0
                                selectedOption = null
                                isFinished = false
                                resultSaved = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VibrantPurple),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth(0.7f).height(48.dp)
                        ) {
                            Text("Try Again", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedButton(
                        onClick = onBack,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(0.75f).height(48.dp)
                    ) {
                        Text(if (isRestricted && currentAttemptNumber >= 2) "Done / Back to Practice" else "Back to Practice")
                    }
                }
                return@Scaffold
            }

            val question = pilotTestQuestions[currentIndex]

            // Top Attempt badge if restricted
            if (isRestricted) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = VibrantPurple.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (currentAttemptNumber == 1) "Attempt 1 of 2" else "Attempt 2 of 2 (Final)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = VibrantPurple,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    if (highestPastScore > 0) {
                        Text(
                            text = "Best: $highestPastScore/${pilotTestQuestions.size}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

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

                val containerColor = if (isSelected) {
                    VibrantPurple.copy(alpha = 0.2f)
                } else {
                    MaterialTheme.colorScheme.surface
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
                            if (index == question.correctIndex) score++
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = option, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
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
