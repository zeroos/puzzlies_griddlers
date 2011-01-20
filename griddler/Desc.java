package griddler;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author zeroos
 */

public class Desc{
	ArrayList<ArrayList<DescField> > rows;
	ArrayList<ArrayList<DescField> > cols;
	int longestRow = 0;
	int longestCol = 0;

	Desc(){
		rows = new ArrayList<ArrayList<DescField>>();
		cols = new ArrayList<ArrayList<DescField>>();
	}

	public void addRow(ArrayList<DescField> row){
		rows.add(row);
		if(row.size() > longestRow) longestRow = row.size();
	}
	public void addRow(DescField[] row){
		rows.add(new ArrayList<DescField>(Arrays.asList(row)));
		if(row.length > longestRow) longestRow = row.length;
	}
	public void addCol(ArrayList<DescField> col){
		cols.add(col);
		if(col.size() > longestCol) longestCol = col.size();
	}
	public void addCol(DescField[] col){
		cols.add(new ArrayList<DescField>(Arrays.asList(col)));
		if(col.length > longestCol) longestCol = col.length;
	}
	public void addDescFieldToLastCol(DescField d){
		ArrayList<DescField> col = cols.get(cols.size()-1);
		col.add(d);
		if(col.size() > longestCol) longestCol = col.size();
	}
	public void addDescFieldToLastRow(DescField d){
		ArrayList<DescField> row = rows.get(rows.size()-1);
		row.add(d);
		if(row.size() > longestRow) longestRow = row.size();
	}
	public ArrayList<DescField> getCol(int n){
		return cols.get(n);
	}
	public ArrayList<DescField> getRow(int n){
		return rows.get(n);
	}
	public int getLongestRow(){
		return longestRow;
	}
	public int getLongestCol(){
		return longestCol;
	}
	public int getColsSize(){
		return cols.size();
	}
	public int getRowsSize(){
		return rows.size();
	}
	public String toXML(){
		return toXML(0);
	}
	public String toXML(int indentI){
		String indent = "";
		while(indentI-->0) indent += "\t";
		String ret = indent+"<desc>\n";
		ret += indent + "\t<rows>\n";
		for(int i=0; i<rows.size(); i++){
			ret += indent + "\t\t<row id=\""+i+"\">\n";
				for(int j=0; j<rows.get(i).size(); j++){
					ret += indent + "\t\t\t" 
						+ rows.get(i).get(j).toXML();
				}
			ret += indent + "\t\t</row>\n";
		}
		ret += indent + "\t</rows>\n";
		ret += indent + "\t<cols>\n";
		for(int i=0; i<cols.size(); i++){
			ret += indent + "\t\t<col id=\""+i+"\">\n";
				for(int j=0; j<cols.get(i).size(); j++){
					ret += indent + "\t\t\t" 
						+ cols.get(i).get(j).toXML();
				}
			ret += indent + "\t\t</col>\n";
		}

		ret += indent + "\t</cols>\n";
		ret += indent + "</desc>\n";
		return ret;
	}
	public String toString(){
		return toXML();
	}
}
