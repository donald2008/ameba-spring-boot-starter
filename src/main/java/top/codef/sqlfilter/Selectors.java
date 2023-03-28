package top.codef.sqlfilter;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import top.codef.exceptions.JpaAmebaException;
import top.codef.sqlfilter.functional.SelectCondition;

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

	@SuppressWarnings("unchecked")
	public static SelectCondition abs(String field) {
		return (builder, path) -> {
			var res = path(path, field);
			if (Number.class.isAssignableFrom(res.getJavaType())) {
				var numRes = (Expression<? extends Number>) res;
				return builder.abs(numRes);
			} else {
				throw new JpaAmebaException("the given fieldType cannot calculate abs");
			}
		};
	}

	private static Path<?> path(Path<?> path, String field) {
		var result = path;
		for (String element : field.split("\\.")) {
			result = result.get(element);
		}
		return result;
	}
}
