package de.hrw.swep.votingservice.persistence;

/**
 * @author andriesc
 *
 */
public class PersistenceException extends RuntimeException {

	private static final long serialVersionUID = 4409868268974540924L;

	public PersistenceException() {
		super();
	}

	public PersistenceException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

	public PersistenceException(String message, Throwable cause) {
		super(message, cause);

	}

	public PersistenceException(String message) {
		super(message);

	}

	public PersistenceException(Throwable cause) {
		super(cause);

	}
}
