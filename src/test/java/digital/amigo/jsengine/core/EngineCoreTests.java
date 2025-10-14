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
        EngineCore engineCore = new EngineCore(new EngineOptions(false));
        assertNotNull(engineCore);
        assertTrue(engineCore.hasMember("context"));
    }

    @Test
    public void testLoadLib() {
        log.debug(">> Probando Compilación de librería en el núcleo del motor.");
        EngineCore engineCore = new EngineCore(new EngineOptions(false));
        engineCore.loadLibrary("moment.min.js");

        assertTrue(engineCore.hasMember("moment"));
    }

    @Test
    public void testLoadScript() {
        log.debug(">> Probando Compilación de script nativo en el núcleo del motor.");
        EngineCore engineCore = new EngineCore(new EngineOptions(false));
        var scriptName = "EVAL_TEST";
        engineCore.loadScript("var "+scriptName+" = function(ret){return ret;}");
        assertTrue(engineCore.hasMember(scriptName));
    }

    @Test
    public void testExecuteScript() {
        log.debug(">> Probando Ejecución de script nativo en el núcleo del motor.");
        EngineCore engineCore = new EngineCore(new EngineOptions(false));
        var scriptName = "EVAL_TEST";
        engineCore.loadScript(scriptName, "var "+scriptName+" = function(ret){return ret;}");

        assertTrue(engineCore.hasMember(scriptName));
        assertTrue(engineCore.execute(scriptName,true).asBoolean());
    }
}
