# ActiveMQ with Spring Boot

## Table of Contents
1. [What is ActiveMQ?](#what-is-activemq)
2. [How ActiveMQ Works](#how-activemq-works)
3. [Key Concepts](#key-concepts)
4. [Message Retry Mechanism and Redelivery Policy](#message-retry-mechanism-and-redelivery-policy)
5. [Dead Letter Queue (DLQ)](#dead-letter-queue-dlq)
6. [Topics - Message Persistence and Durable Subscriptions](#topics---message-persistence-and-durable-subscriptions)
7. [Acknowledgement Modes In Depth](#acknowledgement-modes-in-depth)
8. [Request-Reply Pattern (@SendTo) and Return Value Behavior](#request-reply-pattern-sendto-and-return-value-behavior)
9. [Message Headers and Custom Properties](#message-headers-and-custom-properties)
10. [Integration with Spring Boot](#integration-with-spring-boot)
11. [Complete Example](#complete-example)
12. [Best Practices](#best-practices)

## What is ActiveMQ?

Apache ActiveMQ is an open-source message broker written in Java that implements the Java Message Service (JMS) API. It facilitates communication between different applications or components by enabling asynchronous message passing.

### Key Features:
- **Multi-protocol support**: JMS, AMQP, STOMP, MQTT, WebSocket
- **Flexible deployment**: Standalone, embedded, or clustered
- **High availability**: Master-slave configurations, network of brokers
- **Persistent messaging**: Messages can be stored to disk
- **Transaction support**: XA and local transactions
- **Security**: Authentication and authorization mechanisms

### Use Cases:
- Decoupling microservices
- Load balancing and scalability
- Handling asynchronous processing
- Event-driven architectures
- Reliable message delivery
- Order processing systems
- Real-time notifications

## How ActiveMQ Works

### Architecture Overview

```
┌─────────────┐         ┌──────────────────┐         ┌─────────────┐
│  Producer   │────────>│   ActiveMQ       │────────>│  Consumer   │
│ Application │         │   Broker         │         │ Application │
└─────────────┘         └──────────────────┘         └─────────────┘
                                │
                                │
                        ┌───────▼────────┐
                        │  Destinations  │
                        │  - Queues      │
                        │  - Topics      │
                        └────────────────┘
```

### Message Flow:

1. **Producer** creates a message and sends it to the broker
2. **Broker** receives the message and stores it in the appropriate destination
3. **Consumer** connects to the broker and retrieves messages
4. Message is acknowledged and removed from the queue (or marked as consumed)

### Messaging Models:

#### 1. Point-to-Point (Queue)
- One producer, one or more consumers
- Each message is consumed by only ONE consumer
- Messages remain in queue until consumed or expired

#### 2. Publish-Subscribe (Topic)
- One publisher, multiple subscribers
- Each message is delivered to ALL active subscribers
- Messages are not persisted for absent subscribers (unless durable subscriptions)

## Key Concepts

### 1. Connection Factory
The factory that creates connections to the ActiveMQ broker.

### 2. Destination
The target where messages are sent - either a Queue or Topic.

### 3. Message Producer
The component that sends messages to destinations.

### 4. Message Consumer
The component that receives messages from destinations.

### 5. Message
The data being transmitted, which includes:
- **Header**: Metadata (priority, timestamp, expiration)
- **Properties**: Custom key-value pairs
- **Body**: The actual payload (text, object, bytes, etc.)

## Message Retry Mechanism and Redelivery Policy

When a consumer fails to process a message (throws an exception or does not acknowledge it), ActiveMQ does **not** discard the message. Instead it uses a **redelivery policy** to retry delivering that message.

### How Retry Works Step by Step

```
Message sent to consumer
        │
        ▼
Consumer processing fails (exception thrown)
        │
        ▼
Message is NOT acknowledged
        │
        ▼
ActiveMQ marks message for redelivery
        │
        ▼
Broker waits for redelivery delay
        │
        ▼
Message redelivered to a consumer (same or different instance)
        │
        ▼
Retry count exceeded? ──Yes──> Move to Dead Letter Queue (DLQ)
        │
       No
        │
        ▼
Retry again (back to waiting for delay)
```

### Default Redelivery Behavior

By default, ActiveMQ will:
- Redeliver the message **6 times** (the initial delivery + 6 retries = 7 total attempts)
- Use **no delay** between redeliveries (0 ms)
- After all retries are exhausted, move the message to the **Dead Letter Queue (DLQ)**

### Configuring Redelivery Policy in Spring Boot

```java
@Configuration
public class RedeliveryConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL("tcp://localhost:61616");

        // Configure redelivery policy
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(5);          // retry 5 times
        redeliveryPolicy.setInitialRedeliveryDelay(1000);     // 1 second before first retry
        redeliveryPolicy.setRedeliveryDelay(2000);            // 2 seconds between retries
        redeliveryPolicy.setUseExponentialBackOff(true);      // enable exponential backoff
        redeliveryPolicy.setBackOffMultiplier(2.0);           // double the delay each retry
        redeliveryPolicy.setMaximumRedeliveryDelay(60000);    // cap delay at 60 seconds

        factory.setRedeliveryPolicy(redeliveryPolicy);
        return factory;
    }
}
```

### Redelivery Timeline Example (Exponential Backoff)

With the configuration above, the retry timeline looks like:

| Attempt | Delay Before Delivery | Cumulative Time |
|---------|----------------------|-----------------|
| 1st (initial) | 0 ms | 0 ms |
| 2nd (retry 1) | 1,000 ms | 1 s |
| 3rd (retry 2) | 2,000 ms | 3 s |
| 4th (retry 3) | 4,000 ms | 7 s |
| 5th (retry 4) | 8,000 ms | 15 s |
| 6th (retry 5) | 16,000 ms | 31 s |
| Exhausted | → Moved to DLQ | — |

### Per-Destination Redelivery Policy

You can set different retry policies for different queues:

```java
@Bean
public ConnectionFactory connectionFactory() {
    ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
    factory.setBrokerURL("tcp://localhost:61616");

    // Default policy
    RedeliveryPolicy defaultPolicy = new RedeliveryPolicy();
    defaultPolicy.setMaximumRedeliveries(3);
    defaultPolicy.setInitialRedeliveryDelay(1000);

    // Policy for order queue - more retries with longer delays
    RedeliveryPolicy orderPolicy = new RedeliveryPolicy();
    orderPolicy.setMaximumRedeliveries(10);
    orderPolicy.setInitialRedeliveryDelay(5000);
    orderPolicy.setUseExponentialBackOff(true);
    orderPolicy.setBackOffMultiplier(2.0);

    // Policy for notification queue - fewer retries
    RedeliveryPolicy notificationPolicy = new RedeliveryPolicy();
    notificationPolicy.setMaximumRedeliveries(2);
    notificationPolicy.setInitialRedeliveryDelay(500);

    RedeliveryPolicyMap policyMap = new RedeliveryPolicyMap();
    policyMap.setDefaultEntry(defaultPolicy);
    policyMap.put(new ActiveMQQueue("order.queue"), orderPolicy);
    policyMap.put(new ActiveMQQueue("notification.queue"), notificationPolicy);

    factory.setRedeliveryPolicyMap(policyMap);
    return factory;
}
```

### How the Consumer Triggers a Retry

When using Spring's `@JmsListener`, a retry is triggered when the listener method throws an exception. If the session is transacted, the transaction rolls back and the message is redelivered.

```java
@JmsListener(destination = "order.queue")
public void receiveOrder(Order order) {
    // If this method throws an exception, the message will be
    // redelivered according to the redelivery policy
    if (order.getQuantity() <= 0) {
        throw new RuntimeException("Invalid order quantity");
        // ActiveMQ will redeliver this message
    }
    processOrder(order);
}
```

### Checking Redelivery Count in Consumer

You can inspect the redelivery count to decide how to handle a message:

```java
@JmsListener(destination = "order.queue")
public void receiveOrder(Message message) throws JMSException {
    int redeliveryCount = message.getIntProperty("JMSXDeliveryCount") - 1;
    Order order = (Order) ((ObjectMessage) message).getObject();

    if (redeliveryCount > 0) {
        logger.warn("Redelivery attempt {} for order: {}", redeliveryCount, order.getOrderId());
    }

    if (redeliveryCount >= 3) {
        logger.error("Message failed after {} retries, handling manually", redeliveryCount);
        // Custom fallback logic instead of waiting for DLQ
        saveToDatabaseForManualReview(order);
        message.acknowledge();
        return;
    }

    processOrder(order);
}
```

---

## Dead Letter Queue (DLQ)

The Dead Letter Queue is where messages go after all redelivery attempts are exhausted. This prevents poison messages (messages that can never be processed) from blocking the queue forever.

### Default DLQ Behavior

- Default DLQ name: `ActiveMQ.DLQ`
- **All** failed messages from all queues go to this single DLQ by default
- Messages in the DLQ retain their original headers and properties

### Configuring Individual DLQs Per Queue

You can configure ActiveMQ (in `activemq.xml` on the broker side) to use separate DLQs for each source queue:

```xml
<broker>
    <destinationPolicy>
        <policyMap>
            <policyEntries>
                <!-- Individual DLQ for order queue -->
                <policyEntry queue="order.queue">
                    <deadLetterStrategy>
                        <individualDeadLetterStrategy
                            queuePrefix="DLQ."
                            useQueueForQueueMessages="true" />
                    </deadLetterStrategy>
                </policyEntry>

                <!-- Default policy for all other queues -->
                <policyEntry queue=">">
                    <deadLetterStrategy>
                        <sharedDeadLetterStrategy
                            processExpired="true"
                            processNonPersistent="true" />
                    </deadLetterStrategy>
                </policyEntry>
            </policyEntries>
        </policyMap>
    </destinationPolicy>
</broker>
```

With `individualDeadLetterStrategy`, failed messages from `order.queue` go to `DLQ.order.queue`.

### Consuming from the DLQ

You can create a listener to process DLQ messages (for alerting, logging, or manual retry):

```java
@Service
public class DlqConsumer {

    private static final Logger logger = LoggerFactory.getLogger(DlqConsumer.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @JmsListener(destination = "DLQ.order.queue")
    public void processDlqMessage(Order order) {
        logger.error("DLQ received failed order: {}", order.getOrderId());

        // Option 1: Save to database for manual review
        saveForManualReview(order);

        // Option 2: Send alert
        sendAlertToOpsTeam(order);

        // Option 3: Retry after fixing the issue
        // jmsTemplate.convertAndSend("order.queue", order);
    }
}
```

### Discarding Messages Instead of DLQ

If you don't want failed messages to go to DLQ at all (discard them):

```xml
<policyEntry queue="transient.queue">
    <deadLetterStrategy>
        <sharedDeadLetterStrategy processExpired="false"
                                  processNonPersistent="false" />
    </deadLetterStrategy>
</policyEntry>
```

### Setting Message Expiry (TTL)

Messages can expire if not consumed within a time limit. Expired messages also go to the DLQ:

```java
public void sendOrderWithTTL(Order order) {
    jmsTemplate.setExplicitQosEnabled(true);
    jmsTemplate.setTimeToLive(300000); // 5 minutes TTL
    jmsTemplate.convertAndSend("order.queue", order);
}
```

---

## Topics - Message Persistence and Durable Subscriptions

This is one of the most important concepts to understand. The behavior of messages in **Topics** is fundamentally different from **Queues**.

### Queue vs Topic - Message Retention Comparison

| Behavior | Queue | Topic (Non-Durable) | Topic (Durable) |
|----------|-------|---------------------|------------------|
| Message stored until consumed? | Yes | No | Yes |
| Offline consumer receives message? | Yes | **No** | Yes |
| Message deleted after consumption? | Yes (removed) | Immediately after broadcast | After all durable subscribers consume |
| One consumer down, message lost? | No (another consumer picks up) | **Yes, for that subscriber** | No |

### Non-Durable Topic Subscription (Default)

By default, topic subscribers are **non-durable**. This means:

1. Messages are delivered **only to currently active (connected) subscribers**
2. If a subscriber is offline/disconnected when a message is published, **that message is permanently lost** for that subscriber
3. The broker does **not** store messages waiting for absent subscribers
4. Once a message is broadcast to all active subscribers, it is **discarded from storage**

```
Publisher sends message "M1" to topic
        │
        ├──> Subscriber A (ONLINE)  ──> Receives M1 ✓
        ├──> Subscriber B (ONLINE)  ──> Receives M1 ✓
        └──> Subscriber C (OFFLINE) ──> NEVER receives M1 ✗ (message lost)
```

### Durable Topic Subscription

With **durable subscriptions**, the broker **stores messages** for offline subscribers until they reconnect and consume them.

#### How Durable Subscriptions Work:

1. A subscriber registers with a **unique client ID** and **subscription name**
2. The broker tracks which messages each durable subscriber has consumed
3. When the subscriber goes offline, the broker **continues storing messages** for it
4. When the subscriber reconnects, it receives **all missed messages**
5. Messages are deleted from the broker's storage **only after all durable subscribers have consumed them**

```
Publisher sends message "M1" to topic
        │
        ├──> Durable Subscriber A (ONLINE)  ──> Receives M1 ✓
        ├──> Durable Subscriber B (ONLINE)  ──> Receives M1 ✓
        └──> Durable Subscriber C (OFFLINE) ──> M1 stored, waiting...
                                                    │
                                  C reconnects later │
                                                    ▼
                                            Receives M1 ✓
                                            (broker deletes M1 from storage now)
```

### Configuring Durable Subscriptions in Spring Boot

**Step 1: Set Client ID on Connection Factory**

```java
@Configuration
@EnableJms
public class DurableTopicConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL("tcp://localhost:61616");
        factory.setClientID("my-application-1"); // REQUIRED for durable subscriptions
        return factory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory durableTopicListenerFactory(
            ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPubSubDomain(true);               // enable topic mode
        factory.setSubscriptionDurable(true);         // enable durable subscription
        factory.setClientId("my-application-1");      // client ID
        factory.setMessageConverter(jacksonJmsMessageConverter());
        return factory;
    }
}
```

**Step 2: Create Durable Subscriber Listener**

```java
@Service
public class DurableNotificationSubscriber {

    private static final Logger logger = LoggerFactory.getLogger(DurableNotificationSubscriber.class);

    // Durable subscriber - will receive missed messages after reconnection
    @JmsListener(
        destination = "notification.topic",
        containerFactory = "durableTopicListenerFactory",
        subscription = "email-notification-sub"   // unique subscription name
    )
    public void receiveEmailNotification(String message) {
        logger.info("Email subscriber received: {}", message);
        sendEmail(message);
    }

    // Another durable subscriber with different subscription name
    @JmsListener(
        destination = "notification.topic",
        containerFactory = "durableTopicListenerFactory",
        subscription = "sms-notification-sub"
    )
    public void receiveSmsNotification(String message) {
        logger.info("SMS subscriber received: {}", message);
        sendSms(message);
    }
}
```

### What Happens When One Listener Is Down?

**Scenario: 3 durable subscribers, subscriber C goes down**

| Time | Event | Subscriber A | Subscriber B | Subscriber C |
|------|-------|-------------|-------------|-------------|
| T1 | All connected | Online | Online | Online |
| T2 | Message M1 published | Receives M1 | Receives M1 | Receives M1 |
| T3 | Subscriber C crashes | Online | Online | **Offline** |
| T4 | Message M2 published | Receives M2 | Receives M2 | M2 **stored by broker** |
| T5 | Message M3 published | Receives M3 | Receives M3 | M3 **stored by broker** |
| T6 | Subscriber C restarts | Online | Online | **Receives M2 and M3** |

**Key point:** The broker does NOT delete messages M2 and M3 just because A and B consumed them. It keeps them in storage **until C also consumes them**.

### When Is a Message Deleted from Topic Storage?

For durable subscriptions, a message is deleted from the broker's persistent storage when **ALL** of the following are true:

1. All **active** durable subscribers have acknowledged the message
2. All **offline** durable subscribers have reconnected and acknowledged the message
3. OR the message has expired (if TTL was set)
4. OR the durable subscription has been explicitly unsubscribed/removed

### Retroactive Consumers (Alternative to Durable Subscriptions)

If you want new subscribers to receive recent messages without durable subscriptions:

```xml
<!-- activemq.xml broker configuration -->
<destinationPolicy>
    <policyMap>
        <policyEntries>
            <policyEntry topic="notification.topic">
                <subscriptionRecoveryPolicy>
                    <!-- Keep last 10 minutes of messages for new subscribers -->
                    <timedSubscriptionRecoveryPolicy recoverDuration="600000" />
                </subscriptionRecoveryPolicy>
            </policyEntry>
        </policyEntries>
    </policyMap>
</destinationPolicy>
```

### Virtual Topics (Recommended Alternative)

ActiveMQ provides **Virtual Topics** which combine the benefits of both Queues and Topics:

- Messages are published to a topic but consumed from queues
- Each subscriber gets its own queue (so messages are not lost if subscriber is offline)
- Load balancing works within each subscriber's queue

```java
// Producer publishes to virtual topic
@Service
public class VirtualTopicPublisher {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void publish(String message) {
        // Naming convention: VirtualTopic.<name>
        jmsTemplate.convertAndSend("VirtualTopic.Orders", message);
    }
}

// Consumer reads from virtual topic consumer queue
@Service
public class VirtualTopicConsumer {

    // Naming convention: Consumer.<clientName>.VirtualTopic.<name>
    @JmsListener(destination = "Consumer.EmailService.VirtualTopic.Orders")
    public void receiveForEmail(String message) {
        // This queue persists messages even when this consumer is offline
        logger.info("Email service received: {}", message);
    }

    @JmsListener(destination = "Consumer.SmsService.VirtualTopic.Orders")
    public void receiveForSms(String message) {
        logger.info("SMS service received: {}", message);
    }
}
```

**Why Virtual Topics are preferred:**
- No client ID required (avoids single-connection limitation)
- Messages are persisted per consumer queue (no message loss)
- Supports competing consumers (load balancing) per subscriber group
- Simpler configuration than durable subscriptions

---

## Acknowledgement Modes In Depth

The acknowledgement mode determines when the broker considers a message as successfully consumed and can remove it from the destination. Until a message is acknowledged, the broker **keeps it in storage** and can redeliver it.

### What Is Acknowledgement?

Acknowledgement is the consumer telling the broker: "I have successfully received and processed this message, you can delete it now." Without acknowledgement, the broker assumes the message was not consumed and will redeliver it.

```
Broker sends message to Consumer
        │
        ▼
Consumer processes message
        │
        ├── Success ──> Consumer sends ACK ──> Broker deletes message ✓
        │
        └── Failure ──> No ACK sent ──> Broker redelivers message ↩
```

### All Acknowledgement Modes Comparison

| Mode | Who Acknowledges? | When? | Message Safe? | Performance |
|------|------------------|-------|---------------|-------------|
| `AUTO_ACKNOWLEDGE` | JMS session (automatic) | After listener method returns without exception | Risk of loss if app crashes after return but before processing completes | Fastest |
| `CLIENT_ACKNOWLEDGE` | Your code (manual) | When you call `message.acknowledge()` | Safe - you control exactly when | Medium |
| `DUPS_OK_ACKNOWLEDGE` | JMS session (lazy automatic) | Batched, not immediate | May get duplicates | Faster than AUTO |
| `SESSION_TRANSACTED` | Transaction manager | On transaction commit | Safest - rollback = redeliver | Slowest |

---

### AUTO_ACKNOWLEDGE (Default)

The session automatically acknowledges **after the listener method returns successfully**. If the method throws an exception, the message is **not** acknowledged and will be redelivered.

```java
@Bean
public DefaultJmsListenerContainerFactory autoAckFactory(
        ConnectionFactory connectionFactory) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
    return factory;
}
```

**How it works internally:**

```java
@JmsListener(destination = "order.queue")
public void receiveOrder(Order order) {
    // 1. Message delivered to this method
    processOrder(order);
    // 2. Method returns successfully
    // 3. Spring/JMS session AUTOMATICALLY sends ACK to broker
    // 4. Broker deletes the message

    // If processOrder() throws an exception:
    // - Method does NOT return normally
    // - ACK is NOT sent
    // - Broker redelivers the message
}
```

**When to use:** Simple consumers where processing is fast and you don't need fine-grained control over acknowledgement.

**Risk:** If your method returns successfully but the application crashes before the ACK reaches the broker (rare but possible), the message will be redelivered, causing duplicate processing.

---

### CLIENT_ACKNOWLEDGE

The consumer must **explicitly call `message.acknowledge()`**. This gives you full control over when a message is considered consumed. The broker will not delete the message until it receives your explicit ACK.

```java
@Bean
public DefaultJmsListenerContainerFactory clientAckFactory(
        ConnectionFactory connectionFactory) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
    return factory;
}
```

**Basic usage:**

```java
@JmsListener(destination = "order.queue", containerFactory = "clientAckFactory")
public void receiveOrder(Message message) throws JMSException {
    Order order = (Order) ((ObjectMessage) message).getObject();

    try {
        processOrder(order);
        saveToDatabase(order);

        // ONLY NOW tell the broker to delete the message
        message.acknowledge();  // ← explicit ACK

    } catch (Exception e) {
        // Don't acknowledge - message will be redelivered
        logger.error("Failed to process order", e);
        // No need to call anything - just don't call acknowledge()
    }
}
```

**Important behavior:** `message.acknowledge()` acknowledges ALL unacknowledged messages on the same session up to and including this message, not just this one message.

```java
// If session received messages M1, M2, M3 in order:
// Calling acknowledge() on M3 also acknowledges M1 and M2

@JmsListener(destination = "order.queue", containerFactory = "clientAckFactory")
public void receiveOrder(Message message) throws JMSException {
    // This is message M3
    message.acknowledge();
    // M1, M2, and M3 are ALL acknowledged now
}
```

**When to use:**
- You need to ensure processing is 100% complete before the message is removed
- You do multiple operations (process + save to DB + call external API) and want to acknowledge only after all succeed
- You want to implement custom retry logic

**Acknowledging after multiple steps:**

```java
@JmsListener(destination = "order.queue", containerFactory = "clientAckFactory")
public void receiveOrder(Message message) throws JMSException {
    Order order = extractOrder(message);

    // Step 1: Validate
    validateOrder(order);

    // Step 2: Save to database
    orderRepository.save(order);

    // Step 3: Call payment service
    paymentService.charge(order);

    // Step 4: Send confirmation email
    emailService.sendConfirmation(order);

    // ALL steps succeeded - now acknowledge
    message.acknowledge();

    // If any step above throws, acknowledge() is never called,
    // and the broker will redeliver the message
}
```

---

### DUPS_OK_ACKNOWLEDGE

The session acknowledges in a **lazy/batched** manner. Instead of acknowledging each message immediately, it batches acknowledgements for performance. This means the consumer **may receive duplicate messages** if the session fails before a batch acknowledgement is sent.

```java
@Bean
public DefaultJmsListenerContainerFactory dupsOkFactory(
        ConnectionFactory connectionFactory) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setSessionAcknowledgeMode(Session.DUPS_OK_ACKNOWLEDGE);
    return factory;
}

@JmsListener(destination = "analytics.queue", containerFactory = "dupsOkFactory")
public void receiveAnalyticsEvent(String event) {
    // This consumer might receive the same event twice
    // Your processing logic must be idempotent
    analyticsService.record(event);
}
```

**When to use:** High-throughput scenarios where duplicates are acceptable (analytics, logging, metrics). Your consumer logic must be **idempotent** (safe to process the same message more than once).

---

### SESSION_TRANSACTED

Wraps consumption in a JMS transaction. The message is acknowledged when the **transaction commits**. If the transaction **rolls back**, the message is redelivered. This is the safest mode.

```java
@Bean
public DefaultJmsListenerContainerFactory transactedFactory(
        ConnectionFactory connectionFactory) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setSessionTransacted(true); // enables transacted mode
    return factory;
}
```

**How it works:**

```java
@JmsListener(destination = "order.queue", containerFactory = "transactedFactory")
public void receiveOrder(Order order) {
    // 1. Transaction begins automatically before this method is called

    processOrder(order);
    saveToDatabase(order);

    // 2a. Method returns normally → Transaction COMMITS → Message acknowledged ✓
    // 2b. Method throws exception → Transaction ROLLS BACK → Message redelivered ↩
}
```

**Combining JMS transaction with database transaction:**

```java
@JmsListener(destination = "order.queue", containerFactory = "transactedFactory")
@Transactional  // Spring @Transactional for database
public void receiveOrder(Order order) {
    // Both JMS and DB are in a transaction
    Order saved = orderRepository.save(order);
    paymentService.charge(saved);

    // If paymentService.charge() fails:
    // - DB transaction rolls back (order not saved)
    // - JMS transaction rolls back (message redelivered)
    // Everything is consistent
}
```

**When to use:** When message consumption involves database writes or other transactional resources and you need atomicity (all-or-nothing).

---

### Manually Recovering (NACK equivalent)

JMS does not have an explicit NACK (negative acknowledgement) like RabbitMQ. Instead, you control redelivery by:

**1. Throwing an exception (AUTO_ACKNOWLEDGE / SESSION_TRANSACTED):**

```java
@JmsListener(destination = "order.queue")
public void receiveOrder(Order order) {
    if (!isValid(order)) {
        // Throwing triggers redelivery
        throw new RuntimeException("Invalid order");
    }
    processOrder(order);
}
```

**2. Calling `session.recover()` (CLIENT_ACKNOWLEDGE):**

```java
@JmsListener(destination = "order.queue", containerFactory = "clientAckFactory")
public void receiveOrder(Session session, Message message) throws JMSException {
    Order order = extractOrder(message);

    if (!isValid(order)) {
        // Explicitly tell the session to redeliver all unacknowledged messages
        session.recover();
        return;
    }

    processOrder(order);
    message.acknowledge();
}
```

**3. Not calling `acknowledge()` (CLIENT_ACKNOWLEDGE):**

If you simply don't call `message.acknowledge()` and the method returns, the message remains unacknowledged. It will be redelivered when the session is recovered or the consumer reconnects.

---

### Which Mode Should You Choose?

| Scenario | Recommended Mode | Why |
|----------|-----------------|-----|
| Simple processing, no DB writes | `AUTO_ACKNOWLEDGE` | Simple and fast, low risk |
| Multi-step processing (process + save + call API) | `CLIENT_ACKNOWLEDGE` | Acknowledge only after all steps succeed |
| High throughput logging/analytics | `DUPS_OK_ACKNOWLEDGE` | Best performance, duplicates are acceptable |
| Database writes that must be atomic with message consumption | `SESSION_TRANSACTED` | Ensures DB and message are consistent |
| Financial transactions, payment processing | `SESSION_TRANSACTED` | Cannot afford message loss or double processing |
| Idempotent consumers with high volume | `DUPS_OK_ACKNOWLEDGE` | Performance over exactness |

---

## Request-Reply Pattern (@SendTo) and Return Value Behavior

### What Happens When a @JmsListener Returns a Value?

When a `@JmsListener` method has a **return value**, Spring JMS automatically takes that return value and sends it as a **new message** to a response destination. This is the Request-Reply (or Request-Response) messaging pattern.

### How @SendTo Works Internally

```java
@JmsListener(destination = "request.queue")
@SendTo("response.queue")
public Order receiveAndReply(Order order) {
    order.setOrderId("PROCESSED-" + order.getOrderId());
    return order;  // ← This return value becomes a new message
}
```

**Step-by-step flow:**

```
1. Producer sends Order to "request.queue"
        │
        ▼
2. @JmsListener picks up the message from "request.queue"
        │
        ▼
3. receiveAndReply() method executes, processes the order
        │
        ▼
4. Method returns an Order object
        │
        ▼
5. Spring JMS takes the returned Order and sends it as a NEW message
   to "response.queue" (specified in @SendTo)
        │
        ▼
6. Original message in "request.queue" is acknowledged (consumed)
        │
        ▼
7. Another consumer listening on "response.queue" receives the response
```

### Complete Request-Reply Example

**Step 1: The Request Sender (Client)**

```java
@Service
public class OrderRequestService {

    @Autowired
    private JmsTemplate jmsTemplate;

    // Option A: Fire-and-forget request (don't wait for reply)
    public void sendOrderRequest(Order order) {
        jmsTemplate.convertAndSend("request.queue", order);
        // Response will arrive at "response.queue" asynchronously
    }

    // Option B: Synchronous request-reply (blocks until reply arrives)
    public Order sendAndWaitForReply(Order order) {
        // This method:
        // 1. Sends the order to "request.queue"
        // 2. Creates a temporary reply queue automatically
        // 3. Sets JMSReplyTo header to the temp queue
        // 4. BLOCKS and waits for a response on the temp queue
        // 5. Returns the response
        Order processedOrder = (Order) jmsTemplate.convertSendAndReceive(
            "request.queue", order
        );
        return processedOrder;
        // Default timeout: waits indefinitely
        // Configure with: jmsTemplate.setReceiveTimeout(5000); // 5 seconds
    }
}
```

**Step 2: The Processor (Server) - Returns a Reply**

```java
@Service
public class OrderProcessor {

    private static final Logger logger = LoggerFactory.getLogger(OrderProcessor.class);

    @JmsListener(destination = "request.queue")
    @SendTo("response.queue")
    public Order receiveAndReply(Order order) {
        logger.info("Received order request: {}", order.getOrderId());

        // Process the order
        order.setOrderId("PROCESSED-" + order.getOrderId());
        order.setPrice(order.getPrice() * 0.9); // apply 10% discount

        logger.info("Returning processed order: {}", order.getOrderId());
        return order;
        // Spring takes this return value and sends it to "response.queue"
    }
}
```

**Step 3: The Response Consumer**

```java
@Service
public class OrderResponseConsumer {

    @JmsListener(destination = "response.queue")
    public void receiveResponse(Order processedOrder) {
        logger.info("Received processed order: {}", processedOrder.getOrderId());
        // Update UI, notify user, etc.
    }
}
```

### What Happens at Each Stage - Detailed Breakdown

#### When Method Returns Successfully

```java
@JmsListener(destination = "request.queue")
@SendTo("response.queue")
public Order receiveAndReply(Order order) {
    return processedOrder;  // returns non-null
}
```

1. Spring receives the returned `Order` object
2. Spring converts it to a JMS message using the configured `MessageConverter`
3. Spring sends the converted message to `response.queue`
4. The original request message is **acknowledged** (removed from `request.queue`)
5. Both operations (reply send + request ack) happen within the same session

#### When Method Returns null

```java
@JmsListener(destination = "request.queue")
@SendTo("response.queue")
public Order receiveAndReply(Order order) {
    if (!isValid(order)) {
        return null;  // ← What happens?
    }
    return processedOrder;
}
```

- **No reply message is sent.** Spring skips sending when the return is `null`.
- The original request message is still **acknowledged** (consumed and removed).
- The sender waiting for a reply (if using `convertSendAndReceive`) will **time out**.

#### When Method Throws an Exception

```java
@JmsListener(destination = "request.queue")
@SendTo("response.queue")
public Order receiveAndReply(Order order) {
    throw new RuntimeException("Processing failed");
}
```

- **No reply message is sent.**
- The original request message is **NOT acknowledged**.
- The message will be **redelivered** according to the redelivery policy.
- If all retries are exhausted, the message goes to the **DLQ**.
- The sender waiting for a reply (if using `convertSendAndReceive`) will **time out**.

### Summary of Return Scenarios

| Return Scenario | Reply Sent? | Request Message Acknowledged? | Sender Behavior |
|----------------|-------------|------------------------------|-----------------|
| Returns non-null object | Yes → sent to `@SendTo` destination | Yes (consumed) | Receives reply |
| Returns `null` | No | Yes (consumed) | Times out waiting |
| Throws exception | No | No (redelivered) | Times out waiting |
| Returns void (no `@SendTo`) | No | Yes (consumed) | N/A |

### Dynamic Reply Destination (JMSReplyTo Header)

Instead of hardcoding `@SendTo("response.queue")`, you can let the **sender** specify where the reply should go using the `JMSReplyTo` header. This takes priority over `@SendTo`.

**Sender sets JMSReplyTo:**

```java
public void sendWithCustomReplyDestination(Order order) {
    jmsTemplate.convertAndSend("request.queue", order, message -> {
        // Tell the processor to reply to this specific queue
        message.setJMSReplyTo(new ActiveMQQueue("my-custom-reply.queue"));
        // Set a correlation ID so we can match request to reply
        message.setJMSCorrelationID(UUID.randomUUID().toString());
        return message;
    });
}
```

**Processor respects JMSReplyTo automatically:**

```java
@JmsListener(destination = "request.queue")
@SendTo("response.queue")  // fallback if JMSReplyTo is not set
public Order receiveAndReply(Order order) {
    return processOrder(order);
    // If JMSReplyTo header exists → reply goes to that destination
    // If JMSReplyTo header is missing → reply goes to "response.queue" (the @SendTo value)
}
```

**Priority order for reply destination:**
1. `JMSReplyTo` header on the incoming message (highest priority)
2. `@SendTo` annotation value (fallback)
3. If neither exists → no reply is sent (return value is discarded)

### Accessing the Raw Message in a Reply Method

You can inject both the deserialized payload and the raw JMS `Message` to access headers:

```java
@JmsListener(destination = "request.queue")
@SendTo("response.queue")
public Order receiveAndReply(Order order, Message requestMessage) throws JMSException {
    String correlationId = requestMessage.getJMSCorrelationID();
    String replyTo = requestMessage.getJMSReplyTo() != null
            ? requestMessage.getJMSReplyTo().toString()
            : "response.queue (default)";

    logger.info("Processing request correlationId={}, replyTo={}", correlationId, replyTo);

    order.setOrderId("PROCESSED-" + order.getOrderId());
    return order;
}
```

### Using convertSendAndReceive (Synchronous Request-Reply)

`convertSendAndReceive` is the simplest way to do request-reply. It handles temporary queues, correlation IDs, and blocking internally.

```java
@Service
public class SyncOrderService {

    @Autowired
    private JmsTemplate jmsTemplate;

    public Order processOrderSync(Order order) {
        // Set a timeout so we don't block forever
        jmsTemplate.setReceiveTimeout(10000); // 10 seconds

        // This internally:
        // 1. Creates a temporary reply queue
        // 2. Sets JMSReplyTo to the temp queue
        // 3. Sets JMSCorrelationID
        // 4. Sends order to "request.queue"
        // 5. Blocks waiting on the temp queue for a reply
        // 6. Returns the reply (or null if timeout)
        Order result = (Order) jmsTemplate.convertSendAndReceive("request.queue", order);

        if (result == null) {
            throw new RuntimeException("Order processing timed out after 10 seconds");
        }

        return result;
    }
}
```

**How `convertSendAndReceive` works under the hood:**

```
Client calls convertSendAndReceive("request.queue", order)
        │
        ├── 1. Creates temporary queue: "ID:temp-queue-xyz"
        ├── 2. Sets JMSReplyTo = "ID:temp-queue-xyz"
        ├── 3. Sets JMSCorrelationID = "abc-123"
        ├── 4. Sends message to "request.queue"
        │
        ▼
        5. BLOCKS, listening on "ID:temp-queue-xyz"
                                    │
        ┌───────────────────────────┘
        │   Meanwhile on the server:
        │   @JmsListener picks up from "request.queue"
        │   Processes, returns Order
        │   Spring sends reply to JMSReplyTo ("ID:temp-queue-xyz")
        │   with matching JMSCorrelationID
        └───────────────────────────┐
                                    │
        6. Client receives reply on temp queue
        7. Returns the deserialized Order object
```

### Error Handling in Request-Reply

```java
@JmsListener(destination = "request.queue")
@SendTo("response.queue")
public Object receiveAndReply(Order order) {
    try {
        Order processed = processOrder(order);
        return processed;  // success reply
    } catch (ValidationException e) {
        // Return an error response instead of throwing
        // This way the sender gets a reply (not a timeout)
        ErrorResponse error = new ErrorResponse(
            "VALIDATION_ERROR",
            e.getMessage(),
            order.getOrderId()
        );
        return error;  // sender receives ErrorResponse instead of Order
    }
    // If you throw here instead, NO reply is sent and sender times out
}
```

---

## Message Headers and Custom Properties

JMS messages have three parts: **headers**, **properties**, and **body**. Headers and properties carry metadata about the message, while the body carries the payload. Understanding how to set and read these is essential for routing, filtering, tracing, and passing contextual information between producer and consumer.

### JMS Standard Headers

These are built-in headers defined by the JMS specification. Some are set automatically by the broker or the `send()` method, while others can be set by the producer.

| Header | Set By | Description |
|--------|--------|-------------|
| `JMSMessageID` | Broker (auto) | Unique message identifier, e.g. `ID:hostname-12345-1234567890-1:1:1` |
| `JMSTimestamp` | Broker (auto) | Time the message was handed to the broker (milliseconds since epoch) |
| `JMSDestination` | Broker (auto) | The queue/topic the message was sent to |
| `JMSDeliveryMode` | Producer | `PERSISTENT` (default) or `NON_PERSISTENT` |
| `JMSPriority` | Producer | 0-9, default is 4 |
| `JMSExpiration` | Producer | When the message expires (0 = never) |
| `JMSReplyTo` | Producer | Destination for reply messages (request-reply pattern) |
| `JMSCorrelationID` | Producer | Used to link a reply to its request |
| `JMSRedelivered` | Broker (auto) | `true` if the message is being redelivered |
| `JMSType` | Producer | Application-defined message type identifier |

### Setting Headers When Sending Messages

Use `MessagePostProcessor` (the lambda in `convertAndSend`) to set headers and custom properties on outgoing messages.

#### Setting Standard JMS Headers

```java
@Service
public class HeaderAwareProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendWithHeaders(Order order) {
        jmsTemplate.convertAndSend("order.queue", order, message -> {
            // Standard JMS headers
            message.setJMSPriority(7);
            message.setJMSCorrelationID("corr-" + order.getOrderId());
            message.setJMSReplyTo(new ActiveMQQueue("order.reply.queue"));
            message.setJMSType("OrderMessage");
            return message;
        });
    }
}
```

#### Setting Custom Properties

Custom properties are key-value pairs you define. They can be used for routing (via message selectors), tracing, or passing metadata without modifying the message body.

```java
@Service
public class CustomPropertyProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendWithCustomProperties(Order order) {
        jmsTemplate.convertAndSend("order.queue", order, message -> {
            // String properties
            message.setStringProperty("orderType", "PREMIUM");
            message.setStringProperty("region", "US-EAST");
            message.setStringProperty("traceId", UUID.randomUUID().toString());
            message.setStringProperty("source", "web-checkout");

            // Numeric properties
            message.setIntProperty("retryCount", 0);
            message.setLongProperty("orderAmount", 15000L);
            message.setDoubleProperty("discountRate", 0.15);

            // Boolean properties
            message.setBooleanProperty("isExpress", true);
            message.setBooleanProperty("requiresApproval", order.getPrice() > 10000);

            return message;
        });
    }
}
```

#### Setting Headers with JmsTemplate Directly (Without MessagePostProcessor)

For headers that apply to all messages sent by a `JmsTemplate`, you can configure them on the template itself:

```java
@Bean
public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
    JmsTemplate template = new JmsTemplate(connectionFactory);
    template.setExplicitQosEnabled(true);  // REQUIRED to enable priority/TTL settings
    template.setPriority(7);               // default priority for all messages
    template.setTimeToLive(300000);        // 5 minutes TTL for all messages
    template.setDeliveryPersistent(true);  // persistent delivery
    return template;
}
```

**Important:** `setExplicitQosEnabled(true)` must be called for priority, TTL, and delivery mode settings to take effect. Without it, these values are ignored and JMS defaults are used.

### Reading Headers in a @JmsListener

There are multiple ways to access headers and properties in a listener, from simple to fully manual.

#### Method 1: Using @Header Annotation (Recommended for Specific Headers)

The `@Header` annotation extracts individual headers or properties by name. This is the cleanest approach when you only need a few specific values.

```java
@Service
public class HeaderAwareConsumer {

    @JmsListener(destination = "order.queue")
    public void receiveOrder(
            Order order,
            @Header("orderType") String orderType,
            @Header("region") String region,
            @Header("isExpress") boolean isExpress,
            @Header("traceId") String traceId
    ) {
        logger.info("Order received: type={}, region={}, express={}, traceId={}",
                orderType, region, isExpress, traceId);
        processOrder(order);
    }
}
```

#### @Header with Optional Properties (Avoiding Exceptions)

If a header might not be present, use `required = false` with a default value. Without this, a missing header throws `MessageConversionException`.

```java
@JmsListener(destination = "order.queue")
public void receiveOrder(
        Order order,
        @Header(name = "orderType", required = false) String orderType,
        @Header(name = "retryCount", defaultValue = "0") int retryCount,
        @Header(name = "isExpress", required = false) Boolean isExpress
) {
    // orderType will be null if not set
    // retryCount will be 0 if not set
    // isExpress will be null if not set (use Boolean wrapper, not primitive)
    String type = (orderType != null) ? orderType : "STANDARD";
    boolean express = (isExpress != null) ? isExpress : false;

    logger.info("Processing order: type={}, retryCount={}, express={}", type, retryCount, express);
}
```

#### Reading Standard JMS Headers with @Header

Standard JMS headers use the `JmsHeaders` constants:

```java
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.jms.support.JmsHeaders;

@JmsListener(destination = "order.queue")
public void receiveOrder(
        Order order,
        @Header(JmsHeaders.MESSAGE_ID) String messageId,
        @Header(JmsHeaders.TIMESTAMP) long timestamp,
        @Header(JmsHeaders.CORRELATION_ID) String correlationId,
        @Header(JmsHeaders.REPLY_TO) String replyTo,
        @Header(JmsHeaders.REDELIVERED) boolean redelivered,
        @Header(JmsHeaders.PRIORITY) int priority
) {
    logger.info("Message ID: {}, timestamp: {}, priority: {}, redelivered: {}",
            messageId, timestamp, priority, redelivered);
}
```

#### Method 2: Using @Headers to Get All Headers as a Map

If you need access to all headers at once, inject them as a `Map` or `MessageHeaders`:

```java
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.MessageHeaders;

@JmsListener(destination = "order.queue")
public void receiveOrder(Order order, @Headers Map<String, Object> headers) {
    // Access any header by name
    String orderType = (String) headers.get("orderType");
    String region = (String) headers.get("region");
    String messageId = (String) headers.get(JmsHeaders.MESSAGE_ID);

    // Iterate all headers
    headers.forEach((key, value) ->
        logger.debug("Header: {} = {}", key, value)
    );

    processOrder(order);
}

// Alternative: use MessageHeaders type directly
@JmsListener(destination = "order.queue")
public void receiveOrder(Order order, MessageHeaders headers) {
    String orderType = headers.get("orderType", String.class);
    Long timestamp = headers.get(JmsHeaders.TIMESTAMP, Long.class);
}
```

#### Method 3: Using the Raw JMS Message Object

For full access to the underlying JMS API, inject `javax.jms.Message` directly. This gives you access to every JMS header, property, and method.

```java
import javax.jms.Message;
import javax.jms.TextMessage;

@JmsListener(destination = "order.queue")
public void receiveOrder(Message message) throws JMSException {
    // Standard JMS headers
    String messageId = message.getJMSMessageID();
    long timestamp = message.getJMSTimestamp();
    int priority = message.getJMSPriority();
    int deliveryMode = message.getJMSDeliveryMode();
    String correlationId = message.getJMSCorrelationID();
    boolean redelivered = message.getJMSRedelivered();
    long expiration = message.getJMSExpiration();
    Destination replyTo = message.getJMSReplyTo();
    Destination destination = message.getJMSDestination();

    // Custom properties
    String orderType = message.getStringProperty("orderType");
    int retryCount = message.getIntProperty("retryCount");
    boolean isExpress = message.getBooleanProperty("isExpress");
    double discountRate = message.getDoubleProperty("discountRate");

    // Check if a property exists before reading
    if (message.propertyExists("region")) {
        String region = message.getStringProperty("region");
    }

    // Enumerate all custom properties
    Enumeration<?> propertyNames = message.getPropertyNames();
    while (propertyNames.hasMoreElements()) {
        String name = (String) propertyNames.nextElement();
        Object value = message.getObjectProperty(name);
        logger.info("Property: {} = {} (type: {})", name, value, value.getClass().getSimpleName());
    }

    // Get the body
    String body = ((TextMessage) message).getText();
    // Or deserialize manually using your message converter
}
```

#### Method 4: Injecting Both Payload and Message

You can inject the deserialized payload along with the raw message to get the best of both worlds:

```java
@JmsListener(destination = "order.queue")
public void receiveOrder(Order order, Message message) throws JMSException {
    // 'order' is automatically deserialized by the message converter
    // 'message' gives you access to all headers and properties

    String traceId = message.getStringProperty("traceId");
    int priority = message.getJMSPriority();

    logger.info("Processing order {} with traceId={} priority={}",
            order.getOrderId(), traceId, priority);
    processOrder(order);
}
```

### Using Headers for Message Filtering (Selectors)

Message selectors use SQL-92 syntax to filter messages based on headers and properties. Only messages matching the selector expression are delivered to the consumer.

```java
// Only receive PREMIUM orders from US-EAST region
@JmsListener(
    destination = "order.queue",
    selector = "orderType = 'PREMIUM' AND region = 'US-EAST'"
)
public void receivePremiumUsEastOrders(Order order) {
    logger.info("Premium US-East order: {}", order.getOrderId());
}

// Only receive express orders over $1000
@JmsListener(
    destination = "order.queue",
    selector = "isExpress = TRUE AND orderAmount > 1000"
)
public void receiveExpressHighValueOrders(Order order) {
    logger.info("Express high-value order: {}", order.getOrderId());
}

// Exclude retried messages
@JmsListener(
    destination = "order.queue",
    selector = "retryCount = 0"
)
public void receiveFirstAttemptOnly(Order order) {
    logger.info("First attempt: {}", order.getOrderId());
}
```

**Selector syntax reference:**

| Expression | Example | Description |
|-----------|---------|-------------|
| `=` / `<>` | `orderType = 'PREMIUM'` | String equality/inequality |
| `>` `<` `>=` `<=` | `orderAmount > 1000` | Numeric comparison |
| `AND` `OR` `NOT` | `isExpress = TRUE AND region = 'US'` | Logical operators |
| `BETWEEN` | `JMSPriority BETWEEN 5 AND 9` | Range check |
| `IN` | `region IN ('US-EAST', 'US-WEST')` | Set membership |
| `LIKE` | `orderType LIKE 'PREM%'` | Pattern matching |
| `IS NULL` / `IS NOT NULL` | `traceId IS NOT NULL` | Null check |

**Important:** Selectors can only filter on **headers and properties**, not on the message body content.

### Passing Headers Through the Full Flow (Tracing Example)

A common use case is propagating trace/correlation IDs through a chain of services.

**Producer sets a traceId:**

```java
@Service
public class OrderService {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void placeOrder(Order order, String traceId) {
        jmsTemplate.convertAndSend("order.queue", order, message -> {
            message.setStringProperty("traceId", traceId);
            message.setStringProperty("source", "order-service");
            message.setLongProperty("createdAt", System.currentTimeMillis());
            return message;
        });
    }
}
```

**First consumer reads and forwards the traceId:**

```java
@Service
public class OrderProcessor {

    @Autowired
    private JmsTemplate jmsTemplate;

    @JmsListener(destination = "order.queue")
    public void processOrder(Order order, @Header("traceId") String traceId) {
        logger.info("[traceId={}] Processing order {}", traceId, order.getOrderId());

        // After processing, forward to the next queue with the same traceId
        jmsTemplate.convertAndSend("shipping.queue", order, message -> {
            message.setStringProperty("traceId", traceId);          // propagate trace
            message.setStringProperty("source", "order-processor"); // update source
            return message;
        });
    }
}
```

**Second consumer reads the same traceId:**

```java
@Service
public class ShippingService {

    @JmsListener(destination = "shipping.queue")
    public void handleShipping(
            Order order,
            @Header("traceId") String traceId,
            @Header("source") String source
    ) {
        logger.info("[traceId={}] Shipping order {} (from: {})",
                traceId, order.getOrderId(), source);
        shipOrder(order);
    }
}
```

### Summary: Which Approach to Use

| Approach | When to Use |
|----------|-------------|
| `@Header("name")` | You need 1-5 specific properties. Cleanest and most readable. |
| `@Header(name="x", required=false)` | Property may or may not exist. Avoids exceptions. |
| `@Headers Map<String, Object>` | You need access to many or all properties dynamically. |
| `Message message` (raw JMS) | You need full JMS API access (e.g., `propertyExists`, enumerating all properties, `session.recover()`). |
| Payload + `Message` together | You want the convenience of auto-deserialization plus full header access. |

---

## Integration with Spring Boot

### Step 1: Add Dependencies

Add the following dependencies to your `pom.xml`:

```xml
<dependencies>
    <!-- Spring Boot Starter for ActiveMQ -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-activemq</artifactId>
    </dependency>
    
    <!-- Optional: For embedded ActiveMQ -->
    <dependency>
        <groupId>org.apache.activemq</groupId>
        <artifactId>activemq-broker</artifactId>
    </dependency>
    
    <!-- For JSON message conversion -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
</dependencies>
```

For Gradle (`build.gradle`):

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-activemq'
    implementation 'org.apache.activemq:activemq-broker'
    implementation 'com.fasterxml.jackson.core:jackson-databind'
}
```

### Step 2: Configure ActiveMQ

**application.properties:**

```properties
# ActiveMQ Configuration
spring.activemq.broker-url=tcp://localhost:61616
spring.activemq.user=admin
spring.activemq.password=admin

# Use connection pooling
spring.activemq.pool.enabled=true
spring.activemq.pool.max-connections=10

# JMS Configuration
spring.jms.pub-sub-domain=false
# Set to true for Topic, false for Queue
```

**application.yml:**

```yaml
spring:
  activemq:
    broker-url: tcp://localhost:61616
    user: admin
    password: admin
    pool:
      enabled: true
      max-connections: 10
  jms:
    pub-sub-domain: false  # false for Queue, true for Topic
```

### Step 3: JMS Configuration Class

```java
package com.example.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;

@Configuration
@EnableJms
public class JmsConfig {

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(brokerUrl);
        return connectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        template.setMessageConverter(jacksonJmsMessageConverter());
        return template;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setConcurrency("3-10");
        factory.setMessageConverter(jacksonJmsMessageConverter());
        return factory;
    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
}
```

## Complete Example

### 1. Message Model

```java
package com.example.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Order implements Serializable {
    private String orderId;
    private String productName;
    private int quantity;
    private double price;
    private LocalDateTime orderDate;

    // Constructors
    public Order() {
    }

    public Order(String orderId, String productName, int quantity, double price) {
        this.orderId = orderId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.orderDate = LocalDateTime.now();
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", orderDate=" + orderDate +
                '}';
    }
}
```

### 2. Message Producer

```java
package com.example.service;

import com.example.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    private static final Logger logger = LoggerFactory.getLogger(OrderProducer.class);
    
    @Autowired
    private JmsTemplate jmsTemplate;
    
    private static final String ORDER_QUEUE = "order.queue";

    public void sendOrder(Order order) {
        try {
            logger.info("Sending order: {}", order);
            jmsTemplate.convertAndSend(ORDER_QUEUE, order);
            logger.info("Order sent successfully");
        } catch (Exception e) {
            logger.error("Error sending order", e);
            throw e;
        }
    }

    // Send to specific destination
    public void sendToQueue(String queueName, Order order) {
        jmsTemplate.convertAndSend(queueName, order);
        logger.info("Order sent to queue: {}", queueName);
    }
}
```

### 3. Message Consumer

```java
package com.example.service;

import com.example.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderConsumer.class);

    @JmsListener(destination = "order.queue")
    public void receiveOrder(Order order) {
        logger.info("Received order: {}", order);
        
        // Process the order
        processOrder(order);
    }

    private void processOrder(Order order) {
        // Business logic here
        logger.info("Processing order: {}", order.getOrderId());
        
        // Simulate processing
        try {
            Thread.sleep(1000);
            logger.info("Order {} processed successfully", order.getOrderId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Error processing order", e);
        }
    }

    // Multiple consumers for the same queue (load balancing)
    @JmsListener(destination = "order.queue", concurrency = "3-5")
    public void receiveOrderWithConcurrency(Order order) {
        logger.info("Consumer [{}] received order: {}", 
                    Thread.currentThread().getName(), order);
        processOrder(order);
    }
}
```

### 4. REST Controller

```java
package com.example.controller;

import com.example.model.Order;
import com.example.service.OrderProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderProducer orderProducer;

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody Order order) {
        try {
            // Generate order ID if not present
            if (order.getOrderId() == null || order.getOrderId().isEmpty()) {
                order.setOrderId(UUID.randomUUID().toString());
            }
            
            orderProducer.sendOrder(order);
            
            return ResponseEntity.ok("Order placed successfully with ID: " + order.getOrderId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error placing order: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        Order testOrder = new Order(
            UUID.randomUUID().toString(),
            "Test Product",
            5,
            99.99
        );
        
        orderProducer.sendOrder(testOrder);
        
        return ResponseEntity.ok("Test order sent");
    }
}
```

### 5. Main Application Class

```java
package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ActiveMQSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActiveMQSpringBootApplication.class, args);
    }
}
```

## Advanced Features

### 1. Topic (Publish-Subscribe)

**Configuration for Topic:**

```java
@Configuration
public class TopicConfig {

    @Bean
    public JmsListenerContainerFactory<?> topicListenerFactory(
            ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = 
            new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPubSubDomain(true);  // Enable Topic mode
        return factory;
    }
}
```

**Producer for Topic:**

```java
@Service
public class NotificationPublisher {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void publishNotification(String message) {
        jmsTemplate.setPubSubDomain(true);
        jmsTemplate.convertAndSend("notification.topic", message);
    }
}
```

**Consumer for Topic:**

```java
@Service
public class NotificationSubscriber {

    @JmsListener(destination = "notification.topic", 
                 containerFactory = "topicListenerFactory")
    public void subscribeNotification(String message) {
        System.out.println("Subscriber 1 received: " + message);
    }

    @JmsListener(destination = "notification.topic", 
                 containerFactory = "topicListenerFactory")
    public void subscribeNotification2(String message) {
        System.out.println("Subscriber 2 received: " + message);
    }
}
```

### 2. Message Priority

#### How Priority Works

JMS defines 10 priority levels (0-9). ActiveMQ groups them into two bands:

| Priority Range | Band | Description |
|---------------|------|-------------|
| 0-3 | **Low** (Normal) | Default messages, processed in FIFO order |
| 4 | **Default** | The default priority if none is set |
| 5-9 | **High** (Expedited) | Processed before low-priority messages |

**Key behavior:** Within the same priority level, messages follow FIFO (First-In-First-Out). Higher priority messages are delivered **before** lower priority messages, even if the lower priority message arrived first.

```
Queue state (arrival order):
  1. Order-A  (priority 4 - normal)
  2. Order-B  (priority 9 - highest)
  3. Order-C  (priority 1 - low)
  4. Order-D  (priority 9 - highest)

Consumer receives in this order:
  1. Order-B  (priority 9) ← arrived 2nd but consumed 1st
  2. Order-D  (priority 9) ← same priority as B, FIFO within level
  3. Order-A  (priority 4) ← normal priority
  4. Order-C  (priority 1) ← lowest priority, consumed last
```

#### Enabling Priority on the Broker

Priority-based ordering is **NOT enabled by default**. You must enable it on the broker in `activemq.xml`:

```xml
<broker>
    <destinationPolicy>
        <policyMap>
            <policyEntries>
                <policyEntry queue=">" prioritizedMessages="true" />
                <!-- Or for specific queues only -->
                <policyEntry queue="order.queue"
                             prioritizedMessages="true"
                             useCache="false"
                             expireMessagesPeriod="0"
                             queuePrefetch="1" />
            </policyEntries>
        </policyMap>
    </destinationPolicy>
</broker>
```

**Important:** Set `queuePrefetch="1"` when using priority. Without this, the consumer prefetches a batch of messages (default 1000), and priority ordering is only applied within that prefetched batch, not globally across the queue.

#### Sending Priority Messages in Spring Boot

```java
@Service
public class PriorityOrderProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    // Send with explicit priority
    public void sendHighPriorityOrder(Order order) {
        jmsTemplate.convertAndSend("order.queue", order, message -> {
            message.setJMSPriority(9);  // 0-9, where 9 is highest
            return message;
        });
    }

    public void sendLowPriorityOrder(Order order) {
        jmsTemplate.convertAndSend("order.queue", order, message -> {
            message.setJMSPriority(1);
            return message;
        });
    }

    // Send with dynamic priority based on business logic
    public void sendOrder(Order order) {
        int priority = calculatePriority(order);
        jmsTemplate.convertAndSend("order.queue", order, message -> {
            message.setJMSPriority(priority);
            // Also set as a custom property for selectors
            message.setIntProperty("orderPriority", priority);
            return message;
        });
    }

    private int calculatePriority(Order order) {
        if (order.getPrice() > 10000) return 9;    // VIP orders
        if (order.getPrice() > 1000) return 7;     // High value
        if (order.getPrice() > 100) return 5;      // Medium value
        return 4;                                    // Normal
    }
}
```

#### Consuming Priority Messages with Selectors

You can use **message selectors** to route high and low priority messages to different consumers:

```java
@Service
public class PriorityOrderConsumer {

    // Dedicated consumer for high-priority orders only
    @JmsListener(
        destination = "order.queue",
        selector = "JMSPriority >= 7"
    )
    public void receiveHighPriorityOrder(Order order) {
        logger.info("HIGH PRIORITY order received: {}", order.getOrderId());
        processUrgentOrder(order);
    }

    // Consumer for normal and low-priority orders
    @JmsListener(
        destination = "order.queue",
        selector = "JMSPriority < 7"
    )
    public void receiveNormalOrder(Order order) {
        logger.info("Normal order received: {}", order.getOrderId());
        processOrder(order);
    }
}
```

#### When to Use Priority Messages

| Use Case | Priority Level | Why |
|----------|---------------|-----|
| Payment failure alerts | 9 (Highest) | Must be processed immediately to avoid revenue loss |
| VIP customer orders | 8-9 | Business-critical customers need faster processing |
| Order cancellations | 7-8 | Time-sensitive, customer is waiting |
| Regular orders | 4 (Default) | Normal processing flow |
| Analytics/logging events | 1-2 | Can wait, no user impact |
| Batch report generation | 0 (Lowest) | Background task, no urgency |

#### Gotchas with Priority Messages

1. **Prefetch kills priority ordering** - If `prefetch=1000` (default), the consumer pulls 1000 messages at once. Priority is only respected within those 1000, not across the entire queue. Always set `prefetch=1` for strict priority ordering.
2. **Performance trade-off** - Priority ordering requires the broker to sort messages. This is slower than plain FIFO. Only enable it on queues that actually need it.
3. **Starvation risk** - If high-priority messages keep arriving, low-priority messages may never be consumed. Consider separate queues if this is a concern.
4. **Not a replacement for separate queues** - If you have fundamentally different processing needs (e.g., real-time vs batch), use separate queues instead of priorities.

---

### 3. Delayed/Scheduled Messages

#### How Delayed Messages Work

ActiveMQ's scheduler allows you to send a message now but have it delivered to consumers **only after a specified delay**. The message is stored by the broker's scheduler and is invisible to consumers until the delay expires.

```
Producer sends message at T=0 with delay=30s
        │
        ▼
Broker receives message, stores in scheduler
        │
        ▼
T=0 to T=30s: Message is NOT visible to any consumer
        │
        ▼
T=30s: Broker moves message to the destination queue/topic
        │
        ▼
Consumer receives the message
```

#### Enabling the Scheduler on the Broker

The scheduler must be enabled in `activemq.xml`:

```xml
<broker xmlns="http://activemq.apache.org/schema/core"
        brokerName="localhost"
        schedulerSupport="true">
    <!-- ... -->
</broker>
```

Or via the broker URL for embedded brokers:

```properties
spring.activemq.broker-url=vm://localhost?broker.persistent=true&broker.schedulerSupport=true
```

#### Scheduler Properties

ActiveMQ supports four scheduling properties:

| Property | Type | Description |
|----------|------|-------------|
| `AMQ_SCHEDULED_DELAY` | long | Delay in milliseconds before first delivery |
| `AMQ_SCHEDULED_PERIOD` | long | Interval in milliseconds between repeated deliveries |
| `AMQ_SCHEDULED_REPEAT` | int | Number of times to repeat delivery |
| `AMQ_SCHEDULED_CRON` | String | Cron expression for scheduled delivery |

#### Sending Delayed Messages

```java
import org.apache.activemq.ScheduledMessage;

@Service
public class ScheduledMessageProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    // Simple delay: deliver after 30 seconds
    public void sendDelayedOrder(Order order, long delayInMillis) {
        jmsTemplate.convertAndSend("order.queue", order, message -> {
            message.setLongProperty(
                ScheduledMessage.AMQ_SCHEDULED_DELAY,
                delayInMillis
            );
            return message;
        });
    }

    // Repeated delivery: send every 60 seconds, 5 times
    public void sendRepeatingNotification(String notification) {
        jmsTemplate.convertAndSend("notification.queue", notification, message -> {
            message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 10000);   // first delivery after 10s
            message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD, 60000);  // repeat every 60s
            message.setIntProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT, 5);       // repeat 5 times
            return message;
        });
    }

    // Cron-based delivery: every day at 9 AM
    public void sendDailyReport(String report) {
        jmsTemplate.convertAndSend("report.queue", report, message -> {
            message.setStringProperty(
                ScheduledMessage.AMQ_SCHEDULED_CRON,
                "0 9 * * *"   // minute=0, hour=9, every day
            );
            return message;
        });
    }

    // Combined: delay + repeat (start after 5 min, repeat every hour, 24 times)
    public void sendPeriodicHealthCheck(String payload) {
        jmsTemplate.convertAndSend("healthcheck.queue", payload, message -> {
            message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 300000);    // 5 min initial delay
            message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD, 3600000);  // every 1 hour
            message.setIntProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT, 24);        // 24 repetitions
            return message;
        });
    }
}
```

#### When to Use Delayed Messages

| Use Case | Delay | Why |
|----------|-------|-----|
| **Retry after failure** | 5s, 30s, 5min (exponential) | Give downstream service time to recover before retrying |
| **Order timeout** | 30 minutes | If payment not received in 30 min, cancel the order |
| **Email follow-up** | 24 hours | Send "Did you forget something?" email after cart abandonment |
| **Rate limiting** | 1-5 seconds | Spread out API calls to avoid hitting rate limits |
| **Scheduled tasks** | Cron-based | Daily reports, periodic cleanup, scheduled notifications |
| **Cooling period** | 5-15 minutes | Debounce rapid events (e.g., user settings changes) |
| **SLA monitoring** | Based on SLA window | If task not completed within SLA, trigger escalation |

#### Real-World Example: Order Timeout with Delayed Message

```java
@Service
public class OrderTimeoutService {

    @Autowired
    private JmsTemplate jmsTemplate;

    // When order is placed, schedule a timeout check
    public void scheduleOrderTimeoutCheck(String orderId) {
        jmsTemplate.convertAndSend("order.timeout.queue", orderId, message -> {
            // Check payment status after 30 minutes
            message.setLongProperty(
                ScheduledMessage.AMQ_SCHEDULED_DELAY,
                30 * 60 * 1000  // 30 minutes
            );
            return message;
        });
    }
}

@Service
public class OrderTimeoutConsumer {

    @Autowired
    private OrderRepository orderRepository;

    // This message arrives 30 minutes after order was placed
    @JmsListener(destination = "order.timeout.queue")
    public void checkOrderTimeout(String orderId) {
        Order order = orderRepository.findById(orderId);

        if (order.getStatus() == OrderStatus.PENDING_PAYMENT) {
            // Payment not received within 30 minutes - cancel order
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            logger.info("Order {} cancelled due to payment timeout", orderId);
        } else {
            // Payment was already received - do nothing
            logger.info("Order {} already paid, timeout check passed", orderId);
        }
    }
}
```

#### Real-World Example: Exponential Backoff Retry with Delay

```java
@Service
public class ExternalApiConsumer {

    @Autowired
    private JmsTemplate jmsTemplate;

    @JmsListener(destination = "api.call.queue")
    public void callExternalApi(Message message) throws JMSException {
        String payload = ((TextMessage) message).getText();
        int retryCount = message.getIntProperty("retryCount");

        try {
            externalApiClient.call(payload);
        } catch (ApiUnavailableException e) {
            if (retryCount >= 5) {
                logger.error("API call failed after 5 retries, sending to DLQ");
                jmsTemplate.convertAndSend("DLQ.api.call.queue", payload);
                return;
            }

            // Re-send with increasing delay: 5s, 25s, 125s, 625s, 3125s
            long delay = (long) Math.pow(5, retryCount + 1) * 1000;
            jmsTemplate.convertAndSend("api.call.queue", payload, msg -> {
                msg.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
                msg.setIntProperty("retryCount", retryCount + 1);
                return msg;
            });
            logger.warn("API unavailable, retry {} scheduled in {}ms", retryCount + 1, delay);
        }
    }
}
```

#### When Does the Consumer Actually Receive a Delayed Message?

The consumer receives the message **only after the delay has elapsed**. The timeline:

1. **T=0** - Producer sends message with `AMQ_SCHEDULED_DELAY=60000` (60 seconds)
2. **T=0** - Broker acknowledges receipt, stores in internal scheduler store
3. **T=0 to T=60s** - Message is **not in the queue**. Consumers cannot see it. It does not show up in the ActiveMQ web console queue view.
4. **T=60s** - Broker's scheduler fires, moves the message into the destination queue
5. **T=60s+** - Message is now visible. The next available consumer picks it up.

**If no consumer is running at T=60s**, the message stays in the queue (as a normal message) until a consumer connects and consumes it. The delay only controls when the message **enters** the queue, not when it must be consumed.

### 4. Request-Reply Pattern

**Sender:**

```java
public Order sendAndReceive(Order order) {
    return (Order) jmsTemplate.convertSendAndReceive("request.queue", order);
}
```

**Receiver:**

```java
@JmsListener(destination = "request.queue")
@SendTo("response.queue")
public Order processRequest(Order order) {
    // Process and return response
    order.setOrderId("PROCESSED-" + order.getOrderId());
    return order;
}
```

### 5. Error Handling

```java
@Service
public class OrderConsumerWithErrorHandling {

    @JmsListener(destination = "order.queue")
    public void receiveOrder(Order order) {
        try {
            processOrder(order);
        } catch (Exception e) {
            // Send to Dead Letter Queue
            sendToDeadLetterQueue(order, e);
        }
    }

    private void sendToDeadLetterQueue(Order order, Exception e) {
        // Log error and send to DLQ
        logger.error("Failed to process order: {}", order.getOrderId(), e);
        jmsTemplate.convertAndSend("DLQ.order.queue", order);
    }
}
```

## Best Practices

### 1. Connection Pooling
Always enable connection pooling for better performance:
```properties
spring.activemq.pool.enabled=true
spring.activemq.pool.max-connections=10
```

### 2. Message Acknowledgement
- **AUTO_ACKNOWLEDGE**: Automatic acknowledgment (default)
- **CLIENT_ACKNOWLEDGE**: Manual acknowledgment
- **DUPS_OK_ACKNOWLEDGE**: Allows duplicate messages

### 3. Transaction Management

```java
@JmsListener(destination = "order.queue")
@Transactional
public void receiveOrderTransactional(Order order) {
    // If exception occurs, message will be redelivered
    processOrder(order);
    saveToDatabase(order);
}
```

### 4. Message Selectors

```java
@JmsListener(
    destination = "order.queue",
    selector = "priority > 5"
)
public void receiveHighPriorityOrders(Order order) {
    // Only receives messages where priority > 5
    processOrder(order);
}
```

### 5. Monitoring and Management
- Use ActiveMQ Web Console: `http://localhost:8161/admin`
- Default credentials: admin/admin
- Monitor queues, topics, connections, and messages

### 6. Production Considerations

```properties
# Enable persistent messaging
spring.jms.template.delivery-mode=persistent

# Set time-to-live for messages (milliseconds)
spring.jms.template.time-to-live=3600000

# Set redelivery policy
spring.activemq.pool.max-connections=50
spring.activemq.pool.idle-timeout=30000

# Enable failover
spring.activemq.broker-url=failover:(tcp://broker1:61616,tcp://broker2:61616)
```

### 7. Security Configuration

```java
@Bean
public ConnectionFactory connectionFactory() {
    ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
    factory.setBrokerURL(brokerUrl);
    factory.setUserName("admin");
    factory.setPassword("admin");
    factory.setTrustAllPackages(false);
    factory.setTrustedPackages(Arrays.asList("com.example.model"));
    return factory;
}
```

## Testing

### Unit Test Example

```java
@SpringBootTest
@TestPropertySource(properties = {
    "spring.activemq.broker-url=vm://localhost?broker.persistent=false"
})
class OrderProducerTest {

    @Autowired
    private OrderProducer orderProducer;

    @Test
    void testSendOrder() {
        Order order = new Order("TEST-001", "Test Product", 1, 10.0);
        
        assertDoesNotThrow(() -> orderProducer.sendOrder(order));
    }
}
```

## Troubleshooting

### Common Issues:

1. **Connection Refused**: Ensure ActiveMQ broker is running
2. **Serialization Error**: Check message converter configuration
3. **Consumer Not Receiving**: Verify queue names and listener configuration
4. **Memory Issues**: Configure max page size and producer flow control

### Useful Commands:

```bash
# Start ActiveMQ
./bin/activemq start

# Stop ActiveMQ
./bin/activemq stop

# Check status
./bin/activemq status
```

## Conclusion

ActiveMQ with Spring Boot provides a robust solution for implementing message-driven architectures. The combination offers:
- Easy configuration through Spring Boot auto-configuration
- Flexible messaging patterns (Queue and Topic)
- Reliable message delivery
- Scalability through multiple consumers
- Transaction support

This guide covers the fundamentals and should give you a solid foundation to build message-driven applications using ActiveMQ and Spring Boot.

## Additional Resources

- [Apache ActiveMQ Documentation](https://activemq.apache.org/documentation)
- [Spring JMS Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#jms)
- [Spring Boot ActiveMQ Starter](https://docs.spring.io/spring-boot/docs/current/reference/html/messaging.html#messaging.jms.activemq)
