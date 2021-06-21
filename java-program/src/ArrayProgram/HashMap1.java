package ArrayProgram;

import java.util.HashMap;
import java.util.Map;

public class HashMap1
{

	public static void main(String[] args)
	{
		HashMap<Integer, String> hm = new HashMap<Integer, String>();
		hm.put(1, "Mahi");
		hm.put(2, "Raj ");
		hm.put(3, "Rahul");

		System.out.println("********** put() method **********");
		for (Map.Entry m : hm.entrySet())
		{
			System.out.println(m.getKey() + " " + m.getValue());
		}

		hm.putIfAbsent(4, "Jigar");
		System.out.println("********** putIfAbsent() method **********");
		for (Map.Entry m : hm.entrySet())
		{
			System.out.println(m.getKey() + " " + m.getValue());
		}
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put(5, "Rusel");
		map.putAll(hm);
		System.out.println("********** putAll() method *********");
		for (Map.Entry m : map.entrySet())
		{
			System.out.println(m.getKey() + " " + m.getValue());
		}
	}
}
