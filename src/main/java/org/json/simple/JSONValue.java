package org.json.simple;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



/**
 * Static entry points for parsing JSON and converting Java values to JSON text.
 * Parsing is delegated to {@link JSONParser}; serialization dispatches to the
 * library's object, array, and custom-value interfaces.
 */
public class JSONValue {
	/**
	 * Parses a JSON document, returning {@code null} when parsing fails.
	 * Prefer {@link #parseWithException(Reader)} when the failure details are
	 * needed.
	 *
	 * @param in source containing one JSON document
	 * @return the parsed value, or {@code null} if parsing fails
	 */
	@Deprecated
	public static Object parse(Reader in){
		try{
			JSONParser parser=new JSONParser();
			return parser.parse(in);
		}
		catch(Exception e){
			return null;
		}
	}
	
	/**
	 * Parses a JSON string, returning {@code null} when parsing fails.
	 *
	 * @param s text containing one JSON document
	 * @return the parsed value, or {@code null} if parsing fails
	 */
	@Deprecated
	public static Object parse(String s){
		StringReader in=new StringReader(s);
		return parse(in);
	}
	
	/**
	 * Parses one JSON document from a reader and reports parse failures.
	 *
	 * @param in source containing the document
	 * @return the parsed JSON value, represented by standard Java values and
	 *         {@code JSONObject}/{@code JSONArray} containers
	 * @throws IOException if reading the source fails
	 * @throws ParseException if the source is not a valid JSON document
	 */
	public static Object parseWithException(Reader in) throws IOException, ParseException{
		JSONParser parser=new JSONParser();
		return parser.parse(in);
	}
	
	/**
	 * Parses one JSON document from a string and reports parse failures.
	 *
	 * @param s text containing the document
	 * @return the parsed JSON value
	 * @throws ParseException if the string is not a valid JSON document
	 */
	public static Object parseWithException(String s) throws ParseException{
		JSONParser parser=new JSONParser();
		return parser.parse(s);
	}
	
	/**
	 * Writes a Java value as JSON to a writer. Maps, collections, arrays, and
	 * values implementing the JSON-aware interfaces are handled according to
	 * their respective representations.
	 *
	 * @param value value to encode
	 * @param out destination for the JSON text
	 * @throws IOException if writing to the destination fails
	 */
	public static void writeJSONString(Object value, Writer out) throws IOException {
		if(value == null){
			out.write("null");
			return;
		}
		
		if(value instanceof String){		
            out.write('\"');
			out.write(escape((String)value));
            out.write('\"');
			return;
		}
		
		if(value instanceof Double){
			if(((Double)value).isInfinite() || ((Double)value).isNaN())
				out.write("null");
			else
				out.write(value.toString());
			return;
		}
		
		if(value instanceof Float){
			if(((Float)value).isInfinite() || ((Float)value).isNaN())
				out.write("null");
			else
				out.write(value.toString());
			return;
		}		
		
		if(value instanceof Number){
			out.write(value.toString());
			return;
		}
		
		if(value instanceof Boolean){
			out.write(value.toString());
			return;
		}
		
		if((value instanceof JSONStreamAware)){
			((JSONStreamAware)value).writeJSONString(out);
			return;
		}
		
		if((value instanceof JSONAware)){
			out.write(((JSONAware)value).toJSONString());
			return;
		}
		
		if(value instanceof Map){
			JSONObject.writeJSONString((Map)value, out);
			return;
		}
		
		if(value instanceof Collection){
			JSONArray.writeJSONString((Collection)value, out);
            return;
		}
		
		if(value instanceof byte[]){
			JSONArray.writeJSONString((byte[])value, out);
			return;
		}
		
		if(value instanceof short[]){
			JSONArray.writeJSONString((short[])value, out);
			return;
		}
		
		if(value instanceof int[]){
			JSONArray.writeJSONString((int[])value, out);
			return;
		}
		
		if(value instanceof long[]){
			JSONArray.writeJSONString((long[])value, out);
			return;
		}
		
		if(value instanceof float[]){
			JSONArray.writeJSONString((float[])value, out);
			return;
		}
		
		if(value instanceof double[]){
			JSONArray.writeJSONString((double[])value, out);
			return;
		}
		
		if(value instanceof boolean[]){
			JSONArray.writeJSONString((boolean[])value, out);
			return;
		}
		
		if(value instanceof char[]){
			JSONArray.writeJSONString((char[])value, out);
			return;
		}
		
		if(value instanceof Object[]){
			JSONArray.writeJSONString((Object[])value, out);
			return;
		}
		
		out.write(value.toString());
	}

	/**
	 * Returns the JSON representation of a Java value.
	 *
	 * @param value value to encode
	 * @return JSON text representing the value
	 */
	public static String toJSONString(Object value){
		final StringWriter writer = new StringWriter();
		
		try{
			writeJSONString(value, writer);
			return writer.toString();
		} catch(IOException e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * Escapes characters in a string for use inside a JSON string literal.
	 * The returned text does not include surrounding quotation marks.
	 *
	 * @param s string to escape
	 * @return escaped string, or {@code null} when the input is {@code null}
	 */
	public static String escape(String s){
		if(s==null)
			return null;
        StringBuffer sb = new StringBuffer();
        escape(s, sb);
        return sb.toString();
    }

	/**
	 * Appends the escaped form of a string to an existing buffer.
	 *
	 * @param s source string to escape
	 * @param sb destination buffer receiving escaped characters
	 */
    static void escape(String s, StringBuffer sb) {
    	final int len = s.length();
		for(int i=0;i<len;i++){
			char ch=s.charAt(i);
			switch(ch){
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '/':
				sb.append("\\/");
				break;
			default:
				if((ch>='\u0000' && ch<='\u001F') || (ch>='\u007F' && ch<='\u009F') || (ch>='\u2000' && ch<='\u20FF')){
					String ss=Integer.toHexString(ch);
					sb.append("\\u");
					for(int k=0;k<4-ss.length();k++){
						sb.append('0');
					}
					sb.append(ss.toUpperCase());
				}
				else{
					sb.append(ch);
				}
			}
		} 
	}

}
