/**
 * 
 */
package digital.amigo.jsengine;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Regla base
 * @author jorge.morando
 *
 */
@RequiredArgsConstructor
public class Rule {
	
	@NonNull
	@Getter @Setter
	private String name;
	
	@NonNull
	@Getter @Setter
	private String code;
	
	@Getter @Setter
	private boolean complex;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.JSON_STYLE)
				.append("name",name)
				.append("code",StringEscapeUtils.escapeJson(code))
				.append("complex",complex)
				.build();
	}
}
