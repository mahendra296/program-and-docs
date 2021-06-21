package File;

import java.io.*;
import java.util.Scanner;

public class FileExample1
{
	public static void main(String[] args) throws IOException
	{
		int i;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter The Message : ");
		String s = br.readLine();

		File myfile = new File("./src/File/java.txt");

		myfile.createNewFile();

		FileOutputStream fout = new FileOutputStream(myfile, true);

		char ch[] = s.toCharArray();
		for (i = 0; i < s.length(); i++)
		{
			fout.write(ch[i]);
		}
		fout.close();

	}
}
