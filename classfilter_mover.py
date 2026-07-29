import json

TXT_PATH = "src/main/resources/kubejs.classfilter.txt"
JSON_PATH = "src/main/resources/kube.plugin.json"

allow: list[str] = []
deny: list[str] = []

# Creates a list of allowed and denied classes.
# Removes the space after the '+' and '-'
with open(TXT_PATH, "r") as file:
    content = file.read()

for line in content.splitlines():
    lne = line.strip()
    if not lne or lne.startswith("#"):
        # skip blank lines and comments
        continue
    if lne.startswith("+"):
        allow.append(lne[1:].strip())
    elif lne.startswith("-"):
        deny.append(lne[1:].strip())
    else:
        print(f"Warning: ignoring unrecognized line: {line!r}")

# Read existing JSON first
with open(JSON_PATH, "r") as file:
    data = json.load(file)

# Most addons are one plugin, so we load the first one
plugin = data["plugins"][0]
class_filter = plugin.setdefault("class_filter", {})

if allow:
    class_filter["allow"] = allow
if deny:
    class_filter["deny"] = deny

# Write back separately, after reading is done
with open(JSON_PATH, "w") as file:
    json.dump(data, file, indent=2)
    file.write("\n")

print("Class filter migration completed. You can now delete the class filter txt file.")