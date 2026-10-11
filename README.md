# Restful Booker API Test Automation Framework

A Java-based API test automation framework for validating Restful Booker-style endpoints using REST Assured, TestNG, and Allure.

This project uses Java 27 and a local mock API server instead of a shared public sandbox, so the test suite is stable, repeatable, and not dependent on an external service.

## What this project does

- Tests authentication flows
- Validates booking CRUD operations
- Verifies data-driven scenarios
- Checks contract and search behavior
- Produces rich reports with Allure
- Uses environment-based configuration for different runs

## Tech stack

- Java 27
- Maven
- TestNG
- REST Assured
- Jackson
- Owner configuration library
- Datafaker
- AssertJ
- Logback + SLF4J
- Allure

## Local mock API

The framework is configured to run against a local in-memory API at:

- http://localhost:8080

The server is started automatically by the test suite lifecycle in BaseTest and is stopped at the end of the run. This keeps the tests isolated and prevents failures caused by an unstable public API.

## Project structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/framework/
│   │       ├── assertions/
│   │       ├── auth/
│   │       ├── client/
│   │       ├── config/
│   │       ├── data/
│   │       ├── exceptions/
│   │       ├── models/
│   │       ├── reporting/
│   │       └── serialization/
│   └── resources/
│       └── config/
│           ├── default.properties
│           ├── qa.properties
│           └── staging.properties
└── test/
    ├── java/
    │   └── com/framework/
    │       ├── base/
    │       ├── dataproviders/
    │       ├── listeners/
    │       ├── mock/
    │       └── tests/
    └── resources/
        ├── allure.properties
        ├── logback-test.xml
        ├── schemas/
        ├── suites/
        └── testdata/
```

## Framework design

The code is organized around a few simple ideas:

- Clients handle HTTP operations for each API resource.
- Authentication is injected using the Strategy pattern.
- Config values are loaded from property files and environment variables.
- The BaseTest class manages the test lifecycle.
- The mock API simulates the Restful Booker contract without depending on an external service.

## Authentication support

The framework supports three auth modes:

- TOKEN
- BASIC
- NONE

These are selected through the configuration using auth.type and are implemented through the auth strategy classes.

## Running the tests

From the project root:

```bash
mvn clean test
```

Run a specific suite:

```bash
mvn clean test -DsuiteXmlFile=src/test/resources/suites/smoke.xml
```

Run with a different environment:

```bash
mvn clean test -Denv=staging
```

Run with explicit config overrides:

```bash
mvn clean test -Dbase.url=http://localhost:8080 -Dauth.type=BASIC
```

Generate the Allure report:

```bash
mvn allure:report
```

Open the report locally:

```bash
mvn allure:serve
```

## Configuration

The project uses Owner-based configuration with a clear precedence order:

1. JVM system properties / environment variables
2. environment-specific files such as qa.properties or staging.properties
3. default.properties
4. Java defaults defined in the config interface

This gives you flexibility without hardcoding values into tests.

## Mock server behavior

The local mock supports the endpoints and flows used by this automation suite, including:

- POST /auth
- GET /booking
- POST /booking
- GET /booking/{id}
- PUT /booking/{id}
- PATCH /booking/{id}
- DELETE /booking/{id}
- GET /ping

It keeps booking data in memory during the test run and resets after the suite completes.

## Notes for contributors

- Add new endpoints by extending the client layer and updating the mock server contract.
- Add new auth strategies by implementing the auth strategy interface.
- Prefer environment-based configuration over hardcoded values.
- Keep test data and test expectations readable and explicit.

## Important note

This framework is designed for stable local automation runs. It uses a local mock API to keep tests deterministic and independent from any external shared service.
