package program;

class OperatorExample
{
	public static void main(String args[])
	{
		int a = 10;
		int b = 10;
		int c = 15;
		int d = -12;
		boolean e = true;
		boolean f = false;
		System.out.println(a++ + ++a);//10+12=22  
		System.out.println(b++ + b++);//10+11=21  

		System.out.println(++a + ++a);//10+12=22  
		System.out.println(++b + b++);//10+11=21 

		System.out.println(~c);//-11 (minus of total positive value which starts from 0)  
		System.out.println(~d);//9 (positive of total minus, positive starts from 0)  
		System.out.println(!e);//false (opposite of boolean value)  
		System.out.println(!f);//true  

		System.out.println(10 << 2);//10*2^2=10*4=40  
		System.out.println(10 << 3);//10*2^3=10*8=80  
		System.out.println(20 << 2);//20*2^2=20*4=80  
		System.out.println(56 << 3);
		System.out.println(20 >>> 2);
	}

}
