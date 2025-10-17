/**
 * 
 */
package digital.amigo.jsengine.core;

import digital.amigo.jsengine.DefaultRuleEngineContext;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyArray;
import org.graalvm.polyglot.proxy.ProxyObject;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Interfaz base para objetos que ser&aacute;n utilizados de contexto paranalizados contra reglas.
 * @author jorge.morando
 *
 */
public abstract class RuleEngineContext extends HashMap<String,Object> implements ProxyObject {
		
	/**
	 * 
	 */
	@Serial
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

	/**
	 * Convierte esta instancia en un Mapa de valores
	 * @return
	 */
	public Map<String,Object> toMap(){
		return (Map<String,Object>)this;
	}

	@Override
	public Object getMember(String key) {
		return get(key);
	}

	@Override
	public Object getMemberKeys() {
		return new ProxyArray() {
			private final Object[] keys = keySet().toArray();

			public void set(long index, Value value) {
				throw new UnsupportedOperationException();
			}

			public long getSize() {
				return keys.length;
			}

			public Object get(long index) {
				if (index < 0 || index > Integer.MAX_VALUE) {
					throw new ArrayIndexOutOfBoundsException();
				}
				return keys[(int) index];
			}

		};
	}

	@Override
	public boolean hasMember(String key) {
		return containsKey(key);
	}

	@Override
	public void putMember(String key, Value value) {
		put(key,value);
	}

	@Override
	public boolean removeMember(String key) {
		return Objects.nonNull(remove(key));
	}
	
}
