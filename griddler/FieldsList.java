package griddler;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseMotionAdapter;
import java.lang.Math;
import javax.swing.JPanel;
import utils.MyPreferences;

/**
 *
 * @author zeroos
 */

public class FieldsList extends JPanel{
	private MyPreferences pref;
	private GriddlerBoard board;
	//colors
	Color lineColor;
	Color descColor;
	//sizes
	int fieldW;
	int fieldH;

	public FieldsList(GriddlerBoard b){
		board = b;
		pref = MyPreferences.getInstance();
		lineColor = new Color(pref.getInt("lineColor", new Color(0x33, 0x33, 0x33).getRGB()));
		descColor = new Color(pref.getInt("lineColor", new Color(0x00, 0x00, 0x00).getRGB()));

		fieldW = 20;
		fieldH = 20;

		b.getData().addGriddlerDataListener(new GriddlerDataListener(){
			public void fieldChanged(int x, int y){ }
			public void fieldsListChanged(){
				setSize(getPreferredSize());
				repaint();
			}
			public void descChanged(){ }
		});
		addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				//position of mouse in fields
				int x = (int)Math.floor((float)e.getX()/fieldW);
				x--;
				if(e.getButton() == e.BUTTON1)
					board.setLeftColor(x);
				else if(e.getButton() == e.BUTTON2)
					board.setMiddleColor(x);
				else if(e.getButton() == e.BUTTON3)
					board.setRightColor(x);

				if(e.getClickCount() == 2){
					FieldsManager.getInstance(board.getData(), x);
				}

			}
		});
	}
	protected void paintComponent(Graphics g){
		String shortcuts = pref.get("fields_shortcuts", "`1234567890qwertyuiopasdfghjkl");
		//paint fields
		Field ef = new Field(Field.EMPTY);
		Field[] fs = new Field[board.getData().getFields().length+1] ;
		fs[0] = ef;
		System.arraycopy(board.getData().getFields(), 0, fs, 1, board.getData().getFields().length);
		//ef.paint(g,0,0,fieldW,fieldH);
		for(int i=0; i< fs.length; i++){
			Field f = fs[i];
			f.paint(g,i*fieldW,0,fieldW,fieldH);


			//paint shortcuts
			if(i>=shortcuts.length()) continue;
			Color fieldColor = new Color(
					(f.getColor1().getRed()  +f.getColor2().getRed())/2,
					(f.getColor1().getGreen()+f.getColor2().getGreen())/2,
					(f.getColor1().getBlue() +f.getColor2().getBlue())/2
				);
			if(Color.RGBtoHSB(fieldColor.getRed(),
					fieldColor.getGreen(),
					fieldColor.getBlue(), null)[2] < 0.5){
				//dark background, bright color
				g.setColor(new Color(255-descColor.getRed(), 255-descColor.getGreen(), 255-descColor.getBlue()));
			}else{
				//bright background, dark color
				g.setColor(descColor);
			}
			String str = Character.toString(shortcuts.charAt(i));
	
			int fontSize = (int)(fieldH*0.5)+1;
			int strWidth;

			Font font;
			do{
				fontSize--;
				font = new Font(Font.SERIF, Font.PLAIN, fontSize);
			}while((strWidth = g.getFontMetrics().stringWidth(str)) > 0.9*fieldW);
	
			g.setFont(font);
			g.drawString(str, i*fieldW+fieldW/4-strWidth/2,
					fieldH/4 + fontSize/2);
		}

		//paint grid
		g.setColor(lineColor);
		int totalWidth = (board.getData().getFields().length+1)*fieldW;
		for(int i=0; i< board.getData().getFields().length+2; i++){
			g.drawLine(i*fieldW, 0, i*fieldW, fieldH);
		}
		g.drawLine(0, 0, totalWidth, 0);
		g.drawLine(0, fieldH, totalWidth, fieldH);


	}
	public Dimension getPreferredSize(){
		return new Dimension((board.getData().getFields().length+1)*fieldW+1, fieldH+1);
	}
	public Dimension getMinimumSize(){
		return getPreferredSize();
	}

}
