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
public class pra3
{

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		// TODO code application logic here
		int fact = 1, n = 5, i;
		System.out.print("Enter value of number==>");

		for (i = 1; i <= n; i++)
		{
			fact = fact * i;
		}
		System.out.println("Factorial = " + fact);
	}

}
