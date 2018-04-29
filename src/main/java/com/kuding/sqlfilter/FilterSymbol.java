package com.kuding.sqlfilter;

public enum FilterSymbol {

	EQ("等于"), NEQ("不等于"), LIKE("近似"), ISNULL("为null"), ISNOTNULL("不为null"), IN("在列表内"), NOTIN("不在列表内"), LT("小于"), LE(
			"小于等于"), GT("大于"), GE("大于等于");

	private final String explain;

	private FilterSymbol(String explain) {
		this.explain = explain;
	}

	public String getExplain() {
		return explain;
	}
}
