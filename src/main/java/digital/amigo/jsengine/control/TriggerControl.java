package digital.amigo.jsengine.control;

import digital.amigo.jsengine.Fact;
import digital.amigo.jsengine.RuleEngineContext;
import digital.amigo.jsengine.TriggerResult;

/**
 * Interfaz de control de disparo de reglas del motor
 * @author jorge.morando
 *
 */
public interface TriggerControl {
	
	
	/**
	 * Dispara una regla con el hecho especficado<br>
	 * Si la regla tiene diferentes versiones, dispara la &uacute;ltima versi&oacute;n
	 * @param name String
	 * @param fact Fact
	 * @return {@link TriggerResult }
	 */
	TriggerResult trigger(String name, Fact fact);
	
	/**
	 * Dispara una regla con el hecho especficado<br>
	 * Si la regla tiene diferentes versiones, dispara la &uacute;ltima versi&oacute;n
	 * @param name String
	 * @param fact {@link Fact }
	 * @param ctx {@link RuleEngineContext }
	 * @return {@link TriggerResult }
	 */
	TriggerResult trigger(String name, Fact fact, RuleEngineContext ctx);
	
	/**
	 * Dispara una versi&oacute;n espec&iacute;fica de una regla con el hecho especificado. 
	 * @param name String
	 * @param version int
	 * @param fact {@link Fact }
	 * @return {@link TriggerResult }
	 */
	TriggerResult trigger(String name, int version, Fact fact, RuleEngineContext ctx);


}
