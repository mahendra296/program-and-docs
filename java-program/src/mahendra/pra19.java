
package mahendra;

import mahendra.pra19_1;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class pra19
{

	public static void main(String[] args)
	{
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Enter number :");
			pra19_1 p = new pra19_1();
			String ss = br.readLine();
			StringBuffer s = p.rs(ss);
			System.out.println("Reverse Digit" + s);
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}

}
