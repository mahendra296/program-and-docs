package DesignPattern.Flyweight;

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

        System.out.println("Total Circle objects created: " + ShapeFactory.getCircleCount());
    }
}
