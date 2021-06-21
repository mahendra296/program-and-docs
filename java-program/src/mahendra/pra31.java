
package mahendra;

import java.applet.Applet;
import java.awt.Graphics;

public class pra31 extends Applet implements Runnable
{
	Thread	t;
	int		x, y;
	int		cc;

	public void init()
	{
		t = new Thread(this);
		t.start();
		cc = 0;
	}
	public void paint(Graphics g)
	{
		/*  x=(int)(Math.random()*500);
		y=(int)(Math.random()*500);
		g.fillOval(x,y,50,50);*/

		/* x=(int)(Math.random()*500);
		 y=(int)(Math.random()*500);
		 g.fillOval(x,y,75,75); */

		g.drawString("conter : " + cc, 150, 150);
		cc++;

	}

	public void run()
	{
		try
		{
			while (true)
			{
				repaint();
				Thread.sleep(300);
			}
		}
		catch (Exception e)
		{
			System.out.println();
		}
	}

}
