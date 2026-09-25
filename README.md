# json-simple Debugging Tasks

You will complete two debugging tasks in this repository.

## About JSON and json-simple

**JSON (JavaScript Object Notation)** is a text format used to represent and exchange structured data. It can contain strings, numbers, booleans, arrays, and objects with key-value pairs.

For example:

```json
{
  "name": "Ada",
  "languages": ["Java", "Python"]
}
```

**json-simple** is a Java library for reading and writing JSON. It supports two main operations:

- **Parsing:** converting JSON text into Java values and objects.
- **Serialization:** converting Java values and objects into JSON text.

The debugging tasks involve these two types of functionality.

## Tools

This project uses **Maven** to compile and build the Java project. The commands you need are provided in each task.

Some reproduction steps also use **JShell**, which allows you to run Java statements directly from the Terminal without creating a separate Java program.

To exit JShell:

```text
/exit
```

## Tasks

All main source code files are in json-simple/src/main/java. 

Complete the tasks in order:


1. [Task 1: Malformed JSON Input](debugging-task/task-1-malformed-input-location.md)
2. [Task 2: String-Like Value Encoding](debugging-task/task-2-string-like-value-encoding.md)

Please complete Task 1 before beginning Task 2.