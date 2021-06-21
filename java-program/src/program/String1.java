package program;

public class String1
{
	public static void main(java.lang.String[] args)
	{
		String s1 = "Computer";
		String s2 = "Computer";
		String ss = "Laptop";
		String s3 = new String("computer");
		System.out.println(s1.equals(s2));
		System.out.println(s2.equals(s3));
		System.out.println(s1.equals(ss));
		System.out.println(s1.equalsIgnoreCase(s2));

		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");

		System.out.println(s1.startsWith("Ca"));
		System.out.println(s1.endsWith("r"));

		System.out.println(s1.charAt(2));
		System.out.println(s1.charAt(5));

		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");

		System.out.println(s1 == s2);
		System.out.println(s2 == s3);

		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println(s1.substring(0, 2));
		System.out.println(s1.substring(3));

		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");

		String con = "Chhatraliya" + "Mahi" + 29 + 01;
		System.out.println(con);
		System.out.println(s1.concat(ss));

		String trim1 = "  Mahi  ";
		System.out.println(trim1);
		System.out.println(trim1.trim());

		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");

		System.out.println(s1.compareTo(s2));//0  
		System.out.println(s1.compareTo(ss));//1(because s1>s3)  
		System.out.println(ss.compareTo(s1));

		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");

		String s4 = new String("Computer");
		String s5 = new String("computer");
		System.out.println(s1);
		String s = s1.toUpperCase();
		System.out.println(s);
		s = s1.toLowerCase();
		System.out.println(s);

		int i = s1.indexOf("p");
		int i1 = s1.indexOf("ute", 3);
		int i2 = s1.lastIndexOf("mp", 2);
		System.out.println("Index is : " + i);
		System.out.println("Index is : " + i1);
		System.out.println("Index is : " + i2);

		if (s1.equals(s3))
		{
			System.out.println("String is same");
		}
		else
		{
			System.out.println("String is not same");
		}
		if (s1.equalsIgnoreCase(s3))
		{
			System.out.println("String is same");
		}
		else
		{
			System.out.println("String is not same");
		}
		int t1 = s1.compareTo(s3);
		if (i == 0)
		{
			System.out.println("string are same");
		}
		else if (i > 0)
		{
			System.out.println("oposite to dictionary order");
		}
		else
		{
			System.out.println("Dictonary Order");

		}
	}
}
