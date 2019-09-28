package digital.amigo.jsengine.control;

import digital.amigo.jsengine.Context;
import digital.amigo.jsengine.Fact;
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
	 * @param name
	 * @param fact
	 * @return
	 */
	public TriggerResult trigger(String name, Fact fact);
	
	/**
	 * Dispara una regla con el hecho especficado<br>
	 * Si la regla tiene diferentes versiones, dispara la &uacute;ltima versi&oacute;n
	 * @param name
	 * @param fact
	 * @return
	 */
	public TriggerResult trigger(String name, Fact fact, Context ctx);
	
	/**
	 * Dispara una versi&oacute;n espec&iacute;fica de una regla con el hecho especificado. 
	 * @param name
	 * @param version
	 * @param fact
	 * @return
	 */
	public TriggerResult trigger(String name, int version, Fact fact, Context ctx);
	
	
	/**
	 * Ejecuta un plan de disparos de regla estructurado. 
	 * @param plan
	 * @return
	 */
//	public ExecutionResult execute(ExecutionPlan plan);
	
	
}
