package com.kuding.sqlfilter;

import com.kuding.exceptions.JpaAmebaException;
import com.kuding.sqlfilter.functional.SelectCondition;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;

public class Selectors {

	public static SelectCondition count(String field) {
		return (builder, path) -> builder.count(path(path, field));
	}

	public static SelectCondition select(String field) {
		return (builder, path) -> path(path, field);
	}

	@SuppressWarnings("unchecked")
	public static SelectCondition avg(String field) {
		return (builder, path) -> {
			var res = path(path, field);
			if (Number.class.isAssignableFrom(res.getJavaType())) {
				var numRes = (Expression<Number>) res;
				return builder.avg(numRes);
			} else
				throw new JpaAmebaException("the given fieldType cannot calculate avg");
		};
	}

	@SuppressWarnings("unchecked")
	public static SelectCondition max(String field) {
		return (builder, path) -> {
			var res = path(path, field);
			if (Number.class.isAssignableFrom(res.getJavaType())) {
				var numRes = (Expression<Number>) res;
				return builder.max(numRes);
			} else
				throw new JpaAmebaException("the given fieldType cannot calculate max");
		};
	}

	private static Path<?> path(Path<?> path, String field) {
		var result = path;
		for (String element : field.split(",")) {
			result = result.get(element);
		}
		return result;
	}
}
