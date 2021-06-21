package program;

class matrix extends Thread
{

	public void run()
	{
		int a[][] = { { 1, 1, 1 }, { 2, 2, 2 }, { 3, 3, 3 } };
		int b[][] = { { 1, 1, 1 }, { 2, 2, 2 }, { 3, 3, 3 } };
		int c[][] = new int[3][3];
		for (int i = 0; i <= 2; i++)
		{
			for (int j = 0; j <= 2; j++)
			{
				c[i][j] = a[i][j] * b[i][j];
				System.out.println("Thread A : " + c[i][j]);
			}
		}

	}
}
class matrix1 extends Thread
{

	public void run()
	{
		int a[][] = { { 1, 2, 3 }, { 2, 3, 4 }, { 3, 4, 5 } };
		int b[][] = { { 1, 1, 1 }, { 2, 2, 2 }, { 3, 3, 3 } };
		int c[][] = new int[3][3];
		for (int i = 0; i <= 2; i++)
		{
			for (int j = 0; j <= 2; j++)
			{
				c[i][j] = a[i][j] * b[i][j];
				System.out.println("Thread B : " + c[i][j]);
			}
		}

	}
}
public class ThreadMatrix
{
	public static void main(String[] args) throws InterruptedException
	{
		Thread t1 = new Thread(new matrix());
		Thread t2 = new Thread(new matrix1());
		t1.start();
		t1.sleep(3000);
		t2.setPriority(Thread.MAX_PRIORITY);
		t2.start();
	}
}
