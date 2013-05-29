package griddler.importImage;

import griddler.GriddlerData;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import utils.MyPreferences;
import utils.TR;

/**
 *
 * @author zeroos
 */
public class ImportImage extends JDialog {

    protected MyPreferences pref;
    protected GriddlerData data;
    protected ImportedImage importedImage = new ImportedImage();

    public ImportImage(Window owner, GriddlerData d) {
        super(owner, TR.t("Import image"));

        pref = MyPreferences.getInstance();
        data = d;
        JPanel previewPanel = new JPanel();
        JPanel zoomPanel = new JPanel();
        JPanel reduceColorsPanel = new JPanel();
        JPanel buttonPanel = new JPanel();

        previewPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
        zoomPanel.setBorder(BorderFactory.createTitledBorder("Zoom image"));
        reduceColorsPanel.setBorder(BorderFactory.createTitledBorder("Reduce colors"));




        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(TR.t("Image"), "jpg", "jpeg", "png", "bmp", "gif");
        chooser.setFileFilter(filter);
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        importedImage.loadImage(file);



        //preview panel
        previewPanel.setLayout(new FlowLayout());
        previewPanel.add(importedImage);


        //reduceColors panel
        SpinnerModel reduceColorsSpinnerModel =
        new SpinnerNumberModel(4, //initial value
                               2, //min
                               10, //max
                               1);                //step
        final JSpinner reduceColorsSpinner = new JSpinner(reduceColorsSpinnerModel);
        reduceColorsPanel.add(reduceColorsSpinner);
        reduceColorsSpinner.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent ce) {
                SpinnerModel numberModel = reduceColorsSpinner.getModel();
                int limit = ((SpinnerNumberModel)numberModel).getNumber().intValue();
                importedImage.reduceColors(limit);
            }
        });


        //button panel
        buttonPanel.setLayout(new FlowLayout());
        JButton cancelButton = new JButton("Cancel");
        JButton importButton = new JButton("Import");

        cancelButton.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        importButton.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                apply();
                close();
            }
        });
        buttonPanel.add(cancelButton);
        buttonPanel.add(importButton);

//        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        BoxLayout layout = new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS);
        getContentPane().setLayout(layout);

        add(previewPanel);
        add(zoomPanel);
        add(reduceColorsPanel);
        add(buttonPanel);

        pack();
        setVisible(true);
    }

    public void apply() {
        data.setFields(importedImage.getFields());
        data.setGrid(importedImage.getGrid());
        try {
            System.out.println(data.getBoardDataString());
        } catch (Exception e) {
            System.out.println("Exception");
        }
    }

    public void close() {
        this.setVisible(false);
    }
}
