package top.codef.sqlfilter.functional;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;

@FunctionalInterface
public interface OrderCondition extends FilterModel {

	public Order order(CriteriaBuilder builder, Path<?> path);

}
