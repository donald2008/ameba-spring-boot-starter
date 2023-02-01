package top.codef.models;

import java.util.ArrayList;
import java.util.List;

public class Page<T> {

	private Pageable pageable;

	private List<T> content = new ArrayList<T>(0);

	public Page(Pageable pageable) {
		this.pageable = pageable;
	}

	public Page() {
	}

	/**
	 * @return the pageable
	 */
	public Pageable getPageable() {
		return pageable;
	}

	/**
	 * @param pageable the pageable to set
	 */
	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}

	/**
	 * @return the content
	 */
	public List<T> getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(List<T> content) {
		this.content = content;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Page [pageable=" + pageable + ", content=" + content + "]";
	}

}
