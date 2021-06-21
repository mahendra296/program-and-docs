
package mahendra;

import java.applet.Applet;
import java.awt.Button;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;

public class pra20 extends Applet implements ActionListener
{
	TextField	tf;
	TextArea	ta;
	Button		b;
	public void init()
	{
		tf = new TextField(20);
		ta = new TextArea(5, 30);
		b = new Button("SHOW");
		b.addActionListener(this);
		add(tf);
		add(ta);
		add(b);

	}
	public void actionPerformed(ActionEvent ae)
	{
		try
		{
			FileReader fr = new FileReader("D:/" + tf.getText());
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
}
