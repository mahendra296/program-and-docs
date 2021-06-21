package program;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileWrite
{
	public static void main(String[] args)
	{
		File file = new File("Mahi.txt");
		try
		{
			file.createNewFile();
			FileOutputStream fout = new FileOutputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Enter The Message : ");
			String s = br.readLine();
			byte[] b = s.getBytes();
			fout.write(b);
			fout.close();
			System.out.println("Write Success");
		}
		catch (IOException ex)
		{
			Logger.getLogger(FileWrite.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
}
