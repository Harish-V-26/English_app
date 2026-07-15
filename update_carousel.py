import os
import re

DRAWABLE_DIR = r'app/src/main/res/drawable'
CAROUSEL = r'app/src/main/java/com/example/english_app/ui/CarouselScreen.kt'

valid_extensions = {'.png', '.jpg', '.jpeg', '.webp', '.gif'}
image_files = [f for f in os.listdir(DRAWABLE_DIR) if os.path.splitext(f)[1].lower() in valid_extensions]

image_names = sorted(list(set([os.path.splitext(f)[0] for f in image_files])))

with open(CAROUSEL, 'r', encoding='utf-8') as f:
    carousel_content = f.read()

when_branches = []
for img in image_names:
    when_branches.append(f'        "{img}" -> R.drawable.{img}')
when_branches.append('        else -> R.drawable.ic_placeholder_word')

when_block = '    return when (imageName) {\n' + '\n'.join(when_branches) + '\n    }'

carousel_content = re.sub(
    r'    return when \(imageName\) \{.*?\n    \}',
    when_block.replace('\\', '\\\\'),
    carousel_content,
    flags=re.DOTALL
)

with open(CAROUSEL, 'w', encoding='utf-8') as f:
    f.write(carousel_content)

print(f'Updated CarouselScreen.kt with {len(image_names)} images')
