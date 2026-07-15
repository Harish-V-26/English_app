import re
import os

# 1. Read original vocab
with open('original_vocab.kt', 'r', encoding='utf-16le') as f:
    lines = f.readlines()

new_lines = []
for line in lines:
    # Match Word(..., ..., ..., ..., "image_name"...)
    # We will use a simpler approach: just find all string literals in the line
    m = re.match(r'^(\s*Word\()(.*)(\).*)$', line)
    if m:
        prefix = m.group(1)
        args_str = m.group(2)
        suffix = m.group(3)
        
        # Split args by comma, but be careful with commas inside strings.
        # Simple split using regex for string literals:
        args = re.findall(r'\"(?:[^\"]*)\"|[^,]+', args_str)
        args = [a.strip() for a in args]
        
        # If there are at least 5 arguments, the 5th one is the image (index 4)
        if len(args) >= 5 and args[4].startswith('"'):
            old_img_raw = args[4].strip('"')
            
            # Normalize the image name just like we did to the files
            valid_img = old_img_raw.lower().strip()
            valid_img = re.sub(r'[^a-z0-9]', '_', valid_img)
            valid_img = re.sub(r'_+', '_', valid_img)
            
            # Reconstruct args
            args[4] = f'"{valid_img}"'
            
        new_args_str = ", ".join(args)
        new_lines.append(f"{prefix}{new_args_str}{suffix}\n")
    else:
        new_lines.append(line)

with open('app/src/main/java/com/example/english_app/ui/VocabularyDocs.kt', 'w', encoding='utf-8') as f:
    f.writelines(new_lines)

print("Restored original image mappings with correct Android naming convention!")
