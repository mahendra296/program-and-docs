package program;

import java.lang.Runnable;
import java.lang.Thread;

class Th1 extends Thread
{
	public void run()
	{
		for (int i = 0; i <= 10; i++)
		{
			System.out.println("Thred A :" + i);

		}
	}

}
class Th2 extends Thread
{
	public void run()
	{
		for (int i = 0; i <= 10; i++)
		{
			System.out.println("Thred B :" + i);
		}
	}

}

public class ThreadExample
{
	public static void main(String[] args)
	{
		Thread t1 = new Thread(new Th1());
		Thread t2 = new Thread(new Th2());
		t1.start();
		t2.start();
	}

}
