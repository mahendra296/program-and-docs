
public class Constr
{
	private final int	x;
	private static int	y;
	{
		x = 5;
		System.out.println(x + " " + y);
	}
	/*   private int l,b,h;
	   public constr(){
	l=10;b=8;h=4;
	System.out.println("l="+l+"b="+b+"c="+h);
	}
	   public constr(int l,int b,int h){
	   l=l;b=b;h=h;
	   System.out.println("l="+l+"b="+b+"c="+h);
	   }
	*/
	public static void main(String[] args)
	{
		Constr c1 = new Constr();
		Constr c2 = new Constr();
	}
}
