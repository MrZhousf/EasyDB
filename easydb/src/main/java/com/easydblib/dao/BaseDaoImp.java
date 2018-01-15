package com.easydblib.dao;

import com.easydblib.callback.EasyRun;
import com.easydblib.handler.EasyDBProxyHandler;
import com.easydblib.helper.BaseDBHelper;
import com.easydblib.info.OrderInfo;
import com.easydblib.info.WhereInfo;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 数据库操作接口实现类
 * @author : zhousf
 */
public class BaseDaoImp<T> implements DBDao<T> {

    private Dao<T, Long> dao;

    private RealBaseDao<T> baseDao;

    public BaseDaoImp(BaseDBHelper helper, Class<T> clazz) {
        try {
            Class<T> mClass = clazz!=null ? clazz : initClazz();
            String databaseName = helper.getDatabaseName();
            dao = helper.fetchDao(mClass);
            baseDao = new EasyDBProxyHandler<T>(helper,dao,mClass,databaseName).getProxy(new RealBaseDaoImpl<>(dao));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressWarnings(value = "all")
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
    public int add(T model) {
        return baseDao.add(model).getLine();
    }

    @Override
    public int add(List<T> list) {
        return baseDao.add(list).getLine();
    }

    @Override
    public int addOrUpdate(T model) {
        return baseDao.addOrUpdate(model).getLine();
    }

    @Override
    public T addIfNotExists(T model) {
        return baseDao.addIfNotExists(model).getModel();
    }

    @Override
    public int delete(T model) {
        return baseDao.delete(model).getLine();
    }

    @Override
    public int delete(List<T> list) {
        return baseDao.delete(list).getLine();
    }

    @Override
    public int delete(WhereInfo whereInfo) {
        return baseDao.delete(whereInfo).getLine();
    }

    @Override
    public int update(T model) {
        return baseDao.update(model).getLine();
    }

    @Override
    public int update(WhereInfo whereInfo) {
        return baseDao.update(whereInfo).getLine();
    }

    @Override
    public List<T> queryAll() {
        return baseDao.queryAll().getList();
    }

    @Override
    public List<T> queryAll(OrderInfo orderInfo) {
        return baseDao.queryAll(orderInfo).getList();
    }

    @Override
    public List<T> query(WhereInfo whereInfo) {
        return baseDao.query(whereInfo).getList();
    }

    @Override
    public List<T> queryLimit(WhereInfo whereInfo) {
        return baseDao.queryLimit(whereInfo).getList();
    }

    @Override
    public List<T> query(QueryBuilder<T, Integer> queryBuilder){
        return baseDao.query(queryBuilder).getList();
    }

    @Override
    public long countOf() {
        return baseDao.countOf(null).getCount();
    }

    @Override
    public long countOf(WhereInfo whereInfo) {
        return baseDao.countOf(whereInfo).getCount();
    }

    @Override
    public boolean isExist(WhereInfo whereInfo){
        return baseDao.isExist(whereInfo).isExist();
    }

    @Override
    public int executeRaw(String statement, String... arguments) {
        return baseDao.executeRaw(statement,arguments).getLine();
    }

    @Override
    public Dao<T, Long> fetchDao(){
        return dao;
    }

    @Override
    public String getTableName() {
        return baseDao.getTableName();
    }

    @Override
    public int clearTable(){
       return baseDao.clearTable().getLine();
    }

    @Override
    public int dropTable(){
        return baseDao.dropTable().getLine();
    }

    @Override
    public void callInTransaction(Callable<T> callable){
        baseDao.callInTransaction(callable);
    }

    @Override
    public <CT> CT callBatchTasks(Callable<CT> callable){
        return baseDao.callBatchTasks(callable);
    }

    @Override
    public <T> void asyncTask(final EasyRun<T> easyRun) {
        baseDao.asyncTask(easyRun);
    }


}
