package program;

import java.io.File;

public class FileDelete
{
	public static void main(String[] args)
	{
		File file = new File("Mahi.txt");
		if (file.delete())
		{
			System.out.println("File is deleted");
		}
	}
}
