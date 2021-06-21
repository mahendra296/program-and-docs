package program;

class De1 implements Runnable
{

	@Override
	public void run()
	{
		for (int i = 0; i <= 1000; i++)
		{

			if (Thread.currentThread().isDaemon())
			{//checking for daemon thread  
				System.out.println("Daemon thread De1 : " + i);
			}
			else
			{
				System.out.println("Thred De1 : " + i);
			}
		}
	}

}

class De2 implements Runnable
{

	@Override
	public void run()
	{
		for (int i = 0; i <= 1000; i++)
		{
			System.out.println("Thred De2 : " + i);
		}
	}

}
class De3 implements Runnable
{

	@Override
	public void run()
	{
		for (int i = 0; i <= 10; i++)
		{

			System.out.println("Thred De3 : " + i);
		}
	}
}

public class DemonThread
{

	public static void main(String[] args)
	{
		Thread t1 = new Thread(new De1());
		Thread t2 = new Thread(new De2());
		Thread t3 = new Thread(new De2());
		Thread t4 = new Thread(new De3());
		Thread t5 = new Thread(new De3());
		t1.setDaemon(true);
		t1.start();
		t2.start();

		t3.start();
		t4.start();
		t5.start();
		//System.out.println(t1.isAlive());
	}
}
