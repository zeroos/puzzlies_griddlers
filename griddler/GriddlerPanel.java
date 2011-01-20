package griddler;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import javax.swing.AbstractAction;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.SwingUtilities;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import utils.*;
import utils.buttons.*;


/**
 *
 * @author zeroos
 **/
public class GriddlerPanel extends JPanel{
	private File file;
	private final GriddlerBoard board;
	public GriddlerPanel(){
		this(new GriddlerBoard());
	}

	public GriddlerPanel(GriddlerBoard l_board){
		this(l_board, new MyButton[]{});
	}

	public GriddlerPanel(GriddlerBoard l_board, MyButton[] buttons){
		//buttons are displayed in the top-right corner
		this.board = l_board;
		JScrollPane scrolledBoard = new JScrollPane(board);

		setOpaque(false);
		//Panels
		griddler.Zoomer zoomer = new griddler.Zoomer(board);
		zoomer.setOpaque(false);
		griddler.FieldsList fieldsList = new griddler.FieldsList(board);
		griddler.MouseColors mouseColors = new griddler.MouseColors(board);
		griddler.DescPreview descPreviewH = new griddler.DescPreview(board, griddler.DescPreview.HORIZONTAL);
		griddler.DescPreview descPreviewV = new griddler.DescPreview(board, griddler.DescPreview.VERTICAL);
		griddler.GriddlerPreview griddlerPreview = new griddler.GriddlerPreview(board);

		final UndoButton undoButton = new UndoButton();
		final RedoButton redoButton = new RedoButton();

		undoButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				board.undo();
				undoButton.setEnabled(board.canUndo());
				redoButton.setEnabled(board.canRedo());
			}
		});
		
		redoButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				board.redo();
				redoButton.setEnabled(board.canRedo());
				undoButton.setEnabled(board.canUndo());
			}
		});

		board.addUndoableEditListener(new UndoableEditListener(){
			public void undoableEditHappened(UndoableEditEvent e){
				undoButton.setEnabled(board.canUndo());
				redoButton.setEnabled(board.canRedo());
			}
		});
		undoButton.setEnabled(board.canUndo());
		redoButton.setEnabled(board.canRedo());


		final SelectModeButton selectModeButton = new utils.buttons.SelectModeButton(board);
		selectModeButton.addActionListener(new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				board.nextSelectMode();
			}
		});

		//Layouts
		FlowLayout topButtonLayout = new FlowLayout(FlowLayout.RIGHT,2,0);
		FlowLayout bottomButtonLayout = new FlowLayout(FlowLayout.LEFT,2,0);
		GridBagLayout layout = new GridBagLayout();
		GridBagLayout topLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		//Containers for another panels
		JPanel topPanel = new JPanel();
		JPanel bottomPanel = new JPanel();
		JPanel topButtonPanel = new JPanel();
		JPanel bottomButtonPanel = new JPanel();
		topPanel.setOpaque(false);
		bottomPanel.setOpaque(false);
		topButtonPanel.setOpaque(false);
		bottomButtonPanel.setOpaque(false);



		//setting layout
		topPanel.setLayout(topLayout);
		bottomPanel.setLayout(new BorderLayout(5,0));
		setLayout(layout);

		for(MyButton b: buttons){
			topButtonPanel.add(b);
		}
		topButtonPanel.setLayout(topButtonLayout);

		bottomButtonPanel.setLayout(bottomButtonLayout);
		bottomButtonPanel.add(undoButton);
		bottomButtonPanel.add(redoButton);
		bottomButtonPanel.add(new utils.buttons.PauseButton(new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				board.togglePause();
			}
		}));
		bottomButtonPanel.add(new utils.buttons.ColorsButton(new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				ColorsManager.getInstance();
			}
		}));
		bottomButtonPanel.add(selectModeButton);


		c.insets = new Insets(1,1,1,1);//global padding


		//add GriddlerPreview to topPanel
		c.weightx = 0;
		c.weighty = 0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 2;
		c.gridheight = 2;
		topLayout.setConstraints(griddlerPreview, c);
		topPanel.add(griddlerPreview);

		c.gridheight = 1; //reset field height

		//add FieldsList to topPanel
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = GridBagConstraints.RELATIVE;
		topLayout.setConstraints(fieldsList, c);
		topPanel.add(fieldsList);

		//add fieldsManagerButton to topPanel
		c.weightx = 0;
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = GridBagConstraints.REMAINDER;//end row
		topLayout.setConstraints(topButtonPanel, c);
		topPanel.add(topButtonPanel);



		//add DescPreviewH to topPanel
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = GridBagConstraints.REMAINDER;//end row
		topLayout.setConstraints(descPreviewH, c);
		topPanel.add(descPreviewH);


		//add top panel
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.REMAINDER;//end row
		layout.setConstraints(topPanel, c);
		add(topPanel);


		//DescPreviewV
		c.weightx = 0.0;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.gridheight = 2;
		layout.setConstraints(descPreviewV, c);
		add(descPreviewV);

		c.gridheight = 1; //reset field height
		//Board
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;//end row
		layout.setConstraints(scrolledBoard, c);
		add(scrolledBoard);



		//add MouseColors to bottom panel
		bottomPanel.add(mouseColors, BorderLayout.WEST);

		//add bottomButtonPanel to bottom panel
		bottomPanel.add(bottomButtonPanel, BorderLayout.CENTER);

		//add Zoomer to bottom panel
		bottomPanel.add(zoomer, BorderLayout.EAST);



		//bottom panel
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridwidth = GridBagConstraints.REMAINDER;//end row
		c.anchor = GridBagConstraints.SOUTH;
		c.fill = GridBagConstraints.BOTH;
		layout.setConstraints(bottomPanel, c);
		add(bottomPanel);
	}
}
