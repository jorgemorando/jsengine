package digital.amigo.jsengine;

import com.fasterxml.jackson.core.JsonProcessingException;
import digital.amigo.jsengine.utils.JSONUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author jorge.morando
 *
 */
public class DefaultRuleEngineContext extends RuleEngineContext {
	
	private static final long serialVersionUID = -902353066044611122L;
	
	/* (non-Javadoc)
	 * @see digital.amigo.jsengine.Fact#toStringJson()
	 */
	@Override
	public String toStringJson() {
		try {
			return JSONUtils.getMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return new ReflectionToStringBuilder(this.toMap(),ToStringStyle.JSON_STYLE).build();
		}
	}
}
