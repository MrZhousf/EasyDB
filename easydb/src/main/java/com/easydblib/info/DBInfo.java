package com.easydblib.info;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据操作信息体
 * @author : zhousf
 */
public class DBInfo {

    /**
     * 条件集合：
     */
    public Map<String,Object> wheres = new HashMap<>();

    /**
     * 排序集合
     * false降序 true升序
     */
    public Map<String,Boolean> orders = new HashMap<>();

    //每页条目数
    public int limit;
    //返回条目数
    public int size;
    //当前页
    public int currentPage = 0;

    public DBInfo() {
    }

    public static DBInfo get(){
        return new DBInfo();
    }

    public DBInfo where(String name, Object value){
        wheres.put(name,value);
        return this;
    }

    public DBInfo order(String name, Boolean value){
        orders.put(name,value);
        return this;
    }

    public DBInfo limit(int limit) {
        this.limit = limit;
        return this;
    }
}
