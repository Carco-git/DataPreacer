package top.preacer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import top.preacer.database.User;
import top.preacer.pojo.Result;
import top.preacer.service.UserService;

@RestController
public class UserController {

	@Autowired
	private UserService userService;
	@RequestMapping(value="/login", method = RequestMethod.POST)
	public String login(String username,String password) {
		User user;
		Result ur=new Result();
		try {
			user = userService.LoginUser(username, password);
		}catch(Exception e) {
			e.printStackTrace();
			ur.setStatus(Result.FAILED);
			ur.setMsg("用户不存在");
			return JSON.toJSONString(ur);
		}
		
		if(user==null) {
			ur.setStatus(Result.FAILED);
			ur.setMsg("密码错误");
			return JSON.toJSONString(ur);
		}
		//logger.info(user+"登录成功");
		//ur.setToken(tokenService.generateToken(user));
		ur.setStatus(Result.SUCCESS);
		ur.setMsg("登录成功");
		return JSON.toJSONString(ur);
	}
	/*
	@RequestMapping(value="/test", method = RequestMethod.GET)
	public String adfa() {
		return  "111";
	}
	*/
	@RequestMapping(value="/createUser", method = RequestMethod.POST)
	public String createUser(String username,String password) {
		return userService.createUser(username, password);	
	}
	@RequestMapping(value="/deleteUser", method = RequestMethod.POST)
	public String deleteUser(String username) {
		return userService.deleteUser(username);	
	}
}
