package top.preacer.pojo;

import java.util.List;
import java.util.Map;

public class SelectTable {
	
	public List<String> getColumnName() {
		return columnName;
	}
	public void setColumnName(List<String> columnName) {
		this.columnName = columnName;
	}
	public List<Map<String, String>> getRowList() {
		return rowList;
	}
	public void setRowList(List<Map<String, String>> rowList) {
		this.rowList = rowList;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	private List<String> columnName;
	private List<Map<String, String>> rowList;
	private int row;
	private int col;
}
