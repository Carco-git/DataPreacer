package top.preacer.service;

import java.util.List;

import top.preacer.pojo.Result;
import top.preacer.pojo.Table;

public interface SQLService {
	public Table doSelect(String sql);
	public List<String> userDBs(String username);
	public List<String> userTables(String username, String dbname);
	public Result doSQLwhenReturnBool(String username,String sql,String dbName);
}
