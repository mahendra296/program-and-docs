/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ArrayProgram;

import java.util.Arrays;

/**
 *
 * @author MAHENDRA
 */
public class EvenOrOdd
{
	public static void main(String[] args)
	{
		int[] arr = { 5, 7, 2, 4, 9 };
		System.out.println("Original Array: " + Arrays.toString(arr));
		int even = 0;
		for (int i = 0; i < arr.length; i++)
		{
			if (arr[i] % 2 == 0)
				even++;
		}
		System.out.println("Number of even numbers : " + even);
		System.out.println("Number of odd numbers  : " + (arr.length - even));
	}

}
