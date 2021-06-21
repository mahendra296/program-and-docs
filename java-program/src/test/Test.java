
package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

class Product
{
	private int		pid;
	private String	pname;
	private float	price;

	public int getPid()
	{
		return pid;
	}

	public void setPid(int pid)
	{
		this.pid = pid;
	}

	public String getPname()
	{
		return pname;
	}

	public void setPname(String pname)
	{
		this.pname = pname;
	}

	public float getPrice()
	{
		return price;
	}

	public void setPrice(float price)
	{
		this.price = price;
	}
	ArrayList<Product> l1 = new ArrayList();
	public void addItem() throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		Product p1 = new Product();

		System.out.println("Enter Product Id->");
		p1.setPid(Integer.parseInt(br.readLine()));

		System.out.println("Enter Product Name->");
		p1.setPname(br.readLine());

		System.out.println("Enter Product Price->");
		p1.setPrice(Float.parseFloat(br.readLine()));

		l1.add(p1);
	}

	public void viewProduct()
	{
		try
		{
			Product p2 = new Product();
			for (int i = 0; i <= l1.size(); i++)
			{
				p2 = l1.get(i);
				System.out.println("Product Id : " + p2.getPid());
				System.out.println("Product Name : " + p2.getPname());
				System.out.println("Product Price : " + p2.getPrice());
				System.out.println("***********************************************");
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}
	public void billProduct()
	{
		float bill = 0, pr;

		try
		{
			Product p2 = new Product();
			for (int i = 0; i <= l1.size(); i++)
			{
				p2 = l1.get(i);
				pr = p2.getPrice();
				bill = bill + pr;

			}

		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		System.out.println("Total Product Price : " + bill);
	}

}
public class Test
{

	public static void main(String[] args) throws IOException
	{

		int choice;
		Product p1 = new Product();
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
					p1.addItem();
					System.out.println("Added Product");
					break;

				case 2:
					p1.viewProduct();
					break;

				case 3:
					p1.billProduct();

					break;
				case 4:
					break;

				default:
					System.out.println("Invalid Choice...");
			}
		}
		while (choice != 4);
	}

}
