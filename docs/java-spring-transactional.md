# Spring `@Transactional` — Complete Reference Guide

> A deep-dive into Spring's transaction management: when transactions commit, how propagation works, rollback rules, and real-world patterns.

[⬅ Back to Spring Boot Annotations Guide](java-spring-boot-document-with-full-details.md#table-of-contents)

> **Version note:** Written for **Spring Boot 3.x / Spring Framework 6.x**. Annotation package: `org.springframework.transaction.annotation.Transactional`.

---

## Table of Contents

1. [What Is a Transaction?](#what-is-a-transaction)
2. [How Spring Manages Transactions](#how-spring-manages-transactions)
3. [Transaction Commit Timing](#transaction-commit-timing)
4. [Scenario Walkthroughs](#scenario-walkthroughs)
5. [All @Transactional Attributes](#all-transactional-attributes)
   - [value / transactionManager](#value--transactionmanager)
   - [propagation](#propagation)
   - [isolation](#isolation)
   - [readOnly](#readonly)
   - [timeout / timeoutString](#timeout--timeoutstring)
   - [rollbackFor / rollbackForClassName](#rollbackfor--rollbackforclassname)
   - [noRollbackFor / noRollbackForClassName](#norollbackfor--norollbackforclassname)
   - [label](#label)
6. [Propagation Deep Dive](#propagation-deep-dive)
7. [Isolation Deep Dive](#isolation-deep-dive)
8. [Where to Put @Transactional](#where-to-put-transactional)
9. [Programmatic Transactions (TransactionTemplate)](#programmatic-transactions-transactiontemplate)
10. [@TransactionalEventListener](#transactionaleventlistener)
11. [@Transactional in Tests](#transactional-in-tests)
12. [Common Pitfalls](#common-pitfalls)
13. [Best Practices](#best-practices)
14. [Quick Reference Table](#quick-reference-table)

---

## What Is a Transaction?

A **database transaction** is a unit of work that is either committed (fully applied) or rolled back (fully undone). It follows the **ACID** principles:

| Property | Meaning |
|----------|---------|
| **A**tomicity | All operations succeed or all are rolled back |
| **C**onsistency | Database moves from one valid state to another |
| **I**solation | Concurrent transactions don't interfere |
| **D**urability | Committed data survives system failures |

---

## How Spring Manages Transactions

Spring wraps your bean in a **proxy** at runtime. When you call a `@Transactional` method, the proxy intercepts the call, opens a transaction, runs your method, then commits or rolls back.

```
[Caller]
   │
   ▼
[Spring Proxy] ←── intercepts the call
   │  - opens transaction
   │  - delegates to real bean
   ▼
[Your @Transactional Method]
   │  - executes business logic
   │  - calls repository / other services
   ▼
[Spring Proxy]
   │  - commits on success
   │  - rolls back on exception
   ▼
[Caller] ← returns result
```

> **Important**: `@Transactional` only works when the method is called **from outside the bean** (through the proxy).
> Self-invocation (`this.method()`) bypasses the proxy and has no transactional effect.
>
> **Method visibility:** `private` methods are never transactional. Since **Spring Framework 6.0**, `protected` and package-private methods are also supported with class-based (CGLIB) proxies, which Spring Boot uses by default. `public` methods are still the safest choice and work with every proxy type.

Spring Boot auto-configures transaction management (`@EnableTransactionManagement` is applied for you) and picks the transaction manager from your dependencies:

| Dependency | Transaction manager |
|------------|---------------------|
| `spring-boot-starter-data-jpa` | `JpaTransactionManager` |
| `spring-boot-starter-jdbc` | `JdbcTransactionManager` (a `DataSourceTransactionManager`) |
| `spring-boot-starter-data-mongodb` | `MongoTransactionManager` (must be declared as a bean yourself) |

---

## Transaction Commit Timing

The golden rule:

> **The transaction commits when the outermost `@Transactional` method returns successfully.**

Everything before that point is **pending** — staged in the database session but not yet durable.

```
AuthController.register()              → no @Transactional
  └── AuthService.register()           → @Transactional ← TRANSACTION STARTS HERE
        ├── userRepository.save()      → SQL executed, NOT yet committed
        ├── other logic...
        └── return AuthResponse        → TRANSACTION COMMITS HERE ✓
```

---

## Scenario Walkthroughs

### Scenario 1 — Direct Repository Save

```
AuthController (no TX)
  │
  └── AuthService.register()   @Transactional → T1 BEGINS
        │
        ├── userRepository.save(user)          → INSERT staged in T1
        │
        └── return AuthResponse                → T1 COMMITS ✓
              │
              ▼
        AuthController receives response
```

**Key point**: The INSERT reaches the database only when `AuthService.register()` exits normally.

---

### Scenario 2 — Calling Another `@Transactional` Service (REQUIRED)

```
AuthController (no TX)
  │
  └── AuthService.register()   @Transactional(REQUIRED) → T1 BEGINS
        │
        ├── ... build user ...
        │
        └── UserService.createUser()  @Transactional(REQUIRED)
              │                           ↑ JOINS T1 (no new transaction created)
              ├── existsByUsername(...)   → SELECT (part of T1)
              ├── existsByEmail(...)      → SELECT (part of T1)
              └── userRepository.save()  → INSERT staged in T1
                    │
                    ▼
              returns to AuthService
        │
        └── return AuthResponse          → T1 COMMITS ✓
              │
              ▼
        AuthController receives response
```

**Key point**: Since both methods use `REQUIRED` (the default), `UserService` joins the existing `T1`. There is only **one** transaction. If `UserService` throws, the entire T1 rolls back.

---

### Scenario 3 — `UserService` throws mid-way

```
AuthController
  │
  └── AuthService.register()   T1 BEGINS
        │
        ├── someLogic()             → writes to DB (staged)
        └── UserService.createUser()
              ├── existsByUsername() → finds duplicate
              └── throw RuntimeException("Username already exists")
                    │
                    ▼
              Exception bubbles up to AuthService
        │
        └── Exception propagates out of AuthService
              │
              ▼
        T1 ROLLS BACK ✗  ← everything undone
              │
              ▼
        AuthController gets exception
```

---

## All `@Transactional` Attributes

```java
@Transactional(
    transactionManager     = "",                    // alias: value
    propagation            = Propagation.REQUIRED,
    isolation              = Isolation.DEFAULT,
    readOnly               = false,
    timeout                = -1,                    // TransactionDefinition.TIMEOUT_DEFAULT
    timeoutString          = "",
    rollbackFor            = {},
    rollbackForClassName   = {},
    noRollbackFor          = {},
    noRollbackForClassName = {},
    label                  = {}
)
```

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` / `transactionManager` | `String` | `""` | Bean name (or qualifier) of the transaction manager to use. Empty → the default/primary one. |
| `propagation` | `Propagation` | `REQUIRED` | How the method behaves when a transaction already exists. |
| `isolation` | `Isolation` | `DEFAULT` | Isolation level of a **new** transaction. |
| `readOnly` | `boolean` | `false` | Optimization hint for read-only work. |
| `timeout` | `int` | `-1` | Timeout in seconds for a **new** transaction (`-1` = use the underlying system's default). |
| `timeoutString` | `String` | `""` | Same as `timeout`, but as a String so it can use a placeholder like `"${app.tx.timeout}"`. |
| `rollbackFor` | `Class<? extends Throwable>[]` | `{}` | Extra exception types that cause rollback. |
| `rollbackForClassName` | `String[]` | `{}` | Same as `rollbackFor`, using class name patterns. |
| `noRollbackFor` | `Class<? extends Throwable>[]` | `{}` | Exception types that must **not** cause rollback. |
| `noRollbackForClassName` | `String[]` | `{}` | Same as `noRollbackFor`, using class name patterns. |
| `label` | `String[]` | `{}` | Free-form labels describing the transaction, for custom transaction managers / monitoring. |

> `isolation`, `timeout` and `readOnly` only take effect when a **new** transaction starts (`REQUIRED` with no existing transaction, `REQUIRES_NEW`, `NESTED` at the outer level). A method that **joins** an existing transaction uses that transaction's settings.

---

### `value` / `transactionManager`

Selects which `TransactionManager` bean to use. Needed only when the application has **more than one** (e.g. two databases, or JPA + MongoDB).

```java
@Configuration
public class TxConfig {

    @Bean
    @Primary
    public PlatformTransactionManager ordersTxManager(EntityManagerFactory ordersEmf) {
        return new JpaTransactionManager(ordersEmf);
    }

    @Bean
    public PlatformTransactionManager reportingTxManager(DataSource reportingDataSource) {
        return new JdbcTransactionManager(reportingDataSource);
    }
}

@Service
public class ReportService {

    // Uses the "reportingTxManager" bean instead of the @Primary one
    @Transactional(transactionManager = "reportingTxManager", readOnly = true)
    public List<SalesRow> monthlySales() { ... }

    // value is an alias, so this is the same
    @Transactional("reportingTxManager")
    public void refreshSnapshot() { ... }
}
```

> A transaction is bound to **one** transaction manager. Writing to two databases in one `@Transactional` method does **not** make it atomic across both; that requires distributed transactions (JTA) or patterns like Outbox/Saga.

---

### `propagation`

Controls **what happens when a transactional method is called from another transactional context**.  
See [Propagation Deep Dive](#propagation-deep-dive) below.

---

### `isolation`

Controls **how much one transaction is isolated from others** running concurrently.  
See [Isolation Deep Dive](#isolation-deep-dive) below.

---

### `readOnly`

```java
@Transactional(readOnly = true)
public List<User> getAllUsers() {
    return userRepository.findAll();
}
```

| Aspect | Detail |
|--------|--------|
| **Default** | `false` |
| **Effect** | Tells the persistence provider to skip dirty checking and flush; hints to JDBC driver for potential optimizations |
| **Does NOT prevent writes** | It's an optimization hint, not a hard constraint (depends on driver) |
| **Use for** | All query-only methods, reporting, read-heavy endpoints |
| **Performance gain** | Hibernate skips entity snapshot comparison; some databases optimize read-only connections |

```
AuthController
  └── UserService.getAllUsers()  @Transactional(readOnly=true) → T1 BEGINS (read-only)
        └── userRepository.findAll()  → SELECT
              │
              └── return List<User>  → T1 COMMITS (no writes, nothing to flush)
```

---

### `timeout` / `timeoutString`

```java
@Transactional(timeout = 30) // seconds
public void longRunningOperation() {
    // If the transaction runs longer than 30 seconds, the next database call fails and T1 rolls back
}

// Configurable via application.properties: app.tx.import-timeout=120
@Transactional(timeoutString = "${app.tx.import-timeout}")
public void importData() { ... }
```

| Aspect | Detail |
|--------|--------|
| **Default** | `-1` (use the underlying transaction system's default, usually no limit) |
| **Unit** | Seconds |
| **How it is enforced** | Spring sets a deadline when the transaction starts. Each JDBC statement gets a query timeout of the **remaining** time, and Spring checks the deadline before each statement. |
| **On timeout** | `TransactionTimedOutException` (or a JDBC query-timeout exception) is thrown, which triggers rollback |
| **Not interrupted** | Pure Java work (loops, HTTP calls) is **not** stopped; the timeout is noticed at the next database access |
| **Use for** | Preventing long-running transactions from holding locks |

```
T1 BEGINS (timeout = 30s)
  ├── 0s  - SELECT ...           (query timeout = 30s)
  ├── 25s - UPDATE ...           (query timeout = 5s)
  ├── 31s - INSERT ...  ← deadline passed → TransactionTimedOutException
  └── T1 ROLLS BACK ✗
```

Global default for all transactions: `spring.transaction.default-timeout=30s`.

---

### `rollbackFor` / `rollbackForClassName`

By default, Spring **only rolls back** for unchecked exceptions (`RuntimeException` and `Error`).  
Checked exceptions are **committed by default**.

```java
// Default behavior
@Transactional
public void method() throws IOException {
    repo.save(entity);
    throw new IOException("checked"); // ← transaction COMMITS even though exception thrown
}

// Force rollback for checked exception
@Transactional(rollbackFor = IOException.class)
public void method() throws IOException {
    repo.save(entity);
    throw new IOException("checked"); // ← transaction ROLLS BACK ✗
}

// Using class name (string-based, useful for cross-module references)
@Transactional(rollbackForClassName = {"java.io.IOException", "com.example.CustomBusinessException"})
public void method() { ... }

// Multiple exceptions
@Transactional(rollbackFor = {IOException.class, CustomException.class})
public void method() { ... }
```

**Decision tree**:
```
Exception thrown
  ├── Is it a RuntimeException or Error?
  │     └── YES → ROLLBACK (always, unless noRollbackFor overrides)
  └── Is it a checked Exception?
        ├── Listed in rollbackFor? → YES → ROLLBACK
        └── Not listed?           → NO  → COMMIT
```

> **Class name patterns match by substring.** `rollbackForClassName = "Exception"` matches almost every exception, and `"IOException"` also matches `UncheckedIOException`. Prefer `rollbackFor` with real classes, or use fully-qualified names.

**Roll back on every exception by default (Spring Framework 6.2+):**

```java
@Configuration
@EnableTransactionManagement(rollbackOn = RollbackOn.ALL_EXCEPTIONS)
public class TxConfig { }
// Now checked exceptions also roll back, without rollbackFor on each method
```

---

### `noRollbackFor` / `noRollbackForClassName`

Prevent rollback for specific exceptions that would normally trigger one.

```java
// ValidationException is RuntimeException, normally causes rollback
// But here we want to commit audit logs even if validation fails
@Transactional(noRollbackFor = ValidationException.class)
public void processWithAudit() {
    auditRepository.save(auditEntry); // this WILL be committed
    if (!isValid()) {
        throw new ValidationException("Invalid input"); // TX still commits!
    }
}

// Using class name
@Transactional(noRollbackForClassName = "ValidationException")
public void method() { ... }
```

**When to use**: When you want partial commits — e.g., saving an audit/log entry regardless of business rule failures.

> `noRollbackFor` only works on the method that **starts** the transaction. If an inner `REQUIRED` method throws an exception that is not excluded **on that inner method**, the transaction is already marked rollback-only (see [Pitfall 4](#4-transaction-marked-rollback-only)).

---

### `label`

Descriptive labels attached to the transaction definition (Spring Framework 5.3+). Spring's standard transaction managers **ignore** them; custom `TransactionManager` implementations or monitoring code can read them via `TransactionDefinition.getLabels()`.

```java
@Transactional(label = {"batch", "priority:low"})
public void nightlyCleanup() { ... }
```

---

## Propagation Deep Dive

### `REQUIRED` (default)

```java
@Transactional(propagation = Propagation.REQUIRED)
```

**Rule**: Use existing transaction. Create new if none exists.

```
Case A: Caller HAS a transaction
──────────────────────────────────────────────
Caller (T1) → Method (joins T1) → Commits with T1

Case B: Caller has NO transaction
──────────────────────────────────────────────
Caller (no TX) → Method (creates T1) → T1 commits when method returns

Your code:
AuthController (no TX)
  └── AuthService @Transactional(REQUIRED)  → T1 created
        └── UserService @Transactional(REQUIRED) → joins T1
              └── save() → INSERT in T1
        T1 commits when AuthService returns ✓
```

**Rollback behavior**: If UserService throws, T1 is marked for rollback. Even if AuthService catches the exception, the transaction is poisoned — it will NOT commit. Spring sets a `rollback-only` flag.

---

### `REQUIRES_NEW`

```java
@Transactional(propagation = Propagation.REQUIRES_NEW)
```

**Rule**: Always create a brand-new transaction. Suspend any existing one.

```
AuthController (no TX)
  └── AuthService @Transactional(REQUIRED) → T1 BEGINS
        │
        └── AuditService @Transactional(REQUIRES_NEW) → T1 SUSPENDED
              │                                          T2 BEGINS
              ├── auditRepo.save(log)   → INSERT in T2
              └── returns               → T2 COMMITS ✓
                    │
              T1 RESUMES
        │
        └── ... more work in T1 ...
        T1 COMMITS ✓ (or rolls back independently)
```

**Key difference from REQUIRED**:
- T2 commits **immediately** when `AuditService` returns — regardless of what T1 does
- If T1 rolls back, T2's data is **still committed**
- If T2 rolls back, T1 is **not affected** (unless the exception propagates uncaught)

**Use case**: Audit logging, notification events, any "fire and forget" secondary operation that must persist regardless of the main transaction outcome.

> **Connection pool warning:** while T2 runs, T1 is suspended but still **holds its database connection**. Each `REQUIRES_NEW` level needs one more connection. Under load (or with nested `REQUIRES_NEW` calls) a small pool can deadlock waiting for connections. Size the pool accordingly, and don't call `REQUIRES_NEW` in loops.
>
> **Must be a different bean:** `REQUIRES_NEW` on a method called via `this.` is ignored (self-invocation), so the work silently stays in T1.

---

### `SUPPORTS`

```java
@Transactional(propagation = Propagation.SUPPORTS)
```

**Rule**: Participate in transaction if one exists. Run non-transactionally if none.

```
Case A: Caller HAS a transaction
──────────────────────────────────────────────
AuthService (T1) → UserService (SUPPORTS) → joins T1 → commits with T1

Case B: Caller has NO transaction
──────────────────────────────────────────────
Controller (no TX) → UserService (SUPPORTS) → runs without TX
  each SQL auto-commits individually
```

**Use case**: Utility/helper methods that work correctly in both transactional and non-transactional contexts. Read-only services that can participate if called within a transaction.

---

### `MANDATORY`

```java
@Transactional(propagation = Propagation.MANDATORY)
```

**Rule**: MUST be called within an existing transaction. Throws exception if no active transaction.

```
Case A: ✓ Valid
──────────────────────────────────────────────
AuthService (T1) → UserService (MANDATORY) → joins T1 → commits with T1

Case B: ✗ Invalid — throws exception
──────────────────────────────────────────────
Controller (no TX) → UserService (MANDATORY) 
  → throws IllegalTransactionStateException: "No existing transaction found"
```

**Use case**: Low-level DAOs or domain services that should **never** be called without a wrapping transaction. Acts as a safety net to enforce architectural rules.

---

### `NOT_SUPPORTED`

```java
@Transactional(propagation = Propagation.NOT_SUPPORTED)
```

**Rule**: Always run WITHOUT a transaction. Suspend current transaction if one exists.

```
AuthController (no TX)
  └── AuthService (T1 BEGINS)
        │
        └── ReportService @Transactional(NOT_SUPPORTED)
              │                → T1 SUSPENDED
              ├── slow query...  → runs non-transactionally, each SQL auto-commits
              └── returns        → T1 RESUMES
        │
        └── T1 COMMITS or ROLLS BACK (ReportService's work already auto-committed)
```

**Use case**: Long-running read queries or batch reads where holding a transaction open would be wasteful or cause lock contention. Bulk export operations.

---

### `NEVER`

```java
@Transactional(propagation = Propagation.NEVER)
```

**Rule**: Must NOT run within a transaction. Throws exception if one exists.

```
Case A: ✓ Valid
──────────────────────────────────────────────
Controller (no TX) → BatchJob (NEVER) → runs without TX → auto-commits each query

Case B: ✗ Invalid — throws exception
──────────────────────────────────────────────
AuthService (T1) → BatchJob (NEVER)
  → throws IllegalTransactionStateException: "Existing transaction found"
```

**Use case**: Methods that are explicitly designed to avoid transactional overhead, or for enforcing that a method is never accidentally wrapped in a transaction.

---

### `NESTED`

```java
@Transactional(propagation = Propagation.NESTED)
```

**Rule**: Run within a **nested transaction** (savepoint) if a transaction exists. Behaves like `REQUIRED` if no transaction exists.

```
AuthController (no TX)
  └── AuthService @Transactional(REQUIRED) → T1 BEGINS
        │
        ├── userRepo.save(user)          → INSERT in T1
        │
        └── OrderService @Transactional(NESTED)
              │                 → SAVEPOINT created within T1
              ├── orderRepo.save(order)  → INSERT in T1 (after savepoint)
              └── ✗ throws RuntimeException
                    │
                    ▼
              ROLLBACK TO SAVEPOINT (only order INSERT is undone)
              OrderService exception caught by AuthService
        │
        └── AuthService continues...
        T1 COMMITS ✓ — user INSERT preserved, order INSERT rolled back
```

**Key difference from REQUIRES_NEW**:
- `REQUIRES_NEW` creates a completely **independent** transaction
- `NESTED` creates a **savepoint inside** the existing transaction — commits only when the outer transaction commits

**Requires**: JDBC savepoint support (not all databases / transaction managers support this).

| Transaction manager | `NESTED` support |
|---------------------|------------------|
| `DataSourceTransactionManager` / `JdbcTransactionManager` (JDBC, `JdbcTemplate`) | ✅ Savepoints |
| `JpaTransactionManager` (Spring Data JPA / Hibernate) | ⚠️ Not allowed by default (`nestedTransactionAllowed = false`) → `NestedTransactionNotSupportedException`. Even when enabled, the savepoint only rolls back **JDBC** changes; the `EntityManager`'s cached entities are not rolled back. |
| `JtaTransactionManager` | ❌ Usually not supported |

> In a typical Spring Boot + JPA application, use `REQUIRES_NEW` (separate transaction) instead of `NESTED`.

---

### Propagation Comparison Table

| Propagation | No TX exists | TX exists | Rollback scope |
|-------------|-------------|-----------|----------------|
| `REQUIRED` | Creates new TX | Joins existing | Entire TX |
| `REQUIRES_NEW` | Creates new TX | Suspends, creates new | Own TX only |
| `SUPPORTS` | No TX (auto-commit) | Joins existing | Entire TX (if joined) |
| `MANDATORY` | **Exception** | Joins existing | Entire TX |
| `NOT_SUPPORTED` | No TX | Suspends TX, no TX | N/A |
| `NEVER` | No TX | **Exception** | N/A |
| `NESTED` | Creates new TX | Creates savepoint | Since savepoint |

---

## Isolation Deep Dive

> More on anomalies, per-database defaults, MVCC and locks: [Database Systems Guide](Database_systems.md#4-transaction-isolation-levels).

Isolation controls what **concurrent** transactions can see of each other's uncommitted or in-progress data.

### Isolation Problems

| Problem | Description |
|---------|-------------|
| **Dirty Read** | Reading data that another transaction has modified but not yet committed |
| **Non-Repeatable Read** | Reading same row twice gets different values because another TX committed between reads |
| **Phantom Read** | Re-running a query returns different rows because another TX inserted/deleted rows |

### Isolation Levels

#### `DEFAULT`
Uses the database default (PostgreSQL = READ_COMMITTED, MySQL InnoDB = REPEATABLE_READ).

#### `READ_UNCOMMITTED`
```java
@Transactional(isolation = Isolation.READ_UNCOMMITTED)
```
- Can read uncommitted changes from other transactions (dirty reads allowed)
- Fastest, but lowest consistency
- **Rarely used in production**

```
T1: UPDATE users SET balance = 9000 WHERE id = 1;  (not committed)
T2: SELECT balance FROM users WHERE id = 1;         → reads 9000 (dirty!)
T1: ROLLBACK;
T2: acted on data that never existed! ✗
```

#### `READ_COMMITTED`
```java
@Transactional(isolation = Isolation.READ_COMMITTED)
```
- Only reads committed data (no dirty reads)
- Same row may return different values in same transaction (non-repeatable reads possible)
- PostgreSQL default

```
T1: SELECT balance FROM users WHERE id = 1;  → 10000
T2: UPDATE ... SET balance = 9000; COMMIT;
T1: SELECT balance FROM users WHERE id = 1;  → 9000 (changed!)
```

#### `REPEATABLE_READ`
```java
@Transactional(isolation = Isolation.REPEATABLE_READ)
```
- Guarantees same row read twice returns same value
- Phantom reads still possible (new rows can appear)
- MySQL InnoDB default

```
T1: SELECT balance FROM users WHERE id = 1;   → 10000
T2: UPDATE ... SET balance = 9000; COMMIT;
T1: SELECT balance FROM users WHERE id = 1;   → 10000 (same! protected)
T1: SELECT COUNT(*) FROM users WHERE age > 20 → might change (phantoms)
```

#### `SERIALIZABLE`
```java
@Transactional(isolation = Isolation.SERIALIZABLE)
```
- Full isolation — transactions behave as if executed serially
- No dirty reads, non-repeatable reads, or phantom reads
- Heaviest locking, lowest concurrency, highest consistency

```
T1 and T2 both run, but effectively they run one after the other from a data perspective.
No anomalies possible, but performance cost is significant.
```

### Isolation Comparison Table

| Level | Dirty Read | Non-Repeatable Read | Phantom Read | Performance |
|-------|-----------|---------------------|--------------|-------------|
| READ_UNCOMMITTED | ✅ allowed | ✅ allowed | ✅ allowed | Fastest |
| READ_COMMITTED | ✗ prevented | ✅ allowed | ✅ allowed | Fast |
| REPEATABLE_READ | ✗ prevented | ✗ prevented | ✅ allowed | Medium |
| SERIALIZABLE | ✗ prevented | ✗ prevented | ✗ prevented | Slowest |

---

## Where to Put `@Transactional`

| Placement | Effect |
|-----------|--------|
| **Class** | Applies to every method of the bean that the proxy can intercept. |
| **Method** | Applies to that method; **overrides** class-level settings completely (attributes are not merged). |
| **Interface / interface method** | Works with JDK and CGLIB proxies since Spring 5. Annotating the concrete class is still recommended. |
| **Spring Data repository** | `SimpleJpaRepository` is already `@Transactional(readOnly = true)` at class level, and its write methods (`save`, `delete`...) are `@Transactional`. |

```java
@Service
@Transactional(readOnly = true)            // default for all methods: read-only
public class OrderService {

    public Order findById(Long id) { ... }            // read-only TX

    public List<Order> findRecent() { ... }           // read-only TX

    @Transactional                                     // overrides: read-write TX
    public Order place(OrderRequest request) { ... }

    @Transactional(rollbackFor = PaymentException.class, timeout = 10)
    public void pay(Long orderId) throws PaymentException { ... }
}
```

`jakarta.transaction.Transactional` (JTA) is also recognized by Spring, but it supports fewer options (`value` = TxType, `rollbackOn`, `dontRollbackOn`; no isolation, timeout or readOnly). Prefer Spring's annotation.

---

## Programmatic Transactions (`TransactionTemplate`)

Use code instead of the annotation when you need a transaction around **part** of a method, in a loop per item, or inside a method of the **same class** (avoids the self-invocation problem).

```java
@Service
public class ImportService {

    private final TransactionTemplate tx;
    private final ProductRepository productRepository;

    public ImportService(PlatformTransactionManager txManager, ProductRepository productRepository) {
        this.tx = new TransactionTemplate(txManager);
        this.tx.setTimeout(30);
        this.productRepository = productRepository;
    }

    // Each row commits in its own transaction; one bad row doesn't undo the others
    public ImportResult importAll(List<ProductRow> rows) {
        int ok = 0, failed = 0;
        for (ProductRow row : rows) {
            try {
                tx.executeWithoutResult(status -> productRepository.save(row.toEntity()));
                ok++;
            } catch (RuntimeException e) {
                failed++;                                  // only this row was rolled back
            }
        }
        return new ImportResult(ok, failed);
    }

    // Returning a value + manual rollback without throwing
    public Long createIfValid(Product p) {
        return tx.execute(status -> {
            Product saved = productRepository.save(p);
            if (saved.getPrice().signum() < 0) {
                status.setRollbackOnly();                  // roll back, return null
                return null;
            }
            return saved.getId();
        });
    }
}
```

Inside an annotated method you can also mark rollback without throwing:

```java
@Transactional
public void process() {
    // ...
    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
}
```

---

## `@TransactionalEventListener`

Runs an event listener **at a specific phase of the publishing transaction** — most commonly **after commit**, so emails, messages or cache updates happen only when the data is really saved.

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `phase` | `TransactionPhase` | `AFTER_COMMIT` | `BEFORE_COMMIT`, `AFTER_COMMIT`, `AFTER_ROLLBACK`, `AFTER_COMPLETION` (commit or rollback). |
| `fallbackExecution` | `boolean` | `false` | `true` → still run the listener if the event is published **without** a transaction (runs immediately). |
| `value` / `classes` | `Class<?>[]` | `{}` | Event types handled (normally inferred from the method parameter). |
| `condition` | `String` | `""` | SpEL condition, e.g. `"#event.amount > 1000"`. |
| `id` | `String` | `""` | Listener id. |

```java
public record UserRegisteredEvent(Long userId, String email) {}

@Service
public class AuthService {
    private final ApplicationEventPublisher events;
    // ...
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        User user = userRepository.save(new User(request));
        events.publishEvent(new UserRegisteredEvent(user.getId(), user.getEmail()));
        return new AuthResponse(user.getId());
    }   // ← commit happens here, THEN the listener below runs
}

@Component
public class WelcomeMailListener {

    @TransactionalEventListener                      // phase = AFTER_COMMIT
    public void sendWelcomeMail(UserRegisteredEvent event) {
        mailService.sendWelcome(event.email());      // never sent if register() rolled back
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onFailure(UserRegisteredEvent event) {
        log.warn("Registration rolled back for {}", event.email());
    }
}
```

> In `AFTER_COMMIT` the original transaction is already committed. If the listener needs to **write** to the database, annotate it with `@Transactional(propagation = Propagation.REQUIRES_NEW)` (Spring rejects plain `REQUIRED` here since 6.1) or run it `@Async`.

---

## `@Transactional` in Tests

In Spring test classes (`@SpringBootTest`, `@DataJpaTest`), `@Transactional` on a test method or class means: **run the test in a transaction and roll it back at the end**, so the database is clean for the next test.

```java
@DataJpaTest                     // already @Transactional: every test rolls back
class UserRepositoryTest {

    @Autowired UserRepository repo;

    @Test
    void savesUser() {
        repo.save(new User("mahendra"));
        assertThat(repo.count()).isEqualTo(1);
    }   // rolled back automatically
}

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Test
    @Commit                      // keep the data (same as @Rollback(false))
    void registerCommits() { ... }
}
```

> **Caveat:** if the test is transactional, your service's `REQUIRED` methods **join** the test transaction, so you never test the real commit (constraint violations at flush time, `@TransactionalEventListener(AFTER_COMMIT)` listeners). Call `entityManager.flush()` in the test, or test those paths without `@Transactional`.

---

## Common Pitfalls

### 1. Self-Invocation (The Most Common Bug)

```java
@Service
public class AuthService {

    public void doRegister(RegisterRequest request) {
        // ❌ WRONG — calls internal method, proxy is bypassed, NO TRANSACTION
        this.register(request);

        // ✅ CORRECT — inject self or extract to another bean
    }

    @Transactional
    public void register(RegisterRequest request) {
        userRepository.save(user);
    }
}
```

**Fix** (in order of preference):
1. Move the `@Transactional` method to a **separate bean** and call it through that bean.
2. Use a [`TransactionTemplate`](#programmatic-transactions-transactiontemplate) inside the same class.
3. Self-inject the proxy: `@Autowired @Lazy private AuthService self;` then call `self.register(request)`.

---

### 2. `@Transactional` on Private Methods

```java
@Service
public class AuthService {

    // ❌ WRONG — a proxy can never intercept a private method (silently no transaction)
    @Transactional
    private void register(RegisterRequest request) { ... }

    // ✅ CORRECT
    @Transactional
    public void register(RegisterRequest request) { ... }
}
```

`protected` and package-private methods work with Spring Boot's default CGLIB proxies since Spring Framework 6.0, but `public` remains the safest choice. Even when visible, a method only becomes transactional if it's called from **another bean** (see pitfall 1).

---

### 3. Catching Exception Without Re-throwing

```java
@Transactional
public void register(RegisterRequest request) {
    try {
        userRepository.save(user);
        riskyOperation(); // throws RuntimeException
    } catch (Exception e) {
        log.error("Error", e);
        // ❌ WRONG — exception swallowed, transaction commits with incomplete data!
    }
}

@Transactional
public void registerFixed(RegisterRequest request) {
    try {
        userRepository.save(user);
        riskyOperation();
    } catch (Exception e) {
        log.error("Error", e);
        throw e; // ✅ CORRECT — re-throw to trigger rollback
    }
}
```

---

### 4. Transaction Marked Rollback-Only

```java
// UserService
@Transactional(propagation = Propagation.REQUIRED) // joins caller's TX
public void createUser(User user) {
    throw new RuntimeException("duplicate");
    // T1 is now marked rollback-only
}

// AuthService
@Transactional
public void register(RegisterRequest request) {
    try {
        userService.createUser(user); // throws → T1 marked rollback-only
    } catch (RuntimeException e) {
        log.warn("User exists, continuing..."); // ❌ even though caught, T1 is poisoned
    }
    // When register() exits → T1 tries to commit → Spring throws:
    // UnexpectedRollbackException: Transaction silently rolled back
}
```

**Fix**: Use `Propagation.REQUIRES_NEW` on `createUser` if you want independent commit/rollback.

---

### 5. `@Transactional` in `@Controller`

```java
// ❌ WRONG — transactions belong in the service layer
@RestController
public class AuthController {
    @Transactional
    @PostMapping("/register")
    public ResponseEntity<?> register(...) { ... }
}

// ✅ CORRECT — keep @Transactional in @Service
@Service
public class AuthService {
    @Transactional
    public AuthResponse register(...) { ... }
}
```

---

### 6. Lazy Loading Outside Transaction

```java
@Transactional(readOnly = true)
public User getUser(Long id) {
    return userRepository.findById(id).orElseThrow(); // roles not loaded yet (LAZY)
} // ← transaction and persistence context end here

// Later, outside the transaction (async task, scheduled job, Kafka listener...):
user.getRoles().size(); // ❌ LazyInitializationException — could not initialize proxy - no Session
```

**Fix**: Load what you need **inside** the transaction:
- Map to a DTO inside the `@Transactional` method (preferred).
- Fetch the association in the query: `JOIN FETCH` or `@EntityGraph(attributePaths = "roles")`.

> **Open Session in View:** Spring Boot enables `spring.jpa.open-in-view=true` by default (and logs a warning). It keeps the persistence context open for the whole web request, so the same code **won't fail inside a controller**, but it silently runs extra lazy-loading queries while rendering the response. Many teams set `spring.jpa.open-in-view=false` and load data explicitly.

---

## Best Practices

| Practice | Reason |
|----------|--------|
| Keep `@Transactional` in `@Service` layer | Controllers shouldn't own DB lifecycle |
| Use `readOnly = true` for all queries | Performance optimization, intent clarity |
| Keep transactions short | Reduces lock contention, better scalability |
| Don't catch and swallow exceptions | Prevents silent data corruption |
| Use `REQUIRES_NEW` for audit logs | Audit must persist even if main TX rolls back |
| Avoid `Propagation.NEVER` unless enforcing strict rules | Usually unnecessary |
| Always specify `rollbackFor` for checked exceptions | Default behavior is surprising (or use `rollbackOn = ALL_EXCEPTIONS` on Spring 6.2+) |
| Never use `@Transactional` on private methods | Proxy won't intercept them |
| No remote calls (HTTP, email, Kafka) inside a transaction | Holds DB connections and locks while waiting; use `@TransactionalEventListener(AFTER_COMMIT)` |
| Prefer `REQUIRES_NEW` over `NESTED` with JPA | `JpaTransactionManager` doesn't allow `NESTED` by default |
| Test rollback scenarios | Verify your rollback rules work as expected |
| Monitor transaction boundaries | Use logging or AOP to trace TX open/close |

---

## Quick Reference Table

### Propagation

| Propagation | Creates New TX | Joins Existing | Suspends Existing | Throws if TX exists | Throws if no TX |
|-------------|:--------------:|:--------------:|:-----------------:|:-------------------:|:---------------:|
| REQUIRED | ✓ (if needed) | ✓ | — | — | — |
| REQUIRES_NEW | ✓ (always) | — | ✓ | — | — |
| SUPPORTS | — | ✓ | — | — | — |
| MANDATORY | — | ✓ | — | — | ✓ |
| NOT_SUPPORTED | — | — | ✓ | — | — |
| NEVER | — | — | — | ✓ | — |
| NESTED | ✓ (savepoint) | ✓ | — | — | — |

### Rollback Rules

| Exception Type | Default Rollback? | Override with |
|----------------|:-----------------:|---------------|
| `RuntimeException` | ✓ YES | `noRollbackFor` |
| `Error` | ✓ YES | `noRollbackFor` |
| Checked `Exception` | ✗ NO | `rollbackFor` |

### Attribute Defaults

| Attribute | Default Value |
|-----------|--------------|
| `value` / `transactionManager` | `""` (primary transaction manager) |
| `propagation` | `REQUIRED` |
| `isolation` | `DEFAULT` (DB default) |
| `readOnly` | `false` |
| `timeout` / `timeoutString` | `-1` / `""` (system default, usually no limit) |
| `rollbackFor` / `rollbackForClassName` | `{}` (only RuntimeException/Error) |
| `noRollbackFor` / `noRollbackForClassName` | `{}` |
| `label` | `{}` |

---

*Generated for Spring Boot / Spring Framework · `@Transactional` applies to `org.springframework.transaction.annotation.Transactional`*
