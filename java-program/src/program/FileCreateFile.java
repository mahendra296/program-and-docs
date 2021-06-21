package program;

import java.io.File;
import java.io.IOException;

public class FileCreateFile
{
	public static void main(String[] args) throws IOException
	{
		File file = new File("Mahi.txt");

		if (file.exists())
		{
			System.out.println("File Is Exists...");
			System.out.println(file.getAbsolutePath());
		}
		else
		{
			file.createNewFile();
			System.out.println("File is Created succesfully");

		}
	}
}
