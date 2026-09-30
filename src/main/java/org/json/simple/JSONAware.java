package org.json.simple;

/**
 * Provides a JSON text representation for an object that can encode itself.
 * Implementations are used by {@link JSONValue} when serializing custom values.
 */
public interface JSONAware {
	/**
	 * Returns this object's JSON representation.
	 *
	 * @return JSON text representing this object
	 */
	String toJSONString();
}
