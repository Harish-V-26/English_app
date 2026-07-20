import os
from PIL import Image

drawable_dir = r"app/src/main/res/drawable"
max_size = 1000 # Max width or height

compressed_count = 0
total_saved = 0

for filename in os.listdir(drawable_dir):
    file_path = os.path.join(drawable_dir, filename)
    if not os.path.isfile(file_path):
        continue
        
    ext = os.path.splitext(filename)[1].lower()
    if ext not in ['.png', '.jpg', '.jpeg']:
        continue
        
    original_size = os.path.getsize(file_path)
    if original_size > 1.5 * 1024 * 1024: # > 1.5 MB
        try:
            print(f"Compressing {filename} ({original_size / 1024 / 1024:.2f} MB)...")
            img = Image.open(file_path)
            
            # Keep RGB/RGBA mode
            # Resize if too large
            width, height = img.size
            if width > max_size or height > max_size:
                if width > height:
                    new_width = max_size
                    new_height = int(height * (max_size / width))
                else:
                    new_height = max_size
                    new_width = int(width * (max_size / height))
                img = img.resize((new_width, new_height), Image.Resampling.LANCZOS)
                print(f"  Resized from {width}x{height} to {new_width}x{new_height}")
            
            # Save back
            if ext == '.png':
                # For PNG, optimize and save
                img.save(file_path, format="PNG", optimize=True)
            else:
                img.save(file_path, format="JPEG", quality=85, optimize=True)
                
            new_size = os.path.getsize(file_path)
            saved = original_size - new_size
            total_saved += saved
            compressed_count += 1
            print(f"  New size: {new_size / 1024 / 1024:.2f} MB (Saved {saved / 1024 / 1024:.2f} MB)")
        except Exception as e:
            print(f"  Error compressing {filename}: {e}")

print(f"Compressed {compressed_count} images. Total space saved: {total_saved / 1024 / 1024:.2f} MB")
