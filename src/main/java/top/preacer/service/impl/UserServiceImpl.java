package top.preacer.service.impl;
import java.io.*;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import top.preacer.database.User;
import top.preacer.database.util.FileBackUpUtil;
import top.preacer.pojo.Result;
import top.preacer.service.UserService;
@Service
public class UserServiceImpl implements UserService {
	public User LoginUser(String name, String password) throws Exception {
        User user = null;
        File file = new File("storage/"+name, "user.pojo");
        if (!file.exists()) {
            System.out.println("用户不存在");
            throw new Exception("user not exist");
        }
        
        try (
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
        ) {
            user = (top.preacer.database.User) ois.readObject();
            if (null == user) {
                System.out.println("用户不存在");
                throw new Exception("user not exist");
            } else if (!password.equals(user.getPassword())) {
                //如果密码不正确，返回null
                user = null;
                System.out.println("密码错误");
            }else 
            {System.out.println("登录成功");}
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return user;
    }

	@Override
	public User getUser(String name) { 
		User user = null;
    File file = new File("storage/"+name, "user.pojo");
    if (!file.exists()) {
        System.out.println("用户不存在");
        return null;
    }
    try (
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis)
    ) {
        user = (top.preacer.database.User) ois.readObject();
        if (null == user) {
            System.out.println("此用户不存在");
        }
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    }
    return user;
	}

	@Override
	public String createUser(String name, String password) {
		User user = new User(name,password);
    	user.setRole(user.NORMAL);
		File filefolder=new File("storage/"+user.getName());
		if(!filefolder.exists()){//如果文件夹不存在
			filefolder.mkdir();//创建文件夹
		    System.out.println("已添加"+user.getName()+"用户");
		}
		else
		{
			System.out.println("用户已存在");
			return "用户已存在";
		}
    	File file = new File("storage/"+user.getName(), "user.pojo");
        try (
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(user);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "FileNotFoundException";
        } catch (IOException e) {
            e.printStackTrace();
            return "IOException";
        }
        return "OK";
		
	}

	@Override
	public String deleteUser(String name) {
		User user = User.getUser(name);
		File filefolder=new File("storage/"+user.getName());
		if(!filefolder.exists()){//如果文件夹不存在	
	     System.out.println("用户不存在");
		 return "用户不存在";
		}
		else
		{
		 delDir("storage/"+user.getName());//删除文件夹
		 System.out.println("已删除"+user.getName()+"用户");
		 return "已删除"+user.getName()+"用户";
		}
		
	}
	public void delDir(String path){
	    File dir=new File(path);
	    if(dir.exists()){
	        File[] tmp=dir.listFiles();
	        for(int i=0;i<tmp.length;i++){
	            if(tmp[i].isDirectory()){
	                delDir(path+"/"+tmp[i].getName());
	            }
	            else{
	            	tmp[i].delete();
	            }
	        }
	        dir.delete();
	    }
	}

	@Override
	public String backUp(String username, String dbname,HttpServletResponse response) {
		Date time = new Date();
		File filefolder=new File("storage/"+username+"/"+dbname);
		if(filefolder.exists()) {
			String filename=dbname+"-"+time.getTime()+".zip";
			try {
				FileBackUpUtil.compress("storage/"+username+"/"+dbname,"storage/"+username+"/"+filename);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
				 response.setContentType("application/force-download");// 设置强制下载不打开            
	             response.addHeader("Content-Disposition", "attachment;fileName=" + filename);
	             FileInputStream fis = null;
	             
	             File file = new File("storage/"+username+"/"+filename);
	             
	             try {
	                 fis = new FileInputStream("storage/"+username+"/"+filename);
//	            	 fis = new FileInputStream("F:\\workspace-sts-3.9.9.RELEASE\\drugpreacer.rar");
	            	 ServletOutputStream outputStream = response.getOutputStream();
	                 byte[] buffer =new byte[1024];
	                 int b=0;
	                 while((b=fis.read(buffer))!= -1) {
	                	 System.out.println(buffer);
	                	 outputStream.write(buffer,0,b);
	                 }
	                 outputStream.flush();
	               
	               return "下载成功";
	             } catch (Exception e) {
	                 e.printStackTrace();
	             } finally {
	                 if (fis != null) {
	                     try {
	                         fis.close();
	                     } catch (IOException e) {
	                         e.printStackTrace();
	                     }
	                 }
	             }
	         }
	        return "下载失败";    
	}

	@Override
	public Result createDatabase(String username, String dbname) {
		Result result=new Result();
		long startMili=System.currentTimeMillis();// 当前时间对应的毫秒数
		User user = User.getUser(username);
		File filefolder=new File("storage/"+user.getName()+"/"+dbname);
		if(!filefolder.exists()){//如果文件夹不存在
			filefolder.mkdir();//创建文件夹
		    System.out.println("已添加"+dbname+"数据库");
			 result.wellDone();
		}
		else
		{
			System.out.println("数据库已存在");
            result.setStatus(Result.FAILED);
		}
		return result;
	}


}
