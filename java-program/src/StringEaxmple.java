
public class StringEaxmple
{
	public static void main(String[] args)
	{
		String s = "Mahi";
		String ss = "Mahi";
		String ss1 = new String("Mahi");
		String ss2 = new String("Mahi");
		String s1 = new String("Computer");
		String s3 = new String("computer");

		System.out.println(s.equals(ss)); //true
		System.out.println(s.equals(ss1)); //true
		System.out.println(ss1.equals(ss2)); //true
		System.out.println(ss1.equals(ss)); //true

		String str = "Hello Javatpoint";
		System.out.println(str.contains("javatpoint")); //false because case sensetive

		System.out.println(s.contains("hi")); //true
		System.out.println(s.concat(" Ni ").concat(s3)); // Mahi Ni computer 
		System.out.println(s1); // Computer
		String s2 = s1.toUpperCase();
		System.out.println(s2); // COMPUTER
		s2 = s1.toLowerCase();
		System.out.println(s2); // computer

		int i = s1.indexOf("p");
		int i1 = s1.indexOf("ute", 3);
		int i2 = s1.lastIndexOf("mp", 1);
		int i3 = s3.lastIndexOf("rr", 1);
		int i4 = s1.indexOf("omp", 2);
		System.out.println("Index is (p): " + i);
		System.out.println("Index is (ute,3) : " + i1);
		System.out.println("lastIndexOf is (mp,1) : " + i2);
		System.out.println("Index is (omp,1) : " + i4);

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
