/**
 * 
 */
package digital.amigo.jsengine.core;

import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * Regla Versionada
 * @author jorge.morando
 *
 */
class VersionedRule {

	@Getter
	private Map<Integer, Rule> versions = new HashMap<>();

	private VersionedRule(Integer version, Rule object) {
		getVersions().put(version,object);
	}

	private VersionedRule(Rule object) {
		getVersions().put(getNextUnpublishedVersion(),object);
	}
	
	public RuleVersion get(int version){
		var rule = versions.get(version);
		return new RuleVersion(rule, version);
	}
	
	public void add(Rule version){
		versions.put(getNextUnpublishedVersion(),version);
	}
	
	public void update(int versionNum, Rule version){
		if(versionNum == 0){
			versionNum = getNextUnpublishedVersion();
		}
		versions.put(versionNum,version);
	}
	
	public RuleVersion getLatest(){
		return get(latest());
	}
	
	public int latest(){
		Optional<Integer> higher = versions.keySet().stream().reduce(Integer::max);
		return higher.orElse(0);
	}
	
	public int getNextUnpublishedVersion(){
		return latest()+1;
	}
	
	public static  VersionedRule of(Rule object){
		return new VersionedRule(object);
	}

	public static  VersionedRule of(Rule object, Integer version){
		return new VersionedRule(version,object);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.JSON_STYLE)
				.append("versions", versions)
				.build();
	}
	
	
}
