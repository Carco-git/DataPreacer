package top.preacer.pojo;

import java.util.List;

public class Table {
	public List<String> getColumnName() {
		return columnName;
	}
	public void setColumnName(List<String> columnName) {
		this.columnName = columnName;
	}
	public List<String> getColumnProp() {
		return columnProp;
	}
	public void setColumnProp(List<String> columnProp) {
		this.columnProp = columnProp;
	}
	public List<List<String>> getRowList() {
		return rowList;
	}
	public void setRowList(List<List<String>> rowList) {
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
	private List<String> columnProp;
	private List<List<String>> rowList;
	private int row;
	private int col;
}
