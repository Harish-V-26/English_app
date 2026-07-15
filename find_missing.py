import re

with open('app/src/main/java/com/example/english_app/ui/VocabularyDocs.kt', 'r', encoding='utf-8') as f:
    s = f.read()

def get_words(doc_name):
    # Find the start of the list
    start_idx = s.find(f'val {doc_name}')
    if start_idx == -1: return []
    # Find the end of the list (next 'val docWords' or end of file)
    end_idx = s.find('val docWords', start_idx + 1)
    if end_idx == -1: end_idx = len(s)
    
    chunk = s[start_idx:end_idx]
    items = re.findall(r'Word\(\"([^\"]+)\"', chunk)
    return items

docs = {
    'Adv': 'docWords1',
    'Basic': 'docWords2',
    'Basic vs adv': 'docWords3',
    'Confused': 'docWords4',
    'Kitchen': 'docWords5',
    'Noun/Verb': 'docWords6',
    'Movement': 'docWords7'
}

for name, doc_name in docs.items():
    words = get_words(doc_name)
    print(f'{name} ({doc_name}): {len(words)} words')
    if name == 'Movement':
        if len(words) >= 28: print(f"  28: {words[27]}")
    if name == 'Basic vs adv':
        if len(words) >= 30: print(f"  30: {words[29]}, 5: {words[4]}, 2: {words[1]}")
    if name == 'Adv':
        if len(words) >= 34: print(f"  27: {words[26]}, 34: {words[33]}")
    if name == 'Kitchen':
        if len(words) >= 15: print(f"  1: {words[0]}, 15: {words[14]}")
