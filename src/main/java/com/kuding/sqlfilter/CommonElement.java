package com.kuding.sqlfilter;

import java.util.LinkedList;
import java.util.List;

public class CommonElement {

	private List<Element<?>> list = new LinkedList<>();

	public List<Element<?>> getList() {
		return list;
	}

	public void setList(List<Element<?>> list) {
		this.list = list;
	}

	public static CommonElement create() {
		return new CommonElement();
	}

	public <T> CommonElement addElement(String field, T value) {
		list.add(new Element<T>(field, value));
		return this;
	}

}
