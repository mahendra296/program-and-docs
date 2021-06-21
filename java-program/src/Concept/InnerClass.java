/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Concept;

class Outer
{
	class Inner
	{
		public void show()
		{
			System.out.println("Hello");
		}

	}

}
public class InnerClass
{
	public static void main(String[] args)
	{
		Outer out = new Outer();
		Outer.Inner in = out.new Inner();

		Outer.Inner in1 = new Outer().new Inner();

		in.show();
		in1.show();
	}
}
