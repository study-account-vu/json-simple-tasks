# json-simple Debugging Tasks

## About JSON and json-simple

**JSON (JavaScript Object Notation)** is a text format commonly used to represent and exchange structured data. JSON can represent values such as strings, numbers, booleans, arrays, and objects containing key-value pairs.

For example:

```json
{
  "name": "Ada",
  "year": 2026,
  "languages": ["Java", "Python"]
}
```

**json-simple** is a Java library for reading and writing JSON. It provides functionality for two main operations:

* **Parsing:** converting JSON text into Java values and objects.
* **Serialization:** converting Java values and objects into JSON text.

For example, json-simple can parse:

```text
{"name":"Ada"}
```

into Java data that a program can work with. It can also perform the reverse operation by taking supported Java values and producing JSON text.

The two debugging tasks involve different parts of this general functionality. The task instructions describe the behavior that should be investigated.

## Maven

This repository uses **Maven**, a build and dependency-management tool commonly used with Java projects. Maven reads the project's `pom.xml` file to determine how the project should be compiled, tested, and built.

You do not need prior experience with Maven for these tasks. The commands needed to build and test the project are provided below.

## Building the Project

From the repository root, compile the project with:

```sh
mvn -q -DskipTests compile
```

Warnings may appear during compilation and can be ignored as long as the build completes without errors.

Some task reproduction steps use **JShell**, Java's interactive command-line environment. JShell allows Java statements to be entered and executed directly without creating a separate Java program. The individual task instructions provide the commands needed to reproduce each behavior.

To open JShell enter: 

```text
jshell --class-path target/classes
```

To exit JShell at any time, enter:

```text
/exit
```

## Testing a Change

After making a code change, compile the project again before reproducing the behavior:

```sh
mvn -q -DskipTests compile
```

Then repeat the reproduction steps provided in the task instructions to check whether the behavior has changed.

## Tasks

Complete the following tasks in order:

1. [Task 1: Malformed Input Location](task-1-malformed-input-location.md)
2. [Task 2: String-Like Value Encoding](task-2-string-like-value-encoding.md)

Please complete Task 1 before beginning Task 2.
