package digital.amigo.jsengine.core;

import digital.amigo.jsengine.exception.RuleEngineException;
import digital.amigo.jsengine.utils.Versioned;
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
 *
 * @author jorge.morando
 */
 public final class RuleRegistry {

    private final Logger log = LoggerFactory.getLogger(RuleRegistry.class);

    private final EngineCore engine;

    private Map<String, VersionedRule> rules;

    private Map<String, CompiledRule> compiledRules;

    RuleRegistry(EngineCore engine) {
        this.engine = engine;
        bootstrap();
    }

    private void bootstrap() {
        rules = new HashMap<>();
        compiledRules = new HashMap<>();
    }

    public Map<String, VersionedRule> listRules() {
        if (Objects.isNull(rules))
            rules = new HashMap<>();

        return this.rules;
    }

    public void reset() {
        log.debug("Reseteando RuleRegistry");
        bootstrap();
    }

    /**
     * Agrega una versi&oacute;n espec&iacute;fica de la regla.
     *
     * @param rule Rule
     * @return {@link Versioned}.
     * @throws RuleEngineException Si la versión de la regla proporcionada es nula o si ya existe en el repositorio.
     */
    public RuleVersion register(Rule rule, int version) {
        assertNotNull(rule, "Null Rule provided to RuleRegistry for adition.");

        VersionedRule ruleVersions = null;
        RuleVersion ruleVersion = null;

        //failsafe naming
        String code = "({})";
        String versionedRuleName = "noop";

        if (version == 0) {//agraga una versión con número v+1
            log.debug("Intentando agregar la regla \"" + rule.getName() + "\" al repositorio de reglas");
            ruleVersions = getVersionsOf(rule.getName());
            if (ruleVersions == null) {
                ruleVersions = VersionedRule.of(rule);

            } else {
                ruleVersions.add(rule);
            }
            ruleVersion = ruleVersions.getLatest();
        } else {//agrega una versión con número especificado
            log.debug("Intentando agregar la versión \"" + version + "\" de la regla \"" + rule.getName() + "\" al repositorio de reglas");
            assertFalse(has(rule.getName(), version), "Version \"" + version + "\" of rule \"" + rule.getName() + "\" is already loaded.");

            if (has(rule.getName())) {
                ruleVersions = getVersionsOf(rule.getName());
                ruleVersions.update(version, rule);
            } else {
                ruleVersions = VersionedRule.of(rule, version);
            }
            ruleVersion = ruleVersions.get(version);
        }

        rules.put(rule.getName(), ruleVersions);
        versionedRuleName = ruleVersion.versionName();
        log.debug(versionedRuleName);

        code = completeRuleCode(ruleVersion);
        log.debug(code);
        engine.loadScript(versionedRuleName, code);

        var ruleReference = engine.getExecutableReference(versionedRuleName);
        assertTrue(ruleReference.canExecute(), "Rule " + versionedRuleName + " is not executable");
        compiledRules.put(versionedRuleName, new CompiledRule(ruleVersion, ruleReference));

        return ruleVersion;
    }

    /**
     * Devuelve la &uacute;ltima versi&oacute;n de la regla con el nombre especificado
     *
     * @param ruleName String
     * @return {@link Rule}
     * @throws RuleEngineException Si el nombre es nulo o si la regla no existe en el repositorio.
     * @see RuleRegistry#register(Rule, int)
     */
    public RuleVersion getLatest(String ruleName) {
        assertNotNull(ruleName, "Must specify Rule name");
        VersionedRule vRule = rules.get(ruleName);
        assertNotNull(vRule, "Rule is not registered in RuleRegistry");
        return vRule.getLatest();
    }

    /**
     * Devuelve la regla versionada (con todas sus versiones) con el nombre especificado o <strong>null</strong> si no existe una regla en el repositorio con ese nombre.
     *
     * @param ruleName String
     * @return {@link Versioned}
     * @throws RuleEngineException Si el nombre es nulo.
     * @see RuleRegistry#register(Rule, int)
     */
    public VersionedRule getVersionsOf(String ruleName) {
        assertNotNull(ruleName, "Must specify Rule name");
        return rules.get(ruleName);
    }

    /**
     * Devuelve una versi&oacute;n espec&iacute;fica de la regla con el nombre <strong>ruleName</strong> o <strong>null</strong> si la versi&oacute;n especificada no existe.
     *
     * @param ruleName String
     * @param version  int
     * @return {@link Rule}
     * @throws RuleEngineException Si ruleName es nulo o si la regla no existe en el repositorio.
     */
    public RuleVersion get(String ruleName, int version) {
        assertNotNull(ruleName, "Must specify Rule name");
        if (!has(ruleName)) return null;
        VersionedRule vRule = rules.get(ruleName);
        assertNotNull(vRule, "Version not registered");
        return vRule.get(version);
    }

    /**
     * @return the compiledRules
     */
    public Map<String, CompiledRule> getCompiledRules() {
        return Collections.unmodifiableMap(compiledRules);
    }

    /**
     * Verifica que la regla est&eacute; versionada en el repositorio
     *
     * @param ruleName String
     * @return boolean
     */
    public boolean has(String ruleName) {
        return has(ruleName, 0);
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
     *
     * @param ruleName String
     * @param version  int
     * @return boolean
     */
    public boolean has(String ruleName, int version) {
        if (ruleName == null)
            return false;
        VersionedRule vRule = rules.get(ruleName);
        if (vRule == null)
            return false;
        if (version == 0)//noop para entender que hace version==0 (trabaja sobre la última versión)
            return true;
        return (vRule.get(version) != null);
    }

    /*----------------------------- PRIVATE -------------------------------*/
    private String completeRuleCode(RuleVersion ruleVersion) {
        var factName = engine.getOptions().factName();
        var contextName = engine.getOptions().contextName();
        var ruleName = ruleVersion.rule().getName();
        var memberName = ruleVersion.versionName();

        StringBuilder f = new StringBuilder();

        f.append("function");
        f.append(" ");
        f.append(memberName);
        f.append("(" + factName + ", " + contextName + "){\n");
        //f.append(factName+"=JSON.parse("+factName+");\n");//si es un objeto java debería parsearse, pero tarda mucho tiempo
        f.append(ruleVersion.rule().getRawCode().trim());
        if (!ruleVersion.rule().getRawCode().endsWith(";")) {
            f.append(";");
        }
        f.append("\n");
        f.append("return (" + contextName + "." + ruleName + "==undefined  || " + contextName + "." + ruleName + "==null ? false:" + contextName + "." + ruleName + ");\n");
        f.append("};\n");

        return f.toString();
    }


}
