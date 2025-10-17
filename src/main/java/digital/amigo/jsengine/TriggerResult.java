package digital.amigo.jsengine;

import digital.amigo.jsengine.core.Fact;
import digital.amigo.jsengine.core.RuleEngineContext;
import digital.amigo.jsengine.core.RuleVersion;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

@Data
public class TriggerResult {


	private boolean triggered = false;

	private boolean evaluated = false;

	private RuleVersion ruleVersion;

	private Fact fact;
	
	private RuleEngineContext ruleEngineContext;
	
	private List<String> messages = new ArrayList<>();

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.JSON_STYLE)
				.append("triggered", triggered)
				.append("evaluated", evaluated)
				.append("rule", ruleVersion)
				.append("fact", fact)
				.append("context", ruleEngineContext)
				.append("message", messages)
				.build();
	}
}
