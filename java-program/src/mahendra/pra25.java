
package mahendra;

import java.applet.Applet;
import java.awt.Label;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class pra25 extends Applet implements MouseMotionListener
{
	Label	l, l1;
	int		c	= 0;

	public void init()
	{

		l = new Label(" (X Y) ");
		l1 = new Label("   Draged     ");
		add(l);
		add(l1);
		addMouseMotionListener(this);

	}
	public void mouseMoved(MouseEvent me)
	{
		l.setText("(" + me.getX() + "," + me.getX() + ")");
	}
	public void mouseDragged(MouseEvent me)
	{
		c++;
		l1.setText("You have Dragged" + c + "pixels");
	}

}
