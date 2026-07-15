import re
s = open('app/src/main/java/com/example/english_app/ui/VocabularyDocs.kt', encoding='utf-8').read()
m = re.search(r'Word\("Alight"[^\)]+\)', s)
print(m.group(0) if m else 'not found')
