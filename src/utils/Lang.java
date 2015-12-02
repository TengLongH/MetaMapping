package utils;

import java.io.IOException;
import java.util.Properties;

public class Lang {

	private static Properties p = new Properties();
	private static String curLang="";
	
	public static void load( String lang ) throws IOException{
		if( null == lang || lang.trim().equals("") )return;
		if( curLang.equals(lang) )return ;
		
		switch( lang.trim() ){
		case "zh-CN":
			//in = Lang.class.getResource(name)
			p.load( Lang.class.getResourceAsStream("/lang/zh-CN.properties"));
			break;
		case "en-US":
			p.load( Lang.class.getResourceAsStream("/lang/en-US.properties"));
			break;
		default:
			return ;
		} 
		curLang = lang.trim();
	}
	public static String get( String key ){
		try{
			return p.getProperty(key);
		}catch( Exception e ){}
		return null;
	}
	public static String get( String key, String defVlaue ){
		try{
			return p.getProperty(key, defVlaue );
		}catch( Exception e ){}
		return null;
	}
	
	public static String getLang(){
		return curLang;
	}
}
