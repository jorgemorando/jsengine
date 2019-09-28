package digital.amigo.jsengine.control;

/**
 * Interfaz de controles del motor de reglas<br>
 * @author jorge.morando
 *
 */
public interface EngineControl {

	/**
	 * Devuelve el control de disparo de regla del motor
	 * @return
	 */
	public TriggerControl getTriggerControl();
	
	/**
	 * Devuelve el control de gesti&oacute;n de reglas del motor
	 * @return
	 */
	public RulesControl getRulesControl();
	
	/**
	 * Devuelve el motor al estado original.
	 */
	public void reset();
	
}
