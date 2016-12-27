package com.easydblib.util;

import com.j256.ormlite.field.DatabaseField;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * 比较工具
 * @author : zhousf
 */
public class CompareUtil {


    /**
     * 比较两个实体并返回新实体
     * @param from 赋值的model
     * @param to 被赋值的model
     * @return to
     */
    public static <T> T compare(T from,T to) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException{
        if(to != null && from != null){
            Field[] field_obj = from.getClass().getDeclaredFields();
            for(Field field : field_obj){
                // 获取属性的名字
                String name = field.getName();
                if(!name.equals("serialVersionUID")){
                    Object value_obj = field.get(from);
                    Object value_dbObj = field.get(to);
                    DatabaseField type = field.getAnnotation(DatabaseField.class);
                    //判断是否主键
                    if(type == null || !type.generatedId())
                        //比较两个实体的属性值是否一致
                        if(value_obj!=null && (value_dbObj == null ||!value_dbObj.toString().trim().equals(value_obj.toString().trim()))){
                            Field field_dbObj = to.getClass().getDeclaredField(name);
                            field_dbObj.setAccessible(true);
                            field_dbObj.set(to, value_obj);
                        }
                }
            }
            return to;
        }
        return null;
    }



}
