package pattern;

class star
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
		    *
		    **
		    ***
		    ****
		    *****     
		
		*/
		for (i = 1; i <= 5; i++)
		{
			for (j = 1; j <= i; j++)
			{
				System.out.print("*");
			}
			System.out.println();
		}
	}

	public void star3()
	{
		/*
		    *
		    **
		    ***
		    ****
		    *****     
		
		*/
		for (i = 1; i <= 5; i++)
		{
			for (j = 1; j <= 5; j++)
			{
				if (j <= i)
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

	public void star4()
	{
		/*
		    *****
		     ****
		      ***
		       **
		        *    
		
		*/
		for (i = 1; i <= 5; i++)
		{
			for (j = 1; j <= 5; j++)
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

	public void star5()
	{
		/*
		        *
		       **
		      ***
		     ****
		    *****   
		
		*/
		for (i = 1; i <= 5; i++)
		{
			for (j = 1; j <= 5; j++)
			{
				if (j >= 6 - i)
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

	public void star6()
	{
		/*
		    *****
		    **** 
		    ***  
		    **   
		    *       
		
		*/
		for (i = 1; i <= 5; i++)
		{
			for (j = 1; j <= 5; j++)
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

	public void star7()
	{
		/*
		    *   *
		     * *
		      *
		     * *
		    *   *      
		
		*/
		for (i = 1; i <= 5; i++)
		{
			for (j = 1; j <= 5; j++)
			{
				if (j == i || j == 6 - i)
					System.out.print("*");
				else
					System.out.print(" ");
			}
			System.out.println();
		}
	}

}
public class StarDigit
{
	public static void main(String[] args)
	{
		star s = new star();
		s.star1();
		System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||");
		s.star2();
		System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||");
		s.star3();
		System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||");
		s.star4();
		System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||");
		s.star5();
		System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||");
		s.star6();
		System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||");
		s.star7();
	}

}
