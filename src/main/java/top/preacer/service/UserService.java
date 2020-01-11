package top.preacer.service;

import top.preacer.database.User;

public interface UserService {
	User LoginUser(String name, String password) throws Exception;
	User getUser(String name);
	String createUser(String name,String password);
	String deleteUser(String name);
}
