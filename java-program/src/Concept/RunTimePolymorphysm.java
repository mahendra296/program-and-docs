
package Concept;
class Bank
{
	float getRateOfInterest()
	{
		return 0;
	}
}
class Sbi extends Bank
{
	@Override
	float getRateOfInterest()
	{
		return 7.1f;
	}
}
class Icici extends Bank
{
	@Override
	float getRateOfInterest()
	{
		return 5.1f;
	}
}
class Axis extends Bank
{
	@Override
	float getRateOfInterest()
	{
		return 8.2f;
	}
}
public class RunTimePolymorphysm
{
	public static void main(String[] args)
	{
		Bank b;
		b = new Bank();
		System.out.println("Bank Rate of Interest: " + b.getRateOfInterest());
		b = new Sbi();
		System.out.println("SBI Rate of Interest: " + b.getRateOfInterest());
		b = new Icici();
		System.out.println("Icici Rate of Interest: " + b.getRateOfInterest());
		b = new Axis();
		System.out.println("Axis Rate of Interest: " + b.getRateOfInterest());

	}

}
