
import java.util.Scanner;

public class CountSpare
{
	public static void main(String[] args)
	{
		int i, n;
		int arr[] = new int[10];
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter Number =>");
		for (i = 0; i <= 9; i++)
		{
			n = sc.nextInt();
			arr[i] = n;
		}

		System.out.println("Enter Number which Want to Compare = > ");
		int no = sc.nextInt();
		int c = 0;
		for (i = 0; i <= 9; i++)
		{
			if (arr[i] == no)
			{
				c++;
			}
		}
		System.out.println("Number Of Count Spare : " + Math.abs(c / 2));

	}

}
