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
public class MaxMinDiff
{
	public static void main(String[] args)
	{
		int[] arr = { 5, 7, 2, 4, 9 };
		System.out.println("Original Array: " + Arrays.toString(arr));
		int max_val = arr[0];
		int min = arr[0];
		for (int i = 1; i < arr.length; i++)
		{
			if (arr[i] > max_val)
				max_val = arr[i];
			else if (arr[i] < min)
				min = arr[i];
		}
		System.out.println("Difference between the largest and smallest values of the said array: " + (max_val - min));
	}

}
