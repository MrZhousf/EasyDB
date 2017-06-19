package com.easydblib.dao;

import android.text.TextUtils;

import com.easydblib.callback.EasyRun;
import com.easydblib.handler.HandlerHelper;
import com.easydblib.handler.MessageInfo;
import com.easydblib.info.OrderInfo;
import com.easydblib.info.Result;
import com.easydblib.info.WhereInfo;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.StatementBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author : zhousf
 * @description : 数据库操作接口实现类
 */
public class RealBaseDaoImpl<T> implements RealBaseDao<T> {

    private Dao<T, Long> dao;
    private static ExecutorService executorService;

    public RealBaseDaoImpl(Dao<T, Long> dao) {
        this.dao = dao;
        if(executorService == null)
            executorService = Executors.newCachedThreadPool();

    }

    @Override
    public Result<T> add(T model) {
        Result<T> result = new Result<>(Result.LINE);
        try {
            result.setLine(dao.create(model));
        } catch (Exception e) {
            result.setException(e);
        }
        return result;
    }

    @Override
    public Result<T> add(List<T> list) {
        Result<T> result = new Result<>(Result.LINE);
        try {
            result.setLine(dao.create(list));
        } catch (Exception e) {
            result.setException(e);
        }
        return result;
    }

    @Override
    public Result<T> addOrUpdate(T model) {
        Result<T> result = new Result<>(Result.LINE);
        try {
            Dao.CreateOrUpdateStatus status = dao.createOrUpdate(model);
            result.setLine(status.getNumLinesChanged());
        } catch (Exception e) {
            result.setException(e);
        }
        return result;
    }

    @Override
    public Result<T> addIfNotExists(T model) {
        Result<T> result = new Result<>(Result.MODEL);
        try {
            result.setModel(dao.createIfNotExists(model));
        } catch (Exception e) {
            result.setException(e);
        }
        return result;
    }

    @Override
    public Result<T> delete(T model) {
        int line = 0;
        Result<T> result = new Result<>(Result.LINE);
        try {
            line = dao.delete(model);
            result.setLine(line);
        } catch (Exception e) {
            result.setException(e);
        }
        return result;
    }

    @Override
    public Result<T> delete(List<T> list) {
        Result<T> result = new Result<>(Result.LINE);
        try {
            result.setLine(dao.delete(list));
        } catch (Exception e) {
            result.setException(e);
        }
        return result;
    }

    @Override
    public Result<T> delete(WhereInfo whereInfo) {
        Result<T> result = new Result<>(Result.LINE);
        try {
            DeleteBuilder<T, Long> deleteBuilder = dao.deleteBuilder();
            deleteBuilder = (DeleteBuilder<T, Long>)fetchQueryBuilder(deleteBuilder, whereInfo);
            result.setLine(dao.delete(deleteBuilder.prepare()));
        } catch (Exception e){
            result.setException(e);
        }
        return result;
    }

    @Override
    public Result<T> update(T model) {
        Result<T> result = new Result<>(Result.LINE);
        try {
            result.setLine(dao.update(model));
        } catch (Exception e) {
            result.setException(e);
        }
        return result;
    }

    @Override
    public Result<T> update(WhereInfo whereInfo) {
        Result<T> result = new Result<>(Result.LINE);
        try {
            QueryBuilder<T,Long> queryBuilder = dao.queryBuilder();
            UpdateBuilder<T,Long> preparedUpdate = dao.updateBuilder();
            for (com.easydblib.info.Where where : whereInfo.wheres) {
                if(where.op.equals(com.easydblib.info.Where.UPDATE)){
                    preparedUpdate.updateColumnValue(where.name,where.value);
                }
            }
            preparedUpdate.setWhere(fetchWhere(queryBuilder, whereInfo));
            result.setLine(preparedUpdate.update());
        } catch (Exception e) {
            result.setException(e);
        }
        return result;
    }

    @Override
    public Result<T> queryAll() {
        Result<T> result = new Result<>(Result.LIST);
        try {
            result.setList(dao.queryForAll());
        } catch (Exception e) {
            result.setException(e);
        }
        return result;
    }

    @Override
    public Result<T> queryAll(OrderInfo orderInfo) {
        Result<T> result = new Result<>(Result.LIST);
        try {
            QueryBuilder<T, Long> queryBuilder = dao.queryBuilder();
            orderBy(queryBuilder, orderInfo.orders);
            result.setList(dao.query(queryBuilder.prepare()));
        } catch (Exception e) {
            result.setException(e);
        }
        return result;
    }

    @Override
    public Result<T> query(WhereInfo whereInfo) {
        Result<T> result = new Result<>(Result.LIST);
        try {
            QueryBuilder<T, Long> queryBuilder = dao.queryBuilder();
            orderBy(queryBuilder, whereInfo.orders);
            queryBuilder = (QueryBuilder<T, Long>)fetchQueryBuilder(queryBuilder, whereInfo);
            result.setList(dao.query(queryBuilder.prepare()));
        } catch (Exception e){
            result.setException(e);
        }
        return result;
    }

    @Override
    public Result<T> queryLimit(WhereInfo whereInfo) {
        List<T> all = new ArrayList<T>();
        Result<T> result = new Result<>(Result.LIST);
        try {
            QueryBuilder<T, Long> queryBuilder = dao.queryBuilder();
            orderBy(queryBuilder, whereInfo.orders);
            int offset = whereInfo.currentPage;
            if(offset != 0){
                offset = (whereInfo.currentPage -1) * whereInfo.limit + whereInfo.size;
            }
            queryBuilder = (QueryBuilder<T, Long>)fetchQueryBuilder(queryBuilder, whereInfo);
            queryBuilder.offset((long)offset);
            queryBuilder.limit((long) whereInfo.limit);
            all = dao.query(queryBuilder.prepare());
            whereInfo.currentPage = ++whereInfo.currentPage;
            whereInfo.size = all.size();
            result.setList(all);
        } catch (Exception e){
            result.setException(e);
        }
        return result;
    }

    @Override
    public Result<T> query(QueryBuilder<T, Integer> queryBuilder){
        Result<T> result = new Result<>(Result.LIST);
        try {
            result.setList(dao.query(queryBuilder.prepare()));
        } catch (Exception e){
            result.setException(e);
        }
        return result;
    }

    @Override
    public Result<T> countOf() {
        return countOf(null);
    }

    @Override
    public Result<T> countOf(WhereInfo whereInfo) {
        Result<T> result = new Result<>(Result.COUNT);
        try {
            QueryBuilder<T, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.setCountOf(true);
            if(null != whereInfo){
                queryBuilder = (QueryBuilder<T, Long>)fetchQueryBuilder(queryBuilder, whereInfo);
                result.setCount(dao.countOf(queryBuilder.prepare()));
            }else{
                result.setCount(dao.countOf());
            }
        } catch (Exception e){
            result.setException(e);
        }
        return result;
    }

    @Override
    public Result<T> isExist(WhereInfo whereInfo){
        List<T> all = new ArrayList<T>();
        Result<T> result = new Result<>(Result.IS_EXIST);
        try {
            QueryBuilder<T, Long> queryBuilder = dao.queryBuilder();
            queryBuilder = (QueryBuilder<T, Long>)fetchQueryBuilder(queryBuilder, whereInfo);
            all = dao.query(queryBuilder.prepare());
            result.setExist(!all.isEmpty());
        } catch (Exception e){
            result.setException(e);
        }
        return result;
    }

    @Override
    public Result<T> executeRaw(String statement, String... arguments) {
        Result<T> result = new Result<>(Result.LINE);
        try {
            result.setLine(dao.executeRaw(statement,arguments));
        } catch (Exception e){
            result.setException(e);
        }
        return result;
    }

    @Override
    public Result<T> clearTable(){
        Result<T> result = new Result<>(Result.LINE);
        try {
            result.setLine(TableUtils.clearTable(dao.getConnectionSource(),dao.getDataClass()));
        } catch (Exception e){
            result.setException(e);
        }
        return result;
    }

    @Override
    public Result<T> dropTable(){
        Result<T> result = new Result<>(Result.LINE);
        try {
            if(dao.isTableExists()){
                result.setLine(TableUtils.dropTable(dao.getConnectionSource(),dao.getDataClass(),false));
            }
        } catch (Exception e){
            result.setException(e);
        }
        return result;
    }

    @Override
    public Dao<T, Long> fetchDao(){
        return dao;
    }

    @Override
    public String getTableName() {
        return dao.getTableName();
    }

    @Override
    public void callInTransaction(Callable<T> callable){
        TransactionManager transactionManager = new TransactionManager(dao.getConnectionSource());
        try {
            transactionManager.callInTransaction(callable);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public <CT> CT callBatchTasks(Callable<CT> callable){
        try {
            return dao.callBatchTasks(callable);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> void asyncTask(final EasyRun<T> easyRun) {
        if(null != easyRun){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        T data = easyRun.run();
                        MessageInfo<T> info = new MessageInfo<T>();
                        info.what = HandlerHelper.WHAT_CALLBACK;
                        info.model = data;
                        info.easyRun = easyRun;
                        HandlerHelper.get().sendMessage(info.build());
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    private Where<T, Long> fetchWhere(StatementBuilder<T, Long> queryBuilder, WhereInfo whereInfo) throws SQLException{
        List<com.easydblib.info.Where> wheres = whereInfo.wheres;
        Where<T, Long> whereBuilder = null;
        if(!wheres.isEmpty()){
            whereBuilder = queryBuilder.where();
            boolean isFirst = true;
            for(int i = 0; i < wheres.size(); i++){
                com.easydblib.info.Where where = wheres.get(i);
                if(TextUtils.isEmpty(where.name))
                    continue;
                // 处理and-or
                if(!com.easydblib.info.Where.UPDATE.equals(where.op)){
                    isFirst = appendAnd(whereBuilder,where,isFirst);
                }
                // 等于
                if(com.easydblib.info.Where.EQ.equals(where.op)){
                    whereBuilder.eq(where.name, where.value);
                }
                // 模糊查询
                if(com.easydblib.info.Where.LIKE.equals(where.op)){
                    whereBuilder.like(where.name,where.value);
                }
                // between
                if(com.easydblib.info.Where.BETWEEN.equals(where.op)){
                    whereBuilder.between(where.name,where.low,where.high);
                }
                // lt 小于
                if(com.easydblib.info.Where.LT.endsWith(where.op)){
                    whereBuilder.lt(where.name, where.value);
                }
                // gt 大于
                if(com.easydblib.info.Where.GT.endsWith(where.op)){
                    whereBuilder.gt(where.name,where.value);
                }
                // ge 大于等于
                if(com.easydblib.info.Where.GE.endsWith(where.op)){
                    whereBuilder.ge(where.name,where.value);
                }
                // le 小于等于
                if(com.easydblib.info.Where.LE.endsWith(where.op)){
                    whereBuilder.le(where.name,where.value);
                }
                // ne 不等于
                if(com.easydblib.info.Where.NE.endsWith(where.op)){
                    whereBuilder.ne(where.name,where.value);
                }
                // in 包含
                if(com.easydblib.info.Where.IN.endsWith(where.op)){
                    whereBuilder.in(where.name,where.values);
                }
                // notIn 不包含
                if(com.easydblib.info.Where.NOT_IN.endsWith(where.op)){
                    whereBuilder.notIn(where.name,where.values);
                }
            }
        }
        return whereBuilder;
    }


    /**
     * 构建查询条件
     */
    private StatementBuilder<T,Long> fetchQueryBuilder(StatementBuilder<T, Long> queryBuilder, WhereInfo whereInfo) throws SQLException {
        fetchWhere(queryBuilder,whereInfo);
        return queryBuilder;
    }

    /**
     * 追加连接符
     */
    private boolean appendAnd(Where<T, Long> whereBuilder,com.easydblib.info.Where where, boolean isFirst){
        if(!isFirst){
            if(com.easydblib.info.Where.AND.equals(where.andOr)){
                whereBuilder.and();
            }
            if(com.easydblib.info.Where.OR.endsWith(where.andOr)){
                whereBuilder.or();
            }
        }
        return false;
    }

    /**
     * 排序
     */
    private void orderBy(QueryBuilder<T, Long> queryBuilder, Map<String,Boolean> orders){
        if(!orders.isEmpty()){
            for(Map.Entry<String,Boolean> order : orders.entrySet()){
                queryBuilder.orderBy(order.getKey(),order.getValue());
            }
        }
    }



}
