package File;

import java.io.*;

public class FileRead2
{
	public static void main(String[] args) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader("./src/File/myfile.txt"));
		/*String s;
		while((s=br.readLine())!=null){
		System.out.println(s);
		}
		br.close();
		
		*/
		char[] s = new char[20];
		br.read(s, 0, 15);
		System.out.println(s);
		br.close();
	}
}
