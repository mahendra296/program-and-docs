package ArrayProgram;

import java.util.*;

public class ArrayLargeElement
{
	public static void main(String[] args)
	{
		int arr[] = new int[20];
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter no of element :=> ");
		int n = sc.nextInt();
		System.out.println("Enter number -> ");
		for (int i = 0; i < n; i++)
		{
			arr[i] = sc.nextInt();
		}
		for (int i = 0; i < n; i++)
		{
			if (arr[0] < arr[i])
			{
				arr[0] = arr[i];
			}
		}

		System.out.println("Largest element is :: " + arr[0]);
	}

}
