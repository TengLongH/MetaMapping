package utils;

import java.io.IOException;
import java.util.Properties;

public class Config {

	private static Properties p = new Properties();
	public static void load() throws IOException{
		String config = "/config/config.properties";
		p.load( Config.class.getResourceAsStream(config) );
	}
	public static String get(String key ) {
		return p.getProperty(key);
	}
}
