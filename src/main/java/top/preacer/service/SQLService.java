package top.preacer.service;

import java.util.List;

import top.preacer.pojo.Result;
import top.preacer.pojo.SelectTable;

public interface SQLService {
	public SelectTable doSelect(String username,String sql,String dbName);
	public List<String> userDBs(String username);
	public List<String> userTables(String username, String dbname);
	public Result doSQLwhenReturnBool(String username,String sql,String dbName);
	public Result doSQLwhenGrantOrRevoke(String sql);
	public Result deleteUser(String sql);
	public Result createUser(String sql);
}
