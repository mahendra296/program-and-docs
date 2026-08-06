package DesignPattern.State;

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
