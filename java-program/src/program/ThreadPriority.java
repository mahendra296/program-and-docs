package program;

class Tprio1 extends Thread
{
	@Override
	public void run()
	{
		for (int i = 0; i <= 10; i++)
		{
			System.out.println("Thred A :" + i);
			System.out.println("Runing Thread Name : " + Thread.currentThread().getName());
			System.out.println("Running Thread Priority is : " + Thread.currentThread().getPriority());
		}
	}

}
class Tprio2 extends Thread
{
	@Override
	public void run()
	{
		for (int i = 0; i <= 10; i++)
		{
			System.out.println("Thred B :" + i);

			System.out.println("Runing Thread  Name is : " + Thread.currentThread().getName());
			System.out.println("Running Thread Priority is : " + Thread.currentThread().getPriority());
		}
	}

}
public class ThreadPriority
{
	public static void main(String[] args)
	{
		Tprio1 t1 = new Tprio1();
		Tprio2 t2 = new Tprio2();
		Tprio1 t3 = new Tprio1();
		t1.setName("A");
		t2.setName("B");
		t1.setPriority(Thread.MIN_PRIORITY);
		t2.setPriority(Thread.MAX_PRIORITY);
		t1.start();
		t2.start();
		t3.start();
	}
}
