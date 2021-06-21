package program;

import java.util.Iterator;
import java.util.TreeSet;

public class Treeset1
{

	public static void main(String[] args)
	{
		TreeSet<String> t = new TreeSet();
		t.add("1");
		t.add("2");
		t.add("3");
		t.add("1");
		t.add("3");
		//Traversing elements  
		Iterator<String> i = t.iterator();
		while (i.hasNext())
		{
			System.out.print("[" + i.next() + "]");
		}
		System.out.println("Initial Set: " + t);

		System.out.println("Reverse Set: " + t.descendingSet());

		System.out.println("Head Set: " + t.headSet("2", true));

		System.out.println("SubSet: " + t.subSet("1", true, "3", true));

		System.out.println("TailSet: " + t.tailSet("3", false));

		System.out.println();
		TreeSet<Integer> iset = new TreeSet<>();
		iset.add(24);
		iset.add(66);
		iset.add(12);
		iset.add(15);

		System.out.println("Highest Value: " + iset.pollFirst());
		System.out.println("Lowest Value: " + iset.pollLast());

	}
}
