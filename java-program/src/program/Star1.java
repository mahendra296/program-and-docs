package program;

//import java.util.Scanner;
class Patterns
{

	public void star()
	{
		/*     *
		       **
		       *** 
		       ****  
		       *****        
		*/
		for (int i = 1; i <= 5; i++)
		{
			for (int j = 1; j <= i; j++)
			{
				System.out.print("*");
			}
			System.out.println();
		}
	}

	public void rstar()
	{
		/*
		     *****
		     ****
		     *** 
		     **  
		     *                  
		*/
		for (int i = 1; i <= 5; i++)
		{
			for (int j = 1; j <= 5; j++)
			{
				if (j <= 6 - i)
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

	public void rpstar()
	{
		/*       *****
		         #****
		         ##*** 
		         ###**  
		         ####*                     
		*/
		for (int i = 1; i <= 5; i++)
		{
			for (int j = 1; j <= 5; j++)
			{
				if (j >= i)
				{
					System.out.print("*");
				}
				else
				{
					System.out.print("#");
				}
			}
			System.out.println();
		}

	}

	public void triangle()
	{
		/*        *
		         ***
		        ***** 
		       *******  
		*/
		for (int i = 1; i <= 5; i++)
		{
			for (int j = 1; j <= 9; j++)
			{
				if (j >= 6 - i && j <= 4 + i)
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

	public void rtriangle()
	{
		/*
		      *
		      * *
		      * * *
		      * *
		      *
		*/
		int k = 0;
		for (int i = 1; i <= 7; i++)
		{
			if (i <= 4)
			{
				k++;
			}
			else
			{
				k--;
			}
			for (int j = 1; j <= 4; j++)
			{
				if (j <= k)
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

		System.out.println("++++++++++++++++++++++++++++++++++++++++");

		for (int i = 1; i <= 5; i++)
		{
			for (int j = 1; j <= i; j++)
			{
				if (j <= 6 - i)
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

	public void starABCD()
	{
		/*
		      A
		      BA
		      CBA
		      DCBA
		*/
		char k;
		for (int i = 1; i <= 4; i++)
		{
			k = (char) (64 + i);
			for (int j = 1; j <= 4; j++)
			{
				if (j <= i)
				{
					System.out.print(k--);
				}
				else
				{
					System.out.print(" ");
				}
			}
			System.out.println();
		}

	}

	public void starDigit()
	{
		/*        
		            *
		           **
		          *** 
		           **
		            *
		*/

		for (int i = 5; i <= 1; i--)
		{

			for (int j = 3; j <= 1; j--)
			{
				if (j >= i)
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

public class Star1
{

	public static void main(String[] args)
	{
		// int i;
		//Scanner sc = new Scanner(System.in);
		// System.out.println("Enter iumn Number : ");
		//i =sc.nextInt();
		Patterns p = new Patterns();
		p.star();
		System.out.println("\n======================================\n");
		p.rstar();
		System.out.println("\n======================================\n");
		p.rpstar();
		System.out.println("\n======================================\n");
		p.triangle();
		System.out.println("\n======================================\n");
		p.rtriangle();
		System.out.println("\n======================================\n");
		p.starABCD();
		System.out.println("\n======================================\n");
		p.starDigit();

	}
}
