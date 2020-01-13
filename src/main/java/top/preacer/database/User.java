package top.preacer.database;

import java.io.*;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;
    public static final  int NORMAL=1;//常规角色 可以select
    public static final  int ADMIN=2;//管理员角色
    private boolean canDelete=false;
    private boolean canUpdate=false;
    private boolean canInsert=false;
	private String name;
    private String password;
    public int role=1;//默认为常规角色

    

    public User() {
    }


    public User(String name, String password) {
        this.name = name;
        this.password = password;
        //1只能使用select 2能使用全部权限
        role = this.NORMAL;
    }


    public static User getUser(String userName, String password) {
        User user = null;
        File file = new File("storage/"+userName, "user.pojo");
        if (!file.exists()) {
            System.out.println("用户不存在");
            return null;
        }
        try (
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
        	System.out.println(file.getAbsolutePath());
            user =  (User) ois.readObject();
            if (null == user) {
                System.out.println("此用户不存在");
            } else if (!password.equals(user.password)) {
                //如果密码不正确，返回null
                user = null;
                System.out.println("密码错误");
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

    /**
     * 从用户信息文件中读取用户对象
     * @param userName
     * @return
     */
    public static User getUser(String userName) {
        User user = null;
        File file = new File("storage/"+userName, "user.pojo");
        if (!file.exists()) {
            System.out.println("用户不存在");
            return null;
        }
        try (
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            user = (User) ois.readObject();
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

    /**
     * 对用户授权角色
     * @param role 角色
     */
    public void grant(int role) {
        setRole(role);
        User.writeUser(this);
    }

    public static void writeUser(User user) {
        File file = new File("storage/"+user.getName(), "user.pojo");
        try (
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(user);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }


	public boolean isCanDelete() {
		return canDelete;
	}


	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}


	public boolean isCanUpdate() {
		return canUpdate;
	}


	public void setCanUpdate(boolean canUpdate) {
		this.canUpdate = canUpdate;
	}


	public boolean isCanInsert() {
		return canInsert;
	}


	public void setCanInsert(boolean canInsert) {
		this.canInsert = canInsert;
	}
    
}
