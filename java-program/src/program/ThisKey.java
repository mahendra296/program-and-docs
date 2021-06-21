package program;

class Emp
{
	int		empid;
	String	empName;
	int		empAge;
	public void get(int empid, String empName, int empAge)
	{
		this.empid = empid;
		this.empName = empName;
		this.empAge = empAge;

	}
	public void show()
	{
		System.out.println(empid + "\n" + empName + "\n" + empAge);

	}

}
public class ThisKey
{
	public static void main(String[] args)
	{
		Emp e1 = new Emp();
		e1.get(11, "Mahi", 23);
		e1.show();
	}
}
