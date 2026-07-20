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
     * Returns the current user's UID. If no user is signed in (guest),
     * signs in anonymously so progress can still be saved.
     */
    private fun currentUid(): String? = auth.currentUser?.uid

    /**
     * Ensures there is a signed-in Firebase user (anonymous if needed),
     * then runs [block] with that UID.
     */
    private fun withUid(block: (uid: String) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            block(uid)
        } else {
            auth.signInAnonymously()
                .addOnSuccessListener { result ->
                    result.user?.uid?.let { block(it) }
                }
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

    /** Saves user profile data (name, rollNo, department, role, email). */
    fun saveUserProfile(
        name: String,
        rollNo: String,
        department: String,
        role: String = "student",
        email: String = ""
    ) {
        withUid { uid ->
            val data = mapOf(
                "name" to name,
                "rollNo" to rollNo,
                "department" to department,
                "role" to role,
                "email" to email,
                "uid" to uid
            )
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

    /** Loads user profile data. If no profile document exists yet (e.g. a user who
     *  signed in with Google and never went through Sign Up), one is created here
     *  with sensible defaults so they still get a `department`/`role` field and
     *  show up correctly once they set their department in Settings. Without this,
     *  such users' quiz results would be saved but invisible to the Admin Panel,
     *  since there'd be no `department` to group them under. */
    fun loadUserProfile(onResult: (UserProfile) -> Unit) {
        val uid = currentUid() ?: run {
            onResult(UserProfile())
            return
        }
        val userDocRef = db.collection("users").document(uid)
        userDocRef.get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val profile = UserProfile(
                        name = doc.getString("name") ?: "",
                        rollNo = doc.getString("rollNo") ?: "",
                        department = doc.getString("department") ?: "",
                        role = doc.getString("role") ?: "student",
                        email = doc.getString("email") ?: ""
                    )
                    onResult(profile)
                } else {
                    // First time we've seen this user (e.g. fresh Google Sign-In) —
                    // create a minimal profile now so they're never a Firestore
                    // "ghost" with quiz results but no department to be grouped by.
                    val fallbackName = FirebaseAuth.getInstance().currentUser?.displayName ?: ""
                    val fallbackEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
                    val newProfile = UserProfile(
                        name = fallbackName,
                        rollNo = "",
                        department = "",
                        role = "student",
                        email = fallbackEmail
                    )
                    userDocRef.set(
                        mapOf(
                            "name" to newProfile.name,
                            "rollNo" to newProfile.rollNo,
                            "department" to newProfile.department,
                            "role" to newProfile.role,
                            "email" to newProfile.email,
                            "uid" to uid
                        ),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
                    onResult(newProfile)
                }
            }
            .addOnFailureListener {
                onResult(UserProfile())
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
        // IMPORTANT: unlike other progress writes (favorites/bookmarks), quiz
        // results must be tied to a real, named signed-in student account for
        // the Admin Panel's department reports to ever find them. `withUid`
        // silently signs in an anonymous ghost user if nobody is signed in --
        // that's fine for casual guest browsing, but it was quietly eating
        // quiz scores (attributed to a throwaway UID with no department/role,
        // so it could never show up for any admin) whenever this got called
        // during a moment with no real signed-in user -- e.g. right around an
        // account switch. So quiz results specifically require a real user.
        val uid = auth.currentUser?.uid
        if (uid == null) {
            android.util.Log.e(
                "Dashboard",
                "recordQuizResult: no signed-in user -- result NOT saved " +
                    "(this used to silently save to an anonymous ghost account instead)"
            )
            return
        }
        android.util.Log.d("Dashboard", "recordQuizResult called: $categoryId score=$score/$total uid=$uid")
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
                // Also append a summarized version to the user's document for Admin Panel
                val simplifiedResult = mapOf(
                    "categoryTitle" to categoryTitle,
                    "score" to score,
                    "total" to total,
                    "timestamp" to System.currentTimeMillis()
                )
                db.collection("users").document(uid)
                    .set(mapOf("testResults" to FieldValue.arrayUnion(simplifiedResult)), com.google.firebase.firestore.SetOptions.merge())
                    .addOnFailureListener { e -> android.util.Log.e("Dashboard", "❌ Failed to update testResults in user doc: ${e.message}") }
            }
            .addOnFailureListener { e -> android.util.Log.e("Dashboard", "❌ Failed to save quiz result: ${e.message}") }
    }

    /** Loads all detailed quiz results for the current user. */
    fun loadDetailedQuizResults(onResult: (List<DetailedQuizResult>) -> Unit) {
        val uid = currentUid() ?: run {
            onResult(emptyList())
            return
        }
        quizResultsCollection(uid).orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
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
                onResult(results)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    // ─── Admin Panel ───────────────────────────────────────────────

    /** Fetches all students in a given department and their quiz results.
     *  [onError] fires on genuine failures (e.g. Firestore permission denied)
     *  so the UI can tell "permission problem" apart from "really no data". */
    fun getDepartmentStudentReports(
        department: String,
        onResult: (List<StudentReport>) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        db.collection("users")
            .whereEqualTo("department", department)
            .get(com.google.firebase.firestore.Source.SERVER)
            .addOnSuccessListener { allDocs ->
                val userDocs = allDocs.filter { it.getString("role") == "student" }
                
                // --- DEBUG TOAST ---
                val totalCount = allDocs.size()
                val studentCount = userDocs.size
                android.os.Handler(android.os.Looper.getMainLooper()).post {
                    android.widget.Toast.makeText(
                        com.google.firebase.FirebaseApp.getInstance().applicationContext,
                        "DEBUG: Found $totalCount users in $department ($studentCount are Students)",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
                // -------------------

                if (userDocs.isEmpty()) {
                    onResult(emptyList())
                    return@addOnSuccessListener
                }
                
                val reports = mutableListOf<StudentReport>()
                var remaining = userDocs.size
                
                for (userDoc in userDocs) {
                    val uid = userDoc.getString("uid") ?: userDoc.id
                    val name = userDoc.getString("name") ?: ""
                    val rollNo = userDoc.getString("rollNo") ?: ""
                    val dept = userDoc.getString("department") ?: ""
                    
                    val fallbackToUserDoc = {
                        val rawTests = userDoc.get("testResults") as? List<*> ?: emptyList<Any>()
                        val testResults = rawTests.mapNotNull { item ->
                            val map = item as? Map<*, *> ?: return@mapNotNull null
                            StudentTestResult(
                                categoryTitle = map["categoryTitle"] as? String ?: "",
                                score = (map["score"] as? Number)?.toInt() ?: 0,
                                total = (map["total"] as? Number)?.toInt() ?: 0,
                                timestamp = (map["timestamp"] as? Number)?.toLong() ?: 0L
                            )
                        }
                        val totalScore = testResults.sumOf { it.score }
                        val totalQuestions = testResults.sumOf { it.total }

                        reports.add(
                            StudentReport(
                                name = name,
                                rollNo = rollNo,
                                department = dept,
                                totalScore = totalScore,
                                totalQuestions = totalQuestions,
                                testResults = testResults
                            )
                        )
                        remaining--
                        if (remaining <= 0) {
                            onResult(reports.sortedBy { it.rollNo })
                        }
                    }

                    quizResultsCollection(uid).get()
                        .addOnSuccessListener { quizDocs ->
                            if (quizDocs.isEmpty) {
                                fallbackToUserDoc()
                                return@addOnSuccessListener
                            }
                            var totalScore = 0
                            var totalQuestions = 0
                            val testResults = mutableListOf<StudentTestResult>()
                            quizDocs.forEach { doc ->
                                val score = (doc.getLong("score") ?: 0L).toInt()
                                val total = (doc.getLong("total") ?: 0L).toInt()
                                val catTitle = doc.getString("categoryTitle") ?: ""
                                val ts = doc.getLong("timestamp") ?: 0L
                                totalScore += score
                                totalQuestions += total
                                testResults.add(
                                    StudentTestResult(
                                        categoryTitle = catTitle,
                                        score = score,
                                        total = total,
                                        timestamp = ts
                                    )
                                )
                            }
                            reports.add(
                                StudentReport(
                                    name = name,
                                    rollNo = rollNo,
                                    department = dept,
                                    totalScore = totalScore,
                                    totalQuestions = totalQuestions,
                                    testResults = testResults
                                )
                            )
                            remaining--
                            if (remaining <= 0) {
                                onResult(reports.sortedBy { it.rollNo })
                            }
                        }
                        .addOnFailureListener {
                            fallbackToUserDoc()
                        }
                }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("AdminPanel", "getDepartmentStudentReports failed: ${e.message}")
                android.os.Handler(android.os.Looper.getMainLooper()).post {
                    android.widget.Toast.makeText(
                        com.google.firebase.FirebaseApp.getInstance().applicationContext,
                        "DEBUG ERROR: ${e.message}",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
                onError(e.message ?: "Unknown error loading student reports")
                onResult(emptyList())
            }
    }

    /** Fetches all unique departments from the users collection.
     *  [onError] fires on genuine failures (e.g. Firestore permission denied). */
    fun loadAllDepartments(onResult: (List<String>) -> Unit, onError: (String) -> Unit = {}) {
        db.collection("users")
            .whereEqualTo("role", "student")
            .get(com.google.firebase.firestore.Source.SERVER)
            .addOnSuccessListener { docs ->
                val departments = docs.mapNotNull { it.getString("department") }
                    .filter { it.isNotBlank() }
                    .distinct()
                    .sorted()
                android.util.Log.d("AdminPanel", "loadAllDepartments: found ${departments.size} departments from ${docs.size()} student docs")
                onResult(departments)
            }
            .addOnFailureListener { e ->
                android.util.Log.e("AdminPanel", "loadAllDepartments failed: ${e.message}")
                onError(e.message ?: "Unknown error loading departments")
                onResult(emptyList())
            }
    }

    // ─── Dashboard Stats ───────────────────────────────────────────

    /** Aggregate stats used by the Dashboard screen. */
    fun loadDashboardStats(onResult: (DashboardStats) -> Unit) {
        val uid = currentUid() ?: run {
            onResult(DashboardStats())
            return
        }

        wordProgressCollection(uid).get().addOnSuccessListener { progressDocs ->
            val favoriteCount = progressDocs.count { it.getBoolean("favorite") == true }
            val bookmarkedCount = progressDocs.count { it.getBoolean("bookmarked") == true }
            val wordsRated = progressDocs.count { (it.getLong("difficulty") ?: 0L) > 0 }

            quizResultsCollection(uid).get().addOnSuccessListener { quizDocs ->
                val quizzesTaken = quizDocs.size()
                var totalScore = 0
                var totalQuestions = 0
                quizDocs.forEach { doc ->
                    totalScore += (doc.getLong("score") ?: 0L).toInt()
                    totalQuestions += (doc.getLong("total") ?: 0L).toInt()
                }
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
            }.addOnFailureListener {
                onResult(
                    DashboardStats(
                        favoriteCount = favoriteCount,
                        bookmarkedCount = bookmarkedCount,
                        wordsRated = wordsRated
                    )
                )
            }
        }.addOnFailureListener {
            onResult(DashboardStats())
        }
    }

    open class DashboardListener(
        val wordReg: com.google.firebase.firestore.ListenerRegistration?,
        val quizReg: com.google.firebase.firestore.ListenerRegistration?
    ) {
        open fun remove() {
            wordReg?.remove()
            quizReg?.remove()
        }
    }

    /** Real-time aggregate stats used by the Dashboard screen. Works for guests too. */
    fun observeDashboardStats(onResult: (DashboardStats) -> Unit): DashboardListener {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            return attachListeners(uid, onResult)
        }
        // Guest: sign in anonymously first, then attach listeners
        var wordReg: com.google.firebase.firestore.ListenerRegistration? = null
        var quizReg: com.google.firebase.firestore.ListenerRegistration? = null
        val holder = DashboardListener(null, null)
        auth.signInAnonymously().addOnSuccessListener { result ->
            result.user?.uid?.let { newUid ->
                val l = attachListeners(newUid, onResult)
                wordReg = l.wordReg
                quizReg = l.quizReg
            }
        }.addOnFailureListener {
            onResult(DashboardStats())
        }
        return object : DashboardListener(null, null) {
            override fun remove() {
                wordReg?.remove()
                quizReg?.remove()
            }
        }
    }

    private fun attachListeners(uid: String, onResult: (DashboardStats) -> Unit): DashboardListener {
        var favoriteCount = 0
        var bookmarkedCount = 0
        var wordsRated = 0
        var quizzesTaken = 0
        var quizAccuracy = 0f

        fun emit() {
            onResult(
                DashboardStats(
                    favoriteCount = favoriteCount,
                    bookmarkedCount = bookmarkedCount,
                    wordsRated = wordsRated,
                    quizzesTaken = quizzesTaken,
                    quizAccuracy = quizAccuracy
                )
            )
        }

        val wordReg = wordProgressCollection(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                android.util.Log.e("Dashboard", "Error listening to wordProgress: ${error.message}", error)
                return@addSnapshotListener
            }
            if (snapshot == null) return@addSnapshotListener
            favoriteCount = snapshot.count { it.getBoolean("favorite") == true }
            bookmarkedCount = snapshot.count { it.getBoolean("bookmarked") == true }
            wordsRated = snapshot.count { (it.getLong("difficulty") ?: 0L) > 0 }
            emit()
        }

        val quizReg = quizResultsCollection(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                android.util.Log.e("Dashboard", "Error listening to quizResults: ${error.message}", error)
                return@addSnapshotListener
            }
            if (snapshot == null) return@addSnapshotListener
            quizzesTaken = snapshot.size()
            var totalScore = 0
            var totalQuestions = 0
            snapshot.forEach { doc ->
                totalScore += (doc.getLong("score") ?: 0L).toInt()
                totalQuestions += (doc.getLong("total") ?: 0L).toInt()
            }
            quizAccuracy = if (totalQuestions > 0) totalScore.toFloat() / totalQuestions else 0f
            emit()
        }

        return DashboardListener(wordReg, quizReg)
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
    val email: String = ""
)

data class QuizAnswerDetail(
    val word: String,
    val correctAnswer: String,
    val userAnswer: String,
    val isCorrect: Boolean
)

data class DetailedQuizResult(
    val categoryTitle: String,
    val score: Int,
    val total: Int,
    val timestamp: Long,
    val answers: List<QuizAnswerDetail> = emptyList()
)

data class StudentReport(
    val name: String = "",
    val rollNo: String = "",
    val department: String = "",
    val totalScore: Int = 0,
    val totalQuestions: Int = 0,
    val testResults: List<StudentTestResult> = emptyList()
)

data class StudentTestResult(
    val categoryTitle: String = "",
    val score: Int = 0,
    val total: Int = 0,
    val timestamp: Long = 0L
)
