package com.easydb.core;


import android.os.Environment;

import com.easydb.demo.model.SimpleData;
import com.easydblib.helper.BaseDBHelper;

import java.sql.SQLException;

/**
 *
 * 数据库辅助类
 * @author : zhousf
 */
public class EasyDBHelper extends BaseDBHelper {

	//版本号
	private static final int DB_VERSION = 2;

	//数据库存放路径
	private static final String DB_PATH = Environment.getExternalStorageDirectory() + "/easy_db";

	//数据库名称
	private static final String DB_NAME = "easy_android.db";

	//数据表清单
	private static final Class<?>[] tables = {
		SimpleData.class
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

	private EasyDBHelper() {
		//系统数据库
//		super(BaseApplication.getApplication(), null,DB_NAME,DB_VERSION,tables);
		//SD卡数据库
		super(BaseApplication.getApplication(), DB_PATH,DB_NAME,DB_VERSION,tables);
	}

	@Override
	protected BaseDBHelper initHelper() {
		return get();
	}

	@Override
	protected boolean upgrade(int oldVersion, int newVersion) throws SQLException {
		if(oldVersion < 2){
			//增加字段ext
			getDao(SimpleData.class).executeRaw("ALTER TABLE'simpledata' ADD COLUMN ext TEXT DEFAULT 'default';");
		}
		return true;
	}

}
