/**
 * 
 */
package digital.amigo.util;

import digital.amigo.jsengine.Rule;

/**
 * Clase utilitaria para pruebas
 * @author jorge.morando
 *
 */
public class TestUtils {

	public static final String RULE_NAME = "decision_rule";

	public static final String RULE_1_VALUE = "decision";
	public static final String RULE_2_VALUE = "decision2";

	public static final String RULE_CLEAN_CODE  = "context.decision_rule = object.name == '"+RULE_1_VALUE+"';";
	public static final String RULE_CLEAN_CODE2  = "context.decision_rule =  object.name == '"+RULE_2_VALUE+"';";
	
	public static final Rule CLEAN_RULE = getCleanRule();
	public static final Rule CLEAN_RULE_v2 = getCleanRuleV2();
	
	private static Rule getCleanRule(){
		Rule rule = new Rule(RULE_NAME,RULE_CLEAN_CODE);
		return rule;
	}
	private static Rule getCleanRuleV2(){
		Rule rule = new Rule(RULE_NAME,RULE_CLEAN_CODE2);
		return rule;
	}
}
