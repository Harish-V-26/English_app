import re
s = open('app/src/main/java/com/example/english_app/ui/VocabularyDocs.kt', encoding='utf-8').read()
match = re.search(r'val docWords7.*?listOf\((.*?)\)\n\n', s, re.DOTALL)
if not match: match = re.search(r'val docWords7.*?listOf\((.*)\)', s, re.DOTALL)
items = re.findall(r'Word\(\"([^\"]+)\"', match.group(1))
print(items[27] if len(items)>27 else 'not long enough')
