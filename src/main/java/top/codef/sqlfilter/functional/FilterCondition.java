package top.codef.sqlfilter.functional;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

@FunctionalInterface
public interface FilterCondition extends FilterModel {

	public Predicate condition(CriteriaBuilder builder, Path<?> path);
}
