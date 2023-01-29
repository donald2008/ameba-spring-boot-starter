package com.kuding.sqlfilter;

import com.kuding.sqlfilter.typed.TypedCommonFilter;

public class QueryBuilder {

	public static CommonFilter createFilter() {
		return new CommonFilter();
	}

	public static <T> TypedCommonFilter<T> createFilter(Class<T> clazz) {
		return new TypedCommonFilter<>(clazz);
	}
}
