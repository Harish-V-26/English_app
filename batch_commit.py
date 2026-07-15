import subprocess
import os

def run_cmd(cmd):
    print(f"Running: {cmd}")
    res = subprocess.run(cmd, shell=True, capture_output=True, text=True)
    return res.returncode, res.stdout.strip(), res.stderr.strip()

# 1. Reset commit
code, out, err = run_cmd("git reset --mixed 64bcd2953544d0c90ef91ae0fb2b1310d920fc9b")
print(f"Reset code: {code}, out: {out[:100]}, err: {err}")

# 2. Get list of modified/untracked files in res/drawable
# We will use git status --porcelain
code, out, err = run_cmd("git status --porcelain")
lines = out.split('\n')

drawable_files = []
other_files = []

for line in lines:
    if not line:
        continue
    # Format of git status --porcelain is: XY path
    status = line[:2]
    path = line[3:].strip()
    
    # Clean quotes if any
    if path.startswith('"') and path.endswith('"'):
        path = path[1:-1]
        
    if "app/src/main/res/drawable" in path:
        drawable_files.append(path)
    else:
        # Don't add Python files
        if not path.endswith('.py') and not path.endswith('.log'):
            other_files.append(path)

print(f"Found {len(drawable_files)} drawable files and {len(other_files)} other files.")

# 3. Stage and push drawables in batches of 40
batch_size = 40
for i in range(0, len(drawable_files), batch_size):
    batch = drawable_files[i:i+batch_size]
    print(f"\n--- Batch {i//batch_size + 1}: Staging {len(batch)} files ---")
    
    # Stage files
    for f in batch:
        run_cmd(f'git add "{f}"')
        
    # Commit
    commit_msg = f"Add/update drawables part {i//batch_size + 1}"
    code, out, err = run_cmd(f'git commit -m "{commit_msg}"')
    if code != 0:
        print(f"Commit failed: {err}")
        break
        
    # Push
    print("Pushing batch...")
    code, out, err = run_cmd("git push origin main")
    if code != 0:
        print(f"Push failed: {err}")
        # Try again once with HTTP config if not already set
        break
    else:
        print("Batch pushed successfully!")

# 4. Stage and push remaining other files
if other_files:
    print(f"\n--- Final Batch: Staging {len(other_files)} non-drawable/code files ---")
    for f in other_files:
        run_cmd(f'git add "{f}"')
    
    code, out, err = run_cmd('git commit -m "Update app logic, layout scaling, and mappings"')
    if code == 0:
        print("Pushing final batch...")
        code, out, err = run_cmd("git push origin main")
        if code == 0:
            print("Final batch pushed successfully!")
        else:
            print(f"Final push failed: {err}")
    else:
        print(f"Final commit failed: {err}")

print("Batch commit process complete!")
