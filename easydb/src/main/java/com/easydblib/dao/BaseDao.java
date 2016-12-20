package com.easydblib.dao;


import com.easydblib.info.WhereInfo;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.List;
import java.util.concurrent.Callable;


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
     * @param whereInfo 查询信息体
     * @return 映射类集合
     */
    List<T> queryForAll(WhereInfo whereInfo);

    /**
     * 多条件查询并排序
     * @param whereInfo 查询信息体
     * @return 映射类集合
     */
    List<T> query(WhereInfo whereInfo);


    /**
     * 分页查询
     * @param whereInfo 查询信息体
     * @return 映射类集合
     */
    List<T> queryLimit(WhereInfo whereInfo);

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
     * @param whereInfo 查询信息体
     * @return 条目数
     */
    long countOf(WhereInfo whereInfo);

    /**
     * 是否存在
     * @param whereInfo  查询信息体
     * @return true 存在  false 不存在
     */
    boolean isExist(WhereInfo whereInfo);

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
     * 删除表
     * @return 条目数
     */
    int dropTable();

    /**
     * 获取数据表DAO
     * @return dao
     */
    Dao<T, Integer> fetchDao();

    /**
     * 执行事务
     * @param callable 事务回调
     */
    void callInTransaction(Callable<T> callable);

    /**
     * 批处理-大量数据库操作时请采用该方法（性能最优）
     * @param callable 回调
     */
    <CT> CT callBatchTasks(Callable<CT> callable);


}
