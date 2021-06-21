
package pattern;

class PrintStar
{
	int i, j;
	public void star1()
	{
		/*
		    *****
		   *****
		  *****
		 *****
		*****  
		*/
		for (i = 1; i <= 5; i++)
		{
			for (j = 1; j <= 5 - i; j++)
			{
				System.out.print(" ");
			}

			for (j = 1; j <= 5; j++)
			{
				System.out.print("*");
			}
			System.out.println();
		}
	}

	public void star2()
	{
		/*
		    *****
		   *   *
		  *   *
		 *   *
		*****  
		*/
		for (i = 1; i <= 5; i++)
		{
			for (j = 1; j <= 5 - i; j++)
			{
				System.out.print(" ");
			}

			for (j = 1; j <= 5; j++)
			{
				if (i == 1 || i == 5 || j == 1 || j == 5)
					System.out.print("*");
				else
					System.out.print(" ");
			}
			System.out.println();
		}
	}

}
public class Star1
{
	public static void main(String[] args)
	{
		PrintStar p1 = new PrintStar();
		p1.star2();

	}

}
