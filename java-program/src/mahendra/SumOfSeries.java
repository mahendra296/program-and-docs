/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mahendra;

/**
 *
 * @author Student
 */
public class SumOfSeries
{

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		// TODO code application logic here
		double sum = 0, i;
		int n = 5;

		for (i = 1; i <= n; i++)
		{
			sum = sum + 1 / i;
		}
		System.out.println("sum of series = " + sum);
	}

}
