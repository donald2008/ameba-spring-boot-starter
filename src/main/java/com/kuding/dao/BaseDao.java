package com.kuding.dao;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kuding.enums.OrderEnum;
import com.kuding.exceptions.JpaAmebaException;
import com.kuding.models.Page;
import com.kuding.models.Pageable;
import com.kuding.sqlfilter.CommonFilter;
import com.kuding.sqlfilter.ComparebleFilterElement;
import com.kuding.sqlfilter.FilterElement;
import com.kuding.sqlfilter.GroupingElement;
import com.kuding.sqlfilter.JoinTable;
import com.kuding.sqlfilter.OrderBy;

public abstract class BaseDao extends AbstractDao {

	private final Log logger = LogFactory.getLog(getClass());

	/**
	 * 获取单个实例
	 * 
	 * @param clazz
	 * @param commonFilter
	 * @return
	 */
	public <T> T getSingle(Class<T> clazz, CommonFilter commonFilter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(clazz);
		Root<T> root = query.from(clazz);
		if (commonFilter.getJoinList().size() > 0)
			joinTable(commonFilter.getJoinList(), root);
		Predicate[] predicates = seperate(commonFilter, builder, root);
		query = predicates.length > 0 ? query.where(builder.and(predicates)) : query;
		try {
			T result = getEntityManager().createQuery(query).getSingleResult();
			return result;
		} catch (NoResultException e) {
			logger.warn("无对象返回：" + clazz.getName());
		}
		return null;
	}

	/**
	 * 获取单个实例
	 * 
	 * @param clazz
	 * @param commonFilter
	 * @return
	 */
	public <T, K> T getSingle(Class<T> tarClazz, Class<K> rootClass, CommonFilter commonFilter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(tarClazz);
		Root<K> root = query.from(rootClass);
		if (commonFilter.getJoinList().size() > 0)
			joinTable(commonFilter.getJoinList(), root);
		List<Selection<?>> selectionList = commonFilter.getSelectors().stream().map(x -> createSelection(root, x))
				.collect(toList());
		query.multiselect(selectionList);
		Predicate[] predicates = seperate(commonFilter, builder, root);
		query = predicates.length > 0 ? query.where(builder.and(predicates)) : query;
		try {
			T result = getEntityManager().createQuery(query).getSingleResult();
			return result;
		} catch (NoResultException e) {
			logger.error("无对象返回：" + rootClass.getName());
		}
		return null;
	}

	public <T, K> List<T> getList(Class<T> tarClazz, Class<K> rootClazz, CommonFilter commonFilter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(tarClazz);
		Root<K> root = query.from(rootClazz);
		if (commonFilter.getJoinList().size() > 0)
			joinTable(commonFilter.getJoinList(), root);
		List<Selection<?>> list = commonFilter.getSelectors().stream().map(x -> createSelection(root, x))
				.collect(toList());
		query.multiselect(list);
		Predicate[] predicates = seperate(commonFilter, builder, root);
		List<Order> orderList = commonFilter.getOrderList().stream()
				.map(x -> createOrder(x.getField(), x.getValue(), builder, root)).filter(x -> x != null)
				.collect(toList());
		query = orderList.size() > 0 ? query.orderBy(orderList) : query;
		query = predicates.length > 0 ? query.where(builder.and(predicates)) : query;
		List<T> re = getEntityManager().createQuery(query).getResultList();
		return re;
	}

	/**
	 * 分页所做查询列表
	 *
	 * @param page
	 * @param nullFilter
	 * @param eqFilters
	 * @return
	 */
	public <T> List<T> getList(Class<T> clazz, Pageable page, CommonFilter commonFilter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(clazz);
		Root<T> root = query.from(clazz);
		if (commonFilter.getJoinList().size() > 0)
			joinTable(commonFilter.getJoinList(), root);
		Predicate[] predicates = seperate(commonFilter, builder, root);
		query = predicates.length > 0 ? query.where(builder.and(predicates)) : query;
		List<Order> orderList = commonFilter.getOrderList().stream()
				.map(x -> createOrder(x.getField(), x.getValue(), builder, root)).collect(toList());
		orderList.add(createOrder(page.getOrderStr(), page.getOrder(), builder, root));
		orderList = orderList.stream().filter(x -> x != null).collect(toList());
		query = orderList.size() > 0 ? query.orderBy(orderList) : query;
		int first = page.getEachPageSize() * (page.getPageNo() - 1);
		List<T> list = getEntityManager().createQuery(query).setFirstResult(first).setMaxResults(page.getEachPageSize())
				.getResultList();
		return list;
	}

	/**
	 * @param tarClazz
	 * @param rootClazz
	 * @param page
	 * @param commonFilter
	 * @return
	 */
	public <T, R> List<T> getList(Class<T> tarClazz, Class<R> rootClazz, Pageable page, CommonFilter commonFilter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(tarClazz);
		Root<R> root = query.from(rootClazz);
		if (commonFilter.getJoinList().size() > 0)
			joinTable(commonFilter.getJoinList(), root);
		Predicate[] predicates = seperate(commonFilter, builder, root);
		query = predicates.length > 0 ? query.where(builder.and(predicates)) : query;
		List<Order> orderList = createOrderList(commonFilter.getOrderList(), page, builder, root);
		query = orderList.size() > 0 ? query.orderBy(orderList) : query;
		List<Selection<?>> selections = commonFilter.getSelectors().stream().map(x -> createSelection(root, x))
				.collect(toList());
		query.multiselect(selections);
		int first = page.getEachPageSize() * (page.getPageNo() - 1);
		List<T> list = getEntityManager().createQuery(query).setFirstResult(first).setMaxResults(page.getEachPageSize())
				.getResultList();
		return list;
	}

	public <T> Page<T> getPage(Class<T> clazz, Pageable pageable, CommonFilter filter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(clazz);
		Root<T> root = query.from(clazz);
		joinTable(filter.getJoinList(), root);
		Predicate[] predicates = seperate(filter, builder, root);
		query = predicates.length > 0 ? query.where(builder.and(predicates)) : query;
		List<Order> orderList = createOrderList(filter.getOrderList(), pageable, builder, root);
		query = orderList.size() > 0 ? query.orderBy(orderList) : query;
		Page<T> page = new Page<>(pageable);
		int first = pageable.getEachPageSize() * (pageable.getPageNo() - 1);
		List<T> list = getEntityManager().createQuery(query).setFirstResult(first)
				.setMaxResults(pageable.getEachPageSize()).getResultList();
		page.setContent(list);

		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
		Root<T> countRoot = countQuery.from(clazz);
		countQuery.select(builder.count(countRoot)).where(builder.and(predicates));
		Long count = getEntityManager().createQuery(countQuery).getSingleResult();
		pageable.setTotalCount(count);
		pageable.setPageCount((long) Math.ceil(pageable.getTotalCount().doubleValue() / pageable.getEachPageSize()));
		return page;
	}

	public <T, R> Page<T> getPage(Class<T> tarClazz, Class<R> rootClazz, Pageable pageable, CommonFilter filter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(tarClazz);
		Root<R> root = query.from(rootClazz);
		List<Selection<?>> selections = createSelectionList(root, filter.getSelectors());
		query.multiselect(selections);
		joinTable(filter.getJoinList(), root);
		Predicate[] predicates = seperate(filter, builder, root);
		query = predicates.length > 0 ? query.where(builder.and(predicates)) : query;
		List<Order> orders = createOrderList(filter.getOrderList(), pageable, builder, root);
		query = orders.size() > 0 ? query.orderBy(orders) : query;
		int first = pageable.getEachPageSize() * (pageable.getPageNo() - 1);
		List<T> list = getEntityManager().createQuery(query).setFirstResult(first)
				.setMaxResults(pageable.getEachPageSize()).getResultList();
		Page<T> page = new Page<>(pageable);
		page.setContent(list);
		confirmPageCount(rootClazz, page, builder, predicates);
		return page;

	}

	private void confirmPageCount(Class<?> clazz, Page<?> page, CriteriaBuilder builder, Predicate[] predicates) {
		Pageable pageable = page.getPageable();
		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
		Root<?> countRoot = countQuery.from(clazz);
		countQuery.select(builder.count(countRoot)).where(builder.and(predicates));
		Long count = getEntityManager().createQuery(countQuery).getSingleResult();
		pageable.setTotalCount(count);
		pageable.setPageCount((long) Math.ceil(pageable.getTotalCount().doubleValue() / pageable.getEachPageSize()));
	}

	/**
	 * 获取所有数据
	 * 
	 * @param clazz
	 * @return
	 */
	public <T> List<T> getList(Class<T> clazz) {
		return getList(clazz, null);
	}

	/**
	 * 获取列表
	 * 
	 * @param nullFilter
	 * @param eqFilters
	 * @return
	 */
	public <T> List<T> getList(Class<T> clazz, CommonFilter commonFilter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(clazz);
		Root<T> root = query.from(clazz);
		if (commonFilter != null) {
			if (commonFilter.getJoinList().size() > 0)
				joinTable(commonFilter.getJoinList(), root);
			Predicate[] predicates = seperate(commonFilter, builder, root);

			query = predicates.length > 0 ? query.where(builder.and(predicates)) : query;
			if (commonFilter.getGroupingBy().size() > 0)
				query.groupBy(groupBy(commonFilter, builder, root));
			List<Order> orderList = commonFilter.getOrderList().stream()
					.map(x -> createOrder(x.getField(), x.getValue(), builder, root)).collect(toList());
			query = orderList.size() > 0 ? query.orderBy(orderList) : query;
		}
		return getEntityManager().createQuery(query).getResultList();
	}

	/**
	 * 数量
	 *
	 * @param nullFilter
	 * @param eqFilters
	 * @return
	 */
	public <T> Long count(Class<T> clazz, CommonFilter commonFilter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<T> root = query.from(clazz);
		if (commonFilter.getJoinList().size() > 0)
			joinTable(commonFilter.getJoinList(), root);
		Predicate[] predicates = seperate(commonFilter, builder, root);
		query = predicates.length > 0 ? query.select(builder.count(root)).where(builder.and(predicates))
				: query.select(builder.count(root));
		Long count = getEntityManager().createQuery(query).getSingleResult();
		return count;
	}

	/**
	 * 数量
	 *
	 * @param nullFilter
	 * @param eqFilters
	 * @return
	 */
	public <T> Long countDistinct(Class<T> clazz, String field, CommonFilter commonFilter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<T> root = query.from(clazz);
		if (commonFilter.getJoinList().size() > 0)
			joinTable(commonFilter.getJoinList(), root);
		Predicate[] predicates = seperate(commonFilter, builder, root);
		query = predicates.length > 0
				? query.select(builder.countDistinct(root.get(field))).where(builder.and(predicates))
				: query.select(builder.countDistinct(root.get(field)));
		Long count = getEntityManager().createQuery(query).getSingleResult();
		return count;
	}

	public <T> Integer update(Class<T> clazz, CommonFilter commonFilter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaUpdate<T> update = builder.createCriteriaUpdate(clazz);
		Root<T> root = update.from(clazz);
		commonFilter.getUpdatableList().stream().filter(x -> x.getValue() != null)
				.forEach(x -> update.set(root.get(x.getField()), x.getValue()));
		Predicate[] predicates = seperate(commonFilter, builder, root);
		update.where(predicates);
		int count = getEntityManager().createQuery(update).executeUpdate();
		return count;
	}

	public <T> Integer updateWithNull(Class<T> clazz, CommonFilter commonFilter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaUpdate<T> update = builder.createCriteriaUpdate(clazz);
		Root<T> root = update.from(clazz);
		commonFilter.getUpdatableList().stream().forEach(x -> update.set(root.get(x.getField()), x.getValue()));
		Predicate[] predicates = seperate(commonFilter, builder, root);
		update.where(predicates);
		int count = getEntityManager().createQuery(update).executeUpdate();
		return count;
	}

	public <T> int delete(Class<T> clazz, CommonFilter commonFilter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaDelete<T> delete = builder.createCriteriaDelete(clazz);
		Root<T> root = delete.from(clazz);
		Predicate[] predicates = seperate(commonFilter, builder, root);
		if (predicates.length == 0)
			throw new JpaAmebaException("不支持的操作");
		delete.where(builder.and(predicates));
		int count = getEntityManager().createQuery(delete).executeUpdate();
		return count;
	}

	protected Predicate[] seperate(CommonFilter commonFilter, CriteriaBuilder builder, Root<?> root) {
		Predicate[] array = commonFilter.getList().stream().map(x -> createPredicate(x, builder, root))
				.filter(x -> x != null).toArray(Predicate[]::new);
		return array;
	}

	protected Path<?>[] groupBy(CommonFilter commonFilter, CriteriaBuilder builder, Root<?> root) {
		Collection<GroupingElement<?>> grouping = commonFilter.getGroupingBy();
		Path<?>[] array = grouping.stream().map(x -> root.get(x.getField())).toArray(Path[]::new);
		return array;
	}

	private Predicate createPredicate(FilterElement<? extends Object> filter, CriteriaBuilder builder, Root<?> root) {
		Path<?> path = root.get(filter.getField());
		Object value = filter.getValue();
		while (filter.getValue() instanceof FilterElement) {
			filter = (FilterElement<?>) filter.getValue();
			value = filter.getValue();
			path = path.get(filter.getField());
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
			return path.in((Collection<?>) value);
		case NOTIN:
			return builder.not(path.in((Collection<?>) value));
		default:
			return createPredicate((ComparebleFilterElement<?>) filter, path.getParentPath(), builder);
		}
	}

	private <Y extends Comparable<Y>> Predicate createPredicate(ComparebleFilterElement<Y> comparableFilter,
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

	private List<Order> createOrderList(List<OrderBy> orderBies, Pageable page, CriteriaBuilder builder, Root<?> root) {
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

	private List<Order> createOrderList(List<OrderBy> orderBies, CriteriaBuilder builder, Root<?> root) {
		List<Order> list = orderBies.stream().map(x -> createOrder(x.getField(), x.getValue(), builder, root))
				.collect(toList());
		return list;
	}

	private Order createOrder(String fields, OrderEnum order, CriteriaBuilder builder, Root<?> root) {
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

	private void joinTable(List<JoinTable> list, Root<?> root) {
		for (JoinTable joinTable : list)
			root.join(joinTable.getField(), joinTable.getValue());
	}

	private List<Selection<?>> createSelectionList(Root<?> root, List<String> list) {
		List<Selection<?>> selections = list.stream().map(x -> createSelection(root, x)).collect(toList());
		return selections;
	}

	private Selection<?> createSelection(Root<?> root, String str) {
		List<String> fields = Arrays.stream(str.split("\\.")).collect(toList());
		Path<?> path = root.get(fields.remove(0));
		for (String field : fields)
			path = path.get(field);
		return path;
	}
}
