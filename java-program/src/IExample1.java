
public class IExample1
{
	public static void main(String[] args)
	{
		Istudent1 s1 = new Istudent1();
		s1.setRollNo(100);
		s1.setName("Mahi");
		s1.setAge(22);

		System.out.println("Roll No : " + s1.getrollNo());
		System.out.println("Name : " + s1.getName());
		System.out.println("Age : " + s1.getAge());
	}
}
