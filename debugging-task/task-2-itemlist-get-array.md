# Task 2: Array Conversion

Please complete Task 1 before beginning this task.

## Bug Report

A list containing several text values can be created successfully. However, attempting to retrieve those values as an array causes the operation to fail.

## Reproduce the Bug

Run:

```sh
REPO_ROOT="$(git rev-parse --show-toplevel)"
mvn -f "$REPO_ROOT/pom.xml" -q -DskipTests compile
jshell --class-path "$REPO_ROOT/target/classes"
```

At the JShell prompt, run:

```java
import org.json.simple.ItemList;

ItemList items = new ItemList("a,b,c");

System.out.println(items.size());
String[] values = items.getArray();
```

**Current:** The list contains three items, but retrieving them as an array throws a `ClassCastException`.

**Expected:** The operation should return a `String[]` containing `"a"`, `"b"`, and `"c"` without throwing an exception.

## Your Task

**This is an implementation task, not just a diagnosis. Reproduce the bug, investigate its cause, and modify the Java implementation to fix it.** You are not expected to create or run unit tests; verify your change by repeating the JShell reproduction below.

## Verify Your Fix

After making a code change, exit JShell:

```text
/exit
```

Recompile:

```sh
mvn -f "$REPO_ROOT/pom.xml" -q -DskipTests compile
```

Start JShell again:

```sh
jshell --class-path "$REPO_ROOT/target/classes"
```

Repeat the reproduction steps above to check whether your fix works.