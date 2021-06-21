
public class GenericMethod
{

	public <m> void printArray(m[] s)
	{

		//for(m x:s)
		//    System.out.println(s[i]); 
		for (int i = 0; i < s.length; i++)
		{
			System.out.println(s[i]);
		}
	}

	public static void main(String[] args)
	{
		GenericMethod g = new GenericMethod();
		String contry[] = new String[] { "India", "Pakistan", "Nepal" };
		Integer number[] = { 12, 13, 14, 15, 16 };

		g.printArray(contry);
		g.printArray(number);

	}

}
