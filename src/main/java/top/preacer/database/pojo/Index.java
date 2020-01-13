
package top.preacer.database.pojo;
import java.io.Serializable;

public class Index implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String filePath;
    private int lineNum;

    public Index(String filePath, int lineNum) {
        this.filePath = filePath;
        this.lineNum = lineNum;
    }

    public String getFilePath() {
        return filePath;
    }


    public int getLineNum() {
        return lineNum;
    }

}
