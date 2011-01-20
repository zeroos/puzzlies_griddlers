package griddler;

/**
 *
 * @author zeroos
 */
import java.util.ArrayList;


public class GriddlerSolver{
	GriddlerBoard board;
	GriddlerData data;
	String stepDesc = "";
	static int assumptionCounter = 0;

	public GriddlerSolver(Desc desc){
	}
	public GriddlerSolver(GriddlerBoard board){
		this.data = board.getData();
		this.board = board;
	}
	public GriddlerSolver(GriddlerData data){
		this.data = data;
//		grid = data.getGrid();
//		desc = data.getDesc();
	}
	
	public void setData(GriddlerData data){
		this.data = data;
	}
	public GriddlerData getData(){
		return this.data;
	}
	public void reinit(){
		if(board != null) setData(board.getData());
	}

	public String getStepDesc(){
		return stepDesc;
	}
	public void setStepDesc(String desc){
		stepDesc = desc;
	}

	public boolean isSolvable(){
		try{
			solve();
		}catch(UnsolvableException e){
			return false;
		}
		return true;

	}
	public String descNextStep(){
		nextStep();
		return getStepDesc();
	}
	public void nextStep(){
		try{
			solve(1);
		}catch(UnsolvableException e){
			System.out.println("Sry, unsolvable.");
		}
	}

	public void solve() throws UnsolvableException{
		solve(-1);
	}
	public void solve(int numberOfSteps) throws UnsolvableException{
		try{
			if(numberOfSteps == 0) return;
			boolean changed = false;
	
			while(forEachRowAndColumn(new SimpleBoxesAlgorithm(), numberOfSteps)){
				System.out.println("Simple boxes: board changed.");
				numberOfSteps--;
				changed = true;
				if(numberOfSteps == 0) return;
			}
			while(forEachRowAndColumn(new SimpleSpacesAlgorithm(), numberOfSteps)){
				System.out.println("Simple spaces: board changed.");
				numberOfSteps--;
				changed = true;
				if(numberOfSteps == 0) return;
			}
			if(data.checkBoardFinished(false) == 1){
				System.out.println("Board finished.");
				return;
			}
			makeAssumption();
			if(data.checkBoardFinished(false) != 1) throw new UnsolvableException(UnsolvableException.CONTRADICTION); //end of board, no solutions
		}catch(UnsolvableException e){
			if(e.getReason() == e.MULTIPLE_SOLUTIONS) System.out.println("Multiple solutions");
			else if(e.getReason() == e.CONTRADICTION){
				System.out.println("Contradiction");
//				e.printStackTrace();
			}
			throw e;
		}
	}

	private void makeAssumption() throws UnsolvableException{
		for(int x=0; x<data.getW(); x++){
			for(int y=0; y<data.getH(); y++){
				//find an unfilled field
				if(data.getFieldVal(x,y) == -1){
					System.out.println("-1!");
					//copy data
					GriddlerData solution = null;
					for(int i=0; i<data.getFields().length; i++){
						GriddlerData newData = data.clone();
						//for each field value
						newData.setFieldVal(i, x, y);
						assumptionCounter++;
						int assumptionNum = assumptionCounter;
						System.out.println("Assumption " + assumptionCounter + ": field " + x + "x"+ y + " set to " + i);
						if(data.checkBoardFinished(false) == -1){
							System.out.println(" ^^^ instantly failed");
							continue;
						}
						GriddlerSolver newSolver = new GriddlerSolver(newData);
						try{
							newSolver.solve(); //if unsolveble throws exception
							if(solution != null){
								throw new UnsolvableException(UnsolvableException.MULTIPLE_SOLUTIONS);
							}
							solution = newSolver.getData().clone();
							System.out.println(assumptionNum + " success");
						}catch(UnsolvableException e){
							System.out.println(assumptionNum + " failed");
						}
					}
					if(solution != null) this.data.setGrid(solution.getGrid());
					return;
				}
			}
		}
//		if(!data.checkBoardFinished(false)) throw new UnsolvableException(UnsolvableException.CONTRADICTION);
		return;
	}


	private boolean forEachRowAndColumn(SolvingAlgorithm a) throws UnsolvableException{
		return forEachRowAndColumn(a, -1);
	}
	private boolean forEachRowAndColumn(SolvingAlgorithm a, int stepLimit) throws UnsolvableException{
		boolean stepPerformed = false;
		for(int i=0; i<data.getH() && stepLimit!=0; i++){
			try{
				if(a.solve(data.getRow(i), data.getDesc().getRow(i))){
					data.setRow(i, a.getNewFieldSet());
					stepLimit--;
					stepPerformed = true;
				}
			}catch(IndexOutOfBoundsException e){
				System.out.println("out");
//				e.printStackTrace();
			}
		}
		if(stepPerformed) return stepPerformed;
		for(int i=0; i<data.getW() && stepLimit!=0; i++){
			try{
				if(a.solve(data.getCol(i), data.getDesc().getCol(i))){
					data.setCol(i, a.getNewFieldSet());
					stepLimit--;
					stepPerformed = true;
				}
			}catch(IndexOutOfBoundsException e){
				System.out.println("out");
//				e.printStackTrace();
			}
		}
		return stepPerformed;

	}


	private class SimpleBoxesAlgorithm extends SolvingAlgorithm{
		public boolean solve(int[] fs, ArrayList<DescField> ds) throws UnsolvableException{
			boolean changed = false;
			//create a temprorary array and try to fill it with condensed boxes
			int[] leftTempTab = new int[fs.length];
			int[] rightTempTab = new int[fs.length];
			for(int i=0; i<leftTempTab.length; i++) leftTempTab[i] = -1;
			for(int i=0; i<rightTempTab.length; i++) rightTempTab[i] = -1;

			int tempTabPos = 0;
			for(int descTabPos = 0; descTabPos < ds.size(); descTabPos++){
				DescField d = ds.get(descTabPos);
				for(int descFieldPos=0; descFieldPos<d.getLength(); descFieldPos++){
					leftTempTab[tempTabPos++] = descTabPos;
				}
				if(descTabPos+1 < ds.size() && d.getValue() == ds.get(descTabPos+1).getValue()){//if the same color of two blocks
					leftTempTab[tempTabPos++] = -1;
				}
			}
			tempTabPos = rightTempTab.length-1;
			for(int descTabPos = ds.size()-1; descTabPos >= 0; descTabPos--){
				DescField d = ds.get(descTabPos);
				for(int descFieldPos=0; descFieldPos<d.getLength(); descFieldPos++){
					rightTempTab[tempTabPos--] = descTabPos;
				}
				if(descTabPos > 0 && d.getValue() == ds.get(descTabPos-1).getValue()){//if the same color of two blocks
					rightTempTab[tempTabPos--] = -1;
				}
			}
			for(int i=0; i<fs.length; i++){
				//search for fields with the same number in two arrays
				if(leftTempTab[i] == rightTempTab[i] && leftTempTab[i] != -1){

					if(fs[i] > 0 && fs[i] != ds.get(rightTempTab[i]).value){
						//at i there is something else than should be
						throw new UnsolvableException(UnsolvableException.CONTRADICTION);
					}else if(fs[i] <= 0 || fs[i] != ds.get(rightTempTab[i]).value){
						//if fs[i] is set to the correct value, this code won't be executed
						changed = true;
						fs[i] = ds.get(rightTempTab[i]).value;
					}
				}
			}
			if(changed) setNewFieldSet(fs);
			return changed;
		}
	}

	private class SimpleSpacesAlgorithm extends SolvingAlgorithm{
		public boolean solve(int[] fs, ArrayList<DescField> ds) throws UnsolvableException{
			boolean changed = false;
			return changed;
		}
	}

	private abstract class SolvingAlgorithm{
		int[] newFieldSet = new int[]{};
		public abstract boolean solve(int[] fieldSet, ArrayList<DescField> descFieldSet) throws UnsolvableException;
		public int[] getNewFieldSet(){
			return newFieldSet;
		}
		public void setNewFieldSet(int[] f){
			newFieldSet = f;
		}
	}
}
