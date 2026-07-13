package com.example.english_app.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Handles everything related to a signed-in user's saved progress:
 *  - favorite words
 *  - bookmarked ("saved") words
 *  - per-word difficulty ratings
 *  - quiz results (for the Dashboard stats)
 *
 * Data layout in Firestore:
 *   users/{uid}/wordProgress/{categoryId_word}   -> { favorite, bookmarked, difficulty }
 *   users/{uid}/quizResults/{autoId}             -> { categoryId, score, total, timestamp }
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

    /** Records a quiz result — works for both guests (anonymous) and logged-in users. */
    fun recordQuizResult(categoryId: String, categoryTitle: String, score: Int, total: Int) {
        android.util.Log.d("Dashboard", "recordQuizResult called: $categoryId score=$score/$total uid=${auth.currentUser?.uid}")
        withUid { uid ->
            val entry = mapOf(
                "categoryId" to categoryId,
                "categoryTitle" to categoryTitle,
                "score" to score,
                "total" to total,
                "timestamp" to System.currentTimeMillis()
            )
            android.util.Log.d("Dashboard", "Saving quiz result to Firestore under uid=$uid")
            quizResultsCollection(uid).add(entry)
                .addOnSuccessListener { android.util.Log.d("Dashboard", "✅ Quiz result saved successfully!") }
                .addOnFailureListener { e -> android.util.Log.e("Dashboard", "❌ Failed to save quiz result: ${e.message}") }
        }
    }

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

data class DashboardStats(
    val favoriteCount: Int = 0,
    val bookmarkedCount: Int = 0,
    val wordsRated: Int = 0,
    val quizzesTaken: Int = 0,
    val quizAccuracy: Float = 0f
)
