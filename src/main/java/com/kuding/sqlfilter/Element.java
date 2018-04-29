package com.kuding.sqlfilter;

public class Element<T> {

	protected String field;
	protected T value;

	public Element(String field, T value) {
		this.field = field;
		this.value = value;
	}

	public Element() {
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Element [field=" + field + ", value=" + value + "]";
	}

}
