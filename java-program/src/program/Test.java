package program;

import java.util.Scanner;

class Fact
{
	int		fact	= 1;
	int		a, b, c;
	String	s;
	public void fact(int n)
	{
		while (n > 0)
		{
			fact = fact * n;
			n--;
		}
		System.out.println(fact);
	}
}
class Reverse extends Fact
{
	public void reverseString()
	{
		String rever = "";
		System.out.println("Enter Any String ==>");
		Scanner sc = new Scanner(System.in);
		s = sc.nextLine();
		int len = s.length();
		int l = len;
		System.out.println(l);
		for (int i = len - 1; i >= 0; i--)
		{
			rever = rever + s.charAt(i);

		}

		System.out.println("orignal String is : " + s);
		System.out.println("Reverse String is : " + rever);

	}
}
class Sum extends Fact
{
	Scanner sc = new Scanner(System.in);
	public void sum()
	{
		System.out.println("Enter Number 1==>");
		a = sc.nextInt();
		System.out.println("Enter Number 2==>");
		b = sc.nextInt();

		c = a + b;

		System.out.println(c);
	}
}
public class Test
{
	public static void main(String[] args)
	{
		//   Scanner sc = new Scanner(System.in);
		//   Sum s1 = new Sum();
		//  System.out.println("Enter Number==>");

		//    int no = sc.nextInt();
		// s1.fact(no);
		//   s1.sum();

		Reverse r1 = new Reverse();
		r1.reverseString();

	}
}
