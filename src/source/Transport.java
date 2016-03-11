package source;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import common.ColumnMapping;
import utils.Lang;
import utils.Template;

public class Transport {

	private Workbook sourceBook;
	private Workbook templateBook;
	private Element sourceRoot;

	private List<List<ColumnMapping>> maps = new ArrayList<List<ColumnMapping>>();
	private List<ColumnMapping> testInfo = new ArrayList<ColumnMapping>();
	private List<ColumnMapping> descript = new ArrayList<ColumnMapping>();

	public Transport(File source, File descript) {
		try {
			String name = "Transport.xls";
			name += source.getName().endsWith("xls") ? "" : "x";
			Path temp = Paths.get("tree", "sys", name);
			if (Files.exists(temp))
				Files.delete(temp);
			Files.copy(new FileInputStream(source), temp);
			Path plate = Paths.get("tree", "sys", "template.xlsx");
			Path result = Paths.get("tree", "sys", "result.xlsx");
			if (Files.exists(result))
				Files.delete(result);
			Files.copy(plate, result);

			templateBook = WorkbookFactory.create(result.toFile());
			sourceBook = WorkbookFactory.create(temp.toFile());
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			sourceRoot = builder.parse(descript).getDocumentElement();
			transformBook();
			FileOutputStream fos = new FileOutputStream(new File("result.xlsx"));
			templateBook.write(fos);
			templateBook.close();
			fos.close();
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

		int startRow = 0;
		try {
			// 解析该表和模板文件的对应关系
			parseSheetMapping(xmlSheet);
			startRow = Integer.parseInt(xmlSheet.getAttribute("data"));
			for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (isEmpty(row))
					continue;
				transformRow(row);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.printf("Sheet:%s row:%d", sheet.getSheetName(), startRow);
		}
	}

	private void transformRow(Row row) {

		boolean extra = false;
		for (List<ColumnMapping> map : maps) {
			String sheetName = map.get(0).getPlateSheet();
			extra = sheetName.equals("ExtraSheet");
			Sheet psheet = templateBook.getSheet(sheetName);
			Row prow = psheet.createRow(psheet.getLastRowNum() + 1);
			for (ColumnMapping cm : map) {
				Cell src = row.getCell(cm.getSourceColumn());
				Cell des = prow.getCell(cm.getPlateColumn(), Row.CREATE_NULL_AS_BLANK);
				copyCell(src, des);
				if( extra ){
					Cell cell = prow.createCell(Template.getExtraPath());
					cell.setCellValue( cm.getSourcePath() );
				}
			}
		}
	}

	private void parseSheetMapping(Element xmlSheet) {

		maps.clear();
		testInfo.clear();
		descript.clear();
		List<ColumnMapping> data = new ArrayList<ColumnMapping>();

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
				if (null == mapping || mapping.trim().equals(""))
					continue;

				mapping = mapping.substring(2).trim();
				// 获取映射节点的路径
				path = new ArrayList<String>(Arrays.asList(mapping.split(">")));
				path.remove(0);
				sheetName = path.get(0);

				// -------------------------------------
				// String strColumn = Template.get(path.toArray(), "colum");
				Element element = Template.getElement(path.toArray());
				String strColumn = element.getAttribute("colum");
				String type = element.getAttribute("type");

				// -----------------------------------------------------
				columnIndex = Integer.parseInt(strColumn);

				// 源数据文件中该列的路径，添加进Extra表时会用到
				String srcPath = utils.Utils.elementPathToString(column);

				ColumnMapping ncm = new ColumnMapping(srcPath, srcIndex, columnIndex, sheetName, type);
				if (ncm.getPlateSheet().equals("Description")) {
					descript.add(ncm);
					if( ncm.getPlateColumn() == Template.getSampleNo() ){
						
						ColumnMapping tcm = new ColumnMapping(
												ncm.getSourcePath(),
												ncm.getSourceColumn(),
												0, "");
						
						testInfo.add( tcm );
					}
					continue;
				}
				
				if (type.equals("TestInfo")) {
					testInfo.add(ncm);
					continue;
				}
				data.add(ncm);
			}
			// 按照对应列所属的模板表名进行分类
			Collections.sort(data);

			List<ColumnMapping> temp = new ArrayList<ColumnMapping>();
			String pre = data.get(0).getPlateSheet();
			String cur = pre;
			for (ColumnMapping cm : data) {
				cur = cm.getPlateSheet();
				if (!cur.equals(pre)) {

					List<ColumnMapping> info = new ArrayList<ColumnMapping>(testInfo);
					Collections.copy(info, testInfo);
					for (int i = 0; i < info.size(); i++) {
						ColumnMapping infocm = info.get(i);
						infocm.setPlateSheet(pre);
						temp.addAll(info);
					}
					maps.add(temp);
					temp = new ArrayList<ColumnMapping>();
					pre = cur;

				}
				temp.add(cm);
			}

			List<ColumnMapping> info = new ArrayList<ColumnMapping>(testInfo);
			Collections.copy(info, testInfo);
			for (int i = 0; i < info.size(); i++) {
				ColumnMapping infocm = info.get(i);
				infocm.setPlateSheet(pre);
			}

			temp.addAll(info);

			maps.add(temp);
			maps.add(descript);
			
		} catch (Exception e) {
			System.err.printf("%s %s %d", mapping, path, srcIndex);
			e.printStackTrace();
		}

	}
	private boolean isEmpty(Row row) {
		if (null == row)
			return true;
		int column = 0, station = -1, type = -1;

		for (ColumnMapping cm : descript) {
			column = cm.getPlateColumn();
			if (column == Template.getStationNo()) {
				station = column;
			}
			if (column == Template.getSampleType()) {
				type = column;
			}
		}
		if (station < 0 || type < 0)
			return true;

		Cell cell = row.getCell(station);
		if (isEmpty(cell))
			return true;
		cell = row.getCell(type);
		if (isEmpty(cell))
			return true;
		return false;
	}

	private boolean isEmpty(Cell cell) {
		if (null == cell)
			return true;
		if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
			if (cell.getStringCellValue().trim().equals("")) {
				return true;
			}
		}
		return false;
	}

	private void copyCell(Cell cell, Cell desCell) {
		if( null == cell || null == desCell )return;
		
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
			System.out.printf( "%s:(%d,%d)", 
					desCell.getSheet().getSheetName(), 
					desCell.getRowIndex(), 
					desCell.getColumnIndex());
			e.printStackTrace();
		}
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
		new Transport(new File("tree/commit.xls"), new File("tree/commit.xml"));

	}
}
