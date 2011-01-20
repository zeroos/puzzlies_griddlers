package griddler;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseMotionAdapter;
import java.lang.Math;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import utils.MyPreferences;
import utils.TR;




import javax.swing.colorchooser.AbstractColorChooserPanel;
/**
 *
 * @author zeroos
 */

public class ColorsManager extends JDialog{
	protected MyPreferences pref;
	protected GriddlerData data;


	//colors
	Color lineColor;
	Color line5Color;
	Color hlLineColor;
	Color bgColor;

	Color initialLineColor;
	Color initialLine5Color;
	Color initialHlLineColor;
	Color initialBgColor;

	JColorChooser colorChooser;

	JRadioButton lineColorRadio;
	JRadioButton line5ColorRadio;
	JRadioButton hlLineColorRadio;
	JRadioButton bgColorRadio;
	

	private static ColorsManager instance;

	public static ColorsManager getInstance(){
		return getInstance(null);
	}
	public static ColorsManager getInstance(Window owner){
		if(instance == null) instance = new ColorsManager(owner);
		instance.setLocationRelativeTo(owner);
		instance.setVisible(true);
		return instance;
	}
	//field is a field that will be edited or null if ou would like to 
	//create a new field
	private ColorsManager(Window owner){
		super(owner, TR.t("Colors editor"));
		pref = MyPreferences.getInstance();


		//set colors
		initialLineColor = new Color(pref.getInt("lineColor", new Color(0x66, 0x66, 0x66).getRGB()));
		initialLine5Color = new Color(pref.getInt("line5Color", new Color(0x00, 0x00, 0x00).getRGB()));
		initialHlLineColor = new Color(pref.getInt("hlLineColor", new Color(0xff, 0xff, 0x00).getRGB()));
		initialBgColor = new Color(pref.getInt("bgColor", new Color (0xcc,0xcc,0xcc, 0x88).getRGB()));

		//layout
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(layout);

		//colorChooser
		colorChooser = new JColorChooser();
		colorChooser.setPreviewPanel(new JPanel()); //should be changed
		colorChooser.getSelectionModel().addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				//color changed
				if(lineColorRadio.isSelected()){
					pref.setInt("lineColor", colorChooser.getColor().getRGB());
				}else if(line5ColorRadio.isSelected()){
					pref.setInt("line5Color", colorChooser.getColor().getRGB());
				}else if(hlLineColorRadio.isSelected()){
					pref.setInt("hlLineColor", colorChooser.getColor().getRGB());
				}else if(bgColorRadio.isSelected()){
					pref.setInt("bgColor", colorChooser.getColor().getRGB());
				}
			}
		});


		//buttons
		JButton okButton = new JButton(TR.t("ok"));
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				confirmAndQuit();
			}
		});

		JButton resetButton = new JButton(TR.t("reset"));
		resetButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				pref.setInt("lineColor", initialLineColor.getRGB());
				pref.setInt("line5Color", initialLine5Color.getRGB());
				pref.setInt("hlLineColor", initialHlLineColor.getRGB());
				pref.setInt("bgColor", initialBgColor.getRGB());
			}
		});

		//add everything to the layout
		
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.anchor = GridBagConstraints.CENTER;

		//colorChooser
		layout.setConstraints(colorChooser, c);
		add(colorChooser);
		//color1 and color2
		ButtonGroup buttonGroup = new ButtonGroup();
		lineColorRadio = new JRadioButton(TR.t("Lines color"));
		line5ColorRadio = new JRadioButton(TR.t("Bolded lines color"));
		hlLineColorRadio = new JRadioButton(TR.t("Highlighted lines color"));
		bgColorRadio = new JRadioButton(TR.t("Background color"));
		lineColorRadio.setSelected(true);
		c.gridwidth = 1;
		layout.setConstraints(lineColorRadio, c);
		layout.setConstraints(line5ColorRadio, c);
		layout.setConstraints(hlLineColorRadio, c);
		c.gridwidth = GridBagConstraints.REMAINDER;
		layout.setConstraints(bgColorRadio, c);
		add(lineColorRadio);
		add(line5ColorRadio);
		add(hlLineColorRadio);
		add(bgColorRadio);
		buttonGroup.add(lineColorRadio);
		buttonGroup.add(line5ColorRadio);
		buttonGroup.add(hlLineColorRadio);
		buttonGroup.add(bgColorRadio);
		//buttons
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(okButton, c);
		add(okButton);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		c.gridwidth = GridBagConstraints.REMAINDER;
		layout.setConstraints(resetButton, c);
		add(resetButton);

//		setMinimumSize(new Dimension(100, 100));
		pack();
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
	}

	public void confirmAndQuit(){
		setVisible(false);
	}
}
