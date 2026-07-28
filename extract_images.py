import fitz
import os

pdf_dir = "Words"
out_dir = "app/src/main/res/drawable"
pdfs = sorted([f for f in os.listdir(pdf_dir) if f.endswith('.pdf')])

image_names = [
    ("crouch", "flinch"),
    ("splendid", "elegant"),
    ("collasal", "many"),
    ("doc4_brunch", "doc4_smog"),
    ("saute", "doc5_whisk"),
    ("wink", "chew")
]

print(f"Found PDFs: {pdfs}")
for i, pdf_name in enumerate(pdfs):
    if i >= len(image_names): break
    print(f"Processing {pdf_name} for {image_names[i]}")
    doc = fitz.open(os.path.join(pdf_dir, pdf_name))
    for page_index in range(2):
        if page_index >= len(doc): break
        page = doc[page_index]
        image_list = page.get_images()
        print(f"Page {page_index} image_list: {image_list}")
        if image_list:
            # get the largest image
            xref = max(image_list, key=lambda img: img[2] * img[3])[0]
            base_image = doc.extract_image(xref)
            image_bytes = base_image["image"]
            ext = base_image["ext"]
            filename = f"{image_names[i][page_index]}.jpg" # Android prefers jpg/png, we'll force extension or let Android pick it up
            # If it's png, let's write as .png. Let's just remove old ones with same base name
            base_name = image_names[i][page_index]
            for old_ext in ["jpg", "png", "jpeg", "webp"]:
                old_path = os.path.join(out_dir, f"{base_name}.{old_ext}")
                if os.path.exists(old_path):
                    os.remove(old_path)
            
            new_path = os.path.join(out_dir, f"{base_name}.{ext}")
            with open(new_path, "wb") as f:
                f.write(image_bytes)
            print(f"Extracted {new_path}")
