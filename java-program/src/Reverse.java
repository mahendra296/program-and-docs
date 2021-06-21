
import java.util.Scanner;

public class Reverse
{
	public static void main(String args[])
	{
		int n, rem = 0, reverse = 0;
		String reverse1 = "";

		System.out.println("Enter an integer to reverse");
		Scanner in = new Scanner(System.in);
		n = in.nextInt();

		while (n != 0)
		{
			// rem = n%10;
			//  reverse1 = reverse1 + rem;
			//   n = n/10;
			reverse = reverse * 10;
			reverse = reverse + n % 10;
			n = n / 10;
		}

		System.out.println("Reverse of the number is " + reverse);
		// System.out.println("Reverse of the number is " + reverse1);
	}
}
