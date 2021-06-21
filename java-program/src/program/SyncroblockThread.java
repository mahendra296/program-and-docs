package program;

class Table
{

	void printTable(int n)
	{
		synchronized (this)
		{//synchronized block  
			for (int i = 1; i <= n; i++)
			{
				for (int j = 1; j <= n; j++)
				{
					System.out.print(j + "*" + i + "=" + (j * i) + " ");

				}
				System.out.println();

			}

		}
	}
}

class Syncro1 extends Thread
{
	Table t;
	Syncro1(Table t)
	{
		this.t = t;
	}
	public void run()
	{
		t.printTable(3);
	}
}

class Syncro2 extends Thread
{

	Table t;
	Syncro2(Table t)
	{
		this.t = t;
	}
	public void run()
	{
		t.printTable(5);
	}
}

class Syncro3 extends Thread
{

	Table t;
	Syncro3(Table t)
	{
		this.t = t;
	}
	public void run()
	{
		t.printTable(7);
	}
}

public class SyncroblockThread
{

	public static void main(String[] args)
	{
		Syncro1 t1 = new Syncro1(new Table());
		Syncro1 t2 = new Syncro1(new Table());
		Syncro2 t3 = new Syncro2(new Table());
		Syncro3 t4 = new Syncro3(new Table());

		t1.start();
		t2.start();
		t3.start();
		t4.start();
	}
}
