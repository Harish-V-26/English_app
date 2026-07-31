import fitz
import os

pdf_path = r"C:\Users\acer\.gemini\antigravity-ide\brain\271b0991-d784-45f9-a8d4-3f4cb27646ba\media__1785462683538.pdf"
doc = fitz.open(pdf_path)
out_dir = r"c:\Users\acer\Desktop\English_app\app\src\main\res\drawable"

img_index = 0
word_names = [
    "bat", "bank", "bark", "bow", "fair", "jam", "lead", "match", "spring", "tire", 
    "current", "minute", "scale", "clip", "bolt", "charge", "spring2", "pitch", "console", 
    "pound", "suspend", "plot", "monitor", "anchor", "appraise", "abstract_img", "racket", 
    "vault", "alight", "annotate", "conflate", "cower", "dissipate", "enervate", "convive"
]

for page_num in range(len(doc)):
    page = doc[page_num]
    images = page.get_images(full=True)
    
    # Sort images top-to-bottom if possible, but get_images usually returns in content stream order.
    # We will assume they are in order.
    for img in images:
        if img_index >= len(word_names):
            break
            
        xref = img[0]
        base_image = doc.extract_image(xref)
        image_bytes = base_image["image"]
        image_ext = base_image["ext"]
        
        # Save original file but without extension in the list? No, drawables don't need ext in R.drawable.*
        # Wait, if we overwrite an existing file like "bat.jpeg" with "bat.png", we might have two files "bat".
        # Let's save them as `vt_{word_names[img_index]}.ext` to avoid conflicts, or delete old ones?
        # Let's name them `vt_{word_names[img_index]}.{ext}`.
        
        filename = f"vt_{word_names[img_index]}.{image_ext}"
        filepath = os.path.join(out_dir, filename)
        
        with open(filepath, "wb") as f:
            f.write(image_bytes)
            
        img_index += 1

print(f"Extracted {img_index} images.")
