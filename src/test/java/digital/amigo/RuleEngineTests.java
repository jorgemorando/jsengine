package digital.amigo;

import digital.amigo.jsengine.DefaultFact;
import digital.amigo.jsengine.TriggerResult;
import digital.amigo.jsengine.control.EngineControl;
import digital.amigo.jsengine.control.RulesControl;
import digital.amigo.jsengine.control.TriggerControl;
import digital.amigo.jsengine.core.EngineOptions;
import digital.amigo.jsengine.core.RuleEngine;
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
	public void testRuleFire(){
		log.debug(">> Probando disparo exitoso/fallido de regla.");
		EngineControl controls = RuleEngine.newBuilder()
				.withRules(CLEAN_RULE)
				.build();
		TriggerControl engine = controls.getTriggerControl();
		DefaultFact fact = new DefaultFact();
		
		
		fact.put("name", "decision");//success
		TriggerResult result = engine.trigger(RULE_NAME, fact, null);
		assertNotNull(result);
		assertTrue(result.isFired());
		assertTrue(result.isSuccess());
		log.debug("Trigger Result object {}",result);
		log.debug(">> disparo exitoso correcto.");

		fact.put("name", "other");//fail
		TriggerResult result2 = engine.trigger(RULE_NAME, fact, null);
		assertNotNull(result2);
		assertTrue(result2.isFired());
		assertFalse(result2.isSuccess());
		log.debug("Trigger Result object {}",result2);
		log.debug(">> disparo fallido correcto.");
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
		assertEquals(1,rule.getRuleRegistry().get(RULE_NAME).latest());
		
		rule.register(CLEAN_RULE_v2);
		assertTrue(rule.isRegistered(RULE_NAME));
		assertEquals(1,rule.list().size());
		assertEquals(2,rule.getRuleRegistry().get(RULE_NAME).latest());
	}
	
	@Test
	public void testVersionedRuleFire(){
		log.debug(">> Probando disparo versionado de regla.");
		EngineControl controls = RuleEngine.newBuilder()
				.build();
		RulesControl rule = controls.getRulesControl();
		TriggerControl engine = controls.getTriggerControl();
		
		DefaultFact fact = new DefaultFact();
		
		rule.register(CLEAN_RULE);
		rule.register(CLEAN_RULE_v2);
		
		//success on v1
		fact.put("name", RULE_1_VALUE);
		int v = 1;
		log.debug(">> Probando success en \"{}\" v{}.",RULE_NAME,v);
		TriggerResult result = engine.trigger(RULE_NAME, v,fact, null);
		assertNotNull(result);
		assertTrue(result.isFired());
		assertTrue(result.isSuccess());
		assertEquals(v, result.getVersion());
		log.debug(">> Passed");

		//fail on v1
		fact.put("name", "other");
		log.debug(">> Probando fail en \"{}\" v{}.",RULE_NAME,v);
		result = engine.trigger(RULE_NAME, v,fact, null);
		assertNotNull(result);
		assertTrue(result.isFired());
		assertFalse(result.isSuccess());
		assertEquals(v, result.getVersion());
		log.debug(">> Passed");

		//success on v2
		v = 2;
		fact.put("name", RULE_2_VALUE);
		log.debug(">> Probando success en \"{}\" v{}.",RULE_NAME,v);
		result = engine.trigger(RULE_NAME, v,fact, null);
		assertNotNull(result);
		assertTrue(result.isFired());
		assertTrue(result.isSuccess());
		assertEquals(v, result.getVersion());
		log.debug(">> Passed");

		//fail on v2
		fact.put("name", "other");//failure
		log.debug(">> Probando fail en \"{}\" v{}.",RULE_NAME,v);
		result = engine.trigger(RULE_NAME, v,fact, null);
		assertNotNull(result);
		assertTrue(result.isFired());
		assertFalse(result.isSuccess());
		assertEquals(v, result.getVersion());
		log.debug(">> Passed");

		//success on latest
		fact.put("name", RULE_2_VALUE);//success
		log.debug(">> Probando success \"{}\" (latest).",RULE_NAME);
		result = engine.trigger(RULE_NAME, fact, null);//default version latest
		assertNotNull(result);
		assertTrue(result.isFired());
		assertTrue(result.isSuccess());
		assertEquals(v, result.getVersion());
		log.debug(">> Passed");

		//fail on latest
		fact.put("name", "other");//failure
		log.debug(">> Probando fail \"{}\" (latest).",RULE_NAME);
		result = engine.trigger(RULE_NAME, fact, null);//default version latest
		assertNotNull(result);
		assertTrue(result.isFired());
		assertFalse(result.isSuccess());
		assertEquals(v, result.getVersion());
		log.debug(">> Passed");
	}
	
	
	@Test
	public void testSpeed(){
		log.debug(">> Probando velocidad de disparo simple.");
		TriggerControl control = RuleEngine.newBuilder()
				.withRules(CLEAN_RULE)
				.build()
				.getTriggerControl();
		
		DefaultFact fact = new DefaultFact();
		fact.put("name", "decision");//success
		
		long start = System.currentTimeMillis();
		int fires = 1000;
		
		for (int i = 0; i < fires; i++) {
			TriggerResult result = control.trigger(RULE_NAME, fact, null);
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
			TriggerResult result = control.trigger(RULE_NAME, fact, null);
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
			TriggerResult result = control.trigger(RULE_NAME, fact, null);
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
