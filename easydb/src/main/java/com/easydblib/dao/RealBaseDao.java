package com.easydblib.dao;


import com.easydblib.callback.EasyRun;
import com.easydblib.info.OrderInfo;
import com.easydblib.info.Result;
import com.easydblib.info.WhereInfo;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.util.List;
import java.util.concurrent.Callable;


/**
 * 数据库操作接口
 * @author : zhousf
 */
public interface RealBaseDao<T> {

    /**
     * 增加
     * @param model 映射类
     * @return 影响行数
     */
    Result<T> add(T model);

    /**
     * 增加集合
     * @param list 映射类集合
     * @return 影响行数
     */
    Result<T> add(List<T> list);

    /**
     * 增加或更新
     * @param model 映射类
     * @return 影响行数
     */
    Result<T> addOrUpdate(T model);

    /**
     * 不存在时增加
     * @param model 映射类
     * @return 增加的对象
     */
    Result<T> addIfNotExists(T model);

    /**
     * 删除
     * @param model 映射类
     * @return 影响行数
     */
    Result<T> delete(T model);

    /**
     * 删除集合
     * @param list 映射类集合
     * @return 影响行数
     */
    Result<T> delete(List<T> list);

    /**
     * 根据条件删除
     * @param whereInfo 查询信息体
     * @return 影响行数
     */
    Result<T> delete(WhereInfo whereInfo);

    /**
     * 更新
     * @param model 映射类
     * @return 影响行数
     */
    Result<T> update(T model);

    /**
     * 更新
     * @param whereInfo 更新条件
     */
    Result<T> update(WhereInfo whereInfo);

    /**
     * 查询所有
     * @return 映射类集合
     */
    Result<T> queryAll();

    /**
     * 查询所有并排序
     * @param orderInfo 排序信息体
     * @return 映射类集合
     */
    Result<T> queryAll(OrderInfo orderInfo);

    /**
     * 多条件查询并排序
     * @param whereInfo 查询信息体
     * @return 映射类集合
     */
    Result<T> query(WhereInfo whereInfo);


    /**
     * 分页查询
     * @param whereInfo 查询信息体
     * @return 映射类集合
     */
    Result<T> queryLimit(WhereInfo whereInfo);

    /**
     * 自定义查询
     * @param queryBuilder 查询构建
     * @return 映射类集合
     */
    Result<T> query(QueryBuilder<T, Integer> queryBuilder);

    /**
     * 统计条目数
     * @return 条目数
     */
    Result<T> countOf();

    /**
     * 统计条目数
     * @param whereInfo 查询信息体
     * @return 条目数
     */
    Result<T> countOf(WhereInfo whereInfo);

    /**
     * 是否存在
     * @param whereInfo  查询信息体
     * @return true 存在  false 不存在
     */
    Result<T> isExist(WhereInfo whereInfo);

    /**
     * 执行原生的SQL语句
     * @param statement SQL语句
     * @param arguments 参数值-占位符?的值
     * @return 影响行数
     */
    Result<T> executeRaw(String statement, String... arguments);

    /**
     * 清空表
     * @return 条目数
     */
    Result<T> clearTable();

    /**
     * 删除表
     * @return 条目数
     */
    Result<T> dropTable();

    /**
     * 获取数据表DAO
     * @return dao
     */
    Dao<T, Long> fetchDao();

    /**
     * 获取表名
     * @return 表名
     */
    String getTableName();

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

    /**
     * 异步执行
     * @param easyRun 异步run
     */
    <T> void asyncTask(EasyRun<T> easyRun);

}
