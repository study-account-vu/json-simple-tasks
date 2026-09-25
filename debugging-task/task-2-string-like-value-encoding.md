# Task 2: String-Like Value Encoding

Please complete Task 1 before beginning this task.

## Bug Report

JSON strings are surrounded by double quotes (`"`). For example:

```text
"hello"
```

In Java, `StringBuilder` and `StringBuffer` can also contain text. Currently, json-simple serializes these values without the double quotes required for a JSON string.

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
import org.json.simple.JSONValue;

StringBuilder builder = new StringBuilder("hello");
StringBuffer buffer = new StringBuffer("world");

System.out.println(JSONValue.toJSONString(builder));
System.out.println(JSONValue.toJSONString(buffer));
```

**Current:**

```text
hello
world
```

**Expected:**

```text
"hello"
"world"
```

## Your Task

**Investigate why `StringBuilder` and `StringBuffer` values are not serialized as valid JSON strings, and modify the code to fix the bug.**

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