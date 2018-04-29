package com.kuding.sqlfilter;

import com.kuding.enums.OrderEnum;

public class OrderBy extends Element<OrderEnum> {

	public OrderBy(String field, OrderEnum enOrderEnum) {
		super(field, enOrderEnum);
	}

}
