package com.kuding.sqlfilter;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.springframework.util.Assert;

import com.kuding.enums.OrderEnum;

import jakarta.persistence.criteria.JoinType;

public class CommonFilter {

	protected final List<Element<? extends Object>> updatableList = new LinkedList<>();

	protected final List<FilterElement<? extends Object>> list = new LinkedList<>();

	protected final List<JoinTable> joinList = new LinkedList<>();

	protected final List<OrderBy> orderList = new LinkedList<>();

	protected final List<SelectElement> selectors = new LinkedList<>();

	protected final GroupingElement groupingBy = new GroupingElement();

	protected Integer limitCount;

	protected Integer limitStart;

	protected FilterElement<? extends Object> currentElement;

	public List<FilterElement<? extends Object>> getList() {
		return list;
	}

	public FilterElement<? extends Object> getCurrentElement() {
		return currentElement;
	}

	public List<OrderBy> getOrderList() {
		return orderList;
	}

	public GroupingElement getGroupingBy() {
		return groupingBy;
	}

	public List<SelectElement> getSelectors() {
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

	@SuppressWarnings("unchecked")
	public <T> CommonFilter in(String field, T... value) {
		return filter(field, Arrays.asList(value), FilterSymbol.IN);
	}

	public <T> CommonFilter notIn(String field, Collection<T> value) {
		return filter(field, value, FilterSymbol.NOTIN);
	}

	@SuppressWarnings("unchecked")
	public <T> CommonFilter notIn(String field, T... value) {
		return filter(field, Arrays.asList(value), FilterSymbol.NOTIN);
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

	public CommonFilter groupBy(String... fields) {
		this.groupingBy.add(fields);
		return this;
	}

	public CommonFilter select(String... selectField) {
		for (String field : selectField)
			selectors.add(new PathElement(field));
		return this;
	}

	public CommonFilter select(SelectElement... elements) {
		for (SelectElement selectElement : elements) {
			selectors.add(selectElement);
		}
		return this;
	}

	public CommonFilter select(List<String> selectField) {
		select(selectField.toArray(new String[] {}));
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

	public CommonFilter limit(int limit) {
		Assert.isTrue(limit > 0, "limit count must larger then 0");
		limitCount = limit;
		return this;
	}

	public CommonFilter limit(int limitStart, int limitCount) {
		Assert.isTrue(limitCount > 0, "limit count must larger then 0");
		this.limitStart = limitStart;
		this.limitCount = limitCount;
		return this;
	}

	public boolean hasLimit() {
		return this.limitStart != null;
	}

	/**
	 * @return the limitCount
	 */
	public Integer getLimitCount() {
		return limitCount;
	}

	/**
	 * @param limitCount the limitCount to set
	 */
	public void setLimitCount(Integer limitCount) {
		this.limitCount = limitCount;
	}

	/**
	 * @return the limitStart
	 */
	public Integer getLimitStart() {
		return limitStart;
	}

	/**
	 * @param limitStart the limitStart to set
	 */
	public void setLimitStart(Integer limitStart) {
		this.limitStart = limitStart;
	}

}
