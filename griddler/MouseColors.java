package griddler;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.lang.Math;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JPanel;
import utils.MyPreferences;

/**
 *
 * @author zeroos
 */

public class MouseColors extends JPanel implements ChangeListener{
	private MyPreferences pref;
	private GriddlerBoard board;
	//colors
	Color lineColor;
	//sizes
	int fieldW;
	int fieldH;

	public MouseColors(GriddlerBoard b){
		board = b;
		pref = MyPreferences.getInstance();
		lineColor = new Color(pref.getInt("lineColor", new Color(0x33, 0x33, 0x33).getRGB()));

		fieldW = pref.getInt("fieldW", 20);
		fieldH = pref.getInt("fieldH", 20);
		board.addChangeListener(this);
	}
	protected void paintComponent(Graphics g){
		//paint fields
		Field[] fs = new Field[3];
		fs[0] = board.getData().getField(board.getLeftColorVal());
		fs[1] = board.getData().getField(board.getMiddleColorVal());
		fs[2] = board.getData().getField(board.getRightColorVal());
		//ef.paint(g,0,0,fieldW,fieldH);
		for(int i=0; i< fs.length; i++){
			Field f = fs[i];
			f.paint(g,i*fieldW,0,fieldW,fieldH);

		}

		//paint grid
		g.setColor(lineColor);
		int totalWidth = 3*fieldW;
		for(int i=0; i< 5; i++){
			g.drawLine(i*fieldW, 0, i*fieldW, fieldH);
		}
		g.drawLine(0, 0, totalWidth, 0);
		g.drawLine(0, fieldH, totalWidth, fieldH);


	}
	public Dimension getPreferredSize(){
		return new Dimension(3*fieldW+1, fieldH+1);
	}
	public Dimension getMinimumSize(){
		return getPreferredSize();
	}

	public void stateChanged(ChangeEvent e){
		repaint();
	}

}
