package program;

import java.util.Scanner;

class Bank_process
{
	int		AcNo	= 001, amount;
	String	Name;
	float	balance;

	Scanner	sc		= new Scanner(System.in);

	public void checkBalance()
	{
		System.out.println("Avaliable Balance of " + AcNo + " is => " + balance);
	}
	public void withdrowAmount()
	{
		System.out.println("Enter The Withdrow Amount =>\n");
		amount = Integer.parseInt(sc.nextLine());
		if (amount < balance)
		{
			balance -= amount;
			System.out.println("ACNO => " + AcNo + "\nWithdrow Amount is => " + amount + "\nAvaliable Balance is => " + balance);

		}
		else
		{
			System.out.println("Insufficient Balance to Withdraw");
		}
	}
	public void dipositeAmount()
	{
		System.out.println("Enter The Diposite Amount =>\n");
		amount = Integer.parseInt(sc.nextLine());

		balance += amount;
		System.out.println("ACNO => " + AcNo + "\nDiposite Amount is => " + amount + "\nAvaliable Balance is => " + balance);
	}
}

public class Bank
{

	public static void main(String[] args)
	{

		Bank_process b = new Bank_process();
		Scanner sc = new Scanner(System.in);
		int choice;
		do
		{
			System.out.println("\n*****Menu*****\n");
			System.out.println("1. checkBalance..");
			System.out.println("2. Cach Diposite..");
			System.out.println("3. Cash Withdraw..");

			System.out.println("4:Exit..");
			choice = Integer.parseInt(sc.nextLine());
			switch (choice)
			{
				case 1:
					b.checkBalance();
					break;
				case 2:
					b.dipositeAmount();
					break;
				case 3:
					b.withdrowAmount();
					break;
				case 4:
					System.exit(0);
					break;
				default:
					System.out.println("Invalid Choice..");

			}
		}
		while (choice != 0);

	}

}
