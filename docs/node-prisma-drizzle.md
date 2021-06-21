# Node.js ORMs: Prisma vs Drizzle - Complete Guide

## Table of Contents
1. [Prisma ORM](#prisma-orm)
2. [Drizzle ORM](#drizzle-orm)
3. [Prisma vs Drizzle Comparison](#prisma-vs-drizzle-comparison)

---

## Prisma ORM

### What is Prisma?

Prisma is a next-generation ORM (Object-Relational Mapping) for Node.js and TypeScript. It provides a type-safe database client that makes database access easy with an auto-generated query builder.

### Installation

```bash
npm install prisma --save-dev
npm install @prisma/client
npx prisma init
```

### Core Components

1. **Prisma Schema** - Declarative data modeling
2. **Prisma Client** - Auto-generated type-safe query builder
3. **Prisma Migrate** - Migration system
4. **Prisma Studio** - GUI for database

---

### 1. Models (Tables)

Models represent database tables in Prisma schema.

#### Prisma Schema Example:
```prisma
// schema.prisma

generator client {
  provider = "prisma-client-js"
}

datasource db {
  provider = "postgresql"
  url      = env("DATABASE_URL")
}

model User {
  id        Int      @id @default(autoincrement())
  email     String   @unique
  name      String?
  createdAt DateTime @default(now())
  updatedAt DateTime @updatedAt
}
```

#### Resulting SQL (PostgreSQL):
```sql
CREATE TABLE "User" (
    "id" SERIAL PRIMARY KEY,
    "email" TEXT UNIQUE NOT NULL,
    "name" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL
);
```

---

### 2. Auto Increment

Auto-increment fields automatically generate sequential values.

#### Prisma Schema:
```prisma
model Product {
  id          Int    @id @default(autoincrement())
  productCode String @unique @default(cuid())
  name        String
}
```

#### SQL Equivalent:
```sql
CREATE TABLE "Product" (
    "id" SERIAL PRIMARY KEY,
    "productCode" TEXT UNIQUE NOT NULL,
    "name" TEXT NOT NULL
);
```

**Note:** `@default(autoincrement())` uses database-native auto-increment (SERIAL in PostgreSQL, AUTO_INCREMENT in MySQL).

---

### 3. Unique Keys

Unique constraints ensure column values are unique across all rows.

#### Single Column Unique:
```prisma
model User {
  id       Int    @id @default(autoincrement())
  email    String @unique
  username String @unique
}
```

#### Composite Unique (Multiple Columns):
```prisma
model UserProfile {
  userId   Int
  platform String
  handle   String
  
  @@unique([userId, platform])
}
```

#### SQL Equivalent:
```sql
-- Single column unique
CREATE TABLE "User" (
    "id" SERIAL PRIMARY KEY,
    "email" TEXT UNIQUE NOT NULL,
    "username" TEXT UNIQUE NOT NULL
);

-- Composite unique
CREATE TABLE "UserProfile" (
    "userId" INTEGER NOT NULL,
    "platform" TEXT NOT NULL,
    "handle" TEXT NOT NULL,
    CONSTRAINT "UserProfile_userId_platform_key" UNIQUE ("userId", "platform")
);
```

---

### 4. Foreign Keys & Relations

Foreign keys establish relationships between tables.

#### One-to-Many Relationship:
```prisma
model User {
  id    Int    @id @default(autoincrement())
  email String @unique
  posts Post[]
}

model Post {
  id       Int    @id @default(autoincrement())
  title    String
  content  String?
  authorId Int
  author   User   @relation(fields: [authorId], references: [id])
}
```

#### SQL Equivalent:
```sql
CREATE TABLE "User" (
    "id" SERIAL PRIMARY KEY,
    "email" TEXT UNIQUE NOT NULL
);

CREATE TABLE "Post" (
    "id" SERIAL PRIMARY KEY,
    "title" TEXT NOT NULL,
    "content" TEXT,
    "authorId" INTEGER NOT NULL,
    CONSTRAINT "Post_authorId_fkey" FOREIGN KEY ("authorId") 
        REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE
);
```

#### One-to-One Relationship:
```prisma
model User {
  id      Int      @id @default(autoincrement())
  email   String   @unique
  profile Profile?
}

model Profile {
  id     Int    @id @default(autoincrement())
  bio    String
  userId Int    @unique
  user   User   @relation(fields: [userId], references: [id])
}
```

#### SQL Equivalent:
```sql
CREATE TABLE "User" (
    "id" SERIAL PRIMARY KEY,
    "email" TEXT UNIQUE NOT NULL
);

CREATE TABLE "Profile" (
    "id" SERIAL PRIMARY KEY,
    "bio" TEXT NOT NULL,
    "userId" INTEGER UNIQUE NOT NULL,
    CONSTRAINT "Profile_userId_fkey" FOREIGN KEY ("userId") 
        REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE
);
```

#### Many-to-Many Relationship:
```prisma
model Post {
  id         Int        @id @default(autoincrement())
  title      String
  categories Category[]
}

model Category {
  id    Int    @id @default(autoincrement())
  name  String @unique
  posts Post[]
}
```

This creates an implicit join table:

#### SQL Equivalent:
```sql
CREATE TABLE "Post" (
    "id" SERIAL PRIMARY KEY,
    "title" TEXT NOT NULL
);

CREATE TABLE "Category" (
    "id" SERIAL PRIMARY KEY,
    "name" TEXT UNIQUE NOT NULL
);

-- Implicit join table created by Prisma
CREATE TABLE "_CategoryToPost" (
    "A" INTEGER NOT NULL,
    "B" INTEGER NOT NULL,
    CONSTRAINT "_CategoryToPost_A_fkey" FOREIGN KEY ("A") REFERENCES "Category"("id") ON DELETE CASCADE,
    CONSTRAINT "_CategoryToPost_B_fkey" FOREIGN KEY ("B") REFERENCES "Post"("id") ON DELETE CASCADE
);

CREATE UNIQUE INDEX "_CategoryToPost_AB_unique" ON "_CategoryToPost"("A", "B");
CREATE INDEX "_CategoryToPost_B_index" ON "_CategoryToPost"("B");
```

#### Explicit Many-to-Many (with extra fields):
```prisma
model Post {
  id              Int              @id @default(autoincrement())
  title           String
  categoriesOnPosts CategoriesOnPosts[]
}

model Category {
  id              Int              @id @default(autoincrement())
  name            String
  categoriesOnPosts CategoriesOnPosts[]
}

model CategoriesOnPosts {
  post       Post     @relation(fields: [postId], references: [id])
  postId     Int
  category   Category @relation(fields: [categoryId], references: [id])
  categoryId Int
  assignedAt DateTime @default(now())

  @@id([postId, categoryId])
}
```

---

### 5. Indexes

Indexes improve query performance on frequently searched columns.

#### Single Column Index:
```prisma
model User {
  id    Int    @id @default(autoincrement())
  email String @unique
  name  String

  @@index([name])
}
```

#### Composite Index:
```prisma
model Post {
  id        Int      @id @default(autoincrement())
  title     String
  status    String
  createdAt DateTime @default(now())

  @@index([status, createdAt])
}
```

#### Unique Index (alternative to @unique):
```prisma
model Product {
  id  Int    @id @default(autoincrement())
  sku String

  @@unique([sku], name: "unique_sku")
}
```

#### SQL Equivalent:
```sql
-- Single column index
CREATE INDEX "User_name_idx" ON "User"("name");

-- Composite index
CREATE INDEX "Post_status_createdAt_idx" ON "Post"("status", "createdAt");

-- Unique index
CREATE UNIQUE INDEX "unique_sku" ON "Product"("sku");
```

---

### 6. Cascade Delete & Update

Control what happens when related records are deleted or updated.

```prisma
model User {
  id    Int    @id @default(autoincrement())
  email String @unique
  posts Post[]
}

model Post {
  id       Int    @id @default(autoincrement())
  title    String
  authorId Int
  author   User   @relation(fields: [authorId], references: [id], onDelete: Cascade, onUpdate: Cascade)
}
```

**Options:**
- `Cascade` - Delete/update related records
- `Restrict` - Prevent deletion if related records exist
- `NoAction` - Similar to Restrict
- `SetNull` - Set foreign key to NULL
- `SetDefault` - Set foreign key to default value

#### SQL Equivalent:
```sql
CREATE TABLE "Post" (
    "id" SERIAL PRIMARY KEY,
    "title" TEXT NOT NULL,
    "authorId" INTEGER NOT NULL,
    CONSTRAINT "Post_authorId_fkey" FOREIGN KEY ("authorId") 
        REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE
);
```

---

### 7. Enums

Define a set of predefined values.

```prisma
enum Role {
  USER
  ADMIN
  MODERATOR
}

model User {
  id    Int    @id @default(autoincrement())
  email String @unique
  role  Role   @default(USER)
}
```

#### SQL Equivalent (PostgreSQL):
```sql
CREATE TYPE "Role" AS ENUM ('USER', 'ADMIN', 'MODERATOR');

CREATE TABLE "User" (
    "id" SERIAL PRIMARY KEY,
    "email" TEXT UNIQUE NOT NULL,
    "role" "Role" NOT NULL DEFAULT 'USER'
);
```

---

### 8. Default Values

Set default values for fields.

```prisma
model Post {
  id         Int      @id @default(autoincrement())
  title      String
  published  Boolean  @default(false)
  views      Int      @default(0)
  createdAt  DateTime @default(now())
  uuid       String   @default(uuid())
  slug       String   @default(cuid())
}
```

#### SQL Equivalent:
```sql
CREATE TABLE "Post" (
    "id" SERIAL PRIMARY KEY,
    "title" TEXT NOT NULL,
    "published" BOOLEAN NOT NULL DEFAULT false,
    "views" INTEGER NOT NULL DEFAULT 0,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "uuid" TEXT NOT NULL,
    "slug" TEXT NOT NULL
);
```

---

### 9. Prisma Client Usage

```javascript
// After running: npx prisma generate

const { PrismaClient } = require('@prisma/client');
const prisma = new PrismaClient();

// Create
const user = await prisma.user.create({
  data: {
    email: 'alice@example.com',
    name: 'Alice',
    posts: {
      create: [
        { title: 'First Post', content: 'Hello World' }
      ]
    }
  },
  include: { posts: true }
});

// Read
const users = await prisma.user.findMany({
  where: { email: { contains: '@example.com' } },
  include: { posts: true },
  orderBy: { createdAt: 'desc' }
});

// Update
const updatedUser = await prisma.user.update({
  where: { id: 1 },
  data: { name: 'Alice Updated' }
});

// Delete
await prisma.user.delete({
  where: { id: 1 }
});

// Complex query
const result = await prisma.user.findMany({
  where: {
    posts: {
      some: {
        published: true
      }
    }
  },
  include: {
    posts: {
      where: { published: true },
      orderBy: { createdAt: 'desc' },
      take: 5
    }
  }
});
```

---

### 10. Understanding the `include` Option

The `include` option in Prisma allows you to fetch related data along with the main record. Without `include`, Prisma only returns the fields from the queried table.

#### Without vs With `include`:

```javascript
// WITHOUT include - only gets user fields
const user = await prisma.user.create({
  data: { email: 'alice@example.com' }
});
// Returns: { id: 1, email: 'alice@example.com' }

// WITH include - gets user + related posts
const userWithPosts = await prisma.user.create({
  data: { email: 'alice@example.com' },
  include: { posts: true }
});
// Returns: {
//   id: 1,
//   email: 'alice@example.com',
//   posts: []  // Empty array since no posts exist yet
// }
```

#### Creating with Nested Data and Include:

```javascript
const user = await prisma.user.create({
  data: {
    email: 'alice@example.com',
    posts: {
      create: [
        { title: 'First Post', content: 'Hello World' },
        { title: 'Second Post', content: 'Another post' }
      ]
    }
  },
  include: { posts: true }
});
// Returns: {
//   id: 1,
//   email: 'alice@example.com',
//   posts: [
//     { id: 1, title: 'First Post', content: 'Hello World', authorId: 1 },
//     { id: 2, title: 'Second Post', content: 'Another post', authorId: 1 }
//   ]
// }
```

---

### 11. Multiple Relations in a Single Table

When a model has multiple relations (e.g., User has posts, feedback, comments), you can include any combination of them.

#### Schema Example with Multiple Relations:

```prisma
model User {
  id        Int        @id @default(autoincrement())
  email     String     @unique
  name      String?
  posts     Post[]
  feedbacks Feedback[]
  comments  Comment[]
  profile   Profile?
}

model Post {
  id       Int    @id @default(autoincrement())
  title    String
  authorId Int
  author   User   @relation(fields: [authorId], references: [id])
}

model Feedback {
  id        Int      @id @default(autoincrement())
  message   String
  rating    Int
  userId    Int
  user      User     @relation(fields: [userId], references: [id])
  createdAt DateTime @default(now())
}

model Comment {
  id        Int      @id @default(autoincrement())
  content   String
  userId    Int
  user      User     @relation(fields: [userId], references: [id])
  createdAt DateTime @default(now())
}

model Profile {
  id     Int    @id @default(autoincrement())
  bio    String
  userId Int    @unique
  user   User   @relation(fields: [userId], references: [id])
}
```

#### Include Single Relation:

```javascript
const user = await prisma.user.findUnique({
  where: { id: 1 },
  include: { posts: true }
});
// Returns user with posts only
// { id: 1, email: '...', posts: [...] }
```

#### Include Multiple Relations:

```javascript
const user = await prisma.user.findUnique({
  where: { id: 1 },
  include: {
    posts: true,
    feedbacks: true
  }
});
// Returns: {
//   id: 1,
//   email: 'alice@example.com',
//   posts: [{ id: 1, title: 'First Post', ... }],
//   feedbacks: [{ id: 1, message: 'Great service!', rating: 5, ... }]
// }
```

#### Include All Relations:

```javascript
const user = await prisma.user.findUnique({
  where: { id: 1 },
  include: {
    posts: true,
    feedbacks: true,
    comments: true,
    profile: true
  }
});
// Returns user with all related data
// {
//   id: 1,
//   email: 'alice@example.com',
//   posts: [...],
//   feedbacks: [...],
//   comments: [...],
//   profile: { id: 1, bio: '...', userId: 1 }
// }
```

#### Filtered and Ordered Includes:

```javascript
const user = await prisma.user.findUnique({
  where: { id: 1 },
  include: {
    // Get only published posts, ordered by date
    posts: {
      where: { published: true },
      orderBy: { createdAt: 'desc' },
      take: 5
    },
    // Get only high-rating feedbacks
    feedbacks: {
      where: { rating: { gte: 4 } },
      orderBy: { createdAt: 'desc' }
    },
    // Get recent comments
    comments: {
      orderBy: { createdAt: 'desc' },
      take: 10
    },
    profile: true
  }
});
```

#### Nested Includes (Relations of Relations):

```javascript
// If Post has comments relation
const user = await prisma.user.findUnique({
  where: { id: 1 },
  include: {
    posts: {
      include: {
        comments: true  // Include comments on each post
      }
    },
    feedbacks: true
  }
});
// Returns: {
//   id: 1,
//   email: 'alice@example.com',
//   posts: [
//     {
//       id: 1,
//       title: 'First Post',
//       comments: [{ id: 1, content: 'Nice post!', ... }]
//     }
//   ],
//   feedbacks: [...]
// }
```

#### Using `select` with `include`:

```javascript
// Select specific fields AND include relations
const user = await prisma.user.findUnique({
  where: { id: 1 },
  select: {
    id: true,
    email: true,
    // Cannot use both select and include at same level
    // Use select with nested relations instead
    posts: {
      select: {
        id: true,
        title: true
      }
    },
    feedbacks: {
      select: {
        message: true,
        rating: true
      }
    }
  }
});
// Returns: {
//   id: 1,
//   email: 'alice@example.com',
//   posts: [{ id: 1, title: 'First Post' }],
//   feedbacks: [{ message: 'Great!', rating: 5 }]
// }
```

#### Create with Multiple Relations:

```javascript
const user = await prisma.user.create({
  data: {
    email: 'bob@example.com',
    name: 'Bob',
    posts: {
      create: [
        { title: 'My First Post' }
      ]
    },
    feedbacks: {
      create: [
        { message: 'Love the platform!', rating: 5 }
      ]
    },
    profile: {
      create: { bio: 'Software Developer' }
    }
  },
  include: {
    posts: true,
    feedbacks: true,
    profile: true
  }
});
```

---

## Drizzle ORM

### What is Drizzle?

Drizzle is a lightweight, type-safe ORM for TypeScript. It's designed to be a thin layer over SQL with maximum type safety and minimal runtime overhead.

### Installation

```bash
npm install drizzle-orm
npm install -D drizzle-kit

# Database driver (choose one)
npm install postgres      # PostgreSQL
npm install mysql2        # MySQL
npm install better-sqlite3 # SQLite
```

---

### Table Definition Syntax: pgTable vs mysqlTable

Drizzle uses different table functions for each database. Each has its own import path and database-specific column types.

#### Import Paths:

```typescript
// PostgreSQL
import { pgTable, serial, text, integer, boolean, timestamp, varchar, pgEnum } from 'drizzle-orm/pg-core';

// MySQL
import { mysqlTable, serial, text, int, boolean, timestamp, varchar, mysqlEnum } from 'drizzle-orm/mysql-core';
```

---

#### pgTable Syntax (PostgreSQL):

```typescript
import {
  pgTable,
  serial,
  bigserial,
  text,
  varchar,
  integer,
  bigint,
  boolean,
  timestamp,
  date,
  time,
  json,
  jsonb,
  uuid,
  decimal,
  doublePrecision,
  real,
  pgEnum,
  primaryKey,
  unique,
  index
} from 'drizzle-orm/pg-core';

// Define enum (PostgreSQL native enum)
export const roleEnum = pgEnum('role', ['user', 'admin', 'moderator']);
export const statusEnum = pgEnum('status', ['active', 'inactive', 'pending']);

// Define table
export const users = pgTable('users', {
  // Primary key options
  id: serial('id').primaryKey(),                              // SERIAL (auto-increment)
  // id: bigserial('id', { mode: 'number' }).primaryKey(),    // BIGSERIAL
  // id: uuid('id').defaultRandom().primaryKey(),             // UUID with auto-generate

  // String types
  email: varchar('email', { length: 255 }).notNull().unique(),
  name: text('name'),                                          // Unlimited length
  username: varchar('username', { length: 50 }).notNull(),     // Limited length

  // Number types
  age: integer('age'),
  balance: bigint('balance', { mode: 'number' }),              // For large numbers
  price: decimal('price', { precision: 10, scale: 2 }),        // Exact decimal
  rating: doublePrecision('rating'),                           // Floating point
  score: real('score'),                                        // Single precision float

  // Boolean
  isActive: boolean('is_active').default(true).notNull(),

  // Date/Time types
  createdAt: timestamp('created_at').defaultNow().notNull(),
  updatedAt: timestamp('updated_at').defaultNow().notNull(),
  birthDate: date('birth_date'),
  loginTime: time('login_time'),

  // JSON types
  metadata: json('metadata'),                                  // JSON (text storage)
  settings: jsonb('settings'),                                 // JSONB (binary, indexed)

  // Enum
  role: roleEnum('role').default('user').notNull(),
  status: statusEnum('status').default('active'),

  // UUID
  uniqueCode: uuid('unique_code').defaultRandom(),

}, (table) => ({
  // Composite primary key (alternative)
  // pk: primaryKey({ columns: [table.id, table.uniqueCode] }),

  // Indexes
  emailIdx: index('email_idx').on(table.email),
  nameStatusIdx: index('name_status_idx').on(table.name, table.status),

  // Unique constraints
  usernameUnique: unique('username_unique').on(table.username),
  emailRoleUnique: unique('email_role_unique').on(table.email, table.role),
}));
```

#### SQL Generated (PostgreSQL):
```sql
CREATE TYPE "role" AS ENUM ('user', 'admin', 'moderator');
CREATE TYPE "status" AS ENUM ('active', 'inactive', 'pending');

CREATE TABLE "users" (
    "id" SERIAL PRIMARY KEY,
    "email" VARCHAR(255) NOT NULL UNIQUE,
    "name" TEXT,
    "username" VARCHAR(50) NOT NULL,
    "age" INTEGER,
    "balance" BIGINT,
    "price" DECIMAL(10, 2),
    "rating" DOUBLE PRECISION,
    "score" REAL,
    "is_active" BOOLEAN NOT NULL DEFAULT true,
    "created_at" TIMESTAMP NOT NULL DEFAULT NOW(),
    "updated_at" TIMESTAMP NOT NULL DEFAULT NOW(),
    "birth_date" DATE,
    "login_time" TIME,
    "metadata" JSON,
    "settings" JSONB,
    "role" "role" NOT NULL DEFAULT 'user',
    "status" "status" DEFAULT 'active',
    "unique_code" UUID DEFAULT gen_random_uuid()
);

CREATE INDEX "email_idx" ON "users" ("email");
CREATE INDEX "name_status_idx" ON "users" ("name", "status");
CREATE UNIQUE INDEX "username_unique" ON "users" ("username");
CREATE UNIQUE INDEX "email_role_unique" ON "users" ("email", "role");
```

---

#### mysqlTable Syntax (MySQL):

```typescript
import {
  mysqlTable,
  serial,
  bigint,
  int,
  smallint,
  mediumint,
  tinyint,
  text,
  tinytext,
  mediumtext,
  longtext,
  varchar,
  char,
  boolean,
  timestamp,
  datetime,
  date,
  time,
  year,
  json,
  decimal,
  float,
  double,
  mysqlEnum,
  primaryKey,
  unique,
  index
} from 'drizzle-orm/mysql-core';

// Define enum (inline in MySQL)
// MySQL enums are defined inline within the column, not separately

// Define table
export const users = mysqlTable('users', {
  // Primary key options
  id: serial('id').primaryKey(),                              // BIGINT UNSIGNED AUTO_INCREMENT
  // id: int('id').autoincrement().primaryKey(),              // INT AUTO_INCREMENT
  // id: bigint('id', { mode: 'number' }).autoincrement().primaryKey(),

  // String types
  email: varchar('email', { length: 255 }).notNull().unique(),
  name: text('name'),                                          // TEXT (65,535 chars)
  shortDesc: tinytext('short_desc'),                          // TINYTEXT (255 chars)
  content: mediumtext('content'),                              // MEDIUMTEXT (16MB)
  longContent: longtext('long_content'),                       // LONGTEXT (4GB)
  username: varchar('username', { length: 50 }).notNull(),
  code: char('code', { length: 10 }),                          // Fixed-length string

  // Number types
  age: int('age'),                                             // INT (signed)
  smallNum: smallint('small_num'),                             // SMALLINT
  mediumNum: mediumint('medium_num'),                          // MEDIUMINT
  tinyNum: tinyint('tiny_num'),                                // TINYINT
  balance: bigint('balance', { mode: 'number' }),              // BIGINT
  price: decimal('price', { precision: 10, scale: 2 }),        // DECIMAL
  rating: double('rating'),                                    // DOUBLE
  score: float('score'),                                       // FLOAT

  // Boolean (TINYINT(1) in MySQL)
  isActive: boolean('is_active').default(true).notNull(),

  // Date/Time types
  createdAt: timestamp('created_at').defaultNow().notNull(),
  updatedAt: datetime('updated_at'),                           // DATETIME
  birthDate: date('birth_date'),
  loginTime: time('login_time'),
  graduationYear: year('graduation_year'),                     // YEAR

  // JSON
  metadata: json('metadata'),

  // Enum (defined inline)
  role: mysqlEnum('role', ['user', 'admin', 'moderator']).default('user').notNull(),
  status: mysqlEnum('status', ['active', 'inactive', 'pending']).default('active'),

}, (table) => ({
  // Indexes
  emailIdx: index('email_idx').on(table.email),
  nameStatusIdx: index('name_status_idx').on(table.name, table.status),

  // Unique constraints
  usernameUnique: unique('username_unique').on(table.username),
}));
```

#### SQL Generated (MySQL):
```sql
CREATE TABLE `users` (
    `id` SERIAL PRIMARY KEY,
    `email` VARCHAR(255) NOT NULL UNIQUE,
    `name` TEXT,
    `short_desc` TINYTEXT,
    `content` MEDIUMTEXT,
    `long_content` LONGTEXT,
    `username` VARCHAR(50) NOT NULL,
    `code` CHAR(10),
    `age` INT,
    `small_num` SMALLINT,
    `medium_num` MEDIUMINT,
    `tiny_num` TINYINT,
    `balance` BIGINT,
    `price` DECIMAL(10, 2),
    `rating` DOUBLE,
    `score` FLOAT,
    `is_active` BOOLEAN NOT NULL DEFAULT true,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME,
    `birth_date` DATE,
    `login_time` TIME,
    `graduation_year` YEAR,
    `metadata` JSON,
    `role` ENUM('user', 'admin', 'moderator') NOT NULL DEFAULT 'user',
    `status` ENUM('active', 'inactive', 'pending') DEFAULT 'active'
);

CREATE INDEX `email_idx` ON `users` (`email`);
CREATE INDEX `name_status_idx` ON `users` (`name`(255), `status`);
CREATE UNIQUE INDEX `username_unique` ON `users` (`username`);
```

---

#### Key Differences: pgTable vs mysqlTable

| Feature | pgTable (PostgreSQL) | mysqlTable (MySQL) |
|---------|---------------------|-------------------|
| **Import** | `drizzle-orm/pg-core` | `drizzle-orm/mysql-core` |
| **Auto Increment** | `serial()`, `bigserial()` | `serial()`, `.autoincrement()` |
| **Enum** | `pgEnum()` (separate type) | `mysqlEnum()` (inline) |
| **JSON** | `json()`, `jsonb()` | `json()` only |
| **Text Types** | `text()` only | `tinytext()`, `text()`, `mediumtext()`, `longtext()` |
| **Integer Types** | `integer()`, `bigint()` | `tinyint()`, `smallint()`, `mediumint()`, `int()`, `bigint()` |
| **UUID** | Native `uuid()` type | Use `varchar(36)` |
| **Boolean** | Native `BOOLEAN` | `TINYINT(1)` |
| **Identifier Quotes** | Double quotes `"table"` | Backticks `` `table` `` |

---

#### Complete Comparison Table:

| Data Type | pgTable (PostgreSQL) | mysqlTable (MySQL) |
|-----------|---------------------|-------------------|
| **Auto ID** | `serial()`, `bigserial()` | `serial()`, `int().autoincrement()` |
| **Integer** | `integer()` | `int()` |
| **Small Integer** | `smallint()` | `smallint()`, `tinyint()`, `mediumint()` |
| **Big Integer** | `bigint()` | `bigint()` |
| **Float** | `real()`, `doublePrecision()` | `float()`, `double()` |
| **Decimal** | `decimal({ precision, scale })` | `decimal({ precision, scale })` |
| **String (variable)** | `text()`, `varchar({ length })` | `text()`, `varchar({ length })` |
| **String (fixed)** | `char({ length })` | `char({ length })` |
| **Text Sizes** | `text()` only | `tinytext()`, `text()`, `mediumtext()`, `longtext()` |
| **Boolean** | `boolean()` | `boolean()` (stored as TINYINT(1)) |
| **Timestamp** | `timestamp()` | `timestamp()`, `datetime()` |
| **Date** | `date()` | `date()` |
| **Time** | `time()` | `time()`, `year()` |
| **JSON** | `json()`, `jsonb()` | `json()` |
| **UUID** | `uuid()` (native) | `varchar(36)` (no native UUID) |
| **Enum** | `pgEnum()` (separate type) | `mysqlEnum()` (inline) |
| **Binary** | `bytea()` | `binary()`, `varbinary()`, `blob()` |

---

### 1. Models (Tables) in Drizzle

#### Drizzle Schema Example:
```typescript
// schema.ts
import { pgTable, serial, text, timestamp, varchar } from 'drizzle-orm/pg-core';

export const users = pgTable('users', {
  id: serial('id').primaryKey(),
  email: varchar('email', { length: 255 }).notNull().unique(),
  name: text('name'),
  createdAt: timestamp('created_at').defaultNow().notNull(),
  updatedAt: timestamp('updated_at').defaultNow().notNull(),
});
```

#### SQL Equivalent:
```sql
CREATE TABLE "users" (
    "id" SERIAL PRIMARY KEY,
    "email" VARCHAR(255) UNIQUE NOT NULL,
    "name" TEXT,
    "created_at" TIMESTAMP DEFAULT NOW() NOT NULL,
    "updated_at" TIMESTAMP DEFAULT NOW() NOT NULL
);
```

---

### 2. Auto Increment in Drizzle

```typescript
import { pgTable, serial, integer } from 'drizzle-orm/pg-core';

export const products = pgTable('products', {
  id: serial('id').primaryKey(),
  // OR use integer with generated identity
  // id: integer('id').primaryKey().generatedAlwaysAsIdentity(),
  name: text('name').notNull(),
});
```

#### SQL Equivalent:
```sql
CREATE TABLE "products" (
    "id" SERIAL PRIMARY KEY,
    "name" TEXT NOT NULL
);

-- OR with generated identity
CREATE TABLE "products" (
    "id" INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "name" TEXT NOT NULL
);
```

---

### 3. Unique Keys in Drizzle

#### Single Column Unique:
```typescript
import { pgTable, serial, text, varchar, unique } from 'drizzle-orm/pg-core';

export const users = pgTable('users', {
  id: serial('id').primaryKey(),
  email: varchar('email', { length: 255 }).notNull().unique(),
  username: varchar('username', { length: 50 }).notNull().unique(),
});
```

#### Composite Unique:
```typescript
export const userProfiles = pgTable('user_profiles', {
  userId: integer('user_id').notNull(),
  platform: varchar('platform', { length: 50 }).notNull(),
  handle: varchar('handle', { length: 100 }).notNull(),
}, (table) => ({
  uniqueUserPlatform: unique().on(table.userId, table.platform),
}));
```

#### SQL Equivalent:
```sql
-- Single unique
CREATE TABLE "users" (
    "id" SERIAL PRIMARY KEY,
    "email" VARCHAR(255) UNIQUE NOT NULL,
    "username" VARCHAR(50) UNIQUE NOT NULL
);

-- Composite unique
CREATE TABLE "user_profiles" (
    "user_id" INTEGER NOT NULL,
    "platform" VARCHAR(50) NOT NULL,
    "handle" VARCHAR(100) NOT NULL,
    CONSTRAINT "user_profiles_user_id_platform_unique" UNIQUE ("user_id", "platform")
);
```

---

### 4. Foreign Keys & Relations in Drizzle

#### One-to-Many Relationship:
```typescript
import { pgTable, serial, text, integer } from 'drizzle-orm/pg-core';
import { relations } from 'drizzle-orm';

export const users = pgTable('users', {
  id: serial('id').primaryKey(),
  email: text('email').notNull().unique(),
});

export const posts = pgTable('posts', {
  id: serial('id').primaryKey(),
  title: text('title').notNull(),
  content: text('content'),
  authorId: integer('author_id').notNull().references(() => users.id),
});

// Define relations (for query purposes)
export const usersRelations = relations(users, ({ many }) => ({
  posts: many(posts),
}));

export const postsRelations = relations(posts, ({ one }) => ({
  author: one(users, {
    fields: [posts.authorId],
    references: [users.id],
  }),
}));
```

#### SQL Equivalent:
```sql
CREATE TABLE "users" (
    "id" SERIAL PRIMARY KEY,
    "email" TEXT UNIQUE NOT NULL
);

CREATE TABLE "posts" (
    "id" SERIAL PRIMARY KEY,
    "title" TEXT NOT NULL,
    "content" TEXT,
    "author_id" INTEGER NOT NULL,
    CONSTRAINT "posts_author_id_fkey" FOREIGN KEY ("author_id") 
        REFERENCES "users"("id")
);
```

#### One-to-One Relationship:
```typescript
export const users = pgTable('users', {
  id: serial('id').primaryKey(),
  email: text('email').notNull().unique(),
});

export const profiles = pgTable('profiles', {
  id: serial('id').primaryKey(),
  bio: text('bio').notNull(),
  userId: integer('user_id').notNull().unique().references(() => users.id),
});

export const usersRelations = relations(users, ({ one }) => ({
  profile: one(profiles, {
    fields: [users.id],
    references: [profiles.userId],
  }),
}));

export const profilesRelations = relations(profiles, ({ one }) => ({
  user: one(users, {
    fields: [profiles.userId],
    references: [users.id],
  }),
}));
```

#### Many-to-Many Relationship:
```typescript
export const posts = pgTable('posts', {
  id: serial('id').primaryKey(),
  title: text('title').notNull(),
});

export const categories = pgTable('categories', {
  id: serial('id').primaryKey(),
  name: text('name').notNull().unique(),
});

// Junction table
export const postsToCategories = pgTable('posts_to_categories', {
  postId: integer('post_id').notNull().references(() => posts.id),
  categoryId: integer('category_id').notNull().references(() => categories.id),
  assignedAt: timestamp('assigned_at').defaultNow().notNull(),
}, (table) => ({
  pk: primaryKey({ columns: [table.postId, table.categoryId] }),
}));

// Relations
export const postsRelations = relations(posts, ({ many }) => ({
  postsToCategories: many(postsToCategories),
}));

export const categoriesRelations = relations(categories, ({ many }) => ({
  postsToCategories: many(postsToCategories),
}));

export const postsToCategoriesRelations = relations(postsToCategories, ({ one }) => ({
  post: one(posts, {
    fields: [postsToCategories.postId],
    references: [posts.id],
  }),
  category: one(categories, {
    fields: [postsToCategories.categoryId],
    references: [categories.id],
  }),
}));
```

---

### 5. Indexes in Drizzle

```typescript
import { pgTable, serial, text, timestamp, index, uniqueIndex } from 'drizzle-orm/pg-core';

export const posts = pgTable('posts', {
  id: serial('id').primaryKey(),
  title: text('title').notNull(),
  status: text('status').notNull(),
  createdAt: timestamp('created_at').defaultNow().notNull(),
}, (table) => ({
  // Single column index
  nameIdx: index('name_idx').on(table.title),
  
  // Composite index
  statusCreatedIdx: index('status_created_idx').on(table.status, table.createdAt),
  
  // Unique index
  titleUniqueIdx: uniqueIndex('title_unique_idx').on(table.title),
}));
```

#### SQL Equivalent:
```sql
CREATE TABLE "posts" (
    "id" SERIAL PRIMARY KEY,
    "title" TEXT NOT NULL,
    "status" TEXT NOT NULL,
    "created_at" TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE INDEX "name_idx" ON "posts"("title");
CREATE INDEX "status_created_idx" ON "posts"("status", "created_at");
CREATE UNIQUE INDEX "title_unique_idx" ON "posts"("title");
```

---

### 6. Cascade Delete & Update in Drizzle

```typescript
import { pgTable, serial, text, integer } from 'drizzle-orm/pg-core';

export const users = pgTable('users', {
  id: serial('id').primaryKey(),
  email: text('email').notNull().unique(),
});

export const posts = pgTable('posts', {
  id: serial('id').primaryKey(),
  title: text('title').notNull(),
  authorId: integer('author_id')
    .notNull()
    .references(() => users.id, { onDelete: 'cascade', onUpdate: 'cascade' }),
});
```

**Options:** `cascade`, `restrict`, `no action`, `set null`, `set default`

#### SQL Equivalent:
```sql
CREATE TABLE "posts" (
    "id" SERIAL PRIMARY KEY,
    "title" TEXT NOT NULL,
    "author_id" INTEGER NOT NULL,
    CONSTRAINT "posts_author_id_fkey" FOREIGN KEY ("author_id") 
        REFERENCES "users"("id") ON DELETE CASCADE ON UPDATE CASCADE
);
```

---

### 7. Enums in Drizzle

```typescript
import { pgTable, serial, text, pgEnum } from 'drizzle-orm/pg-core';

export const roleEnum = pgEnum('role', ['USER', 'ADMIN', 'MODERATOR']);

export const users = pgTable('users', {
  id: serial('id').primaryKey(),
  email: text('email').notNull().unique(),
  role: roleEnum('role').default('USER').notNull(),
});
```

#### SQL Equivalent:
```sql
CREATE TYPE "role" AS ENUM ('USER', 'ADMIN', 'MODERATOR');

CREATE TABLE "users" (
    "id" SERIAL PRIMARY KEY,
    "email" TEXT UNIQUE NOT NULL,
    "role" "role" NOT NULL DEFAULT 'USER'
);
```

---

### 8. Default Values in Drizzle

```typescript
import { pgTable, serial, text, boolean, integer, timestamp, uuid } from 'drizzle-orm/pg-core';
import { sql } from 'drizzle-orm';

export const posts = pgTable('posts', {
  id: serial('id').primaryKey(),
  title: text('title').notNull(),
  published: boolean('published').default(false).notNull(),
  views: integer('views').default(0).notNull(),
  createdAt: timestamp('created_at').defaultNow().notNull(),
  uuid: uuid('uuid').defaultRandom().notNull(),
  // Custom SQL default
  customDefault: text('custom_default').default(sql`'custom-value'`),
});
```

---

### 9. Drizzle Client Usage

```typescript
import { drizzle } from 'drizzle-orm/node-postgres';
import { Pool } from 'pg';
import { users, posts, usersRelations, postsRelations } from './schema';
import { eq, and, or, like, desc } from 'drizzle-orm';

const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
});

const db = drizzle(pool, { schema: { users, posts, usersRelations, postsRelations } });

// Create
const newUser = await db.insert(users).values({
  email: 'alice@example.com',
  name: 'Alice',
}).returning();

// Create with relation
const newPost = await db.insert(posts).values({
  title: 'First Post',
  content: 'Hello World',
  authorId: newUser[0].id,
}).returning();

// Read - Simple select
const allUsers = await db.select().from(users);

// Read - With where clause
const filteredUsers = await db
  .select()
  .from(users)
  .where(like(users.email, '%@example.com'));

// Read - With joins
const usersWithPosts = await db
  .select()
  .from(users)
  .leftJoin(posts, eq(users.id, posts.authorId))
  .where(eq(users.id, 1));

// Read - With relations (easier syntax)
const userWithPosts = await db.query.users.findFirst({
  where: eq(users.id, 1),
  with: {
    posts: true,
  },
});

// Update
await db
  .update(users)
  .set({ name: 'Alice Updated' })
  .where(eq(users.id, 1));

// Delete
await db
  .delete(users)
  .where(eq(users.id, 1));

// Complex query with multiple conditions
const complexQuery = await db
  .select()
  .from(posts)
  .where(
    and(
      eq(posts.published, true),
      or(
        like(posts.title, '%important%'),
        eq(posts.authorId, 1)
      )
    )
  )
  .orderBy(desc(posts.createdAt))
  .limit(10);

// Raw SQL when needed
const rawResult = await db.execute(sql`
  SELECT * FROM users WHERE email LIKE '%@example.com'
`);
```

---

## Prisma vs Drizzle Comparison

### 1. **Philosophy & Approach**

| Aspect | Prisma | Drizzle |
|--------|--------|---------|
| **Philosophy** | Schema-first, declarative | Code-first, SQL-like |
| **Abstraction Level** | High-level, hides SQL | Thin layer over SQL |
| **Type Safety** | Generated types | TypeScript-native |
| **Learning Curve** | Easier for beginners | Requires SQL knowledge |

---

### 2. **Schema Definition**

**Prisma:**
- Uses custom `.prisma` DSL
- Declarative and readable
- Schema in separate file
- Auto-generates TypeScript types

**Drizzle:**
- Pure TypeScript/JavaScript
- Defines schema as code
- Schema is TypeScript code
- No code generation needed

---

### 3. **Type Safety**

**Prisma:**
```typescript
// Types are generated after npx prisma generate
const user = await prisma.user.findUnique({
  where: { id: 1 },
  include: { posts: true } // Fully typed
});
// user.posts is typed automatically
```

**Drizzle:**
```typescript
// Types are inferred from schema
const user = await db.query.users.findFirst({
  where: eq(users.id, 1),
  with: { posts: true } // Fully typed
});
// user.posts is typed from schema definition
```

---

### 4. **Migrations**

**Prisma:**
```bash
# Auto-generate migration from schema changes
npx prisma migrate dev --name add_user_table

# Apply migrations
npx prisma migrate deploy
```

**Drizzle:**
```bash
# Generate migration
npx drizzle-kit generate:pg

# Run migration
npx drizzle-kit push:pg

# OR use custom migration runner
npm run migrate
```

---

### 5. **Query Syntax Comparison**

#### Create
**Prisma:**
```typescript
await prisma.user.create({
  data: {
    email: 'user@example.com',
    posts: {
      create: { title: 'Hello' }
    }
  }
});
```

**Drizzle:**
```typescript
const [user] = await db.insert(users)
  .values({ email: 'user@example.com' })
  .returning();

await db.insert(posts)
  .values({ title: 'Hello', authorId: user.id });
```

#### Read with Relations
**Prisma:**
```typescript
await prisma.user.findMany({
  where: { email: { contains: '@example.com' } },
  include: { posts: true },
  orderBy: { createdAt: 'desc' }
});
```

**Drizzle:**
```typescript
await db.query.users.findMany({
  where: like(users.email, '%@example.com'),
  with: { posts: true },
  orderBy: [desc(users.createdAt)]
});

// OR with explicit join
await db.select()
  .from(users)
  .leftJoin(posts, eq(users.id, posts.authorId))
  .where(like(users.email, '%@example.com'))
  .orderBy(desc(users.createdAt));
```

---

### 6. **Performance**

| Metric | Prisma | Drizzle |
|--------|--------|---------|
| **Runtime Overhead** | Higher (query engine) | Lower (direct SQL) |
| **Bundle Size** | Larger (~5MB) | Smaller (~50KB) |
| **Query Speed** | Good | Excellent |
| **Cold Start** | Slower | Faster |

---

### 7. **Raw SQL Support**

**Prisma:**
```typescript
// Tagged template
await prisma.$queryRaw`SELECT * FROM users WHERE email = ${email}`;

// String query
await prisma.$executeRaw`DELETE FROM users WHERE id = ${id}`;
```

**Drizzle:**
```typescript
import { sql } from 'drizzle-orm';

await db.execute(sql`SELECT * FROM users WHERE email = ${email}`);

// Mix SQL with query builder
await db.select()
  .from(users)
  .where(sql`email LIKE '%@example.com'`);
```

---

### 8. **Database Support**

| Database | Prisma | Drizzle |
|----------|--------|---------|
| PostgreSQL | ✅ | ✅ |
| MySQL | ✅ | ✅ |
| SQLite | ✅ | ✅ |
| MongoDB | ✅ | ❌ |
| SQL Server | ✅ | ❌ |
| CockroachDB | ✅ | ✅ |

---

### 9. **Tooling & DX**

**Prisma:**
- ✅ Prisma Studio (GUI)
- ✅ VS Code extension
- ✅ Auto-completion
- ✅ Schema formatting
- ✅ Migration preview

**Drizzle:**
- ✅ Drizzle Kit (CLI)
- ✅ Drizzle Studio (GUI)
- ✅ TypeScript IntelliSense
- ✅ SQL-like syntax
- ⚠️ Fewer GUI tools

---

### 10. **When to Use Each**

### Use **Prisma** when:
- 🎯 You want rapid development with minimal SQL
- 🎯 Team has mixed SQL experience levels
- 🎯 You need a robust migration system
- 🎯 You want excellent TypeScript support out of the box
- 🎯 Prisma Studio GUI is valuable for your workflow
- 🎯 Working with MongoDB (NoSQL)
- 🎯 Building prototypes or MVPs quickly

### Use **Drizzle** when:
- 🎯 Performance is critical (edge functions, serverless)
- 🎯 You want maximum control over SQL
- 🎯 Bundle size matters (small applications, edge)
- 🎯 Team is comfortable with SQL
- 🎯 You need minimal runtime overhead
- 🎯 Working with complex SQL queries
- 🎯 Building high-performance applications

---

### 11. **Code Comparison: Complete Example**

#### Prisma Schema & Usage:
```prisma
// schema.prisma
model User {
  id        Int      @id @default(autoincrement())
  email     String   @unique
  name      String?
  role      Role     @default(USER)
  posts     Post[]
  profile   Profile?
  createdAt DateTime @default(now())
}

model Post {
  id        Int      @id @default(autoincrement())
  title     String
  published Boolean  @default(false)
  authorId  Int
  author    User     @relation(fields: [authorId], references: [id], onDelete: Cascade)
  
  @@index([authorId, published])
}

model Profile {
  id     Int    @id @default(autoincrement())
  bio    String
  userId Int    @unique
  user   User   @relation(fields: [userId], references: [id])
}

enum Role {
  USER
  ADMIN
}
```

```typescript
// Usage
const user = await prisma.user.create({
  data: {
    email: 'user@example.com',
    name: 'John',
    profile: {
      create: { bio: 'Developer' }
    },
    posts: {
      create: [
        { title: 'First Post', published: true }
      ]
    }
  },
  include: {
    posts: true,
    profile: true
  }
});
```

#### Drizzle Schema & Usage:
```typescript
// schema.ts
import { pgTable, serial, text, boolean, integer, timestamp, pgEnum, index, unique } from 'drizzle-orm/pg-core';
import { relations } from 'drizzle-orm';

export const roleEnum = pgEnum('role', ['USER', 'ADMIN']);

export const users = pgTable('users', {
  id: serial('id').primaryKey(),
  email: text('email').notNull().unique(),
  name: text('name'),
  role: roleEnum('role').default('USER').notNull(),
  createdAt: timestamp('created_at').defaultNow().notNull(),
});

export const posts = pgTable('posts', {
  id: serial('id').primaryKey(),
  title: text('title').notNull(),
  published: boolean('published').default(false).notNull(),
  authorId: integer('author_id').notNull()
    .references(() => users.id, { onDelete: 'cascade' }),
}, (table) => ({
  authorPublishedIdx: index('author_published_idx').on(table.authorId, table.published),
}));

export const profiles = pgTable('profiles', {
  id: serial('id').primaryKey(),
  bio: text('bio').notNull(),
  userId: integer('user_id').notNull().unique()
    .references(() => users.id),
});

// Relations
export const usersRelations = relations(users, ({ one, many }) => ({
  posts: many(posts),
  profile: one(profiles, {
    fields: [users.id],
    references: [profiles.userId],
  }),
}));

export const postsRelations = relations(posts, ({ one }) => ({
  author: one(users, {
    fields: [posts.authorId],
    references: [users.id],
  }),
}));

export const profilesRelations = relations(profiles, ({ one }) => ({
  user: one(users, {
    fields: [profiles.userId],
    references: [users.id],
  }),
}));
```

```typescript
// Usage
const [user] = await db.insert(users).values({
  email: 'user@example.com',
  name: 'John',
}).returning();

const [profile] = await db.insert(profiles).values({
  bio: 'Developer',
  userId: user.id,
}).returning();

await db.insert(posts).values({
  title: 'First Post',
  published: true,
  authorId: user.id,
});

// Query with relations
const userWithData = await db.query.users.findFirst({
  where: eq(users.id, user.id),
  with: {
    posts: true,
    profile: true,
  },
});
```

---

## Conclusion

Both Prisma and Drizzle are excellent ORMs with different strengths:

- **Prisma** excels at developer experience, rapid development, and providing a complete database toolkit
- **Drizzle** excels at performance, SQL control, and lightweight applications

Choose based on your project requirements, team expertise, and performance needs.

---

## Additional Resources

### Prisma
- Documentation: https://www.prisma.io/docs
- GitHub: https://github.com/prisma/prisma
- Discord: https://pris.ly/discord

### Drizzle
- Documentation: https://orm.drizzle.team
- GitHub: https://github.com/drizzle-team/drizzle-orm
- Discord: https://driz.link/discord
