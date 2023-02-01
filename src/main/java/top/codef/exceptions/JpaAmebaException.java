package top.codef.exceptions;

public class JpaAmebaException extends RuntimeException {

	private static final long serialVersionUID = -8878042718421706287L;

	public JpaAmebaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public JpaAmebaException(String message, Throwable cause) {
		super(message, cause);
	}

	public JpaAmebaException(String message) {
		super(message);
	}

	public JpaAmebaException(Throwable cause) {
		super(cause);
	}

}
