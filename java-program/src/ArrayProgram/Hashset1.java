package ArrayProgram;

import static java.lang.reflect.Array.set;
import java.util.HashSet;
import java.util.Iterator;

public class Hashset1
{

	public static void main(String[] args)
	{
		HashSet<String> h1 = new HashSet();
		h1.add("Mahi");
		h1.add("Ram");
		h1.add("Krishna");
		h1.add("Radhe");
		h1.add("Mahi");

		System.out.println("Hashset : \n\n" + h1);

		Iterator<String> i = h1.iterator();
		System.out.println();
		while (i.hasNext())
		{
			System.out.println(i.next());
		}

		System.out.println("Size of Hashset : " + h1.size());
		boolean b = h1.isEmpty();
		String s = "";
		if (b = false)
		{
			s = "Yes";
		}
		else
		{
			s = "No";
		}
		System.out.println("Hash set is Empty : " + s);
		h1.clear();
		System.out.println(h1);
	}
}
