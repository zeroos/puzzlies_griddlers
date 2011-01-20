package utils;

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

/**
 *
 * @author zeroos
 */
public class MyButton extends JPanel{
	protected MyPreferences pref;
	protected Font font;
	protected FontMetrics fontMetrics;

	//colors
	protected Color fontColor;
	protected Color lineColor;
	protected Color bgColor;

	protected String str;
	protected int fieldH;
	protected int fontSize;
	protected int padding = 5;
	protected boolean mouseOver = false;
	protected boolean mousePressed = false;

        EventListenerList actionListenerList = new EventListenerList();

	public MyButton(){
		this("");
	}
	public MyButton(String txt){
		super();
		str = txt;
		pref = MyPreferences.getInstance();

		fieldH = pref.getInt("fieldH", 20);

		fontSize = 10;
		font = new Font(Font.SANS_SERIF, Font.PLAIN, fontSize);
		fontColor = new Color(pref.getInt("fontColor", (Color.BLACK).getRGB()));
		lineColor = new Color(pref.getInt("lineColor", new Color(0x33, 0x33, 0x33).getRGB()));
		bgColor = new Color(pref.getInt("bgColor", (new Color(0xcc,0xcc,0xcc)).getRGB()));

		calcSize();

		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				mousePressed = true;
				repaint();
			}
			public void mouseClicked(MouseEvent e){
				fireActionEvent();
			}
			public void mouseReleased(MouseEvent e){
				mousePressed = false;
				repaint();
			}
			public void mouseEntered(MouseEvent e){
				mouseOver = true;
				repaint();
			}
			public void mouseExited(MouseEvent e){
				mouseOver = false;
				mousePressed = false;
				repaint();
			}
		});
	}
	public void paint(Graphics g){
		g.setFont(font);

		fontMetrics = g.getFontMetrics();
		calcSize();
		int strWidth = fontMetrics.stringWidth(str);

		if(mouseOver)
			g.setColor(bgColor);
		else
			g.setColor(bgColor.brighter());
			
		g.fillRoundRect(0,0,strWidth+2*padding-1, fieldH-1, 5,5);
		g.setColor(lineColor);
		g.drawRoundRect(0,0,strWidth+2*padding-1, fieldH-1, 5,5);
		g.setColor(fontColor);
		if(mousePressed)
			g.drawString(str, padding+1, fieldH-((fieldH-fontSize)/2)+1);
		else 
			g.drawString(str, padding, fieldH-((fieldH-fontSize)/2));

	}
	private void calcSize(){
		if(fontMetrics != null){
			int strWidth = fontMetrics.stringWidth(str);
			setPreferredSize(new Dimension(strWidth+2*padding, fieldH));
		}else{
			setPreferredSize(new Dimension(fontSize*str.length(), fieldH));
		}
		setSize(getPreferredSize());
	}
	public void setLabel(String label){
		str = label;
		calcSize();
		revalidate();
	}
	public void addActionListener(ActionListener l){
		actionListenerList.add(ActionListener.class, l);
	}
	public void removeActionListener(ActionListener l){
		actionListenerList.remove(ActionListener.class, l);
	}
	public void fireActionEvent(){
		ActionListener listeners[] =
			actionListenerList.getListeners(ActionListener.class);
		for(ActionListener l: listeners){
			l.actionPerformed(new ActionEvent(this, 0, "pressed"));
		}
	}
}
