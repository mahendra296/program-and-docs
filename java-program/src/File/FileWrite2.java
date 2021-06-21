package File;

import java.io.*;

public class FileWrite2
{
	public static void main(String[] args) throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter Name :");
		String s = br.readLine();

		File myfile = new File("./src/File/myfile.txt");
		myfile.createNewFile();
		FileWriter fw = new FileWriter(myfile);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(s);
		bw.close();
	}
}
