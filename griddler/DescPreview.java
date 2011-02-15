package griddler;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.lang.Math;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JPanel;
import utils.MyPreferences;

/**
 *
 * @author zeroos
 */

public class DescPreview extends JPanel implements ChangeListener{
	protected MyPreferences pref;
	protected GriddlerBoard board;

	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;

	protected int direction; 
	protected int hlCol=-2; //currently highlighted col
	protected int hlRow=-2; //currently highlighted row

	//colors
	Color lineColor;
	//sizes
	int fieldW;
	int fieldH;

	public DescPreview(GriddlerBoard b, int direction){
		setOpaque(false);
		board = b;
		this.direction = direction;
		pref = MyPreferences.getInstance();
		lineColor = new Color(pref.getInt("lineColor", new Color(0x33, 0x33, 0x33).getRGB()));

		fieldW = pref.getInt("fieldW", 20);
		fieldH = pref.getInt("fieldH", 20);
		board.getData().addGriddlerDataListener(new GriddlerDataListener(){
				public void fieldChanged(int x, int y){ }
				public void fieldsListChanged(){ }
				public void descChanged(){
					repaint();
				}
				public void boardFinished(){ }
		});
		board.addHlChangeListener(this);
	}
	protected void paintComponent(Graphics g){
//		g.clearRect(0,0,(int)getPreferredSize().getWidth(), (int)getPreferredSize().getHeight());
		hlCol = board.getHlCol();
		hlRow = board.getHlRow();
		try{
			if(direction == HORIZONTAL){
				ArrayList<DescField> fs = board.getData().getDesc().getRow(board.getHlRow());
				for(int i=0; i< fs.size(); i++){
					DescField f = fs.get(i);
					f.paint(g, board.getData().getField(f.value), i*fieldW,0,fieldW,fieldH);
				}
				//paint grid
				g.setColor(lineColor);
				int nOfFields = board.getData().getDesc().getRow(board.getHlRow()).size();
				for(int i=0; i< nOfFields+1; i++){
					g.drawLine(i*fieldW, 0, i*fieldW, fieldH);
				}
				g.drawLine(0, 0, nOfFields*fieldW, 0);
				g.drawLine(0, fieldH, nOfFields*fieldW, fieldH);
		
			}else if(direction == VERTICAL){
				ArrayList<DescField> fs = board.getData().getDesc().getCol(board.getHlCol());
				for(int i=0; i< fs.size(); i++){
					DescField f = fs.get(i);
					f.paint(g, board.getData().getField(f.value), 0, i*fieldH,fieldW,fieldH);
				}
				//paint grid
				g.setColor(lineColor);
				int nOfFields = board.getData().getDesc().getCol(board.getHlCol()).size();
				for(int i=0; i< nOfFields+1; i++){
					g.drawLine(0, i*fieldH, fieldW, i*fieldH);
				}
				g.drawLine(0, 0, 0, nOfFields*fieldH);
				g.drawLine(fieldW, 0, fieldW, nOfFields*fieldH);

			}
		}catch(IndexOutOfBoundsException e){
			//nothing highlighted
		}
	}
	public Dimension getPreferredSize(){
		Desc d = board.getData().getDesc();
		if(direction == HORIZONTAL){
			return new Dimension(d.getLongestRow()*fieldW+1, fieldH+1);
		}else{
			return new Dimension(fieldW+1, d.getLongestCol()*fieldH+1);
		}
	}
	public Dimension getMinimumSize(){
		return getPreferredSize();
	}


	public void stateChanged(ChangeEvent e){
		if(hlCol != board.getHlCol() && direction==VERTICAL) repaint();
		else if(hlRow != board.getHlRow() && direction==HORIZONTAL) repaint();
	}
}
