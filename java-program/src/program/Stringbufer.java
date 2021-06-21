package program;

public class Stringbufer
{

	public static void main(String args[])
	{
		StringBuffer sb = new StringBuffer("Hello");
		StringBuffer sb1 = new StringBuffer("Mahi");
		StringBuffer sb2 = new StringBuffer("Mahendra");

		System.out.println(sb.charAt(3));

		sb.append("Java");

		System.out.println(sb);
		sb1.insert(3, "Hi..");
		System.out.println(sb1);

		sb2.reverse();
		System.out.println(sb2);
		System.out.println(sb2.capacity());

		sb.replace(1, 2, "Kal");
		System.out.println(sb);
		sb.delete(1, 5);
		System.out.println(sb);

	}
}
