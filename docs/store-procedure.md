# PostgreSQL Stored Procedures and Functions — Complete Guide

## Table of Contents
1. [Introduction](#introduction)
2. [Banking System — Table Structure](#banking-system--table-structure)
3. [PostgreSQL Functions](#postgresql-functions)
4. [PostgreSQL Stored Procedures](#postgresql-stored-procedures)
5. [Key Differences](#key-differences)
6. [Exception Handling (Try-Catch)](#exception-handling-try-catch)
7. [Transaction Control — COMMIT & ROLLBACK](#transaction-control--commit--rollback)
8. [Conditional Logic — IF / CASE](#conditional-logic--if--case)
9. [Loops — FOR, WHILE, LOOP](#loops--for-while-loop)
10. [Cursors](#cursors)
11. [Returning Values from Procedures](#returning-values-from-procedures)
12. [Variables and Annotations](#variables-and-annotations)
13. [Complete Real-World Examples](#complete-real-world-examples)
14. [Window Functions in Stored Procedures](#window-functions-in-stored-procedures)
15. [Packaging — Organizing Procedures and Functions](#packaging--organizing-procedures-and-functions)
16. [Default IN Parameters — Rules and Restrictions](#default-in-parameters--rules-and-restrictions)
17. [Best Practices](#best-practices)

---

## Introduction

PostgreSQL supports both **functions** and **stored procedures** to encapsulate reusable SQL logic. While they might seem similar, they serve different purposes and have distinct characteristics.

- **Functions** must return a value and can be used inside SQL expressions (`SELECT`, `WHERE`, `JOIN`).
- **Stored Procedures** (introduced in PostgreSQL 11) do not return values directly, but support **transaction control** (`COMMIT`/`ROLLBACK`) and use `OUT`/`INOUT` parameters to pass data back.

This guide uses a **Banking System** database to demonstrate every feature with practical examples.

---

## Banking System — Table Structure

Below are the `CREATE TABLE` statements for the banking system used throughout this guide.

### 1. branches — Bank Branches

```sql
CREATE TABLE branches (
    id              SERIAL PRIMARY KEY,
    branch_code     VARCHAR(10) UNIQUE NOT NULL,        -- e.g. 'BR-001'
    branch_name     VARCHAR(150) NOT NULL,               -- e.g. 'Downtown Main Branch'
    city            VARCHAR(100) NOT NULL,
    state           VARCHAR(100),
    phone           VARCHAR(20),
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2. customers — Bank Customers

```sql
CREATE TABLE customers (
    id              SERIAL PRIMARY KEY,
    customer_code   VARCHAR(20) UNIQUE NOT NULL,         -- e.g. 'CUST-00001'
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(150) UNIQUE,
    phone           VARCHAR(20),
    date_of_birth   DATE,
    address         TEXT,
    id_type         VARCHAR(30) NOT NULL                 -- 'PASSPORT', 'NATIONAL_ID', 'DRIVING_LICENSE'
                    CHECK (id_type IN ('PASSPORT','NATIONAL_ID','DRIVING_LICENSE')),
    id_number       VARCHAR(50) UNIQUE NOT NULL,
    kyc_verified    BOOLEAN DEFAULT FALSE,
    status          VARCHAR(20) DEFAULT 'ACTIVE'
                    CHECK (status IN ('ACTIVE','INACTIVE','BLOCKED','CLOSED')),
    branch_id       INT NOT NULL REFERENCES branches(id),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 3. accounts — Bank Accounts (Savings, Current, Fixed Deposit)

```sql
CREATE TABLE accounts (
    id              SERIAL PRIMARY KEY,
    account_number  VARCHAR(20) UNIQUE NOT NULL,         -- e.g. 'ACC-1000000001'
    customer_id     INT NOT NULL REFERENCES customers(id),
    branch_id       INT NOT NULL REFERENCES branches(id),
    account_type    VARCHAR(20) NOT NULL                 -- 'SAVINGS', 'CURRENT', 'FIXED_DEPOSIT'
                    CHECK (account_type IN ('SAVINGS','CURRENT','FIXED_DEPOSIT')),
    balance         DECIMAL(15,2) DEFAULT 0.00,
    currency        VARCHAR(3) DEFAULT 'USD',
    interest_rate   DECIMAL(5,2) DEFAULT 0.00,           -- annual interest rate %
    min_balance     DECIMAL(15,2) DEFAULT 500.00,
    overdraft_limit DECIMAL(15,2) DEFAULT 0.00,          -- only for CURRENT accounts
    status          VARCHAR(20) DEFAULT 'ACTIVE'
                    CHECK (status IN ('ACTIVE','DORMANT','FROZEN','CLOSED')),
    opened_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    closed_at       TIMESTAMP
);
```

### 4. transactions — All Account Transactions (Deposits, Withdrawals, Transfers)

```sql
CREATE TABLE transactions (
    id              SERIAL PRIMARY KEY,
    transaction_ref VARCHAR(30) UNIQUE NOT NULL,          -- e.g. 'TXN-20250129-00001'
    account_id      INT NOT NULL REFERENCES accounts(id),
    transaction_type VARCHAR(20) NOT NULL
                    CHECK (transaction_type IN ('DEPOSIT','WITHDRAWAL','TRANSFER_IN','TRANSFER_OUT','INTEREST','FEE','REVERSAL')),
    amount          DECIMAL(15,2) NOT NULL CHECK (amount > 0),
    balance_before  DECIMAL(15,2) NOT NULL,
    balance_after   DECIMAL(15,2) NOT NULL,
    description     TEXT,
    related_txn_id  INT REFERENCES transactions(id),  -- links transfer pairs
    performed_by    INT,                                  -- teller/employee id
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 5. transfers — Fund Transfer Records

```sql
CREATE TABLE transfers (
    id                  SERIAL PRIMARY KEY,
    transfer_ref        VARCHAR(30) UNIQUE NOT NULL,      -- e.g. 'TRF-20250129-00001'
    from_account_id     INT NOT NULL REFERENCES accounts(id),
    to_account_id       INT NOT NULL REFERENCES accounts(id),
    amount              DECIMAL(15,2) NOT NULL CHECK (amount > 0),
    fee                 DECIMAL(15,2) DEFAULT 0.00,
    status              VARCHAR(20) DEFAULT 'COMPLETED'
                        CHECK (status IN ('PENDING','COMPLETED','FAILED','REVERSED')),
    description         TEXT,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_diff_accounts CHECK (from_account_id != to_account_id)
);
```

### 6. loans — Loan Accounts

```sql
CREATE TABLE loans (
    id                  SERIAL PRIMARY KEY,
    loan_number         VARCHAR(20) UNIQUE NOT NULL,      -- e.g. 'LN-00001'
    customer_id         INT NOT NULL REFERENCES customers(id),
    account_id          INT NOT NULL REFERENCES accounts(id),  -- linked disbursement account
    loan_type           VARCHAR(30) NOT NULL
                        CHECK (loan_type IN ('PERSONAL','HOME','AUTO','BUSINESS','EDUCATION')),
    principal_amount    DECIMAL(15,2) NOT NULL,
    interest_rate       DECIMAL(5,2) NOT NULL,            -- annual %
    tenure_months       INT NOT NULL,
    emi_amount          DECIMAL(15,2) NOT NULL,
    total_paid          DECIMAL(15,2) DEFAULT 0.00,
    outstanding_balance DECIMAL(15,2) NOT NULL,
    status              VARCHAR(20) DEFAULT 'ACTIVE'
                        CHECK (status IN ('PENDING','APPROVED','ACTIVE','CLOSED','DEFAULTED')),
    disbursed_at        TIMESTAMP,
    next_emi_date       DATE,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 7. loan_payments — EMI / Loan Repayments

```sql
CREATE TABLE loan_payments (
    id              SERIAL PRIMARY KEY,
    loan_id         INT NOT NULL REFERENCES loans(id),
    payment_ref     VARCHAR(30) UNIQUE NOT NULL,
    payment_date    DATE NOT NULL,
    principal_part  DECIMAL(15,2) NOT NULL,
    interest_part   DECIMAL(15,2) NOT NULL,
    total_amount    DECIMAL(15,2) NOT NULL,
    penalty         DECIMAL(15,2) DEFAULT 0.00,
    status          VARCHAR(20) DEFAULT 'PAID'
                    CHECK (status IN ('PAID','FAILED','REVERSED')),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 8. beneficiaries — Saved Transfer Recipients

```sql
CREATE TABLE beneficiaries (
    id              SERIAL PRIMARY KEY,
    customer_id     INT NOT NULL REFERENCES customers(id),
    beneficiary_name VARCHAR(150) NOT NULL,
    account_number  VARCHAR(20) NOT NULL,
    bank_name       VARCHAR(150) DEFAULT 'SAME_BANK',
    ifsc_code       VARCHAR(20),
    is_verified     BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(customer_id, account_number)
);
```

### 9. audit_log — Audit Trail for All Operations

```sql
CREATE TABLE audit_log (
    id              SERIAL PRIMARY KEY,
    table_name      VARCHAR(100) NOT NULL,
    operation       VARCHAR(20) NOT NULL,                 -- 'INSERT', 'UPDATE', 'DELETE', 'TRANSFER', etc.
    record_id       INT,
    old_values      JSONB,
    new_values      JSONB,
    performed_by    INT,
    ip_address      VARCHAR(45),
    performed_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Entity Relationship Summary

```
branches ──< customers ──< accounts ──< transactions
                │               │
                │               ├──< transfers (from_account_id)
                │               ├──< transfers (to_account_id)
                │               │
                ├──< loans ─────┘
                │       │
                │       └──< loan_payments
                │
                └──< beneficiaries

accounts ──< transactions (related_txn_id self-ref)
```

---

## Sample Data — INSERT Statements for All Tables

Insert the data in the order shown below (parent tables first to satisfy foreign key constraints).

### 1. Insert Branches

```sql
INSERT INTO branches (branch_code, branch_name, city, state, phone) VALUES
('BR-001', 'Downtown Main Branch',   'New York',    'NY', '+1-212-555-0101'),
('BR-002', 'Westside Branch',        'Los Angeles', 'CA', '+1-310-555-0102'),
('BR-003', 'Lakefront Branch',       'Chicago',     'IL', '+1-312-555-0103'),
('BR-004', 'Midtown Branch',         'Houston',     'TX', '+1-713-555-0104'),
('BR-005', 'Central Avenue Branch',  'Phoenix',     'AZ', '+1-602-555-0105');
```

### 2. Insert Customers

```sql
INSERT INTO customers (customer_code, first_name, last_name, email, phone, date_of_birth, address, id_type, id_number, kyc_verified, status, branch_id) VALUES
('CUST-00001', 'John',    'Doe',      'john.doe@email.com',      '+1-212-555-1001', '1990-05-15', '123 Main St, New York, NY',       'PASSPORT',        'P12345678', TRUE,  'ACTIVE', 1),
('CUST-00002', 'Jane',    'Smith',    'jane.smith@email.com',    '+1-310-555-1002', '1985-08-22', '456 Oak Ave, Los Angeles, CA',     'NATIONAL_ID',     'N98765432', TRUE,  'ACTIVE', 2),
('CUST-00003', 'Robert',  'Johnson',  'robert.j@email.com',      '+1-312-555-1003', '1978-12-01', '789 Lake Dr, Chicago, IL',         'DRIVING_LICENSE',  'D55667788', TRUE,  'ACTIVE', 3),
('CUST-00004', 'Emily',   'Williams', 'emily.w@email.com',       '+1-713-555-1004', '1995-03-10', '321 Elm St, Houston, TX',          'PASSPORT',        'P87654321', FALSE, 'ACTIVE', 4),
('CUST-00005', 'Michael', 'Brown',    'michael.b@email.com',     '+1-602-555-1005', '1982-07-28', '654 Pine Rd, Phoenix, AZ',         'NATIONAL_ID',     'N11223344', TRUE,  'ACTIVE', 5),
('CUST-00006', 'Sarah',   'Davis',    'sarah.d@email.com',       '+1-212-555-1006', '1992-11-05', '987 Broadway, New York, NY',       'DRIVING_LICENSE',  'D99887766', TRUE,  'ACTIVE', 1),
('CUST-00007', 'David',   'Wilson',   'david.w@email.com',       '+1-310-555-1007', '1988-01-19', '147 Sunset Blvd, Los Angeles, CA', 'PASSPORT',        'P44556677', TRUE,  'INACTIVE', 2),
('CUST-00008', 'Lisa',    'Taylor',   'lisa.t@email.com',        '+1-312-555-1008', '2000-09-30', '258 Michigan Ave, Chicago, IL',    'NATIONAL_ID',     'N33445566', TRUE,  'ACTIVE', 3),
('CUST-00009', 'James',   'Anderson', 'james.a@email.com',       '+1-713-555-1009', '1975-06-14', '369 Texas Ave, Houston, TX',       'DRIVING_LICENSE',  'D22334455', TRUE,  'BLOCKED', 4),
('CUST-00010', 'Karen',   'Thomas',   'karen.t@email.com',       '+1-602-555-1010', '1998-04-25', '741 Desert Rd, Phoenix, AZ',       'PASSPORT',        'P66778899', TRUE,  'ACTIVE', 5);
```

### 3. Insert Accounts

```sql
INSERT INTO accounts (account_number, customer_id, branch_id, account_type, balance, currency, interest_rate, min_balance, overdraft_limit, status) VALUES
-- John Doe — 2 accounts
('ACC-0000000001', 1, 1, 'SAVINGS',       52400.00, 'USD', 3.50, 500.00,     0.00, 'ACTIVE'),
('ACC-0000000002', 1, 1, 'CURRENT',       18500.00, 'USD', 0.00, 5000.00, 10000.00, 'ACTIVE'),
-- Jane Smith
('ACC-0000000003', 2, 2, 'SAVINGS',       34200.00, 'USD', 3.50, 500.00,     0.00, 'ACTIVE'),
('ACC-0000000004', 2, 2, 'FIXED_DEPOSIT', 100000.00,'USD', 6.50, 10000.00,   0.00, 'ACTIVE'),
-- Robert Johnson
('ACC-0000000005', 3, 3, 'SAVINGS',       12750.00, 'USD', 3.50, 500.00,     0.00, 'ACTIVE'),
('ACC-0000000006', 3, 3, 'CURRENT',       45000.00, 'USD', 0.00, 5000.00, 25000.00, 'ACTIVE'),
-- Emily Williams
('ACC-0000000007', 4, 4, 'SAVINGS',        8200.00, 'USD', 3.50, 500.00,     0.00, 'ACTIVE'),
-- Michael Brown
('ACC-0000000008', 5, 5, 'SAVINGS',       67800.00, 'USD', 4.00, 500.00,     0.00, 'ACTIVE'),
('ACC-0000000009', 5, 5, 'CURRENT',       22000.00, 'USD', 0.00, 5000.00, 15000.00, 'ACTIVE'),
-- Sarah Davis
('ACC-0000000010', 6, 1, 'SAVINGS',       15400.00, 'USD', 3.50, 500.00,     0.00, 'ACTIVE'),
-- David Wilson (INACTIVE customer)
('ACC-0000000011', 7, 2, 'SAVINGS',        3200.00, 'USD', 3.50, 500.00,     0.00, 'DORMANT'),
-- Lisa Taylor
('ACC-0000000012', 8, 3, 'SAVINGS',       28900.00, 'USD', 3.50, 500.00,     0.00, 'ACTIVE'),
-- James Anderson (BLOCKED customer)
('ACC-0000000013', 9, 4, 'CURRENT',       41000.00, 'USD', 0.00, 5000.00, 20000.00, 'FROZEN'),
-- Karen Thomas
('ACC-0000000014', 10, 5, 'SAVINGS',       9600.00, 'USD', 3.50, 500.00,     0.00, 'ACTIVE'),
('ACC-0000000015', 10, 5, 'FIXED_DEPOSIT', 50000.00,'USD', 6.00, 10000.00,   0.00, 'ACTIVE');
```

### 4. Insert Transactions

```sql
INSERT INTO transactions (transaction_ref, account_id, transaction_type, amount, balance_before, balance_after, description, performed_by) VALUES
-- John Doe savings (ACC-0000000001)
('TXN-20250101-00001', 1, 'DEPOSIT',      50000.00, 0.00,     50000.00, 'Initial deposit',            1),
('TXN-20250105-00002', 1, 'DEPOSIT',      10000.00, 50000.00, 60000.00, 'Salary credit - Jan 2025',   NULL),
('TXN-20250110-00003', 1, 'WITHDRAWAL',    5000.00, 60000.00, 55000.00, 'ATM withdrawal',             NULL),
('TXN-20250115-00004', 1, 'TRANSFER_OUT',  3000.00, 55000.00, 52000.00, 'Rent payment to ACC-03',     2),
('TXN-20250120-00005', 1, 'INTEREST',       400.00, 52000.00, 52400.00, 'Monthly interest - Jan',     NULL),

-- John Doe current (ACC-0000000002)
('TXN-20250101-00006', 2, 'DEPOSIT',      25000.00, 0.00,     25000.00, 'Initial deposit',            1),
('TXN-20250112-00007', 2, 'WITHDRAWAL',    6500.00, 25000.00, 18500.00, 'Business expense',           NULL),

-- Jane Smith savings (ACC-0000000003)
('TXN-20250101-00008', 3, 'DEPOSIT',      30000.00, 0.00,     30000.00, 'Initial deposit',            1),
('TXN-20250115-00009', 3, 'TRANSFER_IN',   3000.00, 30000.00, 33000.00, 'Rent received from ACC-01',  2),
('TXN-20250118-00010', 3, 'DEPOSIT',       1200.00, 33000.00, 34200.00, 'Freelance payment',          NULL),

-- Robert Johnson savings (ACC-0000000005)
('TXN-20250101-00011', 5, 'DEPOSIT',      15000.00, 0.00,     15000.00, 'Initial deposit',            3),
('TXN-20250108-00012', 5, 'WITHDRAWAL',    2500.00, 15000.00, 12500.00, 'Bill payment',               NULL),
('TXN-20250122-00013', 5, 'INTEREST',       250.00, 12500.00, 12750.00, 'Monthly interest - Jan',     NULL),

-- Michael Brown savings (ACC-0000000008)
('TXN-20250101-00014', 8, 'DEPOSIT',      60000.00, 0.00,     60000.00, 'Initial deposit',            5),
('TXN-20250110-00015', 8, 'DEPOSIT',       5000.00, 60000.00, 65000.00, 'Bonus credit',               NULL),
('TXN-20250115-00016', 8, 'TRANSFER_OUT',  2000.00, 65000.00, 63000.00, 'Transfer to friend ACC-14',  NULL),
('TXN-20250120-00017', 8, 'DEPOSIT',       5000.00, 63000.00, 68000.00, 'Dividend income',            NULL),
('TXN-20250125-00018', 8, 'FEE',            200.00, 68000.00, 67800.00, 'Annual maintenance fee',     NULL),

-- Karen Thomas savings (ACC-0000000014)
('TXN-20250101-00019', 14, 'DEPOSIT',      7500.00, 0.00,      7500.00, 'Initial deposit',            5),
('TXN-20250115-00020', 14, 'TRANSFER_IN',  2000.00, 7500.00,   9500.00, 'Transfer from ACC-08',       NULL),
('TXN-20250128-00021', 14, 'INTEREST',      100.00, 9500.00,   9600.00, 'Monthly interest - Jan',     NULL);
```

### 5. Insert Transfers

```sql
INSERT INTO transfers (transfer_ref, from_account_id, to_account_id, amount, fee, status, description) VALUES
('TRF-20250115-00001', 1,  3,  3000.00, 0.00, 'COMPLETED', 'Rent payment - Jan 2025'),
('TRF-20250115-00002', 8, 14,  2000.00, 5.00, 'COMPLETED', 'Transfer to friend Karen'),
('TRF-20250120-00003', 5,  1,  1000.00, 5.00, 'COMPLETED', 'Repayment to John'),
('TRF-20250122-00004', 3, 10,   500.00, 0.00, 'COMPLETED', 'Gift to Sarah'),
('TRF-20250125-00005', 6,  2,  2000.00, 5.00, 'FAILED',    'Insufficient funds transfer attempt');
```

### 6. Insert Loans

```sql
INSERT INTO loans (loan_number, customer_id, account_id, loan_type, principal_amount, interest_rate, tenure_months, emi_amount, total_paid, outstanding_balance, status, disbursed_at, next_emi_date) VALUES
('LN-00001', 1, 1, 'HOME',      500000.00, 8.50, 240, 4341.76,  52101.12, 447898.88, 'ACTIVE',   '2024-01-15', '2025-02-15'),
('LN-00002', 2, 3, 'PERSONAL',   50000.00, 12.00,  36, 1661.22,  11628.54,  38371.46, 'ACTIVE',   '2024-06-01', '2025-02-01'),
('LN-00003', 3, 5, 'AUTO',      200000.00, 9.00,   60, 4150.67,  24904.02, 175095.98, 'ACTIVE',   '2024-08-10', '2025-02-10'),
('LN-00004', 5, 8, 'BUSINESS',  300000.00, 10.50,  48, 7672.84,      0.00, 300000.00, 'APPROVED', NULL,          NULL),
('LN-00005', 6, 10,'EDUCATION',  80000.00, 7.50,   48, 1936.45,   5809.35,  74190.65, 'ACTIVE',   '2024-10-01', '2025-02-01'),
('LN-00006', 8, 12,'PERSONAL',   25000.00, 11.00,  24, 1169.59,   3508.77,  21491.23, 'ACTIVE',   '2024-11-01', '2025-02-01'),
('LN-00007', 10,14,'HOME',      150000.00, 8.00,  180, 1433.48,      0.00, 150000.00, 'PENDING',  NULL,          NULL);
```

### 7. Insert Loan Payments

```sql
INSERT INTO loan_payments (loan_id, payment_ref, payment_date, principal_part, interest_part, total_amount, penalty, status) VALUES
-- Home loan (LN-00001) — 12 months of payments
(1, 'PAY-LN01-202402', '2024-02-15', 802.26,  3539.50, 4341.76, 0.00, 'PAID'),
(1, 'PAY-LN01-202403', '2024-03-15', 807.94,  3533.82, 4341.76, 0.00, 'PAID'),
(1, 'PAY-LN01-202404', '2024-04-15', 813.67,  3528.09, 4341.76, 0.00, 'PAID'),
(1, 'PAY-LN01-202405', '2024-05-15', 819.43,  3522.33, 4341.76, 0.00, 'PAID'),
(1, 'PAY-LN01-202406', '2024-06-15', 825.24,  3516.52, 4341.76, 0.00, 'PAID'),
(1, 'PAY-LN01-202407', '2024-07-15', 831.08,  3510.68, 4341.76, 0.00, 'PAID'),
(1, 'PAY-LN01-202408', '2024-08-15', 836.97,  3504.79, 4341.76, 0.00, 'PAID'),
(1, 'PAY-LN01-202409', '2024-09-15', 842.90,  3498.86, 4341.76, 0.00, 'PAID'),
(1, 'PAY-LN01-202410', '2024-10-15', 848.87,  3492.89, 4341.76, 0.00, 'PAID'),
(1, 'PAY-LN01-202411', '2024-11-15', 854.88,  3486.88, 4341.76, 0.00, 'PAID'),
(1, 'PAY-LN01-202412', '2024-12-15', 860.93,  3480.83, 4341.76, 0.00, 'PAID'),
(1, 'PAY-LN01-202501', '2025-01-15', 866.95,  3474.81, 4341.76, 0.00, 'PAID'),

-- Personal loan (LN-00002) — 7 months
(2, 'PAY-LN02-202407', '2024-07-01', 1161.22, 500.00, 1661.22, 0.00, 'PAID'),
(2, 'PAY-LN02-202408', '2024-08-01', 1172.83, 488.39, 1661.22, 0.00, 'PAID'),
(2, 'PAY-LN02-202409', '2024-09-01', 1184.56, 476.66, 1661.22, 0.00, 'PAID'),
(2, 'PAY-LN02-202410', '2024-10-01', 1196.41, 464.81, 1661.22, 0.00, 'PAID'),
(2, 'PAY-LN02-202411', '2024-11-01', 1208.37, 452.85, 1661.22, 0.00, 'PAID'),
(2, 'PAY-LN02-202412', '2024-12-01', 1220.46, 440.76, 1661.22, 0.00, 'PAID'),
(2, 'PAY-LN02-202501', '2025-01-01', 1232.67, 428.55, 1661.22, 0.00, 'PAID'),

-- Auto loan (LN-00003) — 6 months
(3, 'PAY-LN03-202409', '2024-09-10', 2650.67, 1500.00, 4150.67, 0.00, 'PAID'),
(3, 'PAY-LN03-202410', '2024-10-10', 2670.55, 1480.12, 4150.67, 0.00, 'PAID'),
(3, 'PAY-LN03-202411', '2024-11-10', 2690.58, 1460.09, 4150.67, 0.00, 'PAID'),
(3, 'PAY-LN03-202412', '2024-12-10', 2710.76, 1439.91, 4150.67, 0.00, 'PAID'),
(3, 'PAY-LN03-202501', '2025-01-10', 2731.09, 1419.58, 4150.67, 0.00, 'PAID'),
(3, 'PAY-LN03-202502', '2025-01-25', 2751.46, 1399.21, 4150.67, 75.00,'PAID'),  -- late payment with penalty

-- Education loan (LN-00005) — 3 months
(4, 'PAY-LN05-202411', '2024-11-01', 1436.45, 500.00, 1936.45, 0.00, 'PAID'),
(4, 'PAY-LN05-202412', '2024-12-01', 1445.42, 491.03, 1936.45, 0.00, 'PAID'),
(4, 'PAY-LN05-202501', '2025-01-01', 1454.43, 482.02, 1936.45, 0.00, 'PAID'),

-- Personal loan (LN-00006) — 3 months
(5, 'PAY-LN06-202412', '2024-12-01',  940.51, 229.08, 1169.59, 0.00, 'PAID'),
(5, 'PAY-LN06-202501', '2025-01-01',  949.13, 220.46, 1169.59, 0.00, 'PAID'),
(5, 'PAY-LN06-202502', '2025-01-28',  957.82, 211.77, 1169.59, 0.00, 'PAID');
```

### 8. Insert Beneficiaries

```sql
INSERT INTO beneficiaries (customer_id, beneficiary_name, account_number, bank_name, ifsc_code, is_verified) VALUES
(1, 'Jane Smith',         'ACC-0000000003', 'SAME_BANK',        NULL,           TRUE),
(1, 'Robert Johnson',     'ACC-0000000005', 'SAME_BANK',        NULL,           TRUE),
(1, 'Alice Cooper',       '9876543210',     'Chase Bank',       'CHAS0001234',  TRUE),
(2, 'John Doe',           'ACC-0000000001', 'SAME_BANK',        NULL,           TRUE),
(2, 'External Vendor',    '1122334455',     'Bank of America',  'BOFA0005678',  FALSE),
(5, 'Karen Thomas',       'ACC-0000000014', 'SAME_BANK',        NULL,           TRUE),
(5, 'Supplier Corp',      '5566778899',     'Wells Fargo',      'WELF0009012',  TRUE),
(6, 'John Doe',           'ACC-0000000001', 'SAME_BANK',        NULL,           TRUE),
(8, 'Sarah Davis',        'ACC-0000000010', 'SAME_BANK',        NULL,           TRUE),
(10,'Michael Brown',      'ACC-0000000008', 'SAME_BANK',        NULL,           TRUE);
```

### 9. Insert Audit Log

```sql
INSERT INTO audit_log (table_name, operation, record_id, old_values, new_values, performed_by, ip_address) VALUES
('customers', 'INSERT', 1,  NULL, '{"customer_code":"CUST-00001","name":"John Doe"}',                                         1, '192.168.1.10'),
('accounts',  'INSERT', 1,  NULL, '{"account_number":"ACC-0000000001","type":"SAVINGS","initial_deposit":50000}',              1, '192.168.1.10'),
('accounts',  'INSERT', 2,  NULL, '{"account_number":"ACC-0000000002","type":"CURRENT","initial_deposit":25000}',              1, '192.168.1.10'),
('transfers', 'TRANSFER', 1, NULL,'{"from":"ACC-0000000001","to":"ACC-0000000003","amount":3000,"fee":0}',                     2, '192.168.1.15'),
('accounts',  'UPDATE', 11, '{"status":"ACTIVE"}', '{"status":"DORMANT","reason":"No activity for 365 days"}',                NULL, '10.0.0.1'),
('accounts',  'UPDATE', 13, '{"status":"ACTIVE"}', '{"status":"FROZEN","reason":"Suspicious activity detected"}',             10, '10.0.0.1'),
('loans',     'INSERT', 1,  NULL, '{"loan_number":"LN-00001","type":"HOME","principal":500000}',                               3, '192.168.1.20'),
('loans',     'DISBURSE', 1, '{"status":"APPROVED"}', '{"status":"ACTIVE","disbursed_amount":500000}',                         3, '192.168.1.20'),
('transfers', 'TRANSFER', 2, NULL,'{"from":"ACC-0000000008","to":"ACC-0000000014","amount":2000,"fee":5}',                     NULL, '192.168.1.50'),
('customers', 'UPDATE', 9,  '{"status":"ACTIVE"}', '{"status":"BLOCKED","reason":"Failed KYC re-verification"}',               10, '10.0.0.1');
```

---

## PostgreSQL Functions

### What is a Function?

A function is a reusable block of SQL/PL/pgSQL code that **must return a value**. Functions can be used inside `SELECT`, `WHERE`, `JOIN`, and other SQL expressions.

### Syntax

```sql
CREATE OR REPLACE FUNCTION function_name(param1 type, param2 type, ...)
RETURNS return_type
LANGUAGE plpgsql
AS $$
DECLARE
    -- variable declarations
BEGIN
    -- function body
    RETURN value;
END;
$$;
```

### Key Characteristics

- **Must return a value** (single value, table, or void)
- Can be used in SQL expressions and SELECT statements
- Cannot execute transaction control commands (COMMIT, ROLLBACK)
- Can have IN parameters only (default behavior)
- Runs within a single transaction
- Can be called from SELECT, WHERE, JOIN, etc.

### Types of Functions

#### 1. Scalar Function — Get Account Balance

Returns a single value.

```sql
CREATE OR REPLACE FUNCTION get_account_balance(p_account_id INT)
RETURNS DECIMAL
LANGUAGE plpgsql
AS $$
DECLARE
    v_balance DECIMAL;
BEGIN
    SELECT balance INTO v_balance
    FROM accounts
    WHERE id = p_account_id;

    IF NOT FOUND THEN
        RETURN 0.00;
    END IF;

    RETURN v_balance;
END;
$$;

-- Usage
SELECT get_account_balance(1);                             -- Returns e.g. 52400.00
SELECT account_number, get_account_balance(id) AS balance
FROM accounts
WHERE customer_id = 10;
```

#### 2. Table-Valued Function — Customer Account Summary

Returns a result set (table).

```sql
CREATE OR REPLACE FUNCTION get_customer_accounts(p_customer_id INT)
RETURNS TABLE(
    account_number  VARCHAR,
    account_type    VARCHAR,
    balance         DECIMAL,
    currency        VARCHAR,
    status          VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT
        a.account_number,
        a.account_type,
        a.balance,
        a.currency,
        a.status
    FROM accounts a
    WHERE a.customer_id = p_customer_id
      AND a.status != 'CLOSED'
    ORDER BY a.account_type;
END;
$$;

-- Usage
SELECT * FROM get_customer_accounts(1);
```

#### 3. Function with Calculations — Loan EMI Calculator

```sql
CREATE OR REPLACE FUNCTION calculate_emi(
    p_principal     DECIMAL,
    p_annual_rate   DECIMAL,
    p_tenure_months INT
)
RETURNS DECIMAL
LANGUAGE plpgsql
IMMUTABLE
AS $$
DECLARE
    v_monthly_rate DECIMAL;
    v_emi          DECIMAL;
BEGIN
    IF p_annual_rate = 0 THEN
        RETURN ROUND(p_principal / p_tenure_months, 2);
    END IF;

    v_monthly_rate := p_annual_rate / 12 / 100;

    -- EMI = P * r * (1+r)^n / ((1+r)^n - 1)
    v_emi := p_principal * v_monthly_rate * POWER(1 + v_monthly_rate, p_tenure_months)
             / (POWER(1 + v_monthly_rate, p_tenure_months) - 1);

    RETURN ROUND(v_emi, 2);
END;
$$;

-- Usage
SELECT calculate_emi(500000, 8.5, 60);                    -- Returns 10235.83
SELECT loan_number, principal_amount, calculate_emi(principal_amount, interest_rate, tenure_months) AS emi
FROM loans
WHERE status = 'ACTIVE';
```

---

## PostgreSQL Stored Procedures

### What is a Stored Procedure?

A stored procedure is a reusable block of SQL code (introduced in PostgreSQL 11). Unlike functions, procedures **do not return values directly** but can use `OUT`/`INOUT` parameters. They support **transaction control** (`COMMIT`/`ROLLBACK`).

### Syntax

```sql
CREATE OR REPLACE PROCEDURE procedure_name(param1 type, param2 type, ...)
LANGUAGE plpgsql
AS $$
DECLARE
    -- variable declarations
BEGIN
    -- procedure body
    -- Can include COMMIT and ROLLBACK
END;
$$;
```

### Key Characteristics

- **Does not return a value** (uses OUT parameters to return data)
- Cannot be used in SQL expressions
- **Supports transaction control** (COMMIT, ROLLBACK)
- Can have IN, OUT, and INOUT parameters
- Called using `CALL` statement
- Ideal for complex operations requiring transaction management

### Basic Procedure — Deposit Money

```sql
CREATE OR REPLACE PROCEDURE deposit_money(
    IN  p_account_id  INT,
    IN  p_amount      DECIMAL,
    IN  p_description TEXT DEFAULT 'Cash Deposit',
    IN  p_performed_by INT DEFAULT NULL,
    OUT p_txn_ref     VARCHAR,
    OUT p_new_balance DECIMAL,
    OUT p_message     TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_old_balance   DECIMAL;
    v_acc_status    VARCHAR;
BEGIN
    -- Validate account
    SELECT balance, status
    INTO v_old_balance, v_acc_status
    FROM accounts
    WHERE id = p_account_id;

    IF NOT FOUND THEN
        p_message := 'Account not found';
        RETURN;
    END IF;

    IF v_acc_status != 'ACTIVE' THEN
        p_message := 'Account is ' || v_acc_status || '. Deposits not allowed';
        RETURN;
    END IF;

    IF p_amount <= 0 THEN
        p_message := 'Deposit amount must be greater than zero';
        RETURN;
    END IF;

    -- Generate transaction reference
    p_txn_ref := 'TXN-' || TO_CHAR(NOW(), 'YYYYMMDD') || '-' || LPAD(nextval('transactions_id_seq')::TEXT, 5, '0');

    -- Update balance
    p_new_balance := v_old_balance + p_amount;

    UPDATE accounts SET balance = p_new_balance WHERE id = p_account_id;

    -- Record transaction
    INSERT INTO transactions (transaction_ref, account_id, transaction_type, amount, balance_before, balance_after, description, performed_by)
    VALUES (p_txn_ref, p_account_id, 'DEPOSIT', p_amount, v_old_balance, p_new_balance, p_description, p_performed_by);

    COMMIT;
    p_message := 'Deposit successful. New balance: ' || p_new_balance;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_txn_ref := NULL;
        p_new_balance := NULL;
        p_message := 'Deposit failed: ' || SQLERRM;
END;
$$;

-- Call and capture result
CALL deposit_money(1, 10000.00, 'Salary credit', 5, NULL, NULL, NULL);
-- Returns:
-- p_txn_ref          | p_new_balance | p_message
-- -------------------+---------------+-----------------------------------------
-- TXN-20250129-00012 | 62400.00      | Deposit successful. New balance: 62400.00
```

---

## Key Differences

| Feature | Function | Stored Procedure |
|---------|----------|------------------|
| **Return Value** | Must return a value | Uses OUT parameters |
| **Usage in SQL** | Can be used in SELECT, WHERE, JOIN | Cannot be used in SQL expressions |
| **Transaction Control** | Cannot use COMMIT/ROLLBACK | Can use COMMIT/ROLLBACK |
| **Call Method** | `SELECT function_name()` | `CALL procedure_name()` |
| **Parameters** | IN parameters (default) | IN, OUT, INOUT parameters |
| **Purpose** | Calculations, data retrieval | Complex operations, transaction management |
| **Introduced** | Early PostgreSQL versions | PostgreSQL 11 |

---

## Exception Handling (Try-Catch)

PostgreSQL uses `BEGIN ... EXCEPTION ... END` blocks as the equivalent of try-catch. The `BEGIN` block is the "try" and `EXCEPTION` is the "catch".

### Syntax

```sql
BEGIN
    -- "try" block: normal operations
EXCEPTION
    WHEN <condition_name> THEN
        -- "catch" block: handle specific error
    WHEN OTHERS THEN
        -- catch-all handler
END;
```

### Common Exception Condition Names

| Condition Name | SQLSTATE | Description |
|----------------|----------|-------------|
| `unique_violation` | 23505 | Duplicate key |
| `foreign_key_violation` | 23503 | FK constraint failed |
| `check_violation` | 23514 | CHECK constraint failed |
| `not_null_violation` | 23502 | NULL in NOT NULL column |
| `raise_exception` | P0001 | User-raised EXCEPTION |
| `no_data_found` | P0002 | Query returned no rows |
| `too_many_rows` | P0003 | Query returned multiple rows (expected one) |
| `OTHERS` | — | Catches everything not matched above |

### Built-in Error Variables

| Variable | Description |
|----------|-------------|
| `SQLSTATE` | 5-character error code (e.g. `'23505'`) |
| `SQLERRM` | Human-readable error message |

### Example — Safe Customer Registration with Exception Handling

```sql
CREATE OR REPLACE PROCEDURE register_customer(
    IN  p_first_name  VARCHAR,
    IN  p_last_name   VARCHAR,
    IN  p_email       VARCHAR,
    IN  p_phone       VARCHAR,
    IN  p_dob         DATE,
    IN  p_id_type     VARCHAR,
    IN  p_id_number   VARCHAR,
    IN  p_branch_id   INT,
    OUT p_customer_id  INT,
    OUT p_customer_code VARCHAR,
    OUT p_error_msg    TEXT
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- Generate customer code
    p_customer_code := 'CUST-' || LPAD(nextval('customers_id_seq')::TEXT, 5, '0');

    -- "try" block
    INSERT INTO customers (customer_code, first_name, last_name, email, phone, date_of_birth, id_type, id_number, branch_id)
    VALUES (p_customer_code, p_first_name, p_last_name, p_email, p_phone, p_dob, p_id_type, p_id_number, p_branch_id)
    RETURNING id INTO p_customer_id;

    p_error_msg := NULL;   -- no error

EXCEPTION
    WHEN unique_violation THEN
        p_customer_id := NULL;
        -- Check which unique constraint was violated
        IF SQLERRM LIKE '%email%' THEN
            p_error_msg := 'Email "' || p_email || '" is already registered';
        ELSIF SQLERRM LIKE '%id_number%' THEN
            p_error_msg := 'ID number "' || p_id_number || '" is already registered';
        ELSE
            p_error_msg := 'Duplicate entry: ' || SQLERRM;
        END IF;

    WHEN check_violation THEN
        p_customer_id := NULL;
        p_error_msg := 'Invalid value for id_type. Must be PASSPORT, NATIONAL_ID, or DRIVING_LICENSE';

    WHEN foreign_key_violation THEN
        p_customer_id := NULL;
        p_error_msg := 'Branch ID ' || p_branch_id || ' does not exist';

    WHEN not_null_violation THEN
        p_customer_id := NULL;
        p_error_msg := 'Required field is missing: ' || SQLERRM;

    WHEN OTHERS THEN
        p_customer_id := NULL;
        p_error_msg := 'Unexpected error [' || SQLSTATE || ']: ' || SQLERRM;
END;
$$;

-- Successful call
CALL register_customer('John', 'Doe', 'john@email.com', '+1234567890', '1990-05-15', 'PASSPORT', 'P12345678', 1, NULL, NULL, NULL);
-- Returns: p_customer_id = 1, p_customer_code = 'CUST-00001', p_error_msg = NULL

-- Duplicate email
CALL register_customer('Jane', 'Doe', 'john@email.com', '+9876543210', '1992-03-20', 'NATIONAL_ID', 'N98765432', 1, NULL, NULL, NULL);
-- Returns: p_customer_id = NULL, p_error_msg = 'Email "john@email.com" is already registered'

-- Invalid ID type
CALL register_customer('Bob', 'Smith', 'bob@email.com', '+1112223333', '1985-11-01', 'INVALID', 'X00000', 1, NULL, NULL, NULL);
-- Returns: p_customer_id = NULL, p_error_msg = 'Invalid value for id_type...'
```

### Nested Exception Blocks (Inner Try-Catch)

You can nest `BEGIN...EXCEPTION...END` blocks to handle errors at different levels.

```sql
CREATE OR REPLACE PROCEDURE open_account_with_deposit(
    IN  p_customer_id   INT,
    IN  p_branch_id     INT,
    IN  p_account_type  VARCHAR,
    IN  p_initial_deposit DECIMAL,
    OUT p_account_id    INT,
    OUT p_account_number VARCHAR,
    OUT p_message       TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_min_balance   DECIMAL;
    v_kyc_verified  BOOLEAN;
BEGIN
    -- Check KYC
    SELECT kyc_verified INTO v_kyc_verified
    FROM customers WHERE id = p_customer_id;

    IF NOT FOUND THEN
        p_message := 'Customer not found';
        RETURN;
    END IF;

    IF NOT v_kyc_verified THEN
        p_message := 'Customer KYC not verified. Cannot open account';
        RETURN;
    END IF;

    -- Determine minimum balance
    v_min_balance := CASE p_account_type
        WHEN 'SAVINGS' THEN 500.00
        WHEN 'CURRENT' THEN 5000.00
        WHEN 'FIXED_DEPOSIT' THEN 10000.00
        ELSE 500.00
    END;

    IF p_initial_deposit < v_min_balance THEN
        p_message := 'Minimum deposit for ' || p_account_type || ' is ' || v_min_balance;
        RETURN;
    END IF;

    -- Generate account number
    p_account_number := 'ACC-' || LPAD(nextval('accounts_id_seq')::TEXT, 10, '0');

    -- Inner try-catch: Insert account
    BEGIN
        INSERT INTO accounts (account_number, customer_id, branch_id, account_type, balance, min_balance)
        VALUES (p_account_number, p_customer_id, p_branch_id, p_account_type, p_initial_deposit, v_min_balance)
        RETURNING id INTO p_account_id;

    EXCEPTION
        WHEN check_violation THEN
            RAISE NOTICE 'Account insert failed: Invalid account type';
            RAISE;  -- Re-throw to outer block for full rollback
        WHEN foreign_key_violation THEN
            RAISE NOTICE 'Account insert failed: Invalid branch or customer ID';
            RAISE;  -- Re-throw to outer block for full rollback
        WHEN OTHERS THEN
            RAISE NOTICE 'Account insert failed: %', SQLERRM;
            RAISE;  -- Re-throw to outer block for full rollback
    END;
	BEGIN
		-- Record initial deposit transaction
	    INSERT INTO transactions (transaction_ref, account_id, transaction_type, amount, balance_before, balance_after, description)
	    VALUES (
	        'TXN-' || TO_CHAR(NOW(), 'YYYYMMDD') || '-' || LPAD(nextval('transactions_id_seq')::TEXT, 5, '0'),
	        p_account_id, 'DEPOSIT', p_initial_deposit, 0.00, p_initial_deposit,
	        'Initial deposit for account opening'
	    );

	EXCEPTION
        WHEN foreign_key_violation THEN
            RAISE NOTICE 'Transaction insert failed: Invalid account ID';
            RAISE;  -- Re-throw to outer block - THIS WILL ROLLBACK THE ACCOUNT INSERT TOO
        WHEN OTHERS THEN
            RAISE NOTICE 'Transaction insert failed: %', SQLERRM;
            RAISE;  -- Re-throw to outer block - THIS WILL ROLLBACK THE ACCOUNT INSERT TOO
	END;

    p_message := 'Account ' || p_account_number || ' opened with balance ' || p_initial_deposit;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_account_id := NULL;
        p_account_number := NULL;
        p_message := 'Account opening failed: ' || SQLERRM;
END;
$$;

-- Usage
CALL open_account_with_deposit(1, 1, 'SAVINGS', 5000.00, NULL, NULL, NULL);
-- Returns:
-- p_account_id | p_account_number    | p_message
-- -------------+---------------------+-----------------------------------------------
-- 3            | ACC-0000000003      | Account ACC-0000000003 opened with balance 5000.00
```

---

## Transaction Control — COMMIT & ROLLBACK

Only **stored procedures** can use `COMMIT` and `ROLLBACK`. Functions run inside a single transaction and cannot control it.

### How It Works

| Command | Behavior |
|---------|----------|
| `COMMIT` | Saves all changes made since the last COMMIT/ROLLBACK (or start) |
| `ROLLBACK` | Undoes all changes since the last COMMIT/ROLLBACK (or start) |

When you `CALL` a procedure, PostgreSQL starts an implicit transaction. The procedure can then explicitly commit or roll back at any point.

### Example — Fund Transfer Between Accounts (COMMIT on success, ROLLBACK on failure)

```sql
CREATE OR REPLACE PROCEDURE transfer_funds(
    IN  p_from_account_id INT,
    IN  p_to_account_id   INT,
    IN  p_amount          DECIMAL,
    IN  p_description     TEXT DEFAULT 'Fund transfer',
    IN  p_performed_by    INT DEFAULT NULL,
    OUT p_transfer_ref    VARCHAR,
    OUT p_fee             DECIMAL,
    OUT p_message         TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_from_balance  DECIMAL;
    v_to_balance    DECIMAL;
    v_from_status   VARCHAR;
    v_to_status     VARCHAR;
    v_from_branch   INT;
    v_to_branch     INT;
    v_total_debit   DECIMAL;
    v_txn_out_ref   VARCHAR;
    v_txn_in_ref    VARCHAR;
    v_txn_out_id    INT;
BEGIN
    -- 1. Validate source account
    SELECT balance, status, branch_id
    INTO v_from_balance, v_from_status, v_from_branch
    FROM accounts WHERE id = p_from_account_id;

    IF NOT FOUND THEN
        p_message := 'Source account not found';
        RETURN;
    END IF;

    IF v_from_status != 'ACTIVE' THEN
        p_message := 'Source account is ' || v_from_status;
        RETURN;
    END IF;

    -- 2. Validate destination account
    SELECT balance, status, branch_id
    INTO v_to_balance, v_to_status, v_to_branch
    FROM accounts WHERE id = p_to_account_id;

    IF NOT FOUND THEN
        p_message := 'Destination account not found';
        RETURN;
    END IF;

    IF v_to_status != 'ACTIVE' THEN
        p_message := 'Destination account is ' || v_to_status;
        RETURN;
    END IF;

    -- 3. Calculate fee (inter-branch transfer = $5, same branch = free)
    IF v_from_branch != v_to_branch THEN
        p_fee := 5.00;
    ELSE
        p_fee := 0.00;
    END IF;

    v_total_debit := p_amount + p_fee;

    -- 4. Check sufficient balance
    IF v_from_balance < v_total_debit THEN
        p_message := 'Insufficient balance. Available: ' || v_from_balance || ', Required: ' || v_total_debit || ' (includes fee: ' || p_fee || ')';
        RETURN;
    END IF;

    -- 5. Generate references
    p_transfer_ref := 'TRF-' || TO_CHAR(NOW(), 'YYYYMMDD') || '-' || LPAD(nextval('transfers_id_seq')::TEXT, 5, '0');
    v_txn_out_ref := 'TXN-' || TO_CHAR(NOW(), 'YYYYMMDD') || '-' || LPAD(nextval('transactions_id_seq')::TEXT, 5, '0');
    v_txn_in_ref  := 'TXN-' || TO_CHAR(NOW(), 'YYYYMMDD') || '-' || LPAD(nextval('transactions_id_seq')::TEXT, 5, '0');

    -- 6. Debit source account
    UPDATE accounts SET balance = balance - v_total_debit WHERE id = p_from_account_id;

    INSERT INTO transactions (transaction_ref, account_id, transaction_type, amount, balance_before, balance_after, description, performed_by)
    VALUES (v_txn_out_ref, p_from_account_id, 'TRANSFER_OUT', p_amount, v_from_balance, v_from_balance - v_total_debit, p_description, p_performed_by)
    RETURNING id INTO v_txn_out_id;

    -- 7. Record fee if applicable
    IF p_fee > 0 THEN
        INSERT INTO transactions (transaction_ref, account_id, transaction_type, amount, balance_before, balance_after, description, performed_by)
        VALUES (
            'TXN-' || TO_CHAR(NOW(), 'YYYYMMDD') || '-' || LPAD(nextval('transactions_id_seq')::TEXT, 5, '0'),
            p_from_account_id, 'FEE', p_fee, v_from_balance, v_from_balance - v_total_debit,
            'Inter-branch transfer fee', p_performed_by
        );
    END IF;

    -- 8. Credit destination account
    UPDATE accounts SET balance = balance + p_amount WHERE id = p_to_account_id;

    INSERT INTO transactions (transaction_ref, account_id, transaction_type, amount, balance_before, balance_after, description, related_txn_id, performed_by)
    VALUES (v_txn_in_ref, p_to_account_id, 'TRANSFER_IN', p_amount, v_to_balance, v_to_balance + p_amount, p_description, v_txn_out_id, p_performed_by);

    -- 9. Record transfer
    INSERT INTO transfers (transfer_ref, from_account_id, to_account_id, amount, fee, status, description)
    VALUES (p_transfer_ref, p_from_account_id, p_to_account_id, p_amount, p_fee, 'COMPLETED', p_description);

    -- 10. COMMIT — all changes saved atomically
    COMMIT;
    p_message := 'Transfer successful. Ref: ' || p_transfer_ref;

EXCEPTION
    WHEN OTHERS THEN
        -- ROLLBACK — undo debit, credit, everything
        ROLLBACK;
        p_transfer_ref := NULL;
        p_fee := NULL;
        p_message := 'Transfer failed: ' || SQLERRM;
END;
$$;

-- Usage: Transfer $2000 from account 1 to account 2
CALL transfer_funds(1, 2, 2000.00, 'Rent payment', 5, NULL, NULL, NULL);
-- Returns:
-- p_transfer_ref       | p_fee | p_message
-- ---------------------+-------+-----------------------------------------
-- TRF-20250129-00001   | 0.00  | Transfer successful. Ref: TRF-20250129-00001

-- Insufficient balance
CALL transfer_funds(1, 2, 999999.00, 'Large transfer', 5, NULL, NULL, NULL);
-- Returns:
-- p_transfer_ref | p_fee | p_message
-- ---------------+-------+--------------------------------------------------
-- NULL           | NULL  | Insufficient balance. Available: 50400.00, Required: 999999.00 (includes fee: 0.00)
```

### Batch Processing with Intermediate Commits

```sql
CREATE OR REPLACE PROCEDURE apply_monthly_interest(
    IN  p_month         DATE,
    OUT p_accounts_processed INT,
    OUT p_total_interest DECIMAL,
    OUT p_message        TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_acc           RECORD;
    v_interest      DECIMAL;
    v_daily_rate    DECIMAL;
    v_days_in_month INT;
BEGIN
    p_accounts_processed := 0;
    p_total_interest     := 0;
    v_days_in_month      := EXTRACT(DAY FROM (p_month + INTERVAL '1 month' - INTERVAL '1 day'))::INT;

    FOR v_acc IN
        SELECT id, account_number, balance, interest_rate
        FROM accounts
        WHERE account_type = 'SAVINGS'
          AND status = 'ACTIVE'
          AND interest_rate > 0
        ORDER BY id
    LOOP
        BEGIN
            -- Daily compounding: interest = balance * (annual_rate / 365) * days
            v_daily_rate := v_acc.interest_rate / 100 / 365;
            v_interest := ROUND(v_acc.balance * v_daily_rate * v_days_in_month, 2);

            IF v_interest > 0 THEN
                -- Credit interest
                UPDATE accounts SET balance = balance + v_interest WHERE id = v_acc.id;

                INSERT INTO transactions (transaction_ref, account_id, transaction_type, amount, balance_before, balance_after, description)
                VALUES (
                    'TXN-INT-' || TO_CHAR(p_month, 'YYYYMM') || '-' || v_acc.id,
                    v_acc.id, 'INTEREST', v_interest,
                    v_acc.balance, v_acc.balance + v_interest,
                    'Monthly interest for ' || TO_CHAR(p_month, 'Mon YYYY')
                );

                p_total_interest := p_total_interest + v_interest;
            END IF;

            p_accounts_processed := p_accounts_processed + 1;

            -- Commit every 100 accounts to avoid long-running transactions
            IF p_accounts_processed % 100 = 0 THEN
                COMMIT;
                RAISE NOTICE 'Checkpoint: % accounts processed', p_accounts_processed;
            END IF;

        EXCEPTION
            WHEN OTHERS THEN
                RAISE NOTICE 'Error on account %: %', v_acc.account_number, SQLERRM;
        END;
    END LOOP;

    COMMIT;   -- final commit for remaining accounts
    p_message := 'Interest applied. Accounts: ' || p_accounts_processed || ', Total interest: ' || p_total_interest;
END;
$$;

-- Usage
CALL apply_monthly_interest('2025-01-01', NULL, NULL, NULL);
-- Returns:
-- p_accounts_processed | p_total_interest | p_message
-- ---------------------+------------------+-----------------------------------------------
-- 350                  | 28450.75         | Interest applied. Accounts: 350, Total interest: 28450.75
```

---

## Conditional Logic — IF / CASE

### IF / ELSIF / ELSE

```sql
IF condition THEN
    -- statements
ELSIF another_condition THEN
    -- statements
ELSE
    -- statements
END IF;
```

### Example — Withdrawal with Account Type Rules

```sql
CREATE OR REPLACE PROCEDURE withdraw_money(
    IN  p_account_id   INT,
    IN  p_amount       DECIMAL,
    IN  p_performed_by INT DEFAULT NULL,
    OUT p_txn_ref      VARCHAR,
    OUT p_new_balance  DECIMAL,
    OUT p_message      TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_balance       DECIMAL;
    v_status        VARCHAR;
    v_acc_type      VARCHAR;
    v_min_balance   DECIMAL;
    v_overdraft     DECIMAL;
    v_max_withdraw  DECIMAL;
BEGIN
    -- Fetch account details
    SELECT balance, status, account_type, min_balance, overdraft_limit
    INTO v_balance, v_status, v_acc_type, v_min_balance, v_overdraft
    FROM accounts
    WHERE id = p_account_id;

    IF NOT FOUND THEN
        p_message := 'Account not found';
        RETURN;
    END IF;

    -- Status check
    IF v_status = 'FROZEN' THEN
        p_message := 'Account is FROZEN by the bank. Contact customer support';
        RETURN;
    ELSIF v_status = 'DORMANT' THEN
        p_message := 'Account is DORMANT. Please reactivate first';
        RETURN;
    ELSIF v_status = 'CLOSED' THEN
        p_message := 'Account is CLOSED';
        RETURN;
    ELSIF v_status != 'ACTIVE' THEN
        p_message := 'Account status "' || v_status || '" does not allow withdrawals';
        RETURN;
    END IF;

    -- Account type rules
    IF v_acc_type = 'FIXED_DEPOSIT' THEN
        p_message := 'Cannot withdraw from Fixed Deposit. Use break-FD procedure';
        RETURN;

    ELSIF v_acc_type = 'SAVINGS' THEN
        -- Savings: must maintain minimum balance, no overdraft
        IF (v_balance - p_amount) < v_min_balance THEN
            p_message := 'Withdrawal denied. Balance would drop below minimum (' || v_min_balance || '). Available: ' || (v_balance - v_min_balance);
            RETURN;
        END IF;

    ELSIF v_acc_type = 'CURRENT' THEN
        -- Current: overdraft allowed up to limit
        IF (v_balance - p_amount) < (v_min_balance - v_overdraft) THEN
            v_max_withdraw := v_balance - v_min_balance + v_overdraft;
            p_message := 'Withdrawal denied. Exceeds overdraft limit. Max withdrawal: ' || v_max_withdraw;
            RETURN;
        END IF;
    END IF;

    -- Process withdrawal
    p_new_balance := v_balance - p_amount;
    p_txn_ref := 'TXN-' || TO_CHAR(NOW(), 'YYYYMMDD') || '-' || LPAD(nextval('transactions_id_seq')::TEXT, 5, '0');

    UPDATE accounts SET balance = p_new_balance WHERE id = p_account_id;

    INSERT INTO transactions (transaction_ref, account_id, transaction_type, amount, balance_before, balance_after, description, performed_by)
    VALUES (p_txn_ref, p_account_id, 'WITHDRAWAL', p_amount, v_balance, p_new_balance, 'Cash withdrawal', p_performed_by);

    COMMIT;
    p_message := 'Withdrawal successful. New balance: ' || p_new_balance;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_txn_ref := NULL;
        p_new_balance := NULL;
        p_message := 'Withdrawal failed: ' || SQLERRM;
END;
$$;

-- Successful withdrawal from savings
CALL withdraw_money(1, 2000.00, 5, NULL, NULL, NULL);
-- Returns: p_txn_ref = 'TXN-20250129-00020', p_new_balance = 48400.00, p_message = 'Withdrawal successful...'

-- Frozen account
CALL withdraw_money(3, 500.00, 5, NULL, NULL, NULL);
-- Returns: p_txn_ref = NULL, p_message = 'Account is FROZEN by the bank...'

-- Below minimum balance
CALL withdraw_money(1, 99000.00, 5, NULL, NULL, NULL);
-- Returns: p_txn_ref = NULL, p_message = 'Withdrawal denied. Balance would drop below minimum...'
```

### CASE Expression (Simple and Searched)

```sql
-- Simple CASE
CASE variable
    WHEN 'value1' THEN result1
    WHEN 'value2' THEN result2
    ELSE default_result
END;

-- Searched CASE
CASE
    WHEN condition1 THEN result1
    WHEN condition2 THEN result2
    ELSE default_result
END;
```

### Example — Loan Eligibility Check Using CASE

```sql
CREATE OR REPLACE FUNCTION check_loan_eligibility(
    p_customer_id   INT,
    p_loan_type     VARCHAR,
    p_requested_amt DECIMAL
)
RETURNS TEXT
LANGUAGE plpgsql
AS $$
DECLARE
    v_total_balance     DECIMAL;
    v_active_loans      INT;
    v_max_loan_amount   DECIMAL;
    v_kyc               BOOLEAN;
    v_status            VARCHAR;
BEGIN
    -- Get customer info
    SELECT kyc_verified, status INTO v_kyc, v_status
    FROM customers WHERE id = p_customer_id;

    IF NOT FOUND THEN
        RETURN 'REJECTED: Customer not found';
    END IF;

    IF NOT v_kyc THEN
        RETURN 'REJECTED: KYC not verified';
    END IF;

    IF v_status != 'ACTIVE' THEN
        RETURN 'REJECTED: Customer status is ' || v_status;
    END IF;

    -- Total account balance
    SELECT COALESCE(SUM(balance), 0) INTO v_total_balance
    FROM accounts WHERE customer_id = p_customer_id AND status = 'ACTIVE';

    -- Active loans count
    SELECT COUNT(*) INTO v_active_loans
    FROM loans WHERE customer_id = p_customer_id AND status = 'ACTIVE';

    -- Max loan amount based on type (CASE)
    v_max_loan_amount := CASE p_loan_type
        WHEN 'PERSONAL'  THEN v_total_balance * 5
        WHEN 'HOME'      THEN v_total_balance * 30
        WHEN 'AUTO'      THEN v_total_balance * 8
        WHEN 'BUSINESS'  THEN v_total_balance * 15
        WHEN 'EDUCATION' THEN v_total_balance * 10
        ELSE 0
    END;

    -- Eligibility decision using searched CASE
    RETURN CASE
        WHEN v_active_loans >= 3 THEN
            'REJECTED: Too many active loans (' || v_active_loans || ')'
        WHEN p_requested_amt > v_max_loan_amount THEN
            'REJECTED: Max eligible amount for ' || p_loan_type || ' is ' || v_max_loan_amount
        WHEN v_total_balance < 1000 THEN
            'REJECTED: Minimum account balance of 1000 required. Current: ' || v_total_balance
        ELSE
            'ELIGIBLE: Approved for ' || p_loan_type || ' up to ' || v_max_loan_amount
    END;
END;
$$;

-- Usage
SELECT check_loan_eligibility(1, 'HOME', 500000);
-- Returns: 'ELIGIBLE: Approved for HOME up to 1500000'

SELECT check_loan_eligibility(1, 'PERSONAL', 999999);
-- Returns: 'REJECTED: Max eligible amount for PERSONAL is 250000'
```

---

## Loops — FOR, WHILE, LOOP

### 1. FOR Loop — Iterating Over a Query Result

```sql
FOR record_variable IN query LOOP
    -- body
END LOOP;
```

#### Example — Daily Dormant Account Check

```sql
CREATE OR REPLACE PROCEDURE mark_dormant_accounts(
    IN  p_inactive_days INT DEFAULT 365,
    OUT p_marked_count  INT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_acc RECORD;
    v_last_txn_date TIMESTAMP;
BEGIN
    p_marked_count := 0;

    FOR v_acc IN
        SELECT id, account_number, customer_id
        FROM accounts
        WHERE status = 'ACTIVE'
          AND account_type != 'FIXED_DEPOSIT'
    LOOP
        -- Find last transaction date
        SELECT MAX(created_at) INTO v_last_txn_date
        FROM transactions
        WHERE account_id = v_acc.id;

        -- If no transactions ever, use account open date
        IF v_last_txn_date IS NULL THEN
            SELECT opened_at INTO v_last_txn_date
            FROM accounts WHERE id = v_acc.id;
        END IF;

        -- Mark dormant if inactive beyond threshold
        IF v_last_txn_date < NOW() - (p_inactive_days || ' days')::INTERVAL THEN
            UPDATE accounts SET status = 'DORMANT' WHERE id = v_acc.id;

            INSERT INTO audit_log (table_name, operation, record_id, new_values)
            VALUES ('accounts', 'STATUS_CHANGE', v_acc.id,
                jsonb_build_object('new_status', 'DORMANT', 'last_activity', v_last_txn_date, 'inactive_days', p_inactive_days)
            );

            p_marked_count := p_marked_count + 1;
        END IF;
    END LOOP;

    COMMIT;
END;
$$;

-- Usage
CALL mark_dormant_accounts(365, NULL);
-- Returns: p_marked_count = 12
```

### 2. FOR Loop — Iterating Over a Range of Numbers

```sql
FOR i IN 1..12 LOOP
    -- body using i
END LOOP;

-- Reverse
FOR i IN REVERSE 12..1 LOOP
    -- counts 12, 11, 10, ..., 1
END LOOP;
```

#### Example — Generate EMI Schedule for a Loan

```sql
CREATE OR REPLACE FUNCTION generate_emi_schedule(
    p_principal     DECIMAL,
    p_annual_rate   DECIMAL,
    p_tenure_months INT,
    p_start_date    DATE
)
RETURNS TABLE(
    installment_no  INT,
    due_date        DATE,
    emi_amount      DECIMAL,
    principal_part  DECIMAL,
    interest_part   DECIMAL,
    remaining_balance DECIMAL
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_emi           DECIMAL;
    v_monthly_rate  DECIMAL;
    v_remaining     DECIMAL;
    v_interest      DECIMAL;
    v_principal     DECIMAL;
BEGIN
    v_monthly_rate := p_annual_rate / 12 / 100;
    v_emi := calculate_emi(p_principal, p_annual_rate, p_tenure_months);
    v_remaining := p_principal;

    FOR i IN 1..p_tenure_months LOOP
        v_interest  := ROUND(v_remaining * v_monthly_rate, 2);
        v_principal := v_emi - v_interest;

        -- Last installment adjustment
        IF i = p_tenure_months THEN
            v_principal := v_remaining;
            v_emi := v_principal + v_interest;
        END IF;

        v_remaining := v_remaining - v_principal;

        installment_no    := i;
        due_date          := p_start_date + (i || ' months')::INTERVAL;
        emi_amount        := v_emi;
        principal_part    := v_principal;
        interest_part     := v_interest;
        remaining_balance := GREATEST(v_remaining, 0);

        RETURN NEXT;
    END LOOP;
END;
$$;

-- Usage: Generate schedule for a $100,000 loan at 9% for 12 months
SELECT * FROM generate_emi_schedule(100000, 9.0, 12, '2025-01-01');
-- Returns 12 rows with installment_no, due_date, emi, principal, interest, remaining
```

### 3. WHILE Loop

```sql
WHILE condition LOOP
    -- body
END LOOP;
```

#### Example — Generate Unique Account Number with Retry

```sql
CREATE OR REPLACE FUNCTION generate_account_number()
RETURNS VARCHAR
LANGUAGE plpgsql
AS $$
DECLARE
    v_number VARCHAR;
    v_exists BOOLEAN;
    v_seq    INT := 1;
BEGIN
    WHILE TRUE LOOP
        v_number := 'ACC-' || LPAD(v_seq::TEXT, 10, '0');

        SELECT EXISTS(
            SELECT 1 FROM accounts WHERE account_number = v_number
        ) INTO v_exists;

        EXIT WHEN NOT v_exists;   -- EXIT breaks out of WHILE
        v_seq := v_seq + 1;
    END LOOP;

    RETURN v_number;
END;
$$;

-- Usage
SELECT generate_account_number();   -- Returns 'ACC-0000000001' (or next available)
```

### 4. Simple LOOP with EXIT

```sql
LOOP
    -- body
    EXIT WHEN condition;    -- break out
    -- or: EXIT;            -- unconditional break
END LOOP;
```

### 5. CONTINUE — Skip to Next Iteration

```sql
FOR v_acc IN SELECT * FROM accounts LOOP
    CONTINUE WHEN v_acc.status = 'CLOSED';    -- skip closed accounts
    -- process active/dormant/frozen accounts
END LOOP;
```

---

## Cursors

A cursor allows you to process query results **one row at a time**, with explicit control over fetching. Useful when you need fine-grained control that a `FOR` loop doesn't provide.

### Cursor Lifecycle

1. **DECLARE** the cursor
2. **OPEN** it (executes the query)
3. **FETCH** rows one at a time
4. **CLOSE** when done

### Syntax

```sql
DECLARE
    cursor_name CURSOR FOR query;
    -- or parameterized:
    cursor_name CURSOR (param type) FOR query_using_param;
BEGIN
    OPEN cursor_name;
    -- or: OPEN cursor_name(value);

    LOOP
        FETCH cursor_name INTO variable;
        EXIT WHEN NOT FOUND;
        -- process row
    END LOOP;

    CLOSE cursor_name;
END;
```

### Example — Overdue Loan EMI Penalty Processing with Cursor

```sql
CREATE OR REPLACE PROCEDURE process_overdue_emi_penalties(
    OUT p_penalties_applied INT,
    OUT p_total_penalty     DECIMAL,
    OUT p_message           TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    loan_cursor CURSOR FOR
        SELECT l.id, l.loan_number, l.emi_amount, l.next_emi_date,
               c.first_name || ' ' || c.last_name AS customer_name,
               CURRENT_DATE - l.next_emi_date AS days_overdue
        FROM loans l
        JOIN customers c ON l.customer_id = c.id
        WHERE l.status = 'ACTIVE'
          AND l.next_emi_date < CURRENT_DATE
        ORDER BY l.next_emi_date;

    v_rec           RECORD;
    v_penalty       DECIMAL;
    v_penalty_rate  DECIMAL;
BEGIN
    p_penalties_applied := 0;
    p_total_penalty     := 0;

    OPEN loan_cursor;

    LOOP
        FETCH loan_cursor INTO v_rec;
        EXIT WHEN NOT FOUND;

        -- Determine penalty rate based on days overdue
        IF v_rec.days_overdue > 90 THEN
            v_penalty_rate := 5.0;  -- 5% of EMI
        ELSIF v_rec.days_overdue > 60 THEN
            v_penalty_rate := 3.0;
        ELSIF v_rec.days_overdue > 30 THEN
            v_penalty_rate := 2.0;
        ELSE
            v_penalty_rate := 1.0;
        END IF;

        v_penalty := ROUND(v_rec.emi_amount * v_penalty_rate / 100, 2);

        -- Inner exception block: one loan failing won't stop others
        BEGIN
            -- Update loan outstanding
            UPDATE loans
            SET outstanding_balance = outstanding_balance + v_penalty
            WHERE id = v_rec.id;

            -- Log penalty
            INSERT INTO audit_log (table_name, operation, record_id, new_values)
            VALUES ('loans', 'PENALTY', v_rec.id,
                jsonb_build_object(
                    'loan_number', v_rec.loan_number,
                    'customer', v_rec.customer_name,
                    'days_overdue', v_rec.days_overdue,
                    'penalty_rate', v_penalty_rate || '%',
                    'penalty_amount', v_penalty,
                    'emi_amount', v_rec.emi_amount
                )
            );

            p_penalties_applied := p_penalties_applied + 1;
            p_total_penalty     := p_total_penalty + v_penalty;

            RAISE NOTICE 'Penalty $% applied to loan % (% — % days overdue)',
                v_penalty, v_rec.loan_number, v_rec.customer_name, v_rec.days_overdue;

        EXCEPTION
            WHEN OTHERS THEN
                RAISE NOTICE 'Error on loan %: %', v_rec.loan_number, SQLERRM;
        END;
    END LOOP;

    CLOSE loan_cursor;
    COMMIT;
    p_message := 'Penalties applied: ' || p_penalties_applied || ', Total: $' || p_total_penalty;

EXCEPTION
    WHEN OTHERS THEN
        CLOSE loan_cursor;
        ROLLBACK;
        p_penalties_applied := 0;
        p_total_penalty := 0;
        p_message := 'Penalty processing failed: ' || SQLERRM;
END;
$$;

-- Usage
CALL process_overdue_emi_penalties(NULL, NULL, NULL);
-- Returns:
-- p_penalties_applied | p_total_penalty | p_message
-- --------------------+-----------------+--------------------------------------------
-- 8                   | 1250.00         | Penalties applied: 8, Total: $1250.00
```

### Parameterized Cursor Example — Account Statement

```sql
CREATE OR REPLACE PROCEDURE print_account_statement(
    IN  p_account_id INT,
    IN  p_from_date  DATE,
    IN  p_to_date    DATE,
    OUT p_txn_count  INT,
    OUT p_total_debit  DECIMAL,
    OUT p_total_credit DECIMAL
)
LANGUAGE plpgsql
AS $$
DECLARE
    stmt_cursor CURSOR (c_acc_id INT, c_from DATE, c_to DATE) FOR
        SELECT transaction_ref, transaction_type, amount,
               balance_before, balance_after, description, created_at
        FROM transactions
        WHERE account_id = c_acc_id
          AND created_at::DATE BETWEEN c_from AND c_to
        ORDER BY created_at;

    v_rec RECORD;
BEGIN
    p_txn_count    := 0;
    p_total_debit  := 0;
    p_total_credit := 0;

    OPEN stmt_cursor(p_account_id, p_from_date, p_to_date);

    LOOP
        FETCH stmt_cursor INTO v_rec;
        EXIT WHEN NOT FOUND;

        p_txn_count := p_txn_count + 1;

        IF v_rec.transaction_type IN ('WITHDRAWAL', 'TRANSFER_OUT', 'FEE') THEN
            p_total_debit := p_total_debit + v_rec.amount;
        ELSE
            p_total_credit := p_total_credit + v_rec.amount;
        END IF;

        RAISE NOTICE '% | %-15s | %10s | Before: % | After: % | %',
            TO_CHAR(v_rec.created_at, 'YYYY-MM-DD'),
            v_rec.transaction_type,
            v_rec.amount,
            v_rec.balance_before,
            v_rec.balance_after,
            COALESCE(v_rec.description, '');
    END LOOP;

    CLOSE stmt_cursor;
END;
$$;

-- Usage
CALL print_account_statement(1, '2025-01-01', '2025-01-31', NULL, NULL, NULL);
-- Returns: p_txn_count = 15, p_total_debit = 12000.00, p_total_credit = 25000.00
```

### Cursor vs FOR Loop — When to Use What

| Use Case | Recommendation |
|----------|---------------|
| Simple row-by-row processing | `FOR rec IN query LOOP` (simpler syntax) |
| Need to FETCH one row at a time with pauses/commits | Explicit `CURSOR` |
| Pass query parameters dynamically | Parameterized `CURSOR` |
| Need to update current row | `CURSOR ... FOR UPDATE` with `WHERE CURRENT OF` |

---

## Returning Values from Procedures

Procedures don't use `RETURN value`. Here is every approach to get data back.

### 1. OUT Parameters (most common)

```sql
CREATE OR REPLACE PROCEDURE get_account_summary(
    IN  p_account_id     INT,
    OUT p_account_number VARCHAR,
    OUT p_account_type   VARCHAR,
    OUT p_balance        DECIMAL,
    OUT p_total_deposits DECIMAL,
    OUT p_total_withdrawals DECIMAL
)
LANGUAGE plpgsql
AS $$
BEGIN
    SELECT a.account_number, a.account_type, a.balance
    INTO p_account_number, p_account_type, p_balance
    FROM accounts a
    WHERE a.id = p_account_id;

    SELECT COALESCE(SUM(CASE WHEN transaction_type IN ('DEPOSIT','TRANSFER_IN','INTEREST') THEN amount ELSE 0 END), 0),
           COALESCE(SUM(CASE WHEN transaction_type IN ('WITHDRAWAL','TRANSFER_OUT','FEE') THEN amount ELSE 0 END), 0)
    INTO p_total_deposits, p_total_withdrawals
    FROM transactions
    WHERE account_id = p_account_id;
END;
$$;

-- Call directly — returns a result row
CALL get_account_summary(1, NULL, NULL, NULL, NULL, NULL);
-- Returns:
-- p_account_number | p_account_type | p_balance  | p_total_deposits | p_total_withdrawals
-- -----------------+----------------+------------+------------------+--------------------
-- ACC-0000000001   | SAVINGS        | 52400.00   | 125000.00        | 72600.00

-- Capture in a DO block
DO $$
DECLARE
    v_num VARCHAR; v_type VARCHAR; v_bal DECIMAL; v_dep DECIMAL; v_wd DECIMAL;
BEGIN
    CALL get_account_summary(1, v_num, v_type, v_bal, v_dep, v_wd);
    RAISE NOTICE 'Account: %, Type: %, Balance: %', v_num, v_type, v_bal;
END;
$$;
```

### 2. INOUT Parameters

The caller passes a value in, the procedure modifies it, and the modified value is returned.

```sql
CREATE OR REPLACE PROCEDURE apply_service_charge(
    INOUT p_balance DECIMAL,
    IN    p_account_type VARCHAR DEFAULT 'SAVINGS'
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_charge DECIMAL;
BEGIN
    v_charge := CASE p_account_type
        WHEN 'SAVINGS' THEN 50.00
        WHEN 'CURRENT' THEN 150.00
        ELSE 25.00
    END;

    p_balance := p_balance - v_charge;
END;
$$;

-- Usage
CALL apply_service_charge(10000.00, 'CURRENT');
-- Returns: p_balance = 9850.00

-- In a DO block
DO $$
DECLARE
    v_bal DECIMAL := 25000.00;
BEGIN
    CALL apply_service_charge(v_bal, 'SAVINGS');
    RAISE NOTICE 'Balance after charge: %', v_bal;  -- 24950.00
END;
$$;
```

### 3. Writing to a Result Table

For complex output (multiple rows, reports), write to a table and query it afterward.

```sql
CREATE TABLE report_output (
    id              SERIAL PRIMARY KEY,
    report_name     VARCHAR(100),
    generated_at    TIMESTAMP DEFAULT NOW(),
    data            JSONB
);

CREATE OR REPLACE PROCEDURE generate_branch_report(
    IN  p_branch_id INT,
    OUT p_report_id INT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_data JSONB;
BEGIN
    SELECT jsonb_build_object(
        'total_customers', (SELECT COUNT(*) FROM customers WHERE branch_id = p_branch_id AND status = 'ACTIVE'),
        'total_accounts', (SELECT COUNT(*) FROM accounts WHERE branch_id = p_branch_id AND status = 'ACTIVE'),
        'total_deposits', (SELECT COALESCE(SUM(balance), 0) FROM accounts WHERE branch_id = p_branch_id AND status = 'ACTIVE'),
        'active_loans', (SELECT COUNT(*) FROM loans l JOIN accounts a ON l.account_id = a.id WHERE a.branch_id = p_branch_id AND l.status = 'ACTIVE'),
        'loan_outstanding', (SELECT COALESCE(SUM(outstanding_balance), 0) FROM loans l JOIN accounts a ON l.account_id = a.id WHERE a.branch_id = p_branch_id AND l.status = 'ACTIVE')
    ) INTO v_data;

    INSERT INTO report_output (report_name, data)
    VALUES ('Branch Report - ' || p_branch_id, v_data)
    RETURNING id INTO p_report_id;

    COMMIT;
END;
$$;

-- Usage
CALL generate_branch_report(1, NULL);
-- Returns: p_report_id = 1

-- Then query the result
SELECT * FROM report_output WHERE id = 1;
```

---

## Variables and Annotations

### Variable Declaration (DECLARE block)

```sql
DECLARE
    v_name      VARCHAR(100) := 'Default';      -- VARCHAR with default
    v_count     INT := 0;                        -- Integer
    v_amount    DECIMAL(15,2) := 0.00;           -- Decimal with precision
    v_is_active BOOLEAN := TRUE;                 -- Boolean
    v_today     DATE := CURRENT_DATE;            -- Date
    v_note      TEXT;                             -- NULL by default
    v_created   TIMESTAMP := NOW();              -- Timestamp
```

### CONSTANT — Immutable Variables

```sql
DECLARE
    TRANSFER_FEE   CONSTANT DECIMAL := 5.00;
    MAX_DAILY_LIMIT CONSTANT DECIMAL := 50000.00;
BEGIN
    -- TRANSFER_FEE := 10.00;  -- ERROR: variable "transfer_fee" is declared CONSTANT
```

### %TYPE — Match Column Type

```sql
DECLARE
    v_balance    accounts.balance%TYPE;              -- inherits DECIMAL(15,2)
    v_acc_number accounts.account_number%TYPE;       -- inherits VARCHAR(20)
```

### %ROWTYPE — Match Entire Row

```sql
DECLARE
    v_acc accounts%ROWTYPE;
BEGIN
    SELECT * INTO v_acc FROM accounts WHERE account_id = 1;
    RAISE NOTICE 'Account: %, Balance: %', v_acc.account_number, v_acc.balance;
```

### RECORD — Dynamic Row Type

```sql
DECLARE
    rec RECORD;   -- structure determined at runtime
BEGIN
    FOR rec IN SELECT account_number, balance FROM accounts WHERE status = 'ACTIVE' LOOP
        RAISE NOTICE '%: $%', rec.account_number, rec.balance;
    END LOOP;
```

### FOUND — Row Existence Check

```sql
SELECT balance INTO v_balance FROM accounts WHERE account_id = 999;
IF NOT FOUND THEN
    RAISE EXCEPTION 'Account 999 does not exist';
END IF;
```

### GET DIAGNOSTICS — Row Count After DML

```sql
DECLARE
    v_rows INT;
BEGIN
    DELETE FROM audit_log WHERE performed_at < NOW() - INTERVAL '1 year';
    GET DIAGNOSTICS v_rows = ROW_COUNT;
    RAISE NOTICE 'Deleted % old audit records', v_rows;
```

### RAISE — Logging and Errors

| Level | Behavior |
|-------|----------|
| `DEBUG` | Lowest priority, usually suppressed |
| `LOG` | Written to server log |
| `INFO` | Sent to client |
| `NOTICE` | Default client message level |
| `WARNING` | Warning to client |
| `EXCEPTION` | Aborts current transaction/block |

```sql
RAISE NOTICE 'Processing account %', v_account_id;
RAISE WARNING 'Balance below minimum for account %', v_account_id;
RAISE EXCEPTION 'Transfer failed: insufficient funds. Available: %', v_balance;
```

---

## Complete Real-World Examples

### Example 1: Full Loan Disbursement — Validation + Account Credit + Audit

```sql
CREATE OR REPLACE PROCEDURE disburse_loan(
    IN  p_loan_id       INT,
    IN  p_approved_by   INT,
    OUT p_loan_number   VARCHAR,
    OUT p_disbursed_amt DECIMAL,
    OUT p_success       BOOLEAN,
    OUT p_message       TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_loan          loans%ROWTYPE;
    v_acc_status    VARCHAR;
    v_txn_ref       VARCHAR;
BEGIN
    -- 1. Fetch loan
    SELECT * INTO v_loan FROM loans WHERE loan_id = p_loan_id;

    IF NOT FOUND THEN
        p_success := FALSE;
        p_message := 'Loan not found';
        RETURN;
    END IF;

    p_loan_number := v_loan.loan_number;

    -- 2. Validate status
    IF v_loan.status != 'APPROVED' THEN
        p_success := FALSE;
        p_message := 'Loan status is ' || v_loan.status || '. Only APPROVED loans can be disbursed';
        RETURN;
    END IF;

    -- 3. Validate linked account
    SELECT status INTO v_acc_status FROM accounts WHERE account_id = v_loan.account_id;

    IF v_acc_status != 'ACTIVE' THEN
        p_success := FALSE;
        p_message := 'Linked account is ' || v_acc_status;
        RETURN;
    END IF;

    -- 4. Credit loan amount to account
    v_txn_ref := 'TXN-LOAN-' || v_loan.loan_number;

    UPDATE accounts
    SET balance = balance + v_loan.principal_amount
    WHERE account_id = v_loan.account_id;

    INSERT INTO transactions (transaction_ref, account_id, transaction_type, amount,
        balance_before, balance_after, description, performed_by)
    SELECT v_txn_ref, v_loan.account_id, 'DEPOSIT', v_loan.principal_amount,
        a.balance - v_loan.principal_amount, a.balance,
        'Loan disbursement: ' || v_loan.loan_number, p_approved_by
    FROM accounts a WHERE a.account_id = v_loan.account_id;

    -- 5. Update loan status
    UPDATE loans
    SET status = 'ACTIVE',
        disbursed_at = NOW(),
        next_emi_date = CURRENT_DATE + INTERVAL '1 month'
    WHERE loan_id = p_loan_id;

    -- 6. Audit
    INSERT INTO audit_log (table_name, operation, record_id, new_values, performed_by)
    VALUES ('loans', 'DISBURSE', p_loan_id,
        jsonb_build_object('loan_number', v_loan.loan_number, 'amount', v_loan.principal_amount, 'account_id', v_loan.account_id),
        p_approved_by);

    COMMIT;
    p_disbursed_amt := v_loan.principal_amount;
    p_success := TRUE;
    p_message := 'Loan ' || v_loan.loan_number || ' disbursed. Amount: $' || v_loan.principal_amount;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_disbursed_amt := NULL;
        p_success := FALSE;
        p_message := 'Disbursement failed: ' || SQLERRM;
END;
$$;

-- Usage
CALL disburse_loan(1, 10, NULL, NULL, NULL, NULL);
-- Returns:
-- p_loan_number | p_disbursed_amt | p_success | p_message
-- --------------+-----------------+-----------+--------------------------------------------------
-- LN-00001      | 500000.00       | TRUE      | Loan LN-00001 disbursed. Amount: $500000.00
```

### Example 2: End-of-Day Processing — Loop + Cursor + Exception + Commit Batching

```sql
CREATE OR REPLACE PROCEDURE end_of_day_processing(
    IN  p_processing_date DATE,
    OUT p_interest_accounts  INT,
    OUT p_dormant_marked     INT,
    OUT p_penalties_applied  INT,
    OUT p_message            TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    -- Cursor for interest calculation
    interest_cursor CURSOR FOR
        SELECT account_id, account_number, balance, interest_rate
        FROM accounts
        WHERE account_type = 'SAVINGS' AND status = 'ACTIVE' AND interest_rate > 0;

    v_rec           RECORD;
    v_daily_interest DECIMAL;
    v_processed     INT := 0;
BEGIN
    p_interest_accounts := 0;
    p_dormant_marked    := 0;
    p_penalties_applied := 0;

    RAISE NOTICE 'Starting end-of-day processing for %', p_processing_date;

    -- PHASE 1: Apply daily interest accrual (cursor + commit batching)
    OPEN interest_cursor;
    LOOP
        FETCH interest_cursor INTO v_rec;
        EXIT WHEN NOT FOUND;

        BEGIN
            v_daily_interest := ROUND(v_rec.balance * (v_rec.interest_rate / 100 / 365), 2);

            IF v_daily_interest > 0.01 THEN
                UPDATE accounts SET balance = balance + v_daily_interest WHERE account_id = v_rec.account_id;

                INSERT INTO transactions (transaction_ref, account_id, transaction_type, amount, balance_before, balance_after, description)
                VALUES (
                    'TXN-INT-' || TO_CHAR(p_processing_date, 'YYYYMMDD') || '-' || v_rec.account_id,
                    v_rec.account_id, 'INTEREST', v_daily_interest,
                    v_rec.balance, v_rec.balance + v_daily_interest,
                    'Daily interest accrual'
                );

                p_interest_accounts := p_interest_accounts + 1;
            END IF;

            v_processed := v_processed + 1;

            IF v_processed % 100 = 0 THEN
                COMMIT;
                RAISE NOTICE 'Interest checkpoint: % accounts', v_processed;
            END IF;

        EXCEPTION
            WHEN OTHERS THEN
                RAISE NOTICE 'Interest error on %: %', v_rec.account_number, SQLERRM;
        END;
    END LOOP;
    CLOSE interest_cursor;
    COMMIT;

    -- PHASE 2: Mark dormant accounts (FOR loop)
    FOR v_rec IN
        SELECT a.account_id, a.account_number
        FROM accounts a
        WHERE a.status = 'ACTIVE'
          AND a.account_type != 'FIXED_DEPOSIT'
          AND NOT EXISTS (
              SELECT 1 FROM transactions t
              WHERE t.account_id = a.account_id
                AND t.created_at > NOW() - INTERVAL '365 days'
          )
    LOOP
        UPDATE accounts SET status = 'DORMANT' WHERE account_id = v_rec.account_id;
        p_dormant_marked := p_dormant_marked + 1;
    END LOOP;
    COMMIT;

    -- PHASE 3: Apply loan penalties (FOR loop)
    FOR v_rec IN
        SELECT loan_id, loan_number, emi_amount, next_emi_date,
               CURRENT_DATE - next_emi_date AS days_late
        FROM loans
        WHERE status = 'ACTIVE' AND next_emi_date < p_processing_date
    LOOP
        BEGIN
            UPDATE loans
            SET outstanding_balance = outstanding_balance + ROUND(emi_amount * 0.02, 2)
            WHERE loan_id = v_rec.loan_id;

            p_penalties_applied := p_penalties_applied + 1;

        EXCEPTION
            WHEN OTHERS THEN
                RAISE NOTICE 'Penalty error on loan %: %', v_rec.loan_number, SQLERRM;
        END;
    END LOOP;
    COMMIT;

    p_message := 'EOD complete. Interest: ' || p_interest_accounts
        || ', Dormant: ' || p_dormant_marked
        || ', Penalties: ' || p_penalties_applied;

    RAISE NOTICE '%', p_message;
END;
$$;

-- Usage
CALL end_of_day_processing('2025-01-29', NULL, NULL, NULL, NULL);
-- Returns:
-- p_interest_accounts | p_dormant_marked | p_penalties_applied | p_message
-- --------------------+------------------+---------------------+--------------------------------------------------
-- 340                 | 5                | 8                   | EOD complete. Interest: 340, Dormant: 5, Penalties: 8
```

---

## Window Functions in Stored Procedures

Window functions perform calculations **across a set of rows related to the current row** without collapsing them into a single output row (unlike `GROUP BY`). They are extremely powerful inside stored procedures for ranking, running totals, comparisons, and analytics.

### Syntax

```sql
function_name() OVER (
    [PARTITION BY column1, column2, ...]
    [ORDER BY column3 [ASC|DESC], ...]
    [frame_clause]
)
```

| Component | Purpose |
|-----------|---------|
| `function_name()` | The window function (RANK, ROW_NUMBER, SUM, etc.) |
| `OVER(...)` | Defines the "window" of rows the function operates on |
| `PARTITION BY` | Divides rows into groups (like GROUP BY, but keeps all rows) |
| `ORDER BY` | Defines the order within each partition |
| `frame_clause` | Optional: `ROWS BETWEEN ...` or `RANGE BETWEEN ...` for sliding windows |

### OVER() — The Window Definition

`OVER()` is **required** for every window function. Without it, the function is not a window function.

```sql
-- OVER() with no arguments = entire result set is one window
SELECT account_number, balance,
       SUM(balance) OVER() AS total_balance_all_accounts
FROM accounts;

-- OVER(ORDER BY ...) = running calculation in that order
SELECT account_number, balance,
       SUM(balance) OVER(ORDER BY account_id) AS running_total
FROM accounts;

-- OVER(PARTITION BY ...) = separate window per group
SELECT account_number, account_type, balance,
       SUM(balance) OVER(PARTITION BY account_type) AS type_total
FROM accounts;

-- OVER(PARTITION BY ... ORDER BY ...) = running calculation within each group
SELECT account_number, account_type, balance,
       SUM(balance) OVER(PARTITION BY account_type ORDER BY balance DESC) AS running_total_in_type
FROM accounts;
```

### PARTITION BY — Grouping Without Collapsing

`PARTITION BY` splits the result set into partitions. The window function runs **independently** on each partition. Unlike `GROUP BY`, all original rows are preserved.

```sql
-- GROUP BY: collapses to 1 row per account_type (3 rows)
SELECT account_type, SUM(balance) FROM accounts GROUP BY account_type;

-- PARTITION BY: keeps all 15 rows, adds sum per type to each row
SELECT account_number, account_type, balance,
       SUM(balance) OVER(PARTITION BY account_type) AS type_total,
       ROUND(balance / SUM(balance) OVER(PARTITION BY account_type) * 100, 2) AS pct_of_type
FROM accounts
WHERE status = 'ACTIVE';
```

**Result:**
```
account_number   | account_type  | balance   | type_total | pct_of_type
-----------------+---------------+-----------+------------+------------
ACC-0000000002   | CURRENT       | 18500.00  | 85500.00   | 21.64
ACC-0000000006   | CURRENT       | 45000.00  | 85500.00   | 52.63
ACC-0000000009   | CURRENT       | 22000.00  | 85500.00   | 25.73
ACC-0000000004   | FIXED_DEPOSIT | 100000.00 | 150000.00  | 66.67
ACC-0000000015   | FIXED_DEPOSIT | 50000.00  | 150000.00  | 33.33
ACC-0000000001   | SAVINGS       | 52400.00  | 229250.00  | 22.86
...
```

---

### Window Function Reference

#### 1. ROW_NUMBER() — Sequential Number (No Ties)

Assigns a unique sequential integer to each row within a partition. **No duplicates** — ties are broken arbitrarily.

```sql
ROW_NUMBER() OVER(PARTITION BY ... ORDER BY ...)
```

**Example — Number Each Customer's Transactions Chronologically:**

```sql
CREATE OR REPLACE FUNCTION get_account_transactions_numbered(p_account_id INT)
RETURNS TABLE(
    row_num         BIGINT,
    transaction_ref VARCHAR,
    transaction_type VARCHAR,
    amount          DECIMAL,
    balance_after   DECIMAL,
    created_at      TIMESTAMP
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT
        ROW_NUMBER() OVER(ORDER BY t.created_at) AS row_num,
        t.transaction_ref,
        t.transaction_type,
        t.amount,
        t.balance_after,
        t.created_at
    FROM transactions t
    WHERE t.account_id = p_account_id
    ORDER BY t.created_at;
END;
$$;

-- Usage
SELECT * FROM get_account_transactions_numbered(1);
-- Returns:
-- row_num | transaction_ref     | transaction_type | amount   | balance_after | created_at
-- --------+---------------------+------------------+----------+---------------+-------------------
-- 1       | TXN-20250101-00001  | DEPOSIT          | 50000.00 | 50000.00      | 2025-01-01 ...
-- 2       | TXN-20250105-00002  | DEPOSIT          | 10000.00 | 60000.00      | 2025-01-05 ...
-- 3       | TXN-20250110-00003  | WITHDRAWAL       | 5000.00  | 55000.00      | 2025-01-10 ...
-- 4       | TXN-20250115-00004  | TRANSFER_OUT     | 3000.00  | 52000.00      | 2025-01-15 ...
-- 5       | TXN-20250120-00005  | INTEREST         | 400.00   | 52400.00      | 2025-01-20 ...
```

**Example — Get Only the Latest Transaction per Account (Top-1 per group):**

```sql
CREATE OR REPLACE FUNCTION get_latest_transaction_per_account()
RETURNS TABLE(
    account_number  VARCHAR,
    transaction_ref VARCHAR,
    transaction_type VARCHAR,
    amount          DECIMAL,
    balance_after   DECIMAL,
    created_at      TIMESTAMP
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT sub.account_number, sub.transaction_ref, sub.transaction_type,
           sub.amount, sub.balance_after, sub.created_at
    FROM (
        SELECT
            a.account_number,
            t.transaction_ref,
            t.transaction_type,
            t.amount,
            t.balance_after,
            t.created_at,
            ROW_NUMBER() OVER(PARTITION BY t.account_id ORDER BY t.created_at DESC) AS rn
        FROM transactions t
        JOIN accounts a ON t.account_id = a.account_id
    ) sub
    WHERE sub.rn = 1
    ORDER BY sub.account_number;
END;
$$;

-- Usage
SELECT * FROM get_latest_transaction_per_account();
-- Returns 1 row per account — the most recent transaction
```

---

#### 2. RANK() — Ranking with Gaps for Ties

Assigns a rank to each row. **Tied rows get the same rank**, and the next rank **skips** accordingly (1, 2, 2, 4 — not 1, 2, 2, 3).

```sql
RANK() OVER(PARTITION BY ... ORDER BY ...)
```

**Example — Rank Customers by Total Balance within Each Branch:**

```sql
CREATE OR REPLACE FUNCTION rank_customers_by_balance(p_branch_id INT DEFAULT NULL)
RETURNS TABLE(
    branch_name     VARCHAR,
    customer_name   TEXT,
    total_balance   DECIMAL,
    balance_rank    BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT
        b.branch_name,
        c.first_name || ' ' || c.last_name AS customer_name,
        SUM(a.balance) AS total_balance,
        RANK() OVER(
            PARTITION BY b.branch_id
            ORDER BY SUM(a.balance) DESC
        ) AS balance_rank
    FROM customers c
    JOIN accounts a ON c.customer_id = a.customer_id AND a.status = 'ACTIVE'
    JOIN branches b ON c.branch_id = b.branch_id
    WHERE (p_branch_id IS NULL OR b.branch_id = p_branch_id)
    GROUP BY b.branch_id, b.branch_name, c.customer_id, c.first_name, c.last_name
    ORDER BY b.branch_name, balance_rank;
END;
$$;

-- Usage: All branches
SELECT * FROM rank_customers_by_balance();
-- Returns:
-- branch_name            | customer_name   | total_balance | balance_rank
-- -----------------------+-----------------+---------------+-------------
-- Downtown Main Branch   | John Doe        | 70900.00      | 1
-- Downtown Main Branch   | Sarah Davis     | 15400.00      | 2
-- Lakefront Branch       | Lisa Taylor     | 28900.00      | 1
-- Lakefront Branch       | Robert Johnson  | 57750.00      | 1   -- tied if same
-- ...

-- Usage: Single branch
SELECT * FROM rank_customers_by_balance(1);
```

---

#### 3. DENSE_RANK() — Ranking Without Gaps

Like `RANK()`, but **no gaps** in the ranking sequence (1, 2, 2, 3 — not 1, 2, 2, 4).

```sql
DENSE_RANK() OVER(PARTITION BY ... ORDER BY ...)
```

**Example — RANK vs DENSE_RANK vs ROW_NUMBER Comparison:**

```sql
SELECT
    account_number,
    account_type,
    balance,
    ROW_NUMBER() OVER(ORDER BY balance DESC) AS row_num,      -- always unique: 1,2,3,4,5
    RANK()       OVER(ORDER BY balance DESC) AS rank_val,      -- gaps on tie:  1,2,2,4,5
    DENSE_RANK() OVER(ORDER BY balance DESC) AS dense_rank_val -- no gaps:      1,2,2,3,4
FROM accounts
WHERE status = 'ACTIVE'
ORDER BY balance DESC;
```

**Example — Top 3 Loan Amounts per Loan Type (using DENSE_RANK):**

```sql
CREATE OR REPLACE FUNCTION top_loans_per_type(p_top_n INT DEFAULT 3)
RETURNS TABLE(
    loan_type       VARCHAR,
    loan_number     VARCHAR,
    customer_name   TEXT,
    principal_amount DECIMAL,
    d_rank          BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT sub.loan_type, sub.loan_number, sub.customer_name, sub.principal_amount, sub.d_rank
    FROM (
        SELECT
            l.loan_type,
            l.loan_number,
            c.first_name || ' ' || c.last_name AS customer_name,
            l.principal_amount,
            DENSE_RANK() OVER(PARTITION BY l.loan_type ORDER BY l.principal_amount DESC) AS d_rank
        FROM loans l
        JOIN customers c ON l.customer_id = c.customer_id
    ) sub
    WHERE sub.d_rank <= p_top_n
    ORDER BY sub.loan_type, sub.d_rank;
END;
$$;

-- Usage
SELECT * FROM top_loans_per_type(2);
```

---

#### 4. SUM() / AVG() / COUNT() / MIN() / MAX() as Window Functions

Any aggregate function can become a window function by adding `OVER()`.

```sql
SUM(column) OVER(PARTITION BY ... ORDER BY ...)
```

**Example — Running Balance in Account Statement:**

```sql
CREATE OR REPLACE FUNCTION get_running_balance_statement(p_account_id INT)
RETURNS TABLE(
    txn_date         TIMESTAMP,
    transaction_ref  VARCHAR,
    transaction_type VARCHAR,
    credit           DECIMAL,
    debit            DECIMAL,
    running_balance  DECIMAL
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT
        t.created_at,
        t.transaction_ref,
        t.transaction_type,
        CASE WHEN t.transaction_type IN ('DEPOSIT','TRANSFER_IN','INTEREST','REVERSAL')
             THEN t.amount ELSE 0.00 END AS credit,
        CASE WHEN t.transaction_type IN ('WITHDRAWAL','TRANSFER_OUT','FEE')
             THEN t.amount ELSE 0.00 END AS debit,
        SUM(
            CASE WHEN t.transaction_type IN ('DEPOSIT','TRANSFER_IN','INTEREST','REVERSAL')
                 THEN t.amount
                 ELSE -t.amount
            END
        ) OVER(ORDER BY t.created_at, t.transaction_id) AS running_balance
    FROM transactions t
    WHERE t.account_id = p_account_id
    ORDER BY t.created_at, t.transaction_id;
END;
$$;

-- Usage
SELECT * FROM get_running_balance_statement(1);
-- Returns:
-- txn_date   | transaction_ref     | transaction_type | credit   | debit   | running_balance
-- -----------+---------------------+------------------+----------+---------+----------------
-- 2025-01-01 | TXN-20250101-00001  | DEPOSIT          | 50000.00 | 0.00    | 50000.00
-- 2025-01-05 | TXN-20250105-00002  | DEPOSIT          | 10000.00 | 0.00    | 60000.00
-- 2025-01-10 | TXN-20250110-00003  | WITHDRAWAL       | 0.00     | 5000.00 | 55000.00
-- 2025-01-15 | TXN-20250115-00004  | TRANSFER_OUT     | 0.00     | 3000.00 | 52000.00
-- 2025-01-20 | TXN-20250120-00005  | INTEREST         | 400.00   | 0.00    | 52400.00
```

**Example — Average Balance per Account Type with Deviation:**

```sql
SELECT
    account_number,
    account_type,
    balance,
    ROUND(AVG(balance) OVER(PARTITION BY account_type), 2) AS avg_balance_in_type,
    ROUND(balance - AVG(balance) OVER(PARTITION BY account_type), 2) AS deviation_from_avg,
    COUNT(*) OVER(PARTITION BY account_type) AS accounts_in_type,
    MIN(balance) OVER(PARTITION BY account_type) AS min_in_type,
    MAX(balance) OVER(PARTITION BY account_type) AS max_in_type
FROM accounts
WHERE status = 'ACTIVE';
```

---

#### 5. LAG() and LEAD() — Access Previous / Next Row

```sql
LAG(column, offset, default)  OVER(ORDER BY ...)  -- previous row value
LEAD(column, offset, default) OVER(ORDER BY ...)  -- next row value
```

| Parameter | Description |
|-----------|-------------|
| `column` | Column to read from adjacent row |
| `offset` | How many rows back (LAG) or forward (LEAD). Default: 1 |
| `default` | Value if no adjacent row exists. Default: NULL |

**Example — Transaction-to-Transaction Balance Change:**

```sql
CREATE OR REPLACE FUNCTION get_balance_changes(p_account_id INT)
RETURNS TABLE(
    transaction_ref  VARCHAR,
    transaction_type VARCHAR,
    amount           DECIMAL,
    balance_after    DECIMAL,
    prev_balance     DECIMAL,
    balance_change   DECIMAL,
    next_txn_type    VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT
        t.transaction_ref,
        t.transaction_type,
        t.amount,
        t.balance_after,
        LAG(t.balance_after, 1, 0.00) OVER(ORDER BY t.created_at)  AS prev_balance,
        t.balance_after - LAG(t.balance_after, 1, 0.00) OVER(ORDER BY t.created_at) AS balance_change,
        LEAD(t.transaction_type, 1, 'N/A') OVER(ORDER BY t.created_at) AS next_txn_type
    FROM transactions t
    WHERE t.account_id = p_account_id
    ORDER BY t.created_at;
END;
$$;

-- Usage
SELECT * FROM get_balance_changes(1);
-- Returns:
-- transaction_ref    | type         | amount   | balance_after | prev_balance | balance_change | next_txn_type
-- -------------------+--------------+----------+---------------+--------------+----------------+--------------
-- TXN-20250101-00001 | DEPOSIT      | 50000.00 | 50000.00      | 0.00         | +50000.00      | DEPOSIT
-- TXN-20250105-00002 | DEPOSIT      | 10000.00 | 60000.00      | 50000.00     | +10000.00      | WITHDRAWAL
-- TXN-20250110-00003 | WITHDRAWAL   | 5000.00  | 55000.00      | 60000.00     | -5000.00       | TRANSFER_OUT
-- TXN-20250115-00004 | TRANSFER_OUT | 3000.00  | 52000.00      | 55000.00     | -3000.00       | INTEREST
-- TXN-20250120-00005 | INTEREST     | 400.00   | 52400.00      | 52000.00     | +400.00        | N/A
```

---

#### 6. FIRST_VALUE() / LAST_VALUE() / NTH_VALUE()

```sql
FIRST_VALUE(column) OVER(PARTITION BY ... ORDER BY ...)    -- first row in window
LAST_VALUE(column)  OVER(PARTITION BY ... ORDER BY ... ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING)
NTH_VALUE(column, n) OVER(PARTITION BY ... ORDER BY ...)   -- nth row in window
```

**Example — First and Largest Transaction per Account:**

```sql
SELECT
    a.account_number,
    t.transaction_ref,
    t.transaction_type,
    t.amount,
    FIRST_VALUE(t.transaction_ref) OVER(
        PARTITION BY t.account_id ORDER BY t.created_at
    ) AS first_txn_ref,
    FIRST_VALUE(t.amount) OVER(
        PARTITION BY t.account_id ORDER BY t.amount DESC
    ) AS largest_txn_amount,
    LAST_VALUE(t.transaction_ref) OVER(
        PARTITION BY t.account_id ORDER BY t.created_at
        ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
    ) AS last_txn_ref
FROM transactions t
JOIN accounts a ON t.account_id = a.account_id
ORDER BY a.account_number, t.created_at;
```

> **Note:** `LAST_VALUE` requires the frame clause `ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING`. Without it, the default frame only extends to the current row, so `LAST_VALUE` would just return the current row's value.

---

#### 7. NTILE() — Divide Rows into Buckets

Distributes rows into `n` roughly equal groups and assigns a bucket number (1 to n).

```sql
NTILE(n) OVER(ORDER BY ...)
```

**Example — Divide Accounts into Quartiles by Balance:**

```sql
SELECT
    account_number,
    account_type,
    balance,
    NTILE(4) OVER(ORDER BY balance) AS balance_quartile,
    CASE NTILE(4) OVER(ORDER BY balance)
        WHEN 1 THEN 'LOW (Bottom 25%)'
        WHEN 2 THEN 'BELOW AVERAGE (25-50%)'
        WHEN 3 THEN 'ABOVE AVERAGE (50-75%)'
        WHEN 4 THEN 'HIGH (Top 25%)'
    END AS balance_category
FROM accounts
WHERE status = 'ACTIVE';
```

---

#### 8. PERCENT_RANK() and CUME_DIST()

```sql
PERCENT_RANK() OVER(ORDER BY ...)   -- relative rank: (rank - 1) / (total_rows - 1), range 0 to 1
CUME_DIST()    OVER(ORDER BY ...)   -- cumulative distribution: rows_up_to_current / total_rows
```

**Example — Percentile Ranking of Account Balances:**

```sql
SELECT
    account_number,
    balance,
    ROUND(PERCENT_RANK() OVER(ORDER BY balance)::NUMERIC, 4) AS pct_rank,
    ROUND(CUME_DIST()    OVER(ORDER BY balance)::NUMERIC, 4) AS cum_dist
FROM accounts
WHERE status = 'ACTIVE'
ORDER BY balance;
-- An account with pct_rank = 0.85 means 85% of accounts have a lower balance
```

---

### Window Functions Inside Stored Procedures

Window functions work the same inside procedures. Use them with `SELECT ... INTO` or inside `FOR` loops.

#### Example — Procedure to Flag Top-N Customers per Branch for Rewards

```sql
CREATE OR REPLACE PROCEDURE flag_top_customers_for_rewards(
    IN  p_top_n         INT DEFAULT 3,
    OUT p_flagged_count INT,
    OUT p_message       TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_rec RECORD;
BEGIN
    p_flagged_count := 0;

    FOR v_rec IN
        SELECT sub.customer_id, sub.customer_name, sub.branch_name,
               sub.total_balance, sub.branch_rank
        FROM (
            SELECT
                c.customer_id,
                c.first_name || ' ' || c.last_name AS customer_name,
                b.branch_name,
                SUM(a.balance) AS total_balance,
                RANK() OVER(
                    PARTITION BY c.branch_id
                    ORDER BY SUM(a.balance) DESC
                ) AS branch_rank
            FROM customers c
            JOIN accounts a ON c.customer_id = a.customer_id AND a.status = 'ACTIVE'
            JOIN branches b ON c.branch_id = b.branch_id
            WHERE c.status = 'ACTIVE'
            GROUP BY c.customer_id, c.first_name, c.last_name, c.branch_id, b.branch_name
        ) sub
        WHERE sub.branch_rank <= p_top_n
    LOOP
        -- Log reward flag
        INSERT INTO audit_log (table_name, operation, record_id, new_values)
        VALUES ('customers', 'REWARD_FLAG', v_rec.customer_id,
            jsonb_build_object(
                'customer', v_rec.customer_name,
                'branch', v_rec.branch_name,
                'total_balance', v_rec.total_balance,
                'rank', v_rec.branch_rank
            )
        );

        p_flagged_count := p_flagged_count + 1;

        RAISE NOTICE 'Reward: % (%) — Rank #% — $%',
            v_rec.customer_name, v_rec.branch_name, v_rec.branch_rank, v_rec.total_balance;
    END LOOP;

    COMMIT;
    p_message := 'Flagged ' || p_flagged_count || ' customers for rewards (top ' || p_top_n || ' per branch)';
END;
$$;

-- Usage
CALL flag_top_customers_for_rewards(2, NULL, NULL);
-- Returns:
-- p_flagged_count | p_message
-- ----------------+--------------------------------------------------
-- 10              | Flagged 10 customers for rewards (top 2 per branch)
```

#### Example — Procedure to Detect Unusual Transactions (Amount > 3× Average)

```sql
CREATE OR REPLACE PROCEDURE detect_unusual_transactions(
    IN  p_days_back      INT DEFAULT 30,
    OUT p_flagged_count  INT,
    OUT p_message        TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_rec RECORD;
BEGIN
    p_flagged_count := 0;

    FOR v_rec IN
        SELECT sub.*
        FROM (
            SELECT
                t.transaction_id,
                t.transaction_ref,
                a.account_number,
                t.transaction_type,
                t.amount,
                AVG(t.amount) OVER(PARTITION BY t.account_id) AS avg_amount,
                t.amount / NULLIF(AVG(t.amount) OVER(PARTITION BY t.account_id), 0) AS ratio,
                ROW_NUMBER() OVER(PARTITION BY t.account_id ORDER BY t.amount DESC) AS amount_rank
            FROM transactions t
            JOIN accounts a ON t.account_id = a.account_id
            WHERE t.created_at >= NOW() - (p_days_back || ' days')::INTERVAL
        ) sub
        WHERE sub.ratio > 3.0   -- more than 3× the average
    LOOP
        INSERT INTO audit_log (table_name, operation, record_id, new_values)
        VALUES ('transactions', 'UNUSUAL_ACTIVITY', v_rec.transaction_id,
            jsonb_build_object(
                'ref', v_rec.transaction_ref,
                'account', v_rec.account_number,
                'amount', v_rec.amount,
                'avg_amount', ROUND(v_rec.avg_amount, 2),
                'ratio', ROUND(v_rec.ratio, 2)
            )
        );

        p_flagged_count := p_flagged_count + 1;
    END LOOP;

    COMMIT;
    p_message := 'Flagged ' || p_flagged_count || ' unusual transactions in last ' || p_days_back || ' days';
END;
$$;

-- Usage
CALL detect_unusual_transactions(30, NULL, NULL);
-- Returns:
-- p_flagged_count | p_message
-- ----------------+----------------------------------------------------
-- 3               | Flagged 3 unusual transactions in last 30 days
```

### Window Function Quick Reference

| Function | Ties? | Gaps? | Returns | Example Output (4 rows, 2 tied) |
|----------|-------|-------|---------|-------------------------------|
| `ROW_NUMBER()` | Breaks ties arbitrarily | N/A | Unique integer | 1, 2, 3, 4 |
| `RANK()` | Same rank for ties | Yes | Integer with gaps | 1, 2, 2, 4 |
| `DENSE_RANK()` | Same rank for ties | No | Consecutive integer | 1, 2, 2, 3 |
| `NTILE(n)` | — | — | Bucket number (1..n) | 1, 1, 2, 2 |
| `PERCENT_RANK()` | — | — | 0.0 to 1.0 | 0.0, 0.33, 0.33, 1.0 |
| `CUME_DIST()` | — | — | 0.0 to 1.0 | 0.25, 0.75, 0.75, 1.0 |
| `LAG(col, n)` | — | — | Value from n rows before | prev row's value |
| `LEAD(col, n)` | — | — | Value from n rows after | next row's value |
| `FIRST_VALUE(col)` | — | — | First value in window | first row's value |
| `LAST_VALUE(col)` | — | — | Last value in window frame | last row's value |
| `NTH_VALUE(col, n)` | — | — | Nth value in window | nth row's value |
| `SUM/AVG/COUNT/MIN/MAX` | — | — | Aggregate over window | running or partition total |

---

## Packaging — Organizing Procedures and Functions

PostgreSQL **does not have a built-in package system** like Oracle's `CREATE PACKAGE`. However, there are several effective strategies to organize related procedures and functions together.

### Strategy 1: Schema-Based Packaging (Recommended)

Use **schemas** as packages. Each schema groups related functions and procedures.

```sql
-- Create "packages" (schemas)
CREATE SCHEMA IF NOT EXISTS pkg_accounts;
CREATE SCHEMA IF NOT EXISTS pkg_loans;
CREATE SCHEMA IF NOT EXISTS pkg_transfers;
CREATE SCHEMA IF NOT EXISTS pkg_reports;

-- Add functions/procedures to the schema
CREATE OR REPLACE FUNCTION pkg_accounts.get_balance(p_account_id INT)
RETURNS DECIMAL
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN (SELECT balance FROM public.accounts WHERE account_id = p_account_id);
END;
$$;

CREATE OR REPLACE PROCEDURE pkg_accounts.open_account(
    IN  p_customer_id   INT,
    IN  p_branch_id     INT,
    IN  p_account_type  VARCHAR,
    IN  p_initial_deposit DECIMAL,
    OUT p_account_id    INT,
    OUT p_message       TEXT
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO public.accounts (account_number, customer_id, branch_id, account_type, balance)
    VALUES ('ACC-' || LPAD(nextval('public.accounts_account_id_seq')::TEXT, 10, '0'),
            p_customer_id, p_branch_id, p_account_type, p_initial_deposit)
    RETURNING id INTO p_account_id;

    p_message := 'Account created';
    COMMIT;
END;
$$;

CREATE OR REPLACE PROCEDURE pkg_accounts.close_account(
    IN  p_account_id INT,
    OUT p_message    TEXT
)
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE public.accounts SET status = 'CLOSED', closed_at = NOW()
    WHERE account_id = p_account_id;
    p_message := 'Account closed';
    COMMIT;
END;
$$;

CREATE OR REPLACE FUNCTION pkg_accounts.is_active(p_account_id INT)
RETURNS BOOLEAN
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN (SELECT status = 'ACTIVE' FROM public.accounts WHERE account_id = p_account_id);
END;
$$;

-- Usage: Call with schema prefix (like package.function)
SELECT pkg_accounts.get_balance(1);                              -- 52400.00
SELECT pkg_accounts.is_active(1);                                -- TRUE
CALL pkg_accounts.open_account(1, 1, 'SAVINGS', 5000, NULL, NULL);
CALL pkg_accounts.close_account(11, NULL);
```

#### Full Schema-Based Package Example — pkg_transfers

```sql
CREATE SCHEMA IF NOT EXISTS pkg_transfers;

-- Validate a transfer before execution
CREATE OR REPLACE FUNCTION pkg_transfers.validate(
    p_from_id INT,
    p_to_id   INT,
    p_amount  DECIMAL
)
RETURNS TEXT
LANGUAGE plpgsql
AS $$
DECLARE
    v_from_status VARCHAR;
    v_from_balance DECIMAL;
BEGIN
    SELECT status, balance INTO v_from_status, v_from_balance
    FROM public.accounts WHERE account_id = p_from_id;

    IF NOT FOUND THEN RETURN 'Source account not found'; END IF;
    IF v_from_status != 'ACTIVE' THEN RETURN 'Source account is ' || v_from_status; END IF;
    IF v_from_balance < p_amount THEN RETURN 'Insufficient balance: ' || v_from_balance; END IF;

    IF NOT EXISTS (SELECT 1 FROM public.accounts WHERE account_id = p_to_id AND status = 'ACTIVE') THEN
        RETURN 'Destination account not found or inactive';
    END IF;

    RETURN 'VALID';
END;
$$;

-- Execute the transfer
CREATE OR REPLACE PROCEDURE pkg_transfers.execute(
    IN  p_from_id   INT,
    IN  p_to_id     INT,
    IN  p_amount    DECIMAL,
    OUT p_ref       VARCHAR,
    OUT p_message   TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_validation TEXT;
BEGIN
    -- Use the validate function from the same "package"
    v_validation := pkg_transfers.validate(p_from_id, p_to_id, p_amount);

    IF v_validation != 'VALID' THEN
        p_message := v_validation;
        RETURN;
    END IF;

    p_ref := 'TRF-' || TO_CHAR(NOW(), 'YYYYMMDD') || '-' || LPAD(nextval('public.transfers_transfer_id_seq')::TEXT, 5, '0');

    UPDATE public.accounts SET balance = balance - p_amount WHERE account_id = p_from_id;
    UPDATE public.accounts SET balance = balance + p_amount WHERE account_id = p_to_id;

    INSERT INTO public.transfers (transfer_ref, from_account_id, to_account_id, amount, status)
    VALUES (p_ref, p_from_id, p_to_id, p_amount, 'COMPLETED');

    COMMIT;
    p_message := 'Transfer complete. Ref: ' || p_ref;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_ref := NULL;
        p_message := 'Transfer failed: ' || SQLERRM;
END;
$$;

-- Get transfer history
CREATE OR REPLACE FUNCTION pkg_transfers.get_history(p_account_id INT)
RETURNS TABLE(transfer_ref VARCHAR, direction TEXT, other_account INT, amount DECIMAL, status VARCHAR, created_at TIMESTAMP)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT t.transfer_ref,
           CASE WHEN t.from_account_id = p_account_id THEN 'OUT' ELSE 'IN' END AS direction,
           CASE WHEN t.from_account_id = p_account_id THEN t.to_account_id ELSE t.from_account_id END,
           t.amount, t.status, t.created_at
    FROM public.transfers t
    WHERE t.from_account_id = p_account_id OR t.to_account_id = p_account_id
    ORDER BY t.created_at DESC;
END;
$$;

-- Usage — all calls prefixed with pkg_transfers.
SELECT pkg_transfers.validate(1, 2, 1000);          -- 'VALID'
CALL pkg_transfers.execute(1, 2, 1000, NULL, NULL);  -- Transfer complete
SELECT * FROM pkg_transfers.get_history(1);           -- All transfers for account 1
```

### Strategy 2: Naming Convention-Based Packaging

If schemas feel heavy, use a **prefix naming convention** to simulate packages.

```sql
-- Package: accounts
CREATE OR REPLACE FUNCTION accounts__get_balance(p_account_id INT) RETURNS DECIMAL ...;
CREATE OR REPLACE PROCEDURE accounts__open(...) ...;
CREATE OR REPLACE PROCEDURE accounts__close(...) ...;
CREATE OR REPLACE FUNCTION accounts__is_active(p_account_id INT) RETURNS BOOLEAN ...;

-- Package: loans
CREATE OR REPLACE FUNCTION loans__calculate_emi(...) RETURNS DECIMAL ...;
CREATE OR REPLACE PROCEDURE loans__disburse(...) ...;
CREATE OR REPLACE PROCEDURE loans__process_payment(...) ...;
CREATE OR REPLACE FUNCTION loans__get_outstanding(p_loan_id INT) RETURNS DECIMAL ...;

-- Package: reports
CREATE OR REPLACE FUNCTION reports__trial_balance(...) RETURNS TABLE(...) ...;
CREATE OR REPLACE FUNCTION reports__branch_summary(...) RETURNS TABLE(...) ...;

-- Usage
SELECT accounts__get_balance(1);
CALL loans__disburse(1, 10, NULL, NULL, NULL, NULL);
SELECT * FROM reports__branch_summary(1);
```

### Strategy 3: Extension-Based Packaging (Advanced)

For distributable, versioned packages, create a **PostgreSQL extension**.

```
my_banking_pkg/
├── my_banking_pkg.control        -- extension metadata
├── my_banking_pkg--1.0.sql       -- initial version
└── my_banking_pkg--1.0--1.1.sql  -- upgrade script
```

**my_banking_pkg.control:**
```
# Banking package extension
comment = 'Banking system stored procedures and functions'
default_version = '1.0'
relocatable = true
```

**my_banking_pkg--1.0.sql:**
```sql
-- compliant with CREATE EXTENSION

CREATE OR REPLACE FUNCTION get_account_balance(p_account_id INT)
RETURNS DECIMAL LANGUAGE plpgsql AS $$
BEGIN
    RETURN (SELECT balance FROM accounts WHERE account_id = p_account_id);
END;
$$;

CREATE OR REPLACE PROCEDURE transfer_funds(
    IN p_from INT, IN p_to INT, IN p_amount DECIMAL,
    OUT p_message TEXT
) LANGUAGE plpgsql AS $$
BEGIN
    UPDATE accounts SET balance = balance - p_amount WHERE account_id = p_from;
    UPDATE accounts SET balance = balance + p_amount WHERE account_id = p_to;
    COMMIT;
    p_message := 'Done';
END;
$$;

-- ... more functions/procedures
```

**Install and use:**
```sql
CREATE EXTENSION my_banking_pkg;

-- All functions available directly
SELECT get_account_balance(1);
CALL transfer_funds(1, 2, 500, NULL);

-- Upgrade later
ALTER EXTENSION my_banking_pkg UPDATE TO '1.1';

-- Remove
DROP EXTENSION my_banking_pkg;
```

### Packaging Strategy Comparison

| Approach | Namespace | Versioning | Ease of Setup | Best For |
|----------|-----------|------------|---------------|----------|
| **Schema-based** | `pkg_name.func()` | Manual (via migration scripts) | Easy | Most projects |
| **Naming convention** | `pkg__func()` | Manual | Easiest | Small projects |
| **Extension** | `func()` (installed globally) | Built-in (`ALTER EXTENSION UPDATE`) | Complex | Distributable libraries |

---

## Default IN Parameters — Rules and Restrictions

PostgreSQL allows you to assign **default values** to `IN` parameters in both functions and procedures. However, there is a strict rule about parameter ordering when you mix default and non-default parameters, especially with `OUT` parameters.

### Basic Syntax — IN Parameters with Defaults

```sql
CREATE OR REPLACE PROCEDURE log_activity(
    IN p_account_id   INT,
    IN p_action        TEXT,
    IN p_description   TEXT DEFAULT 'No description provided',
    IN p_performed_by  INT DEFAULT NULL
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO transactions (account_id, transaction_type, description, performed_by)
    VALUES (p_account_id, p_action, p_description, p_performed_by);
    COMMIT;
END;
$$;

-- Call with all parameters
CALL log_activity(1, 'AUDIT', 'Manual review completed', 5);

-- Call using defaults for p_description and p_performed_by
CALL log_activity(1, 'AUDIT');

-- Call using named parameter to skip p_description but provide p_performed_by
CALL log_activity(1, 'AUDIT', p_performed_by => 5);
```

### The Strict Rule — No OUT Parameters After Defaulted IN Parameters

PostgreSQL enforces a strict rule: **you cannot have `OUT` parameters after `IN` parameters that have default values**. Once an `IN` parameter has a default, all subsequent `IN` parameters must also have defaults. `OUT` parameters cannot follow defaulted `IN` parameters.

#### This Will FAIL

```sql
-- ERROR: procedure parameter "p_message" is an OUT parameter
--        but follows IN parameter "p_description" which has a default value
CREATE OR REPLACE PROCEDURE bad_example(
    IN  p_account_id  INT,
    IN  p_amount      DECIMAL,
    IN  p_description TEXT DEFAULT 'Cash Deposit',   -- IN with default
    OUT p_message     TEXT                            -- OUT after defaulted IN — NOT ALLOWED
)
LANGUAGE plpgsql
AS $$
BEGIN
    p_message := 'Done';
END;
$$;
```

The error is clear: once you have an `IN` parameter with a default value, all subsequent `IN` parameters must also have defaults. PostgreSQL does not allow `OUT` parameters to appear after defaulted `IN` parameters because it creates ambiguity in how arguments are resolved during the `CALL`.

### Solution 1 — Place OUT Parameters Before Defaulted IN Parameters

Move all `OUT` parameters before any `IN` parameter that has a default value.

```sql
CREATE OR REPLACE PROCEDURE deposit_with_defaults(
    IN  p_account_id  INT,
    IN  p_amount      DECIMAL,
    OUT p_txn_ref     VARCHAR,              -- OUT params come first
    OUT p_new_balance DECIMAL,
    OUT p_message     TEXT,
    IN  p_description TEXT DEFAULT 'Cash Deposit',    -- Defaulted IN params come last
    IN  p_performed_by INT DEFAULT NULL
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_old_balance DECIMAL;
BEGIN
    SELECT balance INTO v_old_balance FROM accounts WHERE id = p_account_id;

    IF NOT FOUND THEN
        p_message := 'Account not found';
        RETURN;
    END IF;

    p_txn_ref := 'TXN-' || TO_CHAR(NOW(), 'YYYYMMDD') || '-' || LPAD(nextval('transactions_id_seq')::TEXT, 5, '0');
    p_new_balance := v_old_balance + p_amount;

    UPDATE accounts SET balance = p_new_balance WHERE id = p_account_id;

    INSERT INTO transactions (transaction_ref, account_id, transaction_type, amount, balance_before, balance_after, description, performed_by)
    VALUES (p_txn_ref, p_account_id, 'DEPOSIT', p_amount, v_old_balance, p_new_balance, p_description, p_performed_by);

    COMMIT;
    p_message := 'Deposit successful. New balance: ' || p_new_balance;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_message := 'Deposit failed: ' || SQLERRM;
END;
$$;

-- Call using defaults (no need to pass description or performed_by)
CALL deposit_with_defaults(1, 5000.00, NULL, NULL, NULL);

-- Call with named parameters to override defaults
CALL deposit_with_defaults(1, 5000.00, NULL, NULL, NULL, p_description => 'Salary credit', p_performed_by => 5);
```

### Solution 2 — Remove Defaults and Pass All Arguments Explicitly

If parameter ordering becomes confusing, simply remove default values and always pass every argument.

```sql
CREATE OR REPLACE PROCEDURE deposit_explicit(
    IN  p_account_id   INT,
    IN  p_amount       DECIMAL,
    IN  p_description  TEXT,
    IN  p_performed_by INT,
    OUT p_txn_ref      VARCHAR,
    OUT p_new_balance  DECIMAL,
    OUT p_message      TEXT
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- Same logic as above
    p_message := 'Deposit processed';
END;
$$;

-- Must always pass all IN parameters — no defaults
CALL deposit_explicit(1, 5000.00, 'Salary credit', 5, NULL, NULL, NULL);
```

### Parameter Ordering Rules — Summary

| Rule | Example | Valid? |
|------|---------|--------|
| IN without default, then OUT | `(IN a INT, OUT b TEXT)` | Yes |
| IN with default, then more IN with defaults | `(IN a INT DEFAULT 1, IN b INT DEFAULT 2)` | Yes |
| IN with default, then IN without default | `(IN a INT DEFAULT 1, IN b INT)` | No |
| IN with default, then OUT | `(IN a INT DEFAULT 1, OUT b TEXT)` | No |
| OUT first, then IN with default | `(OUT b TEXT, IN a INT DEFAULT 1)` | Yes |
| Mixed: IN, OUT, then IN with default | `(IN a INT, OUT b TEXT, IN c INT DEFAULT 1)` | Yes |

### Best Practice

When designing procedures with both `OUT` parameters and `IN` parameters that have defaults:

1. **Put required IN parameters first** (no defaults)
2. **Put OUT parameters next**
3. **Put optional IN parameters last** (with defaults)

```sql
-- Recommended parameter order
CREATE OR REPLACE PROCEDURE recommended_order(
    IN  p_required_1   INT,          -- 1. Required IN params
    IN  p_required_2   DECIMAL,
    OUT p_result        TEXT,         -- 2. OUT params
    OUT p_status        BOOLEAN,
    IN  p_optional_1   TEXT DEFAULT 'default',  -- 3. Optional IN params (with defaults)
    IN  p_optional_2   INT DEFAULT 0
)
LANGUAGE plpgsql
AS $$
BEGIN
    p_result := 'Processed with: ' || p_optional_1;
    p_status := TRUE;
END;
$$;

-- Call with defaults
CALL recommended_order(1, 100.00, NULL, NULL);

-- Call overriding one default using named parameter
CALL recommended_order(1, 100.00, NULL, NULL, p_optional_1 => 'custom value');
```

---

## Best Practices

### For Functions

1. **Keep functions pure when possible** — avoid side effects
2. **Use appropriate return types** — RETURNS TABLE for multiple rows, scalar for single values
3. **Handle NULL values** — always consider NULL inputs
4. **Use IMMUTABLE, STABLE, or VOLATILE** appropriately for optimization
5. **Keep functions focused** — one function, one purpose

### For Stored Procedures

1. **Use for transaction-critical operations** — leverage COMMIT/ROLLBACK
2. **Implement proper error handling** — always use EXCEPTION blocks for critical operations
3. **Use OUT parameters wisely** — return status + message for caller feedback
4. **Batch commits for large operations** — avoid long-running transactions (commit every N rows)
5. **Validate inputs early** — check parameters before processing; RETURN early on failure
6. **Use nested BEGIN...EXCEPTION...END** — so one row's failure doesn't abort the whole batch

### General

1. **Use meaningful names** — `transfer_funds`, not `proc1`
2. **Return structured feedback** — always provide `OUT p_success BOOLEAN, OUT p_message TEXT` or similar
3. **Log with RAISE NOTICE** — for debugging and monitoring
4. **Use cursors only when needed** — `FOR...IN...LOOP` is simpler for most cases
5. **Never mix business logic and transaction control** — keep validation separate from commits

---

## Dropping Functions and Procedures

```sql
-- Drop function (must specify parameter types if overloaded)
DROP FUNCTION IF EXISTS get_account_balance(INT);

-- Drop procedure
DROP PROCEDURE IF EXISTS transfer_funds(INT, INT, DECIMAL, TEXT, INT);
```

---

## Quick Reference — Calling and Capturing Results

| Technique | Syntax |
|-----------|--------|
| Call procedure directly | `CALL transfer_funds(1, 2, 500, 'test', 5, NULL, NULL, NULL);` — OUT params returned as result row |
| Capture in DO block | `DO $$ DECLARE v_ref VARCHAR; v_fee DECIMAL; v_msg TEXT; BEGIN CALL transfer_funds(1,2,500,'test',5,v_ref,v_fee,v_msg); RAISE NOTICE '%', v_msg; END; $$;` |
| Call function in SELECT | `SELECT calculate_emi(100000, 9.0, 12);` |
| Call table function | `SELECT * FROM get_customer_accounts(1);` |
| Use function in WHERE | `SELECT * FROM accounts WHERE get_account_balance(account_id) > 10000;` |
