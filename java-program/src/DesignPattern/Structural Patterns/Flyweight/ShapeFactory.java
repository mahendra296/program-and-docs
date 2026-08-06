package DesignPattern.Flyweight;

import java.util.HashMap;
import java.util.Map;

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
