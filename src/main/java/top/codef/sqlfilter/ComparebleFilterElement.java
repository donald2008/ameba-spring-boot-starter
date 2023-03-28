package top.codef.sqlfilter;

public class ComparebleFilterElement<T extends Comparable<T>> extends FilterElement<T> {

	public ComparebleFilterElement(String field, T value, FilterSymbol filterSymbol) {
		super(field, value, filterSymbol);
	}
}
