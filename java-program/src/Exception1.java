
public class Exception1
{
	public static void main(String[] args)
	{
		System.out.println("First line");
		try
		{
			System.out.println("Result is : " + 3 / 0);
		}
		catch (Exception e)
		{
			System.out.println("not devided");
		}
		System.out.println("Last line");
	}
}
