
//  Converting a subclass type to a superclass type is known as up casting.

package Concept;

class Super
{
	void Sample()
	{
		System.out.println("method of super class");
	}
}

class Sub extends Super
{
	void Sample()
	{
		System.out.println("method of sub class");
	}
}
public class Upcasting1
{
	public static void main(String args[])
	{
		Super obj = (Super) new Sub();
		obj.Sample();
	}
}
