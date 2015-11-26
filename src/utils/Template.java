package utils;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Template {

	private static Document xml;
	private static Element book;
	
	private static int descriptRow;
	private static int sampleNo;
	private static int sampleType;
	private static int stationNo;
	static{
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			xml = builder.parse(new File("tree/sys/templateTree.xml"));
			book = xml.getDocumentElement();
			Element descript = Utils.getChildElementByName(book, "Description");
			descriptRow = Integer.parseInt( descript.getAttribute("row"));
			Element column = utils.Utils.getChildElementByName(
					descript, "SampleNo");
			sampleNo = Integer.parseInt( column.getAttribute("colum"));
			column = utils.Utils.getChildElementByName(
					descript, "StationNo");
			stationNo = Integer.parseInt(column.getAttribute("colum") );
			column = utils.Utils.getChildElementByName(
					descript, "SampleType");
			sampleType = Integer.parseInt(column.getAttribute("colum") );
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static String get( Object[] path, String attri ){
		return Utils.getAttribute(book, path, attri);
	}


	public static int getSampleNo() {
		return sampleNo;
	}


	public static int getSampleType() {
		return sampleType;
	}


	public static int getStationNo() {
		return stationNo;
	}


	public static int getDescriptRow() {
		return descriptRow;
	}
	
	
}
