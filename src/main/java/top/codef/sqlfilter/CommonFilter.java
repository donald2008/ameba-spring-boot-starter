package top.codef.sqlfilter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.util.Assert;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import top.codef.sqlfilter.functional.FilterCondition;
import top.codef.sqlfilter.functional.GroupCondition;
import top.codef.sqlfilter.functional.JoinCondition;
import top.codef.sqlfilter.functional.OrderCondition;
import top.codef.sqlfilter.functional.SelectCondition;

public class CommonFilter {

	protected final List<Element<? extends Object>> updatableList = new LinkedList<>();

	protected final List<FilterCondition> list = new LinkedList<>();

	protected final List<JoinCondition> joinList = new LinkedList<>();

	protected final List<OrderCondition> orderList = new LinkedList<>();

	protected final List<SelectCondition> selectors = new LinkedList<>();

	protected final List<GroupCondition> groupingBy = new LinkedList<>();

	protected Integer limitCount;

	protected Integer limitStart;

	protected <T> boolean checkNotNull(T value) {
		return value != null;
	}

	public List<FilterCondition> getList() {
		return list;
	}

	public List<SelectCondition> getSelectors() {
		return selectors;
	}

	public List<JoinCondition> getJoinList() {
		return joinList;
	}

	public List<OrderCondition> getOrderList() {
		return orderList;
	}

	public List<GroupCondition> getGroupingBy() {
		return groupingBy;
	}

	public <T> CommonFilter update(String field, T value) {
		updatableList.add(new Element<Object>(field, value));
		return this;
	}

	public List<Element<? extends Object>> getUpdatableList() {
		return updatableList;
	}

	public <T> CommonFilter eq(String field, T value) {
		if (checkNotNull(value))
			list.add((builder, path) -> builder.equal(PathUtils.getPath(field, path), value));
		return this;
	}

	public CommonFilter eqFeild(String field1, String field2) {
		list.add((builder, path) -> builder.equal(PathUtils.getPath(field1, path), PathUtils.getPath(field2, path)));
		return this;
	}

	public <T> CommonFilter neq(String field, T value) {
		if (checkNotNull(value))
			list.add((builder, path) -> builder.notEqual(PathUtils.getPath(field, path), value));
		return this;
	}

	public CommonFilter neq(String field1, String Field2) {
		list.add((builder, path) -> builder.notEqual(PathUtils.getPath(field1, path), PathUtils.getPath(Field2, path)));
		return this;
	}

	@SuppressWarnings("unchecked")
	public CommonFilter like(String field, String value) {
		if (checkNotNull(value))
			list.add((builder, path) -> builder.like((Expression<String>) PathUtils.getPath(field, path), value));
		return this;
	}

	public CommonFilter isNull(String field) {
		list.add((builder, path) -> builder.isNull(PathUtils.getPath(field, path)));
		return this;
	}

	public <T> CommonFilter isNotNull(String field) {
		list.add((builder, path) -> builder.isNotNull(PathUtils.getPath(field, path)));
		return this;
	}

	public <T> CommonFilter in(String field, Collection<T> value) {
		if (checkNotNull(value))
			list.add((builder, path) -> PathUtils.getPath(field, path).in(value.toArray()));
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> CommonFilter in(String field, T... value) {
		list.add((builder, path) -> PathUtils.getPath(field, path).in(value));
		return this;
	}

	public <T> CommonFilter notIn(String field, Collection<T> value) {
		list.add((builder, path) -> builder.not(PathUtils.getPath(field, path).in(value.toArray())));
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> CommonFilter notIn(String field, T... value) {
		list.add((builder, path) -> builder.not(PathUtils.getPath(field, path).in(value)));
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T extends Number> CommonFilter lt(String field, T value) {
		if (checkNotNull(value))
			list.add((builder, path) -> builder.lt((Expression<? extends Number>) PathUtils.getPath(field, path),
					value));
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T extends Number> CommonFilter le(String field, T value) {
		if (checkNotNull(value))
			list.add((builder, path) -> builder.le((Expression<? extends Number>) PathUtils.getPath(field, path),
					value));
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T extends Number> CommonFilter gt(String field, T value) {
		if (checkNotNull(value))
			list.add((builder, path) -> builder.gt((Expression<? extends Number>) PathUtils.getPath(field, path),
					value));
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T extends Number> CommonFilter ge(String field, T value) {
		if (checkNotNull(value))
			list.add((builder, path) -> builder.ge((Expression<? extends Number>) PathUtils.getPath(field, path),
					value));
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T extends Comparable<T>> CommonFilter between(String field, T minValue, T maxValue) {
		if (checkNotNull(maxValue) && checkNotNull(minValue)) {
			list.add((builder, path) -> builder.between((Path<T>) PathUtils.getPath(field, path), minValue, maxValue));
		}
		return this;
	}

	public CommonFilter orderByAsc(String... fields) {
		Stream.of(fields).forEach(x -> orderList.add((builder, path) -> builder.asc(PathUtils.getPath(x, path))));
		return this;
	}

	public CommonFilter orderByDesc(String... fields) {
		Stream.of(fields).forEach(x -> orderList.add((builder, path) -> builder.desc(PathUtils.getPath(x, path))));
		return this;
	}

	public CommonFilter groupBy(String... fields) {
		Stream.of(fields).forEach(x -> groupingBy.add((path) -> PathUtils.getPath(x, path)));
		return this;
	}

	public CommonFilter select(String... selectField) {
		for (String field : selectField)
			selectors.add((builder, path) -> PathUtils.getPath(field, path));
		return this;
	}

	public CommonFilter select(SelectCondition... elements) {
		for (SelectCondition selectElement : elements) {
			selectors.add(selectElement);
		}
		return this;
	}

	public CommonFilter select(Collection<String> selectField) {
		select(selectField.toArray(new String[] {}));
		return this;
	}

	public CommonFilter innerJoin(String field) {
		joinList.add((root) -> root.join(field, JoinType.INNER));
		return this;
	}

	public CommonFilter leftJoin(String table) {
		joinList.add(root -> root.join(table, JoinType.LEFT));
		return this;
	}

	public CommonFilter rightJoin(String table) {
		joinList.add(root -> root.join(table, JoinType.RIGHT));
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
