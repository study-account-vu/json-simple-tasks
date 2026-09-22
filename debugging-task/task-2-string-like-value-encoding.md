# Debugging Task: String-Like Value Encoding

Please ensure that you have completed Task 1 before beginning this task.

## Report

JSON uses delimiters to mark the beginning and end of certain values. A delimiter is a character that indicates where a value starts or ends. For JSON strings, double quotes (`"`) are used as delimiters.

For example, the text:

```text
hello
```

is represented as a JSON string by surrounding it with double quotes:

```text
"hello"
```

In Java, `StringBuilder` and `StringBuffer` can also contain text. When these values are serialized by json-simple, however, the text is written without the surrounding double quotes required for a JSON string.

For example, a `StringBuilder` containing `hello` is currently serialized as:

```text
hello
```

instead of:

```text
"hello"
```

## Expected Behavior

Text contained in a `StringBuilder` or `StringBuffer` should be serialized as a valid JSON string, including the surrounding double quotes and any escaping required to represent the text correctly.

For example:

```text
StringBuilder containing: hello

Expected JSON: "hello"
```

## Reproduction

From the repository root, compile the project:

```sh
mvn -q -DskipTests compile
```

Warnings may appear during compilation and can be ignored as long as the build completes without errors.

Start the Java REPL with the compiled classes:

```sh
jshell --class-path target/classes
```
To exit JShell at any time, enter:

/exit

Then run:

```java
import org.json.simple.JSONValue;

StringBuilder builder = new StringBuilder("hello");
StringBuffer buffer = new StringBuffer("world");

System.out.println(JSONValue.toJSONString(builder));
System.out.println(JSONValue.toJSONString(buffer));
```

The current output is:

```text
hello
world
```

The expected output is:

```text
"hello"
"world"
```

## Task

Investigate the cause of this behavior and fix it.

## Acceptance Checks

* `StringBuilder("hello")` serializes as `"hello"`.
* `StringBuffer("world")` serializes as `"world"`.
* Text containing quotes, backslashes, or control characters produces valid JSON.
* Serialized values can be parsed back to their original text.
* Existing serialization behavior for ordinary strings, numbers, booleans, `null`, arrays, collections, and maps remains unchanged.
