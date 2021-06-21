
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Sum
{
	public static void main(String[] args) throws IOException
	{

		int a, b, c;

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.print("Enter First Number-->");
		a = Integer.parseInt(br.readLine());
		System.out.print("Enter Second Number-->");
		b = Integer.parseInt(br.readLine());

		c = a + b;

		System.out.println("Sum is = " + c);
	}
}
