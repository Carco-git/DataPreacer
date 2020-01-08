package top.preacer.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import top.preacer.database.Operating;
import top.preacer.database.User;
import top.preacer.pojo.Result;
import top.preacer.pojo.Table;
import top.preacer.service.SQLService;
import top.preacer.util.FileUtils;
import top.preacer.util.SQLHandle;
@Service
public class SQLServiceImpl implements SQLService{
    private static final Pattern PATTERN_INSERT = Pattern.compile("insert\\s+into\\s+(\\w+)(\\(((\\w+,?)+)\\))?\\s+\\w+\\((([^\\)]+,?)+)\\);?");
    private static final Pattern PATTERN_CREATE_TABLE = Pattern.compile("create\\stable\\s(\\w+)\\s?\\(((?:\\s?\\w+\\s\\w+,?)+)\\)\\s?;");
    private static final Pattern PATTERN_ALTER_TABLE_ADD = Pattern.compile("alter\\stable\\s(\\w+)\\sadd\\s(\\w+\\s\\w+)\\s?;");
    private static final Pattern PATTERN_DELETE = Pattern.compile("delete\\sfrom\\s(\\w+)(?:\\swhere\\s(\\w+\\s?[<=>]\\s?[^\\s\\;]+(?:\\sand\\s(?:\\w+)\\s?(?:[<=>])\\s?(?:[^\\s\\;]+))*))?\\s?;");
    private static final Pattern PATTERN_UPDATE = Pattern.compile("update\\s(\\w+)\\sset\\s(\\w+\\s?=\\s?[^,\\s]+(?:\\s?,\\s?\\w+\\s?=\\s?[^,\\s]+)*)(?:\\swhere\\s(\\w+\\s?[<=>]\\s?[^\\s\\;]+(?:\\sand\\s(?:\\w+)\\s?(?:[<=>])\\s?(?:[^\\s\\;]+))*))?\\s?;");
    private static final Pattern PATTERN_DROP_TABLE = Pattern.compile("drop\\stable\\s(\\w+);");
    private static final Pattern PATTERN_SELECT = Pattern.compile("select\\s(\\*|(?:(?:\\w+(?:\\.\\w+)?)+(?:\\s?,\\s?\\w+(?:\\.\\w+)?)*))\\sfrom\\s(\\w+(?:\\s?,\\s?\\w+)*)(?:\\swhere\\s([^\\;]+\\s?;))?");
    private static final Pattern PATTERN_DELETE_INDEX = Pattern.compile("delete\\sindex\\s(\\w+)\\s?;");
    private static final Pattern PATTERN_GRANT_ADMIN = Pattern.compile("grant\\sadmin\\sto\\s([^;\\s]+)\\s?;");
    private static final Pattern PATTERN_REVOKE_ADMIN = Pattern.compile("revoke\\sadmin\\sfrom\\s([^;\\s]+)\\s?;");

	@Override
	public Table doSelect(String sql) {
		sql=SQLHandle.canonicalSQL(sql);
		Matcher matcherSelect = PATTERN_SELECT.matcher(sql);
		if(matcherSelect.find()) {
			Operating.select(matcherSelect);
		}
		return null;
	}

	@Override
	public List<String> userTables(String username,String dbname) {
		String path="dir" + "/" + username + "/" + dbname + "/";
		return FileUtils.getDirectory(path);
	}

	@Override
	public List<String> userDBs(String username) {
		String path="dir" + "/" + username + "/" ;
		return FileUtils.getDirectory(path);
	}

	@Override
	public Result doSQLwhenReturnBool(String username,String sql,String dbName) {
		Result result=new Result();
		User user = User.getUser(username);
		top.preacer.database.Table.init(username, dbName);
		long startMili=System.currentTimeMillis();// 当前时间对应的毫秒数
		sql=SQLHandle.canonicalSQL(sql);
		Matcher matcherDelete = PATTERN_DELETE.matcher(sql);
		Matcher matcherInsert = PATTERN_INSERT.matcher(sql);
		Matcher matcherDropTable = PATTERN_DROP_TABLE.matcher(sql);
		Matcher matcherDeleteIndex = PATTERN_DELETE_INDEX.matcher(sql);
		Matcher matcherUpdate = PATTERN_UPDATE.matcher(sql);
		Matcher matcherCreateTable = PATTERN_CREATE_TABLE.matcher(sql);
		Matcher matcherAlterTable_add = PATTERN_ALTER_TABLE_ADD.matcher(sql);
		//执行修改表的操作
		while (matcherAlterTable_add.find()) {
            if (user.getLevel() != User.ADMIN) {
            	result.setMsg("权限不足");
            	result.setStatus(Result.FAILED);
            }
            String get=Operating.alterTableAdd(matcherAlterTable_add);
            if(get.contains("未找到表")) {
            	result.setMsg(get);
            	result.setStatus(Result.FAILED);
            }else if(get.contains("存在重复添加的")){
            	result.setMsg(get);
            	result.setStatus(Result.FAILED);
            }else if(get.contains("success")) {
            	result.setMsg("OK");
            	result.setStatus(Result.SUCCESS);
            }
            result.setSpendTime((System.currentTimeMillis()-startMili)/10.0);
            return result;
        }
		//执行创建表的操作
		while (matcherCreateTable.find()) {
            if (user.getLevel() != User.ADMIN) {
            	result.setMsg("权限不足");
            	result.setStatus(Result.FAILED);
            }else{
            	String get=Operating.createTable(matcherCreateTable);
            	
            	if(get.contains("已经存在表")) {
                	result.setMsg(get);
                	result.setStatus(Result.FAILED);
                }else if(get.contains("success")) {
                	result.setMsg("OK");
                	result.setStatus(Result.SUCCESS);
                }
            }
            result.setSpendTime((System.currentTimeMillis()-startMili)/10.0);
            return result;    
        }
		//执行删除表的操作
		while (matcherDropTable.find()) {
			 if (user.getLevel() != User.ADMIN) {
	            	result.setMsg("权限不足");
	            	result.setStatus(Result.FAILED);
	          }else{
	            	String get=Operating.dropTable(matcherDropTable);
	            	if(get.contains("不存在表")) {
	                	result.setMsg(get);
	                	result.setStatus(Result.FAILED);
	                }else if(get.contains("success")) {
	                	result.setMsg("OK");
	                	result.setStatus(Result.SUCCESS);
	                }
	           }
			 result.setSpendTime((System.currentTimeMillis()-startMili)/10.0);
	         return result;    
        }
		//执行插入表的操作
		while (matcherInsert.find()) {
			 if (user.getLevel() != User.ADMIN||!user.isCanInsert()) {
				result.setMsg("权限不足");
	            result.setStatus(Result.FAILED);
			 }else {
				 String get=Operating.insert(matcherInsert);
			 }
		 }
		 while (matcherUpdate.find()) {
			 if (user.getLevel() != User.ADMIN||!user.isCanUpdate()) {
				result.setMsg("权限不足");
	            result.setStatus(Result.FAILED);
			 }else {
				
			 }
		 }
		
		result.setMsg("存在语法错误");
        result.setStatus(Result.FAILED);
		return result;
	}

}
