package com.easydblib.dao;

import android.text.TextUtils;

import com.easydblib.callback.EasyRun;
import com.easydblib.handler.HandlerHelper;
import com.easydblib.handler.MessageInfo;
import com.easydblib.info.WhereInfo;
import com.easydblib.util.CastUtil;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.StatementBuilder;
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

    private Dao<T, Integer> dao;
    private static ExecutorService executorService;

    public RealBaseDaoImpl(Dao<T, Integer> dao) {
        this.dao = dao;
        if(executorService == null)
            executorService = Executors.newCachedThreadPool();

    }

    @Override
    public int add(T model) {
        int line = 0;
        try {
            line = dao.create(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }

    @Override
    public int add(List<T> list) {
        int line = 0;
        try {
            line = dao.create(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }

    @Override
    public int addOrUpdate(T model) {
        int line = 0;
        try {
            Dao.CreateOrUpdateStatus status = dao.createOrUpdate(model);
            line = status.getNumLinesChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }

    @Override
    public T addIfNotExists(T model) {
        try {
            return dao.createIfNotExists(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int delete(T model) {
        int line = 0;
        try {
            line = dao.delete(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }

    @Override
    public int delete(List<T> list) {
        int line = 0;
        try {
            line = dao.delete(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }

    @Override
    public int delete(WhereInfo whereInfo) {
        int line = 0;
        try {
            DeleteBuilder<T, Integer> deleteBuilder = dao.deleteBuilder();
            deleteBuilder = (DeleteBuilder<T, Integer>)fetchQueryBuilder(deleteBuilder, whereInfo);
            line = dao.delete(deleteBuilder.prepare());
        } catch (Exception e){
            e.printStackTrace();
        }
        return line;
    }

    @Override
    public int update(T model) {
        int line = 0;
        try {
            line = dao.update(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }

    @Override
    public int update(T model, WhereInfo whereInfo) {
        int line = 0;
        try {
            QueryBuilder<T,Integer> queryBuilder = dao.queryBuilder();
            //获取第一条数据
            queryBuilder = (QueryBuilder<T, Integer>)fetchQueryBuilder(queryBuilder, whereInfo);
            T data =  dao.queryForFirst(queryBuilder.prepare());
            //若不存在则添加
            if(data == null){
                line = dao.create(model);
            }else{
                //存在则更新
                model = CastUtil.castModel(model,data);
                line = dao.update(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }

    @Override
    public List<T> queryAll() {
        List<T> all = new ArrayList<T>();
        try {
            all = dao.queryForAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return all;
    }

    @Override
    public List<T> queryAll(WhereInfo whereInfo) {
        List<T> all = new ArrayList<T>();
        try {
            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            orderBy(queryBuilder, whereInfo.orders);
            all =  dao.query(queryBuilder.prepare());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return all;
    }

    @Override
    public List<T> query(WhereInfo whereInfo) {
        List<T> all = new ArrayList<T>();
        try {
            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            orderBy(queryBuilder, whereInfo.orders);
            queryBuilder = (QueryBuilder<T, Integer>)fetchQueryBuilder(queryBuilder, whereInfo);
            all = dao.query(queryBuilder.prepare());
        } catch (Exception e){
            e.printStackTrace();
        }
        return all;
    }

    @Override
    public List<T> queryLimit(WhereInfo whereInfo) {
        List<T> all = new ArrayList<T>();
        try {
            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            orderBy(queryBuilder, whereInfo.orders);
            int offset = whereInfo.currentPage;
            if(offset != 0){
                offset = (whereInfo.currentPage -1) * whereInfo.limit + whereInfo.size;
            }
            queryBuilder.offset((long)offset);
            queryBuilder.limit((long) whereInfo.limit);
            queryBuilder = (QueryBuilder<T, Integer>)fetchQueryBuilder(queryBuilder, whereInfo);
            all = dao.query(queryBuilder.prepare());
            whereInfo.currentPage = ++whereInfo.currentPage;
            whereInfo.size = all.size();
        } catch (Exception e){
            e.printStackTrace();
        }
        return all;
    }

    @Override
    public List<T> query(QueryBuilder<T, Integer> queryBuilder){
        List<T> all = new ArrayList<T>();
        try {
            all = dao.query(queryBuilder.prepare());
        } catch (Exception e){
            e.printStackTrace();
        }
        return all;
    }

    @Override
    public long countOf() {
        return countOf(null);
    }

    @Override
    public long countOf(WhereInfo whereInfo) {
        long line = 0;
        try {
            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.setCountOf(true);
            if(null != whereInfo){
                queryBuilder = (QueryBuilder<T, Integer>)fetchQueryBuilder(queryBuilder, whereInfo);
                line = dao.countOf(queryBuilder.prepare());
            }else{
                line = dao.countOf();
            }
            return line;
        } catch (Exception e){
            e.printStackTrace();
        }
        return line;
    }

    @Override
    public boolean isExist(WhereInfo whereInfo){
        List<T> all = new ArrayList<T>();
        try {
            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder = (QueryBuilder<T, Integer>)fetchQueryBuilder(queryBuilder, whereInfo);
            all = dao.query(queryBuilder.prepare());
        } catch (Exception e){
            e.printStackTrace();
        }
        return !all.isEmpty();
    }

    @Override
    public int executeRaw(String statement, String... arguments) {
        int line = 0;
        try {
            line = dao.executeRaw(statement,arguments);
        } catch (Exception e){
            e.printStackTrace();
        }
        return line;
    }

    @Override
    public Dao<T, Integer> fetchDao(){
        return dao;
    }

    @Override
    public String getTableName() {
        return dao.getTableName();
    }

    @Override
    public int clearTable(){
        int line = 0;
        try {
            line = TableUtils.clearTable(dao.getConnectionSource(),dao.getDataClass());
        } catch (Exception e){
            e.printStackTrace();
        }
        return line;
    }

    @Override
    public int dropTable(){
        int line = 0;
        try {
            if(dao.isTableExists()){
                line = TableUtils.dropTable(dao.getConnectionSource(),dao.getDataClass(),false);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return line;
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
            CT ct =  dao.callBatchTasks(callable);
            return ct;
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





    /**
     * 构建查询条件
     */
    private StatementBuilder<T,Integer> fetchQueryBuilder(StatementBuilder<T, Integer> queryBuilder, WhereInfo whereInfo) throws SQLException {
        List<com.easydblib.info.Where> wheres = whereInfo.wheres;
        if(!wheres.isEmpty()){
            Where<T, Integer> whereBuilder = queryBuilder.where();
            boolean isFirst = true;
            for(int i = 0; i < wheres.size(); i++){
                com.easydblib.info.Where where = wheres.get(i);
                if(TextUtils.isEmpty(where.name))
                    continue;
                // 处理and-or
                isFirst = appendAnd(whereBuilder,where,isFirst);
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
        return queryBuilder;
    }

    /**
     * 追加连接符
     */
    private boolean appendAnd(Where<T, Integer> whereBuilder,com.easydblib.info.Where where, boolean isFirst){
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
    private void orderBy(QueryBuilder<T, Integer> queryBuilder, Map<String,Boolean> orders){
        if(!orders.isEmpty()){
            for(Map.Entry<String,Boolean> order : orders.entrySet()){
                queryBuilder.orderBy(order.getKey(),order.getValue());
            }
        }
    }



}
