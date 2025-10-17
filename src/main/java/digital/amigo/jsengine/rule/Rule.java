/**
 * 
 */
package digital.amigo.jsengine.rule;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.text.StringEscapeUtils;

import java.util.Objects;

/**
 * Regla base
 * @author jorge.morando
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Rule {
	
	@NonNull
	@Getter @Setter
	private String name;

	@NonNull
	@Getter @Setter
	private Condition condition;

	@NonNull
	@Getter @Setter
	private String result;

	@Getter @Setter
	private boolean complex;

//	@NonNull
//	@Getter @Setter
//	private RuleType type;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.JSON_STYLE)
				.append("name",name)
				.append("conditions",condition)
				.append("code", StringEscapeUtils.escapeJson(result))
				.append("complex",complex)
//				.append("type",type.toString())
				.build();
	}


	/**
	 * Crea una instancia de Builder para Rule.
	 * @param name el nombre que tendrá la regla
	 * @return
	 */
	public static RuleBuilder createWithName(String name){
		return new RuleBuilder(name);
	}

	public static class RuleBuilder {

		private final String name;
		private Condition condition;
		private String result;

		private RuleBuilder(String name) {
			this.name = name;
		}

		public RuleBuilder when(Condition condition) {
			this.condition = condition;
			return this;
		}

		public RuleBuilder then(String result) {
			this.result = result;
			return this;
		}

		public Rule build() {
			if(Objects.isNull(name) || name.isBlank())
				throw new IllegalArgumentException("Rule name cannot be blank");

			if(Objects.isNull(condition))
				throw new IllegalArgumentException("Rule condition cannot be null");

			if(Objects.isNull(result))
				throw new IllegalArgumentException("Rule result cannot be null");

			return new Rule(name, condition, result);
		}
	}
}



