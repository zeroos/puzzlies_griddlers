package griddler;

import java.util.EventListener;


/**
 *
 * @author zeroos
 */

abstract class GriddlerDataListener implements EventListener{
	void fieldChanged(int x, int y){	
	}
	void fieldsListChanged(){
	}
	void descChanged(){
	}
	void boardFinished(){
	}
}


