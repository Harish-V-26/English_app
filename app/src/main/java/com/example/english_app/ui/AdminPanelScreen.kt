package com.example.english_app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.english_app.data.UserProgressRepository
import com.example.english_app.data.StudentReport
import com.example.english_app.data.StudentTestResult
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

private fun exportCsv(context: Context, uri: android.net.Uri, reports: List<StudentReport>, department: String) {
    try {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            java.io.OutputStreamWriter(outputStream).use { writer ->
                writer.write("Department:,${department}\n\n")
                writer.write("Roll No,Name,Tests Taken,Total Score,Total Questions,Accuracy (%)\n")
                for (report in reports) {
                    val percent = if (report.totalQuestions > 0) (report.totalScore * 100 / report.totalQuestions) else 0
                    writer.write("${report.rollNo},${report.name},${report.testResults.size},${report.totalScore},${report.totalQuestions},${percent}%\n")
                }
            }
        }
        Toast.makeText(context, "Report downloaded successfully!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to download report: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    onBack: () -> Unit,
    teacherDepartment: String = "",
    isAdmin: Boolean = false
) {
    // Real access gate: even if someone reaches this screen via a forged nav
    // state or deep link, non-admins see nothing but a blocked message here.
    // (The Admin Panel menu item on Home is already hidden for non-admins —
    // this is the second, enforced layer. The real, authoritative layer must
    // still be your Firestore security rules — see note at the bottom of this file.)
    if (!isAdmin) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Admin Panel", color = Color.White) },
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
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Restricted",
                    tint = Color.Gray,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Admin access only",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "This section is restricted to teacher/admin accounts.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        return
    }

    var departments by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedDepartment by remember { mutableStateOf(teacherDepartment) }
    var reports by remember { mutableStateOf<List<StudentReport>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var expandedDropdown by remember { mutableStateOf(false) }
    
    val availableCategories = remember(reports) {
        reports.flatMap { it.testResults }.map { it.categoryTitle }.distinct().sorted()
    }
    var selectedCategory by remember { mutableStateOf("All Categories") }
    var expandedCategoryDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(availableCategories) {
        if (selectedCategory != "All Categories" && !availableCategories.contains(selectedCategory)) {
            selectedCategory = "All Categories"
        }
    }

    val filteredReports = remember(reports, selectedCategory) {
        if (selectedCategory == "All Categories") {
            reports
        } else {
            reports.map { report ->
                report.copy(
                    testResults = report.testResults.filter { it.categoryTitle == selectedCategory }
                )
            }.map { report ->
                report.copy(
                    totalScore = report.testResults.sumOf { it.score },
                    totalQuestions = report.testResults.sumOf { it.total }
                )
            }
        }
    }

    val context = LocalContext.current
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            exportCsv(context, uri, reports, selectedDepartment)
        }
    }

    // Load all departments then auto-load teacher's data
    LaunchedEffect(Unit) {
        // Step 1: If teacher has a department, immediately start loading their students
        if (selectedDepartment.isNotBlank()) {
            isLoading = true
            errorMessage = null
            UserProgressRepository.getDepartmentStudentReports(
                department = selectedDepartment,
                onResult = { result ->
                    reports = result
                    isLoading = false
                },
                onError = { message ->
                    errorMessage = message
                    isLoading = false
                }
            )
        }
        // Step 2: Load full list of departments for the dropdown
        UserProgressRepository.loadAllDepartments(
            onResult = { depts ->
                // Add teacher's own department to the list if not already there
                val allDepts = if (selectedDepartment.isNotBlank() && selectedDepartment !in depts) {
                    (listOf(selectedDepartment) + depts).distinct().sorted()
                } else depts
                departments = allDepts
            },
            onError = { message ->
                if (errorMessage == null) errorMessage = message
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Panel", color = Color.White) },
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
                    containerColor = VibrantPurple
                ),
                actions = {
                    IconButton(onClick = {
                        if (selectedDepartment.isNotBlank()) {
                            isLoading = true
                            errorMessage = null
                            UserProgressRepository.getDepartmentStudentReports(
                                department = selectedDepartment,
                                onResult = { result ->
                                    reports = result
                                    isLoading = false
                                },
                                onError = { message ->
                                    errorMessage = message
                                    isLoading = false
                                }
                            )
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Data",
                            tint = Color.White
                        )
                    }
                    if (filteredReports.isNotEmpty()) {
                        IconButton(onClick = {
                            val fileName = if (selectedDepartment.isNotBlank()) "${selectedDepartment}_Report.csv" else "Department_Report.csv"
                            createDocumentLauncher.launch(fileName)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download Report",
                                tint = Color.White
                            )
                        }
                    }
                }
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
            // Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = VibrantPurple.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Admin",
                            tint = VibrantPurple,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Student Test Reports",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = VibrantPurple
                            )
                            Text(
                                text = "View your department's student performance",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Department Selector
            item {
                Text(
                    text = "Select Department",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedDepartment.ifBlank { "Choose a department" },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = if (expandedDropdown) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = VibrantPurple
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VibrantPurple,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { expandedDropdown = !expandedDropdown }
                    )
                    DropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        if (departments.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No departments found") },
                                onClick = { expandedDropdown = false }
                            )
                        }
                        departments.forEach { dept ->
                            DropdownMenuItem(
                                text = { Text(dept) },
                                onClick = {
                                    selectedDepartment = dept
                                    expandedDropdown = false
                                    isLoading = true
                                    errorMessage = null
                                    UserProgressRepository.getDepartmentStudentReports(
                                        department = dept,
                                        onResult = { result ->
                                            reports = result
                                            isLoading = false
                                        },
                                        onError = { message ->
                                            errorMessage = message
                                            isLoading = false
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Category Selector
            if (availableCategories.isNotEmpty()) {
                item {
                    Text(
                        text = "Select Category",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    ExposedDropdownMenuBox(
                        expanded = expandedCategoryDropdown,
                        onExpandedChange = { expandedCategoryDropdown = it }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoryDropdown)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VibrantPurple,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategoryDropdown,
                            onDismissRequest = { expandedCategoryDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Categories") },
                                onClick = {
                                    selectedCategory = "All Categories"
                                    expandedCategoryDropdown = false
                                }
                            )
                            availableCategories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        selectedCategory = cat
                                        expandedCategoryDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Loading
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = VibrantPurple)
                    }
                }
            }

            // Summary Stats
            if (!isLoading && filteredReports.isNotEmpty()) {
                item {
                    val totalStudents = filteredReports.size
                    val totalTests = filteredReports.sumOf { it.testResults.size }
                    val avgScore = if (filteredReports.any { it.totalQuestions > 0 }) {
                        val totalScore = filteredReports.sumOf { it.totalScore }
                        val totalQ = filteredReports.sumOf { it.totalQuestions }
                        if (totalQ > 0) (totalScore * 100 / totalQ) else 0
                    } else 0

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AdminStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Students",
                            value = "$totalStudents",
                            icon = Icons.Default.People,
                            color = VibrantBlue
                        )
                        AdminStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Tests Taken",
                            value = "$totalTests",
                            icon = Icons.Default.Quiz,
                            color = VibrantGreen
                        )
                        AdminStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Avg Score",
                            value = "$avgScore%",
                            icon = Icons.Default.TrendingUp,
                            color = VibrantOrange
                        )
                    }
                }

                // Per-Student Graph
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    DepartmentPerformanceGraph(filteredReports)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Per-Category Graph
                item {
                    CategoryPerformanceGraph(filteredReports)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Table Header
                item {
                    Text(
                        text = "Student Reports",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = VibrantPurple.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Roll No", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = VibrantPurple)
                            Text("Name", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = VibrantPurple)
                            Text("Tests", modifier = Modifier.weight(0.7f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = VibrantPurple, textAlign = TextAlign.Center)
                            Text("Score", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = VibrantPurple, textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            // Student rows
            if (!isLoading) {
                items(filteredReports) { report ->
                    StudentReportRow(report)
                }
            }

            // Error state — distinct from "genuinely no data", e.g. Firestore
            // permission denied when reading other students' documents. Seeing
            // this instead of a silent empty list is the whole point of the fix.
            if (!isLoading && errorMessage != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = VibrantRed.copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = "Error",
                                    tint = VibrantRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Couldn't load student reports",
                                    fontWeight = FontWeight.Bold,
                                    color = VibrantRed
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = errorMessage ?: "",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "If this mentions permissions, your Firestore security rules likely don't allow an admin account to read other students' documents yet — that needs a rule change in the Firebase console, not just an app update.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Empty state
            if (!isLoading && errorMessage == null && selectedDepartment.isNotBlank() && filteredReports.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = "No results",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No student reports found for $selectedDepartment",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Bottom spacer
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun AdminStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StudentReportRow(report: StudentReport) {
    var expanded by remember { mutableStateOf(false) }
    val percent = if (report.totalQuestions > 0) (report.totalScore * 100 / report.totalQuestions) else 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = report.rollNo,
                    modifier = Modifier.weight(1f),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = report.name,
                    modifier = Modifier.weight(1.5f),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${report.testResults.size}",
                    modifier = Modifier.weight(0.7f),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Card(
                    modifier = Modifier.weight(0.8f),
                    colors = CardDefaults.cardColors(
                        containerColor = if (percent >= 70) Color(0xFFC8E6C9) else if (percent >= 40) Color(0xFFFFF9C4) else Color(0xFFFFCDD2)
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (report.totalQuestions > 0) "$percent%" else "—",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (percent >= 70) Color(0xFF2E7D32) else if (percent >= 40) Color(0xFFF57F17) else Color(0xFFC62828),
                        textAlign = TextAlign.Center
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp).size(20.dp)
                )
            }

            // Expanded: show individual test results
            if (expanded && report.testResults.isNotEmpty()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Completed Tests:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    report.testResults.sortedByDescending { it.timestamp }.forEach { test ->
                        val testPercent = if (test.total > 0) (test.score * 100 / test.total) else 0
                        val dateStr = remember(test.timestamp) {
                            val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
                            sdf.format(java.util.Date(test.timestamp))
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(
                                    color = if (testPercent >= 70) Color(0xFFE8F5E9) else if (testPercent >= 40) Color(0xFFFFFDE7) else Color(0xFFFFEBEE),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = test.categoryTitle,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = dateStr,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "${test.score}/${test.total} ($testPercent%)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (testPercent >= 70) Color(0xFF2E7D32) else if (testPercent >= 40) Color(0xFFF57F17) else Color(0xFFC62828)
                                )
                            }
                            
                            if (test.answers.isNotEmpty()) {
                                val incorrect = test.answers.filter { !it.isCorrect }
                                if (incorrect.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Needs Review:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                                    incorrect.forEach { ans ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(ans.word, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                            Text("Ans: ${ans.userAnswer}", fontSize = 11.sp, color = Color(0xFFC62828))
                                        }
                                    }
                                } else if (testPercent == 100) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Perfect score! 🎉", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                }
                            }
                        }
                    }
                }
            } else if (expanded && report.testResults.isEmpty()) {
                Text(
                    text = "No tests completed yet.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryPerformanceGraph(reports: List<StudentReport>) {
    // Aggregate every student's test results by category to get a class-wide
    // average accuracy per category (Advanced Vocabulary, Kitchen Vocabulary, etc.)
    data class CategoryAgg(var totalScore: Int = 0, var totalQuestions: Int = 0, var attempts: Int = 0)
    val byCategory = LinkedHashMap<String, CategoryAgg>()

    reports.forEach { report ->
        report.testResults.forEach { test ->
            val agg = byCategory.getOrPut(test.categoryTitle) { CategoryAgg() }
            agg.totalScore += test.score
            agg.totalQuestions += test.total
            agg.attempts += 1
        }
    }

    if (byCategory.isEmpty()) return

    val categoryPercents = byCategory.entries
        .map { (title, agg) ->
            val percent = if (agg.totalQuestions > 0) (agg.totalScore * 100 / agg.totalQuestions) else 0
            Triple(title, percent, agg.attempts)
        }
        .sortedByDescending { it.second }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Average Score by Category",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Class-wide accuracy across all attempts, per category",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categoryPercents) { (title, percent, attempts) ->
                    val barColor = if (percent >= 70) VibrantGreen else if (percent >= 40) VibrantOrange else VibrantRed
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(56.dp)
                    ) {
                        Text(
                            text = "$percent%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(0.7f),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(if (percent > 0) (percent / 100f) else 0.03f)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(barColor)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = title,
                            fontSize = 9.sp,
                            maxLines = 2,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.height(24.dp)
                        )
                        Text(
                            text = "$attempts attempt(s)",
                            fontSize = 8.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DepartmentPerformanceGraph(reports: List<StudentReport>) {
    if (reports.isEmpty()) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Accuracy Overview",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // Take top 10 students to avoid overcrowding the graph
                val displayReports = reports.take(10)
                
                displayReports.forEach { report ->
                    val percent = if (report.totalQuestions > 0) (report.totalScore * 100 / report.totalQuestions) else 0
                    val barHeight = if (percent > 0) (percent / 100f) else 0.05f
                    val barColor = if (percent >= 70) VibrantGreen else if (percent >= 40) VibrantOrange else VibrantRed

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${percent}%",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .fillMaxHeight(barHeight)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(barColor)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = report.name.take(4), // Abbreviate name
                            fontSize = 10.sp,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
