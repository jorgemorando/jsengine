# Lightweight Rule Engine

Lightweight Java-based JavaScript Rules Engine that compiles and executes JS rules using GraalVM. Built as a Spring Boot application, the project provides programmatic and REST-ready controls for registering, versioning and executing JavaScript rules from Java code.

## Technology stack
- Java 22
- GraalVM JavaScript (GraalJS)
- Spring Boot
- Maven

## What it does
- Load, compile and cache JavaScript libraries and rule scripts via GraalVM.
- Register rules and manage multiple versions per rule.
- Execute rules against facts/contexts
- Provide triggering results and aggregated multi-trigger results.
- Expose programmatic controls and Spring-based components to integrate with applications.

## Capabilities
- Fast JS execution using GraalVM polyglot contexts.
- Rule versioning and registry with safe, named version semantics.
- Fact and Context models for passing state to JS rules.
- Error mapping between JS exceptions and Java exceptions for clearer diagnostics.

## High-level architecture / runtime flow
ASCII diagram (simplified):

Client (app / REST) 
    |
    v
Controls (RulesControl / RuleEvaluationControl / EngineControl)
    |
    v
RuleRegistry  <---- stored metadata & versions
    |
    v
EngineCore  (compiles & caches via GraalVM)
    |
    v
Execution -> TriggerResult / MultiTriggerResult -> returned to caller

## Key components
- EngineCore: compilation and execution layer using GraalVM.
- RuleRegistry / RuleVersion: metadata and version management.
- CompiledRule: compiled artifact ready for fast execution.
- RuleEngineContext, Fact, TriggerResult: runtime data models.
- Controls (control.*): Spring-friendly components that wire registry and core for app integration.
- Resources: embedded JS libraries for enhanced in-rule operations (json.org.js, moment.min.js).

## JS resources
The engine ships with helper JS resources under src/main/resources (engine.js, json.org.js, moment.min.js). These are loaded into the JS execution context and are available to rule scripts. It can load external libraries marked as resources or that are included in the classpath.

## Setup & bootstrap (developer)
Prerequisites
- Install GraalVM Java 22 (community or enterprise) that includes GraalJS support (matching the project's targeted GraalJS version).
- Maven 3.6+
- Git (to clone repo)

Windows environment example
1) Install GraalVM and note its install path (example: C:\graalvm\graalvm-jdk-22)
2) Set JAVA_HOME to your GraalVM installation (PowerShell):
   - setx JAVA_HOME "C:\graalvm\graalvm-jdk-22"
   - setx PATH "%JAVA_HOME%\bin;%PATH%"
   - Restart the terminal / IDE to pick up environment variables.

3) Build the project:
   - mvn clean package

4) Run tests:
   - mvn test

5) Run the Spring Boot app (development):
   - mvn spring-boot:run
   or run the packaged jar:
   - java -jar target/*.jar

Notes
- Ensure the GraalVM runtime on PATH / JAVA_HOME is the one used by Maven/IDE.
- If running inside an IDE, configure the project SDK to the GraalVM JDK.
- Review src/main/resources to adapt embedded JS helpers if needed.

For more operational integration and code examples, see QUICKSTART.md.