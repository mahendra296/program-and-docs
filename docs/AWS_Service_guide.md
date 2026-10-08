# AWS Services Guide — Secrets Manager, SQS, SNS & EventBridge Scheduler

A practical guide to four AWS services that almost every Spring Boot backend on AWS uses: **Secrets Manager** (credentials), **SQS** (queues), **SNS** (pub/sub notifications) and **EventBridge Scheduler** (scheduled jobs). Each section explains the concepts, the important limits and settings, CLI examples, and **Spring Boot integration** with the AWS SDK for Java v2 and Spring Cloud AWS 3.x.

> Related: [Spring Boot Annotations Guide](java-spring-boot-document-with-full-details.md) · [Spring @Transactional Guide](java-spring-transactional.md) · [Database Systems Guide](Database_systems.md)

## Table of Contents

- [Overview: Which Service When?](#overview-which-service-when)
    - [Prerequisites: credentials, region and local development](#prerequisites-credentials-region-and-local-development)
    - [Spring Cloud AWS Properties Reference](#spring-cloud-aws-properties-reference)
- [1. AWS Secrets Manager](#1-aws-secrets-manager)
    - [What It Is](#what-it-is)
    - [Secret Types](#secret-types)
    - [Versioning & Staging Labels](#versioning--staging-labels)
    - [Automatic Rotation](#automatic-rotation)
    - [KMS Encryption](#kms-encryption)
    - [IAM Policies & Resource Policies](#iam-policies--resource-policies)
    - [VPC Endpoints](#vpc-endpoints)
    - [Cross-Region Replication](#cross-region-replication)
    - [Secrets Manager vs SSM Parameter Store](#secrets-manager-vs-ssm-parameter-store)
    - [Spring Boot Integration & SDK Examples](#spring-boot-integration--sdk-examples)
- [2. AWS SQS (Simple Queue Service)](#2-aws-sqs-simple-queue-service)
    - [What It Is](#what-it-is-1)
    - [Standard vs FIFO Queue](#standard-vs-fifo-queue)
    - [Core Concepts](#core-concepts)
    - [Visibility Timeout](#visibility-timeout)
    - [Dead Letter Queue (DLQ)](#dead-letter-queue-dlq)
    - [Long Polling vs Short Polling](#long-polling-vs-short-polling)
    - [Message Lifecycle](#message-lifecycle)
    - [Delay Queue & Message Timers](#delay-queue--message-timers)
    - [SQS + Lambda Event Source Mapping](#sqs--lambda-event-source-mapping)
    - [Security](#security)
    - [Scaling Patterns](#scaling-patterns)
    - [Spring Boot with the AWS SDK (SqsClient)](#spring-boot-with-the-aws-sdk-sqsclient)
    - [Spring Cloud AWS Setup & Auto-configured Beans](#spring-cloud-aws-setup--auto-configured-beans)
    - [@SqsListener (Spring Cloud AWS)](#sqslistener-spring-cloud-aws)
    - [@SqsListener Method Arguments](#sqslistener-method-arguments)
    - [Acknowledgement Modes](#acknowledgement-modes)
    - [SqsMessageListenerContainerFactory](#sqsmessagelistenercontainerfactory)
    - [Error Handling, Retries & Back-off](#error-handling-retries--back-off)
    - [DLQ Handling in Spring Boot](#dlq-handling-in-spring-boot)
    - [FIFO Queues in Spring Boot](#fifo-queues-in-spring-boot)
    - [SqsTemplate (Sending & Receiving)](#sqstemplate-sending--receiving)
    - [Testing SQS Listeners](#testing-sqs-listeners)
- [3. AWS SNS (Simple Notification Service)](#3-aws-sns-simple-notification-service)
    - [What It Is](#what-it-is-2)
    - [Standard vs FIFO Topics](#standard-vs-fifo-topics)
    - [Subscription Types](#subscription-types)
    - [Message Filtering](#message-filtering)
    - [Fan-Out Pattern (SNS + SQS)](#fan-out-pattern-sns--sqs)
    - [Message Attributes](#message-attributes)
    - [DLQ for SNS Subscriptions](#dlq-for-sns-subscriptions)
    - [SNS Large Message Support](#sns-large-message-support)
    - [Security](#security-1)
    - [Spring Boot with the AWS SDK (SnsClient)](#spring-boot-with-the-aws-sdk-snsclient)
    - [Spring Cloud AWS for SNS: Annotations & Classes](#spring-cloud-aws-for-sns-annotations--classes)
    - [SnsTemplate & SnsSmsTemplate (Publishing with Spring Cloud AWS)](#snstemplate--snssmstemplate-publishing-with-spring-cloud-aws)
    - [Consuming SNS Messages in SQS Listeners (@SnsNotificationMessage)](#consuming-sns-messages-in-sqs-listeners-snsnotificationmessage)
    - [Receiving SNS over HTTP (@NotificationMessageMapping)](#receiving-sns-over-http-notificationmessagemapping)
    - [Testing SNS Publishing](#testing-sns-publishing)
- [4. AWS EventBridge Scheduler](#4-aws-eventbridge-scheduler)
    - [What It Is](#what-it-is-3)
    - [EventBridge Scheduler vs EventBridge Rules vs CloudWatch Events](#eventbridge-scheduler-vs-eventbridge-rules-vs-cloudwatch-events)
    - [Schedule Types](#schedule-types)
    - [Targets](#targets)
    - [Flexible Time Windows](#flexible-time-windows)
    - [Retry Policy & DLQ](#retry-policy--dlq)
    - [Scheduler Groups](#scheduler-groups)
    - [Timezone Support](#timezone-support)
    - [Cross-Account & Cross-Region](#cross-account--cross-region)
    - [Spring Boot Integration & SDK Examples](#spring-boot-integration--sdk-examples-1)
- [5. Combined Architecture Patterns](#5-combined-architecture-patterns)
    - [Pattern 1: Secure Spring Boot App (Secrets Manager + DataSource)](#pattern-1-secure-spring-boot-app-secrets-manager--datasource)
    - [Pattern 2: Event-Driven Microservices (SNS → SQS → Spring Boot)](#pattern-2-event-driven-microservices-sns--sqs--spring-boot)
    - [Pattern 3: Scheduled Nightly Pipeline (EventBridge → ECS → SQS → Spring Boot)](#pattern-3-scheduled-nightly-pipeline-eventbridge--ecs--sqs--spring-boot)
    - [Pattern 4: Rate Limiting Downstream Calls (SQS + Spring Boot Consumer)](#pattern-4-rate-limiting-downstream-calls-sqs--spring-boot-consumer)
    - [Pattern 5: Scheduled Secret Rotation Alert (EventBridge + Lambda → SNS → SQS → Spring Boot)](#pattern-5-scheduled-secret-rotation-alert-eventbridge--lambda--sns--sqs--spring-boot)

---

## Overview: Which Service When?

| Service | Model | Use it for | Not for |
|---|---|---|---|
| **Secrets Manager** | Encrypted key-value store with rotation | DB passwords, API keys, OAuth client secrets | Plain configuration and feature flags (use SSM Parameter Store / AppConfig) |
| **SQS** | **Queue** (pull, point-to-point): each message is processed by **one** consumer | Background jobs, buffering spikes, decoupling services, retries with DLQ | Broadcasting one event to many services (use SNS/EventBridge) |
| **SNS** | **Topic** (push, pub/sub): each message goes to **every** subscriber | Fan-out of events, alerts, email/SMS/mobile push | Durable work queues by itself (combine with SQS) |
| **EventBridge Scheduler** | Managed scheduler (cron, rate, one-time) | Recurring jobs, per-user reminders, delayed one-off tasks | Event routing between services (use EventBridge **rules**) |

```
                 ┌──────────────────────── Secrets Manager (credentials at startup)
                 │
Spring Boot ─────┼── publish ──► SNS topic ──► SQS queue A ──► consumer service A
   app           │                         └─► SQS queue B ──► consumer service B
                 │
                 └── send ─────► SQS queue ──► @SqsListener worker

EventBridge Scheduler ── cron / at() ──► Lambda / SQS / ECS task / Step Functions
```

### Prerequisites: credentials, region and local development

The AWS SDK finds credentials through the **default credentials provider chain**, in this order: Java system properties → environment variables (`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`) → web identity token (EKS IRSA) → shared `~/.aws/credentials` / SSO profile → ECS container credentials → EC2 instance profile. In AWS, **always use an IAM role** (EC2 instance profile, ECS task role, EKS IRSA/Pod Identity, Lambda execution role); never put access keys in code or `application.yml`.

Spring Cloud AWS 3.x manages versions with a BOM and auto-configures the SDK clients (`SqsAsyncClient`, `SnsClient`, `SecretsManagerClient`...) from properties:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.awspring.cloud</groupId>
            <artifactId>spring-cloud-aws-dependencies</artifactId>
            <version>3.4.0</version>          <!-- pick the release matching your Spring Boot version -->
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

```yaml
spring:
  cloud:
    aws:
      region:
        static: us-east-1        # or let the SDK detect it from the environment
      # credentials: default chain is used automatically (IAM role in AWS, profile locally)
```

**Local development with LocalStack** (no AWS account needed):

```yaml
# application-local.yml
spring:
  cloud:
    aws:
      endpoint: http://localhost:4566     # LocalStack emulates SQS, SNS, Secrets Manager, Scheduler...
      region:
        static: us-east-1
      credentials:
        access-key: test
        secret-key: test
```

For integration tests, Testcontainers' `LocalStackContainer` can be wired with `@DynamicPropertySource` (see [Testing](java-spring-boot-document-with-full-details.md#dynamicpropertysource)).

---

### Spring Cloud AWS Properties Reference

| Property | Default | Description |
|---|---|---|
| `spring.cloud.aws.region.static` | — | Region for all clients (otherwise detected from the environment) |
| `spring.cloud.aws.endpoint` | — | Override endpoint for all services (LocalStack: `http://localhost:4566`) |
| `spring.cloud.aws.credentials.access-key` / `secret-key` | — | Static credentials (local/testing only) |
| `spring.cloud.aws.credentials.profile.name` | — | Use a named profile from `~/.aws/credentials` |
| `spring.cloud.aws.sqs.enabled` | `true` | Enable SQS auto-configuration |
| `spring.cloud.aws.sqs.endpoint` / `region` | — | SQS-specific endpoint / region |
| `spring.cloud.aws.sqs.listener.max-concurrent-messages` | `10` | Default for all listeners |
| `spring.cloud.aws.sqs.listener.max-messages-per-poll` | `10` | Default for all listeners |
| `spring.cloud.aws.sqs.listener.poll-timeout` | `10s` | Default long-poll wait |
| `spring.cloud.aws.sns.enabled` | `true` | Enable SNS auto-configuration |
| `spring.cloud.aws.sns.endpoint` / `region` | — | SNS-specific endpoint / region |
| `spring.cloud.aws.secretsmanager.enabled` | `true` | Enable Secrets Manager integration |
| `spring.cloud.aws.secretsmanager.endpoint` / `region` | — | Secrets Manager endpoint / region |

```yaml
spring:
  cloud:
    aws:
      region:
        static: ap-south-1
      sqs:
        listener:
          max-concurrent-messages: 20
          max-messages-per-poll: 10
          poll-timeout: 20s
app:
  sqs:
    orders-queue: orders-queue
  sns:
    order-events-arn: arn:aws:sns:ap-south-1:123456789012:order-events
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 1. AWS Secrets Manager

### What It Is

AWS Secrets Manager is a fully managed service for storing, retrieving, and automatically rotating sensitive credentials — database passwords, API keys, OAuth tokens, SSH keys, and any arbitrary secret data. Unlike hardcoding secrets in config files or environment variables, Secrets Manager provides:

- Centralized secret storage with audit trails (CloudTrail integration)
- Automatic rotation via AWS Lambda
- Fine-grained IAM access control
- Encryption via AWS KMS
- Cross-region replication
- Versioning with staging labels

**Core use case:** Your application never stores a database password. Instead, at runtime it calls Secrets Manager, gets the current password, and connects. When the password rotates, nothing in the application changes.

---

### Secret Types

A secret value is either a **string** (usually JSON with several fields) or **binary** data. Database secrets use a standard JSON structure so that AWS's built-in rotation functions understand them. Name secrets hierarchically (`<env>/<app>/<purpose>`) so IAM policies can grant access with wildcards.

```jsonc
// 1. RDS Database Credentials (structured JSON)
{
  "username": "admin",
  "password": "s3cr3tP@ssword!",
  "engine": "postgres",
  "host": "mydb.cluster-xxx.us-east-1.rds.amazonaws.com",
  "port": 5432,
  "dbname": "production"
}

// 2. Other Database Credentials (MySQL, Redshift, DocumentDB, etc.)
{
  "username": "appuser",
  "password": "MyP@ssword123",
  "host": "mysql.example.com",
  "port": 3306,
  "dbname": "appdb"
}

// 3. API Key (arbitrary key-value)
{
  "api_key": "sk-abc123xyz",
  "api_secret": "secretvalue456"
}

// 4. Plain text secret
"my-plain-text-api-key-12345"
```

```bash
# Create a secret via CLI
aws secretsmanager create-secret \
  --name "prod/myapp/db-password" \
  --description "Production PostgreSQL credentials" \
  --secret-string '{"username":"admin","password":"s3cr3tP@ssword!"}'

# Create from file
aws secretsmanager create-secret \
  --name "prod/myapp/api-keys" \
  --secret-string file://secrets.json
```

---

### Versioning & Staging Labels

Every time a secret value is updated, a new **version** is created. Versions are identified by a UUID and tagged with **staging labels**:

| Staging Label | Meaning |
|---|---|
| `AWSCURRENT` | The current active version (default for GetSecretValue) |
| `AWSPENDING` | New secret during rotation (not yet live) |
| `AWSPREVIOUS` | Previous version (kept for rollback) |

```bash
# Get current version (default):
aws secretsmanager get-secret-value --secret-id "prod/myapp/db-password"

# Get a specific version by label:
aws secretsmanager get-secret-value \
  --secret-id "prod/myapp/db-password" \
  --version-stage AWSPREVIOUS

# Get a specific version by ID:
aws secretsmanager get-secret-value \
  --secret-id "prod/myapp/db-password" \
  --version-id "abc123-uuid-here"

# List all versions:
aws secretsmanager list-secret-version-ids \
  --secret-id "prod/myapp/db-password"
```

**Rotation staging label flow:**
```
Before rotation:
  Version A → AWSCURRENT

During rotation (Version B created, not yet live):
  Version A → AWSCURRENT
  Version B → AWSPENDING

After rotation completes:
  Version A → AWSPREVIOUS
  Version B → AWSCURRENT
```

---

### Automatic Rotation

Secrets Manager rotates credentials using a **Lambda function** that follows a 4-step process:

```
Step 1: createSecret   → Generate new credential, store as AWSPENDING
Step 2: setSecret      → Set the new credential in the target service (e.g., ALTER USER)
Step 3: testSecret     → Validate the AWSPENDING secret works
Step 4: finishSecret   → Move AWSPENDING → AWSCURRENT, old AWSCURRENT → AWSPREVIOUS
```

```bash
# Enable rotation for an RDS secret (AWS provides built-in rotation Lambda):
aws secretsmanager rotate-secret \
  --secret-id "prod/myapp/db-password" \
  --rotation-lambda-arn "arn:aws:lambda:us-east-1:123456789:function:SecretsManagerRotation" \
  --rotation-rules '{"AutomaticallyAfterDays": 30}'

# Enable rotation with schedule expression (more precise):
aws secretsmanager rotate-secret \
  --secret-id "prod/myapp/db-password" \
  --rotation-lambda-arn "arn:aws:lambda:us-east-1:123456789:function:SecretsManagerRotation" \
  --rotation-rules '{"ScheduleExpression": "cron(0 2 1 * ? *)"}'
# Rotates at 2AM on the 1st of every month

# Force immediate rotation:
aws secretsmanager rotate-secret \
  --secret-id "prod/myapp/db-password" \
  --rotate-immediately
```

---

### KMS Encryption

Every secret is encrypted at rest using AWS KMS. By default, Secrets Manager uses the AWS-managed key `aws/secretsmanager`. For compliance or cross-account scenarios, use a customer-managed key (CMK).

```bash
# Create a secret using a custom KMS key:
aws secretsmanager create-secret \
  --name "prod/myapp/db-password" \
  --kms-key-id "arn:aws:kms:us-east-1:123456789:key/abcd-1234-efgh-5678" \
  --secret-string '{"password":"s3cr3t"}'

# Update existing secret's KMS key:
aws secretsmanager update-secret \
  --secret-id "prod/myapp/db-password" \
  --kms-key-id "arn:aws:kms:us-east-1:123456789:key/new-key-id"
```

**KMS permissions needed by callers** when the secret uses a customer-managed key (`kms:Decrypt` to read, `kms:GenerateDataKey` to create/update values):
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": ["kms:Decrypt", "kms:GenerateDataKey"],
      "Resource": "arn:aws:kms:us-east-1:123456789:key/abcd-1234"
    }
  ]
}
```

---

### IAM Policies & Resource Policies

**Identity-based policy (attached to role/user):**
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": ["secretsmanager:GetSecretValue"],
      "Resource": "arn:aws:secretsmanager:us-east-1:123456789:secret:prod/myapp/*"
    },
    {
      "Effect": "Deny",
      "Action": ["secretsmanager:DeleteSecret", "secretsmanager:PutSecretValue"],
      "Resource": "*"
    }
  ]
}
```

**Resource-based policy (on the secret itself — for cross-account access):**
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": "arn:aws:iam::987654321:role/ExternalAppRole"
      },
      "Action": "secretsmanager:GetSecretValue",
      "Resource": "*",
      "Condition": {
        "StringEquals": {
          "secretsmanager:VersionStage": "AWSCURRENT"
        }
      }
    }
  ]
}
```

> **Cross-account access** requires a **customer-managed KMS key** whose key policy also allows the other account: secrets encrypted with the AWS-managed key `aws/secretsmanager` can't be read from another account.

```bash
# Attach resource policy to a secret:
aws secretsmanager put-resource-policy \
  --secret-id "prod/myapp/db-password" \
  --resource-policy file://policy.json
```

---

### VPC Endpoints

By default, Secrets Manager API calls go over the public internet. For private subnets (no NAT gateway), use a VPC Interface Endpoint.

```bash
# Create VPC endpoint for Secrets Manager:
aws ec2 create-vpc-endpoint \
  --vpc-id vpc-12345678 \
  --service-name com.amazonaws.us-east-1.secretsmanager \
  --vpc-endpoint-type Interface \
  --subnet-ids subnet-aaa subnet-bbb \
  --security-group-ids sg-12345678 \
  --private-dns-enabled
```

With `--private-dns-enabled`, the SDK automatically routes to the private endpoint without any code changes.

---

### Cross-Region Replication

**Replica secrets** are read-only copies kept in sync with the primary secret (including rotations). Applications in another region read the local replica (lower latency, and still available if the primary region has an outage). A replica can be **promoted** to a standalone secret during disaster recovery.

```bash
# Replicate a secret to another region (for multi-region DR):
aws secretsmanager replicate-secret-to-regions \
  --secret-id "prod/myapp/db-password" \
  --add-replica-regions '[{"Region":"eu-west-1"},{"Region":"ap-southeast-1"}]'

# Check replication status:
aws secretsmanager describe-secret \
  --secret-id "prod/myapp/db-password" \
  --query 'ReplicationStatus'
```

---

### Secrets Manager vs SSM Parameter Store

| Feature | Secrets Manager | SSM Parameter Store |
|---|---|---|
| Cost | $0.40/secret/month + $0.05/10K API calls | Free (standard), $0.05/advanced param/month |
| Automatic Rotation | Yes (built-in) | No (manual via Lambda) |
| Max Secret Size | 64 KB | 4 KB (standard), 8 KB (advanced) |
| Cross-region Replication | Yes | No (manual) |
| Secret Versioning | Yes (full) | Yes (last 100 versions) |
| Built-in RDS Integration | Yes | No |
| Resource-based Policies | Yes | No |
| Best For | Database credentials, API keys needing rotation | Config values, feature flags, non-secret params |

**Rule of thumb:** Use Secrets Manager for anything that rotates or is truly secret. Use SSM Parameter Store for configuration values, togglable flags, and things that don't need rotation.

---

### Spring Boot Integration & SDK Examples

**Maven dependencies (pom.xml):**
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>bom</artifactId>
            <version>2.31.0</version>          <!-- any recent 2.x release -->
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>secretsmanager</artifactId>
    </dependency>
    <!-- Spring Cloud AWS Secrets Manager integration -->
    <dependency>
        <groupId>io.awspring.cloud</groupId>
        <artifactId>spring-cloud-aws-starter-secrets-manager</artifactId>
        <!-- version managed by spring-cloud-aws-dependencies BOM (see Prerequisites) -->
    </dependency>
</dependencies>
```

**Option 1 — Spring Cloud AWS (auto-inject secrets as properties):**
```yaml
# application.yml
spring:
  config:
    import: "aws-secretsmanager:prod/myapp/db-credentials"     # secret NAME (no leading slash)
    # several secrets: "aws-secretsmanager:prod/myapp/db-credentials;prod/myapp/api-keys"
    # don't fail startup if missing: "optional:aws-secretsmanager:..."

# Secrets Manager secret value (JSON) — each key becomes a Spring property:
# { "db.url": "jdbc:postgresql://...", "db.username": "admin", "db.password": "s3cr3t" }
```

> Simplest variant: store the keys as `spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password` in the secret JSON. Spring Boot then configures the DataSource with **no Java code at all**. The explicit `@Configuration` below is only needed for custom pools.

```java
@Configuration
public class DataSourceConfig {

    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.username}")
    private String dbUsername;

    @Value("${db.password}")           // injected directly from Secrets Manager
    private String dbPassword;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        return new HikariDataSource(config);
    }
}
```

**Option 2 — AWS SDK v2 directly (with in-memory cache):**

Use this when secrets are needed **at runtime** (not just at startup), e.g. per-tenant API keys. Cache the values: every `GetSecretValue` call costs money and adds latency. AWS also provides a ready-made cache: `com.amazonaws.secretsmanager:aws-secretsmanager-caching-java`.
```java
// SecretsManagerConfig.java
@Configuration
public class SecretsManagerConfig {

    @Bean
    public SecretsManagerClient secretsManagerClient() {
        return SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
```

```java
// DbCredentials.java  (maps JSON secret fields)
@Data
public class DbCredentials {
    private String username;
    private String password;
    private String host;
    private int    port;
    private String dbname;
}
```

```java
// SecretsManagerService.java
@Service
@Slf4j
public class SecretsManagerService {

    private final SecretsManagerClient client;
    private final ObjectMapper          objectMapper;

    // Simple TTL cache: secretId → (value, expiry)
    private final Map<String, CachedEntry> cache = new ConcurrentHashMap<>();
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    public SecretsManagerService(SecretsManagerClient client, ObjectMapper objectMapper) {
        this.client       = client;
        this.objectMapper = objectMapper;
    }

    public String getSecretString(String secretId) {
        CachedEntry entry = cache.get(secretId);
        if (entry != null && entry.isValid()) {
            return entry.getValue();
        }
        GetSecretValueResponse response = client.getSecretValue(
                GetSecretValueRequest.builder().secretId(secretId).build());
        String value = response.secretString();
        cache.put(secretId, new CachedEntry(value, Instant.now().plus(CACHE_TTL)));
        return value;
    }

    public <T> T getSecretAs(String secretId, Class<T> clazz) {
        try {
            return objectMapper.readValue(getSecretString(secretId), clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize secret: " + secretId, e);
        }
    }

    public DbCredentials getDbCredentials(String secretId) {
        return getSecretAs(secretId, DbCredentials.class);
    }

    public void createSecret(String name, String description, Object secretValue) {
        try {
            client.createSecret(CreateSecretRequest.builder()
                    .name(name)
                    .description(description)
                    .secretString(objectMapper.writeValueAsString(secretValue))
                    .build());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialization failed", e);
        }
    }

    public void updateSecret(String secretId, Object newValue) {
        try {
            client.updateSecret(UpdateSecretRequest.builder()
                    .secretId(secretId)
                    .secretString(objectMapper.writeValueAsString(newValue))
                    .build());
            cache.remove(secretId);  // invalidate cached entry
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialization failed", e);
        }
    }

    public void deleteSecret(String secretId, int recoveryWindowDays) {
        client.deleteSecret(DeleteSecretRequest.builder()
                .secretId(secretId)
                .recoveryWindowInDays((long) recoveryWindowDays)
                .build());
        cache.remove(secretId);
    }

    public void forceDeleteSecret(String secretId) {
        client.deleteSecret(DeleteSecretRequest.builder()
                .secretId(secretId)
                .forceDeleteWithoutRecovery(true)
                .build());
        cache.remove(secretId);
    }

    public List<SecretListEntry> listSecrets(String nameFilter) {
        ListSecretsRequest request = ListSecretsRequest.builder()
                .filters(Filter.builder()
                        .key(FilterNameStringType.NAME)
                        .values(nameFilter)
                        .build())
                .build();
        return client.listSecrets(request).secretList();
    }

    @Data
    @AllArgsConstructor
    private static class CachedEntry {
        private final String  value;
        private final Instant expiresAt;

        boolean isValid() {
            return Instant.now().isBefore(expiresAt);
        }
    }
}
```

**Custom rotation Lambda in Java (4-step skeleton):**

For RDS, Aurora, Redshift and DocumentDB, use the **AWS-provided rotation functions** (one click in the console). Write your own only for other systems (third-party API keys, LDAP...). Each step must be **idempotent** because Secrets Manager may call it again: e.g. `createSecret` should do nothing if an `AWSPENDING` version for this token already exists.
```java
// RotationHandler.java — deployed as a Lambda function
public class RotationHandler implements RequestHandler<Map<String, String>, Void> {

    private final SecretsManagerClient client = SecretsManagerClient.create();

    @Override
    public Void handleRequest(Map<String, String> event, Context context) {
        String arn   = event.get("SecretId");
        String token = event.get("ClientRequestToken");
        String step  = event.get("Step");

        switch (step) {
            case "createSecret"  -> createSecret(arn, token);
            case "setSecret"     -> setSecret(arn, token);
            case "testSecret"    -> testSecret(arn, token);
            case "finishSecret"  -> finishSecret(arn, token);
            default -> throw new IllegalArgumentException("Unknown step: " + step);
        }
        return null;
    }

    private void createSecret(String arn, String token) {
        // Generate new random password
        String newPassword = client.getRandomPassword(
                GetRandomPasswordRequest.builder()
                        .passwordLength(32L)
                        .excludeCharacters("/@\"\\")
                        .build())
                .randomPassword();

        // Load current secret to preserve structure
        String currentJson = client.getSecretValue(
                GetSecretValueRequest.builder().secretId(arn).versionStage("AWSCURRENT").build())
                .secretString();

        // Update password field and store as AWSPENDING (pseudo-code: parse JSON, replace "password")
        String updatedJson = replacePassword(currentJson, newPassword);

        client.putSecretValue(PutSecretValueRequest.builder()
                .secretId(arn)
                .clientRequestToken(token)
                .secretString(updatedJson)
                .versionStages(List.of("AWSPENDING"))
                .build());
    }

    private void setSecret(String arn, String token) {
        // Retrieve AWSPENDING secret and apply to the target service
        // e.g., run ALTER USER in RDS, call external API, etc.
    }

    private void testSecret(String arn, String token) {
        // Connect using AWSPENDING credentials — throw exception if it fails
    }

    private void finishSecret(String arn, String token) {
        // Find current AWSCURRENT version
        DescribeSecretResponse meta = client.describeSecret(
                DescribeSecretRequest.builder().secretId(arn).build());

        String currentVersionId = meta.versionIdsToStages().entrySet().stream()
                .filter(e -> e.getValue().contains("AWSCURRENT"))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow();

        // Promote AWSPENDING → AWSCURRENT
        client.updateSecretVersionStage(UpdateSecretVersionStageRequest.builder()
                .secretId(arn)
                .versionStage("AWSCURRENT")
                .moveToVersionId(token)
                .removeFromVersionId(currentVersionId)
                .build());
    }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 2. AWS SQS (Simple Queue Service)

### What It Is

Amazon SQS is a fully managed, highly available message queuing service. It decouples producers and consumers so they can operate independently at different speeds. SQS guarantees message durability (stored across multiple AZs) and provides at-least-once delivery (standard) or exactly-once processing (FIFO).

**Core value:** A producer can send messages even when the consumer is down, slow, or being scaled. The queue absorbs traffic spikes.

```
Producer → [SQS Queue] → Consumer
  (fast)      (buffer)     (slow or scaled)
```

---

### Standard vs FIFO Queue

| Feature | Standard Queue | FIFO Queue |
|---|---|---|
| Throughput | Nearly unlimited | 300 API calls/s per action (3,000 msg/s with batching); **high-throughput FIFO** mode allows far more |
| Ordering | Best-effort (NOT guaranteed) | Strict first-in-first-out per group |
| Delivery | At-least-once (duplicates possible) | Exactly-once processing |
| Deduplication | No | Yes (5-minute dedup window) |
| Use Cases | High-throughput, order doesn't matter | Financial transactions, ordered workflows |
| Message Groups | No | Yes (MessageGroupId) |
| Naming | Any name | Must end with `.fifo` |

```bash
# Create Standard Queue:
aws sqs create-queue \
  --queue-name my-standard-queue \
  --attributes '{"VisibilityTimeout":"30","MessageRetentionPeriod":"86400"}'

# Create FIFO Queue:
aws sqs create-queue \
  --queue-name my-fifo-queue.fifo \
  --attributes '{
    "FifoQueue": "true",
    "ContentBasedDeduplication": "true",
    "VisibilityTimeout": "30"
  }'
```


> Sending and receiving FIFO messages from Spring Boot (group id, deduplication id, ordering per group): [FIFO Queues in Spring Boot](#fifo-queues-in-spring-boot).

---

### Core Concepts

The key settings and limits of a queue (set as queue attributes, some overridable per message):

| Concept | Description | Default | Max |
|---|---|---|---|
| Message Size | Max size per message (body + attributes) | — | 1 MiB (raised from 256 KiB in 2025; use S3 + a pointer for larger payloads) |
| Message Retention | How long messages stay | 4 days | 14 days (min 60 s) |
| Visibility Timeout | How long a message is hidden after being received | 30 sec | 12 hours |
| Delivery Delay | Initial delay before message is visible | 0 sec | 15 min |
| Long Poll Wait | Time to wait for messages | 0 (short) | 20 sec |
| Batch Size | Messages per receive/send call | — | 10 |
| In-flight messages | Received but not deleted | — | ~120,000 (standard), 120,000 (FIFO) |

---

### Visibility Timeout

The most critical concept in SQS. When a consumer receives a message, it becomes **invisible** to other consumers for the visibility timeout duration. This prevents double-processing while the consumer works.

```
Timeline:
  t=0s:   Consumer A receives message → message becomes invisible for 30s
  t=15s:  Consumer A is still processing
  t=30s:  Visibility timeout expires → message becomes visible again!
  t=30s:  Consumer B receives the SAME message (double processing!)
  t=32s:  Consumer A finishes and calls DeleteMessage → too late, B already has it

Fix 1: Set visibility timeout > max processing time
Fix 2: Extend visibility timeout programmatically while processing
```

```java
// SqsConsumerService.java — manual consumer with visibility timeout extension
@Service
@RequiredArgsConstructor
@Slf4j
public class SqsConsumerService {

    private final SqsClient sqsClient;

    @Value("${app.sqs.queue-url}")
    private String queueUrl;

    public void pollAndProcess() {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(20)           // long polling
                .attributeNamesWithStrings("All")
                .build();

        List<Message> messages = sqsClient.receiveMessage(request).messages();

        for (Message message : messages) {
            String receiptHandle = message.receiptHandle();
            try {
                // Extend visibility if processing might take longer than default
                sqsClient.changeMessageVisibility(
                        ChangeMessageVisibilityRequest.builder()
                                .queueUrl(queueUrl)
                                .receiptHandle(receiptHandle)
                                .visibilityTimeout(60)  // give 60 more seconds
                                .build());

                processMessage(message.body());

                // Delete ONLY after successful processing
                sqsClient.deleteMessage(DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(receiptHandle)
                        .build());

            } catch (Exception e) {
                log.error("Failed to process message {}: {}", message.messageId(), e.getMessage());
                // Do NOT delete — message becomes visible again after timeout → retry
            }
        }
    }
}
```


> With `@SqsListener` you set it via `messageVisibilitySeconds` ([@SqsListener](#sqslistener-spring-cloud-aws)) and change it per message for back-off ([Error Handling, Retries & Back-off](#error-handling-retries--back-off)).

---

### Dead Letter Queue (DLQ)

When a message fails processing repeatedly (exceeds `maxReceiveCount`), SQS moves it to a Dead Letter Queue. This prevents "poison pill" messages from blocking the queue forever.

```bash
# Step 1: Create the DLQ
aws sqs create-queue --queue-name my-queue-dlq

# Get DLQ ARN
DLQ_ARN=$(aws sqs get-queue-attributes \
  --queue-url https://sqs.us-east-1.amazonaws.com/123456789/my-queue-dlq \
  --attribute-names QueueArn \
  --query 'Attributes.QueueArn' --output text)

# Step 2: Set redrive policy on the main queue
aws sqs set-queue-attributes \
  --queue-url https://sqs.us-east-1.amazonaws.com/123456789/my-queue \
  --attributes "{
    \"RedrivePolicy\": \"{\\\"deadLetterTargetArn\\\":\\\"$DLQ_ARN\\\",\\\"maxReceiveCount\\\":\\\"3\\\"}\"
  }"
# After 3 failed receives, message goes to DLQ

# Move messages from DLQ back to source queue for reprocessing:
aws sqs start-message-move-task \
  --source-arn "arn:aws:sqs:us-east-1:123456789:my-queue-dlq" \
  --destination-arn "arn:aws:sqs:us-east-1:123456789:my-queue" \
  --max-number-of-messages-per-second 10
```

> Spring Boot side of DLQs (creating queues with a redrive policy, a DLQ listener, redriving via code): [DLQ Handling in Spring Boot](#dlq-handling-in-spring-boot).

**Monitor DLQ depth.** The recommended way is a **CloudWatch alarm** (no code):

```bash
aws cloudwatch put-metric-alarm \
  --alarm-name "my-queue-dlq-not-empty" \
  --namespace AWS/SQS \
  --metric-name ApproximateNumberOfMessagesVisible \
  --dimensions Name=QueueName,Value=my-queue-dlq \
  --statistic Maximum --period 60 --evaluation-periods 1 \
  --threshold 0 --comparison-operator GreaterThanThreshold \
  --alarm-actions arn:aws:sns:us-east-1:123456789:ops-alerts
```

> DLQ rules: the DLQ must be the **same type** (FIFO → FIFO) in the **same account and region**, and its retention should be **longer** than the source queue's, because a standard message keeps its original enqueue timestamp when moved.

Or check it from the application:
```java
// DlqMonitorService.java
@Service
@RequiredArgsConstructor
@Slf4j
public class DlqMonitorService {

    private final SqsClient sqsClient;

    @Value("${app.sqs.dlq-url}")
    private String dlqUrl;

    @Scheduled(fixedDelay = 60_000)   // check every minute
    public void checkDlqDepth() {
        GetQueueAttributesResponse attrs = sqsClient.getQueueAttributes(
                GetQueueAttributesRequest.builder()
                        .queueUrl(dlqUrl)
                        .attributeNamesWithStrings(
                                "ApproximateNumberOfMessages",
                                "ApproximateNumberOfMessagesNotVisible")
                        .build());

        int depth = Integer.parseInt(
                attrs.attributesAsStrings().get("ApproximateNumberOfMessages"));

        if (depth > 0) {
            log.error("DLQ has {} unprocessed messages — investigation required", depth);
            // trigger alert, publish metric, etc.
        }
    }
}
```

---

### Long Polling vs Short Polling

**Polling** is how consumers ask SQS for messages. Always use **long polling**: it waits for messages to arrive instead of returning empty responses immediately, which reduces cost (you pay per request) and latency.

```
Short Polling (WaitTimeSeconds=0):
  Client → SQS: "Any messages?"
  SQS checks a SUBSET of servers → "No messages" (even if messages exist on other servers!)
  Client: immediately polls again
  Result: high CPU, high API cost, empty responses, potential missed messages

Long Polling (WaitTimeSeconds=1-20):
  Client → SQS: "Any messages? I'll wait up to 20 seconds"
  SQS checks ALL servers, waits if empty
  SQS → Client: returns when a message arrives OR after 20 seconds
  Result: lower cost, fewer API calls, more reliable
```

```bash
# Set long polling at queue level (applies to all consumers):
aws sqs set-queue-attributes \
  --queue-url $QUEUE_URL \
  --attributes '{"ReceiveMessageWaitTimeSeconds":"20"}'
```

```java
// Always use WaitTimeSeconds=20 for polling in production:
ReceiveMessageRequest longPollRequest = ReceiveMessageRequest.builder()
        .queueUrl(queueUrl)
        .waitTimeSeconds(20)       // long poll — always do this
        .maxNumberOfMessages(10)
        .build();
```

---

### Message Lifecycle

A message moves between three states: **available** (can be received), **in flight** (received and hidden for the visibility timeout) and **deleted**. If the consumer doesn't delete it in time, it becomes available again and its **receive count** increases.

```
                    ┌──────────────────────────────────────────────────────────┐
                    │                    SQS Queue                              │
 Producer           │                                                           │
   │                │  ┌─────────┐                                             │
   │──send──────────┤  │ Message │ ← AVAILABLE (visible to consumers)          │
                    │  └────┬────┘                                             │
                    │       │ receiveMessage()                                  │
                    │       ▼                                                   │
                    │  ┌─────────┐                                             │
                    │  │ Message │ ← IN FLIGHT (invisible, being processed)    │
                    │  └────┬────┘                                             │
                    │       │                                                   │
                    │   ┌───┴───┐                                              │
                    │   │       │                                               │
                    │ delete  timeout                                           │
                    │   │     expires                                           │
                    │   │       │                                               │
                    │ DELETED  AVAILABLE again (for retry)                     │
                    │                                                           │
                    │ After maxReceiveCount retries → moves to DLQ             │
                    └──────────────────────────────────────────────────────────┘
```

---

### Delay Queue & Message Timers

A **delay** keeps a new message invisible for up to 15 minutes after it's sent. Set it for the whole queue (`DelaySeconds`) or per message (**message timer**). FIFO queues support only the queue-level delay. For delays longer than 15 minutes, use [EventBridge Scheduler](#4-aws-eventbridge-scheduler) one-time schedules.

```bash
# Delay Queue: ALL messages are delayed by N seconds before becoming visible
aws sqs set-queue-attributes \
  --queue-url $QUEUE_URL \
  --attributes '{"DelaySeconds":"60"}'

# Per-message delay (overrides queue delay):
aws sqs send-message \
  --queue-url $QUEUE_URL \
  --message-body "Process this in 5 minutes" \
  --delay-seconds 300
```

```java
// Per-message delay in Java:
sqsClient.sendMessage(SendMessageRequest.builder()
        .queueUrl(queueUrl)
        .messageBody("Delayed task payload")
        .delaySeconds(300)  // visible after 5 minutes
        .build());
```

---

### SQS + Lambda Event Source Mapping

Lambda can be triggered directly by SQS. Lambda manages polling, scaling, and deletion internally.

```bash
# Create event source mapping:
aws lambda create-event-source-mapping \
  --event-source-arn "arn:aws:sqs:us-east-1:123456789:my-queue" \
  --function-name my-processor-lambda \
  --batch-size 10 \
  --maximum-batching-window-in-seconds 5 \
  --function-response-types ReportBatchItemFailures
```

**Java Lambda handler with partial batch failure reporting:**
```java
// OrderProcessorHandler.java — deployed as AWS Lambda
public class OrderProcessorHandler
        implements RequestHandler<SQSEvent, SQSBatchResponse> {

    private final OrderService orderService = new OrderService();

    @Override
    public SQSBatchResponse handleRequest(SQSEvent event, Context context) {
        List<SQSBatchResponse.BatchItemFailure> failures = new ArrayList<>();

        for (SQSEvent.SQSMessage record : event.getRecords()) {
            try {
                OrderEvent order = parseOrder(record.getBody());
                orderService.process(order);
                // SUCCESS: Lambda auto-deletes this message

            } catch (Exception e) {
                LambdaLogger logger = context.getLogger();
                logger.log("Failed to process " + record.getMessageId() + ": " + e.getMessage());
                // Report failure → only THIS message returns to queue
                // Other messages in the batch are still deleted
                failures.add(SQSBatchResponse.BatchItemFailure.builder()
                        .withItemIdentifier(record.getMessageId())
                        .build());
            }
        }
        return SQSBatchResponse.builder()
                .withBatchItemFailures(failures)
                .build();
        // Without ReportBatchItemFailures: one failure = entire batch retried
        // With ReportBatchItemFailures: only failed messages retry
    }
}
```

**Lambda + SQS scaling behavior (standard queues):**
```
Start:            Lambda polls with a few concurrent pollers
Scale-up rate:    adds up to 300 concurrent executions per minute while messages are waiting
Upper bound:      up to 1,250 concurrent executions per event source mapping,
                  limited by the account's Lambda concurrency (default 1,000 per region)
Control it:       "MaximumConcurrency" on the event source mapping (2–1,000) caps
                  concurrency per queue (better than reserved concurrency, which causes throttling)
FIFO queues:      concurrency ≤ number of active message groups (order is kept per group)
```

> Set the queue's **visibility timeout to at least 6× the Lambda function timeout** (AWS recommendation), so messages aren't redelivered while a batch is still being retried.

---

### Security

Access to a queue is controlled by **IAM policies** (on the producer/consumer roles) and an optional **queue policy** (resource-based, required for other AWS services or accounts to send to the queue). Encryption at rest is **SSE-SQS** (enabled by default for new queues) or **SSE-KMS** with your own key.

```jsonc
// SQS Queue Policy (resource-based) — allow SNS to send messages:
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": { "Service": "sns.amazonaws.com" },
      "Action": "sqs:SendMessage",
      "Resource": "arn:aws:sqs:us-east-1:123456789:my-queue",
      "Condition": {
        "ArnEquals": {
          "aws:SourceArn": "arn:aws:sns:us-east-1:123456789:my-topic"
        }
      }
    }
  ]
}
```

```bash
# Enable SSE (Server-Side Encryption) with KMS:
aws sqs set-queue-attributes \
  --queue-url $QUEUE_URL \
  --attributes '{
    "KmsMasterKeyId": "arn:aws:kms:us-east-1:123456789:key/abcd-1234",
    "KmsDataKeyReusePeriodSeconds": "300"
  }'
```

---

### Scaling Patterns

Common ways to use queues to absorb load and control processing:

```
Pattern 1: Throttle downstream processing
  Producer (burst: 10,000 msg/sec)
       ↓
  SQS Queue (absorbs burst)
       ↓
  Consumer (steady: 100 msg/sec)

Pattern 2: Priority queues (separate queues per priority)
  High-priority queue   → dedicated consumers (always running)
  Medium-priority queue → consumers start when high-priority is empty
  Low-priority queue    → consumers start when medium is empty

Pattern 3: Fan-out via SNS → multiple SQS (see SNS section)

Pattern 4: Request-reply with correlation ID
  Requester → Request Queue → Worker
  Worker    → Reply Queue   → Requester (matches via correlationId in MessageAttributes)
```

---

### Spring Boot with the AWS SDK (SqsClient)

This subsection uses the **AWS SDK v2** directly (`SqsClient`): full control, no extra framework. The next subsections show the higher-level **Spring Cloud AWS** approach (`@SqsListener`, `SqsTemplate`), which is recommended for most applications.

**Maven dependencies:**
```xml
<dependencies>
    <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>sqs</artifactId>
    </dependency>
    <!-- Spring Cloud AWS — @SqsListener, auto-serialization, auto-delete -->
    <dependency>
        <groupId>io.awspring.cloud</groupId>
        <artifactId>spring-cloud-aws-starter-sqs</artifactId>
        <!-- version managed by spring-cloud-aws-dependencies BOM -->
    </dependency>
</dependencies>
```

```yaml
# application.yml
spring:
  cloud:
    aws:
      region:
        static: us-east-1
      sqs:
        listener:
          max-concurrent-messages: 10      # per listener container
          max-messages-per-poll: 10
          poll-timeout: 20s                 # long polling

app:
  sqs:
    queue-url: https://sqs.us-east-1.amazonaws.com/123456789/my-queue
    fifo-queue-url: https://sqs.us-east-1.amazonaws.com/123456789/my-queue.fifo
    dlq-url: https://sqs.us-east-1.amazonaws.com/123456789/my-queue-dlq
```

```java
// SqsConfig.java
@Configuration
public class SqsConfig {

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
```

```java
// SqsProducerService.java
@Service
@RequiredArgsConstructor
@Slf4j
public class SqsProducerService {

    private final SqsClient    sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${app.sqs.queue-url}")
    private String queueUrl;

    @Value("${app.sqs.fifo-queue-url}")
    private String fifoQueueUrl;

    // Send a single message
    public String sendMessage(Object payload) {
        try {
            SendMessageResponse response = sqsClient.sendMessage(
                    SendMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .messageBody(objectMapper.writeValueAsString(payload))
                            .messageAttributes(Map.of(
                                    "event_type", MessageAttributeValue.builder()
                                            .dataType("String")
                                            .stringValue("order.created")
                                            .build()
                            ))
                            .build());
            log.info("Sent message: {}", response.messageId());
            return response.messageId();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize message", e);
        }
    }

    // Send a batch (up to 10 — reduces API cost by 10x)
    public void sendBatch(List<Object> payloads) {
        List<SendMessageBatchRequestEntry> entries = new ArrayList<>();
        for (int i = 0; i < payloads.size(); i++) {
            try {
                entries.add(SendMessageBatchRequestEntry.builder()
                        .id(String.valueOf(i))
                        .messageBody(objectMapper.writeValueAsString(payloads.get(i)))
                        .build());
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Serialization failed at index " + i, e);
            }
        }
        SendMessageBatchResponse response = sqsClient.sendMessageBatch(
                SendMessageBatchRequest.builder()
                        .queueUrl(queueUrl)
                        .entries(entries)
                        .build());

        if (!response.failed().isEmpty()) {
            log.error("{} messages failed to send", response.failed().size());
        }
    }

    // Send to FIFO queue with ordering guarantee
    public void sendToFifo(Object payload, String messageGroupId, String deduplicationId) {
        try {
            sqsClient.sendMessage(SendMessageRequest.builder()
                    .queueUrl(fifoQueueUrl)
                    .messageBody(objectMapper.writeValueAsString(payload))
                    .messageGroupId(messageGroupId)               // all msgs for this group are ordered
                    .messageDeduplicationId(deduplicationId)      // dedup window: 5 minutes
                    .build());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize message", e);
        }
    }

    // Get queue depth metrics
    public Map<String, Integer> getQueueDepth() {
        GetQueueAttributesResponse attrs = sqsClient.getQueueAttributes(
                GetQueueAttributesRequest.builder()
                        .queueUrl(queueUrl)
                        .attributeNamesWithStrings(
                                "ApproximateNumberOfMessages",
                                "ApproximateNumberOfMessagesNotVisible")
                        .build());
        Map<String, Integer> depth = new HashMap<>();
        depth.put("visible",  Integer.parseInt(attrs.attributesAsStrings().get("ApproximateNumberOfMessages")));
        depth.put("inFlight", Integer.parseInt(attrs.attributesAsStrings().get("ApproximateNumberOfMessagesNotVisible")));
        return depth;
    }

    // Purge all messages (careful — irreversible!)
    public void purgeQueue() {
        sqsClient.purgeQueue(PurgeQueueRequest.builder().queueUrl(queueUrl).build());
        log.warn("Queue purged: {}", queueUrl);
    }
}
```

```java
// OrderEvent.java — message payload POJO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private Long   orderId;
    private Long   customerId;
    private BigDecimal amount;
    private String status;
}
```

> The Spring Cloud AWS way (`@SqsListener`, `SqsTemplate`) is covered in the following subsections.

> **Idempotency:** standard queues deliver **at least once**, and any queue redelivers after a crash or timeout. Make consumers idempotent: store processed message IDs (or a business key) in a table with a unique constraint and skip duplicates.

---

### Spring Cloud AWS Setup & Auto-configured Beans

**Spring Cloud AWS** wraps the AWS SDK with Spring-style features: `@SqsListener` methods, `SqsTemplate`/`SnsTemplate`, JSON conversion and Boot auto-configuration. The rest of this section uses it.

> Versions: Spring Cloud AWS **3.x** (`io.awspring.cloud`) works with Spring Boot 3.x and AWS SDK v2. The older 2.x line (`org.springframework.cloud:spring-cloud-aws-*`, `@SqsListener` with `deletionPolicy`) is end-of-life; attribute names differ.

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.awspring.cloud</groupId>
            <artifactId>spring-cloud-aws-dependencies</artifactId>
            <version>3.4.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>io.awspring.cloud</groupId>
        <artifactId>spring-cloud-aws-starter-sqs</artifactId>
    </dependency>
    <dependency>
        <groupId>io.awspring.cloud</groupId>
        <artifactId>spring-cloud-aws-starter-sns</artifactId>
    </dependency>
</dependencies>
```

With the starters on the classpath, Spring Boot auto-configures:

| Bean | Starter | Used for |
|---|---|---|
| `SqsAsyncClient` | sqs | Low-level SQS client used by listeners and the template |
| `SqsTemplate` (`SqsOperations`, `SqsAsyncOperations`) | sqs | Sending / receiving messages |
| `SqsMessageListenerContainerFactory` (bean name `defaultSqsListenerContainerFactory`) | sqs | Creates a listener container for every `@SqsListener` |
| `SnsClient` | sns | Low-level SNS client |
| `SnsTemplate` (`SnsOperations`) | sns | Publishing notifications |
| `SnsSmsTemplate` (`SnsSmsOperations`) | sns | Sending SMS |

Any of them can be replaced by declaring your own bean of the same type.

The examples below use the `OrderEvent` class defined in the previous subsection.

---

### @SqsListener (Spring Cloud AWS)

**Package:** `io.awspring.cloud.sqs.annotation.SqsListener`
**Applies to:** Method of a Spring bean (also usable as a meta-annotation)


**Annotations used with SQS:**

| Annotation | Package | Used on | Purpose |
|---|---|---|---|
| `@SqsListener` | `io.awspring.cloud.sqs.annotation` | Method | Consume messages from one or more SQS queues |
| `@Payload` | `org.springframework.messaging.handler.annotation` | Listener parameter | Explicitly mark the message body parameter (optional) |
| `@Header` | `org.springframework.messaging.handler.annotation` | Listener parameter | Inject one message header / SQS attribute |
| `@Headers` | `org.springframework.messaging.handler.annotation` | Listener parameter (`Map`) | Inject all headers |
| `@SnsNotificationMessage` | `io.awspring.cloud.sqs.annotation` | Listener parameter | Unwrap an SNS envelope (see [SNS](#consuming-sns-messages-in-sqs-listeners-snsnotificationmessage)) |
| `@EnableSqs` | `io.awspring.cloud.sqs.config` | Configuration class | Enable `@SqsListener` without Boot auto-configuration (not needed with the starter) |

There are **no annotations for DLQs**: a DLQ is configured on the **queue** (redrive policy); Spring code only reacts to it (see [DLQ Handling in Spring Boot](#dlq-handling-in-spring-boot)).

#### What it does
Registers the method as a **consumer** of one or more SQS queues. For each annotated method, Spring creates a **listener container** that:
1. **Polls** the queue (long polling) in the background.
2. **Converts** each message body (JSON by default, via Jackson) to the parameter type.
3. **Invokes** your method, with up to `maxConcurrentMessages` messages in parallel.
4. **Acknowledges** (= deletes) the message according to the acknowledgement mode, by default only if the method returns normally.

If the method throws, the message is **not deleted**: it becomes visible again after the visibility timeout and is retried, and after `maxReceiveCount` attempts SQS moves it to the DLQ.

#### Attributes

All attributes are **`String`s**, so they accept property placeholders (`"${app.queues.orders}"`) and SpEL (`"#{...}"`).

| Attribute | Type | Default | Description |
|---|---|---|---|
| `value` / `queueNames` | `String[]` | `{}` | Queue **names**, **URLs** or **ARNs** to listen to. Several queues → one container polling all of them. |
| `id` | `String` | generated | Container id. Use it to look up the container (e.g. via `MessageListenerContainerRegistry`) to **start/stop** it at runtime. |
| `factory` | `String` | `""` → `defaultSqsListenerContainerFactory` | Bean name of the `SqsMessageListenerContainerFactory` to use (for different settings per listener: client, error handler, interceptors...). |
| `maxConcurrentMessages` | `String` (int) | `"10"` | Maximum number of messages **processed at the same time** by this container (per queue). Controls parallelism and back pressure. |
| `maxMessagesPerPoll` | `String` (int) | `"10"` | Messages requested per `ReceiveMessage` call (SQS maximum is 10). In batch mode this is the batch size. |
| `pollTimeoutSeconds` | `String` (int) | `"10"` | Long-polling wait time per receive call (max **20**). |
| `messageVisibilitySeconds` | `String` (int) | queue's own setting | Visibility timeout requested when receiving. Must be longer than your processing time. |
| `acknowledgementMode` | `String` | `"ON_SUCCESS"` | When messages are deleted: `ON_SUCCESS`, `ALWAYS` or `MANUAL` (see [Acknowledgement Modes](#acknowledgement-modes)). |

> Depending on your Spring Cloud AWS version there may be a few more attributes (for example `maxDelayBetweenPollsSeconds`); anything not on the annotation can always be set on the [container factory](#sqsmessagelistenercontainerfactory).

#### Example: all attributes

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderListeners {

    private final OrderService orderService;

    // Simplest form: queue name, JSON → OrderEvent, delete on success
    @SqsListener("orders-queue")
    public void onOrder(OrderEvent event) {
        orderService.process(event);
    }

    // Every attribute, values from application.yml
    @SqsListener(
            value = "${app.sqs.orders-queue}",              // name, URL or ARN
            id = "orders-listener",                          // to start/stop it at runtime
            factory = "ordersContainerFactory",              // custom container factory bean
            maxConcurrentMessages = "20",                    // up to 20 messages in parallel
            maxMessagesPerPoll = "10",                       // ReceiveMessage batch size
            pollTimeoutSeconds = "20",                       // long polling
            messageVisibilitySeconds = "120",                // 2 minutes to process each message
            acknowledgementMode = "ON_SUCCESS")              // delete only when the method succeeds
    public void onOrderTuned(OrderEvent event) {
        orderService.process(event);
    }

    // Several queues, one method
    @SqsListener({"orders-eu-queue", "orders-us-queue"})
    public void onAnyRegion(OrderEvent event,
                            @Header(SqsHeaders.SQS_QUEUE_NAME_HEADER) String queueName) {
        log.info("Order {} from {}", event.getOrderId(), queueName);
    }
}
```

#### Starting and stopping a listener at runtime

```java
@Service
@RequiredArgsConstructor
public class ListenerControlService {

    private final MessageListenerContainerRegistry registry;

    public void pauseOrders()  { registry.getContainerById("orders-listener").stop(); }
    public void resumeOrders() { registry.getContainerById("orders-listener").start(); }
}
```

#### Custom meta-annotation

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@SqsListener(value = "${app.sqs.orders-queue}", maxConcurrentMessages = "20", pollTimeoutSeconds = "20")
public @interface OrdersListener { }

@OrdersListener
public void handle(OrderEvent event) { ... }
```

#### Common mistakes / best practices
- By default a **missing queue is created** automatically (`queueNotFoundStrategy = CREATE`). In production set it to `FAIL` (see [SqsMessageListenerContainerFactory](#sqsmessagelistenercontainerfactory)) so a typo in the queue name fails at startup instead of silently creating a new, unused queue.
- Listener methods must be **idempotent**: SQS delivers at least once.
- Don't catch and swallow all exceptions inside the listener unless you mean "acknowledge anyway": a normal return **deletes** the message.
- `messageVisibilitySeconds` (or the queue's visibility timeout) must exceed the worst-case processing time, or the same message is processed twice in parallel.

---

### @SqsListener Method Arguments

A `@SqsListener` method can declare any combination of these parameters; Spring resolves each one from the received message.

| Parameter | Single-message listener | Batch listener | What you get |
|---|:---:|:---:|---|
| `MyPojo` / `String` / `byte[]` | ✅ | — | The converted body (JSON → POJO) |
| `@Payload MyPojo` | ✅ | — | Same, explicit |
| `org.springframework.messaging.Message<MyPojo>` | ✅ | — | Body **and** headers |
| `List<MyPojo>` / `List<Message<MyPojo>>` | — | ✅ | All messages of the poll (batch mode) |
| `@Header("name") T` | ✅ | — | One header: SQS message attribute, system attribute, or Spring Cloud AWS header |
| `@Headers Map<String, Object>` / `MessageHeaders` | ✅ | — | All headers |
| `Acknowledgement` | ✅ | — | Delete this message manually (`MANUAL` mode) |
| `BatchAcknowledgement<T>` | — | ✅ | Delete some or all messages of a batch manually |
| `Visibility` | ✅ | — | Change this message's visibility timeout (back-off) |
| `BatchVisibility` | — | ✅ | Change visibility for the batch |
| `QueueAttributes` | ✅ | ✅ | Attributes of the queue (URL, ARN, settings) |
| `software.amazon.awssdk.services.sqs.model.Message` | ✅ | ✅ (`List<...>`) | The raw SDK message (body string, receipt handle, attributes) |

**Useful header names** (constants in `io.awspring.cloud.sqs.listener.SqsHeaders`):

| Constant | Contains |
|---|---|
| `SqsHeaders.SQS_QUEUE_NAME_HEADER` | Queue name |
| `SqsHeaders.SQS_QUEUE_URL_HEADER` | Queue URL |
| `SqsHeaders.SQS_RECEIPT_HANDLE_HEADER` | Receipt handle (needed to delete / change visibility) |
| `SqsHeaders.SQS_VISIBILITY_TIMEOUT_HEADER` | A `Visibility` object for this message |
| `SqsHeaders.MessageSystemAttributes.SQS_APPROXIMATE_RECEIVE_COUNT` | How many times the message has been received (1 = first attempt) |
| `SqsHeaders.MessageSystemAttributes.SQS_SENT_TIMESTAMP` | When the message was sent (epoch millis) |
| `SqsHeaders.MessageSystemAttributes.SQS_MESSAGE_GROUP_ID_HEADER` | FIFO message group id |
| `SqsHeaders.MessageSystemAttributes.SQS_MESSAGE_DEDUPLICATION_ID_HEADER` | FIFO deduplication id |
| `MessageHeaders.ID` | Message id (derived from the SQS `MessageId`) |
| `"your_attribute_name"` | Any **custom message attribute** set by the producer appears as a header with its own name |

#### Example

```java
@Component
@Slf4j
public class ArgumentExamples {

    // Body + one custom attribute + a system attribute
    @SqsListener("orders-queue")
    public void handle(@Payload OrderEvent event,
                       @Header("event_type") String eventType,
                       @Header(SqsHeaders.MessageSystemAttributes.SQS_APPROXIMATE_RECEIVE_COUNT) int receiveCount) {
        log.info("{} attempt #{} for order {}", eventType, receiveCount, event.getOrderId());
    }

    // Whole Spring Message (body + headers)
    @SqsListener("audit-queue")
    public void audit(Message<OrderEvent> message) {
        log.info("Headers: {}", message.getHeaders());
        log.info("Payload: {}", message.getPayload());
    }

    // Raw SDK message (no conversion)
    @SqsListener("raw-queue")
    public void raw(software.amazon.awssdk.services.sqs.model.Message message) {
        log.info("Body={} receiptHandle={}", message.body(), message.receiptHandle());
    }

    // Queue metadata
    @SqsListener("orders-queue")
    public void withQueue(OrderEvent event, QueueAttributes queue) {
        log.info("From queue {}", queue.getQueueUrl());
    }
}
```

#### Batch listener

A method whose parameter is a `List` receives **all messages of a poll at once** (up to `maxMessagesPerPoll`). Useful for bulk database inserts.

```java
@SqsListener(value = "metrics-queue", maxMessagesPerPoll = "10")
public void onBatch(List<Message<MetricEvent>> messages) {
    metricRepository.saveAll(messages.stream().map(Message::getPayload).toList());
    // ON_SUCCESS: the whole batch is deleted if this returns normally,
    // and the whole batch is retried if it throws (see BatchAcknowledgement for partial success)
}
```

---

### Acknowledgement Modes

**Acknowledging** a message = **deleting** it from the queue. The mode decides when that happens.

| Mode | Message deleted when... | Use for |
|---|---|---|
| `ON_SUCCESS` (default) | The listener returns normally (or the error handler recovers) | Almost everything |
| `ALWAYS` | After processing, **even if it failed** | Best-effort messages where a retry is useless (metrics, logs) |
| `MANUAL` | Only when **you** call `acknowledge()` | Partial batch success, async processing, acknowledging later |

#### MANUAL: single message

```java
@SqsListener(value = "payments-queue", acknowledgementMode = "MANUAL")
public void onPayment(PaymentEvent event, Acknowledgement acknowledgement) {
    if (paymentService.tryCharge(event)) {
        acknowledgement.acknowledge();          // delete now
    }
    // not acknowledged → message reappears after the visibility timeout → retry
}
```

#### MANUAL: partial batch success

```java
@SqsListener(value = "orders-queue", acknowledgementMode = "MANUAL", maxMessagesPerPoll = "10")
public void onBatch(List<Message<OrderEvent>> messages, BatchAcknowledgement<OrderEvent> ack) {
    List<Message<OrderEvent>> ok = new ArrayList<>();
    for (Message<OrderEvent> m : messages) {
        try {
            orderService.process(m.getPayload());
            ok.add(m);
        } catch (Exception e) {
            log.warn("Order {} failed, will retry", m.getPayload().getOrderId(), e);
        }
    }
    ack.acknowledge(ok);                         // only successful ones are deleted
}
```

> Acknowledgements are **batched** and sent asynchronously (`DeleteMessageBatch`) by default, every second or every 10 messages. Tune with `acknowledgementInterval` / `acknowledgementThreshold` in the container options; set both to zero for immediate deletes.

---

### SqsMessageListenerContainerFactory

**Package:** `io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory`

#### What it does
Creates the listener container behind each `@SqsListener`. Declare your own factory bean to change the **defaults for all listeners** (bean name `defaultSqsListenerContainerFactory`) or to create a **named** factory selected with `@SqsListener(factory = "...")`. Here you also plug in the error handler, interceptors and acknowledgement callbacks.

#### Container options (`configure(options -> ...)`)

| Option | Default | Description |
|---|---|---|
| `maxConcurrentMessages(int)` | `10` | Messages processed in parallel per queue. |
| `maxMessagesPerPoll(int)` | `10` | Messages per receive call / batch size. |
| `pollTimeout(Duration)` | `10s` | Long-polling wait (max 20 s). |
| `maxDelayBetweenPolls(Duration)` | `10s` | Max time to wait for free capacity before polling again (back pressure). |
| `messageVisibility(Duration)` | queue default | Visibility timeout requested on receive. |
| `listenerMode(ListenerMode)` | `SINGLE_MESSAGE` | `SINGLE_MESSAGE` or `BATCH` (normally detected from the method signature). |
| `acknowledgementMode(AcknowledgementMode)` | `ON_SUCCESS` | `ON_SUCCESS`, `ALWAYS`, `MANUAL`. |
| `acknowledgementInterval(Duration)` | `1s` | Flush pending deletes at this interval (`0` = immediately). |
| `acknowledgementThreshold(int)` | `10` | Flush pending deletes when this many are waiting (`0` = immediately). |
| `acknowledgementOrdering(AcknowledgementOrdering)` | `PARALLEL` (`ORDERED` for FIFO) | Whether deletes must follow processing order. |
| `queueNotFoundStrategy(QueueNotFoundStrategy)` | `CREATE` | `CREATE` the queue if missing, or `FAIL` at startup. **Use `FAIL` in production.** |
| `messageAttributeNames(Collection<String>)` | all | Which custom message attributes to fetch. |
| `messageSystemAttributeNames(...)` | all | Which system attributes to fetch (`ApproximateReceiveCount`, `SentTimestamp`...). |
| `backPressureMode(BackPressureMode)` | `AUTO` | How in-flight capacity limits polling. |
| `listenerShutdownTimeout(Duration)` | `20s` | How long to wait for in-flight messages on shutdown (graceful). |

#### Factory-level components

| Builder method | Plug in |
|---|---|
| `sqsAsyncClient(...)` | A specific `SqsAsyncClient` (other region/account/endpoint) |
| `errorHandler(...)` / `errorHandler(AsyncErrorHandler)` | What to do when the listener throws ([Error Handling, Retries & Back-off](#error-handling-retries--back-off)) |
| `messageInterceptor(...)` | Code that runs **before/after** every message (MDC, tracing, tenant context) |
| `acknowledgementResultCallback(...)` | Called after deletes succeed/fail |
| `containerComponentFactories(...)` | Advanced: custom polling/ack components |

#### Example

```java
@Configuration
public class SqsListenerConfig {

    // Replaces the default factory → applies to every @SqsListener without "factory"
    @Bean
    public SqsMessageListenerContainerFactory<Object> defaultSqsListenerContainerFactory(
            SqsAsyncClient sqsAsyncClient,
            OrderErrorHandler errorHandler,
            MdcMessageInterceptor mdcInterceptor) {

        return SqsMessageListenerContainerFactory.builder()
                .sqsAsyncClient(sqsAsyncClient)
                .configure(options -> options
                        .maxConcurrentMessages(20)
                        .maxMessagesPerPoll(10)
                        .pollTimeout(Duration.ofSeconds(20))
                        .messageVisibility(Duration.ofMinutes(2))
                        .acknowledgementMode(AcknowledgementMode.ON_SUCCESS)
                        .queueNotFoundStrategy(QueueNotFoundStrategy.FAIL)
                        .listenerShutdownTimeout(Duration.ofSeconds(30)))
                .errorHandler(errorHandler)
                .messageInterceptor(mdcInterceptor)
                .build();
    }

    // Named factory for slow, heavy jobs: @SqsListener(value = "reports-queue", factory = "slowJobsFactory")
    @Bean
    public SqsMessageListenerContainerFactory<Object> slowJobsFactory(SqsAsyncClient sqsAsyncClient) {
        return SqsMessageListenerContainerFactory.builder()
                .sqsAsyncClient(sqsAsyncClient)
                .configure(options -> options
                        .maxConcurrentMessages(2)
                        .maxMessagesPerPoll(1)
                        .messageVisibility(Duration.ofMinutes(15)))
                .build();
    }
}

// Interceptor: put the message id into the logging context
@Component
public class MdcMessageInterceptor implements MessageInterceptor<Object> {
    @Override
    public Message<Object> intercept(Message<Object> message) {
        MDC.put("messageId", String.valueOf(message.getHeaders().getId()));
        return message;
    }

    @Override
    public void afterProcessing(Message<Object> message, Throwable t) {
        MDC.remove("messageId");
    }
}
```

---

### Error Handling, Retries & Back-off

**What happens when a listener throws:**

```
listener throws
   │
   ├── ErrorHandler configured?
   │      ├── handler returns normally → treated as SUCCESS → message deleted (ON_SUCCESS)
   │      └── handler re-throws        → treated as FAILURE
   │
   └── FAILURE → message NOT deleted
                → visible again after the visibility timeout → received again (ApproximateReceiveCount + 1)
                → after maxReceiveCount receives → SQS moves it to the DLQ
```

So **retries come from SQS itself**: the number of attempts is `maxReceiveCount` in the queue's redrive policy, and the delay between attempts is the **visibility timeout**.

#### Error handler with exponential back-off

Change the failed message's visibility so the next attempt happens later (10 s, 20 s, 40 s...), and give up quickly on errors that will never succeed.

```java
@Component
@Slf4j
public class OrderErrorHandler implements ErrorHandler<Object> {

    @Override
    public void handle(Message<Object> message, Throwable t) {
        Throwable cause = t.getCause() != null ? t.getCause() : t;   // listener exceptions arrive wrapped

        if (cause instanceof ValidationException) {
            // Poison message: retrying won't help. Log it and let it go (handler returns → deleted),
            // or publish it to a "parking" queue yourself before returning.
            log.error("Invalid message dropped: {}", message.getPayload(), cause);
            return;
        }

        int receiveCount = Integer.parseInt(String.valueOf(message.getHeaders().get(
                SqsHeaders.MessageSystemAttributes.SQS_APPROXIMATE_RECEIVE_COUNT)));
        int delaySeconds = (int) Math.min(900, 10 * Math.pow(2, receiveCount - 1));   // 10, 20, 40 ... max 15 min

        Visibility visibility = message.getHeaders()
                .get(SqsHeaders.SQS_VISIBILITY_TIMEOUT_HEADER, Visibility.class);
        visibility.changeTo(delaySeconds);

        log.warn("Attempt {} failed, retrying in {} s", receiveCount, delaySeconds, cause);
        throw new RuntimeException(cause);                // re-throw → not deleted → retried later
    }
}
```

The same back-off can be done **inside a listener** by taking a `Visibility` parameter:

```java
@SqsListener("orders-queue")
public void handle(OrderEvent event, Visibility visibility,
                   @Header(SqsHeaders.MessageSystemAttributes.SQS_APPROXIMATE_RECEIVE_COUNT) int attempt) {
    try {
        orderService.process(event);
    } catch (TemporaryException e) {
        visibility.changeTo(30 * attempt);   // linear back-off
        throw e;
    }
}
```

> Exceptions thrown by a `@SqsListener` method reach the error handler wrapped in a `ListenerExecutionFailedException`; use `getCause()` to inspect the original.

---

### DLQ Handling in Spring Boot

A DLQ is just another SQS queue. The **source queue's redrive policy** (`deadLetterTargetArn` + `maxReceiveCount`) tells SQS where to move messages that failed too many times. Spring Boot is involved in three ways: **creating** the queues (local/dev), **watching** the DLQ, and **redriving** messages after a fix.

```
orders-queue  (redrive: maxReceiveCount = 5, DLQ = orders-dlq)
   │  attempt 1..5 fail
   ▼
orders-dlq   ──► @SqsListener (alert / store for analysis)   ──► after the fix: redrive back to orders-queue
```

#### Configuration values to choose

| Setting | Where | Recommendation |
|---|---|---|
| `maxReceiveCount` | Source queue redrive policy | 3–10. Too low → transient errors go to the DLQ; too high → slow poison messages block capacity. |
| `VisibilityTimeout` | Source queue (or listener) | > max processing time (× 6 for Lambda) |
| `MessageRetentionPeriod` | **DLQ** | Longer than the source queue's (e.g. 14 days), since the original enqueue time is kept |
| `RedriveAllowPolicy` | DLQ | Restrict which source queues may use it (`byQueue`) |
| Queue type | DLQ | Same type as the source: FIFO source → FIFO DLQ |

#### Creating a queue with a DLQ from Spring Boot (dev / LocalStack)

In production, create queues with infrastructure-as-code (Terraform, CloudFormation, CDK). For local development and tests, a startup runner is convenient:

```java
@Configuration
@Profile({"local", "test"})
@Slf4j
public class SqsQueueSetup {

    @Bean
    ApplicationRunner createQueues(SqsAsyncClient sqs) {
        return args -> {
            // 1. DLQ with long retention
            String dlqUrl = sqs.createQueue(r -> r.queueName("orders-dlq")
                    .attributes(Map.of(QueueAttributeName.MESSAGE_RETENTION_PERIOD, "1209600")))  // 14 days
                    .join().queueUrl();

            String dlqArn = sqs.getQueueAttributes(r -> r.queueUrl(dlqUrl)
                            .attributeNames(QueueAttributeName.QUEUE_ARN))
                    .join().attributes().get(QueueAttributeName.QUEUE_ARN);

            // 2. Source queue with redrive policy → after 5 failed receives, move to DLQ
            String redrivePolicy = """
                    {"deadLetterTargetArn":"%s","maxReceiveCount":"5"}""".formatted(dlqArn);

            sqs.createQueue(r -> r.queueName("orders-queue")
                    .attributes(Map.of(
                            QueueAttributeName.VISIBILITY_TIMEOUT, "60",
                            QueueAttributeName.RECEIVE_MESSAGE_WAIT_TIME_SECONDS, "20",
                            QueueAttributeName.REDRIVE_POLICY, redrivePolicy)))
                    .join();
            log.info("Created orders-queue with DLQ {}", dlqArn);
        };
    }
}
```

The same in **Terraform**:

```hcl
resource "aws_sqs_queue" "orders_dlq" {
  name                      = "orders-dlq"
  message_retention_seconds = 1209600
}

resource "aws_sqs_queue" "orders" {
  name                       = "orders-queue"
  visibility_timeout_seconds = 60
  receive_wait_time_seconds  = 20
  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.orders_dlq.arn
    maxReceiveCount     = 5
  })
}
```

#### Listening to the DLQ

A DLQ listener should **not** re-process automatically (the message already failed several times). Typical actions: alert, store for analysis, or park it.

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class OrdersDlqListener {

    private final FailedMessageRepository failedMessages;
    private final SnsTemplate snsTemplate;

    @SqsListener(value = "orders-dlq", maxConcurrentMessages = "1")
    public void onDeadLetter(software.amazon.awssdk.services.sqs.model.Message message) {
        failedMessages.save(new FailedMessage(message.messageId(), message.body(), Instant.now()));
        snsTemplate.sendNotification("ops-alerts",
                "Order message moved to DLQ: " + message.messageId(), "DLQ alert");
        log.error("DLQ message {} stored for analysis", message.messageId());
    }   // returns normally → deleted from the DLQ (it's now in the database)
}
```

> Alternative: **don't** consume the DLQ at all; keep messages there, alarm on `ApproximateNumberOfMessagesVisible > 0`, investigate, fix the bug, then redrive.

#### Redriving DLQ messages back to the source queue

```java
@Service
@RequiredArgsConstructor
public class DlqRedriveService {

    private final SqsAsyncClient sqs;

    // Uses SQS's built-in "message move task" (same as the console's "Start DLQ redrive")
    public String redrive(String dlqArn, String sourceQueueArn) {
        return sqs.startMessageMoveTask(r -> r
                        .sourceArn(dlqArn)
                        .destinationArn(sourceQueueArn)          // omit → back to the original source queues
                        .maxNumberOfMessagesPerSecond(10))       // throttle
                .join().taskHandle();
    }
}
```

Expose it through an admin endpoint (secured!) so operators can redrive after deploying a fix.

---

### FIFO Queues in Spring Boot

FIFO queue names end in `.fifo`. Spring Cloud AWS detects FIFO queues automatically and then:
- processes messages of the **same message group one at a time, in order** (different groups run in parallel),
- acknowledges in order,
- on failure, **stops** processing the rest of that group's batch so ordering isn't broken.

**Sending** (the group id is required; the deduplication id is required unless content-based deduplication is enabled):

```java
sqsTemplate.send(to -> to
        .queue("orders.fifo")
        .payload(event)
        .messageGroupId("customer-" + event.getCustomerId())     // order kept per customer
        .messageDeduplicationId("order-" + event.getOrderId()));  // duplicates within 5 min are dropped
```

**Receiving**:

```java
@SqsListener(value = "orders.fifo", maxConcurrentMessages = "10")
public void onFifo(OrderEvent event,
                   @Header(SqsHeaders.MessageSystemAttributes.SQS_MESSAGE_GROUP_ID_HEADER) String groupId) {
    log.info("Group {} → order {}", groupId, event.getOrderId());
}
```

> Throughput tip: choose a group id with **many distinct values** (customer id, account id). A single group id means strictly one message at a time for the whole queue.

---

### SqsTemplate (Sending & Receiving)

**Package:** `io.awspring.cloud.sqs.operations.SqsTemplate` (implements `SqsOperations` and `SqsAsyncOperations`)

#### Send options (`send(to -> to...)`)

| Method | Description |
|---|---|
| `queue(String)` | Target queue name, URL or ARN (or set a default queue on the template) |
| `payload(T)` | Message body, serialized to JSON |
| `header(String, Object)` / `headers(Map)` | Custom headers → SQS **message attributes** (max 10) |
| `delaySeconds(int)` | Per-message delay (0–900 s, standard queues) |
| `messageGroupId(String)` | FIFO group id |
| `messageDeduplicationId(String)` | FIFO deduplication id |

#### Receive options (`receive(from -> from...)`)

| Method | Description |
|---|---|
| `queue(String)` | Queue to read |
| `pollTimeout(Duration)` | Long-polling wait |
| `maxNumberOfMessages(int)` | For `receiveMany` (max 10) |
| `visibilityTimeout(Duration)` | Visibility for received messages |
| `additionalHeader(...)` | Extra headers on the returned message |

#### Template options (`SqsTemplate.builder().configure(o -> o...)`)

| Option | Description |
|---|---|
| `defaultQueue(String)` | Queue used when none is given |
| `acknowledgementMode(TemplateAcknowledgementMode)` | `ACKNOWLEDGE` (receive deletes automatically, default) or `MANUAL` |
| `queueNotFoundStrategy(QueueNotFoundStrategy)` | `CREATE` or `FAIL` |
| `sendBatchFailureHandlingStrategy(...)` | Throw or return partial results on `sendMany` failures |
| `defaultPollTimeout(Duration)` / `defaultMaxNumberOfMessages(int)` | Receive defaults |

#### Example

```java
@Service
@RequiredArgsConstructor
public class OrderQueueClient {

    private final SqsTemplate sqsTemplate;

    // Simple send
    public void send(OrderEvent event) {
        sqsTemplate.send("orders-queue", event);
    }

    // Send with attributes and delay
    public void sendDelayed(OrderEvent event) {
        SendResult<OrderEvent> result = sqsTemplate.send(to -> to
                .queue("orders-queue")
                .payload(event)
                .header("event_type", "order.created")
                .header("tenant", "acme")
                .delaySeconds(30));
        System.out.println("Sent " + result.messageId());
    }

    // Async send
    public CompletableFuture<SendResult<OrderEvent>> sendAsync(OrderEvent event) {
        return sqsTemplate.sendAsync("orders-queue", event);
    }

    // Batch send (up to 10 per call; the template splits larger lists)
    public void sendAll(List<OrderEvent> events) {
        List<Message<OrderEvent>> messages = events.stream()
                .map(e -> MessageBuilder.withPayload(e).build())
                .toList();
        sqsTemplate.sendMany("orders-queue", messages);
    }

    // Pull-style receive (e.g. in a scheduled job instead of a listener)
    public Optional<OrderEvent> receiveOne() {
        return sqsTemplate.receive(from -> from
                        .queue("orders-queue")
                        .pollTimeout(Duration.ofSeconds(10)), OrderEvent.class)
                .map(Message::getPayload);              // deleted automatically (ACKNOWLEDGE mode)
    }
}

// Custom template, e.g. with a default queue and manual acknowledgement
@Bean
public SqsTemplate reportsSqsTemplate(SqsAsyncClient client) {
    return SqsTemplate.builder()
            .sqsAsyncClient(client)
            .configure(o -> o
                    .defaultQueue("reports-queue")
                    .acknowledgementMode(TemplateAcknowledgementMode.MANUAL)
                    .queueNotFoundStrategy(QueueNotFoundStrategy.FAIL))
            .build();
}
```

---

### Testing SQS Listeners

**Integration test with LocalStack (Testcontainers):**

```java
@SpringBootTest
@Testcontainers
class OrderListenerIT {

    @Container
    static LocalStackContainer localstack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack:4.0"))
                    .withServices(LocalStackContainer.Service.SQS, LocalStackContainer.Service.SNS);

    @DynamicPropertySource
    static void awsProps(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.aws.endpoint", () -> localstack.getEndpoint().toString());
        registry.add("spring.cloud.aws.region.static", localstack::getRegion);
        registry.add("spring.cloud.aws.credentials.access-key", localstack::getAccessKey);
        registry.add("spring.cloud.aws.credentials.secret-key", localstack::getSecretKey);
    }

    @Autowired SqsTemplate sqsTemplate;
    @MockitoBean OrderService orderService;

    @Test
    void listenerProcessesOrder() {
        sqsTemplate.send("orders-queue", new OrderEvent(1L, 7L, new BigDecimal("10.00"), "NEW"));

        await().atMost(Duration.ofSeconds(10))
               .untilAsserted(() -> verify(orderService).process(any(OrderEvent.class)));
    }
}
```

**Unit test**: a `@SqsListener` method is a plain method, so call it directly with a payload and mocks; no AWS needed.

```java
@Test
void processesOrder() {
    OrderService service = mock(OrderService.class);
    new OrderListeners(service).onOrder(new OrderEvent(1L, 7L, BigDecimal.TEN, "NEW"));
    verify(service).process(any());
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 3. AWS SNS (Simple Notification Service)

### What It Is

Amazon SNS is a fully managed pub/sub messaging service. A **publisher** sends a message to a **topic**. SNS then fans that message out to all **subscribers** simultaneously. It supports push-based delivery to many endpoints: SQS queues, Lambda functions, HTTP/S endpoints, email, SMS, and mobile push notifications.

**Core value:** One event → multiple consumers, each getting their own copy.

```
                    ┌─── SQS Queue (orders-service)
Publisher ─► Topic ─┼─── Lambda (analytics-processor)
                    ├─── HTTP endpoint (webhook)
                    └─── Email (ops-team@company.com)
```

---

### Standard vs FIFO Topics

| Feature | Standard Topic | FIFO Topic |
|---|---|---|
| Throughput | Nearly unlimited | Lower per-topic limit (check current quotas; high-throughput mode available) |
| Ordering | Best-effort | Strict per message group |
| Deduplication | No | Yes (5-min window) |
| Subscribers | SQS, Lambda, HTTP/S, Email, SMS, Mobile push, Firehose | SQS queues (use FIFO queues to keep ordering) |
| Use Case | Notifications, fan-out | Ordered event streaming |

```bash
# Create Standard Topic:
aws sns create-topic --name my-topic

# Create FIFO Topic:
aws sns create-topic \
  --name my-topic.fifo \
  --attributes '{
    "FifoTopic": "true",
    "ContentBasedDeduplication": "true"
  }'
```

---

### Subscription Types

A **subscription** connects a topic to an endpoint. HTTP/S and email subscriptions must be **confirmed** by the endpoint before messages flow; SQS and Lambda subscriptions in the same account are confirmed automatically (the queue still needs a policy allowing SNS to send).

| Protocol | Delivery | Typical use |
|---|---|---|
| `sqs` | Push into a queue (durable) | Service-to-service fan-out |
| `lambda` | Invokes a function asynchronously | Serverless processing |
| `http` / `https` | POST to your endpoint | Webhooks |
| `email` / `email-json` | Email | Ops notifications |
| `sms` | Text message | OTPs, alerts |
| `application` | Mobile push (APNs, FCM) | App notifications |
| `firehose` | Kinesis Data Firehose | Archiving to S3 / analytics |

```bash
# Subscribe SQS queue to SNS topic:
aws sns subscribe \
  --topic-arn "arn:aws:sns:us-east-1:123456789:my-topic" \
  --protocol sqs \
  --notification-endpoint "arn:aws:sqs:us-east-1:123456789:my-queue"

# Subscribe Lambda:
aws sns subscribe \
  --topic-arn "arn:aws:sns:us-east-1:123456789:my-topic" \
  --protocol lambda \
  --notification-endpoint "arn:aws:lambda:us-east-1:123456789:function:my-function"

# Subscribe HTTP endpoint (AWS sends SubscriptionConfirmation → must confirm):
aws sns subscribe \
  --topic-arn "arn:aws:sns:us-east-1:123456789:my-topic" \
  --protocol https \
  --notification-endpoint "https://myapp.com/webhooks/sns"

# Subscribe email (requires manual confirmation click):
aws sns subscribe \
  --topic-arn "arn:aws:sns:us-east-1:123456789:my-topic" \
  --protocol email \
  --notification-endpoint "ops@company.com"

# Subscribe SMS:
aws sns subscribe \
  --topic-arn "arn:aws:sns:us-east-1:123456789:my-topic" \
  --protocol sms \
  --notification-endpoint "+15551234567"
```

---

### Message Filtering

Without filtering, every subscriber receives every message. Filter policies let each subscriber declare which messages it cares about — based on message attributes.

```bash
# Publish a message with attributes:
aws sns publish \
  --topic-arn "arn:aws:sns:us-east-1:123456789:my-topic" \
  --message '{"order_id": 123, "amount": 500}' \
  --message-attributes '{
    "event_type": {"DataType": "String", "StringValue": "order.created"},
    "region":     {"DataType": "String", "StringValue": "us-east"},
    "amount":     {"DataType": "Number", "StringValue": "500"}
  }'

# Set filter policy — only receive order.created events from us-east:
aws sns set-subscription-attributes \
  --subscription-arn "arn:aws:sns:us-east-1:123456789:my-topic:abc-123" \
  --attribute-name FilterPolicy \
  --attribute-value '{
    "event_type": ["order.created", "order.updated"],
    "region":     ["us-east", "us-west"]
  }'

# Numeric filter — only orders >= $100:
aws sns set-subscription-attributes \
  --subscription-arn "arn:aws:sns:us-east-1:123456789:my-topic:abc-123" \
  --attribute-name FilterPolicy \
  --attribute-value '{"amount": [{"numeric": [">=", 100]}]}'

# Filter on message body (not attributes):
aws sns set-subscription-attributes \
  --subscription-arn "arn:aws:sns:us-east-1:123456789:my-topic:abc-123" \
  --attribute-name FilterPolicyScope \
  --attribute-value MessageBody

aws sns set-subscription-attributes \
  --subscription-arn "arn:aws:sns:us-east-1:123456789:my-topic:abc-123" \
  --attribute-name FilterPolicy \
  --attribute-value '{"status": ["active"]}'
```

---

### Fan-Out Pattern (SNS + SQS)

The most common and powerful pattern. SNS delivers to multiple SQS queues simultaneously — each queue has its own consumers with independent retry, DLQ, and scaling.

```
                         ┌─── orders-sqs  ──► OrderService (Spring Boot consumer)
Order Created Event      │
       ↓                 ├─── inventory-sqs ► InventoryService (Spring Boot consumer)
   SNS Topic   ──────────┤
                         ├─── analytics-sqs ► AnalyticsService (Spring Boot consumer)
                         │
                         └─── notifications-sqs ► NotificationService (Spring Boot consumer)

Benefits:
- Each consumer operates independently (different speeds, different DLQs)
- One service failing does not affect others
- Add new consumers without touching the publisher
- Each SQS queue buffers messages for its consumer
```

```bash
# Infrastructure: set up fan-out via CLI
TOPIC_ARN=$(aws sns create-topic --name order-events --query 'TopicArn' --output text)

ORDERS_QUEUE=$(aws sqs create-queue --queue-name orders-processing --query 'QueueUrl' --output text)
ANALYTICS_QUEUE=$(aws sqs create-queue --queue-name analytics-events --query 'QueueUrl' --output text)

# Subscribe both queues to the topic:
aws sns subscribe --topic-arn $TOPIC_ARN --protocol sqs \
  --notification-endpoint $(aws sqs get-queue-attributes \
    --queue-url $ORDERS_QUEUE --attribute-names QueueArn \
    --query 'Attributes.QueueArn' --output text)

aws sns subscribe --topic-arn $TOPIC_ARN --protocol sqs \
  --notification-endpoint $(aws sqs get-queue-attributes \
    --queue-url $ANALYTICS_QUEUE --attribute-names QueueArn \
    --query 'Attributes.QueueArn' --output text) \
  --attributes RawMessageDelivery=true

# Each queue also needs a queue policy allowing this topic to send (see SQS → Security).
```

**Raw message delivery:** by default SNS wraps every message in a JSON **envelope** (`Type`, `MessageId`, `TopicArn`, `Message`, `Timestamp`, `Signature`...). With `RawMessageDelivery=true` on an SQS (or HTTP) subscription, the queue receives **only your original message body**, and message attributes become SQS message attributes. Enable it when the consumer is an `@SqsListener` expecting your payload type; otherwise unwrap the envelope with `@SnsNotificationMessage`:

```java
@SqsListener("analytics-events")
public void onOrder(@SnsNotificationMessage OrderEvent event) {   // Spring Cloud AWS unwraps the SNS envelope
    analytics.record(event);
}
```


> Publishing from Spring Boot: [SnsTemplate](#snstemplate--snssmstemplate-publishing-with-spring-cloud-aws). Consuming in each service: [@SqsListener](#sqslistener-spring-cloud-aws) (with raw delivery) or [@SnsNotificationMessage](#consuming-sns-messages-in-sqs-listeners-snsnotificationmessage).

---

### Message Attributes

**Message attributes** are typed metadata sent alongside the body (up to 10 per message). They're used by **filter policies**, and passed through to SQS subscribers with raw message delivery. Types: `String`, `String.Array`, `Number`, `Binary`.

```java
// Publish with multiple attribute types (Java SDK v2):
Map<String, MessageAttributeValue> attributes = new HashMap<>();

// String attribute:
attributes.put("event_type", MessageAttributeValue.builder()
        .dataType("String")
        .stringValue("order.created")
        .build());

// Number attribute (stored as string, treated as numeric for filtering):
attributes.put("amount", MessageAttributeValue.builder()
        .dataType("Number")
        .stringValue("299.99")
        .build());

// String.Array attribute (for array-based filter policies):
attributes.put("tags", MessageAttributeValue.builder()
        .dataType("String.Array")
        .stringValue("[\"premium\",\"new\"]")
        .build());

snsClient.publish(PublishRequest.builder()
        .topicArn(topicArn)
        .message(payload)
        .messageAttributes(attributes)
        .build());
```

---

### DLQ for SNS Subscriptions

When SNS fails to deliver to a subscriber (Lambda errors, HTTP endpoint down), messages can go to a per-subscription DLQ.

```bash
# Set DLQ on an SNS subscription:
aws sns set-subscription-attributes \
  --subscription-arn "arn:aws:sns:us-east-1:123456789:my-topic:abc-123" \
  --attribute-name RedrivePolicy \
  --attribute-value '{"deadLetterTargetArn": "arn:aws:sqs:us-east-1:123456789:sns-dlq"}'

```

**SNS delivery retry policy** (before a message goes to the subscription's DLQ):

| Phase | AWS-managed endpoints (SQS, Lambda, Firehose) | Customer endpoints (HTTP/S, email, SMS) |
|---|---|---|
| Immediate retries | 3 | 0 |
| Pre-backoff | 2 (1 s apart) | 2 (10 s apart) |
| Backoff | 10 (exponential 1 s → 20 s) | 10 (exponential 10 s → 600 s) |
| Post-backoff | 100,000 (20 s apart) | 38 (600 s apart) |
| **Total** | **100,015 attempts over ~23 days** | **50 attempts over ~6 hours** |

HTTP/S retry behaviour can be customized with a **delivery policy**. Client-side errors (e.g. the subscribed queue or function was deleted, or permissions are missing) are not retried; they go straight to the DLQ.

---

### SNS Large Message Support

SNS limits a message (body + attributes) to **256 KB**. For larger payloads, store the payload in **S3** and publish a **pointer** (the "claim check" pattern). AWS provides this as a library: the **Amazon SNS Extended Client Library for Java** (and the matching SQS Extended Client) do the upload/download transparently. The manual version looks like this:

```java
// Large message pattern: store in S3, send S3 reference via SNS
@Service
@RequiredArgsConstructor
public class SnsLargeMessageService {

    private final SnsClient  snsClient;
    private final S3Client   s3Client;
    private final ObjectMapper objectMapper;

    @Value("${app.sns.large-message-bucket}")
    private String bucketName;

    public void publishLargeMessage(String topicArn, Object payload) throws Exception {
        byte[] bytes = objectMapper.writeValueAsBytes(payload);

        if (bytes.length <= 250 * 1024) {
            // Under 250KB — publish directly
            snsClient.publish(PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(new String(bytes, StandardCharsets.UTF_8))
                    .build());
        } else {
            // Over limit — store in S3 first
            String s3Key = "sns-large-payloads/" + UUID.randomUUID() + ".json";

            s3Client.putObject(
                    PutObjectRequest.builder().bucket(bucketName).key(s3Key).build(),
                    RequestBody.fromBytes(bytes));

            // Send S3 pointer as the SNS message
            Map<String, String> pointer = Map.of(
                    "s3Bucket", bucketName,
                    "s3Key", s3Key,
                    "originalSize", String.valueOf(bytes.length)
            );
            snsClient.publish(PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(objectMapper.writeValueAsString(pointer))
                    .messageAttributes(Map.of(
                            "payloadType", MessageAttributeValue.builder()
                                    .dataType("String")
                                    .stringValue("S3_POINTER")
                                    .build()
                    ))
                    .build());
        }
    }
}
```

---

### Security

Like SQS, a topic is protected by IAM policies plus an optional **topic policy** (resource-based) that lets other services or accounts publish/subscribe, and can be encrypted with **SSE-KMS**. If the topic uses a customer-managed KMS key, publishers (including AWS services like EventBridge or CloudWatch) need `kms:GenerateDataKey*` and `kms:Decrypt` on that key.

```jsonc
// SNS Topic Policy — allow EventBridge to publish:
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": { "Service": "events.amazonaws.com" },
      "Action": "sns:Publish",
      "Resource": "arn:aws:sns:us-east-1:123456789:my-topic",
      "Condition": {
        "ArnEquals": {
          "aws:SourceArn": "arn:aws:events:us-east-1:123456789:rule/my-rule"
        }
      }
    }
  ]
}
```

```bash
# Enable SSE on SNS topic:
aws sns set-topic-attributes \
  --topic-arn "arn:aws:sns:us-east-1:123456789:my-topic" \
  --attribute-name KmsMasterKeyId \
  --attribute-value "arn:aws:kms:us-east-1:123456789:key/abcd-1234"
```

---

### Spring Boot with the AWS SDK (SnsClient)

This subsection uses the **AWS SDK v2** directly (`SnsClient`). The next subsections show **Spring Cloud AWS** (`SnsTemplate`, `@SnsNotificationMessage`, HTTP endpoint annotations).

**Maven dependencies:**
```xml
<dependencies>
    <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>sns</artifactId>
    </dependency>
    <dependency>
        <groupId>io.awspring.cloud</groupId>
        <artifactId>spring-cloud-aws-starter-sns</artifactId>
        <!-- version managed by spring-cloud-aws-dependencies BOM -->
    </dependency>
</dependencies>
```

```java
// SnsConfig.java
@Configuration
public class SnsConfig {

    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
```

```java
// SnsPublisherService.java
@Service
@RequiredArgsConstructor
@Slf4j
public class SnsPublisherService {

    private final SnsClient    snsClient;
    private final ObjectMapper objectMapper;

    @Value("${app.sns.order-events-topic-arn}")
    private String orderEventsTopicArn;

    // Publish single message to topic
    public String publishToTopic(String topicArn, Object payload, String eventType) {
        try {
            PublishResponse response = snsClient.publish(PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(objectMapper.writeValueAsString(payload))
                    .messageAttributes(Map.of(
                            "event_type", MessageAttributeValue.builder()
                                    .dataType("String")
                                    .stringValue(eventType)
                                    .build()
                    ))
                    .build());
            log.info("Published to SNS: messageId={}", response.messageId());
            return response.messageId();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize SNS payload", e);
        }
    }

    // Publish to FIFO topic (ordered, deduplicated)
    public void publishToFifoTopic(String topicArn, Object payload,
                                   String messageGroupId, String deduplicationId) {
        try {
            snsClient.publish(PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(objectMapper.writeValueAsString(payload))
                    .messageGroupId(messageGroupId)
                    .messageDeduplicationId(deduplicationId)
                    .build());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialization failed", e);
        }
    }

    // Publish batch (up to 10 messages)
    public void publishBatch(String topicArn, List<Object> payloads) {
        List<PublishBatchRequestEntry> entries = new ArrayList<>();
        for (int i = 0; i < payloads.size(); i++) {
            try {
                entries.add(PublishBatchRequestEntry.builder()
                        .id(String.valueOf(i))
                        .message(objectMapper.writeValueAsString(payloads.get(i)))
                        .build());
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed at index " + i, e);
            }
        }
        PublishBatchResponse response = snsClient.publishBatch(
                PublishBatchRequest.builder()
                        .topicArn(topicArn)
                        .publishBatchRequestEntries(entries)
                        .build());

        if (!response.failed().isEmpty()) {
            log.error("{} SNS batch messages failed", response.failed().size());
        }
    }

    // Direct SMS (no topic needed)
    public void sendSms(String phoneNumber, String message) {
        snsClient.publish(PublishRequest.builder()
                .phoneNumber(phoneNumber)
                .message(message)
                .messageAttributes(Map.of(
                        "AWS.SNS.SMS.SMSType", MessageAttributeValue.builder()
                                .dataType("String")
                                .stringValue("Transactional")   // vs Promotional
                                .build()
                ))
                .build());
    }

    // Per-protocol messages (different payload per subscriber type)
    public void publishMultiProtocol(String topicArn, OrderEvent order) {
        try {
            Map<String, String> messages = new HashMap<>();
            messages.put("default", "Order " + order.getOrderId() + " created");
            messages.put("sqs",     objectMapper.writeValueAsString(order)); // full payload for SQS
            messages.put("email",   "Your order #" + order.getOrderId() + " has been placed. Total: $" + order.getAmount());

            snsClient.publish(PublishRequest.builder()
                    .topicArn(topicArn)
                    .messageStructure("json")
                    .message(objectMapper.writeValueAsString(messages))
                    .build());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Multi-protocol publish failed", e);
        }
    }

    // List all subscriptions for a topic
    public List<Subscription> listSubscriptions(String topicArn) {
        ListSubscriptionsByTopicResponse response = snsClient.listSubscriptionsByTopic(
                ListSubscriptionsByTopicRequest.builder().topicArn(topicArn).build());
        response.subscriptions().forEach(sub ->
                log.info("{} → {}", sub.protocol(), sub.endpoint()));
        return response.subscriptions();
    }
}
```

**Receiving an SNS message via HTTP webhook in Spring Boot:**
```java
// SnsWebhookController.java
@RestController
@RequestMapping("/webhooks/sns")
@RequiredArgsConstructor
@Slf4j
public class SnsWebhookController {

    private final ObjectMapper objectMapper;
    private final RestClient   restClient = RestClient.create();

    @PostMapping
    public ResponseEntity<Void> handleSnsMessage(
            @RequestHeader("x-amz-sns-message-type") String messageType,
            @RequestBody String rawBody) throws Exception {

        JsonNode node = objectMapper.readTree(rawBody);

        if ("SubscriptionConfirmation".equals(messageType)) {
            // Auto-confirm subscription by visiting the SubscribeURL
            String subscribeUrl = node.get("SubscribeURL").asText();
            restClient.get().uri(subscribeUrl).retrieve().toBodilessEntity();   // HTTP GET confirms
            log.info("SNS subscription confirmed: {}", subscribeUrl);
            return ResponseEntity.ok().build();
        }

        if ("Notification".equals(messageType)) {
            String message = node.get("Message").asText();
            log.info("Received SNS notification: {}", message);
            // process message
        }
        return ResponseEntity.ok().build();
    }
}
```

> **Security:** anyone can POST to a public webhook. **Verify the SNS message signature** (`SignatureVersion`, `SigningCertURL`, `Signature` fields; the certificate URL must be an `sns.<region>.amazonaws.com` host) before trusting a message or visiting `SubscribeURL`, and check that `TopicArn` is the topic you expect. Spring Cloud AWS also offers annotation-based handlers for HTTP endpoints; see [Receiving SNS over HTTP (@NotificationMessageMapping)](#receiving-sns-over-http-notificationmessagemapping).

---

### Spring Cloud AWS for SNS: Annotations & Classes

Spring Cloud AWS has **no publishing annotation**: you publish with `SnsTemplate`. Annotations are used on the **receiving** side.

| Annotation / class | Package | Used on | Purpose |
|---|---|---|---|
| `SnsTemplate` | `io.awspring.cloud.sns.core` | Injected bean | Publish to topics (JSON conversion, headers → message attributes) |
| `SnsSmsTemplate` | `io.awspring.cloud.sns.sms` | Injected bean | Send SMS directly to phone numbers |
| `@SnsNotificationMessage` | `io.awspring.cloud.sqs.annotation` | `@SqsListener` parameter | Read the payload from the SNS envelope in an SQS queue |
| `@NotificationMessageMapping` | `io.awspring.cloud.sns.annotation.endpoint` | Controller method | Handle SNS **notifications** posted to an HTTP endpoint |
| `@NotificationSubscriptionMapping` | `io.awspring.cloud.sns.annotation.endpoint` | Controller method | Handle the **subscription confirmation** request |
| `@NotificationUnsubscribeConfirmationMapping` | `io.awspring.cloud.sns.annotation.endpoint` | Controller method | Handle the **unsubscribe confirmation** request |
| `@NotificationMessage` | `io.awspring.cloud.sns.annotation.handlers` | Controller parameter | Inject the SNS `Message` field |
| `@NotificationSubject` | `io.awspring.cloud.sns.annotation.handlers` | Controller parameter | Inject the SNS `Subject` field |

Add the starter (version from the Spring Cloud AWS BOM, see [Spring Cloud AWS Setup](#spring-cloud-aws-setup--auto-configured-beans)):

```xml
<dependency>
    <groupId>io.awspring.cloud</groupId>
    <artifactId>spring-cloud-aws-starter-sns</artifactId>
</dependency>
```

It auto-configures `SnsClient`, `SnsTemplate` and `SnsSmsTemplate`.

---

### SnsTemplate & SnsSmsTemplate (Publishing with Spring Cloud AWS)

**Package:** `io.awspring.cloud.sns.core.SnsTemplate` (implements `SnsOperations`)

#### Main methods

| Method | Description |
|---|---|
| `convertAndSend(String topic, Object payload)` | Publish a payload (JSON-serialized) to a topic name or ARN |
| `convertAndSend(String topic, Object payload, Map<String, Object> headers)` | Same, with headers → SNS **message attributes** (used by filter policies) |
| `sendNotification(String topic, Object message, String subject)` | Publish with a **subject** (shown in email notifications) |
| `send(String topic, Message<?> message)` | Publish a Spring `Message` (full control over headers) |

#### Special headers (`io.awspring.cloud.sns.core.SnsHeaders`)

| Constant | Effect |
|---|---|
| `SnsHeaders.NOTIFICATION_SUBJECT_HEADER` | Message subject |
| `SnsHeaders.MESSAGE_GROUP_ID_HEADER` | FIFO topic group id |
| `SnsHeaders.MESSAGE_DEDUPLICATION_ID_HEADER` | FIFO topic deduplication id |
| any other header | Sent as a message attribute |

#### Topic name vs ARN
You can pass a **topic ARN** or a **topic name**. With a name, the default `TopicArnResolver` resolves it by calling `CreateTopic` (idempotent: returns the existing ARN, but **creates** the topic if it doesn't exist and needs `sns:CreateTopic` permission). In production prefer **ARNs**, or register a `TopicsListingTopicArnResolver` bean that only looks up existing topics.

#### Example

```java
@Service
@RequiredArgsConstructor
public class OrderNotifications {

    private final SnsTemplate snsTemplate;

    @Value("${app.sns.order-events-arn}")
    private String orderEventsArn;

    // Publish with attributes used by subscription filter policies
    public void orderCreated(OrderEvent event) {
        snsTemplate.convertAndSend(orderEventsArn, event, Map.of(
                "event_type", "order.created",
                "region", "eu"));
    }

    // Publish with a subject (email subscribers see it as the email subject)
    public void alertOps(String text) {
        snsTemplate.sendNotification("ops-alerts", text, "Production alert");
    }

    // FIFO topic: group id + deduplication id via headers
    public void orderStatusChanged(OrderEvent event) {
        snsTemplate.send("arn:aws:sns:us-east-1:123456789:order-status.fifo",
                MessageBuilder.withPayload(event)
                        .setHeader(SnsHeaders.MESSAGE_GROUP_ID_HEADER, "order-" + event.getOrderId())
                        .setHeader(SnsHeaders.MESSAGE_DEDUPLICATION_ID_HEADER, event.getOrderId() + "-" + event.getStatus())
                        .build());
    }
}

// SMS without a topic
@Service
@RequiredArgsConstructor
public class OtpSender {

    private final SnsSmsTemplate smsTemplate;

    public void sendOtp(String phone, String code) {
        smsTemplate.send(phone, "Your OTP is " + code,
                SmsMessageAttributes.builder()
                        .smsType(SmsType.TRANSACTIONAL)      // higher delivery priority than PROMOTIONAL
                        .senderID("MYAPP")
                        .build());
    }
}
```

---

### Consuming SNS Messages in SQS Listeners (@SnsNotificationMessage)

When an SQS queue is subscribed to an SNS topic **without raw message delivery**, each SQS message body is the SNS **envelope**:

```json
{
  "Type": "Notification",
  "MessageId": "7f1c...",
  "TopicArn": "arn:aws:sns:us-east-1:123456789:order-events",
  "Subject": "Order created",
  "Message": "{\"orderId\":42,\"customerId\":7,\"amount\":499.00,\"status\":\"NEW\"}",
  "Timestamp": "2026-10-08T10:15:30.000Z",
  "MessageAttributes": { "event_type": { "Type": "String", "Value": "order.created" } }
}
```

**Package:** `io.awspring.cloud.sqs.annotation.SnsNotificationMessage`
**Applies to:** `@SqsListener` method parameter
**Attributes:** none

`@SnsNotificationMessage` tells Spring Cloud AWS to read the `Message` field from the envelope and convert **it** to the parameter type.

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryListener {

    private final ObjectMapper objectMapper;

    // Without raw delivery: unwrap the envelope
    @SqsListener("inventory-queue")
    public void onOrder(@SnsNotificationMessage OrderEvent event) {
        log.info("Reserve stock for order {}", event.getOrderId());
    }

    // Need the subject or topic too? Read the envelope yourself:
    @SqsListener("audit-queue")
    public void onEnvelope(String body) throws JsonProcessingException {
        JsonNode envelope = objectMapper.readTree(body);
        String topicArn = envelope.get("TopicArn").asText();
        OrderEvent event = objectMapper.readValue(envelope.get("Message").asText(), OrderEvent.class);
    }
}
```

**Raw delivery vs `@SnsNotificationMessage`:**

| | `RawMessageDelivery=true` | Envelope + `@SnsNotificationMessage` |
|---|---|---|
| SQS body | Your original payload | SNS JSON envelope |
| Listener parameter | `OrderEvent event` | `@SnsNotificationMessage OrderEvent event` |
| SNS message attributes | Become SQS message attributes → `@Header("event_type")` | Inside the envelope's `MessageAttributes` |
| Topic ARN / subject available | ❌ | ✅ (in the envelope) |

---

### Receiving SNS over HTTP (@NotificationMessageMapping)

For an **HTTP/S subscription**, SNS POSTs three kinds of requests to your endpoint; the `x-amz-sns-message-type` header says which one. Spring Cloud AWS maps each to a controller method.

| Annotation | Package | SNS request type | Typical action |
|---|---|---|---|
| `@NotificationSubscriptionMapping` | `io.awspring.cloud.sns.annotation.endpoint` | `SubscriptionConfirmation` | Call `status.confirmSubscription()` |
| `@NotificationMessageMapping` | `io.awspring.cloud.sns.annotation.endpoint` | `Notification` | Process the message |
| `@NotificationUnsubscribeConfirmationMapping` | `io.awspring.cloud.sns.annotation.endpoint` | `UnsubscribeConfirmation` | Optionally re-subscribe |

These annotations have **no attributes**; the URL comes from the controller's `@RequestMapping`. Method parameters:

| Parameter | Package | Value |
|---|---|---|
| `@NotificationMessage String` (or a POJO) | `io.awspring.cloud.sns.annotation.handlers` | The `Message` field |
| `@NotificationSubject String` | `io.awspring.cloud.sns.annotation.handlers` | The `Subject` field |
| `NotificationStatus` | `io.awspring.cloud.sns.handlers` | `confirmSubscription()` |

```java
@Controller
@RequestMapping("/webhooks/sns/orders")
@RequiredArgsConstructor
@Slf4j
public class SnsOrdersEndpoint {

    private final OrderService orderService;

    @NotificationSubscriptionMapping
    public void confirm(NotificationStatus status) {
        status.confirmSubscription();                 // visits the SubscribeURL
    }

    @NotificationMessageMapping
    public void receive(@NotificationSubject String subject,
                        @NotificationMessage OrderEvent event) {
        log.info("{} → order {}", subject, event.getOrderId());
        orderService.process(event);
    }

    @NotificationUnsubscribeConfirmationMapping
    public void unsubscribed(NotificationStatus status) {
        log.warn("Unsubscribed from SNS topic");
    }
}
```

> **Prefer SQS subscriptions** over HTTP for service-to-service messaging: they're durable, retryable from your side, and don't expose a public endpoint. If you do use HTTP, **verify the SNS message signature** and the `TopicArn` before trusting the content, and return **2xx quickly** (SNS retries on errors and timeouts).

---

### Testing SNS Publishing

**Unit test:** mock `SnsTemplate` and verify what was published.

```java
@ExtendWith(MockitoExtension.class)
class OrderNotificationsTest {

    @Mock SnsTemplate snsTemplate;
    @InjectMocks OrderNotifications notifications;

    @Test
    void publishesWithFilterAttributes() {
        ReflectionTestUtils.setField(notifications, "orderEventsArn", "arn:aws:sns:us-east-1:000000000000:order-events");
        OrderEvent event = new OrderEvent(42L, 7L, new BigDecimal("499.00"), "NEW");

        notifications.orderCreated(event);

        verify(snsTemplate).convertAndSend(
                eq("arn:aws:sns:us-east-1:000000000000:order-events"),
                eq(event),
                eq(Map.of("event_type", "order.created", "region", "eu")));
    }
}
```

**Integration test:** with LocalStack (see [Testing SQS Listeners](#testing-sqs-listeners)), create a topic, subscribe a test queue with raw delivery, publish through `SnsTemplate`, and assert the message arrives in the queue with `SqsTemplate.receive(...)`.

[⬆ Back to Table of Contents](#table-of-contents)

---

## 4. AWS EventBridge Scheduler

### What It Is

AWS EventBridge Scheduler is a fully managed, serverless scheduler that lets you run tasks on a schedule — one-time or recurring — at any scale. It invokes over 270 AWS service targets directly (Lambda, SQS, SNS, ECS, Step Functions, etc.) without needing an intermediary.

**Key distinction from cron/EventBridge Rules:**
- **EventBridge Scheduler** = purpose-built for scheduling. Each schedule is an independent resource. Millions of schedules at once. Universal targets. Flexible time windows. Per-schedule retry/DLQ.
- **EventBridge Rules (scheduled)** = event pattern matching with optional rate/cron. Default quota of 300 rules per event bus (adjustable). Better for event-driven routing, not purely scheduling.

**Why not just Spring's `@Scheduled`?** `@Scheduled` runs inside every instance of your app: with 3 instances, the job runs 3 times (unless you add a distributed lock like ShedLock), and nothing runs while the app is down. EventBridge Scheduler runs **outside** the application, exactly once per trigger, with retries and a DLQ. See [@Scheduled](java-spring-boot-document-with-full-details.md#scheduled).

---

### EventBridge Scheduler vs EventBridge Rules vs CloudWatch Events

| Feature | EventBridge Scheduler | EventBridge Scheduled Rules | CloudWatch Events |
|---|---|---|---|
| Purpose | Pure scheduling | Event matching + some scheduling | Legacy (same as EventBridge Rules) |
| Scale | Millions of schedules (1M per account/region by default) | 300 rules/bus (adjustable) | 300 rules/bus |
| One-time schedule | Yes | No | No |
| Flexible time window | Yes | No | No |
| Timezone support | Yes (IANA) | No (UTC only) | No (UTC only) |
| Per-schedule DLQ | Yes | No | No |
| Per-schedule retry | Yes | No | No |
| Universal targets | 270+ targets directly | Limited | Limited |
| Pricing | Per invocation | Per event | Per event |

---

### Schedule Types

A schedule expression is one of three forms: `rate(...)` (fixed interval), `cron(...)` (calendar-based, 6 fields), or `at(...)` (once at a date-time).

#### 1. Rate-Based (simple recurring)

```bash
# Run every 5 minutes:
rate(5 minutes)

# Run every 2 hours:
rate(2 hours)

# Run every 1 day:
rate(1 day)

# Create rate schedule via CLI:
aws scheduler create-schedule \
  --name "every-5-minutes" \
  --schedule-expression "rate(5 minutes)" \
  --target '{
    "Arn": "arn:aws:lambda:us-east-1:123456789:function:my-function",
    "RoleArn": "arn:aws:iam::123456789:role/scheduler-role"
  }' \
  --flexible-time-window '{"Mode": "OFF"}'
```

#### 2. Cron-Based (precise recurring)

```
Cron format: cron(minutes hours day-of-month month day-of-week year)

cron(0 12 * * ? *)          → Every day at 12:00 PM UTC
cron(0 9 ? * MON-FRI *)     → Every weekday at 9:00 AM UTC
cron(0 2 1 * ? *)           → 2:00 AM on the 1st of every month
cron(15 10 ? * 6L *)        → 10:15 AM on the last Friday of every month
cron(0/15 * * * ? *)        → Every 15 minutes
cron(0 8 1 1 ? 2025)        → 8:00 AM on January 1, 2025 (one-time via cron)

Note: day-of-month and day-of-week cannot both be specified — one must be ?
Note: AWS cron has 6 fields with YEAR last, minutes first, and day-of-week 1 = SUNDAY.
      This differs from Spring's @Scheduled cron (6 fields, SECONDS first, no year).
```

```bash
aws scheduler create-schedule \
  --name "daily-report" \
  --schedule-expression "cron(0 9 ? * MON-FRI *)" \
  --schedule-expression-timezone "America/New_York" \
  --target '{
    "Arn": "arn:aws:lambda:us-east-1:123456789:function:generate-report",
    "RoleArn": "arn:aws:iam::123456789:role/scheduler-role",
    "Input": "{\"report_type\": \"daily\", \"format\": \"pdf\"}"
  }' \
  --flexible-time-window '{"Mode": "OFF"}'
```

#### 3. One-Time Schedule

```bash
# Run once at a specific date/time — auto-delete after firing:
aws scheduler create-schedule \
  --name "black-friday-sale-start" \
  --schedule-expression "at(2024-11-29T00:00:00)" \
  --schedule-expression-timezone "America/Chicago" \
  --target '{
    "Arn": "arn:aws:lambda:us-east-1:123456789:function:start-sale",
    "RoleArn": "arn:aws:iam::123456789:role/scheduler-role"
  }' \
  --flexible-time-window '{"Mode": "OFF"}' \
  --action-after-completion DELETE
```

---

### Targets

EventBridge Scheduler can invoke 270+ AWS services as targets without needing a Lambda wrapper.

```bash
# Target: SQS Queue
aws scheduler create-schedule \
  --name "queue-filler" \
  --schedule-expression "rate(1 minute)" \
  --target '{
    "Arn": "arn:aws:sqs:us-east-1:123456789:my-queue",
    "RoleArn": "arn:aws:iam::123456789:role/scheduler-role",
    "Input": "{\"task\": \"cleanup\"}"
  }' \
  --flexible-time-window '{"Mode": "OFF"}'

# Target: ECS Fargate Task (run a container on schedule)
aws scheduler create-schedule \
  --name "nightly-etl" \
  --schedule-expression "cron(0 2 * * ? *)" \
  --target '{
    "Arn": "arn:aws:ecs:us-east-1:123456789:cluster/my-cluster",
    "RoleArn": "arn:aws:iam::123456789:role/scheduler-role",
    "EcsParameters": {
      "TaskDefinitionArn": "arn:aws:ecs:us-east-1:123456789:task-definition/etl-task:5",
      "TaskCount": 1,
      "LaunchType": "FARGATE",
      "NetworkConfiguration": {
        "AwsvpcConfiguration": {
          "Subnets": ["subnet-abc123"],
          "AssignPublicIp": "ENABLED"
        }
      }
    }
  }' \
  --flexible-time-window '{"Mode": "OFF"}'

# Target: Step Functions State Machine
aws scheduler create-schedule \
  --name "weekly-workflow" \
  --schedule-expression "cron(0 6 ? * MON *)" \
  --target '{
    "Arn": "arn:aws:states:us-east-1:123456789:stateMachine:MyWorkflow",
    "RoleArn": "arn:aws:iam::123456789:role/scheduler-role",
    "Input": "{\"mode\": \"full\"}"
  }' \
  --flexible-time-window '{"Mode": "OFF"}'
```

**IAM Role for Scheduler:**
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": ["lambda:InvokeFunction"],
      "Resource": "arn:aws:lambda:us-east-1:123456789:function:my-function"
    }
  ]
}
```

```json
// Trust policy — allow EventBridge Scheduler to assume the role:
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": { "Service": "scheduler.amazonaws.com" },
      "Action": "sts:AssumeRole"
    }
  ]
}
```

---

### Flexible Time Windows

A flexible time window allows the scheduler to invoke the target within a random window around the scheduled time. This avoids thundering herd.

```bash
# OFF: fire at exactly the scheduled time
--flexible-time-window '{"Mode": "OFF"}'

# FLEXIBLE: fire within 15 minutes of scheduled time
--flexible-time-window '{"Mode": "FLEXIBLE", "MaximumWindowInMinutes": 15}'
# 2:00 AM schedule → fires randomly between 2:00 AM and 2:15 AM

# Use case: 50,000 scheduled reminders at midnight
# Without flexible window: 50,000 Lambda invocations at exactly midnight (spike)
# With 15-min window: 50,000 invocations spread over 15 minutes (~55/sec, manageable)
```

---

### Retry Policy & DLQ

Each schedule has its own independent retry policy and DLQ. **Defaults:** up to **185 retries** within **24 hours** (`MaximumEventAgeInSeconds = 86400`), with exponential back-off. Lower them for jobs where a late run is useless. Without a DLQ, an invocation that exhausts its retries is **dropped**. The DLQ receives the event plus error details.

```bash
aws scheduler create-schedule \
  --name "critical-job" \
  --schedule-expression "rate(1 hour)" \
  --target '{
    "Arn": "arn:aws:lambda:us-east-1:123456789:function:my-function",
    "RoleArn": "arn:aws:iam::123456789:role/scheduler-role"
  }' \
  --flexible-time-window '{"Mode": "OFF"}' \
  --target-retry-policy '{
    "MaximumRetryAttempts": 3,
    "MaximumEventAgeInSeconds": 3600
  }' \
  --target-dead-letter-config '{
    "Arn": "arn:aws:sqs:us-east-1:123456789:scheduler-dlq"
  }'
```

---

### Scheduler Groups

Group related schedules together for lifecycle management and tagging.

```bash
# Create a scheduler group:
aws scheduler create-schedule-group \
  --name "production-jobs" \
  --tags '[{"Key": "Environment", "Value": "production"}]'

# List all schedules in a group:
aws scheduler list-schedules --group-name "production-jobs"

# Delete all schedules in a group at once:
aws scheduler delete-schedule-group --name "production-jobs"
```

---

### Timezone Support

Cron and `at()` expressions are evaluated in **UTC** unless you set `--schedule-expression-timezone` to an IANA time zone name. With a time zone, the schedule follows **daylight saving time** automatically (a 9:30 AM New York job stays at 9:30 local time all year).

```bash
# Respects DST automatically with IANA timezone names:
aws scheduler create-schedule \
  --name "market-open-trigger" \
  --schedule-expression "cron(30 9 ? * MON-FRI *)" \
  --schedule-expression-timezone "America/New_York" \
  --target '{...}' \
  --flexible-time-window '{"Mode": "OFF"}'
# Fires at 9:30 AM Eastern time (automatically adjusts for DST)

# Common IANA timezones:
# America/New_York    → US Eastern      America/Los_Angeles → US Pacific
# America/Chicago     → US Central      Europe/London       → UK
# Asia/Kolkata        → India           Asia/Tokyo          → Japan
```

---

### Cross-Account & Cross-Region

A schedule always lives in one account and region, and its **execution role** must be allowed to call the target. To trigger work in another account, the most reliable pattern is to target a resource there that supports **resource-based policies**, such as an SQS queue, SNS topic or EventBridge event bus, and let that account route it further. Check the Scheduler documentation for which target types accept cross-account ARNs directly.

```bash
# Schedule in account A targeting Lambda in account B:
aws scheduler create-schedule \
  --name "cross-account-job" \
  --schedule-expression "rate(1 hour)" \
  --target '{
    "Arn": "arn:aws:lambda:us-east-1:987654321:function:target-function",
    "RoleArn": "arn:aws:iam::123456789:role/cross-account-scheduler-role"
  }' \
  --flexible-time-window '{"Mode": "OFF"}'
# The IAM role in account A must have permission to invoke the Lambda in account B
# Lambda in account B must have a resource policy allowing account A to invoke it
```

---

### Spring Boot Integration & SDK Examples

**Maven dependency:**
```xml
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>scheduler</artifactId>
</dependency>
```

```java
// SchedulerConfig.java
@Configuration
public class SchedulerConfig {

    @Bean
    public SchedulerClient schedulerClient() {
        return SchedulerClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
```

```java
// EventBridgeSchedulerService.java
@Service
@RequiredArgsConstructor
@Slf4j
public class EventBridgeSchedulerService {

    private final SchedulerClient schedulerClient;
    private final ObjectMapper     objectMapper;

    @Value("${app.scheduler.role-arn}")
    private String schedulerRoleArn;

    @Value("${app.scheduler.dlq-arn}")
    private String dlqArn;

    // Create a rate-based recurring schedule
    public void createRateSchedule(String name, String rateExpression,
                                   String targetArn, Object input) {
        try {
            schedulerClient.createSchedule(CreateScheduleRequest.builder()
                    .name(name)
                    .scheduleExpression("rate(" + rateExpression + ")")
                    .flexibleTimeWindow(FlexibleTimeWindow.builder()
                            .mode(FlexibleTimeWindowMode.OFF)
                            .build())
                    .target(Target.builder()
                            .arn(targetArn)
                            .roleArn(schedulerRoleArn)
                            .input(objectMapper.writeValueAsString(input))
                            .build())
                    .state(ScheduleState.ENABLED)
                    .build());
            log.info("Created rate schedule: {}", name);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize schedule input", e);
        }
    }

    // Create a cron-based schedule with flexible time window and retry/DLQ
    public void createCronSchedule(String name, String cronExpression,
                                   String timezone, String targetArn, Object input) {
        try {
            schedulerClient.createSchedule(CreateScheduleRequest.builder()
                    .name(name)
                    .scheduleExpression("cron(" + cronExpression + ")")
                    .scheduleExpressionTimezone(timezone)
                    .flexibleTimeWindow(FlexibleTimeWindow.builder()
                            .mode(FlexibleTimeWindowMode.FLEXIBLE)
                            .maximumWindowInMinutes(15)
                            .build())
                    .target(Target.builder()
                            .arn(targetArn)
                            .roleArn(schedulerRoleArn)
                            .input(objectMapper.writeValueAsString(input))
                            .retryPolicy(RetryPolicy.builder()
                                    .maximumRetryAttempts(3)
                                    .maximumEventAgeInSeconds(3600)
                                    .build())
                            .deadLetterConfig(DeadLetterConfig.builder()
                                    .arn(dlqArn)
                                    .build())
                            .build())
                    .state(ScheduleState.ENABLED)
                    .description("Cron schedule: " + cronExpression + " (" + timezone + ")")
                    .build());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize schedule input", e);
        }
    }

    // Create a one-time schedule that fires at a specific instant and self-deletes
    public void createOneTimeSchedule(String name, Instant fireAt,
                                      String targetArn, Object input) {
        try {
            // at() needs yyyy-MM-ddTHH:mm:ss — no fractional seconds, no offset
            String atExpression = "at(" +
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                            .withZone(ZoneOffset.UTC)
                            .format(fireAt.truncatedTo(ChronoUnit.SECONDS)) + ")";

            schedulerClient.createSchedule(CreateScheduleRequest.builder()
                    .name(name)
                    .scheduleExpression(atExpression)
                    .scheduleExpressionTimezone("UTC")
                    .flexibleTimeWindow(FlexibleTimeWindow.builder()
                            .mode(FlexibleTimeWindowMode.OFF)
                            .build())
                    .target(Target.builder()
                            .arn(targetArn)
                            .roleArn(schedulerRoleArn)
                            .input(objectMapper.writeValueAsString(input))
                            .build())
                    .actionAfterCompletion(ActionAfterCompletion.DELETE)  // auto-cleanup
                    .state(ScheduleState.ENABLED)
                    .build());
            log.info("Created one-time schedule {} firing at {}", name, fireAt);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialization failed", e);
        }
    }

    // Disable a schedule (pause without deleting)
    public void disableSchedule(String name, String groupName) {
        GetScheduleResponse current = schedulerClient.getSchedule(
                GetScheduleRequest.builder().name(name).groupName(groupName).build());

        schedulerClient.updateSchedule(UpdateScheduleRequest.builder()
                .name(name)
                .groupName(groupName)
                .state(ScheduleState.DISABLED)
                .scheduleExpression(current.scheduleExpression())
                .scheduleExpressionTimezone(current.scheduleExpressionTimezone())
                .flexibleTimeWindow(current.flexibleTimeWindow())
                .target(current.target())
                .build());
        log.info("Disabled schedule: {}", name);
    }

    // Enable a previously disabled schedule
    public void enableSchedule(String name, String groupName) {
        GetScheduleResponse current = schedulerClient.getSchedule(
                GetScheduleRequest.builder().name(name).groupName(groupName).build());

        schedulerClient.updateSchedule(UpdateScheduleRequest.builder()
                .name(name)
                .groupName(groupName)
                .state(ScheduleState.ENABLED)
                .scheduleExpression(current.scheduleExpression())
                .scheduleExpressionTimezone(current.scheduleExpressionTimezone())
                .flexibleTimeWindow(current.flexibleTimeWindow())
                .target(current.target())
                .build());
    }

    // List all schedules in a group (paginated)
    public List<ScheduleSummary> listSchedules(String groupName) {
        List<ScheduleSummary> all  = new ArrayList<>();
        String nextToken = null;

        do {
            ListSchedulesRequest.Builder builder = ListSchedulesRequest.builder()
                    .groupName(groupName);
            if (nextToken != null) builder.nextToken(nextToken);

            ListSchedulesResponse response = schedulerClient.listSchedules(builder.build());
            all.addAll(response.schedules());
            nextToken = response.nextToken();
        } while (nextToken != null);

        all.forEach(s -> log.info("{}: {} [{}]", s.name(), s.scheduleExpression(), s.state()));
        return all;
    }

    // Delete a single schedule
    public void deleteSchedule(String name, String groupName) {
        schedulerClient.deleteSchedule(DeleteScheduleRequest.builder()
                .name(name)
                .groupName(groupName)
                .build());
        log.info("Deleted schedule: {}", name);
    }

    // Create a schedule group
    public void createScheduleGroup(String groupName, Map<String, String> tags) {
        List<Tag> tagList = tags.entrySet().stream()
                .map(e -> Tag.builder().key(e.getKey()).value(e.getValue()).build())
                .collect(Collectors.toList());

        schedulerClient.createScheduleGroup(CreateScheduleGroupRequest.builder()
                .name(groupName)
                .tags(tagList)
                .build());
    }
}
```

**Example: Spring Boot service that schedules a future reminder for each user at signup:**
```java
// UserService.java
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository             userRepository;
    private final EventBridgeSchedulerService schedulerService;

    @Value("${app.lambda.reminder-arn}")
    private String reminderLambdaArn;

    public User registerUser(CreateUserRequest request) {
        User user = userRepository.save(new User(request.getName(), request.getEmail()));
        // Note: if registerUser runs in a @Transactional method, create the schedule AFTER COMMIT
        // (e.g. from a @TransactionalEventListener) so a rolled-back signup doesn't leave a schedule behind.

        // Schedule a one-time follow-up reminder 3 days after signup
        Instant reminderTime = Instant.now().plus(3, ChronoUnit.DAYS);
        Map<String, Object> payload = Map.of(
                "userId",  user.getId(),
                "email",   user.getEmail(),
                "trigger", "signup_followup"
        );
        schedulerService.createOneTimeSchedule(
                "followup-user-" + user.getId(),
                reminderTime,
                reminderLambdaArn,
                payload
        );
        return user;
    }
}
```

[⬆ Back to Table of Contents](#table-of-contents)

---

## 5. Combined Architecture Patterns

### Pattern 1: Secure Spring Boot App (Secrets Manager + DataSource)

The application loads database credentials from Secrets Manager at startup; no password exists in the code, the repository or environment variables. The app's IAM role only needs `secretsmanager:GetSecretValue` on this one secret.

> When the secret **rotates**, connections already open keep working, but new connections need the new password. Use the **AWS Advanced JDBC Wrapper** (Secrets Manager plugin) or refresh the DataSource on authentication failure, or rotate with the **alternating users** strategy so the old user stays valid.

```java
// The application never holds a plaintext password anywhere.
// Secrets Manager injects credentials at startup via Spring Cloud AWS.

// application.yml:
// spring.config.import: "aws-secretsmanager:prod/myapp/db"
// spring.datasource.url:      jdbc:postgresql://${host}:${port}/${dbname}   # keys from the RDS secret JSON
// spring.datasource.username: ${username}
// spring.datasource.password: ${password}
// spring.datasource.hikari.maximum-pool-size: 10
// spring.datasource.hikari.minimum-idle: 2

@SpringBootApplication
public class Application {
    // No credentials anywhere in code or config files
    // HikariCP connection pool starts up with injected Secrets Manager values
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

---

### Pattern 2: Event-Driven Microservices (SNS → SQS → Spring Boot)

The order service publishes **one** event; SNS copies it into one queue per interested service, and each service processes its queue independently.

```
OrderController (Spring Boot)
      │
      │ POST /orders
      ▼
OrderService.createOrder()
      │
      ▼
SnsPublisherService.publishToTopic("order.created", orderEvent)
      │
      ▼
SNS fans out to:
  ├── orders-sqs        ──► @SqsListener in InventoryService  (reserve stock)
  ├── payment-sqs       ──► @SqsListener in PaymentService    (charge card)
  └── notification-sqs  ──► @SqsListener in NotificationService (send email)
```

```java
// OrderController.java
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService       orderService;
    private final SnsPublisherService snsPublisher;

    @Value("${app.sns.order-events-arn}")          // resolve the property; a "${...}" string literal is NOT resolved
    private String orderEventsArn;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest req) {
        Order order = orderService.save(req);
        snsPublisher.publishToTopic(orderEventsArn, order, "order.created");
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}

// InventoryService.java (separate microservice)
@Component
@RequiredArgsConstructor
public class InventoryEventListener {

    private final InventoryRepository inventoryRepository;

    @SqsListener("inventory-events-queue")
    public void onOrderCreated(OrderEvent event) {
        event.getItems().forEach(item ->
                inventoryRepository.decrementStock(item.getProductId(), item.getQuantity()));
    }
}
```

> **Dual-write problem:** if the database commit succeeds but the SNS publish fails (or the other way round), services disagree. Production systems use the **transactional outbox** pattern: write the event to an `outbox` table in the **same transaction** as the order, and publish from that table (a poller or CDC with Debezium). At minimum, publish **after commit** with [@TransactionalEventListener](java-spring-boot-document-with-full-details.md#transactionaleventlistener).

---

### Pattern 3: Scheduled Nightly Pipeline (EventBridge → ECS → SQS → Spring Boot)

A schedule starts a short-lived container that extracts work items into a queue; long-running Spring Boot workers process them at their own pace, and SNS announces completion.

```
EventBridge Scheduler  (cron: every night at 2AM)
      │
      ▼
ECS Fargate Task: "data-extractor" container
      │  (extracts records, sends to SQS)
      ▼
SQS Queue: "etl-records-queue"
      │
      ▼
@SqsListener in Spring Boot ETL service (batch processes records)
      │
      ▼
SnsPublisherService.publishToTopic("pipeline.complete")
      │
      ▼
SNS → Email: "Nightly pipeline complete: 45,231 records"
SNS → SQS  → @SqsListener in Metrics service (update dashboard)
```

---

### Pattern 4: Rate Limiting Downstream Calls (SQS + Spring Boot Consumer)

The queue absorbs bursts of requests; the consumer drains it no faster than the downstream API allows.

```java
// Control how fast a third-party API is called.
// Third-party API limit: 10 requests/second.
//
// maxConcurrentMessages limits CONCURRENCY (parallel calls), not RATE:
// 10 concurrent calls that each take 100 ms = ~100 calls/second.
// To enforce a rate, add a rate limiter (Resilience4j / Bucket4j / Guava RateLimiter).
// With several app instances, divide the rate between them or use a shared limiter (e.g. Redis).

@Component
@RequiredArgsConstructor
public class ThirdPartyApiConsumer {

    private final ThirdPartyClient apiClient;
    private final RateLimiter rateLimiter = RateLimiter.of("third-party",
            RateLimiterConfig.custom()
                    .limitForPeriod(10)                        // 10 permits
                    .limitRefreshPeriod(Duration.ofSeconds(1)) // per second
                    .timeoutDuration(Duration.ofSeconds(30))   // wait up to 30 s for a permit
                    .build());

    @SqsListener(value = "api-call-queue", maxConcurrentMessages = "5")
    public void callApi(ApiRequestEvent event) {
        RateLimiter.decorateRunnable(rateLimiter, () -> apiClient.execute(event.getPayload())).run();
        // If this throws: the message is not deleted and is retried after the visibility timeout
    }
}
```

---

### Pattern 5: Scheduled Secret Rotation Alert (EventBridge + Lambda → SNS → SQS → Spring Boot)

A monthly compliance check: find secrets that haven't been rotated recently and notify both humans (email) and systems (ticket creation).

```
EventBridge Scheduler: cron(0 9 1 * ? *) → first of month at 9AM
      │
      ▼
Lambda: scans Secrets Manager for secrets not rotated in 90 days
      │
      ▼
SnsPublisherService: publish to "security-alerts" topic
      │
      ▼
SNS → Email (ops-team): "3 secrets require rotation"
SNS → SQS  → @SqsListener in TicketService: auto-creates Jira tickets
```

[⬆ Back to Table of Contents](#table-of-contents)
