# json-simple Debugging Tasks

You will complete two debugging tasks in this repository.

---

# Pilot Testing Setup

> **This section is only for pilot testers who are setting up the repository on their own computer.**
>
> **Study participants will not complete this setup.** During the study, the repository and required development tools will already be installed and configured.

## 1. Clone the Repository

Clone the study repository:

```sh
git clone <REPOSITORY-URL>
cd <REPOSITORY-NAME>
```

## 2. Check Java

Run:

```sh
java -version
javac -version
jshell --version
```

A JDK that includes JShell is required.

If one of these commands is unavailable, install a JDK before continuing.

## 3. Check Maven

Run:

```sh
mvn -version
```

If Maven is not installed, install it before continuing.

On macOS with Homebrew:

```sh
brew install maven
```

Then verify:

```sh
mvn -version
```

## 4. Check the Project

From anywhere inside the cloned repository, run:

```sh
REPO_ROOT="$(git rev-parse --show-toplevel)"
mvn -f "$REPO_ROOT/pom.xml" -q -DskipTests compile
```

Warnings may appear and can be ignored as long as the project compiles without errors.

If the project compiles successfully, setup is complete.

---

# Participant Instructions

> **Begin here during the study.**
>
> The repository and required development tools will already be set up for you.

## About JSON and json-simple

**JSON (JavaScript Object Notation)** is a text format used to represent and exchange structured data. It can contain strings, numbers, booleans, arrays, and objects with key-value pairs.

For example:

```json
{
  "name": "Ada",
  "languages": ["Java", "Python"]
}
```

**json-simple** is a Java library for reading, writing, and working with JSON data.

The main Java source code is located in:

```text
src/main/java
```

## Using JShell

The debugging tasks use **JShell**, which allows Java statements to be executed directly from the Terminal.

The task instructions provide the commands needed to compile the project and start JShell.

When JShell is running, you will see:

```text
jshell>
```

To exit JShell:

```text
/exit
```

### After Changing Java Code

After modifying Java source code:

1. Exit JShell with `/exit`.
2. Recompile the project using the command provided in the task.
3. Start JShell again.
4. Repeat the reproduction to test your change.

## Your Debugging Tasks

For each task:

1. Reproduce the reported bug.
2. Investigate its cause.
3. **Modify the Java implementation to fix the bug.**
4. Rebuild and repeat the reproduction to verify your fix.

You are not expected to create or run unit tests. Verify each fix by repeating the task's reproduction steps after rebuilding.

Complete the tasks in order:

1. [Task 1: String-Like Value Encoding](debugging-task/task-1-string-like-value-encoding.md)
2. [Task 2: ItemList Array Conversion](debugging-task/task-2-itemlist-get-array.md)

Please complete Task 1 before beginning Task 2.

## Environment Problems

If a required development tool stops working or the project cannot run because of an environment problem, **notify the researcher** rather than modifying the project to fix the development environment.