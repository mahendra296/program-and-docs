/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ArrayProgram;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author MAHENDRA
 */
public class Arraylist
{
	public static void main(String[] args)
	{
		ArrayList<Serializable> l = new ArrayList<Serializable>();
		ArrayList<String> l1 = new ArrayList<String>();
		l1.add("1");
		l1.add("2");
		l1.add("3");

		l.add("A");
		l.add("B");
		l.add("C");
		l.add("E");
		l.add("C");
		l.add("F");
		l.add("C");
		System.out.println(l);
		l.add(2, l1);
		System.out.println(l);
		l.set(1, "D");
		System.out.println(l);
		System.out.println("IndeOf C : " + l.indexOf("C"));
		System.out.println("IndeOf c : " + l.indexOf("c"));
		System.out.println("IndeOf E : " + l.indexOf("E"));
		System.out.println("lastIndeOf C : " + l.lastIndexOf("C"));
		System.out.println("Using Get Method : " + l.get(3));
		l.remove(0);
		System.out.println("Size of ArrayList : " + l.size());
		System.out.println("Remove second Element : " + l);
		l.retainAll(l1);
		System.out.println("RetainAll : " + l);

	}

}
