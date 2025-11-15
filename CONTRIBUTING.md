# Contributing to Void

Thanks for your interest in contributing! This guide explains how to propose changes, report bugs, and help improve the
project.

## Table of contents

- Code of Conduct
- Ways to contribute
- Development setup
- Running and testing
- Style and conventions
- Submitting changes (PRs)
- Issue triage
- Release and versioning

## Code of Conduct

By participating in this project, you agree to abide by our [Code of Conduct](CODE_OF_CONDUCT.md).

## Ways to contribute

- Report bugs and request features via GitHub Issues
- Improve documentation and examples
- Add tests or fix flaky ones
- Implement small, well-scoped enhancements

If you plan a significant change, please open an issue first to discuss the approach and scope.

## Development setup

Requirements:

- Java 17+
- Kotlin 2.2.10
- Gradle (the repo includes Gradle Wrapper)

Clone the repository and open it in IntelliJ IDEA or your editor of choice. This is a standard multi-module Kotlin
project with a `backend` module and a small `test` app.

### Build

```
./gradlew build
```

### Run the example app

The test module contains sample routes you can run to try the framework:

```
./gradlew :test:run
```

Or run your own small snippet using the `io.voidx.server.server { }` and `io.voidx.router.router { }` DSLs from a small
`main()`.

## Running and testing

This repository currently focuses on example-based validation. If you add functionality, please include at least a
minimal example usage in the test module or unit tests when applicable.

## Style and conventions

- Kotlin style is enforced with ktlint (plugin is applied). You can format with:
  ```
  ./gradlew ktlintFormat
  ```
- Prefer small, composable functions and clear names.
- Public APIs should be documented with KDoc.
- Avoid breaking changes unless discussed and agreed.

## Submitting changes (PRs)

1. Fork the repository and create a feature branch.
2. Make your changes with clear, focused commits.
3. Update or add documentation and examples as needed.
4. Ensure the project builds and ktlint passes locally.
5. Open a Pull Request:
    - Describe the problem and solution, including motivation
    - Link related issues
    - Note any breaking changes or migration steps

We use standard GitHub checks for CI; please make sure they pass.

## Issue triage

When filing an issue, include:

- Void version, JVM version, OS
- Steps to reproduce
- Expected vs actual behavior
- Minimal code snippet or test if possible

## Release and versioning

Releases are published via tags and JitPack. We aim to follow semantic versioning as the API stabilizes.

## Security

Please do not report vulnerabilities in public issues. Follow the process in [SECURITY.md](SECURITY.md).
