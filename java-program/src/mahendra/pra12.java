
package mahendra;

import java.applet.Applet;
import java.awt.Canvas;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextField;

public class pra12 extends Applet
{

	public void init()
	{
		GridLayout g = new GridLayout(4, 2, 20, 20);//set the components in rows and columns
		setLayout(g);
		Canvas c = new Canvas();
		c.setSize(10, 10);
		add(c);
		Label lblno = new Label("NO");
		TextField txtno = new TextField(5);
		Label lblname = new Label("NAME");
		TextField txtname = new TextField(5);
		Label lbladd = new Label("ADDRESS");
		TextField txtadd = new TextField(5);
		Label lblcity = new Label("CITY");
		TextField txtcity = new TextField(5);
		add(lblno);
		add(txtno);
		add(lblname);
		add(txtname);
		add(lbladd);
		add(txtadd);
		add(lblcity);
		add(txtcity);
	}
}
