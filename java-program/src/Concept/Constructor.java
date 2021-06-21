package Concept;

class Test1
{
	Test1(int x)
	{
		System.out.println("Constructor called " + x);
	}
}

// This class contains an instance of Test1  
class Test2
{
	Test1 t1 = new Test1(10);

	Test2(int i)
	{
		t1 = new Test1(i);
	}

}
public class Constructor
{

	public static void main(String[] args)
	{
		Test2 t2 = new Test2(5);
	}
}

/*

First t2 object is instantiated in the main method. As the order of initialization of local variables comes first than the constructor,
first the instance variable (t1), in the class Test2 is allocated to the memory. In this line a new Test1 object is created, constructor 
is called in class Test1 and ‘Constructor called 10’ is printed. Next the constructor of Test2 is called and again a new object of the 
class Test1 is created and ‘Constructor called 5’ is printed.

*/
