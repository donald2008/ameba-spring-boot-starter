package com.kuding.sqlfilter.functional;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;

@FunctionalInterface
public interface SelectCondition extends FilterModel {

	Expression<?> select(CriteriaBuilder builder, Path<?> path);
}
