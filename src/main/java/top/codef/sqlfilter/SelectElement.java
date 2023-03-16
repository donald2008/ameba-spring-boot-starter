package top.codef.sqlfilter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;

@FunctionalInterface
public interface SelectElement {

	Expression<?> select(CriteriaBuilder builder, Path<?> path);
}
