package top.preacer.database.pojo;
//域文件用来规定属性名字 属性种类 属性是否为主键 用于写入数据字典
public class Field {
    private String name;
    private String type;
    private boolean primaryKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }
}
