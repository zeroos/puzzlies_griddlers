
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.KeyboardFocusManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Collections;
import javax.swing.AbstractAction;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.SwingUtilities;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import utils.*;


/**
 *
 * @author zeroos
 **/
public class Main{
	/**
	 * @param args the command line arguments
	 */
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if(args.length > 0){
					createAndShowGUI(args[0]);
				}else{
					createAndShowGUI();
				}
			}
		});
	}

	private static File file;
	private static griddler.GriddlerBoard board;
	private static griddler.GriddlerSolver solver;
	private static JFrame f;

	private static void save(Component parent){
		if(file == null){
			saveAs(parent);
			return;
		}
		String data = board.getData().toXML();
		try{
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			osw.write(data, 0, data.length());
			osw.close();
		}catch(Exception e){
			//FileNotFound, UnsupportedEncoding
			JOptionPane.showMessageDialog(parent, TR.t("An error occured and the file could not be saved."), 
					TR.t("Error"), JOptionPane.ERROR_MESSAGE);
			System.err.println(e.getMessage());
		}
	}
	private static void saveAs(Component parent){
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(TR.t("Griddlers"), "grd", "grid", "xml");
		chooser.setFileFilter(filter);
		if(chooser.showSaveDialog(parent) !=JFileChooser.APPROVE_OPTION) return;
		File newFile = chooser.getSelectedFile();
		if(!filter.accept(newFile)) newFile = new File(newFile.getPath() + ".grd");
		file = newFile;
		save(parent);
	}

	private static void createAndShowGUI() {
		createAndShowGUI(null);
	}
	private static void createAndShowGUI(String file) {
		f = new JFrame(TR.t("Griddler tester"));
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//	f.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
	//	f.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);


		if(file!=null){
			board = new griddler.GriddlerBoard(new griddler.GriddlerStaticData(file));
			board.setEditData(null);
		}
		else board = new griddler.GriddlerBoard();
		solver = new griddler.GriddlerSolver(board);

		f.setJMenuBar(getMenuBar());

		final MyButton editModeButton = new MyButton(TR.t("Edit mode"));
		editModeButton.addActionListener(new ActionListener(){
			boolean editMode = false;
			public void actionPerformed(ActionEvent e){
				editMode = !editMode;
				board.setEditMode(editMode);
				if(!editMode){
					editModeButton.setLabel(TR.t("Edit mode"));
				}else{
					editModeButton.setLabel(TR.t("Normal mode"));
				}
			}
		});


		f.setContentPane(new griddler.GriddlerPanel(board, new MyButton[]{editModeButton}));
		f.setSize(350, 300);
		f.setVisible(true);
	}
	private static JMenuBar getMenuBar(){
		//menu bar
		//todo: mnemonics i18n
		JMenuBar menu = new JMenuBar();
		JMenu menuFile = new JMenu(TR.t("File"));
		menuFile.setMnemonic(KeyEvent.VK_F);

		JMenuItem menuItemNew = new JMenuItem(new AbstractAction(TR.t("New")){
			public void actionPerformed(ActionEvent e){
				board.setData(new griddler.GriddlerStaticData());
				f.setSize(f.getWidth()+1, f.getHeight());
				solver.reinit();
			}
		});
		menuItemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		menuItemNew.setMnemonic(KeyEvent.VK_N);
		menuFile.add(menuItemNew);

		JMenuItem menuItemOpen = new JMenuItem(new AbstractAction(TR.t("Open")){
			public void actionPerformed(ActionEvent e){
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(TR.t("Griddlers"), "grd", "grid", "xml");
				chooser.setFileFilter(filter);
				if(chooser.showOpenDialog(f) !=JFileChooser.APPROVE_OPTION) return;
				File newFile = chooser.getSelectedFile();
				board.setData(new griddler.GriddlerStaticData(newFile.toURI().toString()));

				file = newFile;

				f.setSize(f.getWidth()+1, f.getHeight());
				solver.reinit();
			}
		});
		menuItemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		menuItemOpen.setMnemonic(KeyEvent.VK_O);
		menuFile.add(menuItemOpen);

		JMenuItem menuItemSave = new JMenuItem(new AbstractAction(TR.t("Save")){
			public void actionPerformed(ActionEvent e){
				save(f);
			}
		});
		menuItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		menuItemSave.setMnemonic(KeyEvent.VK_S);
		menuFile.add(menuItemSave);

		JMenuItem menuItemSaveAs = new JMenuItem(new AbstractAction(TR.t("Save as...")){
			public void actionPerformed(ActionEvent e){
				saveAs(f);
			}
		});
		menuItemSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		menuItemSaveAs.setMnemonic(KeyEvent.VK_A);
		menuFile.add(menuItemSaveAs);


		menuFile.addSeparator();

		JMenuItem menuItemExit = new JMenuItem(new AbstractAction(TR.t("Exit")){
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		menuItemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
		menuItemExit.setMnemonic(KeyEvent.VK_X);
		menuFile.add(menuItemExit);




		JMenu menuEdit = new JMenu(TR.t("Edit"));
		menuEdit.setMnemonic(KeyEvent.VK_E);

		JMenuItem menuItemCrop = new JMenuItem(new AbstractAction(TR.t("Crop board")){
			public void actionPerformed(ActionEvent e){
				board.getData().crop();
			}
		});
		menuItemCrop.setMnemonic(KeyEvent.VK_C);
		menuEdit.add(menuItemCrop);

		JMenuItem menuItemGenDesc =new JMenuItem(new AbstractAction(TR.t("Generate descriptions")){
			public void actionPerformed(ActionEvent e){
				board.getData().genDesc();
			}
		});
		menuItemGenDesc.setMnemonic(KeyEvent.VK_G);
		menuEdit.add(menuItemGenDesc);

		JMenuItem menuItemDelUnusedColors =new JMenuItem(new AbstractAction(TR.t("Delete unused fields")){
			public void actionPerformed(ActionEvent e){
				board.getData().delUnusedFields();
			}
		});
		menuItemDelUnusedColors.setMnemonic(KeyEvent.VK_D);
		menuEdit.add(menuItemDelUnusedColors);


		JMenuItem menuItemNormalize = new JMenuItem(new AbstractAction(TR.t("Normalize")){
			public void actionPerformed(ActionEvent e){
				board.getData().delUnusedFields();
				board.getData().crop();
				board.getData().genDesc();
			}
		});
		menuItemNormalize.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
		menuItemNormalize.setMnemonic(KeyEvent.VK_N);
		menuEdit.add(menuItemNormalize);

		menuEdit.addSeparator();

		final JMenuItem menuItemUndo = new JMenuItem();
		final JMenuItem menuItemRedo = new JMenuItem();
		menuItemUndo.setAction(new AbstractAction(TR.t("Undo")){
			public void actionPerformed(ActionEvent e){
				board.undo();
				menuItemRedo.setEnabled(board.canRedo());
				menuItemUndo.setEnabled(board.canUndo());
			}
		});
		menuItemUndo.setEnabled(board.canUndo());
		menuItemUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
		menuItemUndo.setMnemonic(KeyEvent.VK_U);
		menuEdit.add(menuItemUndo);

		menuItemRedo.setAction(new AbstractAction(TR.t("Redo")){
			public void actionPerformed(ActionEvent e){
				board.redo();
				menuItemUndo.setEnabled(board.canUndo());
				menuItemRedo.setEnabled(board.canRedo());
			}
		});
		menuItemRedo.setEnabled(board.canRedo());
		menuItemRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		menuItemRedo.setMnemonic(KeyEvent.VK_R);
		menuEdit.add(menuItemRedo);

		board.addUndoableEditListener(new UndoableEditListener(){
				public void undoableEditHappened(UndoableEditEvent e){
					menuItemUndo.setEnabled(board.canUndo());
					menuItemRedo.setEnabled(board.canRedo());
				}
		});

		menuEdit.addSeparator();

		JMenuItem menuItemSelectModeSingle = new JMenuItem(new AbstractAction(TR.t("Single select mode")){
			public void actionPerformed(ActionEvent e){
				board.setSelectMode(board.SINGLE);
			}
		});
		menuItemSelectModeSingle.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_DOWN_MASK));
		menuItemSelectModeSingle.setMnemonic(KeyEvent.VK_S);
		menuEdit.add(menuItemSelectModeSingle);

		JMenuItem menuItemSelectModeBlock = new JMenuItem(new AbstractAction(TR.t("Block select mode")){
			public void actionPerformed(ActionEvent e){
				board.setSelectMode(board.BLOCK);
			}
		});
		menuItemSelectModeBlock.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.CTRL_DOWN_MASK));
		menuItemSelectModeBlock.setMnemonic(KeyEvent.VK_B);
		menuEdit.add(menuItemSelectModeBlock);


		JMenu menuWindows = new JMenu(TR.t("Windows"));
		menuWindows.setMnemonic(KeyEvent.VK_W);

		JMenuItem menuItemFieldsManager = new JMenuItem(new AbstractAction(TR.t("Fields manager")){
			public void actionPerformed(ActionEvent e){
				griddler.FieldsManager.getInstance(f, board.getData());
			}
		});
		menuItemFieldsManager.setMnemonic(KeyEvent.VK_F);
		menuWindows.add(menuItemFieldsManager);

		JMenuItem menuItemColorsManager = new JMenuItem(new AbstractAction(TR.t("Colors manager")){
			public void actionPerformed(ActionEvent e){
				griddler.ColorsManager.getInstance(f);
			}
		});
		menuItemFieldsManager.setMnemonic(KeyEvent.VK_C);
		menuWindows.add(menuItemColorsManager);



		JMenu menuSolve = new JMenu(TR.t("Solve"));
		menuSolve.setMnemonic(KeyEvent.VK_S);

		JMenuItem menuItemNextStep = new JMenuItem(new AbstractAction(TR.t("Next step")){
			public void actionPerformed(ActionEvent e){
				System.out.println(solver.descNextStep());
			}
		});
		menuItemNextStep.setMnemonic(KeyEvent.VK_N);
		menuSolve.add(menuItemNextStep);

		JMenuItem menuItemIsSolvable = new JMenuItem(new AbstractAction(TR.t("Is solvable?")){
			public void actionPerformed(ActionEvent e){
				System.out.println(solver.isSolvable());
			}
		});
		menuItemIsSolvable.setMnemonic(KeyEvent.VK_S);
		menuSolve.add(menuItemIsSolvable);


		menu.add(menuFile);
		menu.add(menuEdit);
		menu.add(menuWindows);
		menu.add(menuSolve);

		return menu;
	}
}
