package top.codef.sqlfilter;

public class FilterElement<T> extends Element<T> {

	protected FilterSymbol fsy;

	public FilterElement(String field, T value, FilterSymbol fsy) {
		this.field = field;
		this.value = value;
		this.fsy = fsy;
	}

	public FilterElement() {
	}

	public FilterSymbol getFsy() {
		return fsy;
	}

	public void setFsy(FilterSymbol fsy) {
		this.fsy = fsy;
	}

	@Override
	public String toString() {
		return "FilterElement [field=" + field + ", value=" + value + ", fsy=" + fsy + "]";
	}

}
