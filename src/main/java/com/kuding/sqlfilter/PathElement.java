package com.kuding.sqlfilter;

public class PathElement extends Element<NullValue> implements Comparable<PathElement> {

	public PathElement() {
		super();
	}

	public PathElement(String field, NullValue value) {
		super(field, value);
	}

	public static PathElement path(String field) {
		return new PathElement(field, NullValue.create());
	}

	@Override
	public int compareTo(PathElement o) {
		return this.getField().compareTo(o.getField());
	}
}
