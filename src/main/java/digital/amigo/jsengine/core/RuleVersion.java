package digital.amigo.jsengine.core;

import digital.amigo.jsengine.rule.Rule;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

public record RuleVersion(Rule rule, int version) {

    public RuleVersion {
        Objects.requireNonNull(rule, "Rule cannot be null");
    }

    public String versionName(){
        return name() + "_v" + version;
    }

    public String humanReadableName(){
        return rule.getName();
    }

    public String name(){
        return rule().getName().replaceAll("\\s+", "_");
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("rule", rule)
                .append("versioned", version)
                .build();
    }

}
