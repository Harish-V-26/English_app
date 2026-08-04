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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
            java.io.OutputStreamWriter(outputStream, java.nio.charset.StandardCharsets.UTF_8).use { writer ->
                // Write UTF-8 Byte Order Mark (BOM) so Excel reads formatting and special characters perfectly
                writer.write("\uFEFF")

                val sdf = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault())
                val nowFormatted = sdf.format(java.util.Date())
                val deptTitle = department.ifBlank { "All Departments" }

                val sortedReports = reports.sortedWith(compareBy({ it.name.lowercase() }, { it.rollNo }))
                val totalStudents = sortedReports.size
                val evaluatedStudents = sortedReports.count { it.totalQuestions > 0 }
                val totalTests = sortedReports.sumOf { it.testResults.size }
                val avgAccuracy = if (sortedReports.any { it.totalQuestions > 0 }) {
                    val totalScore = sortedReports.sumOf { it.totalScore }
                    val totalQ = sortedReports.sumOf { it.totalQuestions }
                    if (totalQ > 0) (totalScore * 100 / totalQ) else 0
                } else 0

                // Header Block with professional spacing
                writer.write("STUDENT TEST PERFORMANCE REPORT\n")
                writer.write("Department:,${deptTitle}\n")
                writer.write("Report Generated On:,${nowFormatted}\n")
                writer.write("Total Students:,${totalStudents}\n")
                writer.write("Department Average Score:,${avgAccuracy}%\n\n")

                // Table Headers
                writer.write("S.No,Roll Number,Student Name,Department,Test Name,Highest Score,Total Questions,Accuracy (%),Date of Attempt\n")

                var sNo = 1
                for (report in sortedReports) {
                    // Take strictly the highest score attempt per test category
                    val bestTests = report.testResults
                        .groupBy { it.categoryTitle }
                        .mapValues { (_, list) -> list.maxByOrNull { it.score } ?: list.first() }
                        .values
                        .toList()

                    val highestScore = bestTests.sumOf { it.score }
                    val totalQuestions = bestTests.sumOf { it.total }
                    val percent = if (totalQuestions > 0) (highestScore * 100 / totalQuestions) else 0

                    val testName = if (bestTests.isNotEmpty()) {
                        bestTests.joinToString(" | ") { it.categoryTitle }
                    } else {
                        "Not Attempted"
                    }

                    val testTakenOn = if (bestTests.isNotEmpty()) {
                        bestTests.sortedByDescending { it.timestamp }.map { test ->
                            if (test.timestamp > 0L) {
                                sdf.format(java.util.Date(test.timestamp))
                            } else {
                                "Completed"
                            }
                        }.distinct().joinToString(" | ")
                    } else {
                        "N/A"
                    }

                    // Format roll number with ="..." formula syntax so Excel NEVER converts to scientific notation (e.g. 2.4E+07)
                    val rollNoFormula = "\"=\"\"${report.rollNo.replace("\"", "\"\"")}\"\"\""
                    val nameSafe = "\"${report.name.replace("\"", "\"\"")}\""
                    val deptSafe = "\"${report.department.replace("\"", "\"\"")}\""
                    val testNameSafe = "\"${testName.replace("\"", "\"\"")}\""
                    val testTakenOnSafe = "\"${testTakenOn.replace("\"", "\"\"")}\""

                    writer.write("${sNo},${rollNoFormula},${nameSafe},${deptSafe},${testNameSafe},${highestScore},${totalQuestions},${percent}%,${testTakenOnSafe}\n")
                    sNo++
                }

                // Summary footer with line spacing
                writer.write("\n\n")
                writer.write("SUMMARY OVERVIEW\n")
                writer.write("Total Evaluated Students:,${evaluatedStudents}\n")
                writer.write("Total Tests Completed:,${totalTests}\n")
                writer.write("Overall Department Accuracy:,${avgAccuracy}%\n")
            }
        }
        Toast.makeText(context, "Report exported successfully!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to export report: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    onBack: () -> Unit,
    teacherDepartment: String = ""
) {
    var departments by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedDepartment by remember { mutableStateOf(teacherDepartment) }
    var reports by remember { mutableStateOf<List<StudentReport>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var expandedDropdown by remember { mutableStateOf(false) }
    
    var showClearConfirmDialog by remember { mutableStateOf(false) }

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
        val base = if (selectedCategory == "All Categories") {
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
        base.sortedWith(compareBy({ it.name.lowercase() }, { it.rollNo }))
    }

    val context = LocalContext.current
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            exportCsv(context, uri, filteredReports, selectedDepartment)
        }
    }

    // Load all departments on initial composition
    LaunchedEffect(Unit) {
        UserProgressRepository.loadAllDepartments { depts ->
            departments = depts
            if (selectedDepartment.isBlank()) {
                val normTeacherDept = UserProgressRepository.formatDepartmentName(teacherDepartment)
                val defaultDept = if (normTeacherDept.isNotBlank() && depts.any { it.equals(normTeacherDept, ignoreCase = true) }) {
                    depts.first { it.equals(normTeacherDept, ignoreCase = true) }
                } else if (depts.isNotEmpty()) {
                    depts.first()
                } else {
                    "All Departments"
                }
                selectedDepartment = defaultDept
            }
        }
    }

    // Real-time live listener for student reports (updates instantly as tests are submitted)
    DisposableEffect(selectedDepartment) {
        val deptToFetch = selectedDepartment.ifBlank { "All Departments" }
        isLoading = true
        val registration = UserProgressRepository.observeDepartmentStudentReports(deptToFetch) { result ->
            reports = result
            isLoading = false
        }
        onDispose {
            registration.remove()
        }
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = {
                Text(
                    text = "Clear Past Test History?",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "This will remove all student quiz and test results recorded during testing. This action cannot be undone.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showClearConfirmDialog = false
                        isLoading = true
                        UserProgressRepository.clearAllTestResults(selectedDepartment) { success ->
                            if (success) {
                                Toast.makeText(context, "Test history cleared successfully!", Toast.LENGTH_SHORT).show()
                                val deptToFetch = selectedDepartment.ifBlank { "All Departments" }
                                UserProgressRepository.getDepartmentStudentReports(deptToFetch) { result ->
                                    reports = result
                                    isLoading = false
                                }
                            } else {
                                isLoading = false
                                Toast.makeText(context, "Failed to clear test history.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VibrantRed)
                ) {
                    Text("Clear All Data", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel")
                }
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
                        showClearConfirmDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear Test History",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = {
                        UserProgressRepository.loadAllDepartments { depts ->
                            departments = depts
                        }
                        val deptToFetch = selectedDepartment.ifBlank { "All Departments" }
                        isLoading = true
                        UserProgressRepository.getDepartmentStudentReports(deptToFetch) { result ->
                            reports = result
                            isLoading = false
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
                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = it }
                ) {
                    OutlinedTextField(
                        value = selectedDepartment.ifBlank { "Choose a department" },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown)
                        },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
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
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
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
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
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
                            icon = Icons.AutoMirrored.Filled.TrendingUp,
                            color = VibrantOrange
                        )
                    }
                }

                // Department Graph
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    DepartmentPerformanceGraph(filteredReports)
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
                    StudentReportRow(report = report)
                }
            }

            // Empty state
            if (!isLoading && selectedDepartment.isNotBlank() && filteredReports.isEmpty()) {
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
fun StudentReportRow(
    report: StudentReport
) {
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
                        text = "Completed Tests (Highest Score):",
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(
                                    color = if (testPercent >= 70) Color(0xFFE8F5E9) else if (testPercent >= 40) Color(0xFFFFFDE7) else Color(0xFFFFEBEE),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp),
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
fun DepartmentPerformanceGraph(reports: List<StudentReport>) {
    if (reports.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Score & Accuracy Distribution",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            var below40 = 0
            var between40and60 = 0
            var between60and80 = 0
            var between80and90 = 0
            var above90 = 0

            reports.forEach { report ->
                val percent = if (report.totalQuestions > 0) (report.totalScore * 100 / report.totalQuestions) else 0
                when {
                    percent < 40 -> below40++
                    percent < 60 -> between40and60++
                    percent < 80 -> between60and80++
                    percent < 90 -> between80and90++
                    else -> above90++
                }
            }

            val total = reports.size.toFloat()
            val below40Angle = if (total > 0) (below40 / total) * 360f else 0f
            val between40and60Angle = if (total > 0) (between40and60 / total) * 360f else 0f
            val between60and80Angle = if (total > 0) (between60and80 / total) * 360f else 0f
            val between80and90Angle = if (total > 0) (between80and90 / total) * 360f else 0f
            val above90Angle = if (total > 0) (above90 / total) * 360f else 0f

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pie Chart
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        var startAngle = -90f
                        if (above90Angle > 0) {
                            drawArc(
                                color = Color(0xFF7B1FA2), // Purple - 90%+
                                startAngle = startAngle,
                                sweepAngle = above90Angle,
                                useCenter = true,
                                size = size
                            )
                            startAngle += above90Angle
                        }
                        if (between80and90Angle > 0) {
                            drawArc(
                                color = Color(0xFF43A047), // Green - 80% to 89%
                                startAngle = startAngle,
                                sweepAngle = between80and90Angle,
                                useCenter = true,
                                size = size
                            )
                            startAngle += between80and90Angle
                        }
                        if (between60and80Angle > 0) {
                            drawArc(
                                color = Color(0xFF1E88E5), // Blue - 60% to 79%
                                startAngle = startAngle,
                                sweepAngle = between60and80Angle,
                                useCenter = true,
                                size = size
                            )
                            startAngle += between60and80Angle
                        }
                        if (between40and60Angle > 0) {
                            drawArc(
                                color = Color(0xFFFB8C00), // Orange - 40% to 59%
                                startAngle = startAngle,
                                sweepAngle = between40and60Angle,
                                useCenter = true,
                                size = size
                            )
                            startAngle += between40and60Angle
                        }
                        if (below40Angle > 0) {
                            drawArc(
                                color = Color(0xFFE53935), // Red - Below 40%
                                startAngle = startAngle,
                                sweepAngle = below40Angle,
                                useCenter = true,
                                size = size
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Legend
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PieChartLegendItem(color = Color(0xFF7B1FA2), text = "≥ 90% (Excellent)", count = above90, total = reports.size)
                    PieChartLegendItem(color = Color(0xFF43A047), text = "80% - 89% (Good)", count = between80and90, total = reports.size)
                    PieChartLegendItem(color = Color(0xFF1E88E5), text = "60% - 79% (Moderate)", count = between60and80, total = reports.size)
                    PieChartLegendItem(color = Color(0xFFFB8C00), text = "40% - 59% (Average)", count = between40and60, total = reports.size)
                    PieChartLegendItem(color = Color(0xFFE53935), text = "< 40% (Below 40%)", count = below40, total = reports.size)
                }
            }
        }
    }
}

@Composable
private fun PieChartLegendItem(color: Color, text: String, count: Int, total: Int) {
    val pct = if (total > 0) (count * 100 / total) else 0
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "$count ($pct%)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
