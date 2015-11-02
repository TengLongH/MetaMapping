package common;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class MyDefaultTableModel extends DefaultTableModel {
	private static final long serialVersionUID = 1L;

	public MyDefaultTableModel(Vector data, Vector columnNames) {
		super(data, columnNames);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return (column != 0);
	}
	
}
