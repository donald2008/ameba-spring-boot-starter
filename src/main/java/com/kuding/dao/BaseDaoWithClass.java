package com.kuding.dao;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kuding.exceptions.JpaAmebaException;
import com.kuding.models.Page;
import com.kuding.models.Pageable;
import com.kuding.sqlfilter.CommonFilter;
import com.kuding.sqlfilter.QueryBuilder;

import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public abstract class BaseDaoWithClass<T> extends AbstractDaoWithClass<T> {

	private final Log logger = LogFactory.getLog(getClass());

	/**
	 * 获取单个实例
	 * 
	 * @param clazz
	 * @param commonFilter
	 * @return
	 */
	public T getSingle(CommonFilter commonFilter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(clazz());
		Root<T> root = query.from(clazz());
		if (commonFilter.getJoinList().size() > 0)
			joinTable(commonFilter.getJoinList(), root);
		groupBy(commonFilter, query, builder, root);
		Predicate[] predicates = seperate(commonFilter, builder, root);
		query = predicates.length > 0 ? query.where(builder.and(predicates)) : query;
		try {
			TypedQuery<T> typedQuery = getEntityManager().createQuery(query);
			limit(commonFilter, typedQuery);
			T result = typedQuery.getSingleResult();
			return result;
		} catch (NoResultException e) {
			logger.warn("无对象返回：" + clazz().getName());
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
	@SuppressWarnings("unchecked")
	public <K> K getSingle(Class<K> tarClazz, CommonFilter commonFilter) {
		if (commonFilter.getSelectors().size() == 1) {
			Object result = getSingleWithSingleExistJavaObj(commonFilter);
			return (K) result;
		}
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		if (commonFilter.getSelectors().size() == 0)
			throw new JpaAmebaException("没有选择的字段");
		CriteriaQuery<K> query = builder.createQuery(tarClazz);
		Root<T> root = query.from(clazz());
		if (commonFilter.getJoinList().size() > 0)
			joinTable(commonFilter.getJoinList(), root);
		select(commonFilter, builder, query, root);
		groupBy(commonFilter, query, builder, root);
		Predicate[] predicates = seperate(commonFilter, builder, root);
		query = predicates.length > 0 ? query.where(builder.and(predicates)) : query;
		try {
			TypedQuery<K> typedQuery = getEntityManager().createQuery(query);
			limit(commonFilter, typedQuery);
			K result = typedQuery.getSingleResult();
			return result;
		} catch (NoResultException e) {
			logger.warn("无对象返回：" + clazz().getName());
		}
		return null;
	}

	private Object getSingleWithSingleExistJavaObj(CommonFilter commonFilter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Object> query = builder.createQuery();
		Root<T> root = query.from(clazz());
		if (commonFilter.getJoinList().size() > 0)
			joinTable(commonFilter.getJoinList(), root);
		select(commonFilter, builder, query, root);
		groupBy(commonFilter, query, builder, root);
		Predicate[] predicates = seperate(commonFilter, builder, root);
		query = predicates.length > 0 ? query.where(builder.and(predicates)) : query;
		try {
			TypedQuery<Object> typedQuery = getEntityManager().createQuery(query);
			limit(commonFilter, typedQuery);
			Object result = typedQuery.getSingleResult();
			return result;
		} catch (NoResultException e) {
			logger.warn("无对象返回：" + commonFilter.getSelectors() + "；对象：" + clazz());
		}
		return null;
	}

	public <K> List<K> getList(Class<K> tarClazz, CommonFilter commonFilter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<K> query = builder.createQuery(tarClazz);
		Root<T> root = query.from(clazz());
		if (commonFilter.getJoinList().size() > 0)
			joinTable(commonFilter.getJoinList(), root);
		select(commonFilter, builder, query, root);
		groupBy(commonFilter, query, builder, root);
		Predicate[] predicates = seperate(commonFilter, builder, root);
		List<Order> orderList = commonFilter.getOrderList().stream()
				.map(x -> createOrder(x.getField(), x.getValue(), builder, root)).filter(x -> x != null)
				.collect(toList());
		query = orderList.size() > 0 ? query.orderBy(orderList) : query;
		query = predicates.length > 0 ? query.where(builder.and(predicates)) : query;
		TypedQuery<K> typedQuery = getEntityManager().createQuery(query);
		limit(commonFilter, typedQuery);
		List<K> re = typedQuery.getResultList();
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
	public List<T> getList(Pageable page, CommonFilter commonFilter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(clazz());
		Root<T> root = query.from(clazz());
		if (commonFilter.getJoinList().size() > 0)
			joinTable(commonFilter.getJoinList(), root);
		Predicate[] predicates = seperate(commonFilter, builder, root);
		groupBy(commonFilter, query, builder, root);
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
	public <R> List<R> getList(Class<R> tarClazz, Pageable page, CommonFilter commonFilter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<R> query = builder.createQuery(tarClazz);
		Root<T> root = query.from(clazz());
		if (commonFilter.getJoinList().size() > 0)
			joinTable(commonFilter.getJoinList(), root);
		groupBy(commonFilter, query, builder, root);
		Predicate[] predicates = seperate(commonFilter, builder, root);
		query = predicates.length > 0 ? query.where(builder.and(predicates)) : query;
		List<Order> orderList = createOrderList(commonFilter.getOrderList(), page, builder, root);
		query = orderList.size() > 0 ? query.orderBy(orderList) : query;
		select(commonFilter, builder, query, root);
		int first = page.getEachPageSize() * (page.getPageNo() - 1);
		List<R> list = getEntityManager().createQuery(query).setFirstResult(first).setMaxResults(page.getEachPageSize())
				.getResultList();
		return list;
	}

	public Page<T> getPage(Pageable pageable, CommonFilter filter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(clazz());
		Root<T> root = query.from(clazz());
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
		Root<T> countRoot = countQuery.from(clazz());
		countQuery.select(builder.count(countRoot)).where(builder.and(predicates));
		Long count = getEntityManager().createQuery(countQuery).getSingleResult();
		pageable.setTotalCount(count);
		pageable.setPageCount((long) Math.ceil(pageable.getTotalCount().doubleValue() / pageable.getEachPageSize()));
		return page;
	}

	public <R> Page<R> getPage(Class<R> tarClazz, Pageable pageable, CommonFilter filter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<R> query = builder.createQuery(tarClazz);
		Root<T> root = query.from(clazz());
		select(filter, builder, query, root);
		joinTable(filter.getJoinList(), root);
		Predicate[] predicates = seperate(filter, builder, root);
		query = predicates.length > 0 ? query.where(builder.and(predicates)) : query;
		List<Order> orders = createOrderList(filter.getOrderList(), pageable, builder, root);
		query = orders.size() > 0 ? query.orderBy(orders) : query;
		int first = pageable.getEachPageSize() * (pageable.getPageNo() - 1);
		List<R> list = getEntityManager().createQuery(query).setFirstResult(first)
				.setMaxResults(pageable.getEachPageSize()).getResultList();
		Page<R> page = new Page<>(pageable);
		page.setContent(list);
		confirmPageCount(page, builder, predicates);
		return page;

	}

	private void confirmPageCount(Page<?> page, CriteriaBuilder builder, Predicate[] predicates) {
		Pageable pageable = page.getPageable();
		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
		Root<?> countRoot = countQuery.from(clazz());
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
	public List<T> getList() {
		return getList(clazz(), QueryBuilder.createFilter());
	}

	/**
	 * 获取列表
	 * 
	 * @param nullFilter
	 * @param eqFilters
	 * @return
	 */
	public List<T> getList(CommonFilter commonFilter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(clazz());
		Root<T> root = query.from(clazz());
		if (commonFilter != null) {
			if (commonFilter.getJoinList().size() > 0)
				joinTable(commonFilter.getJoinList(), root);
			groupBy(commonFilter, query, builder, root);
			Predicate[] predicates = seperate(commonFilter, builder, root);
			query = predicates.length > 0 ? query.where(builder.and(predicates)) : query;
			List<Order> orderList = commonFilter.getOrderList().stream()
					.map(x -> createOrder(x.getField(), x.getValue(), builder, root)).collect(toList());
			query = orderList.size() > 0 ? query.orderBy(orderList) : query;
		}
		TypedQuery<T> typedQuery = getEntityManager().createQuery(query);
		limit(commonFilter, typedQuery);
		List<T> re = typedQuery.getResultList();
		return re;
	}

	/**
	 * 数量
	 *
	 * @param nullFilter
	 * @param eqFilters
	 * @return
	 */
	public Long count(CommonFilter commonFilter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<T> root = query.from(clazz());
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
	public Long countDistinct(String field, CommonFilter commonFilter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<T> root = query.from(clazz());
		if (commonFilter.getJoinList().size() > 0)
			joinTable(commonFilter.getJoinList(), root);
		Predicate[] predicates = seperate(commonFilter, builder, root);
		query = predicates.length > 0
				? query.select(builder.countDistinct(root.get(field))).where(builder.and(predicates))
				: query.select(builder.countDistinct(root.get(field)));
		Long count = getEntityManager().createQuery(query).getSingleResult();
		return count;
	}

	public Integer update(CommonFilter commonFilter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaUpdate<T> update = builder.createCriteriaUpdate(clazz());
		Root<T> root = update.from(clazz());
		commonFilter.getUpdatableList().stream().filter(x -> x.getValue() != null)
				.forEach(x -> update.set(root.get(x.getField()), x.getValue()));
		Predicate[] predicates = seperate(commonFilter, builder, root);
		update.where(predicates);
		int count = getEntityManager().createQuery(update).executeUpdate();
		return count;
	}

	public Integer updateWithNull(CommonFilter commonFilter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaUpdate<T> update = builder.createCriteriaUpdate(clazz());
		Root<T> root = update.from(clazz());
		commonFilter.getUpdatableList().stream().forEach(x -> update.set(root.get(x.getField()), x.getValue()));
		Predicate[] predicates = seperate(commonFilter, builder, root);
		update.where(predicates);
		int count = getEntityManager().createQuery(update).executeUpdate();
		return count;
	}

	public int delete(CommonFilter commonFilter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaDelete<T> delete = builder.createCriteriaDelete(clazz());
		Root<T> root = delete.from(clazz());
		Predicate[] predicates = seperate(commonFilter, builder, root);
		if (predicates.length == 0)
			throw new JpaAmebaException("不支持的操作");
		delete.where(builder.and(predicates));
		int count = getEntityManager().createQuery(delete).executeUpdate();
		return count;
	}
}
