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
public class Area
{

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws IOException
	{
		// TODO code application logic here
		int r;
		double pi = 3.14, area = 0;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.print("Enter the value of r-->");
		r = Integer.parseInt(br.readLine());

		area = 4 * pi * r * r;

		System.out.println("Area of circle = " + area);
	}

}
