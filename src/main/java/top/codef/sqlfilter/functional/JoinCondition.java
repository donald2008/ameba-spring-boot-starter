package top.codef.sqlfilter.functional;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

@FunctionalInterface
public interface JoinCondition extends FilterModel {

	Join<?, ?> join(Root<?> root);
}
