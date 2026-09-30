package org.json.simple;

import java.io.IOException;
import java.io.Writer;

/**
 * Allows an object to write its JSON representation directly to a character
 * stream, avoiding an intermediate JSON string.
 */
public interface JSONStreamAware {
	/**
	 * Writes this object's JSON representation to the supplied writer.
	 *
	 * @param out destination for the JSON text
	 * @throws IOException if writing to the destination fails
	 */
	void writeJSONString(Writer out) throws IOException;
}
