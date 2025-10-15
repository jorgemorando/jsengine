package digital.amigo.jsengine.core;

import digital.amigo.jsengine.RuleEngineContext;
import digital.amigo.jsengine.Fact;
import digital.amigo.jsengine.Rule;
import digital.amigo.jsengine.TriggerResult;
import digital.amigo.jsengine.control.EngineControl;
import digital.amigo.jsengine.control.RulesControl;
import digital.amigo.jsengine.control.TriggerControl;
import digital.amigo.jsengine.exception.RuleEngineException;
import digital.amigo.jsengine.utils.Assertions;
import digital.amigo.jsengine.utils.Versioned;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Motor de Reglas JavaScript 
 * @author jorge.morando
 *
 */
public final class RuleEngine implements EngineControl, RulesControl, TriggerControl {

	private final Logger log = LoggerFactory.getLogger(RuleEngine.class);
	
	private EngineCore engine;
	
	private RuleRegistry ruleRegistry;
	
	private String factName;
	
	private String contextName;

	private RuleEngine(EngineOptions options){
		log.info("Inicializando Motor de Reglas");
		bootstrap(options.factName(), options.contextName(), options);
	}

	private RuleEngine(){
		log.info("Inicializando Motor de Reglas con configuración por defecto");
		var options = EngineOptions.defaultOptions();
		bootstrap(options.factName(), options.contextName(), options);
	}

	private void bootstrap(String factName, String contextName, EngineOptions options){
		this.engine = new EngineCore(options);
		this.factName = factName;
		this.contextName = contextName;
		this.ruleRegistry = new RuleRegistry(this.engine);
	}
	
	/* (non-Javadoc)
	 * @see EngineControl#reset()
	 */
	@Override
	public void reset() {
		log.info("Reseteando estado de Motor de Reglas");
		log.error("IMPLEMENT METHOD");
		//bootstrap(this.factName, this.contextName, this.options);
	}
	
	@Override
	public void register(Rule rule) {
		register(rule,0);
	}

	@Override
	public void register(List<Rule> rules) {
		rules.forEach(this::register);
	}

	public void register(Rule rule, int version) {
		log.debug("Agregando regla "+rule.getName()+" al motor");
		ruleRegistry.register(rule,version);
	}
	
	public void registerSilently(Rule rule, int version) {
		log.debug("Agregando regla "+rule.getName()+" al motor");
		try {
			ruleRegistry.register(rule,version);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
	}
	
	/* (non-Javadoc)
	 * @see digital.amigo.jsengine.EngineControl#getRuleControl()
	 */
	@Override
	public RulesControl getRulesControl(){
		return this;
	}
	
	/* (non-Javadoc)
	 * @see digital.amigo.jsengine.EngineControl#getTriggerControl()
	 */
	@Override
	public TriggerControl getTriggerControl(){
		return this;
	}

	/* (non-Javadoc)
	 * @see digital.amigo.jsengine.RegistrationControl#isRegistered(java.lang.String)
	 */
	@Override
	public boolean isRegistered(String ruleName) {
		return ruleRegistry.has(ruleName);
	}

	/* (non-Javadoc)
	 * @see digital.amigo.jsengine.RegistrationControl#list()
	 */
	@Override
	public Set<String> list() {
		return ruleRegistry.listRules().keySet();
	}
	
	/* (non-Javadoc)
	 * @see digital.amigo.jsengine.RuleControl#getRuleRegistry()
	 */
	@Override
	public RuleRegistry getRuleRegistry() {
		return ruleRegistry;
	}
	
	/* (non-Javadoc)
	 * @see digital.amigo.jsengine.TriggerControl#trigger(java.lang.String, digital.amigo.jsengine.fact.Fact)
	 */
	@Override
	public TriggerResult trigger(String ruleName, Fact fact) {
		return trigger(ruleName,0,fact,null);
	}
	
	/* (non-Javadoc)
	 * @see digital.amigo.jsengine.TriggerControl#trigger(java.lang.String, digital.amigo.jsengine.fact.Fact,Context)
	 */
	@Override
	public TriggerResult trigger(String ruleName, Fact fact, RuleEngineContext ctx) {
		return trigger(ruleName,0,fact,ctx);
	}

	/* (non-Javadoc)
	 * @see digital.amigo.jsengine.TriggerControl#trigger(java.lang.String, int, digital.amigo.jsengine.fact.Fact)
	 */
	@Override
	public TriggerResult trigger(String ruleName, int version, Fact fact, RuleEngineContext ctx) {
		
		if(Objects.isNull(ctx))
			ctx = RuleEngineContext.empty();
		
		TriggerResult result = new TriggerResult();
		result.setRuleEngineContext(ctx);
		result.setFact(fact);

		Assertions.assertNotNull(ruleName,"Rule name must be specified for triggering.");
		
		try {
			Assertions.assertTrue(ruleRegistry.has(ruleName),"Rule \""+ruleName+"\" not registered");
			if(version == 0){
				VersionedRule vRule = ruleRegistry.get(ruleName);
				version = vRule.latest();
			}else{
				Assertions.assertTrue(ruleRegistry.has(ruleName, version),"Version \""+version+"\" of rule \""+ruleName+"\" not registered");
			}
		} catch (RuleEngineException e) {
			String stack = ExceptionUtils.getStackTrace(e);
			log.warn("Error when triggering rule \"{}\": {} -> {}",ruleName,e.getMessage(),stack);
			result.getMessages().add("Error when triggering rule \""+ruleName+"\":"+e.getMessage()+" -> "+stack);
			return result;
		}
		String memberName =  Versioned.name(ruleName, version);
		var compiled = ruleRegistry.getCompiledRules().get(memberName);

		log.trace("Disparando regla: '{}' version: {}",ruleName,version);

		result.setRule(ruleRegistry.get(ruleName, version));
		result.setVersion(version);

		try {
			result.setSuccess(compiled.execute(fact, ctx).asBoolean());
			result.setFired(true);
		} catch (Exception e) {
			log.error("Error capturado en disparo de regla {} version: {}",ruleName,version,e);
			String stack = ExceptionUtils.getStackTrace(e);
			result.setSuccess(false);
			result.getMessages().add("Error when triggering rule \""+ruleName+"\" -> "+stack);
		}
		return result;
	}
	
	/*-------------------BUILDER------------------*/
	/**
	 * Devuelve una instancia del objeto constructor del motor de reglas
	 * @see RuleEngine.RuleEngineBuilder
	 * @return RuleEngine.RuleEngineBuilder
	 */
	public static RuleEngineBuilder newBuilder(){
		return new RuleEngineBuilder();
	}

	public static RuleEngineBuilder newDefaultBuilder(){
		return new RuleEngineBuilder().withOptions(EngineOptions.defaultOptions());
	}
	
	/**
	 * Objeto constructor de instancias de control y registro de reglas del motor
	 * @author jorge.morando
	 */
	public static class RuleEngineBuilder {
		
		private EngineOptions options = EngineOptions.defaultOptions();

		private final List<Rule> rules = new ArrayList<>();
		
		private RuleEngineBuilder(){}
		
		public RuleEngineBuilder withOptions(EngineOptions options){
			this.options = options;
			return this;
		}

		public RuleEngineBuilder withRules(Rule ... rules){
			this.rules.addAll(Arrays.asList(rules));
			return this;
		}
		
		public EngineControl build(){
			RuleEngine instance = new RuleEngine(this.options);
			instance.register(this.rules);
			return instance;
		}
	}
}
