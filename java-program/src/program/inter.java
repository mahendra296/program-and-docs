package program;

interface A
{
	public void Ab();
	public void hello();
}

class B implements A
{
	public void Ab()
	{

		System.out.println("A");
	}
	public void hello()
	{

		System.out.println("hello");
	}
}
public class inter
{
	public static void main(String[] args)
	{
		B b1 = new B();
		b1.Ab();
		b1.hello();

	}
}
