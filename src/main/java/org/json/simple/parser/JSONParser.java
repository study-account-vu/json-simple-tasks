package org.json.simple.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;



/**
 * Parses JSON text into Java containers or emits events to a {@link ContentHandler}.
 * The parser combines tokens from {@link Yylex} with a state stack to recognize
 * nested objects and arrays; an instance retains lexer and callback state when
 * event parsing is paused for later resumption.
 */
public class JSONParser {
	/** Initial state before a document value has been read. */
	public static final int S_INIT=0;
	/** State after the top-level value is complete, awaiting end of input. */
	public static final int S_IN_FINISHED_VALUE=1; 
	/** State while reading members of an object. */
	public static final int S_IN_OBJECT=2;
	/** State while reading values in an array. */
	public static final int S_IN_ARRAY=3;
	/** State after an object member name, awaiting its separator or value. */
	public static final int S_PASSED_PAIR_KEY=4;
	/** State used to complete a streamed object entry after a nested value. */
	public static final int S_IN_PAIR_VALUE=5;
	/** State after a streamed document has been fully delivered. */
	public static final int S_END=6;
	/** Error state entered after an invalid token or parsing failure. */
	public static final int S_IN_ERROR=-1;
	
	/** Saved callback-parser nesting states used when event delivery is resumed. */
	private LinkedList handlerStatusStack;
	/** Scanner shared by the parse operations of this parser instance. */
	private Yylex lexer = new Yylex((Reader)null);
	/** Most recently scanned token, retained across paused event parsing. */
	private Yytoken token = null;
	/** Current state of the parser's state machine. */
	private int status = S_INIT;
	
	/** Returns the most recent nested state, or {@code -1} for an empty stack. */
	private int peekStatus(LinkedList statusStack){
		if(statusStack.size()==0)
			return -1;
		Integer status=(Integer)statusStack.getFirst();
		return status.intValue();
	}
	
	/** Resets the parser state so a new input can be parsed. */
    public void reset(){
        token = null;
        status = S_INIT;
        handlerStatusStack = null;
    }
    
	/**
	 * Replaces the scanner input and resets parser state.
	 *
	 * @param in reader containing the next JSON document
	 */
	public void reset(Reader in){
		lexer.yyreset(in);
		reset();
	}
	
	/**
	 * Returns the current input position maintained by the lexer.
	 *
	 * @return character offset in the current input
	 */
	public int getPosition(){
		return lexer.getPosition();
	}
	
	/**
	 * Parses a JSON string into Java values and default JSON containers.
	 *
	 * @param s text containing one JSON document
	 * @return parsed value
	 * @throws ParseException if the input cannot be parsed
	 */
	public Object parse(String s) throws ParseException{
		return parse(s, (ContainerFactory)null);
	}
	
	/**
	 * Parses a JSON string using optional caller-supplied object and array
	 * containers.
	 *
	 * @param s text containing one JSON document
	 * @param containerFactory factory for parsed object and array containers, or
	 *                         {@code null} for the standard containers
	 * @return parsed value
	 * @throws ParseException if the input cannot be parsed
	 */
	public Object parse(String s, ContainerFactory containerFactory) throws ParseException{
		StringReader in=new StringReader(s);
		try{
			return parse(in, containerFactory);
		}
		catch(IOException ie){
			throw new ParseException(-1, ParseException.ERROR_UNEXPECTED_EXCEPTION, ie);
		}
	}
	
	/**
	 * Parses a reader into Java values and default JSON containers.
	 *
	 * @param in reader containing one JSON document
	 * @return parsed value
	 * @throws IOException if reading the source fails
	 * @throws ParseException if the input cannot be parsed
	 */
	public Object parse(Reader in) throws IOException, ParseException{
		return parse(in, (ContainerFactory)null);
	}
	
	/**
	 * Parses a reader using optional caller-supplied object and array containers.
	 *
	 * @param in reader containing one JSON document
	 * @param containerFactory factory for parsed object and array containers, or
	 *                         {@code null} for the standard containers
	 * @return parsed value
	 * @throws IOException if reading the source fails
	 * @throws ParseException if the input cannot be parsed
	 */
	public Object parse(Reader in, ContainerFactory containerFactory) throws IOException, ParseException{
		reset(in);
		// Keep the current container and parser state aligned at each nesting level.
		LinkedList statusStack = new LinkedList();
		LinkedList valueStack = new LinkedList();
		
		try{
			do{
				nextToken();
				switch(status){
				case S_INIT:
					switch(token.type){
					case Yytoken.TYPE_VALUE:
						status=S_IN_FINISHED_VALUE;
						statusStack.addFirst(Integer.valueOf(status));
						valueStack.addFirst(token.value);
						break;
					case Yytoken.TYPE_LEFT_BRACE:
						status=S_IN_OBJECT;
						statusStack.addFirst(Integer.valueOf(status));
						valueStack.addFirst(createObjectContainer(containerFactory));
						break;
					case Yytoken.TYPE_LEFT_SQUARE:
						status=S_IN_ARRAY;
						statusStack.addFirst(Integer.valueOf(status));
						valueStack.addFirst(createArrayContainer(containerFactory));
						break;
					default:
						status=S_IN_ERROR;
					} 
					break;
					
				case S_IN_FINISHED_VALUE:
					if(token.type==Yytoken.TYPE_EOF)
						return valueStack.removeFirst();
					else
						throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
					
				case S_IN_OBJECT:
					switch(token.type){
					case Yytoken.TYPE_COMMA:
						break;
					case Yytoken.TYPE_VALUE:
						if(token.value instanceof String){
							String key=(String)token.value;
							valueStack.addFirst(key);
							status=S_PASSED_PAIR_KEY;
							statusStack.addFirst(Integer.valueOf(status));
						}
						else{
							status=S_IN_ERROR;
						}
						break;
					case Yytoken.TYPE_RIGHT_BRACE:
						if(valueStack.size()>1){
							statusStack.removeFirst();
							valueStack.removeFirst();
							status=peekStatus(statusStack);
						}
						else{
							status=S_IN_FINISHED_VALUE;
						}
						break;
					default:
						status=S_IN_ERROR;
						break;
					} 
					break;
					
				case S_PASSED_PAIR_KEY:
					switch(token.type){
					case Yytoken.TYPE_COLON:
						break;
					case Yytoken.TYPE_VALUE:
						statusStack.removeFirst();
						String key=(String)valueStack.removeFirst();
						Map parent=(Map)valueStack.getFirst();
						parent.put(key,token.value);
						status=peekStatus(statusStack);
						break;
					case Yytoken.TYPE_LEFT_SQUARE:
						statusStack.removeFirst();
						key=(String)valueStack.removeFirst();
						parent=(Map)valueStack.getFirst();
						List newArray=createArrayContainer(containerFactory);
						parent.put(key,newArray);
						status=S_IN_ARRAY;
						statusStack.addFirst(Integer.valueOf(status));
						valueStack.addFirst(newArray);
						break;
					case Yytoken.TYPE_LEFT_BRACE:
						statusStack.removeFirst();
						key=(String)valueStack.removeFirst();
						parent=(Map)valueStack.getFirst();
						Map newObject=createObjectContainer(containerFactory);
						parent.put(key,newObject);
						status=S_IN_OBJECT;
						statusStack.addFirst(Integer.valueOf(status));
						valueStack.addFirst(newObject);
						break;
					default:
						status=S_IN_ERROR;
					}
					break;
					
				case S_IN_ARRAY:
					switch(token.type){
					case Yytoken.TYPE_COMMA:
						break;
					case Yytoken.TYPE_VALUE:
						List val=(List)valueStack.getFirst();
						val.add(token.value);
						break;
					case Yytoken.TYPE_RIGHT_SQUARE:
						if(valueStack.size()>1){
							statusStack.removeFirst();
							valueStack.removeFirst();
							status=peekStatus(statusStack);
						}
						else{
							status=S_IN_FINISHED_VALUE;
						}
						break;
					case Yytoken.TYPE_LEFT_BRACE:
						val=(List)valueStack.getFirst();
						Map newObject=createObjectContainer(containerFactory);
						val.add(newObject);
						status=S_IN_OBJECT;
						statusStack.addFirst(Integer.valueOf(status));
						valueStack.addFirst(newObject);
						break;
					case Yytoken.TYPE_LEFT_SQUARE:
						val=(List)valueStack.getFirst();
						List newArray=createArrayContainer(containerFactory);
						val.add(newArray);
						status=S_IN_ARRAY;
						statusStack.addFirst(Integer.valueOf(status));
						valueStack.addFirst(newArray);
						break;
					default:
						status=S_IN_ERROR;
					} 
					break;
				case S_IN_ERROR:
					throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
				} 
				if(status==S_IN_ERROR){
					throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
				}
			}while(token.type!=Yytoken.TYPE_EOF);
		}
		catch(IOException ie){
			throw ie;
		}
		
		throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
	}
	
	/** Advances the lexer and represents its end-of-input result as a token. */
	private void nextToken() throws ParseException, IOException{
		token = lexer.yylex();
		if(token == null)
			token = new Yytoken(Yytoken.TYPE_EOF, null);
	}
	
	/** Creates an object container, applying the factory's null fallback. */
	private Map createObjectContainer(ContainerFactory containerFactory){
		if(containerFactory == null)
			return new JSONObject();
		Map m = containerFactory.createObjectContainer();
		
		if(m == null)
			return new JSONObject();
		return m;
	}
	
	/** Creates an array container, applying the factory's null fallback. */
	private List createArrayContainer(ContainerFactory containerFactory){
		if(containerFactory == null)
			return new JSONArray();
		List l = containerFactory.creatArrayContainer();
		
		if(l == null)
			return new JSONArray();
		return l;
	}
	
	/**
	 * Parses a string and delivers its structure and values to a handler.
	 *
	 * @param s text containing one JSON document
	 * @param contentHandler receiver for parsing events
	 * @throws ParseException if the input cannot be parsed
	 */
	public void parse(String s, ContentHandler contentHandler) throws ParseException{
		parse(s, contentHandler, false);
	}
	
	/**
	 * Parses a string through callbacks, optionally resuming a prior paused parse.
	 * A handler callback returning {@code false} pauses processing; resume with
	 * this parser instance and the same handler.
	 *
	 * @param s text containing one JSON document
	 * @param contentHandler receiver for parsing events
	 * @param isResume whether to continue previously paused event parsing
	 * @throws ParseException if the input cannot be parsed
	 */
	public void parse(String s, ContentHandler contentHandler, boolean isResume) throws ParseException{
		StringReader in=new StringReader(s);
		try{
			parse(in, contentHandler, isResume);
		}
		catch(IOException ie){
			throw new ParseException(-1, ParseException.ERROR_UNEXPECTED_EXCEPTION, ie);
		}
	}
	
	/**
	 * Parses a reader and delivers its structure and values to a handler.
	 *
	 * @param in reader containing one JSON document
	 * @param contentHandler receiver for parsing events
	 * @throws IOException if reading the source fails
	 * @throws ParseException if the input cannot be parsed
	 */
	public void parse(Reader in, ContentHandler contentHandler) throws IOException, ParseException{
		parse(in, contentHandler, false);
	}
	
	/**
	 * Parses a reader through callbacks, optionally resuming a prior paused parse.
	 * Resumption continues from the parser's saved lexer token and nesting state.
	 *
	 * @param in reader containing the document; used to start a new parse
	 * @param contentHandler receiver for parsing events
	 * @param isResume whether to continue previously paused event parsing
	 * @throws IOException if reading the source fails
	 * @throws ParseException if the input cannot be parsed
	 */
	public void parse(Reader in, ContentHandler contentHandler, boolean isResume) throws IOException, ParseException{
		if(!isResume){
			reset(in);
			handlerStatusStack = new LinkedList();
		}
		else{
			if(handlerStatusStack == null){
				isResume = false;
				reset(in);
				handlerStatusStack = new LinkedList();
			}
		}
		
		LinkedList statusStack = handlerStatusStack;	
		
		try{
			do{
				switch(status){
				case S_INIT:
					contentHandler.startJSON();
					nextToken();
					switch(token.type){
					case Yytoken.TYPE_VALUE:
						status=S_IN_FINISHED_VALUE;
						statusStack.addFirst(Integer.valueOf(status));
						if(!contentHandler.primitive(token.value))
							return;
						break;
					case Yytoken.TYPE_LEFT_BRACE:
						status=S_IN_OBJECT;
						statusStack.addFirst(Integer.valueOf(status));
						if(!contentHandler.startObject())
							return;
						break;
					case Yytoken.TYPE_LEFT_SQUARE:
						status=S_IN_ARRAY;
						statusStack.addFirst(Integer.valueOf(status));
						if(!contentHandler.startArray())
							return;
						break;
					default:
						status=S_IN_ERROR;
					} 
					break;
					
				case S_IN_FINISHED_VALUE:
					nextToken();
					if(token.type==Yytoken.TYPE_EOF){
						contentHandler.endJSON();
						status = S_END;
						return;
					}
					else{
						status = S_IN_ERROR;
						throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
					}
			
				case S_IN_OBJECT:
					nextToken();
					switch(token.type){
					case Yytoken.TYPE_COMMA:
						break;
					case Yytoken.TYPE_VALUE:
						if(token.value instanceof String){
							String key=(String)token.value;
							status=S_PASSED_PAIR_KEY;
							statusStack.addFirst(Integer.valueOf(status));
							if(!contentHandler.startObjectEntry(key))
								return;
						}
						else{
							status=S_IN_ERROR;
						}
						break;
					case Yytoken.TYPE_RIGHT_BRACE:
						if(statusStack.size()>1){
							statusStack.removeFirst();
							status=peekStatus(statusStack);
						}
						else{
							status=S_IN_FINISHED_VALUE;
						}
						if(!contentHandler.endObject())
							return;
						break;
					default:
						status=S_IN_ERROR;
						break;
					} 
					break;
					
				case S_PASSED_PAIR_KEY:
					nextToken();
					switch(token.type){
					case Yytoken.TYPE_COLON:
						break;
					case Yytoken.TYPE_VALUE:
						statusStack.removeFirst();
						status=peekStatus(statusStack);
						if(!contentHandler.primitive(token.value))
							return;
						if(!contentHandler.endObjectEntry())
							return;
						break;
					case Yytoken.TYPE_LEFT_SQUARE:
						statusStack.removeFirst();
						statusStack.addFirst(Integer.valueOf(S_IN_PAIR_VALUE));
						status=S_IN_ARRAY;
						statusStack.addFirst(Integer.valueOf(status));
						if(!contentHandler.startArray())
							return;
						break;
					case Yytoken.TYPE_LEFT_BRACE:
						statusStack.removeFirst();
						statusStack.addFirst(Integer.valueOf(S_IN_PAIR_VALUE));
						status=S_IN_OBJECT;
						statusStack.addFirst(Integer.valueOf(status));
						if(!contentHandler.startObject())
							return;
						break;
					default:
						status=S_IN_ERROR;
					}
					break;
				
				case S_IN_PAIR_VALUE:
					statusStack.removeFirst();
					status = peekStatus(statusStack);
					if(!contentHandler.endObjectEntry())
						return;
					break;
					
				case S_IN_ARRAY:
					nextToken();
					switch(token.type){
					case Yytoken.TYPE_COMMA:
						break;
					case Yytoken.TYPE_VALUE:
						if(!contentHandler.primitive(token.value))
							return;
						break;
					case Yytoken.TYPE_RIGHT_SQUARE:
						if(statusStack.size()>1){
							statusStack.removeFirst();
							status=peekStatus(statusStack);
						}
						else{
							status=S_IN_FINISHED_VALUE;
						}
						if(!contentHandler.endArray())
							return;
						break;
					case Yytoken.TYPE_LEFT_BRACE:
						status=S_IN_OBJECT;
						statusStack.addFirst(Integer.valueOf(status));
						if(!contentHandler.startObject())
							return;
						break;
					case Yytoken.TYPE_LEFT_SQUARE:
						status=S_IN_ARRAY;
						statusStack.addFirst(Integer.valueOf(status));
						if(!contentHandler.startArray())
							return;
						break;
					default:
						status=S_IN_ERROR;
					} 
					break;
					
				case S_END:
					return;
					
				case S_IN_ERROR:
					throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
				} 
				if(status==S_IN_ERROR){
					throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
				}
			}while(token.type!=Yytoken.TYPE_EOF);
		}
		catch(IOException ie){
			status = S_IN_ERROR;
			throw ie;
		}
		catch(ParseException pe){
			status = S_IN_ERROR;
			throw pe;
		}
		catch(RuntimeException re){
			status = S_IN_ERROR;
			throw re;
		}
		catch(Error e){
			status = S_IN_ERROR;
			throw e;
		}
		
		status = S_IN_ERROR;
		throw new ParseException(getPosition(), ParseException.ERROR_UNEXPECTED_TOKEN, token);
	}
}
