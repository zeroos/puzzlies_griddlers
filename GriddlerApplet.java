
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.Frame;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import javax.swing.AbstractAction;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.SwingUtilities;
import javax.swing.JApplet;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import utils.*;

/**
 *
 * @author zeroos
 **/
public class GriddlerApplet extends JApplet{
	public static final String HOST = "http://tinybeast/";
	//public static final String HOST = "http://cdauth.eu:8090/";

	private File file;
	private griddler.GriddlerBoard board;
	private griddler.GriddlerSolver solver;
	private JFrame f;
	private Dimension initialSize;
	private boolean isPoppedOut = false;

	final MyButton popOutButton = new MyButton();
	ActionListener appletCloseListener;

	public void init() {
		try{
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					createAndShowGUI();
				}
			});
		}catch(Exception e){
			System.err.println("Error while initializing applet.");
		}
	}


	private void createAndShowGUI() {
		final JApplet applet = this;
		initialSize = getSize();
		try{
			board = new griddler.GriddlerBoard(
				new griddler.GriddlerStaticData(HOST + "griddlers/api/" + getParameter("id")),
				new AbstractAction(){
					public void actionPerformed(ActionEvent e){
						//griddler solved
	
						try{
							//init data
							String data = URLEncoder.encode("board_md5", "UTF-8") + "=" 
								+ URLEncoder.encode(board.getData().getBoardDataMD5(), "UTF-8");
							data += "&" + URLEncoder.encode("timetoken", "UTF-8") + "=" 
								+ URLEncoder.encode(getParameter("timetoken"), "UTF-8");
	
			
							//POST data
							URL url = new URL(HOST + "griddlers/api/solve/" + getParameter("id") + "/");
							URLConnection conn = url.openConnection();
							conn.setDoOutput(true);
							OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
							wr.write(data);
							wr.flush(); 
							// Get the response 
							BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
							String result = rd.readLine();
							System.out.println("Server msg: " + rd.readLine());
							wr.close();
							rd.close(); 
							String msg = TR.t("Unknown error occured.");
							if(result.equals("OK")){
								//reload page
								//get a string without query part
								URL redirectTo = new URL(applet.getDocumentBase().toString().substring(0,
											applet.getDocumentBase().toString().indexOf('?')));
								applet.getAppletContext().showDocument(redirectTo, "_self");
								return;
							}else if(result.equals("INCORRECT_SOLUTION")){
								msg = TR.t("Your solution is incorrect.");
							}else if(result.equals("NOT_AUTHENTICATED")){
								msg = TR.t("Congratulations! You have solved this puzzle.\n\n" + 
									"You are not logged in, so it won't be saved. Log in to track your progress!");
								JOptionPane.showMessageDialog(null, msg, "Griddler solved", JOptionPane.INFORMATION_MESSAGE); 
								return;
							}else if(result.equals("ERROR")){
								msg = TR.t("Sorry, server error. Try again in a while.");
							}
	
							JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE); 
						}catch(Exception ex){
							String msg = TR.t("An error occured while connecting to the server. Try again.");
							JOptionPane.showMessageDialog(null, msg, TR.t("Connection error"), JOptionPane.ERROR_MESSAGE); 
							System.err.println(msg);
						}
					}
				}
			);
			solver = new griddler.GriddlerSolver(board);
	
			updatePopOutButton();
	
			setContentPane(new griddler.GriddlerPanel(board, new MyButton[]{popOutButton}));
			setBackground(Color.WHITE);
	
	//		setJMenuBar(getMenuBar());
	
			popOutButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					if(isPoppedOut){
						appletCloseListener.actionPerformed(null);
					}
				}
			});
		}catch(java.security.AccessControlException e){
			add(new JLabel("This applet may be used only on '" + HOST + "'."));
			System.err.println("An error occured during applet initialization.");
		}catch(Exception e){
			add(new JLabel("An error occured during applet initialization."));
			System.err.println("An error occured during applet initialization.");
		}
	}
	private JMenuBar getMenuBar(){
		//menu bar
		//todo: mnemonics i18n
		JMenuBar menu = new JMenuBar(){
			public void paint(Graphics g){
				super.paint(g);
				if(!isPoppedOut) return;
				g.setColor(new Color(0xaa, 0xaa, 0xaa));
				g.fillRect(getWidth()-20, 5, 15, 2);
			}	
		};
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
				FileNameExtensionFilter filter = new FileNameExtensionFilter(TR.t("Griddlers"), "grd", "grid");
				chooser.setFileFilter(filter);
				if(chooser.showOpenDialog(f) !=JFileChooser.APPROVE_OPTION) return;
				File newFile = chooser.getSelectedFile();
				board.setData(new griddler.GriddlerStaticData(newFile.toURI().toString()));
				f.setSize(f.getWidth()+1, f.getHeight());
				solver.reinit();
			}
		});
		menuItemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		menuItemOpen.setMnemonic(KeyEvent.VK_O);
		menuFile.add(menuItemOpen);

		JMenuItem menuItemSave = new JMenuItem(new AbstractAction(TR.t("Save")){
			public void actionPerformed(ActionEvent e){
//				save(f);
			}
		});
		menuItemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		menuItemSave.setMnemonic(KeyEvent.VK_S);
		menuFile.add(menuItemSave);

		JMenuItem menuItemSaveAs = new JMenuItem(new AbstractAction(TR.t("Save as...")){
			public void actionPerformed(ActionEvent e){
//				saveAs(f);
			}
		});
		menuItemSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		menuItemSaveAs.setMnemonic(KeyEvent.VK_A);
		menuFile.add(menuItemSaveAs);


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

		JMenuItem menuItemNormalize = new JMenuItem(new AbstractAction(TR.t("Normalize")){
			public void actionPerformed(ActionEvent e){
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


		JMenuItem menuItemFieldsManager = new JMenuItem(new AbstractAction(TR.t("Fields manager")){
			public void actionPerformed(ActionEvent e){
				griddler.FieldsManager.getInstance(f, board.getData());
			}
		});
		menuItemFieldsManager.setMnemonic(KeyEvent.VK_F);
		menuEdit.add(menuItemFieldsManager);


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
		menu.add(menuSolve);

		return menu;
	}


	private void updatePopOutButton(){
		if(isPoppedOut)
			popOutButton.setLabel(TR.t("Pop in"));
		else
			popOutButton.setLabel(TR.t("Pop out"));
	}

	public boolean isAppletDragStart(MouseEvent e) {
		if(e.getSource().equals(popOutButton) && !isPoppedOut) return true;
		return false;
	}
	public void appletDragStarted(){
		isPoppedOut = true;
		updatePopOutButton();
		Container container = this.getParent();
		while(container != null) {
			if(container instanceof Frame) {
				Frame frame = (Frame)container;
				frame.setResizable(true);
				frame.setUndecorated(false);
				return;
			}
			container = container.getParent();
		}
	}
	public void setAppletCloseListener(ActionListener l){
		//do not display floating x button
		appletCloseListener = l;
	}
	public void appletRestored(){
		isPoppedOut = false;
		updatePopOutButton();
		this.setSize(initialSize);
	}
}
