package ArrayProgram;

import com.sun.istack.internal.FinalArrayList;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class ArraylistPrac
{

	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		ArrayList<String> l1 = new ArrayList<String>();
		ArrayList<String> l2 = new ArrayList<String>();
		ArrayList<String> l3 = new ArrayList<String>();
		ArrayList<String> l4 = new ArrayList<String>();
		l1.add("Mahi");
		l1.add("Nil");
		l1.add("Nitin");
		l1.add("Mukesh"); // boolean add(E e)
		l1.add("Sahid");
		l1.add(3, "Rahu"); //void add(int index, E element)
		l1.add(5, "Ketu");
		l2.addAll(l1); //boolean addAll(Collection<? extends E> c)
		l3.addAll(l1);
		l4.add("111");
		l4.add("222");
		l4.add("333");
		l4.add("444");
		l4.add("Nil");

		System.out.println("*************Direct Print List **************");
		System.out.println("ArrayList l1 " + l1);
		System.out.println("ArrayList l2 " + l2);
		System.out.println("ArrayList l3 " + l3);

		Iterator it1 = l1.iterator();
		Iterator it2 = l2.iterator();

		System.out.println("************* Iterator List l1 **************");

		while (it1.hasNext())
		{
			System.out.print("[" + it1.next() + "] , ");
		}
		System.out.println("\n");
		System.out.println("************* Iterator List l2 **************");
		while (it2.hasNext())
		{
			System.out.print("[" + it2.next() + "] , ");
		}
		System.out.println("\n");
		System.out.println("************* Using For Each  **************");
		for (String i : l1)
		{
			System.out.print("[" + i + "] , ");
		}
		System.out.println("\n");
		System.out.println("****** Remove Element From L1 *********");
		String r1 = l1.remove(3);
		l3.removeAll(l3);
		System.out.println(l3);
		System.out.println("Remove Element : " + r1);
		System.out.println(l1);

		System.out.println("****** Serialize  *********");

		FileOutputStream fos = new FileOutputStream("Array.txt");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(l1);
		fos.close();
		oos.close();
		FileInputStream fis = new FileInputStream("Array.txt");
		ObjectInputStream ois = new ObjectInputStream(fis);
		ArrayList list = (ArrayList) ois.readObject();
		System.out.println(list);

		System.out.println("********boolean addAll(int index, Collection<? extends E> c)**********");
		l1.addAll(3, l4); //boolean addAll(int index, Collection<? extends E> c)
		System.out.println(l1);
		System.out.println("********Clone shallow copy l4**********");
		l4.clone();
		System.out.println(l4);
		System.out.println("********void clear**********");
		l4.clear(); //void clear()
		System.out.println(l4);
		System.out.println("********Get**********");
		System.out.println(l2.get(2)); // E get(int index)
		System.out.println(l2.lastIndexOf(l2));
		System.out.println("Removed from l2 : " + l2.remove(2));
	}
}
