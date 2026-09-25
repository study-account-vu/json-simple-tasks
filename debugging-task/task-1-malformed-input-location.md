# Task 1: Malformed JSON Input

## Bug Report

In JSON, string keys and values are surrounded by double quotes (`"`), and a colon (`:`) separates a key from its value.

For example:

```text
{"city":"Nashville"}
```

An extra quote makes the input invalid:

```text
{"city"":"Nashville"}
```

json-simple rejects this malformed input, but reports the error at a later character instead of the unexpected extra quote.

## Reproduce the Bug

From the repository root, compile the project:

```sh
mvn -q -DskipTests compile
```

Start JShell:

```sh
jshell --class-path target/classes
```

Then run:

```java
import org.json.simple.parser.JSONParser;

String input = "[{\"name\"\":\"diego\"},{\"name\":\"andre\"},{\"name\":\"arambulo\"}]";

new JSONParser().parse(input);
```

**Current:** The parser reports the `d` in `diego` at zero-based position 11.

**Expected:** The malformed JSON should be rejected at the unexpected extra quote at zero-based position 8.

## Your Task

**Investigate why the malformed input is identified at the wrong location, and modify the code to fix the bug.**

## Test Your Fix

After making a code change, exit JShell:

```text
/exit
```

Recompile:

```sh
mvn -q -DskipTests compile
```

Start JShell again:

```sh
jshell --class-path target/classes
```

Repeat the reproduction steps above to check whether your fix works.