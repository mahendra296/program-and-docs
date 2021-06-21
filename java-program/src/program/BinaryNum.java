package program;

import java.util.Scanner;

public class BinaryNum
{

	public static void main(String args[])
	{
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter Num => ");
		int num = sc.nextInt();
		int n[] = new int[10];
		int i = 0;

		while (num > 0)
		{
			n[i] = num % 2;
			num = num / 2;
			i++;
		}
		for (int j = i - 1; j >= 0; j--)
		{
			System.out.println(n[j]);
		}
	}
}
