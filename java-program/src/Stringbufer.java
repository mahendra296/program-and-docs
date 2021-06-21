
public class Stringbufer
{
	public static void main(String args[])
	{
		StringBuffer sb = new StringBuffer("Hello");
		StringBuffer sb1 = new StringBuffer("Mahi");
		StringBuffer sb2 = new StringBuffer("Mahendra");

		System.out.println(sb.charAt(3));

		sb.append("Java");
		sb1.insert(1, "Hi..");

		sb2.reverse();
		System.out.println(sb2);
		System.out.println(sb2.capacity());

		System.out.println(sb);
		System.out.println(sb1);
		sb.replace(1, 2, "new");
		System.out.println(sb);
		sb.delete(1, 5);
		System.out.println(sb);

	}
}
