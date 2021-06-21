
import java.util.Scanner;

public class Prime100
{
	public static void main(String[] args)
	{
		Scanner sc = new Scanner(System.in);
		int n, prime = 1, count = 1, i;

		System.out.println("Enter Number =>");
		n = sc.nextInt();

		while (count <= n)
		{
			for (i = 2; i <= prime - 1; i++)
			{
				if (prime % i == 0)
				{
					break;
				}
			}
			if (i == prime)
			{
				System.out.println(prime);
				count++;
			}
			prime++;
		}

	}

}
