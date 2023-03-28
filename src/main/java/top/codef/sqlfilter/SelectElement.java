package top.codef.sqlfilter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;

@FunctionalInterface
public interface SelectElement {

	Expression<?> select(CriteriaBuilder builder, Path<?> path);
}
