package program;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

class Product
{
	private int		pid;
	private String	pname;
	private float	price;

	public Product(int pid, String pname, float price)
	{
		this.pid = pid;
		this.pname = pname;
		this.price = price;
	}

}
public class tes
{

	public static void main(String[] args) throws IOException
	{

		int choice;
		tes ts = new tes();
		ArrayList<Product> l1 = new ArrayList();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		do
		{
			System.out.println("\n\n--------Menu--------");
			System.out.println("1. Add Product");
			System.out.println("2. List Product");
			System.out.println("3. Bill");
			System.out.println("4. Exit");
			System.out.print("Enter Your Choice : ");
			choice = Integer.parseInt(br.readLine());

			switch (choice)
			{

				case 1:
					System.out.println("1 Exit");
					ts.addItem();
					System.out.println("Added Product");
					break;

				case 2:
					System.out.println("2 Exit");
					break;

				case 3:
					System.out.println("3 Exit");
					break;
				case 4:
					System.out.println("4 Exit");
					break;

				default:
					System.out.println("Invalid Choice...");
			}
		}
		while (choice != 4);
	}
	public void addItem() throws IOException
	{
		ArrayList<Product> l1 = new ArrayList();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Enter Product Id->");
		int pid = Integer.parseInt(br.readLine());

		System.out.println("Enter Product Name->");
		String pname = br.readLine();

		System.out.println("Enter Product Price->");
		float price = Float.parseFloat(br.readLine());

		Product p1 = new Product(pid, pname, price);
		l1.add(p1);

	}

}
