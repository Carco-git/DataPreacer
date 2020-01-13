package top.preacer.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    private static final Pattern SQL_INSERT = Pattern.compile("insert\\s+into\\s+(\\w+)(\\(((\\w+,?)+)\\))?\\s+\\w+\\((([^\\)]+,?)+)\\);?");
    private static final Pattern SQL_CREATE_TABLE = Pattern.compile("create\\stable\\s(\\w+)\\s?\\(((?:\\s?\\w+\\s\\w+,?)+)\\)\\s?;?");
    private static final Pattern SQL_ALTER_TABLE_ADD = Pattern.compile("alter\\stable\\s(\\w+)\\sadd\\s(\\w+\\s\\w+)\\s?;?");
    private static final Pattern SQL_DELETE = Pattern.compile("delete\\sfrom\\s(\\w+)(?:\\swhere\\s(\\w+\\s?[<=>]\\s?[^\\s\\;]+(?:\\sand\\s(?:\\w+)\\s?(?:[<=>])\\s?(?:[^\\s\\;]+))*))?\\s?;?(limit\\s(\\d))?");
    private static final Pattern SQL_UPDATE = Pattern.compile("update\\s(\\w+)\\sset\\s(\\w+\\s?=\\s?[^,\\s]+(?:\\s?,\\s?\\w+\\s?=\\s?[^,\\s]+)*)(?:\\swhere\\s(\\w+\\s?[<=>]\\s?[^\\s\\;]+(?:\\sand\\s(?:\\w+)\\s?(?:[<=>])\\s?(?:[^\\s\\;]+))*))?\\s?;?");
    private static final Pattern SQL_DROP_TABLE = Pattern.compile("drop\\stable\\s(\\w+);?");
    private static final Pattern SQL_SELECT = Pattern.compile("select\\s(\\*|(?:(?:\\w+(?:\\.\\w+)?)+(?:\\s?,\\s?\\w+(?:\\.\\w+)?)*))\\sfrom\\s(\\w+(?:\\s?,\\s?\\w+)*)(?:\\swhere\\s([^\\;]+\\s?;))?");
    private static final Pattern SQL_DELETE_INDEX = Pattern.compile("delete\\sindex\\s(\\w+)\\s?;?");
    private static final Pattern SQL_GRANT_ADMIN = Pattern.compile("grantadmin\\sto\\s([^;\\s]+)\\s?;?");
    private static final Pattern SQL_REVOKE_ADMIN = Pattern.compile("revokeadmin\\sfrom\\s([^;\\s]+)\\s?;?");
    public static void main(String[] args) {
//        SQLimpl SQLimpl = new SQLimpl();
//        SQLimpl.dbms();
    	Scanner sc = new Scanner(System.in);
    	User user = User.getUser("user1", "abc");
        if (null == user) {
            System.out.println("已退出dbms");
            return;
        } else {
            System.out.println(user.getName() + "登陆成功!");
        }
        //默认进入user1用户文件夹
        File userFolder = new File("storage", user.getName());
        //默认进入user1的默认数据库db1
        File dbFolder = new File(userFolder, "db1");
        Table.init(user.getName(), dbFolder.getName());
        String cmd;
        while (!"exit".equals(cmd = sc.nextLine())) {
            Matcher matcherGrantAdmin = SQL_GRANT_ADMIN.matcher(cmd);
            Matcher matcherRevokeAdmin = SQL_REVOKE_ADMIN.matcher(cmd);
            Matcher matcherInsert = SQL_INSERT.matcher(cmd);
            Matcher matcherCreateTable = SQL_CREATE_TABLE.matcher(cmd);
            Matcher matcherAlterTable_add = SQL_ALTER_TABLE_ADD.matcher(cmd);
            Matcher matcherDelete = SQL_DELETE.matcher(cmd);
            Matcher matcherUpdate = SQL_UPDATE.matcher(cmd);
            Matcher matcherDropTable = SQL_DROP_TABLE.matcher(cmd);
            Matcher matcherSelect = SQL_SELECT.matcher(cmd);
            Matcher matcherDeleteIndex = SQL_DELETE_INDEX.matcher(cmd);

            while (matcherGrantAdmin.find()) {
                User grantUser = User.getUser(matcherGrantAdmin.group(1));
                if (null == grantUser) {
                    System.out.println("授权失败！");
                } else if (user.getName().equals(grantUser.getName())) {
                    //如果是当前操作的用户，就直接更改当前用户权限
                    user.grant(User.ADMIN);
                    System.out.println("用户:" + user.getName() + "授权成功！");
                } else {
                    grantUser.grant(User.ADMIN);
                    System.out.println("用户:" + grantUser.getName() + "授权成功!");
                }
            }

            while (matcherRevokeAdmin.find()) {
                User revokeUser = User.getUser(matcherRevokeAdmin.group(1));
                if (null == revokeUser) {
                    System.out.println("取消授权失败!");
                }
                if (user.getName().equals(revokeUser.getName())) {
                    //如果是当前操作的用户，就直接更改当前用户权限
                    user.grant(User.NORMAL);
                    System.out.println("用户:" + user.getName() + "已取消授权！");
                } else {
                    revokeUser.grant(User.NORMAL);
                    System.out.println("用户:" + revokeUser.getName() + "已取消授权！");
                }
            }

            while (matcherAlterTable_add.find()) {
                if (user.getRole() != User.ADMIN) {
                    System.out.println("用户" + user.getName() + "权限不够，无法完成此操作！");
                    break;
                }
                SQLimpl.alterTableAdd(matcherAlterTable_add);
            }

            while (matcherDropTable.find()) {
                if (user.getRole() != User.ADMIN) {
                    System.out.println("用户" + user.getName() + "权限不够，无法完成此操作！");
                    break;
                }
                SQLimpl.dropTable(matcherDropTable);
            }


            while (matcherCreateTable.find()) {
                if (user.getRole() != User.ADMIN) {
                    System.out.println("用户" + user.getName() + "权限不够，无法完成此操作！");
                    break;
                }
                SQLimpl.createTable(matcherCreateTable);
            }

            while (matcherDelete.find()) {
                if (user.getRole() != User.ADMIN) {
                    System.out.println("用户" + user.getName() + "权限不够，无法完成此操作！");
                    break;
                }
                SQLimpl.delete(matcherDelete);
            }

            while (matcherUpdate.find()) {
                if (user.getRole() != User.ADMIN) {
                    System.out.println("用户" + user.getName() + "权限不够，无法完成此操作！");
                    break;
                }
                SQLimpl.update(matcherUpdate);
            }

            while (matcherInsert.find()) {
                if (user.getRole() != User.ADMIN) {
                    System.out.println("用户" + user.getName() + "权限不够，无法完成此操作！");
                    break;
                }
                SQLimpl.insert(matcherInsert);
            }

//            while (matcherSelect.find()) {
//            	SQLimpl.select(matcherSelect);
//            }

            while (matcherDeleteIndex.find()) {
                if (user.getRole() != User.ADMIN) {
                    System.out.println("用户" + user.getName() + "权限不够，无法完成此操作！");
                    break;
                }
                SQLimpl.deleteIndex(matcherDeleteIndex);
            }
            
        }
    }
}