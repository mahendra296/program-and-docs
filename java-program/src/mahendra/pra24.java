
package mahendra;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Label;
import java.awt.List;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class pra24 extends Applet implements ActionListener
{
	Label		l;
	TextField	tf;
	List		l1;
	Button		b1, b2, b3;

	public void init()
	{
		l = new Label("Enter Value");
		tf = new TextField(10);
		b1 = new Button("Add");
		b2 = new Button("Delete");
		b3 = new Button("Clear");
		l1 = new List(10);

		add(l);
		add(tf);
		add(b1);
		add(b2);
		add(b3);
		add(l1);
		b1.addActionListener(this);
		b2.addActionListener(this);
		b3.addActionListener(this);
	}
	public void actionPerformed(ActionEvent ae)
	{
		if (ae.getSource() == b1)
		{
			String s = tf.getText();
			l1.add(s);
		}
		if (ae.getSource() == b2)
		{
			String s;
			s = l1.getSelectedItem();
			l1.remove(s);
		}
		if (ae.getSource() == b3)
		{

			String s = tf.getText();
			tf.setText("");

		}
	}
}
