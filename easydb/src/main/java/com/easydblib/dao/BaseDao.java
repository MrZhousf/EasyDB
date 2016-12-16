package com.easydblib.dao;


import com.easydblib.info.DBInfo;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

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
     * @param dbInfo 查询信息体
     * @return 映射类集合
     */
    List<T> queryForAll(DBInfo dbInfo);

    /**
     * 多条件查询并排序
     * @param dbInfo 查询信息体
     * @return 映射类集合
     */
    List<T> query(DBInfo dbInfo);


    /**
     * 分页查询
     * @param dbInfo 查询信息体
     * @return 映射类集合
     */
    List<T> queryLimit(DBInfo dbInfo);

    /**
     * 自定义查询
     * @param queryBuilder 查询构建
     * @return 映射类集合
     */
    List<T> query(QueryBuilder<T, Integer> queryBuilder);

    /**
     * 统计条目数
     * @return 条目数
     */
    long countOf();

    /**
     * 统计条目数
     * @param dbInfo 查询信息体
     * @return 条目数
     */
    long countOf(DBInfo dbInfo);

    /**
     * 是否存在
     * @param dbInfo
     * @return true 存在  false 不存在
     */
    boolean isExist(DBInfo dbInfo);

    /**
     * 执行原生的SQL语句
     * @param statement SQL语句
     * @param arguments 参数值-占位符?的值
     * @return 影响行数
     */
    int executeRaw(String statement, String... arguments);

    /**
     * 清空表
     * @return 条目数
     */
    int clearTable();

    /**
     * 获取数据表DAO
     * @return dao
     */
    Dao<T, Integer> fetchDao();

}
