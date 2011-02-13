package utils;

import java.util.HashMap;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;


/**
 *
 * @author zeroos
 */
public class MyPreferences{
	private static MyPreferences pref;
	private HashMap map;
	EventListenerList changeListenerList = new EventListenerList();


	private MyPreferences(){
		map = new HashMap();
	}
	static public MyPreferences getInstance(){
		if(pref == null){
			pref = new MyPreferences();
		}
		return pref;
	}
	public int getInt(String key, int def){
		Object val = map.get(key);
		if(val != null){
			return ((Integer)val).intValue();
		}else{
			return def;
		}
	}
	public String get(String key, String def){
		String val = (String)map.get(key);
		if(val != null) return val;
		else return def;
	}
	public void setInt(String key, int val){
		map.put(key, val);
		fireChangeEvent();
	}

	public void addChangeListener(ChangeListener l){
		changeListenerList.add(ChangeListener.class, l);
	}
	public void removeChangeListener(ChangeListener l){
		changeListenerList.remove(ChangeListener.class, l);
	}
	public void fireChangeEvent(){
		ChangeListener listeners[] =
			changeListenerList.getListeners(ChangeListener.class);
		for(ChangeListener l: listeners){
			l.stateChanged(new ChangeEvent(this));
		}
	}
}
