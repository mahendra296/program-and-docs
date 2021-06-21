
package ArrayProgram;

public class MissingNumber
{
	public static void main(String[] args)
	{
		int i = 1, j;
		String number = "";
		int arr[] = { 8, 7, 6, 5, 3 };
		int n = arr.length;

		for (i = 1; i < 10; i++)
		{
			int counter = 0;
			for (j = 0; j < n; j++)
			{
				if (i != arr[j])
				{
					counter = counter + 1;
				}
			}
			if (counter == n)
			{
				//Appended the missing number to the String
				number = number + i + " ";
			}
		}
		System.out.println(number);

	}

}
