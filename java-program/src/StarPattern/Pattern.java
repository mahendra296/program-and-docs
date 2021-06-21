package pattern;
class Pattern1
{
	int i, j;
	public void star1()
	{
		int c = 0;
		/*
		    *********
		    **** ****
		    ***   ***
		    **     **
		    *       *
		    **     **
		    ***   ***
		    **** ****
		    *********
		
		*/
		for (i = 1; i < 10; i++)
		{
			if (i <= 5)
			{
				c++;
			}
			else
			{
				c--;
			}

			for (j = 1; j < 10; j++)
			{
				if (j <= 6 - c || j >= 4 + c)
				{
					System.out.print("*");

				}
				else
				{
					System.out.print(" ");

				}
			}

			System.out.println();
		}
	}

	public void star2()
	{
		int c = 0;
		/*
		    *********
		    **** ****
		    ***   ***
		    **     **
		    *       *
		    **     **
		    ***   ***
		    **** ****
		    *********
		
		*/
		for (i = 1; i <= 9; i++)
		{
			if (i <= 5)
			{
				c++;
			}
			else
			{
				c--;
			}

			for (j = 1; j <= 9; j++)
			{
				if (j == 6 - c || j == 4 + c)
				{
					System.out.print("*");

				}
				else
				{
					System.out.print(" ");

				}
			}

			System.out.println();
		}
	}

}
public class Pattern
{
	public static void main(String[] args)
	{
		Pattern1 p = new Pattern1();
		p.star1();
		System.out.println("++++++++++++++++++++");
		System.out.println();
		p.star2();

	}

}
