package com.easydb.demo.model;

import com.easydblib.annotation.TableModel;
import com.j256.ormlite.field.DatabaseField;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple demonstration object we are creating and persisting to the database.
 */
@TableModel
public class SimpleData {

	//以下字段别名可以用过AndroidStudio插件生成
	public final static String _id = "id";
	public final static String _index = "index";
	public final static String _description = "description";
	public final static String _myDouble = "myDouble";
	public final static String _myFloat = "myFloat";
	public final static String _myLong = "myLong";
	public final static String _date = "date";
	public final static String _group1 = "group1";
	public final static String _group2 = "group2";
	public final static String _ext = "ext";

	@DatabaseField(generatedId = true)
	public int id;
	@DatabaseField(index = true)
	public int index;
	@DatabaseField
	public String description;
	@DatabaseField
	public double myDouble;
	@DatabaseField
	public float myFloat;
	@DatabaseField
	public long myLong;
	@DatabaseField
	public Date date;
	@DatabaseField
	public boolean group1;
	@DatabaseField
	public boolean group2;
	@DatabaseField
	public String ext;


	//必须有无参构造方法
	public SimpleData() {
	}

	public SimpleData(int index,String description) {
		this.date = new Date(System.currentTimeMillis());
		this.index = index;
		this.description = description;
		this.group1 = ((index % 2) == 0);
		this.group2 = ((index % 4) == 0);
		this.myDouble = 5.0;
		this.myFloat = 2;
		this.myLong = 6;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id=").append(id);
		sb.append(", ").append("index=").append(index);
		sb.append(", ").append("description=").append(description);
		SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.S");
		sb.append(", ").append("date=").append(dateFormatter.format(date));
		sb.append(", ").append("group1=").append(group1);
		sb.append(", ").append("group2=").append(group2);
		sb.append(", ").append("ext=").append(ext);
		return sb.toString();
	}
}
