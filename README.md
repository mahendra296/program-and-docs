# Program and Docs

A learning repository containing Java programs and technical documentation covering multiple technologies. It serves as a reference for fundamental programming concepts, common coding problems, and technology guides.

## Project Structure

```
program-and-docs/
├── java-program/          - Java source code and examples
│   └── src/
│       ├── ArrayProgram/  - Array operations and collections
│       ├── Concept/       - OOP concepts (inheritance, polymorphism, inner classes)
│       ├── File/          - File I/O operations
│       ├── practice/      - Practice programs
│       ├── mahendra/      - Additional practice programs
│       └── *.java         - Core programs (String, Math, Threads, etc.)
├── docs/                  - Technical documentation and reference guides
│   ├── java-collections.md
│   ├── java-design-patterns.md
│   ├── GraphQL-docs.md
│   ├── kotlin-features-over-java.md
│   ├── node-feature.md
│   ├── node-prisma-drizzle.md
│   ├── active-MQ-with-springboot.md
│   └── store-procedure.md
└── README.md
```

## Java Programs

The `java-program/` directory contains 80+ Java programs organized by topic:

- **Arrays & Collections** - ArrayList, LinkedList, HashMap, HashSet, array rotation, merge, deduplication
- **OOP Concepts** - Inheritance, polymorphism, constructors, inner classes, upcasting, downcasting, interfaces
- **String Operations** - Reverse, palindrome, permutations, StringBuffer, StringBuilder
- **Math Programs** - Factorial, Fibonacci, prime numbers, Armstrong numbers, area calculation
- **File Handling** - File read/write operations
- **Multithreading** - Thread creation and management
- **Exception Handling** - Try-catch-finally examples

### How to Run

```bash
cd java-program/src
javac ProgramName.java
java ProgramName
```

## Documentation

The `docs/` directory contains reference guides on various technologies:

| Document | Description |
|----------|-------------|
| [Java Collections](docs/java-collections.md) | Complete guide to the Java Collections Framework — Map, Set, List, and Queue implementations |
| [Java Design Patterns](docs/java-design-patterns.md) | Complete guide to creational, structural, and behavioral design patterns in Java |
| [GraphQL Guide](docs/GraphQL-docs.md) | GraphQL fundamentals including queries, mutations, response handling, and Spring Boot integration |
| [Kotlin vs Java](docs/kotlin-features-over-java.md) | Comprehensive comparison of Kotlin features over Java with syntax examples |
| [Node.js & Express.js](docs/node-feature.md) | Reference guide for Node.js and Express.js features, NPM commands, and application setup |
| [Prisma vs Drizzle](docs/node-prisma-drizzle.md) | Complete comparison of Prisma and Drizzle ORMs for Node.js |
| [ActiveMQ with Spring Boot](docs/active-MQ-with-springboot.md) | ActiveMQ messaging — queues, topics, retry mechanisms, DLQ, and acknowledgement modes |
| [PostgreSQL Stored Procedures](docs/store-procedure.md) | PostgreSQL functions and stored procedures — transactions, exception handling, and examples |
