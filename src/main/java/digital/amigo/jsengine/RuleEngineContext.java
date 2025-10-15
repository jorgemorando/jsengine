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
public abstract class RuleEngineContext extends HashMap<String,Object> {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -6840884947784664898L;

	/**
	 * Devuelve una representaci&oacute; alfanumérica del objeto en formato JSON
	 * @return
	 */
	public abstract String toStringJson();
	
	public static RuleEngineContext empty(){
		return new DefaultRuleEngineContext();
	}
	
	public static RuleEngineContext clone(RuleEngineContext ruleEngineContext){
		RuleEngineContext ctx = empty();
		ctx.putAll(ruleEngineContext);
		return ctx;
	}
	
	public static RuleEngineContext fromMap(Map<String,Object> map){
		RuleEngineContext ctx = empty();
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
