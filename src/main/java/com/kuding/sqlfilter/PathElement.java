//package com.kuding.sqlfilter;
//
//import jakarta.persistence.criteria.CriteriaBuilder;
//import jakarta.persistence.criteria.Expression;
//import jakarta.persistence.criteria.Path;
//
//public class PathElement extends Element<NullValue> implements Comparable<PathElement>, SelectElement {
//
//	public PathElement() {
//		super();
//	}
//
//	public PathElement(String field, NullValue value) {
//		super(field, value);
//	}
//
//	public PathElement(String field) {
//		super(field, NullValue.create());
//	}
//
//	public static PathElement path(String field) {
//		return new PathElement(field, NullValue.create());
//	}
//
//	@Override
//	public int compareTo(PathElement o) {
//		return this.getField().compareTo(o.getField());
//	}
//
//	@Override
//	public Expression<?> select(CriteriaBuilder builder, Path<?> path) {
//		return PathUtils.getPath(field, path);
//	}
//}
