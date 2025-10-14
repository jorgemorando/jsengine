package digital.amigo.jsengine.core;

import digital.amigo.jsengine.exception.RuleEngineException;
import org.apache.commons.io.IOUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

class EngineCore {

    private final Logger log = LoggerFactory.getLogger(EngineCore.class);

    private Context engine;

    private final EngineOptions options;

    EngineCore(EngineOptions options) {
        this.options = options;
        bootstrap();
    }

    /*---------------------PRIVATE----------------------*/
    private void bootstrap() {
        log.info("Inicializando motor de reglas");

        engine = Context.newBuilder("js")
                .allowAllAccess(true)
                .build();

        initializeContext();

        if (options.loadLibs()) {
            loadLibrary("moment.min.js");
        }
        log.info("Inicializado exitoso");
    }

    private void initializeContext() {
        log.info("Inicializando contexto global del motor de reglas");

        Value bindings = engine.getBindings("js");
        Value contextObject = engine.eval("js", "({})");
        bindings.putMember("context", contextObject);
    }

    void loadLibrary(String libName) {
        log.debug("Compilando librería " + libName);

        Instant start = Instant.now();

        URL lib = EngineCore.class.getResource("/" + libName);
        String libStr;
        if (Objects.isNull(lib)) {
            lib = EngineCore.class.getResource("/resources/" + libName);
        }
        try {
            libStr = IOUtils.toString(lib, StandardCharsets.UTF_8);
            loadScript(libName, libStr);
        } catch (IOException e) {
            throw new RuleEngineException("Library (" + libName + ") not found in classpath", e);
        }
        log.debug("Library loaded in {} milliseconds", Duration.between(start, Instant.now()).toMillis());
    }


    Object loadScript(String scriptName, String script) {
        log.debug("Compilando script " + scriptName);
        try {
            var source = Source.newBuilder("js",  new StringReader(script), scriptName).build();
            return engine.eval(source);
        } catch (IOException e) {
            String msg = "Error loading script for '" + scriptName + "' ( " + script + " )";
            log.error(msg);
            throw new RuleEngineException(msg, e);
        }
    }

    boolean hasMember(String memberName){
        return engine.getBindings("js").hasMember(memberName);
    }

    Value execute(String memberName, Object ... args){
        return engine.getBindings("js").getMember(memberName).execute(args);
    }
}
