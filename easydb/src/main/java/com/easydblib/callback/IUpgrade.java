package com.easydblib.callback;

import com.easydblib.helper.DBHelper;

import java.sql.SQLException;

/**
 * 数据库升级接口
 * @author : zhousf
 */
public interface IUpgrade {

    void upgrade(DBHelper dbHelper, int oldVersion, int newVersion) throws SQLException;

}
