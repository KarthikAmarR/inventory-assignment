# Inventory Assignment

A Spring Boot-based inventory and order management system for the engineering assessment.

## Features

- Create and retrieve products with validation
- Create orders with multiple items
- Update product stock transactionally
- Fetch low-stock products using Streams
- Summarize order value per product via Streams
- RESTful API design with proper HTTP status codes
- Global exception handling and optimistic locking
- Unit tests for services and controllers using Mockito
- In-memory H2 database for development/testing

## Technologies

- Java 17
- Spring Boot
- JPA/Hibernate
- H2 Database
- JUnit 5 + Mockito

## How to Run

```bash
./gradlew bootRun
```

## How to Test

```bash
./gradlew test
```
