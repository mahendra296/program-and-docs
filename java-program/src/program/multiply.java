package program;

import java.util.Scanner;

public class multiply
{
	public static void main(String[] args)
	{
		/* int i,mul,n;
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter Number : ");
		i = sc.nextInt();
		System.out.println("Enter Range Number :");
		n = sc.nextInt();
		
		for(int j=1;j<=n;j++){
		    mul = i*j;
		    System.out.println(i+"*"+j+"="+mul);
		}
		*/

		int i, j, n, mul;
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter Number : ");
		n = sc.nextInt();
		for (i = 1; i <= 10; i++)
		{
			for (j = 1; j <= n; j++)
			{
				mul = i * j;
				System.out.print(j + " * " + i + " = " + mul + "\t");

			}
			System.out.println("\n");
		}

	}
}
