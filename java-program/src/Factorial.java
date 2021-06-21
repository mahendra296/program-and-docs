
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Factorial
{
	public static void main(String[] args) throws IOException
	{
		int i, fact = 1;

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Enter Number ->");
		i = Integer.parseInt(br.readLine());

		while (i > 0)
		{
			fact = fact * i;
			i--;
		}
		System.out.println("Factorial is :" + fact);
	}
}
