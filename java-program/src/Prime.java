
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
public class Prime {
   public static void main(String[] args)throws IOException {
    int no,div;
    int temp=0;
    
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));  
    
    System.out.println("Enter Number ->");
    no=Integer.parseInt(br.readLine());
      for(int i=2;i<no;i++)
       {  
           if(no%i==0){
                temp=1;
                break;}
                         
       }     if(temp==0){ System.out.println("Number is Prime"); }else{System.out.println("Number is Not Prime"); } 
      
     
      System.out.println("Prime No : \n");
    
    for(no=1;no<=100;no++)
       {
       temp=0;
       for(div=2;div<=no-1;div++)
       if(no%div==0)
       temp=1;
       if(temp==0)
       System.out.println("Prme Number is" + no);
       }
    
} 
}

*/

public class Prime
{
	public static void main(String[] args)
	{
		int i = 0;
		int num = 0;
		//Empty String
		String primeNumbers = "";

		for (i = 1; i <= 100; i++)
		{
			int counter = 0;
			for (num = i; num >= 1; num--)
			{
				if (i % num == 0)
				{
					counter = counter + 1;
				}
			}
			if (counter == 2)
			{
				//Appended the Prime number to the String
				primeNumbers = primeNumbers + i + " ";
			}
		}
		System.out.println("Prime numbers from 1 to 100 are :");
		System.out.println(primeNumbers);
	}
}
