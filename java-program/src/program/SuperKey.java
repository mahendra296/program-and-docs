package program;

class per
{
	int		id;
	String	name;
	int		age;

}
class stud1 extends per
{
	public void get(int id, String name, int age)
	{
		super.id = id;
		super.name = name;
		super.age = age;
	}
	public void show()
	{
		System.out.println(id + "\n" + name + "\n" + age);

	}
}

public class SuperKey
{
	public static void main(String[] args)
	{
		stud1 s = new stud1();
		s.get(1, "Mahi", 23);
		s.show();

	}
}
