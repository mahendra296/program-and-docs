package DesignPattern.Factory;

public class FactoryPatternDemo {
    public static void main(String[] args) {
        VehicleFactory factory = new VehicleFactory();

        Vehicle car = factory.getVehicle("car");
        car.drive();

        Vehicle bike = factory.getVehicle("bike");
        bike.drive();

        Vehicle truck = factory.getVehicle("truck");
        truck.drive();
    }
}
