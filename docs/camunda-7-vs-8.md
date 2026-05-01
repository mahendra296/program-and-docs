# Camunda 7 vs Camunda 8 — Complete Guide

## Table of Contents
- [Overview](#overview)
- [Key Differences](#key-differences)
- [Architecture](#architecture)
    - [Camunda 7 Architecture](#camunda-7-architecture)
    - [Camunda 8 Architecture](#camunda-8-architecture)
- [BPMN Diagram Deployment](#bpmn-diagram-deployment)
    - [Deploying in Camunda 7](#deploying-in-camunda-7)
    - [Deploying in Camunda 8](#deploying-in-camunda-8)
- [Spring Boot Integration](#spring-boot-integration)
    - [Camunda 7 with Spring Boot](#camunda-7-with-spring-boot)
    - [Camunda 8 with Spring Boot](#camunda-8-with-spring-boot)
- [Comparison Table](#comparison-table)
- [When to Use Which](#when-to-use-which)
- [How Camunda Resolves Delegates and Job Workers](#how-camunda-resolves-delegates-and-job-workers)
    - [Camunda 7 — How the Engine Finds a Java Delegate](#camunda-7--how-the-engine-finds-a-java-delegate)
    - [Camunda 8 — How the Broker Finds a Job Worker](#camunda-8--how-the-broker-finds-a-job-worker)
    - [Polling Behaviour in Detail — When, How Often, How Long](#polling-behaviour-in-detail--when-how-often-how-long)
    - [Who Triggers the gRPC Request — JVM or Something Else?](#who-triggers-the-grpc-request--jvm-or-something-else)
    - [Multiple Job Workers in One Spring App](#multiple-job-workers-in-one-spring-app)
    - [No Host URL Needed — Why the Models Differ](#no-host-url-needed--why-the-models-differ)
    - [What Happens Step-by-Step When a Service Task Runs](#what-happens-step-by-step-when-a-service-task-runs)
    - [Common Mistakes and How to Avoid Them](#common-mistakes-and-how-to-avoid-them)
- [Third-Party Integrations — Triggering Service Tasks Externally](#third-party-integrations--triggering-service-tasks-externally)
    - [Camunda 7 — External Task Pattern](#camunda-7--external-task-pattern)
    - [Camunda 8 — Any Language, Any Service](#camunda-8--any-language-any-service)
    - [Side-by-Side: Third-Party Integration Patterns](#side-by-side-third-party-integration-patterns)
    - [End-to-End Connection Summary](#end-to-end-connection-summary)

---

## Overview

**Camunda 7** embeds the process engine directly inside your application (or runs it as a shared database-backed engine). It is a mature, battle-tested platform ideal for traditional enterprise deployments.

**Camunda 8** is a completely redesigned platform built for cloud-native, distributed environments. It uses **Zeebe** — a distributed, broker-based engine that runs entirely outside your application. Your app becomes a *worker* that connects to it via gRPC.

---

## Key Differences

| Feature | Camunda 7 | Camunda 8 |
|---|---|---|
| Engine location | Embedded inside the app (JVM) | External Zeebe broker |
| Architecture style | Monolithic / embedded | Distributed / microservices |
| Database | Relational DB (PostgreSQL, MySQL, H2) | Zeebe manages its own state (RocksDB) |
| Scaling | Scale the whole app | Scale workers independently |
| Transaction scope | Shared with your DB transaction | Eventual consistency |
| Deployment model | BPMN via classpath or API at startup | BPMN deployed to broker separately |
| Service task style | `JavaDelegate` (synchronous) | `@JobWorker` (async job polling) |
| Monitoring UI | Cockpit (bundled) | Operate (separate service) |
| Human tasks UI | Tasklist (bundled) | Tasklist (separate service) |
| Cloud support | Self-hosted only | Self-hosted + Camunda SaaS |
| License | Open source (AGPLv3) + Enterprise | Camunda Platform 8 (SSPL) + SaaS |

---

## Architecture

### Camunda 7 Architecture

The process engine is embedded **inside** the Spring Boot application as a Java library (JAR). It shares the same JVM, the same database transaction, and the same application lifecycle.

```
┌─────────────────────────────────────────────────────┐
│              Spring Boot Application                │
│                                                     │
│   ┌──────────────────────────────────────────────┐  │
│   │          Camunda Process Engine              │  │
│   │  BPMN execution, job scheduling, history    │  │
│   │  Embedded as a Java library (JAR)           │  │
│   └───────────────┬──────────────┬──────────────┘  │
│                   │              │                  │
│   ┌───────────────▼──┐  ┌────────▼───────────────┐  │
│   │    REST API      │  │    Java Delegates       │  │
│   │  Camunda engine  │  │  Service task handlers  │  │
│   └──────────────────┘  └────────────────────────┘  │
│                                                     │
│   ┌──────────────────────────────────────────────┐  │
│   │         Cockpit & Tasklist (Web UI)          │  │
│   │      Bundled — served from same app server  │  │
│   └──────────────────────────────────────────────┘  │
│                                                     │
└────────────────────────┬────────────────────────────┘
                         │ JDBC
              ┌──────────▼──────────┐
              │    Relational DB    │
              │  PostgreSQL / MySQL │
              │     H2 (dev)        │
              └─────────────────────┘
```

**Key characteristics:**
- Engine lives **inside** the app — same JVM, same DB transaction
- BPMN files deployed on app startup via classpath or API call
- Java delegates are Spring beans — called **synchronously** by the engine
- All components (engine, UI, API) run in the same process

---

### Camunda 8 Architecture

The process engine (Zeebe broker) runs **completely outside** your application. Your Spring Boot apps are stateless workers that poll the broker for jobs.

```
┌────────────────────────────────────────────────────────────┐
│                    Camunda 8 Platform                      │
│                  (runs outside your app)                   │
│                                                            │
│   ┌──────────────────────────────────────────────────────┐ │
│   │                   Zeebe Broker                       │ │
│   │   Distributed, event-sourced engine                  │ │
│   │   Manages job activation & BPMN state               │ │
│   └────────────────────┬──────────────────┬─────────────┘ │
│                        │                  │               │
│           ┌────────────▼──────┐  ┌────────▼──────────┐   │
│           │     Operate       │  │     Tasklist       │   │
│           │  Monitor process  │  │  Human task UI    │   │
│           │  instances        │  │                   │   │
│           └───────────────────┘  └───────────────────┘   │
│                                                            │
└────────────────────────────────────────────────────────────┘
                 ▲ gRPC / REST (job polling)
     ┌───────────┴───────────────────────────┐
     │                                       │
┌────▼────────────────────┐   ┌─────────────▼────────────────┐
│   Spring Boot Worker A  │   │   Spring Boot Worker B       │
│  @JobWorker("payment")  │   │  @JobWorker("shipping")      │
│  Polls broker for jobs  │   │  Polls broker for jobs       │
│  Independent service    │   │  Scales independently        │
│  No engine embedded     │   │  Stateless                   │
└─────────────────────────┘   └──────────────────────────────┘
```

**Key characteristics:**
- Workers **poll** the broker — no engine embedded in the app
- Broker manages all process state (uses RocksDB internally)
- Workers are fully stateless and can scale independently
- Elasticsearch is used by Operate for process history/monitoring
- BPMN diagrams deployed to the broker via API or Camunda Modeler

---

## BPMN Diagram Deployment

### Deploying in Camunda 7

**Option 1: Auto-deploy on startup (via `application.properties`)**

```properties
# application.properties
camunda.bpm.auto-deployment-enabled=true
```

Place your BPMN files in:
```
src/main/resources/
  └── processes/
        └── order-process.bpmn
```

The engine auto-discovers and deploys all `.bpmn` files from the classpath on startup.

---

**Option 2: Programmatic deployment**

```java
@Service
public class ProcessDeploymentService {

    @Autowired
    private RepositoryService repositoryService;

    @PostConstruct
    public void deployProcesses() {
        repositoryService.createDeployment()
            .name("Order Process")
            .addClasspathResource("processes/order-process.bpmn")
            .addClasspathResource("processes/payment-process.bpmn")
            .enableDuplicateFiltering(true)  // skip if unchanged
            .deploy();
    }
}
```

---

**Option 3: Via REST API**

```bash
curl -X POST http://localhost:8080/engine-rest/deployment/create \
  -F "deployment-name=order-process" \
  -F "order-process.bpmn=@/path/to/order-process.bpmn"
```

---

### Deploying in Camunda 8

In Camunda 8, BPMN files are deployed **to the Zeebe broker**, separately from your application.

**Option 1: Via Camunda Modeler (GUI)**

Open the BPMN file in Camunda Modeler → Click **Deploy** → Point to your Zeebe broker endpoint.

---

**Option 2: Programmatic deployment using Zeebe Java client**

```java
@Service
public class ProcessDeploymentService {

    @Autowired
    private ZeebeClient zeebeClient;

    @PostConstruct
    public void deployProcesses() {
        DeploymentEvent deployment = zeebeClient
            .newDeployResourceCommand()
            .addResourceFromClasspath("processes/order-process.bpmn")
            .addResourceFromClasspath("processes/payment-process.bpmn")
            .send()
            .join();

        System.out.println("Deployed process version: "
            + deployment.getProcesses().get(0).getVersion());
    }
}
```

---

**Option 3: Via Zeebe REST API**

```bash
curl -X POST http://localhost:26500/v1/deployments \
  -H "Content-Type: multipart/form-data" \
  -F "resources=@/path/to/order-process.bpmn"
```

---

**Option 4: Via Spring Boot auto-deploy (spring-zeebe)**

```yaml
# application.yaml
zeebe:
  client:
    broker:
      gateway-address: localhost:26500
    security:
      plaintext: true
# Place BPMN files in: src/main/resources/processes/
# They are auto-deployed on startup by spring-zeebe starter
```

---

## Spring Boot Integration

### Camunda 7 with Spring Boot

**Maven dependency:**

```xml
<dependency>
    <groupId>org.camunda.bpm.springboot</groupId>
    <artifactId>camunda-bpm-spring-boot-starter-rest</artifactId>
    <version>7.20.0</version>
</dependency>
<dependency>
    <groupId>org.camunda.bpm.springboot</groupId>
    <artifactId>camunda-bpm-spring-boot-starter-webapp</artifactId>
    <version>7.20.0</version>
</dependency>
```

**application.properties:**

```properties
camunda.bpm.admin-user.id=admin
camunda.bpm.admin-user.password=admin
camunda.bpm.auto-deployment-enabled=true
spring.datasource.url=jdbc:postgresql://localhost:5432/camunda
spring.datasource.username=camunda
spring.datasource.password=camunda
```

**Starting a process instance:**

```java
@Service
public class OrderService {

    @Autowired
    private RuntimeService runtimeService;

    public void startOrderProcess(String orderId) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", orderId);
        variables.put("amount", 150.00);

        runtimeService.startProcessInstanceByKey("order-process", variables);
    }
}
```

**Java Delegate (service task handler):**

```java
@Component("paymentDelegate")
public class PaymentDelegate implements JavaDelegate {

    @Autowired
    private PaymentService paymentService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String orderId = (String) execution.getVariable("orderId");
        Double amount = (Double) execution.getVariable("amount");

        String status = paymentService.processPayment(orderId, amount);

        // Write output variable back to process
        execution.setVariable("paymentStatus", status);
    }
}
```

In your BPMN:
```xml
<serviceTask id="paymentTask"
             camunda:delegateExpression="${paymentDelegate}" />
```

**Handling user tasks:**

```java
@Service
public class TaskService {

    @Autowired
    private org.camunda.bpm.engine.TaskService taskService;

    public List<Task> getTasksForUser(String userId) {
        return taskService.createTaskQuery()
            .taskAssignee(userId)
            .orderByTaskCreateTime().asc()
            .list();
    }

    public void completeTask(String taskId, Map<String, Object> variables) {
        taskService.complete(taskId, variables);
    }
}
```

---

### Camunda 8 with Spring Boot

**Maven dependency:**

```xml
<dependency>
    <groupId>io.camunda.spring</groupId>
    <artifactId>spring-boot-starter-camunda</artifactId>
    <version>8.5.0</version>
</dependency>
```

**application.yaml:**

```yaml
zeebe:
  client:
    broker:
      gateway-address: localhost:26500
    security:
      plaintext: true   # set to false in production with TLS
    worker:
      default-type: ""
      threads: 1
```

**Starting a process instance:**

```java
@Service
public class OrderService {

    @Autowired
    private ZeebeClient zeebeClient;

    public void startOrderProcess(String orderId) {
        ProcessInstanceEvent event = zeebeClient
            .newCreateInstanceCommand()
            .bpmnProcessId("order-process")
            .latestVersion()
            .variables(Map.of(
                "orderId", orderId,
                "amount", 150.00
            ))
            .send()
            .join();

        System.out.println("Started process instance: " + event.getProcessInstanceKey());
    }
}
```

**Job Worker (service task handler):**

```java
@Component
public class PaymentWorker {

    @Autowired
    private PaymentService paymentService;

    @JobWorker(type = "payment", autoComplete = false)
    public void handlePayment(final JobClient client, final ActivatedJob job) {
        try {
            Map<String, Object> vars = job.getVariablesAsMap();
            String orderId = (String) vars.get("orderId");
            Double amount = (Double) vars.get("amount");

            String status = paymentService.processPayment(orderId, amount);

            // Complete the job with output variables
            client.newCompleteCommand(job.getKey())
                .variable("paymentStatus", status)
                .send()
                .join();

        } catch (Exception e) {
            // Throw BPMN error for process-level error handling
            client.newThrowErrorCommand(job.getKey())
                .errorCode("PAYMENT_FAILED")
                .errorMessage(e.getMessage())
                .send()
                .join();
        }
    }
}
```

In your BPMN:
```xml
<serviceTask id="paymentTask">
  <extensionElements>
    <zeebe:taskDefinition type="payment" />
  </extensionElements>
</serviceTask>
```

**Handling user tasks (Camunda 8):**

```java
@Component
public class ApprovalWorker {

    // User tasks in Camunda 8 are handled via Tasklist UI
    // or programmatically via the Tasklist API / job workers

    @JobWorker(type = "io.camunda.zeebe:userTask")
    public void handleUserTask(final JobClient client, final ActivatedJob job) {
        // Custom user task handling logic
        Map<String, Object> vars = job.getVariablesAsMap();
        // ... process the task
        client.newCompleteCommand(job.getKey()).send().join();
    }
}
```

---

## Comparison Table

| Aspect | Camunda 7 | Camunda 8 |
|---|---|---|
| **Maven starter** | `camunda-bpm-spring-boot-starter` | `spring-boot-starter-camunda` |
| **Engine location** | Embedded in Spring Boot JVM | External Zeebe broker |
| **Service task** | `JavaDelegate` (synchronous bean) | `@JobWorker` (async job polling) |
| **Process start** | `RuntimeService.startProcessInstanceByKey()` | `ZeebeClient.newCreateInstanceCommand()` |
| **Variables** | `DelegateExecution.getVariable()` | `ActivatedJob.getVariablesAsMap()` |
| **Complete task** | Auto — method returns | `client.newCompleteCommand().send()` |
| **Error handling** | Throw Java exception | `client.newThrowErrorCommand()` |
| **DB required in app** | Yes — PostgreSQL/MySQL | No — broker owns state |
| **Transaction scope** | Shared with your DB transaction | Separate — eventual consistency |
| **BPMN deployment** | Classpath at startup or REST API | Zeebe API or Modeler |
| **Monitoring** | Cockpit (bundled in app) | Operate (separate service) |
| **Scaling** | Scale the whole app | Scale workers independently |
| **Connection protocol** | JDBC (to DB) | gRPC (to Zeebe broker) |

---

## When to Use Which

**Choose Camunda 7 if:**
- You have an existing Camunda 7 application (migration is non-trivial)
- You need strong transactional consistency between process state and your DB
- You prefer a simpler, single-application deployment model
- Your team is already familiar with the Camunda 7 ecosystem
- You don't need horizontal worker scaling

**Choose Camunda 8 if:**
- You are starting a new project
- You need cloud-native, horizontally scalable architecture
- You are building microservices where workers scale independently
- You want managed SaaS (Camunda Cloud) with no infrastructure overhead
- You need high throughput process execution (Zeebe is optimised for this)

---

> **Migration note:** Camunda 7 and Camunda 8 are **not backward compatible**. `JavaDelegate` does not work in Camunda 8 — you must rewrite service tasks as `@JobWorker`. BPMN files may need adjustments as Camunda 8 uses Zeebe-specific extension elements.

---

## How Camunda Resolves Delegates and Job Workers

This is the most important thing to understand — **Camunda 7 and Camunda 8 use completely different resolution models**. Camunda 7 resolves delegates by name inside the same JVM. Camunda 8 resolves workers by job type via a polling mechanism — no host URL, no direct call.

---

### Camunda 7 — How the Engine Finds a Java Delegate

#### The core idea: same JVM, Spring bean lookup

In Camunda 7, the engine is embedded **inside your Spring Boot application**. When a process instance reaches a service task, the engine does NOT make any HTTP call or network request. It simply does a **Spring bean lookup** inside the same running JVM.

```
BPMN file (service task definition)
         │
         │  camunda:delegateExpression="${paymentDelegate}"
         │        OR
         │  camunda:class="com.example.PaymentDelegate"
         ▼
Camunda Engine (running inside same JVM)
         │
         │  Resolves the Spring bean from ApplicationContext
         │  Calls delegate.execute(execution) synchronously
         ▼
PaymentDelegate.java  ← Spring bean, same JVM, same thread
```

#### Three ways to wire a delegate in the BPMN

**Method 1: Delegate Expression (recommended)**

The BPMN references a Spring bean by its bean name using Spring EL:

```xml
<serviceTask id="paymentTask" name="Process Payment"
             camunda:delegateExpression="${paymentDelegate}">
```

Your Java class must be a Spring bean with a matching name:

```java
@Component("paymentDelegate")   // ← bean name MUST match ${paymentDelegate}
public class PaymentDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // engine calls this method directly, in the same thread
    }
}
```

The engine uses Spring's `ApplicationContext.getBean("paymentDelegate")` internally. **No host, no URL, no network — just a bean lookup.**

---

**Method 2: Fully Qualified Class Name**

The BPMN references the full class path directly:

```xml
<serviceTask id="paymentTask"
             camunda:class="com.example.delegates.PaymentDelegate">
```

The engine instantiates the class using reflection. Note: the class is **not** a Spring bean here — it is instantiated fresh each time, so `@Autowired` fields do NOT work unless you use `@SpringBean` manually. This method is less preferred.

---

**Method 3: Expression (for simple logic)**

```xml
<serviceTask id="paymentTask"
             camunda:expression="${paymentService.processPayment(execution)}">
```

Directly calls a method on a Spring bean. Useful for one-liners without a dedicated delegate class.

---

#### How the engine resolves the bean — internal flow

```
1. Process engine reaches service task node in BPMN
2. Reads the attribute: camunda:delegateExpression="${paymentDelegate}"
3. Evaluates the Spring EL expression using the Spring ApplicationContext
4. Gets the Spring bean: PaymentDelegate instance
5. Calls: delegate.execute(delegateExecution)  ← synchronous, same thread
6. Delegate runs, sets variables, returns
7. Engine continues to next BPMN node
```

Everything happens **in-process**. The engine has no concept of "where is the delegate hosted" because it IS hosted in the same application.

---

#### What happens if the bean name doesn't match?

```
org.camunda.bpm.engine.ProcessEngineException:
  Unknown property used in expression: ${paymentDelegate}
```

The engine throws at runtime when the service task is reached — not at deployment time. Always verify bean names match exactly (case-sensitive).

---

### Camunda 8 — How the Broker Finds a Job Worker

#### The core idea: job type matching via polling

In Camunda 8, the Zeebe broker and your Spring Boot workers run in **separate processes** — often on separate machines. The broker does NOT call your application. Instead, your application **polls the broker** for jobs of a specific type.

```
BPMN file deployed to broker
         │
         │  <zeebe:taskDefinition type="payment" />
         ▼
Zeebe Broker (external process)
         │
         │  Creates a job of type "payment" when task is reached
         │  Waits for a worker to claim it
         ▼
         ▲  (Worker polls broker via gRPC: "give me jobs of type payment")
         │
Spring Boot Worker App (separate process, any host)
         │
         │  @JobWorker(type = "payment")
         ▼
handlePayment() method is called with the activated job
```

#### The job type is the only contract

The BPMN declares a `type` string. The worker declares the same `type` string. **That string is the entire connection.** There is no IP address, no hostname, no bean name — just a string that both sides agree on.

```xml
<!-- In BPMN — deployed to Zeebe broker -->
<serviceTask id="paymentTask" name="Process Payment">
  <extensionElements>
    <zeebe:taskDefinition type="payment" />   <!-- ← this string is the contract -->
  </extensionElements>
</serviceTask>
```

```java
// In your Spring Boot worker app — connects to broker via gRPC
@JobWorker(type = "payment")   // ← must match the BPMN type string exactly
public void handlePayment(final JobClient client, final ActivatedJob job) {
    // broker activates this job and sends it to this worker
}
```

#### How the worker connects to the broker

The worker app specifies the broker's address in `application.yaml`. This is the **only** host/URL configuration needed — and it points from your worker TO the broker, not the other way around:

```yaml
# application.yaml in your Spring Boot worker app
zeebe:
  client:
    broker:
      gateway-address: localhost:26500    # ← broker host:port (gRPC)
    security:
      plaintext: true                    # false in production (TLS)
```

For Camunda SaaS (Camunda Cloud):

```yaml
zeebe:
  client:
    cloud:
      cluster-id: your-cluster-id
      client-id: your-client-id
      client-secret: your-client-secret
      region: bru-2
```

#### The polling flow — step by step internally

```
1. Spring Boot app starts
2. spring-zeebe detects @JobWorker(type="payment") on handlePayment()
3. Opens a long-lived gRPC stream to broker at gateway-address:26500
4. Sends: ActivateJobsRequest { type: "payment", maxJobsToActivate: 32 }
5. Broker holds the stream open (long poll)

--- Meanwhile, a process instance reaches the payment service task ---

6. Broker creates a job: { key: 12345, type: "payment", variables: {...} }
7. Broker pushes the job to the waiting worker stream
8. Worker receives the job, calls handlePayment(client, job)
9. Worker calls: client.newCompleteCommand(12345).send()
10. Broker marks the job complete, process continues
```

The broker never stores or knows the worker's host/IP. The worker comes to the broker — not the other way around.

#### What if no worker is listening for that job type?

The job stays in the broker's job queue in an `ACTIVATABLE` state indefinitely. After a configurable timeout (job timeout), the job is retried. If no worker ever polls for that type, the job keeps retrying until it exhausts retries and goes to an `INCIDENT` state — visible in Operate.

```yaml
# Control retry and timeout behaviour
zeebe:
  client:
    worker:
      default-job-timeout: 5m       # how long a worker has to complete the job
      default-job-poll-interval: 100ms
      default-max-jobs-active: 32   # max jobs pulled at once per worker
```

---

#### Polling behaviour in detail — when, how often, how long

Understanding the exact polling lifecycle is critical for tuning throughput and avoiding incidents.

##### The long-poll cycle

Zeebe workers do **not** poll on a fixed timer like a cron job. They use **gRPC long polling** — the request stays open on the broker side until either a job arrives or the request timeout expires.

```
Worker                                    Zeebe Broker
  │                                           │
  │── ActivateJobsRequest ──────────────────► │
  │   { type: "payment",                      │
  │     maxJobsToActivate: 32,                │ ← broker holds request open
  │     requestTimeout: 10s,                  │   waiting for jobs up to 10s
  │     timeout: 300000ms }                   │
  │                                           │
  │   (no jobs available yet)                 │
  │                                           │
  │   (job arrives at t=3s)                   │
  │◄── ActivateJobsResponse ────────────────  │ ← broker responds immediately
  │    { jobs: [{ key: 123, ... }] }          │   when a job appears
  │                                           │
  │── handlePayment(job) → processes it       │
  │                                           │
  │── CompleteJobRequest ───────────────────► │
  │                                           │
  │── ActivateJobsRequest ──────────────────► │ ← immediately polls again
  │   (next cycle starts right away)          │
```

##### What happens when no job arrives within the request timeout

```
Worker                                    Zeebe Broker
  │                                           │
  │── ActivateJobsRequest ──────────────────► │
  │   { requestTimeout: 10s }                 │ ← broker waits...
  │                                           │
  │   (no jobs for 10 seconds)                │
  │                                           │
  │◄── ActivateJobsResponse (empty) ────────  │ ← timeout: broker returns empty
  │    { jobs: [] }                           │
  │                                           │
  │   (waits pollInterval: 100ms)             │ ← worker waits briefly (default 100ms)
  │                                           │
  │── ActivateJobsRequest ──────────────────► │ ← polls again
```

The `pollInterval` (100ms by default) is only the **wait between cycles when no jobs were received**. When jobs are actively arriving, the worker polls immediately after completing the previous batch.

##### All the timing knobs and what they control

```yaml
zeebe:
  client:
    worker:
      default-max-jobs-active: 32       # max jobs activated per poll (default: 32)
                                        # broker sends at most this many per request

      default-job-timeout: 300000       # lock duration in ms (default: 5 minutes)
                                        # how long the broker gives the worker to
                                        # complete/fail the job before it's retried
                                        # by another worker

      default-job-poll-interval: 100    # ms to wait before re-polling when last
                                        # response had no jobs (default: 100ms)
                                        # keeps idle workers from hammering the broker

      default-request-timeout: 10000    # ms the broker holds the request open waiting
                                        # for a job (long poll timeout, default: 10s)
                                        # higher = fewer round-trips when jobs are sparse
```

##### Per-worker overrides (when one worker needs different settings)

```java
@JobWorker(
    type = "payment",
    timeout = 60000,          // override lock duration to 60s for this worker only
    maxJobsActive = 10,       // pull max 10 jobs at a time
    pollInterval = 500,       // wait 500ms between idle cycles
    requestTimeout = 20000,   // long poll for 20s
    autoComplete = true
)
public Map<String, Object> handlePayment(final ActivatedJob job) {
    // ...
    return Map.of("paymentStatus", "SUCCESS");
}
```

##### What "job timeout" (lock duration) means in practice

When a worker activates a job, the broker starts a countdown equal to `job-timeout`. If the worker does not complete or fail the job within that window:

```
t=0s    Worker activates job (lock starts)
t=?     Worker processes the job (ideally fast)
t=300s  Job timeout expires — broker marks job as available again
        → another worker instance picks it up (duplicate execution risk!)
```

Set `job-timeout` to comfortably longer than your worst-case processing time. If your handler calls a slow external API that can take up to 30s, set at least 60s.

##### Timeline of a full polling lifecycle

```
App start
    │
    ├─ Worker thread opens gRPC connection to broker:26500
    │
    ├─ [Poll 1] ActivateJobsRequest sent (requestTimeout=10s)
    │       ├─ No jobs → 10s passes → empty response
    │       └─ Wait 100ms (pollInterval)
    │
    ├─ [Poll 2] ActivateJobsRequest sent
    │       ├─ Job arrives at t=2s → broker pushes it immediately
    │       ├─ Worker calls handlePayment()
    │       ├─ Worker calls completeCommand
    │       └─ Next poll starts immediately
    │
    ├─ [Poll 3] ActivateJobsRequest sent
    │       ├─ 3 jobs returned at once (maxJobsToActivate=32)
    │       ├─ Worker processes them in parallel (thread pool)
    │       └─ As each completes, next poll sent if below max active threshold
    │
    └─ (continues indefinitely until app shuts down)
```

##### Camunda 7 external task polling timing (for comparison)

In Camunda 7, external task clients also have a polling cycle but via HTTP (not gRPC):

```java
ExternalTaskClient client = ExternalTaskClient.create()
    .baseUrl("http://engine:8080/engine-rest")
    .asyncResponseTimeout(10000)   // ← long poll: engine holds request 10s (in ms)
    .lockDuration(30000)           // ← lock duration: 30s to complete the task
    .build();

// The underlying poll interval between cycles is ~300ms by default
// Configure it explicitly:
ExternalTaskClient client = ExternalTaskClient.create()
    .baseUrl("http://engine:8080/engine-rest")
    .asyncResponseTimeout(10000)
    .lockDuration(30000)
    .build();
```

| Timing knob | Camunda 7 External Task | Camunda 8 Job Worker |
|---|---|---|
| **Long poll duration** | `asyncResponseTimeout` (ms) | `requestTimeout` (ms, default 10s) |
| **Lock duration** | `lockDuration` (ms) | `default-job-timeout` (ms, default 5min) |
| **Idle wait between polls** | ~300ms (internal default) | `default-job-poll-interval` (default 100ms) |
| **Max jobs per request** | `maxTasks` in fetchAndLock body | `default-max-jobs-active` (default 32) |
| **Protocol** | HTTP REST (`/external-task/fetchAndLock`) | gRPC (`ActivateJobsRequest`) |

---

### No Host URL Needed — Why the Models Differ

This is the key insight — **neither model requires you to configure where the delegate or worker is hosted**:

| | Camunda 7 | Camunda 8 |
|---|---|---|
| **How engine/broker finds handler** | Spring bean lookup in same JVM | Worker polls broker with a job type string |
| **Who initiates the connection** | Engine calls delegate directly | Worker opens gRPC stream TO broker |
| **Host/URL configuration** | None — same process | Worker configures broker address only |
| **What the BPMN stores** | Bean name or class name | Job type string |
| **Resolution time** | At service task execution (runtime) | At job activation (runtime, after worker polls) |
| **What breaks if handler missing** | `ProcessEngineException` immediately | Job sits in queue → incident after retries |

In Camunda 7, **no URL is needed** because the delegate is in the same JVM — there is nothing to connect to.

In Camunda 8, **the broker URL is needed by the worker** (not the BPMN). The BPMN only stores the job type. The broker doesn't know or care where workers are running.

---

### Who Triggers the gRPC Request — JVM or Something Else?

**Short answer: the JVM triggers it.** Specifically, your Spring Boot application triggers the gRPC call on startup — not Camunda, not the broker, not any external scheduler.

#### What happens inside Spring Boot when it sees `@JobWorker`

```
Spring Boot app starts
        │
        ▼
ZeebeClientAutoConfiguration runs
        │  reads: zeebe.client.broker.gateway-address=localhost:26500
        │  creates: ZeebeClient bean (manages gRPC channel)
        ▼
JobWorkerManager (spring-zeebe) scans ApplicationContext
        │  finds: @JobWorker(type="payment") on PaymentWorker.handlePayment()
        │  finds: @JobWorker(type="shipping") on ShippingWorker.handleShipping()
        ▼
For each @JobWorker found:
        │  calls: zeebeClient.newWorker()
        │               .jobType("payment")
        │               .handler(this::handlePayment)
        │               .timeout(Duration.ofMinutes(5))
        │               .maxJobsActive(32)
        │               .open()           ← opens gRPC stream
        ▼
gRPC stream to broker:26500 is now live — worker is polling
```

The Zeebe broker never reaches into your app. The entire flow is **outbound from your JVM**.

#### The actual gRPC request spring-zeebe sends

Internally, spring-zeebe sends an `ActivateJobsRequest` defined in the Zeebe protobuf schema:

```protobuf
// What gets sent over the wire for @JobWorker(type="payment")
ActivateJobsRequest {
    type:              "payment"        // job type from @JobWorker annotation
    worker:            "default"        // worker identity (customisable)
    timeout:           300000           // lock duration ms (default 5 min)
    maxJobsToActivate: 32               // max jobs per poll
    requestTimeout:    10000            // long poll duration ms (default 10s)
    fetchVariable:     []               // empty = fetch all variables
}
```

#### Equivalent curl (Camunda 8.6+ REST API)

The gRPC call has no direct curl equivalent since gRPC is binary over HTTP/2. But from Camunda 8.6 onwards, the REST API exposes the same operation:

```bash
# What spring-zeebe does automatically — poll for jobs of type "payment"
curl -X POST http://localhost:8080/v1/jobs/activation \
  -H "Content-Type: application/json" \
  -d '{
    "type": "payment",
    "worker": "my-spring-worker",
    "timeout": 300000,
    "maxJobsToActivate": 32,
    "requestTimeout": 10000
  }'

# Response when a job is available:
# {
#   "jobs": [{
#     "key": 12345,
#     "type": "payment",
#     "processInstanceKey": 99,
#     "variables": { "orderId": "ORD-001", "amount": 150.0 },
#     "deadline": 1714900000000
#   }]
# }
```

```bash
# What spring-zeebe calls after your @JobWorker method returns — complete the job
curl -X POST http://localhost:8080/v1/jobs/12345/completion \
  -H "Content-Type: application/json" \
  -d '{
    "variables": { "paymentStatus": "SUCCESS" }
  }'

# Fail the job (e.g. exception in your handler)
curl -X POST http://localhost:8080/v1/jobs/12345/failure \
  -H "Content-Type: application/json" \
  -d '{
    "retries": 2,
    "errorMessage": "Payment gateway timeout"
  }'

# Throw a BPMN error (triggers boundary error event in diagram)
curl -X POST http://localhost:8080/v1/jobs/12345/error \
  -H "Content-Type: application/json" \
  -d '{
    "errorCode": "PAYMENT_FAILED",
    "errorMessage": "Insufficient funds"
  }'
```

#### Equivalent for Camunda 7 external tasks (HTTP, no gRPC)

```bash
# Poll + lock external tasks (what the ExternalTaskClient does)
curl -X POST http://localhost:8080/engine-rest/external-task/fetchAndLock \
  -H "Content-Type: application/json" \
  -d '{
    "workerId": "my-worker-1",
    "maxTasks": 10,
    "asyncResponseTimeout": 10000,
    "topics": [{
      "topicName": "payment-processing",
      "lockDuration": 30000
    }]
  }'

# Complete an external task
curl -X POST http://localhost:8080/engine-rest/external-task/{id}/complete \
  -H "Content-Type: application/json" \
  -d '{
    "workerId": "my-worker-1",
    "variables": {
      "paymentStatus": { "value": "SUCCESS", "type": "String" }
    }
  }'
```

---

### Multiple Job Workers in One Spring App

#### Scenario 1: Different types (standard pattern)

```java
@Component
public class WorkerConfig {

    @JobWorker(type = "payment")
    public void handlePayment(ActivatedJob job) { ... }

    @JobWorker(type = "shipping")
    public void handleShipping(ActivatedJob job) { ... }
}
```

```
Spring Boot App
    │
    ├─ gRPC stream A ──► broker:26500  (subscribed to type="payment")
    └─ gRPC stream B ──► broker:26500  (subscribed to type="shipping")

Each stream handles its own job queue — completely independent.
```

Both workers run in parallel inside the same JVM. They do not affect each other. This is the normal and recommended pattern when one app owns multiple service task types.

#### Scenario 2: Same type, same app (local competing consumers)

```java
@JobWorker(type = "payment", name = "payment-worker-1", maxJobsActive = 16)
public void handlePaymentA(ActivatedJob job) { ... }

@JobWorker(type = "payment", name = "payment-worker-2", maxJobsActive = 16)
public void handlePaymentB(ActivatedJob job) { ... }
```

```
Spring Boot App
    │
    ├─ gRPC stream A ──► broker:26500  (name="payment-worker-1", maxJobs=16)
    └─ gRPC stream B ──► broker:26500  (name="payment-worker-2", maxJobs=16)

Broker sees two separate workers for the same type.
Distributes jobs between them → effective throughput = 32 concurrent jobs.
Each job goes to exactly ONE stream — no duplicate execution.
```

Practically, you would not do this inside a single app. Instead raise `maxJobsActive` on one worker. Multiple same-type workers make sense across separate deployed instances (horizontal scaling).

#### Scenario 3: Same type across multiple app instances (horizontal scaling)

```
Instance 1 (Pod 1): @JobWorker(type="payment") ──► broker:26500
Instance 2 (Pod 2): @JobWorker(type="payment") ──► broker:26500
Instance 3 (Pod 3): @JobWorker(type="payment") ──► broker:26500
```

```
Broker receives job key=123 (type="payment")
        │
        ├─ Pod 1 has capacity → job sent to Pod 1 only
        │
        └─ Pod 2 and Pod 3 do NOT receive job 123
           (competing consumer — exactly-once delivery per job)
```

This is the primary scaling mechanism in Camunda 8. Add more pods → more throughput. The broker handles distribution automatically with no configuration change needed.

#### What the broker sees for each worker

Each `@JobWorker` creates a named subscription. In Operate (monitoring UI) you can see:

| Worker name | Type | Status | Jobs active |
|---|---|---|---|
| `payment-worker-1` | `payment` | `ACTIVE` | 12/32 |
| `payment-worker-2` | `payment` | `ACTIVE` | 8/32 |
| `shipping-worker` | `shipping` | `IDLE` | 0/32 |

The `name` field in `@JobWorker(name = "...")` sets this identifier. Default is `"default"`.

#### Key broker guarantee regardless of worker count

> **One job → exactly one worker activation at a time.**

The broker's lock (`job-timeout`) enforces this. Even if 10 workers are all polling for the same type simultaneously, each job is sent to only one of them. If that worker crashes before completing, the lock expires and the job becomes available again for the next worker to pick up.

---

### What Happens Step-by-Step When a Service Task Runs

#### Camunda 7 — Full flow

```
Step 1: BPMN deployed to engine (at app startup)
        └─ engine reads: camunda:delegateExpression="${paymentDelegate}"
        └─ stores it in DB (ACT_RE_PROCDEF table)

Step 2: Process instance started
        └─ runtimeService.startProcessInstanceByKey("order-process")

Step 3: Engine executes BPMN token, reaches service task
        └─ reads delegateExpression from DB: "${paymentDelegate}"

Step 4: Engine evaluates Spring EL expression
        └─ applicationContext.getBean("paymentDelegate")
        └─ returns PaymentDelegate instance

Step 5: Engine calls synchronously
        └─ paymentDelegate.execute(delegateExecution)
        └─ runs in SAME thread as engine

Step 6: Delegate sets output variables
        └─ execution.setVariable("paymentStatus", "SUCCESS")

Step 7: Engine continues to next BPMN node
        └─ all in same DB transaction
```

#### Camunda 8 — Full flow

```
Step 1: BPMN deployed to Zeebe broker (separately, via API or Modeler)
        └─ broker stores: serviceTask type="payment"

Step 2: Worker app starts, opens gRPC stream to broker
        └─ "I handle jobs of type: payment"

Step 3: Process instance started
        └─ zeebeClient.newCreateInstanceCommand().bpmnProcessId("order-process")

Step 4: Broker executes BPMN, reaches service task
        └─ creates job: { key: 12345, type: "payment", variables: {...} }
        └─ job status: ACTIVATABLE

Step 5: Worker's long-poll stream receives the job
        └─ broker pushes job to whichever worker is listening
        └─ job status: ACTIVATED (locked to this worker)

Step 6: Worker calls handlePayment(client, job)
        └─ runs in worker's thread pool (separate from broker)

Step 7: Worker completes the job
        └─ client.newCompleteCommand(12345).variable("paymentStatus","SUCCESS").send()

Step 8: Broker marks job complete, process continues
        └─ different transaction from your application DB
```

---

### Common Mistakes and How to Avoid Them

#### Camunda 7 mistakes

**Mistake 1: Bean name mismatch**

```xml
<!-- BPMN says -->
<serviceTask camunda:delegateExpression="${PaymentDelegate}" />
                                          ↑ capital P

<!-- Java says -->
@Component("paymentDelegate")   ← lowercase p — MISMATCH
```

Fix: bean names are case-sensitive. Use `${paymentDelegate}` (lowercase) to match `@Component("paymentDelegate")`.

---

**Mistake 2: Using `camunda:class` and expecting Spring injection**

```java
// camunda:class instantiates the class directly — NOT via Spring
public class PaymentDelegate implements JavaDelegate {
    @Autowired
    private PaymentService paymentService;  // ← will be NULL at runtime!
}
```

Fix: use `camunda:delegateExpression="${paymentDelegate}"` so Spring manages the bean and `@Autowired` works.

---

**Mistake 3: Throwing checked exceptions without declaring them**

```java
@Override
public void execute(DelegateExecution execution) {
    throw new RuntimeException("Payment failed");
    // This causes an incident in Cockpit — process stops
}
```

Fix: throw a `BpmnError` for business errors you want the BPMN to handle:

```java
throw new BpmnError("PAYMENT_FAILED", "Insufficient funds");
// This triggers a boundary error event in BPMN — process continues on error path
```

---

#### Camunda 8 mistakes

**Mistake 1: Job type mismatch between BPMN and worker**

```xml
<!-- BPMN says -->
<zeebe:taskDefinition type="processPayment" />
```

```java
// Worker says
@JobWorker(type = "payment")  // ← MISMATCH — broker never sends jobs to this worker
```

Fix: the `type` string must be identical in both places. Job stays in `ACTIVATABLE` state forever → incident.

---

**Mistake 2: Wrong broker address**

```yaml
zeebe:
  client:
    broker:
      gateway-address: localhost:26500  # ← if broker is elsewhere, jobs never arrive
```

Fix: set the correct host and port for your Zeebe broker. In Docker Compose, use the service name (e.g., `zeebe:26500`).

---

**Mistake 3: Not completing the job**

```java
@JobWorker(type = "payment", autoComplete = false)
public void handlePayment(final JobClient client, final ActivatedJob job) {
    // forgot to call client.newCompleteCommand()!
    doSomeWork();
    // job times out → broker retries → duplicate execution!
}
```

Fix: always call `newCompleteCommand()` or `newThrowErrorCommand()`. Or use `autoComplete = true` (default) if you have no complex error handling — spring-zeebe will auto-complete the job when your method returns normally.

---

**Mistake 4: Multiple workers with the same type — expected vs actual behaviour**

```
Worker App A: @JobWorker(type = "payment")  ← instance 1
Worker App B: @JobWorker(type = "payment")  ← instance 2
```

This is actually correct and intended for scaling! The broker distributes jobs across all workers of the same type in a round-robin fashion. Each job is sent to exactly one worker (competing consumers pattern). This is how you scale Camunda 8 workers horizontally.

---

#### Summary diagram — resolution at a glance

```
CAMUNDA 7
─────────────────────────────────────────────────────────
BPMN:    camunda:delegateExpression="${paymentDelegate}"
                                          │
                                          ▼ Spring EL lookup (same JVM)
CODE:    @Component("paymentDelegate")    ← bean name must match


CAMUNDA 8
─────────────────────────────────────────────────────────
BPMN:    <zeebe:taskDefinition type="payment" />
                                    │
                    job type string │ is the contract
                                    ▼ worker polls broker for this type
CODE:    @JobWorker(type = "payment")     ← type must match
CONFIG:  gateway-address: broker:26500   ← only the broker URL, set in worker app
```

---

## Third-Party Integrations — Triggering Service Tasks Externally

Both engines support external systems (non-Java, non-Spring) completing service tasks. The patterns differ significantly.

---

### Camunda 7 — External Task Pattern

When a service task must be handled by an external system (Python script, Node.js service, SAP, etc.), Camunda 7 provides the **External Task pattern**. Instead of embedding a `JavaDelegate`, the task is published as an external job that any system can fetch and complete via REST.

#### BPMN configuration

```xml
<!-- Mark the service task as external with a topic name -->
<serviceTask id="paymentTask" name="Process Payment"
             camunda:type="external"
             camunda:topic="payment-processing">
```

The `topic` is the equivalent of the job type in Camunda 8 — it is the string that external workers subscribe to.

#### How the external task flow works

```
Step 1: Process instance reaches the service task
        └─ engine creates an external task with topic "payment-processing"
        └─ task sits in ACT_RU_EXT_TASK table — waiting

Step 2: External system (Python / Node.js / REST client) polls:
        GET /engine-rest/external-task?topicName=payment-processing&maxResults=10

Step 3: External system locks the task for itself:
        POST /engine-rest/external-task/fetchAndLock
        {
          "workerId": "python-worker-1",
          "maxTasks": 5,
          "topics": [{ "topicName": "payment-processing", "lockDuration": 30000 }]
        }

Step 4: External system processes the work

Step 5: External system completes the task:
        POST /engine-rest/external-task/{id}/complete
        {
          "workerId": "python-worker-1",
          "variables": {
            "paymentStatus": { "value": "SUCCESS", "type": "String" }
          }
        }

Step 6: Engine continues to next BPMN node
```

#### Java External Task client (alternative to REST polling)

Camunda provides a lightweight Java client specifically for external tasks (not the full engine):

```java
// This runs in a SEPARATE application — not the one with the engine embedded
ExternalTaskClient client = ExternalTaskClient.create()
    .baseUrl("http://camunda-engine-host:8080/engine-rest")  // ← engine REST URL
    .asyncResponseTimeout(10000)
    .build();

client.subscribe("payment-processing")
    .lockDuration(30000)
    .handler((externalTask, externalTaskService) -> {
        String orderId = externalTask.getVariable("orderId");
        // do the work...
        externalTaskService.complete(externalTask,
            Map.of("paymentStatus", "SUCCESS"));
    })
    .open();
```

#### Node.js / Python polling via REST

```python
import requests

BASE = "http://camunda-host:8080/engine-rest"
WORKER_ID = "python-worker-1"

# 1. Fetch and lock external tasks
response = requests.post(f"{BASE}/external-task/fetchAndLock", json={
    "workerId": WORKER_ID,
    "maxTasks": 5,
    "topics": [{"topicName": "payment-processing", "lockDuration": 30000}]
})
tasks = response.json()

for task in tasks:
    task_id = task["id"]
    order_id = task["variables"]["orderId"]["value"]

    # 2. Do the work
    status = call_payment_api(order_id)

    # 3. Complete the task
    requests.post(f"{BASE}/external-task/{task_id}/complete", json={
        "workerId": WORKER_ID,
        "variables": {"paymentStatus": {"value": status, "type": "String"}}
    })
```

#### Key point: the engine REST URL is the only connection needed

Third-party systems connect to the **Camunda 7 REST API** (`/engine-rest`). The engine must be reachable over HTTP. There is no gRPC, no message broker — just HTTP polling against the engine's external task endpoint.

```
Third-party system                     Camunda 7 app
─────────────────                      ─────────────────────────────────
Python / Node.js  ──── HTTP REST ────► Spring Boot + Camunda Engine
                        /engine-rest    (engine exposes REST externally)
```

---

### Camunda 8 — Any Language, Any Service

In Camunda 8, there is **no special "external task" mode** — the normal job worker mechanism already works for any language. Because workers connect via gRPC and poll for jobs, any system with a Zeebe client library becomes a first-class worker.

#### The BPMN stays the same

```xml
<serviceTask id="paymentTask" name="Process Payment">
  <extensionElements>
    <zeebe:taskDefinition type="payment" />
  </extensionElements>
</serviceTask>
```

There is no difference between a "Java" service task and an "external" service task in Camunda 8 — the job type is the only thing that matters.

#### Node.js job worker

```javascript
import { ZBClient } from 'zeebe-node'

const zbc = new ZBClient({
    gatewayAddress: 'localhost:26500'  // ← same gRPC address
})

zbc.createWorker({
    taskType: 'payment',              // ← matches BPMN type="payment"
    taskHandler: async (job) => {
        const { orderId } = job.variables
        const status = await callPaymentApi(orderId)

        await job.complete({ paymentStatus: status })
    }
})
```

#### Python job worker

```python
from pyzeebe import ZeebeWorker, create_insecure_channel

channel = create_insecure_channel(hostname="localhost", port=26500)
worker = ZeebeWorker(channel)

@worker.task(task_type="payment")   # ← matches BPMN type="payment"
async def handle_payment(orderId: str):
    status = await call_payment_api(orderId)
    return {"paymentStatus": status}

worker.run()
```

#### REST-based completion (Camunda 8.6+)

From Camunda 8.6, a REST API is available alongside gRPC. Third-party systems that cannot use gRPC can use REST:

```bash
# Activate jobs (poll)
POST http://zeebe-gateway:8080/v1/jobs/activation
{
  "type": "payment",
  "maxJobsToActivate": 5,
  "worker": "rest-worker-1",
  "timeout": 30000
}

# Complete a job
POST http://zeebe-gateway:8080/v1/jobs/{key}/completion
{
  "variables": { "paymentStatus": "SUCCESS" }
}
```

#### How the connection works in Camunda 8 for third-party systems

```
Third-party system                     Camunda 8 Platform
──────────────────                     ────────────────────────────────
Node.js worker    ──── gRPC ────────► Zeebe Gateway (port 26500)
Python worker     ──── gRPC ────────► Zeebe Gateway (port 26500)
REST client       ──── HTTP ─────── ► Zeebe REST API (port 8080)  [8.6+]

All workers poll the same broker. Job type string is the only routing key.
Broker distributes jobs — workers don't know about each other.
```

---

### Side-by-Side: Third-Party Integration Patterns

| Aspect | Camunda 7 External Task | Camunda 8 Job Worker |
|---|---|---|
| **BPMN marker** | `camunda:type="external"` + `camunda:topic` | `zeebe:taskDefinition type="..."` |
| **Routing key** | Topic name string | Job type string |
| **Protocol** | HTTP REST polling (`/engine-rest`) | gRPC long-poll (port 26500) or REST (8.6+) |
| **Connection direction** | Worker → Engine REST API | Worker → Zeebe broker gRPC |
| **Java client** | `camunda-external-task-client` | Zeebe Java client (`spring-zeebe`) |
| **Node.js client** | `camunda-external-task-client-js` | `zeebe-node` |
| **Python client** | REST polling (requests library) | `pyzeebe` |
| **No worker listening?** | Task sits in `ACT_RU_EXT_TASK` table | Job sits in broker queue → incident |
| **Locking** | Explicit lock with `lockDuration` | Automatic — broker locks on activation |
| **Multiple workers** | Competing consumers — round-robin | Competing consumers — round-robin |
| **Scaling** | Spin up more external workers | Spin up more worker instances |

---

### End-to-End Connection Summary

#### Camunda 7

```
┌─────────────────────────────────────────────────────────────────┐
│                      Your BPMN File                             │
│                                                                 │
│  Java service task:    camunda:delegateExpression="${myBean}"   │
│  External service task: camunda:type="external" topic="pay"    │
└──────────────────────────────┬──────────────────────────────────┘
                               │
              ┌────────────────▼────────────────┐
              │     Camunda 7 Process Engine     │
              │     (inside your Spring Boot)    │
              └──────┬───────────────────┬───────┘
                     │                   │
          ┌──────────▼──────┐   ┌────────▼────────────────┐
          │  JavaDelegate   │   │  External Task Queue     │
          │  (same JVM,     │   │  (ACT_RU_EXT_TASK table) │
          │   Spring bean)  │   └────────┬────────────────-┘
          └─────────────────┘            │ HTTP REST polling
                                ┌────────▼────────────────┐
                                │  Any external system     │
                                │  (Python, Node.js, SAP) │
                                │  hits /engine-rest API   │
                                └─────────────────────────┘
```

#### Camunda 8

```
┌─────────────────────────────────────────────────────────────────┐
│                      Your BPMN File                             │
│                                                                 │
│  All service tasks:  <zeebe:taskDefinition type="payment" />   │
│                      (no distinction between internal/external) │
└──────────────────────────────┬──────────────────────────────────┘
                               │ deployed to broker
              ┌────────────────▼────────────────┐
              │         Zeebe Broker             │
              │  (external, manages job queue)   │
              │  port 26500 (gRPC gateway)        │
              └──────────────────────────────────┘
                     ▲         ▲         ▲
                     │gRPC     │gRPC     │REST(8.6+)
          ┌──────────┴──┐  ┌───┴──────┐ ┌┴──────────────┐
          │ Java worker │  │ Node.js  │ │ Python / REST │
          │ @JobWorker  │  │ worker   │ │ worker        │
          │ ("payment") │  │ ("pay")  │ │ ("payment")   │
          └─────────────┘  └──────────┘ └───────────────┘
          All workers tell the broker which job type they handle.
          Broker distributes — workers never talk to each other.
```