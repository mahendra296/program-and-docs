package File;

import java.io.*;
import java.util.Scanner;

public class FileWrite1
{
	public static void main(String[] args) throws IOException
	{
		int i;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter The Message : ");
		String s = br.readLine();

		File myfile = new File("./src/File/myfile.txt");

		myfile.createNewFile();

		FileOutputStream fout = new FileOutputStream(myfile);
		OutputStreamWriter ow = new OutputStreamWriter(fout);

		ow.append(s);
		ow.close();
		fout.close();

	}
}
