package top.preacer.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import top.preacer.database.util.FileBackUpUtil;
import top.preacer.pojo.SelectTable;

public class test {

	public static void main(String[] args) throws Exception {
		SQLProject a= new SQLProject();
		a.setDbname("db1");
		a.setUsername("user1");
		a.setSql("select * from table2,table3");
		System.out.println(JSON.toJSONString(a));
//		String username="user1";
//		String dbname="db1";
//		String filename=dbname+"-"+new Date().getTime()+".zip";
//		FileBackUpUtil.compress("storage/"+username+"/"+dbname,"storage/"+username+"/"+filename);
	}

}
