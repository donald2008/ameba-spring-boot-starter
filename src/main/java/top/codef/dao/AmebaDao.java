package top.codef.dao;

import static java.util.stream.Collectors.toList;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import top.codef.enums.OrderEnum;
import top.codef.models.Pageable;
import top.codef.sqlfilter.CommonFilter;
import top.codef.sqlfilter.functional.GroupCondition;
import top.codef.sqlfilter.functional.JoinCondition;
import top.codef.sqlfilter.functional.OrderCondition;
import top.codef.sqlfilter.functional.SelectCondition;

public interface AmebaDao {

	EntityManager getEntityManager();

	/**
	 * mysql可直接使用，生成数据库级唯一id方法;
	 * 
	 * @return
	 */
	default Long generateUid() {
		String sql = "select uuid_short();";
		return (Long) getEntityManager().createNativeQuery(sql).getSingleResult();
	}

	default <T> CriteriaQuery<T> select(CommonFilter commonFilter, CriteriaQuery<T> query,
			CriteriaBuilder criteriaBuilder, Path<?> path) {
		List<SelectCondition> element = commonFilter.getSelectors();
		if (!element.isEmpty()) {
			Expression<?>[] expressions = element.stream().map(x -> x.select(criteriaBuilder, path))
					.toArray(Expression<?>[]::new);
			query.multiselect(expressions);
		}
		return query;
	}

	default <T, R> CriteriaQuery<T> where(CommonFilter commonFilter, CriteriaQuery<T> query, CriteriaBuilder builder,
			Root<R> root) {
		if (commonFilter.getList().size() > 0) {
			var predicates = seperate(commonFilter, builder, root);
			query.where(builder.and(predicates));
		}
		return query;

	}

	default Predicate[] seperate(CommonFilter commonFilter, CriteriaBuilder builder, Root<?> root) {
		Predicate[] array = commonFilter.getList().stream().map(x -> x.condition(builder, root))
				.toArray(Predicate[]::new);
		return array;
	}

	default <T, R> CriteriaQuery<T> groupBy(CommonFilter commonFilter, CriteriaQuery<T> query, CriteriaBuilder builder,
			Root<R> root) {
		var groupBy = commonFilter.getGroupingBy();
		if (groupBy.size() > 0) {
			Path<?>[] array = groupBy(commonFilter.getGroupingBy(), builder, root);
			if (array != null)
				query.groupBy(array);
		}
		return query;
	}

	default Path<?>[] groupBy(List<GroupCondition> groupConditions, CriteriaBuilder builder, Root<?> root) {
		var grouping = groupConditions.stream().map(x -> x.groupBy(root)).toArray(Path<?>[]::new);
		return grouping;
	}

	default void limit(CommonFilter commonFilter, Query query) {
		if (commonFilter.hasLimit()) {
			query.setMaxResults(commonFilter.getLimitCount());
			var start = commonFilter.getLimitStart();
			if (start != null) {
				query.setFirstResult(start);
			}
		}
	}

	default List<Order> orderBy(List<OrderCondition> orders, Pageable page, CriteriaBuilder builder, Root<?> root) {
		var orderList = orderBy(orders, builder, root);
		if (page.getOrderStr() != null) {
			Order order = page.getOrder() == OrderEnum.ASC ? builder.asc(root.get(page.getOrderStr()))
					: builder.desc(root.get(page.getOrderStr()));
			orderList.add(0, order);
		}
		return orderList;
	}

	default List<Order> orderBy(List<OrderCondition> orders, CriteriaBuilder builder, Root<?> root) {
		List<Order> list = orders.stream().map(x -> x.order(builder, root)).collect(toList());
		return list;
	}

	default <T> CriteriaQuery<T> orderBy(CommonFilter commonFilter, CriteriaQuery<T> query, CriteriaBuilder builder,
			Root<?> root) {
		var orderList = commonFilter.getOrderList();
		if (orderList.size() > 0) {
			query.orderBy(orderBy(orderList, builder, root));
		}
		return query;
	}

	default <T> CriteriaQuery<T> orderBy(CommonFilter commonFilter, CriteriaQuery<T> query, Pageable pageable,
			CriteriaBuilder builder, Root<?> root) {
		var orderList = commonFilter.getOrderList();
		if (orderList.size() > 0) {
			query.orderBy(orderBy(orderList, pageable, builder, root));
		}
		return query;
	}

	default void joinTable(List<JoinCondition> list, Root<?> root) {
		if (list.size() > 0)
			list.forEach(x -> x.join(root));
	}

	default <T, R> void conditionHandle(CommonFilter filter, CriteriaQuery<T> query, CriteriaBuilder builder,
			Root<R> root) {
		joinTable(filter.getJoinList(), root);
		select(filter, query, builder, root);
		where(filter, query, builder, root);
		orderBy(filter, query, builder, root);
		groupBy(filter, query, builder, root);
	}

	default <T, R> void conditionHandle(CommonFilter filter, Pageable pageable, CriteriaQuery<T> query,
			CriteriaBuilder builder, Root<R> root) {
		joinTable(filter.getJoinList(), root);
		select(filter, query, builder, root);
		where(filter, query, builder, root);
		orderBy(filter, query, pageable, builder, root);
		groupBy(filter, query, builder, root);
	}

}
