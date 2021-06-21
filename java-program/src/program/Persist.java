package program;

import java.io.*;

class Persist
{
	public static void main(String args[]) throws FileNotFoundException, IOException
	{
		Stude s1 = new Stude(211, "Mahi");
		File f = new File("f.txt");
		f.createNewFile();
		FileOutputStream fout = new FileOutputStream(f);
		ObjectOutputStream out = new ObjectOutputStream(fout);

		out.writeObject(s1);
		out.flush();

		System.out.println("success");
	}
}
