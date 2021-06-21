class one
{
	public one(int x)
	{
		System.out.println("one");
	}
}
class two extends one
{
	public two()
	{
		super(4);
		System.out.println("two");
	}
}
public class ConstructorExample
{
	public static void main(String[] args)
	{
		two to = new two();
	}
}
