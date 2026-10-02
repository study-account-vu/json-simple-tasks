# Task 2: Array Conversion

Please complete Task 1 before beginning this task.

## Your Task

Reproduce the bug using the provided JShell code, then **find and modify the appropriate Java source code under `src/main/java` to fix it.**

**Do not modify the JShell reproduction code.** Finding the relevant source file and method is part of the task.

## Bug Report

A list containing several text values can be created successfully. However, attempting to retrieve those values as an array produces an incorrect result instead of the stored items.

## Reproduce the Bug

From the repository root, compile the project:

```sh
mvn -q -DskipTests compile
```

Start JShell:

```sh
jshell --class-path target/classes
```

At the JShell prompt, run the following code **exactly as shown**:

```java
import org.json.simple.ItemList;

ItemList items = new ItemList("a,b,c");

System.out.println(items.size());
String[] values = items.getArray();
```

**Current:** The list contains three items, but retrieving them as an array silently returns `null` instead of the items.

**Expected:** The operation should return a `String[]` containing `"a"`, `"b"`, and `"c"` without throwing an exception.

## Test Your Fix

After modifying the Java source code, exit JShell:

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

Run the **same reproduction code without changing it** and confirm that it now produces the expected behavior.