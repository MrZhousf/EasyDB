package com.easydblib.util;

import com.j256.ormlite.field.DatabaseField;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * 转换工具
 * @author : zhousf
 */
public class CastUtil {


    /**
     * 将from赋值给to
     * @param from 赋值的model
     * @param to 被赋值的model
     * @return to
     */
    public static <T> T castModel(T from,T to) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException{
        if(to != null && from != null){
            Field[] field_obj = from.getClass().getDeclaredFields();
            for(Field field : field_obj){
                // 获取属性的名字
                String name = field.getName();
                if(!name.equals("serialVersionUID")){
                    field.setAccessible(true);
                    Object value_from = field.get(from);
                    Object value_to = field.get(to);
                    DatabaseField type = field.getAnnotation(DatabaseField.class);
                    //判断是否主键
                    if(type == null || !type.generatedId()){
                        //赋值
                        if(value_from!=null && (value_to == null ||!value_to.toString().trim().equals(value_from.toString().trim()))){
                            Field field_toObj = to.getClass().getDeclaredField(name);
                            field_toObj.setAccessible(true);
                            field_toObj.set(to, value_from);
                        }
                    }
                }
            }
            return to;
        }
        return null;
    }



}
