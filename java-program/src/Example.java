import java.lang.Runnable;
import java.lang.Thread;

class a implements Runnable
{
	public void run()
	{
		for (int i = 0; i <= 10; i++)
		{
			System.out.println("Thred A" + i);
		}
	}

}
class b implements Runnable
{
	public void run()
	{
		for (int i = 0; i <= 10; i++)
		{
			System.out.println("Thred B" + i);
		}
	}

}

public class Example
{
	public static void main(String[] args)
	{
		Thread t1 = new Thread(new a());
		Thread t2 = new Thread(new b());
		t1.start();
		t2.start();
	}

}
