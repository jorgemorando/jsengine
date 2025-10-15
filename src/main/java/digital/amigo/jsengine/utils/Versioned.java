/**
 * 
 */
package digital.amigo.jsengine.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.Getter;

/**
 * Objeto versionador de entidades del motor JavaScript
 * @author jorge.morando
 *
 */
public class Versioned<T> {

	@Getter
	private Map<Integer,T> versions = new HashMap<>();
	
	private Versioned(Integer version,T object) {
		getVersions().put(version,object);
	}
	
	private Versioned(T object) {
		getVersions().put(getNextUnpublishedVersion(),object);
	}
	
	public T get(int version){
		return versions.get(version);
	}
	
	public void add(T version){
		versions.put(getNextUnpublishedVersion(),version);
	}
	
	public void update(int versionNum,T version){
		if(versionNum == 0){
			versionNum = getNextUnpublishedVersion();
		}
		versions.put(versionNum,version);
	}
	
	public T getLatest(){
		return versions.get(latest());
	}
	
	public int latest(){
		Optional<Integer> higher = versions.keySet().stream().reduce(Integer::max);
		return higher.orElse(0);
	}
	
	public int getNextUnpublishedVersion(){
		return latest()+1;
	}
	
	public static String name(String name, int version){
		return name+"_v"+version;
	}
	
	public static Optional<Integer> cleanVersionOf(String name){
		Integer versionNum = null;
		if(Objects.nonNull(name) && name.contains("_v")){
			versionNum = Integer.parseInt(name.split("_v")[1]);
		}
		return Optional.ofNullable(versionNum);
	}
	
	public static Optional<Integer> cleanNameOf(String name){
		Integer versionNum = null;
		if(Objects.nonNull(name) && name.contains("_v")){
			versionNum = Integer.parseInt(name.split("_v")[0]);
		}
		return Optional.ofNullable(versionNum);
	}
	
	public static <T> Versioned<T> of(T object){
		return new Versioned<T>(object);
	}

	public static <T> Versioned<T> of(T object,Integer version){
		return new Versioned<T>(version,object);
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
