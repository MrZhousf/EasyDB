package com.easydblib.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库操作条件
 * @author : zhousf
 */
public class WhereInfo {

    /**
     * 条件集合：
     */
    public List<Where> wheres = new ArrayList<>();

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

    private boolean isOr = false;

    private WhereInfo() {
    }

    public static WhereInfo get(){
        return new WhereInfo();
    }

    /**
     * equal条件：等于
     */
    public WhereInfo equal(String name, Object value){
        wheres.add(Where.get(andOr(),Where.EQ,name,value));
        return this;
    }

    /**
     * like条件：注意使用%（例如：value="北京%",value="%北京",value="%北京%"）
     */
    public WhereInfo like(String name, Object value){
        wheres.add(Where.get(andOr(),Where.LIKE,name,value));
        return this;
    }

    /**
     * between条件
     */
    public WhereInfo between(String name,Object low, Object high){
        wheres.add(Where.get(andOr(),Where.BETWEEN,name,low,high));
        return this;
    }

    /**
     * or条件
     */
    public WhereInfo or(){
        isOr = true;
        return this;
    }

    /**
     * 更新
     */
    public WhereInfo update(String name, Object value){
        wheres.add(Where.get(andOr(),Where.UPDATE,name,value));
        return this;
    }

    /**
     * 排序：false降序 true升序
     */
    public WhereInfo order(String name, boolean value){
        orders.put(name,value);
        return this;
    }

    /**
     * 分页：每页条目数
     */
    public WhereInfo limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * 小于
     */
    public WhereInfo lt(String name, Object value){
        wheres.add(Where.get(andOr(),Where.LT,name,value));
     return this;
    }

    /**
     * 大于
     */
    public WhereInfo gt(String name, Object value){
        wheres.add(Where.get(andOr(),Where.GT,name,value));
        return this;
    }

    /**
     * 大于等于
     */
    public WhereInfo ge(String name, Object value){
        wheres.add(Where.get(andOr(),Where.GE,name,value));
        return this;
    }

    /**
     * 小于等于
     */
    public WhereInfo le(String name, Object value){
        wheres.add(Where.get(andOr(),Where.LE,name,value));
        return this;
    }

    /**
     * 不等于
     */
    public WhereInfo ne(String name, Object value){
        wheres.add(Where.get(andOr(),Where.NE,name,value));
        return this;
    }

    /**
     * 包含
     */
    public WhereInfo in(String name, Object... value){
        wheres.add(Where.get(andOr(),Where.IN,name,value));
        return this;
    }

    /**
     * 不包含
     */
    public WhereInfo notIn(String name, Object... value){
        wheres.add(Where.get(andOr(),Where.NOT_IN,name,value));
        return this;
    }


    private String andOr(){
        if(isOr){
            isOr = false;
            return Where.OR;
        }else{
            return Where.AND;
        }
    }


}
