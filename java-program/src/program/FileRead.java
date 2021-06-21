package program;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileRead
{
	public static void main(String[] args)
	{
		File file = new File("Mahi.txt");
		try
		{
			file.createNewFile();
			FileInputStream fin = new FileInputStream(file);
			int i = 0;
			while ((i = fin.read()) != -1)
			{
				System.out.print((char) i);
			}

		}
		catch (IOException ex)
		{
			Logger.getLogger(FileWrite.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
}
