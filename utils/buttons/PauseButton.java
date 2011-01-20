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
public class PauseButton extends utils.MyButton{
	final int BLOCK_WIDTH = 4;
	final int GAP = 2;
	final int BLOCK_HEIGHT = fieldH*3/4;

	public PauseButton(ActionListener l){
		this();
		this.addActionListener(l);
	}
	public PauseButton(){
		super("P");
		calcSize();
		setToolTipText(TR.t("Pause"));
	}
	public void paint(Graphics g){
		g.clearRect(0,0,getWidth(),getHeight());
		int gWidth = BLOCK_WIDTH*2 + GAP;
		int xOffset = 0;
		int yOffset = 0;

		if(mouseOver){
			g.setColor(bgColor);
		}else{
			g.setColor(bgColor.brighter());
		}
		if(mousePressed){
			xOffset = yOffset = 1;
		}

		g.fillRect(	(getWidth()-gWidth)/2 + xOffset, 
				(getHeight()-BLOCK_HEIGHT)/2 + yOffset, 
				BLOCK_WIDTH, BLOCK_HEIGHT);
		g.fillRect(	getWidth() - (getWidth()-gWidth)/2 - BLOCK_WIDTH + xOffset,
				(getHeight()-BLOCK_HEIGHT)/2 + yOffset,
				BLOCK_WIDTH, BLOCK_HEIGHT);

		g.setColor(fontColor);
		g.drawRect(	(getWidth()-gWidth)/2 + xOffset,
				(getHeight()-BLOCK_HEIGHT)/2 + yOffset,
				BLOCK_WIDTH, BLOCK_HEIGHT);
		g.drawRect(	getWidth() - (getWidth()-gWidth)/2 - BLOCK_WIDTH + xOffset,
				(getHeight()-BLOCK_HEIGHT)/2 + yOffset,
				BLOCK_WIDTH, BLOCK_HEIGHT);

/*		g.setColor(fontColor);
			g.drawString(str, padding+1, fieldH-((fieldH-fontSize)/2)+1);
		else 
			g.drawString(str, padding, fieldH-((fieldH-fontSize)/2));
*/

	}
	private void calcSize(){
		setPreferredSize(new Dimension(fieldH, fieldH));
		setSize(getPreferredSize());
	}
}
