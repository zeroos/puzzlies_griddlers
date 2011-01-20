package griddler;

import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseMotionAdapter;
import java.lang.Math;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JPanel;
import javax.swing.JSlider;
import utils.MyPreferences;

/**
 *
 * @author zeroos
 */

public class Zoomer extends JPanel implements ChangeListener{
	private MyPreferences pref;
	private GriddlerBoard board;
	private JSlider slider;
	
	//colors
	Color lineColor;
	Color hlLineColor;

	//sizes
	int width;
	int height;
	int offsetX;
	int offsetY;

	public Zoomer(GriddlerBoard b){
		board = b;
		pref = MyPreferences.getInstance();
		
		setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

		slider = new JSlider();
		slider.setPreferredSize(new Dimension(80, 20));
		slider.setOpaque(false);
		this.setOpaque(false);
		add(slider);
		width=110;
		height=20;
		offsetX = 5;
		offsetY = 5;

		addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(e.getY() < height-offsetY && e.getY() > offsetY){
					if(e.getX() > offsetX && e.getX() < offsetX+height/2){
						//minus clicked
						if(slider.getValue() < 5){
							slider.setValue(0);
						}else{
							slider.setValue(slider.getValue()-5);
						}
					}else if(e.getX() > width-offsetX-height/2 && e.getX() < width-offsetX){
						//plus clicked
						if(slider.getValue() > 95){
							slider.setValue(100);
						}else{
							slider.setValue(slider.getValue()+5);
						}
					}
				}
			}
		});
		slider.addChangeListener(this);
	}
	public Dimension getPreferredSize(){
		return new Dimension(110, 20);
	}
	public Dimension getMinimumSize(){
		return getPreferredSize();
	}
	public void stateChanged(ChangeEvent e){
		JSlider s = (JSlider)e.getSource();
		int newSize = (int)Math.ceil(( (-Math.log(-s.getValue()+1+100)*11)/Math.log(2)+82 ));

		//(-log(-x+100)*11)/log(2)+82
		board.setFieldW(newSize);
		board.setFieldH(newSize);
	}
	protected void paintComponent(Graphics g){
		g.setColor(new Color(0x33,0x33,0x33));
		//minus
		g.drawLine(offsetX,height/2,height/2+offsetX,height/2);
		//plus
		g.drawLine(width-offsetX,height/2,width-offsetX-height/2,height/2);
		g.drawLine(width-offsetX-height/4,offsetY,width-offsetX-height/4,height-offsetY);
	}
}
