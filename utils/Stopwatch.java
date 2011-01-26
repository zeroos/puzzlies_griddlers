package utils;

/**
 * This class can act as a stopwatch, with the `pause` feature. 
 *
 * @author zeroos
 */

import java.util.GregorianCalendar;

 
public class Stopwatch{
	Long start;
	Long stop;
	Long pause;
	boolean wasPaused = false;

	public Stopwatch(){

	}
	public void start(){
		if(stop != null) stop = null;
		if(pause != null){
			start += new GregorianCalendar().getTimeInMillis() - pause;
			pause = null;
		}
		else start = new GregorianCalendar().getTimeInMillis();
	}
	public void stop(){
		stop = new GregorianCalendar().getTimeInMillis();
	}
	public void pause(){
		wasPaused = true;
		pause = new GregorianCalendar().getTimeInMillis();
	}
	public long getTime(){
		if(pause != null){
			return pause - start;
		}
		if(stop != null){
			return stop - start;
		}
		return new GregorianCalendar().getTimeInMillis() - start;

	}
	public boolean wasPaused(){
		return wasPaused;
	}
}
