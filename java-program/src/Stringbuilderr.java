
public class Stringbuilderr
{
	public static void main(String[] args)
	{

		StringBuilder sb = new StringBuilder("Computer");
		StringBuilder sb1 = new StringBuilder("PC");
		StringBuilder sb2 = new StringBuilder("Mouse");

		System.out.println(sb2.reverse());
		System.out.println(sb.append(" Laptop"));
		System.out.println(sb.replace(1, 2, "new"));
	}
}
