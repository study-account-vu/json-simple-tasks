package org.json.simple.parser;

import java.io.IOException;

/**
 * Receives events while {@link JSONParser} reads a JSON document. Returning
 * {@code false} from a structural or value callback pauses event delivery so
 * parsing can later resume with the same parser and handler state.
 */
public interface ContentHandler {
	/**
	 * Signals the beginning of a document.
	 * @throws ParseException if the handler rejects the event
	 * @throws IOException if handler processing fails
	 */
	void startJSON() throws ParseException, IOException;
	
	/**
	 * Signals that the complete document has been consumed.
	 * @throws ParseException if the handler rejects the event
	 * @throws IOException if handler processing fails
	 */
	void endJSON() throws ParseException, IOException;
	
	/**
	 * Signals the beginning of a JSON object.
	 *
	 * @return {@code true} to continue parsing, or {@code false} to pause
	 * @throws ParseException if the handler rejects the event
	 * @throws IOException if handler processing fails
	 */
	boolean startObject() throws ParseException, IOException;
	
	/**
	 * Signals the end of the current JSON object.
	 *
	 * @return {@code true} to continue parsing, or {@code false} to pause
	 * @throws ParseException if the handler rejects the event
	 * @throws IOException if handler processing fails
	 */
	boolean endObject() throws ParseException, IOException;
	
	/**
	 * Signals the key for the next entry in the current object.
	 *
	 * @param key name of the object entry
	 * @return {@code true} to continue parsing, or {@code false} to pause
	 * @throws ParseException if the handler rejects the event
	 * @throws IOException if handler processing fails
	 */
	boolean startObjectEntry(String key) throws ParseException, IOException;
	
	/**
	 * Signals that the current object entry's value has been delivered.
	 *
	 * @return {@code true} to continue parsing, or {@code false} to pause
	 * @throws ParseException if the handler rejects the event
	 * @throws IOException if handler processing fails
	 */
	boolean endObjectEntry() throws ParseException, IOException;
	
	/**
	 * Signals the beginning of a JSON array.
	 *
	 * @return {@code true} to continue parsing, or {@code false} to pause
	 * @throws ParseException if the handler rejects the event
	 * @throws IOException if handler processing fails
	 */
	boolean startArray() throws ParseException, IOException;
	
	/**
	 * Signals the end of the current JSON array.
	 *
	 * @return {@code true} to continue parsing, or {@code false} to pause
	 * @throws ParseException if the handler rejects the event
	 * @throws IOException if handler processing fails
	 */
	boolean endArray() throws ParseException, IOException;
	
	/**
	 * Delivers a primitive JSON value such as a string, number, boolean, or null.
	 *
	 * @param value decoded primitive value
	 * @return {@code true} to continue parsing, or {@code false} to pause
	 * @throws ParseException if the handler rejects the event
	 * @throws IOException if handler processing fails
	 */
	boolean primitive(Object value) throws ParseException, IOException;		
}
