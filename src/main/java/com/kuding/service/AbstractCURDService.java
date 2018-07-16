package com.kuding.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.kuding.dao.CommonDao;
import com.kuding.models.Page;
import com.kuding.models.Pageable;
import com.kuding.sqlfilter.CommonFilter;

public abstract class AbstractCURDService<T> {

	@Autowired
	private CommonDao commonDao;

	protected abstract Class<T> clazz();

	public T getById(Serializable id) {
		return commonDao.get(clazz(), id);
	}

	public Page<T> getPage(CommonFilter filter, Pageable pageable) {
		Page<T> page = commonDao.getPage(clazz(), pageable, filter);
		return page;
	}

	public List<T> getList(CommonFilter filter) {
		List<T> list = commonDao.getList(clazz(), filter);
		return list;
	}

	public void modify(CommonFilter filter) {
		commonDao.update(clazz(), filter);
	}

	public T modify(T t) {
		commonDao.createOrUpdate(t);
		return t;
	}

	public void del(Serializable id) {
		commonDao.delete(clazz(), id);
	}

	public void del(CommonFilter filter) {
		commonDao.delete(clazz(), filter);
	}
}
