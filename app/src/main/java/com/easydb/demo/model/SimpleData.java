package com.easydb.demo.model;

import com.j256.ormlite.field.DatabaseField;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple demonstration object we are creating and persisting to the database.
 */
public class SimpleData {

	@DatabaseField(generatedId = true)
	public int id;
	@DatabaseField(index = true)
	public int index;
	@DatabaseField
	public String description;
	@DatabaseField
	public Date date;
	@DatabaseField
	public boolean group1;
	@DatabaseField
	public boolean group2;
	@DatabaseField
	public String ext;

	public SimpleData() {
	}

	public SimpleData(int index,String description) {
		this.date = new Date(System.currentTimeMillis());
		this.index = index;
		this.description = description;
		this.group1 = ((index % 2) == 0);
		this.group2 = ((index % 4) == 0);
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
