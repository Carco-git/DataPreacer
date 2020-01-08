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

}
