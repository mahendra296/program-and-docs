
package mahendra;

import java.applet.Applet;
import java.awt.Button;
import java.awt.TextField;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;

public class pra28 extends Applet implements ActionListener
{
	TextField		tf;
	Button			b1, b2;
	TextArea		ta;
	private boolean	a;

	public void init()
	{
		tf = new TextField(20);
		add(tf);
		b1 = new Button("Write");
		b2 = new Button("Read");
		ta = new TextArea(5, 10);
		add(ta);
		add(b1);
		add(b2);

		b1.addActionListener(this);
		b2.addActionListener(this);

	}

	public void actionPerformed(ActionEvent ae)
	{
		if (ae.getSource() == b2)
		{
			try
			{
				FileReader fr = new FileReader("e:/hello.txt");
				//FileInputStream fr=new FileInputStream("s:/hello.txt);
				int i;
				ta.setText("");
				while ((i = fr.read()) != -1)
				{
					ta.append("" + (char) i);
				}
				fr.close();

			}
			catch (Exception e)
			{
				System.out.println(e);
			}
		}
		if (ae.getSource() == b1)
		{
			try
			{
				FileWriter fw = new FileWriter("e:/hello.txt");
				//FileoutputStream fw=new FileoutputStream("d:/hello.txt);
				String s = tf.getText();
				//byte b[]=s.getBytes();
				fw.write(s);
				//fw.write(b);
				fw.close();

			}
			catch (Exception e)
			{
				System.out.println(e);
			}
		}

	}

}
