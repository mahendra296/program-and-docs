package program;

interface Person1
{
	public void age();
	public void name();
}
interface stud extends Person1
{
	public void rollno();
}
interface employee extends Person1
{
	public void empid();
}

abstract class Base
{
	abstract void fun();
}

class Derived extends Base implements stud, employee
{
	@Override
	void fun()
	{
		System.out.println("Hello...");
	}

	@Override
	public void rollno()
	{
		System.out.println("22"); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void age()
	{
		System.out.println("23"); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void name()
	{
		System.out.println("Mahi");//To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void empid()
	{
		System.out.println("Emp1"); //To change body of generated methods, choose Tools | Templates.
	}

}

public class Abstra
{
	public static void main(String args[])
	{
		Derived b = new Derived();
		b.fun();
		b.age();
		b.rollno();
		b.empid();
	}
}
