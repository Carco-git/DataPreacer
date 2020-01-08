package top.preacer.service;

import java.io.File;

import top.preacer.database.User;

public interface UserService {
	User LoginUser(String name, String password) throws Exception;
	User getUser(String name);
}
