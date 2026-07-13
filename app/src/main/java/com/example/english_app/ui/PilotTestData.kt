package com.example.english_app.ui

/** A single fixed question for the Pilot Test (unlike the per-category quizzes,
 *  these questions and options are fixed, not auto-generated). */
data class PilotTestQuestion(
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int
)

// Built from "Vocabulary Test – 20 Questions.docx", verified against its answer key:
// 1-b, 2-c, 3-a, 4-b, 5-a, 6-b, 7-c, 8-b, 9-d, 10-a, 11-b, 12-c, 13-b, 14-c, 15-a,
// 16-c, 17-c, 18-b, 19-a, 20-b
val pilotTestQuestions = listOf(
    PilotTestQuestion(
        "The police took ______ action to arrest the thief.",
        listOf("cozy", "swift", "drowsy", "elegant"), 1
    ),
    PilotTestQuestion(
        "I felt ______ after the heavy dinner.",
        listOf("jubilant", "poignant", "drowsy", "halcyon"), 2
    ),
    PilotTestQuestion(
        "The deer managed to ______ the hunter.",
        listOf("evade", "ponder", "blink", "glance"), 0
    ),
    PilotTestQuestion(
        "The mountain's beauty was truly ______.",
        listOf("swift", "ineffable", "cozy", "smog"), 1
    ),
    PilotTestQuestion(
        "She was ______ after securing first rank in the exam.",
        listOf("jubilant", "drowsy", "poignant", "elegant"), 0
    ),
    PilotTestQuestion(
        "A feeling of obsessive desire for someone is called:",
        listOf("Equanimity", "Limerence", "Querencia", "Serendipity"), 1
    ),
    PilotTestQuestion(
        "A place where you feel safe and comfortable:",
        listOf("Halcyon", "Smog", "Querencia", "Chortle"), 2
    ),
    PilotTestQuestion(
        "Smoke mixed with fog is known as:",
        listOf("Blog", "Smog", "Motel", "Brunch"), 1
    ),
    PilotTestQuestion(
        "\"Breakfast + Lunch\" forms the word:",
        listOf("Chortle", "Blog", "Frenemy", "Brunch"), 3
    ),
    PilotTestQuestion(
        "A friend who behaves like an enemy:",
        listOf("Frenemy", "Chillax", "Blog", "Motel"), 0
    ),
    PilotTestQuestion(
        "She ______ the eggs to make an omelet.",
        listOf("mashed", "whisked", "grated", "blanched"), 1
    ),
    PilotTestQuestion(
        "I ______ coconut for the chutney.",
        listOf("kneaded", "whisked", "grated", "mashed"), 2
    ),
    PilotTestQuestion(
        "The chef used a ______ to serve soup.",
        listOf("tongs", "ladle", "pestle", "whisk"), 1
    ),
    PilotTestQuestion(
        "My grandmother used a ______ and pestle to grind spices.",
        listOf("tongs", "whisk", "mortar", "grate"), 2
    ),
    PilotTestQuestion(
        "She ______ tomatoes before peeling them.",
        listOf("blanched", "mashed", "kneaded", "flicked"), 0
    ),
    PilotTestQuestion(
        "Robert suddenly pulled the window open. He ______ it.",
        listOf("blinked", "nodded", "yanked", "sighed"), 2
    ),
    PilotTestQuestion(
        "The child laughed when I ______ him.",
        listOf("poked", "flicked", "tickled", "leaned"), 2
    ),
    PilotTestQuestion(
        "Students ______ their heads while listening to the teacher.",
        listOf("wink", "nod", "blink", "leap"), 1
    ),
    PilotTestQuestion(
        "John ______ the fan switch.",
        listOf("flicked", "cupped", "poked", "shrugged"), 0
    ),
    PilotTestQuestion(
        "The baby ______ while speaking to the stranger.",
        listOf("sighed", "stuttered", "leaped", "giggled"), 1
    )
)

const val PILOT_TEST_CATEGORY_ID = "pilotTest"
const val PILOT_TEST_TITLE = "Pilot Test"
