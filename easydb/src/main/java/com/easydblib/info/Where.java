package com.easydblib.info;

/**
 * Where信息体
 * @author : zhousf
 */
public class Where {

    public final static String EQ = "equal";//等于
    public final static String LIKE = "like";//模糊匹配
    public final static String BETWEEN = "between";//指定范围
    public final static String OR = "or";//或者
    public final static String AND = "and";//并且
    public final static String LT = "lt";//小于
    public final static String GT = "gt";//大于
    public final static String GE = "ge";//大于等于
    public final static String LE = "le";//小于等于
    public final static String NE = "ne";//不等于
    public final static String IN = "in";//包含
    public final static String NOT_IN = "notIn";//不包含
    public final static String INIT = "init";//初始化

    public String andOr = AND;//连接符
    public String op = INIT;//操作符
    public String name;//操作名
    public Object value;//操作数

    public Object low;
    public Object high;

    public Object[] values;


    public static Where get(String andOr){
        return new Where(andOr);
    }

    public static Where get(String andOr, String op, String name, Object value){
        return new Where(andOr,op,name,value);
    }

    public static Where get(String andOr, String op, String name, Object[] values){
        return new Where(andOr,op,name,values);
    }

    public static Where get(String andOr, String op, String name, Object low, Object high){
        return new Where(andOr,op, name, low, high);
    }

    private Where(String andOr) {
        this.andOr = andOr;
    }

    private Where(String andOr, String op, String name, Object value) {
        this.andOr = andOr;
        this.op = op;
        this.name = name;
        this.value = value;
    }

    private Where(String andOr, String op, String name, Object low, Object high) {
        this.andOr = andOr;
        this.op = op;
        this.name = name;
        this.low = low;
        this.high = high;
    }

    private Where(String andOr, String op, String name, Object[] values) {
        this.andOr = andOr;
        this.op = op;
        this.name = name;
        this.values = values;
    }
}
