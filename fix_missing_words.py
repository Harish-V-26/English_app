import re
import os

fixes = {
    'Consecration': 'consecration_ceremony',
    'Broiling day': 'broiling',
    'Obstinate': 'obsient',
    'Amble': 'ambel',
    'Myriad': 'many',
    'Sauté': 'saute',
    'Foxtail Millet': 'foxtail',
    'Alight': 'alight'
}

vocab_path = r'app/src/main/java/com/example/english_app/ui/VocabularyDocs.kt'
with open(vocab_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()

new_lines = []
for line in lines:
    replaced = False
    for word, img in fixes.items():
        if f'Word("{word}"' in line:
            # Check if it already has the 5th parameter.
            # Usually: Word("Amble", "", "def", "ex")
            # Or: Word("Amble", "", "def", "ex", "something")
            # We will just replace everything after the 4th argument.
            m = re.match(r'(\s*Word\([^"]+"[^"]+"[^"]+"[^"]+"[^"]+"[^"]+"[^"]+"[^"]+")(?:,\s*"[^"]*")?\)(.*)', line)
            if m:
                # m.group(1) is up to the end of the 4th argument (example string)
                # Wait, Word("Word", "Phonetics", "Definition", "Example") -> 4 arguments.
                # That's 8 quotes.
                new_line = m.group(1) + f', "{img}")' + m.group(2) + '\n'
                new_lines.append(new_line)
                replaced = True
                print(f"Fixed {word} -> {img}")
                break
    if not replaced:
        new_lines.append(line)

with open(vocab_path, 'w', encoding='utf-8') as f:
    f.writelines(new_lines)

# Also ensure CarouselScreen.kt has them all in the when block
carousel_path = r'app/src/main/java/com/example/english_app/ui/CarouselScreen.kt'
with open(carousel_path, 'r', encoding='utf-8') as f:
    c_content = f.read()

# We already ran update_carousel.py in the previous step, so all images from res/drawable
# should be in CarouselScreen.kt. Let's just make sure.
