
package ArrayProgram;

public class GenricMethod
{
	public <E> void PrintArray(E[] n)
	{
		for (int i = 0; i < n.length; i++)
		{
			System.out.println(n[i]);
		}
	}
	public static void main(String[] args)
	{
		GenricMethod gm = new GenricMethod();

		String[] name = { "Mahi", "Rana", "Dhruva", "Deva", "Raja" };
		Integer number[] = { 25, 20, 10, 15, 13 };
		gm.PrintArray(name);
		gm.PrintArray(number);

	}

}
