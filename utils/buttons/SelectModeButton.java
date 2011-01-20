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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import griddler.GriddlerBoard;
import utils.TR;

/**
 *
 * @author zeroos
 */
public class SelectModeButton extends utils.MyButton{
	final int BLOCK_WIDTH = 4;
	final int GAP = 2;
	final int BLOCK_HEIGHT = fieldH*3/4;

	GriddlerBoard board;


	public SelectModeButton(GriddlerBoard b, ActionListener l){
		this(b);
		this.addActionListener(l);
	}
	public SelectModeButton(GriddlerBoard b){
		super("S");
		board = b;
		b.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
				repaint();
			}
		});
		calcSize();
		setToolTipText(TR.t("Change selection mode"));
	}
	public void paint(Graphics g){
		g.clearRect(0,0,getWidth(),getHeight());
		int xOffset = 0;
		int yOffset = 0;

		if(mousePressed){
			xOffset = yOffset = 1;
		}

		g.setColor(fontColor);

		if(board.getSelectMode() == GriddlerBoard.SINGLE){
			g.fillOval(	(int)(0.45*getWidth())+xOffset,
					(int)(0.45*getHeight())+yOffset,
					(int)(0.1*getWidth()),(int)(0.1*getHeight())
			);
		}else if(board.getSelectMode() == GriddlerBoard.BLOCK){
			//horizontal border lines
			for(int x=(int)(0.1*getWidth()) + xOffset; x<=1+(int)(0.8*getWidth())+xOffset; x+=3){
				g.drawLine(x,(int)(0.1*getHeight())+yOffset,x+1,(int)(0.1*getHeight())+yOffset);
				g.drawLine(x,(int)(0.9*getHeight())+yOffset,x+1,(int)(0.9*getHeight())+yOffset);
			}
			//vertical border lines
			for(int y=(int)(0.1*getHeight()) + yOffset; y<=1+(int)(0.8*getHeight())+yOffset; y+=3){
				g.drawLine((int)(0.1*getWidth())+xOffset,y,(int)(0.1*getWidth())+xOffset,y+1);
				g.drawLine((int)(0.9*getWidth())+xOffset,y,(int)(0.9*getWidth())+xOffset,y+1);
			}
		}else if(board.getSelectMode() == GriddlerBoard.LINE){
			//i assume that the button is a squere
			int startX = (int)(0.1*getWidth())+xOffset;
			int startY =(int)(0.1*getHeight())+yOffset;
			int endX = (int)(0.9*getWidth())+xOffset;
			int endY =(int)(0.9*getHeight())+yOffset;
			int i=0;
			g.drawLine(startX, startY, endX, endY);
			g.drawLine(startX+1, startY, endX+1, endY);
		}
	}
	private void calcSize(){
		setPreferredSize(new Dimension(fieldH, fieldH));
		setSize(getPreferredSize());
	}
}
