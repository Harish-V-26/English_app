import os
import shutil
import re

source_dir = r"images\Basic Vocabulary"
dest_dir = r"app\src\main\res\drawable"

if os.path.exists(source_dir):
    for f in os.listdir(source_dir):
        if f.lower().endswith('.png') or f.lower().endswith('.jpg'):
            # Example: '1.Splendid.png' -> 'splendid.png'
            new_name = re.sub(r'^[0-9]+\.', '', f)
            new_name = new_name.lower().strip()
            new_name = re.sub(r'[^a-z0-9]', '_', new_name)
            new_name = re.sub(r'_+', '_', new_name)
            
            # fix extension
            if new_name.endswith('_png'): new_name = new_name[:-4] + '.png'
            elif new_name.endswith('_jpg'): new_name = new_name[:-4] + '.jpg'
            
            src_path = os.path.join(source_dir, f)
            dst_path = os.path.join(dest_dir, new_name)
            
            print(f"Copying {f} to {new_name}")
            shutil.copy(src_path, dst_path)
            
print("Done copying basic vocabulary images.")
