package DesignPattern.Composite;

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
