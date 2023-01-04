package com.kuding.sqlfilter;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;

import com.kuding.exceptions.JpaAmebaException;

public class Selectors {

	public static SelectElement count(String field) {
		return (builder, path) -> builder.count(path(path, field));
	}

	public static SelectElement select(String field) {
		return (builder, path) -> path(path, field);
	}

	@SuppressWarnings("unchecked")
	public static SelectElement avg(String field) {
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
	public static SelectElement max(String field) {
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
