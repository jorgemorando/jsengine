/**
 * 
 */
package digital.amigo.jsengine;


import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.text.StringEscapeUtils;

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
	private String rawCode;
	
	@Getter @Setter
	private boolean complex;

	@NonNull
	@Getter @Setter
	private RuleType type;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.JSON_STYLE)
				.append("name",name)
				.append("code", StringEscapeUtils.escapeJson(rawCode))
				.append("complex",complex)
				.append("type",type.toString())
				.build();
	}
}

