/**
 * 
 */
package digital.amigo.util;

import digital.amigo.jsengine.core.Rule;
import digital.amigo.jsengine.RuleType;
import digital.amigo.jsengine.core.EngineOptions;

/**
 * Clase utilitaria para pruebas
 * @author jorge.morando
 *
 */
public class TestUtils {

	private static final String DEFAULT_FACT_NAME = EngineOptions.defaultOptions().factName();
	private static final String DEFAULT_CONTEXT_NAME = EngineOptions.defaultOptions().contextName();

	public static final String RULE_NAME = "decision_rule";

	public static final String RULE_1_VALUE = "decision";
	public static final String RULE_2_VALUE = "decision2";

	public static final String RULE_CLEAN_CODE  = DEFAULT_CONTEXT_NAME+"."+RULE_NAME+" = "+DEFAULT_FACT_NAME+".name == '"+RULE_1_VALUE+"';";
	public static final String RULE_CLEAN_CODE2  = DEFAULT_CONTEXT_NAME+"."+RULE_NAME+" = "+DEFAULT_FACT_NAME+".name == '"+RULE_2_VALUE+"';";

	
	public static final Rule CLEAN_RULE = getCleanRule();
	public static final Rule CLEAN_RULE_v2 = getCleanRuleV2();
	
	private static Rule getCleanRule(){
		Rule rule = new Rule(RULE_NAME,RULE_CLEAN_CODE, RuleType.DECISION);
		return rule;
	}
	private static Rule getCleanRuleV2(){
		Rule rule = new Rule(RULE_NAME,RULE_CLEAN_CODE2, RuleType.DECISION);
		return rule;
	}
}
