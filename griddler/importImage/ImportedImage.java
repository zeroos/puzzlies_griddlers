package griddler.importImage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

import griddler.Field;
import java.awt.image.PixelGrabber;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author zeroos
 */
public class ImportedImage extends JPanel {

    BufferedImage orgImg = new BufferedImage(100, 100, BufferedImage.TYPE_INT_BGR);
    BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_BGR);
    HashMap<Field, Integer> fields = new HashMap<Field, Integer>();

    public ImportedImage() {
        super();
        Dimension d = new Dimension(100, 100);
        setSize(d);
        setMinimumSize(d);
        setPreferredSize(d);
        Graphics g = img.getGraphics();
        g.drawString("No image", 10, 10);
        Graphics gOrg = orgImg.getGraphics();
        gOrg.drawString("No image", 10, 10);
    }

    public void loadImage(File file) {
        try {
            img = ImageIO.read(file);
            ColorModel cm = img.getColorModel();
            boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
            WritableRaster raster = img.copyData(null);
            orgImg = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
            Dimension d = new Dimension(img.getWidth(), img.getHeight());
            setSize(d);
            setMinimumSize(d);
            setPreferredSize(d);
            calcStats();
            reduceColors(4);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error",
                    "Could not load image.",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void calcStats() {
        int w = img.getWidth(null);
        int h = img.getHeight(null);        
        int pix[] = new int[w * h];
        PixelGrabber grabber = new PixelGrabber(img, 0, 0, w, h, pix, 0, w);
        try {
            if (grabber.grabPixels() != true) {
                System.err.println("Grabber failed.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Field f;
        fields.clear();
        for(int i=0; i<pix.length; i++){
            f = new Field(Field.SOLID, new Color(pix[i]));
            if(!fields.containsKey(f)){
                fields.put(f, fields.size());
            }
        }
    }
    
    public void reduceColors(int limit){
        try {
            Graphics g = img.getGraphics();
            g.drawImage(Quantize.quantizeImage(orgImg, limit), 0, 0, null);
            calcStats();
            repaint();
        } catch (IOException ex) {
            Logger.getLogger(ImportedImage.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public ArrayList<Field> getFields() {
        //ArrayList<Field> fieldsArray = new ArrayList<Field>();
        Field[] fieldsArray = new Field[fields.size()];
        Set<Entry<Field, Integer> > entries = fields.entrySet();
        System.out.println(fields.size());
        //fieldsArray.ensureCapacity(fields.size());
        for(Entry<Field, Integer> e: entries){
            fieldsArray[e.getValue()] = e.getKey();
        }
        System.out.println(fieldsArray);
        ArrayList<Field> result = new ArrayList<Field>();
        result.addAll(Arrays.asList(fieldsArray));
        return result;
    }

    public int[][] getGrid() {
        int w = img.getWidth(null);
        int h = img.getHeight(null);        
        int pix[] = new int[w * h];
        PixelGrabber grabber = new PixelGrabber(img, 0, 0, w, h, pix, 0, w);
        
        try {
            if (grabber.grabPixels() != true) {
                System.err.println("Grabber returned false: " +
                                      grabber.status());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Field f;
        System.out.println(fields);
        int[][] grid = new int[img.getWidth()][img.getHeight()];
        for (int x = w; x-- > 0; ) {
            for (int y = h; y-- > 0; ) {
                f = new Field(Field.SOLID,
                        new Color(pix[y*w+x]));
                grid[x][y] = fields.get(f);
            }
        }
        
        /*WritableRaster raster = img.getRaster();
        int[][] grid = new int[img.getWidth()][img.getHeight()];
        int[] pixel = new int[4];
        Field f;
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                raster.getPixel(x, y, pixel);
                f = new Field(Field.SOLID,
                        new Color(pixel[0], pixel[1], pixel[2]));
                System.out.println(fields.get(f));
                grid[x][y] = fields.get(f);
            }
        }*/
        return grid;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, null);
    }
}
