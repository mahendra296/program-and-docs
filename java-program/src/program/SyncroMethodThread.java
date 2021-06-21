package program;

import java.util.Scanner;

class IsBank
{
	String	Name;
	float	balance	= 20000;
	Scanner	sc		= new Scanner(System.in);
	synchronized void withdrowAmount()
	{
		System.out.println("Enter The Withdrow Amount =>\n");
		int amount = Integer.parseInt(sc.nextLine());

		if (amount < balance)
		{
			balance -= amount;
			System.out.println("nWithdrow Amount is => " + amount + "\nAvaliable Balance is => " + balance);

		}
		else
		{
			System.out.println("Insufficient Balance to Withdraw");
		}

	}
}
class Msyncro1 extends Thread
{

	IsBank amount;
	Msyncro1(IsBank amount)
	{
		this.amount = amount;
	}
	public void run()
	{

		amount.withdrowAmount();
		System.out.println("Thread A is Running");

	}
}
class Msyncro2 extends Thread
{

	IsBank amount;
	Msyncro2(IsBank amount)
	{
		this.amount = amount;
	}
	public void run()
	{

		amount.withdrowAmount();

		System.out.println("Thread B is Running");

	}
}
public class SyncroMethodThread
{
	public static void main(String[] args)
	{
		Msyncro1 t1 = new Msyncro1(new IsBank());

		Msyncro2 t2 = new Msyncro2(new IsBank());

		t1.start();
		t2.start();

	}
}
