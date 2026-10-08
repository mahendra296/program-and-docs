# Java Spring Boot Annotations - Complete Guide

A detailed reference for Spring and Spring Boot annotations (plus the JPA, validation and Lombok annotations used with them). Each annotation section explains what the annotation does, where it is used, every attribute (parameter) it accepts, a working example, how it works internally, and common mistakes.

> **Version note:** Examples target **Spring Boot 3.x / Spring Framework 6.x** (Java 17+). Validation annotations use the `jakarta.*` packages. On Spring Boot 2.x replace `jakarta.` with `javax.`.

> **Related guides:** [Database Systems (SQL, DDL/DML, isolation, locks, indexing)](Database_systems.md) · [Spring @Transactional Guide](java-spring-transactional.md) · [AWS Services Guide (Secrets Manager, SQS, SNS, EventBridge Scheduler)](AWS_Service_guide.md)

## Table of Contents

- [1. Spring Core / Component Annotations](#1-spring-core--component-annotations)
    - [1.1 Stereotype Annotations](#11-stereotype-annotations)
    - [1.2 Configuration & Bean Definition](#12-configuration--bean-definition)
    - [1.3 Bean Lifecycle & Initialization](#13-bean-lifecycle--initialization)
    - [1.4 Bean Scope](#14-bean-scope)
    - [1.5 Bean Selection & Ordering](#15-bean-selection--ordering)
- [2. Spring Boot Annotations](#2-spring-boot-annotations)
    - [2.1 Application Bootstrapping](#21-application-bootstrapping)
    - [2.2 Conditional Annotations](#22-conditional-annotations)
- [3. Spring Boot Configuration](#3-spring-boot-configuration)
    - [3.1 Type-safe Configuration Properties](#31-type-safe-configuration-properties)
    - [3.2 Property Sources & Values](#32-property-sources--values)
- [4. REST / Web MVC Annotations](#4-rest--web-mvc-annotations)
    - [4.1 Request Mapping Annotations](#41-request-mapping-annotations)
    - [4.2 Request Data Binding Annotations](#42-request-data-binding-annotations)
    - [4.3 Response Annotations](#43-response-annotations)
    - [4.4 CORS Annotation](#44-cors-annotation)
    - [4.5 Exception Handling Annotations](#45-exception-handling-annotations)
    - [4.6 Session Annotations](#46-session-annotations)
    - [4.7 Data Binder Customization](#47-data-binder-customization)
    - [4.8 HTTP Interface Clients](#48-http-interface-clients)
    - [4.9 REST Quick Reference Summary](#49-rest-quick-reference-summary)
- [5. Dependency Injection](#5-dependency-injection)
    - [5.1 Injection Annotations](#51-injection-annotations)
    - [5.2 Resolving Multiple Candidates](#52-resolving-multiple-candidates)
    - [5.3 Injection Annotations Compared](#53-injection-annotations-compared)
- [6. JPA / Database](#6-jpa--database)
    - [6.1 Entity & Table Mapping](#61-entity--table-mapping)
    - [6.2 Primary Keys](#62-primary-keys)
    - [6.3 Column Mapping](#63-column-mapping)
    - [6.4 Embeddables](#64-embeddables)
    - [6.5 Relationships](#65-relationships)
    - [6.6 Join Configuration](#66-join-configuration)
    - [6.7 Collections](#67-collections)
    - [6.8 Converters & Timestamps](#68-converters--timestamps)
    - [6.9 Inheritance](#69-inheritance)
    - [6.10 Named Queries & Entity Graphs](#610-named-queries--entity-graphs)
    - [6.11 Entity Lifecycle Callbacks](#611-entity-lifecycle-callbacks)
    - [6.12 Useful Hibernate Annotations](#612-useful-hibernate-annotations)
- [7. Spring Data JPA](#7-spring-data-jpa)
    - [7.1 Repository Configuration](#71-repository-configuration)
    - [7.2 Query Annotations](#72-query-annotations)
    - [7.3 Auditing](#73-auditing)
    - [7.4 Pagination, Sorting & Searching](#74-pagination-sorting--searching)
        - [7.4.1 Core Pagination Types](#741-core-pagination-types)
        - [7.4.2 Pagination Annotations](#742-pagination-annotations)
        - [7.4.3 Pagination Configuration Properties](#743-pagination-configuration-properties)
        - [7.4.4 Repository Level](#744-repository-level)
        - [7.4.5 Service Level](#745-service-level)
        - [7.4.6 Controller Level](#746-controller-level)
        - [7.4.7 Search Parameters with Specifications](#747-search-parameters-with-specifications)
        - [7.4.8 JPA Criteria API Query (EntityManager)](#748-jpa-criteria-api-query-entitymanager)
        - [7.4.9 Keyset (Scroll) Pagination](#749-keyset-scroll-pagination)
        - [7.4.10 Pagination Best Practices](#7410-pagination-best-practices)
        - [7.4.11 Pagination Quick Reference](#7411-pagination-quick-reference)
- [8. Transaction Management](#8-transaction-management)
    - [Full @Transactional guide ↗](java-spring-transactional.md) *separate file*
    - [8.1 Transaction Annotations](#81-transaction-annotations)
    - [8.2 Detailed Transaction Guide](#82-detailed-transaction-guide)
- [9. Spring Validation](#9-spring-validation)
    - [9.1 Triggering Validation](#91-triggering-validation)
    - [9.2 Constraint Basics](#92-constraint-basics)
    - [9.3 Null & Empty Checks](#93-null--empty-checks)
    - [9.4 Size & Number Constraints](#94-size--number-constraints)
    - [9.5 String Format Constraints](#95-string-format-constraints)
    - [9.6 Date & Time Constraints](#96-date--time-constraints)
    - [9.7 Boolean & Extra Constraints](#97-boolean--extra-constraints)
- [10. Spring Security](#10-spring-security)
    - [10.1 Security Configuration](#101-security-configuration)
    - [10.2 Method Authorization](#102-method-authorization)
    - [10.3 Accessing the Current User](#103-accessing-the-current-user)
- [11. Spring AOP](#11-spring-aop)
    - [11.1 Aspect Basics](#111-aspect-basics)
    - [11.2 Advice Annotations](#112-advice-annotations)
- [12. Scheduling](#12-scheduling)
    - [12.1 Scheduling Annotations](#121-scheduling-annotations)
- [13. Async Processing](#13-async-processing)
    - [13.1 Async Annotations](#131-async-annotations)
- [14. Caching](#14-caching)
    - [14.1 Enabling & Configuring](#141-enabling--configuring)
    - [14.2 Cache Operations](#142-cache-operations)
- [15. Event Handling](#15-event-handling)
    - [15.1 Event Listener Annotations](#151-event-listener-annotations)
- [16. Testing](#16-testing)
    - [16.1 Test Context & Slices](#161-test-context--slices)
    - [16.2 Mocking Beans](#162-mocking-beans)
    - [16.3 Test Configuration](#163-test-configuration)
- [17. Lombok — commonly used with Spring Boot](#17-lombok--commonly-used-with-spring-boot)
    - [17.1 Accessors & Data Classes](#171-accessors--data-classes)
    - [17.2 Constructors](#172-constructors)
    - [17.3 Builders](#173-builders)
    - [17.4 Object Methods](#174-object-methods)
    - [17.5 Null Checks, Logging & Utilities](#175-null-checks-logging--utilities)
    - [17.6 Lombok with Spring & JPA: Recommendations](#176-lombok-with-spring--jpa-recommendations)

- [Database Systems Guide ↗](Database_systems.md) *separate file: SQL categories, isolation levels, lock types, indexing, tuning*
- [AWS Services Guide ↗](AWS_Service_guide.md) *separate file: Secrets Manager, SQS, SNS, EventBridge Scheduler with Spring Boot*
---

# 1. Spring Core / Component Annotations

These annotations come from the **Spring Framework core** (`spring-context`). They tell the **IoC container** (the `ApplicationContext`) which classes to create as **beans**, how to configure them, and how to choose between them.

```
@SpringBootApplication
   └── @ComponentScan  ──► scans packages for @Component (and @Service, @Repository, @Controller...)
   └── @Configuration  ──► @Bean methods create extra beans
                            │
                            ▼
                     ApplicationContext (bean registry)
                            │
                            ▼
            Dependency Injection (@Autowired / constructor)
```

---

## 1.1 Stereotype Annotations

All stereotypes are **specializations of `@Component`**. Functionally they all register a bean; the specific annotation documents the **layer** and sometimes adds behavior.

| Annotation | Layer | Extra behavior |
|------------|-------|----------------|
| `@Component` | Generic bean | — |
| `@Service` | Business logic | None (documentation only) |
| `@Repository` | Data access | Database exceptions translated to Spring's `DataAccessException` |
| `@Controller` | Web (views) | Detected as an MVC handler |
| `@RestController` | Web (REST/JSON) | `@Controller` + `@ResponseBody` |

### @Component

**Package:** `org.springframework.stereotype.Component`
**Applies to:** Class

#### What it does
Marks a class as a **Spring-managed bean**. During component scanning, Spring finds the class, creates **one instance** (singleton by default), injects its dependencies, and stores it in the application context so other beans can use it.

Use `@Component` for general-purpose beans that are not clearly a service, repository or controller (helpers, mappers, validators, converters, scheduled jobs...).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Bean name. Empty → class name with first letter lower-cased (`EmailValidator` → `emailValidator`). |

#### Example

```java
@Component
public class EmailValidator {
    public boolean isValid(String email) {
        return email != null && email.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]+$");
    }
}

@Component("pdfExporter")          // custom bean name
public class PdfReportExporter implements ReportExporter { ... }

@Service
public class UserService {
    private final EmailValidator emailValidator;

    // Single constructor -> @Autowired is optional
    public UserService(EmailValidator emailValidator) {
        this.emailValidator = emailValidator;
    }
}
```

#### Common mistakes / best practices
- The class must be inside (or below) a **scanned package**. With `@SpringBootApplication`, that is the package of the main class and its sub-packages.
- Don't create a component with `new`; then Spring doesn't manage it and its dependencies are `null`.
- Use `@Bean` (in a `@Configuration`) instead when the class is from a **third-party library** you can't annotate.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Service

**Package:** `org.springframework.stereotype.Service`
**Applies to:** Class

#### What it does
A `@Component` for the **service (business logic) layer**. It adds no technical behavior, but it makes the architecture clear and lets tools/aspects target the service layer (e.g. a pointcut on `@within(org.springframework.stereotype.Service)`).

Services typically hold the business rules and the transaction boundaries (`@Transactional`).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Bean name. |

#### Example

```java
@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;

    public OrderService(OrderRepository orderRepository, PaymentClient paymentClient) {
        this.orderRepository = orderRepository;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public Order placeOrder(OrderRequest request) {
        Order order = Order.from(request);
        orderRepository.save(order);
        paymentClient.charge(order.getTotal());
        return order;
    }
}
```

#### Common mistakes / best practices
- Keep controllers thin and put the logic in services, so it can be reused and unit-tested.
- Prefer **constructor injection** with `final` fields.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Repository

**Package:** `org.springframework.stereotype.Repository`
**Applies to:** Class

#### What it does
A `@Component` for the **data access layer** (DAO). In addition to registering the bean, Spring wraps it in a proxy that performs **exception translation**: technology-specific exceptions (`SQLException`, `PersistenceException`, Hibernate exceptions) are converted to Spring's unchecked `DataAccessException` hierarchy (`DuplicateKeyException`, `DataIntegrityViolationException`, `EmptyResultDataAccessException`...). Your services then handle one consistent set of exceptions, whatever the database technology.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Bean name. |

#### Example

```java
// Hand-written DAO with JdbcTemplate
@Repository
public class ProductDao {

    private final JdbcTemplate jdbc;

    public ProductDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Product> findById(long id) {
        List<Product> list = jdbc.query(
                "SELECT id, name, price FROM products WHERE id = ?",
                (rs, n) -> new Product(rs.getLong("id"), rs.getString("name"), rs.getBigDecimal("price")),
                id);
        return list.stream().findFirst();
    }

    public void insert(Product p) {
        // A duplicate primary key surfaces as org.springframework.dao.DuplicateKeyException
        jdbc.update("INSERT INTO products (id, name, price) VALUES (?, ?, ?)",
                p.id(), p.name(), p.price());
    }
}

// Spring Data repository interface: @Repository is NOT needed
public interface ProductRepository extends JpaRepository<Product, Long> { }
```

#### Common mistakes / best practices
- Spring Data repository **interfaces** are detected automatically; adding `@Repository` to them is harmless but unnecessary.
- Catch `DataAccessException` subclasses in services, not `SQLException`.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Controller

**Package:** `org.springframework.stereotype.Controller`
**Applies to:** Class

#### What it does
Marks a class as a **Spring MVC web controller**. It is a specialization of `@Component`, so the class is detected by component scanning and registered as a bean. By default, the `String` returned from a handler method is treated as a **view name** (e.g. a Thymeleaf template), **not** as the response body.

Use `@Controller` for server-side rendered pages (HTML). For REST APIs that return JSON, use [@RestController](#restcontroller) (or add [@ResponseBody](#responsebody)).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Optional bean name. If empty, the bean name is the class name in camelCase (`userController`). |

#### Example

```java
@Controller
@RequestMapping("/users")
public class UserPageController {

    private final UserService userService;

    public UserPageController(UserService userService) {
        this.userService = userService;
    }

    // Returns view name "users/list" -> resolves to templates/users/list.html (Thymeleaf)
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users/list";
    }

    // Redirect after form submit (Post/Redirect/Get pattern)
    @PostMapping
    public String createUser(@ModelAttribute User user) {
        userService.save(user);
        return "redirect:/users";
    }

    // Returning JSON from a @Controller requires @ResponseBody
    @GetMapping("/{id}/json")
    @ResponseBody
    public User getUserJson(@PathVariable Long id) {
        return userService.findById(id);
    }
}
```

#### How it works internally
- `@Controller` is meta-annotated with `@Component` → picked up by `@ComponentScan`.
- `RequestMappingHandlerMapping` scans beans annotated with `@Controller` for `@RequestMapping` methods.
- Return value `String` → `ViewNameMethodReturnValueHandler` → `ViewResolver` renders the template.

#### Common mistakes / best practices
- Returning an object from a `@Controller` method without `@ResponseBody` → Spring tries to resolve a view and fails (`TemplateInputException` / 404).
- Prefixes `redirect:` and `forward:` are only meaningful in view-returning controllers.
- Keep controllers thin: delegate business logic to `@Service` classes.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @RestController

**Package:** `org.springframework.web.bind.annotation.RestController`
**Applies to:** Class

#### What it does
A convenience annotation that combines **`@Controller` + `@ResponseBody`**. Every handler method's return value is written **directly to the HTTP response body** (serialized to JSON/XML by an `HttpMessageConverter`, usually Jackson) instead of being treated as a view name.

This is the standard annotation for building REST APIs.

```java
// Equivalent definition (simplified)
@Controller
@ResponseBody
public @interface RestController { ... }
```

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Optional bean name (alias for `@Controller.value`). |

#### Example

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET /api/users  -> JSON array
    @GetMapping
    public List<User> getAll() {
        return userService.findAll();
    }

    // GET /api/users/1 -> JSON object
    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) {
        return userService.findById(id);
    }

    // POST /api/users -> 201 Created + Location header + JSON body
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        User saved = userService.save(user);
        URI location = URI.create("/api/users/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }

    // DELETE /api/users/1 -> 204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

Response for `GET /api/users/1`:
```json
{ "id": 1, "name": "Mahendra", "email": "m@example.com", "age": 30 }
```

#### How it works internally
- `RequestResponseBodyMethodProcessor` handles the return value because `@ResponseBody` is present at class level.
- The `Accept` request header + `produces` attribute decide which `HttpMessageConverter` is used (`MappingJackson2HttpMessageConverter` for JSON).

#### Common mistakes / best practices
- Don't return a view name from a `@RestController` — the string `"home"` is sent as plain text.
- Use `ResponseEntity<T>` when you need to control status code and headers.
- Return DTOs instead of JPA entities to avoid lazy-loading exceptions and leaking internal fields.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 1.2 Configuration & Bean Definition

### @Configuration

**Package:** `org.springframework.context.annotation.Configuration`
**Applies to:** Class

#### What it does
Marks a class as a **source of bean definitions**. Its `@Bean` methods create beans. It is itself a `@Component`, so it is picked up by component scanning.

By default (`proxyBeanMethods = true`) Spring subclasses the configuration class with CGLIB, so calling one `@Bean` method from another returns the **same singleton bean** instead of creating a new object.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Bean name of the configuration class itself. |
| `proxyBeanMethods` | `boolean` | `true` | `true` → `@Bean` methods are intercepted (inter-bean calls return the managed singleton). `false` ("lite mode") → no CGLIB proxy, faster startup, but calling a `@Bean` method directly creates a **new** object. |
| `enforceUniqueMethods` | `boolean` | `true` | (Spring 6.0+) Fails if two `@Bean` methods share the same name (overloads). Set `false` to allow overloaded `@Bean` methods. |

#### Example

```java
@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }

    @Bean
    public RestClient paymentRestClient(RestClient.Builder builder) {
        return builder.baseUrl("https://payments.example.com").build();
    }

    // Inter-bean call: with proxyBeanMethods=true this returns the SAME objectMapper bean
    @Bean
    public JsonExporter jsonExporter() {
        return new JsonExporter(objectMapper());
    }
}

// Lite mode: no proxy. Inject dependencies as method parameters instead of calling methods
@Configuration(proxyBeanMethods = false)
public class ClientConfig {

    @Bean
    public HttpClient httpClient() { return HttpClient.newHttpClient(); }

    @Bean
    public WeatherClient weatherClient(HttpClient httpClient) {   // parameter injection
        return new WeatherClient(httpClient);
    }
}
```

#### Common mistakes / best practices
- With `proxyBeanMethods = false`, never call another `@Bean` method directly; take it as a method parameter.
- `@Configuration` classes must not be `final` (when proxied), and `@Bean` methods must not be `private` or `final`.
- Spring Boot's own auto-configurations use `proxyBeanMethods = false` for faster startup.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Bean

**Package:** `org.springframework.context.annotation.Bean`
**Applies to:** Method (usually inside a `@Configuration` class)

#### What it does
Declares that the method's **return value is a bean** managed by Spring. Use it for objects you can't annotate (third-party classes) or that need custom construction logic. Method parameters are injected automatically.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` / `name` | `String[]` | `{}` | Bean name(s). The first is the name, others are aliases. Empty → the **method name**. |
| `autowireCandidate` | `boolean` | `true` | `false` → the bean is not injected by type into other beans (it can still be fetched by name). |
| `defaultCandidate` | `boolean` | `true` | (Spring 6.2+) `false` → only injected where explicitly **qualified** (by `@Qualifier` or name), not for plain by-type injection. |
| `initMethod` | `String` | `""` | Name of a method on the bean to call after creation (like `@PostConstruct`). |
| `destroyMethod` | `String` | `"(inferred)"` | Method called on shutdown. By default Spring **infers** a public `close()` or `shutdown()` method. Use `""` to disable. |

#### Example

```java
@Configuration
public class InfraConfig {

    // Bean name "dataSource", alias "mainDs"
    @Bean(name = {"dataSource", "mainDs"})
    public HikariDataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:postgresql://localhost:5432/app");
        ds.setUsername("app");
        ds.setPassword("secret");
        return ds;                    // close() is called automatically on shutdown (inferred)
    }

    // Custom init / destroy
    @Bean(initMethod = "connect", destroyMethod = "disconnect")
    public FtpClient ftpClient() {
        return new FtpClient("ftp.example.com");
    }

    // Not injected by type anywhere (avoids ambiguity with the main ObjectMapper)
    @Bean(autowireCandidate = false)
    public ObjectMapper legacyXmlMapper() {
        return new XmlMapper();
    }

    // Parameters are injected; combine with @Profile, @Primary, @Scope, @Lazy, @Conditional...
    @Bean
    @Profile("prod")
    public AuditClient auditClient(RestClient.Builder builder,
                                   @Value("${audit.url}") String url) {
        return new AuditClient(builder.baseUrl(url).build());
    }
}
```

#### Common mistakes / best practices
- Two `@Bean` methods returning the same type → `NoUniqueBeanDefinitionException` on injection. Use [@Primary](#52-resolving-multiple-candidates) or [@Qualifier](#52-resolving-multiple-candidates).
- `@Bean` methods returning `BeanFactoryPostProcessor`s should be `static`.
- If your bean has a `close()` you don't want called by Spring, set `destroyMethod = ""`.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @ComponentScan

**Package:** `org.springframework.context.annotation.ComponentScan`
**Applies to:** Class (a `@Configuration` class)

#### What it does
Tells Spring **which packages to scan** for `@Component` classes (including `@Service`, `@Repository`, `@Controller`, `@Configuration`). Without packages, it scans the package of the annotated class and all sub-packages.

`@SpringBootApplication` already includes `@ComponentScan`, so in Spring Boot you only add it to scan **extra** packages or to **filter** classes.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` / `basePackages` | `String[]` | `{}` | Packages to scan, e.g. `"com.example.shared"`. |
| `basePackageClasses` | `Class<?>[]` | `{}` | Type-safe alternative: the package of each listed class is scanned. |
| `includeFilters` | `Filter[]` | `{}` | Extra rules to **include** classes (even without `@Component`). |
| `excludeFilters` | `Filter[]` | `{}` | Rules to **exclude** classes. |
| `useDefaultFilters` | `boolean` | `true` | `false` → stop auto-detecting `@Component` classes; only `includeFilters` apply. |
| `lazyInit` | `boolean` | `false` | `true` → all scanned beans are created lazily (on first use). |
| `nameGenerator` | `Class<? extends BeanNameGenerator>` | `BeanNameGenerator.class` | Custom strategy for bean names (e.g. fully-qualified names to avoid clashes). |
| `scopeResolver` | `Class<? extends ScopeMetadataResolver>` | `AnnotationScopeMetadataResolver.class` | Custom scope detection. |
| `scopedProxy` | `ScopedProxyMode` | `DEFAULT` | Default proxy mode for scoped beans. |
| `resourcePattern` | `String` | `"**/*.class"` | Pattern of class files to consider. |

**`@ComponentScan.Filter` attributes:**

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `type` | `FilterType` | `ANNOTATION` | `ANNOTATION`, `ASSIGNABLE_TYPE`, `ASPECTJ`, `REGEX`, `CUSTOM`. |
| `value` / `classes` | `Class<?>[]` | `{}` | Annotation types, assignable types or `TypeFilter` classes. |
| `pattern` | `String[]` | `{}` | Regex or AspectJ patterns (for `REGEX` / `ASPECTJ`). |

#### Example

```java
@SpringBootApplication
@ComponentScan(
        basePackages = {"com.example.app", "com.example.shared"},     // add a package outside the main one
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.example\\.app\\.legacy\\..*"),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = OldMailSender.class)
        })
public class Application { ... }

// Type-safe package selection + include classes that have no @Component
@Configuration
@ComponentScan(
        basePackageClasses = PluginMarker.class,
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = Plugin.class))
public class PluginConfig { }
```

#### Common mistakes / best practices
- Declaring `@ComponentScan` on the main class **replaces** the default scan of `@SpringBootApplication`, so also list the main package.
- Put the main class in the **root package** so default scanning covers everything; then you rarely need `@ComponentScan`.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Import

**Package:** `org.springframework.context.annotation.Import`
**Applies to:** Class

#### What it does
Imports **other configuration classes** (or components) into the current one, without relying on component scanning. Commonly used to modularize configuration, in libraries, and in tests.

It accepts:
- `@Configuration` classes
- Regular component classes (registered as beans)
- `ImportSelector` implementations (decide at runtime which classes to import)
- `ImportBeanDefinitionRegistrar` implementations (register bean definitions programmatically)

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `Class<?>[]` | — (required) | Classes to import. |

#### Example

```java
@Configuration
public class SecurityConfig { ... }

@Configuration
public class PersistenceConfig { ... }

// Combine modules explicitly
@Configuration
@Import({SecurityConfig.class, PersistenceConfig.class, AuditService.class})
public class AppConfig { }

// "Enable" annotation pattern used by many libraries
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(MetricsConfiguration.class)
public @interface EnableMetrics { }

@SpringBootApplication
@EnableMetrics                         // imports MetricsConfiguration
public class Application { }

// In tests: load only what you need
@WebMvcTest(UserController.class)
@Import(UserMapper.class)
class UserControllerTest { ... }
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @ImportResource

**Package:** `org.springframework.context.annotation.ImportResource`
**Applies to:** Class

#### What it does
Loads bean definitions from **XML configuration files** into an annotation-based application. Used when migrating legacy Spring XML applications or when a library only provides XML config.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` / `locations` | `String[]` | `{}` | Resource locations, e.g. `"classpath:legacy-beans.xml"`. Wildcards allowed (`classpath*:config/*.xml`). |
| `reader` | `Class<? extends BeanDefinitionReader>` | `BeanDefinitionReader.class` | Reader used to parse the resources (XML reader by default for `.xml`). |

#### Example

```java
@SpringBootApplication
@ImportResource("classpath:legacy-beans.xml")
public class Application { }
```

`src/main/resources/legacy-beans.xml`:
```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="legacyMailer" class="com.example.legacy.SmtpMailer">
        <property name="host" value="smtp.example.com"/>
    </bean>
</beans>
```

```java
@Service
public class NotificationService {
    public NotificationService(SmtpMailer legacyMailer) { ... }   // XML-defined bean injected normally
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 1.3 Bean Lifecycle & Initialization

```
Bean lifecycle
  1. Instantiate (constructor)              ← constructor injection
  2. Inject dependencies (fields/setters)   ← @Autowired fields/setters
  3. Aware callbacks (BeanNameAware...)
  4. @PostConstruct / afterPropertiesSet() / @Bean(initMethod)
  5. Bean ready (proxies applied: @Transactional, @Async, @Cacheable...)
  ...
  6. On shutdown: @PreDestroy / destroy() / @Bean(destroyMethod)
```

### @PostConstruct

**Package:** `jakarta.annotation.PostConstruct` (Jakarta Annotations, supported by Spring)
**Applies to:** Method

#### What it does
Marks a method to run **once, right after the bean is created and all dependencies are injected**. Use it for initialization that needs the injected dependencies (loading a cache, validating configuration, opening a connection).

The method must have no parameters, return `void`, and may have any visibility.

#### Attributes
None.

#### Example

```java
@Component
public class CountryCache {

    private final CountryRepository repository;
    private Map<String, Country> byCode = Map.of();

    public CountryCache(CountryRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    void load() {
        byCode = repository.findAll().stream()
                .collect(Collectors.toMap(Country::getCode, c -> c));
        log.info("Loaded {} countries", byCode.size());
    }

    public Country get(String code) { return byCode.get(code); }
}
```

#### Common mistakes / best practices
- `@Transactional` does **not** apply inside `@PostConstruct` (the proxy is not ready yet). For DB work needing a transaction, listen to `ApplicationReadyEvent` instead.
- Keep it fast; a slow `@PostConstruct` delays application startup.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @PreDestroy

**Package:** `jakarta.annotation.PreDestroy`
**Applies to:** Method

#### What it does
Marks a method to run **when the bean is being destroyed** (application shutdown, or end of a request/session for scoped beans). Use it to release resources: close connections, flush buffers, stop threads.

#### Attributes
None.

#### Example

```java
@Component
public class MetricsBuffer {

    private final List<Metric> buffer = new CopyOnWriteArrayList<>();
    private final MetricsClient client;

    public MetricsBuffer(MetricsClient client) { this.client = client; }

    public void add(Metric m) { buffer.add(m); }

    @PreDestroy
    void flushOnShutdown() {
        client.sendAll(buffer);          // don't lose buffered metrics on shutdown
        buffer.clear();
    }
}
```

#### Common mistakes / best practices
- Not called for **prototype** beans (Spring doesn't track them after creation).
- Not called if the JVM is killed (`kill -9`). Enable graceful shutdown with `server.shutdown=graceful`.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @DependsOn

**Package:** `org.springframework.context.annotation.DependsOn`
**Applies to:** Class (component), `@Bean` method

#### What it does
Forces the listed beans to be **initialized before** this bean (and destroyed after it), even though this bean doesn't inject them. Needed only for **hidden dependencies**, e.g. a bean that requires a database migration or a static registry to be set up first.

Normal injection already guarantees ordering, so you rarely need it.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String[]` | `{}` | Names of beans that must be created first. |

#### Example

```java
@Component("schemaInitializer")
public class SchemaInitializer {
    @PostConstruct
    void createTables() { ... }
}

// Reads tables via static JDBC calls (no injection), so declare the dependency explicitly
@Component
@DependsOn("schemaInitializer")
public class LegacyReportJob { ... }

@Bean
@DependsOn({"flyway", "cacheWarmer"})
public ReportGenerator reportGenerator() { return new ReportGenerator(); }
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Lazy

**Package:** `org.springframework.context.annotation.Lazy`
**Applies to:** Class, `@Bean` method, `@Configuration` class, injection point (constructor parameter, field, setter)

#### What it does
By default, singleton beans are created **eagerly at startup**. `@Lazy` delays creation until the bean is **first used**.

- On a **class / `@Bean`** → the bean is created on first request.
- On a **`@Configuration` class** → all its `@Bean`s are lazy.
- On an **injection point** → Spring injects a **lazy proxy**; the real bean is resolved on the first method call. This is also a way to break **circular dependencies**.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `boolean` | `true` | Whether lazy initialization applies. `@Lazy(false)` forces eager creation even when global lazy init is on. |

#### Example

```java
// Heavy bean: only built if a report is actually requested
@Component
@Lazy
public class PdfEngine {
    public PdfEngine() { /* loads fonts, templates... takes 3 seconds */ }
}

@Service
public class ReportService {
    private final PdfEngine pdfEngine;

    // Lazy proxy injected; PdfEngine is created on first pdfEngine.render(...) call
    public ReportService(@Lazy PdfEngine pdfEngine) {
        this.pdfEngine = pdfEngine;
    }
}

// Breaking a circular dependency A <-> B
@Service
public class AService {
    private final BService b;
    public AService(@Lazy BService b) { this.b = b; }
}
```

Global lazy initialization:
```properties
spring.main.lazy-initialization=true
```

#### Common mistakes / best practices
- Lazy beans hide configuration errors until the first request; keep critical beans eager in production.
- Circular dependencies are usually a design smell; prefer refactoring over `@Lazy`. (Spring Boot 2.6+ forbids circular references by default.)

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Lookup

**Package:** `org.springframework.beans.factory.annotation.Lookup`
**Applies to:** Method

#### What it does
**Method injection**: Spring overrides the annotated method (using CGLIB) so that each call returns a bean **from the container**. The typical use is getting a **new prototype bean** each time from inside a singleton. (Normal injection gives a singleton the same prototype instance forever.)

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Bean name to look up. Empty → looked up by the method's **return type**. |

#### Example

```java
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ShoppingCart {
    private final List<Item> items = new ArrayList<>();
    public void add(Item i) { items.add(i); }
}

@Component
public abstract class CheckoutService {          // can be abstract

    // Spring implements this method: returns a NEW ShoppingCart every call
    @Lookup
    protected abstract ShoppingCart newCart();

    public ShoppingCart startCheckout() {
        return newCart();
    }
}
```

Alternative without `@Lookup`: inject `ObjectProvider<ShoppingCart>` and call `provider.getObject()`.

#### Common mistakes / best practices
- Works only for beans created by **component scanning**, not for beans returned from `@Bean` factory methods.
- The class and method must not be `final` or `private`.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 1.4 Bean Scope

### @Scope

**Package:** `org.springframework.context.annotation.Scope`
**Applies to:** Class (component), `@Bean` method

#### What it does
Defines the **lifecycle and sharing** of a bean (how many instances exist and how long they live).

| Scope | Constant | Instances |
|-------|----------|-----------|
| `singleton` (default) | `ConfigurableBeanFactory.SCOPE_SINGLETON` | One per application context |
| `prototype` | `ConfigurableBeanFactory.SCOPE_PROTOTYPE` | New instance every time it's injected/requested |
| `request` | `WebApplicationContext.SCOPE_REQUEST` | One per HTTP request |
| `session` | `WebApplicationContext.SCOPE_SESSION` | One per HTTP session |
| `application` | `WebApplicationContext.SCOPE_APPLICATION` | One per `ServletContext` |
| `websocket` | `"websocket"` | One per WebSocket session |

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` / `scopeName` | `String` | `""` (singleton) | Scope name. |
| `proxyMode` | `ScopedProxyMode` | `DEFAULT` (= `NO`) | Whether to inject a **proxy** instead of the real object. Required when a shorter-lived bean (request/session) is injected into a longer-lived one (singleton). `TARGET_CLASS` = CGLIB proxy, `INTERFACES` = JDK proxy, `NO` = no proxy. |

#### Example

```java
// New object every time
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CsvBuilder { ... }

// One per HTTP request, injectable into singletons through a proxy
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestContext {
    private String correlationId;
    // getters/setters
}

@Service
public class AuditService {                 // singleton
    private final RequestContext ctx;       // proxy: resolves the current request's instance on each call
    public AuditService(RequestContext ctx) { this.ctx = ctx; }

    public void log(String action) {
        System.out.println(ctx.getCorrelationId() + " " + action);
    }
}
```

#### Common mistakes / best practices
- Injecting a **prototype** into a singleton gives only **one** prototype instance. Use `ObjectProvider<T>`, [@Lookup](#lookup), or a scoped proxy.
- Using a request-scoped bean outside an HTTP request (e.g. `@Async` thread) → `ScopeNotActiveException`.
- Singletons must be **thread-safe**: don't keep per-request state in fields.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @RequestScope / @SessionScope / @ApplicationScope

**Package:** `org.springframework.web.context.annotation`
**Applies to:** Class (component), `@Bean` method

#### What they do
Shortcuts for `@Scope("request")`, `@Scope("session")` and `@Scope("application")`, with **`proxyMode = TARGET_CLASS` by default**, so they can be injected directly into singletons.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `proxyMode` | `ScopedProxyMode` | `TARGET_CLASS` | Proxy type (alias for `@Scope.proxyMode`). |

#### Example

```java
@Component
@SessionScope
public class UserPreferences implements Serializable {
    private String theme = "light";
    private Locale locale = Locale.ENGLISH;
    // getters/setters
}

@Component
@RequestScope
public class RequestTimer {
    private final long start = System.currentTimeMillis();
    public long elapsed() { return System.currentTimeMillis() - start; }
}

@RestController
public class PrefsController {
    private final UserPreferences prefs;    // a different instance per user session
    public PrefsController(UserPreferences prefs) { this.prefs = prefs; }

    @PostMapping("/prefs/theme")
    public String setTheme(@RequestParam String theme) {
        prefs.setTheme(theme);
        return "ok";
    }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 1.5 Bean Selection & Ordering

> `@Primary` and `@Qualifier` also belong to this group; they are explained in detail in [5.2 Resolving Multiple Candidates](#52-resolving-multiple-candidates).

### @Profile

**Package:** `org.springframework.context.annotation.Profile`
**Applies to:** Class (component / `@Configuration`), `@Bean` method

#### What it does
Registers the bean **only when one of the given profiles is active**. Used for environment-specific beans: a fake mail sender in `dev`, a real one in `prod`, in-memory storage in `test`...

Activate profiles with `spring.profiles.active=prod` (property, environment variable `SPRING_PROFILES_ACTIVE`, or `--spring.profiles.active=prod`).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String[]` | — (required) | Profile names or **profile expressions**. Several values = OR. |

**Profile expressions:**

| Expression | Active when |
|------------|-------------|
| `"dev"` | `dev` is active |
| `"!prod"` | `prod` is **not** active |
| `{"dev", "test"}` | `dev` **or** `test` |
| `"prod & eu"` | `prod` **and** `eu` |
| `"dev \| qa"` | `dev` **or** `qa` |
| `"prod & !eu"` | `prod` and not `eu` |

#### Example

```java
public interface MailSender { void send(Mail mail); }

@Component
@Profile("prod")
public class SmtpMailSender implements MailSender { ... }

@Component
@Profile("!prod")                         // dev, test, local...
public class LoggingMailSender implements MailSender {
    public void send(Mail mail) { log.info("Would send: {}", mail); }
}

@Configuration
@Profile({"dev", "test"})
public class DevDataConfig {
    @Bean
    CommandLineRunner seedData(UserRepository repo) {
        return args -> repo.save(new User("demo"));
    }
}
```

```properties
# application.properties
spring.profiles.active=dev

# application-dev.properties is loaded automatically when "dev" is active
```

#### Common mistakes / best practices
- If no profile is active, the `default` profile is active: `@Profile("default")` beans are registered.
- Profile-specific **property files** (`application-prod.yml`) are often simpler than profile-specific beans.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Conditional

**Package:** `org.springframework.context.annotation.Conditional`
**Applies to:** Class, `@Bean` method

#### What it does
Registers the bean **only if all given `Condition`s match**. `@Profile` is itself built on `@Conditional`. Spring Boot provides many ready-made conditions (`@ConditionalOnProperty`, `@ConditionalOnClass`...); see [2.2 Conditional Annotations](#22-conditional-annotations). Write a custom `Condition` when none of them fits.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `Class<? extends Condition>[]` | — (required) | Conditions that must all match. |

#### Example

```java
public class OnLinuxCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String os = context.getEnvironment().getProperty("os.name", "");
        return os.toLowerCase().contains("linux");
    }
}

@Configuration
public class FileWatcherConfig {

    @Bean
    @Conditional(OnLinuxCondition.class)
    public FileWatcher inotifyWatcher() { return new InotifyFileWatcher(); }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Order

**Package:** `org.springframework.core.annotation.Order`
**Applies to:** Class, `@Bean` method, field

#### What it does
Defines the **sort order** of beans when Spring collects several of them, **lower values first**. It affects:
- Injection of `List<T>` / arrays / `ObjectProvider.orderedStream()`
- Order of aspects, `@ControllerAdvice`, servlet filters (`OncePerRequestFilter` components), `CommandLineRunner`s, event listeners

It does **not** control the order in which singleton beans are **created** (use `@DependsOn` for that).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `int` | `Ordered.LOWEST_PRECEDENCE` (`Integer.MAX_VALUE`) | Order value. `Ordered.HIGHEST_PRECEDENCE` (`Integer.MIN_VALUE`) runs first. |

#### Example

```java
public interface OrderValidator { void validate(Order order); }

@Component @Order(1)
public class StockValidator implements OrderValidator { ... }

@Component @Order(2)
public class CreditLimitValidator implements OrderValidator { ... }

@Component @Order(3)
public class FraudValidator implements OrderValidator { ... }

@Service
public class OrderValidationService {
    private final List<OrderValidator> validators;   // [Stock, CreditLimit, Fraud] in this order

    public OrderValidationService(List<OrderValidator> validators) {
        this.validators = validators;
    }

    public void validate(Order order) {
        validators.forEach(v -> v.validate(order));
    }
}

// Startup runners in order
@Component @Order(1)
class MigrateRunner implements CommandLineRunner { public void run(String... a) { ... } }

@Component @Order(2)
class WarmCacheRunner implements CommandLineRunner { public void run(String... a) { ... } }
```

Alternative: implement the `Ordered` interface (`getOrder()`) when the order must be computed at runtime.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Priority

**Package:** `jakarta.annotation.Priority`
**Applies to:** Class

#### What it does
A standard (Jakarta) annotation that Spring supports in two ways:
1. **Ordering**, like `@Order` (lower value first).
2. **Choosing a single candidate**: when several beans match an injection point and none is `@Primary`, Spring injects the one with the **highest priority (lowest value)**.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `int` | — (required) | Priority value; lower = higher priority. |

#### Example

```java
public interface PaymentGateway { Receipt pay(BigDecimal amount); }

@Component
@Priority(1)                     // chosen for single injection
public class StripeGateway implements PaymentGateway { ... }

@Component
@Priority(2)
public class PaypalGateway implements PaymentGateway { ... }

@Service
public class CheckoutService {
    public CheckoutService(PaymentGateway gateway) { ... }   // StripeGateway injected
}
```

> `@Priority` can't be placed on `@Bean` methods. For `@Bean`s use `@Order` (ordering) or `@Primary` (selection).

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Description

**Package:** `org.springframework.context.annotation.Description`
**Applies to:** Class (component), `@Bean` method

#### What it does
Adds a human-readable **description** to the bean definition (`BeanDefinition.getDescription()`). It has no runtime effect; it's useful for documentation and for tools that display bean metadata (for example, JMX exporters).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | — (required) | The description text. |

#### Example

```java
@Configuration
public class CacheConfig {

    @Bean
    @Description("Caffeine cache for product catalogue, 10 min TTL")
    public CacheManager productCacheManager() { ... }
}

// Reading it
BeanDefinition bd = ((ConfigurableApplicationContext) ctx).getBeanFactory()
        .getBeanDefinition("productCacheManager");
System.out.println(bd.getDescription());
```

[⬆ Back to Table of Contents](#table-of-contents)

---

# 2. Spring Boot Annotations

## 2.1 Application Bootstrapping

### @SpringBootApplication

**Package:** `org.springframework.boot.autoconfigure.SpringBootApplication`
**Applies to:** Class (the main application class)

#### What it does
The entry point annotation of every Spring Boot application. It is a combination of three annotations:

```java
@SpringBootConfiguration   // this class is a @Configuration
@EnableAutoConfiguration   // configure beans automatically based on the classpath
@ComponentScan             // scan this package and its sub-packages
public @interface SpringBootApplication { }
```

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `exclude` | `Class<?>[]` | `{}` | Auto-configuration classes to disable (alias for `@EnableAutoConfiguration.exclude`). |
| `excludeName` | `String[]` | `{}` | Same as `exclude`, using fully-qualified class names (for classes that may not be on the classpath). |
| `scanBasePackages` | `String[]` | `{}` | Packages to scan (alias for `@ComponentScan.basePackages`). Default: the main class's package. |
| `scanBasePackageClasses` | `Class<?>[]` | `{}` | Type-safe alternative to `scanBasePackages`. |
| `nameGenerator` | `Class<? extends BeanNameGenerator>` | `BeanNameGenerator.class` | Bean name generator for scanned components. |
| `proxyBeanMethods` | `boolean` | `true` | Whether `@Bean` methods in the main class are proxied (see [@Configuration](#configuration)). |

#### Example

```java
package com.example.shop;            // root package: everything below is scanned

@SpringBootApplication
public class ShopApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
    }
}

// Disable an auto-configuration + scan an extra package
@SpringBootApplication(
        exclude = {DataSourceAutoConfiguration.class},
        scanBasePackages = {"com.example.shop", "com.example.common"})
public class ShopApplication { ... }
```

Typical package layout:
```
com.example.shop
 ├── ShopApplication.java        ← @SpringBootApplication
 ├── controller/
 ├── service/
 ├── repository/
 └── config/
```

#### Common mistakes / best practices
- Putting the main class in a **sub-package** (e.g. `com.example.shop.app`) means sibling packages like `com.example.shop.service` are **not scanned**.
- Avoid adding many `@Bean` methods to the main class; use dedicated `@Configuration` classes (test slices load the main class too).

[⬆ Back to Table of Contents](#table-of-contents)

---

### @EnableAutoConfiguration

**Package:** `org.springframework.boot.autoconfigure.EnableAutoConfiguration`
**Applies to:** Class

#### What it does
Turns on Spring Boot's **auto-configuration**: Boot looks at the classpath, existing beans and properties, and automatically creates the beans you probably need. Examples:
- `spring-boot-starter-web` on the classpath → embedded Tomcat, `DispatcherServlet`, Jackson `ObjectMapper`
- `spring-boot-starter-data-jpa` + a JDBC driver → `DataSource`, `EntityManagerFactory`, `JpaTransactionManager`

Auto-configuration classes are listed in `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` inside each Boot jar. They are guarded by `@ConditionalOn...` annotations and **back off** when you define your own bean.

Already included in `@SpringBootApplication`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `exclude` | `Class<?>[]` | `{}` | Auto-configuration classes that should never be applied. |
| `excludeName` | `String[]` | `{}` | Fully-qualified names of auto-configuration classes to exclude. |

#### Example

```java
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class Application { }
```

Excluding with a property (no code change):
```properties
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
```

See which auto-configurations were applied and why:
```properties
debug=true      # prints the "CONDITIONS EVALUATION REPORT" at startup
```
Or with Actuator: `GET /actuator/conditions`.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @SpringBootConfiguration

**Package:** `org.springframework.boot.SpringBootConfiguration`
**Applies to:** Class

#### What it does
A specialized `@Configuration` that marks the **primary configuration** of the application. Behaves like `@Configuration`, but Spring Boot's **test support** searches upwards from the test's package for the class annotated with `@SpringBootConfiguration`, and uses it to build the test context (`@SpringBootTest`, `@WebMvcTest`...).

An application should have **only one** `@SpringBootConfiguration` (normally via `@SpringBootApplication`).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `proxyBeanMethods` | `boolean` | `true` | Same as `@Configuration.proxyBeanMethods`. |

#### Example

```java
// Rarely written by hand: equivalent of @SpringBootApplication without the default scanning
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.example.shop.api")
public class ApiApplication {
    public static void main(String[] args) { SpringApplication.run(ApiApplication.class, args); }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @AutoConfiguration

**Package:** `org.springframework.boot.autoconfigure.AutoConfiguration`
**Applies to:** Class

#### What it does
(Spring Boot 2.7+) Marks a class as an **auto-configuration** for your own library or starter. It is a `@Configuration(proxyBeanMethods = false)` that also defines ordering relative to other auto-configurations. The class must be listed in `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` (it is **not** found by component scanning).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Bean name. |
| `before` / `beforeName` | `Class<?>[]` / `String[]` | `{}` | Apply this auto-configuration **before** these ones. |
| `after` / `afterName` | `Class<?>[]` / `String[]` | `{}` | Apply **after** these ones (e.g. after `DataSourceAutoConfiguration`). |

#### Example: a custom starter

```java
@AutoConfiguration(after = JacksonAutoConfiguration.class)
@ConditionalOnClass(AuditClient.class)
@EnableConfigurationProperties(AuditProperties.class)
public class AuditAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean                       // backs off if the app defines its own
    public AuditClient auditClient(AuditProperties props) {
        return new AuditClient(props.url());
    }
}
```

`src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`:
```
com.example.audit.autoconfigure.AuditAutoConfiguration
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @ServletComponentScan

**Package:** `org.springframework.boot.web.servlet.ServletComponentScan`
**Applies to:** Class

#### What it does
Registers classes annotated with the **Servlet API** annotations `@WebServlet`, `@WebFilter` and `@WebListener` when running with an **embedded** servlet container. Without it, those annotations are ignored in Spring Boot.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` / `basePackages` | `String[]` | `{}` | Packages to scan. Default: package of the annotated class. |
| `basePackageClasses` | `Class<?>[]` | `{}` | Type-safe package selection. |

#### Example

```java
@SpringBootApplication
@ServletComponentScan
public class Application { }

@WebFilter(urlPatterns = "/api/*")
public class TimingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        long start = System.nanoTime();
        chain.doFilter(req, res);
        System.out.println("Took " + (System.nanoTime() - start) / 1_000_000 + " ms");
    }
}
```

> The Spring way is to declare a filter as a `@Component` (or a `FilterRegistrationBean` for URL patterns/order); `@ServletComponentScan` is mainly for existing servlet code.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 2.2 Conditional Annotations

Spring Boot's conditions (package `org.springframework.boot.autoconfigure.condition`) decide whether a `@Configuration` class or `@Bean` is registered. They are what makes auto-configuration "smart", and you can use them in your own configuration too.

| Annotation | Bean registered when... |
|------------|-------------------------|
| `@ConditionalOnProperty` | a property has a given value |
| `@ConditionalOnClass` / `@ConditionalOnMissingClass` | a class is / isn't on the classpath |
| `@ConditionalOnBean` / `@ConditionalOnMissingBean` | a bean exists / doesn't exist |
| `@ConditionalOnWebApplication` / `@ConditionalOnNotWebApplication` | the app is / isn't a web app |
| `@ConditionalOnExpression` | a SpEL expression is true |
| `@ConditionalOnResource` | a resource exists |
| `@ConditionalOnJava` | the Java version matches a range |
| `@ConditionalOnCloudPlatform` | running on Kubernetes, Cloud Foundry, Heroku... |

### @ConditionalOnProperty

**Applies to:** Class, `@Bean` method

#### What it does
Registers the bean only when a **configuration property** has the expected value. The most common way to make features switchable.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `prefix` | `String` | `""` | Prefix added to each name, e.g. `"app.cache"`. |
| `name` / `value` | `String[]` | `{}` | Property name(s). With several names, **all** must match. |
| `havingValue` | `String` | `""` | Expected value. Empty → the property must exist and **not be `false`**. |
| `matchIfMissing` | `boolean` | `false` | Whether to match when the property is **not set**. |

#### Example

```java
@Configuration
public class NotificationConfig {

    // app.notifications.sms.enabled=true  -> registered
    @Bean
    @ConditionalOnProperty(prefix = "app.notifications.sms", name = "enabled", havingValue = "true")
    public SmsSender smsSender() { return new TwilioSmsSender(); }

    // Default ON: registered unless app.audit.enabled=false
    @Bean
    @ConditionalOnProperty(name = "app.audit.enabled", havingValue = "true", matchIfMissing = true)
    public AuditService auditService() { return new AuditService(); }

    // Choose implementation by value
    @Bean
    @ConditionalOnProperty(name = "app.storage.type", havingValue = "s3")
    public FileStorage s3Storage() { return new S3FileStorage(); }

    @Bean
    @ConditionalOnProperty(name = "app.storage.type", havingValue = "local", matchIfMissing = true)
    public FileStorage localStorage() { return new LocalFileStorage(); }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @ConditionalOnClass / @ConditionalOnMissingClass

**Applies to:** Class, `@Bean` method

#### What they do
- `@ConditionalOnClass` → registered only if the given classes **are on the classpath**.
- `@ConditionalOnMissingClass` → registered only if the classes are **absent**.

Used to configure integrations only when the library is present.

#### Attributes

| Annotation | Attribute | Type | Description |
|------------|-----------|------|-------------|
| `@ConditionalOnClass` | `value` | `Class<?>[]` | Classes that must be present. |
| `@ConditionalOnClass` | `name` | `String[]` | Fully-qualified class names (safe when the class might be missing at compile time). |
| `@ConditionalOnMissingClass` | `value` | `String[]` | Fully-qualified names of classes that must be absent. |

#### Example

```java
@Configuration
@ConditionalOnClass(name = "com.github.benmanes.caffeine.cache.Caffeine")
public class CaffeineCacheConfig {
    @Bean
    public CacheManager cacheManager() { return new CaffeineCacheManager(); }
}

@Configuration
@ConditionalOnMissingClass("com.github.benmanes.caffeine.cache.Caffeine")
public class SimpleCacheConfig {
    @Bean
    public CacheManager cacheManager() { return new ConcurrentMapCacheManager(); }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @ConditionalOnBean / @ConditionalOnMissingBean

**Applies to:** Class, `@Bean` method

#### What they do
- `@ConditionalOnBean` → registered only if a matching bean **already exists**.
- `@ConditionalOnMissingBean` → registered only if **no** matching bean exists. This is how auto-configuration **backs off** when you define your own bean.

#### Attributes (both)

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `Class<?>[]` | `{}` | Bean types to check. On a `@Bean` method with no attributes, the **return type** is used. |
| `type` | `String[]` | `{}` | Bean type names. |
| `name` | `String[]` | `{}` | Bean names. |
| `annotation` | `Class<? extends Annotation>[]` | `{}` | Beans annotated with these annotations. |
| `search` | `SearchStrategy` | `ALL` | Where to look: `CURRENT`, `ANCESTORS`, `ALL` contexts. |
| `parameterizedContainer` | `Class<?>[]` | `{}` | Container types like `ObjectProvider` that wrap the bean type. |
| `ignored` / `ignoredType` | `Class<?>[]` / `String[]` | `{}` | (`@ConditionalOnMissingBean` only) Types to ignore when checking. |

#### Example

```java
@Configuration
public class MapperConfig {

    // Default ObjectMapper, unless the application already defines one
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() { return new ObjectMapper(); }

    // Only when a DataSource bean exists
    @Bean
    @ConditionalOnBean(DataSource.class)
    public DbHealthIndicator dbHealthIndicator(DataSource ds) { return new DbHealthIndicator(ds); }
}
```

> These conditions depend on **registration order**, so they are reliable in **auto-configuration** classes (processed after user configuration). In normal `@Configuration` classes the result may depend on processing order.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @ConditionalOnWebApplication / @ConditionalOnNotWebApplication

**Applies to:** Class, `@Bean` method

#### What they do
Register beans only in a **web** application (servlet or reactive) or only in a **non-web** application (CLI tools, batch jobs).

#### Attributes

| Annotation | Attribute | Type | Default | Description |
|------------|-----------|------|---------|-------------|
| `@ConditionalOnWebApplication` | `type` | `Type` | `ANY` | `ANY`, `SERVLET` (Spring MVC) or `REACTIVE` (WebFlux). |
| `@ConditionalOnNotWebApplication` | — | — | — | No attributes. |

#### Example

```java
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class MvcExtrasConfig {
    @Bean
    public FilterRegistrationBean<RequestIdFilter> requestIdFilter() { ... }
}

@Configuration
@ConditionalOnNotWebApplication
public class CliConfig {
    @Bean
    public CommandLineRunner importer() { return args -> System.out.println("Running import..."); }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @ConditionalOnExpression / @ConditionalOnResource

**Applies to:** Class, `@Bean` method

#### What they do
- `@ConditionalOnExpression` → registered if a **SpEL expression** evaluates to `true`. Useful for combining several properties.
- `@ConditionalOnResource` → registered if the given **resources exist**.

#### Attributes

| Annotation | Attribute | Type | Default | Description |
|------------|-----------|------|---------|-------------|
| `@ConditionalOnExpression` | `value` | `String` | `"true"` | SpEL expression. |
| `@ConditionalOnResource` | `resources` | `String[]` | `{}` | Resource locations that must exist, e.g. `"classpath:keys/private.pem"`. |

#### Example

```java
@Bean
@ConditionalOnExpression("${app.reports.enabled:false} and '${app.env}' == 'prod'")
public ReportScheduler reportScheduler() { return new ReportScheduler(); }

@Bean
@ConditionalOnResource(resources = "classpath:license.key")
public LicenseValidator licenseValidator() { return new LicenseValidator(); }
```

> Prefer `@ConditionalOnProperty` when possible: it's simpler and doesn't need SpEL.

[⬆ Back to Table of Contents](#table-of-contents)

---

# 3. Spring Boot Configuration

Spring Boot reads configuration from many **property sources** (in increasing priority):

```
Default properties
  < @PropertySource files
  < application.properties / application.yml (inside the jar)
  < application-{profile}.properties
  < application.properties outside the jar (./config/)
  < OS environment variables (SERVER_PORT)
  < Java system properties (-Dserver.port=9090)
  < Command-line arguments (--server.port=9090)       ← highest
```

There are two ways to read them in code: **`@Value`** (one value at a time) and **`@ConfigurationProperties`** (a whole group bound to a typed object, recommended).

## 3.1 Type-safe Configuration Properties

### @ConfigurationProperties

**Package:** `org.springframework.boot.context.properties.ConfigurationProperties`
**Applies to:** Class, record, `@Bean` method

#### What it does
**Binds a group of properties** with a common prefix to a Java object, with type conversion, nested objects, lists, maps, durations and validation. Property names are matched with **relaxed binding**: `app.mail.max-retries`, `app.mail.maxRetries`, `APP_MAIL_MAXRETRIES` all bind to `maxRetries`.

The class must also be registered as a bean: via [@EnableConfigurationProperties](#enableconfigurationproperties), [@ConfigurationPropertiesScan](#configurationpropertiesscan), or `@Component`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` / `prefix` | `String` | `""` | The prefix of the properties to bind, e.g. `"app.mail"`. Must be kebab-case. |
| `ignoreInvalidFields` | `boolean` | `false` | `true` → ignore values that can't be converted (e.g. `port=abc`) instead of failing at startup. |
| `ignoreUnknownFields` | `boolean` | `true` | `false` → fail if the prefix contains properties that don't exist in the class (catches typos). |

#### Example: immutable record (recommended)

```yaml
# application.yml
app:
  mail:
    host: smtp.example.com
    port: 587
    from: no-reply@example.com
    timeout: 10s                 # Duration
    max-attachment-size: 5MB     # DataSize
    recipients:                  # List
      - admin@example.com
      - ops@example.com
    headers:                     # Map
      X-Env: prod
    retry:                       # nested object
      max-attempts: 3
      backoff: 2s
```

```java
@ConfigurationProperties(prefix = "app.mail")
@Validated
public record MailProperties(
        @NotBlank String host,
        @Min(1) @Max(65535) int port,
        @Email String from,
        @DefaultValue("5s") Duration timeout,
        DataSize maxAttachmentSize,
        List<String> recipients,
        Map<String, String> headers,
        @DefaultValue Retry retry) {          // empty Retry with its own defaults if not configured

    public record Retry(@DefaultValue("3") int maxAttempts,
                        @DefaultValue("1s") Duration backoff) { }
}

@SpringBootApplication
@ConfigurationPropertiesScan                  // registers MailProperties as a bean
public class Application { }

@Service
public class MailService {
    private final MailProperties props;
    public MailService(MailProperties props) { this.props = props; }

    public void send(String to) {
        System.out.println("Using " + props.host() + ":" + props.port()
                + " timeout=" + props.timeout().toSeconds() + "s");
    }
}
```

#### Example: JavaBean style (mutable, setters)

```java
@Component
@ConfigurationProperties(prefix = "app.storage")
public class StorageProperties {
    private String bucket;
    private Path localDir = Path.of("/tmp/uploads");    // default
    private final Limits limits = new Limits();         // nested object

    public static class Limits {
        private int maxFiles = 100;
        // getter/setter
    }
    // getters/setters (required for binding)
}
```

#### Example: on a @Bean method (configure third-party classes)

```java
@Bean
@ConfigurationProperties(prefix = "app.datasource.reporting")
public HikariDataSource reportingDataSource() {
    return new HikariDataSource();      // setters called with app.datasource.reporting.* values
}
```

IDE auto-completion for your own properties:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

> **Secrets** (DB passwords, API keys) shouldn't live in `application.yml`. With Spring Cloud AWS, `spring.config.import=aws-secretsmanager:<secret-name>` loads them as ordinary properties; see [AWS Secrets Manager → Spring Boot Integration](AWS_Service_guide.md#spring-boot-integration--sdk-examples).

#### @ConfigurationProperties vs @Value

| | `@ConfigurationProperties` | `@Value` |
|-|----------------------------|----------|
| Relaxed binding | ✅ | Limited |
| Groups / nested objects / lists / maps | ✅ | ❌ (awkward) |
| Validation (`@Validated`) | ✅ | ❌ |
| SpEL expressions | ❌ | ✅ |
| IDE metadata / auto-completion | ✅ | ❌ |
| Best for | Feature configuration | A single value, SpEL |

#### Common mistakes / best practices
- The class isn't registered as a bean → `No qualifying bean of type MailProperties`. Add `@ConfigurationPropertiesScan` or `@EnableConfigurationProperties`.
- JavaBean style needs **getters and setters**; records/constructor binding need neither.
- Add `@Validated` to fail fast at startup on bad configuration.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @EnableConfigurationProperties

**Package:** `org.springframework.boot.context.properties.EnableConfigurationProperties`
**Applies to:** Class (`@Configuration`)

#### What it does
Registers the listed `@ConfigurationProperties` classes as **beans**. Typical in auto-configurations and in modules where you want explicit registration instead of scanning.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `Class<?>[]` | `{}` | `@ConfigurationProperties` classes to register. |

#### Example

```java
@Configuration
@EnableConfigurationProperties({MailProperties.class, StorageProperties.class})
public class AppConfig {

    @Bean
    public MailClient mailClient(MailProperties props) {
        return new MailClient(props.host(), props.port());
    }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @ConfigurationPropertiesScan

**Package:** `org.springframework.boot.context.properties.ConfigurationPropertiesScan`
**Applies to:** Class (usually the main class)

#### What it does
**Scans packages** for classes annotated with `@ConfigurationProperties` and registers them as beans automatically, so you don't have to list them in `@EnableConfigurationProperties` or add `@Component`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` / `basePackages` | `String[]` | `{}` | Packages to scan. Default: the annotated class's package. |
| `basePackageClasses` | `Class<?>[]` | `{}` | Type-safe package selection. |

#### Example

```java
@SpringBootApplication
@ConfigurationPropertiesScan                               // scans com.example.shop.**
public class ShopApplication { }

@SpringBootApplication
@ConfigurationPropertiesScan({"com.example.shop.config", "com.example.common.props"})
public class ShopApplication { }
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @ConstructorBinding

**Package:** `org.springframework.boot.context.properties.bind.ConstructorBinding` (Spring Boot 3)
**Applies to:** Constructor

#### What it does
Tells Boot **which constructor** to use for binding a `@ConfigurationProperties` class. In Spring Boot 3, a class with a **single parameterized constructor** (and records) uses constructor binding **automatically**, so you only need `@ConstructorBinding` when the class has **several constructors**.

> In Spring Boot 2.x the annotation lived in `org.springframework.boot.context.properties` and was required on the class; that usage is removed in Boot 3.

#### Attributes
None.

#### Example

```java
@ConfigurationProperties("app.client")
public class ClientProperties {
    private final URI baseUrl;
    private final Duration timeout;

    @ConstructorBinding                               // use this one for binding
    public ClientProperties(URI baseUrl, @DefaultValue("30s") Duration timeout) {
        this.baseUrl = baseUrl;
        this.timeout = timeout;
    }

    public ClientProperties(String baseUrl) {         // convenience constructor for tests
        this(URI.create(baseUrl), Duration.ofSeconds(30));
    }
    // getters
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @DefaultValue

**Package:** `org.springframework.boot.context.properties.bind.DefaultValue`
**Applies to:** Constructor parameter / record component (constructor binding)

#### What it does
Provides a **default** for a constructor-bound property when it's not configured. With no value on a nested object parameter, it creates an **empty instance** (with its own defaults) instead of `null`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String[]` | `{}` | Default value(s), converted to the parameter type (`"10s"` → `Duration`, `"a,b"` → `List`). |

#### Example

```java
@ConfigurationProperties("app.cache")
public record CacheProperties(
        @DefaultValue("true") boolean enabled,
        @DefaultValue("10m") Duration ttl,
        @DefaultValue({"products", "categories"}) List<String> names,
        @DefaultValue Limits limits) {                 // never null

    public record Limits(@DefaultValue("1000") int maxEntries) { }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @NestedConfigurationProperty

**Package:** `org.springframework.boot.context.properties.NestedConfigurationProperty`
**Applies to:** Field, record component

#### What it does
Marks a field as a **nested property group** for the configuration **metadata** generator (IDE auto-completion). Needed when the nested type is **not an inner class** of the properties class; binding works without it, but the IDE won't know the nested keys.

#### Attributes
None.

#### Example

```java
// Defined in its own file (not nested)
public class Pool {
    private int maxSize = 10;
    private Duration idleTimeout = Duration.ofMinutes(5);
    // getters/setters
}

@ConfigurationProperties("app.db")
public class DbProperties {
    private String url;

    @NestedConfigurationProperty
    private Pool pool = new Pool();      // IDE now suggests app.db.pool.max-size, app.db.pool.idle-timeout
    // getters/setters
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @DurationUnit / @DataSizeUnit / @PeriodUnit

**Packages:** `org.springframework.boot.convert.DurationUnit`, `DataSizeUnit`, `PeriodUnit`
**Applies to:** Field, parameter, record component

#### What they do
Set the **unit used when a value has no suffix**. Without them, a plain number is read as **milliseconds** (`Duration`), **bytes** (`DataSize`) or **days** (`Period`).

#### Attributes

| Annotation | Attribute | Type | Description |
|------------|-----------|------|-------------|
| `@DurationUnit` | `value` | `java.time.temporal.ChronoUnit` | e.g. `ChronoUnit.SECONDS` |
| `@DataSizeUnit` | `value` | `org.springframework.util.unit.DataUnit` | e.g. `DataUnit.MEGABYTES` |
| `@PeriodUnit` | `value` | `java.time.temporal.ChronoUnit` | e.g. `ChronoUnit.MONTHS` |

#### Example

```properties
app.upload.timeout=30          # seconds (because of @DurationUnit)
app.upload.max-size=20         # megabytes (because of @DataSizeUnit)
app.upload.cleanup-after=500ms # suffixes always win: ns, us, ms, s, m, h, d / B, KB, MB, GB, TB
```

```java
@ConfigurationProperties("app.upload")
public record UploadProperties(
        @DurationUnit(ChronoUnit.SECONDS) Duration timeout,
        @DataSizeUnit(DataUnit.MEGABYTES) DataSize maxSize,
        Duration cleanupAfter) { }
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 3.2 Property Sources & Values

### @PropertySource

**Package:** `org.springframework.context.annotation.PropertySource`
**Applies to:** Class (`@Configuration`)

#### What it does
Adds an extra **properties file** to Spring's `Environment`, so its values are available to `@Value`, `@ConfigurationProperties` and `Environment.getProperty()`. `application.properties` is loaded automatically by Boot; use `@PropertySource` for **additional** files (e.g. a module's own defaults).

`@PropertySource` is **repeatable**: you can place several on one class.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String[]` | — (required) | Resource locations, e.g. `"classpath:mail.properties"`, `"file:/etc/app/override.properties"`. Supports `${...}` placeholders. |
| `name` | `String` | `""` | Name of the property source in the `Environment` (generated if empty). |
| `ignoreResourceNotFound` | `boolean` | `false` | `true` → don't fail if the file doesn't exist (optional files). |
| `encoding` | `String` | `""` | File encoding, e.g. `"UTF-8"`. |
| `factory` | `Class<? extends PropertySourceFactory>` | `PropertySourceFactory.class` | Factory used to parse the file. The default supports `.properties` and XML properties; **YAML needs a custom factory**. |

#### Example

```properties
# src/main/resources/mail.properties
mail.host=smtp.example.com
mail.port=587
```

```java
@Configuration
@PropertySource("classpath:mail.properties")
@PropertySource(value = "file:${user.home}/.app/override.properties", ignoreResourceNotFound = true)
public class MailConfig {

    @Value("${mail.host}")
    private String host;

    @Bean
    public MailClient mailClient(@Value("${mail.port}") int port) {
        return new MailClient(host, port);
    }
}
```

YAML with a custom factory:

```java
public class YamlPropertySourceFactory implements PropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        List<PropertySource<?>> sources = new YamlPropertySourceLoader()
                .load(resource.getResource().getFilename(), resource.getResource());
        return sources.get(0);
    }
}

@Configuration
@PropertySource(value = "classpath:features.yml", factory = YamlPropertySourceFactory.class)
public class FeatureConfig { }
```

#### Common mistakes / best practices
- `@PropertySource` files have **lower priority** than `application.properties`, so they can't override Boot's file. Use `spring.config.import=optional:file:./extra.properties` when you need higher priority.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @PropertySources

**Package:** `org.springframework.context.annotation.PropertySources`
**Applies to:** Class

#### What it does
The **container annotation** for several `@PropertySource` annotations. Since `@PropertySource` is repeatable (Java 8+), you can just repeat it; `@PropertySources` is the older, explicit form.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `PropertySource[]` | — (required) | The property sources. Later files override earlier ones for the same key. |

#### Example

```java
@Configuration
@PropertySources({
        @PropertySource("classpath:defaults.properties"),
        @PropertySource(value = "classpath:overrides.properties", ignoreResourceNotFound = true)
})
public class AppConfig { }
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Value

**Package:** `org.springframework.beans.factory.annotation.Value`
**Applies to:** Field, constructor/method parameter, `@Bean` method parameter

#### What it does
Injects a **single value** into a bean, from:
- **Property placeholders** `${property.name:default}` (from `application.properties`, environment variables, etc.)
- **SpEL expressions** `#{expression}` (computations, other beans, system properties)

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | — (required) | The expression: `"${...}"`, `"#{...}"`, or a literal. |

#### Syntax

| Expression | Result |
|------------|--------|
| `@Value("${server.port}")` | Property value; **startup fails** if missing |
| `@Value("${app.timeout:30}")` | Property, or `30` if missing |
| `@Value("${app.name:}")` | Property, or empty string |
| `@Value("${app.servers}")` with `a,b,c` | `String[]` / `List<String>` (comma split) |
| `@Value("#{'${app.ids}'.split(',')}")` | List via SpEL split |
| `@Value("#{${app.limits}}")` with `{gold:100,silver:50}` | `Map` via SpEL |
| `@Value("#{2 * 60 * 1000}")` | SpEL arithmetic → `120000` |
| `@Value("#{systemProperties['user.home']}")` | System property |
| `@Value("#{@pricingService.defaultRate}")` | Property of another bean |
| `@Value("classpath:templates/mail.html")` | Injected as a `Resource` |

#### Example

```java
@Service
public class PricingService {

    private final String currency;
    private final BigDecimal vatRate;
    private final List<String> allowedCountries;
    private final Duration cacheTtl;

    public PricingService(
            @Value("${app.pricing.currency:INR}") String currency,
            @Value("${app.pricing.vat-rate:0.18}") BigDecimal vatRate,
            @Value("${app.pricing.countries:IN,US}") List<String> allowedCountries,
            @Value("${app.pricing.cache-ttl:10m}") Duration cacheTtl) {
        this.currency = currency;
        this.vatRate = vatRate;
        this.allowedCountries = allowedCountries;
        this.cacheTtl = cacheTtl;
    }

    @Value("classpath:pricing/rules.json")
    private Resource rulesFile;

    @Value("#{T(java.lang.Math).max(1, ${app.pricing.workers:4})}")
    private int workers;
}
```

#### Common mistakes / best practices
- `@Value` on a **`static`** field doesn't work (stays `null`).
- `@Value` fields are `null` inside the **constructor**; use constructor parameters instead.
- `@Value` doesn't work in objects created with `new` (not Spring beans).
- For more than 2–3 related values, use [@ConfigurationProperties](#configurationproperties).
- Don't confuse with **Lombok's `@Value`** (immutable class), see [17](#17-lombok--commonly-used-with-spring-boot).

[⬆ Back to Table of Contents](#table-of-contents)

---

# 4. REST / Web MVC Annotations

All annotations in this part belong to **Spring Web MVC** (`spring-boot-starter-web`).

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<!-- Needed for @Valid / @Validated and constraint annotations -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

### How a request flows through Spring MVC

```
HTTP Request
   │
   ▼
DispatcherServlet ──► HandlerMapping (finds method via @RequestMapping / @GetMapping ...)
   │
   ▼
HandlerAdapter ──► HandlerMethodArgumentResolvers
   │                 (@PathVariable, @RequestParam, @RequestBody, @RequestHeader ...)
   ▼
Controller method executes
   │
   ▼
Return value handlers
   ├── @ResponseBody / @RestController ──► HttpMessageConverter (Jackson → JSON)
   └── @Controller (String view name)  ──► ViewResolver (Thymeleaf, JSP ...)
   │
   ▼  (if an exception is thrown)
@ExceptionHandler ──► @ControllerAdvice / @RestControllerAdvice
   │
   ▼
HTTP Response
```

### Sample domain used in the examples

```java
public class User {
    private Long id;
    private String name;
    private String email;
    private int age;
    // constructors, getters, setters
}
```

---

## 4.1 Request Mapping Annotations

### @RequestMapping

**Package:** `org.springframework.web.bind.annotation.RequestMapping`
**Applies to:** Class, Method

#### What it does
Maps HTTP requests to handler classes/methods. It is the **base annotation** for all request mapping; `@GetMapping`, `@PostMapping`, etc. are shortcuts built on top of it.

- **On a class** → defines a common base path (and other shared conditions) for all methods in it.
- **On a method** → defines the specific path/conditions. Class-level and method-level values are **combined**.

If `method` is not specified, the mapping matches **all HTTP methods**.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `name` | `String` | `""` | A name for the mapping. Used for building URLs from views (`MvcUriComponentsBuilder` / `mvcUrl('UC#getById')`). Rarely used. |
| `value` | `String[]` | `{}` | URL path pattern(s). Alias for `path`. Example: `"/users"`, `{"/users", "/members"}`. |
| `path` | `String[]` | `{}` | Same as `value`. Use one or the other. Supports patterns: `{id}`, `{id:\\d+}` (regex), `*`, `**`. |
| `method` | `RequestMethod[]` | `{}` (all) | HTTP methods to match: `GET`, `POST`, `PUT`, `PATCH`, `DELETE`, `HEAD`, `OPTIONS`, `TRACE`. |
| `params` | `String[]` | `{}` | Request parameter conditions. `"type"` (must be present), `"!type"` (must be absent), `"type=admin"` (must equal), `"type!=admin"` (must not equal). |
| `headers` | `String[]` | `{}` | Header conditions, same syntax as `params`: `"X-API-VERSION=1"`, `"!X-Debug"`. |
| `consumes` | `String[]` | `{}` | Media types the method accepts from the request `Content-Type`. Example: `MediaType.APPLICATION_JSON_VALUE`. Negation: `"!text/plain"`. Mismatch → **415 Unsupported Media Type**. |
| `produces` | `String[]` | `{}` | Media types the method can return, matched against the request `Accept` header. Mismatch → **406 Not Acceptable**. Also sets the response `Content-Type`. |

> **Class + method combination rules:**
> `path` is concatenated (`/api/users` + `/{id}` → `/api/users/{id}`); `method`, `params`, `headers` are combined (both must match); `consumes`/`produces` at method level **override** class level.

#### Example

```java
@RestController
@RequestMapping(
        path = "/api/users",
        produces = MediaType.APPLICATION_JSON_VALUE   // all methods return JSON
)
public class UserController {

    // GET /api/users
    @RequestMapping(method = RequestMethod.GET)
    public List<User> getAll() { ... }

    // GET /api/users/42   (only digits allowed via regex)
    @RequestMapping(path = "/{id:\\d+}", method = RequestMethod.GET)
    public User getById(@PathVariable Long id) { ... }

    // POST /api/users with Content-Type: application/json
    @RequestMapping(method = RequestMethod.POST,
                    consumes = MediaType.APPLICATION_JSON_VALUE)
    public User create(@RequestBody User user) { ... }

    // Matches both GET and HEAD on two paths
    @RequestMapping(path = {"/active", "/enabled"},
                    method = {RequestMethod.GET, RequestMethod.HEAD})
    public List<User> active() { ... }

    // params condition: GET /api/users/search?type=admin
    @RequestMapping(path = "/search", method = RequestMethod.GET, params = "type=admin")
    public List<User> searchAdmins() { ... }

    // params condition: GET /api/users/search  (no "type" param)
    @RequestMapping(path = "/search", method = RequestMethod.GET, params = "!type")
    public List<User> searchAll() { ... }

    // headers condition: API versioning by header
    @RequestMapping(path = "/{id}", method = RequestMethod.GET, headers = "X-API-VERSION=2")
    public UserV2 getByIdV2(@PathVariable Long id) { ... }

    // produces XML only for this method (overrides class-level JSON)
    @RequestMapping(path = "/{id}/xml", method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_XML_VALUE)
    public User getXml(@PathVariable Long id) { ... }
}
```

**Path pattern examples** (Spring Boot 3 uses `PathPatternParser` by default):

| Pattern | Matches | Doesn't match |
|---------|---------|---------------|
| `/users/{id}` | `/users/5` | `/users/5/orders` |
| `/users/{id:\\d+}` | `/users/5` | `/users/abc` |
| `/files/*` | `/files/a.txt` | `/files/a/b.txt` |
| `/files/**` | `/files/a/b/c.txt` | — |
| `/files/{*path}` | `/files/a/b/c.txt` (captures `/a/b/c.txt`) | — |

#### How it works internally
- `RequestMappingHandlerMapping` builds a `RequestMappingInfo` for every mapped method at startup.
- On each request, all conditions (path, method, params, headers, consumes, produces) are evaluated; the **most specific** match wins.
- Two methods with identical conditions → `IllegalStateException: Ambiguous mapping` at startup.

#### Common mistakes / best practices
- Prefer `@GetMapping`, `@PostMapping`, ... on methods — they are shorter and clearer. Use `@RequestMapping` at **class level** for the base path.
- A method-level `@RequestMapping` without `method` accepts **every** HTTP method — usually unintended.
- Trailing slash matching is **disabled** by default since Spring Boot 3 (`/users/` ≠ `/users`).

[⬆ Back to Table of Contents](#table-of-contents)

---

### @GetMapping

**Package:** `org.springframework.web.bind.annotation.GetMapping`
**Applies to:** Method

#### What it does
Shortcut for `@RequestMapping(method = RequestMethod.GET)`. Used to **read/fetch** resources. GET requests should be **safe** (no side effects) and **idempotent**.

A `@GetMapping` method also automatically handles **HEAD** requests (see [@HeadMapping](#headmapping-does-not-exist)).

#### Attributes
All attributes are aliases for the same-named attributes on `@RequestMapping` (there is no `method` attribute, it is fixed to GET).

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `name` | `String` | `""` | Name of the mapping. |
| `value` | `String[]` | `{}` | URL path(s). Alias for `path`. |
| `path` | `String[]` | `{}` | URL path(s). |
| `params` | `String[]` | `{}` | Required/forbidden request parameter conditions (`"page"`, `"!page"`, `"sort=asc"`). |
| `headers` | `String[]` | `{}` | Header conditions (`"X-API-VERSION=1"`). |
| `consumes` | `String[]` | `{}` | Accepted request `Content-Type` (rare for GET since GET usually has no body). |
| `produces` | `String[]` | `{}` | Response media type(s), matched against `Accept`. |

#### Example

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    // GET /api/users
    @GetMapping
    public List<User> getAll() {
        return userService.findAll();
    }

    // GET /api/users/10
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return userService.findOptionalById(id)
                .map(ResponseEntity::ok)                       // 200
                .orElse(ResponseEntity.notFound().build());    // 404
    }

    // GET /api/users/page?page=0&size=20
    @GetMapping(path = "/page", params = {"page", "size"})
    public Page<User> getPage(@RequestParam int page, @RequestParam int size) {
        return userService.findPage(PageRequest.of(page, size));
    }

    // GET /api/users/10/export with Accept: text/csv
    @GetMapping(path = "/{id}/export", produces = "text/csv")
    public String exportCsv(@PathVariable Long id) {
        User u = userService.findById(id);
        return "id,name,email\n" + u.getId() + "," + u.getName() + "," + u.getEmail();
    }

    // Header-based versioning
    @GetMapping(path = "/{id}", headers = "X-API-VERSION=2")
    public UserV2 getByIdV2(@PathVariable Long id) { ... }
}
```

#### Common mistakes / best practices
- Never change server state in a GET handler (caches, crawlers and prefetchers may call it).
- Don't send a request body with GET — many clients/proxies drop it. Use query params.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @PostMapping

**Package:** `org.springframework.web.bind.annotation.PostMapping`
**Applies to:** Method

#### What it does
Shortcut for `@RequestMapping(method = RequestMethod.POST)`. Used to **create** a new resource or trigger a processing action. POST is **not idempotent**: sending it twice usually creates two resources.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `name` | `String` | `""` | Name of the mapping. |
| `value` | `String[]` | `{}` | URL path(s). Alias for `path`. |
| `path` | `String[]` | `{}` | URL path(s). |
| `params` | `String[]` | `{}` | Request parameter conditions. |
| `headers` | `String[]` | `{}` | Header conditions. |
| `consumes` | `String[]` | `{}` | Accepted request `Content-Type`, e.g. `application/json`, `multipart/form-data`, `application/x-www-form-urlencoded`. |
| `produces` | `String[]` | `{}` | Response media type(s). |

#### Example

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    // POST /api/users   Content-Type: application/json
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> create(@Valid @RequestBody User user,
                                       UriComponentsBuilder uriBuilder) {
        User saved = userService.save(user);
        URI location = uriBuilder.path("/api/users/{id}")
                                 .buildAndExpand(saved.getId())
                                 .toUri();
        return ResponseEntity.created(location).body(saved);   // 201 + Location header
    }

    // POST /api/users/form   Content-Type: application/x-www-form-urlencoded
    @PostMapping(path = "/form", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public User createFromForm(@ModelAttribute User user) {
        return userService.save(user);
    }

    // POST /api/users/1/avatar   Content-Type: multipart/form-data
    @PostMapping(path = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadAvatar(@PathVariable Long id,
                               @RequestPart("file") MultipartFile file) {
        userService.storeAvatar(id, file);
        return "Uploaded " + file.getOriginalFilename();
    }

    // POST used for an action (not a resource creation)
    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable Long id) {
        userService.activate(id);
    }
}
```

Request:
```http
POST /api/users HTTP/1.1
Content-Type: application/json

{ "name": "Mahendra", "email": "m@example.com", "age": 30 }
```

#### Common mistakes / best practices
- Return **201 Created** with a `Location` header for resource creation.
- Missing/incorrect `Content-Type` header with `consumes` set → **415**.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @PutMapping

**Package:** `org.springframework.web.bind.annotation.PutMapping`
**Applies to:** Method

#### What it does
Shortcut for `@RequestMapping(method = RequestMethod.PUT)`. Used to **fully replace** a resource. The client sends the **complete** representation; missing fields are typically set to `null`/default. PUT is **idempotent**: sending the same request many times gives the same result.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `name` | `String` | `""` | Name of the mapping. |
| `value` | `String[]` | `{}` | URL path(s). Alias for `path`. |
| `path` | `String[]` | `{}` | URL path(s). |
| `params` | `String[]` | `{}` | Request parameter conditions. |
| `headers` | `String[]` | `{}` | Header conditions (e.g. `"If-Match"` for optimistic locking). |
| `consumes` | `String[]` | `{}` | Accepted request `Content-Type`. |
| `produces` | `String[]` | `{}` | Response media type(s). |

#### Example

```java
// PUT /api/users/1  -> replace the whole user
@PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<User> replace(@PathVariable Long id,
                                    @Valid @RequestBody User user) {
    if (!userService.exists(id)) {
        // Some APIs create the resource on PUT (upsert) and return 201 instead
        return ResponseEntity.notFound().build();
    }
    user.setId(id);                     // id comes from URL, not body
    return ResponseEntity.ok(userService.save(user));
}
```

Request:
```http
PUT /api/users/1 HTTP/1.1
Content-Type: application/json

{ "name": "Mahendra P", "email": "new@example.com", "age": 31 }
```

#### Common mistakes / best practices
- Don't use PUT for partial updates — use [@PatchMapping](#patchmapping).
- Take the ID from the path, not from the body, to avoid mismatch.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @PatchMapping

**Package:** `org.springframework.web.bind.annotation.PatchMapping`
**Applies to:** Method

#### What it does
Shortcut for `@RequestMapping(method = RequestMethod.PATCH)`. Used for **partial updates**: only the fields sent by the client are changed.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `name` | `String` | `""` | Name of the mapping. |
| `value` | `String[]` | `{}` | URL path(s). Alias for `path`. |
| `path` | `String[]` | `{}` | URL path(s). |
| `params` | `String[]` | `{}` | Request parameter conditions. |
| `headers` | `String[]` | `{}` | Header conditions. |
| `consumes` | `String[]` | `{}` | Accepted request `Content-Type`, e.g. `application/json`, `application/merge-patch+json`, `application/json-patch+json`. |
| `produces` | `String[]` | `{}` | Response media type(s). |

#### Example

```java
// PATCH /api/users/1   body: { "email": "changed@example.com" }
@PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
public User partialUpdate(@PathVariable Long id,
                          @RequestBody Map<String, Object> changes) {
    User user = userService.findById(id);

    changes.forEach((field, value) -> {
        switch (field) {
            case "name"  -> user.setName((String) value);
            case "email" -> user.setEmail((String) value);
            case "age"   -> user.setAge((Integer) value);
            default      -> throw new IllegalArgumentException("Unknown field: " + field);
        }
    });
    return userService.save(user);
}

// Alternative: a DTO with nullable fields (only non-null fields are applied)
public record UserPatch(String name, String email, Integer age) {}

@PatchMapping("/{id}/profile")
public User patchProfile(@PathVariable Long id, @RequestBody UserPatch patch) {
    User user = userService.findById(id);
    if (patch.name()  != null) user.setName(patch.name());
    if (patch.email() != null) user.setEmail(patch.email());
    if (patch.age()   != null) user.setAge(patch.age());
    return userService.save(user);
}
```

#### Common mistakes / best practices
- Using primitive types (`int`) in the patch DTO means you can't tell "not sent" from `0`. Use wrappers (`Integer`).
- `@Valid` on a patch DTO with `@NotNull` constraints will reject partial bodies — use validation groups ([@Validated](#validated)) or separate DTOs.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @DeleteMapping

**Package:** `org.springframework.web.bind.annotation.DeleteMapping`
**Applies to:** Method

#### What it does
Shortcut for `@RequestMapping(method = RequestMethod.DELETE)`. Used to **delete** a resource. DELETE is **idempotent**: deleting an already-deleted resource should not cause a different side effect (usually returns 204 or 404).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `name` | `String` | `""` | Name of the mapping. |
| `value` | `String[]` | `{}` | URL path(s). Alias for `path`. |
| `path` | `String[]` | `{}` | URL path(s). |
| `params` | `String[]` | `{}` | Request parameter conditions (e.g. `"force=true"`). |
| `headers` | `String[]` | `{}` | Header conditions. |
| `consumes` | `String[]` | `{}` | Accepted request `Content-Type` (rare, DELETE usually has no body). |
| `produces` | `String[]` | `{}` | Response media type(s). |

#### Example

```java
// DELETE /api/users/1  -> 204 No Content
@DeleteMapping("/{id}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void delete(@PathVariable Long id) {
    userService.delete(id);
}

// DELETE /api/users/1?force=true -> hard delete (only when force=true)
@DeleteMapping(path = "/{id}", params = "force=true")
public ResponseEntity<Void> hardDelete(@PathVariable Long id) {
    userService.hardDelete(id);
    return ResponseEntity.noContent().build();
}

// DELETE /api/users?ids=1,2,3  -> bulk delete
@DeleteMapping
public ResponseEntity<Map<String, Integer>> deleteMany(@RequestParam List<Long> ids) {
    int count = userService.deleteAll(ids);
    return ResponseEntity.ok(Map.of("deleted", count));
}
```

#### Common mistakes / best practices
- Return **204 No Content** (no body) or **200 OK** (with body), not 201.
- Avoid relying on a request body for DELETE; use path/query parameters.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @HeadMapping (does not exist)

> ⚠️ **There is no `@HeadMapping` annotation in Spring.** Using it gives a compile error: `cannot find symbol`.

#### What HEAD is
An HTTP `HEAD` request is the same as `GET` but the server returns **only the headers** (status, `Content-Type`, `Content-Length`, `ETag`...), **no body**. Clients use it to check whether a resource exists or has changed without downloading it.

#### How to handle HEAD in Spring

**1. Automatic (recommended):** every `@GetMapping` method also handles HEAD. Spring runs the GET method, calculates the headers (including `Content-Length`), and discards the body.

```java
@GetMapping("/{id}")
public User getById(@PathVariable Long id) { ... }
// HEAD /api/users/1  -> 200 OK with headers only, no body
```

**2. Explicit HEAD handler** (when you want cheaper logic than the full GET):

```java
@RequestMapping(path = "/{id}", method = RequestMethod.HEAD)
public ResponseEntity<Void> exists(@PathVariable Long id) {
    return userService.exists(id)
            ? ResponseEntity.ok().header("X-User-Exists", "true").build()
            : ResponseEntity.notFound().build();
}
```

An explicit `HEAD` mapping takes priority over the implicit HEAD handling of a `@GetMapping` on the same path.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @OptionsMapping (does not exist)

> ⚠️ **There is no `@OptionsMapping` annotation in Spring.** Using it gives a compile error.

#### What OPTIONS is
An HTTP `OPTIONS` request asks the server **which HTTP methods are allowed** on a URL. The answer is returned in the `Allow` header. Browsers also send OPTIONS as a **CORS preflight** request before cross-origin calls.

#### How to handle OPTIONS in Spring

**1. Automatic (recommended):** Spring answers OPTIONS for any mapped path, setting the `Allow` header from the methods declared on that path.

```http
OPTIONS /api/users/1 HTTP/1.1

HTTP/1.1 200 OK
Allow: GET,HEAD,PUT,PATCH,DELETE,OPTIONS
```

**2. Explicit OPTIONS handler:**

```java
@RequestMapping(path = "/{id}", method = RequestMethod.OPTIONS)
public ResponseEntity<Void> options() {
    return ResponseEntity.ok()
            .allow(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.OPTIONS)
            .build();
}
```

**3. CORS preflight:** don't write OPTIONS handlers for CORS. Use [@CrossOrigin](#crossorigin) or a global `WebMvcConfigurer.addCorsMappings()`; Spring handles the preflight automatically.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 4.2 Request Data Binding Annotations

These annotations go on **controller method parameters** and tell Spring where to get each value from in the HTTP request.

```
POST /api/users/42;role=admin?notify=true      ← path, matrix variable, query param
Header: X-Tenant-Id: acme                      ← header
Cookie: SESSION_LANG=en                        ← cookie
Body:   { "name": "Mahendra" }                 ← body

@PathVariable("id")           -> 42
@MatrixVariable("role")       -> admin
@RequestParam("notify")       -> true
@RequestHeader("X-Tenant-Id") -> acme
@CookieValue("SESSION_LANG")  -> en
@RequestBody User             -> User{name=Mahendra}
```

### @PathVariable

**Package:** `org.springframework.web.bind.annotation.PathVariable`
**Applies to:** Method parameter

#### What it does
Binds a **URI template variable** (the `{...}` part of the path) to a method parameter. The string value is automatically converted to the parameter type (`Long`, `UUID`, `enum`, `LocalDate`...).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Name of the path variable. Alias for `name`. If omitted, the **parameter name** is used (requires compiling with `-parameters`; Spring Boot's Maven/Gradle plugins enable this by default). |
| `name` | `String` | `""` | Same as `value`. |
| `required` | `boolean` | `true` | Whether the variable must be present. Set `false` when the same method is mapped to paths with and without the variable. A missing required variable → `MissingPathVariableException` (500). |

#### Example

```java
@RestController
@RequestMapping("/api")
public class OrderController {

    // GET /api/users/5  -> id = 5
    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) { ... }

    // Explicit name when parameter name differs
    // GET /api/users/5/orders/99
    @GetMapping("/users/{userId}/orders/{orderId}")
    public Order getOrder(@PathVariable("userId") Long uid,
                          @PathVariable(name = "orderId") Long oid) { ... }

    // Regex constraint
    @GetMapping("/products/{code:[A-Z]{3}-\\d{4}}")   // e.g. ABC-1234
    public Product byCode(@PathVariable String code) { ... }

    // Optional variable: method mapped to two paths
    // GET /api/reports        -> year = null
    // GET /api/reports/2026   -> year = 2026
    @GetMapping({"/reports", "/reports/{year}"})
    public List<Report> reports(@PathVariable(required = false) Integer year) { ... }

    // Using Optional instead of required=false
    @GetMapping({"/logs", "/logs/{level}"})
    public List<Log> logs(@PathVariable Optional<String> level) { ... }

    // All path variables in a Map
    @GetMapping("/users/{userId}/orders/{orderId}/items/{itemId}")
    public String all(@PathVariable Map<String, String> vars) {
        return vars.toString();   // {userId=1, orderId=2, itemId=3}
    }

    // Enum conversion: GET /api/orders/status/SHIPPED
    @GetMapping("/orders/status/{status}")
    public List<Order> byStatus(@PathVariable OrderStatus status) { ... }

    // Capture the rest of the path: GET /api/files/docs/2026/report.pdf -> "/docs/2026/report.pdf"
    @GetMapping("/files/{*path}")
    public String file(@PathVariable String path) { return path; }
}
```

#### Common mistakes / best practices
- Type mismatch (`/users/abc` for a `Long`) → `MethodArgumentTypeMismatchException` → **400**. Handle it in an [@ExceptionHandler](#exceptionhandler).
- Spring Boot 3 matches path variables containing a `.` (like `/files/report.pdf`) in full, because suffix pattern matching was removed.
- Use path variables to **identify a resource**; use [@RequestParam](#requestparam) for filtering/sorting/paging.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @RequestParam

**Package:** `org.springframework.web.bind.annotation.RequestParam`
**Applies to:** Method parameter

#### What it does
Binds a **query string parameter** (`?name=value`), a **form field** (`application/x-www-form-urlencoded`) or a **multipart part** to a method parameter, with automatic type conversion.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Name of the request parameter. Alias for `name`. Defaults to the parameter name. |
| `name` | `String` | `""` | Same as `value`. |
| `required` | `boolean` | `true` | Whether the parameter is mandatory. Missing required param → `MissingServletRequestParameterException` → **400**. |
| `defaultValue` | `String` | none | Value used when the parameter is missing **or empty**. Setting it implicitly makes `required = false`. Always written as a `String`; Spring converts it to the target type. |

#### Example

```java
@RestController
@RequestMapping("/api/users")
public class UserSearchController {

    // GET /api/users/search?name=ma
    @GetMapping("/search")
    public List<User> search(@RequestParam String name) { ... }

    // Explicit name + optional
    // GET /api/users/filter            -> city = null
    // GET /api/users/filter?city=Pune  -> city = "Pune"
    @GetMapping("/filter")
    public List<User> filter(@RequestParam(name = "city", required = false) String city) { ... }

    // Default values for paging
    // GET /api/users/list               -> page=0, size=20, sort="name"
    // GET /api/users/list?page=2&size=5 -> page=2, size=5, sort="name"
    @GetMapping("/list")
    public List<User> list(@RequestParam(defaultValue = "0")    int page,
                           @RequestParam(defaultValue = "20")   int size,
                           @RequestParam(defaultValue = "name") String sort) { ... }

    // Optional<T>
    @GetMapping("/by-age")
    public List<User> byAge(@RequestParam Optional<Integer> minAge) {
        return userService.findByMinAge(minAge.orElse(18));
    }

    // Multiple values -> List
    // GET /api/users/by-ids?ids=1&ids=2&ids=3   or   ?ids=1,2,3
    @GetMapping("/by-ids")
    public List<User> byIds(@RequestParam List<Long> ids) { ... }

    // Date conversion
    // GET /api/users/joined?from=2026-01-01
    @GetMapping("/joined")
    public List<User> joined(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                             LocalDate from) { ... }

    // All params as a Map (first value of each)
    @GetMapping("/dynamic")
    public Map<String, String> dynamic(@RequestParam Map<String, String> params) {
        return params;
    }

    // All params, keeping multiple values
    @GetMapping("/dynamic-multi")
    public MultiValueMap<String, String> dynamicMulti(@RequestParam MultiValueMap<String, String> params) {
        return params;
    }

    // Form fields (application/x-www-form-urlencoded)
    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String login(@RequestParam String username, @RequestParam String password) { ... }

    // File upload as a request param (multipart/form-data)
    @PostMapping(path = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String importCsv(@RequestParam("file") MultipartFile file) { ... }
}
```

#### @RequestParam vs @PathVariable

| | `@RequestParam` | `@PathVariable` |
|-|-----------------|-----------------|
| Source | Query string / form data | URL path segment |
| URL | `/users?id=5` | `/users/5` |
| Typical use | Filter, sort, page, search | Identify a resource |
| Default value | Supported (`defaultValue`) | Not supported |

#### Common mistakes / best practices
- `defaultValue` also applies to empty values: `?page=` → uses the default.
- For many filter params, bind them to one object with [@ModelAttribute](#modelattribute) instead of a long parameter list.
- `int` with `required = false` and no default → error when missing (`null` can't go into a primitive). Use `Integer` or `defaultValue`.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @RequestBody

**Package:** `org.springframework.web.bind.annotation.RequestBody`
**Applies to:** Method parameter

#### What it does
Reads the **HTTP request body** and deserializes it into a Java object using an `HttpMessageConverter` chosen by the request `Content-Type` (Jackson for `application/json`). Usually combined with [@Valid](#valid) for validation.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `required` | `boolean` | `true` | Whether a body is required. Empty body with `required = true` → `HttpMessageNotReadableException` → **400**. With `false`, an empty body gives `null`. |

#### Example

```java
// DTO with validation constraints
public record CreateUserRequest(
        @NotBlank String name,
        @Email @NotBlank String email,
        @Min(18) @Max(120) int age,
        List<@NotBlank String> roles
) {}

@RestController
@RequestMapping("/api/users")
public class UserController {

    // JSON -> record, validated
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    // List of objects: [ {...}, {...} ]
    @PostMapping("/bulk")
    public List<User> createBulk(@RequestBody List<@Valid CreateUserRequest> requests) { ... }

    // Generic Map for dynamic JSON
    @PostMapping("/raw")
    public Map<String, Object> raw(@RequestBody Map<String, Object> json) { return json; }

    // Raw String body (StringHttpMessageConverter)
    @PostMapping(path = "/text", consumes = MediaType.TEXT_PLAIN_VALUE)
    public String text(@RequestBody String body) { return body.toUpperCase(); }

    // Optional body
    @PostMapping("/preferences")
    public String prefs(@RequestBody(required = false) Preferences prefs) {
        return prefs == null ? "using defaults" : "saved";
    }

    // Headers + body together
    @PostMapping("/entity")
    public String entity(HttpEntity<CreateUserRequest> entity) {
        return entity.getHeaders().getContentType() + " / " + entity.getBody();
    }
}
```

Request:
```http
POST /api/users HTTP/1.1
Content-Type: application/json

{ "name": "Mahendra", "email": "m@example.com", "age": 30, "roles": ["ADMIN"] }
```

#### How it works internally
`RequestResponseBodyMethodProcessor` → picks an `HttpMessageConverter` whose `canRead(type, contentType)` is true → Jackson `ObjectMapper.readValue(...)` → if `@Valid`/`@Validated` is present, runs Bean Validation → failure throws `MethodArgumentNotValidException` (**400**).

#### Common mistakes / best practices
- Only **one** `@RequestBody` per method (the body stream can be read only once).
- Wrong/missing `Content-Type` → **415 Unsupported Media Type**.
- Malformed JSON → `HttpMessageNotReadableException` → **400**.
- Don't use `@RequestBody` for form submissions (`x-www-form-urlencoded`). Use [@ModelAttribute](#modelattribute) or [@RequestParam](#requestparam).
- Use dedicated request DTOs instead of entities. This prevents clients from setting fields like `id` or `role` that they shouldn't control (mass assignment).

[⬆ Back to Table of Contents](#table-of-contents)

---

### @RequestHeader

**Package:** `org.springframework.web.bind.annotation.RequestHeader`
**Applies to:** Method parameter

#### What it does
Binds an **HTTP request header** value to a method parameter. Header names are **case-insensitive**. Comma-separated values can be bound to arrays/lists.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Header name. Alias for `name`. Defaults to the parameter name. |
| `name` | `String` | `""` | Same as `value`. |
| `required` | `boolean` | `true` | Whether the header is mandatory. Missing → `MissingRequestHeaderException` → **400**. |
| `defaultValue` | `String` | none | Fallback value when the header is missing or empty. Implicitly sets `required = false`. |

#### Example

```java
@RestController
@RequestMapping("/api")
public class HeaderController {

    // Single header
    @GetMapping("/whoami")
    public String userAgent(@RequestHeader("User-Agent") String userAgent) {
        return userAgent;
    }

    // Multi-tenant API: required custom header
    @GetMapping("/orders")
    public List<Order> orders(@RequestHeader("X-Tenant-Id") String tenantId) {
        return orderService.findByTenant(tenantId);
    }

    // Optional header with default
    @GetMapping("/greeting")
    public String greet(@RequestHeader(name = "Accept-Language", defaultValue = "en") String lang) {
        return lang.startsWith("hi") ? "Namaste" : "Hello";
    }

    // Optional header without default
    @GetMapping("/trace")
    public String trace(@RequestHeader(value = "X-Request-Id", required = false) String requestId) {
        return requestId != null ? requestId : UUID.randomUUID().toString();
    }

    // Authorization token
    @GetMapping("/profile")
    public String profile(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        String token = auth.replace("Bearer ", "");
        return "token=" + token;
    }

    // Comma-separated header -> List   (Accept: application/json, text/plain)
    @GetMapping("/accept")
    public List<String> accept(@RequestHeader("Accept") List<String> accept) { return accept; }

    // All headers
    @GetMapping("/headers")
    public Map<String, String> all(@RequestHeader Map<String, String> headers) { return headers; }

    // All headers, typed
    @GetMapping("/headers-typed")
    public String typed(@RequestHeader HttpHeaders headers) {
        return "Content-Length=" + headers.getContentLength();
    }
}
```

#### Common mistakes / best practices
- For auth headers, prefer Spring Security filters over reading `Authorization` manually in every controller.
- Headers needed on every request (tenant ID, correlation ID) are better handled once in a `HandlerInterceptor` or filter.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @RequestPart

**Package:** `org.springframework.web.bind.annotation.RequestPart`
**Applies to:** Method parameter

#### What it does
Binds a **part of a `multipart/form-data` request** to a method parameter. Unlike [@RequestParam](#requestparam), each part is converted by an **`HttpMessageConverter` chosen from that part's own `Content-Type`**. This lets one request carry a file **and** a JSON object.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Name of the multipart part. Alias for `name`. Defaults to the parameter name. |
| `name` | `String` | `""` | Same as `value`. |
| `required` | `boolean` | `true` | Whether the part is mandatory. Missing → `MissingServletRequestPartException` → **400**. |

#### Example

```java
public record DocumentMetadata(@NotBlank String title, String description, List<String> tags) {}

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    // Single file
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String upload(@RequestPart("file") MultipartFile file) throws IOException {
        Path target = Path.of("uploads", UUID.randomUUID() + ".bin");
        file.transferTo(target);
        return "Saved " + file.getSize() + " bytes";
    }

    // File + JSON metadata in one request
    @PostMapping(path = "/with-meta", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadWithMeta(@RequestPart("file") MultipartFile file,
                                 @Valid @RequestPart("meta") DocumentMetadata meta) {
        return meta.title() + " -> " + file.getOriginalFilename();
    }

    // Multiple files + optional thumbnail
    @PostMapping(path = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public int uploadMany(@RequestPart("files") List<MultipartFile> files,
                          @RequestPart(value = "thumbnail", required = false) MultipartFile thumb) {
        return files.size();
    }
}
```

Calling it with curl (note the content type on the JSON part):

```bash
curl -X POST http://localhost:8080/api/documents/with-meta \
  -F "file=@report.pdf" \
  -F 'meta={"title":"Q3 Report","tags":["finance"]};type=application/json'
```

Upload size limits (`application.properties`):

```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=20MB
```

#### @RequestPart vs @RequestParam for multipart

| | `@RequestPart` | `@RequestParam` |
|-|----------------|-----------------|
| `MultipartFile` | ✅ | ✅ |
| JSON object part | ✅ (via `HttpMessageConverter`) | ❌ (simple type conversion only) |
| Uses the part's `Content-Type` | ✅ | ❌ |

#### Common mistakes / best practices
- Forgetting `;type=application/json` on the JSON part → **415**, because the part defaults to `application/octet-stream`.
- Exceeding size limits throws `MaxUploadSizeExceededException`; handle it in a [@ControllerAdvice](#controlleradvice).
- Never use `getOriginalFilename()` directly as a file path (path traversal risk). Sanitize it or generate your own name.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @RequestAttribute

**Package:** `org.springframework.web.bind.annotation.RequestAttribute`
**Applies to:** Method parameter

#### What it does
Binds a **server-side request attribute** (`HttpServletRequest.getAttribute(name)`) to a method parameter. The client does **not** send request attributes. They are set earlier in the same request by a **Filter**, a **HandlerInterceptor**, or a controller that forwarded the request.

Typical use: a filter authenticates the user or resolves the tenant and stores it as an attribute, then the controller reads it.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Attribute name. Alias for `name`. Defaults to the parameter name. |
| `name` | `String` | `""` | Same as `value`. |
| `required` | `boolean` | `true` | Whether the attribute must exist. Missing → `ServletRequestBindingException` → **400**. |

#### Example

```java
// 1. An interceptor sets the attributes
@Component
public class TenantInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenant = request.getHeader("X-Tenant-Id");
        request.setAttribute("tenantId", tenant != null ? tenant : "default");
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }
}

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final TenantInterceptor tenantInterceptor;

    public WebConfig(TenantInterceptor tenantInterceptor) {
        this.tenantInterceptor = tenantInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantInterceptor).addPathPatterns("/api/**");
    }
}

// 2. The controller reads them
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @GetMapping
    public List<Order> list(@RequestAttribute("tenantId") String tenantId,
                            @RequestAttribute(name = "startTime") Long startTime) {
        return orderService.findByTenant(tenantId);
    }

    // Optional attribute
    @GetMapping("/audit")
    public String audit(@RequestAttribute(value = "auditUser", required = false) String user) {
        return user == null ? "anonymous" : user;
    }
}
```

#### Common mistakes / best practices
- Don't confuse it with [@RequestParam](#requestparam) (query params sent by the client) or [@SessionAttribute](#sessionattribute) (lives across requests).
- Attribute names are plain strings. Define them as constants to avoid typos.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @CookieValue

**Package:** `org.springframework.web.bind.annotation.CookieValue`
**Applies to:** Method parameter

#### What it does
Binds the value of an **HTTP cookie** sent by the client (in the `Cookie` header) to a method parameter, with type conversion. You can also bind to `jakarta.servlet.http.Cookie` to get the full cookie object.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Cookie name. Alias for `name`. Defaults to the parameter name. |
| `name` | `String` | `""` | Same as `value`. |
| `required` | `boolean` | `true` | Whether the cookie must be present. Missing → `MissingRequestCookieException` → **400**. |
| `defaultValue` | `String` | none | Value used when the cookie is missing or empty. Implicitly sets `required = false`. |

#### Example

```java
@RestController
@RequestMapping("/api/prefs")
public class PreferenceController {

    // Read a cookie with a default
    @GetMapping("/theme")
    public String theme(@CookieValue(name = "theme", defaultValue = "light") String theme) {
        return "Current theme: " + theme;
    }

    // Optional cookie
    @GetMapping("/lang")
    public String lang(@CookieValue(value = "lang", required = false) String lang) {
        return lang == null ? "en" : lang;
    }

    // Full Cookie object
    @GetMapping("/session")
    public String session(@CookieValue("JSESSIONID") Cookie cookie) {
        return cookie.getName() + "=" + cookie.getValue();
    }

    // Writing a cookie (response side) - use ResponseCookie
    @PostMapping("/theme")
    public ResponseEntity<String> setTheme(@RequestParam String theme) {
        ResponseCookie cookie = ResponseCookie.from("theme", theme)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(30))
                .sameSite("Lax")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Theme saved");
    }
}
```

#### Common mistakes / best practices
- `@CookieValue` only **reads** cookies. To set one, add a `Set-Cookie` header (`ResponseCookie`) or call `HttpServletResponse.addCookie()`.
- Never store sensitive data in plain cookies. Mark auth cookies `HttpOnly`, `Secure` and `SameSite`.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @MatrixVariable

**Package:** `org.springframework.web.bind.annotation.MatrixVariable`
**Applies to:** Method parameter

#### What it does
Binds **matrix variables**: name-value pairs placed inside a **path segment** after a semicolon (`;`), as defined by RFC 3986.

```
/cars;color=red;year=2024
/owners/42;q=11/pets/21;q=22
/products;tags=new,sale        (multiple values: comma-separated or repeated)
```

Matrix variables are rare in public APIs. They are useful when a filter applies to one specific segment of a nested path.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Name of the matrix variable. Alias for `name`. Defaults to the parameter name. |
| `name` | `String` | `""` | Same as `value`. |
| `pathVar` | `String` | `ValueConstants.DEFAULT_NONE` | Name of the **URI path variable** the matrix variable belongs to. Needed when the same matrix variable name appears in more than one path segment. |
| `required` | `boolean` | `true` | Whether the matrix variable is mandatory. Missing → `MissingMatrixVariableException` → **400**. |
| `defaultValue` | `String` | none | Fallback value when missing. Implicitly sets `required = false`. |

#### Enabling matrix variables
- **Spring Boot 2.6+ / 3.x** (default `PathPatternParser`): matrix variables work out of the box.
- If your app still uses the legacy `AntPathMatcher`/`UrlPathHelper`, semicolon content is stripped by default. Enable it like this:

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        UrlPathHelper helper = new UrlPathHelper();
        helper.setRemoveSemicolonContent(false);
        configurer.setUrlPathHelper(helper);
    }
}
```

#### Example

```java
@RestController
@RequestMapping("/api")
public class MatrixController {

    // GET /api/cars/list;color=red;year=2024
    @GetMapping("/cars/{segment}")
    public String cars(@PathVariable String segment,     // "list"
                       @MatrixVariable String color,     // "red"
                       @MatrixVariable int year) {       // 2024
        return color + " " + year;
    }

    // Default value + multiple values
    // GET /api/products/all;tags=new,sale
    @GetMapping("/products/{segment}")
    public List<String> products(@PathVariable String segment,
                                 @MatrixVariable(defaultValue = "all") List<String> tags) {
        return tags;   // [new, sale]
    }

    // Same name in two segments -> use pathVar
    // GET /api/owners/42;q=11/pets/21;q=22
    @GetMapping("/owners/{ownerId}/pets/{petId}")
    public String ownerPet(@MatrixVariable(name = "q", pathVar = "ownerId") int ownerQ,  // 11
                           @MatrixVariable(name = "q", pathVar = "petId")   int petQ) {  // 22
        return ownerQ + " / " + petQ;
    }

    // All matrix variables
    // GET /api/employees/list;dept=IT;level=senior
    @GetMapping("/employees/{segment}")
    public Map<String, String> all(@MatrixVariable Map<String, String> matrixVars) {
        return matrixVars;   // {dept=IT, level=senior}
    }

    // All matrix variables of one segment
    @GetMapping("/teams/{teamId}/members/{memberId}")
    public MultiValueMap<String, String> teamVars(
            @MatrixVariable(pathVar = "teamId") MultiValueMap<String, String> vars) {
        return vars;
    }
}
```

#### Common mistakes / best practices
- The segment that carries matrix variables **must be a path variable** (`{segment}`) in the mapping. A literal segment won't match.
- Prefer [@RequestParam](#requestparam) for normal filtering; most API consumers are unfamiliar with matrix variables.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @ModelAttribute

**Package:** `org.springframework.web.bind.annotation.ModelAttribute`
**Applies to:** Method parameter, Method

#### What it does
`@ModelAttribute` has **two uses**:

1. **On a method parameter**: creates (or retrieves) an object, **binds request parameters** (query params, form fields, path variables) to its fields by name, and adds it to the `Model`. Ideal for HTML forms and for grouping many query params into one object.
2. **On a method**: the method runs **before every handler method** in the controller (or in all controllers when declared in a [@ControllerAdvice](#controlleradvice)), and its return value is added to the `Model`. Used to provide common data (dropdown lists, current user...) to views.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Name of the model attribute. Alias for `name`. If empty, it is derived from the type: `User` → `"user"`, `List<User>` → `"userList"`. |
| `name` | `String` | `""` | Same as `value`. |
| `binding` | `boolean` | `true` | Whether request parameters are bound to the object. Set `false` to use an existing model object **without** overwriting its fields from the request (e.g. to protect an object loaded from the DB). Only meaningful on parameters. |

#### Example 1: Parameter binding (form / query params)

```java
// Search criteria object
public class UserSearchCriteria {
    private String name;
    private String city;
    private Integer minAge;
    private int page = 0;
    private int size = 20;
    // getters & setters (required for binding)
}

@RestController
@RequestMapping("/api/users")
public class UserController {

    // GET /api/users/search?name=ma&city=Pune&minAge=25&page=1
    // All query params are bound into one object
    @GetMapping("/search")
    public List<User> search(@ModelAttribute UserSearchCriteria criteria) {
        return userService.search(criteria);
    }

    // @ModelAttribute is implied for non-simple types: this works the same way
    @GetMapping("/search2")
    public List<User> search2(UserSearchCriteria criteria) { ... }

    // Form submit: application/x-www-form-urlencoded  name=Mahendra&email=m@x.com&age=30
    @PostMapping(path = "/form", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public User submit(@Valid @ModelAttribute("user") User user, BindingResult result) {
        if (result.hasErrors()) {
            throw new IllegalArgumentException(result.getAllErrors().toString());
        }
        return userService.save(user);
    }
}
```

#### Example 2: Method-level (common model data for MVC views)

```java
@Controller
@RequestMapping("/users")
public class UserFormController {

    // Runs before every handler in this controller and adds "countries" to the Model
    @ModelAttribute("countries")
    public List<String> countries() {
        return List.of("India", "USA", "Germany");
    }

    // void variant: add several attributes manually
    @ModelAttribute
    public void commonAttributes(Model model) {
        model.addAttribute("appVersion", "1.0.0");
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("user", new User());
        return "users/form";            // template can use ${countries}, ${appVersion}
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("user") User user, BindingResult result) {
        if (result.hasErrors()) {
            return "users/form";        // re-render the form with error messages
        }
        userService.save(user);
        return "redirect:/users";
    }
}
```

#### Example 3: `binding = false`

```java
@ModelAttribute("account")
public Account loadAccount(@PathVariable Long id) {
    return accountRepository.findById(id).orElseThrow();
}

// Uses the account already in the model; request params will NOT overwrite its fields
@PostMapping("/accounts/{id}/close")
public String close(@ModelAttribute(name = "account", binding = false) Account account) {
    accountService.close(account);
    return "redirect:/accounts";
}
```

#### How it works internally
`ModelAttributeMethodProcessor`: looks up the attribute in the model (or the session via [@SessionAttributes](#sessionattributes)) → otherwise creates it (default constructor, or constructor binding for records) → `WebDataBinder` binds request params → validates if `@Valid` is present → stores it in the `Model`.

#### @ModelAttribute vs @RequestBody

| | `@ModelAttribute` | `@RequestBody` |
|-|-------------------|----------------|
| Source | Query params / form fields / path vars | Request body (JSON/XML) |
| Content-Type | `x-www-form-urlencoded`, `multipart/form-data`, GET query | `application/json`, `application/xml` |
| Mechanism | `WebDataBinder` (setters / constructor) | `HttpMessageConverter` (Jackson) |
| Validation error | `MethodArgumentNotValidException` (a `BindException`) | `MethodArgumentNotValidException` |

#### Common mistakes / best practices
- Sending JSON to a `@ModelAttribute` parameter leaves the fields `null`. Use `@RequestBody` for JSON.
- `BindingResult` must come **immediately after** the `@ModelAttribute` parameter; otherwise binding errors throw an exception instead.
- **Mass assignment risk:** binding sets any field whose name matches a request param. Restrict it with `@InitBinder` + `setAllowedFields(...)` or use dedicated DTOs (see [@InitBinder](#initbinder)).

[⬆ Back to Table of Contents](#table-of-contents)

---

## 4.3 Response Annotations

### @ResponseBody

**Package:** `org.springframework.web.bind.annotation.ResponseBody`
**Applies to:** Class, Method

#### What it does
Tells Spring to write the method's **return value directly into the HTTP response body**, serialized by an `HttpMessageConverter` (JSON via Jackson by default). Without it, a `@Controller` method's return value is treated as a view name.

- **On a method** → applies to that method only.
- **On a class** → applies to all methods in that class. `@RestController` already includes it, so you never need `@ResponseBody` inside a `@RestController`.

#### Attributes
`@ResponseBody` has **no attributes**.

#### Example

```java
@Controller
@RequestMapping("/products")
public class ProductController {

    // View: renders templates/products/list.html
    @GetMapping
    public String page(Model model) {
        model.addAttribute("products", productService.findAll());
        return "products/list";
    }

    // JSON: same controller, but returns data
    @GetMapping("/api")
    @ResponseBody
    public List<Product> json() {
        return productService.findAll();
    }

    // Plain text body (StringHttpMessageConverter) -> "OK"
    @GetMapping("/health")
    @ResponseBody
    public String health() {
        return "OK";
    }

    // Binary body: byte[] -> ByteArrayHttpMessageConverter
    @GetMapping(path = "/{id}/image", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] image(@PathVariable Long id) {
        return productService.loadImage(id);
    }
}
```

#### Which converter is used?

| Return type | Converter | Content-Type |
|-------------|-----------|--------------|
| POJO / `List` / `Map` / record | `MappingJackson2HttpMessageConverter` | `application/json` |
| `String` | `StringHttpMessageConverter` | `text/plain` |
| `byte[]` | `ByteArrayHttpMessageConverter` | `application/octet-stream` |
| `Resource` | `ResourceHttpMessageConverter` | based on file type |
| POJO with `jackson-dataformat-xml` + `Accept: application/xml` | `MappingJackson2XmlHttpMessageConverter` | `application/xml` |

#### @ResponseBody vs ResponseEntity
`@ResponseBody` only controls the **body**; status is 200 by default (change it with [@ResponseStatus](#responsestatus)). `ResponseEntity<T>` controls **status, headers and body** at runtime, and does not need `@ResponseBody`.

```java
@GetMapping("/{id}")
public ResponseEntity<Product> get(@PathVariable Long id) {
    return productService.find(id)
            .map(p -> ResponseEntity.ok().eTag(String.valueOf(p.getVersion())).body(p))
            .orElse(ResponseEntity.notFound().build());
}
```

#### Common mistakes / best practices
- A `String` returned with `@ResponseBody` is sent as plain text, **not** JSON-quoted.
- A missing converter for the return type and `Accept` header causes `HttpMediaTypeNotAcceptableException` (**406**).

[⬆ Back to Table of Contents](#table-of-contents)

---

### @ResponseStatus

**Package:** `org.springframework.web.bind.annotation.ResponseStatus`
**Applies to:** Method, Class (controller or **exception class**)

#### What it does
Sets the **HTTP status code** (and optionally a reason) of the response. It is used in three places:

1. **On a handler method** → status returned when the method finishes normally (e.g. `201 Created`, `204 No Content`).
2. **On a custom exception class** → when that exception is thrown and not otherwise handled, Spring responds with this status.
3. **On an `@ExceptionHandler` method** → status for the error response.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `HttpStatus` | `HttpStatus.INTERNAL_SERVER_ERROR` | The status code. Alias for `code`. |
| `code` | `HttpStatus` | `HttpStatus.INTERNAL_SERVER_ERROR` | Same as `value`. |
| `reason` | `String` | `""` | Reason message. **If set, Spring calls `response.sendError(status, reason)`**: the response becomes a container error page (Spring Boot's `/error` JSON), and **your method's return value is ignored**. |

#### Example 1: On handler methods

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    // 201 Created + JSON body
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        return userService.save(user);
    }

    // 204 No Content, no body
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    // 202 Accepted for async processing
    @PostMapping("/import")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public Map<String, String> startImport() {
        String jobId = importService.start();
        return Map.of("jobId", jobId, "status", "PROCESSING");
    }
}
```

#### Example 2: On a custom exception class

```java
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "User not found")
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User not found: " + id);
    }
}

@GetMapping("/{id}")
public User get(@PathVariable Long id) {
    return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));   // -> 404
}
```

Response (Spring Boot default error body):
```json
{
  "timestamp": "2026-10-08T10:15:30.000+00:00",
  "status": 404,
  "error": "Not Found",
  "path": "/api/users/99"
}
```

> The `reason`/`message` text is only included in the error body when `server.error.include-message=always` is set.

#### Example 3: On an @ExceptionHandler

```java
@ExceptionHandler(IllegalArgumentException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public Map<String, String> badRequest(IllegalArgumentException ex) {
    return Map.of("error", ex.getMessage());
}
```

#### Common mistakes / best practices
- Using `reason` on a handler method that returns a body → the body is **discarded**. Avoid `reason` in REST APIs; return an error body from an `@ExceptionHandler` instead.
- `@ResponseStatus` sets a **fixed** status. Use `ResponseEntity` when the status depends on runtime logic.
- Alternative: `throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")`, which needs no custom exception class.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 4.4 CORS Annotation

### @CrossOrigin

**Package:** `org.springframework.web.bind.annotation.CrossOrigin`
**Applies to:** Class, Method

#### What it does
Enables **CORS (Cross-Origin Resource Sharing)** for a controller or a single handler method. Browsers block JavaScript on one origin (e.g. `http://localhost:3000`) from calling an API on another origin (e.g. `http://localhost:8080`) unless the server sends CORS headers. `@CrossOrigin` makes Spring add those headers and handle the **preflight `OPTIONS`** request automatically.

When placed on both the class and a method, the attributes are **combined** (method-level values add to or override class-level ones).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String[]` | `{}` | Allowed origins. Alias for `origins`. |
| `origins` | `String[]` | `{}` → all origins (`*`) | Exact allowed origins, e.g. `"https://app.example.com"`. `"*"` allows all. Sent back in `Access-Control-Allow-Origin`. |
| `originPatterns` | `String[]` | `{}` | Origin **patterns** with wildcards, e.g. `"https://*.example.com"`, `"http://localhost:[*]"`. Unlike `origins="*"`, patterns can be combined with `allowCredentials = "true"`. |
| `allowedHeaders` | `String[]` | `{}` → all (`*`) | Request headers the browser may send in the actual request (`Access-Control-Allow-Headers`), e.g. `"Authorization"`, `"Content-Type"`. |
| `exposedHeaders` | `String[]` | `{}` | Response headers that JavaScript is allowed to read (`Access-Control-Expose-Headers`), e.g. `"Location"`, `"X-Total-Count"`. By default JS can read only simple headers. |
| `methods` | `RequestMethod[]` | `{}` → methods of the mapping | Allowed HTTP methods (`Access-Control-Allow-Methods`). By default, the methods declared by the `@RequestMapping` are allowed. |
| `allowCredentials` | `String` | `""` (not allowed) | `"true"` lets the browser send cookies / `Authorization` headers cross-origin (`Access-Control-Allow-Credentials`). **Cannot be used with `origins = "*"`**; use specific origins or `originPatterns`. |
| `allowPrivateNetwork` | `String` | `""` (not allowed) | `"true"` allows requests from public sites to private-network hosts (Chrome's Private Network Access). Available since Spring Framework 6.1.3. |
| `maxAge` | `long` | `-1` → 1800 seconds | How long (seconds) the browser may cache the preflight response (`Access-Control-Max-Age`). |

#### Example

```java
// Class level: whole controller allowed from the React dev server
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    // Inherits class-level CORS config
    @GetMapping
    public List<Product> all() { ... }

    // Method level: adds another origin + cookies + exposed header + cache preflight 1 hour
    @PostMapping
    @CrossOrigin(
            origins = "https://admin.example.com",
            allowedHeaders = {"Authorization", "Content-Type"},
            exposedHeaders = {"Location"},
            methods = {RequestMethod.POST},
            allowCredentials = "true",
            maxAge = 3600
    )
    public ResponseEntity<Product> create(@RequestBody Product p) { ... }
}

// Pattern-based origins (subdomains + any localhost port) with credentials
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(originPatterns = {"https://*.example.com", "http://localhost:[*]"},
             allowCredentials = "true")
public class OrderController { ... }
```

#### Global CORS configuration (recommended for many controllers)

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("https://*.example.com")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
                .allowedHeaders("*")
                .exposedHeaders("Location")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

#### Preflight flow

```
Browser (http://localhost:3000)                    API (http://localhost:8080)
  OPTIONS /api/products                    ──►
  Origin: http://localhost:3000
  Access-Control-Request-Method: POST
                                           ◄──  200 OK
                                                Access-Control-Allow-Origin: http://localhost:3000
                                                Access-Control-Allow-Methods: GET,POST
                                                Access-Control-Max-Age: 1800
  POST /api/products (actual request)      ──►
```

#### Common mistakes / best practices
- `allowCredentials = "true"` + `origins = "*"` → `IllegalArgumentException` at runtime. Use explicit origins or `originPatterns`.
- With **Spring Security**, also call `http.cors(Customizer.withDefaults())`; otherwise the security filter may reject the preflight before it reaches MVC.
- CORS is enforced by **browsers only**; it is not an API security mechanism (Postman/curl ignore it).

[⬆ Back to Table of Contents](#table-of-contents)

---

## 4.5 Exception Handling Annotations

### @ExceptionHandler

**Package:** `org.springframework.web.bind.annotation.ExceptionHandler`
**Applies to:** Method

#### What it does
Marks a method as the **handler for one or more exception types** thrown by controller methods. The method can return a response body, a `ResponseEntity`, a `ProblemDetail`, or a view name.

- Declared **inside a controller** → handles exceptions from **that controller only**.
- Declared inside a **[@ControllerAdvice](#controlleradvice) / [@RestControllerAdvice](#restcontrolleradvice)** → handles exceptions **globally**.

When several handlers match, Spring picks the one for the **closest exception type** in the class hierarchy. Handlers in the controller take priority over global advice.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `Class<? extends Throwable>[]` | `{}` | Exception types handled by this method. If empty, the types are taken from the method's exception parameters. Alias for `exception` (6.2+). |
| `exception` | `Class<? extends Throwable>[]` | `{}` | Same as `value` (added in Spring Framework 6.2). |
| `produces` | `String[]` | `{}` | Media types this handler produces (Spring Framework 6.2+). Lets you have different handlers for the same exception depending on the `Accept` header (e.g. JSON for APIs, HTML for browsers). |

#### Supported method arguments (common)
The exception itself, `HttpServletRequest` / `WebRequest`, `HttpServletResponse`, `HttpSession`, `Locale`, `Principal`, `Model` (for views).

#### Example

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        return userService.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    // Local handler: only exceptions from UserController
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(UserNotFoundException ex, HttpServletRequest req) {
        return Map.of(
                "error", "NOT_FOUND",
                "message", ex.getMessage(),
                "path", req.getRequestURI()
        );
    }

    // Multiple exception types in one handler
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handleBadInput(RuntimeException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    // Exception type inferred from the parameter (no value given)
    @ExceptionHandler
    public ResponseEntity<String> handleConflict(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Duplicate record");
    }

    // RFC 9457 ProblemDetail response
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleForbidden(AccessDeniedException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        pd.setTitle("Access denied");
        return pd;
    }
}
```

#### Common mistakes / best practices
- Handlers in one controller don't apply to others; put shared handlers in a `@RestControllerAdvice`.
- Two handlers for the **same** exception type in the same class → `IllegalStateException: Ambiguous @ExceptionHandler`.
- Don't catch `Exception` everywhere and return 200; always return a proper error status.
- Log unexpected exceptions in a catch-all `Exception` handler, but don't leak stack traces to clients.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @ControllerAdvice

**Package:** `org.springframework.web.bind.annotation.ControllerAdvice`
**Applies to:** Class

#### What it does
A specialized `@Component` that declares methods **shared across many controllers**:

- `@ExceptionHandler` → global exception handling
- `@InitBinder` → global data binder customization
- `@ModelAttribute` → global model attributes

By default it applies to **all controllers**. Use its attributes to restrict it to a subset. Methods return **views** by default (like `@Controller`); add `@ResponseBody` or use [@RestControllerAdvice](#restcontrolleradvice) for JSON.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String[]` | `{}` | Base packages to apply to. Alias for `basePackages`. |
| `basePackages` | `String[]` | `{}` | Controllers in these packages (and sub-packages) are targeted, e.g. `"com.example.api"`. |
| `basePackageClasses` | `Class<?>[]` | `{}` | Type-safe alternative to `basePackages`: the package of each given class is used. |
| `assignableTypes` | `Class<?>[]` | `{}` | Controllers that are assignable to (extend/implement) these types are targeted. |
| `annotations` | `Class<? extends Annotation>[]` | `{}` | Controllers annotated with **any** of these annotations are targeted, e.g. `RestController.class`. |

> If several selectors are given, a controller matches if it satisfies **any** of them (OR logic). With no selectors, the advice applies to every controller.

#### Example: Global error page handling for MVC views

```java
@ControllerAdvice(basePackages = "com.example.web")      // only web (view) controllers
public class GlobalWebExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public String notFound(UserNotFoundException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error/404";                               // templates/error/404.html
    }

    @ExceptionHandler(Exception.class)
    public String general(Exception ex, Model model) {
        model.addAttribute("message", "Something went wrong");
        return "error/500";
    }

    // Global model attribute available in every view
    @ModelAttribute("appName")
    public String appName() {
        return "My Shop";
    }

    // Global binder: trim all String inputs, empty -> null
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
}
```

#### Selector examples

```java
@ControllerAdvice(annotations = RestController.class)              // all @RestController classes
public class RestAdvice { ... }

@ControllerAdvice(basePackageClasses = OrderController.class)      // package of OrderController
public class OrderAdvice { ... }

@ControllerAdvice(assignableTypes = {UserController.class, AdminController.class})
public class UserAdminAdvice { ... }
```

#### Ordering multiple advices

```java
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)   // checked first
public class SpecificAdvice { ... }
```

#### Common mistakes / best practices
- Returning an object from a `@ControllerAdvice` handler without `@ResponseBody` → Spring tries to resolve a view. Use `@RestControllerAdvice` for APIs.
- Having both a narrow advice and a catch-all advice without `@Order` → unpredictable selection.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @RestControllerAdvice

**Package:** `org.springframework.web.bind.annotation.RestControllerAdvice`
**Applies to:** Class

#### What it does
Combines **`@ControllerAdvice` + `@ResponseBody`**. Every `@ExceptionHandler` method's return value is written to the response body as JSON. This is the standard way to build a **global exception handler for REST APIs**.

#### Attributes
Same as [@ControllerAdvice](#controlleradvice) (all are aliases):

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String[]` | `{}` | Base packages. Alias for `basePackages`. |
| `basePackages` | `String[]` | `{}` | Packages whose controllers are targeted. |
| `basePackageClasses` | `Class<?>[]` | `{}` | Type-safe package selection. |
| `assignableTypes` | `Class<?>[]` | `{}` | Specific controller types to target. |
| `annotations` | `Class<? extends Annotation>[]` | `{}` | Target controllers annotated with these annotations. |

#### Example: Complete global REST exception handler

```java
// Standard error response
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors
) {}

@RestControllerAdvice(basePackages = "com.example.api")
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 404 - resource not found
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(UserNotFoundException ex, HttpServletRequest req) {
        return new ApiError(Instant.now(), 404, "Not Found", ex.getMessage(), req.getRequestURI(), null);
    }

    // 400 - @Valid @RequestBody validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
          .forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
        return new ApiError(Instant.now(), 400, "Bad Request", "Validation failed",
                            req.getRequestURI(), errors);
    }

    // 400 - @Validated on @RequestParam / @PathVariable (method validation, Spring 6.1+)
    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodValidation(HandlerMethodValidationException ex, HttpServletRequest req) {
        return new ApiError(Instant.now(), 400, "Bad Request", ex.getMessage(), req.getRequestURI(), null);
    }

    // 400 - wrong type in path/query, e.g. /users/abc
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        String msg = "Parameter '" + ex.getName() + "' has invalid value '" + ex.getValue() + "'";
        return new ApiError(Instant.now(), 400, "Bad Request", msg, req.getRequestURI(), null);
    }

    // 400 - malformed JSON
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadJson(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return new ApiError(Instant.now(), 400, "Bad Request", "Malformed JSON request",
                            req.getRequestURI(), null);
    }

    // 500 - anything else
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleAll(Exception ex, HttpServletRequest req) {
        log.error("Unexpected error", ex);
        return new ApiError(Instant.now(), 500, "Internal Server Error",
                            "An unexpected error occurred", req.getRequestURI(), null);
    }
}
```

Validation error response:
```json
{
  "timestamp": "2026-10-08T10:15:30Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/users",
  "fieldErrors": {
    "email": "must be a well-formed email address",
    "age": "must be greater than or equal to 18"
  }
}
```

#### Alternative: extend `ResponseEntityExceptionHandler` (ProblemDetail)

```java
@RestControllerAdvice
public class ProblemDetailsHandler extends ResponseEntityExceptionHandler {
    // Spring MVC's built-in exceptions (400, 404, 405, 415...) already return RFC 9457 ProblemDetail.
    // Add your own:
    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail notFound(UserNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }
}
```

Or simply set `spring.mvc.problemdetails.enabled=true` to get ProblemDetail responses for built-in exceptions.

#### Common mistakes / best practices
- If you extend `ResponseEntityExceptionHandler`, **don't** also declare `@ExceptionHandler(MethodArgumentNotValidException.class)`; it's already handled there → ambiguous handler error. Override `handleMethodArgumentNotValid(...)` instead.
- Keep a single consistent error format across the API.
- Exceptions thrown in **filters** (e.g. Spring Security) never reach `@RestControllerAdvice`.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 4.6 Session Annotations

### @SessionAttribute

**Package:** `org.springframework.web.bind.annotation.SessionAttribute`
**Applies to:** Method parameter

#### What it does
Binds an **existing HTTP session attribute** (`HttpSession.getAttribute(name)`) to a method parameter. The attribute is usually stored by a login flow, a filter, or another controller. `@SessionAttribute` only **reads** it; it does not create or remove it.

> Not to be confused with **[@SessionAttributes](#sessionattributes)** (plural, class-level), which stores **model** attributes in the session temporarily.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Name of the session attribute. Alias for `name`. Defaults to the parameter name. |
| `name` | `String` | `""` | Same as `value`. |
| `required` | `boolean` | `true` | Whether the attribute must exist. Missing → `ServletRequestBindingException` → **400**. Use `false` (or `Optional<T>`) for optional attributes. |

#### Example

```java
@RestController
@RequestMapping("/api/cart")
public class CartController {

    // Store something in the session (normal HttpSession API)
    @PostMapping("/login")
    public String login(@RequestParam String username, HttpSession session) {
        session.setAttribute("loggedInUser", new LoggedInUser(username));
        return "Logged in as " + username;
    }

    // Read it with @SessionAttribute (required)
    @GetMapping("/me")
    public LoggedInUser me(@SessionAttribute("loggedInUser") LoggedInUser user) {
        return user;
    }

    // Optional attribute
    @GetMapping
    public Cart cart(@SessionAttribute(name = "cart", required = false) Cart cart) {
        return cart != null ? cart : new Cart();
    }

    // Optional<T> variant
    @GetMapping("/coupon")
    public String coupon(@SessionAttribute("coupon") Optional<String> coupon) {
        return coupon.orElse("NO_COUPON");
    }

    // Modify a session object: add to the cart
    @PostMapping("/items")
    public Cart addItem(@RequestBody CartItem item, HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
        cart.add(item);
        return cart;
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "Logged out";
    }
}
```

#### Common mistakes / best practices
- REST APIs should ideally be **stateless** (JWT/tokens). Use session attributes mainly for server-rendered apps.
- Objects stored in the session should be `Serializable` if sessions are persisted or replicated (Spring Session + Redis).

[⬆ Back to Table of Contents](#table-of-contents)

---

### @SessionAttributes

**Package:** `org.springframework.web.bind.annotation.SessionAttributes`
**Applies to:** Class (controller)

#### What it does
Declares which **model attributes** should be **stored in the HTTP session between requests** for this controller. Typical use: **multi-step forms (wizards)**, where one object is filled across several pages.

Lifecycle:
1. When a handler adds a matching attribute to the `Model`, Spring copies it into the session.
2. On later requests, a `@ModelAttribute` parameter with that name is **loaded from the session** instead of being newly created.
3. Calling `SessionStatus.setComplete()` **removes** these attributes from the session.

The attributes are only visible to **this controller**.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String[]` | `{}` | Names of model attributes to store in the session. Alias for `names`. |
| `names` | `String[]` | `{}` | Same as `value`. |
| `types` | `Class<?>[]` | `{}` | Types of model attributes to store. Any model attribute of these types is stored in the session, whatever its name. |

#### Example: Multi-step registration wizard

```java
@Controller
@RequestMapping("/register")
@SessionAttributes("registration")          // keep "registration" in session across steps
public class RegistrationController {

    // Creates the object the first time (only if not already in the session)
    @ModelAttribute("registration")
    public RegistrationForm registration() {
        return new RegistrationForm();
    }

    // Step 1: personal details
    @GetMapping("/step1")
    public String step1() {
        return "register/step1";
    }

    @PostMapping("/step1")
    public String saveStep1(@ModelAttribute("registration") RegistrationForm form) {
        // name, email bound from the form; object stays in the session
        return "redirect:/register/step2";
    }

    // Step 2: address (same object from the session, now with name/email already filled)
    @GetMapping("/step2")
    public String step2() {
        return "register/step2";
    }

    @PostMapping("/step2")
    public String saveStep2(@ModelAttribute("registration") RegistrationForm form) {
        return "redirect:/register/confirm";
    }

    // Final step: save and clear the session attribute
    @PostMapping("/confirm")
    public String confirm(@ModelAttribute("registration") RegistrationForm form,
                          SessionStatus status) {
        userService.register(form);
        status.setComplete();                // removes "registration" from the session
        return "redirect:/register/success";
    }
}
```

Using `types`:

```java
@SessionAttributes(types = {RegistrationForm.class, Address.class})
public class WizardController { ... }
```

#### @SessionAttribute vs @SessionAttributes

| | `@SessionAttribute` | `@SessionAttributes` |
|-|---------------------|----------------------|
| Target | Method parameter | Controller class |
| Purpose | **Read** an existing global session attribute | **Store** model attributes in the session temporarily |
| Scope | Any session attribute | Only this controller's model attributes |
| Cleanup | Manual (`session.removeAttribute`) | `SessionStatus.setComplete()` |

#### Common mistakes / best practices
- Forgetting `status.setComplete()` → old form data stays in the session and shows up the next time.
- If the attribute is not in the session and there is no `@ModelAttribute` method creating it → `HttpSessionRequiredException`.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 4.7 Data Binder Customization

### @InitBinder

**Package:** `org.springframework.web.bind.annotation.InitBinder`
**Applies to:** Method

#### What it does
Marks a method that **customizes the `WebDataBinder`** used for request parameter binding (`@RequestParam`, `@PathVariable`, `@ModelAttribute`, `@RequestHeader`, `@CookieValue`...). It runs **before binding**, on every request handled by the controller.

Common uses:
- Register custom **property editors / formatters** (date formats, trimming strings, custom types).
- **Restrict which fields** can be bound (protect against mass assignment).
- Attach a custom **`Validator`**.

Declared in a controller → applies to that controller. Declared in a [@ControllerAdvice](#controlleradvice) → applies globally.

> `@InitBinder` does **not** affect `@RequestBody` JSON. Jackson handles JSON, so configure it with Jackson annotations (`@JsonFormat`) or `ObjectMapper` settings instead. One exception: validators added with `binder.addValidators(...)` **do** run for `@Valid @RequestBody`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String[]` | `{}` | Names of model attributes and/or request parameters this init-binder applies to. Empty → applies to **all** attributes/parameters. |

The method must return `void` and usually takes a `WebDataBinder` parameter (it can also take `WebRequest`, `Locale`, etc.).

#### Example

```java
@Controller
@RequestMapping("/employees")
public class EmployeeController {

    // 1. Applies to all binding in this controller
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Trim all strings; convert "" to null
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));

        // Custom date format for java.util.Date fields: 08-10-2026
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        df.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(df, true));
    }

    // 2. Only for the "employee" model attribute: whitelist bindable fields
    @InitBinder("employee")
    public void initEmployeeBinder(WebDataBinder binder) {
        binder.setAllowedFields("name", "email", "department", "joiningDate");
        // or blacklist: binder.setDisallowedFields("id", "salary", "role");
        binder.addValidators(new EmployeeValidator());
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("employee") Employee employee, BindingResult result) {
        // If the request contains salary=999999 it is ignored (not an allowed field)
        if (result.hasErrors()) return "employees/form";
        employeeService.save(employee);
        return "redirect:/employees";
    }

    // GET /employees/by-date?date=08-10-2026  -> converted by the CustomDateEditor
    @GetMapping("/by-date")
    @ResponseBody
    public List<Employee> byDate(@RequestParam Date date) {
        return employeeService.joinedOn(date);
    }
}

// Spring Validator used by addValidators
public class EmployeeValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) { return Employee.class.isAssignableFrom(clazz); }

    @Override
    public void validate(Object target, Errors errors) {
        Employee e = (Employee) target;
        if (e.getEmail() != null && !e.getEmail().endsWith("@company.com")) {
            errors.rejectValue("email", "email.domain", "Email must be a company address");
        }
    }
}
```

#### Custom type conversion with a PropertyEditor

```java
// GET /orders?status=shipped  (lower case) -> OrderStatus.SHIPPED
@InitBinder
public void statusBinder(WebDataBinder binder) {
    binder.registerCustomEditor(OrderStatus.class, new PropertyEditorSupport() {
        @Override
        public void setAsText(String text) {
            setValue(OrderStatus.valueOf(text.trim().toUpperCase()));
        }
    });
}
```

#### Global init binder

```java
@ControllerAdvice
public class GlobalBindingConfig {
    @InitBinder
    public void globalBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
}
```

> **Modern alternative:** for type conversion, registering a `Converter<String, T>` or `Formatter<T>` via `WebMvcConfigurer.addFormatters(FormatterRegistry)` is thread-safe and applies globally. Prefer it over `PropertyEditor`s for new code.

#### Common mistakes / best practices
- Expecting `@InitBinder` date editors to work on JSON `@RequestBody` fields — they won't. Use `@JsonFormat(pattern = "dd-MM-yyyy")`.
- `@InitBinder("name")` values must match the **model attribute name** or **request parameter name** exactly.
- Use `setAllowedFields` on any form bound directly to an entity to prevent mass assignment.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 4.8 HTTP Interface Clients

The annotations above are for **receiving** requests. Since Spring Framework 6, you can also **call** other REST APIs declaratively: write a Java interface with exchange annotations, and Spring generates the HTTP client (similar to Feign, but built in).

### @HttpExchange

**Package:** `org.springframework.web.service.annotation.HttpExchange`
**Applies to:** Interface, interface method

#### What it does
Declares an **HTTP client method**. On the interface, it sets shared values (base path, content type). On a method, it maps the method to an HTTP request. Method parameters use `@PathVariable`, `@RequestParam`, `@RequestHeader`, `@RequestBody`, `@RequestPart`, `@CookieValue` just like controllers, but in the opposite direction (they **fill** the outgoing request).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` / `url` | `String` | `""` | URL or path, appended to the interface-level URL and the client's base URL. |
| `method` | `String` | `""` | HTTP method (`"GET"`, `"POST"`...). Usually left empty on the interface and set by the shortcut annotations. |
| `contentType` | `String` | `""` | `Content-Type` of the request body. |
| `accept` | `String[]` | `{}` | Media types for the `Accept` header. |
| `headers` | `String[]` | `{}` | (Spring 6.1+) Static headers as `"name=value"`. |

**Shortcut annotations** (same attributes, method fixed): `@GetExchange`, `@PostExchange`, `@PutExchange`, `@PatchExchange`, `@DeleteExchange`.

#### Example

```java
public record Post(Long id, Long userId, String title, String body) {}

@HttpExchange(url = "/posts", accept = "application/json")
public interface PostClient {

    @GetExchange
    List<Post> findAll(@RequestParam(required = false) Long userId);

    @GetExchange("/{id}")
    Post findById(@PathVariable Long id);

    @PostExchange(contentType = "application/json")
    ResponseEntity<Post> create(@RequestBody Post post);

    @PutExchange("/{id}")
    Post update(@PathVariable Long id, @RequestBody Post post);

    @DeleteExchange("/{id}")
    void delete(@PathVariable Long id, @RequestHeader("X-Request-Id") String requestId);
}

@Configuration
public class ClientConfig {

    @Bean
    public PostClient postClient(RestClient.Builder builder) {
        RestClient restClient = builder
                .baseUrl("https://jsonplaceholder.typicode.com")
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(PostClient.class);
    }
}

@Service
public class FeedService {
    private final PostClient postClient;
    public FeedService(PostClient postClient) { this.postClient = postClient; }

    public List<Post> postsOf(Long userId) {
        return postClient.findAll(userId);     // GET https://jsonplaceholder.typicode.com/posts?userId=1
    }
}
```

#### Common mistakes / best practices
- HTTP errors (4xx/5xx) throw `RestClientResponseException` subclasses (`HttpClientErrorException.NotFound`...). Configure `defaultStatusHandler` on the `RestClient` to map them.
- Set connection and read timeouts on the underlying client; the defaults may wait forever.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 4.9 REST Quick Reference Summary

| Annotation | Target | Purpose | Key attributes |
|------------|--------|---------|----------------|
| `@Controller` | Class | MVC controller returning views | `value` |
| `@RestController` | Class | REST controller (`@Controller` + `@ResponseBody`) | `value` |
| `@RequestMapping` | Class, Method | Map requests by path/method/params/headers/media type | `path`, `method`, `params`, `headers`, `consumes`, `produces`, `name` |
| `@GetMapping` | Method | Map HTTP GET (also handles HEAD) | `path`, `params`, `headers`, `consumes`, `produces` |
| `@PostMapping` | Method | Map HTTP POST (create) | same as above |
| `@PutMapping` | Method | Map HTTP PUT (full replace) | same as above |
| `@PatchMapping` | Method | Map HTTP PATCH (partial update) | same as above |
| `@DeleteMapping` | Method | Map HTTP DELETE | same as above |
| ~~`@HeadMapping`~~ | — | **Does not exist**; GET handles HEAD, or `@RequestMapping(method = HEAD)` | — |
| ~~`@OptionsMapping`~~ | — | **Does not exist**; automatic, or `@RequestMapping(method = OPTIONS)` | — |
| `@PathVariable` | Parameter | Bind a URI template variable | `name`, `required` |
| `@RequestParam` | Parameter | Bind a query/form parameter | `name`, `required`, `defaultValue` |
| `@RequestBody` | Parameter | Deserialize the request body | `required` |
| `@RequestHeader` | Parameter | Bind a request header | `name`, `required`, `defaultValue` |
| `@RequestPart` | Parameter | Bind a multipart part (file/JSON) | `name`, `required` |
| `@RequestAttribute` | Parameter | Bind a server-side request attribute | `name`, `required` |
| `@CookieValue` | Parameter | Bind a cookie value | `name`, `required`, `defaultValue` |
| `@MatrixVariable` | Parameter | Bind `;key=value` path segment variables | `name`, `pathVar`, `required`, `defaultValue` |
| `@ModelAttribute` | Parameter, Method | Bind params into an object / add common model data | `name`, `binding` |
| `@ResponseBody` | Class, Method | Write the return value to the response body | — |
| `@ResponseStatus` | Class, Method, Exception | Set the HTTP status | `code`, `reason` |
| `@CrossOrigin` | Class, Method | Enable CORS | `origins`, `originPatterns`, `methods`, `allowedHeaders`, `exposedHeaders`, `allowCredentials`, `allowPrivateNetwork`, `maxAge` |
| `@ExceptionHandler` | Method | Handle exceptions | `value`/`exception`, `produces` |
| `@ControllerAdvice` | Class | Global handlers/binders/model attributes (views) | `basePackages`, `basePackageClasses`, `assignableTypes`, `annotations` |
| `@RestControllerAdvice` | Class | Global handlers returning JSON | same as `@ControllerAdvice` |
| `@SessionAttribute` | Parameter | Read an existing session attribute | `name`, `required` |
| `@SessionAttributes` | Class | Keep model attributes in the session (wizards) | `names`, `types` |
| `@Valid` | Parameter, Field | Trigger Bean Validation (cascading) | — |
| `@Validated` | Class, Method, Parameter | Validation with groups / method validation | `value` (groups) |
| `@InitBinder` | Method | Customize `WebDataBinder` | `value` (attribute/param names) |

[⬆ Back to Table of Contents](#table-of-contents)

---

# 5. Dependency Injection

**Dependency Injection (DI)** means a class receives the objects it needs (its dependencies) from the Spring container instead of creating them with `new`. This makes classes loosely coupled and easy to test with mocks.

| Injection type | How | Recommended? |
|----------------|-----|--------------|
| **Constructor** | Dependencies are constructor parameters | ✅ Yes: fields can be `final`, dependencies are explicit, easy to test |
| **Setter** | `@Autowired` on a setter | For optional dependencies only |
| **Field** | `@Autowired` on a field | ❌ Avoid: hidden dependencies, can't be `final`, hard to test without Spring |

## 5.1 Injection Annotations

### @Autowired

**Package:** `org.springframework.beans.factory.annotation.Autowired`
**Applies to:** Constructor, method (setter), field, parameter

#### What it does
Asks Spring to inject a matching bean **by type**. If several beans of that type exist, Spring narrows down using `@Primary`, `@Priority`, `@Qualifier`, and finally the **parameter/field name** matching a bean name.

If a class has **only one constructor**, `@Autowired` on it is **optional** (Spring 4.3+).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `required` | `boolean` | `true` | `true` → startup fails with `NoSuchBeanDefinitionException` if no bean matches. `false` → the field/setter is simply skipped (stays `null`). |

#### Example

```java
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;
    private NotificationSender notificationSender;          // optional

    // 1. Constructor injection (single constructor: @Autowired optional)
    public OrderService(OrderRepository orderRepository, PaymentGateway paymentGateway) {
        this.orderRepository = orderRepository;
        this.paymentGateway = paymentGateway;
    }

    // 2. Setter injection for an optional dependency
    @Autowired(required = false)
    public void setNotificationSender(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
    }
}

@Component
public class DiscountEngine {

    // All beans of a type, ordered by @Order
    private final List<DiscountRule> rules;

    // Map: bean name -> bean
    private final Map<String, DiscountRule> rulesByName;

    // Optional dependency
    private final Optional<LoyaltyService> loyaltyService;

    // Lazy / optional / multiple access
    private final ObjectProvider<CouponService> couponService;

    public DiscountEngine(List<DiscountRule> rules,
                          Map<String, DiscountRule> rulesByName,
                          Optional<LoyaltyService> loyaltyService,
                          ObjectProvider<CouponService> couponService) {
        this.rules = rules;
        this.rulesByName = rulesByName;
        this.loyaltyService = loyaltyService;
        this.couponService = couponService;
    }

    public void apply(Order order) {
        rules.forEach(r -> r.apply(order));
        loyaltyService.ifPresent(l -> l.addPoints(order));
        couponService.ifAvailable(c -> c.redeem(order));
    }
}

// With several constructors, mark the one Spring should use
@Component
public class ReportGenerator {
    public ReportGenerator() { ... }

    @Autowired
    public ReportGenerator(TemplateEngine engine) { ... }
}
```

#### Common mistakes / best practices
- Field injection (`@Autowired private X x;`) makes unit tests need reflection or Spring. Prefer constructor injection (Lombok's [@RequiredArgsConstructor](#requiredargsconstructor) removes the boilerplate).
- `NoUniqueBeanDefinitionException: expected single matching bean but found 2` → use [@Qualifier](#qualifier) or [@Primary](#primary).
- `@Autowired` does nothing in objects created with `new`.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Inject / @Named

**Packages:** `jakarta.inject.Inject`, `jakarta.inject.Named` (JSR-330)
**Applies to:** `@Inject`: constructor, method, field. `@Named`: class, parameter, field

#### What they do
Standard Java (Jakarta) dependency-injection annotations that Spring supports as alternatives:
- `@Inject` ≈ `@Autowired` (by type), but has **no `required` attribute**. Use `Optional<T>` or `Provider<T>` for optional dependencies.
- `@Named("x")` on an injection point ≈ `@Qualifier("x")`. On a class, `@Named` ≈ `@Component`.

Useful for code that should not depend on Spring APIs. Requires the dependency:

```xml
<dependency>
    <groupId>jakarta.inject</groupId>
    <artifactId>jakarta.inject-api</artifactId>
</dependency>
```

#### Attributes

| Annotation | Attribute | Type | Default | Description |
|------------|-----------|------|---------|-------------|
| `@Inject` | — | — | — | No attributes. |
| `@Named` | `value` | `String` | `""` | Bean name (on a class) or qualifier (at an injection point). |

#### Example

```java
@Named("smsSender")                              // registers a bean like @Component("smsSender")
public class SmsSender implements MessageSender { ... }

@Named("emailSender")
public class EmailSender implements MessageSender { ... }

@Named
public class AlertService {

    private final MessageSender sender;
    private final Provider<AuditLog> auditLog;   // jakarta.inject.Provider: lazy/prototype access

    @Inject
    public AlertService(@Named("smsSender") MessageSender sender, Provider<AuditLog> auditLog) {
        this.sender = sender;
        this.auditLog = auditLog;
    }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Resource

**Package:** `jakarta.annotation.Resource` (Jakarta Annotations)
**Applies to:** Field, setter method, class

#### What it does
Injects a bean **by name first**, then falls back to **by type**. With `name` set, it looks up exactly that bean name. Without `name`, it uses the **field name** (or the setter's property name) as the bean name, and if no bean has that name, falls back to type matching.

Note: `@Resource` is **not** supported on constructors.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `name` | `String` | `""` | Bean name to inject. Empty → field/property name. |
| `type` | `Class<?>` | `Object.class` | Expected bean type (restricts matching). |
| `lookup` | `String` | `""` | JNDI name to look up (application servers). |
| `mappedName` | `String` | `""` | Vendor-specific (JNDI) name. Rarely used. |
| `authenticationType` | `AuthenticationType` | `CONTAINER` | For JNDI resources managed by an application server. Not used by Spring beans. |
| `shareable` | `boolean` | `true` | For JNDI resources. Not used by Spring beans. |
| `description` | `String` | `""` | Free-text description. |

#### Example

```java
@Configuration
public class DataSourceConfig {
    @Bean public DataSource ordersDataSource()    { ... }
    @Bean public DataSource reportingDataSource() { ... }
}

@Repository
public class ReportDao {

    @Resource(name = "reportingDataSource")      // exact bean by name
    private DataSource dataSource;

    @Resource                                    // name taken from the field: "ordersDataSource"
    private DataSource ordersDataSource;
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

> **@Value** is also an injection annotation (it injects configuration values rather than beans). It is explained in [3.2 Property Sources & Values](#value).

---

## 5.2 Resolving Multiple Candidates

When several beans match the type being injected, Spring must choose one. The resolution order is:

```
1. @Qualifier / @Named on the injection point  → only matching beans remain
2. @Primary bean                               → chosen
3. @Priority (lowest value)                    → chosen
4. Parameter / field name == bean name         → chosen
5. Otherwise                                   → NoUniqueBeanDefinitionException

@Fallback beans (Spring 6.2+) are only used when no other candidate is left.
```

### @Qualifier

**Package:** `org.springframework.beans.factory.annotation.Qualifier`
**Applies to:** Field, parameter, method, class, `@Bean` method, annotation type

#### What it does
Narrows down **which bean** is injected when several beans of the same type exist.
- On an **injection point** → "inject the bean with this qualifier/name".
- On a **bean** (`@Component` class or `@Bean` method) → gives the bean a qualifier value, separate from its name.
- On an **annotation type** → creates a custom, type-safe qualifier annotation.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Qualifier value. If no bean has this qualifier, it is matched against **bean names**. |

#### Example

```java
public interface PaymentGateway { Receipt pay(BigDecimal amount); }

@Component("stripe")
public class StripeGateway implements PaymentGateway { ... }

@Component
@Qualifier("paypal")                         // qualifier on the bean
public class PaypalGateway implements PaymentGateway { ... }

@Service
public class CheckoutService {
    private final PaymentGateway cardGateway;
    private final PaymentGateway walletGateway;

    public CheckoutService(@Qualifier("stripe") PaymentGateway cardGateway,
                           @Qualifier("paypal") PaymentGateway walletGateway) {
        this.cardGateway = cardGateway;
        this.walletGateway = walletGateway;
    }
}

// Qualifier on @Bean methods
@Configuration
public class DataSourceConfig {
    @Bean @Qualifier("orders")    public DataSource ordersDs()    { ... }
    @Bean @Qualifier("reporting") public DataSource reportingDs() { ... }
}
```

**Custom qualifier annotation** (no string typos):

```java
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface Express { }

@Component @Express
public class ExpressShipping implements ShippingService { ... }

@Service
public class DeliveryService {
    public DeliveryService(@Express ShippingService shipping) { ... }
}
```

#### Common mistakes / best practices
- With **Lombok `@RequiredArgsConstructor`**, `@Qualifier` on a field is **not copied** to the generated constructor parameter unless you add `lombok.copyableAnnotations += org.springframework.beans.factory.annotation.Qualifier` to `lombok.config`.
- `@Qualifier` on a `List<T>` injects only the beans carrying that qualifier.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Primary

**Package:** `org.springframework.context.annotation.Primary`
**Applies to:** Class (component), `@Bean` method

#### What it does
Marks a bean as the **default choice** when several beans of the same type exist and the injection point has no `@Qualifier`. Other beans can still be injected explicitly with `@Qualifier`.

#### Attributes
None.

#### Example

```java
@Configuration
public class JsonConfig {

    @Bean
    @Primary                                   // injected everywhere by default
    public ObjectMapper objectMapper() {
        return JsonMapper.builder().findAndAddModules().build();
    }

    @Bean
    public ObjectMapper strictObjectMapper() {
        return JsonMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }
}

@Service
public class ExportService {
    public ExportService(ObjectMapper mapper) { ... }                     // primary one
}

@Service
public class ImportService {
    public ImportService(@Qualifier("strictObjectMapper") ObjectMapper mapper) { ... }
}
```

#### Common mistakes / best practices
- Two `@Primary` beans of the same type → `NoUniqueBeanDefinitionException` again.
- Defining your own bean of a type that Boot auto-configures (e.g. `ObjectMapper`, `DataSource`) makes Boot's back off; mark yours `@Primary` if you add a second one.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Fallback

**Package:** `org.springframework.context.annotation.Fallback`
**Applies to:** Class (component), `@Bean` method

#### What it does
(Spring Framework 6.2+) The opposite of `@Primary`: marks a bean as a **fallback** that is only injected if **no other** candidate of that type is available. Useful for default implementations in libraries.

#### Attributes
None.

#### Example

```java
@Configuration
public class ClockConfig {

    @Bean
    @Fallback                               // used only if the app defines no other Clock
    public Clock systemClock() { return Clock.systemUTC(); }
}

// In tests or another module:
@Bean
public Clock fixedClock() { return Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC); }
// -> fixedClock is injected; systemClock is ignored
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 5.3 Injection Annotations Compared

| Feature | `@Autowired` | `@Inject` | `@Resource` |
|---------|--------------|-----------|-------------|
| Origin | Spring | Jakarta (JSR-330) | Jakarta Annotations (JSR-250) |
| Matches by | Type (then qualifier/name) | Type (then qualifier/name) | **Name** first, then type |
| Constructor injection | ✅ | ✅ | ❌ |
| Field / setter | ✅ | ✅ | ✅ |
| Optional dependency | `required = false` | `Optional<T>` / `Provider<T>` | ❌ |
| Qualifier | `@Qualifier` | `@Named` / `@Qualifier` | `name` attribute |

[⬆ Back to Table of Contents](#table-of-contents)

---

# 6. JPA / Database

**JPA (Jakarta Persistence API)** maps Java classes (**entities**) to database tables. Spring Boot uses **Hibernate** as the JPA implementation (`spring-boot-starter-data-jpa`). All standard annotations are in the package **`jakarta.persistence`** (Boot 3); Hibernate-specific ones are in `org.hibernate.annotations`.

```
Java class  @Entity Employee            ──►  table employees
Field       @Id Long id                 ──►  primary key column id
Field       @Column String email        ──►  column email
Field       @ManyToOne Department dept  ──►  foreign key column department_id
```

> For the SQL side (DDL/DML, constraints, indexes, isolation, locks), see the [Database Systems Guide](Database_systems.md).

Useful properties while learning:
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.hibernate.ddl-auto=validate     # none | validate | update | create | create-drop
```

## 6.1 Entity & Table Mapping

### @Entity

**Package:** `jakarta.persistence.Entity`
**Applies to:** Class

#### What it does
Marks a class as a **JPA entity**: a persistent class mapped to a database table, managed by the `EntityManager`. Every entity needs an [@Id](#id).

Rules for an entity class:
- Must have a **no-args constructor** (`public` or `protected`).
- Must not be `final`, and persistent methods/fields must not be `final` (Hibernate creates proxy subclasses for lazy loading).
- Must not be a record (records are immutable; use them as DTOs/projections instead).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `name` | `String` | `""` | Entity name used in **JPQL** queries. Default: the unqualified class name. Does **not** change the table name (use `@Table`). |

#### Example

```java
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    protected Customer() { }                      // required by JPA

    public Customer(String name, String email) {
        this.name = name;
        this.email = email;
    }
    // getters/setters
}

// Custom entity name for JPQL
@Entity(name = "Client")
public class Customer { ... }
// JPQL: SELECT c FROM Client c
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Table

**Package:** `jakarta.persistence.Table`
**Applies to:** Class (entity)

#### What it does
Specifies the **database table** for the entity. Without it, the table name is derived from the entity name (Spring Boot's naming strategy converts `CustomerOrder` → `customer_order`).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `name` | `String` | `""` | Table name. |
| `schema` | `String` | `""` | Database schema, e.g. `"sales"`. |
| `catalog` | `String` | `""` | Database catalog (used by MySQL as the database name). |
| `uniqueConstraints` | `UniqueConstraint[]` | `{}` | Unique constraints (multi-column) created by schema generation. |
| `indexes` | `Index[]` | `{}` | Indexes created by schema generation. |

**`@UniqueConstraint`**: `name` (constraint name), `columnNames` (`String[]`, required).
**`@Index`**: `name`, `columnList` (required, e.g. `"last_name, first_name"` or `"created_at DESC"`), `unique` (default `false`).

#### Example

```java
@Entity
@Table(
        name = "customers",
        schema = "sales",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_customer_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_customer_tax", columnNames = {"country", "tax_id"})
        },
        indexes = {
                @Index(name = "idx_customer_name", columnList = "last_name, first_name"),
                @Index(name = "idx_customer_created", columnList = "created_at DESC")
        })
public class Customer { ... }
```

#### Common mistakes / best practices
- `uniqueConstraints` and `indexes` only matter when Hibernate **generates the schema** (`ddl-auto=create/update`). In production, manage schema with **Flyway/Liquibase** and keep these annotations as documentation.
- Avoid reserved words as table names (`user`, `order`); use `users`, `orders`.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @MappedSuperclass

**Package:** `jakarta.persistence.MappedSuperclass`
**Applies to:** Class

#### What it does
Marks a **base class whose fields are inherited** by entity subclasses and mapped to **each subclass's own table**. The superclass itself is **not** an entity: it has no table and can't be queried. Ideal for common columns (`id`, `createdAt`, `version`).

#### Attributes
None.

#### Example

```java
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
    // getters
}

@Entity
@Table(name = "products")
public class Product extends BaseEntity {      // table products: id, version, created_at, updated_at, name, price
    private String name;
    private BigDecimal price;
}

@Entity
@Table(name = "categories")
public class Category extends BaseEntity {      // table categories: id, version, created_at, updated_at, title
    private String title;
}
```

> Use [@Inheritance](#inheritance) instead when you need **polymorphic queries** (`SELECT p FROM Payment p` returning all subtypes).

[⬆ Back to Table of Contents](#table-of-contents)

---

## 6.2 Primary Keys

### @Id

**Package:** `jakarta.persistence.Id`
**Applies to:** Field, getter

#### What it does
Marks the **primary key** of the entity. Its position also decides the **access type**: on a field → Hibernate reads/writes fields directly (recommended); on a getter → uses getters/setters.

Supported types: primitives and wrappers, `String`, `UUID`, `BigDecimal`, `BigInteger`, `java.util.Date`, `java.sql.Date`.

#### Attributes
None.

#### Example

```java
@Entity
public class Country {
    @Id
    @Column(length = 2)
    private String isoCode;         // natural key, assigned manually ("IN", "US")
    private String name;
}

@Entity
public class Invoice {
    @Id
    @GeneratedValue                 // generated by the database / Hibernate
    private Long id;
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @GeneratedValue

**Package:** `jakarta.persistence.GeneratedValue`
**Applies to:** Field / getter with `@Id`

#### What it does
Tells JPA that the primary key value is **generated automatically**, and with which strategy.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `strategy` | `GenerationType` | `AUTO` | How the ID is generated (see below). |
| `generator` | `String` | `""` | Name of a [@SequenceGenerator](#sequencegenerator) or [@TableGenerator](#tablegenerator) to use. |

| Strategy | How it works | Notes |
|----------|--------------|-------|
| `IDENTITY` | Auto-increment column (`BIGSERIAL`, `AUTO_INCREMENT`, `IDENTITY`) | Simple. Hibernate must INSERT immediately to get the ID, so **JDBC batch inserts are disabled**. Best for MySQL. |
| `SEQUENCE` | Database sequence (`nextval`) | Best performance (supports batching and pre-allocation). Best for PostgreSQL, Oracle. |
| `TABLE` | A separate table simulates a sequence | Portable but slow (row locks). Avoid. |
| `AUTO` | Hibernate picks: `SEQUENCE` if the DB supports sequences, otherwise `TABLE` | For `UUID` fields, generates a UUID. |
| `UUID` | Generates a random `java.util.UUID` (JPA 3.1+) | Field type must be `UUID` or `String`. |

#### Example

```java
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)        // MySQL style
    private Long id;
}

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_seq")
    @SequenceGenerator(name = "payment_seq", sequenceName = "payment_id_seq", allocationSize = 50)
    private Long id;
}

@Entity
public class ApiKey {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @SequenceGenerator

**Package:** `jakarta.persistence.SequenceGenerator`
**Applies to:** Class, field, method

#### What it does
Defines a **database sequence**-based ID generator that `@GeneratedValue(strategy = SEQUENCE, generator = "...")` refers to.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `name` | `String` | — (required) | Generator name, referenced by `@GeneratedValue.generator`. |
| `sequenceName` | `String` | `""` | Name of the sequence in the database. Default: provider-chosen (Hibernate uses `<entity>_seq`). |
| `schema` | `String` | `""` | Schema of the sequence. |
| `catalog` | `String` | `""` | Catalog of the sequence. |
| `initialValue` | `int` | `1` | First value (used when the schema is generated). |
| `allocationSize` | `int` | `50` | How many IDs Hibernate reserves per sequence call. **Must equal the sequence's `INCREMENT BY`**. |

#### Example

```java
@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticket_gen")
    @SequenceGenerator(name = "ticket_gen", sequenceName = "ticket_seq",
                       initialValue = 1000, allocationSize = 50)
    private Long id;
}
```

```sql
CREATE SEQUENCE ticket_seq START WITH 1000 INCREMENT BY 50;
```

#### How allocationSize works
With `allocationSize = 50`, Hibernate calls `nextval` once and then assigns 50 IDs from memory. Fewer database round trips, but IDs may have **gaps** after restarts (that's normal).

#### Common mistakes / best practices
- `allocationSize` differs from the sequence's `INCREMENT BY` → duplicate key errors or "sequence increment size mismatch" at startup (with `ddl-auto=validate`).

[⬆ Back to Table of Contents](#table-of-contents)

---

### @TableGenerator

**Package:** `jakarta.persistence.TableGenerator`
**Applies to:** Class, field, method

#### What it does
Defines an ID generator that stores the **next value in a database table**. Works on any database (even without sequences) but is **slow** because of row locking. Use only if `IDENTITY` and `SEQUENCE` are impossible.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `name` | `String` | — (required) | Generator name. |
| `table` | `String` | `""` | Name of the generator table. |
| `schema` / `catalog` | `String` | `""` | Schema / catalog of the table. |
| `pkColumnName` | `String` | `""` | Column holding the generator key (e.g. `"gen_name"`). |
| `valueColumnName` | `String` | `""` | Column holding the next value (e.g. `"gen_value"`). |
| `pkColumnValue` | `String` | `""` | Row key for this generator (e.g. `"invoice_id"`). |
| `initialValue` | `int` | `0` | Initial value. |
| `allocationSize` | `int` | `50` | IDs reserved per database update. |
| `uniqueConstraints` | `UniqueConstraint[]` | `{}` | Constraints for the generated table. |
| `indexes` | `Index[]` | `{}` | Indexes for the generated table. |

#### Example

```java
@Entity
public class LegacyInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "invoice_gen")
    @TableGenerator(name = "invoice_gen", table = "id_generators",
                    pkColumnName = "gen_name", valueColumnName = "gen_value",
                    pkColumnValue = "invoice_id", allocationSize = 20)
    private Long id;
}
```

```
id_generators
+-------------+-----------+
| gen_name    | gen_value |
+-------------+-----------+
| invoice_id  | 140       |
+-------------+-----------+
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @IdClass

**Package:** `jakarta.persistence.IdClass`
**Applies to:** Class (entity)

#### What it does
Defines a **composite primary key** (several columns) by putting multiple `@Id` fields directly in the entity, and naming a separate class that holds the same fields. The alternative is [@EmbeddedId](#embeddedid).

The ID class must be `Serializable`, have a no-args constructor, and implement `equals()`/`hashCode()` (a record works).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `Class<?>` | — (required) | The primary key class. |

#### Example

```java
public record EnrollmentId(Long studentId, Long courseId) implements Serializable { }

@Entity
@IdClass(EnrollmentId.class)
public class Enrollment {
    @Id private Long studentId;
    @Id private Long courseId;
    private LocalDate enrolledOn;
}

// Repository uses the ID class as the ID type
public interface EnrollmentRepository extends JpaRepository<Enrollment, EnrollmentId> { }
enrollmentRepository.findById(new EnrollmentId(1L, 7L));
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 6.3 Column Mapping

### @Column

**Package:** `jakarta.persistence.Column`
**Applies to:** Field, getter

#### What it does
Customizes the **column** a field is mapped to. Without it, every non-static, non-transient field is still mapped, using the field name (converted to `snake_case` by Spring Boot).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `name` | `String` | `""` | Column name. |
| `nullable` | `boolean` | `true` | `false` → `NOT NULL` in generated DDL. (Not a runtime validation; use `@NotNull` for that.) |
| `unique` | `boolean` | `false` | Single-column unique constraint in generated DDL. |
| `length` | `int` | `255` | Length for `String` columns (`VARCHAR(255)`). |
| `precision` | `int` | `0` | Total digits for decimals (`DECIMAL(precision, scale)`). |
| `scale` | `int` | `0` | Digits after the decimal point. |
| `insertable` | `boolean` | `true` | `false` → column is never included in `INSERT`. |
| `updatable` | `boolean` | `true` | `false` → column is never included in `UPDATE` (e.g. `createdAt`). |
| `columnDefinition` | `String` | `""` | Raw SQL type used in DDL, e.g. `"TEXT"`, `"JSONB"`, `"DECIMAL(10,2) DEFAULT 0"`. Database-specific. |
| `table` | `String` | `""` | Secondary table holding the column (with `@SecondaryTable`). |

#### Example

```java
@Entity
@Table(name = "products")
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false, length = 150)
    private String name;

    @Column(unique = true, nullable = false, length = 40)
    private String sku;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal price;                       // DECIMAL(12,2)

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Basic

**Package:** `jakarta.persistence.Basic`
**Applies to:** Field, getter

#### What it does
The default mapping for simple fields (strings, numbers, dates, enums...). It is **implicit**; you write it only to change `fetch` or `optional`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `fetch` | `FetchType` | `EAGER` | `LAZY` → load the column only when accessed (a hint; for basic fields Hibernate needs **bytecode enhancement** to honor it). |
| `optional` | `boolean` | `true` | `false` → the value must not be null (schema hint + Hibernate check). |

#### Example

```java
@Entity
public class Article {
    @Id @GeneratedValue private Long id;

    @Basic(optional = false)
    private String title;

    @Lob
    @Basic(fetch = FetchType.LAZY)        // large content loaded on demand (with bytecode enhancement)
    private String content;
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Transient

**Package:** `jakarta.persistence.Transient`
**Applies to:** Field, getter

#### What it does
Excludes a field from persistence: **no column** is created, and it's never saved or loaded. Use it for calculated or temporary values.

> Not the same as Java's `transient` keyword (which excludes a field from Java serialization). A field with the `transient` keyword is also ignored by JPA, but it's then excluded from serialization too.

#### Attributes
None.

#### Example

```java
@Entity
public class OrderLine {
    @Id @GeneratedValue private Long id;
    private int quantity;
    private BigDecimal unitPrice;

    @Transient
    private boolean selected;                   // UI-only flag

    @Transient
    public BigDecimal getTotal() {              // computed, not stored
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Lob

**Package:** `jakarta.persistence.Lob`
**Applies to:** Field, getter

#### What it does
Maps a field to a **large object** column:
- `String`, `char[]` → **CLOB** (large text) (`TEXT` on PostgreSQL/MySQL, `CLOB` on Oracle)
- `byte[]`, `Byte[]`, `Serializable` → **BLOB** (binary)

#### Attributes
None.

#### Example

```java
@Entity
public class Document {
    @Id @GeneratedValue private Long id;

    private String fileName;
    private String contentType;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] data;                  // BLOB

    @Lob
    private String extractedText;         // CLOB
}
```

#### Common mistakes / best practices
- On **PostgreSQL**, `@Lob String` maps to the special `oid` large-object type, not `TEXT`. Prefer `@Column(columnDefinition = "TEXT")` (or `@JdbcTypeCode(SqlTypes.LONGVARCHAR)`) for strings.
- Storing big files in the database bloats it; consider object storage (S3) and keep only the path.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Enumerated

**Package:** `jakarta.persistence.Enumerated`
**Applies to:** Field, getter (of an enum type)

#### What it does
Defines how an **enum** is stored.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `EnumType` | `ORDINAL` | `ORDINAL` → stores the position (`0`, `1`, `2`). `STRING` → stores the name (`"ACTIVE"`). |

#### Example

```java
public enum OrderStatus { NEW, PAID, SHIPPED, CANCELLED }

@Entity
public class Order {
    @Id @GeneratedValue private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private OrderStatus status;           // stored as 'PAID'
}
```

#### Common mistakes / best practices
- **Always use `EnumType.STRING`.** With the default `ORDINAL`, inserting a new constant in the middle of the enum (or reordering) silently **changes the meaning of existing rows**.
- With `STRING`, renaming a constant breaks existing rows; for full control use an [AttributeConverter](#convert) storing a stable code.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Temporal

**Package:** `jakarta.persistence.Temporal`
**Applies to:** Field, getter (of type `java.util.Date` / `java.util.Calendar`)

#### What it does
Specifies the SQL type for **legacy** date types (`java.util.Date`, `Calendar`), which don't say whether they hold a date, a time or both.

> **Not needed with `java.time`** (`LocalDate`, `LocalDateTime`, `Instant`, `OffsetDateTime`), which are supported directly. `@Temporal` is **deprecated in JPA 3.2**. Use `java.time` types in new code.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `TemporalType` | — (required) | `DATE` (date only), `TIME` (time only), `TIMESTAMP` (date + time). |

#### Example

```java
// Legacy style
@Temporal(TemporalType.DATE)
private Date birthDate;

@Temporal(TemporalType.TIMESTAMP)
private Date lastLogin;

// Modern style (no annotation needed)
private LocalDate birthDate;        // DATE
private LocalDateTime lastLogin;    // TIMESTAMP
private Instant createdAt;          // TIMESTAMP (WITH TIME ZONE depending on DB)
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Version

**Package:** `jakarta.persistence.Version`
**Applies to:** Field, getter

#### What it does
Enables **optimistic locking**. JPA increments the version on every update and adds it to the `WHERE` clause:

```sql
UPDATE products SET price = ?, version = 6 WHERE id = ? AND version = 5
```

If another transaction updated the row first, no row matches → JPA throws `OptimisticLockException` (Spring: `ObjectOptimisticLockingFailureException`). This prevents **lost updates** without database locks.

Supported types: `int`, `Integer`, `short`, `Short`, `long`, `Long`, `java.sql.Timestamp`, `Instant`, `LocalDateTime`.

#### Attributes
None.

#### Example

```java
@Entity
public class Product {
    @Id @GeneratedValue private Long id;
    private BigDecimal price;

    @Version
    private Long version;                 // never set it manually
}

@Service
public class PriceService {
    @Transactional
    public void changePrice(Long id, BigDecimal newPrice) {
        Product p = productRepository.findById(id).orElseThrow();
        p.setPrice(newPrice);
    }   // commit → UPDATE ... WHERE id=? AND version=?
}

@RestControllerAdvice
public class ConcurrencyHandler {
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> conflict() {
        return Map.of("error", "The record was modified by someone else. Reload and try again.");
    }
}
```

> Optimistic vs pessimistic locking at the SQL level: [Database Systems Guide](Database_systems.md#optimistic-vs-pessimistic-locking).

#### Common mistakes / best practices
- For REST APIs, send the version to the client (or as an `ETag`) and check it on update; otherwise the check only protects the short time inside one transaction.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 6.4 Embeddables

An **embeddable** is a value object (like `Address` or `Money`) whose fields are stored **in the owning entity's table**. It has no ID and no table of its own.

### @Embeddable

**Package:** `jakarta.persistence.Embeddable`
**Applies to:** Class, record (Hibernate 6.2+)

#### What it does
Marks a class as **embeddable**: it can be used as a field inside entities, and its fields become columns of the entity's table.

#### Attributes
None.

#### Example

```java
@Embeddable
public class Address {
    private String street;
    private String city;
    @Column(name = "zip_code", length = 10)
    private String zip;
    private String country;

    protected Address() { }
    public Address(String street, String city, String zip, String country) { ... }
    // getters (no setters: value object)
}

// Records are supported as embeddables in Hibernate 6.2+
@Embeddable
public record Money(BigDecimal amount, String currency) { }
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Embedded

**Package:** `jakarta.persistence.Embedded`
**Applies to:** Field, getter

#### What it does
Marks a field in an entity whose type is an `@Embeddable`. Its columns are **flattened** into the entity's table. (Optional if the type is already annotated with `@Embeddable`, but it makes the intent clear.)

#### Attributes
None.

#### Example

```java
@Entity
@Table(name = "customers")
public class Customer {
    @Id @GeneratedValue private Long id;
    private String name;

    @Embedded
    private Address address;        // columns: street, city, zip_code, country
}
```

Resulting table:
```
customers: id | name | street | city | zip_code | country
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @EmbeddedId

**Package:** `jakarta.persistence.EmbeddedId`
**Applies to:** Field, getter

#### What it does
Uses an `@Embeddable` class as a **composite primary key**. The embeddable must be `Serializable` and implement `equals()`/`hashCode()` (records do this automatically).

#### Attributes
None.

#### Example

```java
@Embeddable
public record OrderLineId(Long orderId, Integer lineNo) implements Serializable { }

@Entity
public class OrderLine {

    @EmbeddedId
    private OrderLineId id;

    private String product;
    private int quantity;
}

public interface OrderLineRepository extends JpaRepository<OrderLine, OrderLineId> { }

// JPQL uses the path id.orderId
@Query("SELECT l FROM OrderLine l WHERE l.id.orderId = :orderId")
List<OrderLine> findByOrder(Long orderId);
```

#### @EmbeddedId vs @IdClass

| | `@EmbeddedId` | `@IdClass` |
|-|---------------|------------|
| Key fields in the entity | One field (the embeddable) | Several `@Id` fields |
| JPQL path | `e.id.orderId` | `e.orderId` |
| Reuse of key as value object | ✅ Natural | Less natural |

[⬆ Back to Table of Contents](#table-of-contents)

---

### @AttributeOverride

**Package:** `jakarta.persistence.AttributeOverride`
**Applies to:** Field (embedded), class (entity extending a mapped superclass)

#### What it does
**Overrides the column mapping** of a field that comes from an embeddable or a `@MappedSuperclass`. Most useful when the **same embeddable is used twice** in one entity (columns would clash). It is repeatable.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `name` | `String` | — (required) | Name of the field being overridden (dot notation for nested: `"location.lat"`). |
| `column` | `Column` | — (required) | The new column mapping. |

#### Example

```java
@Entity
public class Shipment {
    @Id @GeneratedValue private Long id;

    @Embedded
    @AttributeOverride(name = "street",  column = @Column(name = "from_street"))
    @AttributeOverride(name = "city",    column = @Column(name = "from_city"))
    @AttributeOverride(name = "zip",     column = @Column(name = "from_zip"))
    @AttributeOverride(name = "country", column = @Column(name = "from_country"))
    private Address origin;

    @Embedded
    @AttributeOverride(name = "street",  column = @Column(name = "to_street"))
    @AttributeOverride(name = "city",    column = @Column(name = "to_city"))
    @AttributeOverride(name = "zip",     column = @Column(name = "to_zip"))
    @AttributeOverride(name = "country", column = @Column(name = "to_country"))
    private Address destination;
}

// Override a column inherited from a @MappedSuperclass
@Entity
@AttributeOverride(name = "id", column = @Column(name = "product_id"))
public class Product extends BaseEntity { ... }
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @AttributeOverrides

**Package:** `jakarta.persistence.AttributeOverrides`
**Applies to:** Field, class

#### What it does
The **container** for several `@AttributeOverride`s. Since JPA 2.2, `@AttributeOverride` is repeatable, so you can simply repeat it (as above); `@AttributeOverrides` is the older explicit form.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `AttributeOverride[]` | — (required) | The overrides. |

#### Example

```java
@Embedded
@AttributeOverrides({
        @AttributeOverride(name = "amount",   column = @Column(name = "total_amount", precision = 12, scale = 2)),
        @AttributeOverride(name = "currency", column = @Column(name = "total_currency", length = 3))
})
private Money total;
```

> For relationships inside embeddables, use `@AssociationOverride` / `@AssociationOverrides` the same way (overrides join columns/tables).

[⬆ Back to Table of Contents](#table-of-contents)

---

## 6.5 Relationships

### Relationship attributes at a glance

| Attribute | `@OneToOne` | `@OneToMany` | `@ManyToOne` | `@ManyToMany` | Description |
|-----------|:-----------:|:------------:|:------------:|:-------------:|-------------|
| `fetch` | `EAGER` | `LAZY` | `EAGER` | `LAZY` | When the related data is loaded. |
| `cascade` | `{}` | `{}` | `{}` | `{}` | Operations propagated to the related entity. |
| `optional` | `true` | — | `true` | — | `false` → relationship required (NOT NULL, inner join). |
| `mappedBy` | ✅ | ✅ | — | ✅ | Marks the **inverse** side; value = field name on the owning side. |
| `orphanRemoval` | `false` | `false` | — | — | Delete child rows removed from the relationship. |
| `targetEntity` | ✅ | ✅ | ✅ | ✅ | Target class (only needed for raw/generic types). |

**CascadeType values:** `PERSIST`, `MERGE`, `REMOVE`, `REFRESH`, `DETACH`, `ALL`.

**Owning side vs inverse side:** the **owning side** holds the foreign key (the side with `@JoinColumn`, usually `@ManyToOne`). Only changes to the owning side are written to the database. The **inverse side** uses `mappedBy` and is just a mirror.

> **Best practice:** set `fetch = FetchType.LAZY` on **every** `@ManyToOne` and `@OneToOne`. EAGER defaults cause extra queries (N+1) everywhere the entity is loaded. Load related data explicitly with `JOIN FETCH` or `@EntityGraph` when needed.

### @ManyToOne

**Package:** `jakarta.persistence.ManyToOne`
**Applies to:** Field, getter

#### What it does
Many rows of this entity point to **one** row of another entity (many employees → one department). This side owns the **foreign key** column. It is the most common and the most efficient relationship.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `fetch` | `FetchType` | `EAGER` | Change to `LAZY` (recommended). |
| `optional` | `boolean` | `true` | `false` → every row must have a parent. |
| `cascade` | `CascadeType[]` | `{}` | Usually none: children shouldn't cascade to parents. |
| `targetEntity` | `Class` | inferred | Target entity class. |

#### Example

```java
@Entity
public class Employee {
    @Id @GeneratedValue private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}
```

```
employees: id | name | department_id (FK → departments.id)
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @OneToMany

**Package:** `jakarta.persistence.OneToMany`
**Applies to:** Field, getter (of a collection)

#### What it does
One row of this entity has **many** related rows (one department → many employees). Usually the **inverse side** of a `@ManyToOne`, declared with `mappedBy`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `mappedBy` | `String` | `""` | Field name of the `@ManyToOne` on the child. Without it, JPA creates a **join table** (or uses `@JoinColumn` on this side), which is less efficient. |
| `cascade` | `CascadeType[]` | `{}` | Commonly `ALL` for parent-child aggregates (order → order lines). |
| `orphanRemoval` | `boolean` | `false` | `true` → a child removed from the collection is **deleted**. |
| `fetch` | `FetchType` | `LAZY` | Keep `LAZY`. |
| `targetEntity` | `Class` | inferred | Target entity class. |

#### Example: bidirectional parent-child

```java
@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue private Long id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLine> lines = new ArrayList<>();

    // Helper methods keep BOTH sides in sync
    public void addLine(OrderLine line) {
        lines.add(line);
        line.setOrder(this);
    }

    public void removeLine(OrderLine line) {
        lines.remove(line);
        line.setOrder(null);            // orphanRemoval → DELETE
    }
}

@Entity
public class OrderLine {
    @Id @GeneratedValue private Long id;
    private String product;
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;                // owning side (FK)
}

// Saving the order also saves its lines (cascade)
Order order = new Order();
order.addLine(new OrderLine("Keyboard", 1));
order.addLine(new OrderLine("Mouse", 2));
orderRepository.save(order);
```

#### Common mistakes / best practices
- Only setting `order.getLines().add(line)` without `line.setOrder(order)` → the FK is `null` (the inverse side is ignored when writing).
- Unidirectional `@OneToMany` without `mappedBy` creates an extra join table; add `@JoinColumn(name = "order_id")` or make it bidirectional.
- Don't use `cascade = REMOVE` / `ALL` on relationships to **shared** entities (e.g. tags).
- Never return entities with bidirectional relations directly as JSON (infinite recursion); use DTOs.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @OneToOne

**Package:** `jakarta.persistence.OneToOne`
**Applies to:** Field, getter

#### What it does
One row relates to **exactly one** row of another entity (user → profile). The side with `@JoinColumn` owns the foreign key; the other side can use `mappedBy`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `fetch` | `FetchType` | `EAGER` | Change to `LAZY`. |
| `optional` | `boolean` | `true` | `false` → relationship required. |
| `mappedBy` | `String` | `""` | Inverse side: field name on the owning side. |
| `cascade` | `CascadeType[]` | `{}` | Operations to cascade. |
| `orphanRemoval` | `boolean` | `false` | Delete the related row when the reference is set to `null`. |
| `targetEntity` | `Class` | inferred | Target entity class. |

#### Example: shared primary key with @MapsId (most efficient)

```java
@Entity
public class User {
    @Id @GeneratedValue private Long id;
    private String username;
}

@Entity
public class UserProfile {
    @Id
    private Long id;                      // same value as user.id

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId                               // PK is also the FK to users.id
    @JoinColumn(name = "id")
    private User user;

    private String bio;
    private String avatarUrl;
}
```

#### Example: foreign key column + bidirectional

```java
@Entity
public class Passport {
    @Id @GeneratedValue private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", unique = true)
    private Person person;                // owning side
}

@Entity
public class Person {
    @Id @GeneratedValue private Long id;

    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL)
    private Passport passport;            // inverse side
}
```

> The **inverse side** of a `@OneToOne` can't be lazy loaded by Hibernate (it must query to know whether it's `null`). Prefer unidirectional or `@MapsId` mappings.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @ManyToMany

**Package:** `jakarta.persistence.ManyToMany`
**Applies to:** Field, getter (of a collection)

#### What it does
Many rows relate to many rows (students ↔ courses, posts ↔ tags) via a **join table**. One side owns the relationship (with `@JoinTable`), the other uses `mappedBy`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `mappedBy` | `String` | `""` | Inverse side: field name on the owning side. |
| `cascade` | `CascadeType[]` | `{}` | Usually `PERSIST`/`MERGE` only, **never `REMOVE`**. |
| `fetch` | `FetchType` | `LAZY` | Keep `LAZY`. |
| `targetEntity` | `Class` | inferred | Target entity class. |

#### Example

```java
@Entity
public class Post {
    @Id @GeneratedValue private Long id;
    private String title;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "post_tags",
               joinColumns = @JoinColumn(name = "post_id"),
               inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();      // Set: avoids delete-all/re-insert on List changes

    public void addTag(Tag tag)    { tags.add(tag);    tag.getPosts().add(this); }
    public void removeTag(Tag tag) { tags.remove(tag); tag.getPosts().remove(this); }
}

@Entity
public class Tag {
    @Id @GeneratedValue private Long id;
    @Column(unique = true) private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Post> posts = new HashSet<>();
}
```

#### Common mistakes / best practices
- Need extra columns on the link (e.g. `enrolledOn`, `grade`)? Replace `@ManyToMany` with a **join entity** (`Enrollment`) and two `@ManyToOne`s.
- Use `Set`, and implement `equals/hashCode` on a stable key (or the ID with care).

[⬆ Back to Table of Contents](#table-of-contents)

---

## 6.6 Join Configuration

### @JoinColumn

**Package:** `jakarta.persistence.JoinColumn`
**Applies to:** Field, getter (relationship)

#### What it does
Defines the **foreign key column** of a relationship (on the owning side). Without it, the column name defaults to `<field>_<referenced pk>` (e.g. `department_id`).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `name` | `String` | `""` | FK column name. |
| `referencedColumnName` | `String` | `""` (PK) | Column in the target table the FK points to (when not the PK). |
| `nullable` | `boolean` | `true` | `false` → `NOT NULL` FK. |
| `unique` | `boolean` | `false` | Unique FK (one-to-one). |
| `insertable` / `updatable` | `boolean` | `true` | `false` → read-only mapping of the FK (e.g. when also mapped as a plain column). |
| `columnDefinition` | `String` | `""` | SQL fragment for DDL. |
| `table` | `String` | `""` | Table containing the column (secondary tables). |
| `foreignKey` | `ForeignKey` | provider default | FK constraint name/definition for DDL: `@ForeignKey(name = "fk_emp_dept")`, or `@ForeignKey(ConstraintMode.NO_CONSTRAINT)` to not create one. |

#### Example

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "department_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_employee_department"))
private Department department;

// FK to a non-PK unique column
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "country_code", referencedColumnName = "iso_code")
private Country country;

// Map the FK both as a relation and as a raw ID (read-only relation)
@Column(name = "manager_id")
private Long managerId;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "manager_id", insertable = false, updatable = false)
private Employee manager;
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @JoinColumns

**Package:** `jakarta.persistence.JoinColumns`
**Applies to:** Field, getter

#### What it does
Defines a **composite foreign key** (several columns), needed when the target entity has a composite primary key.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `JoinColumn[]` | — (required) | The FK columns; each with `referencedColumnName`. |
| `foreignKey` | `ForeignKey` | provider default | FK constraint definition. |

#### Example

```java
@Entity
public class Shipment {
    @Id @GeneratedValue private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "order_id", referencedColumnName = "orderId"),
            @JoinColumn(name = "line_no",  referencedColumnName = "lineNo")
    })
    private OrderLine orderLine;                // OrderLine has @EmbeddedId(orderId, lineNo)
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @JoinTable

**Package:** `jakarta.persistence.JoinTable`
**Applies to:** Field, getter (relationship)

#### What it does
Defines the **join (link) table** of a `@ManyToMany` (or a unidirectional `@OneToMany` / `@ManyToOne` through a table).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `name` | `String` | `""` | Join table name (default `<owner>_<target>`). |
| `schema` / `catalog` | `String` | `""` | Schema / catalog. |
| `joinColumns` | `JoinColumn[]` | `{}` | FK column(s) pointing to the **owning** entity. |
| `inverseJoinColumns` | `JoinColumn[]` | `{}` | FK column(s) pointing to the **target** entity. |
| `foreignKey` / `inverseForeignKey` | `ForeignKey` | provider default | FK constraint definitions. |
| `uniqueConstraints` | `UniqueConstraint[]` | `{}` | Constraints on the join table. |
| `indexes` | `Index[]` | `{}` | Indexes on the join table. |

#### Example

```java
@ManyToMany
@JoinTable(
        name = "student_courses",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"}))
private Set<Course> courses = new HashSet<>();
```

```
student_courses: student_id (FK → students.id) | course_id (FK → courses.id)
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @MapsId

**Package:** `jakarta.persistence.MapsId`
**Applies to:** Field, getter (a `@ManyToOne` / `@OneToOne`)

#### What it does
Says that the relationship's **foreign key is also (part of) this entity's primary key**. The ID value is copied from the related entity, so you don't set it yourself.

- Without a value → the whole ID comes from the relationship (shared primary key, see [@OneToOne](#onetoone)).
- With a value → maps one attribute of an `@EmbeddedId`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Name of the attribute inside the `@EmbeddedId` that this relationship provides. Empty → the whole primary key. |

#### Example: join entity with a composite key

```java
@Embeddable
public record EnrollmentKey(Long studentId, Long courseId) implements Serializable { }

@Entity
public class Enrollment {

    @EmbeddedId
    private EnrollmentKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("studentId")                 // fills id.studentId from student.id
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseId")                  // fills id.courseId from course.id
    @JoinColumn(name = "course_id")
    private Course course;

    private LocalDate enrolledOn;
    private String grade;

    protected Enrollment() { }
    public Enrollment(Student s, Course c) {
        this.student = s;
        this.course = c;
        this.id = new EnrollmentKey(s.getId(), c.getId());
        this.enrolledOn = LocalDate.now();
    }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 6.7 Collections

### @OrderBy

**Package:** `jakarta.persistence.OrderBy`
**Applies to:** Field, getter (collection)

#### What it does
Sorts a collection **when it is loaded** from the database (adds `ORDER BY` to the collection query). It does not keep the order when you add elements in Java.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Comma-separated **entity field names** with `ASC`/`DESC`, e.g. `"lineNo ASC"`, `"lastName, firstName DESC"`. Empty → order by primary key. |

#### Example

```java
@Entity
public class Order {
    @Id @GeneratedValue private Long id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("lineNo ASC")
    private List<OrderLine> lines = new ArrayList<>();
}

@ElementCollection
@OrderBy                                  // for basic collections: by the value itself
private List<String> nicknames;
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @OrderColumn

**Package:** `jakarta.persistence.OrderColumn`
**Applies to:** Field, getter (`List`)

#### What it does
Stores the **list position** in an extra column, so a `List` keeps the exact order in which you put elements (playlist songs, steps of a recipe). Hibernate maintains the index values on insert/remove.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `name` | `String` | `""` | Index column name (default `<field>_ORDER`). |
| `nullable` | `boolean` | `true` | Whether the column allows nulls. |
| `insertable` / `updatable` | `boolean` | `true` | Whether the column is included in INSERT / UPDATE. |
| `columnDefinition` | `String` | `""` | SQL fragment for DDL. |

#### Example

```java
@Entity
public class Playlist {
    @Id @GeneratedValue private Long id;

    @ManyToMany
    @JoinTable(name = "playlist_songs",
               joinColumns = @JoinColumn(name = "playlist_id"),
               inverseJoinColumns = @JoinColumn(name = "song_id"))
    @OrderColumn(name = "position")
    private List<Song> songs = new ArrayList<>();
}
```

```
playlist_songs: playlist_id | song_id | position (0, 1, 2...)
```

#### @OrderBy vs @OrderColumn

| | `@OrderBy` | `@OrderColumn` |
|-|------------|----------------|
| Extra column | ❌ | ✅ (index column) |
| Order source | Sorted by entity fields | Exact insertion/user order |
| Cost | Free | Updates index values when the list changes |

[⬆ Back to Table of Contents](#table-of-contents)

---

### @ElementCollection

**Package:** `jakarta.persistence.ElementCollection`
**Applies to:** Field, getter (collection or map)

#### What it does
Maps a collection of **basic values** (`String`, `Integer`, enums) or **embeddables** that belong entirely to the entity. The values live in a separate **collection table**, have no ID of their own, and are deleted with the owner.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `targetClass` | `Class` | inferred | Element type (for raw collections). |
| `fetch` | `FetchType` | `LAZY` | When to load the collection. |

#### Example

```java
@Entity
public class Employee {
    @Id @GeneratedValue private Long id;

    @ElementCollection
    @CollectionTable(name = "employee_skills", joinColumns = @JoinColumn(name = "employee_id"))
    @Column(name = "skill")
    private Set<String> skills = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "employee_phones", joinColumns = @JoinColumn(name = "employee_id"))
    private List<Phone> phones = new ArrayList<>();       // @Embeddable Phone(type, number)

    @ElementCollection
    @CollectionTable(name = "employee_roles")
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = EnumSet.noneOf(Role.class);
}
```

#### Common mistakes / best practices
- Changing one element of a `List` element collection makes Hibernate **delete all rows and re-insert** them. Use `Set` (or `@OrderColumn`) for better updates.
- If the elements need their own identity, queries, or relationships, use a separate entity with `@OneToMany`.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @CollectionTable

**Package:** `jakarta.persistence.CollectionTable`
**Applies to:** Field, getter (with `@ElementCollection`)

#### What it does
Customizes the **table** that stores an `@ElementCollection`. Without it, the table is named `<entity>_<field>` and the FK `<entity>_<pk>`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `name` | `String` | `""` | Collection table name. |
| `schema` / `catalog` | `String` | `""` | Schema / catalog. |
| `joinColumns` | `JoinColumn[]` | `{}` | FK column(s) to the owner entity. |
| `foreignKey` | `ForeignKey` | provider default | FK constraint definition. |
| `uniqueConstraints` | `UniqueConstraint[]` | `{}` | Constraints on the table. |
| `indexes` | `Index[]` | `{}` | Indexes on the table. |

#### Example

```java
@ElementCollection
@CollectionTable(
        name = "product_images",
        joinColumns = @JoinColumn(name = "product_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "url"}))
@Column(name = "url", length = 500)
private Set<String> imageUrls = new HashSet<>();

// Map<String, String>: key column + value column
@ElementCollection
@CollectionTable(name = "product_attributes", joinColumns = @JoinColumn(name = "product_id"))
@MapKeyColumn(name = "attr_name")
@Column(name = "attr_value")
private Map<String, String> attributes = new HashMap<>();
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 6.8 Converters & Timestamps

### @Converter

**Package:** `jakarta.persistence.Converter`
**Applies to:** Class (implementing `AttributeConverter<X, Y>`)

#### What it does
Declares an **`AttributeConverter`** that converts a Java type `X` to a database column type `Y` and back. Use it for custom types: enums with stable codes, encrypted strings, JSON, value objects, lists stored as a CSV string...

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `autoApply` | `boolean` | `false` | `true` → applied automatically to **every** field of type `X` in all entities (no `@Convert` needed). |

#### Example

```java
public enum Priority {
    LOW("L"), MEDIUM("M"), HIGH("H");
    private final String code;
    Priority(String code) { this.code = code; }
    public String code() { return code; }
    public static Priority fromCode(String c) {
        return Arrays.stream(values()).filter(p -> p.code.equals(c)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown priority " + c));
    }
}

@Converter(autoApply = true)              // all Priority fields stored as 'L' / 'M' / 'H'
public class PriorityConverter implements AttributeConverter<Priority, String> {
    @Override
    public String convertToDatabaseColumn(Priority p) { return p == null ? null : p.code(); }

    @Override
    public Priority convertToEntityAttribute(String code) { return code == null ? null : Priority.fromCode(code); }
}

@Converter                                 // applied only where @Convert is used
public class StringListConverter implements AttributeConverter<List<String>, String> {
    @Override
    public String convertToDatabaseColumn(List<String> list) {
        return list == null || list.isEmpty() ? null : String.join(",", list);
    }
    @Override
    public List<String> convertToEntityAttribute(String s) {
        return s == null || s.isBlank() ? new ArrayList<>() : new ArrayList<>(List.of(s.split(",")));
    }
}
```

> Converters can be Spring beans (Hibernate 6 + Spring Boot inject dependencies into them), e.g. an encryption converter that needs a key service.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Convert

**Package:** `jakarta.persistence.Convert`
**Applies to:** Field, getter, class

#### What it does
Applies a specific `AttributeConverter` to a field (or disables an auto-applied one).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `converter` | `Class<? extends AttributeConverter>` | `AttributeConverter.class` | The converter to use. |
| `attributeName` | `String` | `""` | Target attribute inside an embeddable/map (`"key"`, `"value"`, `"address.zip"`). |
| `disableConversion` | `boolean` | `false` | `true` → don't apply an `autoApply` converter here. |

#### Example

```java
@Entity
public class Ticket {
    @Id @GeneratedValue private Long id;

    private Priority priority;                         // PriorityConverter applied automatically

    @Convert(converter = StringListConverter.class)
    @Column(name = "labels")
    private List<String> labels = new ArrayList<>();   // stored as "bug,ui,urgent"

    @Convert(disableConversion = true)
    @Enumerated(EnumType.STRING)
    private Priority legacyPriority;                   // stored as 'HIGH' (converter disabled)
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @CreationTimestamp

**Package:** `org.hibernate.annotations.CreationTimestamp` (Hibernate)
**Applies to:** Field (`Instant`, `LocalDateTime`, `OffsetDateTime`, `LocalDate`, `java.util.Date`...)

#### What it does
Hibernate sets the field to the **current timestamp when the entity is first inserted**. Combine with `@Column(updatable = false)` so it never changes.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `source` | `SourceType` | `VM` | (Hibernate 6.0+) `VM` → time from the JVM clock. `DB` → time from the database (`current_timestamp`). |

#### Example

```java
@Entity
public class Comment {
    @Id @GeneratedValue private Long id;
    private String text;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @UpdateTimestamp

**Package:** `org.hibernate.annotations.UpdateTimestamp` (Hibernate)
**Applies to:** Field (same types as `@CreationTimestamp`)

#### What it does
Hibernate sets the field to the **current timestamp on insert and on every update** of the entity.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `source` | `SourceType` | `VM` | `VM` (JVM clock) or `DB` (database clock). |

#### Example

```java
@Entity
public class Comment {
    @Id @GeneratedValue private Long id;
    private String text;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;          // changes whenever text changes
}
```

> Hibernate's timestamps vs Spring Data auditing ([@CreatedDate / @LastModifiedDate](#createddate)): both work. Use Spring Data auditing if you also need **who** made the change (`@CreatedBy`, `@LastModifiedBy`) or want to stay JPA-provider independent.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 6.9 Inheritance

### @Inheritance

**Package:** `jakarta.persistence.Inheritance`
**Applies to:** Class (root entity of a hierarchy)

#### What it does
Maps a **class hierarchy** of entities to tables, enabling **polymorphic queries** (`SELECT p FROM Payment p` returns `CardPayment`s and `UpiPayment`s).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `strategy` | `InheritanceType` | `SINGLE_TABLE` | How the hierarchy is stored. |

| Strategy | Tables | Pros | Cons |
|----------|--------|------|------|
| `SINGLE_TABLE` | One table for all classes + discriminator column | Fastest (no joins) | Subclass columns must be nullable |
| `JOINED` | One table per class, joined by PK | Normalized, NOT NULL possible | Joins on every polymorphic query |
| `TABLE_PER_CLASS` | One full table per concrete class | No joins for concrete queries | Polymorphic queries use `UNION`; no `IDENTITY` IDs |

#### Example

```java
@Entity
@Table(name = "payments")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "payment_type", discriminatorType = DiscriminatorType.STRING, length = 10)
public abstract class Payment {
    @Id @GeneratedValue private Long id;
    private BigDecimal amount;
}

@Entity
@DiscriminatorValue("CARD")
public class CardPayment extends Payment {
    private String cardLast4;
}

@Entity
@DiscriminatorValue("UPI")
public class UpiPayment extends Payment {
    private String upiId;
}

public interface PaymentRepository extends JpaRepository<Payment, Long> { }
paymentRepository.findAll();   // returns CardPayment and UpiPayment objects
```

```
payments: id | amount | payment_type ('CARD'/'UPI') | card_last4 | upi_id
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @DiscriminatorColumn

**Package:** `jakarta.persistence.DiscriminatorColumn`
**Applies to:** Class (root entity)

#### What it does
Defines the column that stores **which subclass** a row belongs to (for `SINGLE_TABLE`, optional for `JOINED`).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `name` | `String` | `"DTYPE"` | Column name. |
| `discriminatorType` | `DiscriminatorType` | `STRING` | `STRING`, `CHAR` or `INTEGER`. |
| `length` | `int` | `31` | Column length for `STRING`. |
| `columnDefinition` | `String` | `""` | SQL fragment for DDL. |

See the [@Inheritance](#inheritance) example.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @DiscriminatorValue

**Package:** `jakarta.persistence.DiscriminatorValue`
**Applies to:** Class (entity in a hierarchy)

#### What it does
The value written to the discriminator column for this entity class. Default: the entity name (e.g. `"CardPayment"`).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | — (required) | Discriminator value, e.g. `"CARD"`. |

See the [@Inheritance](#inheritance) example.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 6.10 Named Queries & Entity Graphs

### @NamedQuery

**Package:** `jakarta.persistence.NamedQuery`
**Applies to:** Class (entity)

#### What it does
Defines a **static JPQL query with a name** on the entity. It is parsed and validated **at startup**, so syntax errors are found early. Spring Data uses a named query automatically when its name is `<Entity>.<methodName>`. It is repeatable (`@NamedQueries` is the container).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `name` | `String` | — (required) | Query name (unique in the persistence unit). |
| `query` | `String` | — (required) | JPQL query. |
| `hints` | `QueryHint[]` | `{}` | Provider hints, e.g. `@QueryHint(name = "org.hibernate.readOnly", value = "true")`. |
| `lockMode` | `LockModeType` | `NONE` | Lock to apply when executed. |

#### Example

```java
@Entity
@NamedQuery(name = "Employee.findActiveByDepartment",
            query = "SELECT e FROM Employee e WHERE e.department.id = :deptId AND e.status = 'ACTIVE'")
public class Employee { ... }

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // Spring Data finds the named query "Employee.findActiveByDepartment"
    List<Employee> findActiveByDepartment(@Param("deptId") Long deptId);
}

// Or with EntityManager
em.createNamedQuery("Employee.findActiveByDepartment", Employee.class)
  .setParameter("deptId", 3L)
  .getResultList();
```

> For native SQL use `@NamedNativeQuery`. In Spring Data, `@Query` on the repository method is usually more readable.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @NamedEntityGraph

**Package:** `jakarta.persistence.NamedEntityGraph`
**Applies to:** Class (entity)

#### What it does
Defines a named **fetch plan**: which associations to load **together** with the entity in one query. Used to avoid N+1 queries without changing the mapping to `EAGER`. Spring Data's [@EntityGraph](#entitygraph) can reference it by name.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `name` | `String` | `""` | Graph name (default: entity name). |
| `attributeNodes` | `NamedAttributeNode[]` | `{}` | Attributes to fetch: `@NamedAttributeNode("department")`, or with a subgraph `@NamedAttributeNode(value = "lines", subgraph = "lines-product")`. |
| `subgraphs` | `NamedSubgraph[]` | `{}` | Nested fetch plans for related entities. |
| `includeAllAttributes` | `boolean` | `false` | Include all attributes of the entity. |
| `subclassSubgraphs` | `NamedSubgraph[]` | `{}` | Subgraphs for subclasses. |

#### Example

```java
@Entity
@Table(name = "orders")
@NamedEntityGraph(
        name = "Order.withLinesAndProducts",
        attributeNodes = {
                @NamedAttributeNode("customer"),
                @NamedAttributeNode(value = "lines", subgraph = "lines")
        },
        subgraphs = @NamedSubgraph(name = "lines", attributeNodes = @NamedAttributeNode("product")))
public class Order { ... }

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph("Order.withLinesAndProducts")
    Optional<Order> findWithDetailsById(Long id);   // one query: order + customer + lines + products
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 6.11 Entity Lifecycle Callbacks

### @PrePersist / @PostPersist / @PreUpdate / @PostUpdate / @PreRemove / @PostRemove / @PostLoad

**Package:** `jakarta.persistence`
**Applies to:** Method in an entity, or in an entity listener class (see [@EntityListeners](#entitylisteners))

#### What they do
Methods called automatically by JPA at specific moments of an entity's life:

| Annotation | Called... |
|------------|-----------|
| `@PrePersist` | before the entity is inserted (`persist`) |
| `@PostPersist` | after the INSERT (generated ID available) |
| `@PreUpdate` | before an UPDATE is flushed (only if the entity changed) |
| `@PostUpdate` | after the UPDATE |
| `@PreRemove` | before the entity is deleted |
| `@PostRemove` | after the DELETE |
| `@PostLoad` | after the entity is loaded from the database |

Callback methods return `void`, take no arguments (in the entity) or the entity (in a listener class), and must not call `EntityManager` operations.

#### Attributes
None.

#### Example

```java
@Entity
public class Customer {
    @Id @GeneratedValue private Long id;
    private String email;
    private String emailNormalized;
    private Instant createdAt;
    private Instant updatedAt;

    @Transient
    private String displayName;

    @PrePersist
    void beforeInsert() {
        createdAt = Instant.now();
        normalize();
    }

    @PreUpdate
    void beforeUpdate() {
        updatedAt = Instant.now();
        normalize();
    }

    @PostLoad
    void afterLoad() {
        displayName = email.substring(0, email.indexOf('@'));
    }

    private void normalize() {
        emailNormalized = email == null ? null : email.trim().toLowerCase();
    }
}
```

> Bulk JPQL updates/deletes (`@Modifying @Query("UPDATE ...")`) **bypass** these callbacks.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 6.12 Useful Hibernate Annotations

These are Hibernate-specific (`org.hibernate.annotations`), not standard JPA, but very common in Spring Boot projects.

### @NaturalId

**Applies to:** Field

#### What it does
Marks a **business key** (ISBN, email, username) that is unique and usually immutable. Hibernate creates a unique constraint and offers efficient, cacheable lookups by it (`session.bySimpleNaturalId(Book.class).load(isbn)`).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `mutable` | `boolean` | `false` | Whether the natural ID may change. |

```java
@Entity
public class Book {
    @Id @GeneratedValue private Long id;

    @NaturalId
    @Column(nullable = false, length = 13)
    private String isbn;
}
```

---

### @Formula

**Applies to:** Field

#### What it does
A **read-only, computed** field whose value comes from an SQL expression evaluated in the SELECT.

#### Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `value` | `String` | Native SQL expression (column names, subqueries). |

```java
@Entity
public class Order {
    @Id @GeneratedValue private Long id;

    @Formula("(SELECT COUNT(*) FROM order_lines l WHERE l.order_id = id)")
    private int lineCount;
}

@Entity
public class OrderLine {
    @Id @GeneratedValue private Long id;
    private BigDecimal unitPrice;
    private int quantity;

    @Formula("unit_price * quantity")
    private BigDecimal total;
}
```

---

### @SQLDelete and @SQLRestriction (soft delete)

**Applies to:** Class (entity), collection field

#### What they do
- `@SQLDelete(sql = "...")` → replaces the DELETE statement Hibernate executes (e.g. with an UPDATE that sets a `deleted` flag).
- `@SQLRestriction("...")` (Hibernate 6.3+, replaces the deprecated `@Where`) → adds an SQL condition to **every** query of the entity/collection.

Together they implement **soft delete**: rows are flagged instead of removed, and flagged rows are hidden. (Hibernate 6.4+ also offers `@SoftDelete`.)

#### Attributes

| Annotation | Attribute | Type | Description |
|------------|-----------|------|-------------|
| `@SQLDelete` | `sql` | `String` | Custom SQL; `?` = the ID (and version if versioned). |
| `@SQLDelete` | `callable` | `boolean` | `true` → `sql` is a stored procedure call. |
| `@SQLRestriction` | `value` | `String` | SQL condition added to the WHERE clause. |

```java
@Entity
@SQLDelete(sql = "UPDATE products SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Product {
    @Id @GeneratedValue private Long id;
    private String name;
    private boolean deleted = false;
}

productRepository.deleteById(5L);   // runs: UPDATE products SET deleted = true WHERE id = 5
productRepository.findAll();        // runs: SELECT ... WHERE deleted = false
```

---

### @BatchSize

**Applies to:** Class (entity), collection field

#### What it does
When lazy associations are loaded for many entities, Hibernate loads them **in batches** using `WHERE id IN (?, ?, ...)` instead of one query per entity. A simple fix for N+1 queries.

#### Attributes

| Attribute | Type | Description |
|-----------|------|-------------|
| `size` | `int` | Number of entities/collections loaded per query. |

```java
@Entity
public class Department {
    @Id @GeneratedValue private Long id;

    @OneToMany(mappedBy = "department")
    @BatchSize(size = 50)
    private List<Employee> employees;
}
```

Global equivalent: `spring.jpa.properties.hibernate.default_batch_fetch_size=50`.

---

### @DynamicUpdate

**Applies to:** Class (entity)

#### What it does
By default Hibernate's UPDATE includes **all columns**. With `@DynamicUpdate`, the UPDATE includes **only the changed columns**. Useful for wide tables or to reduce conflicts when different processes update different columns.

#### Attributes
None (`value` exists but is deprecated).

```java
@Entity
@DynamicUpdate
public class Account {
    @Id @GeneratedValue private Long id;
    private String name;
    private BigDecimal balance;
    private String notes;
}
// account.setNotes("VIP") → UPDATE account SET notes=? WHERE id=?
```

[⬆ Back to Table of Contents](#table-of-contents)

---

# 7. Spring Data JPA

**Spring Data JPA** generates repository implementations from interfaces: you declare `interface EmployeeRepository extends JpaRepository<Employee, Long>` and get CRUD, paging, query methods (`findByEmail`), custom queries and auditing without writing implementation code.

```
JpaRepository<T, ID>
  └── ListCrudRepository / ListPagingAndSortingRepository
        └── CrudRepository / PagingAndSortingRepository
              └── Repository (marker)
```

## 7.1 Repository Configuration

### @EnableJpaRepositories

**Package:** `org.springframework.data.jpa.repository.config.EnableJpaRepositories`
**Applies to:** Class (`@Configuration`)

#### What it does
Activates Spring Data JPA repositories: scans for repository interfaces and creates their implementations. **Spring Boot does this automatically** for the main application package. Add it yourself only to change the scanned packages, or when using **multiple databases** (each with its own `EntityManagerFactory` and transaction manager).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` / `basePackages` | `String[]` | `{}` | Packages to scan for repositories. Default: the annotated class's package. |
| `basePackageClasses` | `Class<?>[]` | `{}` | Type-safe package selection. |
| `includeFilters` / `excludeFilters` | `Filter[]` | `{}` | Include/exclude repository interfaces. |
| `entityManagerFactoryRef` | `String` | `"entityManagerFactory"` | Bean name of the `EntityManagerFactory` these repositories use. |
| `transactionManagerRef` | `String` | `"transactionManager"` | Bean name of the transaction manager. |
| `repositoryImplementationPostfix` | `String` | `"Impl"` | Suffix of custom fragment implementations (`EmployeeRepositoryCustomImpl`). |
| `repositoryBaseClass` | `Class<?>` | `DefaultRepositoryBaseClass.class` | Custom base class for all repositories (replaces `SimpleJpaRepository`). |
| `repositoryFactoryBeanClass` | `Class<?>` | `JpaRepositoryFactoryBean.class` | Custom factory bean. |
| `queryLookupStrategy` | `QueryLookupStrategy.Key` | `CREATE_IF_NOT_FOUND` | How queries are resolved: `CREATE` (from method name), `USE_DECLARED_QUERY` (`@Query`/named), `CREATE_IF_NOT_FOUND` (declared first, then method name). |
| `namedQueriesLocation` | `String` | `""` | Properties file with named queries (default `META-INF/jpa-named-queries.properties`). |
| `considerNestedRepositories` | `boolean` | `false` | Also detect repository interfaces nested in other classes. |
| `enableDefaultTransactions` | `boolean` | `true` | Repository CRUD methods are `@Transactional` by default. |
| `bootstrapMode` | `BootstrapMode` | `DEFAULT` | `DEFAULT` (eager), `LAZY` (created on first use), `DEFERRED` (initialized in background, faster startup). |
| `escapeCharacter` | `char` | `'\\'` | Escape character for `LIKE` in derived queries (`Containing`, `StartingWith`). |

#### Example: two databases

```java
@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.shop.orders.repository",
        entityManagerFactoryRef = "ordersEntityManagerFactory",
        transactionManagerRef = "ordersTransactionManager")
public class OrdersDbConfig {

    @Bean
    @Primary
    @ConfigurationProperties("app.datasource.orders")
    public DataSource ordersDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean ordersEntityManagerFactory(
            EntityManagerFactoryBuilder builder, DataSource ordersDataSource) {
        return builder.dataSource(ordersDataSource)
                .packages("com.example.shop.orders.entity")
                .persistenceUnit("orders")
                .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager ordersTransactionManager(EntityManagerFactory ordersEntityManagerFactory) {
        return new JpaTransactionManager(ordersEntityManagerFactory);
    }
}
// A second ReportingDbConfig does the same for com.example.shop.reporting.* (without @Primary)
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @NoRepositoryBean

**Package:** `org.springframework.data.repository.NoRepositoryBean`
**Applies to:** Interface

#### What it does
Tells Spring Data **not to create a repository bean** for this interface. Used for **intermediate base interfaces** that add shared methods to several repositories. Without it, Spring Data tries to instantiate the base interface and fails (it has no concrete entity type).

#### Attributes
None.

#### Example

```java
@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {

    // Shared methods for all repositories extending this one
    List<T> findByCreatedAtAfter(Instant since);

    default T getOrThrow(ID id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException("Not found: " + id));
    }
}

public interface ProductRepository extends BaseRepository<Product, Long> { }
public interface CustomerRepository extends BaseRepository<Customer, Long> { }
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 7.2 Query Annotations

### @Query

**Package:** `org.springframework.data.jpa.repository.Query`
**Applies to:** Repository method

#### What it does
Declares the query to execute for a repository method, in **JPQL** (default) or **native SQL**. Use it when a derived method name would be too long or the query is too complex (joins, functions, projections).

Parameters are bound by **name** (`:email` with `@Param("email")` or the parameter name) or by **position** (`?1`). JPQL queries can also use SpEL (`:#{#entityName}`).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | The JPQL or SQL query. |
| `nativeQuery` | `boolean` | `false` | `true` → `value` is native SQL. |
| `countQuery` | `String` | `""` | Count query for `Page<T>` results (see [Pagination @Query](#query-with-countquery)). |
| `countProjection` | `String` | `""` | Projection for the derived count query. |
| `name` | `String` | `""` | Named query to use. |
| `countName` | `String` | `""` | Named query used for counting. |
| `queryRewriter` | `Class<? extends QueryRewriter>` | `QueryRewriter.IdentityQueryRewriter.class` | Hook to modify the final query string before execution (Spring Data 3.0+). |

#### Example

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // JPQL with named parameters
    @Query("SELECT e FROM Employee e WHERE e.email = :email")
    Optional<Employee> findByEmailAddress(@Param("email") String email);

    // Positional parameters
    @Query("SELECT e FROM Employee e WHERE e.salary BETWEEN ?1 AND ?2")
    List<Employee> findInSalaryRange(BigDecimal min, BigDecimal max);

    // JOIN FETCH to avoid N+1
    @Query("SELECT e FROM Employee e JOIN FETCH e.department WHERE e.status = :status")
    List<Employee> findWithDepartment(@Param("status") EmployeeStatus status);

    // DTO projection with constructor expression
    @Query("""
           SELECT new com.example.dto.DeptSalary(d.name, COUNT(e), AVG(e.salary))
           FROM Employee e JOIN e.department d
           GROUP BY d.name
           """)
    List<DeptSalary> salaryByDepartment();

    // Native SQL
    @Query(value = "SELECT * FROM employees WHERE joining_date >= CURRENT_DATE - INTERVAL '30 days'",
           nativeQuery = true)
    List<Employee> joinedLastMonth();

    // IN clause with a collection
    @Query("SELECT e FROM Employee e WHERE e.id IN :ids")
    List<Employee> findAllByIds(@Param("ids") Collection<Long> ids);

    // LIKE
    @Query("SELECT e FROM Employee e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Employee> search(@Param("q") String q);
}
```

#### Common mistakes / best practices
- JPQL uses **entity and field names** (`Employee`, `joiningDate`), not table/column names.
- `UPDATE` / `DELETE` queries also need [@Modifying](#modifying).
- Prefer derived methods (`findByEmail`) for simple queries; they're validated at startup and need no string.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Modifying

**Package:** `org.springframework.data.jpa.repository.Modifying`
**Applies to:** Repository method (with `@Query`)

#### What it does
Marks a `@Query` as a **modifying query** (`UPDATE`, `DELETE`, `INSERT` native, DDL), executed with `executeUpdate()`. The method returns `void` or `int`/`Integer` (number of affected rows). It must run inside a **transaction**.

These **bulk** operations go directly to the database: they **bypass** the persistence context, entity lifecycle callbacks, `@Version` checks and cascades.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `flushAutomatically` | `boolean` | `false` | `true` → flush pending entity changes **before** running the query (so the query sees them). |
| `clearAutomatically` | `boolean` | `false` | `true` → clear the persistence context **after** the query, so later reads don't return stale entities. |

#### Example

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Employee e SET e.salary = e.salary * :factor WHERE e.department.id = :deptId")
    int raiseSalaries(@Param("deptId") Long deptId, @Param("factor") BigDecimal factor);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM Employee e WHERE e.status = 'RESIGNED' AND e.updatedAt < :before")
    int purgeResigned(@Param("before") Instant before);

    // Derived delete query (no @Query needed; loads entities and deletes one by one)
    @Transactional
    long deleteByStatus(EmployeeStatus status);
}
```

#### Common mistakes / best practices
- Missing `@Modifying` on an UPDATE `@Query` → `InvalidDataAccessApiUsageException`/"Not supported for DML operations".
- Missing transaction → `TransactionRequiredException`. Put `@Transactional` on the repository method or (better) on the calling service.
- After a bulk update, entities already loaded in the same transaction still have **old values** unless `clearAutomatically = true`.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Param

**Package:** `org.springframework.data.repository.query.Param`
**Applies to:** Repository method parameter

#### What it does
Binds a method parameter to a **named parameter** (`:name`) in a `@Query`. Optional when the project is compiled with `-parameters` (default in Spring Boot), because Spring Data can then read parameter names; still widely used for clarity.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | — (required) | Name of the query parameter (without `:`). |

#### Example

```java
@Query("SELECT o FROM Order o WHERE o.customer.id = :customerId AND o.createdAt >= :from")
List<Order> findRecent(@Param("customerId") Long id, @Param("from") Instant since);

// Parameter object with SpEL
@Query("SELECT e FROM Employee e WHERE e.name = :#{#filter.name} AND e.status = :#{#filter.status}")
List<Employee> findByFilter(@Param("filter") EmployeeFilter filter);
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @EntityGraph

**Package:** `org.springframework.data.jpa.repository.EntityGraph`
**Applies to:** Repository method

#### What it does
Applies a JPA **entity graph** to a repository query, so the listed associations are **fetched in the same query** (left join). The standard way to avoid N+1 queries in Spring Data without writing `JOIN FETCH`. It works with derived queries, `@Query` and `findAll`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Name of a [@NamedEntityGraph](#namedentitygraph) to use. |
| `attributePaths` | `String[]` | `{}` | Ad-hoc graph: attributes to fetch, with dots for nested ones (`"lines.product"`). |
| `type` | `EntityGraphType` | `FETCH` | `FETCH` → listed attributes eager, all others **lazy**. `LOAD` → listed attributes eager, others use their **mapped** fetch type. |

#### Example

```java
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"customer", "lines", "lines.product"})
    Optional<Order> findDetailedById(Long id);

    @EntityGraph(attributePaths = "customer")
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "customer")
    List<Order> findAll();

    @EntityGraph(value = "Order.withLinesAndProducts", type = EntityGraph.EntityGraphType.LOAD)
    List<Order> findByCustomerId(Long customerId);
}
```

#### Common mistakes / best practices
- Fetching a **collection** together with pagination → Hibernate paginates **in memory** (warning `HHH90003004`). Fetch collections only for single-entity lookups, or use `@BatchSize`.
- Fetching two `List` collections at once → `MultipleBagFetchException`; use `Set` or fetch in two queries.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Lock

**Package:** `org.springframework.data.jpa.repository.Lock`
**Applies to:** Repository method

#### What it does
Applies a **database lock** to the query result. `PESSIMISTIC_WRITE` executes `SELECT ... FOR UPDATE`, so other transactions wait until yours commits. Use it for counters, stock reservation, balance updates where concurrent changes must be serialized.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `LockModeType` | — (required) | `PESSIMISTIC_READ` (shared lock), `PESSIMISTIC_WRITE` (exclusive), `PESSIMISTIC_FORCE_INCREMENT`, `OPTIMISTIC`, `OPTIMISTIC_FORCE_INCREMENT`, `READ`, `WRITE`, `NONE`. |

#### Example

```java
public interface StockRepository extends JpaRepository<Stock, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000"))  // ms
    @Query("SELECT s FROM Stock s WHERE s.productId = :productId")
    Optional<Stock> findForUpdate(@Param("productId") Long productId);
}

@Service
public class InventoryService {
    @Transactional
    public void reserve(Long productId, int qty) {
        Stock stock = stockRepository.findForUpdate(productId).orElseThrow();  // row locked
        if (stock.getAvailable() < qty) throw new OutOfStockException(productId);
        stock.setAvailable(stock.getAvailable() - qty);
    }   // commit releases the lock
}
```

> How row/table locks, `FOR UPDATE`, `NOWAIT`/`SKIP LOCKED` and deadlocks work in the database: [Locking Mechanisms](Database_systems.md#5-locking-mechanisms).

#### Common mistakes / best practices
- Locks only last for the **transaction**; without `@Transactional` the lock is released immediately.
- Always lock rows in a **consistent order** to avoid deadlocks, and set a lock timeout.
- Prefer optimistic locking ([@Version](#version)) when conflicts are rare.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @QueryHints

**Package:** `org.springframework.data.jpa.repository.QueryHints`
**Applies to:** Repository method

#### What it does
Passes **JPA/Hibernate query hints** to the query: read-only results, fetch size, timeouts, lock timeouts, query cache.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `QueryHint[]` | `{}` | The hints, each `@QueryHint(name = "...", value = "...")`. |
| `forCounting` | `boolean` | `true` | Whether the hints also apply to the **count** query of a paged method. |

Common hints:

| Hint | Effect |
|------|--------|
| `org.hibernate.readOnly` = `true` | Entities are loaded read-only (no dirty checking, less memory). |
| `org.hibernate.fetchSize` = `500` | JDBC fetch size (rows per round trip) for large results. |
| `jakarta.persistence.query.timeout` = `5000` | Query timeout in ms. |
| `jakarta.persistence.lock.timeout` = `3000` | Lock wait timeout in ms. |
| `org.hibernate.cacheable` = `true` | Use the query cache (if enabled). |

#### Example

```java
@QueryHints({
        @QueryHint(name = "org.hibernate.readOnly", value = "true"),
        @QueryHint(name = "org.hibernate.fetchSize", value = "1000")
})
@Query("SELECT e FROM Employee e WHERE e.status = :status")
Stream<Employee> streamForExport(@Param("status") EmployeeStatus status);   // use inside a transaction, close the stream
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Procedure

**Package:** `org.springframework.data.jpa.repository.query.Procedure`
**Applies to:** Repository method

#### What it does
Calls a **database stored procedure** from a repository method. Parameters are passed as IN parameters; the return value maps to the OUT parameter.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` / `procedureName` | `String` | `""` | Database procedure name. Default: method name. |
| `name` | `String` | `""` | Name of a `@NamedStoredProcedureQuery` defined on the entity. |
| `outputParameterName` | `String` | `""` | OUT parameter to return (when there are several). |
| `refCursor` | `boolean` | `false` | `true` → the procedure returns a REF CURSOR (result set). |

#### Example

```sql
CREATE PROCEDURE calculate_bonus(IN emp_id BIGINT, OUT bonus DECIMAL(10,2)) ...
```

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Procedure(procedureName = "calculate_bonus", outputParameterName = "bonus")
    BigDecimal calculateBonus(@Param("emp_id") Long employeeId);
}
```

> See [store-procedure.md](store-procedure.md) for more stored procedure examples.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 7.3 Auditing

**Auditing** automatically fills **who** created/changed an entity and **when**. Setup needs three pieces:

```
1. @EnableJpaAuditing                      (turn auditing on)
2. @EntityListeners(AuditingEntityListener.class) on the entity / base class
3. @CreatedDate, @LastModifiedDate, @CreatedBy, @LastModifiedBy on fields
   + an AuditorAware<T> bean (for "who")
```

### @EnableJpaAuditing

**Package:** `org.springframework.data.jpa.repository.config.EnableJpaAuditing`
**Applies to:** Class (`@Configuration`)

#### What it does
Activates JPA auditing: registers the infrastructure that `AuditingEntityListener` uses to set audit fields on insert and update. Not auto-configured by Spring Boot; you must add it.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `auditorAwareRef` | `String` | `""` | Bean name of the `AuditorAware` that returns the current user. Needed only if there are several `AuditorAware` beans. |
| `dateTimeProviderRef` | `String` | `""` | Bean name of a `DateTimeProvider` (custom clock, e.g. for tests or time zones). |
| `setDates` | `boolean` | `true` | Whether to set `@CreatedDate` / `@LastModifiedDate`. |
| `modifyOnCreate` | `boolean` | `true` | `true` → `@LastModifiedDate`/`@LastModifiedBy` are also set when the entity is **created**. `false` → they stay `null` until the first update. |

#### Example

```java
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditConfig {

    // Current user from Spring Security
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getName)
                .or(() -> Optional.of("system"));
    }
}
```

> Put `@EnableJpaAuditing` in a **separate `@Configuration`**, not on the main class; otherwise `@WebMvcTest` slices fail with "JPA metamodel must not be empty".

[⬆ Back to Table of Contents](#table-of-contents)

---

### @EntityListeners

**Package:** `jakarta.persistence.EntityListeners`
**Applies to:** Class (entity or `@MappedSuperclass`)

#### What it does
Registers **listener classes** whose lifecycle callback methods (`@PrePersist`, `@PreUpdate`...) are called for this entity. For auditing, register Spring Data's `AuditingEntityListener`. You can also write your own listeners (e.g. publishing events or writing history rows).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `Class<?>[]` | — (required) | Listener classes. |

#### Example

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    @Column(updatable = false, length = 100)
    private String createdBy;

    @LastModifiedBy
    @Column(length = 100)
    private String updatedBy;
    // getters
}

@Entity
public class Invoice extends Auditable {
    @Id @GeneratedValue private Long id;
    private BigDecimal amount;
}

// Custom listener (Spring beans can be injected into JPA listeners in Spring Boot)
public class InvoiceHistoryListener {
    @PostUpdate
    void onUpdate(Invoice invoice) {
        System.out.println("Invoice " + invoice.getId() + " changed");
    }
}

@Entity
@EntityListeners({AuditingEntityListener.class, InvoiceHistoryListener.class})
public class Invoice { ... }
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @CreatedDate

**Package:** `org.springframework.data.annotation.CreatedDate`
**Applies to:** Field, getter

#### What it does
Filled with the current date/time when the entity is **first saved**. Supported types: `Instant`, `LocalDateTime`, `OffsetDateTime`, `ZonedDateTime`, `LocalDate`, `java.util.Date`, `Calendar`, `Long`/`long` (epoch millis).

#### Attributes
None.

#### Example
See [@EntityListeners](#entitylisteners). Add `@Column(updatable = false)` so the value is never changed by updates.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @LastModifiedDate

**Package:** `org.springframework.data.annotation.LastModifiedDate`
**Applies to:** Field, getter

#### What it does
Filled with the current date/time on **every save that changes the entity** (and on creation when `modifyOnCreate = true`, the default). Same supported types as `@CreatedDate`.

#### Attributes
None.

#### Example
See [@EntityListeners](#entitylisteners).

[⬆ Back to Table of Contents](#table-of-contents)

---

### @CreatedBy

**Package:** `org.springframework.data.annotation.CreatedBy`
**Applies to:** Field, getter

#### What it does
Filled with the **current user** (returned by your `AuditorAware<T>` bean) when the entity is first saved. The type must match `T`: a username `String`, a user ID `Long`/`UUID`, or a `User` entity.

#### Attributes
None.

#### Example

```java
// AuditorAware returning the user ID instead of the name
@Bean
public AuditorAware<Long> auditorProvider() {
    return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .map(Authentication::getPrincipal)
            .filter(AppUserDetails.class::isInstance)
            .map(p -> ((AppUserDetails) p).getId());
}

@CreatedBy
@Column(updatable = false)
private Long createdBy;
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @LastModifiedBy

**Package:** `org.springframework.data.annotation.LastModifiedBy`
**Applies to:** Field, getter

#### What it does
Filled with the **current user** from `AuditorAware` on every save that changes the entity.

#### Attributes
None.

#### Example
See [@EntityListeners](#entitylisteners).

#### Common mistakes / best practices (auditing)
- Fields stay `null` → missing `@EnableJpaAuditing` or missing `@EntityListeners(AuditingEntityListener.class)`.
- `@CreatedBy` is `null` → no `AuditorAware` bean, or it returned `Optional.empty()` (e.g. in a scheduled job without a security context). Return a fallback like `"system"`.
- **Bulk** `@Modifying` queries don't trigger auditing; set the fields in the query.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 7.4 Pagination, Sorting & Searching

Loading thousands of rows in one response is slow and wasteful. **Pagination** returns data in small pages (`page=0, size=20`), **sorting** orders it (`sort=salary,desc`), and **searching/filtering** narrows it (`status=ACTIVE&minSalary=50000`). Spring Data JPA supports all three through `Pageable`, `Sort`, `Page`, `Specification` and the JPA Criteria API.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

#### End-to-end flow

```
GET /api/employees?keyword=ma&status=ACTIVE&page=0&size=10&sort=salary,desc
   │
   ▼
Controller ── Spring builds Pageable(page=0, size=10, sort=salary DESC)
   │          and binds search params into EmployeeSearchCriteria
   ▼
Service ───── validates sort fields, builds Specification / Criteria query
   │
   ▼
Repository ── SELECT ... WHERE ... ORDER BY salary DESC LIMIT 10 OFFSET 0
   │          SELECT COUNT(*) ... WHERE ...          (for total pages)
   ▼
Page<Employee> ──► mapped to Page<EmployeeDto> ──► JSON response
```

#### Sample entities used in this section

```java
@Entity
@Table(name = "departments")
public class Department {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    // getters/setters
}

public enum EmployeeStatus { ACTIVE, ON_LEAVE, RESIGNED }

@Entity
@Table(name = "employees")
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private BigDecimal salary;

    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;

    private LocalDate joiningDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
    // getters/setters
}

// Response DTO (never expose entities directly)
public record EmployeeDto(Long id, String name, String email, BigDecimal salary,
                          EmployeeStatus status, LocalDate joiningDate, String departmentName) {

    public static EmployeeDto from(Employee e) {
        return new EmployeeDto(e.getId(), e.getName(), e.getEmail(), e.getSalary(),
                e.getStatus(), e.getJoiningDate(),
                e.getDepartment() != null ? e.getDepartment().getName() : null);
    }
}
```

---

### 7.4.1 Core Pagination Types

These are not annotations, but every pagination example uses them.

| Type | Package | Purpose |
|------|---------|---------|
| `Pageable` | `org.springframework.data.domain` | The **request**: page number, page size, sort. |
| `PageRequest` | `org.springframework.data.domain` | The standard `Pageable` implementation, created with `PageRequest.of(...)`. |
| `Sort` / `Sort.Order` | `org.springframework.data.domain` | Sorting definition: property + direction (+ ignore case / null handling). |
| `Page<T>` | `org.springframework.data.domain` | The **result**: content + total elements + total pages. Runs an extra **COUNT** query. |
| `Slice<T>` | `org.springframework.data.domain` | Lighter result: content + `hasNext()`. **No COUNT** query (fetches `size + 1` rows). |
| `Window<T>` / `ScrollPosition` | `org.springframework.data.domain` | Keyset / offset scrolling (Spring Data 3.1+). See [7.4.9](#749-keyset-scroll-pagination). |

#### Pageable / PageRequest

| Factory method | Example | Meaning |
|----------------|---------|---------|
| `PageRequest.of(page, size)` | `PageRequest.of(0, 20)` | First page, 20 items, unsorted |
| `PageRequest.of(page, size, sort)` | `PageRequest.of(1, 10, Sort.by("name"))` | Second page, sorted by name ASC |
| `PageRequest.of(page, size, direction, props...)` | `PageRequest.of(0, 10, Sort.Direction.DESC, "salary")` | Sorted by salary DESC |
| `Pageable.ofSize(size)` | `Pageable.ofSize(50)` | First page of 50 |
| `Pageable.unpaged()` | `Pageable.unpaged()` | No paging (returns everything); use with care |

Useful `Pageable` methods: `getPageNumber()`, `getPageSize()`, `getOffset()` (= page × size), `getSort()`, `next()`, `previousOrFirst()`, `first()`, `isPaged()`.

> Page numbers are **zero-based**: `page=0` is the first page.

#### Sort

```java
Sort byName        = Sort.by("name");                                // name ASC
Sort bySalaryDesc  = Sort.by(Sort.Direction.DESC, "salary");         // salary DESC
Sort multi         = Sort.by("department.name").ascending()
                         .and(Sort.by("salary").descending());       // nested property + 2 columns
Sort withOrders    = Sort.by(
                         Sort.Order.desc("salary"),
                         Sort.Order.asc("name").ignoreCase());       // case-insensitive name

// Type-safe (no string property names)
Sort typed = Sort.sort(Employee.class).by(Employee::getSalary).descending();

// Sort by an SQL function (JPQL @Query only) - bypasses property validation
Sort byLength = JpaSort.unsafe(Sort.Direction.ASC, "LENGTH(name)");
```

#### Page vs Slice vs List

| Return type | COUNT query | Knows total pages | Best for |
|-------------|-------------|-------------------|----------|
| `Page<T>` | ✅ Yes | ✅ `getTotalElements()`, `getTotalPages()` | Classic page numbers (1, 2, 3 ... 10) |
| `Slice<T>` | ❌ No | ❌ only `hasNext()` | "Load more" / infinite scroll |
| `List<T>` (+ `Pageable`) | ❌ No | ❌ | Just a limited chunk |

Useful `Page` methods: `getContent()`, `getNumber()`, `getSize()`, `getNumberOfElements()`, `getTotalElements()`, `getTotalPages()`, `hasNext()`, `hasPrevious()`, `isFirst()`, `isLast()`, `getSort()`, `map(Function)`.

[⬆ Back to Table of Contents](#table-of-contents)

---

### 7.4.2 Pagination Annotations

#### @PageableDefault

**Package:** `org.springframework.data.web.PageableDefault`
**Applies to:** Controller method parameter of type `Pageable`

##### What it does
Sets the **default page, size and sort** used when the client does **not** send `page`, `size` or `sort` query parameters. Without it, the global default applies (page `0`, size `20`, unsorted).

##### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `int` | `10` | Default page size. Alias for `size`. |
| `size` | `int` | `10` | Default page size. Use `value` or `size`, not both. |
| `page` | `int` | `0` | Default page number (zero-based). |
| `sort` | `String[]` | `{}` | Default sort properties, e.g. `{"name", "joiningDate"}`. |
| `direction` | `Sort.Direction` | `Sort.Direction.ASC` | Direction applied to **all** properties in `sort`. |

> Note: the annotation's own default size is **10**, while the global default without the annotation is **20** (`spring.data.web.pageable.default-page-size`).

##### Example

```java
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    // GET /api/employees              -> page=0, size=10, sort=name ASC
    // GET /api/employees?page=2       -> page=2, size=10, sort=name ASC
    // GET /api/employees?size=50&sort=salary,desc -> client values win
    @GetMapping
    public Page<EmployeeDto> list(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return employeeService.findAll(pageable);
    }

    // Multiple default sort properties, all DESC
    @GetMapping("/recent")
    public Page<EmployeeDto> recent(
            @PageableDefault(value = 5, sort = {"joiningDate", "id"}, direction = Sort.Direction.DESC)
            Pageable pageable) {
        return employeeService.findAll(pageable);
    }
}
```

##### Common mistakes / best practices
- Client parameters always **override** the defaults; `@PageableDefault` never limits the client. Use `max-page-size` (see [7.4.3](#743-pagination-configuration-properties)) to cap the size.
- `direction` applies to every property in `sort`. For mixed directions use [@SortDefault](#sortdefault).

[⬆ Back to Table of Contents](#table-of-contents)

---

#### @SortDefault

**Package:** `org.springframework.data.web.SortDefault`
**Applies to:** Controller method parameter of type `Sort` or `Pageable`

##### What it does
Sets the **default sort** when the client sends no `sort` parameter. Unlike `@PageableDefault.sort`, each `@SortDefault` has its own direction, and multiple `@SortDefault`s can be combined for **mixed directions**. It also works on a plain `Sort` parameter (no paging).

##### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String[]` | `{}` | Properties to sort by. Alias for `sort`. |
| `sort` | `String[]` | `{}` | Same as `value`. |
| `direction` | `Sort.Direction` | `Sort.Direction.ASC` | Direction for the properties in this annotation. |
| `caseSensitive` | `boolean` | `true` | `false` sorts ignoring case (`ORDER BY LOWER(name)`). |

##### Example

```java
// Plain Sort parameter (no paging)
// GET /api/employees/all  -> ORDER BY name ASC
@GetMapping("/all")
public List<EmployeeDto> all(@SortDefault(sort = "name", caseSensitive = false) Sort sort) {
    return employeeService.findAll(sort);
}

// Pageable + sort default together
@GetMapping
public Page<EmployeeDto> list(
        @PageableDefault(size = 20)
        @SortDefault(sort = "salary", direction = Sort.Direction.DESC)
        Pageable pageable) {
    return employeeService.findAll(pageable);
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

#### @SortDefault.SortDefaults

**Package:** `org.springframework.data.web.SortDefault.SortDefaults`
**Applies to:** Controller method parameter of type `Sort` or `Pageable`

##### What it does
A **container** for several `@SortDefault` annotations, giving a default sort with **different directions per property**.

##### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `SortDefault[]` | — (required) | The individual sort defaults, applied in order. |

##### Example

```java
// Default: ORDER BY status ASC, salary DESC, name ASC (ignore case)
@GetMapping
public Page<EmployeeDto> list(
        @SortDefault.SortDefaults({
                @SortDefault(sort = "status", direction = Sort.Direction.ASC),
                @SortDefault(sort = "salary", direction = Sort.Direction.DESC),
                @SortDefault(sort = "name", caseSensitive = false)
        })
        @PageableDefault(size = 20)
        Pageable pageable) {
    return employeeService.findAll(pageable);
}

```

[⬆ Back to Table of Contents](#table-of-contents)

---

#### @Qualifier (for multiple Pageables)

**Package:** `org.springframework.beans.factory.annotation.Qualifier`
**Applies to:** Controller method parameter of type `Pageable` / `Sort`

##### What it does
When one endpoint needs **two independent paginations** (e.g. a dashboard with employees and departments), `@Qualifier` gives each `Pageable` a **prefix**. The request parameters become `{qualifier}_page`, `{qualifier}_size`, `{qualifier}_sort`.

##### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | The prefix for this pageable's parameters. The delimiter is `_` by default (`spring.data.web.pageable.qualifier-delimiter`). |

##### Example

```java
// GET /api/dashboard?emp_page=0&emp_size=5&emp_sort=salary,desc&dept_page=1&dept_size=10
@GetMapping("/api/dashboard")
public Map<String, Object> dashboard(
        @Qualifier("emp")  @PageableDefault(size = 5)  Pageable employeePageable,
        @Qualifier("dept") @PageableDefault(size = 10) Pageable departmentPageable) {

    return Map.of(
            "employees",   employeeService.findAll(employeePageable),
            "departments", departmentService.findAll(departmentPageable));
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

#### @EnableSpringDataWebSupport

**Package:** `org.springframework.data.web.config.EnableSpringDataWebSupport`
**Applies to:** Configuration class

##### What it does
Registers the web integration of Spring Data:
- `PageableHandlerMethodArgumentResolver` and `SortHandlerMethodArgumentResolver` (turn `page`, `size`, `sort` query params into `Pageable` / `Sort`)
- `DomainClassConverter` (lets `@PathVariable("id") Employee employee` load the entity by ID)
- Jackson support for `Page` serialization

**Spring Boot auto-configures this** (`SpringDataWebAutoConfiguration`), so you normally **don't** add it. You only use it to change the **page serialization mode**.

##### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `pageSerializationMode` | `PageSerializationMode` | `DIRECT` | Spring Data 3.3+. `DIRECT` serializes `PageImpl` as-is (unstable JSON, logs a warning). `VIA_DTO` serializes pages as a stable `PagedModel` structure: `{ "content": [...], "page": {...} }`. |

##### Example

```java
@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class WebConfig { }
```

Or, in Spring Boot 3.3+, with a property (no annotation needed):

```properties
spring.data.web.pageable.serialization-mode=via-dto
```

JSON with `VIA_DTO`:
```json
{
  "content": [
    { "id": 7, "name": "Mahendra", "salary": 95000, "status": "ACTIVE" }
  ],
  "page": { "size": 10, "number": 0, "totalElements": 53, "totalPages": 6 }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

#### @Query (with countQuery)

**Package:** `org.springframework.data.jpa.repository.Query`
**Applies to:** Repository method

##### What it does
Defines a custom JPQL or native SQL query on a repository method. When the method takes a `Pageable` and returns `Page<T>`, Spring Data appends `ORDER BY` / `LIMIT` / `OFFSET` and also needs a **COUNT query** for the total. It derives one automatically for simple JPQL; for joins, fetch joins, `GROUP BY` or native queries you should provide `countQuery` yourself.

##### Attributes (pagination-relevant)

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | The JPQL (or SQL if `nativeQuery = true`) query. |
| `countQuery` | `String` | `""` | Query used to count total elements for `Page<T>`. Derived automatically if empty. |
| `countProjection` | `String` | `""` | Projection used in the derived count query, e.g. `"e.id"` → `SELECT COUNT(e.id)`. |
| `nativeQuery` | `boolean` | `false` | `true` → `value` and `countQuery` are native SQL. |
| `name` | `String` | `""` | Name of a named query to use instead of `value`. |
| `countName` | `String` | `""` | Name of a named query to use as the count query. |

##### Example

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // JPQL with fetch join -> explicit countQuery (a COUNT can't contain a fetch join)
    @Query(value = """
                   SELECT e FROM Employee e
                   LEFT JOIN FETCH e.department d
                   WHERE e.status = :status
                   """,
           countQuery = """
                   SELECT COUNT(e) FROM Employee e
                   WHERE e.status = :status
                   """)
    Page<Employee> findByStatusWithDepartment(@Param("status") EmployeeStatus status, Pageable pageable);

    // Native SQL with pagination -> provide countQuery
    @Query(value = "SELECT * FROM employees WHERE salary >= :minSalary",
           countQuery = "SELECT COUNT(*) FROM employees WHERE salary >= :minSalary",
           nativeQuery = true)
    Page<Employee> findRichNative(@Param("minSalary") BigDecimal minSalary, Pageable pageable);

    // Keyword search across two columns
    @Query("""
           SELECT e FROM Employee e
           WHERE LOWER(e.name)  LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
           """)
    Page<Employee> search(@Param("keyword") String keyword, Pageable pageable);
}
```

##### Common mistakes / best practices
- With native queries, the sort property in `Pageable` must be the **column name** (`joining_date`), not the Java field name.
- Fetch-joining a **collection** (`@OneToMany`) with paging makes Hibernate load everything and paginate **in memory** (warning `HHH90003004`). Paginate the IDs first, or use `@EntityGraph` / batch fetching instead.

[⬆ Back to Table of Contents](#table-of-contents)

---

### 7.4.3 Pagination Configuration Properties

Global defaults in `application.properties` (Spring Boot):

| Property | Default | Description |
|----------|---------|-------------|
| `spring.data.web.pageable.default-page-size` | `20` | Page size when the client sends no `size`. |
| `spring.data.web.pageable.max-page-size` | `2000` | Maximum allowed `size`; bigger values are silently reduced to this. |
| `spring.data.web.pageable.one-indexed-parameters` | `false` | `true` → the client's `page=1` means the first page. |
| `spring.data.web.pageable.page-parameter` | `page` | Name of the page query parameter. |
| `spring.data.web.pageable.size-parameter` | `size` | Name of the size query parameter. |
| `spring.data.web.pageable.prefix` | `""` | Prefix for page and size parameters, e.g. `p_` → `p_page`. |
| `spring.data.web.pageable.qualifier-delimiter` | `_` | Delimiter between the `@Qualifier` value and the parameter name. |
| `spring.data.web.pageable.serialization-mode` | `direct` | `via-dto` for stable `Page` JSON (Spring Boot 3.3+). |
| `spring.data.web.sort.sort-parameter` | `sort` | Name of the sort query parameter. |

```properties
spring.data.web.pageable.default-page-size=10
spring.data.web.pageable.max-page-size=100
spring.data.web.pageable.one-indexed-parameters=false
spring.data.web.pageable.serialization-mode=via-dto
```

#### Request parameter format

| Request | Resulting Pageable |
|---------|--------------------|
| `?page=0&size=10` | page 0, 10 items, unsorted |
| `?sort=name` | sort by name ASC |
| `?sort=salary,desc` | sort by salary DESC |
| `?sort=status,asc&sort=salary,desc` | sort by status ASC, then salary DESC |
| `?sort=department.name,asc` | sort by a nested property (join) |
| `?sort=name,asc,ignorecase` | case-insensitive sort |

[⬆ Back to Table of Contents](#table-of-contents)

---

### 7.4.4 Repository Level

#### Repository interfaces

| Interface | Pagination methods it adds |
|-----------|----------------------------|
| `PagingAndSortingRepository<T, ID>` | `findAll(Pageable)`, `findAll(Sort)` |
| `JpaRepository<T, ID>` | Everything above + CRUD + JPA extras (most common choice) |
| `JpaSpecificationExecutor<T>` | `findAll(Specification, Pageable)`, `findAll(Specification, Sort)`, `count(Specification)`, `findBy(Specification, ...)` |

#### Example: all paging styles in one repository

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long>,
                                            JpaSpecificationExecutor<Employee>,
                                            EmployeeRepositoryCustom {          // Criteria API, see 2.8

    // 1. Inherited: findAll(Pageable) -> Page<Employee>

    // 2. Derived query + Page (runs SELECT + COUNT)
    Page<Employee> findByStatus(EmployeeStatus status, Pageable pageable);

    // 3. Derived query + Slice (no COUNT, fetches size+1 to know hasNext)
    Slice<Employee> findByDepartmentId(Long departmentId, Pageable pageable);

    // 4. Derived query + List (just LIMIT/OFFSET)
    List<Employee> findByStatusAndSalaryGreaterThan(EmployeeStatus status, BigDecimal salary, Pageable pageable);

    // 5. Sort only, no paging
    List<Employee> findByStatus(EmployeeStatus status, Sort sort);

    // 6. Static limit + order in the method name
    List<Employee> findTop5ByStatusOrderBySalaryDesc(EmployeeStatus status);

    // 7. Search with containing / ignore case
    Page<Employee> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name, String email, Pageable pageable);

    // 8. Avoid N+1 when mapping department name: load department in the same query
    @EntityGraph(attributePaths = "department")
    Page<Employee> findByJoiningDateBetween(LocalDate from, LocalDate to, Pageable pageable);

    // 9. Projection: fetch only some columns
    Page<EmployeeSummary> findByStatusOrderByName(EmployeeStatus status, Pageable pageable);
}

// Interface-based projection
public interface EmployeeSummary {
    Long getId();
    String getName();
    BigDecimal getSalary();
}
```

#### SQL generated (MySQL / PostgreSQL style)

```sql
-- findByStatus(ACTIVE, PageRequest.of(2, 10, Sort.by("salary").descending()))
select e.* from employees e where e.status = 'ACTIVE' order by e.salary desc limit 10 offset 20;
select count(e.id) from employees e where e.status = 'ACTIVE';
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### 7.4.5 Service Level

The service builds the `Pageable` (when not coming from a controller), **validates sort fields**, calls the repository, and **maps entities to DTOs**.

```java
@Service
@Transactional(readOnly = true)
public class EmployeeService {

    // Only these properties may be used in ?sort=...
    private static final Set<String> SORTABLE_FIELDS =
            Set.of("id", "name", "email", "salary", "status", "joiningDate", "department.name");

    private static final int MAX_PAGE_SIZE = 100;

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // 1. Simple paging, entity -> DTO with Page.map
    public Page<EmployeeDto> findAll(Pageable pageable) {
        return employeeRepository.findAll(safe(pageable)).map(EmployeeDto::from);
    }

    // 2. Building a Pageable manually (e.g. from a scheduled job or another service)
    public Page<EmployeeDto> topEarners(int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Order.desc("salary"), Sort.Order.asc("name")));
        return employeeRepository.findByStatus(EmployeeStatus.ACTIVE, pageable).map(EmployeeDto::from);
    }

    // 3. Slice for "load more"
    public Slice<EmployeeDto> byDepartment(Long departmentId, Pageable pageable) {
        return employeeRepository.findByDepartmentId(departmentId, safe(pageable)).map(EmployeeDto::from);
    }

    // 4. Processing ALL rows page by page (batch jobs) without loading everything in memory
    @Transactional
    public void giveRaise(BigDecimal percent) {
        Pageable pageable = PageRequest.of(0, 500, Sort.by("id"));
        Page<Employee> page;
        do {
            page = employeeRepository.findByStatus(EmployeeStatus.ACTIVE, pageable);
            page.forEach(e -> e.setSalary(e.getSalary().multiply(BigDecimal.ONE.add(percent))));
            employeeRepository.flush();
            pageable = page.nextPageable();
        } while (page.hasNext());
    }

    // Validates sort properties and caps page size
    Pageable safe(Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            if (!SORTABLE_FIELDS.contains(order.getProperty())) {
                throw new IllegalArgumentException("Cannot sort by '" + order.getProperty() + "'");
            }
        }
        int size = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
        return PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
    }
}
```

#### Custom page response DTO (optional)

If you don't want to expose Spring's `Page` JSON format, return your own wrapper:

```java
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        List<String> sort) {

    public static <T> PageResponse<T> of(Page<T> p) {
        return new PageResponse<>(
                p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(),
                p.getTotalPages(), p.isFirst(), p.isLast(),
                p.getSort().stream().map(o -> o.getProperty() + "," + o.getDirection()).toList());
    }
}
```

##### Common mistakes / best practices
- An unknown sort property (`?sort=password`) throws `PropertyReferenceException` → **500** by default. Validate against a whitelist as above, or handle the exception and return 400.
- The batch loop above modifies rows while paging. If the update changes the **filter or sort column**, rows shift between pages; page by a stable key (`id`) or use keyset scrolling ([7.4.9](#749-keyset-scroll-pagination)).
- `Page.map(...)` keeps all paging metadata, so mapping to DTOs costs nothing extra.

[⬆ Back to Table of Contents](#table-of-contents)

---

### 7.4.6 Controller Level

```java
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeSearchService searchService;

    public EmployeeController(EmployeeService employeeService, EmployeeSearchService searchService) {
        this.employeeService = employeeService;
        this.searchService = searchService;
    }

    // GET /api/employees?page=0&size=10&sort=salary,desc
    @GetMapping
    public Page<EmployeeDto> list(@PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return employeeService.findAll(pageable);
    }

    // Custom response wrapper
    // GET /api/employees/paged?page=1&size=5
    @GetMapping("/paged")
    public PageResponse<EmployeeDto> paged(@PageableDefault(size = 10) Pageable pageable) {
        return PageResponse.of(employeeService.findAll(pageable));
    }

    // Manual params instead of Pageable (when you want full control / custom names)
    // GET /api/employees/manual?pageNo=1&pageSize=20&sortBy=salary&sortDir=desc
    @GetMapping("/manual")
    public Page<EmployeeDto> manual(@RequestParam(defaultValue = "0")    @Min(0) int pageNo,
                                    @RequestParam(defaultValue = "20")   @Min(1) @Max(100) int pageSize,
                                    @RequestParam(defaultValue = "name") String sortBy,
                                    @RequestParam(defaultValue = "asc")  String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy);
        return employeeService.findAll(PageRequest.of(pageNo, pageSize, sort));
    }

    // Search + paging (see 2.7 / 2.8)
    // GET /api/employees/search?keyword=ma&status=ACTIVE&minSalary=50000&page=0&size=10&sort=salary,desc
    @GetMapping("/search")
    public Page<EmployeeDto> search(@ModelAttribute EmployeeSearchCriteria criteria,
                                    @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return searchService.search(criteria, pageable);
    }
}
```

Response for `GET /api/employees?page=0&size=2&sort=salary,desc` (with `serialization-mode=via-dto`):

```json
{
  "content": [
    { "id": 12, "name": "Asha",     "salary": 120000, "status": "ACTIVE", "departmentName": "IT" },
    { "id": 7,  "name": "Mahendra", "salary": 95000,  "status": "ACTIVE", "departmentName": "IT" }
  ],
  "page": { "size": 2, "number": 0, "totalElements": 53, "totalPages": 27 }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### 7.4.7 Search Parameters with Specifications

For a search screen with many **optional** filters, writing one repository method per combination is impossible (`findByStatusAndSalaryGreaterThanAndDepartment...`). A **`Specification<T>`** is one reusable `WHERE` condition built with the JPA Criteria API. Specifications are combined with `and` / `or` / `not`, and passed to `findAll(spec, pageable)`. Filters the client didn't send are simply skipped.

```java
@FunctionalInterface
public interface Specification<T> {
    Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb);
}
```

| Criteria API object | Meaning |
|---------------------|---------|
| `Root<T>` | The entity in the `FROM` clause (`FROM Employee e`). `root.get("salary")` → `e.salary`. |
| `CriteriaQuery<?>` | The whole query (select, distinct, order by, group by). |
| `CriteriaBuilder` (`cb`) | Factory for conditions: `cb.equal`, `cb.like`, `cb.between`, `cb.greaterThanOrEqualTo`, `cb.and`, `cb.or`, `cb.lower`, ... |
| `Predicate` | One condition in the `WHERE` clause. |
| `Join<X, Y>` | A join: `root.join("department")`. |

#### Step 1: Search criteria object (bound from query params)

```java
// GET /api/employees/search?keyword=ma&departmentId=2&statuses=ACTIVE,ON_LEAVE
//        &minSalary=50000&maxSalary=150000&joinedFrom=2025-01-01&joinedTo=2026-12-31
public record EmployeeSearchCriteria(
        String keyword,                                   // matches name OR email
        Long departmentId,
        String departmentName,
        List<EmployeeStatus> statuses,                    // IN (...)
        BigDecimal minSalary,
        BigDecimal maxSalary,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate joinedFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate joinedTo
) {}
```

#### Step 2: Reusable specifications

```java
public final class EmployeeSpecifications {

    private EmployeeSpecifications() {}

    // LOWER(name) LIKE %kw% OR LOWER(email) LIKE %kw%
    public static Specification<Employee> keyword(String keyword) {
        return (root, query, cb) -> {
            String pattern = "%" + keyword.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern));
        };
    }

    // department.id = ?
    public static Specification<Employee> departmentId(Long departmentId) {
        return (root, query, cb) -> cb.equal(root.get("department").get("id"), departmentId);
    }

    // JOIN department d ... LOWER(d.name) = ?
    public static Specification<Employee> departmentName(String name) {
        return (root, query, cb) -> {
            Join<Employee, Department> dept = root.join("department", JoinType.INNER);
            return cb.equal(cb.lower(dept.get("name")), name.toLowerCase());
        };
    }

    // status IN (?, ?)
    public static Specification<Employee> statusIn(Collection<EmployeeStatus> statuses) {
        return (root, query, cb) -> root.get("status").in(statuses);
    }

    // salary >= ?
    public static Specification<Employee> salaryAtLeast(BigDecimal min) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("salary"), min);
    }

    // salary <= ?
    public static Specification<Employee> salaryAtMost(BigDecimal max) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("salary"), max);
    }

    // joining_date BETWEEN ? AND ?  (either bound optional)
    public static Specification<Employee> joinedBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            Path<LocalDate> date = root.get("joiningDate");
            if (from != null && to != null) return cb.between(date, from, to);
            if (from != null)               return cb.greaterThanOrEqualTo(date, from);
            return cb.lessThanOrEqualTo(date, to);
        };
    }

    // Fetch department in the same query to avoid N+1,
    // but NOT in the count query (COUNT can't have a fetch join)
    public static Specification<Employee> fetchDepartment() {
        return (root, query, cb) -> {
            if (Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("department", JoinType.LEFT);
            }
            return null;    // no WHERE condition, only the fetch
        };
    }
}
```

#### Step 3: Service combining only the filters that were sent

```java
@Service
@Transactional(readOnly = true)
public class EmployeeSearchService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;   // for sort validation (safe)

    public EmployeeSearchService(EmployeeRepository employeeRepository, EmployeeService employeeService) {
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
    }

    public Page<EmployeeDto> search(EmployeeSearchCriteria c, Pageable pageable) {
        List<Specification<Employee>> specs = new ArrayList<>();
        specs.add(EmployeeSpecifications.fetchDepartment());

        if (StringUtils.hasText(c.keyword()))        specs.add(EmployeeSpecifications.keyword(c.keyword()));
        if (c.departmentId() != null)                specs.add(EmployeeSpecifications.departmentId(c.departmentId()));
        if (StringUtils.hasText(c.departmentName())) specs.add(EmployeeSpecifications.departmentName(c.departmentName()));
        if (c.statuses() != null && !c.statuses().isEmpty())
                                                     specs.add(EmployeeSpecifications.statusIn(c.statuses()));
        if (c.minSalary() != null)                   specs.add(EmployeeSpecifications.salaryAtLeast(c.minSalary()));
        if (c.maxSalary() != null)                   specs.add(EmployeeSpecifications.salaryAtMost(c.maxSalary()));
        if (c.joinedFrom() != null || c.joinedTo() != null)
                                                     specs.add(EmployeeSpecifications.joinedBetween(c.joinedFrom(), c.joinedTo()));

        Specification<Employee> spec = Specification.allOf(specs);   // AND of all filters

        return employeeRepository.findAll(spec, employeeService.safe(pageable))
                                 .map(EmployeeDto::from);
    }
}
```

#### Combining with OR / NOT

```java
// (status = ACTIVE AND salary >= 100000) OR department name = 'Management'
Specification<Employee> spec =
        Specification.allOf(
                EmployeeSpecifications.statusIn(List.of(EmployeeStatus.ACTIVE)),
                EmployeeSpecifications.salaryAtLeast(new BigDecimal("100000")))
        .or(EmployeeSpecifications.departmentName("Management"));

// NOT resigned
Specification<Employee> notResigned =
        Specification.not(EmployeeSpecifications.statusIn(List.of(EmployeeStatus.RESIGNED)));

// Any of several
Specification<Employee> anyMatch = Specification.anyOf(
        EmployeeSpecifications.keyword("ma"),
        EmployeeSpecifications.departmentId(3L));
```

#### Generated SQL

`GET /api/employees/search?keyword=ma&statuses=ACTIVE&minSalary=50000&page=0&size=10&sort=salary,desc`

```sql
select e.*, d.*
from employees e
left join departments d on d.id = e.department_id
where (lower(e.name) like '%ma%' or lower(e.email) like '%ma%')
  and e.status in ('ACTIVE')
  and e.salary >= 50000
order by e.salary desc
limit 10 offset 0;

select count(e.id)
from employees e
where (lower(e.name) like '%ma%' or lower(e.email) like '%ma%')
  and e.status in ('ACTIVE')
  and e.salary >= 50000;
```

#### Type-safe attribute names (optional)
String names like `"salary"` fail only at runtime when misspelled. Adding the Hibernate metamodel generator (`org.hibernate.orm:hibernate-jpamodelgen`, renamed `hibernate-processor` in Hibernate 7) as an annotation processor generates `Employee_` classes, so you can write `root.get(Employee_.salary)` and get compile-time checks.

##### Common mistakes / best practices
- `LIKE '%keyword%'` can't use a normal index; on large tables consider full-text search.
- Escape `%` and `_` in user keywords if they should be matched literally (`cb.like(expr, pattern, '\\')`).
- Joining a **collection** in a specification can duplicate rows; call `query.distinct(true)`.

[⬆ Back to Table of Contents](#table-of-contents)

---

### 7.4.8 JPA Criteria API Query (EntityManager)

`Specification` is a thin layer over the **JPA Criteria API**. Use the Criteria API directly when you need things a `Specification` can't express easily:
- **Selecting a DTO** or only a few columns (`cb.construct(...)`)
- **Aggregations** (`GROUP BY`, `SUM`, `AVG`) with paging
- Full control over the **count query**

Spring Data lets you add such methods to a repository through a **custom fragment**: an interface plus a class named `<InterfaceName>Impl`.

#### Step 1: Custom repository interface

```java
public interface EmployeeRepositoryCustom {
    Page<EmployeeDto> searchWithCriteria(EmployeeSearchCriteria criteria, Pageable pageable);
}
```

#### Step 2: Implementation with CriteriaBuilder

```java
@Repository
public class EmployeeRepositoryCustomImpl implements EmployeeRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<EmployeeDto> searchWithCriteria(EmployeeSearchCriteria c, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // ---------- 1. Data query: SELECT new EmployeeDto(...) ----------
        CriteriaQuery<EmployeeDto> query = cb.createQuery(EmployeeDto.class);
        Root<Employee> root = query.from(Employee.class);
        Join<Employee, Department> dept = root.join("department", JoinType.LEFT);

        // Select only the needed columns directly into the DTO (record constructor)
        query.select(cb.construct(EmployeeDto.class,
                root.get("id"),
                root.get("name"),
                root.get("email"),
                root.get("salary"),
                root.get("status"),
                root.get("joiningDate"),
                dept.get("name")));

        query.where(buildPredicates(c, cb, root, dept).toArray(Predicate[]::new));

        // ORDER BY from Pageable's Sort
        query.orderBy(toOrders(pageable.getSort(), cb, root, dept));

        TypedQuery<EmployeeDto> typedQuery = em.createQuery(query);
        if (pageable.isPaged()) {
            typedQuery.setFirstResult((int) pageable.getOffset());    // OFFSET
            typedQuery.setMaxResults(pageable.getPageSize());         // LIMIT
        }
        List<EmployeeDto> content = typedQuery.getResultList();

        // ---------- 2. Count query: SELECT COUNT(e) ----------
        // Predicates are tied to their Root, so the count query needs its own Root + predicates
        Supplier<Long> total = () -> {
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<Employee> countRoot = countQuery.from(Employee.class);
            Join<Employee, Department> countDept = countRoot.join("department", JoinType.LEFT);
            countQuery.select(cb.count(countRoot))
                      .where(buildPredicates(c, cb, countRoot, countDept).toArray(Predicate[]::new));
            return em.createQuery(countQuery).getSingleResult();
        };

        // Skips the count query when it can be computed (e.g. last page has fewer rows than size)
        return PageableExecutionUtils.getPage(content, pageable, total::get);
    }

    // Shared WHERE conditions (used by both data and count query)
    private List<Predicate> buildPredicates(EmployeeSearchCriteria c, CriteriaBuilder cb,
                                            Root<Employee> root, Join<Employee, Department> dept) {
        List<Predicate> predicates = new ArrayList<>();

        if (StringUtils.hasText(c.keyword())) {
            String pattern = "%" + c.keyword().trim().toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern)));
        }
        if (c.departmentId() != null) {
            predicates.add(cb.equal(dept.get("id"), c.departmentId()));
        }
        if (StringUtils.hasText(c.departmentName())) {
            predicates.add(cb.equal(cb.lower(dept.get("name")), c.departmentName().toLowerCase()));
        }
        if (c.statuses() != null && !c.statuses().isEmpty()) {
            predicates.add(root.get("status").in(c.statuses()));
        }
        if (c.minSalary() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("salary"), c.minSalary()));
        }
        if (c.maxSalary() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("salary"), c.maxSalary()));
        }
        if (c.joinedFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("joiningDate"), c.joinedFrom()));
        }
        if (c.joinedTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("joiningDate"), c.joinedTo()));
        }
        return predicates;
    }

    // Convert Spring Sort -> JPA Order list (supports "department.name")
    private List<Order> toOrders(Sort sort, CriteriaBuilder cb,
                                 Root<Employee> root, Join<Employee, Department> dept) {
        List<Order> orders = new ArrayList<>();
        for (Sort.Order o : sort) {
            Expression<?> path = o.getProperty().equals("department.name")
                    ? dept.get("name")
                    : root.get(o.getProperty());
            if (o.isIgnoreCase()) {
                path = cb.lower(path.as(String.class));
            }
            orders.add(o.isAscending() ? cb.asc(path) : cb.desc(path));
        }
        return orders;
    }
}
```

> Shortcut: `QueryUtils.toOrders(sort, root, cb)` (`org.springframework.data.jpa.repository.query.QueryUtils`) converts a `Sort` to JPA orders for simple and nested properties, if you don't need custom handling.

#### Step 3: Use it (the main repository already extends `EmployeeRepositoryCustom`)

```java
// Service
public Page<EmployeeDto> searchFast(EmployeeSearchCriteria criteria, Pageable pageable) {
    return employeeRepository.searchWithCriteria(criteria, employeeService.safe(pageable));
}

// Controller
// GET /api/employees/search-fast?departmentName=IT&minSalary=60000&page=0&size=20&sort=department.name&sort=salary,desc
@GetMapping("/search-fast")
public Page<EmployeeDto> searchFast(@ModelAttribute EmployeeSearchCriteria criteria,
                                    @PageableDefault(size = 20) Pageable pageable) {
    return searchService.searchFast(criteria, pageable);
}
```

#### Example: Aggregation with paging (salary report per department)

```java
public record DepartmentSalaryReport(String department, Long employees, BigDecimal totalSalary, Double avgSalary) {}

public Page<DepartmentSalaryReport> salaryReport(EmployeeStatus status, Pageable pageable) {
    CriteriaBuilder cb = em.getCriteriaBuilder();

    CriteriaQuery<DepartmentSalaryReport> q = cb.createQuery(DepartmentSalaryReport.class);
    Root<Employee> e = q.from(Employee.class);
    Join<Employee, Department> d = e.join("department");

    q.select(cb.construct(DepartmentSalaryReport.class,
                    d.get("name"),
                    cb.count(e),
                    cb.sum(e.<BigDecimal>get("salary")),
                    cb.avg(e.<BigDecimal>get("salary"))))
     .where(cb.equal(e.get("status"), status))
     .groupBy(d.get("name"))
     .having(cb.gt(cb.count(e), 0))
     .orderBy(cb.desc(cb.sum(e.<BigDecimal>get("salary"))));

    List<DepartmentSalaryReport> content = em.createQuery(q)
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getPageSize())
            .getResultList();

    // Count = number of groups (distinct departments)
    CriteriaQuery<Long> countQ = cb.createQuery(Long.class);
    Root<Employee> ce = countQ.from(Employee.class);
    countQ.select(cb.countDistinct(ce.get("department")))
          .where(cb.equal(ce.get("status"), status));

    return PageableExecutionUtils.getPage(content, pageable,
            () -> em.createQuery(countQ).getSingleResult());
}
```

#### Specification vs Criteria API vs @Query

| | Derived / `@Query` | `Specification` | Criteria API (`EntityManager`) |
|-|--------------------|-----------------|--------------------------------|
| Dynamic optional filters | ❌ hard | ✅ | ✅ |
| Paging + count | ✅ automatic | ✅ automatic | Manual (two queries) |
| DTO / few columns | ✅ (projections) | ⚠️ limited (`findBy(spec, q -> q.as(...))`) | ✅ `cb.construct` |
| GROUP BY / aggregates | ✅ (static) | ❌ | ✅ |
| Code size | Smallest | Medium | Largest |
| Use when | Fixed queries | Search screens | Reports, complex dynamic queries |

[⬆ Back to Table of Contents](#table-of-contents)

---

### 7.4.9 Keyset (Scroll) Pagination

Offset paging (`LIMIT 10 OFFSET 100000`) gets slower on deep pages because the DB still reads and skips all earlier rows. **Keyset pagination** continues from the **last seen sort key** (`WHERE id > :lastId`) and stays fast. Spring Data 3.1+ supports it with `Window<T>` and `ScrollPosition`. (SQL details: [Keyset Pagination](Database_systems.md#keyset-pagination-vs-offset).)

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Sort must be stable/unique -> order by id (or salary + id)
    Window<Employee> findFirst20ByStatusOrderByIdAsc(EmployeeStatus status, ScrollPosition position);
}

@Service
public class EmployeeExportService {

    // Read all active employees in chunks of 20
    public void exportAll() {
        WindowIterator<Employee> employees = WindowIterator
                .of(position -> employeeRepository.findFirst20ByStatusOrderByIdAsc(EmployeeStatus.ACTIVE, position))
                .startingAt(ScrollPosition.keyset());

        employees.forEachRemaining(this::writeToCsv);
    }

    // API style: client sends back the last seen id
    public Window<Employee> nextChunk(Long lastId) {
        ScrollPosition position = lastId == null
                ? ScrollPosition.keyset()
                : ScrollPosition.forward(Map.of("id", lastId));
        return employeeRepository.findFirst20ByStatusOrderByIdAsc(EmployeeStatus.ACTIVE, position);
    }
}
```

| | Offset (`Pageable`) | Keyset (`ScrollPosition`) |
|-|---------------------|---------------------------|
| Jump to page N | ✅ | ❌ (only next / previous) |
| Speed on deep pages | Slows down | Constant |
| Stable when rows are inserted/deleted | ❌ rows can shift | ✅ |
| Best for | Admin tables with page numbers | Infinite scroll, exports, batch jobs |

[⬆ Back to Table of Contents](#table-of-contents)

---

### 7.4.10 Pagination Best Practices

- **Always sort** paginated queries by a deterministic order (add `id` as the last sort key). Without `ORDER BY`, the database may return rows in a different order on each page → duplicates/missing rows.
- **Cap page size** (`max-page-size` and/or service check) so clients can't request `size=1000000`.
- **Whitelist sort fields**; never pass arbitrary client input to sort native queries.
- Return **DTOs** (`Page.map`) instead of entities; avoids lazy-loading errors and leaking fields.
- Use **`Slice`** when total count isn't needed; `COUNT(*)` on large filtered tables is expensive.
- Avoid **N+1**: use `@EntityGraph`, a fetch in the specification (skipped for count), or DTO projections.
- Never fetch-join **collections** with paging; paginate parent IDs first, then load children.
- Prefer **keyset scrolling** for exports, batch jobs and infinite scroll.
- Add **database indexes** on filter and sort columns (`status`, `salary`, `joining_date`, `department_id`).
- Set `spring.data.web.pageable.serialization-mode=via-dto` (or return your own `PageResponse`) for a stable JSON contract.

[⬆ Back to Table of Contents](#table-of-contents)

---

### 7.4.11 Pagination Quick Reference

| Item | Type | Purpose | Key attributes / methods |
|------|------|---------|--------------------------|
| `@PageableDefault` | Annotation | Default page/size/sort for a `Pageable` param | `size`/`value`, `page`, `sort`, `direction` |
| `@SortDefault` | Annotation | Default sort (per-property direction) | `sort`/`value`, `direction`, `caseSensitive` |
| `@SortDefault.SortDefaults` | Annotation | Container for several `@SortDefault` | `value` |
| `@Qualifier` | Annotation | Prefix for multiple `Pageable` params | `value` |
| `@EnableSpringDataWebSupport` | Annotation | Web support (auto-configured by Boot) | `pageSerializationMode` |
| `@Query` | Annotation | Custom query with paging | `value`, `countQuery`, `countProjection`, `nativeQuery` |
| `@EntityGraph` | Annotation | Fetch associations with the page query | `attributePaths` |
| `Pageable` / `PageRequest` | Class | Page request | `of(page, size, sort)`, `getOffset()`, `next()` |
| `Sort` | Class | Sorting | `by(...)`, `ascending()`, `descending()`, `and(...)` |
| `Page<T>` | Interface | Page + total count | `getContent()`, `getTotalElements()`, `getTotalPages()`, `map()` |
| `Slice<T>` | Interface | Page without count | `getContent()`, `hasNext()` |
| `Specification<T>` | Interface | Dynamic WHERE conditions | `allOf`, `anyOf`, `and`, `or`, `not` |
| `JpaSpecificationExecutor<T>` | Interface | Run specifications | `findAll(spec, pageable)`, `count(spec)` |
| `CriteriaBuilder` | JPA | Build Criteria queries | `equal`, `like`, `between`, `construct`, `count` |
| `PageableExecutionUtils` | Class | Build a `Page` from content + lazy count | `getPage(content, pageable, countSupplier)` |
| `Window<T>` / `ScrollPosition` | Class | Keyset scrolling | `ScrollPosition.keyset()`, `forward(...)`, `WindowIterator` |

[⬆ Back to Table of Contents](#table-of-contents)

---

# 8. Transaction Management

> The full `@Transactional` guide (commit timing, scenario diagrams, propagation, isolation, rollback rules, pitfalls) is in a separate file: **[java-spring-transactional.md](java-spring-transactional.md)**. This section gives a summary and covers `@EnableTransactionManagement`. For isolation levels and locks inside the database, see [Database Systems → Isolation Levels](Database_systems.md#4-transaction-isolation-levels) and [Locking Mechanisms](Database_systems.md#5-locking-mechanisms).

## 8.1 Transaction Annotations

### @Transactional

**Package:** `org.springframework.transaction.annotation.Transactional`
**Applies to:** Class, method (also interface)

#### What it does
Runs the method inside a **database transaction**: Spring's proxy begins a transaction before the method, **commits** when it returns normally, and **rolls back** when it throws a `RuntimeException` or `Error` (checked exceptions commit by default).

#### Attributes

| Attribute | Type | Default | Description | Details |
|-----------|------|---------|-------------|---------|
| `value` / `transactionManager` | `String` | `""` | Transaction manager bean to use. | [→](java-spring-transactional.md#value--transactionmanager) |
| `propagation` | `Propagation` | `REQUIRED` | Join an existing transaction or start a new one (`REQUIRES_NEW`, `NESTED`, `SUPPORTS`, `MANDATORY`, `NOT_SUPPORTED`, `NEVER`). | [→](java-spring-transactional.md#propagation-deep-dive) |
| `isolation` | `Isolation` | `DEFAULT` | Isolation level (`READ_COMMITTED`, `REPEATABLE_READ`, `SERIALIZABLE`...). | [→](java-spring-transactional.md#isolation-deep-dive) |
| `readOnly` | `boolean` | `false` | Optimization hint for read-only work. | [→](java-spring-transactional.md#readonly) |
| `timeout` / `timeoutString` | `int` / `String` | `-1` / `""` | Timeout in seconds. | [→](java-spring-transactional.md#timeout--timeoutstring) |
| `rollbackFor` / `rollbackForClassName` | `Class[]` / `String[]` | `{}` | Extra exceptions that cause rollback. | [→](java-spring-transactional.md#rollbackfor--rollbackforclassname) |
| `noRollbackFor` / `noRollbackForClassName` | `Class[]` / `String[]` | `{}` | Exceptions that must not cause rollback. | [→](java-spring-transactional.md#norollbackfor--norollbackforclassname) |
| `label` | `String[]` | `{}` | Descriptive labels. | [→](java-spring-transactional.md#label) |

#### Example

```java
@Service
@Transactional(readOnly = true)                 // default for the class: read-only
public class TransferService {

    private final AccountRepository accounts;
    private final AuditService audit;

    public TransferService(AccountRepository accounts, AuditService audit) {
        this.accounts = accounts;
        this.audit = audit;
    }

    public BigDecimal balance(Long accountId) {
        return accounts.findById(accountId).orElseThrow().getBalance();
    }

    @Transactional(rollbackFor = InsufficientFundsException.class, timeout = 10)
    public void transfer(Long fromId, Long toId, BigDecimal amount) throws InsufficientFundsException {
        Account from = accounts.findById(fromId).orElseThrow();
        Account to   = accounts.findById(toId).orElseThrow();
        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(fromId);     // rollback (listed in rollbackFor)
        }
        from.debit(amount);
        to.credit(amount);
        audit.record("TRANSFER", fromId, toId, amount);       // REQUIRES_NEW inside AuditService
    }   // both updates commit together, or neither does
}
```

#### Common mistakes / best practices
- Calling a `@Transactional` method from the **same class** (`this.method()`) skips the proxy: no transaction.
- `private` methods are never transactional.
- Catching an exception inside the method without re-throwing → the transaction **commits**.
- See the full list in [Common Pitfalls](java-spring-transactional.md#common-pitfalls).

[⬆ Back to Table of Contents](#table-of-contents)

---

### @EnableTransactionManagement

**Package:** `org.springframework.transaction.annotation.EnableTransactionManagement`
**Applies to:** Class (`@Configuration`)

#### What it does
Enables Spring's **annotation-driven transaction management**: registers the infrastructure (`TransactionInterceptor` + proxy creator) that makes `@Transactional` work.

**Spring Boot enables it automatically** (`TransactionAutoConfiguration`) when a `PlatformTransactionManager` is present, so you normally don't add it. Add it to change the proxy mode/order, or (Spring 6.2+) the default rollback rule.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `proxyTargetClass` | `boolean` | `false` | `true` → CGLIB class proxies instead of JDK interface proxies. (Spring Boot already uses class proxies: `spring.aop.proxy-target-class=true`.) |
| `mode` | `AdviceMode` | `PROXY` | `PROXY` (Spring AOP proxies; self-invocation not intercepted) or `ASPECTJ` (bytecode weaving; also intercepts self-invocation and private methods, needs AspectJ weaving setup). |
| `order` | `int` | `Ordered.LOWEST_PRECEDENCE` | Order of the transaction advice relative to other advice (e.g. run a retry aspect **outside** the transaction). |
| `rollbackOn` | `RollbackOn` | `RUNTIME_EXCEPTIONS` | (Spring 6.2+) Default rollback rule: `RUNTIME_EXCEPTIONS` or `ALL_EXCEPTIONS` (checked exceptions roll back too). |

#### Example

```java
@Configuration
@EnableTransactionManagement(order = Ordered.LOWEST_PRECEDENCE - 10,
                             rollbackOn = RollbackOn.ALL_EXCEPTIONS)
public class TxConfig {

    // Only needed if Boot doesn't auto-configure one (e.g. multiple data sources)
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new JdbcTransactionManager(dataSource);
    }
}
```

Related Spring Boot properties:

```properties
spring.transaction.default-timeout=30s
spring.transaction.rollback-on-commit-failure=true
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 8.2 Detailed Transaction Guide

The separate file covers these topics in depth:

| Topic | Link |
|-------|------|
| How Spring manages transactions (proxy flow) | [Open](java-spring-transactional.md#how-spring-manages-transactions) |
| When a transaction commits + scenario walkthroughs | [Open](java-spring-transactional.md#transaction-commit-timing) |
| Propagation (all 7 types with diagrams) | [Open](java-spring-transactional.md#propagation-deep-dive) |
| Isolation levels and read anomalies | [Open](java-spring-transactional.md#isolation-deep-dive) |
| Where to put `@Transactional` | [Open](java-spring-transactional.md#where-to-put-transactional) |
| Programmatic transactions (`TransactionTemplate`) | [Open](java-spring-transactional.md#programmatic-transactions-transactiontemplate) |
| `@TransactionalEventListener` | [Open](java-spring-transactional.md#transactionaleventlistener) |
| `@Transactional` in tests | [Open](java-spring-transactional.md#transactional-in-tests) |
| Common pitfalls | [Open](java-spring-transactional.md#common-pitfalls) |
| Best practices & quick reference | [Open](java-spring-transactional.md#best-practices) |

[⬆ Back to Table of Contents](#table-of-contents)

---

# 9. Spring Validation

**Bean Validation** checks that data (request bodies, form input, method parameters, configuration) follows declared rules before your business logic uses it. Spring Boot uses **Hibernate Validator**, the reference implementation of **Jakarta Bean Validation**.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

Two kinds of annotations work together:
- **Triggers** (`@Valid`, `@Validated`) say **when** to validate.
- **Constraints** (`@NotBlank`, `@Size`, `@Email`...) say **what** is valid.

## 9.1 Triggering Validation

### @Valid

**Package:** `jakarta.validation.Valid` (Jakarta Bean Validation, **not** Spring)
**Applies to:** Method parameter, Field, Method return value, Type argument (`List<@Valid T>`)

#### What it does
Triggers **Bean Validation** (Hibernate Validator) on an object. The constraints declared on the object's fields (`@NotNull`, `@NotBlank`, `@Email`, `@Size`, `@Min`, ...) are checked.

- On a **controller parameter** (`@RequestBody`, `@ModelAttribute`, `@RequestPart`) → validated before the method runs.
- On a **field** of another object → **cascaded (nested) validation** of that field.

Requires `spring-boot-starter-validation`.

#### Attributes
`@Valid` has **no attributes**. It does **not** support validation groups; use [@Validated](#validated) for that.

#### Common constraint annotations (used together with @Valid)

| Constraint | Meaning |
|------------|---------|
| `@NotNull` | Not `null` |
| `@NotEmpty` | Not `null` and not empty (String/Collection/Map/array) |
| `@NotBlank` | Not `null` and contains at least one non-whitespace character (String) |
| `@Size(min, max)` | Length / size within range |
| `@Min(value)` / `@Max(value)` | Numeric bounds |
| `@Positive` / `@PositiveOrZero` / `@Negative` | Sign checks |
| `@Email` | Valid email format |
| `@Pattern(regexp)` | Matches a regex |
| `@Past` / `@Future` / `@PastOrPresent` | Date checks |
| `@DecimalMin` / `@DecimalMax` / `@Digits` | Decimal checks |

Every constraint accepts a `message` attribute, e.g. `@NotBlank(message = "Name is required")`.

#### Example

```java
public class AddressDto {
    @NotBlank private String city;
    @Pattern(regexp = "\\d{6}", message = "PIN code must be 6 digits")
    private String pinCode;
    // getters/setters
}

public class CreateUserDto {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50)
    private String name;

    @NotBlank @Email
    private String email;

    @Min(18) @Max(120)
    private int age;

    @Valid                        // cascade: validate the nested object
    @NotNull
    private AddressDto address;

    private List<@Valid PhoneDto> phones;   // validate each element
    // getters/setters
}

@RestController
@RequestMapping("/api/users")
public class UserController {

    // Invalid body -> MethodArgumentNotValidException -> 400
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody CreateUserDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(dto));
    }

    // Handle errors locally with BindingResult (must directly follow the validated param)
    @PostMapping("/check")
    public ResponseEntity<?> check(@Valid @RequestBody CreateUserDto dto, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField,
                                              FieldError::getDefaultMessage,
                                              (a, b) -> a));
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok("Valid");
    }
}
```

#### Custom constraint

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
public @interface UniqueEmail {
    String message() default "Email already registered";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    private final UserRepository repo;
    public UniqueEmailValidator(UserRepository repo) { this.repo = repo; }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext ctx) {
        return email == null || !repo.existsByEmail(email);
    }
}
```

#### Common mistakes / best practices
- Missing `spring-boot-starter-validation` → constraints are **silently ignored**.
- Forgetting `@Valid` on a nested field → nested object is **not** validated.
- Handle `MethodArgumentNotValidException` globally (see [@RestControllerAdvice](#restcontrolleradvice)).

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Validated

**Package:** `org.springframework.validation.annotation.Validated` (Spring)
**Applies to:** Class, Method, Method parameter

#### What it does
Spring's variant of `@Valid` with two extra capabilities:

1. **Validation groups**: validate only the constraints belonging to specific groups (e.g. different rules for *create* vs *update*).
2. **Method-level validation** (on a class): when placed on a **class** (`@Service`, `@Component`, `@RestController`), Spring creates a proxy (`MethodValidationPostProcessor`) that validates method **parameters and return values**, including simple types like `@RequestParam @Min(1) int page`.

> **Spring Framework 6.1+ note:** controllers get **built-in method validation**: constraints placed directly on `@RequestParam`, `@PathVariable`, `@RequestHeader` parameters are applied without `@Validated` on the class, and violations raise `HandlerMethodValidationException` (400). On older versions you need `@Validated` on the controller class, and violations raise `ConstraintViolationException`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `Class<?>[]` | `{}` | Validation **groups** to apply. Empty → the `jakarta.validation.groups.Default` group (constraints without an explicit `groups`). |

#### Example 1: Validation groups (create vs update)

```java
// Marker interfaces for groups
public interface OnCreate {}
public interface OnUpdate {}

public class UserDto {
    @Null(groups = OnCreate.class, message = "id must not be sent on create")
    @NotNull(groups = OnUpdate.class, message = "id is required on update")
    private Long id;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class})
    private String name;

    @NotBlank(groups = OnCreate.class)        // email required only on create
    @Email(groups = {OnCreate.class, OnUpdate.class})
    private String email;
    // getters/setters
}

@RestController
@RequestMapping("/api/users")
public class UserController {

    @PostMapping
    public User create(@Validated(OnCreate.class) @RequestBody UserDto dto) { ... }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id,
                       @Validated(OnUpdate.class) @RequestBody UserDto dto) { ... }
}
```

#### Example 2: Validating simple parameters

```java
@RestController
@RequestMapping("/api/products")
@Validated            // required on Spring < 6.1; harmless on 6.1+
public class ProductController {

    // GET /api/products?page=-1  -> 400
    @GetMapping
    public List<Product> list(@RequestParam(defaultValue = "0") @Min(0) int page,
                              @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) { ... }

    // GET /api/products/ab -> 400
    @GetMapping("/{code}")
    public Product byCode(@PathVariable @Pattern(regexp = "[A-Z]{3}-\\d{4}") String code) { ... }
}
```

#### Example 3: Method validation in a service

```java
@Service
@Validated
public class PaymentService {

    public Receipt pay(@NotNull @Positive BigDecimal amount,
                       @NotBlank String accountId) { ... }

    // Validate the return value too
    public @NotNull Receipt findReceipt(@NotBlank String id) { ... }
}
// Calling pay(BigDecimal.valueOf(-5), "") -> ConstraintViolationException
```

#### Example 4: Validating configuration properties

```java
@ConfigurationProperties(prefix = "app.mail")
@Validated
public record MailProperties(@NotBlank String host, @Min(1) @Max(65535) int port) {}
// Application fails to start if app.mail.host is missing
```

#### @Valid vs @Validated

| | `@Valid` | `@Validated` |
|-|----------|--------------|
| Origin | Jakarta Bean Validation (standard) | Spring |
| Validation groups | ❌ | ✅ |
| Nested/cascade validation on fields | ✅ | ❌ (use `@Valid` on the field) |
| Class-level method validation (proxy) | ❌ | ✅ |
| Typical use | `@RequestBody`, nested fields | Groups, services, `@ConfigurationProperties` |

#### Common mistakes / best practices
- `@Validated` on a class works through a **proxy**: calling a validated method from **inside the same class** (self-invocation) skips validation.
- Use `@Valid` on **nested fields** even when the top-level uses `@Validated`.
- With groups, constraints **without** a `groups` attribute belong to `Default` and are **skipped**, unless your group interface extends `Default`.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 9.2 Constraint Basics

All constraint annotations below are from **Jakarta Bean Validation** (`jakarta.validation.constraints`), implemented by **Hibernate Validator** (`spring-boot-starter-validation`). They declare rules on fields, getters, parameters or return values; the rules are checked when [@Valid](#valid) or [@Validated](#validated) triggers validation.

### Attributes shared by every constraint

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `message` | `String` | `"{jakarta.validation.constraints.<Name>.message}"` | Error message. Can be literal text, a message key `{user.name.required}` (resolved from `ValidationMessages.properties` / `messages.properties`), and can use the constraint's attributes: `"must be at least {min} characters"`, or the value: `"${validatedValue} is not valid"`. |
| `groups` | `Class<?>[]` | `{}` | Validation groups this constraint belongs to (see [@Validated](#validated)). Empty → `Default` group. |
| `payload` | `Class<? extends Payload>[]` | `{}` | Extra metadata for clients of the API, e.g. a severity level. Rarely used. |

> **Null handling:** except `@NotNull`, `@NotEmpty`, `@NotBlank` (and `@Null`), every constraint treats **`null` as valid**. To require a value *and* a format, combine them: `@NotNull @Email`.

> Every constraint is **repeatable** with a nested `List` annotation (e.g. `@Size.List`), useful for different rules per group.

### Sample DTO used in this section

```java
public record RegisterRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 20)
        @Pattern(regexp = "^[a-z0-9_]+$", message = "Only lowercase letters, digits and _")
        String username,

        @NotBlank @Email
        String email,

        @NotNull @Size(min = 8, max = 64)
        String password,

        @NotNull @Past
        LocalDate birthDate,

        @Min(0) @Max(10)
        Integer experienceYears,

        @NotNull @DecimalMin(value = "0.00", inclusive = false) @Digits(integer = 8, fraction = 2)
        BigDecimal expectedSalary,

        @NotEmpty
        List<@NotBlank String> skills,

        @AssertTrue(message = "You must accept the terms")
        boolean termsAccepted
) {}

@PostMapping("/register")
public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) { ... }
```

Error response from the global handler (see [@RestControllerAdvice](#restcontrolleradvice)):
```json
{
  "status": 400,
  "message": "Validation failed",
  "fieldErrors": {
    "username": "Only lowercase letters, digits and _",
    "email": "must be a well-formed email address",
    "termsAccepted": "You must accept the terms"
  }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 9.3 Null & Empty Checks

### @NotNull

**Applies to:** Any type

#### What it does
The value must **not be `null`**. Empty strings and empty collections are allowed.

#### Attributes
Only the shared ones (`message`, `groups`, `payload`).

#### Example

```java
@NotNull private Long departmentId;          // null → error
@NotNull private String note;                // "" is valid, "   " is valid
@NotNull private List<String> tags;          // empty list is valid
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @NotEmpty

**Applies to:** `CharSequence`, `Collection`, `Map`, arrays

#### What it does
The value must not be `null` **and** not be empty (length/size > 0). A string of spaces is **allowed**.

#### Attributes
Only the shared ones.

#### Example

```java
@NotEmpty private String code;               // null, "" → error;  "  " → valid
@NotEmpty private List<Long> productIds;     // null, [] → error
@NotEmpty private Map<String, String> attrs; // null, {} → error
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @NotBlank

**Applies to:** `CharSequence` (String)

#### What it does
The string must not be `null` and must contain **at least one non-whitespace character**. The usual choice for required text fields.

#### Attributes
Only the shared ones.

#### Example

```java
@NotBlank(message = "Name is required")
private String name;                         // null, "", "   " → error;  " a " → valid
```

#### @NotNull vs @NotEmpty vs @NotBlank

| Value | `@NotNull` | `@NotEmpty` | `@NotBlank` |
|-------|:----------:|:-----------:|:-----------:|
| `null` | ❌ | ❌ | ❌ |
| `""` | ✅ | ❌ | ❌ |
| `"   "` | ✅ | ✅ | ❌ |
| `"abc"` | ✅ | ✅ | ✅ |

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Null

**Applies to:** Any type

#### What it does
The value **must be `null`**. Mostly used with **groups**: e.g. the `id` must be null when creating, but required when updating.

#### Attributes
Only the shared ones.

#### Example

```java
public record ProductDto(
        @Null(groups = OnCreate.class) @NotNull(groups = OnUpdate.class) Long id,
        @NotBlank String name) { }
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 9.4 Size & Number Constraints

### @Size

**Applies to:** `CharSequence`, `Collection`, `Map`, arrays

#### What it does
The **length** (string) or **size** (collection, map, array) must be between `min` and `max` (inclusive). `null` is valid.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `min` | `int` | `0` | Minimum length/size. |
| `max` | `int` | `Integer.MAX_VALUE` | Maximum length/size. |

#### Example

```java
@Size(min = 3, max = 50, message = "Name must be {min}-{max} characters")
private String name;

@Size(max = 5, message = "At most {max} tags")
private List<String> tags;

@NotNull @Size(min = 1)             // required and at least one element
private Set<Long> roleIds;
```

> Use `@Size` for strings/collections, and `@Min`/`@Max` for numbers.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Min

**Applies to:** `BigDecimal`, `BigInteger`, `byte`, `short`, `int`, `long` and their wrappers (also numeric `CharSequence`)

#### What it does
The number must be **greater than or equal to** `value`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `long` | — (required) | Minimum allowed value (inclusive). |

#### Example

```java
@Min(value = 18, message = "Must be at least {value} years old")
private int age;

@Min(1)
private Integer quantity;              // null is valid; add @NotNull if required
```

> `double` and `float` are **not supported** because of rounding errors; use `@DecimalMin` with `BigDecimal`.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Max

**Applies to:** Same types as `@Min`

#### What it does
The number must be **less than or equal to** `value`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `long` | — (required) | Maximum allowed value (inclusive). |

#### Example

```java
@Min(1) @Max(100)
private int pageSize;

@Max(value = 5, message = "Rating must be at most {value}")
private Integer rating;
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @DecimalMin

**Applies to:** `BigDecimal`, `BigInteger`, `CharSequence`, `byte`, `short`, `int`, `long` and wrappers

#### What it does
The number must be **greater than (or equal to)** a decimal value given as a **String**, so exact decimal limits are possible (`"0.01"`).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | — (required) | Minimum value as a `BigDecimal` string, e.g. `"0.01"`. |
| `inclusive` | `boolean` | `true` | `true` → `>=`. `false` → strictly `>`. |

#### Example

```java
@DecimalMin(value = "0.00", inclusive = false, message = "Price must be greater than 0")
private BigDecimal price;              // 0.00 → error, 0.01 → valid

@DecimalMin("0.5")
private BigDecimal discountFactor;     // 0.5 → valid
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @DecimalMax

**Applies to:** Same types as `@DecimalMin`

#### What it does
The number must be **less than (or equal to)** a decimal value given as a String.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | — (required) | Maximum value, e.g. `"99999.99"`. |
| `inclusive` | `boolean` | `true` | `true` → `<=`. `false` → strictly `<`. |

#### Example

```java
@DecimalMin("0.00") @DecimalMax(value = "100.00", message = "Percentage must be 0-100")
private BigDecimal discountPercent;

@DecimalMax(value = "1.0", inclusive = false)
private BigDecimal probability;        // must be < 1.0
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Digits

**Applies to:** `BigDecimal`, `BigInteger`, `CharSequence`, `byte`, `short`, `int`, `long` and wrappers

#### What it does
Limits the number of **digits before and after the decimal point**. Matches a database `DECIMAL(precision, scale)` column so values always fit.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `integer` | `int` | — (required) | Maximum digits in the integer part. |
| `fraction` | `int` | — (required) | Maximum digits in the fraction part. |

#### Example

```java
// Fits DECIMAL(10,2): up to 8 integer digits, 2 decimals
@Digits(integer = 8, fraction = 2, message = "Max 8 digits and 2 decimals")
private BigDecimal amount;             // 12345678.99 → valid, 123.456 → error

@Digits(integer = 6, fraction = 0)
private String pinCode;                // numeric string with up to 6 digits
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Positive / @PositiveOrZero / @Negative / @NegativeOrZero

**Applies to:** `BigDecimal`, `BigInteger`, `byte`, `short`, `int`, `long`, `float`, `double` and wrappers (also numeric `CharSequence`)

#### What they do

| Annotation | Valid values |
|------------|--------------|
| `@Positive` | `> 0` |
| `@PositiveOrZero` | `>= 0` |
| `@Negative` | `< 0` |
| `@NegativeOrZero` | `<= 0` |

Unlike `@Min`/`@Max`, these also support `double` and `float`.

#### Attributes
Only the shared ones.

#### Example

```java
public record StockAdjustment(
        @Positive Long productId,          // IDs start at 1
        @PositiveOrZero int newQuantity,   // 0 allowed (out of stock)
        @NegativeOrZero BigDecimal writeOffAmount,   // losses recorded as negative or 0
        @Negative Integer temperatureOffset) { }
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 9.5 String Format Constraints

### @Email

**Applies to:** `CharSequence`

#### What it does
The string must be a **well-formed email address**. The default check is lenient (`a@b` is accepted, since local domains are valid). Add `regexp` for stricter rules. `null` and `""` are valid; combine with `@NotBlank` if required.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `regexp` | `String` | `".*"` | Additional regex the email must also match. |
| `flags` | `Pattern.Flag[]` | `{}` | Regex flags, e.g. `Pattern.Flag.CASE_INSENSITIVE`. |

#### Example

```java
@NotBlank @Email
private String email;

// Require a dot in the domain and restrict to the company domain
@Email(regexp = "^[\\w.+-]+@example\\.com$",
       flags = Pattern.Flag.CASE_INSENSITIVE,
       message = "Use your company email")
private String workEmail;
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Pattern

**Applies to:** `CharSequence`

#### What it does
The string must **fully match** a regular expression. `null` is valid.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `regexp` | `String` | — (required) | Java regular expression (the whole string must match). |
| `flags` | `Pattern.Flag[]` | `{}` | `CASE_INSENSITIVE`, `MULTILINE`, `DOTALL`, `UNICODE_CASE`, `CANON_EQ`, `UNIX_LINES`, `COMMENTS`. |

#### Example

```java
@Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian mobile number")
private String mobile;

@Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]$", message = "Invalid PAN")
private String pan;

@Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^\\w\\s]).{8,}$",
         message = "Password needs upper, lower, digit and symbol")
private String password;

@Pattern(regexp = "^[a-z0-9-]+$", flags = Pattern.Flag.CASE_INSENSITIVE)
private String slug;
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 9.6 Date & Time Constraints

**Applies to:** `java.time` types (`LocalDate`, `LocalDateTime`, `Instant`, `OffsetDateTime`, `ZonedDateTime`, `Year`, `YearMonth`, `MonthDay`, `LocalTime`...), `java.util.Date`, `Calendar`.

"Now" comes from the validator's `ClockProvider` (system clock by default). In tests you can configure a fixed clock.

### @Past

#### What it does
The date/time must be **strictly in the past**.

#### Attributes
Only the shared ones.

```java
@NotNull @Past(message = "Birth date must be in the past")
private LocalDate birthDate;
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @PastOrPresent

#### What it does
The date/time must be **in the past or now** (today, for `LocalDate`).

#### Attributes
Only the shared ones.

```java
@PastOrPresent
private LocalDate joiningDate;          // today is allowed
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Future

#### What it does
The date/time must be **strictly in the future**.

#### Attributes
Only the shared ones.

```java
@NotNull @Future(message = "Delivery date must be after today")
private LocalDate deliveryDate;
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @FutureOrPresent

#### What it does
The date/time must be **now or in the future**.

#### Attributes
Only the shared ones.

```java
public record BookingRequest(
        @NotNull @FutureOrPresent LocalDate checkIn,      // today allowed
        @NotNull @Future LocalDate checkOut) { }
```

> Cross-field rules like "checkOut after checkIn" need a class-level check, see [@AssertTrue](#asserttrue--assertfalse).

[⬆ Back to Table of Contents](#table-of-contents)

---

## 9.7 Boolean & Extra Constraints

### @AssertTrue / @AssertFalse

**Applies to:** `boolean`, `Boolean` (field or **getter**)

#### What they do
- `@AssertTrue` → the value must be `true`.
- `@AssertFalse` → the value must be `false`.

Placed on a **getter** that computes a boolean, they are a simple way to write **cross-field validation**.

#### Attributes
Only the shared ones.

#### Example

```java
public class BookingForm {
    @NotNull @FutureOrPresent private LocalDate checkIn;
    @NotNull private LocalDate checkOut;
    @NotBlank private String password;
    @NotBlank private String confirmPassword;

    @AssertTrue(message = "You must accept the terms")
    private boolean termsAccepted;

    @AssertFalse(message = "Blocked users cannot book")
    private boolean blocked;

    // Cross-field rules: the getter name becomes the property in error messages
    @AssertTrue(message = "Check-out must be after check-in")
    public boolean isDateRangeValid() {
        return checkIn == null || checkOut == null || checkOut.isAfter(checkIn);
    }

    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmPassword);
    }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### Hibernate Validator extras: @Length, @Range, @URL, @UUID

**Package:** `org.hibernate.validator.constraints` (not standard Jakarta, but included with `spring-boot-starter-validation`)

| Annotation | Applies to | Attributes | Meaning |
|------------|------------|------------|---------|
| `@Length` | `CharSequence` | `min` (0), `max` (`Integer.MAX_VALUE`) | String length in range (like `@Size`, strings only). |
| `@Range` | Numbers, numeric strings | `min` (0), `max` (`Long.MAX_VALUE`) | Value in range (like `@Min` + `@Max`). |
| `@URL` | `CharSequence` | `protocol`, `host`, `port`, `regexp`, `flags` | Valid URL, optionally with a specific protocol/host/port. |
| `@UUID` | `CharSequence` | `allowEmpty`, `allowNil`, `version`, `variant`, `letterCase` | Valid UUID string (Hibernate Validator 8+). |

```java
@Length(min = 2, max = 30)
private String city;

@Range(min = 1, max = 5)
private int rating;

@URL(protocol = "https")
private String website;

@UUID
private String externalId;
```

> For your own rules (e.g. "email not already registered"), write a **custom constraint**; see the example under [@Valid](#valid).

[⬆ Back to Table of Contents](#table-of-contents)

---

# 10. Spring Security

Spring Security adds **authentication** (who are you?) and **authorization** (what may you do?) to the application. Authorization happens at two levels:
- **URL level**: rules in the `SecurityFilterChain` (`/admin/**` needs `ADMIN`).
- **Method level**: annotations on service/controller methods (`@PreAuthorize`), covered here.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

> **Roles vs authorities:** an *authority* is any granted permission string (`"orders:write"`). A *role* is an authority with the prefix `ROLE_` (`"ROLE_ADMIN"`). `hasRole('ADMIN')` checks for `ROLE_ADMIN`; `hasAuthority('ROLE_ADMIN')` checks the exact string.

## 10.1 Security Configuration

### @EnableWebSecurity

**Package:** `org.springframework.security.config.annotation.web.configuration.EnableWebSecurity`
**Applies to:** Class (`@Configuration`)

#### What it does
Enables Spring Security's **web security** support and lets you define `SecurityFilterChain` beans that configure URL authorization, login, CSRF, CORS, sessions, JWT... Spring Boot applies it automatically when Spring Security is on the classpath, but it's conventional to add it on your security configuration class.

> Since Spring Security 5.7 / 6, configure security with a **`SecurityFilterChain` bean**. The old `WebSecurityConfigurerAdapter` class is **removed**.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `debug` | `boolean` | `false` | `true` → logs detailed security debug info for every request (filter chain, matched rules). **Never in production.** |

#### Example: REST API with JWT

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())                          // stateless REST API
            .cors(Customizer.withDefaults())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/actuator/health").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();   // bcrypt by default
    }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @EnableMethodSecurity

**Package:** `org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity`
**Applies to:** Class (`@Configuration`)

#### What it does
Enables **method-level security** annotations (`@PreAuthorize`, `@PostAuthorize`, `@PreFilter`, `@PostFilter`, and optionally `@Secured`, `@RolesAllowed`). Spring creates proxies around beans with these annotations and checks the rules before/after each call.

> Replaces the deprecated `@EnableGlobalMethodSecurity` (Spring Security 5.6+). Note the different defaults: `prePostEnabled` is now `true` by default.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `prePostEnabled` | `boolean` | `true` | Enables `@PreAuthorize`, `@PostAuthorize`, `@PreFilter`, `@PostFilter`. |
| `securedEnabled` | `boolean` | `false` | Enables Spring's `@Secured`. |
| `jsr250Enabled` | `boolean` | `false` | Enables Jakarta `@RolesAllowed`, `@PermitAll`, `@DenyAll`. |
| `proxyTargetClass` | `boolean` | `false` | Use CGLIB class proxies. |
| `mode` | `AdviceMode` | `PROXY` | `PROXY` or `ASPECTJ` (weaving). |

#### Example

```java
@Configuration
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class MethodSecurityConfig { }
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 10.2 Method Authorization

### @PreAuthorize

**Package:** `org.springframework.security.access.prepost.PreAuthorize`
**Applies to:** Method, class (applies to all methods), interface

#### What it does
Evaluates a **SpEL expression before** the method runs. If it's `false`, the method is not executed and an `AccessDeniedException` is thrown (→ **403 Forbidden**). The most flexible and most used method-security annotation: it can check roles, authorities, method arguments, and call your own beans.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | — (required) | SpEL expression returning a boolean. |

**Common expressions:**

| Expression | Meaning |
|------------|---------|
| `hasRole('ADMIN')` | Has `ROLE_ADMIN` |
| `hasAnyRole('ADMIN', 'MANAGER')` | Has any of these roles |
| `hasAuthority('orders:write')` | Has this exact authority |
| `hasAnyAuthority('a', 'b')` | Has any of these authorities |
| `isAuthenticated()` / `isAnonymous()` | Logged in / not logged in |
| `permitAll()` / `denyAll()` | Always allow / deny |
| `authentication.name` | Current username |
| `principal` | Current principal object (e.g. your `UserDetails`) |
| `#paramName` | A method argument (by parameter name) |
| `@beanName.method(...)` | Call a Spring bean |

#### Example

```java
@Service
public class OrderService {

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteOrder(Long id) { ... }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT') or hasAuthority('orders:read')")
    public List<Order> allOrders() { ... }

    // Users can only read their own orders, admins can read all
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    public List<Order> ordersOf(String username) { ... }

    // Access the argument's property
    @PreAuthorize("#order.customerId == principal.id")
    public void update(Order order) { ... }

    // Delegate complex rules to a bean
    @PreAuthorize("@orderSecurity.canCancel(#orderId, authentication)")
    public void cancel(Long orderId) { ... }
}

@Component("orderSecurity")
public class OrderSecurity {
    public boolean canCancel(Long orderId, Authentication auth) {
        Order o = orderRepository.findById(orderId).orElseThrow();
        return o.getOwner().equals(auth.getName()) && o.getStatus() == OrderStatus.NEW;
    }
}
```

**Custom meta-annotation** (reuse rules, avoid typos):

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ADMIN')")
public @interface IsAdmin { }

@IsAdmin
public void resetAllPasswords() { ... }
```

#### Common mistakes / best practices
- Missing `@EnableMethodSecurity` → annotations are **silently ignored**.
- Self-invocation (`this.deleteOrder()`) bypasses the security proxy, like `@Transactional`.
- `#username` needs parameter names at runtime (`-parameters`, default in Spring Boot).
- Handle `AccessDeniedException` → 403, and don't map it to 500 in a catch-all handler.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @PostAuthorize

**Package:** `org.springframework.security.access.prepost.PostAuthorize`
**Applies to:** Method, class

#### What it does
Evaluates the expression **after** the method returns, with the result available as **`returnObject`**. If `false`, `AccessDeniedException` is thrown and the result is not returned. Useful when the decision depends on the loaded data (e.g. the owner of a record).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | — (required) | SpEL expression; can use `returnObject`. |

#### Example

```java
@PostAuthorize("hasRole('ADMIN') or returnObject.owner == authentication.name")
public Document getDocument(Long id) {
    return documentRepository.findById(id).orElseThrow();
}
```

> The method **has already run**. Don't use `@PostAuthorize` on methods with side effects (updates, emails); the side effect happens even if access is denied (unless a transaction rolls it back).

[⬆ Back to Table of Contents](#table-of-contents)

---

### @PreFilter

**Package:** `org.springframework.security.access.prepost.PreFilter`
**Applies to:** Method (with a `Collection`/array/`Map`/`Stream` parameter)

#### What it does
**Removes elements** from a collection **argument** before the method runs, keeping only those for which the expression is `true`. Each element is available as **`filterObject`**.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | — (required) | SpEL expression evaluated for each element (`filterObject`). |
| `filterTarget` | `String` | `""` | Name of the parameter to filter. Required if the method has **more than one** collection parameter. |

#### Example

```java
// Users may only delete their own documents; others are silently dropped from the list
@PreFilter("filterObject.owner == authentication.name")
public void deleteAll(List<Document> documents) {
    documentRepository.deleteAll(documents);
}

@PreFilter(value = "filterObject.startsWith('public-')", filterTarget = "keys")
public void publish(List<String> keys, String channel) { ... }
```

> The collection must be **mutable** (`new ArrayList<>(...)`), since elements are removed from it.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @PostFilter

**Package:** `org.springframework.security.access.prepost.PostFilter`
**Applies to:** Method (returning a `Collection`/array/`Map`/`Stream`)

#### What it does
**Removes elements** from the **returned** collection, keeping only those for which the expression is `true` (`filterObject`).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | — (required) | SpEL expression evaluated for each element. |

#### Example

```java
@PostFilter("hasRole('ADMIN') or filterObject.owner == authentication.name")
public List<Document> findAll() {
    return documentRepository.findAll();      // returned list contains only allowed documents
}
```

> Filtering happens **in memory after loading everything**, and breaks pagination. For large data, filter in the **query** instead (e.g. `findByOwner(username)`).

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Secured

**Package:** `org.springframework.security.access.annotation.Secured`
**Applies to:** Method, class

#### What it does
Spring's older, simpler annotation: the user must have **at least one of the listed authorities/roles**. No SpEL. Requires `@EnableMethodSecurity(securedEnabled = true)`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String[]` | — (required) | Authorities; roles must include the `ROLE_` prefix (`"ROLE_ADMIN"`). |

#### Example

```java
@Secured("ROLE_ADMIN")
public void purgeCache() { ... }

@Secured({"ROLE_ADMIN", "ROLE_MANAGER"})      // either role
public Report salesReport() { ... }
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @RolesAllowed

**Package:** `jakarta.annotation.security.RolesAllowed` (JSR-250)
**Applies to:** Method, class

#### What it does
The standard Jakarta equivalent of `@Secured`: the user must have **one of the listed roles**. Spring adds the `ROLE_` prefix automatically, so you write `"ADMIN"`. Requires `@EnableMethodSecurity(jsr250Enabled = true)`. Related: `@PermitAll`, `@DenyAll`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String[]` | — (required) | Role names, without `ROLE_`. |

#### Example

```java
@RolesAllowed("ADMIN")
public void deleteUser(Long id) { ... }

@RolesAllowed({"ADMIN", "HR"})
public List<Salary> salaries() { ... }

@PermitAll
public List<Product> publicCatalog() { ... }
```

#### Comparison

| | `@PreAuthorize` | `@Secured` | `@RolesAllowed` |
|-|-----------------|------------|-----------------|
| Origin | Spring | Spring | Jakarta (JSR-250) |
| SpEL / arguments | ✅ | ❌ | ❌ |
| Role prefix | `hasRole('ADMIN')` | `"ROLE_ADMIN"` | `"ADMIN"` |
| Enabled by | default | `securedEnabled = true` | `jsr250Enabled = true` |

[⬆ Back to Table of Contents](#table-of-contents)

---

## 10.3 Accessing the Current User

### @AuthenticationPrincipal

**Package:** `org.springframework.security.core.annotation.AuthenticationPrincipal`
**Applies to:** Controller method parameter

#### What it does
Injects the **currently authenticated principal** (`Authentication.getPrincipal()`) into a controller method: your `UserDetails` implementation for form/basic login, or a `Jwt` for JWT resource servers.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `expression` | `String` | `""` | SpEL evaluated on the principal, to inject one property, e.g. `"username"` or `"claims['email']"`. |
| `errorOnInvalidType` | `boolean` | `false` | `true` → throw `ClassCastException` if the principal isn't of the parameter type. `false` → inject `null`. |

#### Example

```java
@RestController
@RequestMapping("/api/me")
public class MeController {

    // Your UserDetails implementation
    @GetMapping
    public ProfileDto me(@AuthenticationPrincipal AppUserDetails user) {
        return new ProfileDto(user.getId(), user.getUsername());
    }

    // Only one property
    @GetMapping("/name")
    public String name(@AuthenticationPrincipal(expression = "username") String username) {
        return username;
    }

    // JWT resource server
    @GetMapping("/claims")
    public Map<String, Object> claims(@AuthenticationPrincipal Jwt jwt) {
        return Map.of("sub", jwt.getSubject(), "email", jwt.getClaimAsString("email"));
    }
}

// Custom annotation to hide the details
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal
public @interface CurrentUser { }

@GetMapping("/orders")
public List<OrderDto> myOrders(@CurrentUser AppUserDetails user) { ... }
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @CurrentSecurityContext

**Package:** `org.springframework.security.core.annotation.CurrentSecurityContext`
**Applies to:** Controller method parameter

#### What it does
Injects the whole **`SecurityContext`**, or a value computed from it with SpEL (e.g. the `Authentication`, its authorities, or the name). Use it when you need more than the principal.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `expression` | `String` | `""` | SpEL evaluated on the `SecurityContext`, e.g. `"authentication"`, `"authentication.name"`. |
| `errorOnInvalidType` | `boolean` | `false` | Throw instead of injecting `null` when the type doesn't match. |

#### Example

```java
@GetMapping("/whoami")
public String whoami(@CurrentSecurityContext(expression = "authentication.name") String name) {
    return name;
}

@GetMapping("/authorities")
public Collection<? extends GrantedAuthority> authorities(
        @CurrentSecurityContext(expression = "authentication") Authentication auth) {
    return auth.getAuthorities();
}

// Outside controllers (services), read it directly:
String user = SecurityContextHolder.getContext().getAuthentication().getName();
```

[⬆ Back to Table of Contents](#table-of-contents)

---

# 11. Spring AOP

**AOP (Aspect-Oriented Programming)** lets you add behavior (logging, timing, auditing, retries, security) **around existing methods** without changing them. Spring AOP works with **proxies**: only calls from **another bean** to a **public/protected** method of a **Spring bean** are intercepted.

| Term | Meaning |
|------|---------|
| **Aspect** | A class containing cross-cutting logic (`@Aspect`) |
| **Join point** | A method execution that can be intercepted |
| **Pointcut** | An expression selecting join points (`execution(* com.example.service.*.*(..))`) |
| **Advice** | The code run at a join point (`@Before`, `@Around`...) |

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

### Pointcut expression cheat sheet

| Expression | Matches |
|------------|---------|
| `execution(* com.example.service.*.*(..))` | Any method of any class in `service` package |
| `execution(* com.example..*Service.*(..))` | Any method of classes ending in `Service`, any sub-package |
| `execution(public * *(..))` | Any public method |
| `execution(* save*(..))` | Methods starting with `save` |
| `execution(* *(Long, ..))` | Methods whose first parameter is `Long` |
| `within(com.example.web..*)` | Any method in `web` and sub-packages |
| `@annotation(com.example.Loggable)` | Methods annotated with `@Loggable` |
| `@within(org.springframework.stereotype.Service)` | Methods of classes annotated with `@Service` |
| `bean(*Controller)` | Beans whose name ends with `Controller` (Spring-only) |
| `args(id, ..)` | Methods whose first argument binds to advice parameter `id` |

Combine with `&&`, `||`, `!`.

## 11.1 Aspect Basics

### @EnableAspectJAutoProxy

**Package:** `org.springframework.context.annotation.EnableAspectJAutoProxy`
**Applies to:** Class (`@Configuration`)

#### What it does
Enables processing of `@Aspect` beans: Spring creates proxies for beans matched by the aspects' pointcuts. **Spring Boot enables it automatically** with `spring-boot-starter-aop`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `proxyTargetClass` | `boolean` | `false` | `true` → CGLIB class proxies. (Boot: `spring.aop.proxy-target-class=true` by default.) |
| `exposeProxy` | `boolean` | `false` | `true` → the proxy is available via `AopContext.currentProxy()`, a workaround for self-invocation. |

#### Example

```java
@Configuration
@EnableAspectJAutoProxy(exposeProxy = true)
public class AopConfig { }

// Self-invocation through the proxy (so advice runs)
((OrderService) AopContext.currentProxy()).placeOrder(request);
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Aspect

**Package:** `org.aspectj.lang.annotation.Aspect`
**Applies to:** Class

#### What it does
Declares a class as an **aspect**: a container for pointcuts and advice. It must **also be a Spring bean** (`@Component`), since `@Aspect` alone is not detected by component scanning. Order several aspects with `@Order`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Per-clause for the aspect instantiation model (`"perthis(...)"`, `"pertarget(...)"`). Empty → one singleton aspect (normal case). |

#### Example

```java
@Aspect
@Component
@Order(1)                                   // lower runs first (outermost)
public class LoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.example.shop.service.*.*(..))")
    public void logCall(JoinPoint jp) {
        log.info("Calling {} with {}", jp.getSignature().toShortString(), Arrays.toString(jp.getArgs()));
    }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Pointcut

**Package:** `org.aspectj.lang.annotation.Pointcut`
**Applies to:** Method (empty, `void`)

#### What it does
Declares a **named, reusable pointcut expression**. The method body is empty; its name is used as a reference in advice (`@Before("serviceLayer()")`). Pointcuts can be combined.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | The pointcut expression. |
| `argNames` | `String` | `""` | Comma-separated parameter names (only needed when compiled without debug/parameter info). |

#### Example

```java
@Aspect
@Component
public class Pointcuts {

    @Pointcut("within(com.example.shop.service..*)")
    public void serviceLayer() { }

    @Pointcut("within(com.example.shop.repository..*)")
    public void repositoryLayer() { }

    @Pointcut("@annotation(com.example.shop.aop.Audited)")
    public void auditedMethod() { }

    @Pointcut("serviceLayer() && !auditedMethod()")
    public void plainServiceMethod() { }
}

// Use from another aspect with the fully-qualified name
@Before("com.example.shop.aop.Pointcuts.serviceLayer()")
public void before(JoinPoint jp) { ... }
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 11.2 Advice Annotations

**Execution order for one join point:**

```
@Around (code before proceed())
  └── @Before
        └── ► target method ◄
  ┌── @AfterReturning (on success)  or  @AfterThrowing (on exception)
  └── @After (always, like finally)
@Around (code after proceed())
```

### @Before

**Package:** `org.aspectj.lang.annotation.Before`
**Applies to:** Method in an `@Aspect`

#### What it does
Runs **before** the matched method. It can read arguments (`JoinPoint`) but can't change them or skip the method, except by **throwing an exception**.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | — (required) | Pointcut expression or pointcut method reference. |
| `argNames` | `String` | `""` | Parameter names for binding (rarely needed). |

#### Example

```java
@Before("execution(* com.example.shop.service.OrderService.place*(..)) && args(request, ..)")
public void validateTenant(JoinPoint jp, OrderRequest request) {
    if (request.tenantId() == null) {
        throw new IllegalArgumentException("Tenant is required for " + jp.getSignature().getName());
    }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @After

**Package:** `org.aspectj.lang.annotation.After`
**Applies to:** Method in an `@Aspect`

#### What it does
Runs **after** the method finishes, **whether it returned normally or threw** (like a `finally` block). Use it for cleanup (clearing MDC/thread-locals, releasing resources).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | — (required) | Pointcut expression. |
| `argNames` | `String` | `""` | Parameter names for binding. |

#### Example

```java
@After("@annotation(com.example.shop.aop.WithTenant)")
public void clearTenant() {
    TenantContext.clear();
    MDC.remove("tenant");
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @AfterReturning

**Package:** `org.aspectj.lang.annotation.AfterReturning`
**Applies to:** Method in an `@Aspect`

#### What it does
Runs **only after a successful return**. With `returning`, the return value is passed to the advice (it can read it, but not replace it).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` / `pointcut` | `String` | `""` | Pointcut expression (`pointcut` overrides `value`). |
| `returning` | `String` | `""` | Name of the advice parameter that receives the return value. Its type also **filters**: the advice only runs if the return value is of that type. |
| `argNames` | `String` | `""` | Parameter names for binding. |

#### Example

```java
@AfterReturning(pointcut = "execution(* com.example.shop.service.OrderService.place(..))",
                returning = "order")
public void publishCreated(Order order) {
    events.publishEvent(new OrderPlacedEvent(order.getId()));
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @AfterThrowing

**Package:** `org.aspectj.lang.annotation.AfterThrowing`
**Applies to:** Method in an `@Aspect`

#### What it does
Runs **only when the method throws**. With `throwing`, the exception is passed to the advice. It **can't swallow** the exception: it still propagates to the caller (use `@Around` to translate or suppress exceptions).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` / `pointcut` | `String` | `""` | Pointcut expression. |
| `throwing` | `String` | `""` | Name of the advice parameter receiving the exception. Its type **filters** which exceptions trigger the advice. |
| `argNames` | `String` | `""` | Parameter names for binding. |

#### Example

```java
@AfterThrowing(pointcut = "within(com.example.shop.service..*)", throwing = "ex")
public void alertOnDataError(JoinPoint jp, DataAccessException ex) {
    alertService.notify("DB error in " + jp.getSignature().toShortString() + ": " + ex.getMessage());
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Around

**Package:** `org.aspectj.lang.annotation.Around`
**Applies to:** Method in an `@Aspect`

#### What it does
The most powerful advice: it **wraps** the method. It receives a `ProceedingJoinPoint` and decides **whether and when** to call `proceed()`. It can change arguments, change the return value, retry, cache, time, or translate exceptions. Must return `Object` (the result).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | — (required) | Pointcut expression. |
| `argNames` | `String` | `""` | Parameter names for binding. |

#### Example: timing with a custom annotation

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Timed {
    long warnAboveMs() default 500;
}

@Aspect
@Component
public class TimingAspect {
    private static final Logger log = LoggerFactory.getLogger(TimingAspect.class);

    @Around("@annotation(timed)")                 // binds the annotation instance
    public Object time(ProceedingJoinPoint pjp, Timed timed) throws Throwable {
        long start = System.nanoTime();
        try {
            return pjp.proceed();                 // call the real method
        } finally {
            long ms = (System.nanoTime() - start) / 1_000_000;
            if (ms > timed.warnAboveMs()) {
                log.warn("{} took {} ms", pjp.getSignature().toShortString(), ms);
            }
        }
    }
}

@Service
public class ReportService {
    @Timed(warnAboveMs = 200)
    public Report build(Long id) { ... }
}
```

#### Example: simple retry

```java
@Around("@annotation(com.example.shop.aop.Retry)")
public Object retry(ProceedingJoinPoint pjp) throws Throwable {
    int attempts = 0;
    while (true) {
        try {
            return pjp.proceed();
        } catch (TransientDataAccessException ex) {
            if (++attempts >= 3) throw ex;
            Thread.sleep(200L * attempts);
        }
    }
}
```

#### Common mistakes / best practices
- Forgetting to call `proceed()` → the real method **never runs**.
- Forgetting to **return** the result of `proceed()` → callers get `null`.
- Prefer the simplest advice that works (`@Before`/`@AfterReturning`); use `@Around` only when you need control.
- Internal calls (`this.method()`) and `private`/`final` methods are not intercepted.

[⬆ Back to Table of Contents](#table-of-contents)

---

# 12. Scheduling

Spring can run methods **periodically** (every N seconds) or on a **cron schedule** (every day at 02:00) inside the application, without an external scheduler.

## 12.1 Scheduling Annotations

### @EnableScheduling

**Package:** `org.springframework.scheduling.annotation.EnableScheduling`
**Applies to:** Class (`@Configuration` / main class)

#### What it does
Enables detection of `@Scheduled` methods on Spring beans. Without it, `@Scheduled` is silently ignored. (Not auto-enabled by Spring Boot.)

Spring Boot auto-configures a `ThreadPoolTaskScheduler` with **1 thread** by default, so scheduled tasks run **one at a time**. Increase the pool when you have several tasks:

```properties
spring.task.scheduling.pool.size=5
spring.task.scheduling.thread-name-prefix=sched-
# Java 21+: run tasks on virtual threads
spring.threads.virtual.enabled=true
```

#### Attributes
None.

#### Example

```java
@Configuration
@EnableScheduling
public class SchedulingConfig { }
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Scheduled

**Package:** `org.springframework.scheduling.annotation.Scheduled`
**Applies to:** Method (no arguments, usually `void`) of a Spring bean

#### What it does
Runs the method on a schedule. Exactly **one** of `cron`, `fixedDelay` or `fixedRate` must be set (plus optional `initialDelay`). It is **repeatable**: put several `@Scheduled` on one method to combine schedules.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `cron` | `String` | `""` | Cron expression (6 fields, see below), a macro (`@daily`), or `"-"` to **disable** the task. Supports `${...}` placeholders. |
| `zone` | `String` | `""` (server time zone) | Time zone for `cron`, e.g. `"Asia/Kolkata"`. |
| `fixedDelay` | `long` | `-1` | Wait this long **after the previous run finishes** before starting the next. |
| `fixedDelayString` | `String` | `""` | Same, as a String: placeholders or durations (`"${jobs.delay}"`, `"PT30S"`). |
| `fixedRate` | `long` | `-1` | Start a run every N time units, **measured from the previous start** (runs can queue up if a run is slow). |
| `fixedRateString` | `String` | `""` | Same, as a String. |
| `initialDelay` | `long` | `-1` | Delay before the **first** run (with `fixedDelay`/`fixedRate`, and with `cron` since 6.1). |
| `initialDelayString` | `String` | `""` | Same, as a String. |
| `timeUnit` | `TimeUnit` | `MILLISECONDS` | Unit for `fixedDelay`, `fixedRate`, `initialDelay` (Spring 5.3.10+). |
| `scheduler` | `String` | `""` | (Spring 6.1+) Bean name of a specific `TaskScheduler`/executor to run this task. |

#### Cron format (Spring: 6 fields, seconds first)

```
┌───────────── second (0-59)
│ ┌─────────── minute (0-59)
│ │ ┌───────── hour (0-23)
│ │ │ ┌─────── day of month (1-31)
│ │ │ │ ┌───── month (1-12 or JAN-DEC)
│ │ │ │ │ ┌─── day of week (0-7 or MON-SUN; 0 and 7 = Sunday)
│ │ │ │ │ │
* * * * * *
```

| Cron | Meaning |
|------|---------|
| `0 0 2 * * *` | Every day at 02:00:00 |
| `0 */15 * * * *` | Every 15 minutes |
| `0 0 9-18 * * MON-FRI` | Every hour from 9 to 18 on weekdays |
| `0 30 23 L * *` | 23:30 on the last day of each month |
| `0 0 10 ? * MON#1` | 10:00 on the first Monday of the month |
| `@hourly`, `@daily` / `@midnight`, `@weekly`, `@monthly`, `@yearly` | Macros |

#### Example

```java
@Component
public class ReportJobs {

    private static final Logger log = LoggerFactory.getLogger(ReportJobs.class);

    // Every day at 02:00 India time
    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Kolkata")
    public void nightlyReport() {
        log.info("Generating nightly report");
    }

    // 30 seconds after the previous run ENDS, first run 10 seconds after startup
    @Scheduled(fixedDelay = 30, initialDelay = 10, timeUnit = TimeUnit.SECONDS)
    public void pollOutbox() { ... }

    // Every 5 seconds from the previous START
    @Scheduled(fixedRate = 5000)
    public void heartbeat() { ... }

    // Configurable from application.properties; "-" disables it
    // jobs.cleanup.cron=0 0 3 * * SUN
    @Scheduled(cron = "${jobs.cleanup.cron:-}")
    public void weeklyCleanup() { ... }

    // ISO-8601 duration string
    @Scheduled(fixedDelayString = "${jobs.sync.delay:PT1M}")
    public void syncPrices() { ... }
}
```

#### fixedDelay vs fixedRate

```
fixedDelay = 5s  (job takes 2s)
|--job--|.....5s.....|--job--|.....5s.....|--job--|

fixedRate = 5s   (job takes 2s)
|--job--|...|--job--|...|--job--|
0s          5s          10s
```

#### Common mistakes / best practices
- Missing `@EnableScheduling` → nothing runs, no error.
- With the default **single thread**, one slow job delays all others; set `spring.task.scheduling.pool.size`.
- Exceptions are logged and the job runs again next time; catch and handle errors inside the job.
- In a **cluster** (several instances), every instance runs the job. Use a distributed lock such as **ShedLock** (`@SchedulerLock`) or an external scheduler like [AWS EventBridge Scheduler](AWS_Service_guide.md#4-aws-eventbridge-scheduler).
- Unix cron has **5** fields; Spring needs **6** (seconds first).

[⬆ Back to Table of Contents](#table-of-contents)

---

# 13. Async Processing

## 13.1 Async Annotations

### @EnableAsync

**Package:** `org.springframework.scheduling.annotation.EnableAsync`
**Applies to:** Class (`@Configuration`)

#### What it does
Enables Spring's **asynchronous method execution**: calls to `@Async` methods return immediately and the work runs on a **thread pool**. Not auto-enabled by Spring Boot.

Spring Boot auto-configures a `ThreadPoolTaskExecutor` named `applicationTaskExecutor` (core size 8, unbounded queue by default):

```properties
spring.task.execution.pool.core-size=8
spring.task.execution.pool.max-size=16
spring.task.execution.pool.queue-capacity=100
spring.task.execution.thread-name-prefix=async-
# Java 21+: virtual threads
spring.threads.virtual.enabled=true
```

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `annotation` | `Class<? extends Annotation>` | `Annotation.class` | Custom annotation to detect instead of (in addition to) `@Async`. |
| `proxyTargetClass` | `boolean` | `false` | `true` → CGLIB class proxies. |
| `mode` | `AdviceMode` | `PROXY` | `PROXY` or `ASPECTJ` (weaving; also handles self-invocation). |
| `order` | `int` | `Ordered.LOWEST_PRECEDENCE` | Order of the async advice relative to other advisors. |

#### Example

```java
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    // Dedicated pool for mail sending
    @Bean(name = "mailExecutor")
    public ThreadPoolTaskExecutor mailExecutor() {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(2);
        ex.setMaxPoolSize(4);
        ex.setQueueCapacity(500);
        ex.setThreadNamePrefix("mail-");
        ex.setTaskDecorator(new MdcTaskDecorator());     // copy logging context to async threads
        ex.initialize();
        return ex;
    }

    // Errors from void @Async methods (otherwise they are only logged)
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) ->
                LoggerFactory.getLogger(method.getDeclaringClass())
                        .error("Async {} failed", method.getName(), ex);
    }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Async

**Package:** `org.springframework.scheduling.annotation.Async`
**Applies to:** Method, class (all public methods)

#### What it does
Executes the method **in a separate thread**. The caller doesn't wait. Supported return types:
- `void` → fire and forget
- `CompletableFuture<T>` (recommended) / `Future<T>` → the caller can wait for or combine results

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Bean name (qualifier) of the `Executor` to use. Empty → the default executor. Supports `${...}` placeholders. |

#### Example

```java
@Service
public class NotificationService {

    // Fire and forget, runs on "mailExecutor"
    @Async("mailExecutor")
    public void sendWelcomeEmail(String to) {
        mailSender.send(to, "Welcome!", "...");
    }

    // Returns a result asynchronously
    @Async
    public CompletableFuture<PriceQuote> quoteFrom(Supplier supplier, Long productId) {
        PriceQuote quote = supplier.fetchQuote(productId);       // slow HTTP call
        return CompletableFuture.completedFuture(quote);
    }
}

@Service
public class PricingService {
    private final NotificationService notifications;

    // Call 3 suppliers in parallel and take the cheapest
    public PriceQuote bestPrice(Long productId, List<Supplier> suppliers) {
        List<CompletableFuture<PriceQuote>> futures = suppliers.stream()
                .map(s -> notifications.quoteFrom(s, productId))
                .toList();
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        return futures.stream().map(CompletableFuture::join)
                .min(Comparator.comparing(PriceQuote::price))
                .orElseThrow();
    }
}
```

#### Common mistakes / best practices
- Missing `@EnableAsync` → the method runs **synchronously**, no error.
- **Self-invocation** (`this.sendEmail()`) runs synchronously (no proxy).
- `@Async` on `private` methods has no effect.
- A `@Transactional` caller's transaction does **not** extend into the async thread; the async method needs its own `@Transactional`.
- Security context, MDC and request-scoped beans are not available in the async thread unless propagated (`TaskDecorator`, `DelegatingSecurityContextAsyncTaskExecutor`).

[⬆ Back to Table of Contents](#table-of-contents)

---

# 14. Caching

Spring's cache abstraction stores **method results** so repeated calls with the same arguments return the cached value instead of running the method (DB query, HTTP call) again.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
<!-- choose a provider, e.g. Caffeine (local) or spring-boot-starter-data-redis (distributed) -->
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
```

```properties
spring.cache.type=caffeine              # simple | caffeine | redis | jcache | none ...
spring.cache.cache-names=products,categories
spring.cache.caffeine.spec=maximumSize=1000,expireAfterWrite=10m
# Redis: spring.cache.redis.time-to-live=10m
```

**SpEL variables available in cache annotations:**

| Expression | Meaning |
|------------|---------|
| `#id`, `#product` | Method argument by name |
| `#p0`, `#a0` | Argument by index |
| `#product.id` | Property of an argument |
| `#result` | The return value (only in `unless`, `@CachePut.key`, `@CacheEvict` with `beforeInvocation = false`) |
| `#root.methodName`, `#root.args`, `#root.caches` | Method metadata |

## 14.1 Enabling & Configuring

### @EnableCaching

**Package:** `org.springframework.cache.annotation.EnableCaching`
**Applies to:** Class (`@Configuration`)

#### What it does
Enables the cache annotations. Spring Boot then auto-configures a `CacheManager` for the provider on the classpath (Caffeine, Redis, Hazelcast...), or a simple in-memory `ConcurrentMapCacheManager` if none.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `proxyTargetClass` | `boolean` | `false` | `true` → CGLIB class proxies. |
| `mode` | `AdviceMode` | `PROXY` | `PROXY` or `ASPECTJ`. |
| `order` | `int` | `Ordered.LOWEST_PRECEDENCE` | Order of the caching advice relative to other advice (e.g. transactions). |

#### Example

```java
@Configuration
@EnableCaching
public class CacheConfig {

    // Optional: customize Caffeine caches individually
    @Bean
    public CacheManagerCustomizer<CaffeineCacheManager> caffeineCustomizer() {
        return cm -> cm.registerCustomCache("exchangeRates",
                Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(1)).maximumSize(200).build());
    }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @CacheConfig

**Package:** `org.springframework.cache.annotation.CacheConfig`
**Applies to:** Class

#### What it does
Sets **shared cache settings** for all cache annotations in the class, so you don't repeat the cache name on every method.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `cacheNames` | `String[]` | `{}` | Default cache name(s) for the class. |
| `keyGenerator` | `String` | `""` | Default `KeyGenerator` bean name. |
| `cacheManager` | `String` | `""` | Default `CacheManager` bean name. |
| `cacheResolver` | `String` | `""` | Default `CacheResolver` bean name. |

#### Example

```java
@Service
@CacheConfig(cacheNames = "products")
public class ProductService {

    @Cacheable                                   // uses "products"
    public Product get(Long id) { ... }

    @CacheEvict(key = "#product.id")             // uses "products"
    public void update(Product product) { ... }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 14.2 Cache Operations

### @Cacheable

**Package:** `org.springframework.cache.annotation.Cacheable`
**Applies to:** Method, class

#### What it does
Before running the method, Spring looks up the **key** in the cache:
- **Hit** → returns the cached value; the method **does not run**.
- **Miss** → runs the method and stores the result in the cache.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` / `cacheNames` | `String[]` | `{}` | Cache name(s). |
| `key` | `String` | `""` | SpEL for the cache key. Default: generated from **all arguments** (`SimpleKeyGenerator`). |
| `keyGenerator` | `String` | `""` | Custom `KeyGenerator` bean (mutually exclusive with `key`). |
| `cacheManager` | `String` | `""` | `CacheManager` bean to use. |
| `cacheResolver` | `String` | `""` | `CacheResolver` bean (mutually exclusive with `cacheManager`). |
| `condition` | `String` | `""` | SpEL evaluated **before** the call; `false` → no caching at all for this call. |
| `unless` | `String` | `""` | SpEL evaluated **after** the call (can use `#result`); `true` → result is **not stored**. |
| `sync` | `boolean` | `false` | `true` → if several threads miss the same key, only one runs the method and the others wait (prevents a cache stampede). Can't be combined with `unless` or multiple caches. |

#### Example

```java
@Service
public class ProductService {

    @Cacheable(cacheNames = "products", key = "#id")
    public Product getById(Long id) {
        return productRepository.findById(id).orElseThrow();      // only on cache miss
    }

    // Composite key, cache only short queries, don't cache empty results
    @Cacheable(cacheNames = "productSearch",
               key = "#category + ':' + #page",
               condition = "#page < 5",
               unless = "#result.isEmpty()")
    public List<Product> byCategory(String category, int page) { ... }

    // Expensive call, avoid stampede
    @Cacheable(cacheNames = "exchangeRates", key = "#from + '-' + #to", sync = true)
    public BigDecimal rate(String from, String to) { return fxClient.fetch(from, to); }
}
```

#### Common mistakes / best practices
- Self-invocation (`this.getById()`) bypasses the cache proxy.
- Returning **mutable** objects from local caches: callers modifying them change the cached copy.
- Cached objects must be **serializable** for Redis; prefer DTOs over JPA entities (lazy proxies don't serialize).
- Always configure **expiry/size**; the default `ConcurrentMapCacheManager` never evicts.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @CachePut

**Package:** `org.springframework.cache.annotation.CachePut`
**Applies to:** Method, class

#### What it does
**Always runs** the method and **stores the result** in the cache. Used on update methods to keep the cache fresh (instead of evicting).

#### Attributes
Same as `@Cacheable` **except `sync`**: `value`/`cacheNames`, `key`, `keyGenerator`, `cacheManager`, `cacheResolver`, `condition`, `unless`. The `key` may use `#result`.

#### Example

```java
@CachePut(cacheNames = "products", key = "#result.id")
public Product update(Product product) {
    return productRepository.save(product);       // result replaces the cached entry
}
```

> Don't put `@Cacheable` and `@CachePut` on the same method.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @CacheEvict

**Package:** `org.springframework.cache.annotation.CacheEvict`
**Applies to:** Method, class

#### What it does
**Removes** entries from the cache: one key, or all entries.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` / `cacheNames` | `String[]` | `{}` | Cache name(s). |
| `key` | `String` | `""` | SpEL for the key to evict. |
| `keyGenerator` / `cacheManager` / `cacheResolver` | `String` | `""` | As for `@Cacheable`. |
| `condition` | `String` | `""` | SpEL; `false` → don't evict. |
| `allEntries` | `boolean` | `false` | `true` → clear the **whole** cache (ignores `key`). |
| `beforeInvocation` | `boolean` | `false` | `true` → evict **before** the method runs (eviction happens even if the method throws). `false` → evict only after a successful call. |

#### Example

```java
@CacheEvict(cacheNames = "products", key = "#id")
public void delete(Long id) {
    productRepository.deleteById(id);
}

@CacheEvict(cacheNames = "productSearch", allEntries = true)
public void reindexCatalog() { ... }

// Clear caches periodically
@Scheduled(cron = "0 0 * * * *")
@CacheEvict(cacheNames = "exchangeRates", allEntries = true)
public void refreshRates() { }
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Caching

**Package:** `org.springframework.cache.annotation.Caching`
**Applies to:** Method, class

#### What it does
**Groups several cache annotations** of the same or different types on one method (Java doesn't allow repeating `@CacheEvict` with different settings otherwise).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `cacheable` | `Cacheable[]` | `{}` | `@Cacheable` operations. |
| `put` | `CachePut[]` | `{}` | `@CachePut` operations. |
| `evict` | `CacheEvict[]` | `{}` | `@CacheEvict` operations. |

#### Example

```java
@Caching(
        put   = @CachePut(cacheNames = "products", key = "#result.id"),
        evict = {
                @CacheEvict(cacheNames = "productSearch", allEntries = true),
                @CacheEvict(cacheNames = "productsBySku", key = "#product.sku")
        })
public Product save(Product product) {
    return productRepository.save(product);
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

# 15. Event Handling

Spring has a built-in **publish/subscribe** event system (inside one application; for events **between** services use a broker such as Kafka or [AWS SNS + SQS](AWS_Service_guide.md#fan-out-pattern-sns--sqs)). A bean publishes an event with `ApplicationEventPublisher`; any number of listeners react to it. This decouples modules: the order service doesn't need to know about emails, invoices or analytics.

```java
// Event: any object (a record is ideal)
public record OrderPlacedEvent(Long orderId, String customerEmail, BigDecimal total) { }

@Service
public class OrderService {
    private final ApplicationEventPublisher events;
    public OrderService(ApplicationEventPublisher events) { this.events = events; }

    @Transactional
    public Order place(OrderRequest req) {
        Order order = orderRepository.save(Order.from(req));
        events.publishEvent(new OrderPlacedEvent(order.getId(), req.email(), order.getTotal()));
        return order;
    }
}
```

## 15.1 Event Listener Annotations

### @EventListener

**Package:** `org.springframework.context.event.EventListener`
**Applies to:** Method of a Spring bean

#### What it does
Registers the method as a **listener** for events of its parameter type. By default listeners run **synchronously in the publisher's thread** (and inside its transaction), in `@Order` order. Add `@Async` to run them in the background.

If the method **returns** a value (or a collection of values), each non-null result is **published as a new event**.

Also used for application lifecycle events: `ApplicationReadyEvent`, `ContextRefreshedEvent`, `ContextClosedEvent`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` / `classes` | `Class<?>[]` | `{}` | Event types to listen for. Needed when the method has **no parameter** or listens to several types. |
| `condition` | `String` | `""` | SpEL condition; the listener runs only if `true`. Use `#event` (or the parameter name) to access the event. |
| `id` | `String` | `""` | Listener id (default: method signature). Useful for removing/identifying listeners. |

#### Example

```java
@Component
public class OrderListeners {

    // Synchronous, in the publisher's thread
    @EventListener
    public void reserveStock(OrderPlacedEvent event) {
        inventoryService.reserve(event.orderId());
    }

    // Conditional + async
    @Async
    @EventListener(condition = "#event.total > 10000")
    public void notifyHighValue(OrderPlacedEvent event) {
        slack.post("Big order: " + event.orderId());
    }

    // Returning a value publishes it as a new event
    @EventListener
    public InvoiceRequestedEvent requestInvoice(OrderPlacedEvent event) {
        return new InvoiceRequestedEvent(event.orderId());
    }

    // No-arg method: specify the type
    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        log.info("Application is ready");
    }

    // Several event types
    @EventListener({ContextRefreshedEvent.class, ContextClosedEvent.class})
    public void onContextChange(ApplicationContextEvent event) { ... }
}
```

#### Common mistakes / best practices
- An exception in a synchronous listener propagates to the **publisher** (and can roll back its transaction).
- A synchronous listener sees the publisher's **uncommitted** data; if the transaction later rolls back, actions like emails already happened. Use [@TransactionalEventListener](#transactionaleventlistener).

[⬆ Back to Table of Contents](#table-of-contents)

---

### @TransactionalEventListener

**Package:** `org.springframework.transaction.event.TransactionalEventListener`
**Applies to:** Method of a Spring bean

#### What it does
An `@EventListener` bound to the **publisher's transaction**: the listener runs at a chosen **phase** of that transaction. The default is **after commit**, so side effects (emails, messages to Kafka, cache invalidation) happen only if the data was really saved.

If the event is published **without** an active transaction, the listener is **not called** (unless `fallbackExecution = true`).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `phase` | `TransactionPhase` | `AFTER_COMMIT` | `BEFORE_COMMIT`, `AFTER_COMMIT`, `AFTER_ROLLBACK`, `AFTER_COMPLETION` (commit or rollback). |
| `fallbackExecution` | `boolean` | `false` | `true` → run immediately when no transaction is active. |
| `value` / `classes` | `Class<?>[]` | `{}` | Event types. |
| `condition` | `String` | `""` | SpEL condition. |
| `id` | `String` | `""` | Listener id. |

#### Example

```java
@Component
public class OrderSideEffects {

    @TransactionalEventListener                    // AFTER_COMMIT
    public void sendConfirmation(OrderPlacedEvent event) {
        mailService.sendOrderConfirmation(event.customerEmail(), event.orderId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void logFailure(OrderPlacedEvent event) {
        log.warn("Order {} rolled back", event.orderId());
    }

    // Writing to the DB after commit needs a NEW transaction
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordStats(OrderPlacedEvent event) {
        statsRepository.incrementOrders(LocalDate.now());
    }
}
```

More details: [@TransactionalEventListener in the transaction guide](java-spring-transactional.md#transactionaleventlistener).

[⬆ Back to Table of Contents](#table-of-contents)

---

# 16. Testing

`spring-boot-starter-test` brings JUnit 5, AssertJ, Mockito, Hamcrest, JSONassert, JsonPath and Spring's test support.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

**Full context vs test slices:** `@SpringBootTest` starts the **whole** application (slow, realistic). **Slice** annotations (`@WebMvcTest`, `@DataJpaTest`...) start only the beans for **one layer** (fast, focused).

| Annotation | Loads | Typical test |
|------------|-------|--------------|
| `@SpringBootTest` | Everything | Integration / end-to-end |
| `@WebMvcTest` | Controllers, advice, filters, MVC config, Jackson | Controller + JSON + validation |
| `@WebFluxTest` | WebFlux controllers | Reactive controllers |
| `@DataJpaTest` | Entities, repositories, `EntityManager`, `DataSource` | Repository queries |
| `@JdbcTest` | `DataSource`, `JdbcTemplate` | Plain JDBC DAOs |
| `@DataMongoTest` / `@DataRedisTest` | Mongo / Redis repositories | NoSQL repositories |
| `@JsonTest` | Jackson `ObjectMapper`, `JacksonTester` | JSON serialization |
| `@RestClientTest` | `RestTemplateBuilder`/`RestClient.Builder`, `MockRestServiceServer` | HTTP client classes |

## 16.1 Test Context & Slices

### @SpringBootTest

**Package:** `org.springframework.boot.test.context.SpringBootTest`
**Applies to:** Test class

#### What it does
Starts a **full Spring Boot application context** for the test, found via the `@SpringBootConfiguration` class. Optionally starts a real embedded server.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `webEnvironment` | `WebEnvironment` | `MOCK` | `MOCK` (mock servlet environment, no server; use MockMvc), `RANDOM_PORT` (real server on a random port), `DEFINED_PORT` (real server on `server.port`), `NONE` (no web environment). |
| `value` / `properties` | `String[]` | `{}` | Properties for this test, e.g. `"app.feature.x=true"`. |
| `args` | `String[]` | `{}` | Application arguments (`--debug`). |
| `classes` | `Class<?>[]` | `{}` | Specific configuration classes to load instead of searching for `@SpringBootConfiguration`. |
| `useMainMethod` | `UseMainMethod` | `NEVER` | (Boot 3.0+) `ALWAYS` / `WHEN_AVAILABLE` → start the context by calling your `main` method (to test customizations made there). |

#### Example

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                properties = "app.mail.enabled=false")
class OrderApiIntegrationTest {

    @Autowired
    private TestRestTemplate rest;               // pre-configured for the random port

    @LocalServerPort
    private int port;

    @Test
    void createsOrder() {
        ResponseEntity<OrderDto> res = rest.postForEntity("/api/orders",
                new OrderRequest("SKU-1", 2), OrderDto.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(res.getBody().quantity()).isEqualTo(2);
    }
}
```

#### Common mistakes / best practices
- Using `@SpringBootTest` for everything makes the suite slow; prefer slices for single layers.
- Different `@MockitoBean`/properties combinations create **new contexts**; keep test configuration consistent so contexts are cached and reused.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @WebMvcTest

**Package:** `org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest`
**Applies to:** Test class

#### What it does
A **web-layer slice**: loads `@Controller`/`@RestController`, `@ControllerAdvice`, `@JsonComponent`, `Converter`s, `Filter`s, `WebMvcConfigurer`s and `HandlerInterceptor`s, and auto-configures **`MockMvc`**. Services and repositories are **not** loaded: provide them as mocks.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` / `controllers` | `Class<?>[]` | `{}` | Controllers to load. Empty → all controllers. |
| `properties` | `String[]` | `{}` | Test properties. |
| `useDefaultFilters` | `boolean` | `true` | Whether to use the default component filters. |
| `includeFilters` / `excludeFilters` | `Filter[]` | `{}` | Add/remove beans from the slice. |
| `excludeAutoConfiguration` | `Class<?>[]` | `{}` | Auto-configurations to exclude. |

#### Example

```java
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean                       // Boot 3.4+ (use @MockBean on older versions)
    private UserService userService;

    @Test
    void returnsUser() throws Exception {
        given(userService.findById(1L)).willReturn(new UserDto(1L, "Mahendra"));

        mvc.perform(get("/api/users/1").accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.name").value("Mahendra"));
    }

    @Test
    void rejectsInvalidBody() throws Exception {
        mvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                             { "name": "", "email": "not-an-email" }
                             """))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.fieldErrors.email").exists());
    }
}
```

> With Spring Security on the classpath, `@WebMvcTest` also applies security; use [@WithMockUser](#withmockuser) or `.with(user("x"))`.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @DataJpaTest

**Package:** `org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest`
**Applies to:** Test class

#### What it does
A **JPA slice**: loads entities, Spring Data repositories, `EntityManager`, `TestEntityManager`, `DataSource`, Flyway/Liquibase. By default it **replaces your DataSource with an embedded in-memory database** (H2/HSQL/Derby if on the classpath), and every test runs in a **transaction that is rolled back** at the end.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `properties` | `String[]` | `{}` | Test properties. |
| `showSql` | `boolean` | `true` | Log SQL statements. |
| `bootstrapMode` | `BootstrapMode` | `DEFAULT` | Repository bootstrap mode (`DEFAULT`, `DEFERRED`, `LAZY`). |
| `includeFilters` / `excludeFilters` | `Filter[]` | `{}` | Add/remove beans. |
| `excludeAutoConfiguration` | `Class<?>[]` | `{}` | Auto-configurations to exclude. |

#### Example

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)   // use the real DB / Testcontainers
class EmployeeRepositoryTest {

    @Autowired private EmployeeRepository repository;
    @Autowired private TestEntityManager em;

    @Test
    void findsActiveEmployeesByDepartment() {
        Department it = em.persist(new Department("IT"));
        em.persist(new Employee("Asha", it, EmployeeStatus.ACTIVE));
        em.persist(new Employee("Ravi", it, EmployeeStatus.RESIGNED));
        em.flush();
        em.clear();                                  // force real SELECTs

        List<Employee> result = repository.findByDepartmentIdAndStatus(it.getId(), EmployeeStatus.ACTIVE);

        assertThat(result).extracting(Employee::getName).containsExactly("Asha");
    }   // rolled back
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @JdbcTest

**Package:** `org.springframework.boot.test.autoconfigure.jdbc.JdbcTest`
**Applies to:** Test class

#### What it does
A **JDBC slice** for code that uses `JdbcTemplate`/`JdbcClient` directly (no JPA): loads `DataSource`, `JdbcTemplate`, `NamedParameterJdbcTemplate`, `JdbcClient`, transactions and migrations. Like `@DataJpaTest`, it uses an embedded database by default and rolls back each test. Your `@Repository` DAO classes are **not** loaded automatically; `@Import` them.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `properties` | `String[]` | `{}` | Test properties. |
| `includeFilters` / `excludeFilters` | `Filter[]` | `{}` | Add/remove beans. |
| `excludeAutoConfiguration` | `Class<?>[]` | `{}` | Auto-configurations to exclude. |

#### Example

```java
@JdbcTest
@Import(ProductDao.class)
@Sql("/test-data/products.sql")                 // insert test rows
class ProductDaoTest {

    @Autowired private ProductDao dao;

    @Test
    void findsById() {
        assertThat(dao.findById(1L)).isPresent()
                .get().extracting(Product::name).isEqualTo("Keyboard");
    }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @JsonTest

**Package:** `org.springframework.boot.test.autoconfigure.json.JsonTest`
**Applies to:** Test class

#### What it does
A **JSON slice**: loads the auto-configured Jackson `ObjectMapper`, `@JsonComponent`s and Jackson modules, and provides **`JacksonTester<T>`** to check serialization and deserialization of your DTOs (field names, date formats, ignored fields).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `useDefaultFilters` | `boolean` | `true` | Use default filters. |
| `includeFilters` / `excludeFilters` | `Filter[]` | `{}` | Add/remove beans. |
| `excludeAutoConfiguration` | `Class<?>[]` | `{}` | Auto-configurations to exclude. |

#### Example

```java
@JsonTest
class OrderDtoJsonTest {

    @Autowired
    private JacksonTester<OrderDto> json;

    @Test
    void serializes() throws Exception {
        OrderDto dto = new OrderDto(7L, new BigDecimal("199.50"), LocalDate.of(2026, 10, 8));

        assertThat(json.write(dto))
                .hasJsonPathNumberValue("$.id")
                .extractingJsonPathStringValue("$.orderDate").isEqualTo("2026-10-08");
    }

    @Test
    void deserializes() throws Exception {
        String content = """
                         { "id": 7, "total": 199.50, "orderDate": "2026-10-08" }
                         """;
        assertThat(json.parseObject(content).total()).isEqualByComparingTo("199.50");
    }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @RestClientTest

**Package:** `org.springframework.boot.test.autoconfigure.web.client.RestClientTest`
**Applies to:** Test class

#### What it does
A slice for testing **your HTTP client classes** (built with `RestTemplateBuilder` or `RestClient.Builder`). It auto-configures Jackson and a **`MockRestServiceServer`** that intercepts outgoing requests, so no real server is called.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` / `components` | `Class<?>[]` | `{}` | The client classes to test. |
| `properties` | `String[]` | `{}` | Test properties. |
| `useDefaultFilters` | `boolean` | `true` | Use default filters. |
| `includeFilters` / `excludeFilters` | `Filter[]` | `{}` | Add/remove beans. |
| `excludeAutoConfiguration` | `Class<?>[]` | `{}` | Auto-configurations to exclude. |

#### Example

```java
@Service
public class WeatherClient {
    private final RestClient client;
    public WeatherClient(RestClient.Builder builder) {
        this.client = builder.baseUrl("https://api.weather.test").build();
    }
    public Forecast today(String city) {
        return client.get().uri("/forecast?city={c}", city).retrieve().body(Forecast.class);
    }
}

@RestClientTest(WeatherClient.class)
class WeatherClientTest {

    @Autowired private WeatherClient client;
    @Autowired private MockRestServiceServer server;

    @Test
    void readsForecast() {
        server.expect(requestTo("https://api.weather.test/forecast?city=Pune"))
              .andRespond(withSuccess("""
                      { "city": "Pune", "tempC": 29 }
                      """, MediaType.APPLICATION_JSON));

        assertThat(client.today("Pune").tempC()).isEqualTo(29);
    }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 16.2 Mocking Beans

### @MockBean

**Package:** `org.springframework.boot.test.mock.mockito.MockBean`
**Applies to:** Test class field, test class, `@Configuration` class

#### What it does
Adds a **Mockito mock** to the Spring application context, **replacing** any existing bean of the same type. Every bean that depends on it receives the mock. Mocks are reset after each test.

> ⚠️ **Deprecated since Spring Boot 3.4** and **removed in Spring Boot 4.0**. Use [@MockitoBean](#mockitobean--mockitospybean) from Spring Framework 6.2 instead. The usage is almost identical.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `name` | `String` | `""` | Name of the bean to register or replace. |
| `value` / `classes` | `Class<?>[]` | `{}` | Classes to mock (when used on the test class). |
| `extraInterfaces` | `Class<?>[]` | `{}` | Extra interfaces the mock implements. |
| `answer` | `Answers` | `RETURNS_DEFAULTS` | Mockito default answer (e.g. `RETURNS_DEEP_STUBS`). |
| `serializable` | `boolean` | `false` | Whether the mock is serializable. |
| `reset` | `MockReset` | `AFTER` | When to reset the mock: `BEFORE`, `AFTER`, `NONE`. |

#### Example

```java
@SpringBootTest
class CheckoutServiceTest {

    @MockBean
    private PaymentGateway paymentGateway;           // real bean replaced by a mock

    @Autowired
    private CheckoutService checkoutService;         // receives the mock

    @Test
    void chargesCustomer() {
        given(paymentGateway.pay(any())).willReturn(new Receipt("R-1"));

        checkoutService.checkout(cartWithTotal("500.00"));

        then(paymentGateway).should().pay(new BigDecimal("500.00"));
    }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @SpyBean

**Package:** `org.springframework.boot.test.mock.mockito.SpyBean`
**Applies to:** Test class field, test class

#### What it does
Wraps an **existing bean** in a **Mockito spy**: the real methods run, but you can verify calls and stub specific methods.

> ⚠️ **Deprecated since Spring Boot 3.4**, removed in 4.0. Use [@MockitoSpyBean](#mockitobean--mockitospybean).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `name` | `String` | `""` | Name of the bean to spy. |
| `value` / `classes` | `Class<?>[]` | `{}` | Classes to spy (on the test class). |
| `reset` | `MockReset` | `AFTER` | When to reset the spy. |
| `proxyTargetAware` | `boolean` | `true` | Whether Spring AOP proxies are unwrapped when verifying. |

#### Example

```java
@SpringBootTest
class OrderServiceTest {

    @SpyBean
    private NotificationService notificationService;   // real bean, but recorded

    @Autowired
    private OrderService orderService;

    @Test
    void sendsConfirmation() {
        orderService.place(sampleRequest());
        verify(notificationService).sendConfirmation(any());
    }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @MockitoBean / @MockitoSpyBean

**Package:** `org.springframework.test.context.bean.override.mockito` (Spring Framework 6.2+, Spring Boot 3.4+)
**Applies to:** Test class field (also test class / interface for type-level declarations in 6.2.2+)

#### What they do
The **replacements** for `@MockBean` and `@SpyBean`, built into Spring's test framework (bean override support):
- `@MockitoBean` → replaces a bean with a Mockito **mock** (or adds one if none exists).
- `@MockitoSpyBean` → wraps an existing bean in a Mockito **spy** (the bean must exist).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `name` / `value` | `String` | `""` | Name of the bean to override. Empty → by type. |
| `types` | `Class<?>[]` | `{}` | Types to mock (for type-level declarations). |
| `contextName` | `String` | `""` | Context in a hierarchy where the override applies. |
| `reset` | `MockReset` | `AFTER` | When to reset the mock/spy. |
| `extraInterfaces` | `Class<?>[]` | `{}` | (`@MockitoBean` only) Extra interfaces. |
| `answers` | `Answers` | `RETURNS_DEFAULTS` | (`@MockitoBean` only) Default answer. |
| `serializable` | `boolean` | `false` | (`@MockitoBean` only) Serializable mock. |

#### Example

```java
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired MockMvc mvc;

    @MockitoBean
    OrderService orderService;

    @MockitoSpyBean
    OrderMapper orderMapper;                 // real mapper (loaded via @Import), but verifiable

    @Test
    void returns404WhenMissing() throws Exception {
        given(orderService.find(99L)).willThrow(new OrderNotFoundException(99L));
        mvc.perform(get("/api/orders/99")).andExpect(status().isNotFound());
    }
}
```

#### Migration

| Old (Boot) | New (Spring Framework 6.2) |
|------------|----------------------------|
| `org.springframework.boot.test.mock.mockito.MockBean` | `org.springframework.test.context.bean.override.mockito.MockitoBean` |
| `org.springframework.boot.test.mock.mockito.SpyBean` | `org.springframework.test.context.bean.override.mockito.MockitoSpyBean` |
| `@MockBean` in `@Configuration` classes | Not supported: declare `@MockitoBean` in the test class (or a shared base class / interface) |

[⬆ Back to Table of Contents](#table-of-contents)

---

## 16.3 Test Configuration

### @AutoConfigureMockMvc

**Package:** `org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc`
**Applies to:** Test class

#### What it does
Adds an auto-configured **`MockMvc`** to a `@SpringBootTest` (full context, no real server). `@WebMvcTest` already includes it.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `addFilters` | `boolean` | `true` | Whether servlet filters (including Spring Security) are applied to MockMvc requests. |
| `print` | `MockMvcPrint` | `DEFAULT` | Where to print request/response details: `DEFAULT`, `LOG_DEBUG`, `SYSTEM_OUT`, `SYSTEM_ERR`, `NONE`. |
| `printOnlyOnFailure` | `boolean` | `true` | Print details only when a test fails. |
| `webClientEnabled` | `boolean` | `true` | Auto-configure HtmlUnit `WebClient` (if on classpath). |
| `webDriverEnabled` | `boolean` | `true` | Auto-configure Selenium `WebDriver` (if on classpath). |

#### Example

```java
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)          // skip security filters in this test
class ProductFlowTest {

    @Autowired MockMvc mvc;

    @Test
    void createsAndReadsProduct() throws Exception {
        String location = mvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                 { "name": "Mouse", "price": 499 }
                                 """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        mvc.perform(get(location)).andExpect(jsonPath("$.name").value("Mouse"));
    }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @AutoConfigureTestDatabase

**Package:** `org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase`
**Applies to:** Test class

#### What it does
Controls whether the application's `DataSource` is **replaced by an embedded test database**. `@DataJpaTest` and `@JdbcTest` include it with `replace = ANY`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `replace` | `Replace` | `ANY` | `ANY` → replace any DataSource with an embedded one. `AUTO_CONFIGURED` → replace only the auto-configured DataSource (keep manually defined ones). `NONE` → keep the real DataSource (e.g. Testcontainers). |
| `connection` | `EmbeddedDatabaseConnection` | `NONE` (auto-detect) | Which embedded database to use: `H2`, `DERBY`, `HSQLDB`. |

#### Example

```java
// Run JPA tests against a real PostgreSQL in Docker
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class OrderRepositoryPostgresTest {

    @Container
    @ServiceConnection                       // Boot 3.1+: wires spring.datasource.* automatically
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired OrderRepository repository;
    // tests...
}
```

> Testing against the **same database engine** as production (Testcontainers) catches SQL dialect issues that H2 hides.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @ActiveProfiles

**Package:** `org.springframework.test.context.ActiveProfiles`
**Applies to:** Test class

#### What it does
Activates **Spring profiles** for the test context, so `application-<profile>.properties` and `@Profile` beans are used.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` / `profiles` | `String[]` | `{}` | Profiles to activate. |
| `resolver` | `Class<? extends ActiveProfilesResolver>` | `ActiveProfilesResolver.class` | Compute profiles programmatically (e.g. from an environment variable). |
| `inheritProfiles` | `boolean` | `true` | Whether profiles from superclasses are inherited. |

#### Example

```java
@SpringBootTest
@ActiveProfiles({"test", "no-mail"})        // loads application-test.properties
class ReportServiceTest { ... }
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @TestPropertySource

**Package:** `org.springframework.test.context.TestPropertySource`
**Applies to:** Test class

#### What it does
Adds **properties with the highest priority** (above `application.properties`, environment variables and system properties) for the test context, inline or from files.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` / `locations` | `String[]` | `{}` | Property files, e.g. `"classpath:test-overrides.properties"`. |
| `properties` | `String[]` | `{}` | Inline properties: `"key=value"` (or text blocks with several lines, Spring 6.1+). Override properties from `locations`. |
| `inheritLocations` | `boolean` | `true` | Inherit `locations` from superclasses. |
| `inheritProperties` | `boolean` | `true` | Inherit `properties` from superclasses. |
| `encoding` | `String` | `""` | Encoding of the files (Spring 6.1+). |
| `factory` | `Class<? extends PropertySourceFactory>` | `PropertySourceFactory.class` | Factory for parsing files (Spring 6.1+), e.g. for YAML. |

#### Example

```java
@SpringBootTest
@TestPropertySource(
        locations = "classpath:test-overrides.properties",
        properties = {
                "app.mail.enabled=false",
                "spring.task.scheduling.pool.size=1"
        })
class MailDisabledTest { ... }
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @DynamicPropertySource

**Package:** `org.springframework.test.context.DynamicPropertySource`
**Applies to:** `static` method in a test class

#### What it does
Registers properties whose values are only known **at runtime**, typically the URL/port of a **Testcontainers** container started for the test.

#### Attributes
None. The method takes a `DynamicPropertyRegistry` parameter.

#### Example

```java
@SpringBootTest
@Testcontainers
class KafkaIntegrationTest {

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("apache/kafka:3.8.0"));

    @DynamicPropertySource
    static void kafkaProps(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }
}
```

> For supported containers (PostgreSQL, MySQL, Kafka, Redis, Mongo...), Spring Boot 3.1+'s **`@ServiceConnection`** on the container field does this automatically.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @TestConfiguration

**Package:** `org.springframework.boot.test.context.TestConfiguration`
**Applies to:** Class (nested static class in a test, or a separate class)

#### What it does
A `@Configuration` for **tests only**: defines extra or replacement beans. A **nested** static `@TestConfiguration` class is picked up automatically; a top-level one must be `@Import`ed. It is not picked up by the application's component scan.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Bean name. |
| `proxyBeanMethods` | `boolean` | `true` | Same as `@Configuration`. |

#### Example

```java
@SpringBootTest
class InvoiceServiceTest {

    @TestConfiguration
    static class FixedClockConfig {
        @Bean
        @Primary
        Clock testClock() {
            return Clock.fixed(Instant.parse("2026-10-08T10:00:00Z"), ZoneOffset.UTC);
        }
    }

    @Autowired InvoiceService invoiceService;

    @Test
    void usesFixedDate() {
        assertThat(invoiceService.create(order()).getIssueDate()).isEqualTo(LocalDate.of(2026, 10, 8));
    }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Sql

**Package:** `org.springframework.test.context.jdbc.Sql`
**Applies to:** Test class, test method (repeatable)

#### What it does
Runs **SQL scripts** before or after a test, to insert test data or clean tables.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` / `scripts` | `String[]` | `{}` | Script paths. Empty → `<TestClass>.sql` or `<TestClass>.<method>.sql` next to the test. |
| `statements` | `String[]` | `{}` | Inline SQL statements. |
| `executionPhase` | `ExecutionPhase` | `BEFORE_TEST_METHOD` | `BEFORE_TEST_METHOD`, `AFTER_TEST_METHOD`, `BEFORE_TEST_CLASS`, `AFTER_TEST_CLASS` (class phases since 6.1). |
| `config` | `SqlConfig` | `@SqlConfig` | Data source, transaction mode, separators, comment prefixes, error mode. |

#### Example

```java
@DataJpaTest
@Sql("/sql/departments.sql")                                   // before each test
class EmployeeQueriesTest {

    @Test
    @Sql(statements = "INSERT INTO employees(id, name, department_id) VALUES (10, 'Asha', 1)")
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void countsEmployees() { ... }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @DirtiesContext

**Package:** `org.springframework.test.annotation.DirtiesContext`
**Applies to:** Test class, test method

#### What it does
Marks the cached application context as **dirty**, so it is **closed and rebuilt** for the next test. Use it only when a test changes shared state that can't be reset otherwise (singleton bean state, static caches). It slows the suite down.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `classMode` | `ClassMode` | `AFTER_CLASS` | For class level: `BEFORE_CLASS`, `BEFORE_EACH_TEST_METHOD`, `AFTER_EACH_TEST_METHOD`, `AFTER_CLASS`. |
| `methodMode` | `MethodMode` | `AFTER_METHOD` | For method level: `BEFORE_METHOD`, `AFTER_METHOD`. |
| `hierarchyMode` | `HierarchyMode` | `EXHAUSTIVE` | For context hierarchies: `EXHAUSTIVE` or `CURRENT_LEVEL`. |

#### Example

```java
@SpringBootTest
class FeatureToggleTest {

    @Test
    @DirtiesContext                       // this test mutates a singleton; rebuild context afterwards
    void togglesFeature() {
        featureRegistry.enable("beta");
        ...
    }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @WithMockUser

**Package:** `org.springframework.security.test.context.support.WithMockUser` (`spring-security-test`)
**Applies to:** Test class, test method

#### What it does
Runs the test with a **fake authenticated user** in the `SecurityContext`, so secured endpoints and `@PreAuthorize` methods can be tested without real login.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` / `username` | `String` | `"user"` | Username. |
| `password` | `String` | `"password"` | Password. |
| `roles` | `String[]` | `{"USER"}` | Roles (prefixed with `ROLE_` automatically). |
| `authorities` | `String[]` | `{}` | Exact authorities (can't be combined with `roles`). |
| `setupBefore` | `TestExecutionEvent` | `TEST_METHOD` | When the security context is set up. |

#### Example

```java
@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired MockMvc mvc;
    @MockitoBean AdminService adminService;

    @Test
    @WithMockUser(username = "boss", roles = "ADMIN")
    void adminCanAccess() throws Exception {
        mvc.perform(get("/api/admin/stats")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser                         // ROLE_USER
    void userIsForbidden() throws Exception {
        mvc.perform(get("/api/admin/stats")).andExpect(status().isForbidden());
    }

    @Test
    void anonymousIsUnauthorized() throws Exception {
        mvc.perform(get("/api/admin/stats")).andExpect(status().isUnauthorized());
    }
}
```

> Related: `@WithUserDetails("email@x.com")` loads a real user from your `UserDetailsService`; `@WithAnonymousUser` runs as anonymous.

[⬆ Back to Table of Contents](#table-of-contents)

---

# 17. Lombok — commonly used with Spring Boot

These are **Lombok** annotations, not Spring annotations, but you'll see them in almost every Spring project. Lombok is an **annotation processor** that generates boilerplate code (getters, constructors, builders, loggers) **at compile time**.

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

Install the Lombok plugin in your IDE (built into IntelliJ) and enable annotation processing. With the Spring Boot Maven plugin, Lombok is excluded from the final jar automatically.

## 17.1 Accessors & Data Classes

### @Getter / @Setter

**Package:** `lombok.Getter`, `lombok.Setter`
**Applies to:** Class (all non-static fields), field

#### What they do
Generate `getX()` / `isX()` (for `boolean`) and `setX(...)` methods. On a class, they apply to every non-static field (`@Setter` skips `final` fields).

#### Attributes

| Attribute | Annotation | Type | Default | Description |
|-----------|------------|------|---------|-------------|
| `value` | both | `AccessLevel` | `PUBLIC` | Visibility: `PUBLIC`, `PROTECTED`, `PACKAGE`, `PRIVATE`, `NONE` (skip this field). |
| `onMethod` | both | annotations | `{}` | Annotations to put on the generated method (`onMethod_ = {@JsonIgnore}`). |
| `onParam` | `@Setter` | annotations | `{}` | Annotations to put on the setter parameter. |
| `lazy` | `@Getter` | `boolean` | `false` | Compute the (`private final`) field value once on first access, thread-safe. |

#### Example

```java
@Getter
@Setter
public class Customer {
    private Long id;
    private String name;
    private boolean active;                 // isActive() / setActive()

    @Setter(AccessLevel.NONE)               // no setter for this one
    private Instant createdAt;

    @Getter(AccessLevel.PROTECTED)
    private String internalCode;

    @Getter(lazy = true)
    private final Map<String, String> heavyLookup = loadLookup();   // computed on first getHeavyLookup()
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Data

**Package:** `lombok.Data`
**Applies to:** Class

#### What it does
A shortcut for a mutable "data class": **`@Getter` + `@Setter` + `@RequiredArgsConstructor` + `@ToString` + `@EqualsAndHashCode`**.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `staticConstructor` | `String` | `""` | Generate a static factory method with this name (and make the constructor private), e.g. `"of"`. |

#### Example

```java
@Data
public class AddressForm {
    private String street;
    private String city;
    private String zip;
}

// Generated: getters, setters, toString(), equals(), hashCode(), constructor for final fields
```

#### Common mistakes / best practices
- **Don't use `@Data` on JPA entities.** Its `equals/hashCode` use all fields (they change when the ID is generated, breaking `Set`s), and `toString`/`hashCode` touch **lazy** relationships (extra queries, `LazyInitializationException`, infinite recursion in bidirectional relations). Use `@Getter`/`@Setter` and write `equals/hashCode` on the ID or a natural key.
- For immutable DTOs prefer Java **records** (or Lombok `@Value`).

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Value (Lombok)

**Package:** `lombok.Value`
**Applies to:** Class

#### What it does
The **immutable** version of `@Data`: makes the class `final`, all fields `private final`, and generates getters, an all-args constructor, `toString`, `equals` and `hashCode` (no setters).

> ⚠️ Name clash with **Spring's `@Value`** (`org.springframework.beans.factory.annotation.Value`, which injects properties). Check the import. In Java 16+, a **`record`** is usually the better choice.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `staticConstructor` | `String` | `""` | Generate a static factory method (e.g. `"of"`) and make the constructor private. |

#### Example

```java
@Value
public class Money {
    BigDecimal amount;          // becomes private final
    String currency;
}

Money m = new Money(new BigDecimal("10.00"), "INR");
m.getAmount();

// Equivalent record:
public record Money(BigDecimal amount, String currency) { }
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 17.2 Constructors

### @NoArgsConstructor

**Package:** `lombok.NoArgsConstructor`
**Applies to:** Class

#### What it does
Generates a constructor **with no parameters**. Required by JPA entities and by Jackson for classes deserialized with setters.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `access` | `AccessLevel` | `PUBLIC` | Constructor visibility (`PROTECTED` is ideal for JPA entities). |
| `force` | `boolean` | `false` | `true` → allowed even with `final` fields (initialized to `0`/`false`/`null`). |
| `staticName` | `String` | `""` | Generate a static factory method instead of a public constructor. |
| `onConstructor` | annotations | `{}` | Annotations for the generated constructor. |

#### Example

```java
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)    // JPA needs it, application code shouldn't use it
public class Product {
    @Id @GeneratedValue private Long id;
    private String name;

    public Product(String name) { this.name = name; }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @AllArgsConstructor

**Package:** `lombok.AllArgsConstructor`
**Applies to:** Class

#### What it does
Generates a constructor with **one parameter per field** (in declaration order).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `access` | `AccessLevel` | `PUBLIC` | Constructor visibility. |
| `staticName` | `String` | `""` | Generate a static factory method, e.g. `"of"`. |
| `onConstructor` | annotations | `{}` | Annotations for the generated constructor. |

#### Example

```java
@Getter
@AllArgsConstructor(staticName = "of")
public class Coordinates {
    private final double lat;
    private final double lng;
}

Coordinates c = Coordinates.of(18.52, 73.85);
```

> Reordering fields silently changes the constructor's parameter order; with same-typed fields (`double lat, double lng`) callers won't notice. Prefer `@Builder` for many fields.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @RequiredArgsConstructor

**Package:** `lombok.RequiredArgsConstructor`
**Applies to:** Class

#### What it does
Generates a constructor for all **`final` fields** and fields marked `@NonNull` that aren't initialized. In Spring it's the standard way to write **constructor injection** without boilerplate.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `access` | `AccessLevel` | `PUBLIC` | Constructor visibility. |
| `staticName` | `String` | `""` | Generate a static factory method. |
| `onConstructor` | annotations | `{}` | Annotations for the generated constructor (`onConstructor_ = @Autowired`). |

#### Example

```java
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;      // injected
    private final PaymentGateway paymentGateway;        // injected
    private final ApplicationEventPublisher events;     // injected

    private int retries = 3;                             // not final → not in constructor
}
```

**Using `@Qualifier` / `@Value` with it** (`lombok.config` at the project root):

```properties
lombok.copyableAnnotations += org.springframework.beans.factory.annotation.Qualifier
lombok.copyableAnnotations += org.springframework.beans.factory.annotation.Value
```

```java
@Service
@RequiredArgsConstructor
public class CheckoutService {
    @Qualifier("stripe")
    private final PaymentGateway gateway;            // qualifier copied to the constructor parameter
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 17.3 Builders

### @Builder

**Package:** `lombok.Builder`
**Applies to:** Class, constructor, static method

#### What it does
Generates the **Builder pattern**: `Order.builder().customer("A").total(x).build()`. Readable object creation with many (optional) fields, without telescoping constructors.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `builderMethodName` | `String` | `"builder"` | Name of the static method that creates the builder. |
| `buildMethodName` | `String` | `"build"` | Name of the method that creates the object. |
| `builderClassName` | `String` | `""` (`<Type>Builder`) | Name of the generated builder class. |
| `toBuilder` | `boolean` | `false` | `true` → generate `toBuilder()` to copy an instance and change some fields. |
| `access` | `AccessLevel` | `PUBLIC` | Visibility of the builder class/method. |
| `setterPrefix` | `String` | `""` | Prefix for builder methods, e.g. `"with"` → `withName(...)`. |

**`@Builder.Default`**: keeps a field initializer as the default value when the builder doesn't set it (without it, the builder sets the field to `null`/`0`).
**`@Singular`**: on a collection field, generates methods to add **one element at a time** (`.item(x).item(y)`), plus `clearItems()`.

#### Example

```java
@Getter
@Builder(toBuilder = true)
public class EmailMessage {
    private final String to;
    private final String subject;
    private final String body;

    @Builder.Default
    private final boolean html = false;            // default kept when not set

    @Builder.Default
    private final Instant createdAt = Instant.now();

    @Singular
    private final List<String> attachments;        // .attachment("a.pdf").attachment("b.png")
}

EmailMessage msg = EmailMessage.builder()
        .to("user@example.com")
        .subject("Invoice")
        .body("<p>Thanks!</p>")
        .html(true)
        .attachment("invoice.pdf")
        .build();

EmailMessage copy = msg.toBuilder().to("other@example.com").build();
```

#### Common mistakes / best practices
- Forgetting `@Builder.Default` → field initializers are **ignored** by the builder (e.g. a list becomes `null`).
- `@Builder` alone creates an all-args constructor and **no no-args constructor**; JPA entities and Jackson may need `@NoArgsConstructor` + `@AllArgsConstructor` too.
- For Jackson deserialization through the builder, add `@Jacksonized` (Lombok 1.18.14+).

[⬆ Back to Table of Contents](#table-of-contents)

---

### @SuperBuilder

**Package:** `lombok.experimental.SuperBuilder`
**Applies to:** Class (every class in the hierarchy)

#### What it does
A builder that supports **inheritance**: the subclass builder can also set the parent's fields. Plain `@Builder` can't do that. Every class in the hierarchy must have `@SuperBuilder`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `builderMethodName` | `String` | `"builder"` | Static method name. |
| `buildMethodName` | `String` | `"build"` | Build method name. |
| `toBuilder` | `boolean` | `false` | Generate `toBuilder()` (all classes must set it). |
| `setterPrefix` | `String` | `""` | Prefix for builder methods. |

#### Example

```java
@Getter
@SuperBuilder
public abstract class BaseEvent {
    private final String id;
    private final Instant occurredAt;
}

@Getter
@SuperBuilder
public class OrderShippedEvent extends BaseEvent {
    private final Long orderId;
    private final String trackingNo;
}

OrderShippedEvent e = OrderShippedEvent.builder()
        .id(UUID.randomUUID().toString())     // parent field
        .occurredAt(Instant.now())            // parent field
        .orderId(42L)
        .trackingNo("TRK-9")
        .build();
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @With

**Package:** `lombok.With`
**Applies to:** Class, field

#### What it does
Generates **`withX(value)`** methods that return a **copy** of an immutable object with one field changed. Requires an all-args constructor.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `AccessLevel` | `PUBLIC` | Visibility of the generated methods. |
| `onMethod` / `onParam` | annotations | `{}` | Annotations for the generated method / parameter. |

#### Example

```java
@Value
@With
public class Settings {
    String theme;
    String language;
}

Settings s1 = new Settings("light", "en");
Settings s2 = s1.withTheme("dark");      // new object; s1 unchanged
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 17.4 Object Methods

### @ToString

**Package:** `lombok.ToString`
**Applies to:** Class

#### What it does
Generates `toString()` listing the fields: `Customer(id=1, name=Asha)`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `includeFieldNames` | `boolean` | `true` | Print `name=value` (or only values). |
| `callSuper` | `boolean` | `false` | Include the superclass's `toString()`. |
| `doNotUseGetters` | `boolean` | `false` | Read fields directly instead of via getters. |
| `onlyExplicitlyIncluded` | `boolean` | `false` | Only fields marked `@ToString.Include`. |
| `exclude` / `of` | `String[]` | `{}` | Legacy field lists; prefer `@ToString.Exclude` / `@ToString.Include` on fields. |

#### Example

```java
@Getter
@ToString
public class User {
    private Long id;
    private String username;

    @ToString.Exclude
    private String passwordHash;                 // never logged

    @ToString.Exclude
    @OneToMany(mappedBy = "user")
    private List<Order> orders;                  // avoid lazy loading / recursion
}
// User(id=1, username=asha)
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @EqualsAndHashCode

**Package:** `lombok.EqualsAndHashCode`
**Applies to:** Class

#### What it does
Generates `equals()` and `hashCode()` from the fields.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `callSuper` | `boolean` | `false` | Include the superclass's `equals/hashCode` (set `true` when extending a class with state). |
| `onlyExplicitlyIncluded` | `boolean` | `false` | Use only fields marked `@EqualsAndHashCode.Include`. |
| `doNotUseGetters` | `boolean` | `false` | Read fields directly. |
| `cacheStrategy` | `CacheStrategy` | `NEVER` | `LAZY` → cache the hash code (only for immutable objects). |
| `exclude` / `of` | `String[]` | `{}` | Legacy field lists; prefer `@EqualsAndHashCode.Exclude` / `.Include`. |

#### Example

```java
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Book {
    @EqualsAndHashCode.Include
    private String isbn;                  // business key decides equality

    private String title;
    private BigDecimal price;
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 17.5 Null Checks, Logging & Utilities

### @NonNull

**Package:** `lombok.NonNull`
**Applies to:** Field, parameter

#### What it does
Generates a **null check**: on a parameter (or a field set by Lombok-generated constructors/setters), passing `null` throws `NullPointerException("x is marked non-null but is null")` immediately.

#### Attributes
None.

#### Example

```java
public void rename(@NonNull String newName) {
    // generated: if (newName == null) throw new NullPointerException("newName is marked non-null but is null");
    this.name = newName;
}

@RequiredArgsConstructor
public class Greeter {
    @NonNull private String greeting;     // included in the constructor and null-checked
}
```

> This is a runtime check, not validation. For request DTOs use Bean Validation's `@NotNull`.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Slf4j

**Package:** `lombok.extern.slf4j.Slf4j`
**Applies to:** Class

#### What it does
Creates a logger field:
```java
private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MyClass.class);
```
SLF4J with Logback is Spring Boot's default logging setup, so `@Slf4j` is the logger annotation to use.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `topic` | `String` | `""` | Logger name. Empty → the class name. |

#### Example

```java
@Slf4j
@Service
public class PaymentService {
    public void pay(Long orderId, BigDecimal amount) {
        log.info("Paying order {} amount {}", orderId, amount);     // {} placeholders, no string concat
        try {
            gateway.charge(amount);
        } catch (GatewayException e) {
            log.error("Payment failed for order {}", orderId, e);   // exception as last argument
            throw e;
        }
    }
}

@Slf4j(topic = "AUDIT")
public class AuditLogger { }       // logger named "AUDIT"
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @Log

**Package:** `lombok.extern.java.Log`
**Applies to:** Class

#### What it does
Creates a **`java.util.logging`** (JUL) logger: `private static final java.util.logging.Logger log = ...`. In Spring Boot prefer **`@Slf4j`**; JUL output goes through a bridge and has a less convenient API. Other variants: `@Log4j2`, `@CommonsLog`, `@JBossLog`, `@Flogger`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `topic` | `String` | `""` | Logger name. |

#### Example

```java
@Log
public class LegacyTool {
    void run() {
        log.info("Started");
        log.warning("Low disk space");    // JUL levels: severe, warning, info, fine...
    }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

### @UtilityClass

**Package:** `lombok.experimental.UtilityClass`
**Applies to:** Class

#### What it does
Turns a class into a **utility class**: makes it `final`, adds a private constructor that throws, and makes **all fields, methods and inner classes `static`**.

#### Attributes
None. (Experimental feature.)

#### Example

```java
@UtilityClass
public class MoneyUtils {
    public final BigDecimal GST_RATE = new BigDecimal("0.18");       // becomes static

    public BigDecimal withGst(BigDecimal amount) {                     // becomes static
        return amount.add(amount.multiply(GST_RATE)).setScale(2, RoundingMode.HALF_UP);
    }
}

BigDecimal total = MoneyUtils.withGst(new BigDecimal("100"));         // 118.00
```

> Static imports of members from a `@UtilityClass` don't work in some IDEs/compilers; call them through the class name.

[⬆ Back to Table of Contents](#table-of-contents)

---

### @SneakyThrows

**Package:** `lombok.SneakyThrows`
**Applies to:** Method, constructor

#### What it does
Lets a method throw **checked exceptions without declaring them** in `throws` (Lombok tricks the compiler). Handy inside lambdas, but it hides exceptions from callers.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `Class<? extends Throwable>[]` | `Throwable.class` | Exception types to sneakily throw. |

#### Example

```java
@SneakyThrows(IOException.class)
public String readTemplate(Path path) {
    return Files.readString(path);           // IOException not declared
}
```

> Careful with `@Transactional`: a sneaky **checked** exception does **not** trigger rollback by default.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 17.6 Lombok with Spring & JPA: Recommendations

| Use case | Recommended |
|----------|-------------|
| Spring beans (services, controllers) | `@RequiredArgsConstructor` + `@Slf4j` |
| JPA entities | `@Getter`, `@Setter` (selectively), `@NoArgsConstructor(access = PROTECTED)`, `@ToString.Exclude` on relations. **No `@Data`, no default `@EqualsAndHashCode`.** |
| Request/response DTOs | Java `record`s (or `@Value` / `@Builder` + `@Jacksonized`) |
| Configuration properties | `record` with `@ConfigurationProperties` |
| Builders for complex objects | `@Builder` + `@Builder.Default` |

[⬆ Back to Table of Contents](#table-of-contents)
