
public class Instance_InitializationBlock
{

	private int x;

	{
		System.out.println("Initialixation block X = " + x);
		x = 5;
	}

	public Instance_InitializationBlock()
	{
		System.out.println("Constructor X = " + x);
	}
	public static void main(String[] args)
	{
		Instance_InitializationBlock i1 = new Instance_InitializationBlock();
		Instance_InitializationBlock i2 = new Instance_InitializationBlock();
	}

}
