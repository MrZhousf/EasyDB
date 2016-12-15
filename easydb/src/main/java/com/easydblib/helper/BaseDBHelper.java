package com.easydblib.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.easydblib.dao.BaseDao;
import com.easydblib.dao.BaseDaoImp;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据库辅助基类
 * @author : zhousf
 * 引入版本：com.j256.ormlite:ormlite-android:5.0
 */
public abstract class BaseDBHelper extends OrmLiteSqliteOpenHelper {

	private Class<?>[] modelClasses;

	private Map<String,Dao> daoMap = new ConcurrentHashMap<>();

	private Map<String,BaseDaoImp> helperMap = new ConcurrentHashMap<>();

	protected abstract BaseDBHelper initHelper();

	/**
	 * 自定义数据库升级：true自定义，false默认升级（删除所有表并重新创建）
     */
	protected abstract boolean upgrade(int oldVersion, int newVersion) throws SQLException;

	public BaseDBHelper(Context context, String databaseName,
						SQLiteDatabase.CursorFactory factory, int databaseVersion,
						Class<?>[] modelClasses) {
		super(context.getApplicationContext(), databaseName, factory, databaseVersion);
		this.modelClasses = modelClasses;
	}


	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
		try {
			for (Class<?> clazz: modelClasses) {
				TableUtils.createTable(connectionSource, clazz);
			}
		} catch (SQLException e) {
			Log.e("BaseDBHelper", "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			if(!upgrade(oldVersion,newVersion)){
				for (Class<?> clazz: modelClasses) {
					TableUtils.dropTable(connectionSource, clazz, true);
				}
				// after we drop the old databases, we create the new ones
				onCreate(database, connectionSource);
			}
		} catch (SQLException e) {
			Log.e("BaseDBHelper", "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}


	public <T> Dao<T,Integer> fetchDao(Class<T> clazz) {
		Dao<T,Integer> dao = null;
		try {
			String className = clazz.getSimpleName();
			if (!daoMap.containsKey(className)) {
				dao = super.getDao(clazz);
				if(null != dao)
					daoMap.put(className, dao);
			}
			dao = daoMap.get(className);
		}catch (SQLException e){
			e.printStackTrace();
		}
		return dao;
	}

	public <T> BaseDao<T> dao(Class<T> clazz){
		String className = clazz.getSimpleName();
		if(!helperMap.containsKey(className)){
			helperMap.put(className,new BaseDaoImp<>(initHelper(),clazz));
		}
		return helperMap.get(className);
	}

	@Override
	public void close() {
		super.close();
		daoMap.clear();
		helperMap.clear();
	}


}