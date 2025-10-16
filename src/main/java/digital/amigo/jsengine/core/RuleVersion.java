package digital.amigo.jsengine.core;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public record RuleVersion(Rule rule, int version) {

    public String versionName(){
        return rule().getName() + "_v" + version;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("rule", rule)
                .append("versioned", version)
                .build();
    }

}
