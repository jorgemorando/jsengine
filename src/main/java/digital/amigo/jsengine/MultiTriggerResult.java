package digital.amigo.jsengine;

import digital.amigo.jsengine.core.Fact;
import digital.amigo.jsengine.core.RuleEngineContext;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public final class MultiTriggerResult {

    private final List<TriggerResult> results;

    @Getter
    private final Fact fact;

    @Getter
    private final RuleEngineContext context;

    public MultiTriggerResult(Fact fact, RuleEngineContext context, List<TriggerResult> results) {
        this.results = results;
        this.fact = fact;
        this.context = context;
    }

    public int getResultCount() {
        return results.size();
    }

    public List<TriggerResult> getSuccessful(){
        return results.stream().filter(TriggerResult::isTriggered).toList();
    }

    public List<TriggerResult> getFailed(){
        return results.stream().filter(r -> !r.isTriggered()).toList();
    }



    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("results", results)
                .append("resultCount", results.size())
                .append("fact", fact)
                .append("context", context)
                .build();
    }

}
