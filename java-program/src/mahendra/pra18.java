
package mahendra;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class pra18
{

	public static void main(String[] args) throws IOException
	{
		try
		{
			Vector v = new Vector();
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String s = "";
			for (int i = 1; i <= 5; i++)
			{
				System.out.println("\n Enter String :" + i);
				s = br.readLine();
				v.addElement(s);
			}
			int f = 1;
			while (f == 1)
			{
				System.out.println("\n enter 1 for Delete an item");
				System.out.println("\n enter 2 for Specific location");
				System.out.println("\n enter 3 for Add at last position");
				System.out.println("\n enter 4 for Show data");

				int n = Integer.parseInt(br.readLine());
				if (f == 1)
				{
					System.out.println("\n Enter Item Text");
					v.remove(br.readLine());
				}
				if (f == 2)
				{
					System.out.println("\n Enter new Element");
					String p = br.readLine();
					System.out.println("\n Enter Position no");
					int q = Integer.parseInt(br.readLine());
					v.add(q, p);
				}
				if (f == 3)
				{
					System.out.println("\n Enter new Element");
					v.addElement(br.readLine());
				}
				if (f == 4)
				{
					System.out.println("\n Data :" + v);
				}
				System.out.println("\n If you want to continue press1 ");
				f = Integer.parseInt(br.readLine());
			}

		}
		catch (IOException e)
		{
			System.out.println(e);
		}
	}
}
