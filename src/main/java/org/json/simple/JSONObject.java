package org.json.simple;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A mutable JSON object backed by {@link HashMap}. It stores member names and
 * values and provides both string-based and streaming JSON serialization.
 */
public class JSONObject extends HashMap implements Map, JSONAware, JSONStreamAware{
	
	private static final long serialVersionUID = -503443796854799292L;
	
	
	/** Creates an empty JSON object. */
	public JSONObject() {
		super();
	}

	/**
	 * Creates a JSON object containing the entries from the supplied map.
	 *
	 * @param map entries to copy into this object
	 */
	public JSONObject(Map map) {
		super(map);
	}


	/**
	 * Writes a map as a JSON object, converting each key to a JSON member name
	 * and each value through {@link JSONValue}.
	 *
	 * @param map entries to encode; {@code null} is written as JSON null
	 * @param out destination for the JSON text
	 * @throws IOException if writing to the destination fails
	 */
	public static void writeJSONString(Map map, Writer out) throws IOException {
		if(map == null){
			out.write("null");
			return;
		}
		
		boolean first = true;
		Iterator iter=map.entrySet().iterator();
		
        out.write('{');
		while(iter.hasNext()){
            if(first)
                first = false;
            else
                out.write(',');
			Map.Entry entry=(Map.Entry)iter.next();
            out.write('\"');
            out.write(escape(String.valueOf(entry.getKey())));
            out.write('\"');
            out.write(':');
			JSONValue.writeJSONString(entry.getValue(), out);
		}
		out.write('}');
	}

	/**
	 * Writes this object's JSON representation to a writer.
	 *
	 * @param out destination for the JSON text
	 * @throws IOException if writing to the destination fails
	 */
	public void writeJSONString(Writer out) throws IOException{
		writeJSONString(this, out);
	}
	
	/**
	 * Returns a map's JSON object representation.
	 *
	 * @param map entries to encode
	 * @return JSON text representing the map
	 */
	public static String toJSONString(Map map){
		final StringWriter writer = new StringWriter();
		
		try {
			writeJSONString(map, writer);
			return writer.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Returns this object's JSON representation.
	 * @return JSON text representing this object
	 */
	public String toJSONString(){
		return toJSONString(this);
	}
	
	/**
	 * Returns this object's JSON representation.
	 * @return JSON text representing this object
	 */
	public String toString(){
		return toJSONString();
	}

	/**
	 * Returns one JSON object member, consisting of an escaped key and encoded
	 * value, without surrounding object braces.
	 *
	 * @param key member name
	 * @param value member value
	 * @return JSON text for the member
	 */
	public static String toString(String key,Object value){
        StringBuffer sb = new StringBuffer();
        sb.append('\"');
        if(key == null)
            sb.append("null");
        else
            JSONValue.escape(key, sb);
		sb.append('\"').append(':');
		
		sb.append(JSONValue.toJSONString(value));
		
		return sb.toString();
	}
	
	/**
	 * Escapes a string for use as a JSON string value without adding quotes.
	 *
	 * @param s string to escape
	 * @return escaped string, or {@code null} if {@code s} is {@code null}
	 */
	public static String escape(String s){
		return JSONValue.escape(s);
	}
}
