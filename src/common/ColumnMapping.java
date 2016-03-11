package common;

public class ColumnMapping implements Comparable<ColumnMapping>{

	private String sourcePath;
	private int sourceColumn;
	private int plateColumn;
	private String plateSheet;
	private String plateColType;
	
	public ColumnMapping( String path, int sourceColumn, int plateColumn, String plateSheet) {
		this( path, sourceColumn, plateColumn, plateSheet, "" );
	}
	public ColumnMapping( String path, int sourceColumn, int plateColumn, String plateSheet, String plateColType ) {
		this.sourcePath = path;
		this.sourceColumn = sourceColumn;
		this.plateColumn = plateColumn;
		this.plateSheet = plateSheet;
		this.plateColType = plateColType;
	}
	
	public int getSourceColumn() {
		return sourceColumn;
	}
	public int getPlateColumn() {
		return plateColumn;
	}
	public String getPlateSheet() {
		return plateSheet;
	}
	
	public void setPlateSheet( String sheetName ) {
		this.plateSheet = sheetName;
	}
	
	public String getSourcePath() {
		return sourcePath;
	}
	
	
	public String getPlateColType() {
		return plateColType;
	}
	public void setPlateColType(String plateColType) {
		this.plateColType = plateColType;
	}
	@Override
	public int compareTo( ColumnMapping compare ) {
		return plateSheet.compareTo(compare.getPlateSheet());
	}
}
