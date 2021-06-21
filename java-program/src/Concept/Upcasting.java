/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Concept;

class Animal
{
	public void callme()
	{
		System.out.println("Animal");
	}
	public void dont()
	{
		System.out.println("dont");
	}
}

class Dog extends Animal
{
	public void callme()
	{
		System.out.println("Dog");
	}

	public void callme2()
	{
		System.out.println("Dog21212");
	}
}
public class Upcasting
{
	public static void main(String[] args)
	{
		Dog d = new Dog();
		Animal a1 = new Dog();
		Animal a2 = (Animal) d;
		d.callme();
		a1.callme();
		a1.dont();
		a2.callme();
		a2.dont();
		((Dog) a2).callme2();
	}

}
