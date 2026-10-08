# Relational Database Systems — Complete Guide

A practical guide to relational databases for developers and interview preparation: SQL command categories (DDL, DML, DQL, DCL, TCL), normalization, transactions, isolation levels, **all major lock types**, indexing, query optimization, storage internals, replication, backup, tuning, security and scaling. Examples use **PostgreSQL** and **MySQL (InnoDB)**, with notes for SQL Server and Oracle where they differ.

> Related: [Spring Boot Annotations Guide](java-spring-boot-document-with-full-details.md) · [Spring @Transactional Guide](java-spring-transactional.md) · [Stored Procedures](store-procedure.md) · [AWS Services Guide](AWS_Service_guide.md)

## Table of Contents

- [1. SQL Command Categories (DDL, DML, DQL, DCL, TCL)](#1-sql-command-categories-ddl-dml-dql-dcl-tcl)
    - [DDL — Data Definition Language](#ddl--data-definition-language)
    - [DML — Data Manipulation Language](#dml--data-manipulation-language)
    - [DQL — Data Query Language](#dql--data-query-language)
    - [DCL — Data Control Language](#dcl--data-control-language)
    - [TCL — Transaction Control Language](#tcl--transaction-control-language)
    - [SQL Categories in a Spring Boot Application](#sql-categories-in-a-spring-boot-application)
- [2. Normalization & Denormalization](#2-normalization--denormalization)
    - [Starting Point: Unnormalized Table](#starting-point-unnormalized-table)
    - [First Normal Form (1NF)](#first-normal-form-1nf)
    - [Second Normal Form (2NF)](#second-normal-form-2nf)
    - [Third Normal Form (3NF)](#third-normal-form-3nf)
    - [BCNF (Boyce-Codd Normal Form)](#bcnf-boyce-codd-normal-form)
    - [4NF — Multi-Valued Dependencies](#4nf--multi-valued-dependencies)
    - [Denormalization — When and How](#denormalization--when-and-how)
- [3. ACID Properties](#3-acid-properties)
    - [Atomicity](#atomicity)
    - [Consistency](#consistency)
    - [Isolation](#isolation)
    - [Durability](#durability)
- [4. Transaction Isolation Levels](#4-transaction-isolation-levels)
    - [Isolation Anomalies Explained with Examples](#isolation-anomalies-explained-with-examples)
    - [Isolation Levels vs Anomalies](#isolation-levels-vs-anomalies)
    - [Setting Isolation Levels](#setting-isolation-levels)
    - [MVCC Deep Dive](#mvcc-deep-dive)
- [5. Locking Mechanisms](#5-locking-mechanisms)
    - [Lock Granularity](#lock-granularity)
    - [Lock Modes: Shared, Exclusive, Update, Intent](#lock-modes-shared-exclusive-update-intent)
    - [Row-Level Locks](#row-level-locks)
    - [Table-Level Locks](#table-level-locks)
    - [DDL and Metadata Locks](#ddl-and-metadata-locks)
    - [InnoDB Record, Gap and Next-Key Locks](#innodb-record-gap-and-next-key-locks)
    - [Advisory (Application) Locks](#advisory-application-locks)
    - [Deadlock Example and Resolution](#deadlock-example-and-resolution)
    - [Optimistic vs Pessimistic Locking](#optimistic-vs-pessimistic-locking)
    - [Lock Monitoring](#lock-monitoring)
    - [Lock Types Summary](#lock-types-summary)
- [6. Indexing](#6-indexing)
    - [B+Tree Index Internals](#btree-index-internals)
    - [Clustered vs Non-Clustered Index](#clustered-vs-non-clustered-index)
    - [Covering Index](#covering-index)
    - [Composite Index & Leftmost Prefix Rule](#composite-index--leftmost-prefix-rule)
    - [Partial Index](#partial-index)
    - [Functional / Expression Index](#functional--expression-index)
    - [Index Bloat and Maintenance](#index-bloat-and-maintenance)
- [7. Query Optimizer & Execution Plans](#7-query-optimizer--execution-plans)
    - [Reading EXPLAIN Output](#reading-explain-output)
    - [Join Algorithms](#join-algorithms)
    - [Statistics and Cardinality Estimation](#statistics-and-cardinality-estimation)
    - [Parameter Sniffing (SQL Server Problem)](#parameter-sniffing-sql-server-problem)
- [8. Storage Architecture & Internals](#8-storage-architecture--internals)
    - [Page Structure](#page-structure)
    - [Buffer Pool / Shared Buffer](#buffer-pool--shared-buffer)
    - [Write-Ahead Logging (WAL)](#write-ahead-logging-wal)
    - [Checkpoint Mechanism](#checkpoint-mechanism)
    - [VACUUM and Dead Tuples (PostgreSQL)](#vacuum-and-dead-tuples-postgresql)
- [9. Partitioning](#9-partitioning)
    - [Range Partitioning (most common — for time-series data)](#range-partitioning-most-common--for-time-series-data)
    - [List Partitioning](#list-partitioning)
    - [Hash Partitioning](#hash-partitioning)
    - [Indexes on Partitioned Tables](#indexes-on-partitioned-tables)
    - [MySQL Partitioning](#mysql-partitioning)
- [10. Replication & High Availability](#10-replication--high-availability)
    - [PostgreSQL Streaming Replication Setup](#postgresql-streaming-replication-setup)
    - [Synchronous vs Asynchronous Replication](#synchronous-vs-asynchronous-replication)
    - [Logical Replication](#logical-replication)
    - [Patroni — Automated Failover for PostgreSQL](#patroni--automated-failover-for-postgresql)
    - [ProxySQL (MySQL Connection Pooling + Read/Write Split)](#proxysql-mysql-connection-pooling--readwrite-split)
- [11. Backup & Recovery](#11-backup--recovery)
    - [Logical Backup](#logical-backup)
    - [Physical Backup (PostgreSQL)](#physical-backup-postgresql)
    - [Point-in-Time Recovery (PITR)](#point-in-time-recovery-pitr)
    - [Percona XtraBackup (MySQL Hot Backup)](#percona-xtrabackup-mysql-hot-backup)
- [12. Performance Tuning](#12-performance-tuning)
    - [Slow Query Log](#slow-query-log)
    - [N+1 Query Problem](#n1-query-problem)
    - [Function on Indexed Column (Index Killer)](#function-on-indexed-column-index-killer)
    - [Keyset Pagination (vs OFFSET)](#keyset-pagination-vs-offset)
    - [Implicit Type Conversion](#implicit-type-conversion)
    - [Connection Overhead & Pooling](#connection-overhead--pooling)
    - [Monitoring Queries in Flight](#monitoring-queries-in-flight)
- [13. Data Integrity & Constraints](#13-data-integrity--constraints)
    - [All Constraint Types](#all-constraint-types)
    - [Foreign Key Cascade Behaviors](#foreign-key-cascade-behaviors)
    - [Deferred Constraints](#deferred-constraints)
    - [Triggers](#triggers)
    - [Data Type Integrity](#data-type-integrity)
- [14. Security](#14-security)
    - [Role-Based Access Control](#role-based-access-control)
    - [Row-Level Security (RLS)](#row-level-security-rls)
    - [SQL Injection Prevention](#sql-injection-prevention)
    - [Encryption](#encryption)
    - [Audit Logging](#audit-logging)
- [15. Advanced SQL Features](#15-advanced-sql-features)
    - [Window Functions](#window-functions)
    - [Recursive CTEs (Hierarchical Data)](#recursive-ctes-hierarchical-data)
    - [LATERAL Join](#lateral-join)
    - [GROUPING SETS, ROLLUP, CUBE](#grouping-sets-rollup-cube)
    - [MERGE / UPSERT](#merge--upsert)
    - [JSON Support (PostgreSQL JSONB)](#json-support-postgresql-jsonb)
    - [Materialized Views](#materialized-views)
- [16. Scaling Patterns](#16-scaling-patterns)
    - [Read Replicas + Application Routing](#read-replicas--application-routing)
    - [Sharding Strategy](#sharding-strategy)
    - [CQRS Pattern](#cqrs-pattern)
    - [Connection Pooling at Scale](#connection-pooling-at-scale)
    - [Vertical vs Horizontal Scaling Decision Tree](#vertical-vs-horizontal-scaling-decision-tree)
- [17. CAP Theorem & Distributed Considerations](#17-cap-theorem--distributed-considerations)
    - [CAP Theorem](#cap-theorem)
    - [PACELC — Beyond CAP](#pacelc--beyond-cap)
    - [Two-Phase Commit (2PC)](#two-phase-commit-2pc)
    - [Saga Pattern](#saga-pattern)
    - [Eventual Consistency in Practice](#eventual-consistency-in-practice)

---

## 1. SQL Command Categories (DDL, DML, DQL, DCL, TCL)

SQL statements are grouped into **sub-languages** by what they do. Knowing the category tells you whether a statement changes structure or data, whether it can be rolled back, and which permission it needs.

| Category | Full name | Purpose | Main commands |
|---|---|---|---|
| **DDL** | Data Definition Language | Define/change the **structure** (schema objects) | `CREATE`, `ALTER`, `DROP`, `TRUNCATE`, `RENAME`, `COMMENT` |
| **DML** | Data Manipulation Language | Add, change, remove **rows** | `INSERT`, `UPDATE`, `DELETE`, `MERGE` (upsert) |
| **DQL** | Data Query Language | **Read** data | `SELECT` |
| **DCL** | Data Control Language | Control **permissions** | `GRANT`, `REVOKE` |
| **TCL** | Transaction Control Language | Control **transactions** | `BEGIN` / `START TRANSACTION`, `COMMIT`, `ROLLBACK`, `SAVEPOINT`, `SET TRANSACTION` |

```
                         SQL
   ┌─────────┬─────────┬──┴──────┬─────────┬─────────┐
  DDL       DML       DQL       DCL       TCL
 structure  rows      read    security  transactions
 CREATE     INSERT    SELECT   GRANT     BEGIN
 ALTER      UPDATE             REVOKE    COMMIT
 DROP       DELETE                       ROLLBACK
 TRUNCATE   MERGE                        SAVEPOINT
```

> **What about "DSL"?** DSL is **not** a standard SQL category. It means *Domain-Specific Language*, and SQL itself is a DSL (a language specialized for data). When people write "DSL" in this context they usually mean **DQL** (queries) or **DCL** (permissions). Some books also merge DQL into DML, because `SELECT` manipulates data in the sense of retrieving it.

---

### DDL — Data Definition Language

DDL creates and changes **database objects**: databases, schemas, tables, columns, indexes, views, sequences, constraints. DDL changes the **schema**, not the rows.

| Command | What it does |
|---|---|
| `CREATE` | Creates an object (table, index, view, sequence, schema...) |
| `ALTER` | Changes an existing object (add/drop/rename column, change type, add constraint) |
| `DROP` | Deletes an object **and all its data** |
| `TRUNCATE` | Removes **all rows** from a table quickly (keeps the structure) |
| `RENAME` | Renames an object (`ALTER TABLE ... RENAME TO` in PostgreSQL) |
| `COMMENT` | Adds documentation to an object |

```sql
-- CREATE
CREATE TABLE departments (
    dept_id    SERIAL       PRIMARY KEY,
    dept_name  VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE employees (
    emp_id     BIGSERIAL     PRIMARY KEY,
    name       VARCHAR(100)  NOT NULL,
    email      VARCHAR(255)  NOT NULL UNIQUE,
    dept_id    INT           REFERENCES departments(dept_id),
    salary     DECIMAL(10,2) CHECK (salary > 0),
    created_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_employees_dept ON employees(dept_id);
CREATE VIEW active_employees AS SELECT * FROM employees WHERE salary IS NOT NULL;

-- ALTER
ALTER TABLE employees ADD COLUMN phone VARCHAR(20);
ALTER TABLE employees ALTER COLUMN name TYPE VARCHAR(150);        -- PostgreSQL
ALTER TABLE employees MODIFY COLUMN name VARCHAR(150);            -- MySQL
ALTER TABLE employees RENAME COLUMN phone TO mobile;
ALTER TABLE employees ADD CONSTRAINT chk_email CHECK (email LIKE '%@%');
ALTER TABLE employees DROP COLUMN mobile;

-- RENAME / COMMENT
ALTER TABLE employees RENAME TO staff;                            -- PostgreSQL
RENAME TABLE staff TO employees;                                  -- MySQL
COMMENT ON TABLE employees IS 'All employees, including contractors';   -- PostgreSQL / Oracle

-- TRUNCATE / DROP
TRUNCATE TABLE audit_log;                     -- remove all rows, keep table
DROP TABLE IF EXISTS temp_import;             -- remove table and data
DROP INDEX idx_employees_dept;
```

**DDL and transactions** (an important difference between databases):

| Database | Is DDL transactional? |
|---|---|
| PostgreSQL | ✅ Yes: `CREATE`/`ALTER`/`DROP` can be rolled back inside a transaction (except a few, e.g. `CREATE INDEX CONCURRENTLY`, `CREATE DATABASE`) |
| SQL Server | ✅ Mostly yes |
| MySQL | ❌ No: DDL causes an **implicit COMMIT** of the current transaction |
| Oracle | ❌ No: DDL commits before and after |

```sql
-- PostgreSQL: a failed migration leaves no half-applied schema
BEGIN;
ALTER TABLE employees ADD COLUMN grade INT;
ALTER TABLE employees ADD CONSTRAINT fk_grade FOREIGN KEY (grade) REFERENCES grades(id);  -- fails
ROLLBACK;   -- the new column is also gone
```

**Locking note:** most DDL takes a strong table lock (PostgreSQL `ACCESS EXCLUSIVE`, MySQL metadata lock), which blocks queries on that table while it runs and while it **waits**. See [DDL lock queues](#ddl-and-metadata-locks).

---

### DML — Data Manipulation Language

DML changes the **rows** stored in tables. DML is **transactional** in every major database: it can be committed or rolled back.

| Command | What it does |
|---|---|
| `INSERT` | Adds new rows |
| `UPDATE` | Changes values in existing rows |
| `DELETE` | Removes rows (all, or those matching `WHERE`) |
| `MERGE` / upsert | Insert-or-update in one statement (see [MERGE / UPSERT](#merge--upsert)) |

```sql
-- INSERT: single row, multiple rows, from a query
INSERT INTO departments (dept_name) VALUES ('Engineering');

INSERT INTO employees (name, email, dept_id, salary) VALUES
    ('Asha',  'asha@x.com',  1, 90000),
    ('Ravi',  'ravi@x.com',  1, 75000);

INSERT INTO employees_archive (emp_id, name, email)
SELECT emp_id, name, email FROM employees WHERE created_at < '2020-01-01';

-- INSERT ... RETURNING (PostgreSQL): get generated values back
INSERT INTO employees (name, email, dept_id, salary)
VALUES ('Meera', 'meera@x.com', 1, 82000)
RETURNING emp_id, created_at;

-- UPDATE
UPDATE employees SET salary = salary * 1.10 WHERE dept_id = 1;

-- UPDATE with a join
UPDATE employees e                                   -- PostgreSQL
SET    salary = salary + 5000
FROM   departments d
WHERE  d.dept_id = e.dept_id AND d.dept_name = 'Engineering';

UPDATE employees e                                   -- MySQL
JOIN   departments d ON d.dept_id = e.dept_id
SET    e.salary = e.salary + 5000
WHERE  d.dept_name = 'Engineering';

-- DELETE
DELETE FROM employees WHERE emp_id = 42;
DELETE FROM sessions WHERE expires_at < NOW();
```

**Safety tips:**
- Always write the `WHERE` first. `UPDATE employees SET salary = 0;` without `WHERE` changes **every** row.
- Run a `SELECT` with the same `WHERE` before a big `UPDATE`/`DELETE`, and do it inside `BEGIN ... ROLLBACK` first if unsure.
- Delete large volumes **in batches** to avoid long locks and huge undo/WAL:

```sql
-- PostgreSQL: delete in chunks of 10,000
DELETE FROM events
WHERE id IN (SELECT id FROM events WHERE created_at < NOW() - INTERVAL '90 days' LIMIT 10000);
-- repeat until 0 rows affected
```

#### DELETE vs TRUNCATE vs DROP

| | `DELETE` | `TRUNCATE` | `DROP` |
|---|---|---|---|
| Category | DML | DDL | DDL |
| Removes | Selected rows (`WHERE` allowed) | All rows | Rows **and** the table structure |
| Speed on big tables | Slow (row by row, logged) | Very fast (deallocates pages) | Very fast |
| Fires row triggers | ✅ | ❌ | ❌ |
| Resets identity/auto-increment | ❌ | ✅ (MySQL always; PostgreSQL with `RESTART IDENTITY`) | — |
| Rollback possible | ✅ | PostgreSQL/SQL Server ✅, MySQL/Oracle ❌ | PostgreSQL/SQL Server ✅, MySQL/Oracle ❌ |
| Lock | Row locks | Table lock (`ACCESS EXCLUSIVE`) | Table lock |
| Allowed with incoming FKs | ✅ (subject to FK rules) | ❌ unless `CASCADE` | ❌ unless `CASCADE` |

---

### DQL — Data Query Language

DQL is the `SELECT` statement: it **reads** data without changing it. It's the most used and richest part of SQL.

**Logical order of evaluation** (different from the order you write it):

```
Written order:      SELECT → FROM → WHERE → GROUP BY → HAVING → ORDER BY → LIMIT
Evaluation order:   FROM/JOIN → WHERE → GROUP BY → HAVING → SELECT → DISTINCT → ORDER BY → LIMIT/OFFSET
```

That's why a column alias defined in `SELECT` can't be used in `WHERE`, but can be used in `ORDER BY`.

```sql
SELECT   d.dept_name,
         COUNT(*)        AS headcount,
         AVG(e.salary)   AS avg_salary
FROM     employees e
JOIN     departments d ON d.dept_id = e.dept_id      -- 1. join
WHERE    e.salary > 30000                            -- 2. filter rows
GROUP BY d.dept_name                                 -- 3. group
HAVING   COUNT(*) >= 5                               -- 4. filter groups
ORDER BY avg_salary DESC                             -- 6. sort (alias allowed here)
LIMIT    10;                                         -- 7. limit
```

**Join types:**

| Join | Returns |
|---|---|
| `INNER JOIN` | Only rows with a match in both tables |
| `LEFT JOIN` | All rows from the left table + matches (NULLs when no match) |
| `RIGHT JOIN` | All rows from the right table + matches |
| `FULL OUTER JOIN` | All rows from both tables (not in MySQL; emulate with `UNION`) |
| `CROSS JOIN` | Every combination (Cartesian product) |
| Self join | A table joined with itself (e.g. employee → manager) |

```sql
-- Departments with no employees (anti-join)
SELECT d.dept_name
FROM departments d
LEFT JOIN employees e ON e.dept_id = d.dept_id
WHERE e.emp_id IS NULL;

-- Same with NOT EXISTS (often clearer, NULL-safe)
SELECT d.dept_name
FROM departments d
WHERE NOT EXISTS (SELECT 1 FROM employees e WHERE e.dept_id = d.dept_id);

-- Subquery and CTE
WITH high_paid AS (
    SELECT * FROM employees WHERE salary > 100000
)
SELECT dept_id, COUNT(*) FROM high_paid GROUP BY dept_id;
```

> `SELECT ... FOR UPDATE` / `FOR SHARE` reads **and locks** rows; see [Row-Level Locks](#row-level-locks).

---

### DCL — Data Control Language

DCL manages **who can do what**: privileges on databases, schemas, tables, columns, sequences and functions.

| Command | What it does |
|---|---|
| `GRANT` | Gives privileges (or role membership) to a user/role |
| `REVOKE` | Removes privileges |

Common privileges: `SELECT`, `INSERT`, `UPDATE`, `DELETE`, `TRUNCATE`, `REFERENCES`, `TRIGGER`, `CREATE`, `CONNECT`, `USAGE`, `EXECUTE`, `ALL PRIVILEGES`.

```sql
-- Application user: data access only, no DDL
CREATE USER app_user WITH PASSWORD 'change-me';
GRANT CONNECT ON DATABASE shop TO app_user;
GRANT USAGE ON SCHEMA public TO app_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_user;   -- needed for SERIAL/IDENTITY inserts

-- Reporting user: read-only, and only some columns of a table
CREATE USER report_user WITH PASSWORD 'change-me';
GRANT SELECT ON orders, products TO report_user;
GRANT SELECT (emp_id, name, dept_id) ON employees TO report_user;    -- no salary column

-- Allow the grantee to pass the privilege on
GRANT SELECT ON products TO team_lead WITH GRANT OPTION;

-- Remove privileges
REVOKE DELETE ON ALL TABLES IN SCHEMA public FROM app_user;
REVOKE ALL PRIVILEGES ON employees FROM report_user;

-- MySQL syntax
CREATE USER 'app_user'@'%' IDENTIFIED BY 'change-me';
GRANT SELECT, INSERT, UPDATE, DELETE ON shop.* TO 'app_user'@'%';
SHOW GRANTS FOR 'app_user'@'%';
```

**Principle of least privilege:** the application's runtime user should not own the tables or have DDL rights. Run migrations (Flyway/Liquibase) with a separate, more privileged user. See [Security](#14-security) for roles and row-level security.

---

### TCL — Transaction Control Language

TCL groups DML statements into **transactions** (all-or-nothing units of work, see [ACID](#3-acid-properties)).

| Command | What it does |
|---|---|
| `BEGIN` / `START TRANSACTION` | Starts a transaction (otherwise each statement auto-commits) |
| `COMMIT` | Makes all changes permanent and visible to others |
| `ROLLBACK` | Undoes all changes since `BEGIN` |
| `SAVEPOINT name` | Marks a point inside the transaction |
| `ROLLBACK TO SAVEPOINT name` | Undoes changes after the savepoint only; the transaction continues |
| `RELEASE SAVEPOINT name` | Removes a savepoint |
| `SET TRANSACTION` | Sets isolation level / read-only mode for the current transaction |

```sql
BEGIN;                                                        -- or START TRANSACTION
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;

INSERT INTO orders (customer_id, total) VALUES (42, 1500.00);

SAVEPOINT before_loyalty;
UPDATE loyalty SET points = points + 150 WHERE customer_id = 42;
-- loyalty update failed a business check → undo only that part
ROLLBACK TO SAVEPOINT before_loyalty;

UPDATE customers SET last_order_at = NOW() WHERE customer_id = 42;
COMMIT;                                                       -- order + customer update saved
```

**Autocommit:** most clients (psql, MySQL CLI, JDBC by default) run in autocommit mode: every statement is its own transaction unless you start one explicitly. In Spring, `@Transactional` turns autocommit off for the duration of the method and issues `COMMIT`/`ROLLBACK` for you.

---

### SQL Categories in a Spring Boot Application

| Category | Where it appears in Spring Boot |
|---|---|
| DDL | Flyway/Liquibase migration scripts (`V1__init.sql`), `spring.jpa.hibernate.ddl-auto` (dev only) |
| DML | `repository.save()`, `delete()`, `@Modifying @Query("UPDATE ...")` |
| DQL | Derived queries (`findByEmail`), `@Query`, Specifications, Criteria API |
| DCL | Not in code: the DB user in `spring.datasource.username` and its grants |
| TCL | `@Transactional` / `TransactionTemplate` (Spring issues `BEGIN`, `COMMIT`, `ROLLBACK`, `SAVEPOINT` for `NESTED`) |

See [Spring Boot annotations guide → JPA / Database](java-spring-boot-document-with-full-details.md#6-jpa--database) and [Transaction Management](java-spring-boot-document-with-full-details.md#8-transaction-management).

[⬆ Back to Table of Contents](#table-of-contents)

---

## 2. Normalization & Denormalization

### Starting Point: Unnormalized Table

```
OrderDetails (unnormalized):
order_id | customer_name | customer_email | products (list)      | order_date
---------|---------------|----------------|----------------------|----------
1        | Alice Smith   | alice@x.com    | Pen, Notebook, Ruler | 2024-01-10
2        | Bob Jones     | bob@x.com      | Pen                  | 2024-01-11
```

Problem: `products` is a repeating group — violates atomicity.

---

### First Normal Form (1NF)

**Rule:** Atomic values only. No repeating groups. Each row must be unique.

```sql
-- After 1NF: one row per order-product combination
CREATE TABLE order_details_1nf (
    order_id       INT,
    customer_name  VARCHAR(100),
    customer_email VARCHAR(255),
    product_name   VARCHAR(100),
    order_date     DATE,
    PRIMARY KEY (order_id, product_name)
);

-- Data:
-- 1 | Alice Smith | alice@x.com | Pen      | 2024-01-10
-- 1 | Alice Smith | alice@x.com | Notebook | 2024-01-10
-- 1 | Alice Smith | alice@x.com | Ruler    | 2024-01-10
-- 2 | Bob Jones   | bob@x.com   | Pen      | 2024-01-11
```

Still has problems: `customer_name` and `customer_email` repeat for every product in the same order.

---

### Second Normal Form (2NF)

**Rule:** Must be in 1NF. No partial dependency — every non-key column must depend on the *entire* composite primary key, not just part of it.

The composite PK is `(order_id, product_name)`. But `customer_name`, `customer_email`, `order_date` depend only on `order_id` — a partial dependency.

```sql
-- After 2NF: separate orders from order items
CREATE TABLE orders_2nf (
    order_id       INT PRIMARY KEY,
    customer_name  VARCHAR(100),
    customer_email VARCHAR(255),
    order_date     DATE
);

CREATE TABLE order_items_2nf (
    order_id     INT,
    product_name VARCHAR(100),
    PRIMARY KEY (order_id, product_name),
    FOREIGN KEY (order_id) REFERENCES orders_2nf(order_id)
);
```

Still has a problem: `customer_email` is determined by `customer_name` (transitive dependency).

---

### Third Normal Form (3NF)

**Rule:** Must be in 2NF. No transitive dependency — non-key columns must not depend on other non-key columns.

`customer_email` depends on `customer_name` (not directly on `order_id`). This is a transitive dependency.

```sql
-- After 3NF: separate customers
CREATE TABLE customers_3nf (
    customer_id    SERIAL PRIMARY KEY,
    customer_name  VARCHAR(100),
    customer_email VARCHAR(255) UNIQUE
);

CREATE TABLE orders_3nf (
    order_id    INT PRIMARY KEY,
    customer_id INT,
    order_date  DATE,
    FOREIGN KEY (customer_id) REFERENCES customers_3nf(customer_id)
);

CREATE TABLE order_items_3nf (
    order_id     INT,
    product_name VARCHAR(100),
    PRIMARY KEY (order_id, product_name),
    FOREIGN KEY (order_id) REFERENCES orders_3nf(order_id)
);
```

---

### BCNF (Boyce-Codd Normal Form)

**Rule:** Every determinant must be a candidate key. Stricter than 3NF.

**Classic BCNF violation example:**
```
CourseSchedule: {student, course, teacher}
- A teacher teaches only one course: teacher → course
- A student takes a course from one teacher: (student, course) → teacher
- Both (student, course) and (student, teacher) are candidate keys
- But teacher → course: teacher is NOT a candidate key → BCNF violation
```

```sql
-- Fix: decompose
CREATE TABLE teacher_course (
    teacher VARCHAR(100) PRIMARY KEY,
    course  VARCHAR(100)
);

CREATE TABLE student_teacher (
    student VARCHAR(100),
    teacher VARCHAR(100),
    PRIMARY KEY (student, teacher),
    FOREIGN KEY (teacher) REFERENCES teacher_course(teacher)
);
```

---

### 4NF — Multi-Valued Dependencies

```
-- Problem: employee can have multiple skills AND multiple languages
-- These are independent facts, but stored together causes redundancy

employees_skills_languages:
emp_id | skill   | language
-------|---------|----------
1      | Java    | English
1      | Java    | French
1      | Python  | English
1      | Python  | French

-- Fix: separate tables
CREATE TABLE employee_skills     (emp_id INT, skill VARCHAR(100), PRIMARY KEY(emp_id, skill));
CREATE TABLE employee_languages  (emp_id INT, language VARCHAR(100), PRIMARY KEY(emp_id, language));
```

---

### Denormalization — When and How

```sql
-- Scenario: e-commerce order history page. Every load joins 5 tables.
-- Normalized query (slow at scale):
SELECT o.order_id, c.name, c.email, SUM(oi.price * oi.qty) as total
FROM orders o
JOIN customers c ON c.customer_id = o.customer_id
JOIN order_items oi ON oi.order_id = o.order_id
GROUP BY o.order_id, c.name, c.email;

-- Denormalized: store customer_name and order_total directly on orders
ALTER TABLE orders ADD COLUMN customer_name VARCHAR(100);
ALTER TABLE orders ADD COLUMN order_total   DECIMAL(10,2);

-- Fast query now:
SELECT order_id, customer_name, order_total FROM orders WHERE customer_id = 123;
```

**Tradeoffs of denormalization:**
- Faster reads, slower writes (must update redundant data)
- Risk of inconsistency if update logic is wrong
- Larger storage footprint
- Only justified when profiling confirms JOIN cost is the bottleneck

[⬆ Back to Table of Contents](#table-of-contents)

---

## 3. ACID Properties

### Atomicity

Either all statements in a transaction succeed, or none do.

```sql
-- Bank transfer: both operations must succeed or both must fail
BEGIN;

UPDATE accounts SET balance = balance - 500 WHERE account_id = 1;
UPDATE accounts SET balance = balance + 500 WHERE account_id = 2;

-- If second UPDATE fails (e.g., account_id 2 doesn't exist),
-- the first UPDATE is also rolled back. Balance is never lost.
COMMIT;

-- If something goes wrong:
ROLLBACK;  -- both updates are undone
```

Achieved via **undo logs** — before any row is modified, the original value is written to an undo log so it can be restored on rollback.

---

### Consistency

Transaction brings the database from one valid state to another. All constraints must hold before and after.

```sql
-- Consistency enforced by constraints:
CREATE TABLE accounts (
    account_id INT PRIMARY KEY,
    balance    DECIMAL(10,2) CHECK (balance >= 0)  -- consistency rule
);

BEGIN;
UPDATE accounts SET balance = balance - 1000 WHERE account_id = 1;
-- If this makes balance negative, CHECK constraint fires → transaction aborted
-- Database remains in consistent state
COMMIT;
```

---

### Isolation

Concurrent transactions do not interfere with each other.

```sql
-- Session A:
BEGIN;
UPDATE products SET stock = stock - 1 WHERE product_id = 42;
-- (not yet committed)

-- Session B (concurrent):
SELECT stock FROM products WHERE product_id = 42;
-- At READ COMMITTED: sees original stock (not Session A's uncommitted change)
-- At READ UNCOMMITTED: would see Session A's uncommitted change (dirty read)

-- Session A:
COMMIT;
-- Now Session B sees the updated value on next read
```

Achieved via **MVCC** (readers don't block writers) and/or **locking** (conflicting writers wait). See [Transaction Isolation Levels](#4-transaction-isolation-levels) and [Locking Mechanisms](#5-locking-mechanisms).

---

### Durability

Once a transaction commits, it survives crashes.

```sql
BEGIN;
INSERT INTO audit_log (event, ts) VALUES ('user_login', NOW());
COMMIT;
-- After COMMIT, even if the server crashes immediately after,
-- this row will be present when the server restarts.
-- Achieved via WAL: the commit record is fsynced to disk before COMMIT returns.
```

**The WAL guarantee:**
1. Before modifying a data page, write the change to the WAL log.
2. WAL is flushed to disk (fsync) on COMMIT.
3. On crash recovery, WAL is replayed to reconstruct committed changes.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 4. Transaction Isolation Levels

**Isolation** decides how much concurrent transactions can see of each other's work. Stronger isolation prevents more anomalies but reduces concurrency (more waiting, more retries). The SQL standard defines four levels in terms of the **read anomalies** they allow.

### Isolation Anomalies Explained with Examples

| Anomaly | What happens |
|---|---|
| **Dirty read** | Reading another transaction's **uncommitted** change |
| **Non-repeatable read** | Reading the same row twice gives **different values** (someone committed an update in between) |
| **Phantom read** | Re-running a query returns **new/missing rows** (someone inserted/deleted rows matching the condition) |
| **Lost update** | Two transactions read-modify-write the same row; one update **overwrites** the other |
| **Write skew** | Two transactions read overlapping data, each makes a valid change, but **together** they break a rule |

#### Dirty Read (only at READ UNCOMMITTED)
```sql
-- Session A:
BEGIN;
UPDATE accounts SET balance = 10000 WHERE id = 1;
-- NOT committed yet

-- Session B (READ UNCOMMITTED):
SELECT balance FROM accounts WHERE id = 1;
-- Returns 10000 — a dirty read of uncommitted data

-- Session A:
ROLLBACK;
-- Session B read data that never actually existed!
```
> PostgreSQL never allows dirty reads: `READ UNCOMMITTED` behaves like `READ COMMITTED`.

#### Non-Repeatable Read (at READ COMMITTED)
```sql
-- Session A:
BEGIN;
SELECT salary FROM employees WHERE id = 5;  -- returns 50000

-- Session B (concurrent):
UPDATE employees SET salary = 60000 WHERE id = 5;
COMMIT;

-- Session A (same transaction, second read):
SELECT salary FROM employees WHERE id = 5;  -- returns 60000 (different!)
-- The same row returned different values within one transaction
COMMIT;
```

#### Phantom Read (at READ COMMITTED; at REPEATABLE READ per the standard)
```sql
-- Session A:
BEGIN;
SELECT COUNT(*) FROM orders WHERE amount > 1000;  -- returns 5

-- Session B (concurrent):
INSERT INTO orders (amount) VALUES (1500);
COMMIT;

-- Session A (same transaction):
SELECT COUNT(*) FROM orders WHERE amount > 1000;  -- returns 6 (phantom row!)
COMMIT;
```
> The standard allows phantoms at `REPEATABLE READ`, but **PostgreSQL** (snapshot isolation) and **MySQL InnoDB** (consistent snapshot for plain reads, next-key locks for locking reads) prevent them in practice at that level.

#### Lost Update
```sql
-- Both sessions run: read stock, compute in the application, write back
-- Session A: SELECT stock FROM products WHERE id = 42;   -- 10
-- Session B: SELECT stock FROM products WHERE id = 42;   -- 10
-- Session A: UPDATE products SET stock = 9 WHERE id = 42; COMMIT;
-- Session B: UPDATE products SET stock = 9 WHERE id = 42; COMMIT;   -- should be 8!

-- Fixes:
UPDATE products SET stock = stock - 1 WHERE id = 42;            -- 1. atomic update in SQL
SELECT stock FROM products WHERE id = 42 FOR UPDATE;            -- 2. pessimistic lock
UPDATE products SET stock = 9, version = 8
WHERE id = 42 AND version = 7;                                  -- 3. optimistic lock (version check)
```

#### Write Skew
```sql
-- Rule: at least one doctor must be on call.
-- Both doctors are on call; both try to go off call at the same time.
-- Session A: SELECT COUNT(*) FROM doctors WHERE on_call;    -- 2 → OK to leave
-- Session B: SELECT COUNT(*) FROM doctors WHERE on_call;    -- 2 → OK to leave
-- Session A: UPDATE doctors SET on_call = false WHERE name = 'Alice'; COMMIT;
-- Session B: UPDATE doctors SET on_call = false WHERE name = 'Bob';   COMMIT;
-- Result: nobody on call. Different rows were updated, so row locks didn't conflict.
-- Prevented only by SERIALIZABLE (or by locking the rows read: SELECT ... FOR UPDATE).
```

---

### Isolation Levels vs Anomalies

| Level | Dirty read | Non-repeatable read | Phantom read | Lost update | Write skew |
|---|:---:|:---:|:---:|:---:|:---:|
| `READ UNCOMMITTED` | possible | possible | possible | possible | possible |
| `READ COMMITTED` | ❌ | possible | possible | possible | possible |
| `REPEATABLE READ` | ❌ | ❌ | possible (standard) / ❌ (PostgreSQL, InnoDB) | ❌ PostgreSQL (error) / possible InnoDB | possible |
| `SERIALIZABLE` | ❌ | ❌ | ❌ | ❌ | ❌ |

**Defaults by database:**

| Database | Default level | How it's implemented |
|---|---|---|
| PostgreSQL | `READ COMMITTED` | MVCC snapshots; `SERIALIZABLE` = Serializable Snapshot Isolation (SSI) |
| MySQL InnoDB | `REPEATABLE READ` | MVCC for plain reads + next-key locks for locking reads/writes |
| Oracle | `READ COMMITTED` | MVCC (undo segments); `SERIALIZABLE` = snapshot isolation |
| SQL Server | `READ COMMITTED` | Locking by default; MVCC with `READ_COMMITTED_SNAPSHOT ON` (default in Azure SQL) or `SNAPSHOT` isolation |

### Setting Isolation Levels

```sql
-- PostgreSQL (must be the first statement in the transaction)
BEGIN;
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
-- ...
COMMIT;

BEGIN ISOLATION LEVEL SERIALIZABLE;          -- shorthand

-- MySQL
SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;   -- for the session
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;             -- for the next transaction only

-- Check current level
SHOW transaction_isolation;                  -- PostgreSQL
SELECT @@transaction_isolation;              -- MySQL
```

**Serializable needs retries:** at `SERIALIZABLE` (and at `REPEATABLE READ` in PostgreSQL when a concurrent update conflicts), the database aborts one transaction with a **serialization failure** (`SQLSTATE 40001`, "could not serialize access"). The application must **retry** the whole transaction. In Spring, this surfaces as `CannotAcquireLockException` / `ConcurrencyFailureException` subclasses; retry with Spring Retry or a loop.

In Spring: `@Transactional(isolation = Isolation.REPEATABLE_READ)`; see the [isolation deep dive in the transaction guide](java-spring-transactional.md#isolation-deep-dive).

---

### MVCC Deep Dive

**MVCC (Multi-Version Concurrency Control)** keeps **several versions** of a row. Writers create a new version instead of overwriting; each reader sees the version that was committed as of its **snapshot**. Result: **readers don't block writers and writers don't block readers**. Only writer-vs-writer on the same row conflicts.

```sql
-- PostgreSQL stores versions in the table itself. Each row version has hidden columns:
--   xmin: transaction ID that created this version
--   xmax: transaction ID that deleted/replaced this version (0 if still current)

-- Simplified visibility rule for a snapshot taken by transaction 100:
--   visible if xmin is committed and < 100, and (xmax = 0 or xmax not yet committed / > 100)
-- (the real rule also checks the snapshot's list of in-progress transactions)

SELECT xmin, xmax, ctid, * FROM employees WHERE emp_id = 1;
```

| | PostgreSQL | MySQL InnoDB / Oracle |
|---|---|---|
| Where old versions live | In the table (dead tuples) | In the **undo log** (rollback segments) |
| Cleanup | `VACUUM` / autovacuum | Purge thread |
| Snapshot taken | Per statement (`READ COMMITTED`) or per transaction (`REPEATABLE READ`+) | Same idea |

[⬆ Back to Table of Contents](#table-of-contents)

---

## 5. Locking Mechanisms

MVCC handles **read-write** concurrency. **Locks** handle **write-write** conflicts and explicit coordination: they make a transaction **wait** until another one commits or rolls back. Understanding locks explains blocking, timeouts and deadlocks.

```
Transaction A ── holds lock on row 42 ──────────── COMMIT (lock released)
Transaction B ───────── wants row 42 → WAITS ─────────────┘→ continues
```

All row and table locks taken by DML are held **until the end of the transaction** (strict two-phase locking): the longer the transaction, the longer others wait.

### Lock Granularity

Locks can protect objects of different sizes:

| Granularity | Locks... | Concurrency | Overhead | Typical use |
|---|---|---|---|---|
| **Row** (record) | One row / index record | Highest | Highest (many locks) | `UPDATE`, `DELETE`, `SELECT ... FOR UPDATE` |
| **Page** | One data page (8–16 KB, many rows) | Medium | Medium | SQL Server, older engines |
| **Table** | A whole table | Low | Lowest | DDL, `LOCK TABLE`, MyISAM, bulk loads |
| **Database / schema** | The whole database | Lowest | — | Maintenance, restore |

**Lock escalation** (SQL Server, DB2): when one transaction holds too many row/page locks (≈5,000 in SQL Server), the engine converts them into **one table lock** to save memory, which can suddenly block everyone. PostgreSQL and InnoDB **don't escalate** row locks.

### Lock Modes: Shared, Exclusive, Update, Intent

| Mode | Symbol | Meaning | Taken by |
|---|---|---|---|
| **Shared** (read lock) | S | Others may also read-lock, nobody may write | `SELECT ... FOR SHARE`, SQL Server reads under locking isolation |
| **Exclusive** (write lock) | X | Nobody else may lock the resource | `UPDATE`, `DELETE`, `SELECT ... FOR UPDATE` |
| **Update** | U | "I'll probably write": compatible with S, not with other U | SQL Server, during the search phase of `UPDATE` (prevents a classic deadlock) |
| **Intent shared** | IS | "I hold/will take S locks on some rows of this table" | Table level, automatically |
| **Intent exclusive** | IX | "I hold/will take X locks on some rows of this table" | Table level, automatically |
| **Shared + intent exclusive** | SIX | Reading the whole table and updating some rows | Table level |

**Intent locks** let the engine check quickly whether a **table** lock is possible: a request for a table-level X lock only needs to see the table's IX/IS locks, instead of scanning millions of row locks.

**Compatibility matrix** (✅ = both can be held at the same time, ❌ = the second request waits):

| Requested ↓ / Held → | IS | IX | S | SIX | X |
|---|:---:|:---:|:---:|:---:|:---:|
| **IS** | ✅ | ✅ | ✅ | ✅ | ❌ |
| **IX** | ✅ | ✅ | ❌ | ❌ | ❌ |
| **S** | ✅ | ❌ | ✅ | ❌ | ❌ |
| **SIX** | ✅ | ❌ | ❌ | ❌ | ❌ |
| **X** | ❌ | ❌ | ❌ | ❌ | ❌ |

> With **MVCC** (PostgreSQL, InnoDB, Oracle), a plain `SELECT` takes **no row locks**: it reads a snapshot. An `X` row lock therefore blocks other **writers and locking reads**, but **not** plain `SELECT`s.

---

### Row-Level Locks

Row locks are taken automatically by `UPDATE`/`DELETE`, or explicitly with a **locking read** (`SELECT ... FOR ...`), typically to implement read-check-write logic safely.

```sql
BEGIN;
-- Lock the row so nobody can change it until COMMIT
SELECT balance FROM accounts WHERE id = 1 FOR UPDATE;
-- application checks balance >= 500
UPDATE accounts SET balance = balance - 500 WHERE id = 1;
COMMIT;                       -- lock released
```

**PostgreSQL row lock modes** (weakest → strongest):

| Clause | Blocks | Use when |
|---|---|---|
| `FOR KEY SHARE` | Deleting the row or changing its key | Taken automatically on the **referenced** row when inserting a child row with a foreign key |
| `FOR SHARE` | Any update/delete of the row | Reading a row that must not change until you commit |
| `FOR NO KEY UPDATE` | Other updates and `FOR SHARE`, but not `FOR KEY SHARE` | Taken by `UPDATE` that doesn't change key columns (so FK inserts aren't blocked) |
| `FOR UPDATE` | Every other row lock | You will update/delete the row, or change its key |

Row-lock conflicts in PostgreSQL:

| Requested ↓ / Held → | FOR KEY SHARE | FOR SHARE | FOR NO KEY UPDATE | FOR UPDATE |
|---|:---:|:---:|:---:|:---:|
| **FOR KEY SHARE** | ✅ | ✅ | ✅ | ❌ |
| **FOR SHARE** | ✅ | ✅ | ❌ | ❌ |
| **FOR NO KEY UPDATE** | ✅ | ❌ | ❌ | ❌ |
| **FOR UPDATE** | ❌ | ❌ | ❌ | ❌ |

**MySQL InnoDB:** `SELECT ... FOR SHARE` (or older `LOCK IN SHARE MODE`) = S lock; `SELECT ... FOR UPDATE` = X lock.
**SQL Server** uses table hints instead: `SELECT ... FROM accounts WITH (UPDLOCK, ROWLOCK) WHERE id = 1`.
**Oracle:** `SELECT ... FOR UPDATE [NOWAIT | WAIT n | SKIP LOCKED]`.

#### NOWAIT and SKIP LOCKED

By default a locking read **waits** for a conflicting lock. Two options change that:

```sql
-- NOWAIT: fail immediately instead of waiting
SELECT * FROM seats WHERE seat_id = 'A12' FOR UPDATE NOWAIT;
-- ERROR: could not obtain lock on row  (PostgreSQL 55P03 / MySQL 3572)

-- SKIP LOCKED: skip rows that are locked by others → perfect for job queues
BEGIN;
SELECT id, payload
FROM   jobs
WHERE  status = 'PENDING'
ORDER  BY created_at
LIMIT  10
FOR UPDATE SKIP LOCKED;          -- each worker gets a different batch, no waiting
-- process jobs...
UPDATE jobs SET status = 'DONE' WHERE id IN (...);
COMMIT;
```

Supported in PostgreSQL 9.5+, MySQL 8.0+, Oracle. (SQL Server: `WITH (READPAST)`.)

#### Lock timeouts

```sql
-- PostgreSQL
SET lock_timeout = '3s';                        -- give up waiting for a lock after 3 seconds
SET statement_timeout = '30s';                  -- cancel any statement running > 30 s

-- MySQL InnoDB
SET SESSION innodb_lock_wait_timeout = 3;      -- seconds (default 50)
```

---

### Table-Level Locks

Every statement takes a table-level lock (usually a weak one) to stop the table from being dropped or altered underneath it. You can also lock a table explicitly with `LOCK TABLE`.

**PostgreSQL table lock modes** (weakest → strongest) and the commands that take them:

| Mode | Taken automatically by | Conflicts with |
|---|---|---|
| `ACCESS SHARE` | `SELECT` | `ACCESS EXCLUSIVE` only |
| `ROW SHARE` | `SELECT ... FOR UPDATE / FOR SHARE` | `EXCLUSIVE`, `ACCESS EXCLUSIVE` |
| `ROW EXCLUSIVE` | `INSERT`, `UPDATE`, `DELETE`, `MERGE` | `SHARE` and stronger |
| `SHARE UPDATE EXCLUSIVE` | `VACUUM` (non-full), `ANALYZE`, `CREATE INDEX CONCURRENTLY`, some `ALTER TABLE` | itself and stronger |
| `SHARE` | `CREATE INDEX` (non-concurrent) | `ROW EXCLUSIVE` and stronger (blocks writes, allows reads) |
| `SHARE ROW EXCLUSIVE` | `CREATE TRIGGER`, some `ALTER TABLE` | `ROW EXCLUSIVE` and stronger, itself |
| `EXCLUSIVE` | `REFRESH MATERIALIZED VIEW CONCURRENTLY` | everything except `ACCESS SHARE` (only plain reads allowed) |
| `ACCESS EXCLUSIVE` | `DROP TABLE`, `TRUNCATE`, `VACUUM FULL`, `CLUSTER`, most `ALTER TABLE`, `LOCK TABLE` default | **everything**, including `SELECT` |

```sql
-- Explicit table locks (PostgreSQL), held until COMMIT/ROLLBACK
BEGIN;
LOCK TABLE employees IN SHARE MODE;               -- block writes, allow reads (consistent bulk export)
COMMIT;

BEGIN;
LOCK TABLE employees IN EXCLUSIVE MODE;           -- block writes and locking reads; plain SELECT still works
COMMIT;

BEGIN;
LOCK TABLE employees IN ACCESS EXCLUSIVE MODE;    -- block everything, even SELECT
LOCK TABLE employees;                             -- same: ACCESS EXCLUSIVE is the default
COMMIT;

-- MySQL
LOCK TABLES employees READ;                       -- this session and others may only read
LOCK TABLES employees WRITE;                      -- only this session may read/write
UNLOCK TABLES;
```

> In MySQL, `LOCK TABLES` implicitly commits the current transaction and is rarely needed with InnoDB. Prefer row locks.

---

### DDL and Metadata Locks

DDL needs the strongest lock (PostgreSQL `ACCESS EXCLUSIVE`, MySQL exclusive **metadata lock**, MDL). The dangerous part is the **lock queue**:

```
T1: long-running SELECT on orders (holds ACCESS SHARE)       ─────────────────────────►
T2: ALTER TABLE orders ADD COLUMN ...  → waits for T1          ⏳ (queued, wants ACCESS EXCLUSIVE)
T3: SELECT * FROM orders               → waits behind T2!      ⏳
T4, T5, T6 ... every new query on orders also queues            ⏳⏳⏳  → application outage
```

Even a "fast" `ALTER` can freeze a busy table while it **waits**. Protect migrations with a lock timeout and retry:

```sql
-- PostgreSQL migration
SET lock_timeout = '5s';
ALTER TABLE orders ADD COLUMN note TEXT;        -- fails fast instead of blocking everybody

-- Build indexes without blocking writes
CREATE INDEX CONCURRENTLY idx_orders_customer ON orders(customer_id);   -- PostgreSQL (not inside a transaction)
ALTER TABLE orders ADD INDEX idx_customer (customer_id), ALGORITHM=INPLACE, LOCK=NONE;   -- MySQL online DDL

-- MySQL: metadata lock wait timeout
SET SESSION lock_wait_timeout = 5;
```

---

### InnoDB Record, Gap and Next-Key Locks

InnoDB locks **index records**, not table rows directly. To prevent phantoms at `REPEATABLE READ`, it also locks the **gaps** between index records.

| Lock type | Locks | Purpose |
|---|---|---|
| **Record lock** | One index record | Protects an existing row |
| **Gap lock** | The gap **between** two index records (no record itself) | Stops other transactions from **inserting** into the gap |
| **Next-key lock** | A record **+ the gap before it** | Default for locking reads and updates at `REPEATABLE READ`; prevents phantoms |
| **Insert intention lock** | A special gap lock taken by `INSERT` | Many inserts into the same gap at different positions don't block each other |
| **AUTO-INC lock** | Table-level lock on the auto-increment counter | Controlled by `innodb_autoinc_lock_mode` (default 2 = interleaved, fastest) |

```sql
-- Index on orders(amount); existing amounts: 5, 10, 20, 30
-- Session A (REPEATABLE READ):
BEGIN;
SELECT * FROM orders WHERE amount BETWEEN 10 AND 20 FOR UPDATE;
-- Locks records 10 and 20 plus the gaps (5,10], (10,20] and the gap (20,30)
-- (exact ranges depend on the index and the query plan)

-- Session B:
INSERT INTO orders (amount) VALUES (15);   -- BLOCKS: 15 is inside a locked gap
INSERT INTO orders (amount) VALUES (25);   -- BLOCKS: gap before 30 is locked
INSERT INTO orders (amount) VALUES (40);   -- succeeds
```

Key points:
- Without a usable **index** on the `WHERE` column, InnoDB scans and locks **every row (and gap)** in the table. Index the columns you lock by.
- Gap locks are mostly **disabled at `READ COMMITTED`** (only used for FK and duplicate-key checks). Many teams use `READ COMMITTED` on MySQL to reduce gap-lock deadlocks.
- Gap locks don't conflict with each other (two transactions can hold the same gap lock); they only block inserts.

---

### Advisory (Application) Locks

**Advisory locks** are named locks with **no connection to any table**: the application decides what they mean. Useful for "only one instance may run this job/import at a time" across several app servers.

```sql
-- PostgreSQL (keys are bigints; hash a string with hashtext())
SELECT pg_try_advisory_lock(hashtext('nightly-invoice-job'));   -- true = acquired, false = someone else has it
-- ... run the job ...
SELECT pg_advisory_unlock(hashtext('nightly-invoice-job'));

-- Transaction-scoped: released automatically at COMMIT/ROLLBACK
SELECT pg_advisory_xact_lock(42);

-- MySQL
SELECT GET_LOCK('nightly-invoice-job', 0);       -- 1 = acquired, 0 = timeout (0 s → don't wait)
SELECT RELEASE_LOCK('nightly-invoice-job');

-- SQL Server
EXEC sp_getapplock @Resource = 'nightly-invoice-job', @LockMode = 'Exclusive', @LockTimeout = 0;
```

> Session-level advisory locks stay held if the connection returns to a **pool** without unlocking. Prefer transaction-scoped variants, or always unlock in `finally`.

---

### Deadlock Example and Resolution

A **deadlock** happens when two (or more) transactions each hold a lock the other needs. Neither can continue, so the database detects the cycle (wait-for graph) and **aborts one** of them (the victim), which gets an error and must retry.

```sql
-- Session A:
BEGIN;
UPDATE accounts SET balance = balance - 100 WHERE id = 1;  -- locks row 1

-- Session B (concurrent):
BEGIN;
UPDATE accounts SET balance = balance - 100 WHERE id = 2;  -- locks row 2
UPDATE accounts SET balance = balance + 100 WHERE id = 1;  -- WAITS for row 1 (held by A)

-- Session A:
UPDATE accounts SET balance = balance + 100 WHERE id = 2;  -- WAITS for row 2 (held by B)
-- DEADLOCK: A waits for B, B waits for A

-- The database detects the cycle and kills one transaction:
-- PostgreSQL: ERROR: deadlock detected  (SQLSTATE 40P01)
-- MySQL:      ERROR 1213: Deadlock found when trying to get lock; try restarting transaction
```

```
   Session A                      Session B
   holds row 1  ───wants──►  row 2 (held by B)
   row 1 ◄──wants───  holds row 2
           cycle → one victim is rolled back
```

**Prevention — always lock in the same order:**
```sql
-- Both sessions touch rows in ascending id order: the second one simply waits, no cycle
-- Session A:
UPDATE accounts SET balance = balance - 100 WHERE id = 1;  -- lower id first
UPDATE accounts SET balance = balance + 100 WHERE id = 2;

-- Session B (transfer from 2 to 1, still locks id 1 first):
UPDATE accounts SET balance = balance + 100 WHERE id = 1;
UPDATE accounts SET balance = balance - 100 WHERE id = 2;
```

**Deadlock checklist:**
- Access rows/tables in a **consistent order** (sort IDs before updating).
- Keep transactions **short**; no user input or remote calls inside them.
- Add **indexes** so updates lock only the rows they need (especially on InnoDB).
- Lock up front with `SELECT ... FOR UPDATE` on all rows you'll change, in key order.
- **Retry** the victim transaction (it's normal for deadlocks to happen occasionally).
- Diagnose: PostgreSQL logs deadlocks (`log_lock_waits = on`); MySQL `SHOW ENGINE INNODB STATUS` shows the latest deadlock (`innodb_print_all_deadlocks = ON` logs all).

---

### Optimistic vs Pessimistic Locking

| | Pessimistic | Optimistic |
|---|---|---|
| Idea | "Conflicts are likely — lock first" | "Conflicts are rare — check at write time" |
| Mechanism | `SELECT ... FOR UPDATE` (database lock) | Version/timestamp column checked in `UPDATE ... WHERE version = ?` |
| Others wait? | Yes, until commit | No; the loser gets 0 rows updated and retries |
| Best for | Hot rows, short transactions (stock, seats, balances) | Low contention, long user "think time" (edit forms, REST APIs) |
| JPA | `@Lock(LockModeType.PESSIMISTIC_WRITE)` | `@Version` field |

```sql
-- PESSIMISTIC: lock the row when reading
BEGIN;
SELECT * FROM inventory WHERE product_id = 42 FOR UPDATE;
-- row is locked; nobody else can modify or lock it until COMMIT
UPDATE inventory SET quantity = quantity - 1 WHERE product_id = 42;
COMMIT;

-- OPTIMISTIC: no lock; check for conflicts at update time
SELECT quantity, version FROM inventory WHERE product_id = 42;
-- Returns: quantity=10, version=7

UPDATE inventory
SET quantity = 9, version = version + 1
WHERE product_id = 42 AND version = 7;
-- 1 row updated  → success
-- 0 rows updated → someone else changed it first: reload and retry, or report a conflict
```

In Spring Data JPA: [@Version](java-spring-boot-document-with-full-details.md#version) (optimistic) and [@Lock](java-spring-boot-document-with-full-details.md#lock) (pessimistic).

---

### Lock Monitoring

```sql
-- PostgreSQL: who is blocking whom
SELECT blocked.pid                  AS blocked_pid,
       blocked.query                AS blocked_query,
       blocking.pid                 AS blocking_pid,
       blocking.query               AS blocking_query,
       now() - blocked.query_start  AS waiting_for
FROM pg_stat_activity blocked
JOIN pg_stat_activity blocking
  ON blocking.pid = ANY (pg_blocking_pids(blocked.pid));

-- PostgreSQL: all locks on tables
SELECT l.pid, l.relation::regclass AS table_name, l.mode, l.granted
FROM pg_locks l
WHERE l.relation IS NOT NULL
ORDER BY l.granted, l.pid;

-- PostgreSQL: log waits longer than deadlock_timeout (1 s)
-- postgresql.conf: log_lock_waits = on

-- MySQL 8.0: lock waits (information_schema.INNODB_LOCK_WAITS was removed in 8.0)
SELECT * FROM sys.innodb_lock_waits;
SELECT * FROM performance_schema.data_locks;           -- all current InnoDB locks
SELECT * FROM performance_schema.data_lock_waits;
SHOW ENGINE INNODB STATUS;                              -- latest deadlock details

-- SQL Server
SELECT * FROM sys.dm_tran_locks;
EXEC sp_who2;
```

### Lock Types Summary

| Lock | Scope | Taken by | Notes |
|---|---|---|---|
| Shared (S) | Row / table | `FOR SHARE`, `LOCK ... SHARE` | Many readers, no writers |
| Exclusive (X) | Row / table | `UPDATE`, `DELETE`, `FOR UPDATE` | One holder only |
| Update (U) | Row / page | SQL Server `UPDATE` / `UPDLOCK` | Avoids S→X conversion deadlocks |
| Intent (IS, IX, SIX) | Table | Automatically before row locks | Fast table-level conflict checks |
| Key-share / no-key-update | Row | PostgreSQL FKs and non-key updates | Reduce FK blocking |
| Gap / next-key / insert intention | Index range | InnoDB at `REPEATABLE READ` | Prevent phantoms |
| Predicate (SIRead) locks | Index range / page / table | PostgreSQL `SERIALIZABLE` | Don't block; detect serialization conflicts |
| Metadata lock (MDL) / `ACCESS EXCLUSIVE` | Table definition | DDL | Beware lock queues |
| AUTO-INC | Table counter | InnoDB inserts | Mode 2 = minimal locking |
| Advisory | Application-defined | `pg_advisory_lock`, `GET_LOCK` | Coordinate jobs across servers |

[⬆ Back to Table of Contents](#table-of-contents)

---

## 6. Indexing

### B+Tree Index Internals

A B+Tree has three node types:
- **Root node** — top of the tree
- **Internal nodes** — routing nodes with key ranges
- **Leaf nodes** — actual index entries with row pointers; linked as a doubly-linked list (enables range scans)

```
         [50]
        /    \
    [20,35]  [70,90]
   /  |  \    /  |  \
[..][..][..][..][..][..]  ← leaf nodes (linked list)
```

```sql
-- Create a B+Tree index (default type)
CREATE INDEX idx_employees_salary ON employees(salary);

-- Range scan benefits from linked leaf nodes:
SELECT * FROM employees WHERE salary BETWEEN 50000 AND 80000;
-- Finds first leaf entry >= 50000, then follows linked list to 80000
```

---

### Clustered vs Non-Clustered Index

```sql
-- InnoDB: data is always stored in the clustered index (PK order)
CREATE TABLE employees (
    emp_id  INT PRIMARY KEY,    -- clustered index; rows stored in emp_id order
    name    VARCHAR(100),
    dept_id INT,
    salary  DECIMAL(10,2)
);

-- Secondary (non-clustered) index stores: (salary, emp_id)
-- To get full row: look up salary in secondary index → get emp_id → look up in clustered index
CREATE INDEX idx_salary ON employees(salary);

-- This does TWO lookups (double lookup / bookmark lookup):
SELECT * FROM employees WHERE salary = 75000;
-- Step 1: find salary=75000 in idx_salary → returns emp_id=42
-- Step 2: find emp_id=42 in clustered index → returns full row
```

---

### Covering Index

A **covering index** contains every column a query needs, so the database answers from the index alone (an *index-only scan*) without reading the table rows.

```sql
-- Query only needs salary and dept_id — no need to touch the main table
CREATE INDEX idx_covering ON employees(dept_id, salary);

-- This query is answered entirely from the index (no table access):
SELECT dept_id, salary FROM employees WHERE dept_id = 5;
-- EXPLAIN will show "Index Only Scan" (PostgreSQL) or "Using index" (MySQL)

-- vs this query which requires table access:
SELECT dept_id, salary, name FROM employees WHERE dept_id = 5;
-- name is not in the index → must hit the table
-- Fix: include name in the covering index
CREATE INDEX idx_covering_v2 ON employees(dept_id, salary, name);
```

---

### Composite Index & Leftmost Prefix Rule

A **composite** (multi-column) index is sorted by the first column, then the second, and so on, like a phone book sorted by last name then first name. It can only be searched efficiently from its **leftmost** columns.

```sql
CREATE INDEX idx_composite ON employees(dept_id, job_title, salary);

-- USES the index (leftmost prefix):
SELECT * FROM employees WHERE dept_id = 3;
SELECT * FROM employees WHERE dept_id = 3 AND job_title = 'Engineer';
SELECT * FROM employees WHERE dept_id = 3 AND job_title = 'Engineer' AND salary > 50000;

-- DOES NOT use the index efficiently (skips leftmost column):
SELECT * FROM employees WHERE job_title = 'Engineer';        -- full scan
SELECT * FROM employees WHERE salary > 50000;                -- full scan
SELECT * FROM employees WHERE job_title = 'Engineer' AND salary > 50000;  -- full scan
```

**Rule:** The index `(A, B, C)` is usable when the query filters on `A`, `A+B`, or `A+B+C`. Skipping `A` usually makes the index unusable for seeks.

> Some optimizers can still use it with a **skip scan** when `A` has few distinct values (Oracle, MySQL 8.0.13+, PostgreSQL 18+), but don't design for it: put the most frequently filtered, equality-compared column first.

---

### Partial Index

A **partial index** (PostgreSQL, SQLite; SQL Server calls it a *filtered index*) indexes only the rows matching a `WHERE` condition. It's smaller and faster when queries always target that subset.

```sql
-- Only active users need fast lookup; inactive users are rarely queried
CREATE INDEX idx_active_users ON users(email)
WHERE is_active = TRUE;

-- This index is much smaller than a full index on email
-- Query that uses it:
SELECT * FROM users WHERE email = 'alice@x.com' AND is_active = TRUE;

-- Real-world example: index only unpaid invoices
CREATE INDEX idx_unpaid ON invoices(customer_id, due_date)
WHERE paid = FALSE;
```

---

### Functional / Expression Index

An **expression index** stores the result of a function or expression instead of the raw column value. The query must use the **same expression** for the index to be considered.

```sql
-- Without functional index, this query does a full table scan:
SELECT * FROM users WHERE LOWER(email) = 'alice@example.com';

-- Create a functional index:
CREATE INDEX idx_email_lower ON users(LOWER(email));

-- Now the query uses the index:
SELECT * FROM users WHERE LOWER(email) = 'alice@example.com';
```

---

### Index Bloat and Maintenance

Updates and deletes leave unused space inside indexes (**bloat**), making them larger and slower. Monitor index size and usage, drop unused indexes, and rebuild bloated ones.

```sql
-- PostgreSQL: check index bloat
SELECT schemaname, relname AS table_name, indexrelname AS index_name,
       idx_scan,                                    -- 0 scans = possibly unused index
       pg_size_pretty(pg_relation_size(indexrelid)) AS index_size
FROM pg_stat_user_indexes
ORDER BY pg_relation_size(indexrelid) DESC;

-- Rebuild a bloated index without locking the table:
REINDEX INDEX CONCURRENTLY idx_employees_salary;

-- MySQL: optimize table (rebuilds indexes)
OPTIMIZE TABLE employees;
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 7. Query Optimizer & Execution Plans

### Reading EXPLAIN Output

`EXPLAIN` shows the **plan** the optimizer chose; `EXPLAIN ANALYZE` also **runs** the query and shows actual row counts and timings. Read plans from the innermost (most indented) node outwards.

```sql
-- PostgreSQL EXPLAIN ANALYZE
EXPLAIN ANALYZE
SELECT e.name, d.dept_name
FROM employees e
JOIN departments d ON d.dept_id = e.dept_id
WHERE e.salary > 80000;

-- Sample output:
-- Hash Join  (cost=12.50..45.30 rows=150 width=40) (actual time=0.5..2.1 rows=143 loops=1)
--   Hash Cond: (e.dept_id = d.dept_id)
--   ->  Seq Scan on employees e  (cost=0..30.0 rows=500) (actual rows=500 loops=1)
--         Filter: (salary > 80000)
--         Rows Removed by Filter: 357
--   ->  Hash  (cost=6.00..6.00 rows=50) (actual rows=50 loops=1)
--         ->  Seq Scan on departments d  (cost=0..6.0 rows=50)
-- Planning Time: 0.3 ms
-- Execution Time: 2.5 ms
```

**Key things to look for:**
- `Seq Scan` on large tables = missing index
- Large difference between estimated rows and actual rows = stale statistics
- High loops count = N+1 problem
- `Hash` on huge tables = memory spill to disk

---

### Join Algorithms

The optimizer chooses one of three physical join algorithms based on table sizes, available indexes, sort order and memory.

| Algorithm | Best when | Cost |
|---|---|---|
| Nested loop | Outer side small, inner side indexed | O(n · log m) |
| Hash join | Large, unsorted inputs, equality joins | O(n + m), needs memory |
| Merge join | Both inputs already sorted on the join key | O(n + m) if presorted, plus sort cost otherwise |

```sql
-- Force specific join type in PostgreSQL (for testing):
SET enable_hashjoin = OFF;
SET enable_mergejoin = OFF;
-- Now optimizer will use nested loop

-- Nested Loop Join — good when inner side is small with an index:
-- For each row in employees (outer), find matching dept (inner via index)
-- Cost: O(n * log m)  where n=outer rows, m=inner rows

-- Hash Join — good for large unsorted tables:
-- Phase 1: Build hash table from smaller table (departments)
-- Phase 2: Probe hash table for each row in larger table (employees)
-- Cost: O(n + m)  but requires memory for hash table

-- Sort-Merge Join — good when both sides are already sorted:
-- Sort employees by dept_id, sort departments by dept_id, merge
-- Cost: O(n log n + m log m)
```

---

### Statistics and Cardinality Estimation

The optimizer estimates how many rows each step returns (**cardinality**) using table **statistics** (row counts, distinct values, histograms). Stale statistics lead to bad plans, so they're refreshed by `ANALYZE` (automatically by autovacuum/auto-stats, or manually after big data loads).

```sql
-- PostgreSQL: update statistics manually
ANALYZE employees;

-- View statistics
SELECT tablename, attname, n_distinct, correlation
FROM pg_stats
WHERE tablename = 'employees';

-- n_distinct: number of distinct values (negative = fraction of total)
-- correlation: how sorted the column is on disk (1.0 = perfectly sorted)

-- MySQL: update statistics
ANALYZE TABLE employees;

-- View index cardinality
SHOW INDEX FROM employees;
-- Cardinality column shows estimated distinct values in index
```

---

### Parameter Sniffing (SQL Server Problem)

SQL Server caches one plan per parameterized query, compiled for the **first** parameter values it sees. If later values have a very different selectivity, the cached plan can be badly wrong.

```sql
-- SQL Server compiles a plan for the first execution
-- If first call uses dept_id=1 (1000 rows), plan uses table scan
-- Later calls with dept_id=99 (2 rows) still use table scan — wrong plan!

-- Fix 1: Recompile hint
SELECT * FROM employees WHERE dept_id = @dept_id
OPTION (RECOMPILE);

-- Fix 2: OPTIMIZE FOR
SELECT * FROM employees WHERE dept_id = @dept_id
OPTION (OPTIMIZE FOR (@dept_id = 5));

-- Fix 3: Clear plan cache (drastic — affects all plans)
DBCC FREEPROCCACHE;
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 8. Storage Architecture & Internals

### Page Structure

Every RDBMS stores data in fixed-size pages (usually 8KB in PostgreSQL, 16KB in MySQL InnoDB).

```
Page Layout (simplified):
+----------------------------------+
| Page Header (24 bytes)           |  ← LSN, page type, free space info
+----------------------------------+
| Item Pointers (array)            |  ← offset to each tuple
+----------------------------------+
| Free Space                       |
+----------------------------------+
| Tuples (row data, newest first)  |  ← actual row data
+----------------------------------+
| Special Space                    |  ← used by index pages
+----------------------------------+
```

```sql
-- PostgreSQL: see page internals (requires pageinspect extension)
CREATE EXTENSION pageinspect;

-- View page header
SELECT * FROM page_header(get_raw_page('employees', 0));

-- View heap tuples on page 0
SELECT lp, lp_off, lp_len, t_xmin, t_xmax, t_ctid
FROM heap_page_items(get_raw_page('employees', 0));
```

---

### Buffer Pool / Shared Buffer

The **buffer pool** (InnoDB) / **shared buffers** (PostgreSQL) caches data pages in memory. A high **hit ratio** means most reads are served from RAM instead of disk.

```sql
-- PostgreSQL: configure shared_buffers
-- In postgresql.conf:
-- shared_buffers = 4GB   (for 16GB RAM server)

-- Check buffer hit rate (should be > 99% for OLTP):
SELECT
    sum(heap_blks_read)  AS disk_reads,
    sum(heap_blks_hit)   AS buffer_hits,
    ROUND(100.0 * sum(heap_blks_hit) /
          NULLIF(sum(heap_blks_hit) + sum(heap_blks_read), 0), 2) AS hit_ratio
FROM pg_statio_user_tables;

-- MySQL: check InnoDB buffer pool hit rate
SHOW GLOBAL STATUS LIKE 'Innodb_buffer_pool%';
-- Innodb_buffer_pool_reads: pages read from disk
-- Innodb_buffer_pool_read_requests: total page requests
-- Hit rate = 1 - (reads / read_requests)  should be > 0.99
```

---

### Write-Ahead Logging (WAL)

**WAL** (PostgreSQL) / **redo log** (InnoDB) records every change **before** the data pages are written. On commit only the log must be flushed to disk; data pages are written later. After a crash, the log is replayed to restore committed changes.

```
WAL write sequence for UPDATE:
1. BEGIN transaction
2. Write "before image" to undo log (for rollback/MVCC)
3. Write WAL record: "change employees row #42 salary from 50000 to 60000"
4. Modify page in buffer pool (in memory only)
5. COMMIT: fsync WAL to disk ← durability guaranteed here
6. Return success to client
7. Later: checkpoint process flushes dirty buffer pool pages to disk
```

```sql
-- PostgreSQL: check WAL settings
SHOW wal_level;            -- minimal, replica, logical
SHOW synchronous_commit;   -- on, remote_apply, remote_write, local, off

-- WAL location and lag
SELECT pg_current_wal_lsn();        -- current WAL write position
SELECT pg_walfile_name(pg_current_wal_lsn());  -- current WAL file name

-- MySQL: check InnoDB redo log
SHOW VARIABLES LIKE 'innodb_log%';
-- innodb_log_file_size: size of each redo log file
-- innodb_log_buffer_size: in-memory log buffer
```

---

### Checkpoint Mechanism

A **checkpoint** writes all dirty pages from memory to the data files and marks a point in the WAL from which crash recovery starts. More frequent checkpoints mean faster recovery but more I/O.

```sql
-- PostgreSQL checkpoint settings
-- In postgresql.conf:
-- checkpoint_timeout = 5min     (max time between checkpoints)
-- max_wal_size = 1GB            (trigger checkpoint if WAL exceeds this)
-- checkpoint_completion_target = 0.9  (spread I/O over 90% of interval)

-- Force a checkpoint manually (e.g., before backup):
CHECKPOINT;

-- Monitor checkpoint frequency:
SELECT checkpoints_timed, checkpoints_req, checkpoint_write_time
FROM pg_stat_bgwriter;                                   -- PostgreSQL 16 and older

SELECT num_timed, num_requested, write_time
FROM pg_stat_checkpointer;                               -- PostgreSQL 17+
-- requested >> timed: WAL filling up too fast → increase max_wal_size
```

---

### VACUUM and Dead Tuples (PostgreSQL)

In PostgreSQL, `UPDATE` and `DELETE` leave old row versions (**dead tuples**) in the table (see [MVCC](#mvcc-deep-dive)). `VACUUM` marks their space as reusable; autovacuum does this automatically, but heavy-write tables may need tuning.

```sql
-- PostgreSQL MVCC creates dead tuples on UPDATE/DELETE
-- UPDATE actually inserts a new version and marks old as dead

-- Check table bloat:
SELECT relname, n_live_tup, n_dead_tup,
       ROUND(100.0 * n_dead_tup / NULLIF(n_live_tup + n_dead_tup, 0), 2) AS dead_pct
FROM pg_stat_user_tables
ORDER BY n_dead_tup DESC;

-- Manual vacuum (reclaims dead tuple space):
VACUUM employees;

-- Rewrite the whole table (returns disk space to the OS, but takes an ACCESS EXCLUSIVE lock — blocks all reads/writes):
VACUUM FULL employees;

-- Plain VACUUM doesn't block reads or writes; ANALYZE also refreshes planner statistics:
VACUUM (VERBOSE, ANALYZE) employees;

-- Check autovacuum status:
SELECT relname, last_autovacuum, last_autoanalyze
FROM pg_stat_user_tables
WHERE relname = 'employees';
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 9. Partitioning

### Range Partitioning (most common — for time-series data)

**Partitioning** splits one large logical table into smaller physical tables (partitions). **Range** partitioning assigns rows by value ranges (dates, IDs). Queries that filter on the partition key read only the relevant partitions (**partition pruning**).

```sql
-- PostgreSQL declarative partitioning
CREATE TABLE orders (
    order_id   BIGINT,
    order_date DATE NOT NULL,
    customer_id INT,
    amount     DECIMAL(10,2)
) PARTITION BY RANGE (order_date);

-- Create partitions for each year
CREATE TABLE orders_2022 PARTITION OF orders
    FOR VALUES FROM ('2022-01-01') TO ('2023-01-01');

CREATE TABLE orders_2023 PARTITION OF orders
    FOR VALUES FROM ('2023-01-01') TO ('2024-01-01');

CREATE TABLE orders_2024 PARTITION OF orders
    FOR VALUES FROM ('2024-01-01') TO ('2025-01-01');

-- Default partition catches anything that doesn't match
CREATE TABLE orders_default PARTITION OF orders DEFAULT;

-- INSERT automatically routes to correct partition:
INSERT INTO orders VALUES (1, '2023-06-15', 42, 150.00);
-- Row goes into orders_2023

-- Partition pruning: optimizer skips irrelevant partitions
EXPLAIN SELECT * FROM orders WHERE order_date >= '2023-01-01' AND order_date < '2024-01-01';
-- Shows: only orders_2023 is scanned, others are pruned
```

---

### List Partitioning

**List** partitioning assigns rows by explicit values of the key (regions, countries, status codes).

```sql
CREATE TABLE sales (
    sale_id INT,
    region  VARCHAR(10),
    amount  DECIMAL(10,2)
) PARTITION BY LIST (region);

CREATE TABLE sales_us     PARTITION OF sales FOR VALUES IN ('US', 'CA', 'MX');
CREATE TABLE sales_europe PARTITION OF sales FOR VALUES IN ('UK', 'DE', 'FR', 'IT');
CREATE TABLE sales_apac   PARTITION OF sales FOR VALUES IN ('JP', 'AU', 'IN', 'SG');
CREATE TABLE sales_other  PARTITION OF sales DEFAULT;
```

---

### Hash Partitioning

**Hash** partitioning spreads rows evenly across a fixed number of partitions using a hash of the key. Useful when there's no natural range and you want balanced partitions.

```sql
CREATE TABLE user_events (
    event_id  BIGINT,
    user_id   INT,
    event_type VARCHAR(50),
    created_at TIMESTAMP
) PARTITION BY HASH (user_id);

-- Distribute across 4 partitions evenly
CREATE TABLE user_events_0 PARTITION OF user_events FOR VALUES WITH (MODULUS 4, REMAINDER 0);
CREATE TABLE user_events_1 PARTITION OF user_events FOR VALUES WITH (MODULUS 4, REMAINDER 1);
CREATE TABLE user_events_2 PARTITION OF user_events FOR VALUES WITH (MODULUS 4, REMAINDER 2);
CREATE TABLE user_events_3 PARTITION OF user_events FOR VALUES WITH (MODULUS 4, REMAINDER 3);
-- user_id % 4 determines which partition
```

---

### Indexes on Partitioned Tables

An index created on a partitioned table is automatically created on **every partition** (local indexes). Each partition can then be maintained, vacuumed or dropped on its own.

```sql
-- Local index: one index per partition (recommended)
CREATE INDEX ON orders (customer_id);
-- Creates idx on orders_2022, orders_2023, orders_2024 automatically

-- Each partition can be maintained, vacuumed, and dropped independently
-- Archiving old data is as simple as:
DROP TABLE orders_2022;  -- instantly drops partition + all its indexes + data
-- No DELETE needed, no vacuum needed, instant operation
```

---

### MySQL Partitioning

MySQL declares partitions inside `CREATE TABLE`. The partition key must be part of **every unique key** (including the primary key) of the table.

```sql
-- MySQL range partitioning by YEAR
CREATE TABLE orders (
    order_id   INT,
    order_date DATE,
    amount     DECIMAL(10,2)
)
PARTITION BY RANGE (YEAR(order_date)) (
    PARTITION p2022 VALUES LESS THAN (2023),
    PARTITION p2023 VALUES LESS THAN (2024),
    PARTITION p2024 VALUES LESS THAN (2025),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 10. Replication & High Availability

### PostgreSQL Streaming Replication Setup

**Streaming replication** sends the primary's WAL to one or more **standby** servers that replay it continuously, giving read-only replicas and failover targets.

```bash
# On PRIMARY (postgresql.conf):
wal_level = replica
max_wal_senders = 3
wal_keep_size = 1GB

# On PRIMARY (pg_hba.conf): allow replica connection
host replication replicator 192.168.1.101/32 md5

# Create replication user:
CREATE USER replicator WITH REPLICATION PASSWORD 'secret';

# On REPLICA: clone primary
pg_basebackup -h 192.168.1.100 -U replicator -D /var/lib/postgresql/data -Fp -Xs -P -R

# -R flag creates standby.signal and recovery settings automatically
# Start replica — it will stream WAL from primary
```

```sql
-- Monitor replication lag (on primary):
SELECT client_addr, state, sent_lsn, write_lsn, flush_lsn, replay_lsn,
       (sent_lsn - replay_lsn) AS replication_lag_bytes
FROM pg_stat_replication;

-- Check if this server is primary or replica:
SELECT pg_is_in_recovery();
-- TRUE = replica, FALSE = primary
```

---

### Synchronous vs Asynchronous Replication

With **asynchronous** replication the primary commits without waiting for replicas (fast, but recent commits can be lost on failover). With **synchronous** replication it waits for the replica's confirmation (no data loss, higher commit latency).

```sql
-- PostgreSQL: configure synchronous replication
-- postgresql.conf on primary:
synchronous_standby_names = 'replica1'
-- Primary waits for replica1 to confirm WAL receipt before COMMIT returns

-- Per-transaction override:
SET synchronous_commit = 'off';      -- async for this session (faster, small data loss risk)
SET synchronous_commit = 'on';       -- wait for local WAL flush
SET synchronous_commit = 'remote_write';  -- wait for replica to receive
SET synchronous_commit = 'remote_apply';  -- wait for replica to apply
```

---

### Logical Replication

**Logical replication** replicates **row changes** for selected tables (publish/subscribe) instead of whole WAL files. It works across major versions and allows replicating only part of a database.

```sql
-- Use case: replicate specific tables, or replicate to different PostgreSQL version

-- On publisher (source):
ALTER SYSTEM SET wal_level = 'logical';
SELECT pg_reload_conf();

CREATE PUBLICATION my_pub FOR TABLE employees, departments;

-- On subscriber (destination):
CREATE SUBSCRIPTION my_sub
    CONNECTION 'host=192.168.1.100 dbname=mydb user=replicator password=secret'
    PUBLICATION my_pub;

-- Monitor logical replication:
SELECT * FROM pg_stat_subscription;
SELECT * FROM pg_replication_slots;
```

---

### Patroni — Automated Failover for PostgreSQL

**Patroni** manages a PostgreSQL cluster: it uses a distributed store (etcd, Consul, ZooKeeper) to elect a leader and automatically promotes a replica when the primary fails.

```yaml
# patroni.yml (simplified)
scope: postgres-cluster
name: node1

postgresql:
  listen: 0.0.0.0:5432
  data_dir: /var/lib/postgresql/data

bootstrap:
  dcs:
    ttl: 30
    loop_wait: 10
    retry_timeout: 10
    maximum_lag_on_failover: 1048576  # 1MB max lag for failover eligibility

  pg_hba:
    - host replication replicator 0.0.0.0/0 md5

etcd:
  hosts: etcd1:2379,etcd2:2379,etcd3:2379
```

```bash
# Check cluster status
patronictl -c /etc/patroni.yml list
# Shows: primary, replicas, lag, state

# Manual failover
patronictl -c /etc/patroni.yml switchover postgres-cluster --leader node1 --candidate node2
# (planned switchover; use "failover" when the leader is down. --master is deprecated in Patroni 3+)
```

---

### ProxySQL (MySQL Connection Pooling + Read/Write Split)

**ProxySQL** sits between the application and MySQL, pools connections, and routes queries to the primary or replicas based on rules.

```sql
-- ProxySQL routes writes to primary, reads to replicas automatically

-- In ProxySQL admin:
INSERT INTO mysql_servers(hostgroup_id, hostname, port) VALUES
    (1, 'primary-host',  3306),   -- hostgroup 1 = writes
    (2, 'replica1-host', 3306),   -- hostgroup 2 = reads
    (2, 'replica2-host', 3306);

-- Routing rules:
INSERT INTO mysql_query_rules(rule_id, active, match_digest, destination_hostgroup, apply) VALUES
    (1, 1, '^SELECT.*FOR UPDATE', 1, 1),   -- locking reads must go to the primary
    (2, 1, '^SELECT',             2, 1);   -- other SELECTs go to replicas
-- Everything else uses the user's default_hostgroup (the primary, hostgroup 1)

LOAD MYSQL QUERY RULES TO RUNTIME;
SAVE MYSQL QUERY RULES TO DISK;

LOAD MYSQL SERVERS TO RUNTIME;
SAVE MYSQL SERVERS TO DISK;
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 11. Backup & Recovery

### Logical Backup

A **logical backup** exports data as SQL statements or an archive (`pg_dump`, `mysqldump`). It's portable across versions and can restore single tables, but is slower for very large databases.

```bash
# PostgreSQL: dump single database
pg_dump -h localhost -U postgres -d mydb -F c -f mydb.dump
# -F c = custom format (compressed, supports parallel restore)

# Restore:
pg_restore -h localhost -U postgres -d mydb_restored -j 4 mydb.dump
# -j 4 = 4 parallel workers

# Dump specific tables:
pg_dump -t employees -t departments mydb > partial.sql

# MySQL: dump
mysqldump -u root -p --single-transaction --routines --triggers mydb > mydb.sql
# --single-transaction: consistent snapshot without locking (InnoDB only)

# MySQL restore:
mysql -u root -p mydb < mydb.sql
```

---

### Physical Backup (PostgreSQL)

A **physical backup** copies the database files themselves. It's much faster to take and restore for large databases and is the base for point-in-time recovery.

```bash
# pg_basebackup: online physical backup
pg_basebackup -h localhost -U replicator -D /backup/base -Ft -z -P
# -Ft = tar format
# -z = compress
# -P = progress

# Resulting files:
# /backup/base/base.tar.gz       (data directory)
# /backup/base/pg_wal.tar.gz     (WAL files needed for consistency)
```

---

### Point-in-Time Recovery (PITR)

**PITR** restores a base backup and then replays archived WAL up to a chosen moment, for example just before an accidental `DELETE`.

```bash
# Setup WAL archiving (postgresql.conf):
archive_mode = on
archive_command = 'cp %p /wal_archive/%f'
# %p = path to WAL file, %f = filename

# Scenario: accidental DELETE at 14:37 on 2024-01-15
# Recovery steps:

# Step 1: Restore the latest base backup
rm -rf /var/lib/postgresql/data
tar -xzf /backup/base/base.tar.gz -C /var/lib/postgresql/data

# Step 2: Configure the recovery target (PostgreSQL 12+: settings go in postgresql.conf;
#         recovery.conf no longer exists). APPEND with >> — never overwrite the config file.
cat >> /var/lib/postgresql/data/postgresql.conf << EOF
restore_command = 'cp /wal_archive/%f %p'
recovery_target_time = '2024-01-15 14:35:00'   # 2 min before the accident
recovery_target_action = 'promote'
EOF

touch /var/lib/postgresql/data/recovery.signal

# Step 3: Start PostgreSQL — it will replay WAL up to 14:35
pg_ctl start -D /var/lib/postgresql/data
# Database will stop replay at 14:35 and promote to primary
```

---

### Percona XtraBackup (MySQL Hot Backup)

**XtraBackup** takes physical backups of InnoDB **while the server is running** (hot backup) without blocking writes, and supports incremental backups.

```bash
# Full backup (no table locks for InnoDB):
xtrabackup --backup --target-dir=/backup/full --user=root --password=secret

# Prepare backup (apply redo logs):
xtrabackup --prepare --target-dir=/backup/full

# Restore:
systemctl stop mysql
rsync -avrP /backup/full/ /var/lib/mysql/
chown -R mysql:mysql /var/lib/mysql
systemctl start mysql

# Incremental backup (after a full backup):
xtrabackup --backup --target-dir=/backup/inc1 --incremental-basedir=/backup/full
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 12. Performance Tuning

### Slow Query Log

The first step of tuning is finding the queries that cost the most **in total** (calls × average time), not just the single slowest one.

```sql
-- PostgreSQL: log slow queries
-- postgresql.conf:
log_min_duration_statement = 1000   -- log queries taking > 1 second
log_line_prefix = '%t [%p]: [%l-1] user=%u,db=%d '

-- View via pg_stat_statements (best tool):
CREATE EXTENSION pg_stat_statements;

SELECT query,
       calls,
       ROUND(total_exec_time::numeric, 2) AS total_ms,
       ROUND(mean_exec_time::numeric, 2)  AS avg_ms,
       ROUND(stddev_exec_time::numeric, 2) AS stddev_ms,
       rows
FROM pg_stat_statements
ORDER BY total_exec_time DESC
LIMIT 10;

-- MySQL: enable slow query log
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 1;
SET GLOBAL slow_query_log_file = '/var/log/mysql/slow.log';
```

---

### N+1 Query Problem

The **N+1 problem**: one query loads N parent rows, then the application runs one more query **per row** to load children. It's the most common ORM performance issue (lazy-loaded JPA relations). Fix it with a join, a batched `IN (...)` query, or an entity graph.

```sql
-- BAD: Application loop (N+1 problem)
-- Query 1: get all orders
SELECT order_id FROM orders WHERE customer_id = 42;
-- Returns 50 orders

-- Then for each order (50 more queries!):
SELECT * FROM order_items WHERE order_id = 1;
SELECT * FROM order_items WHERE order_id = 2;
-- ... 50 total queries for order_items

-- GOOD: One query with JOIN
SELECT o.order_id, oi.product_id, oi.quantity
FROM orders o
JOIN order_items oi ON oi.order_id = o.order_id
WHERE o.customer_id = 42;
-- 1 query, same result
```

---

### Function on Indexed Column (Index Killer)

Wrapping an indexed column in a function (`YEAR(col)`, `LOWER(col)`, `col + 1`) hides the column from a normal index, so the database must evaluate the function for **every row**.

```sql
-- BAD: function wrapping indexed column prevents index use
SELECT * FROM orders WHERE YEAR(order_date) = 2024;         -- MySQL: full scan
SELECT * FROM orders WHERE DATE_TRUNC('year', order_date) = '2024-01-01';  -- PG: full scan
SELECT * FROM orders WHERE TO_CHAR(order_date, 'YYYY') = '2024';  -- full scan

-- GOOD: use range condition on the column directly
SELECT * FROM orders WHERE order_date >= '2024-01-01' AND order_date < '2025-01-01';
-- Uses index on order_date

-- GOOD alternative: functional index (if you can't change the query)
CREATE INDEX idx_order_year ON orders ((YEAR(order_date)));                  -- MySQL 8.0.13+ (note double parentheses)
CREATE INDEX idx_order_year ON orders ((EXTRACT(YEAR FROM order_date)));     -- PostgreSQL (expression in parentheses)
```

---

### Keyset Pagination (vs OFFSET)

`OFFSET` pagination reads and throws away all earlier rows, so deep pages get slower. **Keyset** (cursor) pagination continues from the last row seen and stays fast. Spring Data supports it with `ScrollPosition`; see [Keyset (Scroll) Pagination](java-spring-boot-document-with-full-details.md#749-keyset-scroll-pagination).

```sql
-- BAD: OFFSET pagination at scale
SELECT * FROM posts ORDER BY created_at DESC LIMIT 20 OFFSET 100000;
-- Scans 100,020 rows just to discard 100,000 of them. Gets slower with every page.

-- GOOD: Keyset (cursor) pagination
-- First page:
SELECT * FROM posts ORDER BY created_at DESC, post_id DESC LIMIT 20;
-- Returns rows, last row has: created_at='2024-01-10 12:00', post_id=9876

-- Next page (using last row's values as cursor):
SELECT * FROM posts
WHERE (created_at, post_id) < ('2024-01-10 12:00', 9876)
ORDER BY created_at DESC, post_id DESC
LIMIT 20;
-- Uses index efficiently regardless of page depth
```

---

### Implicit Type Conversion

When the two sides of a comparison have different types, the database converts one side. If it has to convert the **column**, the index on that column can't be used.

```sql
-- OK-ish: INT column compared with a string constant
SELECT * FROM users WHERE user_id = '123';   -- user_id is INT
-- MySQL converts the constant once and still uses the index; PostgreSQL treats '123' as an
-- untyped literal and casts it to INT. Works, but write the correct type.

-- BAD: VARCHAR column compared with a number
SELECT * FROM users WHERE phone_number = 5551234;  -- phone_number is VARCHAR
-- MySQL converts EVERY row's value to a number → index can't be used (full scan),
-- and '5551234abc' or ' 5551234' also match. PostgreSQL raises an error (operator does not exist).

-- BAD: joining columns of different types (INT vs VARCHAR) or different collations
-- also prevents index use on one side of the join.

-- GOOD: always match types exactly
SELECT * FROM users WHERE user_id = 123;
SELECT * FROM users WHERE phone_number = '5551234';
```

---

### Connection Overhead & Pooling

Each database connection costs memory and setup time (a whole process per connection in PostgreSQL). Applications should use a **connection pool** (HikariCP in Spring Boot), and large deployments add a pooler like PgBouncer in front of the database.

```sql
-- Check current connections (PostgreSQL):
SELECT count(*) FROM pg_stat_activity;
SELECT state, count(*) FROM pg_stat_activity GROUP BY state;
-- state: active, idle, idle in transaction, idle in transaction (aborted)

-- "idle in transaction" is dangerous: holds locks but does nothing
-- Set a timeout:
-- postgresql.conf:
-- idle_in_transaction_session_timeout = 30s

-- PgBouncer config (pgbouncer.ini):
[databases]
mydb = host=127.0.0.1 port=5432 dbname=mydb

[pgbouncer]
pool_mode = transaction         ; release the server connection after each transaction
max_client_conn = 1000          ; clients can open up to 1000 connections
default_pool_size = 25          ; only 25 real DB connections per database/user pair
```

---

### Monitoring Queries in Flight

Find long-running or stuck queries and, when needed, cancel the query (the transaction rolls back) or terminate the whole connection.

```sql
-- PostgreSQL: kill long-running queries
SELECT pid, now() - pg_stat_activity.query_start AS duration, query, state
FROM pg_stat_activity
WHERE state != 'idle'
  AND (now() - pg_stat_activity.query_start) > INTERVAL '5 minutes';

-- Cancel a query gracefully (lets transaction roll back):
SELECT pg_cancel_backend(12345);

-- Kill a connection (more forceful):
SELECT pg_terminate_backend(12345);

-- MySQL: see running queries
SHOW PROCESSLIST;
KILL QUERY 12345;   -- cancel query
KILL 12345;         -- kill connection
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 13. Data Integrity & Constraints

### All Constraint Types

**Constraints** are rules enforced by the database itself, so bad data is rejected no matter which application writes it: `PRIMARY KEY`, `UNIQUE`, `NOT NULL`, `CHECK`, `FOREIGN KEY`, `DEFAULT` (and `EXCLUDE` in PostgreSQL).

```sql
CREATE TABLE employees (
    emp_id      SERIAL         PRIMARY KEY,                          -- PK: unique + not null
    email       VARCHAR(255)   UNIQUE NOT NULL,                      -- alternate key
    name        VARCHAR(100)   NOT NULL,
    dept_id     INT            REFERENCES departments(dept_id)       -- FK
                               ON DELETE RESTRICT
                               ON UPDATE CASCADE,
    salary      DECIMAL(10,2)  CHECK (salary > 0),                   -- check constraint
    hire_date   DATE           NOT NULL DEFAULT CURRENT_DATE,
    status      VARCHAR(20)    CHECK (status IN ('active','inactive','terminated'))
);

-- Table-level constraint (multi-column):
ALTER TABLE order_items
ADD CONSTRAINT chk_quantity CHECK (quantity > 0 AND quantity <= 10000);

-- Named constraint (easier to identify in errors):
ALTER TABLE employees
ADD CONSTRAINT chk_salary_range CHECK (salary BETWEEN 1000 AND 10000000);
```

---

### Foreign Key Cascade Behaviors

`ON DELETE` / `ON UPDATE` decide what happens to **child rows** when the referenced parent row is deleted or its key changes.

| Action | Effect on child rows |
|---|---|
| `NO ACTION` (default) | Reject the change if children exist (check can be deferred) |
| `RESTRICT` | Reject immediately |
| `CASCADE` | Delete / update the children too |
| `SET NULL` | Set the FK column to `NULL` |
| `SET DEFAULT` | Set the FK column to its default |

```sql
CREATE TABLE departments (
    dept_id   INT PRIMARY KEY,
    dept_name VARCHAR(100)
);

-- ON DELETE CASCADE: deleting dept deletes all employees in it
CREATE TABLE employees_cascade (
    emp_id  INT PRIMARY KEY,
    dept_id INT REFERENCES departments(dept_id) ON DELETE CASCADE
);

-- ON DELETE SET NULL: employee stays, dept_id becomes NULL
CREATE TABLE employees_setnull (
    emp_id  INT PRIMARY KEY,
    dept_id INT REFERENCES departments(dept_id) ON DELETE SET NULL
);

-- ON DELETE RESTRICT: cannot delete dept if employees exist (checked immediately)
CREATE TABLE employees_restrict (
    emp_id  INT PRIMARY KEY,
    dept_id INT REFERENCES departments(dept_id) ON DELETE RESTRICT
);

-- ON DELETE NO ACTION (the DEFAULT when nothing is specified): like RESTRICT,
-- but the check can be deferred to the end of the transaction (DEFERRABLE constraints)

-- Test:
DELETE FROM departments WHERE dept_id = 5;
-- CASCADE: deletes all employees with dept_id=5
-- SET NULL: sets their dept_id to NULL
-- RESTRICT: ERROR: update or delete on table "departments" violates foreign key constraint
```

---

### Deferred Constraints

Normally constraints are checked after **each statement**. A `DEFERRABLE INITIALLY DEFERRED` constraint is checked only at **COMMIT**, which allows temporary violations inside a transaction (circular references, swapping unique values).

```sql
-- Problem: inserting a parent-child circular relationship
CREATE TABLE nodes (
    node_id   INT PRIMARY KEY,
    parent_id INT REFERENCES nodes(node_id) DEFERRABLE INITIALLY DEFERRED
);

-- Without deferral, this fails because parent doesn't exist yet:
BEGIN;
INSERT INTO nodes VALUES (1, 2);  -- references node 2 which doesn't exist yet
INSERT INTO nodes VALUES (2, 1);  -- references node 1 which does exist
COMMIT;
-- With DEFERRABLE INITIALLY DEFERRED: FK check happens at COMMIT, not per-INSERT
-- Both rows exist at COMMIT time → succeeds
```

---

### Triggers

A **trigger** runs a function automatically before or after `INSERT`/`UPDATE`/`DELETE`. Useful for audit trails and derived data, but triggers hide logic from application developers and slow down writes; use them sparingly.

```sql
-- Audit trigger: record every salary change
CREATE TABLE salary_audit (
    audit_id   SERIAL PRIMARY KEY,
    emp_id     INT,
    old_salary DECIMAL(10,2),
    new_salary DECIMAL(10,2),
    changed_by TEXT,
    changed_at TIMESTAMP DEFAULT NOW()
);

-- PostgreSQL trigger function:
CREATE OR REPLACE FUNCTION log_salary_change()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.salary IS DISTINCT FROM NEW.salary THEN
        INSERT INTO salary_audit(emp_id, old_salary, new_salary, changed_by)
        VALUES (NEW.emp_id, OLD.salary, NEW.salary, current_user);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_salary_audit
AFTER UPDATE ON employees
FOR EACH ROW
EXECUTE FUNCTION log_salary_change();

-- Test:
UPDATE employees SET salary = 75000 WHERE emp_id = 42;
SELECT * FROM salary_audit;  -- shows the change
```

---

### Data Type Integrity

Choosing the right data type is the first integrity constraint: it rejects invalid values and enables correct arithmetic, sorting and indexing.

```sql
-- WRONG: storing money as FLOAT (floating point errors!)
CREATE TABLE bad_invoices (total FLOAT);
INSERT INTO bad_invoices VALUES (10.10);
SELECT total * 3 FROM bad_invoices;  -- may return 30.299999999999997

-- CORRECT: use DECIMAL/NUMERIC for money
CREATE TABLE invoices (total DECIMAL(12,2));
INSERT INTO invoices VALUES (10.10);
SELECT total * 3 FROM invoices;  -- returns 30.30 exactly

-- WRONG: storing dates as strings
CREATE TABLE bad_events (event_date VARCHAR(20));
INSERT INTO bad_events VALUES ('Jan 15, 2024');  -- inconsistent formats

-- CORRECT: use proper date type
CREATE TABLE events (event_date DATE NOT NULL);
-- Supports: date arithmetic, indexing, range queries, localization
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 14. Security

### Role-Based Access Control

Grant privileges to **roles**, then grant roles to users, instead of granting privileges to each user. See [DCL](#dcl--data-control-language) for `GRANT`/`REVOKE` basics.

```sql
-- Create roles (groups of permissions)
CREATE ROLE readonly;
CREATE ROLE readwrite;
CREATE ROLE admin;

-- Grant to roles
GRANT SELECT ON ALL TABLES IN SCHEMA public TO readonly;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO readwrite;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO admin;

-- Create users and assign roles
CREATE USER app_user WITH PASSWORD 'secure_password';
GRANT readwrite TO app_user;

CREATE USER reporting_user WITH PASSWORD 'another_password';
GRANT readonly TO reporting_user;

-- Ensure future tables are covered too
ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT SELECT ON TABLES TO readonly;

-- Revoke dangerous defaults
REVOKE ALL ON DATABASE mydb FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM PUBLIC;
```

---

### Row-Level Security (RLS)

**Row-Level Security** (PostgreSQL, SQL Server, Oracle VPD) filters rows automatically based on the current user or a session setting, so tenants can only see their own data even if a query forgets the `WHERE`.

```sql
-- Multi-tenant SaaS: each tenant sees only their own data
CREATE TABLE customer_data (
    id          SERIAL PRIMARY KEY,
    tenant_id   INT NOT NULL,
    data        TEXT
);

-- Enable RLS on the table
ALTER TABLE customer_data ENABLE ROW LEVEL SECURITY;

-- Policy: users can only see rows matching their tenant_id
-- (app sets app.current_tenant_id for each connection)
CREATE POLICY tenant_isolation ON customer_data
    USING (tenant_id = current_setting('app.current_tenant_id')::INT);

-- In application code, before queries:
SET app.current_tenant_id = 42;

-- Now this query automatically returns only tenant 42's data:
SELECT * FROM customer_data;
-- Equivalent to: SELECT * FROM customer_data WHERE tenant_id = 42

-- By default the table OWNER bypasses RLS. Make the policy apply to the owner too:
ALTER TABLE customer_data FORCE ROW LEVEL SECURITY;

-- Superusers and roles with the BYPASSRLS attribute always bypass RLS:
ALTER ROLE reporting_admin BYPASSRLS;
```

---

### SQL Injection Prevention

**SQL injection** happens when user input is concatenated into SQL text, letting an attacker change the query. Always pass values as **bind parameters** (prepared statements). In Java: `PreparedStatement`, JPA named parameters, Spring `JdbcTemplate` with `?`.

```sql
-- VULNERABLE: string concatenation (never do this!)
-- Python example of vulnerable code:
-- query = "SELECT * FROM users WHERE username = '" + username + "'"
-- If username = "admin' OR '1'='1", query becomes:
-- SELECT * FROM users WHERE username = 'admin' OR '1'='1'
-- Returns ALL users!

-- Even worse (DROP TABLE injection):
-- username = "'; DROP TABLE users; --"
-- SELECT * FROM users WHERE username = ''; DROP TABLE users; --'

-- CORRECT: parameterized queries (prepared statements)
-- Python (psycopg2):
-- cursor.execute("SELECT * FROM users WHERE username = %s", (username,))

-- PostgreSQL native prepared statement:
PREPARE get_user (TEXT) AS
    SELECT * FROM users WHERE username = $1;

EXECUTE get_user('alice');       -- safe
EXECUTE get_user(user_input);    -- safe regardless of input content

-- The parameter value is NEVER interpreted as SQL
```

---

### Encryption

Protect data **at rest** (disk/column encryption), **in transit** (TLS), and store passwords only as slow **one-way hashes** (bcrypt, scrypt, Argon2), never encrypted or plain.

```sql
-- PostgreSQL: encrypt sensitive columns using pgcrypto
CREATE EXTENSION pgcrypto;

-- Store encrypted SSN (in real systems pass the key as a parameter from a secret store,
-- never hard-code it in SQL — it would appear in logs and pg_stat_statements):
INSERT INTO employees (name, ssn_encrypted)
VALUES ('Alice', pgp_sym_encrypt('123-45-6789', 'encryption_key'));

-- Decrypt:
SELECT name, pgp_sym_decrypt(ssn_encrypted, 'encryption_key') AS ssn
FROM employees;

-- Hash passwords (one-way):
INSERT INTO users (username, password_hash)
VALUES ('alice', crypt('mypassword', gen_salt('bf', 10)));
-- bf = bcrypt, 10 = work factor

-- Verify password:
SELECT username FROM users
WHERE username = 'alice'
  AND password_hash = crypt('mypassword', password_hash);
```

```sql
-- Enforce SSL connections (postgresql.conf):
-- ssl = on
-- ssl_cert_file = 'server.crt'
-- ssl_key_file  = 'server.key'

-- pg_hba.conf: require SSL
hostssl   all   all   0.0.0.0/0   scram-sha-256

-- Check if connection is encrypted:
SELECT ssl, version, cipher FROM pg_stat_ssl WHERE pid = pg_backend_pid();
```

---

### Audit Logging

Audit logs record **who** did **what** and **when**, for security investigations and compliance (GDPR, SOX, PCI-DSS).

```sql
-- PostgreSQL: pgaudit extension for detailed audit logging
CREATE EXTENSION pgaudit;

-- postgresql.conf:
pgaudit.log = 'write, ddl'   -- log all writes and DDL
pgaudit.log_relation = on    -- log individual table access

-- This will log:
-- AUDIT: SESSION,1,1,DDL,CREATE TABLE,,,CREATE TABLE sensitive_data...
-- AUDIT: SESSION,2,1,WRITE,INSERT,public,sensitive_data,INSERT INTO...

-- MySQL: General Query Log (for audit)
SET GLOBAL general_log = 'ON';
SET GLOBAL general_log_file = '/var/log/mysql/general.log';
-- Logs ALL queries — very verbose, use only temporarily or route to audit plugin

-- MySQL Enterprise Audit Plugin (production-grade)
INSTALL PLUGIN audit_log SONAME 'audit_log.so';
SET GLOBAL audit_log_policy = 'ALL';
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 15. Advanced SQL Features

### Window Functions

**Window functions** compute values over a set of rows related to the current row (rankings, running totals, previous/next values) **without collapsing rows** like `GROUP BY` does. `PARTITION BY` defines the groups, `ORDER BY` the order inside each group.

```sql
-- ROW_NUMBER: unique sequential number per partition
SELECT
    emp_id, name, dept_id, salary,
    ROW_NUMBER() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS rank_in_dept
FROM employees;
-- Output: each employee gets a rank within their department

-- RANK vs DENSE_RANK:
SELECT
    name, salary,
    RANK()       OVER (ORDER BY salary DESC) AS rank,        -- gaps after ties: 1,2,2,4
    DENSE_RANK() OVER (ORDER BY salary DESC) AS dense_rank   -- no gaps: 1,2,2,3
FROM employees;

-- LAG/LEAD: access previous/next row
SELECT
    order_date,
    daily_revenue,
    LAG(daily_revenue, 1)  OVER (ORDER BY order_date) AS prev_day_revenue,
    LEAD(daily_revenue, 1) OVER (ORDER BY order_date) AS next_day_revenue,
    daily_revenue - LAG(daily_revenue, 1) OVER (ORDER BY order_date) AS day_over_day_change
FROM daily_sales;

-- Running total with SUM OVER:
SELECT
    order_date,
    amount,
    SUM(amount) OVER (ORDER BY order_date ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS running_total,
    AVG(amount) OVER (ORDER BY order_date ROWS BETWEEN 6 PRECEDING AND CURRENT ROW) AS rolling_7day_avg
FROM orders;

-- NTILE: divide rows into N buckets (e.g., quartiles)
SELECT
    name, salary,
    NTILE(4) OVER (ORDER BY salary) AS salary_quartile
FROM employees;
-- quartile 1 = lowest 25%, quartile 4 = highest 25%
```

---

### Recursive CTEs (Hierarchical Data)

A **recursive CTE** repeatedly joins a table to its own previous results, which is how SQL walks **hierarchies** (org charts, categories, bill of materials) of any depth.

```sql
-- Employee hierarchy (org chart):
CREATE TABLE employees_hier (
    emp_id    INT PRIMARY KEY,
    name      VARCHAR(100),
    manager_id INT REFERENCES employees_hier(emp_id)
);

-- Find all reports under manager emp_id=1 (at any depth):
WITH RECURSIVE org_chart AS (
    -- Anchor: start with the root manager
    SELECT emp_id, name, manager_id, 0 AS depth, name::TEXT AS path
    FROM employees_hier
    WHERE emp_id = 1

    UNION ALL

    -- Recursive: join to find direct reports
    SELECT e.emp_id, e.name, e.manager_id, oc.depth + 1, oc.path || ' -> ' || e.name
    FROM employees_hier e
    JOIN org_chart oc ON oc.emp_id = e.manager_id
)
SELECT depth, path, emp_id, name
FROM org_chart
ORDER BY depth, name;

-- Bill of Materials (parts explosion):
WITH RECURSIVE bom AS (
    SELECT component_id, parent_id, quantity, 1 AS level
    FROM parts WHERE parent_id = 100  -- start from product 100

    UNION ALL

    SELECT p.component_id, p.parent_id, p.quantity * b.quantity, b.level + 1
    FROM parts p
    JOIN bom b ON b.component_id = p.parent_id
)
SELECT component_id, level, quantity FROM bom;
```

---

### LATERAL Join

A `LATERAL` subquery can reference columns of tables listed **before** it, so it runs once per outer row. Ideal for "top N per group" queries.

```sql
-- Get the 3 most recent orders for each customer (impossible with regular JOIN):
SELECT c.customer_id, c.name, o.order_id, o.amount, o.order_date
FROM customers c
CROSS JOIN LATERAL (
    SELECT order_id, amount, order_date
    FROM orders
    WHERE customer_id = c.customer_id
    ORDER BY order_date DESC
    LIMIT 3
) o;

-- SQL Server equivalent (CROSS APPLY):
SELECT c.customer_id, c.name, o.order_id, o.amount
FROM customers c
CROSS APPLY (
    SELECT TOP 3 order_id, amount, order_date
    FROM orders
    WHERE customer_id = c.customer_id
    ORDER BY order_date DESC
) o;
```

---

### GROUPING SETS, ROLLUP, CUBE

These extensions of `GROUP BY` compute **several groupings in one query**: subtotals and grand totals (`ROLLUP`), every combination (`CUBE`), or an explicit list (`GROUPING SETS`).

```sql
-- ROLLUP: subtotals and grand total
SELECT region, product, SUM(sales) AS total_sales
FROM sales_data
GROUP BY ROLLUP (region, product);
-- Produces:
-- region='US', product='Pen' → subtotal
-- region='US', product=NULL → US total
-- region=NULL, product=NULL → grand total

-- CUBE: all possible combinations
SELECT region, product, year, SUM(sales)
FROM sales_data
GROUP BY CUBE (region, product, year);
-- Produces all 8 combinations: (r,p,y), (r,p), (r,y), (p,y), (r), (p), (y), ()

-- GROUPING SETS: specific combinations only
SELECT region, product, SUM(sales)
FROM sales_data
GROUP BY GROUPING SETS (
    (region, product),
    (region),
    ()
);
```

---

### MERGE / UPSERT

An **upsert** inserts a row, or updates it if it already exists, in **one atomic statement**. This avoids the race condition of "SELECT then INSERT or UPDATE".

```sql
-- PostgreSQL: INSERT ... ON CONFLICT (upsert)
INSERT INTO product_inventory (product_id, quantity, last_updated)
VALUES (42, 100, NOW())
ON CONFLICT (product_id) DO UPDATE
    SET quantity     = product_inventory.quantity + EXCLUDED.quantity,
        last_updated = EXCLUDED.last_updated;
-- If product_id=42 exists: add to quantity
-- If not: insert new row

-- Insert only if not exists (ignore duplicates):
INSERT INTO event_log (event_id, data)
VALUES (123, 'some data')
ON CONFLICT (event_id) DO NOTHING;

-- MySQL 8.0.19+ (row alias; the older VALUES(quantity) form is deprecated):
INSERT INTO inventory (product_id, quantity)
VALUES (42, 100) AS new
ON DUPLICATE KEY UPDATE
    quantity = inventory.quantity + new.quantity;

-- SQL Server MERGE:
MERGE INTO inventory AS target
USING (SELECT 42 AS product_id, 100 AS qty) AS source
    ON target.product_id = source.product_id
WHEN MATCHED THEN
    UPDATE SET quantity = target.quantity + source.qty
WHEN NOT MATCHED THEN
    INSERT (product_id, quantity) VALUES (source.product_id, source.qty);
```

---

### JSON Support (PostgreSQL JSONB)

`JSONB` stores JSON in a binary, indexable format. Use it for flexible attributes, but keep frequently filtered or related fields as normal columns.

```sql
-- JSONB: binary storage, indexed, operators available
CREATE TABLE user_profiles (
    user_id  INT PRIMARY KEY,
    profile  JSONB
);

INSERT INTO user_profiles VALUES (1, '{
    "name": "Alice",
    "age": 30,
    "tags": ["admin", "power-user"],
    "address": {"city": "NYC", "zip": "10001"}
}');

-- Query JSON fields:
SELECT profile->>'name' AS name             FROM user_profiles;  -- text
SELECT profile->'address'->>'city' AS city  FROM user_profiles;  -- nested
SELECT * FROM user_profiles WHERE profile->>'name' = 'Alice';

-- JSON array operations:
SELECT * FROM user_profiles WHERE profile->'tags' ? 'admin';  -- has key/element

-- GIN index for fast JSON queries:
CREATE INDEX idx_profile_gin ON user_profiles USING GIN (profile);
-- Now JSON path queries use the index

-- JSON aggregation:
SELECT dept_id,
       JSON_AGG(JSON_BUILD_OBJECT('id', emp_id, 'name', name)) AS employees
FROM employees
GROUP BY dept_id;
```

---

### Materialized Views

A **materialized view** stores the **result** of a query physically (unlike a normal view, which re-runs the query each time). Reads are fast; the data is only as fresh as the last `REFRESH`.

```sql
-- Expensive report: recalculate sales summary
CREATE MATERIALIZED VIEW monthly_sales_summary AS
SELECT
    DATE_TRUNC('month', order_date) AS month,
    product_category,
    COUNT(*) AS order_count,
    SUM(amount) AS total_revenue,
    AVG(amount) AS avg_order_value
FROM orders
JOIN products USING (product_id)
GROUP BY 1, 2;

-- Create index on materialized view for fast lookups
CREATE INDEX ON monthly_sales_summary (month, product_category);

-- Refresh the materialized view (manually):
REFRESH MATERIALIZED VIEW monthly_sales_summary;

-- Refresh without blocking reads (requires unique index):
CREATE UNIQUE INDEX ON monthly_sales_summary (month, product_category);
REFRESH MATERIALIZED VIEW CONCURRENTLY monthly_sales_summary;

-- Check when it was last refreshed:
SELECT schemaname, matviewname, last_refresh
FROM pg_matviews
WHERE matviewname = 'monthly_sales_summary';

-- Schedule refresh (via cron or pg_cron):
SELECT cron.schedule('refresh-monthly-sales', '0 * * * *',
    'REFRESH MATERIALIZED VIEW CONCURRENTLY monthly_sales_summary');
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 16. Scaling Patterns

### Read Replicas + Application Routing

Send **writes** to the primary and **read-only** work to replicas to scale reads. In Spring Boot this is done with an `AbstractRoutingDataSource` that picks the target per transaction.

```java
// Spring Boot: AbstractRoutingDataSource routes reads to replica, writes to primary

// DataSourceType.java
public enum DataSourceType { PRIMARY, REPLICA }

// DataSourceContextHolder.java — ThreadLocal holder
public class DataSourceContextHolder {
    private static final ThreadLocal<DataSourceType> CONTEXT =
            ThreadLocal.withInitial(() -> DataSourceType.PRIMARY);

    public static void setDataSourceType(DataSourceType type) { CONTEXT.set(type); }
    public static DataSourceType getDataSourceType()          { return CONTEXT.get(); }
    public static void clear()                                { CONTEXT.remove(); }
}

// DataSourceRoutingConfig.java
@Configuration
public class DataSourceRoutingConfig {

    @Bean("primaryDataSource")
    @ConfigurationProperties("spring.datasource.primary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("replicaDataSource")
    @ConfigurationProperties("spring.datasource.replica")
    public DataSource replicaDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public DataSource routingDataSource(
            @Qualifier("primaryDataSource") DataSource primary,
            @Qualifier("replicaDataSource") DataSource replica) {

        AbstractRoutingDataSource routing = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return DataSourceContextHolder.getDataSourceType();
            }
        };
        routing.setTargetDataSources(Map.of(
                DataSourceType.PRIMARY, primary,
                DataSourceType.REPLICA, replica
        ));
        routing.setDefaultTargetDataSource(primary);
        return routing;
    }
}

// @ReadOnly annotation to mark read-only methods
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReadOnly {}

// RoutingAspect.java — AOP aspect: @ReadOnly → replica, else primary
@Aspect
@Component
@Order(1)
public class RoutingAspect {

    @Around("@annotation(com.example.db.ReadOnly)")   // fully-qualified annotation name
    public Object routeToReplica(ProceedingJoinPoint pjp) throws Throwable {
        DataSourceContextHolder.setDataSourceType(DataSourceType.REPLICA);
        try {
            return pjp.proceed();
        } finally {
            DataSourceContextHolder.clear();  // always restore to primary
        }
    }
}

// UserService.java
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @ReadOnly
    @Transactional(readOnly = true)
    public User getUser(Long userId) {
        // Routed to REPLICA automatically via @ReadOnly AOP
        return userRepository.findById(userId).orElseThrow();
    }

    public User createUser(String name, String email) {
        // Routed to PRIMARY (default)
        return userRepository.save(new User(name, email));
    }
}

// application.yml:
// spring.datasource.primary.url: jdbc:postgresql://primary-db:5432/mydb
// spring.datasource.primary.username: app
// spring.datasource.replica.url:  jdbc:postgresql://replica-db:5432/mydb
// spring.datasource.replica.username: app

// Important: the routing decision must be made BEFORE the transaction borrows a connection.
// Wrap the routing DataSource in LazyConnectionDataSourceProxy, and keep this aspect's @Order
// higher priority (lower number) than the transaction advice.

// Problem: read-your-writes consistency
// After createUser(), calling getUser() on the replica may return nothing
// if replication hasn't caught up yet.
// Solution: route reads to PRIMARY for the same request context after any write.
```

---

### Sharding Strategy

**Sharding** splits data across **several independent databases** by a shard key (e.g. `user_id`). Each shard holds a subset of rows, so writes scale horizontally, at the cost of complex cross-shard queries and transactions.

```java
// Spring Boot: hash-based shard routing across 4 DataSources

// ShardDataSourceConfig.java
@Configuration
public class ShardDataSourceConfig {

    private static final int NUM_SHARDS = 4;

    @Bean
    public Map<Integer, DataSource> shardDataSources() {
        Map<Integer, DataSource> shards = new HashMap<>();
        for (int i = 0; i < NUM_SHARDS; i++) {
            HikariConfig cfg = new HikariConfig();
            cfg.setJdbcUrl("jdbc:postgresql://shard" + i + "-db:5432/mydb");
            cfg.setUsername("app");
            cfg.setMaximumPoolSize(10);
            shards.put(i, new HikariDataSource(cfg));
        }
        return shards;
    }
}

// ShardRouter.java — determines which shard to query
@Component
@RequiredArgsConstructor
public class ShardRouter {

    private final Map<Integer, DataSource> shardDataSources;
    private static final int NUM_SHARDS = 4;

    public int getShardIndex(long userId) {
        return (int) (userId % NUM_SHARDS);  // hash sharding
    }

    public JdbcTemplate jdbcTemplateFor(long userId) {
        int shard = getShardIndex(userId);
        return new JdbcTemplate(shardDataSources.get(shard));
    }

    // Cross-shard query: must fan out to ALL shards and merge results
    public <T> List<T> queryAllShards(String sql, RowMapper<T> mapper, Object... args) {
        return shardDataSources.values()
                .parallelStream()                          // query shards in parallel
                .flatMap(ds -> new JdbcTemplate(ds)
                        .query(sql, mapper, args).stream())
                .collect(Collectors.toList());
    }
}

// UserRepository.java
@Repository
@RequiredArgsConstructor
public class UserShardRepository {

    private final ShardRouter shardRouter;

    public User findById(long userId) {
        // Query only the relevant shard — efficient O(1) routing
        JdbcTemplate jdbc = shardRouter.jdbcTemplateFor(userId);
        return jdbc.queryForObject(
                "SELECT * FROM users WHERE user_id = ?",
                new UserRowMapper(),
                userId);
    }

    public void createUser(User user) {
        // Insert into the correct shard
        JdbcTemplate jdbc = shardRouter.jdbcTemplateFor(user.getUserId());
        jdbc.update("INSERT INTO users (user_id, name, email) VALUES (?, ?, ?)",
                user.getUserId(), user.getName(), user.getEmail());
    }

    // Cross-shard query — expensive! Must hit ALL shards
    public List<User> findByCity(String city) {
        // Fan out to all 4 shards in parallel, merge results
        return shardRouter.queryAllShards(
                "SELECT * FROM users WHERE city = ?",
                new UserRowMapper(),
                city);
        // Cross-shard queries like this are why sharding is a last resort
    }
}
```

---

### CQRS Pattern

**CQRS** (Command Query Responsibility Segregation) uses separate models for **writes** (normalized, consistent) and **reads** (denormalized, query-shaped), synchronized by events.

```sql
-- Write model (normalized, OLTP-optimized):
-- Handles all writes, enforces all constraints
INSERT INTO orders (customer_id, order_date)   VALUES (42, NOW());
INSERT INTO order_items (order_id, product_id, qty) VALUES (999, 5, 2);

-- Read model (denormalized, pre-joined, optimized for specific queries):
-- Populated by event/trigger from write model
CREATE TABLE order_summary_read_model (
    order_id         INT PRIMARY KEY,
    customer_name    VARCHAR(100),
    customer_email   VARCHAR(255),
    order_date       TIMESTAMP,
    item_count       INT,
    total_amount     DECIMAL(10,2),
    product_names    TEXT  -- comma-separated, pre-joined
);

-- Read queries are extremely fast — no joins needed:
SELECT * FROM order_summary_read_model WHERE customer_email = 'alice@x.com';
```

---

### Connection Pooling at Scale

A pooler multiplexes many client connections over a few real database connections.

```
Without pooling:
┌──────────────┐     2000 connections     ┌───────────┐
│ 2000 clients ├─────────────────────────>│  Database │
└──────────────┘  (~10GB RAM just for     │  (OOM!)   │
                    connections)           └───────────┘

With PgBouncer:
┌──────────────┐   2000 client connections  ┌───────────────┐   25 server connections  ┌───────────┐
│ 2000 clients ├──────────────────────────>│  PgBouncer    ├────────────────────────>│  Database │
└──────────────┘                           │  (pool=25)    │   (~250MB RAM)           └───────────┘
                                           └───────────────┘
```

---

### Vertical vs Horizontal Scaling Decision Tree

Scale up and optimize before scaling out: each step down this list adds operational complexity.

```
Is query performance the problem?
  YES → Optimize queries, add indexes, add RAM (vertical)
  NO  → Continue

Are you I/O bound (disk reads/writes)?
  YES → Add more RAM (larger buffer pool), faster SSDs
  NO  → Continue

Are reads the bottleneck?
  YES → Add read replicas
  NO  → Continue

Is the write throughput too high?
  YES → Consider partitioning, then sharding
  NO  → Continue

Is single-node capacity exhausted?
  YES → Shard (last resort — massive complexity)
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 17. CAP Theorem & Distributed Considerations

### CAP Theorem

During a **network partition** (nodes can't communicate), a distributed system must choose:

- **CP (Consistency + Partition Tolerance):** Refuse to answer until consistent (like a bank — no stale reads). Traditional RDBMS.
- **AP (Availability + Partition Tolerance):** Answer with possibly stale data (like a shopping cart — eventual consistency). DynamoDB, Cassandra.
- **CA (Consistency + Availability):** Only possible with no partitions — i.e., a single-node system.

```
Example — Bank Transfer:
  CP system: If partitioned, "I can't process this transfer right now" (503 error)
  AP system: "Transfer accepted!" but maybe double-counted or lost on reconciliation

Example — Social Media "Like" count:
  AP is fine: showing 1,023 likes instead of 1,024 for a few seconds is acceptable
  Eventual consistency is sufficient
```

---

### PACELC — Beyond CAP

Even when there's **no partition**, there's still a tradeoff between **Latency** and **Consistency**:

```
PACELC:
  P (Partition) → A (Availability) or C (Consistency)  [CAP]
  E (Else)      → L (Latency)      or C (Consistency)  [PACELC extension]

PostgreSQL synchronous replication:
  E: L vs C → choose C (wait for replica confirmation before returning → higher latency)

PostgreSQL asynchronous replication:
  E: L vs C → choose L (return immediately → lower latency, risk of data loss)
```

---

### Two-Phase Commit (2PC)

Used for distributed transactions spanning multiple databases:

```
Phase 1 — PREPARE:
  Coordinator → Shard A: "Can you commit transaction X?"
  Coordinator → Shard B: "Can you commit transaction X?"
  Shard A → Coordinator: "YES, prepared"
  Shard B → Coordinator: "YES, prepared"

Phase 2 — COMMIT:
  Coordinator → Shard A: "Commit"
  Coordinator → Shard B: "Commit"

If any shard says NO in Phase 1 → Coordinator sends ROLLBACK to all shards

Problem: If coordinator crashes between Phase 1 and 2:
  - Shards are in "prepared" state, holding locks forever
  - Human intervention or timeout-based resolution needed
```

```sql
-- PostgreSQL 2PC:
BEGIN;
-- ... operations on shard A
PREPARE TRANSACTION 'txn_001';  -- Phase 1: write to WAL but don't commit yet

-- If coordinator confirms all shards prepared:
COMMIT PREPARED 'txn_001';

-- If coordinator says abort:
ROLLBACK PREPARED 'txn_001';

-- See prepared transactions (potentially stuck!):
SELECT * FROM pg_prepared_xacts;
```

---

### Saga Pattern

Alternative to 2PC for microservices/distributed systems:

```
Order Saga (choreography-based):

Step 1: OrderService      → Creates order (local transaction)
Step 2: PaymentService    → Charges customer (local transaction)
Step 3: InventoryService  → Reserves stock (local transaction)
Step 4: ShippingService   → Creates shipment (local transaction)

If Step 3 fails (out of stock):
Compensating transactions run in reverse:
  → PaymentService: REFUND customer (compensating transaction for Step 2)
  → OrderService:   CANCEL order   (compensating transaction for Step 1)

No distributed lock, no coordinator — each service owns its own transaction
```

```sql
-- Saga compensation table (tracks saga state):
CREATE TABLE saga_state (
    saga_id     UUID PRIMARY KEY,
    step_name   VARCHAR(100),
    status      VARCHAR(20),  -- 'pending', 'completed', 'compensating', 'failed'
    payload     JSONB,
    created_at  TIMESTAMP DEFAULT NOW()
);

-- Each step records its completion and compensation action
INSERT INTO saga_state VALUES
    (gen_random_uuid(), 'charge_payment', 'completed',
     '{"amount": 99.99, "compensate": "refund_payment"}', NOW());
```

---

### Eventual Consistency in Practice

```sql
-- Replication lag monitoring (know your system's SLA):
-- PostgreSQL: check lag on replica
SELECT now() - pg_last_xact_replay_timestamp() AS replication_lag;
-- If this returns > 1 second on OLTP, investigate immediately

-- Application pattern for read-your-writes consistency:
-- After a write, route subsequent reads to primary for N seconds
-- Or: use a "read from primary" flag for the same user session

-- Conflict resolution for multi-master (rare but exists):
-- Last-Write-Wins: whichever write has the latest timestamp wins
-- Custom merge: application defines how to merge conflicting versions
-- These add massive complexity — avoid multi-master if possible
```

[⬆ Back to Table of Contents](#table-of-contents)
