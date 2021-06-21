package pattern;

public class stardigit_1
{
	public static void main(String[] args)
	{
		/*
		   // 0
		  //  01
		  //  012 
		  //  0123  
		   // 01234 
		
		for(int col=1;col<=5;col++){
		   for(int row=1;row<=col;row++){
		      System.out.print(row);
		    }
		   System.out.println();
		}   */

		//   0
		//  10
		//  010 
		//  1010  
		// 01010
		for (int col = 1; col <= 5; col++)
		{
			for (int row = 1; row <= col; row++)
			{
				if ((col + row) % 2 == 0)
				{
					System.out.print(0);
				}
				else
				{
					System.out.print(1);
				}
			}
			System.out.println();
		}
	}
}
