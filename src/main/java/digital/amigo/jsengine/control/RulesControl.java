package digital.amigo.jsengine.control;

import digital.amigo.jsengine.rule.Rule;
import digital.amigo.jsengine.core.RuleRegistry;

import java.util.Set;

/**
 * Interfaz de registro de reglas del motor de reglas<br>
 * Indica el administrador correspondiente de entidades
 * 
 * @see RuleRegistry
 * 
 * @author jorge.morando
 *
 */
public interface RulesControl extends RegistrationControl<Rule> {

	/**
	 * Devuelve el administrador de reglas del motor.
	 * @return {@link RuleRegistry }
	 */
	RuleRegistry getRuleRegistry();


	/**
	 * Devuelve una lista con todos los nombres de las entidades registradas en el administrador del motor
	 * @return Set of registered rules
	 */
	Set<String> list();
}
