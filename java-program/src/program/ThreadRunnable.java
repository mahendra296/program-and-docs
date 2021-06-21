package program;

import java.lang.Runnable;
import java.lang.Thread;

class The1 implements Runnable
{
	public void run()
	{
		for (int i = 0; i <= 10; i++)
		{
			System.out.println("Thred A :" + i);
		}
	}

}
class The2 implements Runnable
{
	public void run()
	{
		for (int i = 0; i <= 10; i++)
		{
			System.out.println("Thred B :" + i);
		}
	}

}

public class ThreadRunnable
{
	public static void main(String[] args)
	{
		Thread t1 = new Thread(new The1());
		Thread t2 = new Thread(new The2());
		t1.start();
		t2.start();
	}

}
