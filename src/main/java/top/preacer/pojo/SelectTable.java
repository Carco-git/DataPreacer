package top.preacer.pojo;

import java.util.Iterator;
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
	@Override
	public String toString() throws NullPointerException{
		
		int[] lengh = new int[columnName.size()];
        Iterator<String> dataNames = columnName.iterator();
        for (int i = 0; i < columnName.size(); i++) {
            String dataName = dataNames.next();
            lengh[i] = dataName.length();
            System.out.printf("|%s", dataName);
        }
        System.out.println("|");
        for (int ls : lengh) {
            for (int l = 0; l <= ls; l++) {
                System.out.printf("-");
            }
        }
        System.out.println("|");

        for (Map<String, String> line : rowList) {
            Iterator<String> valueIter = line.values().iterator();
            for (int i = 0; i < lengh.length; i++) {
                String value = valueIter.next();
                System.out.printf("|%s", value);
                for (int j = 0; j < lengh[i] - value.length(); j++) {
                    System.out.printf(" ");
                }
            }
            System.out.println("|");
        }
		return null;
        
	}
}
