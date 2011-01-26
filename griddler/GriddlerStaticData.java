package griddler;

import java.awt.Color;
import java.io.IOException;
import java.net.URI;
import java.math.*;
import java.security.*;
import java.util.ArrayList;
import javax.swing.event.EventListenerList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import utils.TR;
import utils.MyPreferences;
import utils.Utils;

/**
 *
 * @author zeroos
 */

public class GriddlerStaticData implements GriddlerData{
	public static double API = 0.1;
	private MyPreferences pref;
	int[][] grid;
	ArrayList<Field> fields;
	Desc desc;

	EventListenerList listenerList = new EventListenerList();

	public GriddlerStaticData(){
		init();
	}
	public GriddlerStaticData(String uri){
		init();
		fields = new ArrayList<Field>();
		grid = new int[][]{{-1}};
		try{
			System.out.print("Parsing griddler: " + (new URI(uri)).getPath() + "\t");
		}catch(Exception e){
			System.out.print("Parsing griddler\t\t");
		}
		try{
			SAXParser parser = (SAXParserFactory.newInstance()).newSAXParser();
			parser.parse(uri, new DefaultHandler(){
				private String curEl = null;
				private String curSubEl = null;
				private String curSubSubEl = null;
				private int data_col = 0;
				private int data_row = 0;
				private String value = "";
				public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
					if(curEl == null){
						if(qName == "griddler"){
							if(API < new Double(attributes.getValue("api"))){
								System.err.println("WARNING: API version not compatible.");
							}
						}else if(qName == "meta" || qName == "fields" || qName == "desc" || qName == "board"){
							curEl = qName;
						}
					}else if(curEl == "meta"){
						if(curSubEl == null){
							if(qName == "colors"){
								curSubEl = "colors";
							}
						}else if(curSubEl == "colors"){
							if(curSubSubEl == null){
								if(qName == "line" || qName == "line5" || qName == "hlLine" || qName == "bgColor"){
									curSubSubEl = qName;
									value = "";
								}
							}
						}
					}else if(curEl == "fields"){
						if(qName == "field"){
							Color color1=null, color2=null;
							int type=0;
							try{
								color1 = new Color(Integer.parseInt(attributes.getValue("color1").substring(1), 16));
							}catch(Exception e){
								fatalError(new SAXParseException("field attribute `color1` not set", null));
							}
							try{
								color2 = new Color(Integer.parseInt(attributes.getValue("color2").substring(1), 16));
							}catch(Exception e){
								color2 = null;
							}
							try{
								type = Field.typeToInt(attributes.getValue("type"));
							}catch(Exception e){
								fatalError(new SAXParseException("unknown field type: " + attributes.getValue("type"), null));
							}
							if(color2 != null){
								fields.add(new Field(type, color1, color2));
							}else{
								fields.add(new Field(type, color1));
							}
						}
					}else if(curEl == "desc"){
						if(curSubEl == null){
							if(qName == "row"){
							       curSubEl = "row";
							       desc.addRow(new ArrayList<DescField>());
							}else if(qName == "col"){
								curSubEl = "col";
								desc.addCol(new ArrayList<DescField>());
							}
						}else if((curSubEl == "row" || curSubEl == "col") && qName == "desc_field"){
							int length = 0, value = 0;
							try{
								length = Integer.parseInt(attributes.getValue("length"));
								value = Integer.parseInt(attributes.getValue("value"));
								if(length == 0 || value == 0){
									fatalError(new SAXParseException("incorrect desc_field length od value", null));
								}
							}catch(Exception e){
								fatalError(new SAXParseException("incorrect desc_field length od value", null));
							}
							if(curSubEl == "row"){
								desc.addDescFieldToLastRow(new DescField(length, value));
							}
							else if(curSubEl == "col") desc.addDescFieldToLastCol(new DescField(length, value));
						}
					}else if(curEl == "board"){
						if(curSubEl == null && qName == "data"){
							try{
								grid = new int[Integer.parseInt(attributes.getValue("width"))]
									[Integer.parseInt(attributes.getValue("height"))];
							}catch(Exception e){
								fatalError(new SAXParseException("incorrect board width or height", null));
							}
							curSubEl = qName;
							data_col = 0;
							data_row = 0;
						}
					}
				}
				public void endElement(String uri, String localName, String qName){
					if(curEl == qName){
						curEl = null;
					}else if(curEl == "desc"){
						if(qName == "row" || qName == "col"){
							curSubEl = null;
						}
					}else if(curEl == "board" && qName == "data"){
						curSubEl = null;
					}else if(curEl == "meta" && curSubEl == "colors"){
						if(qName == "colors"){
							curSubEl = null;
						}else if(curSubSubEl == "line" || curSubSubEl == "line5" || curSubSubEl == "hlLine" || curSubSubEl == "bgColor"){
							pref.setInt(curSubSubEl, Integer.parseInt(value.substring(1), 16));
							curSubSubEl = null;
						}
					}
				}
				public void characters(char[] chs, int start, int length) throws SAXException{
					if(curEl == "board" && curSubEl == "data"){
						try{
							for(int i=start; i<start+length; i++){
								char ch = chs[i];
								if(ch == '\n'){
									data_row++;
									data_col=0;
								}else{
									int val=-1;
									if((int)ch >= 'a' && (int)ch <= (int)'z'){
										//a-z
										val = (int)ch - (int)'a';
										val += 10;
										val += (int)'Z' - (int)'A';
									}else if((int)ch >= 'A' && (int)ch <= (int)'Z'){
										//A-Z
										val = (int)ch - (int)'A';
										val += 10;
									}else if((int)ch >= '0' && (int)ch <= (int)'9'){
										//0-9
										val = (int)ch - (int)'0';
									}else if(ch == '-'){
										val = -1;
									}else{
										fatalError(new SAXParseException("unknown field value: '" + ch + "'", null));
									}
									grid[data_col++][data_row] = val;
								}
							}
						}catch(Exception e){
							fatalError(new SAXParseException("error while parsing board data: " + e.getMessage(), null));
						}
					}else if(curEl == "meta" && curSubEl == "colors"){
						if(curSubSubEl == "line" || curSubSubEl == "line5" || curSubSubEl == "hlLine" || curSubSubEl == "bgColor"){
							for(int i=start; i<start+length; i++){
								value += chs[i];
							}
						}
					}

				}
			});
		}catch(SAXException e){
			System.err.println("SAX exception " + e.getMessage());
		}catch(ParserConfigurationException e){
			System.err.println("Conf exception");
		}catch(IOException e){
			System.err.println("IOException");
		}
		System.out.println("DONE");

		while(desc.getColsSize() > grid.length) addRightCol();
		while(desc.getRowsSize() > grid[0].length) addBottomRow();
		checkBoardFinished();
	}
	private void init(){
		pref = MyPreferences.getInstance();
		fields = new ArrayList<Field>();
		fields.add(new Field(Field.SOLID, new Color(0xff, 0xff, 0xff)));
		fields.add(new Field(Field.SOLID, new Color(0x00, 0x00, 0x00)));
		desc = new Desc();
		grid = new int[15][10];
		for(int x=0; x<grid.length; x++){
			for(int y=0; y<grid[0].length; y++){
				grid[x][y] = -1;
			}
		}
	}
	
	public boolean isInitialized(){
		return true;
	}

	public int getW(){
		return grid.length;
	}
	public int getH(){
		return grid[0].length;
	}

	public int[][] getGrid(){
		return grid;
	}
	public void setGrid(int[][] grid){
		this.grid = grid;
		fireFieldChanged(-1,-1);
		checkBoardFinished();
	}
	public int[] getRow(int n){
		int[] row = new int[getW()];
		for(int i=0; i< row.length; i++){
			row[i] = getFieldVal(i,n);
		}
		return row;
	}
	public void setRow(int n, int[] row){
		for(int i=0; i< row.length; i++){
			setFieldVal(row[i], i, n);
		}
	}
	public int[] getCol(int n){
		int[] col = new int[getH()];
		for(int i=0; i< col.length; i++){
			col[i] = getFieldVal(n,i);
		}
		return col;
	}
	public void setCol(int n, int[] col){
		for(int i=0; i< col.length; i++){
			setFieldVal(col[i], n, i);
		}
	}



	public int getFieldVal(int x, int y){
		return grid[x][y];
	}
	public void setFieldVal(int v, int x, int y){
		if(v==grid[x][y]) return;
		grid[x][y] = v;

//		checkRowFinished(y);
//		checkColFinished(x);
		checkBoardFinished();

		fireFieldChanged(x,y);
	}

	public Field getField(int n){
		return getField(n, true);
	}
	public Field getField(int n, boolean emptyFields){
		//if emptyFields == true will return Field.EMPTY when no such color
		//else will return getFild(0);
		try{
			return fields.get(n);
		}catch(IndexOutOfBoundsException e){
			if(emptyFields)
				return new Field(Field.EMPTY);
			else
				return getField(0);
		}
	}
	public Field getField(int x, int y){
		return getField(x, y, true);
	}
	public Field getField(int x, int y, boolean emptyFields){
		//returns field on (x,y)
		return getField(getFieldVal(x,y), emptyFields);
	}

	public Field[] getFields(){
		return fields.toArray(new Field[]{ });
	}

	public void delField(int i){
		//deletes field, updates board, sets -1 insted of this field val
		fields.remove(i);
		for(int x=0; x<getW(); x++){
			for(int y=0; y<getH(); y++){
				if(grid[x][y] > 0){
					if(grid[x][y] == i) grid[x][y] = -1;
					else if(grid[x][y]>i) grid[x][y]--;
				}
			}
		}


	}
	public void addField(Field f){
		fields.add(f);
		fireFieldsListChanged();
	}
	public void setField(int i, Field f){
		fields.set(i, f);
		fireFieldsListChanged();
	}
	public void addLeftCol(){
		addCol(0);
	}
	public void addRightCol(){
		addCol(grid.length);
	}
	public void addTopRow(){
		addRow(0);
	}
	public void addBottomRow(){
		addRow(grid[0].length);
	}
	public void addCol(int pos){
		//adds new column at given position
		int[][] newGrid = new int[grid.length+1][grid[0].length];
		for(int y=0; y<grid[0].length; y++){
			for(int x=0; x<grid.length+1; x++){
				if(x<pos){
					newGrid[x][y] = grid[x][y];
				}else if(x>pos){
					newGrid[x][y] = grid[x-1][y];
				}else{
					//for new fields
					newGrid[x][y] = -1;
				}
			}
		}
		grid=newGrid;
		fireFieldChanged(-1,-1);
	}
	public void addRow(int pos){
		//adds new row at a given position
		int[][] newGrid = new int[grid.length][grid[0].length+1];
		for(int y=0; y<grid[0].length+1; y++){
			for(int x=0; x<grid.length; x++){
				if(y<pos){
					newGrid[x][y] = grid[x][y];
				}else if(y>pos){
					newGrid[x][y] = grid[x][y-1];
				}else{
					//for new fields
					newGrid[x][y] = -1;
				}
			}
		}
		grid=newGrid;
		fireFieldChanged(-1,-1);
	}
	public void delRow(int pos){
		//deletes a row from a given position
		int[][] newGrid = new int[grid.length][grid[0].length-1];
		for(int y=0; y<grid[0].length-1; y++){
			for(int x=0; x<grid.length; x++){
				if(y<pos){
					newGrid[x][y] = grid[x][y];
				}else if(x>= pos){
					newGrid[x][y] = grid[x][y+1];
				}
			}
		}
		grid=newGrid;
		fireFieldChanged(-1,-1);
	}
	public void delCol(int pos){
		//deletes a column from a given position
		int[][] newGrid = new int[grid.length-1][grid[0].length];
		for(int y=0; y<grid[0].length; y++){
			for(int x=0; x<grid.length-1; x++){
				if(x<pos){
					newGrid[x][y] = grid[x][y];
				}else if(x>=pos){
					newGrid[x][y] = grid[x+1][y];
				}
			}
		}
		grid=newGrid;
		fireFieldChanged(-1,-1);
	}

	public Desc getDesc(){
		return desc;
	}


	public void addGriddlerDataListener(GriddlerDataListener l){
		listenerList.add(GriddlerDataListener.class, l);
	}
	public void removeGriddlerDataListener(GriddlerDataListener l){
		listenerList.remove(GriddlerDataListener.class, l);
	}
	public GriddlerDataListener[] getGriddlerDataListeners(){
		return listenerList.getListeners(GriddlerDataListener.class);
	}

	public void fireFieldsListChanged(){
		GriddlerDataListener listeners[] = 
			listenerList.getListeners(GriddlerDataListener.class);
		for(GriddlerDataListener l: listeners){
			l.fieldsListChanged();
		}
	}
	public void fireFieldChanged(int x, int y){
		GriddlerDataListener listeners[] = 
			listenerList.getListeners(GriddlerDataListener.class);
		for(GriddlerDataListener l: listeners){
			l.fieldChanged(x,y);
		}
	}
	public void fireDescChanged(){
		GriddlerDataListener listeners[] = 
			listenerList.getListeners(GriddlerDataListener.class);
		for(GriddlerDataListener l: listeners){
			l.descChanged();
		}

	}
	public void fireBoardFinished(){
		GriddlerDataListener listeners[] = 
			listenerList.getListeners(GriddlerDataListener.class);
		for(GriddlerDataListener l: listeners){
			l.boardFinished();
		}
	}


	public int checkBoardFinished(){
		return checkBoardFinished(true);
	}

	public int checkBoardFinished(boolean changeDesc){
		/*
		 This function is written with a very poor performance.
		 Should think about replacing it in the future.

		 return values:
		 -1 - board incorrect
		 0  - not finished
		 1  - finished
		 */
		int returnVal = 1;
		for(int i=0; i<getH(); i++){
			int state = checkRowFinished(i,changeDesc);
			if(state < returnVal) returnVal = state;
		}
		for(int i=0; i<getW(); i++){
			int state = checkColFinished(i,changeDesc);
			if(state < returnVal) returnVal = state;
		}
		if(returnVal == 1) fireBoardFinished();
		return returnVal;
	}

	public int checkRowFinished(int row){
		return checkRowFinished(row, true);
	}
	public int checkRowFinished(int row, boolean changeDesc){
		ArrayList<DescField> descRow;
		int[] gridRow = new int[getW()];
		try{
			descRow = desc.getRow(row);
		}catch(Exception e){
			return 0;//row not found
		}
		for(int i=0; i<getW(); i++){
			gridRow[i] = grid[i][row];
		}
		return checkDescSetFinished(descRow, gridRow, changeDesc);
	}
	public int checkColFinished(int col){
		return checkColFinished(col, true);
	}
	public int checkColFinished(int col, boolean changeDesc){
		ArrayList<DescField> descCol;
		int[] gridCol = new int[getH()];
		try{
			descCol = desc.getCol(col);
		}catch(Exception e){
			return 0;//row not found
		}
		for(int i=0; i<getH(); i++){
			gridCol[i] = grid[col][i];
		}
		return checkDescSetFinished(descCol, gridCol, changeDesc);
	}

	public int checkDescSetFinished(ArrayList<DescField> descRow, int[] gridRow, boolean changeDesc){
		//return values:
		// -1 - board incorrect
		// 0  - not finished
		// 1  - finished
		int descPos = 0;
		int gridPos = 0;
		int prevField = -1;
		int length = 0;
		int boxesInSolution = 0; //the number of boxes that should be in this row in solution
		int boxesInRow = 0; //the number of boxes in this row
		int returnVal = 0;
		for(int i=0; i<descRow.size(); i++){
			//count boxesInSolution
			boxesInSolution += descRow.get(i).getLength();
		}
		for(int i=0; i<gridRow.length; i++){
			if(gridRow[i] > 0) boxesInRow++;//count boxes
		}
		if(boxesInSolution<boxesInRow) returnVal = -1; //board incorrect
		else{
			while(gridPos <= gridRow.length){
				if(gridPos == gridRow.length || gridRow[gridPos] != prevField){
					//if prevField changed or last field
					if(prevField > 0){
						//if its not the first block of fields
						try{
							if(descRow.get(descPos).getLength() == length && descRow.get(descPos).getValue() == prevField){
								//if the field block and its description match
								descPos++;
							}else{
								descPos = 0;
						       		throw new Exception();
							}
						}catch(Exception e){
							descPos = 0;
							break;
						}
					}
					length = 1;
					if(gridPos == gridRow.length) break;
				}else{
					length++;
				}
				prevField = gridRow[gridPos];
				gridPos++;
			}
			if(descPos==descRow.size()) returnVal = 1; //correct solution
		}
		if(changeDesc){
			for(int i=0; i<descRow.size(); i++){
				if(returnVal == -1){
					descRow.get(i).setState(DescField.WARNING);
				}else if(returnVal == 0){
					descRow.get(i).setState(DescField.NORMAL);
				}else if(returnVal == 1){
					descRow.get(i).setState(DescField.INACTIVE);
				}
			}
			fireDescChanged();
		}
		return returnVal;
	}


	public void crop(){
		//when in edit mode deletes empty lines around picture
		//check columns
		int sum1 = 0;//first column
		int sum2 = 0;//last column
		int sum3 = 0;//first row
		int sum4 = 0;//last row
		for(int y=0; y<grid[0].length; y++){
			sum1 += grid[0][y]<=0?0:grid[0][y];
			sum2 += grid[grid.length-1][y]<=0?0:grid[grid.length-1][y];
		}
		for(int x=0; x<grid.length; x++){
			sum3 += grid[x][0]<=0?0:grid[x][0];
			sum4 += grid[x][grid[x].length-1]<=0?0:grid[x][grid[x].length-1];
		}
		if(sum1 == 0 || sum2 == 0 || sum3 == 0 || sum4 == 0){
			if(grid[0].length > 1 || grid.length > 1){
				if(grid[0].length > 1 && sum4 == 0) delRow(grid[0].length);
				if(grid[0].length > 1 && sum3 == 0) delRow(0);
				if(grid.length > 1 && sum2 == 0) delCol(grid.length);
				if(grid.length > 1 && sum1 == 0) delCol(0);
				crop();
			}
		}
		return;
	}
	public void genDesc(){
		//generate AND OVERWRITE new description
		desc = new Desc();

		//generate rows
		for(int y=0; y<grid[0].length; y++){
			int prevVal = -1;
			int len = 0;
			ArrayList<DescField> row = new ArrayList<DescField>();
			for(int x=0; x<grid.length; x++){
				len++;
				if(grid[x][y] != prevVal){
					if(prevVal > 0){
						row.add(new DescField(len, prevVal));
					}
					len = 0;
					prevVal = grid[x][y];
				}
			}
			if(prevVal > 0) row.add(new DescField(++len, prevVal));
			desc.addRow(row);
		}
		//generate cols
		for(int x=0; x<grid.length; x++){
			int prevVal = -1;
			int len = 0;
			ArrayList<DescField> col = new ArrayList<DescField>();
			for(int y=0; y<grid[0].length; y++){
				len++;
				if(grid[x][y] != prevVal){
					if(prevVal > 0){
						col.add(new DescField(len, prevVal));
					}
					len = 0;
					prevVal = grid[x][y];
				}
			}
			if(prevVal > 0) col.add(new DescField(++len, prevVal));
			desc.addCol(col);
		}


		checkBoardFinished();
		fireDescChanged();
	}
	public void delUnusedFields(){
		int fieldsUsed[] = new int[fields.size()];
		for(int i=0; i<fields.size(); i++) fieldsUsed[i] = 0;
		fieldsUsed[0] = 1; //background is always used
		for(int x=0; x<getW(); x++){
			for(int y=0; y<getH(); y++){
				if(grid[x][y] > 0){
					fieldsUsed[grid[x][y]]++;
				}
			}
		}
		int fieldsDeleted = 0;
		for(int i=0; i<fieldsUsed.length; i++){
			if(fieldsUsed[i] == 0){
				delField(i-fieldsDeleted);
				fieldsDeleted++;
			}
		}
	}

	public String metaToXML(){
		Color lineColor = new Color(pref.getInt("lineColor", new Color(0x66, 0x66, 0x66).getRGB()));
		Color line5Color = new Color(pref.getInt("line5Color", new Color(0x00, 0x00, 0x00).getRGB()));
		Color hlLineColor = new Color(pref.getInt("hlLineColor", new Color(0xff, 0xff, 0x00).getRGB()));
		Color bgColor = new Color(pref.getInt("bgColor", new Color (0xcc,0xcc,0xcc, 0x88).getRGB()));

		String ret = "\t<meta>\n";
		ret += "\t\t<colors>\n";
		ret += "\t\t\t<line>" + Utils.getHTMLColor(lineColor) + "</line>\n";
		ret += "\t\t\t<line5>" + Utils.getHTMLColor(line5Color) + "</line5>\n";
		ret += "\t\t\t<hlLine>" + Utils.getHTMLColor(hlLineColor) + "</hlLine>\n";
		ret += "\t\t\t<bgColor>" + Utils.getHTMLColor(bgColor) + "</bgColor>\n";
		ret += "\t\t</colors>\n";
		ret += "\t</meta>\n";
		return ret;
	}
	public String fieldsToXML(){
		String ret = "\t<fields>\n";
		for(int i=0; i<fields.size(); i++){
			ret += "\t\t" + fields.get(i).toXML(i) + "\n";
		}
		return ret + "\t</fields>\n";
	}
	public String descToXML(){
		return desc.toXML();
	}
	public String descToXML(int indent){
		return desc.toXML(indent);
	}
	public String boardToXML(){
		String ret = "\t<board>\n\t\t<data width=\"" + getW() + "\" height=\"" + getH() + "\">";
		ret += getBoardDataString();
		ret += "</data>\n\t</board>\n";
		return ret;
	}
	public String toXML(){
		String ret = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<griddler api=\"" + Double.toString(API) + "\">\n";
		ret += metaToXML();
		ret += fieldsToXML();
		ret += descToXML(1);
		ret += boardToXML();
		ret += "</griddler>";
		return ret;
	}
	public String getBoardDataString(){
		return getBoardDataString(false);
	}
	public String getBoardDataString(boolean fillBg){
		String ret = "";
		for(int y=0; y<grid[0].length; y++){
			for(int x=0; x<grid.length; x++){
				int ch = grid[x][y];

				if(ch < 0){
					if(fillBg){
						ret += '0';
					}else{
						ret += '-';
					}
				}else if(ch < 10){
					ret += Integer.toString(ch);
				}else if(ch < ((int)'Z' - (int)'A')+10){
					ret += (char)((int)'A' + (int)ch - 10);
				}else{
					ch -= (int)'Z' - (int)'A';
					ret += (char)((int)'a' + (int)ch - 10);
				}
			}
			ret += "\n";
		}
		return ret;
	}
	public String getBoardDataMD5(){
		try{
			String msg = getBoardDataString(true);
			MessageDigest m=MessageDigest.getInstance("MD5");
			m.update(msg.getBytes());
			return new BigInteger(1,m.digest()).toString(16);
		}catch(NoSuchAlgorithmException e){
			System.err.println("MD5 algorithm not found.");
			return "";
		}
	}
	public GriddlerData clone(){
		//performs a shallow copy on the whole object except of the grid,
		//which is deep copied
		try{
			GriddlerData ret = (GriddlerData)super.clone();
			int[][] newGrid = new int[getW()][getH()];
			for(int x=0; x<getW(); x++){
				newGrid[x] = grid[x].clone();
			}
			ret.setGrid(newGrid);
			return ret;
		}catch(CloneNotSupportedException e){
			System.out.println("Clone not supported exception?");
			return new GriddlerStaticData();
		}
	}

//	public Object clone(){
//		try{
//			return super.clone();
//		}catch(CloneNotSupportedException e){
//			System.out.println("FUCK");
//			return "BLE";
//		}
//	}

}
