package digital.amigo.jsengine.core;

import digital.amigo.jsengine.exception.RuleEngineException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

class EngineCore {

    private final Logger log = LoggerFactory.getLogger(EngineCore.class);

    private ScriptEngine engine;

    private EngineOptions options;

    EngineCore(EngineOptions options) {
        this.options = options;
        bootstrap();
    }

    /*---------------------PRIVATE----------------------*/
    private void bootstrap() {
        log.info("Inicializando motor de JavaScript");
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("nashorn");

        if (options.loadLibs()) {
            log.debug("Cargando librería MOMENT ");
            loadLibrary("moment.min.js");
            log.info("Inicializado exitoso");
        }
    }

    Object loadScript(String scriptName, String script) {
        log.debug("Compilando código JavaScript de regla " + scriptName);
        try {
			return engine.eval(script);
		} catch (ScriptException e) {
			String msg = "Error loading script for '" + scriptName + "' ( " + script + " )";
			log.error(msg);
            throw new RuleEngineException(msg, e);
        }
    }

    private void loadLibrary(String libName) {
        Instant start = Instant.now();
        InputStream libIS = EngineCore.class.getResourceAsStream("/" + libName);
        String libStr;
        if (libIS == null) {
            libIS = EngineCore.class.getResourceAsStream("/resources/" + libName);
        }
        try {
            libStr = IOUtils.toString(libIS, StandardCharsets.UTF_8);
            libIS.close();
            loadScript(libName, libStr);
        } catch (IOException e) {
            throw new RuleEngineException("Library (" + libName + ") not found in classpath", e);
        }
        log.debug("Library loaded in {} milliseconds", Duration.between(start, Instant.now()).toMillis());
    }

}
