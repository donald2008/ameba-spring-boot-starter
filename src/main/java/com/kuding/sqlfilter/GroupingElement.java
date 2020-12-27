package com.kuding.sqlfilter;

import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GroupingElement {

	private Set<String> fields;

	public GroupingElement() {
		fields = new HashSet<String>();
	}

	public GroupingElement(String... field) {
		fields = Arrays.stream(field).collect(toSet());
	}

	public Set<String> getFields() {
		return fields;
	}

	public void setFields(Set<String> fields) {
		this.fields = fields;
	}

	public void add(String... fields) {
		this.fields.addAll(Arrays.asList(fields));
	}

}
