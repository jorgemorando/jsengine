package digital.amigo.jsengine;

import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;

import digital.amigo.jsengine.exception.RuleEngineException;
import digital.amigo.jsengine.utils.JSONUtils;

/**
 * @author jorge.morando
 *
 */
public class DefaultFact extends HashMap<String,Object> implements Fact {
	
	private static final long serialVersionUID = -902353066044611122L;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toStringJson();
	}
	
	/* (non-Javadoc)
	 * @see digital.amigo.jsengine.Fact#toStringJson()
	 */
	@Override
	public String toStringJson() {
		try {
			return JSONUtils.getMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new RuleEngineException(e);
		}
	}
}
