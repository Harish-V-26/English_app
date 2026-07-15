import os
import re
import difflib

DRAWABLE_DIR = r"app/src/main/res/drawable"
VOCAB_DOCS = r"app/src/main/java/com/example/english_app/ui/VocabularyDocs.kt"
CAROUSEL = r"app/src/main/java/com/example/english_app/ui/CarouselScreen.kt"

# 1. Get all image base names
valid_extensions = {".png", ".jpg", ".webp"}
image_files = [f for f in os.listdir(DRAWABLE_DIR) if os.path.splitext(f)[1].lower() in valid_extensions]
image_names = [os.path.splitext(f)[0] for f in image_files]

def normalize_word(word):
    # e.g., "Brick kiln" -> "brick_kiln", "Blow your nose" -> "blow_your_nose"
    w = word.lower().strip()
    w = re.sub(r'[^a-z0-9]', '_', w)
    w = re.sub(r'_+', '_', w)
    return w

def find_best_image(word):
    norm = normalize_word(word)
    # Exact match first
    for img in image_names:
        if img == norm:
            return img
    # Match with prefix like doc1_, doc4_, doc5_
    for img in image_names:
        if img.endswith("_" + norm):
            return img
    # Handle plural / slight typos (like brick_kiln -> brick_klins)
    matches = difflib.get_close_matches(norm, image_names, n=1, cutoff=0.8)
    if matches:
        return matches[0]
    # For phrases, maybe we can check if it's contained
    for img in image_names:
        if norm in img or img in norm:
            if len(img) > 4:  # avoid tiny false positives
                return img
    return None

# 2. Update VocabularyDocs.kt
with open(VOCAB_DOCS, "r", encoding="utf-8") as f:
    lines = f.readlines()

new_lines = []
for line in lines:
    m = re.match(r'(\s*Word\("([^"]+)",\s*"([^"]*)",\s*"([^"]*)",\s*"([^"]*)"(?:,\s*"([^"]*)")?\))(.*)', line)
    if m:
        original = m.group(1)
        word_name = m.group(2)
        phonetics = m.group(3)
        definition = m.group(4)
        example = m.group(5)
        existing_img = m.group(6)
        rest = m.group(7)
        
        best_img = find_best_image(word_name)
        if best_img:
            # Reconstruct the line
            new_line = f'    Word("{word_name}", "{phonetics}", "{definition}", "{example}", "{best_img}"){rest}\n'
            new_lines.append(new_line)
        else:
            new_lines.append(line)
    else:
        new_lines.append(line)

with open(VOCAB_DOCS, "w", encoding="utf-8") as f:
    f.writelines(new_lines)

# 3. Update CarouselScreen.kt
with open(CAROUSEL, "r", encoding="utf-8") as f:
    carousel_content = f.read()

# Generate the massive when block for getImageResId
when_branches = []
for img in sorted(image_names):
    when_branches.append(f'        "{img}" -> R.drawable.{img}')
when_branches.append('        else -> R.drawable.ic_placeholder_word')

when_block = "    return when (imageName) {\n" + "\n".join(when_branches) + "\n    }"

# Replace the existing function body
carousel_content = re.sub(
    r'    return when \(imageName\) \{.*?\n    \}',
    when_block.replace('\\', '\\\\'),
    carousel_content,
    flags=re.DOTALL
)

with open(CAROUSEL, "w", encoding="utf-8") as f:
    f.write(carousel_content)

print(f"Updated VocabularyDocs.kt and CarouselScreen.kt with {len(image_names)} images.")
