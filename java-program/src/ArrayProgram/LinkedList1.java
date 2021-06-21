package ArrayProgram;

import java.util.Iterator;
import java.util.LinkedList;

public class LinkedList1
{

	public static void main(String args[])
	{

		LinkedList<String> l = new LinkedList<String>();

		l.add("Ravi");
		l.add("Vijay");
		l.addFirst("Mahi");
		l.addLast("Mahi");
		l.add(2, "Ram");
		l.add("Ajay");

		Iterator<String> itr = l.iterator();
		while (itr.hasNext())
		{
			System.out.print(itr.next() + " ");
		}
		System.out.println();
		System.out.println("****************************************************************");
		LinkedList<String> ll = new LinkedList<String>();
		System.out.println("\n********** Initial list of elements *********\n" + ll);
		ll.add("1");
		ll.add("2");
		ll.add("3");
		System.out.println("\n********** After  add(E e) method *********\n" + ll);
		//Adding an element at the specific position  
		ll.add(1, "4");
		System.out.println("\n********** After  add(int index, E element) method **********\n" + ll);
		LinkedList<String> ll2 = new LinkedList<String>();
		ll2.add("5");
		ll2.add("6");
		//Adding second list elements to the first list  
		ll.addAll(ll2);
		System.out.println("\n********** After  addAll(Collection<? extends E> c) method **********\n" + ll);
		LinkedList<String> ll3 = new LinkedList<String>();
		ll3.add("11");
		ll3.add("22");
		//Adding second list elements to the first list at specific position  
		ll.addAll(1, ll3);
		System.out.println("\n********** After  addAll(int index, Collection<? extends E> c) method **********\n" + ll);
		//Adding an element at the first position  
		ll.addFirst("0");
		System.out.println("\n********** After  addFirst(E e) method **********\n" + ll);
		//Adding an element at the last position  
		ll.addLast("7");
		System.out.println("\n********** After  addLast(E e) method **********\n" + ll);

		System.out.println("*************** Remove *****************************************");

		LinkedList<String> l1 = new LinkedList<String>();
		l1.add("1");
		l1.add("2");
		l1.add("3");
		l1.add("4");
		l1.add("5");
		l1.add("6");
		l1.add("7");
		l1.add("5");
		l1.add("6");
		l1.add("8");
		System.out.println("Initial list of elements: " + l1);
		//Removing specific element from arraylist  
		System.out.println("\n********** After  get() method **********\n" + l1.get(5));
		System.out.println("\n********** After  getFirst() method **********\n" + l1.getFirst());
		System.out.println("\n********** After  getLast() method **********\n" + l1.getLast());
		System.out.println("\n********** After  element() method **********\n" + l1.element());
		System.out.println("\n********** After  contains() method **********\n" + l1.contains("3"));
		l1.remove("Vijay");
		System.out.println("\n ********** After  remove(object) method **********\n" + l1);
		//Removing element on the basis of specific position  
		l1.remove(0);
		System.out.println("\n********** After  remove(index) method **********\n" + l1);
		LinkedList<String> l2 = new LinkedList<String>();
		l2.add("Ravi");
		l2.add("Hanumat");
		// Adding new elements to arraylist  
		l1.addAll(l2);
		System.out.println("\n********** Updated list  **********\n" + l1);
		//Removing al1 the new elements from arraylist  
		l1.removeAll(l2);
		System.out.println("\n********** After  removeAl1() method **********\n" + l1);
		//Removing first element from the list  
		l1.removeFirst();
		System.out.println("\n********** After  removeFirst() method **********\n" + l1);
		//Removing first element from the list  
		l1.removeLast();
		System.out.println("\n********** After  removeLast() method **********\n" + l1);
		//Removing first occurrence of element from the list  
		l1.removeFirstOccurrence("Gaurav");
		System.out.println("\n********** After  removeFirstOccurrence() method **********\n" + l1);
		//Removing last occurrence of element from the list  
		l1.removeLastOccurrence("Harsh");
		System.out.println("\n********** After  removeLastOccurrence() method **********\n" + l1);
		l1.pollFirst();
		System.out.println("\n********** After  pollFirst() method **********\n" + l1);
		l1.pollLast();
		System.out.println("\n********** After  pollLast() method **********\n" + l1);

		System.out.println("\n********** After  peekFirst() method **********\n" + l1.peekFirst());

		System.out.println("\n********** After  peekLast() method **********\n" + l1.peekLast());

		System.out.println("\n********** After  peekLast() method **********\n" + l1.peek());

		l1.offerFirst("8");
		System.out.println("\n********** After  offerFirst() method **********\n" + l1);

		l1.offerLast("9");
		System.out.println("\n********** After  offerLast() method **********\n" + l1);
		//Removing al1 the elements available in the list       
		l1.clear();
		System.out.println("\n********** After  clear() method **********\n" + l1);
	}
}
