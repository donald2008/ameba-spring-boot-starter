package top.codef.dao;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;

public abstract class AbstractDaoWithClass<T> implements AmebaDao {

	@PersistenceContext
	private EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Session getSession() {
		return entityManager.unwrap(Session.class);
	}

	protected abstract Class<T> clazz();

	public void create(T entity) {
		entityManager.persist(entity);
	}

	public void detach(T entity) {
		entityManager.detach(entity);
	}

	public void createOrUpdate(T entity) {
		entityManager.unwrap(Session.class).saveOrUpdate(entity);
	}

	public void merge(T entity) {
		entityManager.merge(entity);
	}

	public void delete(Serializable id) {
		T entity = entityManager.find(clazz(), id);
		entityManager.remove(entity);
	}

	public void delete(T value) {
		entityManager.remove(value);
	}

	public T get(Serializable id) {
		T entity = entityManager.find(clazz(), id);
		return entity;
	}

	public void flush() {
		entityManager.flush();
	}

	public void refresh(T entity) {
		entityManager.refresh(entity);
	}

}
