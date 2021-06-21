package program;

public class Sttringe
{
	public static void main(String args[])
	{
		String s1 = "java";//creating string by java string literal 
		System.out.println(s1.substring(1));
		for (int i = 0; i < s1.length(); i++)
		{
			System.out.println(s1.charAt(i));

		}
		char ch[] = { 's', 't', 'r', 'i', 'n', 'g', 's' };
		String s2 = new String(ch);//converting char array to string  
		String s3 = new String("java");
		System.out.println();
		String s4 = new String("example");
		String s5 = new String("example");
		String s6 = new String("example");//creating java string by new keyword  
		System.out.println(s1);
		System.out.println(s2);
		System.out.println(s3.hashCode() == s4.hashCode());
		System.out.println(s4.hashCode());

		System.out.println(s4 == s6);
		System.out.println(s4.equals(s6));

	}
}
