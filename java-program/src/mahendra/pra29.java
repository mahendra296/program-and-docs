
package mahendra;

import java.io.IOException;
import java.io.RandomAccessFile;

public class pra29
{

	@SuppressWarnings("ConvertToTryWithResources")
	public static void main(String args[]) throws IOException
	{
		{
			try
			{
				RandomAccessFile r = new RandomAccessFile("d:/info.txt", "rw");
				r.writeBytes("Department of computer science");
				r.writeBytes("\nH.N.G.University, Patan");
				r.writeBytes("\nM.Sc.(CA&IT) Semester IV");
				r.close();
				RandomAccessFile rr = new RandomAccessFile("d:/info.txt", "r");

				rr.seek(0);
				int i;
				while ((i = rr.read()) != -1)
				{
					System.out.print("" + (char) i);
				}
				rr.close();
			}
			catch (Exception e)
			{
				System.out.println(e);
			}

		}

	}

}
