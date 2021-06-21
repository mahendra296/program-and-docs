package program;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
class M1
{
	static int count;
	M1()
	{
		count++;
		//System.out.println("Helllo...."+count);
	}

	public static void display()
	{
		count *= 2;
		System.out.println("Helllo...." + count);
	}

	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
}
public class demo1419
{

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, CloneNotSupportedException
	{

		M1 m = new M1();
		M1 m1 = new M1();
		m.display();
		m1.display();
		// M1 m1= M1.class.newInstance();
		//  M1 ab=(M1) m.clone();

		System.out.println("Dispaly==");
	}
}
