package program;

class Bank1
{
	private double	balance;
	private double	interestRate;
	private double	rate;

	public Bank1()
	{
		balance = 0;
		interestRate = 0;
	}
	public Bank1(double amount, double interestRate)
	{
		balance = amount;
		rate = interestRate;

	}
	public void deposit(double amount)
	{
		balance = balance + amount;
	}
	public void withdraw(double amount)
	{
		balance = balance - amount;
	}

	public void setInterest(double rate)
	{
		balance = balance + balance * rate;

	}
	public double computeInterest(int n)
	{
		balance = Math.pow(balance * (1 + rate), n / 12);
		return balance;
	}

	public double getsetInterest()
	{
		return rate;
	}

	public double getBalance()
	{
		return balance;
	}

	public void close()
	{
		balance = 0;
	}
}
public class Account
{
	public static void main(String[] args)
	{
		Bank1 acc1 = new Bank1(500, 0.1);
		Bank1 acc2 = new Bank1(400, 0.2);

		acc1.deposit(500);
		acc1.withdraw(300);
		acc1.computeInterest(12);
		System.out.println(acc1.computeInterest(12));

		acc2.withdraw(200);
		acc2.deposit(800);
		acc2.computeInterest(24);
		System.out.println(acc2.computeInterest(24));

	}
}
