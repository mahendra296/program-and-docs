class AA1
{
	public void f1(int x)
	{
		System.out.println("Class A:");
	}
}

class BB1 extends AA1
{
	public void f1(int x, int y)
	{
		System.out.println("Class B:");
	}
}
public class Foverloding
{
	public static void main(String[] args)
	{
		BB1 b = new BB1();
		b.f1(5);
		b.f1(5, 6);
	}
}
