package com.easydblib.dao;


import com.easydblib.info.DBInfo;

import java.util.List;


/**
 * 数据库操作接口
 * @author : zhousf
 */
public interface BaseDao<T> {

    /**
     * 增加
     * @param model 映射类
     * @return 影响行数
     */
    int create(T model);

    /**
     * 增加集合
     * @param list 映射类集合
     * @return 影响行数
     */
    int create(List<T> list);

    /**
     * 删除
     * @param model 映射类
     * @return 影响行数
     */
    int delete(T model);

    /**
     * 更新集合
     * @param list 映射类集合
     * @return 影响行数
     */
    int delete(List<T> list);

    /**
     * 更新
     * @param model 映射类
     * @return 影响行数
     */
    int update(T model);

    /**
     * 查询所有
     * @return 映射类集合
     */
    List<T> queryForAll();

    /**
     * 查询所有并排序
     * @param DBInfo 查询信息体
     * @return 映射类集合
     */
    List<T> queryForAll(DBInfo DBInfo);

    /**
     * 多条件查询并排序
     * @param DBInfo 查询信息体
     * @return 映射类集合
     */
    List<T> query(DBInfo DBInfo);


    /**
     * 分页查询
     * @param DBInfo 查询信息体
     * @return 映射类集合
     */
    List<T> queryLimit(DBInfo DBInfo);

    /**
     * 统计条目数
     * @return 条目数
     */
    long countOf();

    /**
     * 统计条目数
     * @param DBInfo 查询信息体
     * @return 条目数
     */
    long countOf(DBInfo DBInfo);

}
