/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mahendra;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Student
 */
public class Occurrence
{

	public static void main(String[] args) throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s;
		System.out.print("Enter the string--> ");
		s = br.readLine();
		int x[] = new int[20]; //Define Array
		int a[] = new int[25];
		int l = s.length(); //Find length of string
		int i, j, flag = 0;
		// Convert String into character Array
		for (i = 0; i < l; i++)
		{
			a[i] = s.charAt(i);
			// To fetch particular character using according index

		}
		// Find Occurrence of character  and store into x[]

		for (i = 0; i < l; i++)
		{
			for (j = 0; j < l; j++)
			{
				if (a[i] == a[j])
				{
					x[i] += 1;
				}
			}
		}
		//Display the result
		for (i = 0; i < l; i++)
		{
			for (j = 0; j < i; j++)
			{
				if (a[j] == s.charAt(i))
					flag = 1;
			}
			if (flag == 0)
			{
				System.out.println("The " + (char) a[i] + " occurs " + x[i] + " times");
			}
			flag = 0;
		}
	}
}
