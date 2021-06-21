
package mahendra;

import java.applet.Applet;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class pra16 extends Applet implements AdjustmentListener
{
	Scrollbar	red, green, blue;
	Label		l;

	public void init()
	{
		setLayout(new GridLayout(4, 1));

		red = new Scrollbar(0, 1, 10, 1, 255);
		green = new Scrollbar(0, 1, 10, 1, 255);
		blue = new Scrollbar(0, 1, 10, 1, 255);

		l = new Label("SAMPLE");
		add(red);
		add(green);
		add(blue);
		add(l);
		red.addAdjustmentListener(this);
		green.addAdjustmentListener(this);
		blue.addAdjustmentListener(this);

	}
	public void adjustmentValueChanged(AdjustmentEvent ae)
	{
		int r = red.getValue();
		int g = green.getValue();
		int b = blue.getValue();
		l.setBackground(new Color(r, g, b));
	}
}
