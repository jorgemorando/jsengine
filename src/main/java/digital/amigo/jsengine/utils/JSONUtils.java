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
	
}
