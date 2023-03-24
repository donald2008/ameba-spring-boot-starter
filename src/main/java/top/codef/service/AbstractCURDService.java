//package top.codef.service;
//
//import java.io.Serializable;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//import jakarta.transaction.Transactional;
//import top.codef.dao.CommonDao;
//import top.codef.models.Page;
//import top.codef.models.Pageable;
//import top.codef.sqlfilter.CommonFilter;
//
///**
// * @author donald
// *
// * @param <T>
// */
//@Transactional
//public abstract class AbstractCURDService<T> {
//
//	@Autowired
//	protected CommonDao commonDao;
//
//	protected abstract Class<T> clazz();
//
//	public T getById(Serializable id) {
//		return commonDao.get(clazz(), id);
//	}
//
//	/**
//	 * @param filter
//	 * @param pageable
//	 * @return
//	 */
//	public Page<T> getPage(CommonFilter filter, Pageable pageable) {
//		Page<T> page = commonDao.getPage(clazz(), pageable, filter);
//		return page;
//	}
//
//	/**
//	 * @param filter
//	 * @return
//	 */
//	public List<T> getList(CommonFilter filter) {
//		List<T> list = commonDao.getList(clazz(), filter);
//		return list;
//	}
//
//	/**
//	 * @param filter
//	 */
//	public void modify(CommonFilter filter) {
//		commonDao.update(clazz(), filter);
//	}
//
//	public T createOrModify(T t) {
//		commonDao.createOrUpdate(t);
//		return t;
//	}
//
//	public void del(Serializable id) {
//		commonDao.delete(clazz(), id);
//	}
//
//	public void del(CommonFilter filter) {
//		commonDao.delete(clazz(), filter);
//	}
//
//	public T getSingle(CommonFilter commonFilter) {
//		return commonDao.getSingle(clazz(), commonFilter);
//	}
//
//	public Long count(CommonFilter commonFilter) {
//		return commonDao.count(clazz(), commonFilter);
//	}
//
//}
