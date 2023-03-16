package top.codef.exceptions;

public class HaveReasonException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6617394761169065331L;

	public HaveReasonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public HaveReasonException(String message, Throwable cause) {
		super(message, cause);
	}

	public HaveReasonException(String message) {
		super(message);
	}

	public HaveReasonException(Throwable cause) {
		super(cause);
	}

}
