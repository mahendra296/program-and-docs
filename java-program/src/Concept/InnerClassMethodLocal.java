/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Concept;
/*
class Outer2 { 
    void outerMethod() { 
        System.out.println("inside outerMethod"); 
        // Inner class is local to outerMethod() 
        class Inner { 
            void innerMethod() { 
                System.out.println("inside innerMethod"); 
            } 
        } 
        Inner y = new Inner(); 
        y.innerMethod(); 
    } 
} 
*/
class Outer3
{
	void outerMethod()
	{
		int x = 98;
		System.out.println("inside outerMethod");
		class Inner
		{
			void innerMethod()
			{
				System.out.println("x= " + x);
			}
		}
		Inner y = new Inner();
		y.innerMethod();
	}
}
public class InnerClassMethodLocal
{
	public static void main(String[] args)
	{
		// Outer2 out = new Outer2(); 
		Outer3 out = new Outer3();
		out.outerMethod();
	}
}
