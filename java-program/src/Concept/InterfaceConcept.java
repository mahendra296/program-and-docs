
package Concept;

interface I1
{
	public void show();
}
interface I2
{
	public void display();
}
interface I3 extends I1, I2
{
	public void run();
}

class Extra implements I3
{

	@Override
	public void run()
	{
		System.out.println("Hello Run");
	}

	@Override
	public void show()
	{
		System.out.println("Hello Show");
	}

	@Override
	public void display()
	{
		System.out.println("Hello Display");
	}

}
public class InterfaceConcept
{
	public static void main(String[] args)
	{
		Extra e = new Extra();
		e.run();
		e.show();
		e.display();

	}

}
