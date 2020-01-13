package top.preacer.service;

import javax.servlet.http.HttpServletResponse;

import top.preacer.database.User;
import top.preacer.pojo.Result;

public interface UserService {
	User LoginUser(String name, String password) throws Exception;
	User getUser(String name);
	String createUser(String name,String password);
	String deleteUser(String name);
	String backUp(String username, String dbname, HttpServletResponse response);
	Result createDatabase(String username,String dbname);
}
