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

public class Field implements Icon{
	private int type;
	public Color color1;
	public Color color2;

	public static int EMPTY = 0;
	public static int SOLID = 1;
	public static int TRIANGLE_L = 2;
	public static int TRIANGLE_R = 3;

	public static Field TRANSPARENT_FIELD = new Field(EMPTY, new Color(230,230,230), new Color(200, 200, 200));

	public Field(int type){
		this.type = type;
		this.color1 = new Color(0xcc,0xcc,0xcc);
		this.color2 = color1.darker();
	}
	public Field(int type, Color color){
		this.type = type;
		this.color1 = color;
		this.color2 = color;
		differentiateColors();
	}
	public Field(int type, Color color1, Color color2){
		this.type = type;
		this.color1 = color1;
		this.color2 = color2;
		differentiateColors();
	}
	public void setColor1(Color c){
		color1 = c;
		differentiateColors();
	}
	public void setColor2(Color c){
		color2 = c;
		differentiateColors();
	}
	public void differentiateColors(){
		if(color2.equals(color1)){
			if(color2.equals(Color.BLACK)) color2 = new Color(80,80,80);
			else color2 = color1.brighter();
		}
		if(color2.equals(color1)) color2 = color1.darker();
	}
	public int getType(){
		return type;
	}
	public Color getColor1(){
		return color1;
	}
	public Color getColor2(){
		if(type==SOLID) return color1;
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
    public int hashCode(){
        return (color1.hashCode()+color2.hashCode())/2+type;
    }
    public boolean equals(Object obj){
        if(!(obj instanceof Field)) return false;
        Field f2 = (Field)obj;
        if(this.getColor1().equals(f2.getColor1()) &&
            this.getColor2().equals(f2.getColor2()) &&
            this.getType() == f2.getType()) return true;
        return false;

    }

	public String toString(){
		return toXML(0);
	//	return "Field {type: " + type + "; color1: " + color1 + "; color2: " + color2 + "}";

	}
	public String toXML(int id){
		String ret = "<field id=\"" + id + "\"" +
			" type=\"" + typeToString(type) + "\"" +
			" color1=\"" + Utils.getHTMLColor(color1) + "\"";
		if(type!=SOLID) ret += " color2=\"" + Utils.getHTMLColor(color2) + "\"";
		ret += "/>";
		return ret;
	}
}
