package org.json.simple.parser;

import java.util.List;
import java.util.Map;

/**
 * Supplies the map and list implementations used to represent parsed JSON
 * objects and arrays. A parser falls back to {@code JSONObject} and
 * {@code JSONArray} when a factory is absent or returns {@code null}.
 */
public interface ContainerFactory {
	/**
	 * Creates a container for a JSON object and its name/value pairs.
	 *
	 * @return a map to populate with the object's entries, or {@code null} to use
	 *         the parser's default object container
	 */
	Map createObjectContainer();
	
	/**
	 * Creates a container for a JSON array and its ordered values.
	 *
	 * @return a list to populate with the array's values, or {@code null} to use
	 *         the parser's default array container
	 */
	List creatArrayContainer();
}
