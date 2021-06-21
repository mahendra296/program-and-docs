/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Concept;

class Shape
{

	void draw()
	{
		System.out.println("drawing...");
	}
}

class Rectangle extends Shape
{

	void draw()
	{
		System.out.println("drawing rectangle...");
	}
}

class Circle extends Shape
{

	void draw()
	{
		System.out.println("drawing circle...");
	}
}

class Triangle extends Shape
{

	void draw()
	{
		System.out.println("drawing triangle...");
	}
}

public class RunTimePolymorphysm1
{

	public static void main(String[] args)
	{
		Shape s;
		s = new Rectangle();
		s.draw();
		s = new Circle();
		s.draw();
		s = new Triangle();
		s.draw();
	}

}
