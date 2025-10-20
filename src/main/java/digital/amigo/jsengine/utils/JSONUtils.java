/**
 * 
 */
package digital.amigo.jsengine.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * @author jorge.morando
 *
 */
public class JSONUtils {

	private static final ObjectMapper mapper = new ObjectMapper();
	
	private static final XmlMapper xmlMapper = new XmlMapper();
	
	public static ObjectMapper getMapper(){
		return mapper;
	}
	
	public static XmlMapper getXmlMapper(){
		return xmlMapper;
	}

	public static String toJsValue(Object value) {
		if (value == null) return "null";
		String s = String.valueOf(value);
		if (value instanceof Number || value instanceof Boolean) {
			return s;
		}
		// treat as string: escape backslashes and double quotes
		s = s.replace("\\", "\\\\").replace("\"", "\\\"");
		return "\"" + s + "\"";
	}
	
}
