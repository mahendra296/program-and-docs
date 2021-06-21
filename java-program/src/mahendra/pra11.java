package mahendra;

import java.applet.Applet;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class pra11 extends Applet implements ActionListener
{
	TextField	tf1, tf2;
	TextArea	ta;

	public void init()
	{
		tf1 = new TextField("Not editable");
		tf1.setEditable(false);
		tf2 = new TextField(8);
		tf2.setEchoChar('*');

		ta = new TextArea(10, 8);

		add(tf1);
		add(tf2);
		add(ta);
		tf2.addActionListener(this);

	}

	public void actionPerformed(ActionEvent e)
	{
		ta.append(tf2.getText() + "\n");
	}

}
