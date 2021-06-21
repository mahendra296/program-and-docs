/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ArrayProgram;

public class RemoveDuplicate
{
	public static void main(String[] args)
	{
		int arr[] = { 10, 15, 10, 25, 10, 15, 32 };
		int i, j, k, n, temp;
		n = arr.length;

		for (i = 0; i < n; i++)
		{
			for (j = i + 1; j < n; j++)
			{
				if (arr[i] == arr[j])
				{
					for (k = j; k < n - 1; k++)
					{
						arr[k] = arr[k + 1];
					}
					n--;
					j--;
				}
			}
		}

		for (i = 0; i < n; i++)
		{
			System.out.println(arr[i]);
		}
	}

}
