package org.json.simple.parser;

import java.io.Serializable;

/**
 * A lexical unit produced by {@link Yylex} and consumed by {@link JSONParser}.
 * Structural token kinds carry no value; {@link #TYPE_VALUE} tokens carry the
 * decoded Java value in {@link #value}.
 */
public class Yytoken implements Serializable {
	private static final long serialVersionUID = 4341219912214205621L;
	
	public static final int TYPE_VALUE=0; 
	public static final int TYPE_LEFT_BRACE=1;
	public static final int TYPE_RIGHT_BRACE=2;
	public static final int TYPE_LEFT_SQUARE=3;
	public static final int TYPE_RIGHT_SQUARE=4;
	public static final int TYPE_COMMA=5;
	public static final int TYPE_COLON=6;
	public static final int TYPE_EOF=-1; 
	
	public int type=0;
	public Object value=null;
	
	/**
	 * Creates a token with a category and optional decoded payload.
	 *
	 * @param type token category
	 * @param value decoded payload for a value token
	 */
	public Yytoken(int type,Object value){
		this.type=type;
		this.value=value;
	}
	
	/** Returns a readable label for this token and its value, when present. */
	public String toString(){
		StringBuffer sb = new StringBuffer();
		switch(type){
		case TYPE_VALUE:
			sb.append("VALUE(").append(value).append(")");
			break;
		case TYPE_LEFT_BRACE:
			sb.append("LEFT BRACE({)");
			break;
		case TYPE_RIGHT_BRACE:
			sb.append("RIGHT BRACE(})");
			break;
		case TYPE_LEFT_SQUARE:
			sb.append("LEFT SQUARE([)");
			break;
		case TYPE_RIGHT_SQUARE:
			sb.append("RIGHT SQUARE(])");
			break;
		case TYPE_COMMA:
			sb.append("COMMA(,)");
			break;
		case TYPE_COLON:
			sb.append("COLON(:)");
			break;
		case TYPE_EOF:
			sb.append("END OF FILE");
			break;
		}
		return sb.toString();
	}
}
