package com.sales.blow.exceptions;

public class BlownException extends Exception {

	public BlownException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*public BlownException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}
*/
	public BlownException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public BlownException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public BlownException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	
}
