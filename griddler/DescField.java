package griddler;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import utils.MyPreferences;

/**
 *
 * @author zeroos
 */

class DescField{
	public int length;
	public int value;
	public int state;

	private MyPreferences pref;

	public static int NORMAL=0;
	public static int INACTIVE=1;
//	public static int HIGHLIGHTED=2;
	public static int WARNING=2; //a red field indicating that there is an error in this solution
	
	DescField(int l, int v){
		this(l,v,NORMAL);
	}
	DescField(int l, int v, int s){
                pref = MyPreferences.getInstance();
		length = l;
		value = v;
		state = s;
	}
	
	int getValue(){
		return value;
	}
	int getLength(){
		return length;
	}
	int getState(){
		return state;
	}
	void setState(int state){
		this.state = state;
	}

	public void paint(Graphics g, Field f, int x, int y, int w, int h){
		//paints itself on g, needs Field to paint background
		Color descColor = new Color(pref.getInt("descColor", new Color(0x00, 0x00, 0x00).getRGB()));
		Color inactiveDescColor = new Color(pref.getInt("inactiveDescColor", new Color(0xaa, 0xaa, 0xaa).getRGB()));
		Color highlightedDescColorBright = new Color(pref.getInt("highlightedDescColor", new Color(0xff, 0x99, 0x00).getRGB()));
		Color highlightedDescColorDark = new Color(pref.getInt("highlightedDescColor", new Color(0xaa, 0x00, 0x00).getRGB()));
		
		f.paint(g, x, y, w, h);
		Color fieldColor = f.getAvgColor();

		if(state == WARNING){
			if(fieldColor.getRed() > 0xaa && fieldColor.getGreen() > 0x50) g.setColor(highlightedDescColorDark);
			else g.setColor(highlightedDescColorBright);
		}else{
			if(state == INACTIVE) descColor = inactiveDescColor;
			if(Color.RGBtoHSB(fieldColor.getRed(), 
					fieldColor.getGreen(), 
					fieldColor.getBlue(), null)[2] < 0.5){
			//dark background, bright color
				g.setColor(new Color(255-descColor.getRed(), 255-descColor.getGreen(), 255-descColor.getBlue()));
			}else{
				//bright background, dark color
				g.setColor(descColor);
			}
		}

		String str = Integer.toString(length);

		int fontSize = (int)(h*0.65)+1;
		int strWidth;

		Font font;
		do{
			fontSize--;
			font = new Font(Font.SERIF, state==WARNING?Font.BOLD:Font.PLAIN, fontSize);
		}while((strWidth = g.getFontMetrics().stringWidth(str)) > 0.9*w);

		g.setFont(font);
		g.drawString(str, x+w/2-strWidth/2, 
				y + h/2 + fontSize/2);

	}
	public static String stateToString(int state){
		if(state == NORMAL) return "NORMAL";
		if(state == INACTIVE) return "INACTIVE";
		if(state == WARNING) return "WARNING";
//		if(state == HIGHLIGHTED) return "HIGHLIGHTED";
		return "UNDEFINED";
	}
	public static int stateToInt(String state){
		if(state == "NORMAL") return NORMAL;
		if(state == "INACTIVE") return INACTIVE;
		if(state == "WARNING") return WARNING;
		return -1;
	}

	public String toXML(){
		return "<desc_field length=\"" + length + "\"" +
			" value=\"" + value + 
			"\"/>\n";
	}
	public String toString(){
		return(toXML());
	}
}


