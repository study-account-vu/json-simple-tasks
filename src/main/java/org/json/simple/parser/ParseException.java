package org.json.simple.parser;

/**
 * Reports a lexical or syntactic failure while parsing JSON, including the
 * input position and the unexpected character, token, or underlying exception.
 */
public class ParseException extends Exception {
	private static final long serialVersionUID = -7880698968187728548L;
	
	public static final int ERROR_UNEXPECTED_CHAR = 0;
	public static final int ERROR_UNEXPECTED_TOKEN = 1;
	public static final int ERROR_UNEXPECTED_EXCEPTION = 2;

	private int errorType;
	private Object unexpectedObject;
	private int position;
	
	/**
	 * Creates an exception with an error category and no known input position.
	 *
	 * @param errorType parse error category
	 */
	public ParseException(int errorType){
		this(-1, errorType, null);
	}
	
	/**
	 * Creates an exception with an error category and associated unexpected value.
	 *
	 * @param errorType parse error category
	 * @param unexpectedObject character, token, or exception associated with the error
	 */
	public ParseException(int errorType, Object unexpectedObject){
		this(-1, errorType, unexpectedObject);
	}
	
	/**
	 * Creates an exception with complete diagnostic details.
	 *
	 * @param position character offset where the failure was detected, or {@code -1}
	 *                 if unavailable
	 * @param errorType parse error category
	 * @param unexpectedObject character, token, or exception associated with the error
	 */
	public ParseException(int position, int errorType, Object unexpectedObject){
		this.position = position;
		this.errorType = errorType;
		this.unexpectedObject = unexpectedObject;
	}
	
	/**
	 * Returns the category of this parse error.
	 * @return one of the {@code ERROR_*} category constants
	 */
	public int getErrorType() {
		return errorType;
	}
	
	/**
	 * Sets the category of this parse error.
	 * @param errorType one of the {@code ERROR_*} category constants
	 */
	public void setErrorType(int errorType) {
		this.errorType = errorType;
	}
	
	/**
	 * Returns the input position associated with this error.
	 * @return character offset, or {@code -1} when unavailable
	 */
	public int getPosition() {
		return position;
	}
	
	/**
	 * Sets the input position associated with this error.
	 * @param position character offset, or {@code -1} when unavailable
	 */
	public void setPosition(int position) {
		this.position = position;
	}
	
	/**
	 * Returns the unexpected character, token, or exception associated with this error.
	 * @return associated input or exception object
	 */
	public Object getUnexpectedObject() {
		return unexpectedObject;
	}
	
	/**
	 * Sets the unexpected value associated with this error.
	 * @param unexpectedObject associated character, token, or exception
	 */
	public void setUnexpectedObject(Object unexpectedObject) {
		this.unexpectedObject = unexpectedObject;
	}
	
	/**
	 * Returns the formatted diagnostic message.
	 * @return parse error description
	 */
	public String toString() {
		return getMessage();
	}
	
	/**
	 * Formats the error category, unexpected input, and position as a diagnostic.
	 *
	 * @return human-readable description of this parse failure
	 */
	public String getMessage() {
		StringBuffer sb = new StringBuffer();
		
		switch(errorType){
		case ERROR_UNEXPECTED_CHAR:
			sb.append("Unexpected character (").append(unexpectedObject).append(") at position ").append(position).append(".");
			break;
		case ERROR_UNEXPECTED_TOKEN:
			sb.append("Unexpected token ").append(unexpectedObject).append(" at position ").append(position).append(".");
			break;
		case ERROR_UNEXPECTED_EXCEPTION:
			sb.append("Unexpected exception at position ").append(position).append(": ").append(unexpectedObject);
			break;
		default:
			sb.append("Unkown error at position ").append(position).append(".");
			break;
		}
		return sb.toString();
	}
}
