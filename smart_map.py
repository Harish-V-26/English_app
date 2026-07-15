import re
import os
import difflib

DRAWABLE_DIR = r"app/src/main/res/drawable"
valid_extensions = {".png", ".jpg", ".jpeg", ".webp", ".gif"}
image_files = [os.path.splitext(f)[0] for f in os.listdir(DRAWABLE_DIR) if os.path.splitext(f)[1].lower() in valid_extensions]

with open('original_vocab.kt', 'r', encoding='utf-16le') as f:
    lines = f.readlines()

def normalize_word(w):
    w = w.lower().strip()
    w = re.sub(r'[^a-z0-9]', '_', w)
    w = re.sub(r'_+', '_', w)
    return w

def find_best_image(word, image_list):
    norm = normalize_word(word)
    
    if norm in image_list: return norm
    
    manual_fixes = {
        'limerence': 'limerance',
        'brick_kiln': 'brick_klins',
        'colossal': 'collasal',
        'apprehensive': 'apprensive',
        'negligent': 'negligence',
        'saute': 'saute'
    }
    if norm in manual_fixes and manual_fixes[norm] in image_list:
        return manual_fixes[norm]
        
    for img in image_list:
        if img.endswith('_' + norm):
            return img
            
    matches = difflib.get_close_matches(norm, image_list, n=1, cutoff=0.9)
    if matches:
        return matches[0]
        
    return None

new_lines = []
mapped_count = 0
unmapped_count = 0

for line in lines:
    # Match: Word("word", "phon", "def", "ex" [optional: , "img"])
    m = re.match(r'^(\s*Word\()\"([^\"]*)\"\s*,\s*\"([^\"]*)\"\s*,\s*\"([^\"]*)\"\s*,\s*\"([^\"]*)\"(.*?)\)(.*)$', line)
    if m:
        prefix = m.group(1)
        w_word = m.group(2)
        w_phon = m.group(3)
        w_def = m.group(4)
        w_ex = m.group(5)
        rest = m.group(6) # e.g. `, "doc1_halcyon"` or empty space
        suffix = m.group(7) # usually `,` or just newline
        
        assigned_img = None
        
        # Check if rest has an image
        m_img = re.search(r'\"([^\"]+)\"', rest)
        if m_img:
            old_img = m_img.group(1)
            valid_img = normalize_word(old_img)
            if valid_img in image_files:
                assigned_img = valid_img
            else:
                assigned_img = find_best_image(w_word, image_files)
        else:
            assigned_img = find_best_image(w_word, image_files)
            
        if assigned_img:
            new_line = f'{prefix}"{w_word}", "{w_phon}", "{w_def}", "{w_ex}", "{assigned_img}"){suffix}\n'
            mapped_count += 1
        else:
            new_line = f'{prefix}"{w_word}", "{w_phon}", "{w_def}", "{w_ex}"){suffix}\n'
            unmapped_count += 1
            
        new_lines.append(new_line)
    else:
        new_lines.append(line)

with open('app/src/main/java/com/example/english_app/ui/VocabularyDocs.kt', 'w', encoding='utf-8') as f:
    f.writelines(new_lines)

print(f"Mapped {mapped_count} words to images. Left {unmapped_count} without images.")
