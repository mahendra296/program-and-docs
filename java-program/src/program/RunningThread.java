package program;

class X extends Thread
{
	@Override
	public void run()
	{
		System.out.println("X");
	}
}
class Y extends Thread
{
	@Override
	public void run()
	{
		System.out.println("Y");
	}
}
class Z extends Thread
{
	@Override
	public void run()
	{
		System.out.println("Z");
	}
}
public class RunningThread
{
	public static void main(String[] args)
	{
		X x = new X();
		Y y = new Y();
		Z z = new Z();
		ThreadGroup tg1 = new ThreadGroup("Parent ThreadGroup");

		Thread t1 = new Thread(tg1, x, "one");
		t1.start();
		Thread t2 = new Thread(tg1, y, "two");
		t2.start();
		Thread t3 = new Thread(tg1, z, "three");
		t3.start();
		System.out.println("Thread Group Name: " + tg1.getName());
		tg1.list();
	}
}
