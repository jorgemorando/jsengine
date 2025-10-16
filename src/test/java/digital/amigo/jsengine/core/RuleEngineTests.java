package digital.amigo.jsengine.core;

import digital.amigo.jsengine.DefaultFact;
import digital.amigo.jsengine.DefaultRuleEngineContext;
import digital.amigo.jsengine.MultiTriggerResult;
import digital.amigo.jsengine.TriggerResult;
import digital.amigo.jsengine.control.EngineControl;
import digital.amigo.jsengine.control.RulesControl;
import digital.amigo.jsengine.control.RuleEvaluationControl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static digital.amigo.util.TestUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RuleEngineTests {

	private final Logger log = LoggerFactory.getLogger(RuleEngineTests.class);
	
	@Test
	public void testInstance(){
		log.debug(">> Probando Instanciación con librerías.");
		EngineControl controls = RuleEngine.newBuilder().build();
		assertNotNull(controls);
	}
	
	@Test
	public void testRuleRegistration(){
		log.debug(">> Probando registración de regla.");
		
		RulesControl rule = RuleEngine.newBuilder()
				.withRules(CLEAN_RULE)
				.build()
				.getRulesControl();
		
		assertTrue(rule.isRegistered(RULE_NAME));
		assertEquals(1,rule.list().size());
	}

	@Test
	public void testRuleEngineContext(){
		log.debug(">> Probando context compartido de disparo de regla.");

		var rules = RuleEngine.newBuilder()
				.withRules(CLEAN_RULE,CLEAN_RULE2)
				.build()
				.getTriggerControl();

		DefaultFact fact = new DefaultFact();
		fact.put("name",RULE_1_VALUE);
		DefaultRuleEngineContext ctx = new DefaultRuleEngineContext();

		var results = rules.evaluateRulesFor(fact,ctx);

		assertNotNull("Should have a context",results.getContext());
		assertEquals("Should have 2 context entrances",2, results.getContext().keySet().size());
		log.debug("Trigger Result object {}",results);
	}
	
	@Test
	public void testRuleFire(){
		log.debug(">> Probando disparo exitoso/fallido de regla.");
		EngineControl controls = RuleEngine.newBuilder()
				.withRules(CLEAN_RULE)
				.build();
		RuleEvaluationControl engine = controls.getTriggerControl();
		DefaultFact fact = new DefaultFact();

		fact.put("name", "decision");//success
		TriggerResult result = engine.evaluate(RULE_NAME, fact, null);
		assertNotNull(result);
		assertTrue(result.isFired());
		assertTrue(result.isSuccess());
		log.debug("Trigger Result object {}",result);
		log.debug(">> disparo exitoso correcto.");

		fact.put("name", "other");//fail
		TriggerResult result2 = engine.evaluate(RULE_NAME, fact, null);
		assertNotNull(result2);
		assertTrue(result2.isFired());
		assertFalse(result2.isSuccess());
		log.debug("Trigger Result object {}",result2);
		log.debug(">> disparo fallido correcto.");
	}

	@Test
	public void testRulesFire(){
		log.debug(">> Probando disparo exitoso/fallido de reglas.");
		EngineControl controls = RuleEngine.newBuilder()
				.withRules(CLEAN_RULE,CLEAN_RULE2)
				.build();
		RuleEvaluationControl engine = controls.getTriggerControl();
		DefaultFact fact = new DefaultFact();
		DefaultRuleEngineContext ctx = new DefaultRuleEngineContext();

		fact.put("name", "decision");//success on Rule 1 and fail on Rule 2
		MultiTriggerResult results = engine.evaluateRulesFor(fact,ctx);
		assertNotNull(results);
        assertEquals("Should fire 2 rules instead of " + results.getResultCount(), 2, results.getResultCount());
		var successful = results.getSuccessful();
		var failed = results.getFailed();
		assertTrue("Should have 1 success and 1 failure",successful.size()==1 && failed.size()==1);
        assertEquals("Rule1 should be successful", CLEAN_RULE.getName(), successful.getFirst().getRuleVersion().name());
        assertEquals("Rule2 should be failure", CLEAN_RULE2.getName(), failed.getFirst().getRuleVersion().name());
		log.debug("Trigger Result object {}",results);
		log.debug(">> disparo exitoso correcto.");

		fact = new DefaultFact();
		ctx = new DefaultRuleEngineContext();

		fact.put("name", "decision2");//success on Rule 2 and fail on Rule 1
		MultiTriggerResult results2 = engine.evaluateRulesFor(fact, ctx);
		assertNotNull(results2);
		assertEquals("Should fire 2 rules instead of " + results2.getResultCount(), 2, results.getResultCount());

		successful = results2.getSuccessful();
		failed = results2.getFailed();

		assertTrue("Should have 1 success and 1 failure",successful.size()==1 && failed.size()==1);
		assertEquals("Rule2 should be successful", CLEAN_RULE2.getName(), successful.getFirst().getRuleVersion().name());
		assertEquals("Rule1 should be failure", CLEAN_RULE.getName(), failed.getFirst().getRuleVersion().name());

		log.debug("Trigger Result first fire {}",results);
		log.debug("Trigger Result second fire {}",results2);
	}
	
	@Test
	public void testRuleVersioning(){
		log.debug(">> Probando versionado de regla.");
		EngineControl controls = RuleEngine
				.newBuilder()
				.withOptions(EngineOptions.defaultOptions())
				.build();
		RulesControl rule = controls.getRulesControl();
		
		rule.register(CLEAN_RULE);
		assertTrue(rule.isRegistered(RULE_NAME));
		assertEquals(1,rule.list().size());
		assertEquals(1,rule.getRuleRegistry().getVersionsOf(RULE_NAME).latest());
		
		rule.register(CLEAN_RULE_v2);
		assertTrue(rule.isRegistered(RULE_NAME));
		assertEquals(1,rule.list().size());
		assertEquals(2,rule.getRuleRegistry().getVersionsOf(RULE_NAME).latest());
	}
	
	@Test
	public void testVersionedRuleFire(){
		log.debug(">> Probando disparo versionado de regla.");
		EngineControl controls = RuleEngine.newBuilder()
				.build();
		RulesControl rule = controls.getRulesControl();
		RuleEvaluationControl engine = controls.getTriggerControl();
		
		DefaultFact fact = new DefaultFact();
		
		rule.register(CLEAN_RULE);
		rule.register(CLEAN_RULE_v2);
		
		//success on v1
		fact.put("name", RULE_1_VALUE);
		int v = 1;
		log.debug(">> Probando success en \"{}\" v{}.",RULE_NAME,v);
		TriggerResult result = engine.evaluate(RULE_NAME, v,fact, null);
		assertNotNull(result);
		assertTrue(result.isFired());
		assertTrue(result.isSuccess());
		assertEquals(v, result.getRuleVersion().version());
		log.debug(">> Passed");

		//fail on v1
		fact.put("name", "other");
		log.debug(">> Probando fail en \"{}\" v{}.",RULE_NAME,v);
		result = engine.evaluate(RULE_NAME, v,fact, null);
		assertNotNull(result);
		assertTrue(result.isFired());
		assertFalse(result.isSuccess());
		assertEquals(v, result.getRuleVersion().version());
		log.debug(">> Passed");

		//success on v2
		v = 2;
		fact.put("name", RULE_2_VALUE);
		log.debug(">> Probando success en \"{}\" v{}.",RULE_NAME,v);
		result = engine.evaluate(RULE_NAME, v,fact, null);
		assertNotNull(result);
		assertTrue(result.isFired());
		assertTrue(result.isSuccess());
		assertEquals(v, result.getRuleVersion().version());
		log.debug(">> Passed");

		//fail on v2
		fact.put("name", "other");//failure
		log.debug(">> Probando fail en \"{}\" v{}.",RULE_NAME,v);
		result = engine.evaluate(RULE_NAME, v,fact, null);
		assertNotNull(result);
		assertTrue(result.isFired());
		assertFalse(result.isSuccess());
		assertEquals(v, result.getRuleVersion().version());
		log.debug(">> Passed");

		//success on latest
		fact.put("name", RULE_2_VALUE);//success
		log.debug(">> Probando success \"{}\" (latest).",RULE_NAME);
		result = engine.evaluate(RULE_NAME, fact, null);//default version latest
		assertNotNull(result);
		assertTrue(result.isFired());
		assertTrue(result.isSuccess());
		assertEquals(v, result.getRuleVersion().version());
		log.debug(">> Passed");

		//fail on latest
		fact.put("name", "other");//failure
		log.debug(">> Probando fail \"{}\" (latest).",RULE_NAME);
		result = engine.evaluate(RULE_NAME, fact, null);//default version latest
		assertNotNull(result);
		assertTrue(result.isFired());
		assertFalse(result.isSuccess());
		assertEquals(v, result.getRuleVersion().version());
		log.debug(">> Passed");
	}
	
	
	@Test
	public void testSpeed(){
		log.debug(">> Probando velocidad de disparo simple.");
		RuleEvaluationControl control = RuleEngine.newBuilder()
				.withRules(CLEAN_RULE)
				.build()
				.getTriggerControl();
		
		DefaultFact fact = new DefaultFact();
		fact.put("name", "decision");//success
		
		long start = System.currentTimeMillis();
		int fires = 1000;
		
		for (int i = 0; i < fires; i++) {
			TriggerResult result = control.evaluate(RULE_NAME, fact, null);
			assertTrue(result.isFired() && result.isSuccess());
		}
		
		long finish = System.currentTimeMillis();
		long took = finish-start;
		String msg1 = fires+" fires in "+took+" milliseconds";
		double fps1 = fires/((double)took/1000);
		String stat1 = fps1+" fires/sec";
		
		
		assertTrue("Slow system (<1000 fires/sec)", fps1>1000);
		
		/*======================================================*/
		
		start = System.currentTimeMillis();
		fires = 10000;
		for (int i = 0; i < fires; i++) {
			TriggerResult result = control.evaluate(RULE_NAME, fact, null);
			assertTrue(result.isFired() && result.isSuccess());
		}
		
		finish = System.currentTimeMillis();
		took = finish-start;
		String msg2 = fires+ " fires in "+took+" milliseconds"; 
		double fps2 = fires/((double)took/1000);
		String stat2 = fps2+" fires/sec";
		
		assertTrue("Slow system (<2000 fires/sec)", fps2>2000);
		
		/*======================================================*/
		
		start = System.currentTimeMillis();
		fires = 1000000;
		for (int i = 0; i < fires; i++) {
			TriggerResult result = control.evaluate(RULE_NAME, fact, null);
			assertTrue(result.isFired() && result.isSuccess());
		}
		
		finish = System.currentTimeMillis();
		took = finish-start;
		String msg3 = fires+" fires in "+took+" milliseconds"; 
		double fps3 = fires/((double)took/1000);
		String stat3 = fps3+" fires/sec";
		
		assertTrue("Slow system (<6000 fires/sec)", fps3>6000);
		
		
		log.debug(msg1+" - "+stat1);
		log.debug(msg2+" - "+stat2);
		log.debug(msg3+" - "+stat3);
		
	}

}
