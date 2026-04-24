# Camunda 7 vs Camunda 8 — Complete Guide & Migration Strategy

> **Audience:** Java/Spring developers migrating from Camunda 7 to Camunda 8  
> **Last Updated:** 2024

---

## Table of Contents

1. [Architecture Overview](#1-architecture-overview)
2. [Camunda 7 — Full Documentation](#2-camunda-7--full-documentation)
3. [Camunda 8 — Full Documentation](#3-camunda-8--full-documentation)
4. [Feature Comparison Table](#4-feature-comparison-table)
5. [Migration Strategy](#5-migration-strategy)
   - 5.1 [BPMN Diagram Migration](#51-bpmn-diagram-migration)
   - 5.2 [Database Migration](#52-database-migration)
   - 5.3 [Spring Boot Codebase Migration](#53-spring-boot-codebase-migration)
   - 5.4 [Deployment Strategy](#54-deployment-strategy)
     - 5.4.1 [Deployment Topology Comparison](#541-deployment-topology-comparison)
     - 5.4.2 [Deployment Differences Summary](#542-deployment-differences-summary)
     - 5.4.3 [Infrastructure Setup — Camunda 8](#543-infrastructure-setup--camunda-8)
     - 5.4.4 [Application Deployment — Camunda 8](#544-application-deployment--camunda-8)
     - 5.4.5 [CI/CD Pipeline Migration](#545-cicd-pipeline-migration)
     - 5.4.6 [Migration Deployment Phases](#546-migration-deployment-phases)
     - 5.4.7 [Blue-Green Deployment for Camunda 8 Workers](#547-blue-green-deployment-for-camunda-8-workers)
     - 5.4.8 [BPMN Process Version Management](#548-bpmn-process-version-management)
     - 5.4.9 [Environment Configuration Matrix](#549-environment-configuration-matrix)
     - 5.4.10 [Rollback Plan](#5410-rollback-plan)
   - 5.5 [Worker / Job Handler Migration](#55-worker--job-handler-migration)
   - 5.6 [Process Variables Migration](#56-process-variables-migration)
   - 5.7 [REST API Migration](#57-rest-api-migration)
   - 5.8 [Migration Approach Options](#58-migration-approach-options)
6. [Step-by-Step Migration Checklist](#6-step-by-step-migration-checklist)
7. [Known Gotchas & Pitfalls](#7-known-gotchas--pitfalls)

---

## 1. Architecture Overview

### Camunda 7 Architecture
```
┌─────────────────────────────────────────────────────────┐
│                  Your Spring Boot App                   │
│  ┌──────────────────────────────────────────────────┐   │
│  │            Camunda Engine (Embedded)              │   │
│  │   BPMN Engine + DMN Engine + CMMN Engine          │   │
│  └──────────────────────────────────────────────────┘   │
│                         │                               │
│  ┌──────────────────────▼───────────────────────────┐   │
│  │         Relational Database (MySQL/PostgreSQL)    │   │
│  │   ACT_* tables — runtime, history, repository    │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

**Key characteristic:** The engine runs **inside** your application. One DB, one deployment unit.

---

### Camunda 8 Architecture
```
┌──────────────────────────────────────────────────────────────┐
│                       Zeebe Cluster                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌────────────┐  │
│  │  Broker  │  │  Broker  │  │  Broker  │  │  Gateway   │  │
│  │ (Leader) │  │(Follower)│  │(Follower)│  │  (gRPC)    │  │
│  └──────────┘  └──────────┘  └──────────┘  └────────────┘  │
│            Raft consensus + RocksDB (log-based)              │
└──────────────────────────────────────────────────────────────┘
         │ gRPC                                   │ REST
┌────────▼────────┐                    ┌──────────▼──────────┐
│  Your App       │                    │  Operate / Tasklist /│
│  (Job Workers)  │                    │  Optimize / Modeler  │
└─────────────────┘                    └─────────────────────┘
                                       ┌─────────────────────┐
                                       │  Elasticsearch      │
                                       │  (audit/history)    │
                                       └─────────────────────┘
```

**Key characteristic:** The engine runs **outside** your application as a separate distributed system. Your app connects to it as a client.

---

## 2. Camunda 7 — Full Documentation

### 2.1 Core Concepts

| Concept | Camunda 7 |
|---|---|
| Engine | Embedded in JVM |
| Protocol | Java API (in-process) |
| Database | Relational DB (MySQL, PostgreSQL, Oracle, H2) |
| Deployment | Deployed with your WAR/JAR |
| Scalability | Vertical or clustered via shared DB |
| Process Variables | Stored as BLOBs in DB |
| History | Written synchronously to same DB |

---

### 2.2 Maven Dependencies (Camunda 7)

```xml
<!-- pom.xml -->
<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-parent</artifactId>
  <version>2.7.x</version>
</parent>

<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.camunda.bpm</groupId>
      <artifactId>camunda-bom</artifactId>
      <version>7.21.0</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <!-- Camunda Spring Boot Starter -->
  <dependency>
    <groupId>org.camunda.bpm.springboot</groupId>
    <artifactId>camunda-bpm-spring-boot-starter</artifactId>
  </dependency>

  <!-- Camunda REST API (optional) -->
  <dependency>
    <groupId>org.camunda.bpm.springboot</groupId>
    <artifactId>camunda-bpm-spring-boot-starter-rest</artifactId>
  </dependency>

  <!-- Camunda Web Apps (Cockpit, Tasklist, Admin) -->
  <dependency>
    <groupId>org.camunda.bpm.springboot</groupId>
    <artifactId>camunda-bpm-spring-boot-starter-webapp</artifactId>
  </dependency>

  <!-- Database driver -->
  <dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
  </dependency>
</dependencies>
```

---

### 2.3 Application Configuration (Camunda 7)

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/camunda
    username: camunda
    password: secret
    driver-class-name: org.postgresql.Driver

camunda:
  bpm:
    admin-user:
      id: admin
      password: admin
      first-name: Admin
    filter:
      create: All Tasks
    history-level: full           # none | activity | audit | full
    auto-deployment-enabled: true # auto-deploys from classpath
    database:
      schema-update: true         # auto creates ACT_* tables
    job-execution:
      enabled: true
      core-pool-size: 3
      max-pool-size: 10
```

---

### 2.4 Process Deployment (Camunda 7)

```java
// Approach 1: Auto-deploy from classpath (just place .bpmn in resources/)
// src/main/resources/processes/order-process.bpmn → auto-deployed

// Approach 2: Programmatic deployment
@Service
public class ProcessDeploymentService {

    @Autowired
    private RepositoryService repositoryService;

    public void deployProcess() {
        repositoryService.createDeployment()
            .name("Order Process")
            .addClasspathResource("processes/order-process.bpmn")
            .addClasspathResource("decisions/order-decision.dmn")
            .enableDuplicateFiltering(true)  // skip if unchanged
            .deploy();
    }
}
```

---

### 2.5 Starting a Process Instance (Camunda 7)

```java
@Service
public class OrderService {

    @Autowired
    private RuntimeService runtimeService;

    // Start by process definition key
    public void startOrder(OrderDto order) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", order.getId());
        variables.put("customerId", order.getCustomerId());
        variables.put("totalAmount", order.getTotalAmount());
        variables.put("orderData", order); // serialized as JSON/Java object

        ProcessInstance instance = runtimeService
            .startProcessInstanceByKey("order-process", variables);

        System.out.println("Started: " + instance.getId());
    }

    // Start with a business key for correlation
    public void startOrderWithKey(OrderDto order) {
        runtimeService.startProcessInstanceByKey(
            "order-process",
            "ORDER-" + order.getId(),   // business key
            variables
        );
    }
}
```

---

### 2.6 Implementing Service Tasks — Java Delegate (Camunda 7)

```java
// Approach 1: JavaDelegate (recommended)
@Component("validateOrderDelegate")
public class ValidateOrderDelegate implements JavaDelegate {

    @Autowired
    private OrderValidationService validationService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String orderId = (String) execution.getVariable("orderId");
        double amount  = (Double) execution.getVariable("totalAmount");

        boolean valid = validationService.validate(orderId, amount);

        // Set output variable
        execution.setVariable("isValid", valid);
        execution.setVariable("validationTimestamp", LocalDateTime.now().toString());
    }
}
```

```xml
<!-- In BPMN: reference by Spring bean name -->
<serviceTask id="validateOrder"
             camunda:delegateExpression="${validateOrderDelegate}" />
```

```java
// Approach 2: Expression-based (inline method call)
@Component
public class PaymentService {
    public boolean processPayment(DelegateExecution execution) {
        String orderId = (String) execution.getVariable("orderId");
        // ... payment logic ...
        return true;
    }
}
```

```xml
<!-- In BPMN -->
<serviceTask id="processPayment"
             camunda:expression="${paymentService.processPayment(execution)}" />
```

---

### 2.7 User Tasks & Task Management (Camunda 7)

```java
@Service
public class TaskManagementService {

    @Autowired
    private TaskService taskService;

    // Query tasks
    public List<Task> getTasksForUser(String userId) {
        return taskService.createTaskQuery()
            .taskAssignee(userId)
            .orderByTaskCreateTime().desc()
            .list();
    }

    // Claim and complete a task
    public void claimAndComplete(String taskId, String userId, Map<String, Object> formData) {
        taskService.claim(taskId, userId);
        taskService.complete(taskId, formData);
    }
}
```

---

### 2.8 Listeners (Camunda 7)

```java
// Execution Listener
@Component("orderCreatedListener")
public class OrderCreatedListener implements ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) throws Exception {
        System.out.println("Process started: " + execution.getProcessInstanceId());
    }
}

// Task Listener
@Component("taskCreatedListener")
public class TaskCreatedListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        delegateTask.setAssignee("manager@company.com");
    }
}
```

---

### 2.9 Message Correlation (Camunda 7)

```java
@Service
public class MessageService {

    @Autowired
    private RuntimeService runtimeService;

    // Correlate a message to a waiting process instance
    public void correlatePaymentReceived(String orderId, double amount) {
        runtimeService.createMessageCorrelation("PaymentReceivedMessage")
            .processInstanceBusinessKey("ORDER-" + orderId)
            .setVariable("paidAmount", amount)
            .correlate();
    }

    // Start a new process via message
    public void startViaMessage(String customerId) {
        runtimeService.startProcessInstanceByMessage(
            "NewOrderMessage",
            Map.of("customerId", customerId)
        );
    }
}
```

---

### 2.10 Querying & History (Camunda 7)

```java
@Service
public class ProcessQueryService {

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RuntimeService runtimeService;

    public List<HistoricProcessInstance> getCompletedInstances(String key) {
        return historyService.createHistoricProcessInstanceQuery()
            .processDefinitionKey(key)
            .finished()
            .orderByProcessInstanceEndTime().desc()
            .list();
    }

    public List<ProcessInstance> getActiveInstances(String businessKey) {
        return runtimeService.createProcessInstanceQuery()
            .processInstanceBusinessKey(businessKey)
            .active()
            .list();
    }
}
```

---

### 2.11 Database Tables (Camunda 7)

Camunda 7 creates **50+ tables** in your relational database, prefixed with `ACT_`:

```
Runtime Tables:
  ACT_RU_EXECUTION        — active process instances and executions
  ACT_RU_TASK             — active user tasks
  ACT_RU_VARIABLE         — runtime process variables
  ACT_RU_JOB              — jobs (timers, async continuations)
  ACT_RU_INCIDENT         — incidents / failed jobs
  ACT_RU_EVENT_SUBSCR     — message/signal subscriptions

History Tables:
  ACT_HI_PROCINST         — historic process instances
  ACT_HI_ACTINST          — historic activity instances
  ACT_HI_TASKINST         — historic task instances
  ACT_HI_VARINST          — historic variable values
  ACT_HI_DETAIL           — variable value changes
  ACT_HI_COMMENT          — task comments

Repository Tables:
  ACT_RE_PROCDEF          — deployed process definitions
  ACT_RE_DEPLOYMENT       — deployment metadata
  ACT_GE_BYTEARRAY        — BPMN/DMN XML + serialized variables

Identity Tables:
  ACT_ID_USER             — users
  ACT_ID_GROUP            — groups
  ACT_ID_MEMBERSHIP        — user-group membership
```

---

## 3. Camunda 8 — Full Documentation

### 3.1 Core Concepts

| Concept | Camunda 8 |
|---|---|
| Engine | Zeebe (external distributed broker) |
| Protocol | gRPC (via Java/Go/Node.js clients) |
| Database | RocksDB (internal) + Elasticsearch (export) |
| Deployment | Separate cluster (SaaS or self-managed) |
| Scalability | Horizontal (Raft consensus, partitions) |
| Process Variables | Stored in Zeebe log; exported to Elasticsearch |
| History | Exported asynchronously to Elasticsearch |

---

### 3.2 Maven Dependencies (Camunda 8)

```xml
<!-- pom.xml -->
<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-parent</artifactId>
  <version>3.2.x</version>
</parent>

<properties>
  <camunda.version>8.5.0</camunda.version>
</properties>

<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>io.camunda</groupId>
      <artifactId>zeebe-bom</artifactId>
      <version>${camunda.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <!-- Spring Boot Starter for Camunda 8 -->
  <dependency>
    <groupId>io.camunda.spring</groupId>
    <artifactId>spring-boot-starter-camunda</artifactId>
    <version>8.5.0</version>
  </dependency>

  <!-- Zeebe Java Client (lower-level, if needed) -->
  <dependency>
    <groupId>io.camunda</groupId>
    <artifactId>zeebe-client-java</artifactId>
  </dependency>
</dependencies>
```

---

### 3.3 Application Configuration (Camunda 8)

```yaml
# application.yml

# For Camunda SaaS (Camunda Cloud)
zeebe:
  client:
    cloud:
      cluster-id: your-cluster-id
      client-id: your-client-id
      client-secret: your-client-secret
      region: bru-2           # your cluster region
    job:
      timeout: 30s            # job lock timeout
      poll-interval: 100ms    # how often workers poll
      max-jobs-active: 32     # concurrent jobs per worker

---

# For Self-Managed (Docker / Kubernetes)
zeebe:
  client:
    broker:
      gateway-address: localhost:26500   # Zeebe gateway gRPC address
    security:
      plaintext: true                    # disable TLS for local dev
    job:
      timeout: 30s
      poll-interval: 100ms
      max-jobs-active: 32

camunda:
  client:
    mode: self-managed   # or 'saas'
    operate:
      base-url: http://localhost:8081
    tasklist:
      base-url: http://localhost:8082
```

---

### 3.4 Process Deployment (Camunda 8)

```java
// Approach 1: Auto-deploy from classpath
// Place .bpmn files in: src/main/resources/processes/
// They are auto-deployed on startup by the Spring starter

// Approach 2: Programmatic deployment via ZeebeClient
@Service
public class ProcessDeploymentService {

    @Autowired
    private ZeebeClient zeebeClient;

    public void deployProcess() {
        DeploymentEvent deployment = zeebeClient.newDeployResourceCommand()
            .addResourceFromClasspath("processes/order-process.bpmn")
            .addResourceFromClasspath("decisions/order-decision.dmn")
            .send()
            .join();   // blocking; use .thenAccept() for async

        deployment.getProcesses().forEach(p ->
            System.out.printf("Deployed: %s v%d (key=%d)%n",
                p.getBpmnProcessId(), p.getVersion(), p.getProcessDefinitionKey())
        );
    }
}
```

---

### 3.5 Starting a Process Instance (Camunda 8)

```java
@Service
public class OrderService {

    @Autowired
    private ZeebeClient zeebeClient;

    public void startOrder(OrderDto order) {
        Map<String, Object> variables = Map.of(
            "orderId",     order.getId(),
            "customerId",  order.getCustomerId(),
            "totalAmount", order.getTotalAmount()
        );

        ProcessInstanceEvent event = zeebeClient
            .newCreateInstanceCommand()
            .bpmnProcessId("order-process")
            .latestVersion()
            .variables(variables)
            .send()
            .join();

        System.out.println("Started instance key: " + event.getProcessInstanceKey());
    }

    // Start and await result (synchronous pattern)
    public Map<String, Object> startAndAwait(OrderDto order) throws Exception {
        ProcessInstanceResult result = zeebeClient
            .newCreateInstanceCommand()
            .bpmnProcessId("order-process")
            .latestVersion()
            .variables(Map.of("orderId", order.getId()))
            .withResult()
            .requestTimeout(Duration.ofSeconds(30))
            .send()
            .join();

        return result.getVariablesAsMap();
    }
}
```

---

### 3.6 Implementing Job Workers (Camunda 8)

This is the **biggest conceptual change** from Camunda 7. There are no JavaDelegates.  
Instead, workers **poll** Zeebe for jobs and **complete** them.

```java
// Approach 1: @JobWorker annotation (Spring Starter)
@Component
public class OrderWorkers {

    @Autowired
    private OrderValidationService validationService;

    @JobWorker(type = "validate-order")   // must match "Job type" in BPMN
    public void validateOrder(JobClient client, ActivatedJob job) {
        // Read variables
        Map<String, Object> vars = job.getVariablesAsMap();
        String orderId   = (String) vars.get("orderId");
        Double amount    = (Double) vars.get("totalAmount");

        try {
            boolean valid = validationService.validate(orderId, amount);

            // Complete the job with output variables
            client.newCompleteCommand(job.getKey())
                .variable("isValid", valid)
                .variable("validatedAt", Instant.now().toString())
                .send()
                .join();

        } catch (Exception e) {
            // Fail the job (Zeebe will retry based on retry config)
            client.newFailCommand(job.getKey())
                .retries(job.getRetries() - 1)
                .errorMessage(e.getMessage())
                .send()
                .join();
        }
    }

    // Throw BPMN error to route to error boundary event
    @JobWorker(type = "process-payment")
    public void processPayment(JobClient client, ActivatedJob job) {
        try {
            // ... payment logic ...
            client.newCompleteCommand(job.getKey()).send().join();
        } catch (PaymentDeclinedException e) {
            client.newThrowErrorCommand(job.getKey())
                .errorCode("PAYMENT_DECLINED")
                .errorMessage(e.getMessage())
                .send()
                .join();
        }
    }
}
```

```xml
<!-- In BPMN (Camunda 8 Modeler): -->
<serviceTask id="validateOrder">
  <extensionElements>
    <zeebe:taskDefinition type="validate-order" retries="3" />
  </extensionElements>
</serviceTask>
```

---

### 3.7 Message Correlation (Camunda 8)

```java
@Service
public class MessageService {

    @Autowired
    private ZeebeClient zeebeClient;

    // Correlate a message to a waiting process instance
    public void correlatePaymentReceived(String orderId, double amount) {
        zeebeClient.newPublishMessageCommand()
            .messageName("PaymentReceivedMessage")
            .correlationKey("ORDER-" + orderId)   // must match subscription key in BPMN
            .variable("paidAmount", amount)
            .timeToLive(Duration.ofSeconds(60))    // message TTL if no subscriber yet
            .send()
            .join();
    }
}
```

---

### 3.8 Signal Handling (Camunda 8)

```java
// Signals are broadcast — no correlation key needed
public void broadcastSignal(String signalName) {
    zeebeClient.newBroadcastSignalCommand()
        .signalName(signalName)
        .variable("reason", "system-maintenance")
        .send()
        .join();
}
```

---

### 3.9 Camunda 8 Application Components

| Component | Purpose | Port |
|---|---|---|
| **Zeebe** | Core BPMN/DMN engine (gRPC) | 26500 |
| **Operate** | Process monitoring & incident management | 8081 |
| **Tasklist** | Human task management UI | 8082 |
| **Optimize** | Process analytics & reporting | 8083 |
| **Modeler** | BPMN/DMN modeling (web) | 8070 |
| **Identity** | Auth/RBAC management | 8084 |
| **Elasticsearch** | History & audit storage | 9200 |

---

### 3.10 Docker Compose for Local Development (Camunda 8)

```yaml
# docker-compose.yml
version: "3.8"
services:
  zeebe:
    image: camunda/zeebe:8.5.0
    ports:
      - "26500:26500"   # gRPC
      - "9600:9600"     # monitoring
    environment:
      - ZEEBE_BROKER_EXPORTERS_ELASTICSEARCH_CLASSNAME=io.camunda.zeebe.exporter.ElasticsearchExporter
      - ZEEBE_BROKER_EXPORTERS_ELASTICSEARCH_ARGS_URL=http://elasticsearch:9200
    depends_on: [elasticsearch]

  operate:
    image: camunda/operate:8.5.0
    ports: ["8081:8080"]
    environment:
      - CAMUNDA_OPERATE_ZEEBE_GATEWAYADDRESS=zeebe:26500
      - CAMUNDA_OPERATE_ELASTICSEARCH_URL=http://elasticsearch:9200
    depends_on: [zeebe, elasticsearch]

  tasklist:
    image: camunda/tasklist:8.5.0
    ports: ["8082:8080"]
    environment:
      - CAMUNDA_TASKLIST_ZEEBE_GATEWAYADDRESS=zeebe:26500
      - CAMUNDA_TASKLIST_ELASTICSEARCH_URL=http://elasticsearch:9200
    depends_on: [zeebe, elasticsearch]

  elasticsearch:
    image: elasticsearch:8.9.0
    ports: ["9200:9200"]
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
      - ES_JAVA_OPTS=-Xms512m -Xmx512m
```

---

## 4. Feature Comparison Table

| Feature | Camunda 7 | Camunda 8 |
|---|---|---|
| **Engine location** | Embedded in JVM | External (Zeebe) |
| **Protocol** | Java API (in-process) | gRPC |
| **Service task impl** | JavaDelegate, Expression | Job Worker (`@JobWorker`) |
| **Database** | Relational DB (PostgreSQL, MySQL, Oracle, H2) | RocksDB + Elasticsearch |
| **Schema** | 50+ `ACT_*` tables | No SQL tables in your DB |
| **Scalability** | Shared DB clustering | Horizontal partitioning (Raft) |
| **BPMN standard** | BPMN 2.0 + Camunda extensions | BPMN 2.0 + Zeebe extensions |
| **DMN** | Full DMN 1.3 | DMN 1.3 (limited FEEL) |
| **CMMN** | Supported | **Not supported** |
| **Cockpit** | Built-in web app | Operate (separate service) |
| **Tasklist** | Built-in web app | Tasklist (separate service) |
| **History** | Synchronous to DB | Async export to Elasticsearch |
| **Transactions** | ACID (DB transactions) | Eventual consistency |
| **Process variables** | BLOB in DB | JSON in log |
| **Auth** | Built-in user management | Identity / OAuth / Keycloak |
| **Multi-tenancy** | Supported | Supported (different approach) |
| **Spring Boot** | 2.x supported | 3.x required |
| **Java version** | Java 8+ | Java 17+ |
| **Licensing** | Apache 2.0 (Community) | Apache 2.0 (core) + Enterprise |

---

## 5. Migration Strategy

### 5.1 BPMN Diagram Migration

#### What changes in the XML

**Camunda 7 BPMN namespace:**
```xml
<definitions xmlns:camunda="http://camunda.org/schema/1.0/bpmn" ...>
```

**Camunda 8 BPMN namespace:**
```xml
<definitions xmlns:zeebe="http://camunda.org/schema/zeebe/1.0" ...>
```

#### Service Task — Before (Camunda 7)
```xml
<serviceTask id="validateOrder" name="Validate Order"
             camunda:delegateExpression="${validateOrderDelegate}">
</serviceTask>
```

#### Service Task — After (Camunda 8)
```xml
<serviceTask id="validateOrder" name="Validate Order">
  <extensionElements>
    <zeebe:taskDefinition type="validate-order" retries="3" />
    <zeebe:ioMapping>
      <zeebe:input source="=orderId" target="orderId" />
      <zeebe:input source="=totalAmount" target="totalAmount" />
      <zeebe:output source="=isValid" target="isValid" />
    </zeebe:ioMapping>
  </extensionElements>
</serviceTask>
```

#### User Task — Before (Camunda 7)
```xml
<userTask id="approveOrder" name="Approve Order"
          camunda:assignee="${approver}"
          camunda:candidateGroups="managers">
  <extensionElements>
    <camunda:formKey>embedded:app:forms/approve-order.html</camunda:formKey>
  </extensionElements>
</userTask>
```

#### User Task — After (Camunda 8)
```xml
<userTask id="approveOrder" name="Approve Order">
  <extensionElements>
    <zeebe:assignmentDefinition assignee="=approver" candidateGroups="managers" />
    <zeebe:formDefinition formKey="camunda-forms:bpmn:approveOrderForm" />
  </extensionElements>
</userTask>
```

#### Message Events — Before (Camunda 7)
```xml
<intermediateCatchEvent id="waitForPayment">
  <messageEventDefinition messageRef="PaymentReceivedMessage" />
</intermediateCatchEvent>
```

#### Message Events — After (Camunda 8)
```xml
<intermediateCatchEvent id="waitForPayment">
  <messageEventDefinition messageRef="PaymentReceivedMessage" />
  <extensionElements>
    <zeebe:subscription correlationKey="=orderId" />
  </extensionElements>
</intermediateCatchEvent>
```

#### Key BPMN Differences Summary

| Element | Camunda 7 attribute | Camunda 8 attribute |
|---|---|---|
| Service Task | `camunda:delegateExpression` | `zeebe:taskDefinition type=` |
| Service Task | `camunda:expression` | `zeebe:taskDefinition type=` |
| User Task | `camunda:assignee` | `zeebe:assignmentDefinition assignee=` |
| User Task | `camunda:candidateGroups` | `zeebe:assignmentDefinition candidateGroups=` |
| Forms | `camunda:formKey` | `zeebe:formDefinition formKey=` |
| Listeners | `camunda:executionListener` | Removed — use jobs instead |
| Expressions | `${expression}` (JUEL) | `=expression` (FEEL) |

#### Automated Migration Tool

Camunda provides an official migration diagram converter:
```bash
# Use Camunda Modeler (desktop) — open a C7 diagram, export as C8
# Or use the CLI migration tool:

npx @camunda/c7-to-c8-migration convert \
  --source ./processes/order-process.bpmn \
  --target ./processes-c8/order-process.bpmn
```

#### Delegate to Job Type Mapping Reference

| Camunda 7 Delegate | Camunda 8 Job Type |
|---|---|
| `confirmOrderDelegate` | `confirm-order` |
| `sendEmailDelegate` | `send-email` |
| `updateInventoryDelegate` | `update-inventory` |
| `generateInvoiceDelegate` | `generate-invoice` |
| `validateOrderDelegate` | `validate-order` |
| `processPaymentDelegate` | `process-payment` |
| `errorHandlerDelegate` | `error-handler` |

> Convention: convert camelCase delegate names to kebab-case job types and remove the `Delegate` suffix.

---

### 5.2 Database Migration

#### Camunda 7 Database
- 50+ `ACT_*` tables in your relational database
- Variables stored as blobs
- History written synchronously

#### Camunda 8 Database
- **No** `ACT_*` tables in your application database
- Zeebe uses **RocksDB** internally (you don't manage this)
- History exported to **Elasticsearch**

#### Migration Steps

1. **Keep your application database for your own data** — remove the Camunda datasource config.
2. **Set up Elasticsearch** for historical process data.
3. **Do not migrate ACT_* data** — Camunda 8 starts fresh. Running instances from C7 cannot be migrated automatically.
4. If you need historical C7 data available:

```sql
-- Export key historic data from C7 before cutover
SELECT
    hi.PROC_INST_ID_,
    hi.BUSINESS_KEY_,
    hi.START_TIME_,
    hi.END_TIME_,
    hi.STATE_,
    hv.NAME_ as var_name,
    hv.TEXT_ as var_value
FROM ACT_HI_PROCINST hi
LEFT JOIN ACT_HI_VARINST hv ON hi.PROC_INST_ID_ = hv.PROC_INST_ID_
WHERE hi.PROC_DEF_KEY_ = 'order-process'
ORDER BY hi.START_TIME_ DESC;
```

Store this data in your own application tables for historical reporting.

#### Application Database Config Change

**Before (Camunda 7) — application.yml:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/myapp
    username: user
    password: pass

camunda:
  bpm:
    database:
      schema-update: true
```

**After (Camunda 8) — application.yml:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/myapp   # Only YOUR app tables now
    username: user
    password: pass
    # No Camunda schema-update needed — Zeebe manages its own storage

zeebe:
  client:
    broker:
      gateway-address: localhost:26500
    security:
      plaintext: true
```

---

### 5.3 Spring Boot Codebase Migration

#### Dependencies

**Remove (Camunda 7):**
```xml
<dependency>
  <groupId>org.camunda.bpm.springboot</groupId>
  <artifactId>camunda-bpm-spring-boot-starter</artifactId>
</dependency>
<dependency>
  <groupId>org.camunda.bpm.springboot</groupId>
  <artifactId>camunda-bpm-spring-boot-starter-rest</artifactId>
</dependency>
<dependency>
  <groupId>org.camunda.bpm.springboot</groupId>
  <artifactId>camunda-bpm-spring-boot-starter-webapp</artifactId>
</dependency>
```

**Add (Camunda 8):**
```xml
<dependency>
  <groupId>io.camunda.spring</groupId>
  <artifactId>spring-boot-starter-camunda</artifactId>
  <version>8.5.0</version>
</dependency>
```

Also upgrade Spring Boot from 2.x → 3.x and Java from 8/11 → 17+.

#### Service Class Migration

**Before (Camunda 7):**
```java
@Component("validateOrderDelegate")
public class ValidateOrderDelegate implements JavaDelegate {

    @Autowired
    private ValidationService validationService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String orderId = (String) execution.getVariable("orderId");
        boolean valid = validationService.validate(orderId);
        execution.setVariable("isValid", valid);
    }
}
```

**After (Camunda 8):**
```java
@Component
public class ValidateOrderWorker {

    @Autowired
    private ValidationService validationService;

    @JobWorker(type = "validate-order")
    public void validateOrder(JobClient client, ActivatedJob job) {
        String orderId = (String) job.getVariablesAsMap().get("orderId");
        boolean valid = validationService.validate(orderId);

        client.newCompleteCommand(job.getKey())
            .variable("isValid", valid)
            .send()
            .join();
    }
}
```

#### Process Starting Migration

**Before (Camunda 7):**
```java
@Autowired
private RuntimeService runtimeService;

public void start(String orderId) {
    runtimeService.startProcessInstanceByKey(
        "order-process",
        Map.of("orderId", orderId)
    );
}
```

**After (Camunda 8):**
```java
@Autowired
private ZeebeClient zeebeClient;

public void start(String orderId) {
    zeebeClient.newCreateInstanceCommand()
        .bpmnProcessId("order-process")
        .latestVersion()
        .variable("orderId", orderId)
        .send()
        .join();
}
```

#### Complete API Mapping

| Camunda 7 API | Camunda 8 API |
|---|---|
| `RuntimeService.startProcessInstanceByKey()` | `ZeebeClient.newCreateInstanceCommand()` |
| `RuntimeService.correlateMessage()` | `ZeebeClient.newPublishMessageCommand()` |
| `RuntimeService.setVariable()` | Set via `newSetVariablesCommand()` or in worker |
| `TaskService.complete()` | `ZeebeClient.newCompleteCommand()` (from worker) or Tasklist API |
| `RepositoryService.createDeployment()` | `ZeebeClient.newDeployResourceCommand()` |
| `ManagementService.executeJob()` | Operate UI or `newUpdateJobRetriesCommand()` |
| `HistoryService.createHistoricProcessInstanceQuery()` | Operate REST API / Elasticsearch query |
| `JavaDelegate` | `@JobWorker` method |
| `ExecutionListener` | Not directly supported — use boundary events |
| `TaskListener` | Not directly supported — handle in workers |

#### Expression Language Change

**Camunda 7 uses JUEL (Java Unified Expression Language):**
```
${orderId}
${order.customerId}
${amount > 100}
${validateOrderDelegate.validate(execution)}
```

**Camunda 8 uses FEEL (Friendly Enough Expression Language):**
```
=orderId
=order.customerId
=amount > 100
=if amount > 1000 then "large" else "small"
=string join(["Hello", customer], " ")
```

Key FEEL differences:
- Prefix `=` for expressions
- String concatenation: `string join(list, delimiter)` or `a + b`
- Date functions differ significantly
- No calling Spring beans directly from FEEL

---

### 5.4 Deployment Strategy

This section covers the full deployment migration path — from the current Camunda 7 topology to a production-grade Camunda 8 setup — including infrastructure provisioning, CI/CD pipeline changes, phased rollout patterns, and rollback procedures.

---

#### 5.4.1 Deployment Topology Comparison

**Camunda 7 — Single Deployable Unit**
```
┌─────────────────────────────────────────────────────────┐
│                    myapp.jar                            │
│  ┌─────────────────────────────────────────────────┐   │
│  │  Spring Boot App + Camunda Engine (embedded)    │   │
│  │  REST API · Cockpit · Tasklist · Admin (opt.)   │   │
│  │  BPMN/DMN resources (classpath)                 │   │
│  └─────────────────────────────────────────────────┘   │
│                          │                              │
│  ┌───────────────────────▼─────────────────────────┐   │
│  │       PostgreSQL — ACT_* tables + App tables    │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

**Camunda 8 — Distributed Platform + Stateless Workers**
```
┌──────────────────── Camunda Platform (Helm/Docker) ────────────────────┐
│  ┌─────────────┐  ┌─────────────┐  ┌──────────────┐  ┌─────────────┐  │
│  │ Zeebe       │  │  Operate    │  │  Tasklist    │  │  Identity   │  │
│  │ (3 brokers) │  │  (Monitor)  │  │  (Human UI)  │  │  (AuthN/Z)  │  │
│  └──────┬──────┘  └─────────────┘  └──────────────┘  └─────────────┘  │
│         │ RocksDB (internal)        Elasticsearch (history/search)      │
└─────────┼──────────────────────────────────────────────────────────────┘
          │ gRPC :26500
┌─────────▼──────────────┐
│  Your App (Job Workers) │  ← stateless, scales independently
│  myapp.jar              │
│  (no embedded engine)   │
└─────────────────────────┘
```

---

#### 5.4.2 Deployment Differences Summary

| Aspect | Camunda 7 | Camunda 8 |
|---|---|---|
| BPMN deployment trigger | App startup (auto) or API call | App startup (auto) or API call |
| BPMN storage | `ACT_RE_*` tables in your DB | Zeebe broker (replicated log) |
| App restarts | Idempotent — engine restores from DB | Idempotent — state lives in Zeebe |
| Blue/green deploy | Requires DB schema alignment | Zero-downtime possible |
| Worker scaling | Limited by DB connection pool | Scale workers independently |
| Infrastructure needed | App + DB | App + Zeebe cluster + Elasticsearch |
| Deployment unit size | Single fat JAR | JAR (workers only) + external platform |
| Infrastructure complexity | Low | Medium–High (managed via Helm) |

---

#### 5.4.3 Infrastructure Setup — Camunda 8

**Option A: Docker Compose (Development / Staging)**

```yaml
# docker-compose.yml — full Camunda 8 stack
version: "3.8"

services:
  zeebe:
    image: camunda/zeebe:8.5.0
    ports:
      - "26500:26500"   # gRPC (workers connect here)
      - "9600:9600"     # management/metrics
    environment:
      ZEEBE_BROKER_EXPORTERS_ELASTICSEARCH_CLASSNAME: io.camunda.zeebe.exporter.ElasticsearchExporter
      ZEEBE_BROKER_EXPORTERS_ELASTICSEARCH_ARGS_URL: http://elasticsearch:9200
      ZEEBE_BROKER_EXPORTERS_ELASTICSEARCH_ARGS_INDEX_PREFIX: zeebe-record
    volumes:
      - zeebe-data:/usr/local/zeebe/data
    depends_on: [elasticsearch]
    healthcheck:
      test: ["CMD", "zbctl", "status", "--insecure"]
      interval: 30s
      retries: 5

  operate:
    image: camunda/operate:8.5.0
    ports: ["8081:8080"]
    environment:
      CAMUNDA_OPERATE_ZEEBE_GATEWAYADDRESS: zeebe:26500
      CAMUNDA_OPERATE_ELASTICSEARCH_URL: http://elasticsearch:9200
      CAMUNDA_OPERATE_ZEEBEELASTICSEARCH_URL: http://elasticsearch:9200
    depends_on: [zeebe, elasticsearch]

  tasklist:
    image: camunda/tasklist:8.5.0
    ports: ["8082:8080"]
    environment:
      CAMUNDA_TASKLIST_ZEEBE_GATEWAYADDRESS: zeebe:26500
      CAMUNDA_TASKLIST_ELASTICSEARCH_URL: http://elasticsearch:9200
    depends_on: [zeebe, elasticsearch]

  elasticsearch:
    image: elasticsearch:8.9.0
    ports: ["9200:9200"]
    environment:
      discovery.type: single-node
      xpack.security.enabled: "false"
      ES_JAVA_OPTS: -Xms1g -Xmx1g
    volumes:
      - es-data:/usr/share/elasticsearch/data

volumes:
  zeebe-data:
  es-data:
```

**Option B: Kubernetes via Helm (Production)**

```bash
# Step 1: Add Camunda Helm repository
helm repo add camunda https://helm.camunda.io
helm repo update

# Step 2: Create namespace
kubectl create namespace camunda

# Step 3: Install the platform
helm install camunda camunda/camunda-platform \
  --namespace camunda \
  --version 10.x.x \        # Helm chart version (maps to C8.5)
  -f values-production.yaml
```

```yaml
# values-production.yaml — production-grade configuration
global:
  image:
    tag: 8.5.0
  identity:
    auth:
      enabled: true
      publicIssuerUrl: "https://keycloak.mycompany.com/realms/camunda"

zeebe:
  clusterSize: 3          # number of broker pods
  partitionCount: 3       # parallel partitions (tune for throughput)
  replicationFactor: 3    # replicas per partition (fault tolerance)
  resources:
    requests:
      cpu: "2"
      memory: "4Gi"
    limits:
      cpu: "4"
      memory: "8Gi"
  pvcSize: 128Gi          # persistent volume per broker

zeebeGateway:
  replicas: 2             # HA gateway
  resources:
    requests:
      cpu: "1"
      memory: "1Gi"

operate:
  enabled: true
  resources:
    requests:
      cpu: "500m"
      memory: "1Gi"

tasklist:
  enabled: true
  resources:
    requests:
      cpu: "500m"
      memory: "1Gi"

optimize:
  enabled: false          # enable if analytics needed

elasticsearch:
  enabled: true
  master:
    replicaCount: 3
    persistence:
      size: 64Gi
  resources:
    requests:
      cpu: "1"
      memory: "2Gi"
```

**Verify the platform is ready:**
```bash
# Check all pods are Running
kubectl get pods -n camunda

# Check Zeebe cluster health
kubectl exec -n camunda deployment/camunda-zeebe-gateway -- \
  zbctl status --insecure

# Expected output:
# Cluster size: 3
# Partitions count: 3
# Replication factor: 3
# Gateway version: 8.5.x
# Brokers: 3 (all healthy)
```

---

#### 5.4.4 Application Deployment — Camunda 8

**Kubernetes Deployment Manifest for Your App**

```yaml
# k8s/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
  namespace: my-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: order-service
  template:
    metadata:
      labels:
        app: order-service
    spec:
      containers:
        - name: order-service
          image: mycompany/order-service:2.0.0   # C8-migrated version
          ports:
            - containerPort: 8080
          env:
            - name: ZEEBE_CLIENT_BROKER_GATEWAY-ADDRESS
              value: "camunda-zeebe-gateway.camunda.svc.cluster.local:26500"
            - name: ZEEBE_CLIENT_SECURITY_PLAINTEXT
              value: "false"
            # If using Camunda Cloud (SaaS):
            # - name: ZEEBE_CLIENT_CLOUD_CLUSTER-ID
            #   valueFrom: { secretKeyRef: { name: camunda-cloud, key: cluster-id } }
            # - name: ZEEBE_CLIENT_CLOUD_CLIENT-ID
            #   valueFrom: { secretKeyRef: { name: camunda-cloud, key: client-id } }
            # - name: ZEEBE_CLIENT_CLOUD_CLIENT-SECRET
            #   valueFrom: { secretKeyRef: { name: camunda-cloud, key: client-secret } }
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 20
            periodSeconds: 10
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 15
          resources:
            requests:
              cpu: "500m"
              memory: "512Mi"
            limits:
              cpu: "1"
              memory: "1Gi"
---
apiVersion: v1
kind: Service
metadata:
  name: order-service
  namespace: my-app
spec:
  selector:
    app: order-service
  ports:
    - port: 80
      targetPort: 8080
```

**Spring Boot Actuator Health for Zeebe**

Add this to `application.yml` to include Zeebe connectivity in health checks:
```yaml
management:
  health:
    zeebe:
      enabled: true
  endpoint:
    health:
      show-details: always
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

---

#### 5.4.5 CI/CD Pipeline Migration

**Camunda 7 — Typical CI/CD Flow**
```
Build JAR → Run unit tests → Deploy JAR (with embedded engine + BPMN)
                                    ↓
                          App starts → BPMN auto-deployed on startup
```

**Camunda 8 — New CI/CD Flow**
```
┌────────────────────────────────────────────────────────┐
│ Track 1: Platform (once, then only on version upgrades)│
│   Helm upgrade camunda-platform → Zeebe + Operate +    │
│   Tasklist + Elasticsearch                             │
└────────────────────────────────────────────────────────┘
┌────────────────────────────────────────────────────────┐
│ Track 2: Application (every feature release)           │
│   Build JAR → Tests → Push image → kubectl rollout     │
│   App starts → Auto-deploys BPMN to Zeebe on startup   │
└────────────────────────────────────────────────────────┘
┌────────────────────────────────────────────────────────┐
│ Track 3: Process-only changes (BPMN/DMN hot-deploy)    │
│   zbctl deploy resource processes/*.bpmn               │
│   (no app restart needed for process definition only)  │
└────────────────────────────────────────────────────────┘
```

**GitHub Actions Pipeline Example**

```yaml
# .github/workflows/deploy.yml
name: Build and Deploy

on:
  push:
    branches: [main]

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up Java 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Build and test
        run: mvn clean verify

      - name: Build Docker image
        run: docker build -t mycompany/order-service:${{ github.sha }} .

      - name: Push image
        run: docker push mycompany/order-service:${{ github.sha }}

  deploy-platform:
    runs-on: ubuntu-latest
    if: contains(github.event.head_commit.message, '[platform-upgrade]')
    steps:
      - name: Upgrade Camunda Platform via Helm
        run: |
          helm upgrade camunda camunda/camunda-platform \
            --namespace camunda \
            --reuse-values \
            --set global.image.tag=${{ env.CAMUNDA_VERSION }}

  deploy-application:
    runs-on: ubuntu-latest
    needs: build-and-test
    steps:
      - name: Deploy application to Kubernetes
        run: |
          kubectl set image deployment/order-service \
            order-service=mycompany/order-service:${{ github.sha }} \
            -n my-app
          kubectl rollout status deployment/order-service -n my-app

  deploy-processes-only:
    runs-on: ubuntu-latest
    if: contains(github.event.head_commit.message, '[process-only]')
    steps:
      - name: Deploy BPMN/DMN directly to Zeebe (no app restart)
        run: |
          zbctl deploy resource processes/*.bpmn decisions/*.dmn \
            --address ${{ secrets.ZEEBE_GATEWAY_ADDRESS }} \
            --clientId ${{ secrets.ZEEBE_CLIENT_ID }} \
            --clientSecret ${{ secrets.ZEEBE_CLIENT_SECRET }}
```

**Deploying BPMN/DMN without restarting the app:**
```bash
# zbctl CLI — deploy a single process
zbctl deploy resource processes/order-process.bpmn \
  --address localhost:26500 \
  --insecure

# Deploy multiple resources in one call (atomic)
zbctl deploy resource \
  processes/order-process.bpmn \
  processes/payment-process.bpmn \
  decisions/order-routing.dmn \
  --address localhost:26500 \
  --insecure

# Verify deployed versions
zbctl get processes --address localhost:26500 --insecure
```

---

#### 5.4.6 Migration Deployment Phases

This is the recommended phased approach for migrating a live production system from C7 to C8, with zero or minimal downtime.

```
Timeline ──────────────────────────────────────────────────────────────────►

Week 1–2: Setup & Parallel Run
  ┌──────────────────────────────────────────────────────────────────────┐
  │ Camunda 7 (live)        ← all traffic continues here                │
  │ Camunda 8 (new)         ← provisioned but idle (no live traffic)    │
  └──────────────────────────────────────────────────────────────────────┘

Week 3–4: Shadow Mode (Optional but Recommended)
  ┌──────────────────────────────────────────────────────────────────────┐
  │ Camunda 7 (live)        ← real traffic                              │
  │ Camunda 8 (shadow)      ← mirrors new starts (read/verify only)     │
  └──────────────────────────────────────────────────────────────────────┘

Week 5+: Drain-Out Cutover
  ┌──────────────────────────────────────────────────────────────────────┐
  │ Camunda 7 (draining)    ← no new instances; finishes in-flight work  │
  │ Camunda 8 (live)        ← all new process starts route here          │
  └──────────────────────────────────────────────────────────────────────┘

Week N: Decommission C7
  ┌──────────────────────────────────────────────────────────────────────┐
  │ Camunda 7 (decommissioned after all instances complete)              │
  │ Camunda 8 (sole platform)                                            │
  └──────────────────────────────────────────────────────────────────────┘
```

**Phase 1: Provision Camunda 8 in Parallel (no traffic)**

```bash
# Deploy C8 platform to a new namespace alongside C7
kubectl create namespace camunda-c8
helm install camunda-c8 camunda/camunda-platform \
  --namespace camunda-c8 \
  -f values-production.yaml

# Deploy C8-migrated application (separate deployment, no traffic yet)
kubectl apply -f k8s/order-service-c8.yaml -n my-app-c8

# Smoke test with synthetic requests
curl -X POST http://order-service-c8.my-app-c8/internal/test/start-order \
  -H 'Content-Type: application/json' \
  -d '{"orderId":"TEST-001","test":true}'
```

**Phase 2: Feature Flag — Route New Starts to C8**

Add a runtime switch in your application code to decide which engine handles a new process start. This avoids a hard-cutover deploy and lets you roll back by flipping a flag.

```java
@Service
public class ProcessRouter {

    @Value("${migration.use-camunda8:false}")
    private boolean useCamunda8;

    @Autowired
    private RuntimeService camunda7RuntimeService;      // C7 bean

    @Autowired
    private ZeebeClient zeebeClient;                    // C8 bean

    public void startOrderProcess(String orderId, Map<String, Object> vars) {
        if (useCamunda8) {
            zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId("order-process")
                .latestVersion()
                .variables(vars)
                .send().join();
        } else {
            camunda7RuntimeService.startProcessInstanceByKey(
                "order-process", vars
            );
        }
    }
}
```

```yaml
# application-production.yml
migration:
  use-camunda8: false   # flip to true to start routing new work to C8
```

Flip the flag with a config change (ConfigMap in Kubernetes, no redeploy required if using Spring Cloud Config or env var):
```bash
# Kubernetes: update ConfigMap to flip the flag
kubectl patch configmap app-config -n my-app \
  --type merge \
  -p '{"data":{"MIGRATION_USE_CAMUNDA8":"true"}}'

# Restart pods to pick up the new config (or use Spring Cloud Config for live reload)
kubectl rollout restart deployment/order-service -n my-app
```

**Phase 3: Monitor Drain Progress on Camunda 7**

```bash
# Check how many C7 process instances are still running
# Query via Camunda 7 REST API
curl "http://camunda7-app/engine-rest/process-instance/count?processDefinitionKey=order-process"

# Or via SQL
psql -h $DB_HOST -U camunda -d camunda -c "
  SELECT COUNT(*) as running_instances
  FROM ACT_RU_EXECUTION
  WHERE PROC_DEF_KEY_ = 'order-process'
    AND IS_ACTIVE_ = 1
    AND PARENT_ID_ IS NULL;
"
```

**Phase 4: Decommission Camunda 7**

Only proceed once all in-flight C7 instances have completed:

```bash
# Verify zero running instances in C7
RUNNING=$(curl -s "http://camunda7-app/engine-rest/process-instance/count" | jq '.count')
echo "Running C7 instances: $RUNNING"

# If $RUNNING == 0, safe to decommission
# 1. Export historical data (see Section 5.2)
# 2. Scale down the C7 app
kubectl scale deployment order-service-c7 --replicas=0 -n my-app-c7

# 3. Archive the C7 database (do NOT drop immediately — keep for audit)
pg_dump -h $DB_HOST -U camunda camunda_c7 > camunda7_archive_$(date +%Y%m%d).sql

# 4. Remove C7 resources (after audit retention period)
kubectl delete namespace my-app-c7
```

---

#### 5.4.7 Blue-Green Deployment for Camunda 8 Workers

Once fully on Camunda 8, you can deploy worker updates with zero downtime using a blue-green pattern. Zeebe jobs are pulled by workers, so stopping one deployment and starting another is safe — jobs stay locked in Zeebe until the lock timeout expires, then are re-acquired by the new workers.

```yaml
# Blue deployment (current live)
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service-blue
  labels:
    app: order-service
    slot: blue
spec:
  replicas: 3
  selector:
    matchLabels:
      app: order-service
      slot: blue
  template:
    metadata:
      labels:
        app: order-service
        slot: blue
    spec:
      containers:
        - name: order-service
          image: mycompany/order-service:1.9.0
          env:
            - name: ZEEBE_CLIENT_BROKER_GATEWAY-ADDRESS
              value: "camunda-zeebe-gateway.camunda:26500"
---
# Green deployment (new version — deploy before switching)
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service-green
  labels:
    app: order-service
    slot: green
spec:
  replicas: 3
  selector:
    matchLabels:
      app: order-service
      slot: green
  template:
    metadata:
      labels:
        app: order-service
        slot: green
    spec:
      containers:
        - name: order-service
          image: mycompany/order-service:2.0.0   # new version
          env:
            - name: ZEEBE_CLIENT_BROKER_GATEWAY-ADDRESS
              value: "camunda-zeebe-gateway.camunda:26500"
```

```bash
# Blue-green switch sequence:
# 1. Deploy green (both blue and green run simultaneously — Zeebe distributes jobs to both)
kubectl apply -f k8s/order-service-green.yaml

# 2. Verify green is healthy
kubectl rollout status deployment/order-service-green

# 3. Scale down blue (green now handles all jobs)
kubectl scale deployment order-service-blue --replicas=0

# 4. If issues arise: rollback by scaling blue back up and green down
kubectl scale deployment order-service-blue --replicas=3
kubectl scale deployment order-service-green --replicas=0
```

**Why this works:** Zeebe workers compete for jobs by polling. When blue is scaled down, its in-flight jobs remain locked only until the worker timeout expires (configured via `zeebe.client.job.timeout`), then become available for green workers to pick up. Set a short timeout during migration windows:

```yaml
zeebe:
  client:
    job:
      timeout: 30s   # reduce from default 5m during migration windows
```

---

#### 5.4.8 BPMN Process Version Management

In Camunda 8, every `deploy` call creates a new **version** of the process definition. You must actively manage which version new instances use.

```java
@Service
public class ProcessDeploymentService {

    @Autowired
    private ZeebeClient zeebeClient;

    // Deploy and get the new version number
    public int deployAndGetVersion(String bpmnClasspath) {
        DeploymentEvent event = zeebeClient.newDeployResourceCommand()
            .addResourceFromClasspath(bpmnClasspath)
            .send()
            .join();

        ProcessMetadata process = event.getProcesses().get(0);
        System.out.printf("Deployed %s version %d (key=%d)%n",
            process.getBpmnProcessId(),
            process.getVersion(),
            process.getProcessDefinitionKey());

        return process.getVersion();
    }

    // Start a specific version (for testing a new deployment)
    public void startSpecificVersion(String processId, int version, Map<String, Object> vars) {
        zeebeClient.newCreateInstanceCommand()
            .bpmnProcessId(processId)
            .version(version)     // pinned version
            .variables(vars)
            .send()
            .join();
    }

    // Start latest version (normal operation)
    public void startLatest(String processId, Map<String, Object> vars) {
        zeebeClient.newCreateInstanceCommand()
            .bpmnProcessId(processId)
            .latestVersion()
            .variables(vars)
            .send()
            .join();
    }
}
```

**Version rollback for process definitions:**
```bash
# Check all deployed versions of a process
zbctl get processes --insecure | grep order-process

# A new deployment always creates the next version — you cannot "undeploy"
# To roll back: re-deploy the previous BPMN file (creates version N+1 with old content)
zbctl deploy resource processes/order-process-v1.bpmn --insecure

# Pin your app to start a specific version during the rollback window:
# application.yml:
#   process.order-process.version: 3   # pinned to version 3
```

---

#### 5.4.9 Environment Configuration Matrix

| Config Item | Camunda 7 | Camunda 8 Self-Managed | Camunda 8 SaaS |
|---|---|---|---|
| Engine endpoint | Embedded (none) | `zeebe.client.broker.gateway-address` | `zeebe.client.cloud.*` |
| TLS | N/A | `zeebe.client.security.plaintext=false` | Enforced (built-in) |
| Auth | Basic (DB users) | Keycloak / Identity | OAuth2 (cloud identity) |
| History query | `HistoryService` (Java) | Operate REST API | Operate REST API |
| Process monitoring | Cockpit (`/camunda`) | Operate (`:8081`) | Camunda Cloud Console |
| Task management | Tasklist (`/camunda/app/tasklist`) | Tasklist (`:8082`) | Cloud Tasklist |
| BPMN deploy | Classpath auto or `RepositoryService` | Classpath auto or `zbctl deploy` | Same |

**application.yml template with environment profiles:**

```yaml
# application.yml (shared base)
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USER}
    password: ${DB_PASS}

---
# application-local.yml — local Docker Compose
spring:
  config:
    activate:
      on-profile: local
zeebe:
  client:
    broker:
      gateway-address: localhost:26500
    security:
      plaintext: true

---
# application-staging.yml — staging K8s cluster
spring:
  config:
    activate:
      on-profile: staging
zeebe:
  client:
    broker:
      gateway-address: camunda-zeebe-gateway.camunda-staging:26500
    security:
      plaintext: false
      certChainPath: /etc/certs/zeebe.crt

---
# application-production.yml — production K8s or SaaS
spring:
  config:
    activate:
      on-profile: production
zeebe:
  client:
    cloud:
      cluster-id: ${ZEEBE_CLUSTER_ID}
      client-id: ${ZEEBE_CLIENT_ID}
      client-secret: ${ZEEBE_CLIENT_SECRET}
      region: ${ZEEBE_REGION}
```

---

#### 5.4.10 Rollback Plan

| Scenario | Rollback Action |
|---|---|
| App deploy fails | `kubectl rollout undo deployment/order-service` — previous pod version resumes pulling Zeebe jobs |
| New BPMN breaks running instances | Re-deploy previous BPMN file to create a new version; running instances on old version are unaffected |
| Zeebe cluster unreachable | Worker pods fail health check, Kubernetes restarts them; in-flight jobs are retried after lock timeout |
| Full migration rollback (C8 → C7) | Flip feature flag `migration.use-camunda8=false`; C7 continues in-flight work; new starts return to C7 |
| Zeebe data loss | Restore from Zeebe backup (RocksDB snapshot); configure `ZEEBE_BROKER_DATA_BACKUP_*` in production |

**Configure Zeebe backups (production requirement):**

```yaml
# values.yaml — enable Zeebe backup to S3
zeebe:
  env:
    - name: ZEEBE_BROKER_DATA_BACKUP_STORE
      value: S3
    - name: ZEEBE_BROKER_DATA_BACKUP_S3_BUCKETNAME
      value: my-zeebe-backups
    - name: ZEEBE_BROKER_DATA_BACKUP_S3_REGION
      value: eu-west-1
    - name: ZEEBE_BROKER_DATA_BACKUP_S3_ENDPOINT
      value: ""  # leave empty for AWS, set for MinIO
```

```bash
# Trigger a manual backup via management API
curl -X POST http://zeebe-broker:9600/actuator/backups \
  -H 'Content-Type: application/json' \
  -d '{"backupId": 1}'

# Check backup status
curl http://zeebe-broker:9600/actuator/backups/1
```

---

### 5.5 Worker / Job Handler Migration

#### Error Handling Patterns

**Camunda 7:**
```java
// Retry controlled by job config; exception auto-creates incident
public void execute(DelegateExecution execution) throws Exception {
    throw new BpmnError("PAYMENT_DECLINED", "Card rejected");
}
```

**Camunda 8:**
```java
@JobWorker(type = "process-payment", maxJobsActive = 10, timeout = 30000)
public void processPayment(JobClient client, ActivatedJob job) {
    try {
        paymentService.charge(job.getVariablesAsMap());
        client.newCompleteCommand(job.getKey()).send().join();

    } catch (PaymentDeclinedException e) {
        // Throw BPMN error → routes to error boundary event
        client.newThrowErrorCommand(job.getKey())
            .errorCode("PAYMENT_DECLINED")
            .errorMessage(e.getMessage())
            .send().join();

    } catch (TransientException e) {
        // Fail with retries remaining
        client.newFailCommand(job.getKey())
            .retries(job.getRetries() - 1)
            .retryBackoff(Duration.ofSeconds(5))
            .errorMessage(e.getMessage())
            .send().join();

    } catch (Exception e) {
        // No retries — create incident immediately
        client.newFailCommand(job.getKey())
            .retries(0)
            .errorMessage(e.getMessage())
            .send().join();
    }
}
```

---

### 5.6 Process Variables Migration

#### Camunda 7 Variable Handling
```java
// Get typed variables
String orderId = (String) execution.getVariable("orderId");
execution.setVariable("result", myObject);  // serialized as JSON or Java

// Typed API
StringValue value = execution.getVariableTyped("orderId");
```

#### Camunda 8 Variable Handling
```java
// All variables are JSON; access as Map
Map<String, Object> vars = job.getVariablesAsMap();
String orderId = (String) vars.get("orderId");

// Deserialize with Jackson
ObjectMapper mapper = new ObjectMapper();
OrderDto order = mapper.convertValue(vars.get("order"), OrderDto.class);

// Or get as typed POJO directly (with Spring starter)
@JobWorker(type = "validate-order")
public void validateOrder(JobClient client, ActivatedJob job,
                          @Variable String orderId,          // auto-injected
                          @Variable Double totalAmount) {    // auto-injected
    // ...
    client.newCompleteCommand(job.getKey())
        .variable("isValid", true)
        .send().join();
}
```

#### Variable Scope Differences

| Aspect | Camunda 7 | Camunda 8 |
|---|---|---|
| Variable scope | Process, Execution, Task | Process (flat JSON document) |
| Local variables | Supported (execution-scoped) | Use I/O mappings in BPMN |
| Variable type | Typed (String, Integer, etc.) | JSON (String, Number, Boolean, Object, Array) |
| Binary/Object | Stored as BLOB | Must serialize to JSON string |
| Variable size limit | DB row limit | ~32KB per variable (Zeebe) |

---

### 5.7 REST API Migration

#### Camunda 7 REST API (embedded)
```bash
# Base URL: http://localhost:8080/engine-rest

# Start process instance
POST /engine-rest/process-definition/key/order-process/start
{
  "variables": {
    "orderId": { "value": "123", "type": "String" }
  }
}

# Get running instances
GET /engine-rest/process-instance?processDefinitionKey=order-process

# Complete user task
POST /engine-rest/task/{taskId}/complete
{
  "variables": {
    "approved": { "value": true, "type": "Boolean" }
  }
}
```

#### Camunda 8 REST API (via Operate/Tasklist/Zeebe Gateway)
```bash
# Zeebe gRPC is the primary protocol
# REST available via Zeebe REST API (8.4+) or Operate REST

# Start process via Zeebe REST (8.4+)
POST http://localhost:8080/v1/process-instances
{
  "bpmnProcessId": "order-process",
  "version": -1,
  "variables": {
    "orderId": "123"
  }
}

# Query instances via Operate REST API
POST http://localhost:8081/v1/process-instances/search
{
  "filter": { "bpmnProcessId": "order-process" },
  "size": 50
}

# Complete user task via Tasklist REST API
PATCH http://localhost:8082/v1/tasks/{taskId}/complete
{
  "variables": [
    { "name": "approved", "value": "true" }
  ]
}
```

---

### 5.8 Migration Approach Options

Choose one of three strategies based on your risk tolerance and system constraints.

#### Option A: Drain-Out Migration (Recommended)

Keep Camunda 7 running for existing instances; route all new process starts to Camunda 8.

```
1. Deploy Camunda 8 cluster alongside Camunda 7
2. Route new process starts → Camunda 8 workers
3. Camunda 7 continues processing in-flight instances
4. Once all C7 instances complete, decommission C7
```

**Pros:** Safe, low risk, no forced cutover  
**Cons:** Requires running two systems in parallel temporarily

---

#### Option B: Runtime Migration

Use migration tooling to move running process instances from C7 to C8.

**Pros:** Avoids dual-runtime maintenance period  
**Cons:** Complex; tooling is still evolving and not all instance types are supported

---

#### Option C: Big-Bang Migration

Stop Camunda 7 completely, migrate everything at once, start fresh on Camunda 8.

**Pros:** Clean cut, no parallel maintenance  
**Cons:** High risk; all in-flight work is lost; not recommended for production systems with active instances

---

> **Recommendation:** Option A (Drain-Out) is the safest approach for most production systems. Start new work on C8 immediately after deployment, while C7 finishes its remaining workload.

---

## 6. Step-by-Step Migration Checklist

### Phase 1: Assessment (Week 1–2)
- [ ] Inventory all BPMN/DMN files in use
- [ ] List all JavaDelegate implementations
- [ ] List all ExecutionListeners and TaskListeners
- [ ] Identify CMMN usage (not supported in C8)
- [ ] Identify all process variables and their types (especially binary/Java objects)
- [ ] Document all message correlations and their correlation keys
- [ ] Review expressions (JUEL → FEEL rewrite needed)
- [ ] List all REST API consumers
- [ ] Assess running process instances that cannot be migrated automatically

### Phase 2: Infrastructure Setup (Week 2–3)
- [ ] Upgrade Java to 17+
- [ ] Upgrade Spring Boot to 3.x
- [ ] Set up Zeebe locally via Docker Compose (see Section 3.10)
- [ ] Set up Elasticsearch
- [ ] Install Operate and Tasklist
- [ ] Configure Zeebe client in application.yml

### Phase 3: BPMN Migration (Week 3–5)
- [ ] Run Camunda Diagram Converter on all BPMN files
- [ ] Replace `camunda:` namespaced attributes with `zeebe:` equivalents
- [ ] Convert all service task references to `zeebe:taskDefinition type=`
- [ ] Update user task assignment definitions
- [ ] Update message subscription correlation keys (add `zeebe:subscription`)
- [ ] Convert all JUEL expressions `${...}` to FEEL `=...`
- [ ] Test each process in Zeebe locally

### Phase 4: Code Migration (Week 5–8)
- [ ] Replace all `JavaDelegate` classes with `@JobWorker` methods
- [ ] Replace `ExecutionListener` with dedicated job workers or boundary events
- [ ] Replace `TaskListener` logic with assignment handling in Tasklist
- [ ] Replace `RuntimeService.startProcessInstanceByKey()` with `ZeebeClient`
- [ ] Replace `RuntimeService.correlateMessage()` with `newPublishMessageCommand()`
- [ ] Replace `HistoryService` queries with Operate REST API calls
- [ ] Replace `TaskService` calls with Tasklist REST API or Zeebe commands
- [ ] Update variable handling (remove typed variables, use JSON)
- [ ] Update error handling (add BPMN error throws and fail commands)

### Phase 5: Testing (Week 8–10)
- [ ] Unit test each worker with `ZeebeTestEngine` (in-memory)
- [ ] Integration test full process flows end-to-end
- [ ] Load test worker concurrency settings (`maxJobsActive`, `timeout`)
- [ ] Verify message correlation works correctly
- [ ] Test incident handling and retry behavior

### Phase 6: Deployment & Cutover (Week 10–12)
- [ ] Deploy Zeebe cluster to staging
- [ ] Deploy migrated application to staging
- [ ] Validate all processes in staging
- [ ] Plan for in-flight processes (run C7 until all instances complete, then cut over)
- [ ] Deploy to production with Zeebe cluster
- [ ] Monitor Operate for incidents
- [ ] Keep C7 running in read-only/archive mode until all historical data needs are met

---

## 7. Known Gotchas & Pitfalls

### 1. CMMN is Gone
Camunda 8 does not support CMMN (Case Management Model and Notation). You must remodel CMMN processes in BPMN with event subprocesses or ad-hoc subprocesses.

### 2. ExecutionListeners are Not Supported
Camunda 8 has no equivalent of ExecutionListeners. Move that logic into job workers or use event-driven patterns (start/end events with service tasks).

### 3. FEEL vs JUEL — Big Gotcha
Every `${expression}` in your BPMN must become `=expression`. The semantics differ:
```
C7 JUEL:  ${amount > 100 ? "large" : "small"}
C8 FEEL:  =if amount > 100 then "large" else "small"

C7 JUEL:  ${item.name}
C8 FEEL:  =item.name
```

### 4. No In-Process Engine = Async Only
Camunda 8 workers always operate asynchronously. There's no "synchronous" delegation. Use `withResult()` for request-response patterns.

### 5. Variable Size Limits
Zeebe limits individual variable values to approximately 32KB. Large objects must be stored externally (e.g., S3, your DB) with only the reference stored as a process variable.

### 6. Transaction Behavior Changed
Camunda 7 participates in your Spring transaction. In Camunda 8, each job worker call is its own unit of work. You must handle idempotency manually if your service task and `newCompleteCommand` fail between calls.

```java
// Camunda 8: Make workers idempotent!
@JobWorker(type = "create-invoice")
public void createInvoice(JobClient client, ActivatedJob job) {
    String orderId = (String) job.getVariablesAsMap().get("orderId");

    // Check if already processed (idempotency)
    if (!invoiceRepository.existsByOrderId(orderId)) {
        invoiceRepository.save(new Invoice(orderId));
    }

    client.newCompleteCommand(job.getKey()).send().join();
}
```

### 7. Running Instances Cannot Be Migrated
There is no automated migration of in-flight C7 process instances to C8. You have two options:
- **Drain approach:** Stop accepting new work in C7, let all running instances complete, then switch to C8.
- **Parallel approach:** Run C7 and C8 in parallel, route new requests to C8, let C7 finish existing work.

### 8. History Query API is Completely Different
All `HistoryService` queries must be rewritten as Operate REST API calls or Elasticsearch queries.

### 9. Spring Boot Version Upgrade
Camunda 8 Spring Starter requires Spring Boot 3.x, which requires Java 17 and has Jakarta EE namespace changes (`javax.*` → `jakarta.*`).

### 10. Key Behavioral Changes Summary

- Service tasks are always **asynchronous** — no synchronous delegation
- Workers must be **actively running** to pick up jobs; stopped workers leave jobs locked until timeout
- There is **no embedded engine** — if Zeebe is unreachable, your app cannot start or complete processes
- Manual tasks without a worker implementation will be **skipped** (treated as pass-through)
- No backward compatibility with Camunda 7 APIs — this is a re-architecture, not an upgrade

---

## Quick Reference: Import Changes

```java
// Remove all these imports
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.delegate.*;
import org.camunda.bpm.engine.runtime.*;
import org.camunda.bpm.engine.task.*;
import org.camunda.bpm.engine.history.*;
import org.camunda.bpm.engine.repository.*;

// Add these imports
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.*;
import io.camunda.zeebe.client.api.worker.*;
import io.camunda.spring.client.annotation.JobWorker;
import io.camunda.spring.client.annotation.Variable;
```

---

*This document covers Camunda 7.21 and Camunda 8.5. Always verify against the official Camunda documentation at [docs.camunda.io](https://docs.camunda.io) for the latest changes.*
