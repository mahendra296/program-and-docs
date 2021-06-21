public class TryCatchFinally
{
    public static void main(String args[]) {
        int i = getIntvalue();
        System.out.println("Value of i : " + i);
    }

    private static int getIntvalue() {
        try {
            int num1 = 10;
            int num2 = 0;
            // This will throw java.lang.ArithmeticException: / by zero
            int result = num1 / num2;
            return 1;
        } catch (java.lang.Exception e) {
            System.out.println("Exception..........");
            return 2;
        } finally {
            System.out.println("Finally-----------");
            return 3;
        }
    }
}