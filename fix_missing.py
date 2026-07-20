import re

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

with open('app/src/main/java/com/example/english_app/ui/VocabularyDocs.kt', 'r', encoding='utf-8') as f:
    lines = f.readlines()

new_lines = []
for line in lines:
    replaced = False
    for word, img in fixes.items():
        if f'Word("{word}"' in line:
            # Check if it already has 5th argument
            m = re.match(r'(\s*Word\([^)]+?\))(.*)', line)
            # Actually just replace the whole line carefully using ast or regex
            # It's safer to just do a string replace if we know the exact line format.
            # E.g. Word("Consecration", "", "...", "...") -> Word("Consecration", "", "...", "...", "img")
            
            # Find the last quote before the closing parenthesis
            parts = line.rsplit('"', 2)
            # parts will be like: ['    Word("Consecration", "", "def", ', 'ex', '),\n']
            # if it already has an image, the last part is the image string.
            # let's just do a simple regex:
            pass
            
    if not replaced:
        new_lines.append(line)

