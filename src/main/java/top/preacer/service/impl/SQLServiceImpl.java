package top.preacer.service.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import top.preacer.database.SQLimpl;
import top.preacer.database.User;
import top.preacer.pojo.Result;
import top.preacer.pojo.SelectTable;
import top.preacer.service.SQLService;
import top.preacer.util.FileUtils;
import top.preacer.util.SQLHandle;
@Service
public class SQLServiceImpl implements SQLService{
	 private static final Pattern SQL_INSERT = Pattern.compile("insert\\s+into\\s+(\\w+)(\\(((\\w+,?)+)\\))?\\s+\\w+\\((([^\\)]+,?)+)\\);?");
	 private static final Pattern SQL_CREATE_TABLE = Pattern.compile("create\\stable\\s(\\w+)\\s?\\(((?:\\s?\\w+\\s\\w+,?)+)\\)\\s?;?");
	 private static final Pattern SQL_ALTER_TABLE_ADD = Pattern.compile("alter\\stable\\s(\\w+)\\sadd\\s(\\w+\\s\\w+)\\s?;?");
	 private static final Pattern SQL_DELETE = Pattern.compile("delete\\sfrom\\s(\\w+)(?:\\swhere\\s(\\w+\\s?[<=>]\\s?[^\\s\\;]+(?:\\sand\\s(?:\\w+)\\s?(?:[<=>])\\s?(?:[^\\s\\;]+))*))?\\s?;?\\s?(l?i?m?i?t?\\s?(\\d?))");
	 private static final Pattern SQL_UPDATE = Pattern.compile("update\\s(\\w+)\\sset\\s(\\w+\\s?=\\s?[^,\\s]+(?:\\s?,\\s?\\w+\\s?=\\s?[^,\\s]+)*)(?:\\swhere\\s(\\w+\\s?[<=>]\\s?[^\\s\\;]+(?:\\sand\\s(?:\\w+)\\s?(?:[<=>])\\s?(?:[^\\s\\;]+))*))?\\s?;?");
	 private static final Pattern SQL_DROP_TABLE = Pattern.compile("drop\\stable\\s(\\w+);?");
	 private static final Pattern SQL_SELECT = Pattern.compile("select\\s(\\*|(?:(?:\\w+(?:\\.\\w+)?)+(?:\\s?,\\s?\\w+(?:\\.\\w+)?)*))\\sfrom\\s(\\w+(?:\\s?,\\s?\\w+)*)(?:\\swhere\\s([^\\;]+\\s?;))?");
	 private static final Pattern SQL_DELETE_INDEX = Pattern.compile("delete\\sindex\\s(\\w+)\\s?;?");
	 private static final Pattern SQL_GRANT_ADMIN = Pattern.compile("grant\\sadmin\\sto\\s([^;\\s]+)\\s?;?");
	 private static final Pattern SQL_GRANT_INSERT = Pattern.compile("grant\\sinsert\\sto\\s([^;\\s]+)\\s?;?");
	 private static final Pattern SQL_GRANT_DELETE = Pattern.compile("grant\\sdelete\\sto\\s([^;\\s]+)\\s?;?");
	 private static final Pattern SQL_GRANT_UPDATE = Pattern.compile("grant\\supdate\\sto\\s([^;\\s]+)\\s?;?");
	 private static final Pattern SQL_REVOKE_ADMIN = Pattern.compile("revoke\\sadmin\\sfrom\\s([^;\\s]+)\\s?;?");
	 private static final Pattern SQL_REVOKE_DELETE = Pattern.compile("revoke\\sdelete\\sfrom\\s([^;\\s]+)\\s?;?");
	 private static final Pattern SQL_REVOKE_INSERT = Pattern.compile("revoke\\sinsert\\sfrom\\s([^;\\s]+)\\s?;?");
	 private static final Pattern SQL_REVOKE_UPDATE = Pattern.compile("revoke\\supdate\\sfrom\\s([^;\\s]+)\\s?;?");
	 private static final Pattern SQL_ADDUSER=Pattern.compile("adduser\\s+(\\w+),(\\w+)?;?");
	 private static final Pattern SQL_DELETEUSER = Pattern.compile("deleteuser\\s(\\w+)?;?");

	@Override
	public SelectTable doSelect(String username,String sql,String dbName) {
		top.preacer.database.Table.init(username, dbName);
		SelectTable result=null;
		sql=SQLHandle.canonicalSQL(sql);
		System.out.println(sql);
		
		Matcher matcherSelect = SQL_SELECT.matcher(sql);
		if(matcherSelect.find()) {
			result=SQLimpl.Select(matcherSelect);
		}else {
			return null;
		}
			
		
		return result;
	}

	@Override
	public List<String> userTables(String username,String dbname) {
		String path="storage" + "/" + username + "/" + dbname + "/";
		return FileUtils.getDirectory(path);
	}

	@Override
	public List<String> userDBs(String username) {
		String path="storage" + "/" + username + "/" ;
		return FileUtils.getDirectory(path);
	}

	@Override
	public Result doSQLwhenReturnBool(String username,String sql,String dbName) {
		Result result=new Result();
		User user = User.getUser(username);
		top.preacer.database.Table.init(username, dbName);
		long startMili=System.currentTimeMillis();// 当前时间对应的毫秒数
		sql=SQLHandle.canonicalSQL(sql);
		Matcher matcherDelete = SQL_DELETE.matcher(sql);
		Matcher matcherInsert = SQL_INSERT.matcher(sql);
		Matcher matcherDropTable = SQL_DROP_TABLE.matcher(sql);
		Matcher matcherDeleteIndex = SQL_DELETE_INDEX.matcher(sql);
		Matcher matcherUpdate = SQL_UPDATE.matcher(sql);
		Matcher matcherCreateTable = SQL_CREATE_TABLE.matcher(sql);
		Matcher matcherAlterTable_add = SQL_ALTER_TABLE_ADD.matcher(sql);

		
		//执行修改表的操作
		while (matcherAlterTable_add.find()) {
            if (user.getRole() != User.ADMIN) {
            	result.lackOfAuthority();
            }
            String get=SQLimpl.alterTableAdd(matcherAlterTable_add);
            if(get.contains("未找到表")) {
            	result.setMsg(get);
            	result.setStatus(Result.FAILED);
            }else if(get.contains("存在重复添加的")){
            	result.setMsg(get);
            	result.setStatus(Result.FAILED);
            }else if(get.contains("success")) {
            	result.wellDone().setMsg("修改表成功");
            	
            }
            result.setSpendTime((System.currentTimeMillis()-startMili)/10.0);
            return result;
        }
		
		//执行创建表的操作
		while (matcherCreateTable.find()) {
            if (user.getRole() != User.ADMIN) {
            	result.lackOfAuthority();
            }else{
            	String get=SQLimpl.createTable(matcherCreateTable);
            	
            	if(get.contains("已经存在表")) {
                	result.setMsg(get);
                	result.setStatus(Result.FAILED);
                }else if(get.contains("success")) {
                	result.wellDone().setMsg("创建成功");
                }
            }
            result.setSpendTime((System.currentTimeMillis()-startMili)/10.0);
            return result;    
        }
		//执行删除表的操作
		while (matcherDropTable.find()) {
			 if (user.getRole() != User.ADMIN) {
				 result.lackOfAuthority();
	          }else{
	            	String get=SQLimpl.dropTable(matcherDropTable);
	            	if(get.contains("不存在表")) {
	                	result.setMsg(get);
	                	result.setStatus(Result.FAILED);
	                }else if(get.contains("success")) {
	                	result.wellDone().setMsg("删除成功");
	                }
	           }
			 result.setSpendTime((System.currentTimeMillis()-startMili)/10.0);
	         return result;    
        }
		System.out.println(sql);
//		System.out.println(matcherInsert.find());
//		//执行插入表的操作
//		System.out.println(matcherInsert.find());
		if (matcherInsert.find()) {
			System.out.println("aa");
			 if (user.getRole() != User.ADMIN&&!user.isCanInsert()) {

				 result.lackOfAuthority();
			 }else {
				 String get=SQLimpl.insert(matcherInsert);
				 System.out.println(get);
				 if(get.contains("success")) {
					 result.wellDone().setMsg("插入成功");
				 }else {
					result.setMsg(get);
	                result.setStatus(Result.FAILED);
				 }
			 }
		 }
		//执行更新表的操作
		 while (matcherUpdate.find()) {
			 if (user.getRole() != User.ADMIN&&!user.isCanUpdate()) {
				 result.lackOfAuthority();
			 }else {
				 String get=SQLimpl.update(matcherUpdate);
				 if(get.contains("success")) {
					 result.wellDone().setMsg("更新成功");
					}else {
						result.setMsg(get);
		                result.setStatus(Result.FAILED);
					 }
			 }
		 }
		 while (matcherDelete.find()) {
             if (user.getRole() != User.ADMIN&&!user.isCanDelete()) {
            	 result.lackOfAuthority();
             }else {
				 String get=SQLimpl.delete(matcherDelete);;
				 if(get.contains("success")) {
					 result.wellDone().setMsg("删除成功");
				 }else {
					result.setMsg(get);
	                result.setStatus(Result.FAILED);
				 }
			 }
             
         }
		 while (matcherDeleteIndex.find()) {
             if (user.getRole() != User.ADMIN) {
            	 result.lackOfAuthority();
             }else {
            	 String get=SQLimpl.deleteIndex(matcherDeleteIndex);
				 if(get.contains("success")) {
					 result.wellDone().setMsg("删除索引成功");
				 }else {
					result.setMsg(get);
	                result.setStatus(Result.FAILED);
				 }
			 }
             
         }

		return result;
	}

	@Override
	public Result doSQLwhenGrantOrRevoke(String sql) {
		Result result=new Result();
		sql=SQLHandle.canonicalSQL(sql);
		Matcher matcherGrantAdmin = SQL_GRANT_ADMIN.matcher(sql);
		Matcher matcherGrantInsert= SQL_GRANT_INSERT.matcher(sql);
		Matcher matcherGrantDelete = SQL_GRANT_DELETE.matcher(sql);
		Matcher matcherGrantUpdate= SQL_GRANT_UPDATE.matcher(sql);
		Matcher matcherRevokeAdmin = SQL_REVOKE_ADMIN.matcher(sql);
		Matcher matcherRevokeInsert= SQL_REVOKE_INSERT.matcher(sql);
		Matcher matcherRevokeDelete = SQL_REVOKE_DELETE.matcher(sql);
		Matcher matcherRevokeUpdate= SQL_REVOKE_UPDATE.matcher(sql);
		
		while (matcherGrantAdmin.find()) {
			String get=SQLimpl.grantAdmin(matcherGrantAdmin);
				 if(get.contains("success")) {
					 result.wellDone().setMsg("授权成功");
				 }else {
					result.setMsg(get);
	                result.setStatus(Result.FAILED);
				 }
        }
        while (matcherGrantDelete.find()) {
        	String get=SQLimpl.grantDelete(matcherGrantDelete);
			 if(get.contains("success")) {
				 result.wellDone().setMsg("授权成功");
			 }else {
				result.setMsg(get);
               result.setStatus(Result.FAILED);
			 }
        }
        while (matcherGrantInsert.find()) {
        	String get=SQLimpl.grantInsert(matcherGrantInsert);
			 if(get.contains("success")) {
				 result.wellDone().setMsg("授权成功");
			 }else {
				result.setMsg(get);
               result.setStatus(Result.FAILED);
			 }
        }
        while (matcherGrantUpdate.find()) {
        	String get=SQLimpl.grantUpdate(matcherGrantUpdate);
			 if(get.contains("success")) {
				 result.wellDone().setMsg("授权成功");
			 }else {
				result.setMsg(get);
               result.setStatus(Result.FAILED);
			 }
        }
        while (matcherRevokeAdmin.find()) {
        	String get=SQLimpl.revokeAdmin(matcherRevokeAdmin);
			 if(get.contains("success")) {
				 result.wellDone().setMsg("撤销权限成功");
			 }else {
				result.setMsg(get);
               result.setStatus(Result.FAILED);
			 }
        }
        while (matcherRevokeDelete.find()) {
        	String get=SQLimpl.revokeDelete(matcherRevokeDelete);
			 if(get.contains("success")) {
				 result.wellDone().setMsg("撤销权限成功");
			 }else {
				result.setMsg(get);
               result.setStatus(Result.FAILED);
			 }
        }
        while (matcherRevokeInsert.find()) {
        	String get=SQLimpl.revokeInsert(matcherRevokeInsert);
			 if(get.contains("success")) {
				 result.wellDone().setMsg("撤销权限成功");
			 }else {
				result.setMsg(get);
               result.setStatus(Result.FAILED);
			 }
        }
        while (matcherRevokeUpdate.find()) {
        	String get=SQLimpl.revokeUpdate(matcherRevokeUpdate);
			 if(get.contains("success")) {
				 result.wellDone().setMsg("撤销权限成功");
			 }else {
				result.setMsg(get);
               result.setStatus(Result.FAILED);
			 }
        }
        return result;
	}

	@Override
	public Result deleteUser(String sql) {
		Result result=new Result();
		sql=SQLHandle.canonicalSQL(sql);
        Matcher matcherDeleteuser = SQL_DELETEUSER.matcher(sql);
        
        while(matcherDeleteuser.find())
        {
        	String get=SQLimpl.deleteuser(matcherDeleteuser);
			 if(get.contains("success")) {
				 result.wellDone().setMsg("删除用户成功");
			 }else {
				result.setMsg(get);
              result.setStatus(Result.FAILED);
			 }
        }
		return result;
	}

	@Override
	public Result createUser(String sql) {
		Result result=new Result();
		sql=SQLHandle.canonicalSQL(sql);
        Matcher matcherAdduser = SQL_ADDUSER.matcher(sql);
        while(matcherAdduser.find())
        {
        	String get=SQLimpl.adduser(matcherAdduser);
			 if(get.contains("success")) {
				 result.wellDone().setMsg("创建用户成功");
			 }else {
				result.setMsg(get);
             result.setStatus(Result.FAILED);
			 }
        }
		return result;
	}

}
