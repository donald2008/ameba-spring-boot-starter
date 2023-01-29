package com.kuding.sqlfilter.functional;

import jakarta.persistence.criteria.Path;

@FunctionalInterface
public interface GroupCondition extends FilterModel {

	Path<?> groupBy(Path<?> path);
}
