package br.study.ebah.miguel.cdsCatalog.repo;

/**
 * 
 * @author bruno
 * 
 */
public class RepositoryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9054360958485078900L;

	public RepositoryException() {
		super();
	}

	public RepositoryException(final String message) {
		super(message);
	}

	public RepositoryException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public RepositoryException(final Throwable cause) {
		super(cause);
	}

	public RepositoryException(final String message, final Throwable cause,
			final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
