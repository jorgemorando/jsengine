/**
 * 
 */
package digital.amigo.jsengine.core;

import digital.amigo.jsengine.Rule;
import digital.amigo.jsengine.exception.RuleEngineException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static digital.amigo.jsengine.utils.Assertions.*;

/**
 * Administrador de reglas del motor de reglas JavaScript.<br>
 * Se encarga de llevar un repositorio en memoria de todas las versiones de las reglas compiladas en el motor.
 * @author jorge.morando
 *
 */
public final class RuleRegistry {

	private final Logger log = LoggerFactory.getLogger(RuleRegistry.class);
	
	private EngineCore js;
	
	private String factName;
	
	private String contextName;
	
	private Map<String, Versioned<Rule>> rules;

	private Map<String, ScriptObjectMirror> compiledRules;
	
	public RuleRegistry(String factName, String contextName, EngineCore js){
		this.js = js;
		this.factName = factName;
		this.contextName = contextName;
		bootstrap();
	}
	
	private void bootstrap(){
		rules = new HashMap<>();
		compiledRules = new HashMap<>();
	}

	public Map<String, Versioned<Rule>> listRules(){
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
	 * @param rule
	 * @throws RuleEngineException Si la regla proporcionada es nula o si ya existe en el repositorio. Para actualizar una regla existente utilizar {@link RuleRegistry#update(Rule)}
	 * @return {@link Versioned}.
	 */
	public Versioned<Rule> register(Rule rule){
		return register(rule,0);
	}
	
	/**
	 * Agrega una versi&oacute;n espec&iacute;fica de la regla. 
	 * 
	 * @param rule
	 * @throws RuleEngineException Si la regla proporcionada es nula o si ya existe en el repositorio. Para actualizar una regla existente utilizar {@link RuleRegistry#update(Rule)}
	 * @return {@link Versioned}.
	 */
	public Versioned<Rule> register(Rule rule, int version){
		assertNotNull(rule, "Null Rule provided to RuleRegistry for adition.");
		assertNotNull(rule, "Null Version provided to RuleRegistry for adition.");
		Versioned<Rule> vRule = null;
		
		if(version==0){//agraga una versión con número v+1
			log.debug("Intentando agregar la regla \""+rule.getName()+"\" al repositorio de reglas");
			vRule = get(rule.getName());
			if(vRule == null){
				vRule = Versioned.of(rule);
			}else{
				vRule.add(rule);
			}
		}else{//agrega una versión con número especificado
			log.debug("Intentando agregar la versión \""+version+"\" de la regla \""+rule.getName()+"\" al repositorio de reglas");
			assertFalse(has(rule.getName(), version), "Version \""+version+"\" of rule \""+rule.getName()+"\" is already loaded.");
			
			if(has(rule.getName())) {
				vRule = get(rule.getName());
				vRule.update(version,rule);
			}else {
				vRule = Versioned.of(rule,version);
			}
		}
		String code =  completeCode(rule);
		log.debug(code);
		ScriptObjectMirror ruleReference = (ScriptObjectMirror) js.loadScript(rule.getName(),code);
		
		compiledRules.put(Versioned.name(rule.getName(),version==0?vRule.getLatestVersion():version), ruleReference);
		rules.put(rule.getName(), vRule);
		
		return vRule; 
	}
	
	/**
	 * Actualizar una regla con una nueva versi&oacute;n.
	 * @param rule
	 * @throws RuleEngineException Si la regla proporcionada es nula o si no existe en el repositorio. Para agregar una regla nueva utilizar {@link RuleRegistry#register(Rule)}
	 * @return {@link Versioned}.
	 */
	public Versioned<Rule> update(Rule rule){
		return update(rule,0);
	}
	
	/**
	 * Actualizar una versi&oacute;n de una regla ya registrada.
	 * @param rule
	 * @throws RuleEngineException Si la regla proporcionada es nula, si no existe en el repositorio o si la versi&oacute;n no existe. Para agregar una regla nueva utilizar {@link RuleRegistry#register(Rule)}
	 * @return {@link Versioned}.
	 */
	public Versioned<Rule> update(Rule rule, Integer version){
		assertNotNull(rule, "Null Rule provided to RuleRegistry for update.");
		assertNotNull(rule, "Null Version provided to RuleRegistry for update.");
		log.debug("Intentando actualizar la versión "+(version==0?"":version+" ")+"de la regla "+rule.getName()+" del repositorio de reglas");
		Versioned<Rule> vRule = null;
		
		if(version==0){
			assertTrue(has(rule.getName()), "Rule \""+rule.getName()+"\" not loaded.");
		}else{
			assertTrue(has(rule.getName(), version), "Version \""+version+"\" of rule \""+rule.getName()+"\" not loaded.");
		}
		
		vRule = rules.get(rule.getName());
		
		ScriptObjectMirror ruleReference = (ScriptObjectMirror) js.loadScript(rule.getName(), completeCode(rule));
		
		vRule.update(version,rule);
		
		compiledRules.put(Versioned.name(rule.getName(),version==0?vRule.getLatestVersion():version), ruleReference);
		
		return vRule;
	}
	
	/**
	 * Agrega una nueva regla, y si la regla ya existe ({@link RuleRegistry#has(String)}), la actualiza agregando una nueva versi&oacute;n.
	 * 
	 * @param rule
	 * @return {@link Versioned}.
	 */
	public Versioned<Rule> addOrUpdate(Rule rule){
		return addOrUpdate(rule,0);
	}
	
	/**
	 * Agrega una nueva versi&oacute;n de regla, y si la versi&oacute;n ya existe ({@link RuleRegistry#has(String,Integer)}), la actualiza agregando una nueva versi&oacute;n.
	 * 
	 * @param rule
	 * @return {@link Versioned}.
	 */
	public Versioned<Rule> addOrUpdate(Rule rule,Integer version){
		Versioned<Rule> vRule = null;
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
	 * @see RuleRegistry#update(Rule)
	 * @param ruleName
	 * @throws RuleEngineException Si el nombre es nulo o si la regla no existe en el repositorio.
	 * @return {@link Rule}
	 */
	public Rule getLatest(String ruleName){
		assertNotNull(ruleName, "Must specify Rule name");
		Versioned<Rule> vRule = rules.get(ruleName);
		assertNotNull(vRule, "Rule not versioned in RuleRegistry");
		return vRule.getLatest();
	}
	
	/**
	 * Devuelve la regla versionada con el nombre especificado o <strong>null</strong> si no existe una regla en el repositorio con ese nombre.
	 * 
	 * @see RuleRegistry#register(Rule)
	 * @see RuleRegistry#addOrUpdate(Rule)
	 * @see RuleRegistry#update(Rule)
	 * @param ruleName
	 * @throws RuleEngineException Si el nombre es nulo.
	 * @return {@link Versioned}
	 */
	public Versioned<Rule> get(String ruleName){
		assertNotNull(ruleName, "Must specify Rule name");
		Versioned<Rule> vRule = rules.get(ruleName);
		return vRule;
	}
	
	/**
	 * Devuelve una versi&oacute;n espec&iacute;fica de la regla con el nombre <strong>ruleName</strong> o <strong>null</strong> si la versi&oacute;n especificada no existe.
	 * @param ruleName
	 * @param version
	 * 
	 * @throws RuleEngineException Si ruleName es nulo o si la regla no existe en el repositorio.
	 * @return {@link Rule} 
	 */
	public Rule get(String ruleName,int version){
		assertNotNull(ruleName, "Must specify Rule name");
		if(!has(ruleName)) return null;
		Versioned<Rule> vRule = rules.get(ruleName);
		assertNotNull(vRule, "Rule not versioned in RuleRegistry");
		return vRule.get(version);
	}
	
	/**
	 * @return the compiledRules
	 */
	public Map<String, ScriptObjectMirror> getCompiledRules() {
		return Collections.unmodifiableMap(compiledRules);
	}
	
	/**
	 * Verifica que la regla est&eacute; versionada en el repositorio
	 * @param ruleName
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
	 * @param ruleName
	 * @param version
	 * @return boolean
	 */
	public boolean has(String ruleName, int version){
		if(ruleName == null)
			return false;
		Versioned<Rule> vRule = rules.get(ruleName);
		if(vRule==null)
			return false;
		if(version==0)//noop para entender que hace version==0 (trabaja sobre la última versión)
			return true;
		return (vRule.get(version)!=null);
	}
	
	/*----------------------------- PRIVATE -------------------------------*/
	private String completeCode(Rule rule){
		StringBuilder f = new StringBuilder();
		
		f.append("function");
		f.append("("+factName+", "+contextName+"){\n");
		//f.append(factName+"=JSON.parse("+factName+");\n");//si es un objeto java debería parsearse, pero tarda mucho tiempo
		String code = rule.getCode().trim();
		f.append(code);
		if(!code.endsWith(";")){
			f.append(";");
		}
		f.append("\n");
		f.append("return ("+contextName+"."+rule.getName()+"==undefined  || "+contextName+"."+rule.getName()+"==null ? false:"+contextName+"."+rule.getName()+");\n");
		f.append("};\n");
		
		/* CODIGO VIEJO

		f.append("function");
//		f.append(" "+rule.getName());
		f.append("("+objectContextName+", "+contextName+"){\n");
//		f.append("print(autorizacion);");
//		f.append("autorizacion = JSON.parse(autorizacion);");
//		f.append("try{");
//		f.append("return JSON.stringify(result);");

		f.append("return ("+rule.getCode()+");\n");
		
//		f.append("}catch(e){");
//		//TODO: Investigar porque no se puede retornar excepciones
//		f.append("if(typeof e == 'object'){");
//		f.append("var result = {\"error\":e};");
//		f.append("return result;");
//		f.append("}else{");
//		f.append("var result = {\"error\":e.toString()}");
//		f.append("return result;");
//		f.append("}");
//		f.append("print(e);");
//		f.append("return null;");
//		f.append("}");
		f.append("};\n");
		
		*/
		return f.toString();
	}
	
}
