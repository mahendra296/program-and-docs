/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Concept;

class Demo
{
	void show()
	{
		System.out.println("i am in show method of super class");
	}
}
public class InnerClassAnonymous
{
	static Demo d = new Demo()
	{
		void show()
		{
			super.show();
			System.out.println("i am in Flavor1Demo class");
		}
	};

	public static void main(String[] args)
	{
		d.show();
	}
}
