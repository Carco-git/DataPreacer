package top.preacer.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import top.preacer.database.User;
import top.preacer.pojo.Result;
import top.preacer.service.UserService;
@CrossOrigin
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
	

	@RequestMapping(value="/backUp", method = RequestMethod.GET)    
	public String testDownload(HttpServletRequest request, HttpServletResponse response)throws Exception{
		String username=request.getParameter("username");
		String dbname=request.getParameter("dbname");
		userService.backUp(username,dbname,response);
		return "thanks";
		}
	@RequestMapping(value="/createDB", method = RequestMethod.GET) 
	public String cdb(String username, String dbname){
		return JSON.toJSONString(userService.createDatabase(username, dbname));
		
	}
	
}
