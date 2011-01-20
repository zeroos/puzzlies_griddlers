package griddler;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.lang.Math;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.JPanel;
import utils.MyPreferences;

/**
 *
 * @author zeroos
 */

public class GriddlerPreview extends JPanel{
	private GriddlerBoard board;
	private GriddlerData data;
	private MyPreferences pref;

	//sizes
	int fieldW;
	int fieldH;
	int prefWidth;
	int prefHeight;
	int offsetX = 1;
	int offsetY = 1;

	//colors
	Color lineColor;


	public GriddlerPreview(GriddlerBoard board){
		this.board = board;
		this.data = board.getData();
		pref = MyPreferences.getInstance();

		lineColor = new Color(pref.getInt("lineColor", new Color(0x33, 0x33, 0x33).getRGB()));
		prefWidth = pref.getInt("fieldW", 20)*2;
		prefHeight = pref.getInt("fieldH", 20)*2;
		fieldW = getWidth()/data.getW();
		fieldH = getHeight()/data.getH();
		
		data.addGriddlerDataListener(new GriddlerDataListener(){
			public void fieldChanged(int x, int y){
				repaint();
				revalidate();
			}
			public void fieldsListChanged(){
				repaint();
			}
			public void descChanged(){
			}
		});

		setBorder(BorderFactory.createLineBorder(lineColor));
	}
	
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		this.data = board.getData();
		paintGrid(g);
//		paintLines(g);
	}
	protected void paintGrid(Graphics g){
		fieldW = (int)Math.floor(getWidth()/(double)data.getW());
		fieldH = (int)Math.floor(getHeight()/(double)data.getH());
		if(fieldW==0) fieldW = 1;
		if(fieldH==0) fieldH = 1;

		for(int y=0; y<data.getH(); y++){
			for(int x=0; x<data.getW(); x++){
				Field f;
				f = data.getField(x,y, false);
				f.paint(g,
					offsetX+x*fieldW,
					offsetY+y*fieldH,
					fieldW,
					fieldH
				);

			}
		}
	}

	protected void paintLines(Graphics g){
		//draw grid
		g.setColor(lineColor);
		for(int y=0; y<=data.getH(); y++){
			//horizontal lines
			g.drawLine(	0,
					y*fieldH,
					data.getW()*fieldW,
					y*fieldH);
		}
		for(int x=0; x<=data.getW(); x++){
			//vertical lines
			g.drawLine(	x*fieldW,
					0,
					x*fieldW,
					data.getH()*fieldH);
		}
	}
	public Dimension getPreferredSize(){
//		return new Dimension(data.getW()*prefFieldW,data.getH()*prefFieldH);
//		System.out.println(getWidth());
//		return new Dimension(fieldW*data.getW(), fieldH*data.getH());
		int fieldW = (int)Math.floor(prefWidth/(double)data.getW());
		int fieldH = (int)Math.floor(prefHeight/(double)data.getH());
		if(data.getW() <= prefWidth && data.getH() <= prefHeight)
			return new Dimension(data.getW()*fieldW+2, data.getH()*fieldH+2);
		else 
			return new Dimension(data.getW()+2, data.getH()+2);

	}
	public Dimension getMinimumSize(){
		return new Dimension(data.getW()+2, data.getH()+2);
	}

}
