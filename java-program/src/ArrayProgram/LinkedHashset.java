package ArrayProgram;

import java.util.Iterator;
import java.util.LinkedHashSet;

public class LinkedHashset
{

	public static void main(String[] args)
	{
		LinkedHashSet<String> l = new LinkedHashSet();
		l.add("One");
		l.add("Two");
		l.add("Tee");

		Iterator i = l.iterator();
		while (i.hasNext())
		{
			System.out.println(i.next());
		}
	}
}
