package com.easydblib.dao;

import android.util.Log;

import com.easydblib.helper.BaseDBHelper;
import com.easydblib.info.DBInfo;
import com.j256.ormlite.dao.Dao;
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

/**
 * 数据库操作接口实现类
 * @author : zhousf
 */
public class BaseDaoImp<T> implements BaseDao<T> {

    private final String TAG = this.getClass().getName();

    public Dao<T, Integer> dao;
    private Class<T> mClass;
    private String databaseName;

    public BaseDaoImp(BaseDBHelper helper, Class<T> clazz) {
        mClass = clazz!=null ? clazz : initClazz();
        dao = helper.fetchDao(mClass);
        databaseName = helper.getDatabaseName();
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
            line = dao.create(model);
            doLog("create["+(getTime()-start)+"ms] 影响行数："+line);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return line;
    }

    @Override
    public int create(List<T> list) {
        int line = 0;
        try {
            long start = getTime();
            line = dao.create(list);
            doLog("create["+(getTime()-start)+"ms] 影响行数："+line);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return line;
    }

    @Override
    public int delete(T model) {
        int line = 0;
        try {
            long start = getTime();
            line = dao.delete(model);
            doLog("delete["+(getTime()-start)+"ms] 影响行数："+line);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return line;
    }

    @Override
    public int delete(List<T> list) {
        int line = 0;
        try {
            long start = getTime();
            line = dao.delete(list);
            doLog("delete["+(getTime()-start)+"ms] 影响行数："+line);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return line;
    }

    @Override
    public int update(T model) {
        int line = 0;
        try {
            long start = getTime();
            line = dao.update(model);
            doLog("update["+(getTime()-start)+"ms] 影响行数："+line);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return line;
    }

    @Override
    public List<T> queryForAll() {
        List<T> all = new ArrayList<T>();
        try {
            long start = getTime();
            all = dao.queryForAll();
            doLog("queryForAll["+(getTime()-start)+"ms] 影响行数："+all.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return all;
    }

    @Override
    public List<T> queryForAll(DBInfo dbInfo) {
        List<T> all = new ArrayList<T>();
        try {
            long start = getTime();
            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            orderBy(queryBuilder,dbInfo.orders);
            all =  dao.query(queryBuilder.prepare());
            doLog("queryForAll["+(getTime()-start)+"ms] 影响行数："+all.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return all;
    }

    @Override
    public List<T> query(DBInfo dbInfo) {
        List<T> all = new ArrayList<T>();
        try {
            long start = getTime();
            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            orderBy(queryBuilder, dbInfo.orders);
            all = dao.query(fetchQueryBuilder(queryBuilder, dbInfo.wheres));
            doLog("query["+(getTime()-start)+"ms] 影响行数："+all.size());
        } catch (Exception e){
            e.printStackTrace();
        }
        return all;
    }

    @Override
    public List<T> queryLimit(DBInfo dbInfo) {
        List<T> all = new ArrayList<T>();
        try {
            long start = getTime();
            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            orderBy(queryBuilder,dbInfo.orders);
            int offset = dbInfo.currentPage;
            if(offset != 0){
                offset = (dbInfo.currentPage -1) * dbInfo.limit + dbInfo.size;
            }
            queryBuilder.offset((long)offset);
            queryBuilder.limit((long)dbInfo.limit);
            all = dao.query(fetchQueryBuilder(queryBuilder,dbInfo.wheres));
            dbInfo.currentPage = ++dbInfo.currentPage;
            dbInfo.size = all.size();
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
    public long countOf(DBInfo info) {
        long line = 0;
        try {
            long start = getTime();
            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            queryBuilder.setCountOf(true);
            if(null != info){
                line = dao.countOf(fetchQueryBuilder(queryBuilder,info.wheres));
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
    public boolean isExist(DBInfo dbInfo){
        List<T> all = new ArrayList<T>();
        try {
            long start = getTime();
            QueryBuilder<T, Integer> queryBuilder = dao.queryBuilder();
            all = dao.query(fetchQueryBuilder(queryBuilder, dbInfo.wheres));
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

    public int clearTable(){
        int line = 0;
        try {
            long start = getTime();
            line = TableUtils.clearTable(dao.getConnectionSource(),mClass);
            doLog("clearTable["+(getTime()-start)+"ms] 影响行数："+line);
        } catch (Exception e){
            e.printStackTrace();
        }
        return line;
    }

    private PreparedQuery<T> fetchQueryBuilder(QueryBuilder<T, Integer> queryBuilder, Map<String,Object> wheres) throws SQLException {
        if(!wheres.isEmpty()){
            Where<T, Integer> whereBuilder = queryBuilder.where();
            boolean isFirst = true;
            for(Map.Entry<String,Object> where : wheres.entrySet()){
                if(isFirst){
                    whereBuilder.eq(where.getKey(), where.getValue());
                    isFirst = false;
                }else{
                    whereBuilder.and().eq(where.getKey(), where.getValue());
                }
            }
        }
        return queryBuilder.prepare();
    }

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
        Log.d(TAG,msg+" | "+mClass.getSimpleName()+" | "+databaseName);
    }

}
