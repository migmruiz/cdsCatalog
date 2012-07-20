/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.sql;

/**
 * Thrown to indicate that a method has no data on used database.
 * 
 * @author miguel
 * 
 */
public class SQLDBNoDataException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7462413733033253399L;

	/**
	 * Constructs an <code>DBNoDataException</code> with no detail message.
	 */
	public SQLDBNoDataException() {
		super();
	}

	/**
	 * Constructs an <code>DBNoDataException</code> with the specified detail
	 * message.
	 * 
	 * @param s
	 *            the detail message.
	 */
	public SQLDBNoDataException(String s) {
		super(s);
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * 
	 * <p>
	 * Note that the detail message associated with <code>cause</code> is
	 * <i>not</i> automatically incorporated in this exception's detail message.
	 * 
	 * @param message
	 *            the detail message (which is saved for later retrieval by the
	 *            {@link Throwable#getMessage()} method).
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link Throwable#getCause()} method). (A <tt>null</tt> value
	 *            is permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 * @since 1.5
	 */
	public SQLDBNoDataException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception with the specified cause and a detail message
	 * of <tt>(cause==null ? null : cause.toString())</tt> (which typically
	 * contains the class and detail message of <tt>cause</tt>). This
	 * constructor is useful for exceptions that are little more than wrappers
	 * for other throwables (for example,
	 * {@link java.security.PrivilegedActionException}).
	 * 
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link Throwable#getCause()} method). (A <tt>null</tt> value
	 *            is permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 * @since 1.5
	 */
	public SQLDBNoDataException(Throwable cause) {
		super(cause);
	}

}
