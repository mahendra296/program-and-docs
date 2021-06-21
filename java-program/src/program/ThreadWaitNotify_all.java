package program;

import java.util.logging.Level;
import java.util.logging.Logger;

class Customer extends Thread
{

	int amount = 2000;

	synchronized void withdraw(int amount) throws InterruptedException
	{
		if (this.amount < amount)
		{
			System.out.println("Less balance; waiting for deposit...");
			try
			{
				wait();
			}
			catch (InterruptedException interruptedException)
			{}
		}
		this.amount -= amount;
		System.out.println("withdraw completed...");
	}

	synchronized void deposit(int amount)
	{
		this.amount += amount;
		System.out.println("deposit completed... ");
		notify();
	}
}
class WThread1 extends Thread
{

	public void run()
	{
		try
		{
			Customer c = new Customer();
			c.withdraw(15000);

		}
		catch (InterruptedException ex)
		{
			Logger.getLogger(WThread1.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}

class WThread2 extends Thread
{

	public void run()
	{
		Customer c = new Customer();
		c.deposit(10000);
	}
}

public class ThreadWaitNotify_all
{
	public static void main(String[] args)
	{
		WThread1 t1 = new WThread1();
		WThread2 t2 = new WThread2();

		t1.start();
		t2.start();

	}
}
