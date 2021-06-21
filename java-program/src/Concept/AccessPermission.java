package Concept;
class Point
{
	protected int x, y;

	public Point(int _x, int _y)
	{
		x = _x;
		y = _y;
	}
}
public class AccessPermission
{
	public static void main(String args[])
	{
		Point p = new Point(10, 20);
		System.out.println("x = " + p.x + ", y = " + p.y);
	}
}
