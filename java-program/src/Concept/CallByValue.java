/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Concept;

class Femo
{
	int data1 = 50;

	public void change1(int data1)
	{
		data1 = data1 + 100;
	}
}
public class CallByValue
{
	int data = 50;

	public void change(int data)
	{
		data = data + 100;
	}

	public static void main(String[] args)
	{
		CallByValue cbv = new CallByValue();
		Femo f = new Femo();
		System.out.println("Before Change : " + cbv.data);
		cbv.change(200);
		System.out.println("After Change : " + cbv.data);

		System.out.println("********Femo***");
		System.out.println("Before Change : " + f.data1);
		f.change1(200);
		System.out.println("After Change : " + f.data1);

	}

}
