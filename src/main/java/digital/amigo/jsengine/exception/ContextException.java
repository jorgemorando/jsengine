/**
 * 
 */
package digital.amigo.jsengine.exception;

/**
 * Excepci&oacute; en el flujo de resoluci&oacute;n de contexto de regla para un evento
 * @author jorge.morando
 *
 */
public class ContextException extends RuleEngineException {

	private static final long serialVersionUID = -6400468908428370468L;
	
	/**
	 * @param message
	 * @param cause
	 */
	public ContextException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public ContextException(Throwable cause) {
		super(cause);
	}

	public ContextException(String message) {
		super(message);
	}
}
