package top.codef.models;

public class ResultModel<T> extends StatusResultModel {

	private T result;

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "ResultModel [result=" + result + "]";
	}
}
