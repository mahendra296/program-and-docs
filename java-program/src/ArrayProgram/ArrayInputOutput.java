package ArrayProgram;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mahendra
 */
import java.util.*;

public class ArrayInputOutput
{
	public static void main(String[] args)
	{
		int[] arr = new int[5];
		Scanner sc = new Scanner(System.in);

		System.out.println("Enter 5 Number : ");
		for (int i = 0; i < 5; i++)
		{
			arr[i] = sc.nextInt();

		}
		for (int i = 0; i < 5; i++)
		{
			System.out.println("arr[" + i + "] = " + arr[i]);

		}

	}
}
