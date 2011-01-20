package utils.buttons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import javax.swing.event.EventListenerList;
import javax.swing.JPanel;

import utils.TR;

/**
 *
 * @author zeroos
 */
public class ColorsButton extends utils.MyButton{
	final int BLOCK_WIDTH = 4;
	final int GAP = 2;
	final int BLOCK_HEIGHT = fieldH*3/4;

	public ColorsButton(ActionListener l){
		this();
		this.addActionListener(l);
	}
	public ColorsButton(){
		super("C");
		calcSize();
		setToolTipText(TR.t("Change colors"));
	}
	public void paint(Graphics g){
		g.clearRect(0,0,getWidth(),getHeight());
		int xOffset = 0;
		int yOffset = 0;

		if(mousePressed){
			xOffset = yOffset = 1;
		}

		for(int i=0; i<(int)(0.8*getWidth()); i++){
			Color c = new Color(Color.HSBtoRGB((float)(i*1.0/(int)(0.8*getWidth())),1.0f, 1.0f));
			if(mouseOver){
				g.setColor(c.brighter());
			}else{
				g.setColor(c);
			}

			g.drawLine(	(int)(0.1*getWidth()) + i + xOffset,
					(int)(0.1*getHeight()) + yOffset,
					(int)(0.1*getWidth()) + i + xOffset,
					(int)(0.9*getHeight()) + yOffset);

		}

		g.setColor(fontColor);
		g.drawRect(	(int)(0.1*getWidth()) + xOffset, 
				(int)(0.1*getHeight()) + yOffset, 
				(int)(0.8*getWidth()), (int)(0.8*getHeight()));
	}
	private void calcSize(){
		setPreferredSize(new Dimension(fieldH, fieldH));
		setSize(getPreferredSize());
	}
}
