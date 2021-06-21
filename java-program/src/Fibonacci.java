
import java.util.Scanner;

public class Fibonacci
{
	public static void main(String[] args)
	{
		int i, n, t1 = 0, t2 = 1, nextTerm;
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the number of terms: ");
		n = sc.nextInt();

		System.out.println("Fibonacci Series: ");

		for (i = 2; i <= n; ++i)
		{
			System.out.println(t1);
			nextTerm = t1 + t2;
			t1 = t2;
			t2 = nextTerm;
		}
	}

}
