# Vert.x Framework - Complete Guide

## Table of Contents
1. [Introduction to Vert.x](#introduction)
2. [Core Concepts](#core-concepts)
3. [Key Features](#key-features)
4. [Core Components](#core-components)
5. [Event Bus](#event-bus)
6. [HTTP Server & Client](#http-server-client)
7. [Database Integration](#database-integration)
8. [Reactive Programming](#reactive-programming)
9. [Spring Boot Integration](#spring-boot-integration)
10. [Best Practices](#best-practices)
11. [Complete Examples](#complete-examples)

---

## Introduction to Vert.x {#introduction}

**Eclipse Vert.x** is a toolkit for building reactive, non-blocking, asynchronous applications on the JVM. It's polyglot, supporting multiple languages including Java, Kotlin, JavaScript, Groovy, Ruby, and Scala.

### Why Vert.x?

- **Reactive & Non-blocking**: Built on the Reactor pattern with multi-reactor architecture
- **High Performance**: Handles thousands of concurrent connections with minimal threads
- **Event-Driven**: Uses an event loop model similar to Node.js
- **Polyglot**: Write components in different JVM languages
- **Distributed**: Built-in clustering and event bus for distributed systems
- **Lightweight**: Small footprint with minimal dependencies

### Architecture Overview

```
┌─────────────────────────────────────────┐
│        Application Layer                │
│  (Verticles, Services, Handlers)        │
├─────────────────────────────────────────┤
│         Event Bus Layer                 │
│  (Messaging, Communication)             │
├─────────────────────────────────────────┤
│      Core Layer (Vert.x Core)           │
│  (Event Loop, Buffers, Futures)         │
├─────────────────────────────────────────┤
│           Netty                         │
│  (Network I/O)                          │
└─────────────────────────────────────────┘
```

---

## Core Concepts {#core-concepts}

### 1. Verticles

Verticles are the basic deployment units in Vert.x. They're similar to actors in the Actor Model.

**Types of Verticles:**

- **Standard Verticles**: Run on event loop threads (never block!)
- **Worker Verticles**: Run on worker thread pool (can block)
- **Multi-threaded Worker Verticles**: Multiple instances can run concurrently

```java
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class MyVerticle extends AbstractVerticle {
    
    @Override
    public void start(Promise<Void> startPromise) {
        System.out.println("Verticle started on thread: " + 
            Thread.currentThread().getName());
        startPromise.complete();
    }
    
    @Override
    public void stop(Promise<Void> stopPromise) {
        System.out.println("Verticle stopping...");
        stopPromise.complete();
    }
}
```

### 2. Event Loop

Vert.x uses a **Multi-Reactor Pattern** where each event loop runs on its own thread.

```java
// The Golden Rule: NEVER BLOCK THE EVENT LOOP!

// ✅ Good - Non-blocking
vertx.setTimer(1000, id -> {
    System.out.println("Timer fired!");
});

// ❌ Bad - Blocking
Thread.sleep(1000); // DON'T DO THIS!

// ✅ Good - Use worker verticle for blocking operations
vertx.deployVerticle(new WorkerVerticle(), 
    new DeploymentOptions().setWorker(true));
```

### 3. Futures and Promises

Vert.x uses Futures for asynchronous operations.

```java
import io.vertx.core.Future;
import io.vertx.core.Promise;

// Creating a Future
Future<String> future = Future.succeededFuture("Hello");
Future<String> failedFuture = Future.failedFuture("Error occurred");

// Using Promise
public Future<String> fetchData() {
    Promise<String> promise = Promise.promise();
    
    vertx.setTimer(1000, id -> {
        promise.complete("Data fetched");
    });
    
    return promise.future();
}

// Composing Futures
fetchData()
    .compose(data -> processData(data))
    .compose(result -> saveResult(result))
    .onSuccess(res -> System.out.println("Success: " + res))
    .onFailure(err -> System.err.println("Error: " + err.getMessage()));
```

---

## Key Features {#key-features}

### 1. Non-Blocking I/O
All I/O operations are non-blocking by default, allowing high concurrency.

### 2. Event Bus
Lightweight distributed messaging system for inter-verticle communication.

### 3. Polyglot Support
Write different parts of your application in different languages.

### 4. Reactive Streams
Implements Reactive Streams specification for back-pressure support.

### 5. Clustering
Built-in support for creating distributed applications.

### 6. Module System
Rich ecosystem of modules for web, databases, messaging, etc.

---

## Core Components {#core-components}

### 1. Vert.x Core

```xml
<dependency>
    <groupId>io.vertx</groupId>
    <artifactId>vertx-core</artifactId>
    <version>5.0.7</version>
</dependency>
```

**Basic Vert.x Instance:**

```java
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class Application {
    public static void main(String[] args) {
        // Create Vert.x instance
        Vertx vertx = Vertx.vertx();
        
        // With options
        VertxOptions options = new VertxOptions()
            .setWorkerPoolSize(40)
            .setEventLoopPoolSize(4);
        Vertx customVertx = Vertx.vertx(options);
        
        // Deploy verticle
        vertx.deployVerticle(new MyVerticle())
            .onSuccess(id -> System.out.println("Deployed: " + id))
            .onFailure(err -> System.err.println("Failed: " + err));
    }
}
```

### 2. Vert.x Web

```xml
<dependency>
    <groupId>io.vertx</groupId>
    <artifactId>vertx-web</artifactId>
    <version>5.0.7</version>
</dependency>
```

**Creating a Web Server:**

```java
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class WebServerVerticle extends AbstractVerticle {
    
    @Override
    public void start() {
        Router router = Router.router(vertx);
        
        // Add body handler for POST requests
        router.route().handler(BodyHandler.create());
        
        // GET endpoint
        router.get("/api/hello").handler(ctx -> {
            ctx.json(new JsonObject()
                .put("message", "Hello from Vert.x!")
                .put("timestamp", System.currentTimeMillis()));
        });
        
        // POST endpoint
        router.post("/api/users").handler(ctx -> {
            JsonObject body = ctx.body().asJsonObject();
            String name = body.getString("name");
            
            ctx.json(new JsonObject()
                .put("id", 1)
                .put("name", name)
                .put("created", true));
        });
        
        // Path parameters
        router.get("/api/users/:id").handler(ctx -> {
            String id = ctx.pathParam("id");
            ctx.json(new JsonObject()
                .put("id", id)
                .put("name", "User " + id));
        });
        
        // Query parameters
        router.get("/api/search").handler(ctx -> {
            String query = ctx.queryParam("q").get(0);
            ctx.json(new JsonObject()
                .put("query", query)
                .put("results", 42));
        });
        
        // Create HTTP server
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8080)
            .onSuccess(server -> {
                System.out.println("Server started on port " + 
                    server.actualPort());
            });
    }
}
```

---

## Event Bus {#event-bus}

The Event Bus is the nervous system of Vert.x applications, enabling loose coupling between components.

### Event Bus Patterns

1. **Point-to-Point** (send): One consumer receives the message
2. **Publish-Subscribe** (publish): All consumers receive the message
3. **Request-Reply**: Send and wait for response

### Basic Event Bus Usage

```java
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;

public class EventBusExample extends AbstractVerticle {
    
    @Override
    public void start() {
        EventBus eb = vertx.eventBus();
        
        // 1. Point-to-Point Consumer
        eb.consumer("user.created", message -> {
            JsonObject user = (JsonObject) message.body();
            System.out.println("User created: " + user.getString("name"));
            
            // Send reply
            message.reply(new JsonObject()
                .put("status", "processed")
                .put("timestamp", System.currentTimeMillis()));
        });
        
        // 2. Publish-Subscribe Consumer
        eb.consumer("notifications", message -> {
            System.out.println("Notification 1: " + message.body());
        });
        
        eb.consumer("notifications", message -> {
            System.out.println("Notification 2: " + message.body());
        });
        
        // Sending messages
        vertx.setPeriodic(5000, id -> {
            // Send (point-to-point)
            eb.send("user.created", 
                new JsonObject()
                    .put("name", "John Doe")
                    .put("email", "john@example.com"));
            
            // Publish (broadcast)
            eb.publish("notifications", "System update available");
        });
    }
}
```

### Request-Reply Pattern

```java
public class RequestReplyExample extends AbstractVerticle {
    
    @Override
    public void start() {
        EventBus eb = vertx.eventBus();
        
        // Service that responds to requests
        eb.consumer("math.add", (Message<JsonObject> message) -> {
            JsonObject body = message.body();
            int a = body.getInteger("a");
            int b = body.getInteger("b");
            
            message.reply(new JsonObject().put("result", a + b));
        });
        
        // Client making request
        JsonObject request = new JsonObject()
            .put("a", 5)
            .put("b", 3);
            
        eb.request("math.add", request)
            .onSuccess(reply -> {
                JsonObject result = (JsonObject) reply.body();
                System.out.println("Result: " + result.getInteger("result"));
            })
            .onFailure(err -> {
                System.err.println("Error: " + err.getMessage());
            });
    }
}
```

### Clustered Event Bus

```java
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class ClusteredApp {
    public static void main(String[] args) {
        ClusterManager mgr = new HazelcastClusterManager();
        
        VertxOptions options = new VertxOptions()
            .setClusterManager(mgr);
        
        Vertx.clusteredVertx(options)
            .onSuccess(vertx -> {
                System.out.println("Clustered Vert.x created!");
                vertx.deployVerticle(new MyVerticle());
            });
    }
}
```

---

## HTTP Server & Client {#http-server-client}

### Advanced HTTP Server

```java
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.*;

public class AdvancedWebServer extends AbstractVerticle {
    
    @Override
    public void start() {
        Router router = Router.router(vertx);
        
        // Middleware
        router.route().handler(LoggerHandler.create());
        router.route().handler(TimeoutHandler.create(10000));
        router.route().handler(BodyHandler.create());
        
        // CORS
        router.route().handler(CorsHandler.create()
            .addOrigin("*")
            .allowedMethod(io.vertx.core.http.HttpMethod.GET)
            .allowedMethod(io.vertx.core.http.HttpMethod.POST));
        
        // Static files
        router.route("/static/*")
            .handler(StaticHandler.create("webroot"));
        
        // Error handler
        router.errorHandler(500, ctx -> {
            ctx.json(new JsonObject()
                .put("error", "Internal Server Error")
                .put("message", ctx.failure().getMessage()));
        });
        
        // Routes
        setupRoutes(router);
        
        // Server options
        HttpServerOptions options = new HttpServerOptions()
            .setCompressionSupported(true)
            .setIdleTimeout(60);
        
        vertx.createHttpServer(options)
            .requestHandler(router)
            .listen(8080);
    }
    
    private void setupRoutes(Router router) {
        router.get("/api/health").handler(ctx -> {
            ctx.json(new JsonObject().put("status", "UP"));
        });
    }
}
```

### HTTP Client

```java
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.codec.BodyCodec;

public class HttpClientExample extends AbstractVerticle {
    
    @Override
    public void start() {
        WebClientOptions options = new WebClientOptions()
            .setKeepAlive(true)
            .setMaxPoolSize(10);
        
        WebClient client = WebClient.create(vertx, options);
        
        // GET request
        client.get(443, "api.github.com", "/users/octocat")
            .ssl(true)
            .as(BodyCodec.jsonObject())
            .send()
            .onSuccess(response -> {
                System.out.println("Status: " + response.statusCode());
                System.out.println("Body: " + response.body());
            })
            .onFailure(err -> {
                System.err.println("Request failed: " + err.getMessage());
            });
        
        // POST request
        JsonObject userData = new JsonObject()
            .put("name", "John")
            .put("email", "john@example.com");
        
        client.post(8080, "localhost", "/api/users")
            .sendJsonObject(userData)
            .onSuccess(response -> {
                System.out.println("Created: " + response.body());
            });
        
        // With timeout
        client.get(443, "slow-api.com", "/data")
            .timeout(5000)
            .send()
            .onSuccess(response -> System.out.println("Success"))
            .onFailure(err -> System.out.println("Timeout or error"));
    }
}
```

---

## Database Integration {#database-integration}

### PostgreSQL Client

```xml
<dependency>
    <groupId>io.vertx</groupId>
    <artifactId>vertx-pg-client</artifactId>
    <version>5.0.7</version>
</dependency>
```

```java
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

public class DatabaseExample extends AbstractVerticle {
    
    private PgPool pool;
    
    @Override
    public void start() {
        // Database connection
        PgConnectOptions connectOptions = new PgConnectOptions()
            .setPort(5432)
            .setHost("localhost")
            .setDatabase("mydb")
            .setUser("user")
            .setPassword("password");
        
        PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(5);
        
        pool = PgPool.pool(vertx, connectOptions, poolOptions);
        
        // Query examples
        selectUsers();
        insertUser("Alice", "alice@example.com");
        updateUser(1, "New Name");
        deleteUser(1);
    }
    
    private void selectUsers() {
        pool.query("SELECT id, name, email FROM users")
            .execute()
            .onSuccess(rows -> {
                for (Row row : rows) {
                    System.out.println("User: " + 
                        row.getInteger("id") + " - " +
                        row.getString("name"));
                }
            })
            .onFailure(err -> {
                System.err.println("Query failed: " + err.getMessage());
            });
    }
    
    private void insertUser(String name, String email) {
        pool.preparedQuery(
            "INSERT INTO users (name, email) VALUES ($1, $2) RETURNING id")
            .execute(Tuple.of(name, email))
            .onSuccess(rows -> {
                Long id = rows.iterator().next().getLong("id");
                System.out.println("Inserted user with ID: " + id);
            });
    }
    
    private void updateUser(int id, String newName) {
        pool.preparedQuery("UPDATE users SET name = $1 WHERE id = $2")
            .execute(Tuple.of(newName, id))
            .onSuccess(result -> {
                System.out.println("Updated " + 
                    result.rowCount() + " rows");
            });
    }
    
    private void deleteUser(int id) {
        pool.preparedQuery("DELETE FROM users WHERE id = $1")
            .execute(Tuple.of(id))
            .onSuccess(result -> {
                System.out.println("Deleted " + 
                    result.rowCount() + " rows");
            });
    }
    
    @Override
    public void stop() {
        if (pool != null) {
            pool.close();
        }
    }
}
```

### MongoDB Client

```xml
<dependency>
    <groupId>io.vertx</groupId>
    <artifactId>vertx-mongo-client</artifactId>
    <version>5.0.7</version>
</dependency>
```

```java
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class MongoExample extends AbstractVerticle {
    
    private MongoClient mongoClient;
    
    @Override
    public void start() {
        JsonObject config = new JsonObject()
            .put("connection_string", "mongodb://localhost:27017")
            .put("db_name", "mydb");
        
        mongoClient = MongoClient.createShared(vertx, config);
        
        // Insert document
        JsonObject document = new JsonObject()
            .put("name", "John Doe")
            .put("age", 30)
            .put("email", "john@example.com");
        
        mongoClient.insert("users", document)
            .onSuccess(id -> {
                System.out.println("Inserted with ID: " + id);
            });
        
        // Find documents
        JsonObject query = new JsonObject().put("age", 
            new JsonObject().put("$gte", 18));
        
        mongoClient.find("users", query)
            .onSuccess(results -> {
                results.forEach(doc -> {
                    System.out.println("Found: " + doc.encodePrettily());
                });
            });
        
        // Update document
        JsonObject updateQuery = new JsonObject().put("name", "John Doe");
        JsonObject update = new JsonObject()
            .put("$set", new JsonObject().put("age", 31));
        
        mongoClient.updateCollection("users", updateQuery, update)
            .onSuccess(result -> {
                System.out.println("Modified: " + 
                    result.getDocModified() + " documents");
            });
    }
}
```

---

## Reactive Programming {#reactive-programming}

### RxJava Integration

```xml
<dependency>
    <groupId>io.vertx</groupId>
    <artifactId>vertx-rx-java3</artifactId>
    <version>5.0.7</version>
</dependency>
```

```java
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.http.HttpServer;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.Observable;

public class RxJavaExample extends AbstractVerticle {
    
    @Override
    public Single<String> rxStart() {
        return vertx.createHttpServer()
            .requestHandler(req -> {
                req.response()
                    .putHeader("content-type", "text/plain")
                    .end("Hello RxJava!");
            })
            .rxListen(8080)
            .map(server -> "Server started on port " + 
                server.actualPort());
    }
    
    public void reactiveOperations() {
        // Observable stream
        Observable.range(1, 10)
            .map(i -> i * 2)
            .filter(i -> i > 10)
            .subscribe(
                result -> System.out.println("Result: " + result),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Completed")
            );
        
        // Combining futures
        Single<String> data1 = fetchData1();
        Single<String> data2 = fetchData2();
        
        Single.zip(data1, data2, (d1, d2) -> d1 + " " + d2)
            .subscribe(
                combined -> System.out.println("Combined: " + combined),
                error -> System.err.println("Error: " + error)
            );
    }
    
    private Single<String> fetchData1() {
        return Single.just("Data1");
    }
    
    private Single<String> fetchData2() {
        return Single.just("Data2");
    }
}
```

---

## Spring Boot Integration {#spring-boot-integration}

### Method 1: Using Vert.x as Embedded Server

**Maven Dependencies:**

```xml
<dependencies>
    <!-- Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
        <version>3.2.0</version>
    </dependency>
    
    <!-- Vert.x -->
    <dependency>
        <groupId>io.vertx</groupId>
        <artifactId>vertx-core</artifactId>
        <version>5.0.7</version>
    </dependency>
    
    <dependency>
        <groupId>io.vertx</groupId>
        <artifactId>vertx-web</artifactId>
        <version>5.0.7</version>
    </dependency>
</dependencies>
```

**Spring Boot Application:**

```java
import io.vertx.core.Vertx;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class VertxSpringBootApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(VertxSpringBootApplication.class, args);
    }
    
    @Bean
    public Vertx vertx() {
        return Vertx.vertx();
    }
}
```

**Vert.x Configuration:**

```java
import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class VertxDeployer implements CommandLineRunner {
    
    @Autowired
    private Vertx vertx;
    
    @Autowired
    private ApplicationContext context;
    
    @Override
    public void run(String... args) throws Exception {
        // Deploy Spring-aware Verticle
        SpringVerticle verticle = context.getBean(SpringVerticle.class);
        
        vertx.deployVerticle(verticle)
            .onSuccess(id -> {
                System.out.println("Verticle deployed: " + id);
            })
            .onFailure(err -> {
                System.err.println("Deployment failed: " + err);
            });
    }
}
```

**Spring-Aware Verticle:**

```java
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SpringVerticle extends AbstractVerticle {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public void start() {
        Router router = Router.router(vertx);
        
        // Use Spring-managed services
        router.get("/api/users").handler(ctx -> {
            vertx.executeBlocking(() -> {
                // Call Spring service (can be blocking)
                return userService.getAllUsers();
            })
            .onSuccess(users -> {
                ctx.json(users);
            })
            .onFailure(err -> {
                ctx.response()
                    .setStatusCode(500)
                    .end("Error: " + err.getMessage());
            });
        });
        
        router.post("/api/users").handler(ctx -> {
            JsonObject body = ctx.body().asJsonObject();
            
            vertx.executeBlocking(() -> {
                User user = new User();
                user.setName(body.getString("name"));
                user.setEmail(body.getString("email"));
                return userService.createUser(user);
            })
            .onSuccess(user -> {
                ctx.json(user);
            });
        });
        
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8080)
            .onSuccess(server -> {
                System.out.println("HTTP server started on port 8080");
            });
    }
}
```

**Spring Services:**

```java
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
```

### Method 2: Using Vert.x Event Bus with Spring

```java
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VertxEventBusConfig {
    
    @Bean
    public Vertx vertx() {
        return Vertx.vertx();
    }
    
    @Bean
    public EventBus eventBus(Vertx vertx) {
        return vertx.eventBus();
    }
}
```

**Spring Service Using Event Bus:**

```java
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventBusService {
    
    @Autowired
    private EventBus eventBus;
    
    @Autowired
    private UserService userService;
    
    @PostConstruct
    public void init() {
        // Register consumer for user creation events
        eventBus.consumer("user.create", message -> {
            JsonObject userData = (JsonObject) message.body();
            
            try {
                User user = new User();
                user.setName(userData.getString("name"));
                user.setEmail(userData.getString("email"));
                
                User created = userService.createUser(user);
                
                message.reply(JsonObject.mapFrom(created));
            } catch (Exception e) {
                message.fail(500, e.getMessage());
            }
        });
        
        // Register consumer for user queries
        eventBus.consumer("user.get", message -> {
            Long userId = Long.parseLong(message.body().toString());
            
            try {
                User user = userService.getUserById(userId);
                message.reply(JsonObject.mapFrom(user));
            } catch (Exception e) {
                message.fail(404, "User not found");
            }
        });
    }
    
    public void publishUserCreatedEvent(User user) {
        JsonObject event = new JsonObject()
            .put("type", "USER_CREATED")
            .put("userId", user.getId())
            .put("timestamp", System.currentTimeMillis());
        
        eventBus.publish("user.events", event);
    }
}
```

### Method 3: Complete Integration Example

**Project Structure:**
```
src/main/java
├── com.example
│   ├── VertxSpringApplication.java
│   ├── config
│   │   └── VertxConfig.java
│   ├── verticle
│   │   ├── HttpServerVerticle.java
│   │   └── DatabaseVerticle.java
│   ├── service
│   │   └── UserService.java
│   └── repository
│       └── UserRepository.java
```

**Main Application:**

```java
package com.example;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class VertxSpringApplication {
    
    public static void main(String[] args) {
        // Create Spring context
        ConfigurableApplicationContext context = 
            SpringApplication.run(VertxSpringApplication.class, args);
        
        // Get Vert.x instance from Spring
        Vertx vertx = context.getBean(Vertx.class);
        
        // Deploy verticles with Spring DI
        HttpServerVerticle httpVerticle = 
            context.getBean(HttpServerVerticle.class);
        DatabaseVerticle dbVerticle = 
            context.getBean(DatabaseVerticle.class);
        
        vertx.deployVerticle(dbVerticle)
            .compose(id -> vertx.deployVerticle(httpVerticle))
            .onSuccess(id -> {
                System.out.println("All verticles deployed successfully");
            })
            .onFailure(err -> {
                System.err.println("Deployment failed: " + err);
                System.exit(1);
            });
    }
}
```

**Vert.x Configuration:**

```java
package com.example.config;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VertxConfig {
    
    @Bean
    public Vertx vertx() {
        VertxOptions options = new VertxOptions()
            .setWorkerPoolSize(40)
            .setEventLoopPoolSize(Runtime.getRuntime()
                .availableProcessors() * 2);
        
        return Vertx.vertx(options);
    }
}
```

**HTTP Server Verticle with Spring DI:**

```java
package com.example.verticle;

import com.example.service.UserService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HttpServerVerticle extends AbstractVerticle {
    
    @Autowired
    private UserService userService;
    
    @Override
    public void start() {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        
        // Health check
        router.get("/health").handler(ctx -> {
            ctx.json(new JsonObject()
                .put("status", "UP")
                .put("timestamp", System.currentTimeMillis()));
        });
        
        // Get all users
        router.get("/api/users").handler(ctx -> {
            vertx.<List<User>>executeBlocking(() -> 
                userService.getAllUsers()
            )
            .onSuccess(users -> ctx.json(users))
            .onFailure(err -> ctx.fail(500, err));
        });
        
        // Get user by ID
        router.get("/api/users/:id").handler(ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            
            vertx.<User>executeBlocking(() -> 
                userService.getUserById(id)
            )
            .onSuccess(user -> ctx.json(user))
            .onFailure(err -> ctx.fail(404, err));
        });
        
        // Create user
        router.post("/api/users").handler(ctx -> {
            JsonObject body = ctx.body().asJsonObject();
            
            vertx.<User>executeBlocking(() -> {
                User user = new User();
                user.setName(body.getString("name"));
                user.setEmail(body.getString("email"));
                return userService.createUser(user);
            })
            .onSuccess(user -> {
                ctx.response().setStatusCode(201).end(
                    JsonObject.mapFrom(user).encode()
                );
            })
            .onFailure(err -> ctx.fail(500, err));
        });
        
        // Update user
        router.put("/api/users/:id").handler(ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            JsonObject body = ctx.body().asJsonObject();
            
            vertx.<User>executeBlocking(() -> {
                User user = userService.getUserById(id);
                user.setName(body.getString("name"));
                user.setEmail(body.getString("email"));
                return userService.updateUser(user);
            })
            .onSuccess(user -> ctx.json(user))
            .onFailure(err -> ctx.fail(500, err));
        });
        
        // Delete user
        router.delete("/api/users/:id").handler(ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            
            vertx.<Void>executeBlocking(() -> {
                userService.deleteUser(id);
                return null;
            })
            .onSuccess(v -> ctx.response().setStatusCode(204).end())
            .onFailure(err -> ctx.fail(500, err));
        });
        
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8080)
            .onSuccess(server -> {
                System.out.println("HTTP server on port 8080");
            });
    }
}
```

---

## Best Practices {#best-practices}

### 1. Never Block the Event Loop

```java
// ❌ BAD - Blocking the event loop
public void badExample() {
    // This blocks the event loop!
    Thread.sleep(1000);
    
    // This blocks too!
    String data = readFileSync("/path/to/file");
}

// ✅ GOOD - Non-blocking alternatives
public void goodExample() {
    // Use timer for delays
    vertx.setTimer(1000, id -> {
        System.out.println("Non-blocking delay!");
    });
    
    // Use async file operations
    vertx.fileSystem().readFile("/path/to/file")
        .onSuccess(buffer -> {
            System.out.println("File content: " + buffer);
        });
}

// ✅ GOOD - Use worker verticles for blocking operations
public class BlockingTaskVerticle extends AbstractVerticle {
    @Override
    public void start() {
        // This is OK - worker verticle can block
        try {
            Thread.sleep(5000);
            // Perform blocking database operation
            doBlockingDatabaseWork();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// Deploy as worker
vertx.deployVerticle(new BlockingTaskVerticle(), 
    new DeploymentOptions().setWorker(true));
```

### 2. Proper Error Handling

```java
public void properErrorHandling() {
    // Always handle failures
    vertx.createHttpServer()
        .requestHandler(req -> req.response().end("Hello"))
        .listen(8080)
        .onSuccess(server -> {
            System.out.println("Server started");
        })
        .onFailure(err -> {
            System.err.println("Failed to start: " + err);
            // Take corrective action
        });
    
    // Chain error handling
    fetchData()
        .compose(data -> processData(data))
        .compose(result -> saveResult(result))
        .recover(err -> {
            // Recover from error
            System.err.println("Error: " + err);
            return Future.succeededFuture("default-value");
        })
        .onSuccess(result -> System.out.println("Final: " + result));
}
```

### 3. Resource Management

```java
public class ResourceManagementVerticle extends AbstractVerticle {
    
    private PgPool dbPool;
    private WebClient webClient;
    
    @Override
    public void start() {
        // Initialize resources
        dbPool = PgPool.pool(vertx, connectOptions, poolOptions);
        webClient = WebClient.create(vertx);
    }
    
    @Override
    public void stop() {
        // Clean up resources
        if (dbPool != null) {
            dbPool.close();
        }
        if (webClient != null) {
            webClient.close();
        }
    }
}
```

### 4. Verticle Deployment Strategies

```java
public void deploymentStrategies() {
    // Single instance
    vertx.deployVerticle(new MyVerticle());
    
    // Multiple instances
    DeploymentOptions options = new DeploymentOptions()
        .setInstances(4); // One per CPU core
    
    vertx.deployVerticle(MyVerticle.class.getName(), options);
    
    // Worker verticle
    DeploymentOptions workerOptions = new DeploymentOptions()
        .setWorker(true)
        .setWorkerPoolSize(20);
    
    vertx.deployVerticle(new BlockingVerticle(), workerOptions);
    
    // High availability
    DeploymentOptions haOptions = new DeploymentOptions()
        .setHa(true);
    
    vertx.deployVerticle(new CriticalVerticle(), haOptions);
}
```

### 5. Configuration Management

```java
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;

public class ConfigExample extends AbstractVerticle {
    
    @Override
    public void start() {
        ConfigStoreOptions fileStore = new ConfigStoreOptions()
            .setType("file")
            .setFormat("json")
            .setConfig(new JsonObject()
                .put("path", "config.json"));
        
        ConfigStoreOptions envStore = new ConfigStoreOptions()
            .setType("env");
        
        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
            .addStore(fileStore)
            .addStore(envStore);
        
        ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
        
        retriever.getConfig()
            .onSuccess(config -> {
                int port = config.getInteger("http.port", 8080);
                String dbHost = config.getString("db.host");
                
                // Use configuration
                startServer(port, dbHost);
            });
    }
}
```

---

## Complete Examples {#complete-examples}

### Example 1: RESTful API with Database

```java
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

public class RestApiVerticle extends AbstractVerticle {
    
    private PgPool pool;
    
    @Override
    public void start() {
        // Database setup
        PgConnectOptions connectOptions = new PgConnectOptions()
            .setPort(5432)
            .setHost("localhost")
            .setDatabase("testdb")
            .setUser("user")
            .setPassword("password");
        
        PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
        pool = PgPool.pool(vertx, connectOptions, poolOptions);
        
        // Router setup
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        
        // Routes
        router.get("/api/products").handler(this::getAllProducts);
        router.get("/api/products/:id").handler(this::getProduct);
        router.post("/api/products").handler(this::createProduct);
        router.put("/api/products/:id").handler(this::updateProduct);
        router.delete("/api/products/:id").handler(this::deleteProduct);
        
        // Start server
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8080)
            .onSuccess(server -> {
                System.out.println("API server running on port 8080");
            });
    }
    
    private void getAllProducts(RoutingContext ctx) {
        pool.query("SELECT * FROM products ORDER BY id")
            .execute()
            .onSuccess(rows -> {
                JsonArray products = new JsonArray();
                for (Row row : rows) {
                    products.add(new JsonObject()
                        .put("id", row.getInteger("id"))
                        .put("name", row.getString("name"))
                        .put("price", row.getDouble("price"))
                        .put("quantity", row.getInteger("quantity")));
                }
                ctx.json(products);
            })
            .onFailure(err -> {
                ctx.response()
                    .setStatusCode(500)
                    .end("Error: " + err.getMessage());
            });
    }
    
    private void getProduct(RoutingContext ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        
        pool.preparedQuery("SELECT * FROM products WHERE id = $1")
            .execute(Tuple.of(id))
            .onSuccess(rows -> {
                if (rows.size() == 0) {
                    ctx.response().setStatusCode(404).end("Not found");
                } else {
                    Row row = rows.iterator().next();
                    JsonObject product = new JsonObject()
                        .put("id", row.getInteger("id"))
                        .put("name", row.getString("name"))
                        .put("price", row.getDouble("price"))
                        .put("quantity", row.getInteger("quantity"));
                    ctx.json(product);
                }
            })
            .onFailure(err -> ctx.fail(500, err));
    }
    
    private void createProduct(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        
        String sql = "INSERT INTO products (name, price, quantity) " +
                     "VALUES ($1, $2, $3) RETURNING id";
        
        pool.preparedQuery(sql)
            .execute(Tuple.of(
                body.getString("name"),
                body.getDouble("price"),
                body.getInteger("quantity")))
            .onSuccess(rows -> {
                int id = rows.iterator().next().getInteger("id");
                JsonObject response = body.copy().put("id", id);
                ctx.response()
                    .setStatusCode(201)
                    .putHeader("Location", "/api/products/" + id)
                    .end(response.encode());
            })
            .onFailure(err -> ctx.fail(500, err));
    }
    
    private void updateProduct(RoutingContext ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        JsonObject body = ctx.body().asJsonObject();
        
        String sql = "UPDATE products SET name = $1, price = $2, " +
                     "quantity = $3 WHERE id = $4";
        
        pool.preparedQuery(sql)
            .execute(Tuple.of(
                body.getString("name"),
                body.getDouble("price"),
                body.getInteger("quantity"),
                id))
            .onSuccess(result -> {
                if (result.rowCount() == 0) {
                    ctx.response().setStatusCode(404).end("Not found");
                } else {
                    ctx.json(body.put("id", id));
                }
            })
            .onFailure(err -> ctx.fail(500, err));
    }
    
    private void deleteProduct(RoutingContext ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        
        pool.preparedQuery("DELETE FROM products WHERE id = $1")
            .execute(Tuple.of(id))
            .onSuccess(result -> {
                if (result.rowCount() == 0) {
                    ctx.response().setStatusCode(404).end("Not found");
                } else {
                    ctx.response().setStatusCode(204).end();
                }
            })
            .onFailure(err -> ctx.fail(500, err));
    }
    
    @Override
    public void stop() {
        if (pool != null) {
            pool.close();
        }
    }
}
```

### Example 2: Microservices with Event Bus

**Service 1: User Service**

```java
public class UserServiceVerticle extends AbstractVerticle {
    
    private Map<String, JsonObject> users = new HashMap<>();
    
    @Override
    public void start() {
        EventBus eb = vertx.eventBus();
        
        // Register service consumers
        eb.consumer("user.create", this::createUser);
        eb.consumer("user.get", this::getUser);
        eb.consumer("user.list", this::listUsers);
        
        System.out.println("User Service started");
    }
    
    private void createUser(Message<JsonObject> message) {
        JsonObject userData = message.body();
        String userId = UUID.randomUUID().toString();
        
        JsonObject user = userData.copy()
            .put("id", userId)
            .put("createdAt", System.currentTimeMillis());
        
        users.put(userId, user);
        
        // Publish event
        vertx.eventBus().publish("user.created", user);
        
        message.reply(user);
    }
    
    private void getUser(Message<String> message) {
        String userId = message.body();
        JsonObject user = users.get(userId);
        
        if (user != null) {
            message.reply(user);
        } else {
            message.fail(404, "User not found");
        }
    }
    
    private void listUsers(Message<Void> message) {
        JsonArray userList = new JsonArray();
        users.values().forEach(userList::add);
        message.reply(userList);
    }
}
```

**Service 2: Order Service**

```java
public class OrderServiceVerticle extends AbstractVerticle {
    
    private Map<String, JsonObject> orders = new HashMap<>();
    
    @Override
    public void start() {
        EventBus eb = vertx.eventBus();
        
        // Subscribe to user events
        eb.consumer("user.created", message -> {
            JsonObject user = (JsonObject) message.body();
            System.out.println("New user created: " + 
                user.getString("id"));
            // Initialize order history for user
        });
        
        // Register service consumers
        eb.consumer("order.create", this::createOrder);
        eb.consumer("order.get", this::getOrder);
        eb.consumer("order.list", this::listOrders);
    }
    
    private void createOrder(Message<JsonObject> message) {
        JsonObject orderData = message.body();
        String userId = orderData.getString("userId");
        
        // Verify user exists
        vertx.eventBus().<JsonObject>request("user.get", userId)
            .onSuccess(userReply -> {
                String orderId = UUID.randomUUID().toString();
                
                JsonObject order = new JsonObject()
                    .put("id", orderId)
                    .put("userId", userId)
                    .put("items", orderData.getJsonArray("items"))
                    .put("total", orderData.getDouble("total"))
                    .put("createdAt", System.currentTimeMillis());
                
                orders.put(orderId, order);
                
                // Publish event
                vertx.eventBus().publish("order.created", order);
                
                message.reply(order);
            })
            .onFailure(err -> {
                message.fail(400, "Invalid user: " + userId);
            });
    }
    
    private void getOrder(Message<String> message) {
        String orderId = message.body();
        JsonObject order = orders.get(orderId);
        
        if (order != null) {
            message.reply(order);
        } else {
            message.fail(404, "Order not found");
        }
    }
    
    private void listOrders(Message<String> message) {
        String userId = message.body();
        JsonArray userOrders = new JsonArray();
        
        orders.values().stream()
            .filter(order -> order.getString("userId").equals(userId))
            .forEach(userOrders::add);
        
        message.reply(userOrders);
    }
}
```

**API Gateway:**

```java
public class ApiGatewayVerticle extends AbstractVerticle {
    
    @Override
    public void start() {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        
        EventBus eb = vertx.eventBus();
        
        // User endpoints
        router.post("/api/users").handler(ctx -> {
            JsonObject userData = ctx.body().asJsonObject();
            
            eb.<JsonObject>request("user.create", userData)
                .onSuccess(reply -> ctx.json(reply.body()))
                .onFailure(err -> ctx.fail(500, err));
        });
        
        router.get("/api/users/:id").handler(ctx -> {
            String userId = ctx.pathParam("id");
            
            eb.<JsonObject>request("user.get", userId)
                .onSuccess(reply -> ctx.json(reply.body()))
                .onFailure(err -> ctx.fail(404, err));
        });
        
        router.get("/api/users").handler(ctx -> {
            eb.<JsonArray>request("user.list", null)
                .onSuccess(reply -> ctx.json(reply.body()))
                .onFailure(err -> ctx.fail(500, err));
        });
        
        // Order endpoints
        router.post("/api/orders").handler(ctx -> {
            JsonObject orderData = ctx.body().asJsonObject();
            
            eb.<JsonObject>request("order.create", orderData)
                .onSuccess(reply -> {
                    ctx.response().setStatusCode(201)
                        .end(reply.body().encode());
                })
                .onFailure(err -> ctx.fail(400, err));
        });
        
        router.get("/api/orders/:id").handler(ctx -> {
            String orderId = ctx.pathParam("id");
            
            eb.<JsonObject>request("order.get", orderId)
                .onSuccess(reply -> ctx.json(reply.body()))
                .onFailure(err -> ctx.fail(404, err));
        });
        
        router.get("/api/users/:userId/orders").handler(ctx -> {
            String userId = ctx.pathParam("userId");
            
            eb.<JsonArray>request("order.list", userId)
                .onSuccess(reply -> ctx.json(reply.body()))
                .onFailure(err -> ctx.fail(500, err));
        });
        
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8080)
            .onSuccess(server -> {
                System.out.println("API Gateway on port 8080");
            });
    }
}
```

**Main Application:**

```java
public class MicroservicesApp {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        
        // Deploy microservices
        vertx.deployVerticle(new UserServiceVerticle())
            .compose(id -> vertx.deployVerticle(new OrderServiceVerticle()))
            .compose(id -> vertx.deployVerticle(new ApiGatewayVerticle()))
            .onSuccess(id -> {
                System.out.println("All services deployed");
            })
            .onFailure(err -> {
                System.err.println("Deployment failed: " + err);
            });
    }
}
```

---

## Summary

Vert.x is a powerful, reactive toolkit that excels at:

✅ Building high-performance, non-blocking applications
✅ Handling thousands of concurrent connections
✅ Creating microservices architectures
✅ Polyglot development
✅ Event-driven programming
✅ Integration with Spring Boot and other frameworks

**Key Takeaways:**

1. **Never block the event loop** - Use worker verticles for blocking operations
2. **Embrace asynchronous programming** - Use Futures, Promises, and reactive streams
3. **Use the Event Bus** - For loose coupling between components
4. **Integrate with Spring** - Combine reactive power with dependency injection
5. **Follow best practices** - Proper error handling, resource management, configuration

**Resources:**

- Official Documentation: https://vertx.io/docs/
- GitHub: https://github.com/eclipse-vertx/vert.x
- Examples: https://github.com/vert-x3/vertx-examples
- Community: https://groups.google.com/forum/#!forum/vertx

---

*This guide covers Vert.x 5.0.7. For the latest updates, refer to the official documentation.*
