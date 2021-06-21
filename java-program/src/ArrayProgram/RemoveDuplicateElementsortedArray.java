/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ArrayProgram;

import java.util.Arrays;
import java.util.Scanner;

public class RemoveDuplicateElementsortedArray
{
	public static void main(String[] args)
	{
		int n, count = 0, i, j;
		int a[] = { 1, 2, 2, 3, 4, 5, 5 };
		int b[] = new int[7];
		n = a.length;
		int last = a[n - 1];
		for (i = 0; i < n - 1; i++)
		{
			if (a[i] != a[i + 1])
			{
				a[count] = a[i];
				count++;
			}

		}
		a[count] = last;
		for (i = 0; i <= count; i++)
		{
			System.out.println(a[i]);

		}

		/*
		for (i = 0; i < n - 1; i++)
		{
		    if(a[i] != a[i+1])
		    {
		        b[count] = a[i];
		        count++;
		    }
		        
		}
		b[count] = last; 
		for (i = 0; i < n - 1; i++)
		{
		     System.out.println(b[i]);
		        
		}
		 */
	}
}
