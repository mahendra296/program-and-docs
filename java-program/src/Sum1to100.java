
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Sum1to100
{
	public static void main(String[] args) throws IOException
	{

		int i, s = 0;
		for (i = 1; i <= 100; i++)
		{
			s = s + i;
		}
		int a, s1 = 0, s2 = 0;
		for (a = 1; a <= 100; a++)
		{
			if (a % 2 == 0)
			{
				s1 = s1 + a;
			}
			else
			{
				s2 = s2 + a;
			}

		}

		System.out.println("Sum is = " + s);
		System.out.println("even Sum is = " + s1);
		System.out.println("odd Sum is = " + s2);

	}
}
