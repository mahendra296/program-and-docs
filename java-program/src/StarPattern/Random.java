
package pattern;

public class Random
{

	public static void main(String[] args)
	{
		int i, n;
		for (i = 1; i <= 10; i++)
		{
			n = (int) (Math.random() * (100 - 1)) + 1;
			System.out.println(n);
		}

	}

}
