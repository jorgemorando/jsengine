/**
 * 
 */
package digital.amigo.jsengine;

import java.io.Serializable;

/**
 * Interfaz base para objetos que ser&aacute;n analizados contra reglas.
 * @author jorge.morando
 *
 */
public interface Fact extends Serializable {

	/**
	 * Devuelve una representaci&oacute;n alfanumérica del objeto en formato JSON
	 * @return String json representation of the object
	 */
	String toStringJson();
	
}
