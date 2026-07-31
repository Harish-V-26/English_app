package com.example.english_app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.example.english_app.ui.theme.*
import androidx.compose.ui.graphics.Color

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
    Word("Bat", "", "Meaning 1: A flying mammal\nMeaning 2: A stick used in sports like cricket", "I saw a bat on the tree\nI got signature of Dhoni on my cricket bat.", "vt_bat"),
    Word("Bank", "", "Meaning 1: the place where people save and borrow money\nMeaning 2: riverside", "I am going to the bank to deposit the money.\nShe went to the river bank for relaxation.", "vt_bank")
    /*
    ,Word("Bark", "", "Meaning 1: Sound of a dog\nMeaning 2: outer layer of the tree.", "The dog barks at the thief.\nPriya carved her name on the bark of the tree.", "vt_bark"),
    Word("Bow", "", "Meaning 1: To bend forward\nMeaning 2: a weapon", "The actors bowed to the audience after their performance.\nThe hunter aimed the bow at the deer.", "vt_bow"),
    Word("Fair", "", "Meaning 1: something is right morally\nMeaning 2: exhibition", "It is not fair to be partial towards any particular student.\nThere is a job fair in our campus today.", "vt_fair"),
    Word("Jam", "", "Meaning 1: A sweet fruit spread\nMeaning 2: To get stuck or blocked", "I love strawberry jam on toast.\nThe traffic jam made me late for work.", "vt_jam"),
    Word("Lead", "", "Meaning 1: To guide or direct\nMeaning 2: A type of metal", "She will lead the team in the project.\nPipes were made of lead in old houses.", "vt_lead"),
    Word("Match", "", "Meaning 1: A contest or game\nMeaning 2: A stick used to start fire", "The football match was very exciting.\nHe struck a match to light the candle.", "vt_match"),
    Word("Spring", "", "Meaning 1: The season after winter\nMeaning 2: A coiled device that can stretch or jump back", "Flowers bloom in spring.\nThe mattress has a broken spring inside.", "vt_spring"),
    Word("Tire", "", "Meaning 1: To feel exhausted\nMeaning 2: The rubber covering of a wheel", "Long walks tire me out.\nThe car needs a new tire.", "vt_tire"),
    Word("Current", "", "Meaning 1: Flow of electricity\nMeaning 2: present", "He is not satisfied with his current job.", "vt_current"),
    Word("Minute", "", "Meaning 1: Unit of time\nMeaning 2: Small", "", "vt_minute"),
    Word("Scale", "", "Meaning 1: The outer covering of fish\nMeaning 2: system used to measure", "", "vt_scale"),
    Word("Clip", "", "Meaning 1: to cut\nMeaning 2: a small device", "", "vt_clip"),
    Word("Bolt", "", "Meaning 1: to run away\nMeaning 2: lock", "", "vt_bolt"),
    Word("Charge", "", "Meaning 1: to ask a price\nMeaning 2: to rush", "", "vt_charge"),
    Word("Spring", "", "Meaning 1: season\nMeaning 2: jump", "", "vt_spring2"),
    Word("Pitch", "", "Meaning 1: Tone\nMeaning 2: Throw", "", "vt_pitch"),
    Word("Console", "", "Meaning 1: to comfort\nMeaning 2: a control panel", "", "vt_console"),
    Word("Pound", "", "Meaning 1: to hit\nMeaning 2: weight", "", "vt_pound"),
    Word("Suspend", "", "Meaning 1: to hand something from above\nMeaning 2: to stop something", "", "vt_suspend"),
    Word("Plot", "", "Meaning 1: a secret plan\nMeaning 2: a piece of land", "", "vt_plot"),
    Word("Monitor", "", "Meaning 1: to observe carefully\nMeaning 2: a screen", "", "vt_monitor"),
    Word("Anchor", "", "Meaning 1: an object which keeps ship in place\nMeaning 2: a new presenter", "", "vt_anchor"),
    Word("Appraise", "", "Meaning 1: to evaluate the value of something\nMeaning 2: to judge the quality of", "", "vt_appraise"),
    Word("Abstract", "", "Meaning 1: Existing in thought but not physical\nMeaning 2: to remove", "", "vt_abstract_img"),
    Word("Racket", "", "Meaning 1: disturbing noise\nMeaning 2: dishonest occupation", "", "vt_racket"),
    Word("Vault", "", "Meaning 1: a secured room\nMeaning 2: to jump over something", "", "vt_vault"),
    Word("Alight", "", "Meaning 1: to descend\nMeaning 2: to be on fire", "", "vt_alight"),
    Word("Annotate", "", "Meaning 1: to add something\nMeaning 2: to explain", "", "vt_annotate"),
    Word("Conflate", "", "Meaning 1: to combine\nMeaning 2: to confuse", "", "vt_conflate"),
    Word("Cower", "", "Meaning 1: to shrink physically\nMeaning 2: express fear", "", "vt_cower"),
    Word("Dissipate", "", "Meaning 1: to scatter\nMeaning 2: to waste", "", "vt_dissipate"),
    Word("Enervate", "", "Meaning 1: to weaken\nMeaning 2: to tire", "", "vt_enervate"),
    Word("Convive", "", "Meaning 1: to secretly cooperate\nMeaning 2: to ignore wrongdoing", "", "vt_convive")
    */
)

// ---------- Document 8: Types of Eating ----------
val docWords8 = listOf(
    Word("Nibble", "", "eat small bites", "The child nibbles the biscuit.", "nibble"),
    Word("Gobble", "", "eat quickly and greedily", "She gobbled her food after a day of fasting.", "gobble")
)

// ---------- Document 9: Types of LSRW and Looking ----------
val docWords9 = listOf(
    Word("Listen", "", "listening attentively", "She listens to the music", "listen"),
    Word("Overhear", "", "hear something accidentally", "He overhears their conversation", "overhear")
)

// ---------- Document 10: Types of Walking ----------
val docWords10 = listOf(
    Word("Limp", "", "walk with difficulty due to injury", "The sportsman limped after hurting his leg.", "limp"),
    Word("Stride", "", "walk with long, confident steps", "She strides into the auditorium with confident.", "stride")
)

// ---------- Document 11: Types of Weather ----------
val docWords11 = listOf(
    Word("Sunny", "", "bright with sunlight", "He played outside on sunny day.", "sunny"),
    Word("Stormy", "", "with strong winds, rain, thunder", "Fishermen never go for fishing on stormy days", "stormy")
)

// ---------- Document 12: Ted Talks ----------
val docWords12 = listOf(
    Word("Ted Talks", "", "Links 1-5", "https://youtu.be/xEGTC5jGI94?si=8NiperTW7QRamQoy\nhttps://youtu.be/sB34sRehUvU?si=FV2tY33A51Mhuzi9\nhttps://youtu.be/6LZ7QqoY_1w?si=edlnGRTvrA0VlDV-\nhttps://youtu.be/TIwBwyMgS50?si=OTv0_iwVKvGMtIE8\nhttps://youtu.be/bC0hlK7WGcM?si=63z0xt-wfLaQyvI1", ""),
    Word("Ted Talks", "", "Links 6-10", "https://youtu.be/8ZhWojQnHls?si=mfVTj_xx8sfEASAH\nhttps://youtu.be/0NV1KdWRHck?si=ro4b5JvIwVDYp1Hp\nhttps://youtu.be/lhv72TsRvHU?si=9S4_Mo8ClD_dF1A5\nhttps://youtu.be/TFbv757kup4?si=47JfXz3ns_PcoxYd\nhttps://youtu.be/o_XVt5rdpFY?si=gpR0T30IPjKYOOMP", "")
)

// ---------- Document 13: Stories ----------
val docWords13 = listOf(
    Word("Stories", "", "Links 1-5", "https://youtu.be/IvxUmOQsEOA?si=ufwWMGtV-_oIb-IW\nhttps://youtu.be/oTimWHAoawU?si=VqvchBDW3gd5aA8Y\nhttps://youtu.be/1VHTnrutwAo?si=Y92JjOBscDpVIfUC\nhttps://youtu.be/VkJ0IcJ3EpA?si=mOG1eDn2goa8t2_G\nhttps://youtu.be/Wygb93WOHAo?si=hnb5wXVM6x1GE_4r", ""),
    Word("Stories", "", "Links 6-10", "https://youtu.be/ieFWfWtKmTc?si=eUFdk_gC2n4kBi1K\nhttps://youtu.be/F0lKZqHwNXE?si=nANcP_NHZsnGr1ww\nhttps://youtu.be/TbmSCdn_iUo?si=CaTIpweWQ5Bwi5_G\nhttps://youtu.be/_hheZx7hxGQ?si=P2vXXKjXbdvpjZ1l\nhttps://youtu.be/ESxN1eoCWTc?si=MLjt2YDajIvxiyMp", "")
)

// ---------- Document 14: Podcast Videos ----------
val docWords14 = listOf(
    Word("Podcast Videos", "", "Links 1-5", "https://youtu.be/ZDAoStA38r8?si=MLNanDb7tj9s9CpR\nhttps://youtu.be/aiUGN3TDvw4?si=3Mg_8d0d9wiem7AM\nhttps://youtu.be/31y2Bq1RYQA?si=RatuwQdTUc44BIJW\nhttps://youtu.be/I_tRSrPru94?si=4R-gInm6WCQ6oOU4\nhttps://youtu.be/bq6GBbh3uhU?si=zIOAm1kXGU0_FAKI", ""),
    Word("Podcast Videos", "", "Links 6-10", "https://youtu.be/-SHjj68WaLs?si=HAcCGC2CQE4rjdF1\nhttps://youtu.be/0y185Tz39lk?si=CSSvbbR42WHkOwyS\nhttps://youtu.be/Y__k1dLo-eo?si=Iba2lEOPcaGANSIL\nhttps://youtu.be/eabT4z25qxE?si=6bvgEAEYbKidGipj\nhttps://youtu.be/3Mr4mGMALwI?si=FcZn5gCg1S-QoJub", "")
)

val docCategories = listOf(
    Category("doc1", "Advanced Vocabulary", "Rich, expressive words for advanced learners", VibrantPurple, Icons.Default.AutoStories, docWords1),
    Category("doc2", "Basic Vocabulary", "Everyday words to build a strong foundation", VibrantTeal, Icons.Default.MenuBook, docWords2),
    Category("doc3", "Basic vs Advanced", "Simple words paired with their advanced equivalents", VibrantOrange, Icons.Default.CompareArrows, docWords3),
    Category("doc4", "Blended Words", "Portmanteau words formed by blending two words", VibrantPink, Icons.Default.Shuffle, docWords4),
    Category("doc5", "Kitchen Vocabulary", "Cooking tools, spices, and kitchen terms", VibrantGreen, Icons.Default.Kitchen, docWords5),
    Category("doc6", "Movement Vocabulary", "Words describing small actions and gestures", VibrantBlue, Icons.Default.DirectionsRun, docWords6),
    Category("doc7", "Vocab Twist", "Homographs — one word, multiple meanings", VibrantYellow, Icons.Default.SwapHoriz, docWords7),
    Category("doc8", "Types of Eating", "Different ways to eat", VibrantPink, Icons.Default.Restaurant, docWords8),
    Category("doc9", "Types of LSRW and Looking", "Listening, Speaking, Reading, Writing, Looking", VibrantBlue, Icons.Default.Visibility, docWords9),
    Category("doc10", "Types of Walking", "Different ways of walking", VibrantPurple, Icons.Default.DirectionsWalk, docWords10),
    Category("doc11", "Types of Weather", "Various weather conditions", VibrantOrange, Icons.Default.WbSunny, docWords11),
    Category("doc12", "Ted Talks", "Inspiring Ted Talk videos", VibrantGreen, Icons.Default.VideoLibrary, docWords12),
    Category("doc13", "Stories", "Engaging story videos", VibrantOrange, Icons.Default.MenuBook, docWords13),
    Category("doc14", "Podcast Videos", "Interesting podcast videos", VibrantBlue, Icons.Default.Headphones, docWords14),
    Category("docPilot", "Pilot Quiz Words", "All words from the Pilot Test", Color(0xFFE91E63), Icons.Default.Quiz, docWordsPilot)
)

// ---------- Pilot Quiz Words (all 20 answers + distractors) ----------
val docWordsPilot = listOf(
    Word("Swift", "", "Moving or happening quickly.", "The police took swift action to arrest the thief.", "swift"),
    Word("Drowsy", "", "Feeling sleepy and sluggish.", "I felt drowsy after the heavy dinner.", "drowsy"),
    Word("Evade", "", "To escape or avoid something cleverly.", "The deer managed to evade the hunter.", "evade"),
    Word("Ineffable", "", "Too great or extreme to be expressed in words.", "The mountain's beauty was truly ineffable.", "ineffable"),
    Word("Jubilant", "", "Feeling or expressing great happiness.", "She was jubilant after securing first rank in the exam.", "jubilant"),
    Word("Limerence", "", "A feeling of obsessive desire or infatuation for someone.", "His limerence for her made it hard to focus on work.", "limerance"),
    Word("Querencia", "", "A place where you feel safe, at home, and comfortable.", "Her room was her querencia after a long day.", "querencia"),
    Word("Smog", "", "Smoke mixed with fog — a type of air pollution.", "Smoke mixed with fog is known as smog.", "smog"),
    Word("Brunch", "", "A meal eaten in late morning — Breakfast + Lunch.", "\"Breakfast + Lunch\" forms the word brunch.", "brunch"),
    Word("Frenemy", "", "A person who is a friend but also a rival or enemy.", "A friend who behaves like an enemy is called a frenemy.", "frenemy"),
    Word("Whisk", "", "To beat ingredients quickly to add air.", "She whisked the eggs to make an omelet.", "whisk"),
    Word("Grate", "", "To shred food into small pieces using a grater.", "I grated coconut for the chutney.", "grate"),
    Word("Ladle", "", "A large spoon with a long handle used to serve soup.", "The chef used a ladle to serve soup.", "ladle"),
    Word("Mortar & Pestle", "", "A bowl and club used to grind spices and ingredients.", "My grandmother used a mortar and pestle to grind spices.", "doc5_mortar_pestle"),
    Word("Blanch", "", "To briefly boil food then cool it quickly in cold water.", "She blanched tomatoes before peeling them.", "blanch"),
    Word("Yank", "", "To pull something suddenly and with force.", "Robert suddenly pulled the window open — he yanked it.", "yank"),
    Word("Tickle", "", "To touch lightly so as to cause laughter.", "The child laughed when I tickled him.", "tickle"),
    Word("Nod", "", "To move the head up and down to show agreement.", "Students nod their heads while listening to the teacher.", "nod"),
    Word("Flick", "", "To move something with a quick, light motion.", "John flicked the fan switch.", "flick"),
    Word("Stutter", "", "To speak with sudden stops or repeated sounds.", "The baby stuttered while speaking to the stranger.", "stutter")
)
