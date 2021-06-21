package program;

import java.util.Scanner;

public class calculator
{
	public static void main(String[] args)
	{
		int no1, no2, choice, value = 0;
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter Number 1 =>");
		no1 = Integer.parseInt(sc.nextLine());
		System.out.println("Enter Number 2 =>");
		no2 = Integer.parseInt(sc.nextLine());

		/* System.out.println("\n\n--------Menu--------");
		 System.out.println("1. Addition");
		 System.out.println("2. Subtraction");
		 System.out.println("3. Multiplication");
		 System.out.println("4. Division"); */
		System.out.print("Enter Your operator(+,-,*,/) : ");
		choice = sc.next().charAt(0);
		switch (choice)
		{
			case '+':
				value = no1 + no2;
				break;
			case '-':
				value = no1 - no2;
				break;
			case '*':
				value = no1 * no2;
				break;
			case '/':
				value = no1 / no2;
				break;
			default:
				System.out.println("invalid choice");
		}

		System.out.println(value);
	}
}
