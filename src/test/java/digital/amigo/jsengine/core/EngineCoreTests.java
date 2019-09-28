package digital.amigo.jsengine.core;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class EngineCoreTests {

    private final Logger log = LoggerFactory.getLogger(EngineCoreTests.class);

    @Test
    public void testInstance() {
        log.debug(">> Probando Instanciación del núcleo del motor.");
        EngineCore instance = new EngineCore(new EngineOptions());
        assertNotNull(instance);
    }

    @Test
    public void testCompilation() {
        log.debug(">> Probando Compilación de script en el núcleo del motor.");
        EngineCore engineCore = new EngineCore(new EngineOptions());

        ScriptObjectMirror som = (ScriptObjectMirror) engineCore.loadScript("EVAL_TEST", "function(ret){return ret;}");
        assertNotNull(som);
        assertTrue((Boolean) som.call(null, true));
        assertFalse((Boolean) som.call(null, false));
    }
}
