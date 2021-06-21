package File;

import java.io.*;
import java.util.*;
import java.util.Scanner;

public class FileRead
{
	public static void main(String[] args) throws IOException
	{
		int i;
		File myfile = new File("./src/File/myfile.txt");
		myfile.createNewFile();

		FileInputStream fin = new FileInputStream(myfile);

		do
		{
			i = fin.read();
			if (i != -1)
			{
				System.out.print((char) i);
			}
		}
		while (i != -1);

		fin.close();

	}
}
