package digital.amigo.jsengine.utils;

import digital.amigo.jsengine.exception.RuleEngineException;

/**
 * Utilidades del motor de reglas:<br>
 * 
 * {@link Assertions#assertNotNull(Object, String)}<br>
 * {@link Assertions#assertNull(Object, String)}<br>
 * {@link Assertions#assertTrue(boolean, String)}<br>
 * @author Jorge
 *
 */
public final class Assertions {

	/**
	 * Verifica que el par&aacute;metro 'o' <strong>no</strong> sea nulo o devuelve una RuleEngineException con el mensaje especificado
	 * @param o
	 * @param msg
	 */
	public static void assertNotNull(Object o,String msg){
		if(o==null)
			throw new RuleEngineException(msg);
	}
	
	/**
	 * Verifica que el par&aacute;metro 'o' sea nulo o devuelve una RuleEngineException con el mensaje especificado
	 * @param o
	 * @param msg
	 */
	public static void assertNull(Object o,String msg){
		if(o!=null)
			throw new RuleEngineException(msg);
	}
	
	/**
	 * Verifica que el par&aacute;metro booleano sea verdadero o devuelve una RuleEngineException con el mensaje especificado
	 * @param o
	 * @param msg
	 */
	public static void assertTrue(boolean o,String msg){
		if(!o)
			throw new RuleEngineException(msg);
	}
	
	/**
	 * Verifica que el par&aacute;metro booleano sea falso o devuelve una RuleEngineException con el mensaje especificado
	 * @param o
	 * @param msg
	 */
	public static void assertFalse(boolean o,String msg){
		if(o)
			throw new RuleEngineException(msg);
	}
}
