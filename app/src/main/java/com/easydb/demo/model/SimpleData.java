package com.easydb.demo.model;

import com.easydblib.annotation.TableModel;
import com.j256.ormlite.field.DatabaseField;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple demonstration object we are creating and persisting to the database.
 */
@TableModel
public class SimpleData extends BaseModel{

	public static final String ID = "id";

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
		super.father = "基类"+index;
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
		sb.append(", ").append("father=").append(super.father);
		return sb.toString();
	}
}
