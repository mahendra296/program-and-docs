package program;

class Person
{
	private int		age;
	private String	name;
	public void setAge(int a)
	{
		age = a;
	}
	public void setName(String n)
	{
		name = n;
	}
	public int getAge()
	{
		return (age);
	}
	public String getName()
	{
		return (name);
	}
}
class Stud001 extends Person
{
	private int rollNo;
	public void setRollNo(int r)
	{
		rollNo = r;
	}
	public int getrollNo()
	{
		return (rollNo);
	}
}
public class Info
{
	public static void main(String[] args)
	{
		Stud001 s1 = new Stud001();
		s1.setRollNo(18);
		s1.setName("Mahi");
		s1.setAge(23);

		System.out.println("Roll No : " + s1.getrollNo());
		System.out.println("Name : " + s1.getName());
		System.out.println("Age : " + s1.getAge());
	}
}
