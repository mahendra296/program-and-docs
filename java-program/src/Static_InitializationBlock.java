
public class Static_InitializationBlock
{

	private static int x;
	static
	{
		System.out.println("Initialixation block X = " + x);
		x = 5;
	}

	public Static_InitializationBlock()
	{
		System.out.println("Constructor X = " + x);
	}

	public static void main(String[] args)
	{
		Static_InitializationBlock i1 = new Static_InitializationBlock();
		Static_InitializationBlock i2 = new Static_InitializationBlock();
	}

}
