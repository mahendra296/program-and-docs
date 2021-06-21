
package mahendra;

import java.applet.Applet;
import java.awt.Button;
import java.awt.List;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class pra21 extends Applet implements ActionListener
{
	TextField	tf1;
	List		lst;
	Button		b;

	public void init()
	{
		tf1 = new TextField(15);
		lst = new List(10);
		b = new Button("Click Here");

		add(tf1);
		add(lst);
		add(b);
		b.addActionListener(this);
	}

	public void actionPerformed(ActionEvent ae)
	{
		lst.clear();
		int n, ans;
		n = Integer.parseInt(tf1.getText());
		for (int i = 1; i <= 10; i++)
		{
			ans = n * i;
			lst.add("" + n + "X" + i + "=" + ans);

		}
	}
}
