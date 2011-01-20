package utils.buttons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.event.EventListenerList;
import javax.swing.JPanel;

import utils.TR;

/**
 *
 * @author zeroos
 */
public class UndoButton extends utils.MyButton{
	final int X_MARGIN = 3;
	final int Y_MARGIN = 4;
	private boolean enabled = true;
	
	public UndoButton(ActionListener l){
		this();
		this.addActionListener(l);
	}
	public UndoButton(){
		super("U");
		calcSize();
		setToolTipText(TR.t("Undo"));
	}
	public void paint(Graphics g){
		g.clearRect(0,0,getWidth(),getHeight());
		int xOffset = 0;
		int yOffset = 0;

		if(mousePressed && enabled){
			xOffset = yOffset = 1;
		}
		if(!enabled || !mouseOver){
			g.setColor(bgColor.brighter());
		}else{
			g.setColor(bgColor);
		}

		g.fillPolygon(	new int[]{getWidth()-X_MARGIN+xOffset, X_MARGIN + xOffset, getWidth()-X_MARGIN+xOffset},
				new int[]{Y_MARGIN + yOffset,getHeight()/2  + yOffset, getHeight()-Y_MARGIN+ yOffset},
				3);
		//set antialias on
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if(enabled){
			g.setColor(fontColor);
		}else{
			g.setColor(bgColor.darker());
		}
		g.drawPolygon(	new int[]{getWidth()-X_MARGIN+xOffset, X_MARGIN + xOffset, getWidth()-X_MARGIN+xOffset},
				new int[]{Y_MARGIN + yOffset,getHeight()/2  + yOffset, getHeight()-Y_MARGIN+ yOffset},
				3);

		//set antialias off
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*		g.setColor(fontColor);
			g.drawString(str, padding+1, fieldH-((fieldH-fontSize)/2)+1);
		else 
			g.drawString(str, padding, fieldH-((fieldH-fontSize)/2));
*/

	}
	private void calcSize(){
		setPreferredSize(new Dimension(fieldH*3/4, fieldH));
		setSize(getPreferredSize());
	}
	public void setEnabled(boolean d){
		if(d == enabled) return;
		enabled = d;
		repaint();
	}
	public boolean getEnabled(){
		return enabled;
	}
}
