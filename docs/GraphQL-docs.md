# GraphQL Guide

GraphQL fundamentals (queries, mutations, responses) and a detailed comparison of the GraphQL libraries used with Spring Boot: **Spring for GraphQL** (`spring-boot-starter-graphql`), **Netflix DGS**, **Apollo Kotlin** (`apollo-runtime`) and the legacy **GraphQL Java Kickstart**. Each library section explains what the dependency is, how to add it, and how to implement it in a Spring Boot application.

> **Version note:** Examples target **Spring Boot 3.x** (Java 17+). Where Spring Boot 4.x differs, it is called out. Library versions mentioned were current in October 2026: DGS 10.x (Boot 3) / 12.x (Boot 4), DGS Codegen 8.x, Apollo Kotlin 5.x.

> **Related guides:** [Java Spring Boot Annotations - Complete Guide](java-spring-boot-document-with-full-details.md) · [REST / Web MVC Annotations](java-spring-boot-document-with-full-details.md#4-rest--web-mvc-annotations) · [Testing Annotations](java-spring-boot-document-with-full-details.md#16-testing)

## Table of Contents

- [1. GraphQL Basics](#1-graphql-basics)
    - [1.1 Root Types](#11-root-types)
    - [1.2 Query Operations](#12-query-operations)
    - [1.3 Mutation Operations](#13-mutation-operations)
    - [1.4 Response Handling](#14-response-handling)
- [2. GraphQL Libraries for Spring Boot](#2-graphql-libraries-for-spring-boot)
    - [2.1 How the Libraries Fit Together](#21-how-the-libraries-fit-together)
    - [2.2 Server vs Client Libraries](#22-server-vs-client-libraries)
    - [2.3 Dependency Comparison](#23-dependency-comparison)
    - [2.4 Which One Should You Use?](#24-which-one-should-you-use)
- [3. Spring for GraphQL (spring-boot-starter-graphql)](#3-spring-for-graphql-spring-boot-starter-graphql)
    - [3.1 Dependencies](#31-dependencies)
    - [3.2 Configuration Properties](#32-configuration-properties)
    - [3.3 Schema](#33-schema)
    - [3.4 Controllers and Annotations](#34-controllers-and-annotations)
    - [3.5 Solving N+1 with @BatchMapping](#35-solving-n1-with-batchmapping)
    - [3.6 Error Handling](#36-error-handling)
    - [3.7 Subscriptions](#37-subscriptions)
    - [3.8 Testing](#38-testing)
    - [3.9 Calling Other GraphQL Services](#39-calling-other-graphql-services)
- [4. Netflix DGS](#4-netflix-dgs)
    - [4.1 Dependencies](#41-dependencies)
    - [4.2 Configuration Properties](#42-configuration-properties)
    - [4.3 Schema](#43-schema)
    - [4.4 Data Fetchers and Annotations](#44-data-fetchers-and-annotations)
    - [4.5 Data Loaders](#45-data-loaders)
    - [4.6 Error Handling](#46-error-handling)
    - [4.7 Code Generation](#47-code-generation)
    - [4.8 Testing](#48-testing)
    - [4.9 DGS GraphQL Client](#49-dgs-graphql-client)
- [5. Apollo Kotlin (apollo-runtime)](#5-apollo-kotlin-apollo-runtime)
    - [5.1 What apollo-runtime Is](#51-what-apollo-runtime-is)
    - [5.2 Dependencies](#52-dependencies)
    - [5.3 Schema and Operation Files](#53-schema-and-operation-files)
    - [5.4 Using ApolloClient in Spring Boot (Kotlin)](#54-using-apolloclient-in-spring-boot-kotlin)
    - [5.5 Using Apollo from Java](#55-using-apollo-from-java)
    - [5.6 Apollo on the Server Side (Federation)](#56-apollo-on-the-server-side-federation)
- [6. GraphQL Java Kickstart (Legacy)](#6-graphql-java-kickstart-legacy)
    - [6.1 Dependency and Status](#61-dependency-and-status)
    - [6.2 Resolver Implementation](#62-resolver-implementation)
    - [6.3 Migrating to Spring for GraphQL](#63-migrating-to-spring-for-graphql)
- [7. Same Feature in Each Library](#7-same-feature-in-each-library)
- [8. Microservice Communication](#8-microservice-communication)
    - [8.1 Choosing a Client](#81-choosing-a-client)
    - [8.2 Plain WebClient (No GraphQL Library)](#82-plain-webclient-no-graphql-library)
- [9. Key Points to Remember](#9-key-points-to-remember)
- [10. Best Practices](#10-best-practices)
- [11. Resources](#11-resources)

---

# 1. GraphQL Basics

## 1.1 Root Types

GraphQL has three special root types that define the operations available in your API:

| Type | Purpose | HTTP Equivalent |
|------|---------|----------------|
| **Query** | Reading data | GET operations |
| **Mutation** | Modifying data | POST/PUT/DELETE operations |
| **Subscription** | Real-time updates | WebSocket / Server-Sent Events |

[⬆ Back to Table of Contents](#table-of-contents)

---

## 1.2 Query Operations

### Basic Query

**Request:**
```graphql
query {
  users {
    id
    name
    email
  }
}
```

**Response:**
```json
{
  "data": {
    "users": [
      {
        "id": "1",
        "name": "John Doe",
        "email": "john@example.com"
      }
    ]
  }
}
```

### Query with Variables

**Query Definition:**
```graphql
query GetUser($userId: ID!) {
  user(id: $userId) {
    id
    name
    email
    posts {
      title
    }
  }
}
```

**Variables:**
```json
{
  "userId": "1"
}
```

**What is actually sent over HTTP** (`POST /graphql`, `Content-Type: application/json`):
```json
{
  "query": "query GetUser($userId: ID!) { user(id: $userId) { id name email posts { title } } }",
  "operationName": "GetUser",
  "variables": { "userId": "1" }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 1.3 Mutation Operations

### Basic Mutation (Inline Arguments)

**Request:**
```graphql
mutation {
  createUser(
    name: "Alice Cooper"
    email: "alice@example.com"
    age: 28
  ) {
    id
    name
    email
    age
  }
}
```

**Response:**
```json
{
  "data": {
    "createUser": {
      "id": "4",
      "name": "Alice Cooper",
      "email": "alice@example.com",
      "age": 28
    }
  }
}
```

### Mutation with Input Type

**Request:**
```graphql
mutation {
  createUser(input: {
    name: "John Doe"
    email: "john@example.com"
    age: 30
  }) {
    id
    name
    email
    age
  }
}
```

### Mutation with Variables

**Mutation Definition:**
```graphql
mutation CreateNewPost($title: String!, $content: String!, $authorId: ID!) {
  createPost(
    title: $title
    content: $content
    authorId: $authorId
  ) {
    id
    title
    content
    author {
      name
    }
  }
}
```

**Variables:**
```json
{
  "title": "My First Post",
  "content": "Hello World!",
  "authorId": "1"
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 1.4 Response Handling

GraphQL responses can have different patterns based on success or failure. Unlike REST, the HTTP status is usually **200 OK** even when there are errors; you must check the `errors` field.

### 1. Successful Response (No Errors)

```json
{
  "data": {
    "user": {
      "id": "1",
      "name": "John Doe"
    }
  }
}
```

> **Note:** No `errors` field is present when everything succeeds.

### 2. Partial Success (Some Fields Failed)

```json
{
  "data": {
    "user": {
      "id": "1",
      "name": "John Doe",
      "email": null
    }
  },
  "errors": [
    {
      "message": "Not authorized to view email",
      "path": ["user", "email"]
    }
  ]
}
```

> **Note:** Both `data` and `errors` are present.

### 3. Complete Failure (Query Couldn't Execute)

```json
{
  "data": {
    "user": null
  },
  "errors": [
    {
      "message": "User with ID 999 not found",
      "locations": [
        {
          "line": 2,
          "column": 3
        }
      ],
      "path": ["user"],
      "extensions": {
        "classification": "NOT_FOUND"
      }
    }
  ]
}
```

> **Note:** `data` is null, but `errors` explains why. Spring for GraphQL puts the error type in `extensions.classification`; DGS puts it in `extensions.errorType`.

### 4. Validation Error (Before Execution)

```json
{
  "errors": [
    {
      "message": "Cannot query field 'invalidField' on type 'User'",
      "locations": [
        {
          "line": 3,
          "column": 5
        }
      ]
    }
  ]
}
```

> **Note:** No `data` field at all (query didn't even execute).

[⬆ Back to Table of Contents](#table-of-contents)

---

# 2. GraphQL Libraries for Spring Boot

## 2.1 How the Libraries Fit Together

Every Java GraphQL **server** library is built on the same engine: **[graphql-java](https://www.graphql-java.com/)**. It parses the query, validates it against the schema and calls your code (a `DataFetcher`) for each field. You almost never add `graphql-java` yourself; the starters bring it in. The libraries differ in the **programming model** on top of it (annotations, resolvers, codegen) and the **Spring integration**.

```
                         YOUR SERVER CODE
  ┌──────────────────────┬──────────────────────┬──────────────────────────┐
  │ @Controller          │ @DgsComponent        │ implements               │
  │ @QueryMapping        │ @DgsQuery            │ GraphQLQueryResolver     │
  │ @SchemaMapping       │ @DgsData             │ GraphQLResolver<T>       │
  │ @BatchMapping        │ @DgsDataLoader       │                          │
  ├──────────────────────┼──────────────────────┼──────────────────────────┤
  │ Spring for GraphQL   │ Netflix DGS          │ GraphQL Java Kickstart   │
  │ spring-boot-starter- │ dgs-starter          │ graphql-spring-boot-     │
  │ graphql              │   │                  │ starter  (ARCHIVED)      │
  │                      │   │ runs ON TOP of   │                          │
  │        ▲─────────────┼───┘ Spring for GraphQL                          │
  └────────┼─────────────┴──────────────────────┴────────────┬─────────────┘
           ▼                                                 ▼
  ┌────────────────────────────────────────────────────────────────────────┐
  │                graphql-java  (parse → validate → execute)              │
  │                java-dataloader (batching / N+1)                        │
  └────────────────────────────────────────────────────────────────────────┘

                 CLIENT CODE (calling a GraphQL API)
  ┌──────────────────────┬──────────────────────┬──────────────────────────┐
  │ HttpGraphQlClient /  │ DGS GraphQLClient    │ Apollo Kotlin            │
  │ HttpSyncGraphQlClient│ + codegen query API  │ apollo-runtime           │
  │ (Spring for GraphQL) │ (Netflix DGS)        │ (generated typed models) │
  └──────────────────────┴──────────────────────┴──────────────────────────┘
```

Key facts:
- **Spring for GraphQL** is the official Spring project (Spring Boot 2.7+). `spring-boot-starter-graphql` auto-configures it.
- **Netflix DGS** started as a separate framework (open-sourced 2021). The current `dgs-starter` **integrates with Spring for GraphQL internally**: Spring handles HTTP/WebSocket transport and query execution, while DGS keeps its own annotations, data loaders, code generation and testing utilities.
- **Apollo Kotlin** (`apollo-runtime`) is a **client** library. It does not serve a GraphQL API; it calls one, using classes generated from your `.graphql` query files.
- **GraphQL Java Kickstart** was archived on **19 Dec 2023**; its maintainers recommend Spring for GraphQL.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 2.2 Server vs Client Libraries

| Question | Server library | Client library |
|----------|---------------|----------------|
| What does it do? | **Exposes** a `/graphql` endpoint, runs your resolvers | **Calls** someone else's `/graphql` endpoint |
| Where is the schema? | You write it (`.graphqls`) in your project | You download/copy the remote server's schema |
| Examples | Spring for GraphQL, Netflix DGS, Kickstart | Apollo Kotlin, Spring `HttpGraphQlClient`, DGS `GraphQLClient` |

A Spring Boot microservice can be **both**: it exposes its own GraphQL API with Spring for GraphQL or DGS, and calls another service's GraphQL API with one of the clients.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 2.3 Dependency Comparison

| Aspect | Spring for GraphQL | Netflix DGS | Apollo Kotlin | GraphQL Java Kickstart |
|--------|-------------------|-------------|---------------|------------------------|
| **Main dependency** | `org.springframework.boot:spring-boot-starter-graphql` | `com.netflix.graphql.dgs:dgs-starter` | `com.apollographql.apollo:apollo-runtime` | `com.graphql-java-kickstart:graphql-spring-boot-starter` |
| **Role** | Server (+ HTTP/WebSocket/RSocket client) | Server (+ client, codegen) | **Client only** | Server |
| **Maintained by** | Spring team | Netflix + community | Apollo GraphQL | Archived Dec 2023 (last release 15.1.0) |
| **Version management** | Spring Boot BOM (no version needed) | DGS BOM `graphql-dgs-platform-dependencies` | Gradle plugin + runtime (same version) | Manual |
| **Built on** | graphql-java | Spring for GraphQL + graphql-java | Own Kotlin runtime (OkHttp on JVM) | graphql-java + graphql-java-tools |
| **Programming model** | `@Controller` + `@QueryMapping` / `@SchemaMapping` | `@DgsComponent` + `@DgsQuery` / `@DgsData` | Generated classes from `.graphql` operation files | Resolver interfaces |
| **Default schema folder** | `src/main/resources/graphql/**` | `src/main/resources/schema/**` | `src/main/graphql/` (remote schema + operations) | Any `**/*.graphqls` on classpath |
| **N+1 batching** | `@BatchMapping`, `BatchLoaderRegistry` | `@DgsDataLoader` | n/a (client) | Manual `DataLoaderRegistry` |
| **Code generation** | Not built in (can use DGS Codegen) | DGS Codegen Gradle plugin (community Maven plugin) | Built in and **required** (Gradle plugin) | None |
| **Transports** | HTTP, WebSocket, SSE (subscriptions), RSocket | Via Spring for GraphQL (HTTP, WebSocket) | HTTP, WebSocket | HTTP, WebSocket |
| **Testing** | `@GraphQlTest` + `GraphQlTester` | `@EnableDgsTest` + `DgsQueryExecutor` (also `GraphQlTester`) | Mock HTTP server (WireMock, MockWebServer) | `GraphQLTestTemplate` |
| **Apollo Federation** | Yes (`@EntityMapping`, via federation-jvm) | Yes, built in (`@DgsEntityFetcher`) | n/a | Manual |
| **Language** | Java, Kotlin | Java, Kotlin | Kotlin first; Java via `com.apollographql.java:client` | Java, Kotlin |
| **Build tool** | Maven or Gradle | Maven or Gradle (codegen is best on Gradle) | Gradle (Maven only via community plugin) | Maven or Gradle |
| **Spring Boot versions** | Same as Boot | DGS 10.x → Boot 3 · DGS 11+ → Boot 4 | Independent of Boot | Boot 2 / early Boot 3 only |

[⬆ Back to Table of Contents](#table-of-contents)

---

## 2.4 Which One Should You Use?

| Situation | Use |
|-----------|-----|
| New Spring Boot GraphQL server, team knows Spring MVC | **Spring for GraphQL** (`spring-boot-starter-graphql`) |
| Large schema, many teams, want generated Java types and type-safe query builders | **Netflix DGS** (it runs on Spring for GraphQL, so you keep Spring features) |
| Already using DGS | Stay on DGS; upgrade to the `dgs-starter` (Spring for GraphQL based) |
| Kotlin service or Android app calling a GraphQL API, want compile-time checked queries | **Apollo Kotlin** (`apollo-runtime`) |
| Java Spring Boot service calling another GraphQL service | Spring **`HttpSyncGraphQlClient` / `HttpGraphQlClient`** (simplest), or DGS client + codegen |
| Project on GraphQL Java Kickstart | Migrate to **Spring for GraphQL** ([see 6.3](#63-migrating-to-spring-for-graphql)) |

> **Do not combine server libraries.** `dgs-starter` already pulls in Spring for GraphQL; don't add `spring-boot-starter-graphql` separately "just in case", and never add Kickstart next to either of them. DGS also recommends **not mixing** DGS annotations and Spring for GraphQL annotations in the same service.

[⬆ Back to Table of Contents](#table-of-contents)

---

# 3. Spring for GraphQL (spring-boot-starter-graphql)

**What it is:** the official Spring integration for GraphQL, built on graphql-java. Spring Boot auto-configures the schema, the `/graphql` endpoint, GraphiQL, WebSocket/RSocket transports, exception handling and test support. You write `@Controller` classes with annotations very similar to Spring MVC.

### How a request flows

```
POST /graphql  { "query": "...", "variables": { ... } }
   │
   ▼
GraphQlHttpHandler (Spring MVC or WebFlux)
   │
   ▼
WebGraphQlInterceptor chain        ← read headers, put values into GraphQLContext
   │
   ▼
ExecutionGraphQlService ──► graphql-java: parse → validate → execute
   │
   ▼
DataFetchers created from your @Controller methods
   (@QueryMapping, @MutationMapping, @SchemaMapping, @BatchMapping ...)
   │
   ▼  (if a method throws)
@GraphQlExceptionHandler / DataFetcherExceptionResolver → GraphQLError
   │
   ▼
HTTP 200  { "data": { ... }, "errors": [ ... ] }
```

---

## 3.1 Dependencies

`spring-boot-starter-graphql` contains Spring for GraphQL, graphql-java and the auto-configuration. You also need **one transport starter**.

**Maven:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-graphql</artifactId>
</dependency>

<!-- Transport: Spring MVC (HTTP). Use spring-boot-starter-webflux for reactive HTTP + WebSocket -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Optional: WebSocket subscriptions on Spring MVC -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>

<!-- Optional: @Valid on @Argument -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Testing: @GraphQlTest, GraphQlTester -->
<dependency>
    <groupId>org.springframework.graphql</groupId>
    <artifactId>spring-graphql-test</artifactId>
    <scope>test</scope>
</dependency>
```

**Gradle (Kotlin DSL):**
```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.graphql:spring-graphql-test")
}
```

| Transport starter | Transport | Implementation |
|-------------------|-----------|----------------|
| `spring-boot-starter-web` | HTTP (+ SSE for subscriptions) | Spring MVC |
| `spring-boot-starter-websocket` | WebSocket | WebSocket for Servlet apps |
| `spring-boot-starter-webflux` | HTTP, WebSocket | Spring WebFlux |
| `spring-boot-starter-rsocket` | TCP, WebSocket | RSocket on Reactor Netty |

No version is needed; the Spring Boot BOM manages Spring for GraphQL and graphql-java.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 3.2 Configuration Properties

```properties
# Endpoint (Spring Boot 3.5+; before 3.5 the property is spring.graphql.path)
spring.graphql.http.path=/graphql

# GraphiQL UI (disabled by default; enabled automatically with spring-boot-devtools)
spring.graphql.graphiql.enabled=true
spring.graphql.graphiql.path=/graphiql

# Schema files
spring.graphql.schema.locations=classpath:graphql/**/
spring.graphql.schema.file-extensions=.graphqls,.gqls

# Expose the schema as text at /graphql/schema
spring.graphql.schema.printer.enabled=true

# Turn off introspection in production if the schema should be hidden
spring.graphql.schema.introspection.enabled=true

# WebSocket endpoint (off until this property is set)
spring.graphql.websocket.path=/graphql

# CORS for the GraphQL endpoint
spring.graphql.cors.allowed-origins=https://example.org
spring.graphql.cors.allowed-methods=GET,POST
```

| Property | Default | Description |
|----------|---------|-------------|
| `spring.graphql.http.path` (`spring.graphql.path` before 3.5) | `/graphql` | HTTP endpoint path |
| `spring.graphql.graphiql.enabled` | `false` | Turn on the GraphiQL page |
| `spring.graphql.graphiql.path` | `/graphiql` | GraphiQL page path |
| `spring.graphql.schema.locations` | `classpath:graphql/**/` | Where to find schema files. Use `classpath*:graphql/**/` to include schemas from other JARs |
| `spring.graphql.schema.file-extensions` | `.graphqls`, `.gqls` | Schema file extensions |
| `spring.graphql.schema.printer.enabled` | `false` | Expose schema at `{path}/schema` |
| `spring.graphql.schema.introspection.enabled` | `true` | Allow `__schema` / `__type` queries (GraphiQL needs it) |
| `spring.graphql.websocket.path` | (none) | Enables the WebSocket endpoint |
| `spring.graphql.rsocket.mapping` | (none) | Route for GraphQL over RSocket |
| `spring.graphql.cors.*` | (none) | CORS settings for the endpoint |

[⬆ Back to Table of Contents](#table-of-contents)

---

## 3.3 Schema

Spring for GraphQL is **schema-first**: you write the schema, then implement it. Put it in `src/main/resources/graphql/schema.graphqls`:

```graphql
type Query {
  post(id: ID!): Post
  posts: [Post!]!
}

type Mutation {
  createPost(input: CreatePostInput!): Post!
}

type Subscription {
  postCreated: Post!
}

type Post {
  id: ID!
  title: String!
  content: String!
  published: Boolean!
  author: Author!
}

type Author {
  id: ID!
  name: String!
}

input CreatePostInput {
  title: String!
  content: String!
  authorId: ID!
}
```

### Kinds of types in this schema

| Keyword in schema | Kind | Types in this schema | What it is for |
|-------------------|------|----------------------|----------------|
| `type Query` | Root operation type | `Query` | Entry points for **reading** data. Every schema must have it |
| `type Mutation` | Root operation type | `Mutation` | Entry points for **changing** data (optional) |
| `type Subscription` | Root operation type | `Subscription` | Entry points for **real-time events** (optional) |
| `type` | Object type | `Post`, `Author` | Shape of data the server **returns**. Can only be used as output |
| `input` | Input object type | `CreatePostInput` | Shape of data the client **sends** as an argument. Can only be used as input |
| (built in) | Scalar types | `ID`, `String`, `Boolean` | Leaf values (no sub-fields). Every query ends in scalars |

A field is written as `name(arguments): ReturnType`. Arguments are optional, and the return type may be wrapped in `[ ]` (list) and/or followed by `!` (non-null).

### Built-in scalar types

| Scalar | Meaning | Example value | Typical Java type |
|--------|---------|---------------|-------------------|
| `ID` | Unique identifier. Always **serialized as a string** in JSON; accepts `"1"` or `1` as input | `"42"` | `String`, or `Long` (Spring converts it) |
| `String` | UTF-8 text | `"Hello"` | `String` |
| `Int` | Signed 32-bit integer | `28` | `Integer` / `int` |
| `Float` | Double-precision number | `4.5` | `Double` / `double` |
| `Boolean` | `true` / `false` | `true` | `Boolean` / `boolean` |

Other kinds of types not used in this schema: `enum` (fixed set of values, e.g. `enum Status { DRAFT PUBLISHED }`), `interface` and `union` (a field that can return different object types), and custom `scalar` (e.g. `scalar DateTime`, usually from the `graphql-java-extended-scalars` library).

### Each type in this schema

**`type Query`**: what clients can read

| Field | Meaning |
|-------|---------|
| `post(id: ID!): Post` | Takes a **required** `id` argument. Returns one `Post`, or `null` when no post has that id |
| `posts: [Post!]!` | No arguments. Always returns a list (may be empty); the list never contains `null` items |

**`type Mutation`**: what clients can change

| Field | Meaning |
|-------|---------|
| `createPost(input: CreatePostInput!): Post!` | Takes a **required** input object and always returns the created `Post` |

**`type Subscription`**: what clients can listen to

| Field | Meaning |
|-------|---------|
| `postCreated: Post!` | The client opens a WebSocket/SSE stream; each event pushed to it is a non-null `Post` |

**`type Post`**: a post returned by the server

| Field | Meaning |
|-------|---------|
| `id: ID!` | Always present |
| `title: String!` / `content: String!` | Always present text |
| `published: Boolean!` | Always `true` or `false`, never `null` |
| `author: Author!` | A **relationship**: the field's type is another object type, so the client can ask `post { author { name } }`. Resolved by a separate method ([3.4](#34-controllers-and-annotations), [3.5](#35-solving-n1-with-batchmapping)) |

**`type Author`**: an author returned by the server. `id: ID!` and `name: String!` are always present.

**`input CreatePostInput`**: the data a client sends to `createPost`. All three fields (`title`, `content`, `authorId`) must be sent.

**Why a separate `input` type instead of reusing `Post`?**
- The GraphQL spec doesn't allow object types (`type`) as arguments; arguments must be scalars, enums or `input` types.
- The client shouldn't send `id` (the server generates it), `published` or a full `author` object; it only sends `authorId`.
- Output types can have fields with arguments and cycles (`Post → Author → posts → Post ...`); input types can't, so they stay simple.

### The `!` sign (Non-Null)

`!` means **non-null**. In GraphQL **every type is nullable by default**; adding `!` removes `null` as an allowed value.

> This is the **opposite of Kotlin**: in Kotlin `String` is non-null and `String?` is nullable. In GraphQL, `String` is nullable and `String!` is non-null.

`!` means something slightly different depending on where it is used:

| Where `!` is used | Example | Meaning | If the rule is broken |
|-------------------|---------|---------|-----------------------|
| **Argument** | `post(id: ID!)` | The client **must** pass a value, and it can't be `null` | **Validation error**; the query is not executed at all |
| **Input field** | `title: String!` in `CreatePostInput` | The client **must** include the field in the input object | **Validation error** |
| **Output field** | `title: String!` in `Post` | The server **promises** the value is never `null` | Error added to `errors`, and `null` **propagates to the parent** (see below) |

`!` only checks for `null`. It does **not** reject an empty string `""` or an empty list `[]`. That is why `CreatePostInput` below also has `@NotBlank`.

### `!` with lists

The position of `!` matters: `!` inside the brackets is about the **items**, `!` outside is about the **list itself**.

| Type | Can the list be `null`? | Can an item be `null`? | Allowed values |
|------|------------------------|------------------------|----------------|
| `[Post]` | Yes | Yes | `null`, `[]`, `[p1, null]` |
| `[Post!]` | Yes | No | `null`, `[]`, `[p1, p2]` |
| `[Post]!` | No | Yes | `[]`, `[p1, null]` |
| `[Post!]!` | No | No | `[]`, `[p1, p2]` |

`posts: [Post!]!` is the most common choice: "always a list, possibly empty, with no null items".

### What happens when a non-null output field is `null`

If a resolver returns `null` (or throws) for a field declared with `!`, GraphQL can't return `null` there. It sets the **nearest nullable parent** to `null` instead and adds an error. This is called **null propagation** (or "null bubbling").

Example: `posts` returns 3 posts, but the second post's `title` is `null`:

```graphql
query { posts { id title } }
```

```
Post.title  (String!)    is null  → not allowed → set its parent Post to null
Post item   (Post!)      is null  → not allowed → set the list to null
posts       ([Post!]!)   is null  → not allowed → set "data" to null
```

```json
{
  "data": null,
  "errors": [
    {
      "message": "The field at path '/posts[1]/title' was declared as a non null type, but the code involved in retrieving data has wrongly returned a null value. ...",
      "path": ["posts", 1, "title"]
    }
  ]
}
```

One bad `title` wiped out the whole response. If the schema had been `posts: [Post]!`, only the broken item would become `null` and the other posts would still be returned:

```json
{
  "data": {
    "posts": [ { "id": "1", "title": "First" }, null, { "id": "3", "title": "Third" } ]
  },
  "errors": [ { "message": "...", "path": ["posts", 1, "title"] } ]
}
```

This is also why `post(id: ID!): Post` returns a **nullable** `Post`: "not found" becomes `"post": null` instead of an error that removes the rest of the response.

### `!` and Java types

| Schema | Java | Why |
|--------|------|-----|
| `published: Boolean!` | `boolean published` | Never `null`, so a primitive is fine |
| `age: Int` (nullable) | `Integer age` | Must be able to hold `null`; a primitive `int` can't |
| `post(id: ID!)` | `@Argument Long id` | Always present; Spring rejects the query before your method runs if it is missing |
| `post(titleFilter: String)` (nullable argument) | `@Argument String titleFilter` | May be `null` when the client omits it |
| `title: String!` in an `input` | `@NotBlank String title` | `!` guarantees presence; `@NotBlank` also rejects `""` |

### Choosing where to put `!`

- **Arguments and input fields:** use `!` when the operation can't work without the value (`id`, `title`).
- **Output fields:** use `!` for values that always exist (`id`, `title`). Leave fields **nullable** when the value can be missing, or when it comes from another service or a slow call that might fail, so one failure doesn't null out the parent.
- **Lists:** prefer `[T!]!`; return `[]` instead of `null` when there is nothing.
- **Schema evolution:**
    - For **output** fields, changing `String` to `String!` is safe for clients; changing `String!` to `String` breaks clients that don't handle `null`.
    - For **arguments**, it is the reverse: making an argument required (`String` to `String!`) breaks existing clients that don't send it.

Java types used in the examples (the same types are reused in the DGS section):

```java
public record Post(Long id, String title, String content, boolean published, Long authorId) {}

public record Author(Long id, String name) {}

public record CreatePostInput(
        @NotBlank @Size(max = 200) String title,
        @NotBlank String content,
        @NotNull Long authorId) {}
```

> `Post` has `authorId`, not `author`. The `author` field in the schema is resolved by a separate method (`@SchemaMapping` / `@BatchMapping`), so the author is loaded **only when the client asks for it**.

On startup, Spring checks the schema against your controllers (**schema mapping inspection**) and logs fields that have no data fetcher and no matching Java property.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 3.4 Controllers and Annotations

```java
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import jakarta.validation.Valid;

import java.util.List;

@Controller
public class PostController {

    private final PostService postService;
    private final AuthorService authorService;

    public PostController(PostService postService, AuthorService authorService) {
        this.postService = postService;
        this.authorService = authorService;
    }

    // Query.post(id: ID!)  -> method name = field name
    @QueryMapping
    public Post post(@Argument Long id) {          // ID (String) is converted to Long
        return postService.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }

    // Query.posts
    @QueryMapping
    public List<Post> posts() {
        return postService.findAll();
    }

    // Mutation.createPost(input: CreatePostInput!)
    @MutationMapping
    public Post createPost(@Argument @Valid CreatePostInput input) {
        return postService.create(input);
    }

    // Post.author  -> called once PER Post (see 3.5 for the batched version)
    @SchemaMapping(typeName = "Post", field = "author")
    public Author author(Post post) {              // parent object is injected
        return authorService.findById(post.authorId());
    }
}
```

Fields such as `Post.title` don't need a method: Spring reads them from the `Post` record by name.

### Annotation reference

| Annotation | Target | What it does | Attributes |
|------------|--------|--------------|------------|
| `@Controller` | Class | Registers the class; its annotated methods become GraphQL data fetchers | `value` (bean name) |
| `@QueryMapping` | Method | Handles a field of `Query` | `name` / `value`: field name (default = method name) |
| `@MutationMapping` | Method | Handles a field of `Mutation` | `name` / `value` |
| `@SubscriptionMapping` | Method | Handles a field of `Subscription`; returns `Flux` / `Publisher` | `name` / `value` |
| `@SchemaMapping` | Method, Class | Handles a field of **any** type (nested fields) | `typeName` (default from parent parameter type or class-level annotation), `field` / `value` (default = method name) |
| `@BatchMapping` | Method | Loads a field for **many parents in one call** (N+1 fix) | `typeName`, `field` / `value`, `maxBatchSize` |
| `@Argument` | Parameter | Binds one GraphQL argument (with type conversion and validation) | `name` / `value` (default = parameter name) |
| `@Arguments` | Parameter | Binds **all** arguments onto one object | — |
| `@ContextValue` | Parameter | Value from the request `GraphQLContext` (e.g. set by an interceptor) | `name` / `value`, `required` |
| `@LocalContextValue` | Parameter | Value from the field's local context | `name` / `value`, `required` |
| `@GraphQlExceptionHandler` | Method | Maps an exception to a `GraphQLError` (in a `@Controller` or `@ControllerAdvice`) | `value`: exception types |
| `@EntityMapping` | Method | Resolves a federated entity (Apollo Federation) | `name` |

Other parameters a controller method can declare without annotations: the parent object (for `@SchemaMapping`), `DataFetchingEnvironment`, `GraphQLContext`, `DataFetchingFieldSelectionSet` (which fields the client asked for), `Principal` / `@AuthenticationPrincipal` (Spring Security), `Locale`.

> **Security:** `@PreAuthorize` and `@Secured` work on controller methods the same way as in Spring MVC (see the Spring Security section of the [Spring Boot guide](java-spring-boot-document-with-full-details.md#10-spring-security)).

[⬆ Back to Table of Contents](#table-of-contents)

---

## 3.5 Solving N+1 with @BatchMapping

With the `@SchemaMapping` above, this query calls `authorService.findById` **once per post**:

```graphql
query { posts { title author { name } } }   # 100 posts → 1 + 100 calls  (N+1 problem)
```

`@BatchMapping` collects all `Post` parents of one request and loads their authors **in one call** (it uses graphql-java's `DataLoader` internally):

```java
@Controller
public class PostAuthorController {

    private final AuthorService authorService;

    public PostAuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    // typeName "Post" comes from List<Post>, field "author" from the method name
    @BatchMapping
    public Map<Post, Author> author(List<Post> posts) {
        Set<Long> authorIds = posts.stream().map(Post::authorId).collect(Collectors.toSet());

        Map<Long, Author> authorsById = authorService.findAllById(authorIds).stream()
                .collect(Collectors.toMap(Author::id, Function.identity()));

        Map<Post, Author> result = new LinkedHashMap<>();
        for (Post post : posts) {
            result.put(post, authorsById.get(post.authorId()));
        }
        return result;
    }
}
```

- Return `Map<Parent, Value>` or `List<Value>` (same order as the parents). Async variants `Mono<Map<..>>` / `Flux<..>` are also supported.
- Remove the per-item `@SchemaMapping` for the same field; only one data fetcher per field is allowed.
- For full control, register loaders with `BatchLoaderRegistry` and use `DataLoader<K, V>` as a parameter of a `@SchemaMapping` method.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 3.6 Error Handling

If a controller throws and nothing handles it, Spring returns a generic error (`"INTERNAL_ERROR for <executionId>"`, classification `INTERNAL_ERROR`) and **hides the exception message**. Map your own exceptions with `@GraphQlExceptionHandler`:

```java
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolationException;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GraphQlErrorHandler {

    @GraphQlExceptionHandler
    public GraphQLError handleNotFound(PostNotFoundException ex, DataFetchingEnvironment env) {
        return GraphqlErrorBuilder.newError(env)        // fills in path + location
                .errorType(ErrorType.NOT_FOUND)
                .message(ex.getMessage())
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleValidation(ConstraintViolationException ex, DataFetchingEnvironment env) {
        return GraphqlErrorBuilder.newError(env)
                .errorType(ErrorType.BAD_REQUEST)
                .message("Invalid input: " + ex.getMessage())
                .build();
    }
}
```

| `ErrorType` (Spring) | Use for |
|----------------------|---------|
| `BAD_REQUEST` | Invalid input |
| `UNAUTHORIZED` | Not authenticated |
| `FORBIDDEN` | Authenticated but not allowed |
| `NOT_FOUND` | Entity does not exist |
| `INTERNAL_ERROR` | Unexpected server error |

Alternative (older style): a `@Component` extending `DataFetcherExceptionResolverAdapter` and overriding `resolveToSingleError(Throwable, DataFetchingEnvironment)`.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 3.7 Subscriptions

```properties
spring.graphql.websocket.path=/graphql
```

```java
@Service
public class PostEventPublisher {

    private final Sinks.Many<Post> sink = Sinks.many().multicast().onBackpressureBuffer();

    public void publish(Post post) {
        sink.tryEmitNext(post);
    }

    public Flux<Post> stream() {
        return sink.asFlux();
    }
}

@Controller
public class PostSubscriptionController {

    private final PostEventPublisher publisher;

    public PostSubscriptionController(PostEventPublisher publisher) {
        this.publisher = publisher;
    }

    @SubscriptionMapping
    public Flux<Post> postCreated() {
        return publisher.stream();
    }
}
```

- WebSocket uses the `graphql-transport-ws` protocol. Spring MVC apps need `spring-boot-starter-websocket`; WebFlux apps need nothing extra.
- Over HTTP, subscriptions can also be streamed as Server-Sent Events (`Accept: text/event-stream`).
- Call `publisher.publish(post)` from `PostService.create(...)` to push new posts to subscribers.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 3.8 Testing

`@GraphQlTest` is a test slice: it starts only the GraphQL infrastructure and the listed controllers (no web server, no database).

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@GraphQlTest(PostController.class)
class PostControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean                      // Boot 3.4+ (use @MockBean on older versions)
    private PostService postService;

    @MockitoBean
    private AuthorService authorService;

    @Test
    void returnsPostById() {
        given(postService.findById(1L))
                .willReturn(Optional.of(new Post(1L, "Hello", "World", true, 10L)));

        graphQlTester.document("""
                        query { post(id: 1) { id title } }
                        """)
                .execute()
                .path("post.title").entity(String.class).isEqualTo("Hello");
    }

    @Test
    void unknownPostReturnsError() {
        given(postService.findById(99L)).willReturn(Optional.empty());

        graphQlTester.document("query { post(id: 99) { id } }")
                .execute()
                .errors()
                .satisfy(errors -> assertThat(errors).hasSize(1));
    }
}
```

| Tester | Use |
|--------|-----|
| `GraphQlTester` (with `@GraphQlTest`) | Controller slice tests, no HTTP |
| `HttpGraphQlTester` (with `@SpringBootTest` + `@AutoConfigureHttpGraphQlTester`) | Full app over MockMvc / WebTestClient |
| `WebSocketGraphQlTester` | Subscriptions over a running server |

Queries can also be stored in `src/test/resources/graphql-test/postById.graphql` and loaded with `graphQlTester.documentName("postById")`.

> On Spring Boot 4.x the auto-configured test annotations moved to new modules/packages; the API (`GraphQlTester`, `document(...)`, `path(...)`) is the same.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 3.9 Calling Other GraphQL Services

Spring for GraphQL also contains a **client**. For a client-only service you can depend on `org.springframework.graphql:spring-graphql` (managed by Boot) instead of the full starter.

| Client | Based on | Returns |
|--------|----------|---------|
| `HttpSyncGraphQlClient` (1.3+) | `RestClient` (blocking) | Plain objects |
| `HttpGraphQlClient` | `WebClient` (reactive) | `Mono` / `Flux` |
| `WebSocketGraphQlClient` | WebSocket | `Flux` (subscriptions) |
| `RSocketGraphQlClient` | RSocket | `Mono` / `Flux` |

```java
@Configuration
public class GraphQlClientConfig {

    @Bean
    public HttpSyncGraphQlClient userServiceClient(RestClient.Builder builder) {
        RestClient restClient = builder.baseUrl("http://user-service:8081/graphql").build();
        return HttpSyncGraphQlClient.builder(restClient)
                .header("X-Caller", "post-service")
                .build();
    }
}

public record UserDto(String id, String name, String email) {}

@Service
public class UserClient {

    private final HttpSyncGraphQlClient graphQlClient;

    public UserClient(HttpSyncGraphQlClient graphQlClient) {
        this.graphQlClient = graphQlClient;
    }

    public UserDto getUser(Long id) {
        return graphQlClient.document("""
                        query GetUser($id: ID!) {
                          user(id: $id) { id name email }
                        }
                        """)
                .variable("id", id)
                .retrieveSync("user")              // path inside "data"
                .toEntity(UserDto.class);          // throws FieldAccessException if "user" has errors
    }
}
```

Reactive version: `HttpGraphQlClient.create(webClient)` and `.retrieve("user").toEntity(UserDto.class)` returning `Mono<UserDto>`. Documents can also live in `src/main/resources/graphql-documents/` and be loaded with `.documentName("getUser")`.

[⬆ Back to Table of Contents](#table-of-contents)

---

# 4. Netflix DGS

**What it is:** the **D**omain **G**raph **S**ervice framework, built and used by Netflix for its federated GraphQL architecture. It provides its own annotation model (`@DgsComponent`, `@DgsQuery`, `@DgsData`), data loaders, a **code generation** plugin that creates Java/Kotlin types and type-safe query builders from the schema, a GraphQL client, test utilities and built-in Apollo Federation support.

**Relationship with Spring for GraphQL:** the current DGS starter **runs on Spring for GraphQL**. The schema is built by DGS, but HTTP/WebSocket handling and query execution are done by Spring for GraphQL's `ExecutionGraphQlService`; `DgsQueryExecutor` is a proxy on top of it. So you get DGS's programming model **plus** Spring features such as `@ControllerAdvice` + `@GraphQlExceptionHandler`, schema inspection and `GraphQlTester`.

```
@DgsComponent / @DgsQuery / @DgsDataLoader   ← DGS programming model + codegen
            │
            ▼
DGS builds the GraphQLSchema
            │
            ▼
Spring for GraphQL: GraphQlHttpHandler → ExecutionGraphQlService
            │
            ▼
graphql-java
```

| DGS version | Spring Boot | Status |
|-------------|-------------|--------|
| 5.x | 2.x (JDK 8) | No longer maintained |
| 10.x | 3.x (JDK 17) | Most features backported until second half of 2026 |
| 11.x, 12.x | 4.x | Actively maintained |

---

## 4.1 Dependencies

DGS uses a **BOM** (`graphql-dgs-platform-dependencies`) so that all DGS modules and their transitive dependencies (graphql-java, etc.) have matching versions.

**Maven:**
```xml
<properties>
    <!-- 10.x for Spring Boot 3, 11+ for Spring Boot 4 -->
    <dgs.version>10.6.0</dgs.version>
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.netflix.graphql.dgs</groupId>
            <artifactId>graphql-dgs-platform-dependencies</artifactId>
            <version>${dgs.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <!-- DGS starter (built on Spring for GraphQL) -->
    <dependency>
        <groupId>com.netflix.graphql.dgs</groupId>
        <artifactId>dgs-starter</artifactId>
    </dependency>

    <!-- Web flavor: spring-boot-starter-web OR spring-boot-starter-webflux -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Testing -->
    <dependency>
        <groupId>com.netflix.graphql.dgs</groupId>
        <artifactId>dgs-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

**Gradle (Kotlin DSL):**
```kotlin
plugins {
    id("com.netflix.dgs.codegen") version "8.7.0"     // optional, see 4.7
}

dependencies {
    implementation(platform("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:10.6.0"))
    implementation("com.netflix.graphql.dgs:dgs-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("com.netflix.graphql.dgs:dgs-starter-test")
}
```

> **Old artifact names:** many tutorials still use `com.netflix.graphql.dgs:graphql-dgs-spring-boot-starter` (the pre-Spring-for-GraphQL starter). New projects should use `dgs-starter` / `dgs-starter-test`. Spring Initializr offers "Netflix DGS" and "DGS Code Generation" as dependencies.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 4.2 Configuration Properties

Where DGS and Spring for GraphQL properties overlap, **the DGS property wins**:

| Property | Default | Description |
|----------|---------|-------------|
| `dgs.graphql.schema-locations` | `classpath*:schema/**/*.graphql*` | Schema files (use instead of `spring.graphql.schema.locations`) |
| `dgs.graphql.graphiql.enabled` | `true` | GraphiQL UI (use instead of `spring.graphql.graphiql.enabled`) |
| `dgs.graphql.graphiql.path` | `/graphiql` | GraphiQL path |
| `dgs.graphql.spring.webmvc.asyncdispatch.enabled` | `false` | Use Spring MVC async dispatch for GraphQL requests |
| `spring.graphql.websocket.path` | (none) | Enable WebSocket subscriptions (plus `spring-boot-starter-websocket` on MVC) |
| `spring.graphql.schema.printer.enabled` | `false` | Expose schema text at `/graphql/schema` |

[⬆ Back to Table of Contents](#table-of-contents)

---

## 4.3 Schema

DGS is schema-first too, but its default folder is **`src/main/resources/schema/`**:

```
src/main/resources/schema/schema.graphqls
```

Use the same schema as in [3.3](#33-schema).

[⬆ Back to Table of Contents](#table-of-contents)

---

## 4.4 Data Fetchers and Annotations

```java
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import com.netflix.graphql.dgs.exceptions.DgsEntityNotFoundException;
import org.dataloader.DataLoader;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@DgsComponent
public class PostDataFetcher {

    private final PostService postService;

    public PostDataFetcher(PostService postService) {
        this.postService = postService;
    }

    // Query.post(id: ID!)
    @DgsQuery
    public Post post(@InputArgument String id) {
        return postService.findById(Long.valueOf(id))
                .orElseThrow(() -> new DgsEntityNotFoundException("Post " + id + " not found"));
    }

    // Query.posts
    @DgsQuery
    public List<Post> posts() {
        return postService.findAll();
    }

    // Mutation.createPost(input: CreatePostInput!)
    @DgsMutation
    public Post createPost(@InputArgument CreatePostInput input) {
        return postService.create(input);
    }

    // Post.author  -> uses a data loader (see 4.5), so authors are batched
    @DgsData(parentType = "Post", field = "author")
    public CompletableFuture<Author> author(DgsDataFetchingEnvironment dfe) {
        Post post = dfe.getSource();
        DataLoader<Long, Author> loader = dfe.getDataLoader(AuthorDataLoader.class);
        return loader.load(post.authorId());
    }
}
```

### Annotation reference

| Annotation | Target | What it does | Attributes |
|------------|--------|--------------|------------|
| `@DgsComponent` | Class | Spring component that holds data fetchers, data loaders, scalars | — |
| `@DgsQuery` | Method | Handles a field of `Query` | `field` (default = method name) |
| `@DgsMutation` | Method | Handles a field of `Mutation` | `field` |
| `@DgsSubscription` | Method | Handles a field of `Subscription`; returns `Publisher` | `field` |
| `@DgsData` | Method | Handles a field of **any** type | `parentType`, `field` (default = method name) |
| `@InputArgument` | Parameter | Binds a GraphQL argument (scalars, input types, lists) | `name` (default = parameter name) |
| `@DgsDataLoader` | Class, Field | Registers a `BatchLoader` / `MappedBatchLoader` | `name`, `caching`, `batching`, `maxBatchSize` |
| `@DgsScalar` | Class | Registers a custom scalar (`Coercing` implementation) | `name` |
| `@DgsEntityFetcher` | Method | Resolves a federated entity (Apollo Federation `_entities`) | `name` (type name) |
| `@DgsTypeDefinitionRegistry` | Method | Adds schema types programmatically | — |

Useful method parameters: `DgsDataFetchingEnvironment` (graphql-java's `DataFetchingEnvironment` + `getDataLoader(Class)`), `DgsContext`, `@RequestHeader` (HTTP header).

> **ID type:** GraphQL `ID` arrives as a `String`. DGS Codegen also maps `ID` to `String`, which is why the example takes `String id`.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 4.5 Data Loaders

A data loader receives all keys requested in one GraphQL request and loads them together.

```java
import com.netflix.graphql.dgs.DgsDataLoader;
import org.dataloader.MappedBatchLoader;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;

@DgsDataLoader(name = "authors")
public class AuthorDataLoader implements MappedBatchLoader<Long, Author> {

    private final AuthorService authorService;

    public AuthorDataLoader(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Override
    public CompletionStage<Map<Long, Author>> load(Set<Long> authorIds) {
        return CompletableFuture.supplyAsync(() ->
                authorService.findAllById(authorIds).stream()
                        .collect(Collectors.toMap(Author::id, Function.identity())));
    }
}
```

| Interface | Input → Output | Use when |
|-----------|----------------|----------|
| `BatchLoader<K, V>` | `List<K>` → `List<V>` (same order, same size) | Every key always has a value |
| `MappedBatchLoader<K, V>` | `Set<K>` → `Map<K, V>` | Some keys may have no value |

The data fetcher must return the `CompletableFuture` from `loader.load(key)`; returning `.join()` would break batching.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 4.6 Error Handling

Any `RuntimeException` from a data fetcher becomes an error of type `INTERNAL`. Some exceptions are mapped automatically:

| Exception | `errorType` |
|-----------|-------------|
| `DgsEntityNotFoundException` | `NOT_FOUND` |
| `AccessDeniedException` (Spring Security) | `PERMISSION_DENIED` |
| Any other `RuntimeException` | `INTERNAL` |

Custom mapping, option 1 (recommended): Spring for GraphQL's `@ControllerAdvice` works in DGS too:

```java
@ControllerAdvice
public class DgsErrorHandler {

    @GraphQlExceptionHandler
    public GraphQLError handle(IllegalArgumentException ex) {
        return GraphQLError.newError()
                .errorType(com.netflix.graphql.types.errors.ErrorType.BAD_REQUEST)
                .message(ex.getMessage())
                .build();
    }
}
```

Option 2: a `@Component` implementing graphql-java's `DataFetcherExceptionHandler`. Delegate unknown exceptions to `DefaultDataFetcherExceptionHandler`, or the built-in mapping is lost.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 4.7 Code Generation

DGS Codegen reads your schema at build time and generates:
- **Types**: POJOs (or Kotlin data classes) for every type and input (`<packageName>.types.Post`, `CreatePostInput`), with builders.
- **Constants**: `DgsConstants.QUERY.Post`, `DgsConstants.POST.Author` (type and field names, no string typos).
- **Client API** (`generateClient = true`): `PostGraphQLQuery` and `PostProjectionRoot` to build queries in a type-safe way.

**Gradle (Kotlin DSL):**
```kotlin
plugins {
    id("com.netflix.dgs.codegen") version "8.7.0"
}

tasks.generateJava {
    schemaPaths.add("${projectDir}/src/main/resources/schema")
    packageName = "com.example.posts.codegen"
    generateClient = true
}
```

The `generateJava` task runs as part of the build and writes code to `build/generated` (added to the classpath automatically). On a Kotlin project it generates Kotlin code, despite the task name.

**Maven:** use the community plugin `io.github.deweyjose:graphqlcodegen-maven-plugin` (built on the same codegen core).

With codegen, the data fetcher uses the generated types and constants:

```java
@DgsData(parentType = DgsConstants.POST.TYPE_NAME, field = DgsConstants.POST.Author)
public CompletableFuture<Author> author(DgsDataFetchingEnvironment dfe) { ... }
```

> Codegen is not tied to the DGS runtime. Spring for GraphQL projects can use it only to generate types or client classes.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 4.8 Testing

`@EnableDgsTest` starts only the DGS/GraphQL infrastructure; `DgsQueryExecutor` runs queries without HTTP.

```java
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.test.EnableDgsTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = {PostDataFetcher.class, AuthorDataLoader.class})
@EnableDgsTest
class PostDataFetcherTest {

    @Autowired
    private DgsQueryExecutor dgsQueryExecutor;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private AuthorService authorService;

    @Test
    void returnsPostTitles() {
        given(postService.findAll()).willReturn(List.of(new Post(1L, "Hello", "World", true, 10L)));

        List<String> titles = dgsQueryExecutor.executeAndExtractJsonPath(
                "{ posts { title } }",
                "data.posts[*].title");

        assertThat(titles).containsExactly("Hello");
    }
}
```

Because DGS runs on Spring for GraphQL, `@SpringBootTest` + `@AutoConfigureHttpGraphQlTester` + `HttpGraphQlTester` also work for full HTTP-level tests.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 4.9 DGS GraphQL Client

DGS includes a client (`com.netflix.graphql.dgs:graphql-dgs-client`) that is useful for calling other services and for integration tests.

| Interface | Style | Implementation |
|-----------|-------|----------------|
| `MonoGraphQLClient` | Reactive (`Mono`) | `MonoGraphQLClient.createWithWebClient(webClient)` |
| `GraphQLClient` | Blocking | `GraphQLClient.createCustom(url, requestExecutor)` |
| `ReactiveGraphQLClient` | Streaming (`Flux`, subscriptions) | `SSESubscriptionGraphQLClient`, `WebSocketGraphQLClient` |

```java
WebClient webClient = WebClient.create("http://post-service:8080/graphql");
WebClientGraphQLClient client = MonoGraphQLClient.createWithWebClient(webClient);

String query = "query GetPost($id: ID!) { post(id: $id) { id title } }";

GraphQLResponse response = client
        .reactiveExecuteQuery(query, Map.of("id", "1"))
        .block();

if (response.hasErrors()) {
    throw new IllegalStateException(response.getErrors().get(0).getMessage());
}
String title = response.extractValueAsObject("post.title", String.class);
```

With codegen (`generateClient = true`), build the query from generated classes instead of a string; renaming a field in the schema then breaks the build instead of failing at runtime:

```java
GraphQLQueryRequest request = new GraphQLQueryRequest(
        new PostGraphQLQuery.Builder().id("1").build(),
        new PostProjectionRoot<>().id().title());

String query = request.serialize();   // query string with post(id: "1") { id title }
```

[⬆ Back to Table of Contents](#table-of-contents)

---

# 5. Apollo Kotlin (apollo-runtime)

## 5.1 What apollo-runtime Is

**`com.apollographql.apollo:apollo-runtime`** is the runtime of **Apollo Kotlin**, a strongly-typed, caching **GraphQL client** for the JVM, Android and Kotlin Multiplatform (formerly "Apollo Android").

| It is | It is not |
|-------|-----------|
| A client that **calls** a GraphQL server | A GraphQL server (it cannot expose `/graphql`) |
| Code generation: `.graphql` operation files → Kotlin (or Java) classes | A replacement for Spring for GraphQL or DGS |
| Coroutines / `Flow` based API (`suspend fun execute()`) | A blocking Java API (use `com.apollographql.java:client` for Java) |

How it works:

```
remote schema (schema.graphqls)  +  your operations (GetPost.graphql)
                    │
                    ▼   Gradle plugin com.apollographql.apollo (build time)
      generated: GetPostQuery, GetPostQuery.Data, type.CreatePostInput ...
                    │
                    ▼   apollo-runtime (run time)
      ApolloClient.query(GetPostQuery("1")).execute()
                    │   HTTP POST (OkHttp on the JVM) / WebSocket
                    ▼
            remote GraphQL server (Spring for GraphQL, DGS, Apollo Server ...)
```

The generator validates every query against the schema at **build time**: a typo in a field name, a wrong argument type or a removed field fails the build.

**Artifact history** (useful when reading old tutorials):

| Version | Group ID | Package |
|---------|----------|---------|
| 2.x (Apollo Android) | `com.apollographql.apollo` | `com.apollographql.apollo` |
| 3.x | `com.apollographql.apollo3` | `com.apollographql.apollo3` |
| 4.x, 5.x (current: 5.2.0) | `com.apollographql.apollo` | `com.apollographql.apollo` |

[⬆ Back to Table of Contents](#table-of-contents)

---

## 5.2 Dependencies

Apollo Kotlin needs its **Gradle plugin** (for code generation) and the **runtime**. Use the same version for both.

**Gradle (Kotlin DSL)**, in a Kotlin Spring Boot project:
```kotlin
plugins {
    // ... org.springframework.boot, io.spring.dependency-management, kotlin("jvm"), kotlin("plugin.spring")
    id("com.apollographql.apollo") version "5.2.0"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")   // suspend controllers on Spring MVC
    implementation("com.apollographql.apollo:apollo-runtime:5.2.0")
}

apollo {
    service("postService") {
        packageName.set("com.example.client.posts")

        // Optional: download the remote schema by introspection
        // ./gradlew downloadPostServiceApolloSchemaFromIntrospection
        introspection {
            endpointUrl.set("http://localhost:8080/graphql")
            schemaFile.set(file("src/main/graphql/schema.graphqls"))
        }
    }
}
```

**Maven:** Apollo Kotlin officially supports Gradle only. For Maven there is the community `apollo-client-maven-plugin`, which calls the Apollo compiler to generate the sources.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 5.3 Schema and Operation Files

Client files go in **`src/main/graphql/`** (not `src/main/resources/graphql/`, which is where Spring for GraphQL looks for the **server** schema):

```
src/main/graphql/
├── schema.graphqls        ← schema of the REMOTE server (downloaded or copied)
└── PostOperations.graphql ← queries/mutations this app sends
```

`PostOperations.graphql`:
```graphql
query GetPost($id: ID!) {
  post(id: $id) {
    id
    title
    author {
      name
    }
  }
}

mutation CreatePost($input: CreatePostInput!) {
  createPost(input: $input) {
    id
    title
  }
}
```

Generated (in `build/generated/source/apollo/`):

| Operation | Generated class | Notes |
|-----------|-----------------|-------|
| `query GetPost` | `GetPostQuery(id: String)` | `GetPostQuery.Data`, `GetPostQuery.Post`, `GetPostQuery.Author`; **only the selected fields** |
| `mutation CreatePost` | `CreatePostMutation(input: CreatePostInput)` | |
| `input CreatePostInput` | `com.example.client.posts.type.CreatePostInput` | Nullable input fields use `Optional<T>` |

[⬆ Back to Table of Contents](#table-of-contents)

---

## 5.4 Using ApolloClient in Spring Boot (Kotlin)

**1. `ApolloClient` as a Spring bean** (one client per remote service; it is thread-safe and reuses connections):

```kotlin
import com.apollographql.apollo.ApolloClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApolloConfig {

    @Bean(destroyMethod = "close")
    fun postServiceApolloClient(
        @Value("\${clients.post-service.url}") url: String,
    ): ApolloClient =
        ApolloClient.Builder()
            .serverUrl(url)
            .addHttpHeader("X-Caller", "gateway-service")
            .build()
}
```

```properties
clients.post-service.url=http://post-service:8080/graphql
```

**2. A service that executes operations:**

```kotlin
import com.apollographql.apollo.ApolloClient
import com.example.client.posts.CreatePostMutation
import com.example.client.posts.GetPostQuery
import com.example.client.posts.type.CreatePostInput
import org.springframework.stereotype.Service

@Service
class PostClient(private val apolloClient: ApolloClient) {

    suspend fun getPost(id: String): GetPostQuery.Post? {
        val response = apolloClient.query(GetPostQuery(id = id)).execute()

        // execute() does not throw: check both kinds of failure
        response.exception?.let { throw IllegalStateException("Could not reach post-service", it) }
        if (response.hasErrors()) {
            throw IllegalStateException("GraphQL error: ${response.errors!!.first().message}")
        }
        return response.data?.post
    }

    suspend fun createPost(title: String, content: String, authorId: String): CreatePostMutation.CreatePost {
        val input = CreatePostInput(title = title, content = content, authorId = authorId)
        return apolloClient.mutation(CreatePostMutation(input)).execute().dataOrThrow().createPost
    }
}
```

**3. Expose it through a REST controller** (Spring MVC supports `suspend` functions when `kotlinx-coroutines-reactor` is on the classpath; WebFlux supports them natively):

```kotlin
@RestController
@RequestMapping("/api/posts")
class PostProxyController(private val postClient: PostClient) {

    @GetMapping("/{id}")
    suspend fun getPost(@PathVariable id: String): GetPostQuery.Post? = postClient.getPost(id)
}
```

| `ApolloResponse` member | Meaning |
|-------------------------|---------|
| `data` | Typed result (may be partial when there are errors) |
| `errors` | GraphQL errors returned by the server |
| `exception` | Network / parsing failure (no response) |
| `hasErrors()` | `errors` is not empty |
| `dataOrThrow()` | `data`, or throws if `data` is null |

Other features: `apolloClient.subscription(...).toFlow()` for subscriptions over WebSocket, HTTP/GraphQL interceptors (auth headers, logging, retries), an optional normalized cache (separate artifact), and persisted queries.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 5.5 Using Apollo from Java

`apollo-runtime` exposes a coroutines/`Flow` API that is awkward from Java. For Java projects Apollo publishes a separate client, **`com.apollographql.java:client`** (callback-based), used **instead of** `apollo-runtime`:

```kotlin
// build.gradle.kts (still Gradle: the Apollo plugin does the codegen)
plugins {
    id("com.apollographql.apollo") version "5.2.0"
}

dependencies {
    implementation("com.apollographql.java:client:<version>")   // see the Apollo Java support docs
}

apollo {
    service("postService") {
        packageName.set("com.example.client.posts")
        generateKotlinModels.set(false)                       // generate Java classes
    }
}
```

```java
import com.apollographql.java.client.ApolloClient;

@Service
public class PostClient {

    private final ApolloClient apolloClient = new ApolloClient.Builder()
            .serverUrl("http://post-service:8080/graphql")
            .build();

    public CompletableFuture<GetPostQuery.Data> getPost(String id) {
        CompletableFuture<GetPostQuery.Data> future = new CompletableFuture<>();

        apolloClient.query(new GetPostQuery(id)).enqueue(response -> {
            if (response.exception != null) {
                future.completeExceptionally(response.exception);           // network error
            } else if (response.hasErrors()) {
                future.completeExceptionally(
                        new IllegalStateException(response.errors.get(0).getMessage()));
            } else {
                future.complete(response.data);
            }
        });
        return future;
    }
}
```

> The Java client does not support the HTTP or normalized caches. For a Java Spring Boot service that only calls a few queries, Spring's `HttpSyncGraphQlClient` ([3.9](#39-calling-other-graphql-services)) is simpler and needs no extra build plugin.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 5.6 Apollo on the Server Side (Federation)

Apollo also defines **Apollo Federation**: several GraphQL services (subgraphs) are combined into one graph by the **Apollo Router**. A Spring Boot service becomes a subgraph using **federation-jvm** (`com.apollographql.federation:federation-graphql-java-support`); **`apollo-runtime` is not involved**.

| Server library | Federation support |
|----------------|--------------------|
| Spring for GraphQL | `FederationSchemaFactory` + `@EntityMapping` methods (uses federation-jvm) |
| Netflix DGS | Built in: `@key` in schema + `@DgsEntityFetcher(name = "Post")` |

```graphql
# subgraph schema
type Post @key(fields: "id") {
  id: ID!
  title: String!
}
```

```java
// Spring for GraphQL
@EntityMapping
public Post post(@Argument Long id) { return postService.findById(id).orElseThrow(); }

// Netflix DGS
@DgsEntityFetcher(name = "Post")
public Post post(Map<String, Object> values) { return postService.findById(Long.valueOf((String) values.get("id"))).orElseThrow(); }
```

[⬆ Back to Table of Contents](#table-of-contents)

---

# 6. GraphQL Java Kickstart (Legacy)

## 6.1 Dependency and Status

```xml
<dependency>
    <groupId>com.graphql-java-kickstart</groupId>
    <artifactId>graphql-spring-boot-starter</artifactId>
    <version>15.1.0</version>   <!-- last release, Dec 2023 -->
</dependency>
```

> ⚠️ **Archived.** The `graphql-spring-boot` repository was archived on **19 Dec 2023** and is no longer maintained. Its README says: *"We encourage you to start using Spring for GraphQL instead."* Keep this section only for maintaining existing projects.

It used **graphql-java-tools**: you implement marker interfaces, and methods are matched to schema fields **by name**.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 6.2 Resolver Implementation

#### Define Schema

```graphql
type User {
  id: ID!
  name: String!
  email: String!
  age: Int
}

type Query {
  users: [User!]!
  user(id: ID!): User
}

type Mutation {
  createUser(name: String!, email: String!, age: Int): User!
}
```

#### Query Resolver

```java
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;

@Component
public class UserQueryResolver implements GraphQLQueryResolver {

    private final UserRepository userRepository;

    public UserQueryResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Query: users
    public List<User> users() {
        return userRepository.findAll();
    }

    // Query: user(id: ID!)
    public User user(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with ID " + id + " not found"));
    }
}
```

#### Mutation Resolver

```java
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.stereotype.Component;

@Component
public class UserMutationResolver implements GraphQLMutationResolver {

    private final UserRepository userRepository;

    public UserMutationResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Mutation: createUser
    public User createUser(String name, String email, Integer age) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);

        return userRepository.save(user);
    }
}
```

#### Field Resolver (nested field)

```java
import graphql.kickstart.tools.GraphQLResolver;
import org.springframework.stereotype.Component;

@Component
public class UserFieldResolver implements GraphQLResolver<User> {

    private final PostRepository postRepository;

    public UserFieldResolver(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // User.posts -> first parameter is the parent
    public List<Post> posts(User user) {
        return postRepository.findByAuthorId(user.getId());
    }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 6.3 Migrating to Spring for GraphQL

| Kickstart | Spring for GraphQL |
|-----------|--------------------|
| `graphql-spring-boot-starter` | `spring-boot-starter-graphql` + `spring-boot-starter-web` |
| `@Component implements GraphQLQueryResolver` | `@Controller` + `@QueryMapping` |
| `GraphQLMutationResolver` | `@MutationMapping` |
| `GraphQLSubscriptionResolver` (returns `Publisher`) | `@SubscriptionMapping` (returns `Flux`) |
| `GraphQLResolver<User>` + method `posts(User user)` | `@SchemaMapping(typeName = "User")` or `@BatchMapping` |
| Method arguments matched by position/name | `@Argument` |
| Schema files anywhere (`**/*.graphqls`) | `src/main/resources/graphql/**` (or set `spring.graphql.schema.locations`) |
| `graphql.*` properties | `spring.graphql.*` properties |
| `GraphQLErrorHandler` / `@ExceptionHandler` | `@GraphQlExceptionHandler` / `DataFetcherExceptionResolverAdapter` |
| `GraphQLTestTemplate` | `@GraphQlTest` + `GraphQlTester` |

The schema itself does not change. Migration is mostly replacing interfaces with annotations, method by method.

[⬆ Back to Table of Contents](#table-of-contents)

---

# 7. Same Feature in Each Library

Server-side features (Apollo Kotlin is a client, so it is not in this table):

| Task | Spring for GraphQL | Netflix DGS | Kickstart (legacy) |
|------|-------------------|-------------|--------------------|
| Mark the class | `@Controller` | `@DgsComponent` | `@Component` + resolver interface |
| Query field | `@QueryMapping` | `@DgsQuery` | `GraphQLQueryResolver` method |
| Mutation field | `@MutationMapping` | `@DgsMutation` | `GraphQLMutationResolver` method |
| Subscription field | `@SubscriptionMapping` → `Flux` | `@DgsSubscription` → `Publisher` | `GraphQLSubscriptionResolver` → `Publisher` |
| Nested field | `@SchemaMapping(typeName = "Post", field = "author")` | `@DgsData(parentType = "Post", field = "author")` | `GraphQLResolver<Post>` |
| Argument | `@Argument` | `@InputArgument` | Plain parameter |
| Method name must match field? | No (`name` attribute) | No (`field` attribute) | **Yes** |
| Batching (N+1) | `@BatchMapping` | `@DgsDataLoader` + `DataLoader.load()` | Manual `DataLoaderRegistry` |
| Request context | `@ContextValue`, `GraphQLContext` | `DgsContext`, `@RequestHeader` | `DataFetchingEnvironment` |
| Error mapping | `@GraphQlExceptionHandler` | `@GraphQlExceptionHandler`, `DataFetcherExceptionHandler` | `GraphQLErrorHandler` |
| Unit test | `@GraphQlTest` + `GraphQlTester` | `@EnableDgsTest` + `DgsQueryExecutor` | `GraphQLTestTemplate` |
| Default schema folder | `resources/graphql/` | `resources/schema/` | Anywhere |
| Generated Java types | No (optional DGS Codegen) | Yes (DGS Codegen) | No |

[⬆ Back to Table of Contents](#table-of-contents)

---

# 8. Microservice Communication

## 8.1 Choosing a Client

| Aspect | Plain `WebClient` / `RestClient` | Spring `HttpSyncGraphQlClient` / `HttpGraphQlClient` | DGS `GraphQLClient` | Apollo Kotlin |
|--------|------|------|------|------|
| Dependency | `spring-boot-starter-webflux` / `-web` | `spring-graphql` (in `spring-boot-starter-graphql`) | `graphql-dgs-client` | `apollo-runtime` (or `com.apollographql.java:client`) |
| Query written as | String | String or file in `graphql-documents/` | String or codegen builder | `.graphql` file → generated class |
| Response types | Your DTOs; parse `data`/`errors` yourself | Your DTOs via `toEntity()` | JsonPath extraction or codegen types | Generated, compile-time checked |
| Error handling | Manual | `ClientGraphQlResponse`, `FieldAccessException` | `GraphQLResponse.hasErrors()` | `response.errors` / `response.exception` |
| Style | Blocking or reactive | Blocking or reactive | Blocking or reactive | Coroutines (Kotlin) / callbacks (Java) |
| Best for | One-off calls, no extra deps | **Default choice in Spring Boot** | DGS projects | Kotlin/Android, many typed operations |

Examples: Spring client → [3.9](#39-calling-other-graphql-services) · DGS client → [4.9](#49-dgs-graphql-client) · Apollo → [5.4](#54-using-apolloclient-in-spring-boot-kotlin).

[⬆ Back to Table of Contents](#table-of-contents)

---

## 8.2 Plain WebClient (No GraphQL Library)

A GraphQL call is just an HTTP POST with a JSON body, so any HTTP client works.

#### Add Dependency

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

#### GraphQL Client Service

```java
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class GraphQLClientService {

    private final WebClient webClient;

    public GraphQLClientService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://user-service:8081/graphql")
                .build();
    }

    // Execute GraphQL query.
    // ParameterizedTypeReference keeps the generic type: with GraphQLResponse.class,
    // "data" would be deserialized as a LinkedHashMap and fail with ClassCastException.
    public <T> GraphQLResponse<T> executeQuery(String query,
                                               Map<String, Object> variables,
                                               ParameterizedTypeReference<GraphQLResponse<T>> responseType) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query);

        if (variables != null && !variables.isEmpty()) {
            requestBody.put("variables", variables);
        }

        return webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }
}
```

#### Usage Example

```java
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {

    private final GraphQLClientService graphQLClient;

    public UserService(GraphQLClientService graphQLClient) {
        this.graphQLClient = graphQLClient;
    }

    public UserDto getUserById(Long userId) {
        String query = """
            query GetUser($id: ID!) {
                user(id: $id) {
                    id
                    name
                    email
                }
            }
            """;

        GraphQLResponse<UserData> response = graphQLClient.executeQuery(
            query,
            Map.of("id", userId),
            new ParameterizedTypeReference<GraphQLResponse<UserData>>() {}
        );

        if (response.getErrors() != null && !response.getErrors().isEmpty()) {
            throw new RuntimeException("GraphQL Error: " + response.getErrors().get(0).getMessage());
        }

        return response.getData().user();
    }
}

// "data": { "user": { ... } }  ->  the wrapper has one field per top-level query field
record UserData(UserDto user) {}

record UserDto(String id, String name, String email) {}
```

#### Response DTO

```java
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GraphQLResponse<T> {
    private T data;
    private List<GraphQLError> errors;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class GraphQLError {
    private String message;
    private List<Location> locations;
    private List<Object> path;          // path can contain field names and list indexes
}

@Data
class Location {
    private int line;
    private int column;
}
```

> In a Spring Boot app, prefer `HttpSyncGraphQlClient` ([3.9](#39-calling-other-graphql-services)): it builds the request body, handles `errors`, and maps `data` for you.

[⬆ Back to Table of Contents](#table-of-contents)

---

# 9. Key Points to Remember

1. **Single Endpoint**: All GraphQL operations (Query, Mutation, Subscription) use the same endpoint, usually `/graphql`.

2. **POST Method**: Clients normally send POST requests, even for queries. The GraphQL-over-HTTP spec also allows GET for queries, but POST is the common default.

3. **HTTP 200 with errors**: A failed field still returns HTTP 200; check the `errors` array, not only the status code.

4. **Variables**: Use variables for dynamic values instead of string concatenation.

5. **Type Safety**: GraphQL schemas provide strong typing and validation. Codegen (DGS Codegen, Apollo Kotlin) extends this to your Java/Kotlin code.

6. **One engine**: Spring for GraphQL, DGS and Kickstart all run on graphql-java. DGS now runs on Spring for GraphQL.

7. **Server vs client**: `spring-boot-starter-graphql` and `dgs-starter` build servers; `apollo-runtime` is only a client.

8. **Method Names**:
   - Spring for GraphQL (`@QueryMapping`) and DGS (`@DgsQuery`): method name is the default field name, but can be overridden.
   - Kickstart resolvers: method names must match schema field names exactly.

9. **N+1**: Nested fields are resolved per parent; use `@BatchMapping` (Spring) or `@DgsDataLoader` (DGS).

10. **Kickstart is archived**: Use Spring for GraphQL or DGS for new code.

[⬆ Back to Table of Contents](#table-of-contents)

---

# 10. Best Practices

- ✅ Always use variables for dynamic queries
- ✅ Handle both `data` and `errors` in responses
- ✅ Use Input types for complex mutation arguments
- ✅ Map exceptions to proper error types (`@GraphQlExceptionHandler`) instead of leaking stack traces
- ✅ Batch nested fields (`@BatchMapping` / `@DgsDataLoader`) to avoid N+1 queries
- ✅ Pick **one** server library per service; don't mix DGS and Spring for GraphQL annotations
- ✅ Let the BOM manage versions (Spring Boot BOM, DGS `graphql-dgs-platform-dependencies`)
- ✅ Use a GraphQL client (`HttpSyncGraphQlClient`, DGS client, Apollo) instead of hand-written HTTP code
- ✅ Enable GraphiQL for API exploration during development; consider disabling introspection in production
- ✅ Secure resolvers with `@PreAuthorize` and limit query depth/complexity for public APIs
- ✅ Keep queries specific; request only the fields you need

[⬆ Back to Table of Contents](#table-of-contents)

---

# 11. Resources

- [GraphQL Official Documentation](https://graphql.org/)
- [graphql-java](https://www.graphql-java.com/)
- [Spring for GraphQL Reference](https://docs.spring.io/spring-graphql/reference/)
- [Spring Boot: Spring for GraphQL](https://docs.spring.io/spring-boot/reference/web/spring-graphql.html)
- [Netflix DGS Framework](https://netflix.github.io/dgs/)
- [DGS: Spring for GraphQL Integration](https://netflix.github.io/dgs/spring-graphql-integration/)
- [DGS Codegen](https://github.com/Netflix/dgs-codegen)
- [Apollo Kotlin](https://www.apollographql.com/docs/kotlin)
- [Apollo Kotlin Java Support](https://apollographql.github.io/apollo-kotlin-java-support/)
- [Apollo Federation JVM](https://github.com/apollographql/federation-jvm)
- [GraphQL Java Kickstart (archived)](https://github.com/graphql-java-kickstart/graphql-spring-boot)
- [Java Spring Boot Annotations - Complete Guide](java-spring-boot-document-with-full-details.md)

---

**Last Updated:** October 2026
