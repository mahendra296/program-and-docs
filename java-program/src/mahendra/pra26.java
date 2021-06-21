package mahendra;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class MyDlg extends Dialog implements ActionListener
{
	Label	l;
	Button	bExit;

	MyDlg(Frame f, int n)
	{
		super(f);
		setLayout(new FlowLayout());
		l = new Label("  ");
		l.setText("Sum Of Two Numbers " + n);
		bExit = new Button("Exit");
		bExit.addActionListener(this);
		add(l);
		add(bExit);
		setSize(300, 300);
		setVisible(true);
	}
	public void actionPerformed(ActionEvent ae)
	{
		dispose();
	}
}
class MyFrame26 extends Frame implements ActionListener
{
	MyDlg		md;
	TextField	tf1, tf2;
	Label		l1, l2, l3;
	Button		bsum;

	MyFrame26()
	{
		setLayout(new FlowLayout());
		tf1 = new TextField(20);
		tf2 = new TextField(20);
		l1 = new Label("Enter Number 1 : ");
		l2 = new Label("Enter Number 2 : ");
		bsum = new Button("Sum");

		add(l1);
		add(tf1);
		add(l2);
		add(tf2);
		add(bsum);

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent we)
			{
				dispose();
			}
		});
		setSize(300, 300);
		setVisible(true);
		bsum.addActionListener(this);
	}
	public void actionPerformed(ActionEvent ae)
	{
		int n1 = Integer.parseInt(tf1.getText());
		int n2 = Integer.parseInt(tf2.getText());
		int n3 = n1 + n2;
		md = new MyDlg(this, n3);
	}
}
public class pra26
{
	public static void main(String args[])
	{
		MyFrame26 mf = new MyFrame26();

	}

}
