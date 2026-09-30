package org.json.simple;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A mutable, ordered JSON array backed by {@link ArrayList}. Its serialization
 * methods encode contained values through {@link JSONValue}.
 */
public class JSONArray extends ArrayList implements JSONAware, JSONStreamAware {
	private static final long serialVersionUID = 3957988303675231981L;
	
	/** Creates an empty JSON array. */
	public JSONArray(){
		super();
	}
	
	/**
	 * Creates a JSON array containing the elements of a collection in iteration
	 * order.
	 *
	 * @param c source elements to copy
	 */
	public JSONArray(Collection c){
		super(c);
	}
	
	/**
	 * Writes a collection as a JSON array, encoding each element as a JSON value.
	 *
	 * @param collection elements to encode; {@code null} is written as JSON null
	 * @param out destination for the JSON text
	 * @throws IOException if writing to the destination fails
	 */
	public static void writeJSONString(Collection collection, Writer out) throws IOException{
		if(collection == null){
			out.write("null");
			return;
		}
		
		boolean first = true;
		Iterator iter=collection.iterator();
		
        out.write('[');
		while(iter.hasNext()){
            if(first)
                first = false;
            else
                out.write(',');
            
			Object value=iter.next();
			if(value == null){
				out.write("null");
				continue;
			}
			
			JSONValue.writeJSONString(value, out);
		}
		out.write(']');
	}
	
	/**
	 * Writes this array to a writer.
	 *
	 * @param out destination for the JSON text
	 * @throws IOException if writing to the destination fails
	 */
	public void writeJSONString(Writer out) throws IOException{
		writeJSONString(this, out);
	}
	
	/**
	 * Returns a collection's JSON array representation.
	 *
	 * @param collection elements to encode
	 * @return JSON text representing the collection
	 */
	public static String toJSONString(Collection collection){
		final StringWriter writer = new StringWriter();
		
		try {
			writeJSONString(collection, writer);
			return writer.toString();
		} catch(IOException e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * Writes a list as a JSON array; retained for compatibility with older callers.
	 *
	 * @param list elements to encode
	 * @param out destination for the JSON text
	 * @throws IOException if writing to the destination fails
	 * @deprecated use {@link #writeJSONString(Collection, Writer)}
	 */
	@Deprecated
	public static void writeJSONString(List list, Writer out) throws IOException{
		writeJSONString((Collection)list, out);
	}
	
	/**
	 * Returns a list's JSON array representation.
	 *
	 * @param list elements to encode
	 * @return JSON text representing the list
	 * @deprecated use {@link #toJSONString(Collection)}
	 */
	@Deprecated
	public static String toJSONString(List list){
		return toJSONString((Collection)list);
	}
	
	/**
	 * Writes a byte array as a JSON array of numeric values.
	 *
	 * @param array values to encode; {@code null} is written as JSON null
	 * @param out destination for the JSON text
	 * @throws IOException if writing to the destination fails
	 */
	public static void writeJSONString(byte[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[");
			out.write(String.valueOf(array[0]));
			
			for(int i = 1; i < array.length; i++){
				out.write(",");
				out.write(String.valueOf(array[i]));
			}
			
			out.write("]");
		}
	}
	
	/**
	 * Returns a byte array's JSON representation.
	 * @param array values to encode
	 * @return JSON array text
	 */
	public static String toJSONString(byte[] array){
		final StringWriter writer = new StringWriter();
		
		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Writes a short array as a JSON array of numeric values.
	 * @param array values to encode; {@code null} is written as JSON null
	 * @param out destination for the JSON text
	 * @throws IOException if writing to the destination fails
	 */
	public static void writeJSONString(short[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[");
			out.write(String.valueOf(array[0]));
			
			for(int i = 1; i < array.length; i++){
				out.write(",");
				out.write(String.valueOf(array[i]));
			}
			
			out.write("]");
		}
	}
	
	/**
	 * Returns a short array's JSON representation.
	 * @param array values to encode
	 * @return JSON array text
	 */
	public static String toJSONString(short[] array){
		final StringWriter writer = new StringWriter();
		
		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Writes an integer array as a JSON array of numeric values.
	 * @param array values to encode; {@code null} is written as JSON null
	 * @param out destination for the JSON text
	 * @throws IOException if writing to the destination fails
	 */
	public static void writeJSONString(int[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[");
			out.write(String.valueOf(array[0]));
			
			for(int i = 1; i < array.length; i++){
				out.write(",");
				out.write(String.valueOf(array[i]));
			}
			
			out.write("]");
		}
	}
	
	/**
	 * Returns an integer array's JSON representation.
	 * @param array values to encode
	 * @return JSON array text
	 */
	public static String toJSONString(int[] array){
		final StringWriter writer = new StringWriter();
		
		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Writes a long array as a JSON array of numeric values.
	 * @param array values to encode; {@code null} is written as JSON null
	 * @param out destination for the JSON text
	 * @throws IOException if writing to the destination fails
	 */
	public static void writeJSONString(long[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[");
			out.write(String.valueOf(array[0]));
			
			for(int i = 1; i < array.length; i++){
				out.write(",");
				out.write(String.valueOf(array[i]));
			}
			
			out.write("]");
		}
	}
	
	/**
	 * Returns a long array's JSON representation.
	 * @param array values to encode
	 * @return JSON array text
	 */
	public static String toJSONString(long[] array){
		final StringWriter writer = new StringWriter();
		
		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Writes a float array as a JSON array of numeric values.
	 * @param array values to encode; {@code null} is written as JSON null
	 * @param out destination for the JSON text
	 * @throws IOException if writing to the destination fails
	 */
	public static void writeJSONString(float[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[");
			out.write(String.valueOf(array[0]));
			
			for(int i = 1; i < array.length; i++){
				out.write(",");
				out.write(String.valueOf(array[i]));
			}
			
			out.write("]");
		}
	}
	
	/**
	 * Returns a float array's JSON representation.
	 * @param array values to encode
	 * @return JSON array text
	 */
	public static String toJSONString(float[] array){
		final StringWriter writer = new StringWriter();
		
		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Writes a double array as a JSON array of numeric values.
	 * @param array values to encode; {@code null} is written as JSON null
	 * @param out destination for the JSON text
	 * @throws IOException if writing to the destination fails
	 */
	public static void writeJSONString(double[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[");
			out.write(String.valueOf(array[0]));
			
			for(int i = 1; i < array.length; i++){
				out.write(",");
				out.write(String.valueOf(array[i]));
			}
			
			out.write("]");
		}
	}
	
	/**
	 * Returns a double array's JSON representation.
	 * @param array values to encode
	 * @return JSON array text
	 */
	public static String toJSONString(double[] array){
		final StringWriter writer = new StringWriter();
		
		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Writes a boolean array as a JSON array of boolean values.
	 * @param array values to encode; {@code null} is written as JSON null
	 * @param out destination for the JSON text
	 * @throws IOException if writing to the destination fails
	 */
	public static void writeJSONString(boolean[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[");
			out.write(String.valueOf(array[0]));
			
			for(int i = 1; i < array.length; i++){
				out.write(",");
				out.write(String.valueOf(array[i]));
			}
			
			out.write("]");
		}
	}
	
	/**
	 * Returns a boolean array's JSON representation.
	 * @param array values to encode
	 * @return JSON array text
	 */
	public static String toJSONString(boolean[] array){
		final StringWriter writer = new StringWriter();
		
		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Writes a character array as a JSON array of one-character strings.
	 * @param array characters to encode; {@code null} is written as JSON null
	 * @param out destination for the JSON text
	 * @throws IOException if writing to the destination fails
	 */
	public static void writeJSONString(char[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[\"");
			out.write(String.valueOf(array[0]));
			
			for(int i = 1; i < array.length; i++){
				out.write("\",\"");
				out.write(String.valueOf(array[i]));
			}
			
			out.write("\"]");
		}
	}
	
	/**
	 * Returns a character array's JSON representation.
	 * @param array characters to encode
	 * @return JSON array text
	 */
	public static String toJSONString(char[] array){
		final StringWriter writer = new StringWriter();
		
		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Writes an object array as a JSON array, encoding each element through
	 * {@link JSONValue}.
	 * @param array values to encode; {@code null} is written as JSON null
	 * @param out destination for the JSON text
	 * @throws IOException if writing to the destination fails
	 */
	public static void writeJSONString(Object[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[");
			JSONValue.writeJSONString(array[0], out);
			
			for(int i = 1; i < array.length; i++){
				out.write(",");
				JSONValue.writeJSONString(array[i], out);
			}
			
			out.write("]");
		}
	}
	
	/**
	 * Returns an object array's JSON representation.
	 * @param array values to encode
	 * @return JSON array text
	 */
	public static String toJSONString(Object[] array){
		final StringWriter writer = new StringWriter();
		
		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Returns this array's JSON representation.
	 * @return JSON text representing this array
	 */
	public String toJSONString(){
		return toJSONString(this);
	}

	/**
	 * Returns this array's JSON representation.
	 * @return JSON text representing this array
	 */
	public String toString() {
		return toJSONString();
	}
}
