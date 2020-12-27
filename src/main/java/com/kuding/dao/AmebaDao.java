package com.kuding.dao;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.kuding.enums.OrderEnum;
import com.kuding.models.Pageable;
import com.kuding.sqlfilter.CommonFilter;
import com.kuding.sqlfilter.ComparebleFilterElement;
import com.kuding.sqlfilter.FilterElement;
import com.kuding.sqlfilter.GroupingElement;
import com.kuding.sqlfilter.JoinTable;
import com.kuding.sqlfilter.OrderBy;
import com.kuding.sqlfilter.PathElement;
import com.kuding.sqlfilter.PathUtils;
import com.kuding.sqlfilter.SelectElement;

public interface AmebaDao {

	default void select(CommonFilter commonFilter, CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query,
			Path<?> path) {
		List<SelectElement> element = commonFilter.getSelectors();
		if (!element.isEmpty()) {
			Expression<?>[] expressions = element.stream().map(x -> x.select(criteriaBuilder, path))
					.toArray(Expression<?>[]::new);
			query.multiselect(expressions);
		}
	}

	default Predicate[] seperate(CommonFilter commonFilter, CriteriaBuilder builder, Root<?> root) {
		Predicate[] array = commonFilter.getList().stream().map(x -> createPredicate(x, builder, root))
				.filter(x -> x != null).toArray(Predicate[]::new);
		return array;
	}

	default void groupBy(CommonFilter commonFilter, CriteriaQuery<?> query, CriteriaBuilder builder, Root<?> root) {
		Path<?>[] array = groupBy(commonFilter.getGroupingBy(), builder, root);
		if (array != null)
			query.groupBy(array);
	}

	default Path<?>[] groupBy(GroupingElement groupingElement, CriteriaBuilder builder, Root<?> root) {
		Set<String> grouping = groupingElement.getFields();
		if (grouping.size() == 0)
			return null;
		Path<?>[] array = grouping.stream().map(x -> PathUtils.getPath(x, root)).toArray(Path<?>[]::new);
		return array;
	}

	default Predicate createPredicate(FilterElement<? extends Object> filter, CriteriaBuilder builder, Root<?> root) {
		Path<?> path = root.get(filter.getField());
		Object value = filter.getValue();
		while (filter.getValue() instanceof FilterElement) {
			filter = (FilterElement<?>) filter.getValue();
			value = filter.getValue();
			path = path.get(filter.getField());
		}
		if (value instanceof PathElement) {
			Path<?> path2 = root;
			for (String field : ((PathElement) value).getField().split(".")) {
				path2 = path2.get(field);
			}
			value = path2;
		}
		if (value == null)
			return null;
		switch (filter.getFsy()) {
		case EQ:
			return builder.equal(path, value);
		case NEQ:
			return builder.notEqual(path, value);
		case LIKE:
			@SuppressWarnings("unchecked")
			Path<String> path2 = (Path<String>) path;
			return builder.like(path2, (String) value);
		case ISNULL:
			return builder.isNull(path);
		case ISNOTNULL:
			return builder.isNotNull(path);
		case IN:
			Collection<?> collectionValue = (Collection<?>) value;
			if (collectionValue == null || collectionValue.size() == 0)
				throw new NoResultException("in语句错误");
			return path.in((Collection<?>) value);
		case NOTIN:
			Collection<?> collectionValue2 = (Collection<?>) value;
			if (collectionValue2 == null || collectionValue2.size() == 0)
				return null;
			return builder.not(path.in(collectionValue2));
		default:
			return createPredicate((ComparebleFilterElement<?>) filter, path.getParentPath(), builder);
		}
	}

	default <Y extends Comparable<Y>> Predicate createPredicate(ComparebleFilterElement<Y> comparableFilter,
			Path<?> path, CriteriaBuilder builder) {
		String field = comparableFilter.getField();
		Y value = comparableFilter.getValue();
		switch (comparableFilter.getFsy()) {
		case GE:
			return builder.greaterThanOrEqualTo(path.get(comparableFilter.getField()), value);
		case GT:
			return builder.greaterThan(path.get(field), value);
		case LE:
			return builder.lessThanOrEqualTo(path.get(field), value);
		case LT:
			return builder.lessThan(path.get(field), value);
		default:
			break;
		}
		return null;
	}

	default List<Order> createOrderList(List<OrderBy> orderBies, Pageable page, CriteriaBuilder builder, Root<?> root) {
		if (page.getOrderStr() != null) {
			OrderBy orderBy = orderBies.stream().filter(x -> x.getField().equals(page.getOrderStr())).findFirst()
					.orElse(null);
			if (orderBy != null) {
				orderBies.remove(orderBy);
				orderBies.add(0, orderBy);
			} else
				orderBies.add(0, new OrderBy(page.getOrderStr(), page.getOrder()));
		}
		List<Order> orders = createOrderList(orderBies, builder, root);
		return orders.stream().filter(x -> x != null).collect(toList());
	}

	default List<Order> createOrderList(List<OrderBy> orderBies, CriteriaBuilder builder, Root<?> root) {
		List<Order> list = orderBies.stream().map(x -> createOrder(x.getField(), x.getValue(), builder, root))
				.collect(toList());
		return list;
	}

	default Order createOrder(String fields, OrderEnum order, CriteriaBuilder builder, Root<?> root) {
		if (fields != null && order != null) {
			List<String> fieldList = Arrays.asList(fields.split("\\.")).stream().filter(y -> StringUtils.isNotBlank(y))
					.collect(toList());
			String firstField = fieldList.remove(0);
			Path<?> path = root.get(firstField);
			for (String field : fieldList)
				path = path.get(field);
			return order == OrderEnum.ASC ? builder.asc(path) : builder.desc(path);
		}
		return null;
	}

	default void joinTable(List<JoinTable> list, Root<?> root) {
		for (JoinTable joinTable : list)
			root.join(joinTable.getField(), joinTable.getValue());
	}
}
