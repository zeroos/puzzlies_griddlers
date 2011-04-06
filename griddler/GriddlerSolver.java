package griddler;

/**
 *
 * @author zeroos
 */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import javax.swing.event.EventListenerList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.GregorianCalendar;




public class GriddlerSolver extends Thread{
	public static final boolean VERBOSE = false;
	public boolean printResult = false;
	public boolean printSolution = false;
    public int assumptionsLimit = -1;

    EventListenerList progressChangeListenerList = new EventListenerList();
	GriddlerBoard board;
	GriddlerData data;
	int progress;
	String stepDesc = "";
	static int assumptionCounter = 0;


	public static void main(String[] arg){
		if(arg.length < 1) System.out.println("ERROR\nNo url provided.");
		GriddlerSolver solver = new GriddlerSolver(new GriddlerStaticData(arg[0], false));
		solver.setPrintResult(true);
		solver.setPrintSolution(true);
        solver.setAssumptionsLimit(-1);
		long startTime = new GregorianCalendar().getTimeInMillis();
		long timeout = 60000;//60 seconds
		solver.start();//starts a new thread
		while(true){
			try{
				Thread.sleep(1000);
			}catch(InterruptedException e){ }
			if(new GregorianCalendar().getTimeInMillis() - startTime > timeout) break;
			if(!solver.isAlive()) return;
		}
		System.out.println("TIMEOUT");
		System.exit(1);
		return;
	}
		

	public void run(){
		isSolvable();
	}
	public GriddlerSolver(Desc desc){
	}
	public GriddlerSolver(GriddlerBoard board){
		this.data = board.getData();
		this.board = board;
	}
	public GriddlerSolver(GriddlerData data){
		this.data = data;
	}
	public void setPrintResult(boolean p){
		printResult = p;
	}
	public void setPrintSolution(boolean p){
		printSolution = p;
	}
    public void setAssumptionsLimit(int l){
        assumptionsLimit = l;
    }
    public int getAssumptionsLimit(){
        return assumptionsLimit;
    }
	
	public void setProgress(int progress){
		if(progress==this.progress) return;
		this.progress = progress;
		fireProgressChanged();
	}
	public int getProgress(){
		return this.progress;
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
		}catch(InterruptedException e){
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
			if(printResult) System.out.println("UNSOLVABLE");
		}catch(InterruptedException e){
			if(printResult) System.out.println("INTERRUPTED");
		}
	}

	public void solve() throws UnsolvableException, InterruptedException{
		solve(-1);
	}
	public void updateProgress(){
		int fieldsCompleted = data.getFilledFields();
		int allFields = data.getW()*data.getH();
		int fieldsLeft = allFields - fieldsCompleted;
	//	if(assumptionCounter == 0){
			setProgress(fieldsCompleted*100 / allFields);
			return;
	//	}
//		int assumtions = Math.pow(data.getFields().length, fieldsLeft);
//		setProgress(fieldsCompleted*100 / allFields);

	}
	public void periodicalCheck() throws InterruptedException{
		if(Thread.currentThread().isInterrupted()) throw new InterruptedException();
	}


	public boolean checkBoardFinished(){
		if(data.checkBoardFinished(false) == 1){
			if(printResult){
				System.out.println("COMPLETED");
				System.out.println(assumptionCounter + " ASSUMPTIONS");
			}
			if(printSolution){
				try{
					System.out.println(data.getBoardDataString(true));
				}catch(Exception e){
					System.out.println("ERROR");
				}
			}
			return true;
		}
		return false;

	}
	public void solve(int numberOfSteps) throws UnsolvableException, InterruptedException{
		try{
			updateProgress();
			if(numberOfSteps == 0) return;
			boolean changed = false;
	
/*			while(forEachRowAndColumn(new SimpleBoxesAlgorithm(), numberOfSteps)){
				if(VERBOSE) System.out.println("Simple boxes: board changed.");
				numberOfSteps--;
				changed = true;
				if(numberOfSteps == 0) return;
			}
			while(forEachRowAndColumn(new SimpleSpacesAlgorithm(), numberOfSteps)){
				if(VERBOSE) System.out.println("Simple spaces: board changed.");
				numberOfSteps--;
				changed = true;
				if(numberOfSteps == 0) return;
			}*/
			while(forEachRowAndColumn(new CompleteLineAlgorithm(), numberOfSteps)){
				if(VERBOSE) System.out.println("CompleteLine: board changed.");
				numberOfSteps--;
				changed = true;
				if(numberOfSteps == 0) return;
			}

           if(assumptionsLimit != 0){
                while(makeAssumption(1)){
            		if(checkBoardFinished()) return;
                }
                if(assumptionsLimit != 1){
                    while(makeAssumption(assumptionsLimit)){
            		    if(checkBoardFinished()) return;
                    }
                }
            }
            //makeAssumption(-1);
			if(!checkBoardFinished()) throw new UnsolvableException(UnsolvableException.CONTRADICTION); //end of board, no solutions
		}catch(UnsolvableException e){
			if(printResult){
				if(e.getReason() == e.MULTIPLE_SOLUTIONS){
					System.out.println("MULTIPLE_SOLUTIONS");
				}else if(e.getReason() == e.CONTRADICTION){
					System.out.println("CONTRADICTION");
	//				e.printStackTrace();
				}
			}
			throw e;
		} catch (InterruptedException e) {
			if(printResult) System.out.println("INTERRUPTED");
			throw e;
		}
	}

	private boolean makeAssumption(int assumptionsLimit) throws UnsolvableException, InterruptedException{
        boolean fieldFound = false;
		for(int x=0; x<data.getW(); x++){
			for(int y=0; y<data.getH(); y++){
				//find an unfilled field
				if(data.getFieldVal(x,y) == -1){
					//copy data
					GriddlerData solution = null;
					for(int i=0; i<data.getFields().length; i++){
						GriddlerData newData = data.clone();
                        int fieldVal = (i+1)%data.getFields().length;
						//for each field value
						newData.setFieldVal(fieldVal, x, y);
						assumptionCounter++;
						int assumptionNum = assumptionCounter;
						if(VERBOSE) System.out.println("Assumption " + assumptionCounter + ": field " + x + "x"+ y + " set to " + fieldVal);
						periodicalCheck();
						GriddlerSolver newSolver = new GriddlerSolver(newData);


                        newSolver.setAssumptionsLimit(assumptionsLimit-1);
						try{
							newSolver.solve(); //if unsolveble throws exception


                            if(!newSolver.checkBoardFinished() && fieldVal != 0){
                                assumptionCounter--;
                                //not last field and didn't found solution
                                break;
                            }

							if(solution != null){
								throw new UnsolvableException(UnsolvableException.MULTIPLE_SOLUTIONS);
							}
							solution = newSolver.getData().clone();
                            fieldFound = true;
							if(VERBOSE) System.out.println(assumptionNum + " success");
                            

						}catch(UnsolvableException e){
							if(VERBOSE) System.out.println(assumptionNum + " failed");
                            if(e.getReason() == e.MULTIPLE_SOLUTIONS) throw e;
						}
					}
					if(solution != null) this.data.setGrid(solution.getGrid());
					return fieldFound;
				}
			}
		}
//		if(!data.checkBoardFinished(false)) throw new UnsolvableException(UnsolvableException.CONTRADICTION);
		return fieldFound;
	}


	private boolean forEachRowAndColumn(SolvingAlgorithm a) throws UnsolvableException, InterruptedException{
		return forEachRowAndColumn(a, -1);
	}
	private boolean forEachRowAndColumn(SolvingAlgorithm a, int stepLimit) throws UnsolvableException, InterruptedException{
		boolean stepPerformed = false;
		periodicalCheck();
		updateProgress();
		for(int i=0; i<data.getH() && stepLimit!=0; i++){
			int finished = data.checkRowFinished(i, false);
			if(VERBOSE) System.out.println("###### Row " + i + ":");
			if(finished == 1){
				if(VERBOSE) System.out.println("^^^ finished");
			//	continue;
			}else if(finished == -1) throw new UnsolvableException(UnsolvableException.CONTRADICTION);
			try{

				if(a.solve(data.getRow(i), data.getDesc().getRow(i))){
					data.setRow(i, a.getNewFieldSet());
					stepLimit--;
					stepPerformed = true;
				}
			}catch(IndexOutOfBoundsException e){
				if(VERBOSE){
					System.out.println("out");
					e.printStackTrace();
				}
			}
		}
		if(stepLimit==0) return stepPerformed;
		for(int i=0; i<data.getW() && stepLimit!=0; i++){
			int finished = data.checkColFinished(i, false);
			if(VERBOSE) System.out.println("###### Col " + i + ":");
			if(finished == 1){
				if(VERBOSE) System.out.println("^^^ finished");
			//	continue;
			}else if(finished == -1) throw new UnsolvableException(UnsolvableException.CONTRADICTION);
			try{
				if(a.solve(data.getCol(i), data.getDesc().getCol(i))){
					data.setCol(i, a.getNewFieldSet());
					stepLimit--;
					stepPerformed = true;
				}
			}catch(IndexOutOfBoundsException e){
				if(VERBOSE){
					System.out.println("out");
					e.printStackTrace();
				}
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

	private class CompleteLineAlgorithm extends SolvingAlgorithm{
		ArrayList<int[]> possibleSolutions;
		boolean newFieldFound;
		int[] availableFieldValues;
		int[] fs;
		ArrayList<DescField> ds;

		public boolean solve(int[] fs, ArrayList<DescField> ds) throws UnsolvableException{
            //returns wheather the new field was found or not
			if(ds.size()==0){
                boolean boardChanged = false;
                int[] solution = new int[fs.length];
                for(int i=0; i<fs.length; i++){
                    if(fs[i] > 0) throw new UnsolvableException(UnsolvableException.CONTRADICTION);
                    if(fs[i] != 0){
                        solution[i] = 0;
                        boardChanged = true;
                    }
                }
                if(boardChanged) setNewFieldSet(solution);
                return boardChanged;
            }
			possibleSolutions = new ArrayList<int[]>();
			newFieldFound = false;
			availableFieldValues = getAvailableFieldValues(ds);
			this.fs = fs;
			this.ds = ds;

			int[] solution = new int[fs.length];
			System.arraycopy(fs, 0, solution, 0, fs.length);

			
			if(VERBOSE) System.out.println("Possible solutions before solve: " + possibleSolutions.size());
			solve(0,0);


			if(VERBOSE){
				System.out.println("So far:");
				for(int j=0; j<solution.length;j++){
					System.out.print(solution[j] + ",");
				}
				System.out.println();
				System.out.println("Possible solutions: " + possibleSolutions.size());
				for(int i=0; i<possibleSolutions.size(); i++){
					for(int j=0; j<possibleSolutions.get(i).length;j++){
						System.out.print(possibleSolutions.get(i)[j] + ",");
					}
					System.out.println();
				}
			}

            if(possibleSolutions.size() == 0){//no solutions for this line
                if(VERBOSE) System.out.println("No solutions found.");
                throw new UnsolvableException(UnsolvableException.CONTRADICTION);
            }

			//checks each possible sollution to determine new fields
			for(int i=0; i<solution.length; i++){//for each field
				int possibleFieldValue=-1;
				if(solution[i] != -1){//known value
					continue;
				}
				for(int s=0; s<possibleSolutions.size(); s++){//for each solution
					int[] sol = possibleSolutions.get(s);
					if(s==0){
						possibleFieldValue = sol[i];
					}else if(possibleFieldValue != sol[i]){
						possibleFieldValue = -1;
						break;
					}
				}
				if(possibleFieldValue != -1){//possible solution was found
					solution[i] = possibleFieldValue;
					newFieldFound = true;
				}
			}
			setNewFieldSet(solution);
			return newFieldFound;



		}

		public void solve(int pos, int descPos) throws UnsolvableException{
			if(VERBOSE) System.out.println("solve("+pos+","+descPos+");");

			int blockLength = ds.get(descPos).getLength();
			int blockValue = ds.get(descPos).getValue();

			int rightPadding = 0;
			//count rightPadding
			int previousValue = blockValue;
			for(int i=descPos+1; i<ds.size();i++){
				if(ds.get(i).getValue() == previousValue) rightPadding++;
				rightPadding+=ds.get(i).getLength();
				previousValue = ds.get(i).getValue();
			}
			if(VERBOSE) System.out.println("Right padding: " + rightPadding);


			for(int i=pos; i<=fs.length-rightPadding-blockLength; i++){
				int isPossible = isPossible(descPos, i, pos);
				if(isPossible == 1){
					int old_data[] = new int[fs.length];
					for(int j=pos; j<i+blockLength; j++){
						old_data[j] = fs[j];
						if(j<i) fs[j] = 0;
						else fs[j] = blockValue;
					}
					if(descPos+1 < ds.size()){
						solve(i+blockLength, descPos+1);
					}else{
						boolean possible = true;
						for(int j=i+blockLength; j<fs.length; j++){
							old_data[j] = fs[j];
							if(fs[j]<=0) fs[j]=0;
							else possible = false;//a box is left
						}
						if(possible) markPossibleSolution();

						//restore previous data
						for(int j=i+blockLength; j<fs.length; j++){
							fs[j] = old_data[j];
						}
					}
					//restore previous data
					for(int j=pos; j<i+blockLength; j++){
						fs[j] = old_data[j];
					}
				}else if(isPossible == -1){
					break;
				}
			}	
		}
		public int isPossible(int descPos, int pos, int startPos){
			//checks if it is possible to insert a block from descPos into a pos,
			//assumes that the grid start at startPos AND that it is called for each possibility 
				//(you SHOULD NOT expect it to work if you pass something that's in the middle)

			//returns -1 if it is NOT possible and there is no point in moving further [ie the block was skipped]
			//returns 0 if it is NOT possible, but you should move further
			//returns 1 if it is possible
			int blockLength = ds.get(descPos).getLength();
			int blockValue = ds.get(descPos).getValue();

			if(VERBOSE){
				System.out.println("Trying to put " + blockValue + "x" + blockLength + " on " + pos + " from " + startPos);
				System.out.print("^^^ on fs: ");
				for(int j=0; j<fs.length;j++){
					System.out.print(fs[j] + ",");
				}
				System.out.println();
			}



			//check if no blocks were skipped
			if(pos>startPos){
				if(fs[pos-1]==blockValue){
					if(VERBOSE) System.out.println("S: skipped; pos: " + pos + " startPos: " + startPos);
					return -1;
				}
			}

			//check if not covers any different colors
			for(int i=pos; i<pos+blockLength; i++){
				if(fs[i] != -1 && fs[i] != blockValue){
					if(VERBOSE) System.out.println("S: different color");
                    if(fs[i] == 0) return 0;
                    return -1;
				}
			}
			//check if not collides with any other block of the same value
			boolean collision = false;
			if(pos>0 && fs[pos-1] == blockValue){
				collision = true;
			}else if(pos+blockLength<fs.length && fs[pos+blockLength] == blockValue){
				collision = true;
			}

			if(collision){
				if(VERBOSE) System.out.println("S: collision");
				return 0;
			}

			if(VERBOSE) System.out.println("S: Possible");
			return 1;

		}
		public void markPossibleSolution(){
			int[] solution = new int[fs.length];
			System.arraycopy(fs, 0, solution, 0, fs.length);
			possibleSolutions.add(solution);
		}
	}

	private abstract class SolvingAlgorithm{
		int[] newFieldSet = new int[]{};

		//returns true if new fields were found
		public abstract boolean solve(int[] fieldSet, ArrayList<DescField> descFieldSet) throws UnsolvableException;
		int[] getAvailableFieldValues(ArrayList<DescField> ds){
			Set valuesSet = new HashSet();
			for(int i=0; i<ds.size(); i++){
				valuesSet.add(ds.get(i).getValue());
			}
			int[] values = new int[valuesSet.size()];
			Iterator it = valuesSet.iterator();
			for(int i=0; i<values.length; i++){
				values[i] = (Integer)it.next();
			}
			return values;
		}
		public int[] getNewFieldSet(){
			return newFieldSet;
		}
		public void setNewFieldSet(int[] f){
			newFieldSet = f;
		}
	}














	public void addProgressChangeListener(ChangeListener l){
		progressChangeListenerList.add(ChangeListener.class, l);
	}
	public void removeProgressChangeListener(ChangeListener l){
		progressChangeListenerList.remove(ChangeListener.class, l);
	}
	public void fireProgressChanged(){
		ChangeListener listeners[] =
			progressChangeListenerList.getListeners(ChangeListener.class);
		for(ChangeListener l: listeners){
			l.stateChanged(new ChangeEvent(this));
		}
	}







}
