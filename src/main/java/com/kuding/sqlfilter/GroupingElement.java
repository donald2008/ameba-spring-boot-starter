package com.kuding.sqlfilter;

import com.kuding.enums.SqlFunction;

public class GroupingElement<T> extends Element<SqlFunction> {

	private Class<T> clazz;//TODO 后期加groupBy与函数
	
	public GroupingElement(String field, SqlFunction value, Class<T> clazz) {
		super(field, value);
		this.clazz = clazz;
	}

	public Class<T> getClazz() {
		return clazz;
	}

	public void setClazz(Class<T> clazz) {
		this.clazz = clazz;
	}

	
}
