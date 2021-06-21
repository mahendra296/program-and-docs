package File;

import java.io.*;

public class FileExample
{
	public static void main(String[] srgs) throws IOException
	{
		File f1 = new File("./src/File/java.txt");
		f1.createNewFile();
		System.out.println("Is Exists : " + f1.exists());
		System.out.println("can Write : " + f1.canWrite());
		System.out.println("File Name : " + f1.getName());
		System.out.println("File Size : " + f1.length());
		//  f1.delete();
	}
}
