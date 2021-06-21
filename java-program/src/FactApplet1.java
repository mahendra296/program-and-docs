import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;

public class FactApplet1 extends Applet
{
	Label		l1, l2;
	TextField	t1;
	Button		b1;

	public void init()
	{
		l1 = new Label("Enter Number = > ");
		l2 = new Label();
		t1 = new TextField();
		//  t2 = new TextField();
		b1 = new Button("Factorial Calculate");
		setLayout(null);
		l1.setBounds(30, 50, 100, 20);
		l2.setBounds(30, 100, 100, 20);
		t1.setBounds(150, 50, 100, 20);
		// t2.setBounds(150,100,100,20);
		b1.setBounds(150, 150, 200, 20);

		add(l1);
		add(t1);
		add(l2);//add(t2);
		add(b1);
		b1.addActionListener(new Factorial());
	}
	public class Factorial implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			int n, s = 1;
			n = Integer.parseInt(t1.getText());
			while (n > 0)
			{
				s = s * n;
				n--;
			}

			l2.setText("Factorial is : " + s);

		}

	}
}
