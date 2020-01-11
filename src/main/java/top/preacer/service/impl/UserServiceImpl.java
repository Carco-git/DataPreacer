package top.preacer.service.impl;
import java.io.*;

import org.springframework.stereotype.Service;

import top.preacer.database.User;
import top.preacer.service.UserService;
@Service
public class UserServiceImpl implements UserService {
	public User LoginUser(String name, String password) throws Exception {
        User user = null;
        File file = new File("dir/"+name, "user.info");
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
    File file = new File("dir/"+name, "user.info");
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
    	user.level=1;
		File filefolder=new File("dir/"+user.getName());
		if(!filefolder.exists()){//如果文件夹不存在
			filefolder.mkdir();//创建文件夹
		    System.out.println("已添加"+user.getName()+"用户");
		}
		else
		{
	     System.out.println("用户已存在");
		 return "用户已存在";
		}
		/*try{//异常处理
			//如果文件夹下没有user.info就会创建该文件
			BufferedWriter bw=new BufferedWriter(new FileWriter("D:\\Qiju_Li\\Qiju_Li.txt"));
			bw.write("Hello I/O!");//在创建好的文件中写入"Hello I/O"
			bw.close();//一定要关闭文件
		}catch(IOException e){
			e.printStackTrace();
		}*/
    	File file = new File("dir/"+user.getName(), "user.info");
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
		File filefolder=new File("dir/"+user.getName());
		if(!filefolder.exists()){//如果文件夹不存在	
	     System.out.println("用户不存在");
		 return "用户不存在";
		}
		else
		{
		 delDir("dir/"+user.getName());//删除文件夹
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


}
