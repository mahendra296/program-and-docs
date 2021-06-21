package mahendra;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class pra14 extends Applet implements ActionListener
{
	Label		l1, l2, l3;
	TextField	tf1, tf2, tf3;
	Button		b1, b2;

	public void init()
	{

		l1 = new Label("Enter no1-->");
		l2 = new Label("Enter no2-->");
		l3 = new Label("Answer-->");

		tf1 = new TextField(8);
		tf2 = new TextField(8);
		tf3 = new TextField(8);

		b1 = new Button("add");
		b2 = new Button("sub");

		add(l1);
		add(tf1);
		add(l2);
		add(tf2);
		add(l3);
		add(tf3);

		add(b1);
		add(b2);
		b1.addActionListener(this);
		b2.addActionListener(this);
	}

	/**
	 *
	 * @param e
	 */
	public void actionPerformed(ActionEvent e)
	{
		int a, b, c;
		a = Integer.parseInt(tf1.getText());
		b = Integer.parseInt(tf2.getText());

		if (e.getSource() == b1)
		{
			c = a + b;
			tf3.setText("" + c);
		}
		if (e.getSource() == b2)
		{
			c = a - b;
			tf3.setText("" + c);
		}

	}

}
