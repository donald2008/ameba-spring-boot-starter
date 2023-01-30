package com.kuding.sqlfilter.typed;

import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.cglib.proxy.Enhancer;

import com.kuding.exceptions.JpaAmebaException;
import com.kuding.sqlfilter.CommonFilter;
import com.kuding.sqlfilter.Element;

import jakarta.persistence.Entity;

public class TypedCommonFilter<E> extends CommonFilter {

	private final Class<E> clazz;

	private final E proxy;

	private final EntityMethodInterceptor entityMethodInterceptor;

	@SuppressWarnings("unchecked")
	public TypedCommonFilter(Class<E> clazz) {
		super();
		Entity entity = clazz.getAnnotation(Entity.class);
		if (entity == null)
			throw new JpaAmebaException("the class must be Jpa entity");
		this.clazz = clazz;
		this.entityMethodInterceptor = new EntityMethodInterceptor();
		this.proxy = (E) Enhancer.create(clazz, entityMethodInterceptor);
	}

	public Class<E> getClazz() {
		return clazz;
	}

	public E getProxy() {
		return proxy;
	}

	private <R> String field(Function<E, R> function) {
		function.apply(proxy);
		return entityMethodInterceptor.getPropName();
	}

	private Set<String> fields(@SuppressWarnings("unchecked") Function<E, ?>... functions) {
		var fields = Stream.of(functions).peek(x -> x.apply(proxy)).map(x -> entityMethodInterceptor.getPropName())
				.collect(toSet());
		return fields;
	}

	public <T> TypedCommonFilter<E> update(Function<E, T> function, T value) {
		update(field(function), value);
		return this;
	}

	public List<Element<? extends Object>> getUpdatableList() {
		return updatableList;
	}

	public <T> TypedCommonFilter<E> eq(Function<E, T> function, T value) {
		super.eq(field(function), value);
		return this;
	}

	public <T> TypedCommonFilter<E> neq(Function<E, T> function, T value) {
		super.neq(field(function), value);
		return this;
	}

	public <T> TypedCommonFilter<E> like(Function<E, T> function, String value) {
		super.like(field(function), value);
		return this;
	}

	public <T> TypedCommonFilter<E> isNull(Function<E, T> function) {
		super.isNull(field(function));
		return this;
	}

	public <T> TypedCommonFilter<E> isNotNull(Function<E, T> function) {
		super.isNotNull(field(function));
		return this;
	}

	public <T> TypedCommonFilter<E> in(Function<E, T> function, Collection<T> value) {
		super.in(field(function), value);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> TypedCommonFilter<E> in(Function<E, T> function, T... value) {
		super.in(field(function), Arrays.asList(value));
		return this;
	}

	public <T> TypedCommonFilter<E> notIn(Function<E, T> function, Collection<T> value) {
		super.notIn(field(function), value);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> TypedCommonFilter<E> notIn(Function<E, T> function, T... value) {
		super.notIn(field(function), Arrays.asList(value));
		return this;
	}

	public <T extends Number> TypedCommonFilter<E> lt(Function<E, T> function, T value) {
		super.lt(field(function), value);
		return this;
	}

	public <T extends Number> TypedCommonFilter<E> le(Function<E, T> function, T value) {
		super.le(field(function), value);
		return this;
	}

	public <T extends Number> TypedCommonFilter<E> gt(Function<E, T> function, T value) {
		super.gt(field(function), value);
		return this;
	}

	public <T extends Number> TypedCommonFilter<E> ge(Function<E, T> function, T value) {
		super.ge(field(function), value);
		return this;
	}

	@SuppressWarnings("unchecked")
	public TypedCommonFilter<E> orderByAsc(Function<E, Object>... functions) {
		super.orderByAsc(fields(functions).toArray(new String[] {}));
		return this;
	}

	@SuppressWarnings("unchecked")
	public <R> TypedCommonFilter<E> OrderByDesc(Function<E, Object>... functions) {
		super.orderByDesc(fields(functions).toArray(new String[] {}));
		return this;
	}

	@SuppressWarnings("unchecked")
	public TypedCommonFilter<E> groupBy(Function<E, Object>... functions) {
		super.groupBy(fields(functions).toArray(new String[] {}));
		return this;
	}

	@SuppressWarnings("unchecked")
	public TypedCommonFilter<E> select(Function<E, Object>... functions) {
		super.select(fields(functions));
		return this;
	}
}
