package com.example.english_app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.example.english_app.ui.theme.*

// All words below use imageUrl = "" on purpose. CarouselScreen shows a gray
// placeholder box for any word with a blank imageUrl. Once real images are
// added (as drawables), just set the matching imageUrl (e.g. "docword1")
// and add the mapping in CarouselScreen.getImageResId().

// ---------- Document 1: Advanced Vocabulary ----------
val docWords1 = listOf(
    Word("Crouch", "", "To bend low to hide or get ready to move.", "The cat crouched under the bush before catching its prey.", "crouch"),
    Word("Flinch", "", "To move suddenly out of fear or pain.", "Robert flinched when he saw the snake.", "flinch")
)

// ---------- Document 2: Basic Vocabulary ----------
val docWords2 = listOf(
    Word("Splendid", "", "Very beautiful and impressive.", "The Indian team gave a splendid performance.", "splendid"),
    Word("Elegant", "", "Graceful and stylish.", "Her handwriting is elegant and neat.", "elegant")
)

// ---------- Document 3: Basic vs Advanced Vocabulary ----------
val docWords3 = listOf(
    Word("Colossal", "", "Advanced form of 'big' ΓÇö extremely large.", "I saw a colossal statue in the museum.", "collasal"),
    Word("Myriad", "", "Advanced form of 'many' — a huge, countless number.", "My teacher offered a myriad of information.", "many")
)

// ---------- Document 4: Blended Vocabulary ----------
val docWords4 = listOf(
    Word("Brunch", "", "Breakfast + Lunch.", "I had brunch at 11.00 a.m.", "doc4_brunch"),
    Word("Smog", "", "Smoke + Fog.", "The area was covered with smog after the fire.", "doc4_smog")
)

// ---------- Document 5: Kitchen Vocabulary ----------
val docWords5 = listOf(
    Word("Sauté", "", "Cooking in a pan with oil over medium to high heat.", "Onions are sautéed until they turn golden brown.", "saute"),
    Word("Whisk", "", "To beat ingredients quickly to add air.", "I whisked the eggs to make an omelet.", "doc5_whisk")
)

// ---------- Document 6: Movement Vocabulary ----------
val docWords6 = listOf(
    Word("Wink", "", "To close and open one eye quickly, often as a signal.", "She winked at her friend.", "wink"),
    Word("Chew", "", "To crush food with the teeth before swallowing.", "My friend chews the food slowly.", "chew")
)

// ---------- Document 7: Vocab Twist (Homographs) ----------
val docWords7 = listOf(
    Word("Bat", "", "1) A flying mammal. 2) A stick used in sports like cricket.", "I saw a bat on the tree. / I got Dhoni's signature on my cricket bat.", "bat"),
    Word("Bank", "", "1) Where people save and borrow money. 2) The land alongside a river.", "I am going to the bank to deposit money. / She went to the river bank to relax.", "bank"),
    Word("Bark", "", "1) The sound a dog makes. 2) The outer layer of a tree.", "The dog barks at the thief. / Priya carved her name on the bark of the tree.", "bark"),
    Word("Bow", "", "1) To bend forward. 2) A weapon used to shoot arrows.", "The actors bowed to the audience. / The hunter aimed the bow at the deer.", "bow"),
    Word("Fair", "", "1) Morally right. 2) An exhibition.", "It is not fair to be partial towards any student. / There is a job fair on our campus today.", "fair"),
    Word("Jam", "", "1) A sweet fruit spread. 2) To get stuck or blocked.", "I love strawberry jam on toast. / The traffic jam made me late for work.", "jam"),
    Word("Lead", "", "1) To guide or direct. 2) A type of metal.", "She will lead the team in the project. / Pipes were once made of lead.", "lead"),
    Word("Match", "", "1) A contest or game. 2) A stick used to start a fire.", "The football match was very exciting. / He struck a match to light the candle.", "match"),
    Word("Spring", "", "1) The season after winter. 2) A coiled device that can stretch back.", "Flowers bloom in spring. / The mattress has a broken spring inside.", "spring"),
    Word("Tire", "", "1) To feel exhausted. 2) The rubber covering of a wheel.", "Long walks tire me out. / The car needs a new tire.", "tire"),
    Word("Current", "", "1) The flow of electricity or water. 2) Happening now.", "The current in the river was strong. / He is not satisfied with his current job.", "current"),
    Word("Minute", "", "1) A unit of time. 2) Extremely small.", "Wait a minute, please. / The insect was minute, barely visible.", "minute"),
    Word("Scale", "", "1) The outer covering of a fish. 2) A system used to measure.", "The fish's scale shimmered in the sun. / We used a scale to weigh the vegetables.", "scale"),
    Word("Clip", "", "1) To cut something short. 2) A small device used to hold things together.", "He clipped the hedge neatly. / She used a clip to hold her hair.", "clip"),
    Word("Bolt", "", "1) To run away suddenly. 2) A metal bar used to lock something.", "The horse bolted at the sound of thunder. / He slid the bolt to lock the gate.", "bolt"),
    Word("Charge", "", "1) To ask a price. 2) To rush forward.", "The shop charges extra for delivery. / The bull charged at the fence.", "charge"),
    Word("Pitch", "", "1) The tone of a sound. 2) To throw something.", "Her voice has a high pitch. / He pitched the ball to the batsman.", "pitch"),
    Word("Console", "", "1) To comfort someone. 2) A control panel.", "She tried to console her crying friend. / He pressed the buttons on the game console.", "console"),
    Word("Pound", "", "1) To hit repeatedly. 2) A unit of weight.", "He pounded on the door. / The parcel weighs two pounds.", "pound"),
    Word("Suspend", "", "1) To hang something from above. 2) To stop something temporarily.", "The lamp was suspended from the ceiling. / The match was suspended due to rain.", "suspend"),
    Word("Plot", "", "1) A secret plan. 2) A piece of land.", "The villains hatched a plot. / They bought a plot to build a house.", "plot"),
    Word("Monitor", "", "1) To observe carefully. 2) A screen.", "Teachers monitor the students during exams. / He bought a new computer monitor.", "monitor"),
    Word("Anchor", "", "1) An object that keeps a ship in place. 2) A news presenter.", "The ship dropped its anchor. / She works as a news anchor.", "anchor"),
    Word("Appraise", "", "1) To evaluate the value of something. 2) To judge the quality of something.", "The jeweler appraised the necklace. / The manager appraised his employee's work.", "appraise"),
    Word("Abstract", "", "1) Existing in thought but not physical. 2) To remove.", "Freedom is an abstract idea. / The chemist abstracted the sample for testing."),
    Word("Racket", "", "1) A loud, disturbing noise. 2) A dishonest scheme.", "The children made a racket upstairs. / The police busted an illegal racket.", "racket"),
    Word("Vault", "", "1) A secured room, often for valuables. 2) To jump over something.", "The gold was kept in a bank vault. / The athlete vaulted over the bar.", "vault"),
    Word("Alight", "", "1) To get down from a vehicle. 2) To be on fire.", "Passengers alight at the next stop. / The dry grass was alight within seconds.", "alight"),
    Word("Annotate", "", "1) To add notes to something. 2) To explain with notes.", "She annotated the textbook with her own notes. / The teacher annotated the essay with feedback.", "annotate"),
    Word("Conflate", "", "1) To combine two things into one. 2) To mistakenly treat as the same.", "The report conflates two separate issues. / People often conflate the two festivals.", "conflate"),
    Word("Cower", "", "1) To crouch down in fear. 2) To show fear openly.", "The dog cowered during the storm. / He cowered before his angry boss.", "cower"),
    Word("Dissipate", "", "1) To scatter and disappear. 2) To waste resources.", "The morning fog dissipated by noon. / He dissipated his fortune on gambling.", "dissipate"),
    Word("Enervate", "", "1) To weaken someone. 2) To make someone feel tired.", "The long illness enervated him. / The heat enervated the entire team.", "enervate"),
    Word("Connive", "", "1) To secretly cooperate in a wrongdoing. 2) To ignore a wrongdoing on purpose.", "The two officials connived to hide the fraud. / The guard connived at the theft.")
)

val docCategories = listOf(
    Category("doc1", "Advanced Vocabulary", "Rich, expressive words for advanced learners", VibrantPurple, Icons.Default.AutoStories, docWords1),
    Category("doc2", "Basic Vocabulary", "Everyday words to build a strong foundation", VibrantTeal, Icons.Default.MenuBook, docWords2),
    Category("doc3", "Basic vs Advanced", "Simple words paired with their advanced equivalents", VibrantOrange, Icons.Default.CompareArrows, docWords3),
    Category("doc4", "Blended Words", "Portmanteau words formed by blending two words", VibrantPink, Icons.Default.Shuffle, docWords4),
    Category("doc5", "Kitchen Vocabulary", "Cooking tools, spices, and kitchen terms", VibrantGreen, Icons.Default.Kitchen, docWords5),
    Category("doc6", "Movement Vocabulary", "Words describing small actions and gestures", VibrantBlue, Icons.Default.DirectionsRun, docWords6),
    Category("doc7", "Vocab Twist", "Homographs ΓÇö one word, multiple meanings", VibrantYellow, Icons.Default.SwapHoriz, docWords7)
)
