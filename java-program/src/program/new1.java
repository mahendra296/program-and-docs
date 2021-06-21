package program;

class Student
{
	static String name;
	String toStrin()
	{
		return this.name;
	}
	String setStrin(String name)
	{
		this.name = name;
		return this.name;
	}
}
public class new1
{
	public static void main(String[] args)
	{

		Student s1 = new Student();
		Student s2 = new Student();

		s2.setStrin("Mahi");
		System.out.println(s2.toStrin());
		System.out.println(s1.toStrin());

	}

}
