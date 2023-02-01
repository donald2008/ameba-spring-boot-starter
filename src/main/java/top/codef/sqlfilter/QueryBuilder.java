package top.codef.sqlfilter;

import top.codef.sqlfilter.typed.TypedCommonFilter;

public class QueryBuilder {

	public static CommonFilter createFilter() {
		return new CommonFilter();
	}

	public static <T> TypedCommonFilter<T> createFilter(Class<T> clazz) {
		return new TypedCommonFilter<>(clazz);
	}
}
