package digital.amigo.jsengine.core;

import digital.amigo.jsengine.Rule;
import digital.amigo.jsengine.exception.RuleEngineException;
import digital.amigo.jsengine.utils.Versioned;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static digital.amigo.jsengine.utils.Assertions.assertFalse;
import static digital.amigo.jsengine.utils.Assertions.assertNotNull;
import static digital.amigo.jsengine.utils.Assertions.assertTrue;

/**
 * Administrador de reglas del motor de reglas JavaScript.<br>
 * Se encarga de llevar un repositorio en memoria de todas las versiones de las reglas compiladas en el motor.
 * @author jorge.morando
 *
 */
public final class RuleRegistry {

	private final Logger log = LoggerFactory.getLogger(RuleRegistry.class);

	private final EngineCore engine;
	
	private Map<String, VersionedRule> rules;

	private Map<String, Value> compiledRules;
	
	public RuleRegistry(EngineCore engine){
		this.engine = engine;
		bootstrap();
	}
	
	private void bootstrap(){
		rules = new HashMap<>();
		compiledRules = new HashMap<>();
	}

	public Map<String, VersionedRule> listRules(){
		if (Objects.isNull(rules))
			rules = new HashMap<>();

		return this.rules;
	}


	public void reset(){
		log.debug("Reseteando RuleRegistry");
		bootstrap();
	}
	
	/**
	 * Agrega una regla. 
	 * 
	 * @param rule Rule
	 * @throws RuleEngineException Si la regla proporcionada es nula o si ya existe en el repositorio. Para actualizar una regla existente utilizar {@link RuleRegistry#addOrUpdate(Rule, Integer)}
	 * @return {@link Versioned}.
	 */
	public VersionedRule register(Rule rule){
		return register(rule,0);
	}
	
	/**
	 * Agrega una versi&oacute;n espec&iacute;fica de la regla. 
	 * 
	 * @param rule Rule
	 * @throws RuleEngineException Si la regla proporcionada es nula o si ya existe en el repositorio. Para actualizar una regla existente utilizar {@link RuleRegistry#addOrUpdate(Rule, Integer)}
	 * @return {@link Versioned}.
	 */
	public VersionedRule register(Rule rule, int version){
		assertNotNull(rule, "Null Rule provided to RuleRegistry for adition.");
		assertNotNull(rule, "Null Version provided to RuleRegistry for adition.");

		VersionedRule vRule = null;
		String code ="({})";
		String versionedRuleName = "noop";

		if(version==0){//agraga una versión con número v+1
			log.debug("Intentando agregar la regla \""+rule.getName()+"\" al repositorio de reglas");
			vRule = get(rule.getName());
			if(vRule == null){
				vRule = VersionedRule.of(rule);

			}else{
				vRule.add(rule);
			}
			version = vRule.latest();
		}else{//agrega una versión con número especificado
			log.debug("Intentando agregar la versión \""+version+"\" de la regla \""+rule.getName()+"\" al repositorio de reglas");
			assertFalse(has(rule.getName(), version), "Version \""+version+"\" of rule \""+rule.getName()+"\" is already loaded.");
			
			if(has(rule.getName())) {
				vRule = get(rule.getName());
				vRule.update(version,rule);
			}else {
				vRule = VersionedRule.of(rule,version);
			}
		}
		versionedRuleName = VersionedRule.name(rule.getName(),version);
		log.debug(versionedRuleName);

		code = completeRuleCode(rule.getName(),versionedRuleName,rule.getRawCode());
		log.debug(code);
		engine.loadScript(versionedRuleName,code);

		var ruleReference = engine.getExecutableReference(versionedRuleName);
		
		compiledRules.put(Versioned.name(rule.getName(),version==0?vRule.latest():version), ruleReference);
		rules.put(rule.getName(), vRule);
		
		return vRule; 
	}
	
	/**
	 * Actualizar una versi&oacute;n de una regla ya registrada.
	 * @param rule Rule
	 * @throws RuleEngineException Si la regla proporcionada es nula, si no existe en el repositorio o si la versi&oacute;n no existe. Para agregar una regla nueva utilizar {@link RuleRegistry#register(Rule)}
	 * @return {@link Versioned}.
	 */
	public VersionedRule update(Rule rule, Integer version){
		assertNotNull(rule, "Null Rule provided to RuleRegistry for update.");
		assertNotNull(rule, "Null Version provided to RuleRegistry for update.");
		log.debug("Intentando actualizar la versión "+(version==0?"":version+" ")+"de la regla "+rule.getName()+" del repositorio de reglas");
		VersionedRule vRule = null;
		
		if(version==0){
			assertTrue(has(rule.getName()), "Rule \""+rule.getName()+"\" not loaded.");
		}else{
			assertTrue(has(rule.getName(), version), "Version \""+version+"\" of rule \""+rule.getName()+"\" not loaded.");
		}
		
		vRule = rules.get(rule.getName());

		String versionedRuleName = VersionedRule.name(rule.getName(),version);
		log.debug(versionedRuleName);

		String completedCode = completeRuleCode(rule.getName(),versionedRuleName,rule.getRawCode());
		log.debug(completedCode);

		engine.loadScript(versionedRuleName, completedCode);
		var ruleReference = engine.getExecutableReference(versionedRuleName);

		vRule.update(version,rule);
		
		compiledRules.put(Versioned.name(rule.getName(),version==0?vRule.latest():version), ruleReference);
		
		return vRule;
	}
	
	/**
	 * Agrega una nueva regla, y si la regla ya existe ({@link RuleRegistry#has(String)}), la actualiza agregando una nueva versi&oacute;n.
	 * 
	 * @param rule Rule
	 * @return {@link Versioned}.
	 */
	public VersionedRule addOrUpdate(Rule rule){
		return addOrUpdate(rule,0);
	}
	
	/**
	 * Agrega una nueva versi&oacute;n de regla, y si la versi&oacute;n ya existe ({@link RuleRegistry#has(String,int)}), la actualiza agregando una nueva versi&oacute;n.
	 * 
	 * @param rule Rule
	 * @param version int
	 * @return {@link Versioned}.
	 */
	public VersionedRule addOrUpdate(Rule rule, Integer version){
		VersionedRule vRule = null;
		if(has(rule.getName(),version)){
			vRule = update(rule,version);
		}else{
			vRule = register(rule,version);
		}
		return vRule;
	}
	
	/**
	 * Devuelve la &uacute;ltima versi&oacute;n de la regla con el nombre especificado
	 * 
	 * @see RuleRegistry#register(Rule)
	 * @see RuleRegistry#addOrUpdate(Rule)
	 * @param ruleName String
	 * @throws RuleEngineException Si el nombre es nulo o si la regla no existe en el repositorio.
	 * @return {@link Rule}
	 */
	public Rule getLatest(String ruleName){
		assertNotNull(ruleName, "Must specify Rule name");
		VersionedRule vRule = rules.get(ruleName);
		assertNotNull(vRule, "Rule not versioned in RuleRegistry");
		return vRule.getLatest();
	}
	
	/**
	 * Devuelve la regla versionada con el nombre especificado o <strong>null</strong> si no existe una regla en el repositorio con ese nombre.
	 * 
	 * @see RuleRegistry#register(Rule)
	 * @see RuleRegistry#addOrUpdate(Rule)
	 * @param ruleName String
	 * @throws RuleEngineException Si el nombre es nulo.
	 * @return {@link Versioned}
	 */
	public VersionedRule get(String ruleName){
		assertNotNull(ruleName, "Must specify Rule name");
		VersionedRule vRule = rules.get(ruleName);
		return vRule;
	}
	
	/**
	 * Devuelve una versi&oacute;n espec&iacute;fica de la regla con el nombre <strong>ruleName</strong> o <strong>null</strong> si la versi&oacute;n especificada no existe.
	 * @param ruleName String
	 * @param version int
	 * 
	 * @throws RuleEngineException Si ruleName es nulo o si la regla no existe en el repositorio.
	 * @return {@link Rule} 
	 */
	public Rule get(String ruleName,int version){
		assertNotNull(ruleName, "Must specify Rule name");
		if(!has(ruleName)) return null;
		VersionedRule vRule = rules.get(ruleName);
		assertNotNull(vRule, "Rule not versioned in RuleRegistry");
		return vRule.get(version);
	}
	
	/**
	 * @return the compiledRules
	 */
	public Map<String, Value> getCompiledRules() {
		return Collections.unmodifiableMap(compiledRules);
	}
	
	/**
	 * Verifica que la regla est&eacute; versionada en el repositorio
	 * @param ruleName String
	 * @return boolean
	 */
	public boolean has(String ruleName){
		return has(ruleName,0);
	}
	
	/**
	 * Verifica que exista la versi&oacute;n especificada de la regla con nombre <strong>ruleName</strong> en el repositorio;
	 * <br>
	 * Devuelve TRUE solo si se cumplen los siguientes pasos:
	 * <ol>
	 * <li>ruleName != null</li>
	 * <li>RuleRegistry.has(ruleName) != null</li>
	 * <li>Versioned&ltRule&gt;.getVersion(version) != null</li>
	 * </ol> 
	 * @param ruleName String
	 * @param version int
	 * @return boolean
	 */
	public boolean has(String ruleName, int version){
		if(ruleName == null)
			return false;
		VersionedRule vRule = rules.get(ruleName);
		if(vRule==null)
			return false;
		if(version==0)//noop para entender que hace version==0 (trabaja sobre la última versión)
			return true;
		return (vRule.get(version)!=null);
	}

	/*----------------------------- PRIVATE -------------------------------*/
	private String completeRuleCode(String ruleName, String memberName, String code){
		var factName = engine.getOptions().factName();
		var contextName = engine.getOptions().contextName();

		StringBuilder f = new StringBuilder();

		f.append("function");
		f.append(" ");
		f.append(memberName);
		f.append("("+factName+", "+contextName+"){\n");
		//f.append(factName+"=JSON.parse("+factName+");\n");//si es un objeto java debería parsearse, pero tarda mucho tiempo
		f.append(code.trim());
		if(!code.endsWith(";")){
			f.append(";");
		}
		f.append("\n");
		f.append("return ("+contextName+"."+ruleName+"==undefined  || "+contextName+"."+ruleName+"==null ? false:"+contextName+"."+ruleName+");\n");
		f.append("};\n");

		return f.toString();
	}


}
