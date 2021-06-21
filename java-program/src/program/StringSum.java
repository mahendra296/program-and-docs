package program;

public class StringSum
{
	public static void main(String[] args)
	{
		String s = "1234";

		int n = Integer.parseInt(s);
		System.out.println(n);
		int n1[] = new int[10];
		int i = 0;
		int sum = 0;
		while (n > 0)
		{
			n1[i] = n % 10;
			n = n / 10;
			i++;

		}
		for (int j = i - 1; j >= 0; j--)
		{
			System.out.println(n1[j]);
			sum = sum + n1[j];
		}
		System.out.println(sum);

	}
}
