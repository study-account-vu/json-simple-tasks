#!/usr/bin/env bash
#
# Documentation integrity check.
#
# Verifies that every local target referenced from a Markdown file actually
# exists - Markdown links and images, and HTML <img> and <a> tags, which are
# easy to miss because they do not use Markdown syntax. A broken reference here
# is invisible in a normal build and only shows up as a missing image or a 404
# for a reader.
#
# Usage: tools/check-docs.sh
#
# Requires: git and python3.

set -u

cd "$(dirname "$0")/.." || exit 1

status=0

for f in $(git ls-files '*.md'); do
    python3 - "$f" <<'PY' || status=1
import os, re, sys

path = sys.argv[1]
with open(path, encoding='utf-8') as fh:
    text = fh.read()

# Strip fenced code blocks: examples there are illustrative, not references.
text = re.sub(r'```.*?```', '', text, flags=re.S)

targets = []
targets += [(m, 'markdown') for m in re.findall(r'!?\[[^\]]*\]\(([^)]+)\)', text)]
targets += [(m, 'html src') for m in re.findall(r'<(?:img|source)[^>]*\ssrc="([^"]+)"', text)]
targets += [(m, 'html href') for m in re.findall(r'<a[^>]*\shref="([^"]+)"', text)]

bad = 0
checked = 0
for target, kind in targets:
    target = target.split(' ')[0].strip()
    if target.startswith(('http://', 'https://', 'mailto:', '#')):
        continue
    local = target.split('#')[0]
    if not local:
        continue
    checked += 1
    if not os.path.exists(local):
        print(f"  BROKEN  {path}: {kind} -> {local}")
        bad += 1

print(f"  {path}: {checked} local reference(s), {bad} broken")
sys.exit(1 if bad else 0)
PY
done

if [ "$status" -eq 0 ]; then
    echo "PASS - every local documentation reference resolves"
else
    echo "FAIL - see above"
fi
exit $status
