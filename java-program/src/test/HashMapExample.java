
package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

class Student
{
	private int		sid;
	private String	sname;

	public int getSid()
	{
		return sid;
	}

	public void setSid(int sid)
	{
		this.sid = sid;
	}

	public String getSname()
	{
		return sname;
	}

	public void setSname(String sname)
	{
		this.sname = sname;
	}

	HashMap<Integer, String> hm = new HashMap<Integer, String>();
	public void addStudent() throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Enter Student Id->");
		int sid = Integer.parseInt(br.readLine());

		System.out.println("Enter Student Name->");
		String sname = br.readLine();

		hm.put(sid, sname);

	}

	public void viewStudent()
	{
		for (Map.Entry m : hm.entrySet())
		{
			System.out.println(m.getKey() + " " + m.getValue());
		}
	}

	public void deleteStudent() throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Enter Student Id->");
		int sid = Integer.parseInt(br.readLine());
		hm.remove(sid);
	}

}
public class HashMapExample
{
	public static void main(String[] args) throws IOException
	{

		Student s1 = new Student();

		int choice;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		do
		{
			System.out.println("\n\n--------Menu--------");
			System.out.println("1. Add Student");
			System.out.println("2. View Student");
			System.out.println("3. Delete Student");
			System.out.println("4. Exit");
			System.out.print("Enter Your Choice : ");
			choice = Integer.parseInt(br.readLine());

			switch (choice)
			{

				case 1:
					s1.addStudent();
					System.out.println("Added student");
					break;

				case 2:
					s1.viewStudent();
					break;

				case 3:
					s1.deleteStudent();
					s1.viewStudent();
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
