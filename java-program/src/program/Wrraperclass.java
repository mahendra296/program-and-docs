/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program;

/**
 *
 * @author MAHENDRA
 */
public class Wrraperclass
{
	public static void main(String[] args)
	{
		Integer i = Integer.valueOf("1");
		System.out.println("Value of Integer: " + i);
		Integer i1 = Integer.valueOf("10110", 2);
		System.out.println("Value of Integer: " + i1);
		int a = Integer.parseInt("123");
		System.out.println("parseInt : " + a);

		Double d1 = Double.valueOf("3.14");
		System.out.println("Value of Double : " + d1);
		Double d2 = Double.parseDouble("13.15");
		System.out.println("parseDouble : " + d2);

	}

}
