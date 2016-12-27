package com.easydblib.dao;

import android.text.TextUtils;
import android.util.Log;

import com.easydblib.EasyDBConfig;
import com.easydblib.callback.EasyRun;
import com.easydblib.handler.HandlerHelper;
import com.easydblib.handler.MessageInfo;
import com.easydblib.helper.BaseDBHelper;
import com.easydblib.info.WhereInfo;
import com.easydblib.util.CompareUtil;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.TableUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 数据库操作接口实现类
 * @author : zhousf
 */
public class BaseDaoImp<T> implements BaseDao<T> {

    private final String TAG = this.getClass().getName();

    public Dao<T, Integer> dao;
    private Class<T> mClass;
    private String databaseName;
    private static ExecutorService executorService;

    public BaseDaoImp(BaseDBHelper helper, Class<T> clazz) {
        mClass = clazz!=null ? clazz : initClazz();
        dao = helper.fetchDao(mClass);
        databaseName = helper.getDatabaseName();
        if(executorService == null)
            executorService = Executors.newCachedThreadPool();
    }

    private Class<T> initClazz(){
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if(params[0] instanceof Class){
            return (Class<T>) params[0];
        }else{
            return null;
        }
    }

    @Override
    public int create(T model) {
        int line = 0;
        try {
            long start = getTime();
            prepareDeal();
            line = dao.create(model);
            doLog("create["+(getTime()-start)+"ms] 影响行数："+line);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }

    @Override
    public int create(List<T> list) {
        int line = 0;
        try {
            long start = getTime();
            prepareDeal();
            line = dao.create(list);
            doLog("create["+(getTime()-start)+"ms] 影响行数："+line);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }

    @Override
    public int delete(T model) {
        int line = 0;
        try {
            long start = getTime();
            prepareDeal();
            line = dao.delete(model);
            doLog("delete["+(getTime()-start)+"ms] 影响行数："+line);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }

    @Override
    public int delete(List<T> list) {
        int line = 0;
        try {
            long start = getTime();
            prepareDeal();
            line = dao.delete(list);
            doLog("delete["+(getTime()-start)+"ms] 影响行数："+line);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }

    @Override
    public int update(T model) {
        int line = 0;
        try {
            long start = getTime();
            prepareDeal();
            line = dao.update(model);
            doLog("update["+(getTime()-start)+"ms] 影响行数："+line);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }

    @Override
    public int update(T model, WhereInfo whereInfo) {
        int line = 0;
        try {
            long start = getTime();
            prepareDeal();
            QueryBuilder<T,Integer> queryBuilder = dao.queryBuilder();
            //获取第一条数据
            T data =  dao.queryForFirst(fetchQueryBuilder(queryBuilder, whereInfo));
            //若不存在则添加
            if(data == null){
                line = dao.create(model);
            }else{
                //存在则更新
                model = CompareUtil.castModel(model,data);
                line = dao.update(model);
            }
            doLog("update["+(getTime()-start)+"ms] 影响行数："+line);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }

    @Override
    public List<T> queryForAll() {
        List<T> all = new ArrayList<T>();
        try {
            long start = getTime();
            prepareDeal();
            all = dao.queryForAll();
            doLog("queryForAll["+(getTime()-start)+"ms] 影响行数："+all.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return all;
    }

    @Override
    public List<T> queryForAll(WhereInfo whereInfo) {
        List<T> all = new ArrayList<T>();
        try {
            long start = getTime();
            prepareDeal();
            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            orderBy(queryBuilder, whereInfo.orders);
            all =  dao.query(queryBuilder.prepare());
            doLog("queryForAll["+(getTime()-start)+"ms] 影响行数："+all.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return all;
    }

    @Override
    public List<T> query(WhereInfo whereInfo) {
        List<T> all = new ArrayList<T>();
        try {
            long start = getTime();
            prepareDeal();
            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            orderBy(queryBuilder, whereInfo.orders);
            all = dao.query(fetchQueryBuilder(queryBuilder, whereInfo));
            doLog("query["+(getTime()-start)+"ms] 影响行数："+all.size());
        } catch (Exception e){
            e.printStackTrace();
        }
        return all;
    }

    @Override
    public List<T> queryLimit(WhereInfo whereInfo) {
        List<T> all = new ArrayList<T>();
        try {
            long start = getTime();
            prepareDeal();
            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            orderBy(queryBuilder, whereInfo.orders);
            int offset = whereInfo.currentPage;
            if(offset != 0){
                offset = (whereInfo.currentPage -1) * whereInfo.limit + whereInfo.size;
            }
            queryBuilder.offset((long)offset);
            queryBuilder.limit((long) whereInfo.limit);
            all = dao.query(fetchQueryBuilder(queryBuilder, whereInfo));
            whereInfo.currentPage = ++whereInfo.currentPage;
            whereInfo.size = all.size();
            doLog("queryLimit["+(getTime()-start)+"ms] 影响行数："+all.size());
        } catch (Exception e){
            e.printStackTrace();
        }
        return all;
    }

    @Override
    public List<T> query(QueryBuilder<T, Integer> queryBuilder){
        List<T> all = new ArrayList<T>();
        try {
            long start = getTime();
            prepareDeal();
            all = dao.query(queryBuilder.prepare());
            doLog("query["+(getTime()-start)+"ms] 影响行数："+all.size());
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
    public long countOf(WhereInfo info) {
        long line = 0;
        try {
            long start = getTime();
            prepareDeal();
            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.setCountOf(true);
            if(null != info){
                line = dao.countOf(fetchQueryBuilder(queryBuilder,info));
            }else{
                line = dao.countOf();
            }
            doLog("countOf["+(getTime()-start)+"ms] 影响行数："+line);
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
            long start = getTime();
            prepareDeal();
            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            all = dao.query(fetchQueryBuilder(queryBuilder, whereInfo));
            doLog("isExist["+(getTime()-start)+"ms] 影响行数："+all.size());
        } catch (Exception e){
            e.printStackTrace();
        }
        return !all.isEmpty();
    }

    @Override
    public int executeRaw(String statement, String... arguments) {
        int line = 0;
        try {
            long start = getTime();
            prepareDeal();
            line = dao.executeRaw(statement,arguments);
            doLog("executeRaw["+(getTime()-start)+"ms] 影响行数："+line);
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
        prepareDeal();
        return dao.getTableName();
    }

    @Override
    public int clearTable(){
        int line = 0;
        try {
            long start = getTime();
            prepareDeal();
            line = TableUtils.clearTable(dao.getConnectionSource(),mClass);
            doLog("clearTable["+(getTime()-start)+"ms] 影响行数："+line);
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
                long start = getTime();
                line = TableUtils.dropTable(dao.getConnectionSource(),mClass,false);
                doLog("dropTable["+(getTime()-start)+"ms] 影响行数："+line);
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
            long start = getTime();
            prepareDeal();
            transactionManager.callInTransaction(callable);
            doLog("callInTransaction["+(getTime()-start)+"ms] ");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public <CT> CT callBatchTasks(Callable<CT> callable){
        try {
            long start = getTime();
            prepareDeal();
            CT ct =  dao.callBatchTasks(callable);
            doLog("callBatchTasks["+(getTime()-start)+"ms] ");
            return ct;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> void asyncTask(final EasyRun<T> easyRun) {
        if(null != easyRun){
            prepareDeal();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        long start = getTime();
                        T data = easyRun.run();
                        doLog("asyncTask["+(getTime()-start)+"ms] ");
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
     * 预处理
     */
    private void prepareDeal(){
        checkTable();
    }

    /**
     * 检查数据表
     */
    private void checkTable(){
        try {
            if(!dao.isTableExists()){
                TableUtils.createTable(dao.getConnectionSource(),mClass);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建查询条件
     */
    private PreparedQuery<T> fetchQueryBuilder(QueryBuilder<T, Integer> queryBuilder, WhereInfo whereInfo) throws SQLException {
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
        return queryBuilder.prepare();
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

    long getTime(){
        return System.currentTimeMillis();
    }

    private void doLog(String msg){
        if(EasyDBConfig.showDBLog)
            Log.d(TAG,msg+" | "+mClass.getSimpleName()+" | "+databaseName);
    }

}
