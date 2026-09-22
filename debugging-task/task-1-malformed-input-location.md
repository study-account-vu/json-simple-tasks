# Debugging Task: Malformed Json Input

## Report

In JSON, an object contains key-value pairs. String keys and values are surrounded by double quotes (`"`), and a colon (`:`) separates each key from its value.

For example, a correctly formatted object looks like:

```text
{"city":"Nashville"}
```

An additional quote can make the object invalid:

```text
{"city"":"Nashville"}
```

When json-simple encounters malformed JSON, it reports information about where parsing failed. In some cases involving an extra quote after an object key, the reported location points to a later character rather than the extra quote that made the input invalid.

## Expected Behavior

The malformed JSON should be rejected when the unexpected extra quote is encountered at zero-based position 8, rather than continuing until the later `d` at position 11.

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

```text
/exit
```

Then run:

```java
import org.json.simple.parser.JSONParser;

String input = "[{\"name\"\":\"diego\"},{\"name\":\"andre\"},{\"name\":\"arambulo\"}]";

new JSONParser().parse(input);
```

The current failure identifies the `d` in `diego` at zero-based position 11. The unexpected extra quote occurs at zero-based position 8.

## Task

Investigate the cause of this behavior and modify the implementation so that the malformed input is identified at the correct location.

## Acceptance Checks

* The malformed input still produces a parse failure.
* The failure identifies the unexpected extra quote at position 8.
* Valid JSON input continues to parse normally.
