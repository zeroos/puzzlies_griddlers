package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;

/**
 *
 * @author zeroos
 */


///!---------------- TODO: mnemonics i18n
 
public class TR{
	private static HashMap<String, String> res;
	public static String t(String s){
		return s;
/*		if(res == null){
			init();
		}
		String ret = res.get(s);
		if(ret != null) return ret;
		return s;*/
	}

	public static void init(){
		init(Locale.getDefault());
	}
	public static void init(Locale loc){
		res = new HashMap<String, String>();
		try{
			//choose file
			File dir = new File("i18n");
			File file = new File(dir, "en_EN.translation");

			File localFile = new File(dir, loc.getLanguage() + "_"  + loc.getCountry() + ".translation");
			if(localFile.exists()){
				file = localFile;
			}else{
				String[] locales = dir.list();
				for(String s: locales){
					if(s.substring(0,2).equals(loc.getLanguage())){
						file = new File(dir, s);
						break;
					}
				}
			}
			System.out.print("Loading languag: " + file.getName().substring(0, file.getName().lastIndexOf('.')) + "\t\t");
			//read file and add values to res
			FileInputStream fstream = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream, "UTF-8"));
			String line;
			
			while ((line = br.readLine()) != null){
				try{
					int sepPos = line.indexOf('=');
					res.put(line.substring(0, sepPos), line.substring(sepPos+1));
				}catch(Exception e){
				}
			}
			fstream.close();
			System.out.println("DONE");
		}catch(Exception e){
			System.out.println();
			System.out.println("Error while reading language file: " + e.getMessage());
		}
	}
}
