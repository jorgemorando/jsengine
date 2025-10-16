package digital.amigo.jsengine.control;

/**
 * Interfaz de controles del motor de reglas<br>
 * @author jorge.morando
 *
 */
public interface EngineControl {

	/**
	 * Devuelve el control de disparo de regla del motor
	 * @return RuleEvaluationControl
	 */
	RuleEvaluationControl getTriggerControl();
	
	/**
	 * Devuelve el control de gesti&oacute;n de reglas del motor
	 * @return RulesControl
	 */
	RulesControl getRulesControl();
	
	/**
	 * Devuelve el motor al estado original.
	 */
	void reset();
	
}
