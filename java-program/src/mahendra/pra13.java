
package mahendra;

import java.applet.Applet;
import java.awt.Choice;
import java.awt.List;
import java.awt.TextArea;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileReader;
import java.io.IOException;

public class pra13 extends Applet implements ItemListener
{
	List		l;
	Choice		c;
	TextArea	ta;

	public void init()
	{
		l = new List();
		c = new Choice();
		ta = new TextArea(5, 10);

		l.add("file1.txt");
		l.add("file2.txt");
		l.add("file3.txt");

		c.add("file1.txt");
		c.add("file2.txt");
		c.add("file3.txt");

		add(l);
		add(ta);
		add(c);
		c.select(0);
		c.addItemListener(this);
	}

	public void itemStateChanged(ItemEvent ie)
	{
		System.out.println("selected item :" + c.getSelectedItem());
		try
		{
			FileReader fr = new FileReader("D:/" + c.getSelectedItem());
			int i;
			ta.setText("");
			while ((i = fr.read()) != -1)
			{
				ta.append("" + (char) i);
			}

		}
		catch (IOException e)
		{
			System.out.println(e);
		}
	}
}
