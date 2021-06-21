//  Converting a superclass type to a subclass type is known as downcasting.
package Concept;

class Super1
{
	void Sample()
	{
		System.out.println("method of super class");
	}
}

class Sub1 extends Super1
{
	void Sample()
	{
		System.out.println("method of sub class");
	}
}
public class DownCasting
{
	public static void main(String args[])
	{
		Super1 obj = new Sub1();
		obj.Sample();
		Sub1 sub1 = (Sub1) obj;
		sub1.Sample();
	}
}
