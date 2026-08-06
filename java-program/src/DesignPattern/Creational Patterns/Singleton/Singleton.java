package DesignPattern.Singleton;

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
