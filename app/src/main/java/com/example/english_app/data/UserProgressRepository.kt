package com.example.english_app.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import java.util.UUID

/**
 * Handles everything related to a signed-in user's saved progress:
 *  - favorite words
 *  - bookmarked ("saved") words
 *  - per-word difficulty ratings
 *  - quiz results (for the Dashboard stats)
 *  - user profile (name, rollNo, department, role)
 *  - admin: fetch department student reports
 *
 * Data layout in Firestore:
 *   users/{uid}/wordProgress/{categoryId_word}   -> { favorite, bookmarked, difficulty }
 *   users/{uid}/quizResults/{autoId}             -> { categoryId, score, total, timestamp, answers: [...] }
 *   users/{uid}/profile                          -> { name, rollNo, department, role, email }
 */
object UserProgressRepository {

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    /**
     * Returns the current user's UID. Returns null if no user is signed in.
     */
    private fun currentUid(): String? = auth.currentUser?.uid

    /**
     * Runs [block] with the current signed-in user's UID if available.
     */
    private fun withUid(block: (uid: String) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            block(uid)
        } else {
            android.util.Log.w("UserProgressRepository", "Operation skipped: No signed-in user found.")
        }
    }

    private fun wordDocId(categoryId: String, word: String): String =
        "${categoryId}_${word}".replace(Regex("[^A-Za-z0-9_]"), "_")

    private fun wordProgressCollection(uid: String) =
        db.collection("users").document(uid).collection("wordProgress")

    private fun quizResultsCollection(uid: String) =
        db.collection("users").document(uid).collection("quizResults")

    fun setFavorite(categoryId: String, word: String, isFavorite: Boolean) {
        withUid { uid ->
            wordProgressCollection(uid).document(wordDocId(categoryId, word))
                .set(mapOf("favorite" to isFavorite), com.google.firebase.firestore.SetOptions.merge())
        }
    }

    fun setBookmarked(categoryId: String, word: String, isBookmarked: Boolean) {
        withUid { uid ->
            wordProgressCollection(uid).document(wordDocId(categoryId, word))
                .set(mapOf("bookmarked" to isBookmarked), com.google.firebase.firestore.SetOptions.merge())
        }
    }

    fun setDifficulty(categoryId: String, word: String, rating: Int) {
        withUid { uid ->
            wordProgressCollection(uid).document(wordDocId(categoryId, word))
                .set(mapOf("difficulty" to rating), com.google.firebase.firestore.SetOptions.merge())
        }
    }

    /** Loads saved favorite/bookmark/difficulty state for one word. */
    fun loadWordProgress(
        categoryId: String,
        word: String,
        onResult: (favorite: Boolean, bookmarked: Boolean, difficulty: Int) -> Unit
    ) {
        val uid = currentUid() ?: run {
            onResult(false, false, 0)
            return
        }
        wordProgressCollection(uid).document(wordDocId(categoryId, word)).get()
            .addOnSuccessListener { doc ->
                val favorite = doc.getBoolean("favorite") ?: false
                val bookmarked = doc.getBoolean("bookmarked") ?: false
                val difficulty = (doc.getLong("difficulty") ?: 0L).toInt()
                onResult(favorite, bookmarked, difficulty)
            }
            .addOnFailureListener {
                onResult(false, false, 0)
            }
    }

    // ─── User Profile ──────────────────────────────────────────────

    /**
     * Formats names into Title Case (e.g. "HARISH V" or "harish v" -> "Harish V")
     * ensuring consistent display and matching across the app.
     */
    fun formatDisplayName(name: String): String {
        if (name.isBlank()) return ""
        return name.trim().split(Regex("\\s+")).joinToString(" ") { word ->
            if (word.length == 1) {
                word.uppercase()
            } else {
                word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
        }
    }

    /**
     * Normalizes department names into Title Case (e.g. "computer science" -> "Computer Science")
     * preventing fragmented departments in the Admin Panel.
     */
    fun formatDepartmentName(dept: String): String {
        val trimmed = dept.trim()
        if (trimmed.isBlank()) return ""
        return trimmed.split(Regex("\\s+")).joinToString(" ") { word ->
            if (word.length <= 2) word.uppercase() // e.g. "IT", "CS"
            else word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }

    /** Saves user profile data (name, rollNo, department, role, email). */
    fun saveUserProfile(
        name: String,
        rollNo: String,
        department: String,
        role: String = "student",
        email: String = ""
    ) {
        withUid { uid ->
            val formattedName = formatDisplayName(name)
            val formattedDept = formatDepartmentName(department)
            val data = mutableMapOf<String, Any>("uid" to uid)
            if (formattedName.isNotBlank()) data["name"] = formattedName
            if (rollNo.isNotBlank()) data["rollNo"] = rollNo.trim()
            if (formattedDept.isNotBlank()) data["department"] = formattedDept
            if (role.isNotBlank()) data["role"] = role
            if (email.isNotBlank()) data["email"] = email.trim().lowercase()
            
            db.collection("users").document(uid)
                .set(data, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener {
                    android.util.Log.d("UserProfile", "Profile saved for uid=$uid")
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("UserProfile", "Failed to save profile: ${e.message}")
                }
        }
    }

    /** Loads user profile data. */
    fun loadUserProfile(onResult: (UserProfile) -> Unit) {
        val uid = currentUid() ?: run {
            onResult(UserProfile())
            return
        }
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val profile = UserProfile(
                    name = formatDisplayName(doc.getString("name") ?: ""),
                    rollNo = doc.getString("rollNo") ?: "",
                    department = formatDepartmentName(doc.getString("department") ?: ""),
                    role = doc.getString("role") ?: "student",
                    email = doc.getString("email") ?: "",
                    currentStreak = (doc.getLong("currentStreak") ?: 0L).toInt()
                )
                onResult(profile)
            }
            .addOnFailureListener {
                onResult(UserProfile())
            }
    }

    /**
     * Synchronizes and merges quiz results, test history, and word progress across
     * multiple accounts (e.g., Google Sign-In and Email/Password sign-up) that share
     * the same college email address.
     */
    fun syncAccountProgressByEmail(onComplete: () -> Unit = {}) {
        val currentUid = auth.currentUser?.uid ?: run {
            onComplete()
            return
        }
        val currentEmail = (auth.currentUser?.email ?: "").lowercase().trim()
        if (currentEmail.isBlank()) {
            onComplete()
            return
        }

        db.collection("users")
            .whereEqualTo("email", currentEmail)
            .get()
            .addOnSuccessListener { snapshot ->
                val otherDocs = snapshot.documents.filter { it.id != currentUid }
                if (otherDocs.isEmpty()) {
                    onComplete()
                    return@addOnSuccessListener
                }

                val allOtherTestResults = mutableListOf<Map<String, Any>>()
                var otherName = ""
                var otherDept = ""
                var otherRollNo = ""

                otherDocs.forEach { otherDoc ->
                    if (otherName.isBlank()) otherName = otherDoc.getString("name") ?: ""
                    if (otherDept.isBlank()) otherDept = otherDoc.getString("department") ?: ""
                    if (otherRollNo.isBlank()) otherRollNo = otherDoc.getString("rollNo") ?: ""

                    val tests = otherDoc.get("testResults") as? List<*> ?: emptyList<Any>()
                    tests.forEach { t ->
                        (t as? Map<*, *>)?.let { map ->
                            val cleanMap = mutableMapOf<String, Any>()
                            map.forEach { (k, v) ->
                                if (k is String && v != null) cleanMap[k] = v
                            }
                            if (cleanMap.isNotEmpty()) {
                                allOtherTestResults.add(cleanMap)
                            }
                        }
                    }

                    // Copy subcollection quizResults from other UID to current UID
                    quizResultsCollection(otherDoc.id).get().addOnSuccessListener { otherQuizzes ->
                        otherQuizzes.documents.forEach { qDoc ->
                            val qData = qDoc.data
                            if (qData != null) {
                                quizResultsCollection(currentUid).document(qDoc.id)
                                    .set(qData, com.google.firebase.firestore.SetOptions.merge())
                            }
                        }
                    }

                    // Copy wordProgress
                    wordProgressCollection(otherDoc.id).get().addOnSuccessListener { otherWords ->
                        otherWords.documents.forEach { wDoc ->
                            val wData = wDoc.data
                            if (wData != null) {
                                wordProgressCollection(currentUid).document(wDoc.id)
                                    .set(wData, com.google.firebase.firestore.SetOptions.merge())
                            }
                        }
                    }
                }

                val currentDocRef = db.collection("users").document(currentUid)
                val updates = mutableMapOf<String, Any>()
                if (allOtherTestResults.isNotEmpty()) {
                    updates["testResults"] = FieldValue.arrayUnion(*allOtherTestResults.toTypedArray())
                }
                if (otherDept.isNotBlank()) {
                    updates["department"] = otherDept
                }
                if (otherRollNo.isNotBlank()) {
                    updates["rollNo"] = otherRollNo
                }
                if (otherName.isNotBlank()) {
                    // Only fill name if current is blank
                    currentDocRef.get().addOnSuccessListener { curDoc ->
                        val curName = curDoc.getString("name") ?: ""
                        if (curName.isBlank()) {
                            updates["name"] = otherName
                        }
                        if (updates.isNotEmpty()) {
                            currentDocRef.set(updates, com.google.firebase.firestore.SetOptions.merge())
                                .addOnCompleteListener { onComplete() }
                        } else {
                            onComplete()
                        }
                    }.addOnFailureListener {
                        if (updates.isNotEmpty()) {
                            currentDocRef.set(updates, com.google.firebase.firestore.SetOptions.merge())
                                .addOnCompleteListener { onComplete() }
                        } else {
                            onComplete()
                        }
                    }
                } else if (updates.isNotEmpty()) {
                    currentDocRef.set(updates, com.google.firebase.firestore.SetOptions.merge())
                        .addOnCompleteListener { onComplete() }
                } else {
                    onComplete()
                }
            }
            .addOnFailureListener {
                onComplete()
            }
    }
    // ─── Quiz Results (Detailed) ───────────────────────────────────

    /** Records a quiz result with detailed per-question answers. */
    fun recordQuizResult(
        categoryId: String,
        categoryTitle: String,
        score: Int,
        total: Int,
        answers: List<QuizAnswerDetail> = emptyList()
    ) {
        android.util.Log.d("Dashboard", "recordQuizResult called: $categoryId score=$score/$total uid=${auth.currentUser?.uid}")
        withUid { uid ->
            val answerMaps = answers.map { answer ->
                mapOf(
                    "word" to answer.word,
                    "correctAnswer" to answer.correctAnswer,
                    "userAnswer" to answer.userAnswer,
                    "isCorrect" to answer.isCorrect
                )
            }
            val entry = mapOf(
                "categoryId" to categoryId,
                "categoryTitle" to categoryTitle,
                "score" to score,
                "total" to total,
                "timestamp" to System.currentTimeMillis(),
                "answers" to answerMaps
            )
            android.util.Log.d("Dashboard", "Saving quiz result to Firestore under uid=$uid")
            quizResultsCollection(uid).add(entry)
                .addOnSuccessListener { 
                    android.util.Log.d("Dashboard", "✅ Quiz result saved successfully!") 
                }
                .addOnFailureListener { e -> android.util.Log.e("Dashboard", "❌ Failed to save quiz result: ${e.message}") }

            // Also append a summarized version to the user's document for Admin Panel
            val simplifiedResult = mapOf(
                "categoryId" to categoryId,
                "categoryTitle" to categoryTitle,
                "score" to score,
                "total" to total,
                "timestamp" to System.currentTimeMillis()
            )
            val userUpdates = mutableMapOf<String, Any>(
                "uid" to uid,
                "testResults" to FieldValue.arrayUnion(simplifiedResult),
                "lastActive" to System.currentTimeMillis()
            )
            val currentUser = auth.currentUser
            currentUser?.email?.takeIf { it.isNotBlank() }?.let { email ->
                userUpdates["email"] = email
                userUpdates["rollNo"] = email.substringBefore("@")
            }
            currentUser?.displayName?.takeIf { it.isNotBlank() }?.let { name ->
                userUpdates["name"] = name
            }
            db.collection("users").document(uid)
                .set(userUpdates, com.google.firebase.firestore.SetOptions.merge())
                .addOnFailureListener { e -> android.util.Log.e("Dashboard", "❌ Failed to update testResults in user doc: ${e.message}") }
        }
    }

    /** Loads all detailed quiz results for the current user. */
    fun loadDetailedQuizResults(onResult: (List<DetailedQuizResult>) -> Unit) {
        val uid = currentUid() ?: run {
            onResult(emptyList())
            return
        }
        getAllLinkedUids { uids ->
            val allResults = mutableListOf<DetailedQuizResult>()
            var pendingQueries = uids.size
            if (pendingQueries == 0) {
                onResult(emptyList())
                return@getAllLinkedUids
            }
            for (u in uids) {
                quizResultsCollection(u).get()
                    .addOnSuccessListener { snapshot ->
                        val results = snapshot.documents.mapNotNull { doc ->
                            val categoryTitle = doc.getString("categoryTitle") ?: return@mapNotNull null
                            val score = (doc.getLong("score") ?: 0L).toInt()
                            val total = (doc.getLong("total") ?: 0L).toInt()
                            val timestamp = doc.getLong("timestamp") ?: 0L
                            val answersRaw = doc.get("answers") as? List<*> ?: emptyList<Any>()
                            val answers = answersRaw.mapNotNull { raw ->
                                val map = raw as? Map<*, *> ?: return@mapNotNull null
                                QuizAnswerDetail(
                                    word = map["word"] as? String ?: "",
                                    correctAnswer = map["correctAnswer"] as? String ?: "",
                                    userAnswer = map["userAnswer"] as? String ?: "",
                                    isCorrect = map["isCorrect"] as? Boolean ?: false
                                )
                            }
                            DetailedQuizResult(
                                categoryTitle = categoryTitle,
                                score = score,
                                total = total,
                                timestamp = timestamp,
                                answers = answers
                            )
                        }
                        allResults.addAll(results)
                        pendingQueries--
                        if (pendingQueries <= 0) {
                            val sorted = allResults.sortedByDescending { it.timestamp }
                            // Backfill sync: ensure all old quizzes are available for the Admin Panel
                            if (sorted.isNotEmpty()) {
                                val simplifiedResults = sorted.map {
                                    mapOf(
                                        "categoryTitle" to it.categoryTitle,
                                        "score" to it.score,
                                        "total" to it.total,
                                        "timestamp" to it.timestamp
                                    )
                                }
                                db.collection("users").document(uid)
                                    .set(mapOf("testResults" to simplifiedResults), com.google.firebase.firestore.SetOptions.merge())
                            }
                            onResult(sorted)
                        }
                    }
                    .addOnFailureListener {
                        pendingQueries--
                        if (pendingQueries <= 0) {
                            val sorted = allResults.sortedByDescending { it.timestamp }
                            onResult(sorted)
                        }
                    }
            }
        }
    }

    // ─── Restricted Accounts for Pilot Test ─────────────────────────
    val RESTRICTED_PILOT_TEST_EMAILS = setOf(
        "24130021@srcas.ac.in",
        "24130036@srcas.ac.in",
        "24130049@srcas.ac.in"
    )

    val RESTRICTED_PILOT_TEST_ROLL_NOS = setOf(
        "24130021",
        "24130036",
        "24130049"
    )

    /**
     * Checks if the current user is restricted to 2 attempts for the Pilot Test,
     * and returns the number of attempts taken and past scores.
     */
    fun checkPilotTestAccess(
        onResult: (isRestricted: Boolean, attemptsTaken: Int, highestScore: Int, pastScores: List<Int>) -> Unit
    ) {
        val uid = currentUid()
        if (uid == null) {
            onResult(false, 0, 0, emptyList())
            return
        }

        loadUserProfile { profile ->
            val userEmail = (profile.email.ifBlank { auth.currentUser?.email ?: "" }).lowercase().trim()
            val userRollNo = profile.rollNo.trim()

            val isRestricted = RESTRICTED_PILOT_TEST_EMAILS.contains(userEmail) ||
                    RESTRICTED_PILOT_TEST_ROLL_NOS.contains(userRollNo)

            getAllLinkedUids { uids ->
                val allPilotDocs = mutableListOf<com.google.firebase.firestore.DocumentSnapshot>()
                var pendingQueries = uids.size
                if (pendingQueries == 0) {
                    onResult(isRestricted, 0, 0, emptyList())
                    return@getAllLinkedUids
                }
                for (u in uids) {
                    quizResultsCollection(u).get()
                        .addOnSuccessListener { quizSnapshot ->
                            val pilotDocs = quizSnapshot.documents.filter { doc ->
                                val catId = doc.getString("categoryId") ?: ""
                                val catTitle = doc.getString("categoryTitle") ?: ""
                                catId == "pilotTest" || catTitle.equals("Pilot Test", ignoreCase = true)
                            }
                            allPilotDocs.addAll(pilotDocs)
                            pendingQueries--
                            if (pendingQueries <= 0) {
                                val pastScores = allPilotDocs.map { (it.getLong("score") ?: 0L).toInt() }
                                val attemptsTaken = pastScores.size
                                val highestScore = pastScores.maxOrNull() ?: 0
                                onResult(isRestricted, attemptsTaken, highestScore, pastScores)
                            }
                        }
                        .addOnFailureListener {
                            pendingQueries--
                            if (pendingQueries <= 0) {
                                val pastScores = allPilotDocs.map { (it.getLong("score") ?: 0L).toInt() }
                                val attemptsTaken = pastScores.size
                                val highestScore = pastScores.maxOrNull() ?: 0
                                onResult(isRestricted, attemptsTaken, highestScore, pastScores)
                            }
                        }
                }
            }
        }
    }

    // ─── Admin Panel ───────────────────────────────────────────────

    /**
     * Real-time listener for student reports in a department.
     * Whenever any student completes a quiz, submits a pilot test, or registers,
     * this listener triggers immediately without requiring manual refresh.
     */
    fun observeDepartmentStudentReports(
        department: String,
        onResult: (List<StudentReport>) -> Unit
    ): com.google.firebase.firestore.ListenerRegistration {
        val targetDept = formatDepartmentName(department)
        
        return db.collection("users").addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) {
                android.util.Log.e("AdminPanel", "Error listening to users collection: ${error?.message}")
                return@addSnapshotListener
            }
            
            val allUserDocs = snapshot.documents
            val rawReports = mutableListOf<StudentReport>()
            val docsNeedingSubcollectionFetch = mutableListOf<com.google.firebase.firestore.DocumentSnapshot>()
            
            for (userDoc in allUserDocs) {
                val role = userDoc.getString("role")?.lowercase() ?: ""
                if (role == "teacher" || role == "admin") continue
                
                val email = userDoc.getString("email") ?: ""
                val emailPrefix = email.substringBefore("@").takeIf { it.isNotBlank() } ?: "Unknown"
                val rawName = userDoc.getString("name")?.takeIf { it.isNotBlank() } ?: emailPrefix
                val rawRollNo = userDoc.getString("rollNo")?.takeIf { it.isNotBlank() } ?: emailPrefix
                val rawDept = userDoc.getString("department") ?: ""
                
                val name = if (rawName.equals("Unknown", ignoreCase = true)) "Student" else rawName
                val rollNo = if (rawRollNo.equals("Unknown", ignoreCase = true)) emailPrefix else rawRollNo
                val dept = formatDepartmentName(rawDept).ifBlank { "Unassigned" }
                
                val rawTests = try {
                    userDoc.get("testResults") as? List<*> ?: emptyList<Any>()
                } catch (e: Exception) {
                    emptyList<Any>()
                }
                
                val testResultsFromDoc = rawTests.mapNotNull { item ->
                    val map = item as? Map<*, *> ?: return@mapNotNull null
                    val catTitle = map["categoryTitle"] as? String ?: ""
                    if (catTitle.isBlank()) return@mapNotNull null
                    StudentTestResult(
                        categoryTitle = catTitle,
                        score = (map["score"] as? Number)?.toInt() ?: 0,
                        total = (map["total"] as? Number)?.toInt() ?: 0,
                        timestamp = (map["timestamp"] as? Number)?.toLong() ?: 0L
                    )
                }

                // Take ONLY the highest score attempt for each test category
                val deduplicatedTests = testResultsFromDoc
                    .groupBy { it.categoryTitle }
                    .mapValues { (_, results) -> results.maxByOrNull { it.score } ?: results.first() }
                    .values
                    .toList()
                
                rawReports.add(
                    StudentReport(
                        name = formatDisplayName(name),
                        rollNo = rollNo,
                        department = dept,
                        totalScore = deduplicatedTests.sumOf { it.score },
                        totalQuestions = deduplicatedTests.sumOf { it.total },
                        testResults = deduplicatedTests
                    )
                )
                
                if (testResultsFromDoc.isEmpty()) {
                    docsNeedingSubcollectionFetch.add(userDoc)
                }
            }
            
            fun processAndEmit(reportsList: List<StudentReport>) {
                val merged = mergeStudentReports(reportsList)
                val filtered = merged.filter { student ->
                    if (targetDept == "All Departments" || targetDept.isBlank()) {
                        true
                    } else {
                        student.department.equals(targetDept, ignoreCase = true)
                    }
                }.sortedWith(compareBy({ it.name.lowercase() }, { it.rollNo }))
                
                onResult(filtered)
            }
            
            processAndEmit(rawReports)
            
            if (docsNeedingSubcollectionFetch.isNotEmpty()) {
                var pending = docsNeedingSubcollectionFetch.size
                val extraReports = mutableListOf<StudentReport>()
                
                for (doc in docsNeedingSubcollectionFetch) {
                    val uid = doc.getString("uid") ?: doc.id
                    val email = doc.getString("email") ?: ""
                    val emailPrefix = email.substringBefore("@").takeIf { it.isNotBlank() } ?: "Unknown"
                    val rawName = doc.getString("name")?.takeIf { it.isNotBlank() } ?: emailPrefix
                    val rawRollNo = doc.getString("rollNo")?.takeIf { it.isNotBlank() } ?: emailPrefix
                    val rawDept = doc.getString("department") ?: ""
                    val name = if (rawName.equals("Unknown", ignoreCase = true)) "Student" else rawName
                    val rollNo = if (rawRollNo.equals("Unknown", ignoreCase = true)) emailPrefix else rawRollNo
                    val dept = formatDepartmentName(rawDept).ifBlank { "Unassigned" }
                    
                    quizResultsCollection(uid).get().addOnSuccessListener { qDocs ->
                        if (!qDocs.isEmpty) {
                            val subResults = qDocs.documents.mapNotNull { qDoc ->
                                val catTitle = qDoc.getString("categoryTitle") ?: ""
                                if (catTitle.isBlank()) return@mapNotNull null
                                StudentTestResult(
                                    categoryTitle = catTitle,
                                    score = (qDoc.getLong("score") ?: 0L).toInt(),
                                    total = (qDoc.getLong("total") ?: 0L).toInt(),
                                    timestamp = qDoc.getLong("timestamp") ?: 0L
                                )
                            }
                            if (subResults.isNotEmpty()) {
                                extraReports.add(
                                    StudentReport(
                                        name = formatDisplayName(name),
                                        rollNo = rollNo,
                                        department = dept,
                                        totalScore = subResults.sumOf { it.score },
                                        totalQuestions = subResults.sumOf { it.total },
                                        testResults = subResults
                                    )
                                )
                                val simplified = subResults.map {
                                    mapOf(
                                        "categoryTitle" to it.categoryTitle,
                                        "score" to it.score,
                                        "total" to it.total,
                                        "timestamp" to it.timestamp
                                    )
                                }
                                db.collection("users").document(uid)
                                    .set(mapOf("testResults" to simplified), com.google.firebase.firestore.SetOptions.merge())
                            }
                        }
                    }.addOnCompleteListener {
                        pending--
                        if (pending <= 0 && extraReports.isNotEmpty()) {
                            processAndEmit(rawReports + extraReports)
                        }
                    }
                }
            }
        }
    }

    /** Fetches all students in a given department and their quiz results (highest score per test). */
    fun getDepartmentStudentReports(
        department: String,
        onResult: (List<StudentReport>) -> Unit
    ) {
        val targetDept = formatDepartmentName(department)
        db.collection("users")
            .get()
            .addOnSuccessListener { allDocs ->
                val rawReports = mutableListOf<StudentReport>()
                val userDocs = allDocs.filter { doc ->
                    val role = doc.getString("role")?.lowercase() ?: ""
                    role != "teacher" && role != "admin"
                }
                if (userDocs.isEmpty()) {
                    onResult(emptyList())
                    return@addOnSuccessListener
                }
                
                var remaining = userDocs.size
                
                for (userDoc in userDocs) {
                    val uid = userDoc.getString("uid") ?: userDoc.id
                    val email = userDoc.getString("email") ?: ""
                    val emailPrefix = email.substringBefore("@").takeIf { it.isNotBlank() } ?: "Unknown"
                    val rawName = userDoc.getString("name")?.takeIf { it.isNotBlank() } ?: emailPrefix
                    val rawRollNo = userDoc.getString("rollNo")?.takeIf { it.isNotBlank() } ?: emailPrefix
                    val rawDept = userDoc.getString("department") ?: ""
                    
                    val name = if (rawName.equals("Unknown", ignoreCase = true)) "Student" else rawName
                    val rollNo = if (rawRollNo.equals("Unknown", ignoreCase = true)) emailPrefix else rawRollNo
                    val dept = formatDepartmentName(rawDept).ifBlank { "Unassigned" }
                    
                    val fromUserDoc = try {
                        val rawTests = userDoc.get("testResults") as? List<*> ?: emptyList<Any>()
                        rawTests.mapNotNull { item ->
                            val map = item as? Map<*, *> ?: return@mapNotNull null
                            val catTitle = map["categoryTitle"] as? String ?: ""
                            if (catTitle.isBlank()) return@mapNotNull null
                            StudentTestResult(
                                categoryTitle = catTitle,
                                score = (map["score"] as? Number)?.toInt() ?: 0,
                                total = (map["total"] as? Number)?.toInt() ?: 0,
                                timestamp = (map["timestamp"] as? Number)?.toLong() ?: 0L
                            )
                        }
                    } catch (e: Exception) {
                        emptyList()
                    }

                    quizResultsCollection(uid).get()
                        .addOnSuccessListener { quizDocs ->
                            try {
                                val allResults = mutableListOf<StudentTestResult>()
                                allResults.addAll(fromUserDoc)
                                quizDocs.forEach { doc ->
                                    val score = (doc.getLong("score") ?: 0L).toInt()
                                    val total = (doc.getLong("total") ?: 0L).toInt()
                                    val catTitle = doc.getString("categoryTitle") ?: ""
                                    val ts = doc.getLong("timestamp") ?: 0L
                                    if (catTitle.isNotBlank()) {
                                        allResults.add(
                                            StudentTestResult(
                                                categoryTitle = catTitle,
                                                score = score,
                                                total = total,
                                                timestamp = ts
                                            )
                                        )
                                    }
                                }
                                
                                val testResults = allResults
                                    .groupBy { it.categoryTitle }
                                    .mapValues { (_, results) -> results.maxByOrNull { it.score } ?: results.first() }
                                    .values
                                    .toList()
                                
                                val totalScore = testResults.sumOf { it.score }
                                val totalQuestions = testResults.sumOf { it.total }
                                
                                rawReports.add(
                                    StudentReport(
                                        name = formatDisplayName(name),
                                        rollNo = rollNo,
                                        department = dept,
                                        totalScore = totalScore,
                                        totalQuestions = totalQuestions,
                                        testResults = testResults
                                    )
                                )
                            } catch (e: Exception) {
                                android.util.Log.e("AdminPanel", "Error parsing quiz docs: ${e.message}")
                            } finally {
                                remaining--
                                if (remaining <= 0) {
                                    val merged = mergeStudentReports(rawReports)
                                    val filtered = merged.filter { student ->
                                        if (targetDept == "All Departments" || targetDept.isBlank()) {
                                            true
                                        } else {
                                            student.department.equals(targetDept, ignoreCase = true)
                                        }
                                    }.sortedWith(compareBy({ it.name.lowercase() }, { it.rollNo }))
                                    onResult(filtered)
                                }
                            }
                        }
                        .addOnFailureListener {
                            try {
                                val testResults = fromUserDoc
                                    .groupBy { it.categoryTitle }
                                    .mapValues { (_, results) -> results.maxByOrNull { it.score } ?: results.first() }
                                    .values
                                    .toList()

                                val totalScore = testResults.sumOf { it.score }
                                val totalQuestions = testResults.sumOf { it.total }

                                rawReports.add(
                                    StudentReport(
                                        name = formatDisplayName(name),
                                        rollNo = rollNo,
                                        department = dept,
                                        totalScore = totalScore,
                                        totalQuestions = totalQuestions,
                                        testResults = testResults
                                    )
                                )
                            } catch (e: Exception) {
                                android.util.Log.e("AdminPanel", "Error parsing user doc: ${e.message}")
                            } finally {
                                remaining--
                                if (remaining <= 0) {
                                    val merged = mergeStudentReports(rawReports)
                                    val filtered = merged.filter { student ->
                                        if (targetDept == "All Departments" || targetDept.isBlank()) {
                                            true
                                        } else {
                                            student.department.equals(targetDept, ignoreCase = true)
                                        }
                                    }.sortedWith(compareBy({ it.name.lowercase() }, { it.rollNo }))
                                    onResult(filtered)
                                }
                            }
                        }
                }
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    /**
     * Merges student reports for the same student (e.g., handles "Harish V" and "HARISH V"
     * or matching roll numbers / emails seamlessly as the same student).
     */
    fun mergeStudentReports(rawReports: List<StudentReport>): List<StudentReport> {
        val grouped = rawReports.groupBy { report ->
            val cleanRoll = report.rollNo.trim().uppercase()
            val cleanName = report.name.trim().uppercase()
            when {
                cleanRoll.isNotBlank() && cleanRoll != "UNKNOWN" && !cleanRoll.startsWith("STUDENT") -> cleanRoll
                cleanName.isNotBlank() && cleanName != "STUDENT" && cleanName != "UNKNOWN" -> cleanName
                else -> report.name.trim().uppercase()
            }
        }

        return grouped.values.map { list ->
            // Find best human-readable name (not just roll numbers/default student)
            val bestName = list.map { it.name }
                .firstOrNull { it.isNotBlank() && !it.equals("Student", ignoreCase = true) && !it.equals("Unknown", ignoreCase = true) && !it.all { ch -> ch.isDigit() } }
                ?: list.first().name

            // Find best rollNo
            val bestRollNo = list.map { it.rollNo }
                .firstOrNull { it.isNotBlank() && !it.equals("Unknown", ignoreCase = true) }
                ?: list.first().rollNo

            // Find best department
            val bestDept = list.map { it.department }
                .firstOrNull { it.isNotBlank() && !it.equals("Unassigned", ignoreCase = true) }
                ?: list.first().department

            // Merge all test results across all attempts and duplicate accounts, taking ONLY the highest score per test category
            val allTests = list.flatMap { it.testResults }
            val mergedTests = allTests
                .groupBy { it.categoryTitle }
                .mapValues { (_, results) -> results.maxByOrNull { it.score } ?: results.first() }
                .values
                .toList()

            val totalScore = mergedTests.sumOf { it.score }
            val totalQuestions = mergedTests.sumOf { it.total }

            StudentReport(
                name = formatDisplayName(bestName),
                rollNo = bestRollNo,
                department = bestDept,
                totalScore = totalScore,
                totalQuestions = totalQuestions,
                testResults = mergedTests
            )
        }.sortedWith(compareBy({ it.name.lowercase() }, { it.rollNo }))
    }

    /**
     * Clears all test results and quiz history for the current user.
     */
    fun clearCurrentUserTestHistory(onComplete: (Boolean) -> Unit = {}) {
        val uid = currentUid() ?: run {
            onComplete(false)
            return
        }
        getAllLinkedUids { uids ->
            var pending = uids.size
            if (pending == 0) {
                onComplete(true)
                return@getAllLinkedUids
            }
            var overallSuccess = true
            for (u in uids) {
                db.collection("users").document(u).update("testResults", emptyList<Any>())
                    .addOnCompleteListener {
                        quizResultsCollection(u).get()
                            .addOnSuccessListener { qDocs ->
                                if (qDocs.isEmpty) {
                                    pending--
                                    if (pending <= 0) onComplete(overallSuccess)
                                    return@addOnSuccessListener
                                }
                                val batch = db.batch()
                                qDocs.documents.forEach { qDoc ->
                                    batch.delete(qDoc.reference)
                                }
                                batch.commit().addOnCompleteListener {
                                    pending--
                                    if (pending <= 0) onComplete(overallSuccess)
                                }
                            }
                            .addOnFailureListener {
                                overallSuccess = false
                                pending--
                                if (pending <= 0) onComplete(overallSuccess)
                            }
                    }
            }
        }
    }

    /**
     * Clears all test results and quiz history from Firestore for students.
     * Useful for teachers/admins to start fresh after pilot testing.
     */
    fun clearAllTestResults(department: String = "", onComplete: (Boolean) -> Unit) {
        val targetDept = formatDepartmentName(department)
        db.collection("users").get().addOnSuccessListener { snapshot ->
            val matchingDocs = snapshot.documents.filter { doc ->
                if (targetDept == "All Departments" || targetDept.isBlank()) {
                    true
                } else {
                    val docDept = formatDepartmentName(doc.getString("department") ?: "")
                    docDept.equals(targetDept, ignoreCase = true)
                }
            }

            if (matchingDocs.isEmpty()) {
                onComplete(true)
                return@addOnSuccessListener
            }

            var pendingDocs = matchingDocs.size
            for (doc in matchingDocs) {
                val uid = doc.id
                // Clear testResults on user document
                db.collection("users").document(uid).update("testResults", emptyList<Any>())
                    .addOnCompleteListener {
                        // Delete all subcollection docs in quizResults
                        quizResultsCollection(uid).get().addOnSuccessListener { qDocs ->
                            if (qDocs.isEmpty) {
                                pendingDocs--
                                if (pendingDocs <= 0) {
                                    onComplete(true)
                                }
                                return@addOnSuccessListener
                            }
                            val batch = db.batch()
                            qDocs.documents.forEach { qDoc ->
                                batch.delete(qDoc.reference)
                            }
                            batch.commit().addOnCompleteListener {
                                pendingDocs--
                                if (pendingDocs <= 0) {
                                    onComplete(true)
                                }
                            }
                        }.addOnFailureListener {
                            pendingDocs--
                            if (pendingDocs <= 0) {
                                onComplete(true)
                            }
                        }
                    }
            }
        }.addOnFailureListener {
            onComplete(false)
        }
    }

    /**
     * Completely clears a student's test history:
     * 1. Deletes all documents in subcollection `users/{uid}/quizResults`
     * 2. Deletes the `testResults` array in the main user document `users/{uid}`
     */
    fun clearStudentTestHistory(uid: String, onResult: (Boolean, String?) -> Unit) {
        if (uid.isBlank()) {
            onResult(false, "Invalid student UID")
            return
        }
        val userDocRef = db.collection("users").document(uid)
        val quizColRef = userDocRef.collection("quizResults")

        quizColRef.get().addOnSuccessListener { snapshot ->
            val batch = db.batch()
            for (doc in snapshot.documents) {
                batch.delete(doc.reference)
            }
            // Clear testResults field on user doc
            batch.update(userDocRef, "testResults", FieldValue.delete())

            batch.commit()
                .addOnSuccessListener {
                    android.util.Log.d("AdminPanel", "✅ Successfully cleared test history for uid=$uid")
                    onResult(true, null)
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("AdminPanel", "❌ Failed to commit batch clear for uid=$uid: ${e.message}")
                    // Fallback: try setting empty array
                    userDocRef.set(mapOf("testResults" to emptyList<Any>()), com.google.firebase.firestore.SetOptions.merge())
                        .addOnSuccessListener { onResult(true, null) }
                        .addOnFailureListener { err -> onResult(false, err.message) }
                }
        }.addOnFailureListener { e ->
            // If fetching subcollection fails, at least clear the user doc field
            userDocRef.update("testResults", FieldValue.delete())
                .addOnSuccessListener { onResult(true, null) }
                .addOnFailureListener { onResult(false, e.message) }
        }
    }

    /**
     * Completely clears test history for all students in a given department.
     */
    fun clearDepartmentTestHistory(department: String, onResult: (Boolean, String?) -> Unit) {
        if (department.isBlank()) {
            onResult(false, "Invalid department")
            return
        }
        db.collection("users")
            .whereEqualTo("department", department)
            .get()
            .addOnSuccessListener { allDocs ->
                val studentDocs = allDocs.filter { it.getString("role") == "student" }
                if (studentDocs.isEmpty()) {
                    onResult(true, null)
                    return@addOnSuccessListener
                }

                var remaining = studentDocs.size
                var hasError = false
                var errorMsg: String? = null

                for (doc in studentDocs) {
                    val uid = doc.getString("uid") ?: doc.id
                    clearStudentTestHistory(uid) { success, msg ->
                        if (!success) {
                            hasError = true
                            errorMsg = msg
                        }
                        remaining--
                        if (remaining <= 0) {
                            if (hasError) {
                                onResult(false, errorMsg)
                            } else {
                                onResult(true, null)
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    /**
     * Deletes a single test result attempt for a student.
     */
    fun deleteSingleTestResult(
        uid: String,
        testDocId: String,
        timestamp: Long,
        onResult: (Boolean, String?) -> Unit
    ) {
        if (uid.isBlank()) {
            onResult(false, "Invalid student UID")
            return
        }
        val userDocRef = db.collection("users").document(uid)

        // Remove from subcollection if ID available
        if (testDocId.isNotBlank()) {
            userDocRef.collection("quizResults").document(testDocId).delete()
        }

        // Also remove matching element from userDoc.testResults
        userDocRef.get().addOnSuccessListener { userDoc ->
            val rawTests = userDoc.get("testResults") as? List<*> ?: emptyList<Any>()
            val updatedTests = rawTests.filterNot { item ->
                val map = item as? Map<*, *> ?: return@filterNot false
                val ts = (map["timestamp"] as? Number)?.toLong() ?: 0L
                ts == timestamp
            }
            userDocRef.update("testResults", updatedTests)
                .addOnSuccessListener {
                    onResult(true, null)
                }
                .addOnFailureListener {
                    onResult(true, null)
                }
        }.addOnFailureListener {
            onResult(true, null)
        }
    }

    /** Fetches top students by streak in a given department. */
    fun getTopStudentsByStreak(
        department: String,
        limit: Int,
        onResult: (List<UserProfile>) -> Unit
    ) {
        val targetDept = formatDepartmentName(department)
        db.collection("users")
            .get()
            .addOnSuccessListener { allDocs ->
                val students = allDocs.toList()
                    .filter { doc ->
                        val role = doc.getString("role")?.lowercase() ?: ""
                        if (role == "teacher" || role == "admin") return@filter false
                        val docDept = formatDepartmentName(doc.getString("department") ?: "")
                        if (targetDept == "All Departments" || targetDept.isBlank()) {
                            true
                        } else {
                            docDept.equals(targetDept, ignoreCase = true)
                        }
                    }
                    .map { doc ->
                        UserProfile(
                            name = formatDisplayName(doc.getString("name") ?: ""),
                            rollNo = doc.getString("rollNo") ?: "",
                            department = formatDepartmentName(doc.getString("department") ?: ""),
                            role = doc.getString("role") ?: "student",
                            email = doc.getString("email") ?: "",
                            currentStreak = (doc.getLong("currentStreak") ?: 0L).toInt()
                        )
                    }
                    .groupBy { profile ->
                        val cleanRoll = profile.rollNo.trim().uppercase()
                        val cleanName = profile.name.trim().uppercase()
                        when {
                            cleanRoll.isNotBlank() && cleanRoll != "UNKNOWN" -> cleanRoll
                            cleanName.isNotBlank() -> cleanName
                            else -> profile.email.trim().lowercase()
                        }
                    }
                    .values
                    .map { group ->
                        group.maxByOrNull { it.currentStreak } ?: group.first()
                    }
                    .sortedByDescending { it.currentStreak }
                    .take(limit)
                onResult(students)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    /** Fetches all unique departments from the users collection and standard predefined list. */
    fun loadAllDepartments(onResult: (List<String>) -> Unit) {
        val standardDepartments = listOf(
            "Computer Science",
            "Computer Applications",
            "English",
            "Commerce",
            "Business Administration",
            "Mathematics",
            "Physics",
            "Chemistry",
            "Botany",
            "Zoology",
            "Visual Communication",
            "Tamil",
            "Economics",
            "History"
        )
        db.collection("users")
            .get()
            .addOnSuccessListener { docs ->
                val foundDepts = docs.mapNotNull { it.getString("department") }
                    .map { formatDepartmentName(it) }
                    .filter {
                        it.isNotBlank() &&
                        !it.equals("Faculty", ignoreCase = true) &&
                        !it.equals("Teacher", ignoreCase = true) &&
                        !it.equals("Unassigned", ignoreCase = true)
                    }

                val allDepts = (standardDepartments + foundDepts)
                    .map { formatDepartmentName(it) }
                    .distinctBy { it.lowercase() }
                    .sorted()
                
                val resultList = mutableListOf("All Departments")
                resultList.addAll(allDepts)
                onResult(resultList)
            }
            .addOnFailureListener {
                val resultList = mutableListOf("All Departments")
                resultList.addAll(standardDepartments.map { formatDepartmentName(it) }.sorted())
                onResult(resultList)
            }
    }

    // ─── Dashboard Stats ───────────────────────────────────────────

    /** Aggregate stats used by the Dashboard screen. */
    fun loadDashboardStats(onResult: (DashboardStats) -> Unit) {
        val uid = currentUid() ?: run {
            onResult(DashboardStats())
            return
        }

        getAllLinkedUids { uids ->
            var favoriteCount = 0
            var bookmarkedCount = 0
            var wordsRated = 0
            var quizzesTaken = 0
            var totalScore = 0
            var totalQuestions = 0
            
            var pendingWords = uids.size
            var pendingQuizzes = uids.size
            
            val checkComplete = {
                if (pendingWords == 0 && pendingQuizzes == 0) {
                    val accuracy = if (totalQuestions > 0) totalScore.toFloat() / totalQuestions else 0f
                    onResult(
                        DashboardStats(
                            favoriteCount = favoriteCount,
                            bookmarkedCount = bookmarkedCount,
                            wordsRated = wordsRated,
                            quizzesTaken = quizzesTaken,
                            quizAccuracy = accuracy
                        )
                    )
                }
            }
            
            if (uids.isEmpty()) {
                onResult(DashboardStats())
                return@getAllLinkedUids
            }
            
            for (u in uids) {
                wordProgressCollection(u).get().addOnSuccessListener { progressDocs ->
                    favoriteCount += progressDocs.count { it.getBoolean("favorite") == true }
                    bookmarkedCount += progressDocs.count { it.getBoolean("bookmarked") == true }
                    wordsRated += progressDocs.count { (it.getLong("difficulty") ?: 0L) > 0 }
                    pendingWords--
                    checkComplete()
                }.addOnFailureListener {
                    pendingWords--
                    checkComplete()
                }
                
                quizResultsCollection(u).get().addOnSuccessListener { quizDocs ->
                    quizzesTaken += quizDocs.size()
                    quizDocs.forEach { doc ->
                        totalScore += (doc.getLong("score") ?: 0L).toInt()
                        totalQuestions += (doc.getLong("total") ?: 0L).toInt()
                    }
                    pendingQuizzes--
                    checkComplete()
                }.addOnFailureListener {
                    pendingQuizzes--
                    checkComplete()
                }
            }
        }
    }

    open class DashboardListener(
        val wordRegs: List<com.google.firebase.firestore.ListenerRegistration>,
        val quizRegs: List<com.google.firebase.firestore.ListenerRegistration>
    ) {
        open fun remove() {
            wordRegs.forEach { it.remove() }
            quizRegs.forEach { it.remove() }
        }
    }

    /** Real-time aggregate stats used by the Dashboard screen. Works for guests too. */
    fun observeDashboardStats(onResult: (DashboardStats) -> Unit): DashboardListener {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onResult(DashboardStats())
            return DashboardListener(emptyList(), emptyList())
        }
        
        val wordRegs = mutableListOf<com.google.firebase.firestore.ListenerRegistration>()
        val quizRegs = mutableListOf<com.google.firebase.firestore.ListenerRegistration>()
        
        getAllLinkedUids { uids ->
            val favsMap = mutableMapOf<String, Int>()
            val booksMap = mutableMapOf<String, Int>()
            val ratedMap = mutableMapOf<String, Int>()
            val quizCountMap = mutableMapOf<String, Int>()
            val quizScoreMap = mutableMapOf<String, Int>()
            val quizTotalMap = mutableMapOf<String, Int>()
            
            fun emit() {
                val favoriteCount = favsMap.values.sum()
                val bookmarkedCount = booksMap.values.sum()
                val wordsRated = ratedMap.values.sum()
                val quizzesTaken = quizCountMap.values.sum()
                val totalScore = quizScoreMap.values.sum()
                val totalQuestions = quizTotalMap.values.sum()
                val accuracy = if (totalQuestions > 0) totalScore.toFloat() / totalQuestions else 0f
                onResult(
                    DashboardStats(
                        favoriteCount = favoriteCount,
                        bookmarkedCount = bookmarkedCount,
                        wordsRated = wordsRated,
                        quizzesTaken = quizzesTaken,
                        quizAccuracy = accuracy
                    )
                )
            }
            
            for (u in uids) {
                val wReg = wordProgressCollection(u).addSnapshotListener { snapshot, error ->
                    if (snapshot != null) {
                        favsMap[u] = snapshot.count { it.getBoolean("favorite") == true }
                        booksMap[u] = snapshot.count { it.getBoolean("bookmarked") == true }
                        ratedMap[u] = snapshot.count { (it.getLong("difficulty") ?: 0L) > 0 }
                        emit()
                    }
                }
                wordRegs.add(wReg)
                
                val qReg = quizResultsCollection(u).addSnapshotListener { snapshot, error ->
                    if (snapshot != null) {
                        quizCountMap[u] = snapshot.size()
                        var sc = 0
                        var qn = 0
                        snapshot.forEach { doc ->
                            sc += (doc.getLong("score") ?: 0L).toInt()
                            qn += (doc.getLong("total") ?: 0L).toInt()
                        }
                        quizScoreMap[u] = sc
                        quizTotalMap[u] = qn
                        emit()
                    }
                }
                quizRegs.add(qReg)
            }
        }
        
        return DashboardListener(wordRegs, quizRegs)
    }

    private fun getAllLinkedUids(onResult: (Set<String>) -> Unit) {
        val uid = currentUid()
        if (uid == null) {
            onResult(emptySet())
            return
        }
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val email = doc.getString("email")?.lowercase()?.trim() ?: ""
                if (email.isBlank()) {
                    onResult(setOf(uid))
                    return@addOnSuccessListener
                }
                db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val uids = snapshot.documents.map { it.id }.toSet() + uid
                        onResult(uids)
                    }
                    .addOnFailureListener {
                        onResult(setOf(uid))
                    }
            }
            .addOnFailureListener {
                onResult(setOf(uid))
            }
    }
}

// ─── Data Classes ──────────────────────────────────────────────

data class DashboardStats(
    val favoriteCount: Int = 0,
    val bookmarkedCount: Int = 0,
    val wordsRated: Int = 0,
    val quizzesTaken: Int = 0,
    val quizAccuracy: Float = 0f
)

data class UserProfile(
    val name: String = "",
    val rollNo: String = "",
    val department: String = "",
    val role: String = "student",
    val email: String = "",
    val currentStreak: Int = 0
)

data class QuizAnswerDetail(
    val word: String,
    val correctAnswer: String,
    val userAnswer: String,
    val isCorrect: Boolean
)

data class DetailedQuizResult(
    val id: String = "",
    val categoryTitle: String = "",
    val score: Int = 0,
    val total: Int = 0,
    val timestamp: Long = 0L,
    val answers: List<QuizAnswerDetail> = emptyList()
)

data class StudentReport(
    val uid: String = "",
    val name: String = "",
    val rollNo: String = "",
    val department: String = "",
    val totalScore: Int = 0,
    val totalQuestions: Int = 0,
    val testResults: List<StudentTestResult> = emptyList()
)

data class StudentTestResult(
    val id: String = "",
    val categoryTitle: String = "",
    val score: Int = 0,
    val total: Int = 0,
    val timestamp: Long = 0L
)
