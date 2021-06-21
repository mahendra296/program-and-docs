package mahendra;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Student
 */
public class series
{

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		// TODO code application logic here
		int row, col;
		for (row = 1; row <= 5; row++)
		{
			for (col = 1; col <= row; col++)
			{
				System.out.print(row);
			}
			System.out.println("");
		}
	}

}
