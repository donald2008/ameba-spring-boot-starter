package top.codef.sqlfilter;

import jakarta.persistence.criteria.JoinType;

public class JoinTable extends Element<JoinType> {

	public JoinTable() {
		super();
	}

	public JoinTable(String field, JoinType value) {
		super(field, value);
	}
}
