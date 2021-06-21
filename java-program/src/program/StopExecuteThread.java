package program;

class Tstop1 extends Thread
{
	@Override
	public void run()
	{
		for (int i = 0; i <= 10; i++)
		{
			System.out.println("Thred A :" + i);
			Thread.currentThread().stop();

		}
	}

}
class Tstop2 extends Thread
{
	@Override
	public void run()
	{
		for (int i = 0; i <= 10; i++)
		{
			System.out.println("Thred B :" + i);
			Thread.currentThread().stop();
		}
	}

}

public class StopExecuteThread
{
	public static void main(String[] args)
	{
		Tstop1 t1 = new Tstop1();
		Tstop2 t2 = new Tstop2();
		t1.start();
		t2.start();
	}
}
