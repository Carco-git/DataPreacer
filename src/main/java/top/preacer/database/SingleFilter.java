package top.preacer.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import top.preacer.database.pojo.Field;
import top.preacer.database.pojo.Relate;

public class SingleFilter {
    private Field field;
    private String relateName;
    private String condition;

    public SingleFilter(Field field, String relateName, String condition) {
        this.field = field;//域   a
        this.relateName = relateName;//比较 =
        this.condition = condition;//参数 1
    }

    /**
     * @param srcDatas 原数据
     * @return 过滤后的数据
     */
    public List<Map<String, String>> singleFiltData(List<Map<String, String>> srcDatas) {
        // Field field, Relationship relationship, String condition
    	Relate relationship = Relate.parseRel(relateName);
        List<Map<String, String>> datas = new ArrayList<>();
        //如果没有限定条件，返回原始列表
        if (null == field || null == relationship) {
            return srcDatas;
        }
        for (Map<String, String> srcData : srcDatas) {
            //如果条件匹配成功,则新的列表存储此条数据
            if (Relate.matchCondition(srcData, field, relationship, condition)) {
                datas.add(srcData);
            } else {
                continue;
            }
        }
        return datas;
    }
    public List<Map<String, String>> singleFiltDataWithLinesLimit(List<Map<String, String>> srcDatas, int maxDeleteLines) {
        // Field field, Relationship relationship, String condition
    	Relate relationship = Relate.parseRel(relateName);
        List<Map<String, String>> datas = new ArrayList<>();
        int temp=maxDeleteLines;
        //如果没有限定条件，返回原始列表
        if (null == field || null == relationship) {
            //Collections.copy(datas, srcDatas);
            return srcDatas;
        }
        for (Map<String, String> srcData : srcDatas) {
            //如果条件匹配成功,则新的列表存储此条数据
            if (Relate.matchCondition(srcData, field, relationship, condition)) {
                datas.add(srcData);
                temp--;
                System.out.println(temp);
                if(temp==0) {
                	return datas;
                }
            } else {
                continue;
            }
        }
        return datas;
    }

    public Field getField() {
        return field;
    }

    public String getRelateName() {
        return relateName;
    }

    public String getCondition() {
        return condition;
    }

    public Relate getRelate() {
    	Relate relationship = Relate.parseRel(relateName);
        return relationship;
    }
}
