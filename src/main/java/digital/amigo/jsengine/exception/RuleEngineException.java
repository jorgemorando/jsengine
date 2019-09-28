/**
 * 
 */
package digital.amigo.jsengine.exception;

/**
 * @author jorge.morando
 *
 */
public class RuleEngineException extends RuntimeException {

	private static final long serialVersionUID = 3515789692741948966L;
	
	/**
	 * @param message
	 * @param cause
	 */
	public RuleEngineException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public RuleEngineException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public RuleEngineException(Throwable cause) {
		super(cause);
	}

}
