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

public class FieldsManager extends JDialog{
	protected MyPreferences pref;
	protected GriddlerData data;


	//sizes
	int fieldW;
	int fieldH;
	//colors
	Color color1;
	Color color2;

	//fields
	int editedFieldNum;
	Field[] fieldTypes;


	JList typesList;
	JList fieldsList = new JList();

	JColorChooser colorChooser;

	JRadioButton color1Radio;
	JRadioButton color2Radio;

	private static FieldsManager instance;

	public static FieldsManager getInstance(Window owner, GriddlerData d){
		return getInstance(owner, d, -1);
	}

	public static FieldsManager getInstance(GriddlerData d, int fieldNum){
		return getInstance(null, d, fieldNum);
	}
	public static FieldsManager getInstance(Window owner, GriddlerData d, int fieldNum){
		return getInstance(owner, d, fieldNum, Field.SOLID, d.getField(fieldNum).getColor1(), d.getField(fieldNum).getColor2());
	}
	public static FieldsManager getInstance(Window owner, GriddlerData d, int fieldNum, int type, Color color1, Color color2){
		if(instance == null) instance = new FieldsManager(owner, d, fieldNum, type, color1, color2);
		instance.fieldsList.setSelectedIndex(fieldNum);
		instance.setLocationRelativeTo(owner);
		instance.setVisible(true);
		return instance;
	}
	//field is a field that will be edited or null if ou would like to 
	//create a new field
	public FieldsManager(Window owner, GriddlerData d, int fieldNum, int type, Color color1, Color color2){
		super(owner, TR.t("Field editor"));

		this.color1 = color1;
		this.color2 = color2;

		this.editedFieldNum = fieldNum;

		pref = MyPreferences.getInstance();

		data = d;

		fieldW = pref.getInt("fieldW", 20);
		fieldH = pref.getInt("fieldH", 20);

		//layout
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(layout);

		//list fields
		updateFieldsList();
		//type
		fieldTypes = new Field[]{
			new Field(Field.SOLID, color1, color2),
			new Field(Field.TRIANGLE_L, color1, color2),
			new Field(Field.TRIANGLE_R, color1, color2),
		};
		typesList = new JList(fieldTypes);

		typesList.setLayoutOrientation(JList.VERTICAL_WRAP);
		typesList.setVisibleRowCount(-1);
		typesList.setSelectedIndex(0);


		//colorChooser
		colorChooser = new JColorChooser(getColor1());
		colorChooser.setPreviewPanel(new JPanel());
		colorChooser.getSelectionModel().addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				if(color2Radio.isSelected()){
					setColor2(colorChooser.getColor());
				}else{
					setColor1(colorChooser.getColor());
				}
			}
		});


		//buttons
		JButton cancelButton = new JButton(TR.t("cancel"));
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		});

		JButton okButton = new JButton(TR.t("ok"));
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				confirmAndQuit();
			}
		});

		JButton applyButton = new JButton(TR.t("apply"));
		applyButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				apply();
			}
		});

		//labels
		JLabel labelFields = new JLabel(TR.t("Edited field:"));
		JLabel labelType = new JLabel(TR.t("Type:"));
		JLabel labelColor1 = new JLabel(TR.t("Color1:"));
		JLabel labelColor2 = new JLabel(TR.t("Color2:"));

		//add everything to the layout
		
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;

		//fields
		c.anchor = GridBagConstraints.WEST;
		layout.setConstraints(labelFields, c);
		add(labelFields);
		layout.setConstraints(fieldsList, c);
		add(fieldsList);

		c.anchor = GridBagConstraints.EAST;
		c.gridwidth = GridBagConstraints.REMAINDER;
		//type
		JPanel typesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0,0));
		typesPanel.add(labelType);
		typesPanel.add(typesList);
		layout.setConstraints(typesPanel, c);
		add(typesPanel);
		c.anchor = GridBagConstraints.CENTER;
		//colorChooser
		layout.setConstraints(colorChooser, c);
		add(colorChooser);
		//color1 and color2
		ButtonGroup buttonGroup = new ButtonGroup();
		color1Radio = new JRadioButton(TR.t("First color"));
		color2Radio = new JRadioButton(TR.t("Second color"));
		color1Radio.setSelected(true);
		c.gridwidth = 1;
		layout.setConstraints(color1Radio, c);
		c.gridwidth = GridBagConstraints.REMAINDER;
		layout.setConstraints(color2Radio, c);
		add(color1Radio);
		add(color2Radio);
		buttonGroup.add(color1Radio);
		buttonGroup.add(color2Radio);
		//buttons
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints(okButton, c);
		add(okButton);
		layout.setConstraints(applyButton, c);
		add(applyButton);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		c.gridwidth = GridBagConstraints.REMAINDER;
		layout.setConstraints(cancelButton, c);
		add(cancelButton);

//		setMinimumSize(new Dimension(100, 100));
		pack();
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
	}

	private void updateFieldsList(){
		Object[] fields = new Object[data.getFields().length+1];
		System.arraycopy(data.getFields(), 0, fields, 0, data.getFields().length);
		fields[fields.length-1] = "+";
		fieldsList.setListData(fields);


		fieldsList.setLayoutOrientation(JList.VERTICAL_WRAP);
		fieldsList.setVisibleRowCount(-1);
		if(editedFieldNum < 0){
			fieldsList.setSelectedIndex(fields.length-1);
		}else{
			fieldsList.setSelectedIndex(editedFieldNum);
		}
		fieldsList.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				try{
					setColor1(((Field)fieldsList.getSelectedValue()).getColor1());
					setColor2(((Field)fieldsList.getSelectedValue()).getColor2());
					refresh();
				}catch(ClassCastException ex){
					//adding a new field, don't have to change field colors
				}catch(NullPointerException ex){
				}
			}
		});

	}
	public void apply(){
		Field f = (Field)typesList.getSelectedValue();
		Field newField = new Field(f.getType(), f.getColor1(), f.getColor2());
		try{
			data.setField(fieldsList.getSelectedIndex(), newField);
		}catch(IndexOutOfBoundsException e){
			data.addField(newField);
		}
		updateFieldsList();

	}
	public void confirmAndQuit(){
		apply();
		setVisible(false);
	}

	public void refresh(){
		for(Field f: fieldTypes){
			f.setColor1(color1);
			f.setColor2(color2);
		}
		repaint();
	}

	public void setColor1(Color newColor){
		color1 = newColor;
		refresh();
	}
	public void setColor2(Color newColor){
		color2 = newColor;
		refresh();
	}
	public Color getColor1(){
		return color1;
	}
	public Color getColor2(){
		return color2;
	}

}
