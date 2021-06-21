package test;

import static java.lang.Math.*;
import java.util.Scanner;

public class Newmultiply
{

	public static void main(String[] args)
	{

		int no1, no2, n1 = 0, n2 = 0, s1 = 0;
		Scanner sc = new Scanner(System.in);

		System.out.println("Enter No1 : =>");
		no1 = sc.nextInt();
		n1 = abs(no1);

		System.out.println("Enter No2 : =>");
		no2 = sc.nextInt();
		n2 = abs(no2);

		for (int i = 1; i <= n2; i++)
		{
			s1 = s1 + n1;
		}
		if (no1 < 0 && no2 < 0)
		{
			System.out.println(s1);
		}
		else if (no1 < 0 || no2 < 0)
		{
			System.out.println("-" + s1);
		}
		else
		{
			System.out.println(s1);
		}
	}
}
