package com.kuding.dao;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;

public abstract class AbstractDao implements AmebaDao {

	@PersistenceContext
	private EntityManager entityManager;

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Session getSession() {
		return entityManager.unwrap(Session.class);
	}

	public <T> void create(T entity) {
		entityManager.persist(entity);
	}

	public <T> void detach(T entity) {
		entityManager.detach(entity);
	}

	public <T> void createOrUpdate(T entity) {
		entityManager.unwrap(Session.class).saveOrUpdate(entity);
	}

	public <T> void merge(T entity) {
		entityManager.merge(entity);
	}

	public <T> void delete(Class<T> clazz, Serializable id) {
		T entity = entityManager.find(clazz, id);
		entityManager.remove(entity);
	}

	public <T> void delete(T value) {
		entityManager.remove(value);
	}

	public <T> T get(Class<? extends T> clazz, Serializable id) {
		T entity = entityManager.find(clazz, id);
		return entity;
	}

	public void flush() {
		entityManager.flush();
	}

	public <T> void refresh(T entity) {
		entityManager.refresh(entity);
	}

	public <T> void lock(T entity, LockModeType lockModeType) {
		entityManager.lock(entity, lockModeType);
	}
}
