/**
 * 
 */
package digital.amigo.jsengine;

import java.util.HashMap;
import java.util.Map;

/**
 * Interfaz base para objetos que ser&aacute;n utilizados de contexto paranalizados contra reglas.
 * @author jorge.morando
 *
 */
public abstract class Context extends HashMap<String,Object> {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -6840884947784664898L;

	/**
	 * Devuelve una representaci&oacute; alfanumérica del objeto en formato JSON
	 * @return
	 */
	public abstract String toStringJson();
	
	public static Context empty(){
		return new DefaultContext();
	}
	
	public static Context clone(Context context){
		Context ctx = empty();
		ctx.putAll(context);
		return ctx;
	}
	
	public static Context fromMap(Map<String,Object> map){
		Context ctx = empty();
		ctx.putAll(map);
		return ctx;
	}

	/* (non-Javadoc)
	 * @see java.util.AbstractMap#toString()
	 */
	@Override
	public String toString() {
		return toStringJson();
	}
	
	
	
}
