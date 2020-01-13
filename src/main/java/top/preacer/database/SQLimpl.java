package top.preacer.database;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;

import top.preacer.database.pojo.Field;
import top.preacer.database.util.SelectUtil;
import top.preacer.database.util.StringUtil;
import top.preacer.pojo.SelectTable;
import top.preacer.service.UserService;
import top.preacer.service.impl.UserServiceImpl;

public class SQLimpl {

    public static String deleteIndex(Matcher matcherDeleteIndex) {
        String tableName = matcherDeleteIndex.group(1);
        Table table = Table.getTable(tableName);
        String result =table.deleteIndex();
        System.out.println();
        return result;
    }

    public static String insert(Matcher matcherInsert) {
        String tableName = matcherInsert.group(1);//group1是表名
        Table table = Table.getTable(tableName);
        if (null == table) {
            System.out.println("未找到表：" + tableName);
            return "未找到表";
        }
        Map dictMap = table.getFieldMap();
        Map<String, String> data = new HashMap<>();
        
        String[] fieldValues = matcherInsert.group(5).trim().split(",");
        //如果确定插入指定的字段，即存在指定的列名
        if (null != matcherInsert.group(2)) {
            String[] fieldNames = matcherInsert.group(3).trim().split(",");
            //如果insert的名值数量不相等，错误
            if (fieldNames.length != fieldValues.length) {
                return "列数目与变量数目不匹配";
            }
            for (int i = 0; i < fieldNames.length; i++) {
                String fieldName = fieldNames[i].trim();
                String fieldValue = fieldValues[i].trim();
                //如果在数据字典中未发现这个字段，返回错误
                if (!dictMap.containsKey(fieldName)) {
                    return "未知的列"+fieldName;
                }
                data.put(fieldName, fieldValue);
            }
        } else {//否则插入全部字段
            Set<String> fieldNames = dictMap.keySet();
            int i = 0;
            for (String fieldName : fieldNames) {
                String fieldValue = fieldValues[i].trim();
                data.put(fieldName, fieldValue);
                i++;
            }
        }
        table.insert(data);
        return "success";
    }

    public static String update(Matcher matcherUpdate) {
        String tableName = matcherUpdate.group(1);
        String setStr = matcherUpdate.group(2);
        String whereStr = matcherUpdate.group(3);
        String result=null;
        Table table = Table.getTable(tableName);
        System.out.println("准备更新");
        if (null == table) {
            System.out.println("未找到表：" + tableName);
            result="未找到表：" + tableName;
            return result;
        }
        Map<String, Field> fieldMap = table.getFieldMap();
        Map<String, String> data = StringUtil.parseUpdateSet(setStr);

        List<SingleFilter> singleFilters = new ArrayList<>();
        if (null == whereStr) {
            table.update(data, singleFilters);//空的过滤器
        } else {//存在条件语句
            List<Map<String, String>> filtList = StringUtil.parseWhere(whereStr);
            for (Map<String, String> filtMap : filtList) {
                SingleFilter singleFilter = new SingleFilter(fieldMap.get(filtMap.get("fieldName"))
                        , filtMap.get("relationshipName"), filtMap.get("condition"));

                singleFilters.add(singleFilter);
            }
            table.update(data, singleFilters);
        }
        return "success";
    }

    public static String delete(Matcher matcherDelete) {
        String tableName = matcherDelete.group(1);
        String whereStr = matcherDelete.group(2);
        Table table = Table.getTable(tableName);
        if (null == table) {
            System.out.println("未找到表：" + tableName);
            return "未找到表：" + tableName;
        }
        boolean isLimit = matcherDelete.group(3).contains("limit");
        int maxDeleteLines=0;
        if(isLimit) {
        	maxDeleteLines = Integer.parseInt(matcherDelete.group(4));
        	System.out.println(isLimit+""+maxDeleteLines);
        }
        Map<String, Field> fieldMap = table.getFieldMap();
        List<SingleFilter> singleFilters = new ArrayList<>();
        if (null == whereStr) {
            table.delete(singleFilters,maxDeleteLines);
        } else {
            List<Map<String, String>> filtList = StringUtil.parseWhere(whereStr);
            for (Map<String, String> filtMap : filtList) {
                SingleFilter singleFilter = new SingleFilter(fieldMap.get(filtMap.get("fieldName"))
                        , filtMap.get("relationshipName"), filtMap.get("condition"));
                singleFilters.add(singleFilter);
            }
            table.delete(singleFilters,maxDeleteLines);
        }
        return "success";
    }

    public static String createTable(Matcher matcherCreateTable) {
        String tableName = matcherCreateTable.group(1);
        String propertys = matcherCreateTable.group(2);
        Map<String, Field> fieldMap = StringUtil.parseCreateTable(propertys);
        if(fieldMap==null) {
        	return "属性只能为int或varchar或double";
        }
        String result=Table.createTable(tableName, fieldMap);
        System.out.println(result);
        return result;
    }

    public static String dropTable(Matcher matcherDropTable) {
        String tableName = matcherDropTable.group(1);
        String result=Table.dropTable(tableName);
        System.out.println(result);
        return result;
    }

    public static String alterTableAdd(Matcher matcherAlterTable_add) {
        String tableName = matcherAlterTable_add.group(1);
        String propertys = matcherAlterTable_add.group(2);
        Map<String, Field> fieldMap = StringUtil.parseCreateTable(propertys);
        Table table = Table.getTable(tableName);
        if (null == table) {
            System.out.println("未找到表：" + tableName);
            return ("未找到表：" + tableName);
        }
        String result=table.addDict(fieldMap);
        System.out.println();
        return result;
    }

    /**
     * 将数据整理成tableName.fieldName dataValue的型式
     *
     * @param tableName 表名
     * @param srcDatas  原数据
     * @return 添加表名后的数据
     */
    public static List<Map<String, String>> associatedTableName(String tableName, List<Map<String, String>> srcDatas) {
        List<Map<String, String>> destDatas = new ArrayList<>();
        for (Map<String, String> srcData : srcDatas) {
            Map<String, String> destData = new LinkedHashMap<>();
            for (Map.Entry<String, String> data : srcData.entrySet()) {
                destData.put(tableName + "." + data.getKey(), data.getValue());
            }
            destDatas.add(destData);
        }
        return destDatas;
    }
    public static SelectTable Select(Matcher matcherSelect) {
    	//用来回传给上一层的数据
    	SelectTable result=new SelectTable();
    	
        //将读到的所有数据放到tableDatasMap中
        Map<String, List<Map<String, String>>> tableDatasMap = new LinkedHashMap<>();

        //将投影放在Map<String,List<String>> projectionMap中
        Map<String, List<String>> projectionMap = new LinkedHashMap<>();


        List<String> tableNames = StringUtil.parseFrom(matcherSelect.group(2));

        String whereStr = matcherSelect.group(3);

        //将tableName和table.fieldMap放入
        Map<String, Map<String, Field>> fieldMaps = new HashMap();

        for (String tableName : tableNames) {
            Table table = Table.getTable(tableName);
            if (null == table) {
                System.out.println("此处未找到表：" + tableName);
                return null;
            }
            Map<String, Field> fieldMap = table.getFieldMap();
            fieldMaps.put(tableName, fieldMap);

            //解析选择
            List<SingleFilter> singleFilters = new ArrayList<>();
            List<Map<String, String>> filtList = StringUtil.parseWhere(whereStr, tableName, fieldMap);
            for (Map<String, String> filtMap : filtList) {
                SingleFilter singleFilter = new SingleFilter(fieldMap.get(filtMap.get("fieldName"))
                        , filtMap.get("relationshipName"), filtMap.get("condition"));

                singleFilters.add(singleFilter);
            }
            //projections为所有属性名
            List<String> projections = StringUtil.parseProjection(matcherSelect.group(1), tableName, fieldMap);
            projectionMap.put(tableName, projections);
            //读取数据并进行选择操作
            List<Map<String, String>> srcDatas = table.read(singleFilters);
            List<Map<String, String>> datas = associatedTableName(tableName, srcDatas);
            tableDatasMap.put(tableName, datas);
        }


        //解析连接条件，并创建连接对象jion
        List<Map<String, String>> joinConditionMapList = StringUtil.parseWhere_join(whereStr, fieldMaps);
        List<JoinCondition> joinConditionList = new LinkedList<>();
        for (Map<String, String> joinMap : joinConditionMapList) {
            String tableName1 = joinMap.get("tableName1");
            String tableName2 = joinMap.get("tableName2");
            String fieldName1 = joinMap.get("field1");
            String fieldName2 = joinMap.get("field2");
            Field field1 = fieldMaps.get(tableName1).get(fieldName1);
            Field field2 = fieldMaps.get(tableName2).get(fieldName2);
            String relationshipName = joinMap.get("relationshipName");
            JoinCondition joinCondition = new JoinCondition(tableName1, tableName2, field1, field2, relationshipName);

            joinConditionList.add(joinCondition);

            //将连接条件的字段加入投影中
            projectionMap.get(tableName1).add(fieldName1);
            projectionMap.get(tableName2).add(fieldName2);
        }

        List<Map<String, String>> LastDatas = Join.joinData(tableDatasMap, joinConditionList, projectionMap);
        
        //处理为table.filed
        List<String> PropList = SelectUtil.handleProp(projectionMap);;
            
        result.setCol(PropList.size());
        result.setColumnName(PropList);
        result.setRowList(LastDatas);
        result.setRow(LastDatas.size());
        return result;
    }
    public static String grantAdmin(Matcher matcherGrantAdmin)
    {
    	User grantUser = User.getUser(matcherGrantAdmin.group(1));
        if (null == grantUser) 
            return "授权失败";
        else {
        	if (grantUser.getRole()==User.ADMIN)
        		return "用户已有全部权限";
        	else {
            grantUser.grant(User.ADMIN);
            System.out.println("用户:" + grantUser.getName() + "授权成功!");
            User.writeUser(grantUser);
            return "success";
        	}
        }
    }
    public static String grantInsert(Matcher matcherGrantInsert)
    {
    	User grantUser = User.getUser(matcherGrantInsert.group(1));
        if (null == grantUser) 
            return "授权失败";
        else {
        	if (grantUser.isCanInsert())
        		return "用户已有该权限";
        	else {
            grantUser.setCanInsert(true);
            System.out.println("用户:" + grantUser.getName() + "授权成功!");
            User.writeUser(grantUser);
            return "success";
        	}
        }
    }
    public static String grantDelete(Matcher matcherGrantDelete)
    {
    	User grantUser = User.getUser(matcherGrantDelete.group(1));
        if (null == grantUser) 
            return "授权失败";
        else {
        	if (grantUser.isCanDelete())
        		return "用户已有该权限";
        	else {
            grantUser.setCanDelete(true);
            System.out.println("用户:" + grantUser.getName() + "授权成功!");
            User.writeUser(grantUser);
            return "success";
        	}
        }
    }
    public static String grantUpdate(Matcher matcherGrantUpdate)
    {
    	User grantUser = User.getUser(matcherGrantUpdate.group(1));
        if (null == grantUser) 
            return "授权失败";
        else {
        	if (grantUser.isCanUpdate())
        		return "用户已有该权限";
        	else {
            grantUser.setCanUpdate(true);
            System.out.println("用户:" + grantUser.getName() + "授权成功!");
            User.writeUser(grantUser);
            return "success";
        	}
        }
    }
    public static String revokeAdmin(Matcher matcherRevokeAdmin)
    {
    	User grantUser = User.getUser(matcherRevokeAdmin.group(1));
        if (null == grantUser) 
            return "取消授权失败";
        else {
        	if (grantUser.getRole()==User.ADMIN)
        	{
            grantUser.grant(User.NORMAL);
            System.out.println("用户:" + grantUser.getName() + "取消授权成功!");
            return "success";
        	}
        	else
        	{
        		if(grantUser.getRole()==User.NORMAL&&(grantUser.isCanDelete()||grantUser.isCanInsert()||grantUser.isCanUpdate()))
        		{
            	grantUser.setCanUpdate(false);
            	grantUser.setCanDelete(false);
            	grantUser.setCanInsert(false);
                System.out.println("用户:" + grantUser.getName() + "取消授权成功!");
            	return "success";
        		}
        		else
        			System.out.println("用户已删除过权限，不能重复删除");
        		return "重复删除error";
        	}
        }
    }

    public static String revokeUpdate(Matcher matcherRevokeUpdate)
    {
    	User grantUser = User.getUser(matcherRevokeUpdate.group(1));
        if (null == grantUser) 
            return "授权失败";
        else {
        	if (!grantUser.isCanUpdate())
        		return "用户未拥有该权限";
        	else {
            grantUser.setCanUpdate(false);
            System.out.println("用户:" + grantUser.getName() + "取消授权成功!");
            User.writeUser(grantUser);
            return "success";
        	}
        }
    }
    public static String revokeDelete(Matcher matcherRevokeDelete)
    {
    	User grantUser = User.getUser(matcherRevokeDelete.group(1));
        if (null == grantUser) 
            return "授权失败";
        else {
        	if (!grantUser.isCanDelete())
    		return "用户未拥有该权限";
    	else {
            grantUser.setCanDelete(false);
            System.out.println("用户:" + grantUser.getName() + "取消授权成功!");
            User.writeUser(grantUser);
            return "success";
    	}
        }
    }

    public static String revokeInsert(Matcher matcherRevokeInsert)
    {
    	User grantUser = User.getUser(matcherRevokeInsert.group(1));
        if (null == grantUser) 
            return "授权失败";
        else {
        	if (!grantUser.isCanInsert())
        		return "用户未拥有该权限";
        	else {
            grantUser.setCanInsert(true);
            System.out.println("用户:" + grantUser.getName() + "授权成功!");
            User.writeUser(grantUser);
            return "success";
        	}
        }
    }
    public static String adduser(Matcher matcherAdduser) {
        String userName = matcherAdduser.group(1);
        User user = User.getUser(userName);
        if (null == user) {
            String password = matcherAdduser.group(2);
            UserService us=new UserServiceImpl();
            us.createUser(userName,password);
            return "success";
        }
        return "用户已存在";
    }

    public static String deleteuser(Matcher matcherDeleteuser) {
        String userName = matcherDeleteuser.group(1);
        User user = User.getUser(userName);
        if (null == user) {
            return "用户不存在";
        }
        UserService us=new UserServiceImpl();
        us.deleteUser(userName);
        return "success";
    }
}

