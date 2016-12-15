package com.myeasydb.core;



import java.sql.SQLException;

/**
 *
 * easy数据库辅助类
 * @author : zhousf
 */
public class EasyDBHelper extends BaseDBHelper {

	//版本号
	private static final int DB_VERSION = 3;

	//数据库名称
	private static final String DB_NAME = "easy_android.db";

	//数据表清单
	private static final Class<?>[] tables = {
		com.myeasydb.demo.model.SimpleData.class
	};

	private static EasyDBHelper helper = null;

	public static EasyDBHelper get(){
		if(null == helper){
			synchronized (EasyDBHelper.class){
				if(null == helper){
					helper = new EasyDBHelper();
				}
			}
		}
		return helper;
	}

	public EasyDBHelper() {
		super(com.myeasydb.core.BaseApplication.getApplication(),DB_NAME, null, DB_VERSION, tables);
	}

	@Override
	protected BaseDBHelper initHelper() {
		return get();
	}

	@Override
	protected boolean upgrade(int oldVersion, int newVersion) throws SQLException {
		if(oldVersion<2){
			getDao(com.myeasydb.demo.model.SimpleData.class).executeRaw("ALTER TABLE'simpledata' ADD COLUMN ext TEXT DEFAULT '';");
		}
		if(oldVersion<3){
			getDao(com.myeasydb.demo.model.SimpleData.class).executeRaw("ALTER TABLE'simpledata' ADD COLUMN ext1 TEXT DEFAULT '0';");
		}
		return true;
	}

}
