package top.codef.sqlfilter;

import top.codef.enums.OrderEnum;

public class OrderBy extends Element<OrderEnum> {

	public OrderBy(String field, OrderEnum enOrderEnum) {
		super(field, enOrderEnum);
	}

}
