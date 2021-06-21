# Java Collections Framework - Complete Guide

## Table of Contents
1. [Map Implementations](#map-implementations)
2. [Set Implementations](#set-implementations)
3. [List Implementations](#list-implementations)
4. [Queue Implementations](#queue-implementations)
5. [Quick Reference Table](#quick-reference-table)

---

## Map Implementations

Maps store key-value pairs where each key is unique.

### HashMap

**Characteristics:**
- Uses hash table for storage
- Allows one null key and multiple null values
- No ordering guarantee
- O(1) average time complexity for get/put operations
- Not thread-safe
- Best general-purpose map implementation
- Default initial capacity: 16
- Default load factor: 0.75

**When to Use:**
- Default choice for most scenarios
- When you need fast lookups and don't care about order
- When you're working in a single-threaded environment
- When you need to store null keys or values

**Use Cases:**
- Caching data
- Storing configuration settings
- Counting occurrences of items
- Building indexes

#### HashMap Constructors

```java
// 1. Default constructor (capacity=16, loadFactor=0.75)
HashMap<String, Integer> map1 = new HashMap<>();

// 2. With initial capacity
HashMap<String, Integer> map2 = new HashMap<>(32);

// 3. With initial capacity and load factor
HashMap<String, Integer> map3 = new HashMap<>(32, 0.5f);

// 4. Copy from another map
Map<String, Integer> source = Map.of("a", 1, "b", 2);
HashMap<String, Integer> map4 = new HashMap<>(source);
```

#### HashMap Methods with Examples

**1. put(K key, V value) - Adds or updates a key-value pair**
```java
HashMap<String, Integer> scores = new HashMap<>();
scores.put("Alice", 95);      // Returns null (new key)
scores.put("Bob", 87);        // Returns null (new key)
Integer old = scores.put("Alice", 98);  // Returns 95 (previous value)
System.out.println(old);      // Output: 95
System.out.println(scores);   // Output: {Alice=98, Bob=87}
```

**2. get(Object key) - Retrieves value by key**
```java
HashMap<String, String> capitals = new HashMap<>();
capitals.put("USA", "Washington D.C.");
capitals.put("France", "Paris");
capitals.put("Japan", "Tokyo");

String capital = capitals.get("France");     // Returns "Paris"
String unknown = capitals.get("Germany");    // Returns null
System.out.println(capital);  // Output: Paris
System.out.println(unknown);  // Output: null
```

**3. getOrDefault(Object key, V defaultValue) - Returns value or default if not found**
```java
HashMap<String, Integer> inventory = new HashMap<>();
inventory.put("apples", 50);
inventory.put("oranges", 30);

int appleCount = inventory.getOrDefault("apples", 0);    // Returns 50
int bananaCount = inventory.getOrDefault("bananas", 0);  // Returns 0 (default)
System.out.println(appleCount);   // Output: 50
System.out.println(bananaCount);  // Output: 0
```

**4. containsKey(Object key) - Checks if key exists**
```java
HashMap<String, Double> prices = new HashMap<>();
prices.put("laptop", 999.99);
prices.put("mouse", 29.99);

boolean hasLaptop = prices.containsKey("laptop");    // true
boolean hasTablet = prices.containsKey("tablet");    // false
System.out.println(hasLaptop);  // Output: true
System.out.println(hasTablet);  // Output: false
```

**5. containsValue(Object value) - Checks if value exists**
```java
HashMap<String, String> userRoles = new HashMap<>();
userRoles.put("john", "admin");
userRoles.put("jane", "user");
userRoles.put("bob", "moderator");

boolean hasAdmin = userRoles.containsValue("admin");    // true
boolean hasGuest = userRoles.containsValue("guest");    // false
System.out.println(hasAdmin);  // Output: true
System.out.println(hasGuest);  // Output: false
```

**6. remove(Object key) - Removes entry by key**
```java
HashMap<String, Integer> ages = new HashMap<>();
ages.put("Alice", 25);
ages.put("Bob", 30);
ages.put("Charlie", 35);

Integer removedAge = ages.remove("Bob");      // Returns 30
Integer notFound = ages.remove("David");      // Returns null
System.out.println(removedAge);  // Output: 30
System.out.println(ages);        // Output: {Alice=25, Charlie=35}
```

**7. remove(Object key, Object value) - Removes only if key maps to specified value**
```java
HashMap<String, String> settings = new HashMap<>();
settings.put("theme", "dark");
settings.put("language", "en");

boolean removed1 = settings.remove("theme", "dark");    // true (matches)
boolean removed2 = settings.remove("language", "fr");   // false (value doesn't match)
System.out.println(removed1);    // Output: true
System.out.println(settings);    // Output: {language=en}
```

**8. putAll(Map<? extends K, ? extends V> m) - Copies all entries from another map**
```java
HashMap<String, Integer> map1 = new HashMap<>();
map1.put("a", 1);
map1.put("b", 2);

HashMap<String, Integer> map2 = new HashMap<>();
map2.put("c", 3);
map2.put("d", 4);

map1.putAll(map2);
System.out.println(map1);  // Output: {a=1, b=2, c=3, d=4}
```

**9. putIfAbsent(K key, V value) - Adds only if key doesn't exist**
```java
HashMap<String, String> config = new HashMap<>();
config.put("host", "localhost");

config.putIfAbsent("host", "127.0.0.1");  // Won't change (key exists)
config.putIfAbsent("port", "8080");        // Will add (key doesn't exist)

System.out.println(config.get("host"));  // Output: localhost
System.out.println(config.get("port"));  // Output: 8080
```

**10. replace(K key, V value) - Replaces value only if key exists**
```java
HashMap<String, Integer> stock = new HashMap<>();
stock.put("item1", 100);

Integer old1 = stock.replace("item1", 150);  // Returns 100 (replaced)
Integer old2 = stock.replace("item2", 50);   // Returns null (key doesn't exist)

System.out.println(old1);         // Output: 100
System.out.println(stock);        // Output: {item1=150}
```

**11. replace(K key, V oldValue, V newValue) - Replaces only if key maps to oldValue**
```java
HashMap<String, String> status = new HashMap<>();
status.put("order1", "pending");
status.put("order2", "shipped");

boolean replaced1 = status.replace("order1", "pending", "confirmed");   // true
boolean replaced2 = status.replace("order2", "pending", "confirmed");   // false (value doesn't match)

System.out.println(status);  // Output: {order1=confirmed, order2=shipped}
```

**12. clear() - Removes all entries**
```java
HashMap<String, Integer> data = new HashMap<>();
data.put("x", 1);
data.put("y", 2);
data.put("z", 3);

System.out.println(data.size());  // Output: 3
data.clear();
System.out.println(data.size());  // Output: 0
System.out.println(data.isEmpty()); // Output: true
```

**13. size() - Returns number of entries**
```java
HashMap<String, String> map = new HashMap<>();
System.out.println(map.size());  // Output: 0

map.put("key1", "value1");
map.put("key2", "value2");
System.out.println(map.size());  // Output: 2
```

**14. isEmpty() - Checks if map is empty**
```java
HashMap<String, Integer> map = new HashMap<>();
System.out.println(map.isEmpty());  // Output: true

map.put("item", 1);
System.out.println(map.isEmpty());  // Output: false
```

**15. keySet() - Returns set of all keys**
```java
HashMap<String, Integer> grades = new HashMap<>();
grades.put("Math", 90);
grades.put("English", 85);
grades.put("Science", 92);

Set<String> subjects = grades.keySet();
System.out.println(subjects);  // Output: [Math, English, Science]

// Iterate over keys
for (String subject : grades.keySet()) {
    System.out.println(subject + ": " + grades.get(subject));
}
```

**16. values() - Returns collection of all values**
```java
HashMap<String, Double> prices = new HashMap<>();
prices.put("apple", 1.50);
prices.put("banana", 0.75);
prices.put("orange", 2.00);

Collection<Double> allPrices = prices.values();
System.out.println(allPrices);  // Output: [1.5, 0.75, 2.0]

// Calculate total
double total = 0;
for (Double price : prices.values()) {
    total += price;
}
System.out.println("Total: " + total);  // Output: Total: 4.25
```

**17. entrySet() - Returns set of key-value pairs**
```java
HashMap<String, Integer> population = new HashMap<>();
population.put("New York", 8336817);
population.put("Los Angeles", 3979576);
population.put("Chicago", 2693976);

// Best way to iterate over both keys and values
for (Map.Entry<String, Integer> entry : population.entrySet()) {
    System.out.println(entry.getKey() + " has population: " + entry.getValue());
}

// Output:
// New York has population: 8336817
// Los Angeles has population: 3979576
// Chicago has population: 2693976
```

**18. compute(K key, BiFunction) - Computes new value for key**
```java
HashMap<String, Integer> wordCount = new HashMap<>();
wordCount.put("hello", 1);

// Increment count
wordCount.compute("hello", (key, val) -> val == null ? 1 : val + 1);
wordCount.compute("world", (key, val) -> val == null ? 1 : val + 1);

System.out.println(wordCount);  // Output: {hello=2, world=1}

// Remove entry by returning null
wordCount.compute("hello", (key, val) -> null);
System.out.println(wordCount);  // Output: {world=1}
```

**19. computeIfAbsent(K key, Function) - Computes value only if key is absent**
```java
HashMap<String, List<String>> groupedData = new HashMap<>();

// Creates new list if key doesn't exist, then adds item
groupedData.computeIfAbsent("fruits", k -> new ArrayList<>()).add("apple");
groupedData.computeIfAbsent("fruits", k -> new ArrayList<>()).add("banana");
groupedData.computeIfAbsent("vegetables", k -> new ArrayList<>()).add("carrot");

System.out.println(groupedData);
// Output: {fruits=[apple, banana], vegetables=[carrot]}
```

**20. computeIfPresent(K key, BiFunction) - Computes value only if key exists**
```java
HashMap<String, Integer> scores = new HashMap<>();
scores.put("player1", 100);

// Double the score if player exists
scores.computeIfPresent("player1", (key, val) -> val * 2);
scores.computeIfPresent("player2", (key, val) -> val * 2);  // No effect

System.out.println(scores);  // Output: {player1=200}
```

**21. merge(K key, V value, BiFunction) - Merges values**
```java
HashMap<String, Integer> sales = new HashMap<>();
sales.put("product1", 100);

// Add to existing value or set if absent
sales.merge("product1", 50, Integer::sum);  // 100 + 50 = 150
sales.merge("product2", 75, Integer::sum);  // New entry: 75

System.out.println(sales);  // Output: {product1=150, product2=75}

// Concatenate strings
HashMap<String, String> messages = new HashMap<>();
messages.put("log", "Error: ");
messages.merge("log", "Connection failed", String::concat);
System.out.println(messages.get("log"));  // Output: Error: Connection failed
```

**22. forEach(BiConsumer) - Iterates over all entries**
```java
HashMap<String, Integer> items = new HashMap<>();
items.put("a", 1);
items.put("b", 2);
items.put("c", 3);

// Print all entries
items.forEach((key, value) -> System.out.println(key + " = " + value));

// Modify external state
List<String> keys = new ArrayList<>();
items.forEach((key, value) -> keys.add(key));
System.out.println(keys);  // Output: [a, b, c]
```

**23. replaceAll(BiFunction) - Replaces all values**
```java
HashMap<String, Integer> numbers = new HashMap<>();
numbers.put("a", 1);
numbers.put("b", 2);
numbers.put("c", 3);

// Double all values
numbers.replaceAll((key, value) -> value * 2);
System.out.println(numbers);  // Output: {a=2, b=4, c=6}

// Convert to uppercase for String values
HashMap<String, String> words = new HashMap<>();
words.put("greeting", "hello");
words.put("farewell", "goodbye");
words.replaceAll((key, value) -> value.toUpperCase());
System.out.println(words);  // Output: {greeting=HELLO, farewell=GOODBYE}
```

**24. clone() - Creates shallow copy**
```java
HashMap<String, Integer> original = new HashMap<>();
original.put("x", 10);
original.put("y", 20);

@SuppressWarnings("unchecked")
HashMap<String, Integer> copy = (HashMap<String, Integer>) original.clone();

copy.put("z", 30);
System.out.println(original);  // Output: {x=10, y=20}
System.out.println(copy);      // Output: {x=10, y=20, z=30}
```

#### Practical HashMap Examples

**Word Frequency Counter:**
```java
String text = "the quick brown fox jumps over the lazy dog the fox";
HashMap<String, Integer> wordFreq = new HashMap<>();

for (String word : text.split(" ")) {
    wordFreq.merge(word, 1, Integer::sum);
}
System.out.println(wordFreq);
// Output: {the=3, quick=1, brown=1, fox=2, jumps=1, over=1, lazy=1, dog=1}
```

**Group By Example:**
```java
List<String> names = Arrays.asList("Alice", "Bob", "Anna", "Charlie", "Amy");
HashMap<Character, List<String>> groupedByFirstLetter = new HashMap<>();

for (String name : names) {
    char firstLetter = name.charAt(0);
    groupedByFirstLetter.computeIfAbsent(firstLetter, k -> new ArrayList<>()).add(name);
}
System.out.println(groupedByFirstLetter);
// Output: {A=[Alice, Anna, Amy], B=[Bob], C=[Charlie]}
```

**Two-way Mapping:**
```java
HashMap<String, String> countryToCapital = new HashMap<>();
HashMap<String, String> capitalToCountry = new HashMap<>();

BiConsumer<String, String> addMapping = (country, capital) -> {
    countryToCapital.put(country, capital);
    capitalToCountry.put(capital, country);
};

addMapping.accept("France", "Paris");
addMapping.accept("Japan", "Tokyo");

System.out.println(countryToCapital.get("France"));    // Output: Paris
System.out.println(capitalToCountry.get("Tokyo"));     // Output: Japan
```

---

### LinkedHashMap

**Characteristics:**
- Extends HashMap
- Maintains insertion order (or access order if configured)
- Slightly slower than HashMap due to maintaining linked list
- O(1) time complexity for get/put operations
- Not thread-safe
- Uses more memory than HashMap
- Allows one null key and multiple null values

**When to Use:**
- When you need predictable iteration order
- When you want to maintain insertion order
- Implementing LRU (Least Recently Used) cache
- When you need to process entries in the order they were added

**Use Cases:**
- LRU cache implementation
- Maintaining order in JSON parsing
- Form data processing
- Command history

#### LinkedHashMap Constructors

```java
// 1. Default constructor (insertion order)
LinkedHashMap<String, Integer> map1 = new LinkedHashMap<>();

// 2. With initial capacity
LinkedHashMap<String, Integer> map2 = new LinkedHashMap<>(32);

// 3. With initial capacity and load factor
LinkedHashMap<String, Integer> map3 = new LinkedHashMap<>(32, 0.75f);

// 4. With access order (true = access order, false = insertion order)
LinkedHashMap<String, Integer> map4 = new LinkedHashMap<>(16, 0.75f, true);

// 5. Copy from another map
Map<String, Integer> source = Map.of("a", 1, "b", 2);
LinkedHashMap<String, Integer> map5 = new LinkedHashMap<>(source);
```

#### LinkedHashMap Methods with Examples

**Insertion Order Demonstration:**
```java
LinkedHashMap<String, Integer> insertionOrder = new LinkedHashMap<>();
insertionOrder.put("banana", 2);
insertionOrder.put("apple", 1);
insertionOrder.put("cherry", 3);
insertionOrder.put("date", 4);

// Iteration preserves insertion order
System.out.println("Insertion order:");
insertionOrder.forEach((k, v) -> System.out.println(k + " = " + v));
// Output:
// banana = 2
// apple = 1
// cherry = 3
// date = 4
```

**Access Order Demonstration:**
```java
// accessOrder = true means least-recently-accessed entries come first
LinkedHashMap<String, Integer> accessOrder = new LinkedHashMap<>(16, 0.75f, true);
accessOrder.put("a", 1);
accessOrder.put("b", 2);
accessOrder.put("c", 3);

// Access "a" - moves it to the end
accessOrder.get("a");

// Access "b" - moves it to the end
accessOrder.get("b");

System.out.println("Access order:");
accessOrder.forEach((k, v) -> System.out.println(k + " = " + v));
// Output:
// c = 3  (least recently accessed)
// a = 1
// b = 2  (most recently accessed)
```

**removeEldestEntry() - LRU Cache Implementation:**
```java
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int maxSize;

    public LRUCache(int maxSize) {
        super(maxSize, 0.75f, true);  // accessOrder = true
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }
}

// Usage
LRUCache<String, String> cache = new LRUCache<>(3);
cache.put("a", "1");
cache.put("b", "2");
cache.put("c", "3");
System.out.println(cache);  // {a=1, b=2, c=3}

cache.put("d", "4");  // Exceeds capacity, removes "a" (eldest)
System.out.println(cache);  // {b=2, c=3, d=4}

cache.get("b");  // Access "b", moves it to end
cache.put("e", "5");  // Removes "c" (now eldest)
System.out.println(cache);  // {d=4, b=2, e=5}
```

**Practical Example - Maintaining Form Field Order:**
```java
LinkedHashMap<String, String> formData = new LinkedHashMap<>();
formData.put("firstName", "John");
formData.put("lastName", "Doe");
formData.put("email", "john@example.com");
formData.put("phone", "555-1234");

// Build form HTML in order
StringBuilder html = new StringBuilder("<form>\n");
for (Map.Entry<String, String> entry : formData.entrySet()) {
    html.append(String.format("  <input name=\"%s\" value=\"%s\">\n",
                              entry.getKey(), entry.getValue()));
}
html.append("</form>");
System.out.println(html);
// Fields appear in insertion order
```

**Practical Example - Recent History:**
```java
LinkedHashMap<Long, String> recentSearches = new LinkedHashMap<>(10, 0.75f, true) {
    @Override
    protected boolean removeEldestEntry(Map.Entry<Long, String> eldest) {
        return size() > 5;  // Keep only 5 most recent
    }
};

recentSearches.put(System.currentTimeMillis(), "java collections");
Thread.sleep(10);
recentSearches.put(System.currentTimeMillis(), "linkedhashmap example");
Thread.sleep(10);
recentSearches.put(System.currentTimeMillis(), "treemap vs hashmap");

System.out.println("Recent searches:");
recentSearches.forEach((time, query) -> System.out.println(query));
```

**All HashMap Methods Work in LinkedHashMap:**
```java
LinkedHashMap<String, Integer> map = new LinkedHashMap<>();

// put, get, remove, containsKey, containsValue all work the same
map.put("x", 10);
map.put("y", 20);
map.put("z", 30);

map.getOrDefault("w", 0);  // Returns 0
map.putIfAbsent("x", 100); // No change, returns 10
map.computeIfAbsent("w", k -> 40);  // Adds "w" -> 40

// Order is preserved
System.out.println(map);  // {x=10, y=20, z=30, w=40}
```

---

### TreeMap

**Characteristics:**
- Implements NavigableMap interface
- Stores entries in sorted order (natural ordering or custom Comparator)
- Based on Red-Black tree structure
- O(log n) time complexity for get/put/remove operations
- Does not allow null keys (but allows null values)
- Not thread-safe

**When to Use:**
- When you need sorted order
- When you need range queries (subMap, headMap, tailMap)
- When you need first/last entry operations
- When you need ceiling/floor/higher/lower key operations

**Use Cases:**
- Leaderboards/rankings
- Time-series data
- Range-based queries
- Sorted dictionaries
- Event timelines

#### TreeMap Constructors

```java
// 1. Default constructor (natural ordering)
TreeMap<String, Integer> map1 = new TreeMap<>();

// 2. With custom Comparator
TreeMap<String, Integer> map2 = new TreeMap<>(Comparator.reverseOrder());

// 3. Case-insensitive ordering
TreeMap<String, Integer> map3 = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

// 4. Copy from another map
Map<String, Integer> source = Map.of("b", 2, "a", 1, "c", 3);
TreeMap<String, Integer> map4 = new TreeMap<>(source);

// 5. Copy from another SortedMap (preserves comparator)
SortedMap<String, Integer> sorted = new TreeMap<>(Comparator.reverseOrder());
TreeMap<String, Integer> map5 = new TreeMap<>(sorted);
```

#### TreeMap Methods with Examples

**Basic Operations (Sorted Order):**
```java
TreeMap<String, Integer> ages = new TreeMap<>();
ages.put("Charlie", 35);
ages.put("Alice", 25);
ages.put("Bob", 30);
ages.put("Diana", 28);

// Automatically sorted by key
System.out.println(ages);  // {Alice=25, Bob=30, Charlie=35, Diana=28}

// Iterate in sorted order
ages.forEach((name, age) -> System.out.println(name + ": " + age));
// Alice: 25
// Bob: 30
// Charlie: 35
// Diana: 28
```

**1. firstKey() / lastKey() - Get first/last keys**
```java
TreeMap<Integer, String> scores = new TreeMap<>();
scores.put(85, "Bob");
scores.put(92, "Alice");
scores.put(78, "Charlie");
scores.put(95, "Diana");

Integer lowest = scores.firstKey();   // 78
Integer highest = scores.lastKey();   // 95
System.out.println("Lowest score: " + lowest);   // Output: 78
System.out.println("Highest score: " + highest); // Output: 95
```

**2. firstEntry() / lastEntry() - Get first/last entries**
```java
TreeMap<String, Double> prices = new TreeMap<>();
prices.put("apple", 1.50);
prices.put("banana", 0.75);
prices.put("cherry", 3.00);
prices.put("date", 2.50);

Map.Entry<String, Double> first = prices.firstEntry();  // apple=1.5
Map.Entry<String, Double> last = prices.lastEntry();    // date=2.5

System.out.println("First: " + first.getKey() + " = $" + first.getValue());
System.out.println("Last: " + last.getKey() + " = $" + last.getValue());
```

**3. pollFirstEntry() / pollLastEntry() - Remove and return first/last entry**
```java
TreeMap<Integer, String> queue = new TreeMap<>();
queue.put(3, "Low priority");
queue.put(1, "High priority");
queue.put(2, "Medium priority");

// Process in priority order (removes entry)
Map.Entry<Integer, String> highest = queue.pollFirstEntry();
System.out.println("Processing: " + highest.getValue());  // High priority
System.out.println("Remaining: " + queue);  // {2=Medium priority, 3=Low priority}

// Remove lowest priority
Map.Entry<Integer, String> lowest = queue.pollLastEntry();
System.out.println("Removed: " + lowest.getValue());  // Low priority
```

**4. lowerKey() / higherKey() - Get adjacent keys (exclusive)**
```java
TreeMap<Integer, String> map = new TreeMap<>();
map.put(10, "ten");
map.put(20, "twenty");
map.put(30, "thirty");
map.put(40, "forty");

Integer lowerThan25 = map.lowerKey(25);   // 20 (strictly less than 25)
Integer higherThan25 = map.higherKey(25); // 30 (strictly greater than 25)

Integer lowerThan20 = map.lowerKey(20);   // 10 (strictly less than 20)
Integer higherThan20 = map.higherKey(20); // 30 (strictly greater than 20)

System.out.println("Lower than 25: " + lowerThan25);   // 20
System.out.println("Higher than 25: " + higherThan25); // 30
```

**5. floorKey() / ceilingKey() - Get adjacent keys (inclusive)**
```java
TreeMap<Integer, String> map = new TreeMap<>();
map.put(10, "ten");
map.put(20, "twenty");
map.put(30, "thirty");
map.put(40, "forty");

Integer floorOf25 = map.floorKey(25);     // 20 (≤ 25)
Integer ceilingOf25 = map.ceilingKey(25); // 30 (≥ 25)

Integer floorOf20 = map.floorKey(20);     // 20 (≤ 20, includes exact match)
Integer ceilingOf20 = map.ceilingKey(20); // 20 (≥ 20, includes exact match)

System.out.println("Floor of 25: " + floorOf25);     // 20
System.out.println("Ceiling of 25: " + ceilingOf25); // 30
System.out.println("Floor of 20: " + floorOf20);     // 20
System.out.println("Ceiling of 20: " + ceilingOf20); // 20
```

**6. lowerEntry() / higherEntry() / floorEntry() / ceilingEntry()**
```java
TreeMap<Integer, String> grades = new TreeMap<>();
grades.put(60, "D");
grades.put(70, "C");
grades.put(80, "B");
grades.put(90, "A");

int score = 75;

Map.Entry<Integer, String> floor = grades.floorEntry(score);
Map.Entry<Integer, String> ceiling = grades.ceilingEntry(score);

System.out.println("Score: " + score);
System.out.println("Grade (floor): " + floor.getValue());    // C (70)
System.out.println("Next grade: " + ceiling.getValue());     // B (80)
```

**7. subMap() - Get a range view**
```java
TreeMap<Integer, String> months = new TreeMap<>();
months.put(1, "January");
months.put(2, "February");
months.put(3, "March");
months.put(4, "April");
months.put(5, "May");
months.put(6, "June");

// subMap(fromKey inclusive, toKey exclusive)
SortedMap<Integer, String> q1 = months.subMap(1, 4);
System.out.println("Q1: " + q1);  // {1=January, 2=February, 3=March}

// NavigableMap version with inclusive flags
NavigableMap<Integer, String> q2 = months.subMap(4, true, 6, true);
System.out.println("Q2: " + q2);  // {4=April, 5=May, 6=June}
```

**8. headMap() - Get entries before a key**
```java
TreeMap<String, Integer> inventory = new TreeMap<>();
inventory.put("apple", 50);
inventory.put("banana", 30);
inventory.put("cherry", 20);
inventory.put("date", 40);
inventory.put("elderberry", 15);

// All entries with keys < "cherry"
SortedMap<String, Integer> beforeCherry = inventory.headMap("cherry");
System.out.println(beforeCherry);  // {apple=50, banana=30}

// All entries with keys <= "cherry" (inclusive)
NavigableMap<String, Integer> upToCherry = inventory.headMap("cherry", true);
System.out.println(upToCherry);  // {apple=50, banana=30, cherry=20}
```

**9. tailMap() - Get entries from a key onwards**
```java
TreeMap<Integer, String> events = new TreeMap<>();
events.put(900, "Meeting");
events.put(1200, "Lunch");
events.put(1400, "Presentation");
events.put(1600, "Review");
events.put(1800, "End of day");

// All events from 1200 onwards
SortedMap<Integer, String> afternoon = events.tailMap(1200);
System.out.println(afternoon);  // {1200=Lunch, 1400=Presentation, 1600=Review, 1800=End of day}

// All events after 1200 (exclusive)
NavigableMap<Integer, String> afterLunch = events.tailMap(1200, false);
System.out.println(afterLunch);  // {1400=Presentation, 1600=Review, 1800=End of day}
```

**10. descendingMap() - Reverse order view**
```java
TreeMap<Integer, String> rankings = new TreeMap<>();
rankings.put(1, "Gold");
rankings.put(2, "Silver");
rankings.put(3, "Bronze");

NavigableMap<Integer, String> reversed = rankings.descendingMap();
System.out.println("Normal: " + rankings);    // {1=Gold, 2=Silver, 3=Bronze}
System.out.println("Reversed: " + reversed);  // {3=Bronze, 2=Silver, 1=Gold}

// Iterate in descending order
reversed.forEach((rank, medal) -> System.out.println(rank + ": " + medal));
```

**11. descendingKeySet() - Keys in reverse order**
```java
TreeMap<String, Integer> scores = new TreeMap<>();
scores.put("Alice", 95);
scores.put("Bob", 87);
scores.put("Charlie", 92);

NavigableSet<String> descendingKeys = scores.descendingKeySet();
System.out.println(descendingKeys);  // [Charlie, Bob, Alice]
```

**12. navigableKeySet() - NavigableSet view of keys**
```java
TreeMap<Integer, String> map = new TreeMap<>();
map.put(1, "one");
map.put(2, "two");
map.put(3, "three");
map.put(4, "four");
map.put(5, "five");

NavigableSet<Integer> keys = map.navigableKeySet();

// Use NavigableSet methods
System.out.println("Lower than 3: " + keys.lower(3));    // 2
System.out.println("Higher than 3: " + keys.higher(3));  // 4
System.out.println("Subset 2-4: " + keys.subSet(2, true, 4, true));  // [2, 3, 4]
```

**13. comparator() - Get the comparator**
```java
TreeMap<String, Integer> natural = new TreeMap<>();
TreeMap<String, Integer> reversed = new TreeMap<>(Comparator.reverseOrder());

System.out.println("Natural comparator: " + natural.comparator());   // null (natural ordering)
System.out.println("Reversed comparator: " + reversed.comparator()); // Comparator instance
```

#### Practical TreeMap Examples

**Leaderboard with Reverse Order:**
```java
TreeMap<Integer, List<String>> leaderboard = new TreeMap<>(Comparator.reverseOrder());

// Add players (score -> list of players with that score)
leaderboard.computeIfAbsent(1000, k -> new ArrayList<>()).add("Alice");
leaderboard.computeIfAbsent(850, k -> new ArrayList<>()).add("Bob");
leaderboard.computeIfAbsent(1000, k -> new ArrayList<>()).add("Charlie");
leaderboard.computeIfAbsent(920, k -> new ArrayList<>()).add("Diana");

// Display leaderboard (highest to lowest)
int rank = 1;
for (Map.Entry<Integer, List<String>> entry : leaderboard.entrySet()) {
    for (String player : entry.getValue()) {
        System.out.println(rank + ". " + player + " - " + entry.getKey() + " pts");
    }
    rank += entry.getValue().size();
}
// Output:
// 1. Alice - 1000 pts
// 1. Charlie - 1000 pts
// 3. Diana - 920 pts
// 4. Bob - 850 pts
```

**Time-Based Event Scheduler:**
```java
TreeMap<LocalTime, String> schedule = new TreeMap<>();
schedule.put(LocalTime.of(9, 0), "Standup meeting");
schedule.put(LocalTime.of(10, 30), "Code review");
schedule.put(LocalTime.of(12, 0), "Lunch");
schedule.put(LocalTime.of(14, 0), "Sprint planning");
schedule.put(LocalTime.of(16, 30), "Team sync");

LocalTime now = LocalTime.of(11, 0);

// Next upcoming event
Map.Entry<LocalTime, String> nextEvent = schedule.ceilingEntry(now);
System.out.println("Next event: " + nextEvent.getValue() + " at " + nextEvent.getKey());
// Output: Next event: Lunch at 12:00

// All remaining events today
NavigableMap<LocalTime, String> remaining = schedule.tailMap(now, true);
System.out.println("Remaining events: " + remaining);
```

**Grade Calculator:**
```java
TreeMap<Integer, String> gradeScale = new TreeMap<>();
gradeScale.put(90, "A");
gradeScale.put(80, "B");
gradeScale.put(70, "C");
gradeScale.put(60, "D");
gradeScale.put(0, "F");

public String getGrade(int score) {
    Map.Entry<Integer, String> grade = gradeScale.floorEntry(score);
    return grade != null ? grade.getValue() : "Invalid";
}

System.out.println(getGrade(95));  // A
System.out.println(getGrade(82));  // B
System.out.println(getGrade(75));  // C
System.out.println(getGrade(55));  // F
```

---

### Hashtable

**Characteristics:**
- Legacy class (since Java 1.0)
- Thread-safe (all methods are synchronized)
- Does not allow null keys or values
- Slower than HashMap due to synchronization overhead
- Generally considered obsolete
- Extends Dictionary class (legacy)

**When to Use:**
- Legacy code maintenance
- When you absolutely need thread-safety AND null-safety
- **Generally, you should use ConcurrentHashMap instead**

**Use Cases:**
- Maintaining legacy applications
- **Modern alternative: Use ConcurrentHashMap or synchronized HashMap**

#### Hashtable Constructors

```java
// 1. Default constructor (capacity=11, loadFactor=0.75)
Hashtable<String, Integer> table1 = new Hashtable<>();

// 2. With initial capacity
Hashtable<String, Integer> table2 = new Hashtable<>(20);

// 3. With initial capacity and load factor
Hashtable<String, Integer> table3 = new Hashtable<>(20, 0.8f);

// 4. Copy from another map
Map<String, Integer> source = Map.of("a", 1, "b", 2);
Hashtable<String, Integer> table4 = new Hashtable<>(source);
```

#### Hashtable Methods with Examples

**Basic Operations:**
```java
Hashtable<String, Integer> table = new Hashtable<>();

// put - Add entry
table.put("one", 1);
table.put("two", 2);
table.put("three", 3);

// get - Retrieve value
Integer value = table.get("two");  // 2
Integer missing = table.get("four");  // null

// remove - Delete entry
Integer removed = table.remove("one");  // 1
System.out.println(table);  // {three=3, two=2}
```

**Null Values Not Allowed:**
```java
Hashtable<String, String> table = new Hashtable<>();

try {
    table.put(null, "value");  // Throws NullPointerException!
} catch (NullPointerException e) {
    System.out.println("Cannot put null key");
}

try {
    table.put("key", null);  // Throws NullPointerException!
} catch (NullPointerException e) {
    System.out.println("Cannot put null value");
}

// Compare with HashMap (which allows null)
HashMap<String, String> map = new HashMap<>();
map.put(null, "value");  // OK
map.put("key", null);    // OK
```

**Thread-Safety Demonstration:**
```java
Hashtable<String, Integer> sharedTable = new Hashtable<>();

// Multiple threads can safely access
Runnable writer = () -> {
    for (int i = 0; i < 1000; i++) {
        sharedTable.put("key" + i, i);
    }
};

Runnable reader = () -> {
    for (int i = 0; i < 1000; i++) {
        sharedTable.get("key" + i);
    }
};

Thread t1 = new Thread(writer);
Thread t2 = new Thread(reader);
t1.start();
t2.start();
// Both threads can operate safely (but performance is poor)
```

**Legacy Methods (from Dictionary class):**
```java
Hashtable<String, Integer> table = new Hashtable<>();
table.put("a", 1);
table.put("b", 2);
table.put("c", 3);

// elements() - Returns Enumeration of values (legacy)
Enumeration<Integer> values = table.elements();
while (values.hasMoreElements()) {
    System.out.println(values.nextElement());
}

// keys() - Returns Enumeration of keys (legacy)
Enumeration<String> keys = table.keys();
while (keys.hasMoreElements()) {
    System.out.println(keys.nextElement());
}

// Modern alternative: Use keySet(), values(), entrySet()
for (String key : table.keySet()) {
    System.out.println(key + " = " + table.get(key));
}
```

**contains() vs containsValue():**
```java
Hashtable<String, String> table = new Hashtable<>();
table.put("fruit", "apple");
table.put("vegetable", "carrot");

// contains() - Legacy method, same as containsValue()
boolean hasApple1 = table.contains("apple");      // true
boolean hasApple2 = table.containsValue("apple"); // true (preferred)

// containsKey()
boolean hasFruit = table.containsKey("fruit");    // true
```

**Why You Should Use ConcurrentHashMap Instead:**
```java
// Hashtable - Poor concurrency (locks entire table)
Hashtable<String, Integer> hashtable = new Hashtable<>();
// Every operation locks the entire table

// ConcurrentHashMap - Better concurrency (segment-level locking)
ConcurrentHashMap<String, Integer> concurrent = new ConcurrentHashMap<>();
// Multiple threads can read/write different segments simultaneously

// Modern synchronized HashMap (if you really need one)
Map<String, Integer> syncMap = Collections.synchronizedMap(new HashMap<>());
```

**Migration Example:**
```java
// OLD (Hashtable)
Hashtable<String, String> oldConfig = new Hashtable<>();
oldConfig.put("host", "localhost");
oldConfig.put("port", "8080");

// NEW (ConcurrentHashMap) - Drop-in replacement for most cases
ConcurrentHashMap<String, String> newConfig = new ConcurrentHashMap<>();
newConfig.put("host", "localhost");
newConfig.put("port", "8080");

// Note: If you need null values, use synchronized HashMap instead
Map<String, String> nullableConfig = Collections.synchronizedMap(new HashMap<>());
nullableConfig.put("optional", null);  // OK
```

---

### ConcurrentHashMap

**Characteristics:**
- Thread-safe without locking the entire map
- Uses lock striping (segments) for better concurrency
- Does not allow null keys or values
- O(1) average time complexity
- Better performance than Hashtable in multi-threaded scenarios
- Weakly consistent iterators (don't throw ConcurrentModificationException)
- Default concurrency level: 16

**When to Use:**
- Multi-threaded applications
- High-concurrency scenarios
- When multiple threads read and write simultaneously
- Shared caches in web applications

**Use Cases:**
- Web application session management
- Shared caches
- Counters and metrics in multi-threaded apps
- Real-time data processing systems

#### ConcurrentHashMap Constructors

```java
// 1. Default constructor
ConcurrentHashMap<String, Integer> map1 = new ConcurrentHashMap<>();

// 2. With initial capacity
ConcurrentHashMap<String, Integer> map2 = new ConcurrentHashMap<>(32);

// 3. With initial capacity and load factor
ConcurrentHashMap<String, Integer> map3 = new ConcurrentHashMap<>(32, 0.75f);

// 4. With initial capacity, load factor, and concurrency level
ConcurrentHashMap<String, Integer> map4 = new ConcurrentHashMap<>(32, 0.75f, 16);

// 5. Copy from another map
Map<String, Integer> source = Map.of("a", 1, "b", 2);
ConcurrentHashMap<String, Integer> map5 = new ConcurrentHashMap<>(source);
```

#### ConcurrentHashMap Methods with Examples

**1. putIfAbsent() - Atomic put only if key is absent**
```java
ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

// Thread-safe: only adds if key doesn't exist
String result1 = cache.putIfAbsent("user1", "John");   // Returns null, adds entry
String result2 = cache.putIfAbsent("user1", "Jane");   // Returns "John", doesn't change

System.out.println(result1);            // null
System.out.println(result2);            // John
System.out.println(cache.get("user1")); // John
```

**2. remove(key, value) - Atomic conditional remove**
```java
ConcurrentHashMap<String, String> sessions = new ConcurrentHashMap<>();
sessions.put("session1", "user123");

// Only removes if key maps to specified value
boolean removed1 = sessions.remove("session1", "user123");  // true
boolean removed2 = sessions.remove("session1", "user456");  // false (already removed or wrong value)

System.out.println(removed1);  // true
System.out.println(removed2);  // false
```

**3. replace(key, value) - Atomic replace if key exists**
```java
ConcurrentHashMap<String, Integer> inventory = new ConcurrentHashMap<>();
inventory.put("item1", 100);

Integer old = inventory.replace("item1", 150);  // Returns 100, replaces value
Integer missing = inventory.replace("item2", 50);  // Returns null, does nothing

System.out.println(old);       // 100
System.out.println(missing);   // null
System.out.println(inventory); // {item1=150}
```

**4. replace(key, oldValue, newValue) - Atomic compare-and-swap**
```java
ConcurrentHashMap<String, String> status = new ConcurrentHashMap<>();
status.put("order1", "pending");

// Only replaces if current value matches expected value
boolean success1 = status.replace("order1", "pending", "processing");  // true
boolean success2 = status.replace("order1", "pending", "shipped");     // false (value changed)

System.out.println(success1);            // true
System.out.println(success2);            // false
System.out.println(status.get("order1")); // processing
```

**5. computeIfAbsent() - Atomic compute if key absent**
```java
ConcurrentHashMap<String, List<String>> groupedData = new ConcurrentHashMap<>();

// Thread-safe: creates list only if key doesn't exist
groupedData.computeIfAbsent("fruits", k -> new ArrayList<>()).add("apple");
groupedData.computeIfAbsent("fruits", k -> new ArrayList<>()).add("banana");
groupedData.computeIfAbsent("vegetables", k -> new ArrayList<>()).add("carrot");

System.out.println(groupedData);
// {fruits=[apple, banana], vegetables=[carrot]}

// Useful for caching expensive computations
ConcurrentHashMap<Integer, BigInteger> factorialCache = new ConcurrentHashMap<>();
BigInteger result = factorialCache.computeIfAbsent(10, n -> computeFactorial(n));
```

**6. computeIfPresent() - Atomic compute if key exists**
```java
ConcurrentHashMap<String, Integer> scores = new ConcurrentHashMap<>();
scores.put("player1", 100);

// Thread-safe: only updates if key exists
scores.computeIfPresent("player1", (key, val) -> val + 50);  // Now 150
scores.computeIfPresent("player2", (key, val) -> val + 50);  // No effect

System.out.println(scores);  // {player1=150}
```

**7. compute() - Atomic compute (insert, update, or remove)**
```java
ConcurrentHashMap<String, Integer> wordCount = new ConcurrentHashMap<>();

// Increment count atomically
String[] words = {"apple", "banana", "apple", "cherry", "apple"};
for (String word : words) {
    wordCount.compute(word, (key, val) -> val == null ? 1 : val + 1);
}

System.out.println(wordCount);  // {apple=3, banana=1, cherry=1}

// Remove entry by returning null
wordCount.compute("banana", (key, val) -> null);
System.out.println(wordCount);  // {apple=3, cherry=1}
```

**8. merge() - Atomic merge operation**
```java
ConcurrentHashMap<String, Integer> sales = new ConcurrentHashMap<>();

// Thread-safe: combines values or inserts if absent
sales.merge("product1", 100, Integer::sum);  // Inserts 100
sales.merge("product1", 50, Integer::sum);   // Adds 50, now 150
sales.merge("product2", 75, Integer::sum);   // Inserts 75

System.out.println(sales);  // {product1=150, product2=75}

// Remove by returning null in merge function
sales.merge("product2", 75, (oldVal, newVal) -> null);
System.out.println(sales);  // {product1=150}
```

**9. forEach() with parallelism threshold**
```java
ConcurrentHashMap<String, Integer> data = new ConcurrentHashMap<>();
for (int i = 0; i < 10000; i++) {
    data.put("key" + i, i);
}

// Sequential forEach (threshold = Long.MAX_VALUE)
data.forEach(Long.MAX_VALUE, (key, value) -> {
    // Processes sequentially
});

// Parallel forEach (threshold = 1 means always parallel)
data.forEach(1, (key, value) -> {
    System.out.println(Thread.currentThread().getName() + ": " + key);
});

// Parallel with threshold (parallel if size > threshold)
data.forEach(100, (key, value) -> {
    // Parallel if map has more than 100 elements
});
```

**10. search() - Parallel search**
```java
ConcurrentHashMap<String, Integer> scores = new ConcurrentHashMap<>();
scores.put("Alice", 85);
scores.put("Bob", 92);
scores.put("Charlie", 78);
scores.put("Diana", 95);

// Find first entry with value > 90 (parallel search)
String highScorer = scores.search(1, (key, value) -> value > 90 ? key : null);
System.out.println("High scorer: " + highScorer);  // Bob or Diana

// Search keys only
String result = scores.searchKeys(1, key -> key.startsWith("C") ? key : null);
System.out.println("Starts with C: " + result);  // Charlie

// Search values only
Integer highScore = scores.searchValues(1, value -> value > 90 ? value : null);
System.out.println("High score: " + highScore);  // 92 or 95
```

**11. reduce() - Parallel reduction**
```java
ConcurrentHashMap<String, Integer> prices = new ConcurrentHashMap<>();
prices.put("item1", 100);
prices.put("item2", 200);
prices.put("item3", 150);
prices.put("item4", 300);

// Sum all values (parallel)
Integer total = prices.reduceValues(1, Integer::sum);
System.out.println("Total: " + total);  // 750

// Find max value
Integer max = prices.reduceValues(1, Integer::max);
System.out.println("Max: " + max);  // 300

// Transform and reduce
Integer sumOfSquares = prices.reduceValues(1, v -> v * v, Integer::sum);
System.out.println("Sum of squares: " + sumOfSquares);  // 160000

// Count entries
long count = prices.reduceToLong(1, (k, v) -> 1L, 0L, Long::sum);
System.out.println("Count: " + count);  // 4
```

**12. getOrDefault() - Get with default value**
```java
ConcurrentHashMap<String, Integer> config = new ConcurrentHashMap<>();
config.put("timeout", 30);

int timeout = config.getOrDefault("timeout", 60);  // 30
int retries = config.getOrDefault("retries", 3);   // 3 (default)

System.out.println(timeout);  // 30
System.out.println(retries);  // 3
```

**13. KeySet views**
```java
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
map.put("a", 1);
map.put("b", 2);
map.put("c", 3);

// Get keySet view backed by map
ConcurrentHashMap.KeySetView<String, Integer> keySet = map.keySet();
System.out.println(keySet);  // [a, b, c]

// Create keySet with default value (acts like a Set)
ConcurrentHashMap.KeySetView<String, Boolean> set = ConcurrentHashMap.newKeySet();
set.add("item1");
set.add("item2");
System.out.println(set);  // [item1, item2]

// With initial capacity
ConcurrentHashMap.KeySetView<String, Boolean> largeSet = ConcurrentHashMap.newKeySet(1000);
```

#### Practical ConcurrentHashMap Examples

**Thread-Safe Counter:**
```java
ConcurrentHashMap<String, LongAdder> requestCounters = new ConcurrentHashMap<>();

// Increment counter (thread-safe)
public void recordRequest(String endpoint) {
    requestCounters.computeIfAbsent(endpoint, k -> new LongAdder()).increment();
}

// Get count
public long getRequestCount(String endpoint) {
    LongAdder counter = requestCounters.get(endpoint);
    return counter != null ? counter.sum() : 0;
}

// Usage from multiple threads
recordRequest("/api/users");
recordRequest("/api/users");
recordRequest("/api/products");
System.out.println(getRequestCount("/api/users"));  // 2
```

**Session Management:**
```java
ConcurrentHashMap<String, Session> sessionStore = new ConcurrentHashMap<>();

// Create session if not exists
public Session getOrCreateSession(String sessionId) {
    return sessionStore.computeIfAbsent(sessionId, id -> new Session(id));
}

// Update session (only if it exists)
public void updateLastAccess(String sessionId) {
    sessionStore.computeIfPresent(sessionId, (id, session) -> {
        session.setLastAccess(System.currentTimeMillis());
        return session;
    });
}

// Remove expired sessions
public void cleanupExpiredSessions(long maxAge) {
    long now = System.currentTimeMillis();
    sessionStore.forEach((id, session) -> {
        if (now - session.getLastAccess() > maxAge) {
            sessionStore.remove(id, session);  // Atomic remove
        }
    });
}
```

**Cache with Expiration:**
```java
ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

class CacheEntry {
    Object value;
    long expireTime;

    CacheEntry(Object value, long ttlMillis) {
        this.value = value;
        this.expireTime = System.currentTimeMillis() + ttlMillis;
    }

    boolean isExpired() {
        return System.currentTimeMillis() > expireTime;
    }
}

// Get with expiration check
public Object get(String key) {
    CacheEntry entry = cache.get(key);
    if (entry == null || entry.isExpired()) {
        cache.remove(key);  // Clean up expired
        return null;
    }
    return entry.value;
}

// Put with TTL
public void put(String key, Object value, long ttlMillis) {
    cache.put(key, new CacheEntry(value, ttlMillis));
}
```

**Rate Limiter:**
```java
ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
private static final int MAX_REQUESTS_PER_MINUTE = 100;

public boolean allowRequest(String clientId) {
    AtomicInteger count = requestCounts.computeIfAbsent(clientId, k -> new AtomicInteger(0));
    int currentCount = count.incrementAndGet();
    return currentCount <= MAX_REQUESTS_PER_MINUTE;
}

// Reset counts every minute (call from scheduled task)
public void resetCounts() {
    requestCounts.clear();
}
```

**Parallel Processing:**
```java
ConcurrentHashMap<String, Integer> data = new ConcurrentHashMap<>();
// Populate with data...

// Process all entries in parallel
data.forEach(1, (key, value) -> {
    // Each entry processed potentially in different thread
    processEntry(key, value);
});

// Parallel transformation and collection
List<String> results = new CopyOnWriteArrayList<>();
data.forEach(1, (key, value) -> {
    if (value > threshold) {
        results.add(key + ":" + value);
    }
});
```

---

## Set Implementations

Sets store unique elements (no duplicates).

### HashSet

**Characteristics:**
- Backed by HashMap internally
- No ordering guarantee
- Allows one null element
- O(1) average time complexity for add/remove/contains
- Not thread-safe
- Best general-purpose set implementation

**When to Use:**
- When you need to eliminate duplicates
- When you don't care about order
- When you need fast membership testing
- Single-threaded scenarios

**Example Scenario:**
```java
// Remove duplicates from a list
List<String> listWithDuplicates = Arrays.asList("a", "b", "a", "c");
HashSet<String> uniqueItems = new HashSet<>(listWithDuplicates);

// Check membership
HashSet<String> bannedUsers = new HashSet<>();
if (bannedUsers.contains(userId)) {
    // Deny access
}

// Find unique visitors
HashSet<String> uniqueVisitors = new HashSet<>();
uniqueVisitors.add(ipAddress);
```

**Use Cases:**
- Removing duplicates
- Fast membership testing
- Set operations (union, intersection, difference)
- Storing unique identifiers

---

### LinkedHashSet

**Characteristics:**
- Extends HashSet
- Maintains insertion order
- Slightly slower than HashSet
- O(1) time complexity for basic operations
- Uses more memory than HashSet
- Not thread-safe

**When to Use:**
- When you need unique elements in insertion order
- When you want predictable iteration order
- When order matters but you still need uniqueness

**Example Scenario:**
```java
// Maintain order of tags
LinkedHashSet<String> tags = new LinkedHashSet<>();
tags.add("java");
tags.add("programming");
tags.add("java"); // Won't be added (duplicate)
// Iterates as: java, programming

// Preserve order of user selections
LinkedHashSet<String> selectedOptions = new LinkedHashSet<>();
selectedOptions.add("Option A");
selectedOptions.add("Option C");
selectedOptions.add("Option B");
```

**Use Cases:**
- Maintaining order of user selections
- Ordered tag systems
- Processing items in order without duplicates
- Unique ordered playlists

---

### TreeSet

**Characteristics:**
- Implements NavigableSet interface
- Stores elements in sorted order
- Based on TreeMap internally (Red-Black tree)
- O(log n) time complexity for basic operations
- Does not allow null elements
- Not thread-safe

**When to Use:**
- When you need sorted unique elements
- When you need range queries on sets
- When you need first/last/ceiling/floor operations
- When natural ordering or custom sorting is required

**Example Scenario:**
```java
// Sorted unique numbers
TreeSet<Integer> scores = new TreeSet<>();
scores.add(85);
scores.add(92);
scores.add(78);
// Iterates as: 78, 85, 92

// Get scores within a range
SortedSet<Integer> passingScores = scores.subSet(60, 100);

// Custom sorting
TreeSet<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
names.add("Alice");
names.add("bob");
names.add("Charlie");

// Get highest/lowest
Integer highest = scores.last();
Integer lowest = scores.first();
```

**Use Cases:**
- Maintaining sorted unique collections
- Leaderboards
- Priority-based processing
- Range-based queries on unique elements
- Sorted word lists

---

## List Implementations

Lists store ordered elements with duplicates allowed and index-based access.

### ArrayList

**Characteristics:**
- Backed by dynamic array
- Fast random access: O(1)
- Slow insertion/deletion in middle: O(n)
- Fast append: O(1) amortized
- Not thread-safe
- Better for read-heavy operations
- Default initial capacity: 10
- Grows by 50% when capacity is exceeded

**When to Use:**
- Default choice for most list scenarios
- When you need fast random access by index
- When you mostly append elements
- When you iterate frequently
- When you know approximate size in advance

**Example Scenario:**
```java
// General-purpose list
ArrayList<String> names = new ArrayList<>();
names.add("Alice");
names.add("Bob");
String first = names.get(0); // Fast O(1) access

// Pre-sized for performance
ArrayList<Integer> numbers = new ArrayList<>(1000);

// Frequent reads, occasional writes
ArrayList<Product> catalog = new ArrayList<>();
// Fast iteration and random access
for (Product p : catalog) {
    // Process product
}
```

**Use Cases:**
- Most general-purpose list operations
- Storing collections of objects
- Implementing stacks
- Caching frequently accessed data
- Building result sets

---

### LinkedList

**Characteristics:**
- Doubly-linked list implementation
- Slow random access: O(n)
- Fast insertion/deletion at ends: O(1)
- Fast insertion/deletion in middle (if you have iterator): O(1)
- Implements both List and Deque interfaces
- Not thread-safe
- Uses more memory per element (stores node references)

**When to Use:**
- When you frequently insert/remove from beginning or middle
- When you don't need random access
- When implementing queues or deques
- When you iterate and modify during iteration

**Example Scenario:**
```java
// Queue implementation
LinkedList<Task> taskQueue = new LinkedList<>();
taskQueue.addLast(new Task("Task 1"));
Task next = taskQueue.removeFirst(); // O(1)

// Frequent insertion/deletion
LinkedList<String> editHistory = new LinkedList<>();
editHistory.addFirst("Latest edit");

// Iterator-based removal during traversal
LinkedList<String> items = new LinkedList<>();
Iterator<String> it = items.iterator();
while (it.hasNext()) {
    String item = it.next();
    if (shouldRemove(item)) {
        it.remove(); // Efficient with LinkedList
    }
}
```

**Use Cases:**
- Implementing queues and deques
- Frequent insertion/deletion operations
- Undo/redo functionality
- Browser history (back/forward)
- Music playlist with frequent reordering

---

### Vector

**Characteristics:**
- Legacy class (since Java 1.0)
- Thread-safe (all methods are synchronized)
- Similar to ArrayList but synchronized
- Grows by 100% (doubles) when capacity exceeded
- Slower than ArrayList due to synchronization
- Generally considered obsolete

**When to Use:**
- Legacy code maintenance
- **Modern alternative: Use ArrayList with Collections.synchronizedList() or CopyOnWriteArrayList**

**Example Scenario:**
```java
// Legacy code only
Vector<String> oldList = new Vector<>();

// Modern alternative
List<String> syncList = Collections.synchronizedList(new ArrayList<>());
```

**Use Cases:**
- Maintaining legacy applications
- **Avoid in new code - use ArrayList or synchronized alternatives**

---

## Queue Implementations

Queues store elements for processing in specific order.

### PriorityQueue

**Characteristics:**
- Unbounded priority queue based on binary heap
- Elements ordered by natural ordering or Comparator
- Does not allow null elements
- O(log n) time for insertion and removal
- O(1) for peek
- Not thread-safe
- Not FIFO - orders by priority

**When to Use:**
- When you need to process elements by priority
- Task scheduling based on priority
- When you need efficient access to min/max element
- Event-driven simulations

**Example Scenario:**
```java
// Task scheduling by priority
PriorityQueue<Task> taskQueue = new PriorityQueue<>(
    Comparator.comparingInt(Task::getPriority)
);
taskQueue.offer(new Task("Low priority", 3));
taskQueue.offer(new Task("High priority", 1));
Task next = taskQueue.poll(); // Gets "High priority"

// Finding k largest elements
PriorityQueue<Integer> minHeap = new PriorityQueue<>(k);
for (int num : numbers) {
    minHeap.offer(num);
    if (minHeap.size() > k) {
        minHeap.poll();
    }
}

// Event simulation
PriorityQueue<Event> events = new PriorityQueue<>(
    Comparator.comparing(Event::getTimestamp)
);
```

**Use Cases:**
- Task scheduling systems
- Dijkstra's algorithm
- Huffman coding
- Simulation systems
- Finding k largest/smallest elements
- CPU task scheduling

---

### Deque (ArrayDeque)

**Characteristics:**
- Double-ended queue (pronounced "deck")
- Backed by resizable array
- Can add/remove from both ends efficiently: O(1)
- Faster than LinkedList for most operations
- Not thread-safe
- Does not allow null elements
- No capacity restrictions

**When to Use:**
- When you need a stack (better than Stack class)
- When you need a queue (better than LinkedList)
- When you need to add/remove from both ends
- General-purpose deque operations

**Example Scenario:**
```java
// Stack implementation (better than Stack class)
Deque<String> stack = new ArrayDeque<>();
stack.push("First");
stack.push("Second");
String top = stack.pop(); // "Second"

// Queue implementation
Deque<Order> orderQueue = new ArrayDeque<>();
orderQueue.offerLast(new Order(1));
Order next = orderQueue.pollFirst();

// Sliding window
Deque<Integer> window = new ArrayDeque<>();
for (int num : array) {
    window.offerLast(num);
    if (window.size() > windowSize) {
        window.pollFirst();
    }
}

// Undo/Redo functionality
Deque<Action> undoStack = new ArrayDeque<>();
Deque<Action> redoStack = new ArrayDeque<>();
```

**Use Cases:**
- Stack implementation (replaces Stack class)
- Queue implementation
- Sliding window algorithms
- Undo/Redo functionality
- Browser history
- BFS/DFS algorithms

---

### ArrayBlockingQueue

**Characteristics:**
- Bounded blocking queue backed by array
- Thread-safe
- Blocking operations when queue is full/empty
- FIFO ordering
- Fixed capacity (set at creation)
- Supports fairness policy

**When to Use:**
- Producer-consumer patterns
- Thread pools with bounded task queues
- Rate limiting
- When you need thread-safe queue with blocking

**Example Scenario:**
```java
// Producer-Consumer pattern
ArrayBlockingQueue<Task> taskQueue = new ArrayBlockingQueue<>(100);

// Producer thread
void producer() throws InterruptedException {
    while (true) {
        Task task = createTask();
        taskQueue.put(task); // Blocks if queue is full
    }
}

// Consumer thread
void consumer() throws InterruptedException {
    while (true) {
        Task task = taskQueue.take(); // Blocks if queue is empty
        processTask(task);
    }
}

// Rate limiting
ArrayBlockingQueue<Request> rateLimiter = new ArrayBlockingQueue<>(10);
if (rateLimiter.offer(request)) {
    // Process request
} else {
    // Reject - rate limit exceeded
}
```

**Use Cases:**
- Producer-consumer patterns
- Thread pool task queues
- Rate limiting systems
- Message buffering
- Request throttling
- Work distribution systems

---

## Quick Reference Table

| Collection | Ordering | Nulls | Thread-Safe | Time Complexity (avg) | Best Use Case |
|------------|----------|-------|-------------|----------------------|---------------|
| **HashMap** | No | Yes (1 key) | No | O(1) | General-purpose key-value storage |
| **LinkedHashMap** | Insertion | Yes (1 key) | No | O(1) | Ordered key-value storage, LRU cache |
| **TreeMap** | Sorted | Values only | No | O(log n) | Sorted maps, range queries |
| **Hashtable** | No | No | Yes | O(1) | Legacy code only |
| **ConcurrentHashMap** | No | No | Yes | O(1) | Multi-threaded key-value storage |
| **HashSet** | No | Yes (1) | No | O(1) | Unique elements, fast lookup |
| **LinkedHashSet** | Insertion | Yes (1) | No | O(1) | Unique ordered elements |
| **TreeSet** | Sorted | No | No | O(log n) | Sorted unique elements |
| **ArrayList** | Index | Yes | No | O(1) get, O(n) add/remove middle | Random access, iteration |
| **LinkedList** | Index | Yes | No | O(n) get, O(1) add/remove ends | Frequent insertion/deletion |
| **Vector** | Index | Yes | Yes | O(1) get, O(n) add/remove middle | Legacy code only |
| **PriorityQueue** | Priority | No | No | O(log n) add/poll | Priority-based processing |
| **ArrayDeque** | Index | No | No | O(1) add/remove ends | Stack, queue, deque operations |
| **ArrayBlockingQueue** | FIFO | No | Yes | O(1) add/poll | Producer-consumer, thread pools |

---

## Decision Tree

### Choosing a Map:
```
Need key-value storage?
├─ Single-threaded?
│  ├─ Need ordering?
│  │  ├─ Insertion order? → LinkedHashMap
│  │  └─ Sorted order? → TreeMap
│  └─ No ordering needed? → HashMap
└─ Multi-threaded?
   └─ → ConcurrentHashMap
```

### Choosing a Set:
```
Need unique elements?
├─ Need ordering?
│  ├─ Insertion order? → LinkedHashSet
│  └─ Sorted order? → TreeSet
└─ No ordering needed? → HashSet
```

### Choosing a List:
```
Need indexed collection?
├─ Frequent random access? → ArrayList
├─ Frequent insertion/deletion at ends? → LinkedList
└─ Thread-safe needed? → Collections.synchronizedList(new ArrayList<>())
```

### Choosing a Queue:
```
Need queue operations?
├─ Priority-based? → PriorityQueue
├─ Thread-safe blocking? → ArrayBlockingQueue
└─ General-purpose stack/queue/deque? → ArrayDeque
```

---

## Performance Comparison Summary

### When Speed Matters Most:
- **Fastest lookup**: HashMap, HashSet
- **Fastest iteration**: ArrayList
- **Fastest insertion at ends**: ArrayDeque, LinkedList
- **Fastest sorted access**: TreeMap, TreeSet (O(log n) is best for sorted)

### When Memory Matters:
- **Most memory-efficient**: ArrayList, HashMap, HashSet
- **Most memory-intensive**: LinkedList, LinkedHashMap, LinkedHashSet

### When Thread-Safety Matters:
- **Best concurrent performance**: ConcurrentHashMap
- **Blocking queue**: ArrayBlockingQueue
- **Simple thread-safety**: Collections.synchronized*

---

## Common Mistakes to Avoid

1. **Using Vector or Hashtable in new code** - Use ArrayList/HashMap with synchronization if needed
2. **Using LinkedList for random access** - Use ArrayList instead
3. **Not pre-sizing ArrayList when size is known** - Causes unnecessary resizing
4. **Using HashMap in multi-threaded code** - Use ConcurrentHashMap
5. **Trying to sort HashSet** - Use TreeSet or sort after converting to List
6. **Using == instead of equals() for custom objects** - Override hashCode() and equals()
7. **Modifying collections during iteration** - Use iterator.remove() or ConcurrentHashMap
8. **Using null with TreeMap/TreeSet/PriorityQueue** - These don't support null

---

## Best Practices

1. **Default choices**: HashMap, HashSet, ArrayList for most scenarios
2. **Pre-size collections** when you know approximate size
3. **Choose based on operations**: reads vs writes, random access vs sequential
4. **Use interfaces** in declarations: `List<String> list = new ArrayList<>()`
5. **Consider immutability**: Use `List.of()`, `Set.of()`, `Map.of()` for immutable collections
6. **Thread-safety**: Only synchronize when necessary (performance cost)
7. **Override hashCode() and equals()** for custom objects used as keys/elements
8. **Use appropriate initial capacity** to minimize resizing overhead

---

## Conclusion

Choosing the right collection is crucial for application performance and maintainability. Use this guide as a reference, but always profile your specific use case to make the best decision. When in doubt, start with the default choices (HashMap, HashSet, ArrayList, ArrayDeque) and optimize only if performance testing indicates a need.
