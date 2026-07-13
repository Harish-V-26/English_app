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

    private fun currentUid(): String? = FirebaseAuth.getInstance().currentUser?.uid

    private fun wordDocId(categoryId: String, word: String): String =
        "${categoryId}_${word}".replace(Regex("[^A-Za-z0-9_]"), "_")

    private fun wordProgressCollection(uid: String) =
        db.collection("users").document(uid).collection("wordProgress")

    private fun quizResultsCollection(uid: String) =
        db.collection("users").document(uid).collection("quizResults")

    fun setFavorite(categoryId: String, word: String, isFavorite: Boolean) {
        val uid = currentUid() ?: return
        wordProgressCollection(uid).document(wordDocId(categoryId, word))
            .set(mapOf("favorite" to isFavorite), com.google.firebase.firestore.SetOptions.merge())
    }

    fun setBookmarked(categoryId: String, word: String, isBookmarked: Boolean) {
        val uid = currentUid() ?: return
        wordProgressCollection(uid).document(wordDocId(categoryId, word))
            .set(mapOf("bookmarked" to isBookmarked), com.google.firebase.firestore.SetOptions.merge())
    }

    fun setDifficulty(categoryId: String, word: String, rating: Int) {
        val uid = currentUid() ?: return
        wordProgressCollection(uid).document(wordDocId(categoryId, word))
            .set(mapOf("difficulty" to rating), com.google.firebase.firestore.SetOptions.merge())
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

    fun recordQuizResult(categoryId: String, categoryTitle: String, score: Int, total: Int) {
        val uid = currentUid() ?: return
        val entry = mapOf(
            "categoryId" to categoryId,
            "categoryTitle" to categoryTitle,
            "score" to score,
            "total" to total,
            "timestamp" to System.currentTimeMillis()
        )
        quizResultsCollection(uid).add(entry)
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
}

data class DashboardStats(
    val favoriteCount: Int = 0,
    val bookmarkedCount: Int = 0,
    val wordsRated: Int = 0,
    val quizzesTaken: Int = 0,
    val quizAccuracy: Float = 0f
)
