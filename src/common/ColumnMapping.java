package common;

public class ColumnMapping implements Comparable<ColumnMapping>{

	private String sourcePath;
	private int sourceColumn;
	private int plateColumn;
	private String plateSheet;
	public ColumnMapping( String path, int sourceColumn, int plateColumn, String plateSheet) {
		this.sourcePath = path;
		this.sourceColumn = sourceColumn;
		this.plateColumn = plateColumn;
		this.plateSheet = plateSheet;
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
	
	public String getSourcePath() {
		return sourcePath;
	}
	@Override
	public int compareTo( ColumnMapping compare ) {
		return plateSheet.compareTo(compare.getPlateSheet());
	}
}
