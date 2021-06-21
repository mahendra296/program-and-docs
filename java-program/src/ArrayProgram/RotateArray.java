/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ArrayProgram;

// Java program to rotate an array by  
// d elements  

class RotateArray1
{
	/*Function to left rotate arr[] of size n by d*/
	void leftRotate(int arr[], int d, int n)
	{
		for (int i = 0; i < d; i++)
			leftRotatebyOne(arr, n);
	}

	void leftRotatebyOne(int arr[], int n)
	{
		int i, temp;
		temp = arr[0];
		for (i = 0; i < n - 1; i++)
			arr[i] = arr[i + 1];
		arr[i] = temp;
	}

	/* utility function to print an array */
	void printArray(int arr[], int n)
	{
		for (int i = 0; i < n; i++)
			System.out.print(arr[i] + " ");
	}

	void rotateArrayRightToLeft(int arr[], int n, int dir)
	{
		int i, temp;
		while (dir != 0)
		{
			temp = arr[n - 1];
			for (i = n - 1; i >= 1; i--)
			{
				arr[i] = arr[i - 1];
			}
			arr[0] = temp;
			dir--;
		}

	}

	void rotateArrayLeftToRight(int arr[], int n, int dir)
	{
		int i, temp;
		while (dir != 0)
		{
			temp = arr[0];
			for (i = 0; i < n - 1; i++)
			{
				arr[i] = arr[i + 1];
			}
			arr[n - 1] = temp;
			dir--;
		}

	}

	// Driver program to test above functions 

}
public class RotateArray
{
	public static void main(String[] args)
	{
		RotateArray1 rotate = new RotateArray1();
		int arr[] = { 1, 2, 3, 4, 5, 6, 7 };
		rotate.rotateArrayLeftToRight(arr, arr.length, 2);
		rotate.printArray(arr, 7);

	}
}
