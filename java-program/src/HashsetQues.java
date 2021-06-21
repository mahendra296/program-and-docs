
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author MAHENDRA
 */
public class HashsetQues
{

	public static void main(String[] args)
	{

		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("1", "1");
		hm.put("2", "2");
		hm.put("3", "3");

		hm.clear();

		System.out.println(hm.size());
	}
}
