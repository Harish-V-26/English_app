import os
import subprocess
import sys

def run_cmd(cmd):
    print(f"Running: {cmd}")
    res = subprocess.run(cmd, shell=True, capture_output=True, text=True)
    if res.returncode != 0:
        print(f"Error: {res.stderr.strip()}")
    return res.stdout.strip()

# Get tracked files
tracked_files = run_cmd("git ls-files app/src/main/res/drawable").split('\n')

for file_path in tracked_files:
    if not file_path:
        continue
    dir_name = os.path.dirname(file_path)
    base_name = os.path.basename(file_path)
    
    # Check if there are any uppercase letters in the base name
    if not base_name.islower():
        lower_base_name = base_name.lower()
        
        old_path = file_path
        temp_path = os.path.join(dir_name, "temp_" + lower_base_name).replace('\\', '/')
        new_path = os.path.join(dir_name, lower_base_name).replace('\\', '/')
        
        print(f"Renaming tracked file in Git: {old_path} -> {new_path}")
        
        # Step 1: git mv to temp path
        run_cmd(f'git mv "{old_path}" "{temp_path}"')
        # Step 2: git mv from temp path to lowercase path
        run_cmd(f'git mv "{temp_path}" "{new_path}"')

print("Git renaming complete!")
