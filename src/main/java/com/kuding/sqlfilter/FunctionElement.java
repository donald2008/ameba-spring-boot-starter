package com.kuding.sqlfilter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;

import com.kuding.enums.SqlFunction;

public class FunctionElement extends Element<SqlFunction> implements SelectElement {

	public FunctionElement(String field, SqlFunction value) {
		super(field, value);
	}

	public static FunctionElement sum(String field) {
		return new FunctionElement(field, SqlFunction.SUM);
	}

	public static FunctionElement avg(String field) {
		return new FunctionElement(field, SqlFunction.AVG);
	}

	public static FunctionElement count(String field) {
		return new FunctionElement(field, SqlFunction.COUNT);
	}

	@SuppressWarnings("unchecked")
	public Expression<?> expression(CriteriaBuilder builder, Path<?> path) {
		switch (value) {
		case NO_FUN:
			return path;
		case COUNT:
			return builder.count(path);
		default:
			break;
		}
		if (Number.class.isAssignableFrom(path.getJavaType())) {
			Expression<Number> numPath = (Expression<Number>) path;
			switch (value) {
			case SUM:
				return builder.sum(numPath);
			case AVG:
				return builder.avg(numPath);
			default:
				throw new UnsupportedOperationException("暂不支持");
			}
		}
		return path;
	}

	@Override
	public Expression<?> select(CriteriaBuilder builder, Path<?> path) {
		Path<?> nextPath = PathUtils.getPath(field, path);
		return expression(builder, nextPath);
	}
}
