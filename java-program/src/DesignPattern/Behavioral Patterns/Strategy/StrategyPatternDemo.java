package DesignPattern.Strategy;

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
