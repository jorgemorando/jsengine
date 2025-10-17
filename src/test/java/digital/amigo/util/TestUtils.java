/**
 * 
 */
package digital.amigo.util;

import digital.amigo.jsengine.core.EngineOptions;
import digital.amigo.jsengine.rule.Rule;

import static digital.amigo.jsengine.rule.FieldExpression.factField;


/**
 * Clase utilitaria para pruebas
 * @author jorge.morando
 *
 */
public class TestUtils {

	//FIXME: make 'fact', 'context' and 'globals' reserved keywords
	private static final String DEFAULT_FACT_NAME = EngineOptions.defaultOptions().factName();
	private static final String DEFAULT_CONTEXT_NAME = EngineOptions.defaultOptions().contextName();

	public static final String RULE_NAME = "decision_rule";
	public static final String RULE_NAME2 = "decision_rule2";
	public static final String RULE_NAME3 = "decision_rule3";

	public static final String RULE_1_VALUE = "foo";
	public static final String RULE_2_VALUE = "bar";
	public static final String RULE_3_VALUE = "baz";

	public static final Rule CLEAN_RULE = getCleanRule();
	public static final Rule CLEAN_RULE2 = getCleanRule2();
	public static final Rule CLEAN_RULE3 = getCleanRule3();
	public static final Rule CLEAN_RULE_v2 = getCleanRuleV2();
	
	private static Rule getCleanRule(){
		return Rule.createWithName(RULE_NAME)
				.when(
						factField("name").isEqualTo(RULE_1_VALUE)
				)
				.then("true")
				.build();

//        return new Rule(RULE_NAME,RULE_CLEAN_CODE, RuleType.DECISION);
	}
	private static Rule getCleanRule2(){
		return Rule.createWithName(RULE_NAME2)
				.when(
						factField("name").isEqualTo(RULE_2_VALUE)
				)
				.then("true")
				.build();
	}
	private static Rule getCleanRuleV2(){
		return Rule.createWithName(RULE_NAME)
				.when(
						factField("name").isEqualTo(RULE_2_VALUE)
				)
				.then("true")
				.build();
	}

	private static Rule getCleanRule3(){
		return Rule.createWithName(RULE_NAME3)
				.when(
						factField("inner").exists()
					.and(
						factField("inner.name").exists()
					).and(
						factField("inner.name").isEqualTo(RULE_3_VALUE)
					)
				)
				.then("true")
				.build();
	}
}
