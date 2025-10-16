package digital.amigo.jsengine.core;

import digital.amigo.jsengine.Fact;
import digital.amigo.jsengine.RuleEngineContext;
import lombok.Getter;
import org.graalvm.polyglot.Value;

@Getter
public final class CompiledRule {

    private final RuleVersion rule;
    private final Value compiledRule;

    CompiledRule(RuleVersion rule, Value compiledRule) {
        this.rule = rule;
        this.compiledRule = compiledRule;
    }

    boolean isExecutable(){
        return compiledRule.canExecute();
    }


    public Boolean execute(Fact fact, RuleEngineContext ctx) {
        //FIXME: Need to implement decisions correctly
        //TODO: Nice to have rules read values (providers) and rules alter the rules execution context (providers)
        return compiledRule.execute(fact, ctx).asBoolean();
    }
}
