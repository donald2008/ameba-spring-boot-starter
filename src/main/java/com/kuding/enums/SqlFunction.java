package com.kuding.enums;

public enum SqlFunction {

	NO_FUN(""), DATE("date"), SUM("sum"), COUNT("count");

	private final String function;

	private SqlFunction(String function) {
		this.function = function;
	}

	public String getFunction() {
		return function;
	}
}
