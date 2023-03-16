package top.codef.exceptions;

public class NoResultQueryException extends RuntimeException {

	private static final long serialVersionUID = 3042834222253867910L;

	public NoResultQueryException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoResultQueryException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoResultQueryException(String message) {
		super(message);
	}

	public NoResultQueryException(Throwable cause) {
		super(cause);
	}
}
