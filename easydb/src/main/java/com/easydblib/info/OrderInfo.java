package com.easydblib.info;

import java.util.HashMap;
import java.util.Map;

/**
 * 排序条件
 * @author : zhousf
 */
public class OrderInfo {

    /**
     * 排序集合
     * false降序 true升序
     */
    public Map<String,Boolean> orders = new HashMap<>();


    private OrderInfo() {
    }


    public static OrderInfo get(){
        return new OrderInfo();
    }

    /**
     * 排序：false降序 true升序
     */
    public OrderInfo order(String name, boolean value){
        orders.put(name,value);
        return this;
    }


}
