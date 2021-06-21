
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Area
{
	public static void main(String[] args) throws IOException
	{
		int r;
		double pi = 3.14, area = 0;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Enter Radius ->");
		r = Integer.parseInt(br.readLine());

		area = pi * r * r * r;
		System.out.println("Area of Circle is : " + area);
	}

}
