# Kotlin Features Over Java: Complete Syntax Guide

This document provides a comprehensive overview of Kotlin's features and improvements over Java, with detailed syntax examples and explanations.

## Table of Contents
1. [Null Safety](#null-safety)
2. [Type Inference](#type-inference)
3. [Val vs Var (Immutable vs Mutable)](#val-vs-var-immutable-vs-mutable)
4. [Data Classes](#data-classes)
5. [Extension Functions](#extension-functions)
6. [Smart Casts](#smart-casts)
7. [String Templates](#string-templates)
8. [Default and Named Arguments](#default-and-named-arguments)
9. [When Expression](#when-expression)
10. [Range Expressions](#range-expressions)
11. [Lambda Expressions](#lambda-expressions)
12. [Higher-Order Functions](#higher-order-functions)
13. [Companion Objects](#companion-objects)
14. [Sealed Classes](#sealed-classes)
15. [Object Declarations](#object-declarations)
16. [Delegation](#delegation)
17. [Coroutines](#coroutines)
18. [Properties and Fields](#properties-and-fields)
19. [Primary Constructors](#primary-constructors)
20. [Operator Overloading](#operator-overloading)
21. [Scope Functions](#scope-functions)
22. [Collections and Stream Operations](#collections-and-stream-operations)
23. [Interfaces and Abstract Classes](#interfaces-and-abstract-classes)
24. [Enum Classes](#enum-classes)
25. [Annotations](#annotations)
26. [Generics and Variance](#generics-and-variance)
27. [Type Checks and Casts](#type-checks-and-casts)
28. [Exception Handling](#exception-handling)
29. [Visibility Modifiers](#visibility-modifiers)
30. [Nested and Inner Classes](#nested-and-inner-classes)
31. [Object Expressions vs Object Declarations](#object-expressions-vs-object-declarations)
32. [Java Interoperability](#java-interoperability)

---

## Null Safety

**Explanation:** Kotlin's type system distinguishes between nullable and non-nullable types, eliminating NullPointerException at compile time.

### Java
```java
String name = null;  // Can cause NPE
int length = name.length();  // NullPointerException at runtime
```

### Kotlin
```kotlin
// Non-nullable type
var name: String = "John"
name = null  // Compilation error

// Nullable type
var nullableName: String? = null  // OK
nullableName = "Jane"  // OK

// Safe call operator
val length = nullableName?.length  // Returns null if nullableName is null

// Elvis operator (default value)
val len = nullableName?.length ?: 0  // Returns 0 if nullableName is null

// Non-null assertion
val forcedLength = nullableName!!.length  // Throws NPE if null

// Safe casting
val str: String? = nullableName as? String  // Returns null if cast fails
```

**The !! Operator (Non-Null Assertion Operator):**

The `!!` operator is called the "not-null assertion operator" or "double-bang operator". It converts any nullable type to a non-nullable type and throws a `NullPointerException` if the value is actually null.

```kotlin
// Usage of !! operator
var name: String? = "John"
val length: Int = name!!.length  // OK, returns 4

name = null
val length2: Int = name!!.length  // Throws KotlinNullPointerException

// When to use !!:
// 1. When you're 100% certain the value is not null
val user: User? = getUserFromDatabase()
val userName = user!!.name  // Only if you're absolutely sure user exists

// 2. When you want to fail fast if null
fun process(data: String?) {
    val processedData = data!!.uppercase()  // Fail immediately if null
    // ... rest of processing
}

// When NOT to use !!:
// !! should be used sparingly. Better alternatives:

// Instead of:
val length = name!!.length

// Use safe call:
val length = name?.length  // Returns null instead of crashing

// Use Elvis operator:
val length = name?.length ?: 0  // Provide default value

// Use let with safe call:
name?.let { 
    val length = it.length
    // Use length safely
}

// Use requireNotNull or checkNotNull:
val name: String = requireNotNull(nullableName) { "Name must not be null" }
val length = name.length  // name is now non-nullable

// Multiple !! in a chain (BAD PRACTICE):
val result = user!!.address!!.city!!.name  // Don't do this!
// If NPE occurs, hard to tell which was null

// Better approach:
val result = user?.address?.city?.name  // Safe, returns null if any is null
```

---

## Type Inference

**Explanation:** Kotlin can automatically infer types, reducing boilerplate code while maintaining type safety.

### Java
```java
String name = "John";
List<String> list = new ArrayList<String>();
Map<String, Integer> map = new HashMap<String, Integer>();
```

### Kotlin
```kotlin
// Type inference
val name = "John"  // Inferred as String
val list = listOf("A", "B", "C")  // Inferred as List<String>
val map = mapOf("key" to 1)  // Inferred as Map<String, Int>

// Explicit types (when needed)
val explicitName: String = "John"
val number: Int = 42
```

---

## Val vs Var (Immutable vs Mutable)

**Explanation:** Kotlin distinguishes between immutable (`val`) and mutable (`var`) variables, promoting immutability for safer code.

### Java
```java
// Java has 'final' keyword for immutability
final String name = "John";
name = "Jane";  // Compilation error

String age = "30";
age = "31";  // OK

// Final only prevents reassignment, not mutation
final List<String> list = new ArrayList<>();
list.add("item");  // OK - list itself is mutable
list = new ArrayList<>();  // Error - can't reassign

// Immutable collections require special handling
List<String> immutableList = Collections.unmodifiableList(list);
```

### Kotlin
```kotlin
// val - read-only (immutable reference)
val name = "John"
name = "Jane"  // Compilation error: Val cannot be reassigned

// var - mutable (can be reassigned)
var age = 30
age = 31  // OK

// val with mutable collection
val list = mutableListOf("a", "b")
list.add("c")  // OK - list contents are mutable
list = mutableListOf("x")  // Error - can't reassign list itself

// val with immutable collection
val immutableList = listOf("a", "b", "c")
immutableList.add("d")  // Error - no add method on immutable list
immutableList = listOf("x")  // Error - can't reassign

// Key differences visualized:
val x = 10        // Read-only reference, value cannot be reassigned
var y = 10        // Mutable reference, value can be reassigned

// Properties in classes
class Person {
    val name: String = "John"  // Read-only property (only getter)
    var age: Int = 30          // Mutable property (getter and setter)
    
    fun updateName() {
        // name = "Jane"  // Error: Val cannot be reassigned
        age = 31  // OK
    }
}

// val doesn't mean deeply immutable
data class Address(var street: String)
val address = Address("123 Main St")
address.street = "456 Oak Ave"  // OK - val prevents reassigning address, not its contents
// address = Address("New")  // Error - can't reassign

// Late-initialized val (must be initialized before use)
class Example {
    val name: String
    
    init {
        name = "Initialized in init block"  // OK - one-time initialization
    }
    
    fun changeName() {
        // name = "New name"  // Error: Val cannot be reassigned
    }
}

// Custom getter with val (computed property)
class Rectangle(val width: Int, val height: Int) {
    val area: Int  // No backing field, computed each time
        get() = width * height
}

val rect = Rectangle(5, 10)
println(rect.area)  // 50

// val in function parameters (parameters are always val)
fun greet(name: String) {  // name is implicitly val
    // name = "Changed"  // Error: cannot reassign parameter
    println("Hello, $name")
}

// val in loops
for (i in 1..5) {  // i is val
    // i = 10  // Error: cannot reassign
    println(i)
}

val numbers = listOf(1, 2, 3)
numbers.forEach { num ->  // num is val
    // num = 10  // Error
    println(num)
}

// Destructuring with val and var
data class Point(val x: Int, val y: Int)
val point = Point(10, 20)

val (x1, y1) = point  // Destructure to val
// x1 = 15  // Error

var (x2, y2) = point  // Destructure to var
x2 = 15  // OK
```

### Best Practices

```kotlin
// PREFER val over var (immutability by default)
// ✅ Good - use val when possible
val name = "John"
val age = 30
val users = getUsers()

// ❌ Avoid - don't use var unnecessarily
var name = "John"  // If you never reassign, use val

// Use var only when you need to reassign
var counter = 0
for (i in 1..10) {
    counter += i  // var needed here
}

// Mutable collections with val
val list = mutableListOf<String>()  // val reference, mutable contents
list.add("item")  // OK
list.add("another")  // OK

// Immutable collections (preferred when possible)
val names = listOf("John", "Jane", "Bob")  // Truly immutable

// When to use var:
// 1. Accumulators and counters
var sum = 0
numbers.forEach { sum += it }

// 2. State that changes over time
var currentState = State.LOADING
// ... later
currentState = State.SUCCESS

// 3. Loops with changing variables
var i = 0
while (i < 10) {
    println(i)
    i++
}

// 4. Building complex objects
var builder = StringBuilder()
builder.append("Hello")
builder.append(" World")

// When to use val:
// 1. Constants and configuration
val MAX_USERS = 100
val API_KEY = "abc123"

// 2. Immutable data
val user = User("John", 30)
val result = calculateResult()

// 3. Collections that won't be reassigned
val numbers = listOf(1, 2, 3, 4, 5)
val userMap = mapOf("id" to 123)

// 4. Default choice (use val unless you need var)
val name = getName()
val age = getAge()
```

### Val and Var with Nullable Types

```kotlin
// Combining with nullability
val nonNullName: String = "John"  // Cannot be null, cannot be reassigned
var nullableName: String? = null  // Can be null, can be reassigned
nullableName = "Jane"  // OK

val nonNullButReassignable: String? = "John"  // Can be null, cannot be reassigned
// nonNullButReassignable = null  // Error: Val cannot be reassigned

var nonNullButMutable: String = "John"  // Cannot be null, can be reassigned
nonNullButMutable = "Jane"  // OK
// nonNullButMutable = null  // Error: Type mismatch

// Smart casts work differently with var
var nullableVar: String? = "Hello"
if (nullableVar != null) {
    // Smart cast might not work if var can be modified by another thread
    println(nullableVar.length)  // May require explicit check
}

val nullableVal: String? = "Hello"
if (nullableVal != null) {
    println(nullableVal.length)  // Smart cast works reliably
}
```

### Comparison Table

| Feature | val | var |
|---------|-----|-----|
| **Reassignment** | ❌ Not allowed | ✅ Allowed |
| **Mutability** | Reference immutable, content can be mutable | Reference and content mutable |
| **Java Equivalent** | `final` | Regular variable |
| **Getter** | ✅ Generated | ✅ Generated |
| **Setter** | ❌ Not generated | ✅ Generated |
| **Thread Safety** | More predictable | Less predictable |
| **Smart Casts** | More reliable | Less reliable |
| **Default Choice** | ✅ Preferred | Use when necessary |
| **Performance** | Slightly better (compiler optimizations) | Standard |

---

## Data Classes

**Explanation:** Data classes automatically generate equals(), hashCode(), toString(), copy(), and componentN() functions.

### Java
```java
public class Person {
    private String name;
    private int age;
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public String getName() { return name; }
    public int getAge() { return age; }
    
    @Override
    public boolean equals(Object o) {
        // Boilerplate code...
    }
    
    @Override
    public int hashCode() {
        // Boilerplate code...
    }
    
    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }
}
```

### Kotlin
```kotlin
// Data class - all boilerplate generated automatically
data class Person(val name: String, val age: Int)

// Usage
val person1 = Person("John", 30)
val person2 = Person("John", 30)

println(person1)  // Person(name=John, age=30)
println(person1 == person2)  // true (structural equality)

// Copy with modifications
val person3 = person1.copy(age = 31)  // Person(name=John, age=31)

// Destructuring
val (name, age) = person1
println("$name is $age years old")
```

---

## Extension Functions

**Explanation:** Add new functions to existing classes without modifying their source code or using inheritance.

### Java
```java
// Utility class approach
public class StringUtils {
    public static boolean isPalindrome(String str) {
        return str.equals(new StringBuilder(str).reverse().toString());
    }
}

// Usage
StringUtils.isPalindrome("racecar");
```

### Kotlin
```kotlin
// Extension function
fun String.isPalindrome(): Boolean {
    return this == this.reversed()
}

// Usage - called as if it's a member function
"racecar".isPalindrome()  // true
"hello".isPalindrome()    // false

// Extension property
val String.lastChar: Char
    get() = this[length - 1]

"Hello".lastChar  // 'o'

// Extension function with generics
fun <T> List<T>.secondOrNull(): T? {
    return if (this.size >= 2) this[1] else null
}

listOf(1, 2, 3).secondOrNull()  // 2
```

---

## Smart Casts

**Explanation:** Kotlin automatically casts types after checking them, eliminating explicit casting.

### Java
```java
Object obj = "Hello";
if (obj instanceof String) {
    String str = (String) obj;  // Explicit cast required
    System.out.println(str.length());
}
```

### Kotlin
```kotlin
val obj: Any = "Hello"
if (obj is String) {
    // obj is automatically cast to String
    println(obj.length)  // No explicit cast needed
}

// Smart cast with when
when (obj) {
    is String -> println(obj.length)
    is Int -> println(obj * 2)
    else -> println("Unknown type")
}

// Smart cast with elvis operator
val length = (obj as? String)?.length ?: 0
```

---

## String Templates

**Explanation:** Embed expressions directly in strings without concatenation.

### Java
```java
String name = "John";
int age = 30;
String message = "Hello, " + name + "! You are " + age + " years old.";
System.out.println("Result: " + (5 + 3));
```

### Kotlin
```kotlin
val name = "John"
val age = 30

// Simple string template
val message = "Hello, $name! You are $age years old."

// Expression in string template
println("Result: ${5 + 3}")  // Result: 8

// Complex expressions
val person = Person("Jane", 25)
println("Person: ${person.name} is ${person.age} years old")

// Multiline strings with templates
val multiline = """
    Name: $name
    Age: $age
    Adult: ${age >= 18}
""".trimIndent()
```

---

## Default and Named Arguments

**Explanation:** Functions can have default parameter values, and you can call them with named arguments for clarity.

### Java
```java
// Method overloading for default values
public void connect(String host) {
    connect(host, 8080);
}

public void connect(String host, int port) {
    connect(host, port, 30);
}

public void connect(String host, int port, int timeout) {
    // Implementation
}
```

### Kotlin
```kotlin
// Default arguments
fun connect(host: String, port: Int = 8080, timeout: Int = 30) {
    println("Connecting to $host:$port with timeout $timeout")
}

// Usage
connect("localhost")  // Uses default port and timeout
connect("localhost", 9090)  // Custom port, default timeout
connect("localhost", timeout = 60)  // Named argument, default port

// Named arguments for clarity
fun createUser(
    name: String,
    email: String,
    age: Int = 18,
    isActive: Boolean = true
) {
    // Implementation
}

createUser(
    name = "John",
    email = "john@example.com",
    age = 25
)
```

---

## When Expression

**Explanation:** A more powerful and concise replacement for switch statements.

### Java
```java
int result;
switch (value) {
    case 1:
        result = 10;
        break;
    case 2:
    case 3:
        result = 20;
        break;
    default:
        result = 0;
}
```

### Kotlin
```kotlin
// When as expression (returns a value)
val result = when (value) {
    1 -> 10
    2, 3 -> 20  // Multiple values
    in 4..10 -> 30  // Range
    else -> 0
}

// When with type checking
when (obj) {
    is String -> println("String of length ${obj.length}")
    is Int -> println("Integer: $obj")
    is List<*> -> println("List with ${obj.size} elements")
    else -> println("Unknown type")
}

// When without argument
val age = 25
when {
    age < 18 -> println("Minor")
    age in 18..64 -> println("Adult")
    age >= 65 -> println("Senior")
}

// When with complex conditions
when (x) {
    parseInt(s) -> println("Encoded as number")
    else -> println("Not a number")
}
```

---

## Range Expressions

**Explanation:** Kotlin provides ranges for expressing sequences and checking if values are within bounds.

### Java
```java
for (int i = 1; i <= 10; i++) {
    System.out.println(i);
}

if (age >= 18 && age <= 65) {
    // Adult
}
```

### Kotlin
```kotlin
// Range creation
val oneToTen = 1..10  // Inclusive range: 1, 2, 3, ..., 10
val oneToNine = 1 until 10  // Exclusive end: 1, 2, 3, ..., 9
val tenToOne = 10 downTo 1  // Descending: 10, 9, 8, ..., 1
val evens = 2..10 step 2  // Step: 2, 4, 6, 8, 10

// Iteration
for (i in 1..10) {
    println(i)
}

// Check membership
val age = 25
if (age in 18..65) {
    println("Adult")
}

// Character ranges
for (c in 'a'..'z') {
    print(c)
}

// Range with collections
val list = listOf("a", "b", "c", "d")
for (i in list.indices) {
    println("$i: ${list[i]}")
}
```

---

## Lambda Expressions

**Explanation:** Concise syntax for anonymous functions, making functional programming more accessible.

### Java
```java
// Anonymous class
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
numbers.forEach(new Consumer<Integer>() {
    @Override
    public void accept(Integer n) {
        System.out.println(n * 2);
    }
});

// Java 8 lambda
numbers.forEach(n -> System.out.println(n * 2));
```

### Kotlin
```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

// Lambda syntax
numbers.forEach { n -> println(n * 2) }

// Implicit 'it' parameter for single parameter lambdas
numbers.forEach { println(it * 2) }

// Lambda with multiple parameters
val pairs = listOf(Pair(1, "one"), Pair(2, "two"))
pairs.forEach { (num, word) -> println("$num: $word") }

// Lambda as last parameter (trailing lambda)
fun performOperation(x: Int, operation: (Int) -> Int): Int {
    return operation(x)
}

val result = performOperation(5) { it * 2 }  // 10

// Multi-line lambda
val doubled = numbers.map {
    val temp = it * 2
    temp + 1
}

// Lambda with receiver
val sb = StringBuilder()
sb.apply {
    append("Hello")
    append(" ")
    append("World")
}
```

---

## Higher-Order Functions

**Explanation:** Functions that take other functions as parameters or return functions.

### Java
```java
// Java 8 example
public static List<Integer> filter(List<Integer> list, Predicate<Integer> predicate) {
    return list.stream()
               .filter(predicate)
               .collect(Collectors.toList());
}

List<Integer> evens = filter(numbers, n -> n % 2 == 0);
```

### Kotlin
```kotlin
// Higher-order function definition
fun <T> List<T>.customFilter(predicate: (T) -> Boolean): List<T> {
    val result = mutableListOf<T>()
    for (item in this) {
        if (predicate(item)) {
            result.add(item)
        }
    }
    return result
}

// Usage
val numbers = listOf(1, 2, 3, 4, 5)
val evens = numbers.customFilter { it % 2 == 0 }

// Function returning a function
fun makeMultiplier(factor: Int): (Int) -> Int {
    return { number -> number * factor }
}

val triple = makeMultiplier(3)
println(triple(5))  // 15

// Built-in higher-order functions
val doubled = numbers.map { it * 2 }
val sum = numbers.reduce { acc, n -> acc + n }
val filtered = numbers.filter { it > 2 }

// Function composition
fun compose(f: (Int) -> Int, g: (Int) -> Int): (Int) -> Int {
    return { x -> f(g(x)) }
}

val addOne = { x: Int -> x + 1 }
val multiplyByTwo = { x: Int -> x * 2 }
val addOneThenDouble = compose(multiplyByTwo, addOne)
println(addOneThenDouble(5))  // 12
```

---

## Companion Objects

**Explanation:** Kotlin's replacement for static members, with additional capabilities.

### Java
```java
public class User {
    private static int count = 0;
    
    public static User create(String name) {
        count++;
        return new User(name);
    }
    
    public static int getCount() {
        return count;
    }
}
```

### Kotlin
```kotlin
class User(val name: String) {
    companion object {
        private var count = 0
        
        fun create(name: String): User {
            count++
            return User(name)
        }
        
        fun getCount() = count
        
        // Companion object can have properties
        const val MAX_USERS = 1000
    }
}

// Usage
val user = User.create("John")
println(User.getCount())
println(User.MAX_USERS)

// Companion object with name and interface implementation
interface Factory<T> {
    fun create(): T
}

class MyClass {
    companion object MyFactory : Factory<MyClass> {
        override fun create(): MyClass = MyClass()
    }
}

val factory: Factory<MyClass> = MyClass.MyFactory
```

---

## Sealed Classes

**Explanation:** Restricted class hierarchies where all subclasses are known at compile time, perfect for representing state.

### Java
```java
// Abstract class with subclasses
public abstract class Result {
    public static class Success extends Result {
        public final String data;
        public Success(String data) { this.data = data; }
    }
    
    public static class Error extends Result {
        public final String message;
        public Error(String message) { this.message = message; }
    }
}

// Requires else case even if all types covered
Result result = getData();
if (result instanceof Result.Success) {
    String data = ((Result.Success) result).data;
} else if (result instanceof Result.Error) {
    String message = ((Result.Error) result).message;
}
```

### Kotlin
```kotlin
// Sealed class - all subclasses must be in same file
sealed class Result {
    data class Success(val data: String) : Result()
    data class Error(val message: String) : Result()
    object Loading : Result()
}

// When expression is exhaustive - no else needed
fun handleResult(result: Result) = when (result) {
    is Result.Success -> println("Data: ${result.data}")
    is Result.Error -> println("Error: ${result.message}")
    is Result.Loading -> println("Loading...")
}

// Sealed interfaces (Kotlin 1.5+)
sealed interface Action {
    data class Click(val x: Int, val y: Int) : Action
    data class Swipe(val direction: String) : Action
    object Refresh : Action
}

// Nested sealed hierarchies
sealed class NetworkResult {
    data class Success(val data: String) : NetworkResult()
    
    sealed class Failure : NetworkResult() {
        data class HttpError(val code: Int) : Failure()
        data class NetworkError(val exception: Exception) : Failure()
        object Timeout : Failure()
    }
}
```

---

## Object Declarations

**Explanation:** Singleton pattern implementation and anonymous objects.

### Java
```java
// Singleton pattern
public class Database {
    private static Database instance;
    
    private Database() {}
    
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
}

// Anonymous class
button.addClickListener(new ClickListener() {
    @Override
    public void onClick() {
        System.out.println("Clicked");
    }
});
```

### Kotlin
```kotlin
// Object declaration (singleton)
object Database {
    var url: String = "localhost"
    
    fun connect() {
        println("Connecting to $url")
    }
}

// Usage
Database.connect()
Database.url = "127.0.0.1"

// Object expression (anonymous object)
val clickListener = object : ClickListener {
    override fun onClick() {
        println("Clicked")
    }
}

// Object expression without supertype
val obj = object {
    val x = 10
    val y = 20
}
println(obj.x + obj.y)

// Object expression with multiple interfaces
val handler = object : MouseListener, KeyListener {
    override fun mouseClicked() { }
    override fun keyPressed() { }
}
```

---

## Delegation

**Explanation:** Kotlin provides first-class delegation support for both class and property delegation.

### Java
```java
// Manual delegation
public class Manager implements Worker {
    private Worker worker;
    
    public Manager(Worker worker) {
        this.worker = worker;
    }
    
    @Override
    public void work() {
        worker.work();
    }
}
```

### Kotlin
```kotlin
// Class delegation
interface Worker {
    fun work()
    fun takeBreak()
}

class Employee : Worker {
    override fun work() = println("Working...")
    override fun takeBreak() = println("Taking break...")
}

// Delegate implementation to another object
class Manager(worker: Worker) : Worker by worker {
    // Can override specific methods if needed
    override fun work() {
        println("Manager delegating:")
        // Can't call super, but can create new instance if needed
    }
}

val employee = Employee()
val manager = Manager(employee)
manager.takeBreak()  // Delegated to employee

// Property delegation - Lazy
val lazyValue: String by lazy {
    println("Computing...")
    "Hello"
}
println(lazyValue)  // Computing... Hello
println(lazyValue)  // Hello (no computing)

// Observable property
class User {
    var name: String by Delegates.observable("Initial") { prop, old, new ->
        println("${prop.name}: $old -> $new")
    }
}

val user = User()
user.name = "John"  // name: Initial -> John

// Vetoable property
var age: Int by Delegates.vetoable(0) { _, oldValue, newValue ->
    newValue >= 0  // Only allow non-negative values
}

// Map delegation
class UserData(map: Map<String, Any?>) {
    val name: String by map
    val age: Int by map
}

val userData = UserData(mapOf("name" to "John", "age" to 30))
println(userData.name)  // John
```

---

## Coroutines

**Explanation:** Lightweight concurrency framework for asynchronous programming.

### Java
```java
// Traditional callback hell
public void fetchData(Callback<String> callback) {
    new Thread(() -> {
        String data = downloadData();
        runOnUiThread(() -> callback.onSuccess(data));
    }).start();
}

// Or with CompletableFuture
CompletableFuture.supplyAsync(() -> downloadData())
    .thenApply(data -> processData(data))
    .thenAccept(result -> updateUI(result));
```

### Kotlin
```kotlin
import kotlinx.coroutines.*

// Basic coroutine launch
fun main() = runBlocking {
    launch {
        delay(1000L)
        println("World!")
    }
    println("Hello,")
}

// Suspend function
suspend fun fetchData(): String {
    delay(1000L)  // Non-blocking delay
    return "Data from server"
}

// Async/await pattern
suspend fun loadData() = coroutineScope {
    val data1 = async { fetchData1() }
    val data2 = async { fetchData2() }
    
    // Wait for both to complete
    val result = data1.await() + data2.await()
    println(result)
}

// Coroutine context and dispatchers
fun main() = runBlocking {
    // Main thread
    launch(Dispatchers.Main) {
        // UI work
    }
    
    // Background thread pool
    launch(Dispatchers.IO) {
        // Network/disk operations
    }
    
    // CPU-intensive work
    launch(Dispatchers.Default) {
        // Heavy computations
    }
}

// Flow for streams of data
fun numberFlow(): Flow<Int> = flow {
    for (i in 1..3) {
        delay(100)
        emit(i)
    }
}

suspend fun collectNumbers() {
    numberFlow().collect { value ->
        println(value)
    }
}

// Structured concurrency
suspend fun performTasks() = coroutineScope {
    val job1 = launch { task1() }
    val job2 = launch { task2() }
    
    // If one fails, all are cancelled
    job1.join()
    job2.join()
}

// Exception handling
fun main() = runBlocking {
    val handler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
    }
    
    val job = GlobalScope.launch(handler) {
        throw Exception("Error!")
    }
    job.join()
}
```

---

## Properties and Fields

**Explanation:** Kotlin treats properties as first-class citizens with custom getters and setters.

### Java
```java
public class Person {
    private String name;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
```

### Kotlin
```kotlin
// Simple property
class Person {
    var name: String = ""  // Getter and setter auto-generated
    val birthYear: Int = 1990  // Only getter (immutable)
}

// Custom getter and setter
class Rectangle(val width: Int, val height: Int) {
    val area: Int
        get() = width * height  // Custom getter
    
    var isSquare: Boolean
        get() = width == height
        set(value) {
            // You can't actually make it a square by setting this,
            // but you could throw an exception or log
            if (value && !isSquare) {
                println("Cannot make rectangle a square this way")
            }
        }
}

// Backing field
class User {
    var name: String = ""
        set(value) {
            field = value.trim()  // 'field' is backing field
        }
}

// Late initialization
class MyClass {
    lateinit var data: String
    
    fun initialize() {
        data = "Initialized"
    }
}

// Lazy property
class Heavy {
    val lazyProperty: String by lazy {
        println("Computing...")
        "Result"
    }
}

// Const val (compile-time constant)
class Config {
    companion object {
        const val API_KEY = "abc123"
        const val MAX_SIZE = 100
    }
}

// Private setter
class Counter {
    var count: Int = 0
        private set  // Public getter, private setter
    
    fun increment() {
        count++
    }
}
```

---

## Primary Constructors

**Explanation:** Concise constructor syntax with automatic property initialization.

### Java
```java
public class Person {
    private final String name;
    private final int age;
    private String email;
    
    public Person(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }
    
    public Person(String name, int age) {
        this(name, age, "");
    }
}
```

### Kotlin
```kotlin
// Primary constructor with properties
class Person(
    val name: String,           // Property (val)
    var age: Int,               // Property (var)
    private val email: String = ""  // Property with default value
)

// Init block
class User(val name: String, val age: Int) {
    init {
        require(age >= 0) { "Age must be non-negative" }
        println("User created: $name")
    }
}

// Secondary constructors
class Person(val name: String, val age: Int) {
    var email: String = ""
    
    // Secondary constructor must delegate to primary
    constructor(name: String, age: Int, email: String) : this(name, age) {
        this.email = email
    }
}

// Constructor parameters without properties
class Example(name: String) {  // Not a property
    val upperName = name.uppercase()  // Use in init
}

// Private primary constructor
class Database private constructor(val url: String) {
    companion object {
        fun create(url: String): Database {
            return Database(url)
        }
    }
}

// Multiple init blocks (executed in order)
class Complex(val x: Int) {
    init {
        println("First init: $x")
    }
    
    val y = x * 2
    
    init {
        println("Second init: $y")
    }
}
```

---

## Operator Overloading

**Explanation:** Define custom behavior for operators on your classes.

### Java
```java
// Must use method calls
BigInteger a = new BigInteger("100");
BigInteger b = new BigInteger("200");
BigInteger sum = a.add(b);
```

### Kotlin
```kotlin
// Operator overloading for custom classes
data class Vector(val x: Int, val y: Int) {
    operator fun plus(other: Vector) = Vector(x + other.x, y + other.y)
    operator fun minus(other: Vector) = Vector(x - other.x, y - other.y)
    operator fun times(scalar: Int) = Vector(x * scalar, y * scalar)
    operator fun unaryMinus() = Vector(-x, -y)
}

val v1 = Vector(1, 2)
val v2 = Vector(3, 4)
val v3 = v1 + v2  // Vector(4, 6)
val v4 = v1 * 3   // Vector(3, 6)
val v5 = -v1      // Vector(-1, -2)

// Index access operator
class Matrix(private val data: Array<IntArray>) {
    operator fun get(row: Int, col: Int) = data[row][col]
    operator fun set(row: Int, col: Int, value: Int) {
        data[row][col] = value
    }
}

val matrix = Matrix(arrayOf(intArrayOf(1, 2), intArrayOf(3, 4)))
println(matrix[0, 1])  // 2
matrix[0, 1] = 5

// Invoke operator (object as function)
class Multiplier(val factor: Int) {
    operator fun invoke(value: Int) = value * factor
}

val triple = Multiplier(3)
println(triple(5))  // 15

// In operator (contains)
class Range(val start: Int, val end: Int) {
    operator fun contains(value: Int) = value in start..end
}

val range = Range(1, 10)
println(5 in range)  // true

// Comparison operators
data class Version(val major: Int, val minor: Int) : Comparable<Version> {
    override fun compareTo(other: Version): Int {
        return when {
            major != other.major -> major - other.major
            else -> minor - other.minor
        }
    }
}

val v1 = Version(1, 2)
val v2 = Version(1, 3)
println(v1 < v2)  // true

// Iterator operator
class Countdown(val start: Int) {
    operator fun iterator() = object : Iterator<Int> {
        private var current = start
        
        override fun hasNext() = current > 0
        override fun next() = current--
    }
}

for (i in Countdown(5)) {
    println(i)  // 5, 4, 3, 2, 1
}
```

---

## Scope Functions

**Explanation:** Functions that execute a block of code within the context of an object.

### Java
```java
// Manual null checking and operations
Person person = getPerson();
if (person != null) {
    person.setName("John");
    person.setAge(30);
    saveToDatabase(person);
}

// Builder pattern
StringBuilder sb = new StringBuilder();
sb.append("Hello");
sb.append(" ");
sb.append("World");
String result = sb.toString();
```

### Kotlin
```kotlin
// let - for null safety and transformations
val person: Person? = getPerson()
val result = person?.let {
    it.name = "John"
    it.age = 30
    saveToDatabase(it)
    "Success"  // Return value
}

// run - execute block and return result
val result = "Hello".run {
    println(this)  // Hello
    this.length  // Returns 5
}

// with - group function calls on an object
val sb = StringBuilder()
val result = with(sb) {
    append("Hello")
    append(" ")
    append("World")
    toString()  // Returns "Hello World"
}

// apply - configure object and return it
val person = Person().apply {
    name = "John"
    age = 30
    email = "john@example.com"
}  // Returns configured Person

// also - perform side effects and return object
val numbers = mutableListOf(1, 2, 3)
    .also { println("Before: $it") }
    .apply { add(4) }
    .also { println("After: $it") }

// Comparison of scope functions
data class User(var name: String = "", var age: Int = 0)

// let - 'it', returns lambda result
val length = User().let {
    it.name = "John"
    it.name.length  // Returns 4
}

// run - 'this', returns lambda result
val length = User().run {
    name = "John"
    name.length  // Returns 4
}

// apply - 'this', returns object
val user = User().apply {
    name = "John"
    age = 30
}  // Returns User

// also - 'it', returns object
val user = User().also {
    it.name = "John"
    it.age = 30
}  // Returns User

// with - 'this', returns lambda result
val length = with(User()) {
    name = "John"
    name.length  // Returns 4
}

// Practical example - multiple scope functions
val result = getPerson()
    ?.takeIf { it.age >= 18 }  // Only continue if adult
    ?.let { person ->
        person.apply {
            name = name.uppercase()
        }
    }?.also {
        println("Processed: ${it.name}")
    }?.run {
        saveToDatabase(this)
        "Success"
    }
```

---

## Collections and Stream Operations

**Explanation:** Kotlin provides rich collection APIs similar to Java Streams but more concise and integrated into the standard library. All operations work on collections directly without needing to convert to streams.

### Creating Collections

### Java
```java
// Arrays
String[] array = {"a", "b", "c"};
String[] array2 = new String[5];

// Lists
List<String> list = Arrays.asList("a", "b", "c");
List<String> mutableList = new ArrayList<>();
List<String> immutableList = Collections.unmodifiableList(list);

// Sets
Set<String> set = new HashSet<>(Arrays.asList("a", "b", "c"));
Set<String> immutableSet = Collections.unmodifiableSet(set);

// Maps
Map<String, Integer> map = new HashMap<>();
map.put("a", 1);
map.put("b", 2);
```

### Kotlin
```kotlin
// Arrays
val array = arrayOf("a", "b", "c")
val intArray = intArrayOf(1, 2, 3)  // Primitive array
val nullableArray = arrayOfNulls<String>(5)

// Lists
val list = listOf("a", "b", "c")  // Immutable
val mutableList = mutableListOf("a", "b", "c")  // Mutable
val emptyList = emptyList<String>()

// Sets
val set = setOf("a", "b", "c")  // Immutable
val mutableSet = mutableSetOf("a", "b", "c")  // Mutable
val linkedSet = linkedSetOf("a", "b", "c")  // Maintains order

// Maps
val map = mapOf("a" to 1, "b" to 2, "c" to 3)  // Immutable
val mutableMap = mutableMapOf("a" to 1, "b" to 2)  // Mutable
val emptyMap = emptyMap<String, Int>()

// Sequences (lazy evaluation, like Java Streams)
val sequence = sequenceOf(1, 2, 3, 4, 5)
val sequenceFromList = list.asSequence()
```

---

### Filter Operations

**Explanation:** Select elements based on a predicate.

### Java
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

// Filter
List<Integer> evens = numbers.stream()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());

// Filter with index (complex in Java)
List<Integer> filtered = IntStream.range(0, numbers.size())
    .filter(i -> i % 2 == 0)
    .mapToObj(numbers::get)
    .collect(Collectors.toList());
```

### Kotlin
```kotlin
val numbers = listOf(1, 2, 3, 4, 5, 6)

// filter - returns elements matching predicate
val evens = numbers.filter { it % 2 == 0 }  // [2, 4, 6]

// filterNot - returns elements NOT matching predicate
val odds = numbers.filterNot { it % 2 == 0 }  // [1, 3, 5]

// filterIndexed - filter with index
val filtered = numbers.filterIndexed { index, value -> 
    index % 2 == 0 && value > 2 
}  // [3, 5]

// filterNotNull - removes null values
val mixed: List<Int?> = listOf(1, null, 2, null, 3)
val nonNull = mixed.filterNotNull()  // [1, 2, 3]

// filterIsInstance - filter by type
val mixed2: List<Any> = listOf(1, "a", 2, "b", 3)
val ints = mixed2.filterIsInstance<Int>()  // [1, 2, 3]
val strings = mixed2.filterIsInstance<String>()  // ["a", "b"]

// partition - split into two lists
val (even, odd) = numbers.partition { it % 2 == 0 }
// even = [2, 4, 6], odd = [1, 3, 5]
```

---

### Map Operations

**Explanation:** Transform each element in a collection.

### Java
```java
List<String> words = Arrays.asList("hello", "world");

// Map
List<Integer> lengths = words.stream()
    .map(String::length)
    .collect(Collectors.toList());

// Map with index (complex)
List<String> indexed = IntStream.range(0, words.size())
    .mapToObj(i -> i + ": " + words.get(i))
    .collect(Collectors.toList());
```

### Kotlin
```kotlin
val words = listOf("hello", "world")

// map - transform each element
val lengths = words.map { it.length }  // [5, 5]
val upper = words.map { it.uppercase() }  // ["HELLO", "WORLD"]

// mapIndexed - transform with index
val indexed = words.mapIndexed { index, word -> 
    "$index: $word" 
}  // ["0: hello", "1: world"]

// mapNotNull - transform and remove nulls
val mixed: List<String?> = listOf("1", "2", null, "4")
val numbers = mixed.mapNotNull { it?.toIntOrNull() }  // [1, 2, 4]

// mapIndexedNotNull
val result = mixed.mapIndexedNotNull { index, value -> 
    value?.let { "$index: $it" }
}

// associate - create map from list
val wordLengths = words.associate { it to it.length }
// {hello=5, world=5}

// associateBy - create map with key selector
val byLength = words.associateBy { it.length }
// {5=world} (last value for duplicate keys)

// associateWith - create map with value selector
val withLengths = words.associateWith { it.length }
// {hello=5, world=5}

// groupBy - group elements by key
val numbers = listOf(1, 2, 3, 4, 5, 6)
val grouped = numbers.groupBy { it % 2 }
// {1=[1, 3, 5], 0=[2, 4, 6]}
```

---

### FlatMap Operations

**Explanation:** Transform each element to a collection and flatten the result.

### Java
```java
List<List<Integer>> nested = Arrays.asList(
    Arrays.asList(1, 2),
    Arrays.asList(3, 4),
    Arrays.asList(5, 6)
);

// FlatMap
List<Integer> flat = nested.stream()
    .flatMap(List::stream)
    .collect(Collectors.toList());

// FlatMap with transformation
List<String> words = Arrays.asList("hello", "world");
List<Character> chars = words.stream()
    .flatMap(s -> s.chars().mapToObj(c -> (char) c))
    .collect(Collectors.toList());
```

### Kotlin
```kotlin
val nested = listOf(
    listOf(1, 2),
    listOf(3, 4),
    listOf(5, 6)
)

// flatMap - flatten and transform
val flat = nested.flatMap { it }  // [1, 2, 3, 4, 5, 6]

// flatMap with transformation
val doubled = nested.flatMap { sublist -> 
    sublist.map { it * 2 }
}  // [2, 4, 6, 8, 10, 12]

// flatten - just flatten without transformation
val flattened = nested.flatten()  // [1, 2, 3, 4, 5, 6]

// flatMapIndexed
val result = nested.flatMapIndexed { index, list ->
    list.map { "$index: $it" }
}  // ["0: 1", "0: 2", "1: 3", "1: 4", "2: 5", "2: 6"]

// String to chars
val words = listOf("hello", "world")
val chars = words.flatMap { it.toList() }
// [h, e, l, l, o, w, o, r, l, d]

// flatMap for optional values
val maybeNumbers: List<String?> = listOf("1", null, "2", "3")
val numbers = maybeNumbers.mapNotNull { it?.toIntOrNull() }
// [1, 2, 3]
```

---

### Reduce and Fold Operations

**Explanation:** Accumulate collection elements into a single value.

### Java
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Reduce
int sum = numbers.stream()
    .reduce(0, Integer::sum);

Optional<Integer> product = numbers.stream()
    .reduce((a, b) -> a * b);

// Collect
String joined = numbers.stream()
    .map(String::valueOf)
    .collect(Collectors.joining(", "));
```

### Kotlin
```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

// reduce - accumulate with first element as initial
val sum = numbers.reduce { acc, n -> acc + n }  // 15
val product = numbers.reduce { acc, n -> acc * n }  // 120

// reduceIndexed
val indexed = numbers.reduceIndexed { index, acc, n -> 
    acc + n * index 
}

// reduceOrNull - returns null for empty collections
val empty = emptyList<Int>()
val result = empty.reduceOrNull { acc, n -> acc + n }  // null

// fold - accumulate with initial value
val sum2 = numbers.fold(0) { acc, n -> acc + n }  // 15
val sum3 = numbers.fold(10) { acc, n -> acc + n }  // 25

// foldIndexed
val result2 = numbers.foldIndexed(0) { index, acc, n -> 
    acc + n * index 
}

// foldRight - fold from right to left
val list = listOf("a", "b", "c")
val folded = list.foldRight("") { str, acc -> acc + str }
// "cba"

// runningFold - intermediate results
val running = numbers.runningFold(0) { acc, n -> acc + n }
// [0, 1, 3, 6, 10, 15]

// runningReduce
val running2 = numbers.runningReduce { acc, n -> acc + n }
// [1, 3, 6, 10, 15]

// scan (alias for runningFold)
val scanned = numbers.scan(0) { acc, n -> acc + n }
```

---

### Aggregation Operations

**Explanation:** Calculate aggregate values from collections.

### Java
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Sum
int sum = numbers.stream()
    .mapToInt(Integer::intValue)
    .sum();

// Average
double avg = numbers.stream()
    .mapToInt(Integer::intValue)
    .average()
    .orElse(0.0);

// Min/Max
Optional<Integer> min = numbers.stream().min(Integer::compare);
Optional<Integer> max = numbers.stream().max(Integer::compare);

// Count
long count = numbers.stream().count();
```

### Kotlin
```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

// sum - for numeric types
val sum = numbers.sum()  // 15

// sumOf - with transformation
val sumOfSquares = numbers.sumOf { it * it }  // 55

// average
val avg = numbers.average()  // 3.0

// min/max (deprecated, use minOrNull/maxOrNull)
val min = numbers.minOrNull()  // 1
val max = numbers.maxOrNull()  // 5

// minBy/maxBy - with selector
val words = listOf("a", "bb", "ccc")
val shortest = words.minByOrNull { it.length }  // "a"
val longest = words.maxByOrNull { it.length }  // "ccc"

// minOf/maxOf - with selector returning value
val minLength = words.minOf { it.length }  // 1
val maxLength = words.maxOf { it.length }  // 3

// count
val count = numbers.count()  // 5
val evenCount = numbers.count { it % 2 == 0 }  // 2

// none, any, all
val hasNegative = numbers.any { it < 0 }  // false
val allPositive = numbers.all { it > 0 }  // true
val noneNegative = numbers.none { it < 0 }  // true
```

---

### Sorting Operations

### Java
```java
List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5);

// Sort
List<Integer> sorted = numbers.stream()
    .sorted()
    .collect(Collectors.toList());

// Sort descending
List<Integer> desc = numbers.stream()
    .sorted(Comparator.reverseOrder())
    .collect(Collectors.toList());

// Sort by property
List<Person> people = getPeople();
List<Person> sortedByAge = people.stream()
    .sorted(Comparator.comparing(Person::getAge))
    .collect(Collectors.toList());
```

### Kotlin
```kotlin
val numbers = listOf(3, 1, 4, 1, 5)

// sorted - natural order
val sorted = numbers.sorted()  // [1, 1, 3, 4, 5]

// sortedDescending
val desc = numbers.sortedDescending()  // [5, 4, 3, 1, 1]

// sortedBy - with selector
val words = listOf("banana", "apple", "cherry")
val byLength = words.sortedBy { it.length }
// ["apple", "banana", "cherry"]

// sortedByDescending
val byLengthDesc = words.sortedByDescending { it.length }

// sortedWith - with comparator
val custom = numbers.sortedWith(compareBy { it % 2 })
// Even numbers first

// reversed - reverse order
val reversed = numbers.reversed()  // [5, 1, 4, 1, 3]

// shuffled - random order
val shuffled = numbers.shuffled()

// Multiple comparators
data class Person(val name: String, val age: Int)
val people = listOf(
    Person("John", 30),
    Person("Jane", 25),
    Person("John", 25)
)

val sorted = people.sortedWith(
    compareBy({ it.name }, { it.age })
)
```

---

### Taking and Dropping Elements

### Java
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Take first n
List<Integer> first3 = numbers.stream()
    .limit(3)
    .collect(Collectors.toList());

// Skip first n
List<Integer> after2 = numbers.stream()
    .skip(2)
    .collect(Collectors.toList());
```

### Kotlin
```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

// take - first n elements
val first3 = numbers.take(3)  // [1, 2, 3]

// takeLast - last n elements
val last2 = numbers.takeLast(2)  // [4, 5]

// takeWhile - while predicate is true
val taken = numbers.takeWhile { it < 4 }  // [1, 2, 3]

// takeLastWhile - from end while true
val takenLast = numbers.takeLastWhile { it > 2 }  // [3, 4, 5]

// drop - skip first n elements
val dropped = numbers.drop(2)  // [3, 4, 5]

// dropLast - skip last n elements
val droppedLast = numbers.dropLast(2)  // [1, 2, 3]

// dropWhile - skip while predicate is true
val droppedWhile = numbers.dropWhile { it < 3 }  // [3, 4, 5]

// dropLastWhile
val droppedLastWhile = numbers.dropLastWhile { it > 3 }  // [1, 2, 3]

// slice - get sublist by indices
val sliced = numbers.slice(1..3)  // [2, 3, 4]
val sliced2 = numbers.slice(listOf(0, 2, 4))  // [1, 3, 5]

// chunked - split into chunks
val chunked = numbers.chunked(2)  // [[1, 2], [3, 4], [5]]

// windowed - sliding window
val windowed = numbers.windowed(3)
// [[1, 2, 3], [2, 3, 4], [3, 4, 5]]

val windowed2 = numbers.windowed(size = 3, step = 2)
// [[1, 2, 3], [3, 4, 5]]

// zipWithNext - pair adjacent elements
val pairs = numbers.zipWithNext()
// [(1, 2), (2, 3), (3, 4), (4, 5)]
```

---

### Distinct and Set Operations

### Java
```java
List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 3, 3, 4);

// Distinct
List<Integer> unique = numbers.stream()
    .distinct()
    .collect(Collectors.toList());

// Distinct by property (complex in Java)
List<Person> people = getPeople();
List<Person> uniqueByAge = people.stream()
    .collect(Collectors.toMap(
        Person::getAge,
        p -> p,
        (p1, p2) -> p1
    ))
    .values()
    .stream()
    .collect(Collectors.toList());
```

### Kotlin
```kotlin
val numbers = listOf(1, 2, 2, 3, 3, 3, 4)

// distinct - unique elements
val unique = numbers.distinct()  // [1, 2, 3, 4]

// distinctBy - unique by selector
data class Person(val name: String, val age: Int)
val people = listOf(
    Person("John", 30),
    Person("Jane", 30),
    Person("Bob", 25)
)
val uniqueByAge = people.distinctBy { it.age }
// [Person("John", 30), Person("Bob", 25)]

// Set operations
val list1 = listOf(1, 2, 3, 4)
val list2 = listOf(3, 4, 5, 6)

// union - all elements from both
val union = list1.union(list2)  // [1, 2, 3, 4, 5, 6]

// intersect - common elements
val common = list1.intersect(list2)  // [3, 4]

// subtract - elements in first but not second
val diff = list1.subtract(list2)  // [1, 2]

// toSet - convert to set
val set = numbers.toSet()  // [1, 2, 3, 4]
```

---

### Find and Search Operations

### Java
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// Find first
Optional<Integer> first = numbers.stream()
    .filter(n -> n > 3)
    .findFirst();

// Check if any match
boolean anyEven = numbers.stream()
    .anyMatch(n -> n % 2 == 0);

// Check if all match
boolean allPositive = numbers.stream()
    .allMatch(n -> n > 0);

// Check if none match
boolean noneNegative = numbers.stream()
    .noneMatch(n -> n < 0);
```

### Kotlin
```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

// find - first element matching predicate (or null)
val first = numbers.find { it > 3 }  // 4
val notFound = numbers.find { it > 10 }  // null

// findLast - last element matching predicate
val last = numbers.findLast { it > 3 }  // 5

// first - throws NoSuchElementException if not found
val firstElement = numbers.first()  // 1
val firstEven = numbers.first { it % 2 == 0 }  // 2

// firstOrNull - returns null if not found
val firstOrNull = numbers.firstOrNull { it > 10 }  // null

// last - last element
val lastElement = numbers.last()  // 5
val lastEven = numbers.last { it % 2 == 0 }  // 4

// lastOrNull
val lastOrNull = numbers.lastOrNull { it > 10 }  // null

// single - exactly one element (throws if 0 or > 1)
val single = listOf(42).single()  // 42
val singleEven = listOf(1, 2, 3).single { it % 2 == 0 }  // 2

// singleOrNull
val singleOrNull = numbers.singleOrNull { it == 3 }  // 3
val notSingle = numbers.singleOrNull { it > 2 }  // null (multiple match)

// indexOf - index of element
val index = numbers.indexOf(3)  // 2
val notFoundIndex = numbers.indexOf(10)  // -1

// indexOfFirst - index of first match
val indexFirst = numbers.indexOfFirst { it > 3 }  // 3

// indexOfLast
val indexLast = numbers.indexOfLast { it > 3 }  // 4

// contains - check if element exists
val hasThree = numbers.contains(3)  // true
val hasTen = 10 in numbers  // false (using 'in' operator)

// containsAll
val hasAll = numbers.containsAll(listOf(1, 2, 3))  // true

// elementAt - get by index
val element = numbers.elementAt(2)  // 3

// elementAtOrNull
val elementOrNull = numbers.elementAtOrNull(10)  // null

// elementAtOrElse
val elementOrElse = numbers.elementAtOrElse(10) { -1 }  // -1

// getOrNull
val value = numbers.getOrNull(2)  // 3
val null = numbers.getOrNull(10)  // null

// getOrElse
val value2 = numbers.getOrElse(10) { -1 }  // -1
```

---

### Joining and String Operations

### Java
```java
List<String> words = Arrays.asList("hello", "world", "kotlin");

// Join
String joined = words.stream()
    .collect(Collectors.joining(", "));

// Join with prefix/suffix
String formatted = words.stream()
    .collect(Collectors.joining(", ", "[", "]"));
```

### Kotlin
```kotlin
val words = listOf("hello", "world", "kotlin")

// joinToString - with separator
val joined = words.joinToString(", ")
// "hello, world, kotlin"

// joinToString with prefix, suffix
val formatted = words.joinToString(
    separator = ", ",
    prefix = "[",
    postfix = "]"
)  // "[hello, world, kotlin]"

// joinToString with transformation
val upper = words.joinToString(" ") { it.uppercase() }
// "HELLO WORLD KOTLIN"

// joinToString with limit
val limited = (1..10).toList().joinToString(
    separator = ", ",
    limit = 5,
    truncated = "..."
)  // "1, 2, 3, 4, 5, ..."

// joinTo - append to StringBuilder/Appendable
val sb = StringBuilder()
words.joinTo(sb, separator = " - ")

// joinToString for numbers
val numbers = listOf(1, 2, 3)
val numStr = numbers.joinToString()  // "1, 2, 3"
```

---

### Grouping Operations

### Java
```java
List<Person> people = getPeople();

// Group by
Map<Integer, List<Person>> byAge = people.stream()
    .collect(Collectors.groupingBy(Person::getAge));

// Group by with counting
Map<Integer, Long> countByAge = people.stream()
    .collect(Collectors.groupingBy(
        Person::getAge,
        Collectors.counting()
    ));
```

### Kotlin
```kotlin
data class Person(val name: String, val age: Int, val city: String)
val people = listOf(
    Person("John", 30, "NYC"),
    Person("Jane", 25, "LA"),
    Person("Bob", 30, "NYC"),
    Person("Alice", 25, "LA")
)

// groupBy - group by key
val byAge = people.groupBy { it.age }
// {30=[John, Bob], 25=[Jane, Alice]}

// groupBy with value transformation
val namesByAge = people.groupBy(
    keySelector = { it.age },
    valueTransform = { it.name }
)  // {30=["John", "Bob"], 25=["Jane", "Alice"]}

// groupingBy - for more complex aggregations
val ageCount = people.groupingBy { it.age }.eachCount()
// {30=2, 25=2}

// groupingBy with fold
val totalAgeByCity = people.groupingBy { it.city }
    .fold(0) { acc, person -> acc + person.age }
// {NYC=60, LA=50}

// groupingBy with reduce
val oldestByCity = people.groupingBy { it.city }
    .reduce { _, acc, person -> 
        if (person.age > acc.age) person else acc
    }

// groupingBy with aggregate
val stats = people.groupingBy { it.city }
    .aggregate { _, acc: Int?, person, first ->
        if (first) person.age else (acc ?: 0) + person.age
    }
```

---

### Zip and Combine Operations

### Java
```java
List<String> names = Arrays.asList("John", "Jane", "Bob");
List<Integer> ages = Arrays.asList(30, 25, 35);

// Zip (complex in Java)
List<String> combined = IntStream.range(0, Math.min(names.size(), ages.size()))
    .mapToObj(i -> names.get(i) + ": " + ages.get(i))
    .collect(Collectors.toList());
```

### Kotlin
```kotlin
val names = listOf("John", "Jane", "Bob")
val ages = listOf(30, 25, 35)

// zip - combine two lists
val pairs = names.zip(ages)
// [(John, 30), (Jane, 25), (Bob, 35)]

// zip with transformation
val combined = names.zip(ages) { name, age -> 
    "$name is $age years old"
}
// ["John is 30 years old", "Jane is 25 years old", "Bob is 35 years old"]

// unzip - split pairs into two lists
val listOfPairs = listOf("a" to 1, "b" to 2, "c" to 3)
val (letters, numbers) = listOfPairs.unzip()
// letters = ["a", "b", "c"], numbers = [1, 2, 3]

// zipWithNext - pair adjacent elements
val numbers = listOf(1, 2, 3, 4)
val consecutive = numbers.zipWithNext()
// [(1, 2), (2, 3), (3, 4)]

val differences = numbers.zipWithNext { a, b -> b - a }
// [1, 1, 1]
```

---

### Conversion Operations

### Java
```java
List<Integer> list = Arrays.asList(1, 2, 3);

// To array
Integer[] array = list.toArray(new Integer[0]);

// To set
Set<Integer> set = new HashSet<>(list);

// To map (complex)
Map<Integer, Integer> map = list.stream()
    .collect(Collectors.toMap(n -> n, n -> n * n));
```

### Kotlin
```kotlin
val list = listOf(1, 2, 3, 4, 5)

// toList - creates new list
val newList = list.toList()

// toMutableList
val mutable = list.toMutableList()

// toSet
val set = list.toSet()

// toMutableSet
val mutableSet = list.toMutableSet()

// toHashSet
val hashSet = list.toHashSet()

// toSortedSet
val sortedSet = list.toSortedSet()

// toTypedArray - to generic array
val array = list.toTypedArray()

// toIntArray - to primitive array
val intArray = list.toIntArray()

// toMap - from list of pairs
val pairs = listOf("a" to 1, "b" to 2, "c" to 3)
val map = pairs.toMap()

// associate - create map from list
val map2 = list.associate { it to it * it }
// {1=1, 2=4, 3=9, 4=16, 5=25}

// associateBy - key from element
val map3 = list.associateBy { "key$it" }
// {key1=1, key2=2, key3=3, key4=4, key5=5}

// associateWith - value from element
val map4 = list.associateWith { it * it }
// {1=1, 2=4, 3=9, 4=16, 5=25}

// asSequence - convert to lazy sequence
val sequence = list.asSequence()

// asIterable
val iterable = list.asIterable()
```

---

### Sequence Operations (Lazy Evaluation)

**Explanation:** Sequences are lazy - operations are only performed when terminal operation is called. Similar to Java Streams.

### Java
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Stream processing (lazy)
List<Integer> result = numbers.stream()
    .filter(n -> {
        System.out.println("Filter: " + n);
        return n % 2 == 0;
    })
    .map(n -> {
        System.out.println("Map: " + n);
        return n * n;
    })
    .limit(2)
    .collect(Collectors.toList());
```

### Kotlin
```kotlin
val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Eager evaluation (processes all elements)
val eagerResult = numbers
    .filter { 
        println("Filter: $it")
        it % 2 == 0 
    }
    .map { 
        println("Map: $it")
        it * it 
    }
    .take(2)

// Lazy evaluation with sequence (like Java Stream)
val lazyResult = numbers.asSequence()
    .filter { 
        println("Filter: $it")
        it % 2 == 0 
    }
    .map { 
        println("Map: $it")
        it * it 
    }
    .take(2)
    .toList()  // Terminal operation - triggers evaluation

// Create sequences
val seq1 = sequenceOf(1, 2, 3, 4, 5)
val seq2 = generateSequence(1) { it + 1 }  // Infinite sequence
val seq3 = generateSequence { readLine() }  // Until null

// Sequence operations
val infinite = generateSequence(0) { it + 1 }
val first10Evens = infinite
    .filter { it % 2 == 0 }
    .take(10)
    .toList()

// Fibonacci sequence
val fibonacci = generateSequence(Pair(0, 1)) { (a, b) -> 
    Pair(b, a + b) 
}.map { it.first }

val first10Fib = fibonacci.take(10).toList()
// [0, 1, 1, 2, 3, 5, 8, 13, 21, 34]

// When to use sequences:
// 1. Large collections with multiple operations
// 2. Infinite sequences
// 3. When you need to optimize performance
// 4. When you might not need all results (take, first, etc.)
```

---

---

## Interfaces and Abstract Classes

**Explanation:** Kotlin interfaces can contain method implementations and properties, making them more powerful than Java interfaces.

### Java
```java
// Interface
public interface Clickable {
    void click();
    
    // Default method (Java 8+)
    default void showOff() {
        System.out.println("I'm clickable!");
    }
}

// Abstract class
public abstract class Animated {
    public abstract void animate();
    
    public void playAnimation() {
        System.out.println("Playing animation");
    }
}

// Implementation
public class Button implements Clickable {
    @Override
    public void click() {
        System.out.println("Button clicked");
    }
}
```

### Kotlin
```kotlin
// Interface with properties and default implementations
interface Clickable {
    fun click()  // Abstract method
    
    fun showOff() {  // Method with default implementation
        println("I'm clickable!")
    }
    
    // Property in interface
    val priority: Int
        get() = 0  // Default implementation
}

// Interface with properties
interface Named {
    val name: String  // Abstract property
    val nickname: String
        get() = name.uppercase()  // Property with implementation
}

// Abstract class
abstract class Animated {
    abstract fun animate()  // Abstract method
    
    open fun playAnimation() {  // Open method (can be overridden)
        println("Playing animation")
    }
    
    fun stopAnimation() {  // Final method (cannot be overridden)
        println("Stopping animation")
    }
}

// Multiple interface implementation
class Button : Clickable, Named {
    override val name = "Button"
    
    override fun click() {
        println("Button clicked")
    }
}

// Resolving conflicts when multiple interfaces have same method
interface Focusable {
    fun showOff() {
        println("I'm focusable!")
    }
}

class Button2 : Clickable, Focusable {
    override fun click() { }
    
    // Must override to resolve conflict
    override fun showOff() {
        super<Clickable>.showOff()  // Call specific super
        super<Focusable>.showOff()
    }
}

// Functional (SAM) interfaces
fun interface IntPredicate {
    fun accept(i: Int): Boolean
}

// Can be used with lambda
val isEven = IntPredicate { it % 2 == 0 }
println(isEven.accept(4))  // true
```

---

## Enum Classes

**Explanation:** Kotlin enums can have properties, methods, and implement interfaces.

### Java
```java
public enum Color {
    RED, GREEN, BLUE;
    
    public String getHex() {
        switch(this) {
            case RED: return "#FF0000";
            case GREEN: return "#00FF00";
            case BLUE: return "#0000FF";
            default: return "#000000";
        }
    }
}
```

### Kotlin
```kotlin
// Simple enum
enum class Direction {
    NORTH, SOUTH, EAST, WEST
}

// Enum with properties
enum class Color(val rgb: Int) {
    RED(0xFF0000),
    GREEN(0x00FF00),
    BLUE(0x0000FF);
    
    fun getHex() = "#${rgb.toString(16).padStart(6, '0')}"
}

// Enum with methods and interfaces
interface Printable {
    fun print()
}

enum class State(val message: String) : Printable {
    IDLE("Waiting") {
        override fun print() = println("State: $message")
    },
    RUNNING("Processing") {
        override fun print() = println("Active: $message")
    },
    FINISHED("Done") {
        override fun print() = println("Completed: $message")
    };
    
    abstract override fun print()
}

// Using enums
val color = Color.RED
println(color.rgb)  // 16711680
println(color.name)  // RED
println(color.ordinal)  // 0

// Enum iteration
for (color in Color.values()) {
    println("${color.name}: ${color.getHex()}")
}

// valueOf
val blue = Color.valueOf("BLUE")

// Enum with when (exhaustive)
fun getDescription(color: Color) = when(color) {
    Color.RED -> "Hot"
    Color.GREEN -> "Nature"
    Color.BLUE -> "Cold"
}
```

---

## Annotations

**Explanation:** Kotlin supports annotations with cleaner syntax and additional features.

### Java
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
    String value() default "";
    int timeout() default 0;
}

@Test(value = "MyTest", timeout = 1000)
public void myTest() {
    // Test code
}
```

### Kotlin
```kotlin
// Define annotation
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Test(
    val value: String = "",
    val timeout: Int = 0
)

// Use annotation
@Test(value = "MyTest", timeout = 1000)
fun myTest() {
    // Test code
}

// Multiple annotations
@Test
@Deprecated("Use newMethod instead")
fun oldMethod() { }

// Annotation with array
annotation class Authors(val names: Array<String>)

@Authors(names = ["John", "Jane"])
class MyClass

// Use-site targets
class Example(
    @field:Test val name: String,  // Annotate backing field
    @get:Test val age: Int,         // Annotate getter
    @param:Test val id: Int         // Annotate constructor parameter
)

// File-level annotation
@file:JvmName("Utils")
package com.example

// Built-in annotations
@JvmStatic
@JvmOverloads
@Throws(IOException::class)
@Deprecated("Use newFunction", ReplaceWith("newFunction()"))
fun oldFunction() { }
```

---

## Generics and Variance

**Explanation:** Kotlin has declaration-site variance and reified type parameters.

### Java
```java
// Generic class
public class Box<T> {
    private T value;
    
    public void set(T value) {
        this.value = value;
    }
    
    public T get() {
        return value;
    }
}

// Bounded type
public class NumberBox<T extends Number> {
    private T value;
}

// Wildcards (use-site variance)
List<? extends Number> numbers = new ArrayList<Integer>();
List<? super Integer> integers = new ArrayList<Number>();
```

### Kotlin
```kotlin
// Generic class
class Box<T>(var value: T)

// Generic function
fun <T> singletonList(item: T): List<T> {
    return listOf(item)
}

// Multiple type parameters
class Pair<K, V>(val key: K, val value: V)

// Type constraints (upper bounds)
fun <T : Number> sum(a: T, b: T): Double {
    return a.toDouble() + b.toDouble()
}

// Multiple constraints
fun <T> copy(from: T, to: T) 
    where T : Cloneable,
          T : Comparable<T> {
    // T must implement both interfaces
}

// Declaration-site variance (covariance)
interface Producer<out T> {  // out = covariant
    fun produce(): T
    // fun consume(item: T)  // Error: T is in 'in' position
}

// Declaration-site variance (contravariance)
interface Consumer<in T> {  // in = contravariant
    fun consume(item: T)
    // fun produce(): T  // Error: T is in 'out' position
}

// Variance example
open class Animal
class Dog : Animal()
class Cat : Animal()

val dogProducer: Producer<Dog> = object : Producer<Dog> {
    override fun produce() = Dog()
}

// Covariant: Producer<Dog> can be assigned to Producer<Animal>
val animalProducer: Producer<Animal> = dogProducer

// Reified type parameters (inline functions only)
inline fun <reified T> isInstance(value: Any): Boolean {
    return value is T  // Can check type at runtime
}

println(isInstance<String>("Hello"))  // true
println(isInstance<Int>("Hello"))     // false

// Reified with generics
inline fun <reified T> List<*>.filterIsInstance(): List<T> {
    return this.filter { it is T }.map { it as T }
}

val mixed: List<Any> = listOf(1, "a", 2, "b", 3)
val ints = mixed.filterIsInstance<Int>()  // [1, 2, 3]

// Star projection
fun printList(list: List<*>) {  // List of unknown type
    for (item in list) {
        println(item)
    }
}

// Type erasure workaround with reified
inline fun <reified T> createInstance(): T {
    return T::class.java.getDeclaredConstructor().newInstance()
}
```

---

## Type Checks and Casts

**Explanation:** Kotlin provides safe type checking and casting operators.

### Java
```java
Object obj = "Hello";

// Type check
if (obj instanceof String) {
    String str = (String) obj;  // Explicit cast
    System.out.println(str.length());
}

// Unsafe cast
String str = (String) obj;  // May throw ClassCastException
```

### Kotlin
```kotlin
val obj: Any = "Hello"

// is operator (type check) with smart cast
if (obj is String) {
    println(obj.length)  // Auto-cast to String
}

// !is operator (negated type check)
if (obj !is String) {
    println("Not a string")
}

// as operator (unsafe cast)
val str: String = obj as String  // Throws ClassCastException if fails

// as? operator (safe cast)
val str2: String? = obj as? String  // Returns null if fails
val length = (obj as? String)?.length ?: 0

// Smart casts in when
fun describe(obj: Any) = when(obj) {
    is String -> "String of length ${obj.length}"
    is Int -> "Integer: $obj"
    is List<*> -> "List of size ${obj.size}"
    else -> "Unknown"
}

// Smart cast with &&
if (obj is String && obj.length > 0) {
    println(obj.uppercase())  // obj is smart cast to String
}

// Smart cast with ||
if (obj !is String || obj.isEmpty()) {
    return
}
println(obj.uppercase())  // obj is smart cast to String

// Smart cast doesn't work with var that can be modified
var variable: Any = "Hello"
if (variable is String) {
    // variable.length  // May not work if var can be modified
}

// Unsafe cast for generics
val list: List<String> = listOf("a", "b")
val anyList: List<Any> = list as List<Any>  // Unchecked cast warning

// Checking generic types with reified
inline fun <reified T> checkType(value: Any): Boolean {
    return value is T
}
```

---

## Exception Handling

**Explanation:** Kotlin doesn't have checked exceptions, making code cleaner.

### Java
```java
// Checked exceptions must be declared
public void readFile(String path) throws IOException {
    FileReader reader = new FileReader(path);
    // ...
}

// Must catch or declare
public void caller() {
    try {
        readFile("file.txt");
    } catch (IOException e) {
        e.printStackTrace();
    }
}

// Try-with-resources
try (FileReader reader = new FileReader("file.txt")) {
    // Use reader
} catch (IOException e) {
    e.printStackTrace();
}
```

### Kotlin
```kotlin
// No checked exceptions - throws clause not needed
fun readFile(path: String) {
    val reader = FileReader(path)
    // ...
}

// Try-catch
fun caller() {
    try {
        readFile("file.txt")
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

// Try as an expression
val result: String? = try {
    readFile("file.txt")
    "Success"
} catch (e: IOException) {
    e.printStackTrace()
    null
}

// Try with finally
try {
    performOperation()
} catch (e: Exception) {
    handleError(e)
} finally {
    cleanup()
}

// use function (like try-with-resources)
FileReader("file.txt").use { reader ->
    // Use reader
    // Automatically closed after block
}

// Multiple resources
FileInputStream("input.txt").use { input ->
    FileOutputStream("output.txt").use { output ->
        input.copyTo(output)
    }
}

// Throwing exceptions
fun fail(message: String): Nothing {
    throw IllegalStateException(message)
}

// Elvis with throw
val name = getName() ?: throw IllegalArgumentException("Name required")

// requireNotNull, checkNotNull
val nonNull = requireNotNull(nullable) { "Must not be null" }
val checked = checkNotNull(nullable)

// check, require, assert
fun process(value: Int) {
    require(value > 0) { "Value must be positive" }
    check(isInitialized) { "Not initialized" }
    assert(value < 100) { "Value too large" }
}

// @Throws annotation for Java interop
@Throws(IOException::class)
fun javaCompatibleFunction() {
    throw IOException("Error")
}
```

---

## Visibility Modifiers

**Explanation:** Kotlin has different visibility modifiers with more logical defaults.

### Java
```java
public class MyClass {
    public int publicField;        // Visible everywhere
    protected int protectedField;  // Visible in subclasses
    int packageField;              // Package-private (default)
    private int privateField;      // Only in this class
    
    public void publicMethod() { }
    protected void protectedMethod() { }
    void packageMethod() { }
    private void privateMethod() { }
}
```

### Kotlin
```kotlin
// Top-level declarations (not in Java)
fun topLevelFunction() { }  // public by default
private fun privateTopLevel() { }  // Visible in same file
internal fun internalTopLevel() { }  // Visible in same module

// Class visibility
open class MyClass {
    val publicField = 1              // public by default
    internal val internalField = 2   // Visible in same module
    protected val protectedField = 3 // Visible in subclasses
    private val privateField = 4     // Only in this class
    
    fun publicMethod() { }           // public by default
    internal fun internalMethod() { }
    protected open fun protectedMethod() { }
    private fun privateMethod() { }
}

// Visibility comparison: Kotlin vs Java
// Kotlin:
// - public (default): visible everywhere
// - internal: visible within same module
// - protected: visible in subclasses (not same package)
// - private: visible in same file/class

// Java:
// - public: visible everywhere
// - (no modifier - package-private): visible in same package
// - protected: visible in subclasses AND same package
// - private: visible in same class

// Module definition: A module is a set of Kotlin files compiled together
// - IntelliJ IDEA module
// - Maven project
// - Gradle source set

// Private top-level declarations
private class InternalHelper {
    fun help() { }
}

// Private in file
private val secret = "Hidden"

// Internal visibility
internal class InternalClass {
    internal fun internalFun() { }
}

// Protected cannot be used on top-level
// protected fun topLevel() { }  // Error

// Visibility in constructors
class Private private constructor(val value: Int) {
    companion object {
        fun create(value: Int) = Private(value)
    }
}

// Visibility overriding
open class Base {
    open protected fun foo() { }
}

class Derived : Base() {
    override fun foo() { }  // Can make more visible
}

// Getters and setters visibility
class Person {
    var name: String = ""
        private set  // Private setter, public getter
    
    internal var age: Int = 0
        private set  // Private setter, internal getter
}
```

---

## Nested and Inner Classes

**Explanation:** Kotlin nested classes don't hold reference to outer class by default.

### Java
```java
public class Outer {
    private int value = 10;
    
    // Non-static nested class (inner class)
    public class Inner {
        public void accessOuter() {
            System.out.println(value);  // Can access outer
        }
    }
    
    // Static nested class
    public static class StaticNested {
        public void method() {
            // Cannot access outer instance directly
        }
    }
}

// Usage
Outer outer = new Outer();
Outer.Inner inner = outer.new Inner();
Outer.StaticNested nested = new Outer.StaticNested();
```

### Kotlin
```kotlin
class Outer {
    private val value = 10
    
    // Nested class (like Java static nested - default)
    class Nested {
        fun method() {
            // Cannot access outer instance
            // println(value)  // Error
        }
    }
    
    // Inner class (like Java non-static nested)
    inner class Inner {
        fun accessOuter() {
            println(value)  // Can access outer
            println(this@Outer.value)  // Explicit outer reference
        }
    }
}

// Usage
val nested = Outer.Nested()  // No outer instance needed
val outer = Outer()
val inner = outer.Inner()    // Outer instance required

// Anonymous inner class
val listener = object : ClickListener {
    override fun onClick() {
        println("Clicked")
    }
}

// Anonymous object without supertype
val obj = object {
    val x = 10
    val y = 20
}

// Local classes
fun createListener(): ClickListener {
    class LocalListener : ClickListener {
        override fun onClick() {
            println("Local click")
        }
    }
    return LocalListener()
}

// Key difference:
// Java: Inner class by default (holds outer reference)
// Kotlin: Nested class by default (no outer reference)
// Use 'inner' keyword for inner class in Kotlin
```

---

## Object Expressions vs Object Declarations

**Explanation:** Detailed comparison of object usage in Kotlin.

### Java
```java
// Singleton
public class Singleton {
    private static final Singleton INSTANCE = new Singleton();
    private Singleton() {}
    public static Singleton getInstance() {
        return INSTANCE;
    }
}

// Anonymous class
button.setOnClickListener(new OnClickListener() {
    @Override
    public void onClick(View v) {
        // Handle click
    }
});
```

### Kotlin
```kotlin
// Object Declaration (Singleton)
object DatabaseManager {
    private val connection = "Connected"
    
    fun query(sql: String) {
        println("Executing: $sql")
    }
}

// Usage
DatabaseManager.query("SELECT * FROM users")

// Object can inherit
object DefaultComparator : Comparator<Int> {
    override fun compare(a: Int, b: Int) = a.compareTo(b)
}

// Object Expression (Anonymous object)
val listener = object : OnClickListener {
    override fun onClick() {
        println("Clicked")
    }
}

// Object expression without supertype
val obj = object {
    val name = "Object"
    val value = 42
    
    fun greet() = "Hello from $name"
}

println(obj.name)  // Accessible
println(obj.greet())

// Object expression with multiple supertypes
val multipleInterfaces = object : Runnable, AutoCloseable {
    override fun run() {
        println("Running")
    }
    
    override fun close() {
        println("Closing")
    }
}

// Object expression accessing outer scope
fun performOperation(value: Int) {
    val result = object {
        val doubled = value * 2
        val squared = value * value
    }
    println(result.doubled)  // Can access
}

// Companion object (like Java static)
class MyClass {
    companion object {
        const val CONSTANT = "Constant"
        
        fun create() = MyClass()
    }
}

// Usage
val instance = MyClass.create()
println(MyClass.CONSTANT)

// Named companion object
class AnotherClass {
    companion object Factory {
        fun create() = AnotherClass()
    }
}

// Can use class name or companion name
AnotherClass.create()
AnotherClass.Factory.create()
```

---

## Java Interoperability

**Explanation:** Kotlin's seamless integration with Java code.

### Calling Java from Kotlin

```kotlin
// Java getters/setters as properties
val javaList = ArrayList<String>()
javaList.add("item")  // Java method

// Java getter/setter
val person = JavaPerson()
person.name = "John"  // Calls setName()
val name = person.name  // Calls getName()

// Void methods return Unit
val result: Unit = javaObject.voidMethod()

// Handling Java nullability
val nullableString: String? = javaMethod()  // Might return null
val platformType = javaMethodUnknown()  // Platform type (!)

// Arrays
val javaArray: Array<String> = arrayOf("a", "b")
val intArray: IntArray = intArrayOf(1, 2, 3)

// Varargs
javaMethod(*stringArray)  // Spread operator

// Checked exceptions (use @Throws for Java)
@Throws(IOException::class)
fun kotlinFunction() {
    throw IOException()
}

// Java static methods
val result = JavaClass.staticMethod()

// Java static fields
val value = JavaClass.STATIC_FIELD
```

### Calling Kotlin from Java

```kotlin
// @JvmName - change name for Java
@file:JvmName("Utils")
package com.example

fun helper() { }
// Java: Utils.helper();

// @JvmStatic - make companion object methods static
class MyClass {
    companion object {
        @JvmStatic
        fun create() = MyClass()
    }
}
// Java: MyClass.create();

// @JvmField - expose property as field
class Example {
    @JvmField
    val publicField = "value"
}
// Java: String value = example.publicField;

// @JvmOverloads - generate overloads for default parameters
@JvmOverloads
fun greet(name: String = "User", title: String = "Mr.") {
    println("Hello, $title $name")
}
// Java can call: greet(), greet("John"), greet("John", "Dr.")

// Top-level functions
fun topLevel() { }
// Java: FileNameKt.topLevel();

// Extension functions
fun String.myExtension() { }
// Java: FileNameKt.myExtension("string");

// Handling Nothing type
fun fail(): Nothing {
    throw Exception()
}
// Java sees as void

// @JvmSynthetic - hide from Java
@JvmSynthetic
fun internalFunction() { }

// Properties with backing fields
var property: String = ""
// Java: getProperty(), setProperty(String value)
```

---

## Additional Kotlin Features

### Type Aliases

```kotlin
// Simplify complex types
typealias UserMap = Map<String, List<User>>
typealias ClickHandler = (View) -> Unit

fun processUsers(users: UserMap) {
    // ...
}
```

### Inline Functions

```kotlin
// Inline functions to avoid lambda overhead
inline fun <T> measureTime(block: () -> T): T {
    val start = System.currentTimeMillis()
    val result = block()
    val end = System.currentTimeMillis()
    println("Time: ${end - start}ms")
    return result
}

measureTime {
    // Some operation
}
```

### Destructuring Declarations

```kotlin
// For data classes
val (name, age) = person

// For lists
val (first, second, third) = listOf(1, 2, 3)

// In for loops
for ((key, value) in map) {
    println("$key -> $value")
}

// Custom destructuring
class Result(val status: Int, val message: String) {
    operator fun component1() = status
    operator fun component2() = message
}

val (status, message) = Result(200, "OK")
```

### Infix Functions

```kotlin
// More readable DSL-like syntax
infix fun Int.pow(exponent: Int): Int {
    return Math.pow(this.toDouble(), exponent.toDouble()).toInt()
}

val result = 2 pow 3  // 8

// Built-in infix functions
val pair = "key" to "value"
val range = 1 until 10
```

### Tailrec Functions

```kotlin
// Tail recursion optimization
tailrec fun factorial(n: Long, accumulator: Long = 1): Long {
    return if (n <= 1) accumulator
    else factorial(n - 1, n * accumulator)
}

println(factorial(10000))  // Won't stack overflow
```

### Contracts (Experimental)

```kotlin
import kotlin.contracts.*

// Contracts tell compiler about function behavior
@OptIn(ExperimentalContracts::class)
fun String?.isNotNullOrEmpty(): Boolean {
    contract {
        returns(true) implies (this@isNotNullOrEmpty != null)
    }
    return this != null && this.isNotEmpty()
}

// Usage with smart cast
fun process(value: String?) {
    if (value.isNotNullOrEmpty()) {
        println(value.length)  // Smart cast works because of contract
    }
}
```

### Backing Fields and Properties

```kotlin
// Backing field with custom getter/setter
class Temperature {
    var celsius: Double = 0.0
        set(value) {
            if (value >= -273.15) {
                field = value  // 'field' is backing field
            }
        }
    
    var fahrenheit: Double
        get() = celsius * 9/5 + 32
        set(value) {
            celsius = (value - 32) * 5/9
        }
}

// Property without backing field
class Rectangle(val width: Int, val height: Int) {
    val area: Int
        get() = width * height  // Computed, no backing field
}

// Backing property
class Manager {
    private val _items = mutableListOf<String>()
    val items: List<String>  // Exposed as read-only
        get() = _items
    
    fun addItem(item: String) {
        _items.add(item)
    }
}
```

### Standard Library Utility Functions

```kotlin
// TODO and NotImplementedError
fun futureFeature() {
    TODO("Implement this later")  // Throws NotImplementedError
}

// repeat
repeat(5) {
    println("Iteration $it")
}

// run
val result = run {
    val a = 10
    val b = 20
    a + b  // Returns 30
}

// lazy
val heavy: String by lazy {
    println("Computing...")
    "Expensive calculation"
}

// synchronized
synchronized(lock) {
    // Thread-safe code
}

// measureTimeMillis
val time = measureTimeMillis {
    // Some operation
}

// require, check, assert
fun divide(a: Int, b: Int): Int {
    require(b != 0) { "Divisor cannot be zero" }
    return a / b
}

// takeIf, takeUnless
val positive = number.takeIf { it > 0 }  // Returns number if positive, else null
val notEmpty = list.takeUnless { it.isEmpty() }  // Returns list if not empty

// also, apply, let, run, with (covered in Scope Functions)

// coerceIn
val value = 150
val clamped = value.coerceIn(0, 100)  // 100

// coerceAtLeast, coerceAtMost
val atLeast = value.coerceAtLeast(50)  // 150
val atMost = value.coerceAtMost(100)  // 100
```

### DSL Building

```kotlin
// Type-safe builders (DSL)
class HTML {
    fun body(init: Body.() -> Unit): Body {
        val body = Body()
        body.init()
        return body
    }
}

class Body {
    private val children = mutableListOf<String>()
    
    fun div(init: Div.() -> Unit) {
        val div = Div()
        div.init()
        children.add(div.toString())
    }
    
    override fun toString() = children.joinToString("\n")
}

class Div {
    var text: String = ""
    
    override fun toString() = "<div>$text</div>"
}

// Usage
fun html(init: HTML.() -> Unit): HTML {
    val html = HTML()
    html.init()
    return html
}

val page = html {
    body {
        div {
            text = "Hello"
        }
        div {
            text = "World"
        }
    }
}

// Real-world example: kotlinx.html
/*
html {
    head {
        title { +"My Page" }
    }
    body {
        h1 { +"Welcome" }
        p { +"This is a paragraph" }
    }
}
*/
```

### Multiplatform Features

```kotlin
// Expect/Actual mechanism for multiplatform
// Common code
expect class Platform() {
    val name: String
}

expect fun currentTimeMillis(): Long

// JVM actual implementation
actual class Platform {
    actual val name: String = "JVM"
}

actual fun currentTimeMillis(): Long = System.currentTimeMillis()

// JS actual implementation
actual class Platform {
    actual val name: String = "JS"
}

actual fun currentTimeMillis(): Long = Date.now().toLong()

// Native actual implementation
actual class Platform {
    actual val name: String = "Native"
}

actual fun currentTimeMillis(): Long = /* platform-specific */
```

### Value Classes (Inline Classes)

```kotlin
// Value class (no runtime overhead)
@JvmInline
value class Password(val value: String)

@JvmInline
value class UserId(val id: Int)

// Usage - type safety without performance cost
fun authenticate(password: Password) {
    // password.value to access underlying value
}

val pwd = Password("secret123")
// authenticate("wrong")  // Compile error - type mismatch
authenticate(pwd)  // Correct

// Value class with additional members
@JvmInline
value class Name(val s: String) {
    init {
        require(s.isNotEmpty()) { "Name cannot be empty" }
    }
    
    val length: Int
        get() = s.length
    
    fun greet() = "Hello, $s"
}
```

### Context Receivers (Experimental)

```kotlin
// Context receivers allow passing context implicitly
interface Logger {
    fun log(message: String)
}

// Function requiring Logger context
context(Logger)
fun performOperation() {
    log("Starting operation")
    // Do work
    log("Operation completed")
}

// Usage
class ConsoleLogger : Logger {
    override fun log(message: String) {
        println("[LOG] $message")
    }
}

with(ConsoleLogger()) {
    performOperation()  // Logger context provided implicitly
}
```

---

## Summary

Kotlin provides numerous improvements over Java:

1. **Conciseness**: Less boilerplate code with features like data classes, type inference, and properties
2. **Safety**: Null safety prevents NPEs at compile time
3. **Expressiveness**: Extension functions, operator overloading, and DSL support
4. **Functional Programming**: First-class functions, lambdas, and higher-order functions
5. **Modern Concurrency**: Coroutines for asynchronous programming
6. **Interoperability**: 100% compatible with Java, can use Java libraries seamlessly

These features make Kotlin more productive and safer while maintaining full Java interoperability.