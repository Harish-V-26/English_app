import os
import re
import difflib
import shutil

DRAWABLE_DIR = r"app/src/main/res/drawable"
VOCAB_DOCS = r"app/src/main/java/com/example/english_app/ui/VocabularyDocs.kt"
CAROUSEL = r"app/src/main/java/com/example/english_app/ui/CarouselScreen.kt"

java_reserved = {
    "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
    "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
    "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
    "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp",
    "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void",
    "volatile", "while", "true", "false", "null"
}

valid_extensions = {".png", ".jpg", ".jpeg", ".webp", ".gif"}
image_files = [f for f in os.listdir(DRAWABLE_DIR) if os.path.splitext(f)[1].lower() in valid_extensions]

new_image_names = []
renamed_count = 0
for f in image_files:
    basename, ext = os.path.splitext(f)
    
    # We already converted to lowercase in the last script, but let's double check reserved words
    if basename in java_reserved:
        new_basename = basename + "_img"
        old_path = os.path.join(DRAWABLE_DIR, f)
        new_path = os.path.join(DRAWABLE_DIR, new_basename + ext)
        os.rename(old_path, new_path)
        new_image_names.append(new_basename)
        renamed_count += 1
    else:
        new_image_names.append(basename)

image_names = list(set(new_image_names)) # unique

# We need to remap again
def normalize_word(word):
    w = word.lower().strip()
    w = re.sub(r'[^a-z0-9]', '_', w)
    w = re.sub(r'_+', '_', w)
    return w

def find_best_image(word):
    norm = normalize_word(word)
    # Check if this word's exact norm is now appended with _img due to reserved keyword
    if norm + "_img" in image_names:
        return norm + "_img"
        
    for img in image_names:
        if img == norm:
            return img
    for img in image_names:
        if img.endswith("_" + norm):
            return img
    matches = difflib.get_close_matches(norm, image_names, n=1, cutoff=0.8)
    if matches:
        return matches[0]
    for img in image_names:
        if norm in img or img in norm:
            if len(img) > 4: 
                return img
    return None

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
            new_line = f'    Word("{word_name}", "{phonetics}", "{definition}", "{example}", "{best_img}"){rest}\n'
            new_lines.append(new_line)
        else:
            new_lines.append(line)
    else:
        new_lines.append(line)

with open(VOCAB_DOCS, "w", encoding="utf-8") as f:
    f.writelines(new_lines)

with open(CAROUSEL, "r", encoding="utf-8") as f:
    carousel_content = f.read()

when_branches = []
for img in sorted(image_names):
    when_branches.append(f'        "{img}" -> R.drawable.{img}')
when_branches.append('        else -> R.drawable.ic_placeholder_word')

when_block = "    return when (imageName) {\n" + "\n".join(when_branches) + "\n    }"

carousel_content = re.sub(
    r'    return when \(imageName\) \{.*?\n    \}',
    when_block.replace('\\', '\\\\'),
    carousel_content,
    flags=re.DOTALL
)

with open(CAROUSEL, "w", encoding="utf-8") as f:
    f.write(carousel_content)

print(f"Renamed {renamed_count} reserved keyword files and updated Kotlin code.")
