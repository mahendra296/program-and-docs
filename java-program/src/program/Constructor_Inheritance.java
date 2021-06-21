package program;

class Super1
{
	int x;
	public Super1()
	{
		System.out.println("Default constructor Super1");
	}

	public Super1(int x)
	{
		this.x = x;
		System.out.println("parameter super constructor : " + x);
	}

	public static void status()
	{
		System.out.println("Default");
	}
}

class Sub1 extends Super1
{
	int y;
	public Sub1()
	{
		super();
		System.out.println("Default constructor Sub1");
	}
	public Sub1(int y)
	{
		super(4);
		this.y = y;
		System.out.println("parameter sub constructor : " + y);
	}

	public static void subject()
	{
		System.out.println("Subject");
	}

	public void index()
	{
		System.out.println("index");
	}
}

public class Constructor_Inheritance
{

	public static void main(String[] args)
	{
		Sub1 s = new Sub1(4);

	}
}
