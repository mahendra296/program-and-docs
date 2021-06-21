package program;

class Te
{
	static int	a	= 10;

	int			b	= 20;

	static void m1()
	{
		a = 20 + a;
		System.out.println("from m1");
		System.out.println(a);
	}
}
public class Static_FinalKey
{
	public static void main(String[] args)
	{
		Te.m1();
		Te.m1();
		Te.m1();
	}
}
