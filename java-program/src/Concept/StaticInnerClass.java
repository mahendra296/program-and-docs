/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Concept;

class Outer1
{
	static class Inner1
	{
		public void show()
		{
			System.out.println("Hello");
		}

	}
	class Demo
	{
		public void Display()
		{
			System.out.println("Demo");
		}
	}

}
public class StaticInnerClass
{
	public static void main(String[] args)
	{

		Outer1.Inner1 in = new Outer1.Inner1();
		Outer1.Demo d1 = new Outer1().new Demo();

		in.show();
		d1.Display();

	}
}
