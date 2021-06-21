package File;

import java.io.*;

public class FileRead1
{
	public static void main(String[] args) throws IOException
	{
		int i;
		File myfile = new File("./src/File/myfile.txt");
		myfile.createNewFile();

		FileInputStream fin = new FileInputStream(myfile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fin));

		String oDataraw = "";
		String OBuffer = "";

		while ((oDataraw = br.readLine()) != null)
		{
			OBuffer += oDataraw + "\n";
		}

		System.out.println(OBuffer);
		br.close();

		fin.close();

	}
}
