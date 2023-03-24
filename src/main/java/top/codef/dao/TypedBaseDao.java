package top.codef.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import top.codef.exceptions.JpaAmebaException;
import top.codef.models.Page;
import top.codef.models.Pageable;
import top.codef.sqlfilter.QueryBuilder;
import top.codef.sqlfilter.typed.TypedCommonFilter;

public class TypedBaseDao extends AbstractDao {

	private final Log logger = LogFactory.getLog(getClass());

	/**
	 * 获取单个实例
	 * 
	 * @param commonFilter
	 * @return
	 */
	public <T> T getSingle(TypedCommonFilter<T> commonFilter) {
		var clazz = commonFilter.getClazz();
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(clazz);
		Root<T> root = query.from(clazz);
		conditionHandle(commonFilter, query, builder, root);
		try {
			TypedQuery<T> typedQuery = getEntityManager().createQuery(query);
			limit(commonFilter, typedQuery);
			T result = typedQuery.getSingleResult();
			return result;
		} catch (NoResultException e) {
			logger.warn("无对象返回：" + clazz.getName());
		}
		return null;
	}

	/**
	 * 获取单个实例
	 * 
	 * @param tarClazz
	 * @param commonFilter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T, K> T getSingle(Class<T> tarClazz, TypedCommonFilter<K> commonFilter) {
		var rootClass = commonFilter.getClazz();
		if (commonFilter.getSelectors().size() == 1) {
			Object result = getSingleWithSingleExistJavaObj(rootClass, commonFilter);
			return (T) result;
		}
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		if (commonFilter.getSelectors().size() == 0)
			throw new JpaAmebaException("没有选择的字段");
		CriteriaQuery<T> query = builder.createQuery(tarClazz);
		Root<K> root = query.from(rootClass);
		conditionHandle(commonFilter, query, builder, root);
		try {
			TypedQuery<T> typedQuery = getEntityManager().createQuery(query);
			limit(commonFilter, typedQuery);
			T result = typedQuery.getSingleResult();
			return result;
		} catch (NoResultException e) {
			logger.warn("无对象返回：" + rootClass.getName());
		}
		return null;
	}

	private <K, T> Object getSingleWithSingleExistJavaObj(Class<K> clazz, TypedCommonFilter<T> commonFilter) {
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Object> query = builder.createQuery();
		Root<K> root = query.from(clazz);
		conditionHandle(commonFilter, query, builder, root);
		try {
			TypedQuery<Object> typedQuery = getEntityManager().createQuery(query);
			limit(commonFilter, typedQuery);
			Object result = typedQuery.getSingleResult();
			return result;
		} catch (NoResultException e) {
			logger.warn("无对象返回：" + commonFilter.getSelectors() + "；对象：" + clazz);
		}
		return null;
	}

	public <T, K> List<T> getList(Class<T> tarClazz, TypedCommonFilter<K> commonFilter) {
		var rootClazz = commonFilter.getClazz();
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(tarClazz);
		Root<K> root = query.from(rootClazz);
		conditionHandle(commonFilter, query, builder, root);
		TypedQuery<T> typedQuery = getEntityManager().createQuery(query);
		limit(commonFilter, typedQuery);
		List<T> re = typedQuery.getResultList();
		return re;
	}

	/**
	 * 分页所做查询列表
	 *
	 * @param <T>
	 * @param page
	 * @param commonFilter
	 * @return
	 */
	public <T> List<T> getList(Pageable page, TypedCommonFilter<T> commonFilter) {
		var clazz = commonFilter.getClazz();
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(clazz);
		Root<T> root = query.from(clazz);
		conditionHandle(commonFilter, page, query, builder, root);
		int first = page.getEachPageSize() * (page.getPageNo() - 1);
		List<T> list = getEntityManager().createQuery(query).setFirstResult(first).setMaxResults(page.getEachPageSize())
				.getResultList();
		return list;
	}

	/**
	 * @param tarClazz
	 * @param page
	 * @param commonFilter
	 * @return
	 */
	public <T, R> List<T> getList(Class<T> tarClazz, Pageable page, TypedCommonFilter<R> commonFilter) {
		var rootClazz = commonFilter.getClazz();
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(tarClazz);
		Root<R> root = query.from(rootClazz);
		conditionHandle(commonFilter, page, query, builder, root);
		int first = page.getEachPageSize() * (page.getPageNo() - 1);
		List<T> list = getEntityManager().createQuery(query).setFirstResult(first).setMaxResults(page.getEachPageSize())
				.getResultList();
		return list;
	}

	public <T> Page<T> getPageTyp(Pageable pageable, TypedCommonFilter<T> filter) {
		var clazz = filter.getClazz();
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(clazz);
		Root<T> root = query.from(clazz);
		conditionHandle(filter, pageable, query, builder, root);
		Page<T> page = new Page<>(pageable);
		int first = pageable.getEachPageSize() * (pageable.getPageNo() - 1);
		List<T> list = getEntityManager().createQuery(query).setFirstResult(first)
				.setMaxResults(pageable.getEachPageSize()).getResultList();
		page.setContent(list);
		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
		Root<T> countRoot = countQuery.from(clazz);
		countQuery.select(builder.count(countRoot)).where(builder.and(query.getRestriction()));
		Long count = getEntityManager().createQuery(countQuery).getSingleResult();
		pageable.setTotalCount(count);
		pageable.setPageCount((long) Math.ceil(pageable.getTotalCount().doubleValue() / pageable.getEachPageSize()));
		return page;
	}

	public <T, R> Page<T> getPage(Class<T> tarClazz, Pageable pageable, TypedCommonFilter<R> filter) {
		var rootClazz = filter.getClazz();
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(tarClazz);
		Root<R> root = query.from(rootClazz);
		conditionHandle(filter, pageable, query, builder, root);
		int first = pageable.getEachPageSize() * (pageable.getPageNo() - 1);
		List<T> list = getEntityManager().createQuery(query).setFirstResult(first)
				.setMaxResults(pageable.getEachPageSize()).getResultList();
		Page<T> page = new Page<>(pageable);
		page.setContent(list);
		confirmPageCount(rootClazz, page, builder, query.getRestriction());
		return page;

	}

	private void confirmPageCount(Class<?> clazz, Page<?> page, CriteriaBuilder builder, Predicate... predicates) {
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
		return getList(QueryBuilder.createFilter(clazz));
	}

	/**
	 * 获取列表
	 * 
	 * @param <T>
	 * @param commonFilter
	 * @return
	 */
	public <T> List<T> getList(TypedCommonFilter<T> commonFilter) {
		var clazz = commonFilter.getClazz();
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(clazz);
		Root<T> root = query.from(clazz);
		conditionHandle(commonFilter, query, builder, root);
		TypedQuery<T> typedQuery = getEntityManager().createQuery(query);
		limit(commonFilter, typedQuery);
		List<T> re = typedQuery.getResultList();
		return re;
	}

	/**
	 * 数量
	 *
	 * @param <T>
	 * @param commonFilter
	 * @return
	 */
	public <T> Long count(TypedCommonFilter<T> commonFilter) {
		var clazz = commonFilter.getClazz();
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
	 * @param <T>
	 * @param field
	 * @param commonFilter
	 * @return
	 */
	public <T> Long countDistinct(String field, TypedCommonFilter<T> commonFilter) {
		var clazz = commonFilter.getClazz();
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

	public <T> Integer update(TypedCommonFilter<T> commonFilter) {
		var clazz = commonFilter.getClazz();
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

	public <T> Integer updateWithNull(TypedCommonFilter<T> commonFilter) {
		var clazz = commonFilter.getClazz();
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaUpdate<T> update = builder.createCriteriaUpdate(clazz);
		Root<T> root = update.from(clazz);
		commonFilter.getUpdatableList().stream().forEach(x -> update.set(root.get(x.getField()), x.getValue()));
		Predicate[] predicates = seperate(commonFilter, builder, root);
		update.where(predicates);
		int count = getEntityManager().createQuery(update).executeUpdate();
		return count;
	}

	public <T> int delete(TypedCommonFilter<T> commonFilter) {
		var clazz = commonFilter.getClazz();
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
