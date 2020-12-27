package com.kuding.dao;

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.kuding.exceptions.JpaAmebaException;
import com.kuding.models.Page;
import com.kuding.models.Pageable;
import com.kuding.sqlfilter.CommonFilter;

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
		groupBy(commonFilter, query, builder, root);
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
	@SuppressWarnings("unchecked")
	public <T, K> T getSingle(Class<T> tarClazz, Class<K> rootClass, CommonFilter commonFilter) {
		if (commonFilter.getSelectors().size() == 1) {
			Object result = getSingleWithSingleExistJavaObj(rootClass, commonFilter);
			return (T) result;
		}
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		if (commonFilter.getSelectors().size() == 0)
			throw new JpaAmebaException("没有选择的字段");
		CriteriaQuery<T> query = builder.createQuery(tarClazz);
		Root<K> root = query.from(rootClass);
		if (commonFilter.getJoinList().size() > 0)
			joinTable(commonFilter.getJoinList(), root);
		select(commonFilter, builder, query, root);
		groupBy(commonFilter, query, builder, root);
		Predicate[] predicates = seperate(commonFilter, builder, root);
		query = predicates.length > 0 ? query.where(builder.and(predicates)) : query;
		try {
			T result = getEntityManager().createQuery(query).getSingleResult();
			return result;
		} catch (NoResultException e) {
			logger.warn("无对象返回：" + rootClass.getName());
		}
		return null;
	}

	private <K> Object getSingleWithSingleExistJavaObj(Class<K> clazz, CommonFilter commonFilter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Object> query = builder.createQuery();
		Root<K> root = query.from(clazz);
		if (commonFilter.getJoinList().size() > 0)
			joinTable(commonFilter.getJoinList(), root);
		select(commonFilter, builder, query, root);
		groupBy(commonFilter, query, builder, root);
		Predicate[] predicates = seperate(commonFilter, builder, root);
		query = predicates.length > 0 ? query.where(builder.and(predicates)) : query;
		try {
			Object result = getEntityManager().createQuery(query).getSingleResult();
			return result;
		} catch (NoResultException e) {
			logger.warn("无对象返回：" + commonFilter.getSelectors() + "；对象：" + clazz);
		}
		return null;
	}

	public <T, K> List<T> getList(Class<T> tarClazz, Class<K> rootClazz, CommonFilter commonFilter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(tarClazz);
		Root<K> root = query.from(rootClazz);
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
		groupBy(commonFilter, query, builder, root);
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
		groupBy(commonFilter, query, builder, root);
		Predicate[] predicates = seperate(commonFilter, builder, root);
		query = predicates.length > 0 ? query.where(builder.and(predicates)) : query;
		List<Order> orderList = createOrderList(commonFilter.getOrderList(), page, builder, root);
		query = orderList.size() > 0 ? query.orderBy(orderList) : query;
		select(commonFilter, builder, query, root);
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
		select(filter, builder, query, root);
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
			groupBy(commonFilter, query, builder, root);
			Predicate[] predicates = seperate(commonFilter, builder, root);
			query = predicates.length > 0 ? query.where(builder.and(predicates)) : query;
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

}
