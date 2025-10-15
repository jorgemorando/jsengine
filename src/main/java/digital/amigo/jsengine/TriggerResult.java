package digital.amigo.jsengine;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.Data;

@Data
public class TriggerResult {

	private boolean success = false;
	
	private boolean fired = false;
	
	private Rule rule;

	private int version;
	
	private Fact fact;
	
	private RuleEngineContext ruleEngineContext;
	
	private List<String> messages = new ArrayList<String>(); 

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.JSON_STYLE)
				.append("success", success)
				.append("fired", fired)
				.append("version", version)
				.append("rule", rule)
				.append("fact", fact)
				.append("context", ruleEngineContext)
				.append("message", messages)
				.build();
	}
}
