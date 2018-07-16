package com.kuding.sqlfilter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.criteria.JoinType;

import com.kuding.enums.OrderEnum;
import com.kuding.enums.SqlFunction;

public class CommonFilter {

	private final List<Element<? extends Object>> updatableList = new LinkedList<>();

	private final List<FilterElement<? extends Object>> list = new LinkedList<>();

	private final List<JoinTable> joinList = new LinkedList<>();

	private final List<OrderBy> orderList = new LinkedList<>();

	private final List<String> selectors = new LinkedList<>();

	private final List<GroupingElement<?>> groupingBy = new LinkedList<>();

	private FilterElement<? extends Object> currentElement;

	public List<FilterElement<? extends Object>> getList() {
		return list;
	}

	public FilterElement<? extends Object> getCurrentElement() {
		return currentElement;
	}

	public List<OrderBy> getOrderList() {
		return orderList;
	}

	public List<GroupingElement<?>> getGroupingBy() {
		return groupingBy;
	}

	public List<String> getSelectors() {
		return selectors;
	}

	public List<JoinTable> getJoinList() {
		return joinList;
	}

	public <T> CommonFilter update(String field, T value) {
		updatableList.add(new Element<Object>(field, value));
		return this;
	}

	public List<Element<? extends Object>> getUpdatableList() {
		return updatableList;
	}

	public <T> CommonFilter eq(String field, T value) {
		return filter(field, value, FilterSymbol.EQ);
	}

	public <T> CommonFilter neq(String field, T value) {
		return filter(field, value, FilterSymbol.NEQ);
	}

	public <T> CommonFilter like(String field, T value) {
		return filter(field, value, FilterSymbol.LIKE);
	}

	public CommonFilter isNull(String field) {
		return filter(field, NullValue.create(), FilterSymbol.ISNULL);
	}

	public <T> CommonFilter isNotNull(String field) {
		return filter(field, NullValue.create(), FilterSymbol.ISNOTNULL);
	}

	public <T> CommonFilter in(String field, Collection<T> value) {
		return filter(field, value, FilterSymbol.IN);
	}

	public <T> CommonFilter notIn(String field, Collection<T> value) {
		return filter(field, value, FilterSymbol.NOTIN);
	}

	public <T extends Comparable<T>> CommonFilter lt(String field, T value) {
		return comparableFilter(field, value, FilterSymbol.LT);
	}

	public <T extends Comparable<T>> CommonFilter le(String field, T value) {
		return comparableFilter(field, value, FilterSymbol.LE);
	}

	public <T extends Comparable<T>> CommonFilter gt(String field, T value) {
		return comparableFilter(field, value, FilterSymbol.GT);
	}

	public <T extends Comparable<T>> CommonFilter ge(String field, T value) {
		return comparableFilter(field, value, FilterSymbol.GE);
	}

	private <T> CommonFilter filter(String field, T value, FilterSymbol filterSymbol) {
		currentElement = new FilterElement<T>(field, value, filterSymbol);
		list.add(currentElement);
		return this;
	}

	private <T extends Comparable<T>> CommonFilter comparableFilter(String field, T value, FilterSymbol filterSymbol) {
		currentElement = new ComparebleFilterElement<T>(field, value, filterSymbol);
		list.add(currentElement);
		return this;
	}

	public <T extends FilterElement<?>> CommonFilter joinFilter(String field) {
		list.remove(currentElement);
		currentElement = new FilterElement<FilterElement<?>>(field, currentElement, null);
		list.add(currentElement);
		return this;
	}

	public CommonFilter orderBy(String field, OrderEnum order) {
		orderList.add(new OrderBy(field, order));
		return this;
	}

	public CommonFilter groupBy(GroupingElement<?>... groupFields) {
		for (GroupingElement<?> field : groupFields) {
			groupingBy.add(field);
		}
		return this;
	}

	public CommonFilter groupBy(Collection<GroupingElement<?>> groupFields) {
		groupingBy.addAll(groupFields);
		return this;
	}

	public CommonFilter groupBy(String... groupFields) {
		for (String field : groupFields) {
			groupingBy.add(new GroupingElement<>(field, SqlFunction.NO_FUN, null));
		}
		return this;
	}

	public CommonFilter select(String... selectField) {
		for (String field : selectField)
			selectors.add(field);
		return this;
	}

	public CommonFilter innerJoin(String table) {
		joinList.add(new JoinTable(table, JoinType.INNER));
		return this;
	}

	public CommonFilter leftJoin(String table) {
		joinList.add(new JoinTable(table, JoinType.LEFT));
		return this;
	}

	public CommonFilter rightJoin(String table) {
		joinList.add(new JoinTable(table, JoinType.RIGHT));
		return this;
	}
}
