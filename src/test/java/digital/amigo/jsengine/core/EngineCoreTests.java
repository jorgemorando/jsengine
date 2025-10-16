package digital.amigo.jsengine.core;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EngineCoreTests {

    private final Logger log = LoggerFactory.getLogger(EngineCoreTests.class);

    @Test
    public void testInstance() {
        log.debug(">> Probando Instanciación del núcleo del motor.");
        EngineCore engineCore = new EngineCore(EngineOptions.defaultOptions());
        assertNotNull(engineCore);
        assertTrue(engineCore.memberExists(EngineCore.ENGINE_GLOBAL_CONTEXT));
    }

    @Test
    public void testLoadLib() {
        log.debug(">> Probando Compilación de librería en el núcleo del motor.");
        var options = EngineOptions.defaultOptions();
        EngineCore engineCore = new EngineCore(options);
        engineCore.loadLibrary("moment.min.js");

        assertTrue(engineCore.memberExists("moment"));
    }

    @Test
    public void testLoadAnonymousScript() {
        log.debug(">> Probando Compilación de script nativo en el núcleo del motor.");
        EngineCore engineCore = new EngineCore(EngineOptions.defaultOptions());
        var scriptName = "EVAL_TEST";
        engineCore.loadAnonymousScript("var "+scriptName+" = function(ret){return ret;}");
        assertTrue(engineCore.memberExists(scriptName));
    }

    @Test
    public void testExecuteScript() {
        log.debug(">> Probando Ejecución de script nativo en el núcleo del motor.");
        EngineCore engineCore = new EngineCore(EngineOptions.defaultOptions());
        var scriptName = "EVAL_TEST";
        engineCore.loadScript(scriptName, "var "+scriptName+" = function(ret){return ret;}");

        assertTrue(engineCore.memberExists(scriptName));
        assertTrue(engineCore.execute(scriptName,true).asBoolean());
    }
}
