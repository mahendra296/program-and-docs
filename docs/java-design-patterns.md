# Java Design Patterns - Complete Guide

A comprehensive guide to all major design patterns in Java with practical examples.

## Table of Contents
1. [Creational Patterns](#creational-patterns)
2. [Structural Patterns](#structural-patterns)
3. [Behavioral Patterns](#behavioral-patterns)

---

## Creational Patterns

Creational patterns focus on object creation mechanisms, providing flexibility in what gets created, who creates it, how it gets created, and when.

### 1. Singleton Pattern

Ensures a class has only one instance and provides a global point of access to it.

```java
public class Singleton {
    private static Singleton instance;
    
    private Singleton() {
        // Private constructor prevents instantiation
    }
    
    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}

// Thread-safe eager initialization
public class EagerSingleton {
    private static final EagerSingleton instance = new EagerSingleton();
    
    private EagerSingleton() {}
    
    public static EagerSingleton getInstance() {
        return instance;
    }
}

// Bill Pugh Singleton (recommended)
public class BillPughSingleton {
    private BillPughSingleton() {}
    
    private static class SingletonHelper {
        private static final BillPughSingleton INSTANCE = new BillPughSingleton();
    }
    
    public static BillPughSingleton getInstance() {
        return SingletonHelper.INSTANCE;
    }
}
```

### 2. Factory Pattern

Creates objects without specifying the exact class to create.

```java
// Product interface
interface Vehicle {
    void drive();
}

// Concrete products
class Car implements Vehicle {
    @Override
    public void drive() {
        System.out.println("Driving a car");
    }
}

class Bike implements Vehicle {
    @Override
    public void drive() {
        System.out.println("Riding a bike");
    }
}

class Truck implements Vehicle {
    @Override
    public void drive() {
        System.out.println("Driving a truck");
    }
}

// Factory class
class VehicleFactory {
    public Vehicle getVehicle(String vehicleType) {
        if (vehicleType == null) {
            return null;
        }
        switch (vehicleType.toLowerCase()) {
            case "car":
                return new Car();
            case "bike":
                return new Bike();
            case "truck":
                return new Truck();
            default:
                return null;
        }
    }
}

// Usage
public class FactoryPatternDemo {
    public static void main(String[] args) {
        VehicleFactory factory = new VehicleFactory();
        
        Vehicle car = factory.getVehicle("car");
        car.drive();
        
        Vehicle bike = factory.getVehicle("bike");
        bike.drive();
    }
}
```

### 3. Abstract Factory Pattern

Provides an interface for creating families of related objects without specifying their concrete classes.

```java
// Abstract products
interface Button {
    void paint();
}

interface Checkbox {
    void paint();
}

// Concrete products for Windows
class WindowsButton implements Button {
    @Override
    public void paint() {
        System.out.println("Rendering Windows button");
    }
}

class WindowsCheckbox implements Checkbox {
    @Override
    public void paint() {
        System.out.println("Rendering Windows checkbox");
    }
}

// Concrete products for Mac
class MacButton implements Button {
    @Override
    public void paint() {
        System.out.println("Rendering Mac button");
    }
}

class MacCheckbox implements Checkbox {
    @Override
    public void paint() {
        System.out.println("Rendering Mac checkbox");
    }
}

// Abstract factory
interface GUIFactory {
    Button createButton();
    Checkbox createCheckbox();
}

// Concrete factories
class WindowsFactory implements GUIFactory {
    @Override
    public Button createButton() {
        return new WindowsButton();
    }
    
    @Override
    public Checkbox createCheckbox() {
        return new WindowsCheckbox();
    }
}

class MacFactory implements GUIFactory {
    @Override
    public Button createButton() {
        return new MacButton();
    }
    
    @Override
    public Checkbox createCheckbox() {
        return new MacCheckbox();
    }
}

// Usage
public class AbstractFactoryDemo {
    private Button button;
    private Checkbox checkbox;
    
    public AbstractFactoryDemo(GUIFactory factory) {
        button = factory.createButton();
        checkbox = factory.createCheckbox();
    }
    
    public void paint() {
        button.paint();
        checkbox.paint();
    }
    
    public static void main(String[] args) {
        GUIFactory factory = new WindowsFactory();
        AbstractFactoryDemo app = new AbstractFactoryDemo(factory);
        app.paint();
    }
}
```

### 4. Builder Pattern

Constructs complex objects step by step, allowing different representations using the same construction process.

```java
// Product
class Computer {
    private String CPU;
    private String RAM;
    private String storage;
    private String GPU;
    private boolean isWiFiEnabled;
    private boolean isBluetoothEnabled;
    
    private Computer(ComputerBuilder builder) {
        this.CPU = builder.CPU;
        this.RAM = builder.RAM;
        this.storage = builder.storage;
        this.GPU = builder.GPU;
        this.isWiFiEnabled = builder.isWiFiEnabled;
        this.isBluetoothEnabled = builder.isBluetoothEnabled;
    }
    
    @Override
    public String toString() {
        return "Computer [CPU=" + CPU + ", RAM=" + RAM + ", Storage=" + storage + 
               ", GPU=" + GPU + ", WiFi=" + isWiFiEnabled + ", Bluetooth=" + isBluetoothEnabled + "]";
    }
    
    // Builder class
    public static class ComputerBuilder {
        private String CPU;
        private String RAM;
        private String storage;
        private String GPU;
        private boolean isWiFiEnabled;
        private boolean isBluetoothEnabled;
        
        public ComputerBuilder(String CPU, String RAM) {
            this.CPU = CPU;
            this.RAM = RAM;
        }
        
        public ComputerBuilder setStorage(String storage) {
            this.storage = storage;
            return this;
        }
        
        public ComputerBuilder setGPU(String GPU) {
            this.GPU = GPU;
            return this;
        }
        
        public ComputerBuilder setWiFiEnabled(boolean isWiFiEnabled) {
            this.isWiFiEnabled = isWiFiEnabled;
            return this;
        }
        
        public ComputerBuilder setBluetoothEnabled(boolean isBluetoothEnabled) {
            this.isBluetoothEnabled = isBluetoothEnabled;
            return this;
        }
        
        public Computer build() {
            return new Computer(this);
        }
    }
}

// Usage
public class BuilderPatternDemo {
    public static void main(String[] args) {
        Computer computer = new Computer.ComputerBuilder("Intel i7", "16GB")
                .setStorage("512GB SSD")
                .setGPU("NVIDIA RTX 3060")
                .setWiFiEnabled(true)
                .setBluetoothEnabled(true)
                .build();
        
        System.out.println(computer);
    }
}
```

### 5. Prototype Pattern

Creates new objects by copying existing objects (prototypes).

```java
// Prototype interface
abstract class Shape implements Cloneable {
    private String id;
    protected String type;
    
    public String getType() {
        return type;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public abstract void draw();
    
    @Override
    public Object clone() {
        Object clone = null;
        try {
            clone = super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }
}

// Concrete prototypes
class Rectangle extends Shape {
    public Rectangle() {
        type = "Rectangle";
    }
    
    @Override
    public void draw() {
        System.out.println("Drawing a Rectangle");
    }
}

class Circle extends Shape {
    public Circle() {
        type = "Circle";
    }
    
    @Override
    public void draw() {
        System.out.println("Drawing a Circle");
    }
}

class Triangle extends Shape {
    public Triangle() {
        type = "Triangle";
    }
    
    @Override
    public void draw() {
        System.out.println("Drawing a Triangle");
    }
}

// Prototype registry
class ShapeCache {
    private static Map<String, Shape> shapeMap = new HashMap<>();
    
    public static Shape getShape(String shapeId) {
        Shape cachedShape = shapeMap.get(shapeId);
        return (Shape) cachedShape.clone();
    }
    
    public static void loadCache() {
        Circle circle = new Circle();
        circle.setId("1");
        shapeMap.put(circle.getId(), circle);
        
        Rectangle rectangle = new Rectangle();
        rectangle.setId("2");
        shapeMap.put(rectangle.getId(), rectangle);
        
        Triangle triangle = new Triangle();
        triangle.setId("3");
        shapeMap.put(triangle.getId(), triangle);
    }
}

// Usage
public class PrototypePatternDemo {
    public static void main(String[] args) {
        ShapeCache.loadCache();
        
        Shape clonedShape1 = ShapeCache.getShape("1");
        System.out.println("Shape: " + clonedShape1.getType());
        
        Shape clonedShape2 = ShapeCache.getShape("2");
        System.out.println("Shape: " + clonedShape2.getType());
    }
}
```

---

## Structural Patterns

Structural patterns deal with object composition, creating relationships between objects to form larger structures.

### 6. Adapter Pattern

Allows incompatible interfaces to work together by wrapping an object with an adapter.

```java
// Target interface
interface MediaPlayer {
    void play(String audioType, String fileName);
}

// Adaptee interface
interface AdvancedMediaPlayer {
    void playVlc(String fileName);
    void playMp4(String fileName);
}

// Concrete adaptee classes
class VlcPlayer implements AdvancedMediaPlayer {
    @Override
    public void playVlc(String fileName) {
        System.out.println("Playing vlc file: " + fileName);
    }
    
    @Override
    public void playMp4(String fileName) {
        // Do nothing
    }
}

class Mp4Player implements AdvancedMediaPlayer {
    @Override
    public void playVlc(String fileName) {
        // Do nothing
    }
    
    @Override
    public void playMp4(String fileName) {
        System.out.println("Playing mp4 file: " + fileName);
    }
}

// Adapter class
class MediaAdapter implements MediaPlayer {
    AdvancedMediaPlayer advancedMusicPlayer;
    
    public MediaAdapter(String audioType) {
        if (audioType.equalsIgnoreCase("vlc")) {
            advancedMusicPlayer = new VlcPlayer();
        } else if (audioType.equalsIgnoreCase("mp4")) {
            advancedMusicPlayer = new Mp4Player();
        }
    }
    
    @Override
    public void play(String audioType, String fileName) {
        if (audioType.equalsIgnoreCase("vlc")) {
            advancedMusicPlayer.playVlc(fileName);
        } else if (audioType.equalsIgnoreCase("mp4")) {
            advancedMusicPlayer.playMp4(fileName);
        }
    }
}

// Client class
class AudioPlayer implements MediaPlayer {
    MediaAdapter mediaAdapter;
    
    @Override
    public void play(String audioType, String fileName) {
        if (audioType.equalsIgnoreCase("mp3")) {
            System.out.println("Playing mp3 file: " + fileName);
        } else if (audioType.equalsIgnoreCase("vlc") || audioType.equalsIgnoreCase("mp4")) {
            mediaAdapter = new MediaAdapter(audioType);
            mediaAdapter.play(audioType, fileName);
        } else {
            System.out.println("Invalid media type: " + audioType);
        }
    }
}

// Usage
public class AdapterPatternDemo {
    public static void main(String[] args) {
        AudioPlayer audioPlayer = new AudioPlayer();
        
        audioPlayer.play("mp3", "song.mp3");
        audioPlayer.play("mp4", "video.mp4");
        audioPlayer.play("vlc", "movie.vlc");
        audioPlayer.play("avi", "file.avi");
    }
}
```

### 7. Bridge Pattern

Separates abstraction from implementation so both can vary independently.

```java
// Implementor interface
interface Device {
    boolean isEnabled();
    void enable();
    void disable();
    int getVolume();
    void setVolume(int percent);
    int getChannel();
    void setChannel(int channel);
}

// Concrete implementors
class TV implements Device {
    private boolean on = false;
    private int volume = 30;
    private int channel = 1;
    
    @Override
    public boolean isEnabled() {
        return on;
    }
    
    @Override
    public void enable() {
        on = true;
    }
    
    @Override
    public void disable() {
        on = false;
    }
    
    @Override
    public int getVolume() {
        return volume;
    }
    
    @Override
    public void setVolume(int volume) {
        this.volume = Math.max(0, Math.min(volume, 100));
    }
    
    @Override
    public int getChannel() {
        return channel;
    }
    
    @Override
    public void setChannel(int channel) {
        this.channel = channel;
    }
}

class Radio implements Device {
    private boolean on = false;
    private int volume = 20;
    private int channel = 1;
    
    @Override
    public boolean isEnabled() {
        return on;
    }
    
    @Override
    public void enable() {
        on = true;
    }
    
    @Override
    public void disable() {
        on = false;
    }
    
    @Override
    public int getVolume() {
        return volume;
    }
    
    @Override
    public void setVolume(int volume) {
        this.volume = Math.max(0, Math.min(volume, 100));
    }
    
    @Override
    public int getChannel() {
        return channel;
    }
    
    @Override
    public void setChannel(int channel) {
        this.channel = channel;
    }
}

// Abstraction
class RemoteControl {
    protected Device device;
    
    public RemoteControl(Device device) {
        this.device = device;
    }
    
    public void togglePower() {
        if (device.isEnabled()) {
            device.disable();
        } else {
            device.enable();
        }
    }
    
    public void volumeDown() {
        device.setVolume(device.getVolume() - 10);
    }
    
    public void volumeUp() {
        device.setVolume(device.getVolume() + 10);
    }
    
    public void channelDown() {
        device.setChannel(device.getChannel() - 1);
    }
    
    public void channelUp() {
        device.setChannel(device.getChannel() + 1);
    }
}

// Refined abstraction
class AdvancedRemoteControl extends RemoteControl {
    public AdvancedRemoteControl(Device device) {
        super(device);
    }
    
    public void mute() {
        device.setVolume(0);
    }
}

// Usage
public class BridgePatternDemo {
    public static void main(String[] args) {
        Device tv = new TV();
        RemoteControl remote = new RemoteControl(tv);
        remote.togglePower();
        remote.volumeUp();
        
        Device radio = new Radio();
        AdvancedRemoteControl advancedRemote = new AdvancedRemoteControl(radio);
        advancedRemote.togglePower();
        advancedRemote.mute();
    }
}
```

### 8. Composite Pattern

Composes objects into tree structures to represent part-whole hierarchies.

```java
import java.util.ArrayList;
import java.util.List;

// Component interface
interface Employee {
    void showEmployeeDetails();
}

// Leaf class
class Developer implements Employee {
    private String name;
    private String position;
    
    public Developer(String name, String position) {
        this.name = name;
        this.position = position;
    }
    
    @Override
    public void showEmployeeDetails() {
        System.out.println(name + " - " + position);
    }
}

// Composite class
class Manager implements Employee {
    private String name;
    private String position;
    private List<Employee> subordinates = new ArrayList<>();
    
    public Manager(String name, String position) {
        this.name = name;
        this.position = position;
    }
    
    public void addEmployee(Employee emp) {
        subordinates.add(emp);
    }
    
    public void removeEmployee(Employee emp) {
        subordinates.remove(emp);
    }
    
    @Override
    public void showEmployeeDetails() {
        System.out.println(name + " - " + position);
        for (Employee emp : subordinates) {
            emp.showEmployeeDetails();
        }
    }
}

// Usage
public class CompositePatternDemo {
    public static void main(String[] args) {
        Developer dev1 = new Developer("John", "Senior Developer");
        Developer dev2 = new Developer("Jane", "Junior Developer");
        
        Manager manager = new Manager("Alice", "Engineering Manager");
        manager.addEmployee(dev1);
        manager.addEmployee(dev2);
        
        Developer dev3 = new Developer("Bob", "Lead Developer");
        Manager cto = new Manager("Charlie", "CTO");
        cto.addEmployee(dev3);
        cto.addEmployee(manager);
        
        cto.showEmployeeDetails();
    }
}
```

### 9. Decorator Pattern

Attaches additional responsibilities to objects dynamically without modifying their structure.

```java
// Component interface
interface Coffee {
    String getDescription();
    double getCost();
}

// Concrete component
class SimpleCoffee implements Coffee {
    @Override
    public String getDescription() {
        return "Simple Coffee";
    }
    
    @Override
    public double getCost() {
        return 2.0;
    }
}

// Decorator abstract class
abstract class CoffeeDecorator implements Coffee {
    protected Coffee decoratedCoffee;
    
    public CoffeeDecorator(Coffee coffee) {
        this.decoratedCoffee = coffee;
    }
    
    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription();
    }
    
    @Override
    public double getCost() {
        return decoratedCoffee.getCost();
    }
}

// Concrete decorators
class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription() + ", Milk";
    }
    
    @Override
    public double getCost() {
        return decoratedCoffee.getCost() + 0.5;
    }
}

class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription() + ", Sugar";
    }
    
    @Override
    public double getCost() {
        return decoratedCoffee.getCost() + 0.2;
    }
}

class WhipDecorator extends CoffeeDecorator {
    public WhipDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public String getDescription() {
        return decoratedCoffee.getDescription() + ", Whip";
    }
    
    @Override
    public double getCost() {
        return decoratedCoffee.getCost() + 0.7;
    }
}

// Usage
public class DecoratorPatternDemo {
    public static void main(String[] args) {
        Coffee coffee = new SimpleCoffee();
        System.out.println(coffee.getDescription() + " $" + coffee.getCost());
        
        coffee = new MilkDecorator(coffee);
        System.out.println(coffee.getDescription() + " $" + coffee.getCost());
        
        coffee = new SugarDecorator(coffee);
        System.out.println(coffee.getDescription() + " $" + coffee.getCost());
        
        coffee = new WhipDecorator(coffee);
        System.out.println(coffee.getDescription() + " $" + coffee.getCost());
    }
}
```

### 10. Facade Pattern

Provides a simplified interface to a complex subsystem.

```java
// Complex subsystem classes
class DVDPlayer {
    public void on() {
        System.out.println("DVD Player is on");
    }
    
    public void play(String movie) {
        System.out.println("Playing movie: " + movie);
    }
    
    public void off() {
        System.out.println("DVD Player is off");
    }
}

class Projector {
    public void on() {
        System.out.println("Projector is on");
    }
    
    public void wideScreenMode() {
        System.out.println("Projector in widescreen mode");
    }
    
    public void off() {
        System.out.println("Projector is off");
    }
}

class SoundSystem {
    public void on() {
        System.out.println("Sound system is on");
    }
    
    public void setVolume(int level) {
        System.out.println("Sound system volume set to " + level);
    }
    
    public void off() {
        System.out.println("Sound system is off");
    }
}

class Lights {
    public void dim(int level) {
        System.out.println("Lights dimmed to " + level + "%");
    }
    
    public void on() {
        System.out.println("Lights are on");
    }
}

// Facade class
class HomeTheaterFacade {
    private DVDPlayer dvd;
    private Projector projector;
    private SoundSystem soundSystem;
    private Lights lights;
    
    public HomeTheaterFacade(DVDPlayer dvd, Projector projector, 
                             SoundSystem soundSystem, Lights lights) {
        this.dvd = dvd;
        this.projector = projector;
        this.soundSystem = soundSystem;
        this.lights = lights;
    }
    
    public void watchMovie(String movie) {
        System.out.println("Get ready to watch a movie...");
        lights.dim(10);
        projector.on();
        projector.wideScreenMode();
        soundSystem.on();
        soundSystem.setVolume(5);
        dvd.on();
        dvd.play(movie);
    }
    
    public void endMovie() {
        System.out.println("Shutting down home theater...");
        dvd.off();
        soundSystem.off();
        projector.off();
        lights.on();
    }
}

// Usage
public class FacadePatternDemo {
    public static void main(String[] args) {
        DVDPlayer dvd = new DVDPlayer();
        Projector projector = new Projector();
        SoundSystem soundSystem = new SoundSystem();
        Lights lights = new Lights();
        
        HomeTheaterFacade homeTheater = new HomeTheaterFacade(dvd, projector, soundSystem, lights);
        
        homeTheater.watchMovie("Inception");
        System.out.println();
        homeTheater.endMovie();
    }
}
```

### 11. Flyweight Pattern

Minimizes memory usage by sharing data among similar objects.

```java
import java.util.HashMap;
import java.util.Map;

// Flyweight interface
interface Shape {
    void draw(int x, int y, int radius, String color);
}

// Concrete flyweight
class Circle implements Shape {
    private String type;
    
    public Circle(String type) {
        this.type = type;
    }
    
    @Override
    public void draw(int x, int y, int radius, String color) {
        System.out.println("Drawing " + type + " circle at (" + x + "," + y + 
                         ") with radius " + radius + " and color " + color);
    }
}

// Flyweight factory
class ShapeFactory {
    private static final Map<String, Shape> circleMap = new HashMap<>();
    
    public static Shape getCircle(String type) {
        Circle circle = (Circle) circleMap.get(type);
        
        if (circle == null) {
            circle = new Circle(type);
            circleMap.put(type, circle);
            System.out.println("Creating circle of type: " + type);
        }
        
        return circle;
    }
    
    public static int getCircleCount() {
        return circleMap.size();
    }
}

// Usage
public class FlyweightPatternDemo {
    private static final String[] types = {"Solid", "Dotted", "Dashed"};
    private static final String[] colors = {"Red", "Green", "Blue", "Yellow"};
    
    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) {
            String type = types[(int) (Math.random() * types.length)];
            Shape circle = ShapeFactory.getCircle(type);
            
            int x = (int) (Math.random() * 100);
            int y = (int) (Math.random() * 100);
            int radius = (int) (Math.random() * 50);
            String color = colors[(int) (Math.random() * colors.length)];
            
            circle.draw(x, y, radius, color);
        }
        
        System.out.println("\nTotal Circle objects created: " + ShapeFactory.getCircleCount());
    }
}
```

### 12. Proxy Pattern

Provides a placeholder or surrogate for another object to control access to it.

```java
// Subject interface
interface Image {
    void display();
}

// Real subject
class RealImage implements Image {
    private String filename;
    
    public RealImage(String filename) {
        this.filename = filename;
        loadFromDisk();
    }
    
    private void loadFromDisk() {
        System.out.println("Loading image: " + filename);
    }
    
    @Override
    public void display() {
        System.out.println("Displaying image: " + filename);
    }
}

// Proxy
class ProxyImage implements Image {
    private RealImage realImage;
    private String filename;
    
    public ProxyImage(String filename) {
        this.filename = filename;
    }
    
    @Override
    public void display() {
        if (realImage == null) {
            realImage = new RealImage(filename);
        }
        realImage.display();
    }
}

// Usage
public class ProxyPatternDemo {
    public static void main(String[] args) {
        Image image1 = new ProxyImage("photo1.jpg");
        Image image2 = new ProxyImage("photo2.jpg");
        
        // Image is loaded from disk
        image1.display();
        System.out.println();
        
        // Image is not loaded from disk (cached)
        image1.display();
        System.out.println();
        
        // Image is loaded from disk
        image2.display();
    }
}
```

---

## Behavioral Patterns

Behavioral patterns focus on communication between objects, defining how they interact and distribute responsibility.

### 13. Chain of Responsibility Pattern

Passes requests along a chain of handlers, where each handler decides to process or pass the request.

```java
// Handler abstract class
abstract class Logger {
    public static int INFO = 1;
    public static int DEBUG = 2;
    public static int ERROR = 3;
    
    protected int level;
    protected Logger nextLogger;
    
    public void setNextLogger(Logger nextLogger) {
        this.nextLogger = nextLogger;
    }
    
    public void logMessage(int level, String message) {
        if (this.level <= level) {
            write(message);
        }
        if (nextLogger != null) {
            nextLogger.logMessage(level, message);
        }
    }
    
    abstract protected void write(String message);
}

// Concrete handlers
class ConsoleLogger extends Logger {
    public ConsoleLogger(int level) {
        this.level = level;
    }
    
    @Override
    protected void write(String message) {
        System.out.println("Console Logger: " + message);
    }
}

class FileLogger extends Logger {
    public FileLogger(int level) {
        this.level = level;
    }
    
    @Override
    protected void write(String message) {
        System.out.println("File Logger: " + message);
    }
}

class ErrorLogger extends Logger {
    public ErrorLogger(int level) {
        this.level = level;
    }
    
    @Override
    protected void write(String message) {
        System.out.println("Error Logger: " + message);
    }
}

// Usage
public class ChainOfResponsibilityDemo {
    private static Logger getChainOfLoggers() {
        Logger errorLogger = new ErrorLogger(Logger.ERROR);
        Logger fileLogger = new FileLogger(Logger.DEBUG);
        Logger consoleLogger = new ConsoleLogger(Logger.INFO);
        
        errorLogger.setNextLogger(fileLogger);
        fileLogger.setNextLogger(consoleLogger);
        
        return errorLogger;
    }
    
    public static void main(String[] args) {
        Logger loggerChain = getChainOfLoggers();
        
        loggerChain.logMessage(Logger.INFO, "This is an information message");
        System.out.println();
        loggerChain.logMessage(Logger.DEBUG, "This is a debug message");
        System.out.println();
        loggerChain.logMessage(Logger.ERROR, "This is an error message");
    }
}
```

### 14. Command Pattern

Encapsulates a request as an object, allowing parameterization and queuing of requests.

```java
// Command interface
interface Command {
    void execute();
    void undo();
}

// Receiver class
class Light {
    private boolean isOn = false;
    
    public void turnOn() {
        isOn = true;
        System.out.println("Light is ON");
    }
    
    public void turnOff() {
        isOn = false;
        System.out.println("Light is OFF");
    }
}

// Concrete commands
class LightOnCommand implements Command {
    private Light light;
    
    public LightOnCommand(Light light) {
        this.light = light;
    }
    
    @Override
    public void execute() {
        light.turnOn();
    }
    
    @Override
    public void undo() {
        light.turnOff();
    }
}

class LightOffCommand implements Command {
    private Light light;
    
    public LightOffCommand(Light light) {
        this.light = light;
    }
    
    @Override
    public void execute() {
        light.turnOff();
    }
    
    @Override
    public void undo() {
        light.turnOn();
    }
}

// Invoker class
class RemoteControl {
    private Command command;
    
    public void setCommand(Command command) {
        this.command = command;
    }
    
    public void pressButton() {
        command.execute();
    }
    
    public void pressUndo() {
        command.undo();
    }
}

// Usage
public class CommandPatternDemo {
    public static void main(String[] args) {
        Light light = new Light();
        Command lightOn = new LightOnCommand(light);
        Command lightOff = new LightOffCommand(light);
        
        RemoteControl remote = new RemoteControl();
        
        remote.setCommand(lightOn);
        remote.pressButton();
        remote.pressUndo();
        
        remote.setCommand(lightOff);
        remote.pressButton();
        remote.pressUndo();
    }
}
```

### 15. Iterator Pattern

Provides a way to access elements of a collection sequentially without exposing its underlying representation.

```java
import java.util.ArrayList;
import java.util.List;

// Iterator interface
interface Iterator<T> {
    boolean hasNext();
    T next();
}

// Aggregate interface
interface Container<T> {
    Iterator<T> createIterator();
}

// Concrete iterator
class BookIterator implements Iterator<Book> {
    private List<Book> books;
    private int position = 0;
    
    public BookIterator(List<Book> books) {
        this.books = books;
    }
    
    @Override
    public boolean hasNext() {
        return position < books.size();
    }
    
    @Override
    public Book next() {
        return hasNext() ? books.get(position++) : null;
    }
}

// Model class
class Book {
    private String title;
    private String author;
    
    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    @Override
    public String toString() {
        return "\"" + title + "\" by " + author;
    }
}

// Concrete aggregate
class BookCollection implements Container<Book> {
    private List<Book> books = new ArrayList<>();
    
    public void addBook(Book book) {
        books.add(book);
    }
    
    public void removeBook(Book book) {
        books.remove(book);
    }
    
    @Override
    public Iterator<Book> createIterator() {
        return new BookIterator(books);
    }
}

// Usage
public class IteratorPatternDemo {
    public static void main(String[] args) {
        BookCollection collection = new BookCollection();
        collection.addBook(new Book("Design Patterns", "Gang of Four"));
        collection.addBook(new Book("Effective Java", "Joshua Bloch"));
        collection.addBook(new Book("Clean Code", "Robert Martin"));
        
        Iterator<Book> iterator = collection.createIterator();
        
        while (iterator.hasNext()) {
            Book book = iterator.next();
            System.out.println(book);
        }
    }
}
```

### 16. Mediator Pattern

Defines an object that encapsulates how a set of objects interact, promoting loose coupling.

```java
import java.util.ArrayList;
import java.util.List;

// Mediator interface
interface ChatMediator {
    void sendMessage(String message, User user);
    void addUser(User user);
}

// Concrete mediator
class ChatRoom implements ChatMediator {
    private List<User> users = new ArrayList<>();
    
    @Override
    public void addUser(User user) {
        users.add(user);
    }
    
    @Override
    public void sendMessage(String message, User sender) {
        for (User user : users) {
            if (user != sender) {
                user.receive(message);
            }
        }
    }
}

// Colleague abstract class
abstract class User {
    protected ChatMediator mediator;
    protected String name;
    
    public User(ChatMediator mediator, String name) {
        this.mediator = mediator;
        this.name = name;
    }
    
    public abstract void send(String message);
    public abstract void receive(String message);
}

// Concrete colleague
class ChatUser extends User {
    public ChatUser(ChatMediator mediator, String name) {
        super(mediator, name);
    }
    
    @Override
    public void send(String message) {
        System.out.println(name + " sends: " + message);
        mediator.sendMessage(message, this);
    }
    
    @Override
    public void receive(String message) {
        System.out.println(name + " receives: " + message);
    }
}

// Usage
public class MediatorPatternDemo {
    public static void main(String[] args) {
        ChatMediator chatRoom = new ChatRoom();
        
        User alice = new ChatUser(chatRoom, "Alice");
        User bob = new ChatUser(chatRoom, "Bob");
        User charlie = new ChatUser(chatRoom, "Charlie");
        
        chatRoom.addUser(alice);
        chatRoom.addUser(bob);
        chatRoom.addUser(charlie);
        
        alice.send("Hello everyone!");
        System.out.println();
        bob.send("Hi Alice!");
    }
}
```

### 17. Memento Pattern

Captures and restores an object's internal state without violating encapsulation.

```java
import java.util.ArrayList;
import java.util.List;

// Memento class
class EditorMemento {
    private final String content;
    
    public EditorMemento(String content) {
        this.content = content;
    }
    
    public String getContent() {
        return content;
    }
}

// Originator class
class Editor {
    private String content = "";
    
    public void write(String text) {
        content += text;
    }
    
    public String getContent() {
        return content;
    }
    
    public EditorMemento save() {
        return new EditorMemento(content);
    }
    
    public void restore(EditorMemento memento) {
        content = memento.getContent();
    }
}

// Caretaker class
class History {
    private List<EditorMemento> mementos = new ArrayList<>();
    
    public void save(EditorMemento memento) {
        mementos.add(memento);
    }
    
    public EditorMemento undo() {
        if (!mementos.isEmpty()) {
            int lastIndex = mementos.size() - 1;
            EditorMemento memento = mementos.get(lastIndex);
            mementos.remove(lastIndex);
            return memento;
        }
        return null;
    }
}

// Usage
public class MementoPatternDemo {
    public static void main(String[] args) {
        Editor editor = new Editor();
        History history = new History();
        
        editor.write("First sentence. ");
        history.save(editor.save());
        
        editor.write("Second sentence. ");
        history.save(editor.save());
        
        editor.write("Third sentence. ");
        System.out.println("Current content: " + editor.getContent());
        
        editor.restore(history.undo());
        System.out.println("After undo: " + editor.getContent());
        
        editor.restore(history.undo());
        System.out.println("After undo: " + editor.getContent());
    }
}
```

### 18. Observer Pattern

Defines a one-to-many dependency where when one object changes state, all dependents are notified.

```java
import java.util.ArrayList;
import java.util.List;

// Observer interface
interface Observer {
    void update(String message);
}

// Subject interface
interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers();
}

// Concrete subject
class NewsAgency implements Subject {
    private List<Observer> observers = new ArrayList<>();
    private String news;
    
    public void setNews(String news) {
        this.news = news;
        notifyObservers();
    }
    
    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }
    
    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }
    
    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(news);
        }
    }
}

// Concrete observers
class NewsChannel implements Observer {
    private String name;
    
    public NewsChannel(String name) {
        this.name = name;
    }
    
    @Override
    public void update(String news) {
        System.out.println(name + " received news: " + news);
    }
}

class MobileApp implements Observer {
    private String appName;
    
    public MobileApp(String appName) {
        this.appName = appName;
    }
    
    @Override
    public void update(String news) {
        System.out.println(appName + " notification: " + news);
    }
}

// Usage
public class ObserverPatternDemo {
    public static void main(String[] args) {
        NewsAgency agency = new NewsAgency();
        
        Observer channel1 = new NewsChannel("CNN");
        Observer channel2 = new NewsChannel("BBC");
        Observer app = new MobileApp("NewsApp");
        
        agency.attach(channel1);
        agency.attach(channel2);
        agency.attach(app);
        
        agency.setNews("Breaking: New design pattern discovered!");
        System.out.println();
        
        agency.detach(channel2);
        agency.setNews("Update: Pattern proves revolutionary!");
    }
}
```

### 19. State Pattern

Allows an object to alter its behavior when its internal state changes.

```java
// State interface
interface State {
    void insertCoin();
    void ejectCoin();
    void dispense();
}

// Concrete states
class NoCoinState implements State {
    private VendingMachine machine;
    
    public NoCoinState(VendingMachine machine) {
        this.machine = machine;
    }
    
    @Override
    public void insertCoin() {
        System.out.println("Coin inserted");
        machine.setState(machine.getHasCoinState());
    }
    
    @Override
    public void ejectCoin() {
        System.out.println("No coin to eject");
    }
    
    @Override
    public void dispense() {
        System.out.println("Insert coin first");
    }
}

class HasCoinState implements State {
    private VendingMachine machine;
    
    public HasCoinState(VendingMachine machine) {
        this.machine = machine;
    }
    
    @Override
    public void insertCoin() {
        System.out.println("Coin already inserted");
    }
    
    @Override
    public void ejectCoin() {
        System.out.println("Coin ejected");
        machine.setState(machine.getNoCoinState());
    }
    
    @Override
    public void dispense() {
        System.out.println("Dispensing item...");
        machine.setState(machine.getNoCoinState());
    }
}

// Context class
class VendingMachine {
    private State noCoinState;
    private State hasCoinState;
    private State currentState;
    
    public VendingMachine() {
        noCoinState = new NoCoinState(this);
        hasCoinState = new HasCoinState(this);
        currentState = noCoinState;
    }
    
    public void setState(State state) {
        currentState = state;
    }
    
    public State getNoCoinState() {
        return noCoinState;
    }
    
    public State getHasCoinState() {
        return hasCoinState;
    }
    
    public void insertCoin() {
        currentState.insertCoin();
    }
    
    public void ejectCoin() {
        currentState.ejectCoin();
    }
    
    public void dispense() {
        currentState.dispense();
    }
}

// Usage
public class StatePatternDemo {
    public static void main(String[] args) {
        VendingMachine machine = new VendingMachine();
        
        machine.dispense();
        System.out.println();
        
        machine.insertCoin();
        machine.dispense();
        System.out.println();
        
        machine.insertCoin();
        machine.ejectCoin();
        machine.dispense();
    }
}
```

### 20. Strategy Pattern

Defines a family of algorithms, encapsulates each one, and makes them interchangeable.

```java
// Strategy interface
interface PaymentStrategy {
    void pay(int amount);
}

// Concrete strategies
class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;
    private String name;
    
    public CreditCardPayment(String cardNumber, String name) {
        this.cardNumber = cardNumber;
        this.name = name;
    }
    
    @Override
    public void pay(int amount) {
        System.out.println("Paid $" + amount + " using Credit Card: " + cardNumber);
    }
}

class PayPalPayment implements PaymentStrategy {
    private String email;
    
    public PayPalPayment(String email) {
        this.email = email;
    }
    
    @Override
    public void pay(int amount) {
        System.out.println("Paid $" + amount + " using PayPal: " + email);
    }
}

class BitcoinPayment implements PaymentStrategy {
    private String walletAddress;
    
    public BitcoinPayment(String walletAddress) {
        this.walletAddress = walletAddress;
    }
    
    @Override
    public void pay(int amount) {
        System.out.println("Paid $" + amount + " using Bitcoin: " + walletAddress);
    }
}

// Context class
class ShoppingCart {
    private PaymentStrategy paymentStrategy;
    
    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }
    
    public void checkout(int amount) {
        paymentStrategy.pay(amount);
    }
}

// Usage
public class StrategyPatternDemo {
    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();
        
        cart.setPaymentStrategy(new CreditCardPayment("1234-5678-9012-3456", "John Doe"));
        cart.checkout(100);
        
        cart.setPaymentStrategy(new PayPalPayment("john@example.com"));
        cart.checkout(200);
        
        cart.setPaymentStrategy(new BitcoinPayment("1A2B3C4D5E6F"));
        cart.checkout(300);
    }
}
```

### 21. Template Method Pattern

Defines the skeleton of an algorithm in a method, deferring some steps to subclasses.

```java
// Abstract class with template method
abstract class DataProcessor {
    // Template method
    public final void process() {
        readData();
        processData();
        saveData();
    }
    
    abstract void readData();
    abstract void processData();
    
    // Common implementation
    void saveData() {
        System.out.println("Saving processed data to database");
    }
}

// Concrete classes
class CSVDataProcessor extends DataProcessor {
    @Override
    void readData() {
        System.out.println("Reading data from CSV file");
    }
    
    @Override
    void processData() {
        System.out.println("Processing CSV data");
    }
}

class XMLDataProcessor extends DataProcessor {
    @Override
    void readData() {
        System.out.println("Reading data from XML file");
    }
    
    @Override
    void processData() {
        System.out.println("Processing XML data");
    }
}

class JSONDataProcessor extends DataProcessor {
    @Override
    void readData() {
        System.out.println("Reading data from JSON file");
    }
    
    @Override
    void processData() {
        System.out.println("Processing JSON data");
    }
}

// Usage
public class TemplateMethodDemo {
    public static void main(String[] args) {
        DataProcessor csvProcessor = new CSVDataProcessor();
        csvProcessor.process();
        
        System.out.println();
        
        DataProcessor xmlProcessor = new XMLDataProcessor();
        xmlProcessor.process();
        
        System.out.println();
        
        DataProcessor jsonProcessor = new JSONDataProcessor();
        jsonProcessor.process();
    }
}
```

### 22. Visitor Pattern

Separates an algorithm from an object structure, allowing new operations without modifying the structures.

```java
// Element interface
interface ComputerPart {
    void accept(ComputerPartVisitor visitor);
}

// Concrete elements
class Keyboard implements ComputerPart {
    @Override
    public void accept(ComputerPartVisitor visitor) {
        visitor.visit(this);
    }
}

class Mouse implements ComputerPart {
    @Override
    public void accept(ComputerPartVisitor visitor) {
        visitor.visit(this);
    }
}

class Monitor implements ComputerPart {
    @Override
    public void accept(ComputerPartVisitor visitor) {
        visitor.visit(this);
    }
}

class Computer implements ComputerPart {
    private ComputerPart[] parts;
    
    public Computer() {
        parts = new ComputerPart[] {new Mouse(), new Keyboard(), new Monitor()};
    }
    
    @Override
    public void accept(ComputerPartVisitor visitor) {
        for (ComputerPart part : parts) {
            part.accept(visitor);
        }
        visitor.visit(this);
    }
}

// Visitor interface
interface ComputerPartVisitor {
    void visit(Keyboard keyboard);
    void visit(Mouse mouse);
    void visit(Monitor monitor);
    void visit(Computer computer);
}

// Concrete visitor
class ComputerPartDisplayVisitor implements ComputerPartVisitor {
    @Override
    public void visit(Keyboard keyboard) {
        System.out.println("Displaying Keyboard");
    }
    
    @Override
    public void visit(Mouse mouse) {
        System.out.println("Displaying Mouse");
    }
    
    @Override
    public void visit(Monitor monitor) {
        System.out.println("Displaying Monitor");
    }
    
    @Override
    public void visit(Computer computer) {
        System.out.println("Displaying Computer");
    }
}

// Usage
public class VisitorPatternDemo {
    public static void main(String[] args) {
        ComputerPart computer = new Computer();
        computer.accept(new ComputerPartDisplayVisitor());
    }
}
```

### 23. Interpreter Pattern

Defines a grammar for a language and an interpreter to interpret sentences in that language.

```java
// Abstract expression
interface Expression {
    boolean interpret(String context);
}

// Terminal expressions
class TerminalExpression implements Expression {
    private String data;
    
    public TerminalExpression(String data) {
        this.data = data;
    }
    
    @Override
    public boolean interpret(String context) {
        return context.contains(data);
    }
}

// Non-terminal expressions
class OrExpression implements Expression {
    private Expression expr1;
    private Expression expr2;
    
    public OrExpression(Expression expr1, Expression expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }
    
    @Override
    public boolean interpret(String context) {
        return expr1.interpret(context) || expr2.interpret(context);
    }
}

class AndExpression implements Expression {
    private Expression expr1;
    private Expression expr2;
    
    public AndExpression(Expression expr1, Expression expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }
    
    @Override
    public boolean interpret(String context) {
        return expr1.interpret(context) && expr2.interpret(context);
    }
}

// Usage
public class InterpreterPatternDemo {
    public static Expression getMaleExpression() {
        Expression robert = new TerminalExpression("Robert");
        Expression john = new TerminalExpression("John");
        return new OrExpression(robert, john);
    }
    
    public static Expression getMarriedWomanExpression() {
        Expression julie = new TerminalExpression("Julie");
        Expression married = new TerminalExpression("Married");
        return new AndExpression(julie, married);
    }
    
    public static void main(String[] args) {
        Expression isMale = getMaleExpression();
        Expression isMarriedWoman = getMarriedWomanExpression();
        
        System.out.println("John is male? " + isMale.interpret("John"));
        System.out.println("Julie is a married woman? " + 
                         isMarriedWoman.interpret("Married Julie"));
    }
}
```

---

## Summary

### When to Use Each Pattern

**Creational Patterns:**
- **Singleton**: Global access to a single instance (database connections, configuration)
- **Factory**: Create objects without specifying exact class (payment processors, notifications)
- **Abstract Factory**: Create families of related objects (UI components for different platforms)
- **Builder**: Construct complex objects step-by-step (building queries, configurations)
- **Prototype**: Clone existing objects (document templates, game objects)

**Structural Patterns:**
- **Adapter**: Make incompatible interfaces work together (legacy code integration)
- **Bridge**: Separate abstraction from implementation (cross-platform applications)
- **Composite**: Tree structures with uniform treatment (file systems, UI components)
- **Decorator**: Add responsibilities to objects dynamically (UI decorations, stream wrappers)
- **Facade**: Simplify complex subsystems (library APIs, third-party integrations)
- **Flyweight**: Share common state to support large numbers of objects (text editors, games)
- **Proxy**: Control access to objects (lazy loading, access control, logging)

**Behavioral Patterns:**
- **Chain of Responsibility**: Pass requests through a chain (logging, event handling)
- **Command**: Encapsulate requests as objects (undo/redo, transactions)
- **Iterator**: Access collection elements sequentially (custom collections)
- **Mediator**: Reduce coupling between objects (chat rooms, UI components)
- **Memento**: Save and restore object state (undo functionality, snapshots)
- **Observer**: Notify dependents of state changes (event systems, MVC)
- **State**: Change behavior based on state (workflow systems, state machines)
- **Strategy**: Select algorithm at runtime (sorting algorithms, payment methods)
- **Template Method**: Define algorithm skeleton (data processing pipelines)
- **Visitor**: Add operations to object structures (compiler AST, reporting)
- **Interpreter**: Define grammar and interpret sentences (expression evaluators, DSLs)

---

## Best Practices

1. **Don't overuse patterns** - Apply them when they solve a real problem
2. **Understand the problem first** - Choose patterns based on requirements
3. **Keep it simple** - Start simple and refactor to patterns when needed
4. **Consider maintainability** - Patterns should make code easier to maintain
5. **Learn from examples** - Study real-world implementations in frameworks
6. **Combine patterns** - Many real applications use multiple patterns together
7. **Follow SOLID principles** - Patterns work best with solid OOP fundamentals

---

This guide covers the 23 classic Gang of Four design patterns with practical Java examples. Each pattern solves specific design problems and provides proven solutions for creating flexible, maintainable, and reusable code.
