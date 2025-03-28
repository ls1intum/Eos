# Eos – Headless JavaFX Testing Framework for Education

## Overview

**Eos** is a headless testing framework tailored for automated testing of JavaFX-based GUI applications in educational environments.
It is specifically designed to work in non-graphical environments such as Continuous Integration pipelines or automated grading systems, enabling seamless dynamic GUI testing without a display server.

## Motivation

Running GUI applications typically requires a graphical environment, which poses a challenge in headless CI/CD and auto-grading pipelines.
Eos addresses this by enabling headless execution and automated interaction with JavaFX applications, while also focusing on didactic feedback for students and usability for instructors.

## Key Objectives

- **Headless Execution**  
  Run JavaFX GUI applications in non-graphical environments without requiring a desktop windowing system.

- **Simulated Interaction**  
  Simulate user interactions such as mouse movement, clicks, and keyboard input to test dynamic behavior.

- **Window Analysis**  
  Verify application behavior by inspecting GUI components after interaction. This includes checking for presence, visibility, and properties of UI elements.

- **Educational Feedback**  
  Generate student-friendly, pedagogically helpful test output to enhance learning.

### Quality Requirements

- **Usability**  
  The framework should offer a simple and intuitive API for instructors, allowing rapid creation of effective GUI tests.

- **Performance**  
  Fast feedback is essential. Test execution time should scale proportionally with test complexity and quantity.

## System Architecture

The architecture of Eos consists of several modular components:

![System Architecture](system_architecture.drawio.svg "System Architecture")

- **SUT-GUI Application**: The student-submitted JavaFX application (System Under Test).
- **Window Service**: Provides the windowing environment. In headless mode, this is implemented using Monocle instead of the default desktop OS window service.
- **User Interaction Simulator**: A low-level interface to simulate user actions via the GUI framework. It provides a high-level abstraction for interacting with the GUI.
- **Tests**: Instructor-authored test cases using the Simulator Service.

## Implementation

Eos is implemented using the JavaFX framework with the following components:

- **TestFX**: Handles GUI component interaction and state assertions.
- **Monocle**: Enables headless windowing for JavaFX.
- **Ares**: Ensures secure and sandboxed test execution.

Eos also provides an instructor-friendly API, featuring pre-built checks and assertion utilities tailored for educational use cases.

## Usage

### Example Setup

An example exercise is provided in the `example` directory:

- `Problem.md`: Contains the exercise description.
- `template/`: Code template provided to students.
- `solution/`: A reference implementation of the correct solution.
- `tests/`: Contains pre-configured, Eos-compatible tests ready for auto-grading and CI execution.

Use the following Docker image in your auto-grader: `ghcr.io/ls1intum/eos:0.0.5`

### Manual Setup

We recommend using the `example` directory as a foundation for creating your own exercises.

If you prefer to set up your project independently, the following is a brief, non-exhaustive overview of the key configuration steps required to integrate Eos manually:

1. Add the dependency:

```groovy
testImplementation 'de.tum.cit.ase:eos:0.0.5'
```

2. Configure within the test task in `build.gradle`:
```groovy
    systemProperty "glass.platform", "Monocle"
    systemProperty "monocle.platform", "Headless"
    systemProperty "prism.order", "sw"
    systemProperty "prism.text", "t2k"
```

3. Set up **Ares** for secure execution. Refer to the [Ares documentation](https://github.com/ls1intum/Ares) for details.
4. Extend your test classes with `JavaFXTest` and begin writing tests using the Eos and TestFX APIs.

### Writing Tests

Eos offers a set of utilities for common assertions and GUI structure validation. Examples include:

- `captureAndSaveScreenshot(String fileName)`: Saves a screenshot of the current GUI window.
- `checkForCommonVBox(Node... nodes)`: Verifies recursively that the specified nodes are placed inside a common VBox.
- `getNodesOfType(Class<T> type, String query)`: Retrieves all matching nodes of a specific type.
- `getNodeOfType(Class<T> type, String query)`: Retrieves a single matching node or fails the test if zero or multiple matches are found.

Additionally, the [TestFX API](https://testfx.github.io/TestFX/docs/javadoc/) can be used for interactions and more assertions.

### Running Tests

To run tests locally: `./gradlew clean test`

To simulate user interaction with visible UI (non-headless, for local verification): `./gradlew clean testLocally`

### OS-Specific Configuration

For security reasons, only whitelisted paths are allowed to be accessed during test execution.
Depending on the operating system setup, you might have to configure the whitelist paths differently.
Alternatively, the reproducible docker-based execution can be used to avoid these issues.

- **macOS**: Use the `@TestFXMacAnnotations` annotation provided by Eos to apply necessary `@WhitelistPath` annotation from Ares.
- **Linux/Windows**: Configure custom paths using the `@WhitelistPath` annotation from Ares.

> ⚠️ Remember to remove or comment out OS-specific annotations before pushing to your remote repository. CI pipelines should only run with the minimal required permissions.

#### Docker-Based Execution
For consistent and reproducible test execution, you can run tests using Docker: `docker run -it --rm -v ./:/app -w /app ghcr.io/ls1intum/eos:0.0.5 ./gradlew clean test`
