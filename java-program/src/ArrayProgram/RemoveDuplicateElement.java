/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ArrayProgram;

import java.util.Arrays;

public class RemoveDuplicateElement
{
	static void unique_array(int[] arr)
	{
		System.out.println("Original Array : ");

		for (int i = 0; i < arr.length; i++)
		{
			System.out.print(arr[i] + "\t");
		}

		//Assuming all elements in input array are unique

		int len = arr.length;

		//Comparing each element with all other elements

		for (int i = 0; i < len; i++)
		{
			for (int j = i + 1; j < len; j++)
			{
				//If any two elements are found equal

				if (arr[i] == arr[j])
				{
					//Replace duplicate element with last unique element

					arr[j] = arr[len - 1];

					len--;

					j--;
				}
			}
		}

		//Copying only unique elements of arr into array1

		int[] array1 = Arrays.copyOf(arr, len);

		//Printing arrayWithoutDuplicates

		System.out.println();

		System.out.println("Array with unique values : ");

		for (int i = 0; i < array1.length; i++)
		{
			System.out.print(array1[i] + "\t");
		}

		System.out.println();

		System.out.println("---------------------------");
	}

	public static void main(String[] args)
	{
		unique_array(new int[] { 0, 3, -2, 4, 3, 2 });

		unique_array(new int[] { 10, 22, 10, 20, 11, 22 });

	}
}
