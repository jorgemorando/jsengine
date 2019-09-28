/**
 * 
 */
package digital.amigo.jsengine.exception;

/**
 * Excepci&oacute; en el flujo de procesamiento de regla con un evento
 * @author jorge.morando
 *
 */
public class ProcessRuleException extends RuleEngineException {

	private static final long serialVersionUID = -6400468908428370468L;
	
	/**
	 * @param message
	 * @param cause
	 */
	public ProcessRuleException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public ProcessRuleException(Throwable cause) {
		super(cause);
	}

	public ProcessRuleException(String message) {
		super(message);
	}
}
