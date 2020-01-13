package top.preacer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import top.preacer.pojo.Result;
import top.preacer.pojo.SQLProject;
import top.preacer.pojo.SelectTable;
import top.preacer.pojo.UsernameForSer;
import top.preacer.service.SQLService;
import top.preacer.util.SQLHandle;
@CrossOrigin
@RestController
public class SQlController {
	@Autowired
	SQLService sqlService;
	@Autowired
    private HttpServletRequest request;
	@RequestMapping(value="/getUserTableNames", method = RequestMethod.POST)
	public String getUserTableNames(@RequestBody JSONObject url) {
		System.out.println("一个请求");
		System.out.println(request.getParameterMap());
		List<String> tableNames=sqlService.userTables(url.getString("username"), url.getString("dbname"));
		return JSON.toJSONString(tableNames);
	}
	@RequestMapping(value="/getUserDBNames", method = RequestMethod.POST)
	public String getUserDBNames(@RequestBody UsernameForSer unf) {
		System.out.println("一个请求");
		System.out.println(unf.getUsername());
		
		List<String> dbNames=sqlService.userDBs(unf.getUsername());
		
		return JSON.toJSONString(dbNames);
	}
	
	@RequestMapping(value="/doSQL",method=RequestMethod.POST)
	public String doSQL(@RequestBody SQLProject sp) {
		
		String username=sp.getUsername();
		String sql=sp.getSql();
		String dbname=sp.getDbname();
		
		sql=SQLHandle.canonicalSQL(sql);
		String command =  sql.split(" ")[0].trim().toLowerCase();
		Result returnResult=null;
		switch(command) {
		case "select":
			SelectTable select=sqlService.doSelect(username, sql, dbname);
			if(select==null)return JSON.toJSONString(new Result().setMsg("找不到表").setStatus(Result.FAILED));
			return JSON.toJSONString(select);
		case "grant":returnResult=sqlService.doSQLwhenGrantOrRevoke(sql);
			break;
		case "revoke":returnResult=sqlService.doSQLwhenGrantOrRevoke(sql);
			break;
		case "adduser":returnResult=sqlService.createUser(sql);
			break;
		case "deleteuser":returnResult=sqlService.deleteUser(sql);
			break;
		default:
			returnResult=sqlService.doSQLwhenReturnBool(username, sql, dbname);
			return JSON.toJSONString(returnResult);
		}
			
		return JSON.toJSONString(returnResult);
		
	}
	
	
//	@RequestMapping(value="/do",method=RequestMethod.GET)
//	public String doa() {
//		User user=new User();
//		user.setName("user1");
//		user.setPassword("abc");
//		user.role=user.ADMIN;
//		File file = new File("storage/"+user.getName(), "user.pojo");
//		File filedec = new File("storage/"+user.getName());
//		if(!filedec.exists()) {
//			filedec.mkdir();
//		}
//		System.out.println(file.getAbsolutePath());
//        try (
//                FileOutputStream fos = new FileOutputStream(file);
//                ObjectOutputStream oos = new ObjectOutputStream(fos)
//        ) {
//            oos.writeObject(user);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return "OK";
//	}
	
	
}
