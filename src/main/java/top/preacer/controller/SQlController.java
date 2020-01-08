package top.preacer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import top.preacer.service.SQLService;

@RestController
public class SQlController {
	@Autowired
	SQLService sqlService;
	
	@RequestMapping(value="/getUserTableNames", method = RequestMethod.POST)
	public String getUserTableNames(String username, String dbname) {
		List<String> tableNames=sqlService.userTables(username, dbname);
		return JSON.toJSONString(tableNames);
	}
	@RequestMapping(value="/getUserDBNames", method = RequestMethod.POST)
	public String getUserDBNames(String username, String dbname) {
		List<String> dbNames=sqlService.userDBs(username);
		return JSON.toJSONString(dbNames);
	}
	
	
	
	
}
