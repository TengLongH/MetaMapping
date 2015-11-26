package source;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import common.ColumnMapping;
import utils.Template;

public class Transport {

	private Workbook sourceBook;
	private Workbook templateBook;
	private Element sourceRoot;

	private String preSheetName;
	private Row curRow;

	private List<ColumnMapping> map = new ArrayList<ColumnMapping>() ;

	public Transport(File source, File descript) {
		try {
			OutputStream temp = new FileOutputStream(new File("tree/sys/result.xlsx"));
			templateBook = WorkbookFactory.create(new File("tree/sys/template.xlsx"));
			templateBook.write(temp);
			templateBook.close();
			temp.close();

			templateBook = WorkbookFactory.create(new File("tree/sys/result.xlsx"));
			sourceBook = WorkbookFactory.create(source);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			sourceRoot = builder.parse(descript).getDocumentElement();
			transformBook();
			temp = new FileOutputStream(new File("result.xlsx"));
			templateBook.write(temp);
			temp.flush();
			temp.close();
			templateBook.close();
			sourceBook.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void transformBook() {

		NodeList xmlSheets = sourceRoot.getElementsByTagName("sheet");
		for (int i = 0; i < xmlSheets.getLength(); i++) {
			Element xmlSheet = (Element) xmlSheets.item(i);
			String name = xmlSheet.getAttribute("name").trim();
			Sheet sheet = sourceBook.getSheet(name);
			if (null != sheet) {
				transformSheet(sheet, xmlSheet);
			} else {
				System.err.println("can't find sheet: " + name);
			}
		}
	}

	private void transformSheet(Sheet sheet, Element xmlSheet) {

		int stationNo = -1, sampleType = -1, startRow = -1;
		try {
			// 解析该表和模板文件的对应关系
			parseSheetMapping(xmlSheet);
			Collections.sort(map);
			// 获取样品的站位号和类型
			NodeList columns = xmlSheet.getElementsByTagName("field");
			for (int i = 0; i < columns.getLength(); i++) {
				if (stationNo >= 0 && sampleType >= 0)
					break;
				Element column = (Element) columns.item(i);
				String mapping = column.getAttribute("mapping").trim();
				if (null == mapping)
					continue;
				if (mapping.endsWith("Temlplate>Description>StationNo")) {
					stationNo = Integer.parseInt(column.getAttribute("colum"));
				} else if (mapping.endsWith("Temlplate>Description>SampleType")) {
					sampleType = Integer.parseInt(column.getAttribute("colum"));
				}
			}

			startRow = Integer.parseInt(xmlSheet.getAttribute("row"));
			for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
				transformRow(sheet.getRow(i), stationNo, sampleType);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.printf("Sheet:%s station:%d type:%d row:%d", sheet.getSheetName(), stationNo, sampleType,
					startRow);
		}
	}

	private void transformRow(Row row, int stationNo, int sampleType) {
		String station = null;
		String type = null;
		String sampleNo = null;

		Cell cell = null;
		try {
			station = row.getCell(stationNo).getStringCellValue().trim();
			if( station.equals("") )return ;
			type = row.getCell(sampleType).getStringCellValue().trim();
			if( type.equals("") )return ;
			sampleNo = getsampleNo(station, type);
			for( ColumnMapping m : map ){
				cell = row.getCell(m.getSourceColumn());
				transformCell(cell, m.getPlateSheet(), m.getPlateColumn(), sampleNo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.printf("Row:%d SampleNo:%s Station:%s Type:%s", row.getRowNum(), sampleNo, station, type);
		}
	}

	private void transformCell(Cell srcCell, String sheetName, int desIndex, String sampleNo) {

		try {
			if (!sheetName.equals(preSheetName)) {
				curRow = getRow(sheetName, sampleNo);
			}
			Cell desCell = curRow.getCell(desIndex, Row.CREATE_NULL_AS_BLANK);
			copyCell(srcCell, desCell);
		} catch (Exception e) {
			System.out.printf("Sheet:%s desIndex:%d sampleNo:%s", sheetName, desIndex, sampleNo);
			e.printStackTrace();
		}

	}

	private Row getRow(String sheetName, String sampleNo) {
		Row row = null;
		Cell cell = null;
		Sheet sheet = templateBook.getSheet(sheetName);
		// get the start data row of sheet and the sampleNo column index
		String attr = Template.get(new String[] { sheetName }, "row");
		int startRow = Integer.parseInt(attr);
		attr = Template.get(new String[] { sheetName, "SampleNo" }, "colum");
		int index = Integer.parseInt(attr);
		// search sheet to find the sampleNo
		for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
			row = sheet.getRow(i);
			if (null == row)
				continue;
			cell = row.getCell(index);
			if (null == cell)
				continue;
			String value = cell.getStringCellValue();
			if (value.equals(sampleNo.trim())) {
				return row;
			}
		}
		
		// if the sampleNo not in sheet, add it
		int temp = sheet.getLastRowNum()+1;
		int result = startRow >= temp ? startRow : temp;
		row = sheet.createRow(result);
		cell = row.createCell(index);
		cell.setCellValue(sampleNo);
		return row;
	}

	private void copyCell(Cell cell, Cell desCell) {
		try {
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_NUMERIC:
				desCell.setCellValue(cell.getNumericCellValue());
				break;
			case Cell.CELL_TYPE_STRING:
				desCell.setCellValue(cell.getStringCellValue());
				break;
			case Cell.CELL_TYPE_FORMULA:
				desCell.setCellValue(cell.getNumericCellValue());
				break;
			case Cell.CELL_TYPE_BLANK:

				break;
			case Cell.CELL_TYPE_BOOLEAN:
				desCell.setCellValue(cell.getBooleanCellValue());
				break;
			default:
				System.err.println("Cell Type is error");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseSheetMapping(Element xmlSheet) {

		map.clear();
		String mapping = null;
		List<String> path = null;
		String sheetName = null;
		int columnIndex = -1, srcIndex = -1;
		NodeList columns = xmlSheet.getElementsByTagName("field");
		try {
			for (int i = 0; i < columns.getLength(); i++) {

				Element column = (Element) columns.item(i);
				srcIndex = Integer.parseInt(column.getAttribute("colum"));
				mapping = column.getAttribute("mapping").trim();
				if (null == mapping)
					continue;
				mapping = mapping.substring(2).trim();
				path = new ArrayList<String>(Arrays.asList(mapping.split(">")));
				path.remove(0);
				sheetName = path.get(0);
				String strColumn = Template.get(path.toArray(), "colum");
				columnIndex = Integer.parseInt(strColumn);
				map .add( new ColumnMapping(srcIndex, columnIndex, sheetName));
			}
		} catch (Exception e) {
			System.err.printf("%s %s %d", mapping, path, srcIndex);
			e.printStackTrace();
		}

	}

	private String getsampleNo(String station, String type) {
		String id = searchSample(station, type);
		if (null == id) {
			id = appendSample(station, type);
		}
		return id;
	}

	private String appendSample(String station, String type) {
		String id = createID();
		Sheet descript = templateBook.getSheet("Description");
		int temp = descript.getLastRowNum() + 1 ;
		int index =  temp >= Template.getDescriptRow() ? temp:Template.getDescriptRow();
		Row row = descript.createRow(index);
		Cell cell = row.createCell(Template.getSampleNo());
		cell.setCellValue(id);
		return id;
	}

	private String searchSample(String station, String type) {
		String s = null, t = null;
		Sheet sheet = templateBook.getSheet("Description");
		for (int i = Template.getDescriptRow(); i < sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if (null == row)
				continue;
			Cell cell = row.getCell(Template.getStationNo());
			if (null == cell)
				continue;
			s = cell.getStringCellValue();
			cell = row.getCell(Template.getSampleType());
			if (null == cell)
				continue;
			t = cell.getStringCellValue();
			if (station.equals(s.trim()) && type.equals(t.trim())) {
				return row.getCell(Template.getSampleNo()).getStringCellValue();
			}
		}
		return null;
	}

	private String createID() {
		return java.util.UUID.randomUUID().toString();
	}

	public void printContent() {
		try {
			Sheet sheet = templateBook.getSheet("Description");
			Row row = sheet.getRow(4);
			Cell cell = row.getCell(3);
			System.out.printf("%d %d %s\n", 4, 3, cell);
			cell = row.getCell(4);
			System.out.printf("%d %d %s\n", 4, 4, cell);
			sheet = templateBook.getSheet("MajorElement");
			row = sheet.getRow(5);
			cell = row.getCell(10);
			System.out.printf("%d %d %s\n", 5, 10, cell);
			cell = row.getCell(12);
			System.out.printf("%d %d %s\n", 5, 12, cell);
			cell = row.getCell(13);
			System.out.printf("%d %d %s\n", 5, 13, cell);
			cell = row.getCell(14);
			System.out.printf("%d %d %s\n", 5, 14, cell);
			cell = row.getCell(15);
			System.out.printf("%d %d %s\n", 5, 18, cell);
			System.out.println();
		} catch (Exception e) {
			System.out.println();
		}
	}

	public static void main(String[] args) {
		//new Transport(new File("tree/major.xlsx"), new File("tree/major.xml"));

	}
}
