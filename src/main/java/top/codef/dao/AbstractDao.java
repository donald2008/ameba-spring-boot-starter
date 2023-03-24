package top.codef.dao;

import java.io.Serializable;

import org.hibernate.Session;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

public abstract class AbstractDao implements AmebaDao {

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * 获取当前数据连接对应的entitymanager
	 *
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * 获取hibernate session
	 * @return
	 */
	public Session getSession() {
		return entityManager.unwrap(Session.class);
	}

	/**
	 * 持久化实体
	 * 
	 * @param <T> Entity实体类型
	 * @param entity 要创建的实体
	 */
	public <T> void create(T entity) {
		entityManager.persist(entity);
	}

	/**
	 * 游离化实体
	 * 
	 * @param <T>
	 * @param entity
	 */
	public <T> void detach(T entity) {
		entityManager.detach(entity);
	}

	/**
	 * @param <T>
	 * @param entity
	 */
	public <T> void createOrUpdate(T entity) {
		entityManager.persist(entity);
	}

	/**
	 * 
	 * 合并实体
	 * 
	 * @param <T>
	 * @param entity
	 */
	public <T> void merge(T entity) {
		entityManager.merge(entity);
	}

	/**
	 * 通过id删除实体
	 * 
	 * @param <T>
	 * @param clazz
	 * @param id 实体id
	 */
	public <T> void delete(Class<T> clazz, Serializable id) {
		T entity = entityManager.find(clazz, id);
		entityManager.remove(entity);
	}

	/**
	 * 删除实体
	 * 
	 * @param <T>
	 * @param value
	 */
	public <T> void delete(T value) {
		entityManager.remove(value);
	}

	/**
	 * 
	 * 根据id获取实体
	 * 
	 * @param <T>
	 * @param clazz
	 * @param id 实体id
	 * @return
	 */
	public <T> T get(Class<? extends T> clazz, Serializable id) {
		T entity = entityManager.find(clazz, id);
		return entity;
	}

	/**
	 * 
	 * 同步持久化上下文
	 * 
	 */
	public void flush() {
		entityManager.flush();
	}

	/**
	 * 同步持久化对象
	 * 
	 * @param <T>
	 * @param entity
	 */
	public <T> void refresh(T entity) {
		entityManager.refresh(entity);
	}

	/**
	 * 
	 * 给实体对象加锁
	 * 
	 * @param <T>
	 * @param entity
	 * @param lockModeType
	 */
	public <T> void lock(T entity, LockModeType lockModeType) {
		entityManager.lock(entity, lockModeType);
	}
}
