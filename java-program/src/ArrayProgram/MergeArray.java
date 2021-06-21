package ArrayProgram;

public class MergeArray
{
	public static void main(String[] args)
	{
		int c = 0;
		int arr1[] = { 10, 15, 25, 30 };
		int arr2[] = { 20, 25, 30, 15 };

		int n1 = arr1.length;
		int n2 = arr2.length;
		int arr3[] = new int[n1 + n2];

		for (int i = 0; i < n1; i++)
		{
			arr3[i] = arr1[i];
			c++;
		}
		for (int j = 0; j < n2; j++)
		{
			arr3[c++] = arr2[j];
		}

		for (int k = 0; k < arr3.length; k++)
		{
			System.out.println(arr3[k]);
		}

	}

}
