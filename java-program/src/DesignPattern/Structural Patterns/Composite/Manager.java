package DesignPattern.Composite;

import java.util.ArrayList;
import java.util.List;

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
