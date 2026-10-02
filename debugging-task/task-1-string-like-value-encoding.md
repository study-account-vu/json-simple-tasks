# Task 1: String-Like Value Encoding

## Your Task

Reproduce the bug using the provided JShell code, then **find and modify the appropriate Java source code under `src/main/java` to fix it.**

**Do not modify the JShell reproduction code.** Finding the relevant source file and method is part of the task.

## Bug Report

**Serialization** means converting a Java value into JSON text. A JSON string is text surrounded by double quotes (`"`). For example:

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

At the JShell prompt, run the following code **exactly as shown**:

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

Run the **same reproduction code without changing it** and confirm that it now produces the expected output.