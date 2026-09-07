#!/usr/bin/env bash
#
# Backward-compatibility check against a previous release.
#
# Builds a baseline of json-simple, builds the working tree, then compares the
# two along four axes:
#
#   1. public/protected API surface        (javap dump, diffed)
#   2. binary compatibility                (probes compiled ONLY against the
#                                           baseline, then run against both)
#   3. observable behaviour                (~120 decode/encode inputs, diffed)
#   4. serialization                       (serialVersionUIDs, plus a cross-version
#                                           write/read in both directions)
#
# Any difference is reported. Differences that are intentional for the release
# being prepared are recorded in tools/compat/expected-diff.txt; the script exits
# non-zero only when it finds a difference that is not listed there.
#
# Usage:
#   tools/compat-check.sh                 # against $DEFAULT_BASELINE
#   tools/compat-check.sh tag_release_1_1 # against any git ref
#   tools/compat-check.sh path/to/old.jar # against a published jar
#   tools/compat-check.sh --accept        # rewrite expected-diff.txt from the
#                                         # current result, after review
#
# Requires: git, javac/java (JDK 8+), and nothing else.

set -u

# The last version actually published to Maven Central. master already
# differs from it, so this - not HEAD - is the baseline that matters.
DEFAULT_BASELINE="tag_release_1_1_1"

cd "$(dirname "$0")/.." || exit 1
ROOT="$(pwd)"
WORK="$ROOT/target/compat"
HARNESS="$ROOT/tools/compat"
EXPECTED="$HARNESS/expected-diff.txt"

ACCEPT=0
BASELINE="$DEFAULT_BASELINE"
for arg in "$@"; do
    case "$arg" in
        --accept) ACCEPT=1 ;;
        -h|--help) sed -n '2,30p' "$0"; exit 0 ;;
        *) BASELINE="$arg" ;;
    esac
done

say()  { printf '%s\n' "$*"; }
head2() { printf '\n== %s\n' "$*"; }
die()  { printf 'error: %s\n' "$*" >&2; exit 2; }

rm -rf "$WORK"
mkdir -p "$WORK/baseline-classes" "$WORK/current-classes" "$WORK/probes" "$WORK/out"

# ---------------------------------------------------------------- baseline ---
head2 "Baseline: $BASELINE"

BASELINE_CP=""
if [ -f "$BASELINE" ] && case "$BASELINE" in *.jar) true ;; *) false ;; esac; then
    BASELINE_CP="$BASELINE"
    say "using jar $BASELINE"
else
    git rev-parse --verify --quiet "$BASELINE" >/dev/null \
        || die "'$BASELINE' is neither a .jar nor a git ref"
    mkdir -p "$WORK/baseline-src"
    for f in $(git ls-tree -r --name-only "$BASELINE" -- src/main/java); do
        mkdir -p "$WORK/baseline-src/$(dirname "$f")"
        git show "$BASELINE:$f" > "$WORK/baseline-src/$f"
    done
    find "$WORK/baseline-src" -name '*.java' > "$WORK/baseline-sources.txt"
    javac -nowarn -encoding UTF-8 -d "$WORK/baseline-classes" \
          @"$WORK/baseline-sources.txt" 2>/dev/null \
        || die "could not compile baseline $BASELINE"
    BASELINE_CP="$WORK/baseline-classes"
    say "built $(git rev-parse --short "$BASELINE") from source"
fi

# ----------------------------------------------------------------- current ---
find "$ROOT/src/main/java" -name '*.java' > "$WORK/current-sources.txt"
javac -nowarn -encoding UTF-8 -d "$WORK/current-classes" @"$WORK/current-sources.txt" 2>/dev/null \
    || die "could not compile the working tree"
CURRENT_CP="$WORK/current-classes"
say "built working tree"

# Identity hash codes (Object.toString) and absolute paths would otherwise make
# the comparison non-deterministic.
normalize() { sed -e 's/@[0-9a-f]\{4,\}/@HASH/g' -e "s#$WORK#\$WORK#g"; }

# ------------------------------------------------------- 1. API surface ------
head2 "1. Public API surface"

api_dump() {
    local cp="$1"
    ( cd "$cp" 2>/dev/null || return 0
      find . -name '*.class' ! -name '*$*' | sed -e 's#^\./##' -e 's#\.class$##' -e 's#/#.#g' | sort
    ) > "$WORK/classes.txt"
    while read -r c; do
        [ -n "$c" ] && javap -protected -cp "$cp" "$c" 2>/dev/null
    done < "$WORK/classes.txt"
}
# Enumerate from the baseline so a removed class shows up as a diff, not an error.
api_dump "$BASELINE_CP" > "$WORK/out/api-baseline.txt"
while read -r c; do
    [ -n "$c" ] && javap -protected -cp "$CURRENT_CP" "$c" 2>/dev/null \
        || printf 'CLASS REMOVED: %s\n' "$c"
done < "$WORK/classes.txt" > "$WORK/out/api-current.txt"

diff -a -u "$WORK/out/api-baseline.txt" "$WORK/out/api-current.txt" > "$WORK/out/api.diff"
if [ -s "$WORK/out/api.diff" ]; then
    grep -E '^[+-][^+-]' "$WORK/out/api.diff" | sed 's/^/  /'
else
    say "  identical"
fi

# --------------------------------------------- 2 & 3. probes on both jars ----
# The probes are compiled against the BASELINE only. Running that exact bytecode
# against the current build is what proves binary compatibility.
head2 "2. Binary compatibility and 3. Behaviour"

javac -nowarn -encoding UTF-8 -cp "$BASELINE_CP" -d "$WORK/probes" \
      "$HARNESS/Probe.java" "$HARNESS/LegacyConsumer.java" "$HARNESS/Corpus.java" "$HARNESS/SerCheck.java" 2>/dev/null \
    || die "probes do not compile against the baseline (they must only use baseline API)"

run_probe() {  # run_probe <name> <class> <cp>
    java -cp "$3:$WORK/probes" "$2" 2>&1 | normalize
}

for probe in LegacyConsumer Corpus; do
    run_probe "$probe" "$probe" "$BASELINE_CP" > "$WORK/out/$probe-baseline.txt"
    run_probe "$probe" "$probe" "$CURRENT_CP"  > "$WORK/out/$probe-current.txt"
    diff -a -u "$WORK/out/$probe-baseline.txt" "$WORK/out/$probe-current.txt" \
        > "$WORK/out/$probe.diff"
    n=$(grep -cE '^[+-][^+-]' "$WORK/out/$probe.diff" 2>/dev/null || true)
    if [ "${n:-0}" -eq 0 ]; then
        say "  $probe: identical ($(wc -l < "$WORK/out/$probe-baseline.txt" | tr -d ' ') lines)"
    else
        say "  $probe: $n differing lines"
        grep -E '^[+-][^+-]' "$WORK/out/$probe.diff" | sed 's/^/    /'
    fi
done

# --------------------------------------------------- 4. serialization --------
head2 "4. Serialization"

run_probe svuid SerCheck "$BASELINE_CP" > "$WORK/out/svuid-baseline.txt"
run_probe svuid SerCheck "$CURRENT_CP"  > "$WORK/out/svuid-current.txt"
diff -a -u "$WORK/out/svuid-baseline.txt" "$WORK/out/svuid-current.txt" > "$WORK/out/svuid.diff"
if [ -s "$WORK/out/svuid.diff" ]; then
    grep -E '^[+-][^+-]' "$WORK/out/svuid.diff" | sed 's/^/  /'
else
    say "  serialVersionUIDs unchanged"
fi

java -cp "$BASELINE_CP:$WORK/probes" SerCheck write "$WORK/from-baseline.ser" >/dev/null 2>&1
java -cp "$CURRENT_CP:$WORK/probes"  SerCheck write "$WORK/from-current.ser"  >/dev/null 2>&1
fwd=$(java -cp "$CURRENT_CP:$WORK/probes"  SerCheck read "$WORK/from-baseline.ser" 2>&1)
back=$(java -cp "$BASELINE_CP:$WORK/probes" SerCheck read "$WORK/from-current.ser"  2>&1)
case "$fwd"  in *"id=42"*) say "  baseline -> current: ok" ;; *) say "  baseline -> current: FAILED: $fwd" ;; esac
case "$back" in *"id=42"*) say "  current -> baseline: ok (rollback safe)" ;; *) say "  current -> baseline: FAILED: $back" ;; esac
{ printf 'forward: %s\n' "$fwd"; printf 'backward: %s\n' "$back"; } > "$WORK/out/ser-crossversion.txt"

# ------------------------------------------------------------- verdict -------
cat "$WORK/out/api.diff" "$WORK/out/LegacyConsumer.diff" \
    "$WORK/out/Corpus.diff" "$WORK/out/svuid.diff" 2>/dev/null \
    | grep -E '^[+-][^+-]' > "$WORK/out/all.diff"

head2 "Result"

if [ "$ACCEPT" -eq 1 ]; then
    # The comment block at the top of expected-diff.txt is hand-written: it is
    # where each recorded difference is explained. Carry it over rather than
    # overwriting it, or regenerating silently destroys the documentation.
    if [ -f "$EXPECTED" ] && grep -qE '^[[:space:]]*#' "$EXPECTED"; then
        sed -n '/^[[:space:]]*#/p; /^[^#]/q' "$EXPECTED" > "$WORK/out/header.txt"
        say "keeping the existing $(grep -cE '^[[:space:]]*#' "$WORK/out/header.txt")-line comment header"
    else
        {
            printf '# Differences from baseline %s that are intentional for this release.\n' "$BASELINE"
            printf '# Regenerate with: tools/compat-check.sh %s --accept\n' "$BASELINE"
            printf '# Review every line before committing - this file is the record of\n'
            printf '# what the release deliberately changes for downstream users.\n'
        } > "$WORK/out/header.txt"
    fi
    cat "$WORK/out/header.txt" "$WORK/out/all.diff" > "$EXPECTED"
    say "wrote $(basename "$EXPECTED") ($(grep -cE '^[+-]' "$WORK/out/all.diff" || echo 0) differences)"
    say "the header is hand-maintained - update it if the recorded set changed"
    exit 0
fi

if [ ! -f "$EXPECTED" ]; then
    grep -vE '^[[:space:]]*#' /dev/null > "$WORK/out/expected.txt" 2>/dev/null || : > "$WORK/out/expected.txt"
else
    grep -vE '^[[:space:]]*#' "$EXPECTED" > "$WORK/out/expected.txt" 2>/dev/null || : > "$WORK/out/expected.txt"
fi

if diff -a -q "$WORK/out/expected.txt" "$WORK/out/all.diff" >/dev/null 2>&1; then
    n=$(grep -cE '^[+-]' "$WORK/out/all.diff" 2>/dev/null || echo 0)
    if [ "${n:-0}" -eq 0 ]; then
        say "PASS - fully compatible with $BASELINE, no differences at all"
    else
        say "PASS - the only differences are the $n recorded in tools/compat/expected-diff.txt"
    fi
    exit 0
fi

say "FAIL - differences that are not recorded in tools/compat/expected-diff.txt:"
diff -a "$WORK/out/expected.txt" "$WORK/out/all.diff" | sed 's/^/  /'
say ""
say "If these changes are intentional, review them and run:"
say "  tools/compat-check.sh $BASELINE --accept"
exit 1
