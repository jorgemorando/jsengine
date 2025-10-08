package digital.amigo.jsengine;

import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import digital.amigo.jsengine.utils.JSONUtils;

/**
 * @author jorge.morando
 *
 */
public class DefaultContext extends Context {
	
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
	
	/**
	 * Convierte esta instancia en un Mapa de valores
	 * @return
	 */
	public Map<String,Object> toMap(){
		return (Map<String,Object>)this;
	}
	
	
}
