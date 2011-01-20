package griddler;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;
import utils.Utils;

/**
 *
 * @author zeroos
 */

class Field implements Icon{
	private int type;
	private Color color1;
	private Color color2;

	public static int EMPTY = 0;
	public static int SOLID = 1;
	public static int TRIANGLE_L = 2;
	public static int TRIANGLE_R = 3;

	public static Field TRANSPARENT_FIELD = new Field(EMPTY, new Color(230,230,230), new Color(200, 200, 200));

	Field(int type){
		this.type = type;
		this.color1 = new Color(0xcc,0xcc,0xcc);
		this.color2 = color1.darker();
	}
	Field(int type, Color color){
		this.type = type;
		this.color1 = color;
		this.color2 = color;
	}
	Field(int type, Color color1, Color color2){
		this.type = type;
		this.color1 = color1;
		this.color2 = color2;
	}
	public void setColor1(Color c){
		color1 = c;
	}
	public void setColor2(Color c){
		color2 = c;
	}
	public int getType(){
		return type;
	}
	public Color getColor1(){
		return color1;
	}
	public Color getColor2(){
		return color2;
	}
	public Color getAvgColor(){
		if(type == SOLID) return getColor1();
		return new Color(
			(getColor1().getRed()  +getColor2().getRed())/2,
			(getColor1().getGreen()+getColor2().getGreen())/2,
			(getColor1().getBlue() +getColor2().getBlue())/2
		 );

	}
	public void paint(Graphics g, int x, int y, int w, int h){
		if(type == EMPTY){
			g.setColor(color1);
			g.fillRect(x,y,w,h);
			g.setColor(color2);
			g.fillOval(x+w*3/8,y+h*3/8,w/4,h/4);
		}else if(type == SOLID){
			g.setColor(color1);
			g.fillRect(x,y,w,h);
		}else if(type == TRIANGLE_L){
			g.setColor(color1);
			g.fillRect(x,y,w,h);
			g.setColor(color2);
			g.fillPolygon(new int[]{x,x+w,x+w}, new int[]{y,y+h,y}, 3);
		}else if(type == TRIANGLE_R){
			g.setColor(color1);
			g.fillRect(x,y,w,h);
			g.setColor(color2);
			g.fillPolygon(new int[]{x,x,x+w}, new int[]{y,y+h,y}, 3);
		}
	}



	public int getIconWidth(){
		return 20;
	}
	public int getIconHeight(){
		return 20;
	}
	public void paintIcon(Component c, Graphics g, int x, int y){
		paint(g, x, y, getIconWidth(), getIconHeight());
	}

	public static int typeToInt(String type){
		if(type.equals("EMPTY")) return EMPTY;
		if(type.equals("SOLID")) return SOLID;
		if(type.equals("TRIANGLE_L")) return TRIANGLE_L;
		if(type.equals("TRIANGLE_R")) return TRIANGLE_R;
		return -1;
	}
	public static String typeToString(int type){
		if(type == EMPTY) return "EMPTY";
		else if(type == SOLID) return  "SOLID";
		else if(type == TRIANGLE_L) return  "TRIANGLE_L";
		else if(type == TRIANGLE_R) return  "TRIANGLE_R";
		else return "UNDEFINED";
		
	}

	public String toString(){
		return toXML(0);
	//	return "Field {type: " + type + "; color1: " + color1 + "; color2: " + color2 + "}";

	}
	public String toXML(int id){
		return "<field id=\"" + id + "\"" +
			" type=\"" + typeToString(type) + "\"" +
			" color1=\"" + Utils.getHTMLColor(color1) + "\"" +
			" color2=\"" + Utils.getHTMLColor(color2) + "\"/>";
	}
}
