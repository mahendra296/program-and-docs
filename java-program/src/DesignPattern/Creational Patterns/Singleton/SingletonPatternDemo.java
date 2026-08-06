package DesignPattern.Singleton;

public class SingletonPatternDemo {
    public static void main(String[] args) {
        Singleton s1 = Singleton.getInstance();
        Singleton s2 = Singleton.getInstance();
        System.out.println("Singleton same instance? " + (s1 == s2));

        EagerSingleton e1 = EagerSingleton.getInstance();
        EagerSingleton e2 = EagerSingleton.getInstance();
        System.out.println("EagerSingleton same instance? " + (e1 == e2));

        BillPughSingleton b1 = BillPughSingleton.getInstance();
        BillPughSingleton b2 = BillPughSingleton.getInstance();
        System.out.println("BillPughSingleton same instance? " + (b1 == b2));
    }
}
