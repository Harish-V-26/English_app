import re
import difflib

f = open('app/src/main/java/com/example/english_app/ui/VocabularyDocs.kt','r',encoding='utf-8')
lines = f.readlines()
f.close()

for line in lines:
    m = re.match(r'\s*Word\("([^"]+)",\s*"([^"]*)",\s*"([^"]*)",\s*"([^"]*)",\s*"([^"]*)"(?:,\s*"([^"]*)")?\)', line)
    if m:
        word = m.group(1).lower().strip()
        # The image is the 5th group if no 6th group, but actually the regex above might capture 6 groups if present
        # Wait, the earlier script regex was:
        # \s*Word\("([^"]+)",\s*"([^"]*)",\s*"([^"]*)",\s*"([^"]*)",\s*"([^"]*)"(?:,\s*"([^"]*)")?\)
        # 1=word, 2=phonetics, 3=def, 4=ex, 5=img. 
        img = m.group(5).lower().strip()
        
        word_norm = re.sub(r'[^a-z0-9]', '_', word)
        word_norm = re.sub(r'_+', '_', word_norm)
        
        if img == word_norm:
            pass
        elif img.endswith('_' + word_norm):
            pass
        elif word_norm in img or img in word_norm:
            if len(img) > 4:
                pass
            else:
                print(f'SUSPICIOUS (contains short): {word} -> {img}')
        else:
            score = difflib.SequenceMatcher(None, word_norm, img).ratio()
            if score < 0.9:
                print(f'SUSPICIOUS ({score:.2f}): {word} -> {img}')
