package program;

import java.io.*;

public class FileFunc
{
	public static void main(String[] args) throws IOException, NullPointerException
	{
		File file = new File("Mahi.txt");
		File f = new File("../test");
		file.createNewFile();
		String[] paths;

		System.out.println("File Exists : " + file.exists());
		System.out.println("File AbsoluteFile Path : " + file.getAbsoluteFile());
		System.out.println("File Hidden : " + file.isHidden());
		System.out.println("Is a File : " + file.isFile());
		System.out.println("Is a Directory : " + file.isDirectory());
		System.out.println("File Length : " + file.length());
		System.out.println("can Write : " + file.canWrite());
		System.out.println("can Read : " + file.canRead());
		System.out.println("compare : " + file.compareTo(f));
		System.out.println("File Name : " + file.getName());

		paths = f.list();
		for (String path : paths)
		{
			System.out.println("File list : " + path);
		}
	}
}
