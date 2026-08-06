package DesignPattern.Flyweight;

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
