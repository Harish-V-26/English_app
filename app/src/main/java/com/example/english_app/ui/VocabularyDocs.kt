package com.example.english_app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.MenuBook
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
    Word("Flinch", "", "To move suddenly out of fear or pain.", "Robert flinched when he saw the snake.", "flinch"),
    Word("Limerence", "", "An obsessive desire or infatuation for someone.", "His limerence for the actor grew after watching all his blockbuster movies.", "limerance"),
    Word("Halcyon", "", "Calm, peaceful, and happy.", "I recalled halcyon childhood days.", "doc1_halcyon"),
    Word("Vellichor", "", "The nostalgic feeling of being inside an old bookstore.", "The old book store gives me vellichor.", "vellichor"),
    Word("Ineffable", "", "Too great to be expressed in words.", "The mountain's beauty is ineffable.", "ineffable"),
    Word("Serendipity", "", "A pleasant surprise found by chance.", "I found my lost purse by serendipity.", "serendipity"),
    Word("Poignant", "", "Deeply emotional or touching.", "The climax of the movie is poignant.", "doc1_poignant"),
    Word("Jubilant", "", "Feeling or showing great happiness.", "I felt jubilant after passing in the examinations.", "doc1_jubilant"),
    Word("Equanimity", "", "Calmness and composure, especially in a difficult situation.", "They accepted the demise of their father with equanimity.", "equanimity"),
    Word("Querencia", "", "A place where one feels safe and at home.", "The lawn is my querencia.", "querencia"),
    Word("Dredging", "", "Digging or clearing out to widen a passage.", "Scavengers are dredging the open drainage.", "dredging"),
    Word("Henchman", "", "A loyal follower, especially of a villain.", "The chit fund manager sent his henchmen to collect the pending dues.", "henchman"),
    Word("Brick kiln", "", "A place where bricks are baked.", "Many labourers have been working in the brick kilns.", "brick_klins"),
    Word("Truce", "", "An agreement to stop fighting.", "The two opponent parties signed a truce.", "truce"),
    Word("Clandestine", "", "Kept secret, often because it is illegal.", "Robbers had a clandestine meeting at night.", "clandestine"),
    Word("Disgruntled", "", "Unhappy and annoyed.", "My wife left the house disgruntled after the argument.", "disgruntled"),
    Word("Grappling", "", "Struggling or wrestling with something.", "She is grappling with financial problems.", "grappling"),
    Word("Conclave", "", "A secret or private meeting.", "Leaders met in conclave to discuss the election.", "conclave"),
    Word("Trailblazer", "", "The first person to do or discover something.", "Albert Einstein was a trailblazer in science.", "trailblazer"),
    Word("Pioneer", "", "A person who is among the first to try something new.", "The Wright brothers were pioneers in aviation.", "pioneer"),
    Word("Tweak", "", "To change something slightly.", "She tweaked the whole plan.", "tweak"),
    Word("Ember", "", "A small piece of burning coal or wood.", "My mother blew on the ember to bring the fire back.", "ember"),
    Word("Domicile", "", "A person's permanent home or legal residence.", "Students have to submit domicile proof.", "domicile"),
    Word("Maverick", "", "An independent thinker who does things their own way.", "Gandhi was a maverick leader who chose non-violence instead of war.", "maverick"),
    Word("Whiff", "", "A slight smell carried in the air.", "I caught a whiff of jasmine in the air.", "whiff"),
    Word("Consecration", "", "A sacred ceremony, often at a temple or church.", "The ancient temple was revived with a consecration ceremony.", "consecration_ceremony"),
    Word("Hoax", "", "A false story meant to trick people.", "The story about treasure in the temple was just a hoax.", "hoax"),
    Word("Snollygoster", "", "A selfish, unprincipled politician.", "People should avoid voting for a snollygoster.", "snollygoster"),
    Word("Defenestrate", "", "To throw someone or something out of a window.", "The protesters symbolically defenestrated the leader's portrait.", "defenestrate"),
    Word("Diktat", "", "An order imposed without discussion.", "Employees neglected the management's diktat.", "diktat"),
    Word("Miscreant", "", "A person who behaves badly or breaks the law.", "The miscreants damaged the government transport.", "miscreants"),
    Word("Trespass", "", "To enter someone's property without permission.", "He trespassed on private land.", "tresspass"),
    Word("Broiling day", "", "A very hot, sunny day.", "It was a broiling day.", "broiling"),
    Word("Croon", "", "To sing or hum softly.", "The mother crooned a lullaby.", "croon")
)

// ---------- Document 2: Basic Vocabulary ----------
val docWords2 = listOf(
    Word("Splendid", "", "Very beautiful and impressive.", "The Indian team gave a splendid performance.", "splendid"),
    Word("Elegant", "", "Graceful and stylish.", "Her handwriting is elegant and neat.", "elegant"),
    Word("Swift", "", "Fast.", "The police took swift action to arrest the thief.", "swift"),
    Word("Cozy", "", "Warm and comfortable.", "The room was cozy in winter.", "cozy"),
    Word("Drowsy", "", "Sleepy.", "I felt drowsy after the heavy dinner.", "drowsy"),
    Word("Ponder", "", "To think deeply about something.", "She keeps pondering on her problems.", "ponder"),
    Word("Evade", "", "To escape or avoid.", "The deer evaded the hunter.", "evade"),
    Word("Ephemeral", "", "Lasting for a very short time.", "The beauty of the sunrise is ephemeral.", "ephemeral"),
    Word("Glance", "", "To look quickly.", "She glanced at the wall clock.", "glance"),
    Word("Stare", "", "To look fixedly.", "Ravi stared at Robert in anger.", "stare"),
    Word("Grab", "", "To take suddenly.", "She grabbed my pen.", "grab"),
    Word("Snatch", "", "To take by force.", "The monkey snatched the fruits from my hand.", "snatch"),
    Word("Mumble", "", "To speak unclearly with a half-closed mouth.", "John mumbled the answer because he was shy.", "mumble"),
    Word("Yell", "", "To shout loudly.", "The teacher yelled at him for not completing the assignment.", "yell"),
    Word("Obsolete", "", "Outdated, no longer used.", "Tape recorders are now considered obsolete.", "obsolete"),
    Word("Resilient", "", "Able to recover quickly from difficulties.", "She fell down but was resilient and stood up again.", "resilient"),
    Word("Timid", "", "Shy and nervous.", "She was too timid to answer her teacher.", "timid"),
    Word("Slam", "", "To shut with force.", "Geetha slammed the door with force.", "slam"),
    Word("Groan", "", "To make a low sound of pain.", "The patient groaned in pain.", "groan"),
    Word("Persuade", "", "To convince someone.", "He persuaded his friend to join for higher studies.", "persuade"),
    Word("Reluctant", "", "Unwilling to do something.", "She was reluctant to speak in front of the audience.", "reluctant"),
    Word("Embarrass", "", "To make someone feel shy or uncomfortable.", "I feel embarrassed to speak in front of an audience.", "embarrass"),
    Word("Exaggerate", "", "To describe something as better or worse than it really is.", "He exaggerated his pain to get attention.", "exaggerate"),
    Word("Surly", "", "Rude and bad-tempered.", "She gave a surly look.", "rude"),
    Word("Slush", "", "Melting snow mixed with mud, in a liquid state.", "Heavy rain changed the green field into slush.", "slush"),
    Word("Sludge", "", "A thick, dirty substance like mud or waste.", "Labourers cleaned the sludge from the water tank.", "sludge"),
    Word("Grime", "", "A layer of dirt that is hard to clean.", "The kitchen loft was covered with grime.", "grime"),
    Word("Devour", "", "To eat something quickly and hungrily.", "She devoured the lunch in two minutes.", "devour"),
    Word("Plunge", "", "To jump or dive suddenly.", "She plunged into the water.", "plunge"),
    Word("Shred", "", "To tear something into small pieces.", "She shredded the old notice in anger.", "shred")
)

// ---------- Document 3: Basic vs Advanced Vocabulary ----------
val docWords3 = listOf(
    Word("Colossal", "", "Advanced form of 'big' — extremely large.", "I saw a colossal statue in the museum.", "collasal"),
    Word("Myriad", "", "Advanced form of 'many' — a huge, countless number.", "My teacher offered a myriad of information.", "many"),
    Word("Ecstatic", "", "Advanced form of 'happy' — overjoyed.", "Priya is ecstatic about her new college.", "ecstatic"),
    Word("Incensed", "", "Advanced form of 'angry' — very angry.", "The teacher was incensed by the team's grave mistake.", "incensed"),
    Word("Amble", "", "Advanced form of 'walk' — to walk slowly and relaxed.", "I like to amble along the riverside.", "ambel"),
    Word("Deplorable", "", "Advanced form of 'bad' — very poor or shocking.", "My friend's hostel was in a deplorable state.", "deplorable"),
    Word("Pristine", "", "Advanced form of 'new' — spotless, in original condition.", "Her desk is always pristine.", "pristine"),
    Word("Venerable", "", "Advanced form of 'old' — respected because of age.", "I visited a venerable church in Venice.", "venerable"),
    Word("Perpetually", "", "Advanced form of 'always' — constantly, without stopping.", "My friend is perpetually worried about her examination.", "perpetually"),
    Word("Reticent", "", "Advanced form of 'quiet' — reserved, not saying much.", "Vijay is reticent about the recent issue.", "reticent"),
    Word("Notion", "", "Advanced form of 'idea' — a belief or plan.", "My friend has a notion to travel abroad.", "notion"),
    Word("Perplexing", "", "Advanced form of 'confusing' — very puzzling.", "Her idea was perplexing.", "perplexing"),
    Word("Petrified", "", "Advanced form of 'scared' — frozen with fear.", "He was petrified at the sight of a lion in front of him.", "petrified"),
    Word("Dine", "", "Advanced form of 'eat' — to eat a meal, usually formally.", "They decided to dine at a multi-cuisine restaurant.", "dine"),
    Word("Recurrently", "", "Advanced form of 'often' — happening again and again.", "He lost his job as he made mistakes recurrently.", "recurrently"),
    Word("Sporadically", "", "Advanced form of 'sometimes' — occurring at irregular intervals.", "I go to that hotel sporadically.", "sporadically"),
    Word("Amiable", "", "Advanced form of 'nice' — friendly and pleasant.", "She has a most amiable personality.", "amiable"),
    Word("Meager", "", "Advanced form of 'little' — very small in amount.", "People got meager food after the disaster.", "meager"),
    Word("Apprehensive", "", "Advanced form of 'nervous' — anxious about something.", "She felt apprehensive about presenting the seminar.", "apprensive"),
    Word("Tenuous", "", "Advanced form of 'weak' — flimsy, barely valid.", "They presented tenuous evidence.", "tenuous"),
    Word("Tardy", "", "Advanced form of 'late' — slow to arrive or act.", "The student was marked tardy for arriving after the bell.", "tardy"),
    Word("Bestow", "", "Advanced form of 'give' — to formally present something.", "The father bestowed his blessings on his newborn child.", "bestow"),
    Word("Craft", "", "Advanced form of 'make' — to skillfully create.", "She crafts beautiful jewellery from gold.", "craft"),
    Word("Lucid", "", "Advanced form of 'clear' — easy to understand.", "Her essay is lucid in style.", "lucid"),
    Word("Imminently", "", "Advanced form of 'soon' — about to happen very shortly.", "The examinations will commence imminently.", "imminently"),
    Word("Discourteous", "", "Advanced form of 'rude' — impolite.", "The guest was discourteous to all.", "discourteous"),
    Word("Engrossed", "", "Advanced form of 'busy' — fully absorbed in something.", "She was engrossed in her video call.", "engrossed"),
    Word("Surmise", "", "Advanced form of 'guess' — to conclude from limited evidence.", "I surmise he forgot to take his tablet.", "surmise"),
    Word("Cogitate", "", "Advanced form of 'think' — to think deeply.", "She cogitated on the issue deeply.", "cogitate"),
    Word("Obstinate", "", "Advanced form of 'stubborn' — unwilling to change one's mind.", "She is obstinate in negotiations.", "obsient"),
    Word("Negligent", "", "Advanced form of 'careless' — failing to take proper care.", "The security guard was negligent in his duty.", "negligence"),
    Word("Turmoil", "", "Advanced form of 'trouble' — a state of great disturbance.", "The city was in turmoil after the political rally.", "turmoil"),
    Word("Altercation", "", "Advanced form of 'fight' — a noisy argument.", "He had an altercation while bargaining.", "altercation"),
    Word("Vivid", "", "Advanced form of 'bright' — intensely colorful.", "She painted using vivid colours.", "vivid"),
    Word("Meander", "", "Advanced form of 'wander' — to move slowly with no fixed direction.", "She meandered in the hills.", "mander")
)

// ---------- Document 4: Blended Vocabulary ----------
val docWords4 = listOf(
    Word("Brunch", "", "Breakfast + Lunch.", "I had brunch at 11.00 a.m.", "doc4_brunch"),
    Word("Smog", "", "Smoke + Fog.", "The area was covered with smog after the fire.", "doc4_smog"),
    Word("Motel", "", "Motor + Hotel.", "I stayed in a motel during our trip.", "doc4_motel"),
    Word("Chortle", "", "Chuckle + Snort.", "He chortled while watching the comedy movie.", "doc4_chortle"),
    Word("Spork", "", "Spoon + Fork.", "She used a spork to eat noodles and drink soup.", "spork"),
    Word("Blog", "", "Web + Log.", "I wrote a blog about my summer vacation.", "doc4_blog"),
    Word("Infomercial", "", "Information + Commercial.", "Local channels are telecasting a 30 minute infomercial on kitchen gadgets.", "infomercial"),
    Word("Frenemy", "", "Friend + Enemy.", "Lucy and Riya are frenemies who always compete.", "doc4_frenemy"),
    Word("Chillax", "", "Chill + Relax.", "My friend wanted to chillax over the weekend.", "doc4_chillax"),
    Word("Guesstimate", "", "Guess + Estimate.", "My father gave a guesstimate of how many relatives would come.", "guesstimate"),
    Word("Workaholic", "", "Work + Alcoholic.", "Ruby is a workaholic who even works in late evenings.", "workaholic"),
    Word("Shopaholic", "", "Shop + Alcoholic.", "She is a shopaholic who goes shopping every day.", "shopaholic"),
    Word("Sitcom", "", "Situation + Comedy.", "During hard days, a sitcom will cheer you up.", "sitcom"),
    Word("Edutainment", "", "Education + Entertainment.", "The kids' podcast is a great piece of edutainment.", "edutainment"),
    Word("Podcast", "", "iPod + Broadcast.", "Nowadays, people listen to podcasts every morning.", "podcast"),
    Word("Biopic", "", "Biography + Picture.", "The biopic on the political leader was inspiring.", "biopic"),
    Word("Emoticon", "", "Emotion + Icon.", "My friend added a crying emoticon to her text message.", "emoticon"),
    Word("Staycation", "", "Stay + Vacation.", "I enjoyed a staycation exploring local places.", "staycation"),
    Word("Infotainment", "", "Information + Entertainment.", "The interview with the entrepreneur was a kind of infotainment.", "infotainment"),
    Word("Electrocute", "", "Electric + Execute.", "Two people were electrocuted when they touched the wire.", "electocute"),
    Word("Linner", "", "Lunch + Dinner.", "I missed lunch, so I had linner at 4.30 p.m.", "linner"),
    Word("Netiquette", "", "Internet + Etiquette.", "Everyone should follow netiquette while emailing.", "netiquette"),
    Word("Jeggings", "", "Jeans + Leggings.", "She likes to wear jeggings that look like jeans.", "jeggings"),
    Word("Dramedy", "", "Drama + Comedy.", "I watched an interesting dramedy on TV.", "dramedy"),
    Word("Listicle", "", "List + Article.", "I read a listicle in the newspaper.", "listicle"),
    Word("Pixel", "", "Picture + Element.", "I bought a mobile phone with a good pixel count.", "pixel"),
    Word("Interpol", "", "International + Police.", "Interpol officers helped catch the smugglers who fled abroad.", "interpol"),
    Word("Camcorder", "", "Camera + Recorder.", "The photographer used a camcorder at my daughter's wedding.", "camcorder"),
    Word("Niftastic", "", "Nifty + Fantastic.", "Your new dress is really niftastic.", "niftastic"),
    Word("Cremains", "", "Cremation + Remains.", "My mother's cremains were scattered in the river as part of the ritual.", "cremains"),
    Word("Fortnight", "", "Fourteen + Night.", "I have planned to finish my work in a fortnight.", "fortnight"),
    Word("Mockumentary", "", "Mock + Documentary.", "Students made a mockumentary about teachers for Teachers' Day.", "mockumentary"),
    Word("Nibling", "", "Niece/Nephew + Sibling.", "I visited my niblings last week.", "niblings"),
    Word("Phablet", "", "Phone + Tablet.", "She bought a new phablet.", "phablet"),
    Word("Flexitarian", "", "Flexible + Vegetarian.", "My friend is a flexitarian who eats egg sometimes.", "flexitarian")
)

// ---------- Document 5: Kitchen Vocabulary ----------
val docWords5 = listOf(
    Word("Sauté", "", "Cooking in a pan with oil over medium to high heat.", "Onions are sautéed until they turn golden brown.", "saute"),
    Word("Whisk", "", "To beat ingredients quickly to add air.", "I whisked the eggs to make an omelet.", "doc5_whisk"),
    Word("Knead", "", "To work dough by hand to develop gluten.", "She kneads wheat flour to make chapati.", "doc5_knead"),
    Word("Grate", "", "To shred food into small pieces.", "I grated coconut for chutney.", "doc5_grate"),
    Word("Mash", "", "To crush food into a soft mass.", "I gave mashed bananas to my baby.", "doc5_mash"),
    Word("Blanch", "", "To boil food briefly and then cool it quickly.", "She blanched tomatoes before peeling.", "doc5_blanch"),
    Word("Tongs", "", "A tool used to handle hot vessels or food.", "I used tongs to lift the hot utensils.", "doc5_tongs"),
    Word("Mortar and Pestle", "", "A stone tool used for grinding.", "My grandmother used a mortar and pestle to grind batter for idli.", "doc5_mortar_pestle"),
    Word("Ladle", "", "A deep spoon used to serve curry or soup.", "The chef used a ladle to serve soup into bowls.", "doc5_ladle"),
    Word("Sieve", "", "A tool used to remove lumps or solids from a liquid or powder.", "She used a sieve to remove stones from the wheat flour.", "sieve"),
    Word("Colander", "", "A bowl-shaped tool used to drain rice or vegetables.", "He used a colander to drain water from the boiled noodles.", "colander"),
    Word("Wok", "", "A deep, round frying pan (kadaai).", "I fried fish in a wok.", "wok"),
    Word("Casserole", "", "A dish used to cook and serve food.", "I prepared and served vegetable rice in a casserole.", "casserole"),
    Word("Degustation Menu", "", "A tasting menu that showcases a chef's skill.", "The newly opened restaurant offers a degustation menu.", "degustation_menu"),
    Word("Foxtail Millet", "", "A type of nutritious millet.", "Foxtail millet is used by many people for its nutritional value.", "foxtail"),
    Word("Lentils", "", "Round legumes, used to make dal.", "Lentils are widely used across India in the form of dal.", "lentils"),
    Word("Black gram", "", "A type of pulse used in South Indian cooking.", "Black gram is used to make dosa batter.", "black_gram"),
    Word("Green gram", "", "A green-colored pulse rich in vitamins.", "Green gram is used to make sprouts.", "green_gram"),
    Word("Cumin", "", "An aromatic spice.", "Cumin seeds have a strong aroma.", "cumin"),
    Word("Fennel Seeds", "", "An aromatic seed often used as a mouth freshener.", "Fennel seeds are consumed after food as a mouth freshener.", "fennel_seeds"),
    Word("Fenugreek Seeds", "", "A bitter-tasting spice used in cooking and medicine.", "Fenugreek seeds are used in cooking and medicine.", "fenugreek_seeds"),
    Word("Mustard seeds", "", "A small seed used for seasoning.", "Mustard seeds crackle in hot oil.", "mustard_seeds"),
    Word("Cloves", "", "An aromatic spice, also used medicinally.", "Cloves are used to get relief from a toothache.", "cloves"),
    Word("Cardamom", "", "A fragrant spice used in sweets and tea.", "Cardamom is used in tea for its aromatic flavour.", "cardamom"),
    Word("Cinnamon", "", "A spice made from dried tree bark.", "Cinnamon is used to make desserts.", "cinnamon"),
    Word("Parsley", "", "An aromatic leafy herb.", "Parsley is used for garnishing food.", "parsley"),
    Word("Bay leaves", "", "Aromatic leaves used to flavor food.", "Bay leaves are used while preparing biryani.", "bay_leaves"),
    Word("Poppy seeds", "", "Small seeds added for a nutty flavor.", "Poppy seeds are used to prepare curries.", "poppy_seeds"),
    Word("Asafoetida", "", "A strong-smelling spice that aids digestion.", "Asafoetida is commonly used in Indian cooking to improve digestion.", "asafoetida"),
    Word("Sesame", "", "A small, oil-rich seed used in sweets.", "Sesame is used in sweets and savouries.", "sesame")
)

// ---------- Document 6: Movement Vocabulary ----------
val docWords6 = listOf(
    Word("Wink", "", "To close and open one eye quickly, often as a signal.", "She winked at her friend.", "wink"),
    Word("Chew", "", "To crush food with the teeth before swallowing.", "My friend chews the food slowly.", "chew"),
    Word("Blink", "", "To close and open both eyes quickly.", "They blinked their eyes in surprise.", "blink"),
    Word("Blow your nose", "", "To force air through the nose to clear it.", "She blows her nose gently when she gets a cold.", "blow_your_nose"),
    Word("Sneeze", "", "To suddenly expel air through the nose and mouth.", "Raghu sneezed loudly.", "sneeze"),
    Word("Giggle", "", "To laugh lightly.", "Students giggle at funny jokes made by the teacher.", "giggle"),
    Word("Snap fingers", "", "To make a sharp sound by flicking the fingers.", "I snapped my fingers to get my sister's attention.", "snap_fingers"),
    Word("Nod", "", "To move the head up and down, often to agree.", "Students nod their heads while listening in class.", "nod"),
    Word("Cup ears", "", "To place hands behind the ears to hear better.", "He cupped his ears to hear better in a noisy place.", "cup_ear"),
    Word("Yank", "", "To pull something suddenly and hard.", "Robert yanked open the window.", "yank"),
    Word("Poke", "", "To jab someone or something with a finger.", "My friend poked me playfully.", "poke"),
    Word("Leap", "", "To jump a long way or with force.", "My friend leaped over the puddle.", "leap"),
    Word("Flick", "", "To move something with a quick, light movement.", "John flicks the fan switch.", "flick"),
    Word("Tickle", "", "To touch lightly to cause laughter.", "The child laughs when I tickle him.", "tickle"),
    Word("Shrug", "", "To raise the shoulders to show doubt or indifference.", "He shrugs his shoulders to show his disapproval.", "shrugs"),
    Word("Hop", "", "To jump on one or both feet.", "He hops on the ground.", "hop"),
    Word("Lean", "", "To bend or rest against something.", "Charles leans on his chair to rest.", "lean"),
    Word("Sigh", "", "To breathe out slowly, showing relief or sadness.", "She gives a sigh of relief.", "sigh"),
    Word("Stutter", "", "To speak with involuntary pauses or repeated sounds.", "The child stuttered while speaking to a stranger.", "stutter"),
    Word("Facepalm", "", "To cover the face with the hand out of frustration.", "He did a facepalm after realizing his mistake.", "facepalm"),
    Word("Burp", "", "To release air from the stomach through the mouth.", "He gave a burp after a heavy lunch.", "burp"),
    Word("Slurp", "", "To drink or eat noisily.", "My friend slurped the coffee loudly.", "slurp"),
    Word("Sniff", "", "To breathe in through the nose to smell something.", "The police dogs sniff to find clues.", "sniff"),
    Word("Flutter", "", "To move wings or fabric quickly and lightly.", "The butterfly fluttered its wings beautifully.", "flutter"),
    Word("Peer", "", "To look closely or carefully.", "The officials peered at the fingerprints closely.", "peer"),
    Word("Grimace", "", "To twist the face in pain or disgust.", "The baby grimaced at the sight of the injection.", "grimance"),
    Word("Pout", "", "To push out the lips, showing displeasure.", "The kid began to pout when taken to school.", "pout"),
    Word("Wince", "", "To make a slight facial expression of pain.", "James winced when he noticed his friend's wound.", "winch"),
    Word("Gargle", "", "To rinse the throat with liquid, keeping the head tilted back.", "I gargle with hot water to soothe a sore throat.", "gargle"),
    Word("Hiccup", "", "A sudden, involuntary breathing spasm.", "She ate too quickly and got hiccups.", "hiccup"),
    Word("Climb", "", "To move upward using hands and feet.", "Children climbed trees to play.", "climb"),
    Word("Crawl", "", "To move slowly on hands and knees.", "The baby crawled towards his father.", "crawl"),
    Word("Slap", "", "To hit with an open hand.", "She slapped her sister in anger.", "slap"),
    Word("Squeeze", "", "To press something firmly.", "My daughter squeezed the lemon to make fresh juice.", "squeeze"),
    Word("Squash", "", "To crush something flat.", "I squashed the tomato.", "squash"),
    Word("Crumple", "", "To crush into creases or folds.", "He crumpled the waste paper.", "crumple"),
    Word("Wring", "", "To twist and squeeze to remove liquid.", "I wring a wet towel before hanging it out to dry.", "wring"),
    Word("Spill", "", "To cause liquid to flow out accidentally.", "I spilled the milk in the kitchen.", "spill"),
    Word("Spit", "", "To force saliva out of the mouth.", "Avoid spitting in public places.", "spit"),
    Word("Slouch", "", "To sit, stand, or move with a bent, lazy posture.", "Students are slouching inside the classroom.", "slouch")
)

// ---------- Document 7: Vocab Twist (Homographs) ----------
val docWords7 = listOf(
    Word("Bat", "", "Meaning 1: A flying mammal\nMeaning 2: A stick used in sports like cricket", "I saw a bat on the tree\nI got signature of Dhoni on my cricket bat.", "vt_bat"),
    Word("Bank", "", "Meaning 1: the place where people save and borrow money\nMeaning 2: riverside", "I am going to the bank to deposit the money.\nShe went to the river bank for relaxation.", "vt_bank"),
    Word("Bark", "", "Meaning 1: Sound of a dog\nMeaning 2: outer layer of the tree.", "The dog barks at the thief.\nPriya carved her name on the bark of the tree.", "vt_bark"),
    Word("Bow", "", "Meaning 1: To bend forward\nMeaning 2: a weapon", "The actors bowed to the audience after their performance.\nThe hunter aimed the bow at the deer.", "vt_bow"),
    Word("Fair", "", "Meaning 1: something is right morally\nMeaning 2: exhibition", "It is not fair to be partial towards any particular student.\nThere is a job fair in our campus today.", "vt_fair"),
    Word("Jam", "", "Meaning 1: A sweet fruit spread\nMeaning 2: To get stuck or blocked", "I love strawberry jam on toast.\nThe traffic jam made me late for work.", "vt_jam"),
    Word("Lead", "", "Meaning 1: To guide or direct\nMeaning 2: A type of metal", "She will lead the team in the project.\nPipes were made of lead in old houses.", "vt_lead"),
    Word("Match", "", "Meaning 1: A contest or game\nMeaning 2: A stick used to start fire", "The football match was very exciting.\nHe struck a match to light the candle.", "vt_match"),
    Word("Spring", "", "Meaning 1: The season after winter\nMeaning 2: A coiled device that can stretch or jump back", "Flowers bloom in spring.\nThe mattress has a broken spring inside.", "vt_spring"),
    Word("Tire", "", "Meaning 1: To feel exhausted\nMeaning 2: The rubber covering of a wheel", "Long walks tire me out.\nThe car needs a new tire.", "vt_tire"),
    Word("Current", "", "Meaning 1: Flow of electricity\nMeaning 2: present", "He is not satisfied with his current job.", "vt_current"),
    Word("Minute", "", "Meaning 1: Unit of time\nMeaning 2: Small", "Wait a minute, please.\nThe insect was minute, barely visible.", "vt_minute"),
    Word("Scale", "", "Meaning 1: The outer covering of fish\nMeaning 2: system used to measure", "The fish's scale shimmered in the sun.\nWe used a scale to weigh the vegetables.", "vt_scale"),
    Word("Clip", "", "Meaning 1: to cut\nMeaning 2: a small device", "He clipped the hedge neatly.\nShe used a clip to hold her hair.", "vt_clip"),
    Word("Bolt", "", "Meaning 1: to run away\nMeaning 2: lock", "The horse bolted at the sound of thunder.\nHe slid the bolt to lock the gate.", "vt_bolt"),
    Word("Charge", "", "Meaning 1: to ask a price\nMeaning 2: to rush", "The shop charges extra for delivery.\nThe bull charged at the fence.", "vt_charge"),
    Word("Spring (device)", "", "Meaning 1: season\nMeaning 2: jump", "Flowers bloom in spring.\nThe mattress has a broken spring inside.", "vt_spring2"),
    Word("Pitch", "", "Meaning 1: Tone\nMeaning 2: Throw", "Her voice has a high pitch.\nHe pitched the ball to the batsman.", "vt_pitch"),
    Word("Console", "", "Meaning 1: to comfort\nMeaning 2: a control panel", "She tried to console her crying friend.\nHe pressed the buttons on the game console.", "vt_console"),
    Word("Pound", "", "Meaning 1: to hit\nMeaning 2: weight", "He pounded on the door.\nThe parcel weighs two pounds.", "vt_pound"),
    Word("Suspend", "", "Meaning 1: to hand something from above\nMeaning 2: to stop something", "The lamp was suspended from the ceiling.\nThe match was suspended due to rain.", "vt_suspend"),
    Word("Plot", "", "Meaning 1: a secret plan\nMeaning 2: a piece of land", "The villains hatched a plot.\nThey bought a plot to build a house.", "vt_plot"),
    Word("Monitor", "", "Meaning 1: to observe carefully\nMeaning 2: a screen", "Teachers monitor the students during exams.\nHe bought a new computer monitor.", "vt_monitor"),
    Word("Anchor", "", "Meaning 1: an object which keeps ship in place\nMeaning 2: a new presenter", "The ship dropped its anchor.\nShe works as a news anchor.", "vt_anchor"),
    Word("Appraise", "", "Meaning 1: to evaluate the value of something\nMeaning 2: to judge the quality of", "The jeweler appraised the necklace.\nThe manager appraised his employee's work.", "vt_appraise"),
    Word("Abstract", "", "Meaning 1: Existing in thought but not physical\nMeaning 2: to remove", "Freedom is an abstract idea.\nThe chemist abstracted the sample for testing.", "vt_abstract_img"),
    Word("Racket", "", "Meaning 1: disturbing noise\nMeaning 2: dishonest occupation", "The children made a racket upstairs.\nThe police busted an illegal racket.", "vt_racket"),
    Word("Vault", "", "Meaning 1: a secured room\nMeaning 2: to jump over something", "The gold was kept in a bank vault.\nThe athlete vaulted over the bar.", "vt_vault"),
    Word("Alight", "", "Meaning 1: to descend\nMeaning 2: to be on fire", "Passengers alight at the next stop.\nThe dry grass was alight within seconds.", "vt_alight"),
    Word("Annotate", "", "Meaning 1: to add something\nMeaning 2: to explain", "She annotated the textbook with her own notes.\nThe teacher annotated the essay with feedback.", "annotated"),
    Word("Conflate", "", "Meaning 1: to combine\nMeaning 2: to confuse", "The report conflates two separate issues.\nPeople often conflate the two festivals.", "vt_conflate"),
    Word("Cower", "", "Meaning 1: to shrink physically\nMeaning 2: express fear", "The dog cowered during the storm.\nHe cowered before his angry boss.", "vt_cower"),
    Word("Dissipate", "", "Meaning 1: to scatter\nMeaning 2: to waste", "The morning fog dissipated by noon.\nHe dissipated his fortune on gambling.", "vt_dissipate"),
    Word("Enervate", "", "Meaning 1: to weaken\nMeaning 2: to tire", "The long illness enervated him.\nThe heat enervated the entire team.", "vt_enervate"),
    Word("Connive", "", "Meaning 1: to secretly cooperate\nMeaning 2: to ignore wrongdoing", "The two officials connived to hide the fraud.\nThe guard connived at the theft.", "vt_convive")
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
// Each Word entry = one TED Talk video card
// word      = video title
// example   = full YouTube URL (used to open in browser)
// imageUrl  = YouTube video ID (used to build thumbnail URL)
val docWords12 = listOf(
    Word("Built Your Own Framework", "", "TED Talk · Video 1",  "https://youtu.be/xEGTC5jGI94?si=wT-2sdL6Kum8AHR0",  "xEGTC5jGI94"),
    Word("The Power of Believing You Can Improve", "", "TED Talk · Video 2",  "https://youtu.be/sB34sRehUvU?si=FV2tY33A51Mhuzi9",  "sB34sRehUvU"),
    Word("How to Speak So That People Want to Listen", "", "TED Talk · Video 3",  "https://youtu.be/6LZ7QqoY_1w?si=edlnGRTvrA0VlDV-",  "6LZ7QqoY_1w"),
    Word("Inside the Mind of a Master Procrastinator", "", "TED Talk · Video 4",  "https://youtu.be/TIwBwyMgS50?si=OTv0_iwVKvGMtIE8",  "TIwBwyMgS50"),
    Word("The Puzzle of Motivation", "", "TED Talk · Video 5",  "https://youtu.be/bC0hlK7WGcM?si=63z0xt-wfLaQyvI1",  "bC0hlK7WGcM"),
    Word("How Great Leaders Inspire Action", "", "TED Talk · Video 6",  "https://youtu.be/8ZhWojQnHls?si=mfVTj_xx8sfEASAH",  "8ZhWojQnHls"),
    Word("Your Body Language May Shape Who You Are", "", "TED Talk · Video 7",  "https://youtu.be/0NV1KdWRHck?si=ro4b5JvIwVDYp1Hp",  "0NV1KdWRHck"),
    Word("The Happy Secret to Better Work", "", "TED Talk · Video 8",  "https://youtu.be/lhv72TsRvHU?si=9S4_Mo8ClD_dF1A5",  "lhv72TsRvHU"),
    Word("How to Make Stress Your Friend", "", "TED Talk · Video 9",  "https://youtu.be/TFbv757kup4?si=47JfXz3ns_PcoxYd",  "TFbv757kup4"),
    Word("The Art of Being Yourself", "", "TED Talk · Video 10", "https://youtu.be/o_XVt5rdpFY?si=gpR0T30IPjKYOOMP", "o_XVt5rdpFY")
)

// ---------- Document 13: Stories ----------
// Each Word entry = one Story video card
// word      = story title
// example   = full YouTube URL (used to open in browser)
// imageUrl  = YouTube video ID (used to build thumbnail URL)
val docWords13 = listOf(
    Word("A Girl & Vampire",       "", "Story · Video 1", "https://youtu.be/IvxUmOQsEOA?si=0i79RL_7QxQGieVq", "IvxUmOQsEOA"),
    Word("The Honest Woodcutter",  "", "Story · Video 2", "https://youtu.be/oTimWHAoawU?si=VqvchBDW3gd5aA8Y", "oTimWHAoawU"),
    Word("The Lion and the Mouse", "", "Story · Video 3", "https://youtu.be/1VHTnrutwAo?si=Y92JjOBscDpVIfUC", "1VHTnrutwAo"),
    Word("The Clever Fox",         "", "Story · Video 4", "https://youtu.be/VkJ0IcJ3EpA?si=mOG1eDn2goa8t2_G", "VkJ0IcJ3EpA"),
    Word("The Greedy Dog",         "", "Story · Video 5", "https://youtu.be/Wygb93WOHAo?si=hnb5wXVM6x1GE_4r", "Wygb93WOHAo"),
    Word("The Thirsty Crow",       "", "Story · Video 6", "https://youtu.be/ieFWfWtKmTc?si=eUFdk_gC2n4kBi1K", "ieFWfWtKmTc"),
    Word("The Golden Goose",       "", "Story · Video 7", "https://youtu.be/TbmSCdn_iUo?si=CaTIpweWQ5Bwi5_G", "TbmSCdn_iUo"),
    Word("A Life-Changing Motivational Story | The Courage to Walk Away | Learn English with Stories", "", "Story · Video 8",  "https://youtu.be/JrvJuwIbwvc?si=IJKCrm17HcFc3wqZ",  "JrvJuwIbwvc"),
    Word("The Power of Positive Thinking | A Story That Will Change Your Mindset | Motivational Story",  "", "Story · Video 9",  "https://youtu.be/NBQKIS2CRlc?si=QMWVbbkRQgafZ4am",  "NBQKIS2CRlc"),
    Word("Learn English through stories || The red Book English story || Improve your English || Graded reader", "", "Story · Video 10", "https://youtu.be/W3KyG2xMX0Y?si=LLOtaJGJqghPCXcA", "W3KyG2xMX0Y")
)

// ---------- Document 14: Podcast Videos ----------
// Each Word entry = one Podcast video card
// word      = podcast title
// example   = full YouTube URL (used to open in browser)
// imageUrl  = YouTube video ID (used to build thumbnail URL)
val docWords14 = listOf(
    Word("How to Improve Your English Speaking Skills", "", "Podcast · Video 1",  "https://youtu.be/ZDAoStA38r8?si=MLNanDb7tj9s9CpR", "ZDAoStA38r8"),
    Word("Learn English Naturally Through Podcasts",   "", "Podcast · Video 2",  "https://youtu.be/aiUGN3TDvw4?si=3Mg_8d0d9wiem7AM", "aiUGN3TDvw4"),
    Word("English Listening Practice for Beginners",  "", "Podcast · Video 3",  "https://youtu.be/31y2Bq1RYQA?si=RatuwQdTUc44BIJW", "31y2Bq1RYQA"),
    Word("Speak English Fluently — Daily Podcast",    "", "Podcast · Video 4",  "https://youtu.be/I_tRSrPru94?si=4R-gInm6WCQ6oOU4", "I_tRSrPru94"),
    Word("Advanced English Conversations",            "", "Podcast · Video 5",  "https://youtu.be/bq6GBbh3uhU?si=zIOAm1kXGU0_FAKI", "bq6GBbh3uhU"),
    Word("English Podcast for Intermediate Learners", "", "Podcast · Video 6",  "https://youtu.be/-SHjj68WaLs?si=HAcCGC2CQE4rjdF1", "-SHjj68WaLs"),
    Word("Master English Pronunciation",             "", "Podcast · Video 7",  "https://youtu.be/0y185Tz39lk?si=CSSvbbR42WHkOwyS", "0y185Tz39lk"),
    Word("Real English Conversations",               "", "Podcast · Video 8",  "https://youtu.be/Y__k1dLo-eo?si=Iba2lEOPcaGANSIL", "Y__k1dLo-eo"),
    Word("English Vocabulary Building Podcast",      "", "Podcast · Video 9",  "https://youtu.be/eabT4z25qxE?si=6bvgEAEYbKidGipj", "eabT4z25qxE"),
    Word("Listen & Learn English Daily",             "", "Podcast · Video 10", "https://youtu.be/3Mr4mGMALwI?si=FcZn5gCg1S-QoJub", "3Mr4mGMALwI")
)


// ---------- Pilot Quiz Words (All 25 Words with Images) ----------
val docWordsPilot = listOf(
    Word("Swift", "", "Moving or happening quickly.", "The police took swift action to arrest the thief.", "swift"),
    Word("Drowsy", "", "Feeling sleepy and sluggish.", "I felt drowsy after the heavy dinner.", "drowsy"),
    Word("Evade", "", "To escape or avoid something cleverly.", "The deer managed to evade the hunter.", "evade"),
    Word("Ineffable", "", "Too great or extreme to be expressed in words.", "The mountain's beauty was truly ineffable.", "ineffable"),
    Word("Jubilant", "", "Feeling or expressing great happiness.", "She was jubilant after securing first rank in the exam.", "jubilant"),
    Word("Limerence", "", "A feeling of obsessive desire or infatuation for someone.", "His limerence grew after watching all his movies.", "limerance"),
    Word("Querencia", "", "A place where you feel safe, at home, and comfortable.", "Her grandmother's house was her querencia.", "querencia"),
    Word("Smog", "", "Smoke mixed with fog — a type of air pollution.", "Smoke mixed with fog is known as smog.", "smog"),
    Word("Brunch", "", "A meal eaten in late morning — Breakfast + Lunch.", "\"Breakfast + Lunch\" forms the word brunch.", "brunch"),
    Word("Frenemy", "", "A person who is a friend but also behaves like an enemy.", "A friend who behaves like an enemy is called a frenemy.", "frenemy"),
    Word("Whisk", "", "To beat ingredients quickly to add air.", "She whisked the eggs to make an omelet.", "whisk"),
    Word("Grate", "", "To shred food into small pieces using a grater.", "I grated coconut for the chutney.", "grate"),
    Word("Ladle", "", "A large spoon with a long handle used to serve soup.", "The chef used a ladle to serve soup.", "ladle"),
    Word("Mortar & Pestle", "", "A bowl and club used to grind spices and ingredients.", "My grandmother used a mortar and pestle to grind spices.", "mortar_and_pestle"),
    Word("Blanch", "", "To briefly boil food then cool it quickly in cold water.", "She blanched tomatoes before peeling them.", "blanch"),
    Word("Yank", "", "To pull something suddenly and with force.", "Robert suddenly pulled the window open — he yanked it.", "yank"),
    Word("Tickle", "", "To touch lightly so as to cause laughter.", "The child laughed when I tickled him.", "tickle"),
    Word("Nod", "", "To move the head up and down to show agreement.", "Students nod their heads while listening to the teacher.", "nod"),
    Word("Flick", "", "To move something with a quick, light motion.", "John flicked the fan switch.", "flick"),
    Word("Stutter", "", "To speak with sudden stops or repeated sounds.", "The baby stuttered while speaking to the stranger.", "stutter"),
    Word("Colossal", "", "Extremely large or great in size — advanced form of 'big'.", "The museum exhibited a colossal statue of extraordinary size.", "collasal"),
    Word("Myriad", "", "A countless or extremely great number — advanced form of 'many'.", "The professor offered a myriad of useful information.", "many"),
    Word("Bat", "", "Meaning 1: A flying mammal\nMeaning 2: A stick used in sports like cricket", "I saw a bat on the tree.\nI got signature of Dhoni on my cricket bat.", "vt_bat"),
    Word("Bank", "", "Meaning 1: A place to save and borrow money\nMeaning 2: Land alongside a river", "I am going to the bank to deposit the money.\nShe went to the river bank for relaxation.", "vt_bank"),
    Word("Sauté", "", "Cooking in a pan with oil over medium to high heat.", "Onions are sautéed until they turn golden brown.", "saute")
)

val sampleLearnCategory = Category(
    id = "doc0",
    title = "Sample Learn",
    description = "Practice with photos for all 25 quiz words",
    color = VibrantBlue,
    icon = Icons.AutoMirrored.Filled.MenuBook,
    words = docWordsPilot
)

val docCategories = listOf(
    sampleLearnCategory,
    Category("doc1", "Advanced Vocabulary", "Rich, expressive words for advanced learners", VibrantPurple, Icons.Default.AutoStories, docWords1),
    Category("doc2", "Basic Vocabulary", "Everyday words to build a strong foundation", VibrantTeal, Icons.AutoMirrored.Filled.MenuBook, docWords2),
    Category("doc3", "Basic vs Advanced", "Simple words paired with their advanced equivalents", VibrantOrange, Icons.AutoMirrored.Filled.CompareArrows, docWords3),
    Category("doc4", "Blended Words", "Portmanteau words formed by blending two words", VibrantPink, Icons.Default.Shuffle, docWords4),
    Category("doc5", "Kitchen Vocabulary", "Cooking tools, spices, and kitchen terms", VibrantGreen, Icons.Default.Kitchen, docWords5),
    Category("doc6", "Movement Vocabulary", "Words describing small actions and gestures", VibrantBlue, Icons.AutoMirrored.Filled.DirectionsRun, docWords6),
    Category("doc7", "Vocab Twist", "Homographs — one word, multiple meanings", VibrantYellow, Icons.Default.SwapHoriz, docWords7),
    Category("doc8", "Types of Eating", "Different ways to eat", VibrantPink, Icons.Default.Restaurant, docWords8),
    Category("doc9", "Types of LSRW and Looking", "Listening, Speaking, Reading, Writing, Looking", VibrantBlue, Icons.Default.Visibility, docWords9),
    Category("doc10", "Types of Walking", "Different ways of walking", VibrantPurple, Icons.AutoMirrored.Filled.DirectionsWalk, docWords10),
    Category("doc11", "Types of Weather", "Various weather conditions", VibrantOrange, Icons.Default.WbSunny, docWords11),
    Category("doc12", "Ted Talks", "Inspiring Ted Talk videos", VibrantGreen, Icons.Default.VideoLibrary, docWords12),
    Category("doc13", "Stories", "Engaging story videos", VibrantOrange, Icons.AutoMirrored.Filled.MenuBook, docWords13),
    Category("doc14", "Podcast Videos", "Interesting podcast videos", VibrantBlue, Icons.Default.Headphones, docWords14)
)
