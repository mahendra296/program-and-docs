package ArrayProgram;

import java.util.Scanner;

public class Array1
{
	public static void main(String[] args)
	{
		int arr[] = new int[5];
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter 5 Numbers = >");
		for (int i = 0; i <= 4; i++)
		{
			arr[i] = sc.nextInt();
		}
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
		for (int j = 0; j <= arr.length; j++)
		{
			System.out.println(arr[j]);
		}
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
		int small = arr[0];
		for (int i = 1; i < arr.length; i++)
		{
			if (small > arr[i])
			{
				small = arr[i];
			}
		}
		System.out.println(small);
	}
}
