package program;

class Super
{

	int x;

	public void fun1()
	{
		System.out.println("Super fun1");

	}
}

class Sub extends Super
{

	int x;

	@Override
	public void fun1()
	{
		System.out.println("Sub fun1");
		super.fun1();
	}

	public void fun2()
	{
		int x;
		x = 5;
		this.x = 10;
		super.x = 20;

		System.out.println("x = " + x);
		System.out.println("this.x = " + this.x);
		System.out.println("super.x = " + super.x);

	}

}

public class Super_this_keyword
{
	public static void main(String[] args)
	{
		Sub obj = new Sub();
		obj.fun1();
		obj.fun2();
	}

}
