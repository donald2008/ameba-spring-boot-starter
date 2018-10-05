package com.kuding.models;

import com.kuding.enums.OrderEnum;

public class Pageable {

	private Long pageCount;// 页数

	private int eachPageSize = 15;// 每页大小

	private int pageNo = 1;// 页码

	private OrderEnum order = OrderEnum.DESC;// 排序方式

	private String orderStr = "id";// 排序字段名

	private Long totalCount;

	/**
	 * @return the pageCount
	 */
	public Long getPageCount() {
		return pageCount;
	}

	/**
	 * @param pageCount
	 *            the pageCount to set
	 */
	public void setPageCount(Long pageCount) {
		this.pageCount = pageCount;
	}

	/**
	 * @return the eachPageSize
	 */
	public int getEachPageSize() {
		return eachPageSize;
	}

	/**
	 * @param eachPageSize
	 *            the eachPageSize to set
	 */
	public void setEachPageSize(int eachPageSize) {
		this.eachPageSize = eachPageSize;
	}

	/**
	 * @return the pageNo
	 */
	public int getPageNo() {
		return pageNo;
	}

	/**
	 * @param pageNo
	 *            the pageNo to set
	 */
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	/**
	 * @return the order
	 */
	public OrderEnum getOrder() {
		return order;
	}

	/**
	 * @param order
	 *            the order to set
	 */
	public void setOrder(OrderEnum order) {
		this.order = order;
	}

	/**
	 * @return the orderStr
	 */
	public String getOrderStr() {
		return orderStr;
	}

	/**
	 * @param orderStr
	 *            the orderStr to set
	 */
	public void setOrderStr(String orderStr) {
		this.orderStr = orderStr;
	}

	/**
	 * @return the totalCount
	 */
	public Long getTotalCount() {
		return totalCount;
	}

	/**
	 * @param totalCount
	 *            the totalCount to set
	 */
	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Pageable [pageCount=" + pageCount + ", eachPageSize=" + eachPageSize + ", pageNo=" + pageNo + ", order="
				+ order + ", orderStr=" + orderStr + ", totalCount=" + totalCount + "]";
	}

}
