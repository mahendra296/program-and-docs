/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ArrayProgram;

class Data<D>
{
	D i;
	public void add(D i1)
	{
		i = i1;
	}
	public D get()
	{
		return (i);
	}
}
public class GenricClass
{
	public static void main(String[] args)
	{
		Data<Integer> d = new Data<Integer>();
		Data<String> d1 = new Data<String>();
		Integer k = new Integer(5);
		String s = "Mahi";
		d.add(k);
		d1.add(s);

		System.out.println("Integer : " + d.get());
		System.out.println("String : " + d1.get());

	}

}
