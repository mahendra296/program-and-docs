
package pattern;
class BlankPatarn
{
	int i, j;
	public void star1()
	{
		/*
		    *    
		    **   
		    * *  
		    *  * 
		    *****
		*/
		for (i = 1; i <= 5; i++)
		{

			for (j = 1; j <= 5; j++)
			{
				if (i == 5 || j == 1 || j == i)
					System.out.print("*");
				else
					System.out.print(" ");
			}
			System.out.println();
		}
	}

	public void star2()
	{
		/*
		        *
		       **
		      * *
		     *  *
		    *****
		
		*/
		for (i = 1; i <= 5; i++)
		{

			for (j = 5; j >= 1; j--)
			{
				if (i == 5 || j == 1 || j == i)
					System.out.print("*");
				else
					System.out.print(" ");
			}
			System.out.println();
		}
	}

	public void star3()
	{
		/*
		    *****
		    *  * 
		    * *  
		    **   
		    *    
		
		*/
		for (i = 5; i >= 1; i--)
		{

			for (j = 1; j <= 5; j++)
			{
				if (i == 5 || j == 1 || j == i)
					System.out.print("*");
				else
					System.out.print(" ");
			}
			System.out.println();
		}
	}

	public void star4()
	{
		/*
		    *****
		     *  *
		      * *
		       **
		        *
		
		*/
		for (i = 5; i >= 1; i--)
		{

			for (j = 5; j >= 1; j--)
			{
				if (i == 5 || j == 1 || j == i)
					System.out.print("*");
				else
					System.out.print(" ");
			}
			System.out.println();
		}
	}

	public void star5()
	{
		/*
		    *****
		     *  *
		      * *
		       **
		        *
		
		*/
		for (i = 1; i <= 5; i++)
		{

			for (j = 1; j <= 5; j++)
			{
				if (i == 5 || j == 4 - i || j == 2 + i)
					System.out.print("*");
				else
					System.out.print(" ");
			}
			System.out.println();
		}
	}

}

public class BlankPattern
{
	public static void main(String[] args)
	{
		BlankPatarn b = new BlankPatarn();
		/*    b.star1();
		System.out.println();
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
		System.out.println();
		
		b.star2();
		System.out.println();
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
		System.out.println();
		
		b.star3();
		System.out.println();
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
		System.out.println();
		
		b.star4();
		System.out.println();
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
		System.out.println();
		
		b.star5();
		System.out.println();
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
		System.out.println();
		*/

	}

}
