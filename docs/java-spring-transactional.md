# Spring `@Transactional` — Complete Reference Guide

> A deep-dive into Spring's transaction management: when transactions commit, how propagation works, rollback rules, and real-world patterns.

---

## Table of Contents

1. [What Is a Transaction?](#what-is-a-transaction)
2. [How Spring Manages Transactions](#how-spring-manages-transactions)
3. [Transaction Commit Timing](#transaction-commit-timing)
4. [Scenario Walkthroughs](#scenario-walkthroughs)
5. [All @Transactional Attributes](#all-transactional-attributes)
   - [propagation](#propagation)
   - [isolation](#isolation)
   - [readOnly](#readonly)
   - [timeout](#timeout)
   - [rollbackFor / rollbackForClassName](#rollbackfor--rollbackforclassname)
   - [noRollbackFor / noRollbackForClassName](#norollbackfor--norollbackforclassname)
6. [Propagation Deep Dive](#propagation-deep-dive)
7. [Isolation Deep Dive](#isolation-deep-dive)
8. [Common Pitfalls](#common-pitfalls)
9. [Best Practices](#best-practices)
10. [Quick Reference Table](#quick-reference-table)

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

> **Important**: `@Transactional` only works on public methods called **from outside the bean**.  
> Self-invocation (`this.method()`) bypasses the proxy and has no transactional effect.

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
    propagation          = Propagation.REQUIRED,
    isolation            = Isolation.DEFAULT,
    readOnly             = false,
    timeout              = -1,
    rollbackFor          = {},
    rollbackForClassName = {},
    noRollbackFor        = {},
    noRollbackForClassName = {}
)
```

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

### `timeout`

```java
@Transactional(timeout = 30) // seconds
public void longRunningOperation() {
    // If this takes more than 30 seconds, transaction is rolled back
}
```

| Aspect | Detail |
|--------|--------|
| **Default** | `-1` (no timeout) |
| **Unit** | Seconds |
| **On timeout** | Throws `TransactionTimedOutException`, triggers rollback |
| **Use for** | Preventing long-running transactions from holding locks |

```
T1 BEGINS
  ├── 0s  - start
  ├── 15s - still running...
  ├── 30s - TIMEOUT ← TransactionTimedOutException thrown
  └── T1 ROLLS BACK ✗
```

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
@Transactional(rollbackForClassName = {"IOException", "CustomBusinessException"})
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

**Fix**: Extract `@Transactional` methods to a separate Spring-managed bean, or use `ApplicationContext.getBean()` self-injection.

---

### 2. `@Transactional` on Non-Public Methods

```java
@Service
public class AuthService {

    // ❌ WRONG — Spring's default proxy ignores non-public methods
    @Transactional
    private void register(RegisterRequest request) { ... }

    // ✅ CORRECT
    @Transactional
    public void register(RegisterRequest request) { ... }
}
```

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
@Transactional
public User getUser(Long id) {
    return userRepository.findById(id).get(); // loaded
} // ← transaction ends here

// Later in controller:
user.getRoles(); // ❌ LazyInitializationException — no active session!
```

**Fix**: Use `@Transactional(readOnly = true)` at the service level, fetch eagerly with JOIN FETCH, or use DTOs.

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
| Always specify `rollbackFor` for checked exceptions | Default behavior is surprising |
| Never use `@Transactional` on private methods | Proxy won't intercept them |
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
| `propagation` | `REQUIRED` |
| `isolation` | `DEFAULT` (DB default) |
| `readOnly` | `false` |
| `timeout` | `-1` (no limit) |
| `rollbackFor` | `{}` (only RuntimeException/Error) |
| `noRollbackFor` | `{}` |

---

*Generated for Spring Boot / Spring Framework · `@Transactional` applies to `org.springframework.transaction.annotation.Transactional`*
