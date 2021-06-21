package program;

public class SumofString
{
	public static void main(String[] args)
	{
		String s = "12345";
		int n;
		int sum = 0;

		/*for(int j=s.length() -1;j>=0;j--)
		{
			System.out.println(s.charAt(j));
		}*/

		for (int i = 0; i < s.length(); i++)
		{
			n = Integer.valueOf(String.valueOf(s.charAt(i)));
			// System.out.println(n);
			sum = sum + n;
		}
		System.out.println(sum);
	}
}
