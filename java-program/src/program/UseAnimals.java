/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program;

class Animal
{
	public void sup()
	{
		System.out.println("super callme of Animal");
	}
	public void me1()
	{
		System.out.println("sup callme of Animal");
	}
}

class Dog extends Animal
{
	public void me1()
	{
		System.out.println("sub callme of Dog");
	}

	public void me2()
	{
		System.out.println("sub callme2 of Dog");
	}
}

public class UseAnimals
{
	public static void main(String[] args)
	{
		Dog d = new Dog();
		Animal a = (Animal) d;
		Animal a1 = new Dog();
		d.me1();
		a.me1();
		a1.sup();
		((Dog) a).sup();
	}
}
