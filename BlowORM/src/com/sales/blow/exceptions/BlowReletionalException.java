package com.sales.blow.exceptions;

import java.sql.SQLException;

public class BlowReletionalException extends SQLException {

	public BlowReletionalException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BlowReletionalException(String reason, String sqlState,
			int vendorCode, Throwable cause) {
		super(reason, sqlState, vendorCode, cause);
		// TODO Auto-generated constructor stub
	}

	public BlowReletionalException(String reason, String SQLState,
			int vendorCode) {
		super(reason, SQLState, vendorCode);
		// TODO Auto-generated constructor stub
	}

	public BlowReletionalException(String reason, String sqlState,
			Throwable cause) {
		super(reason, sqlState, cause);
		// TODO Auto-generated constructor stub
	}

	public BlowReletionalException(String reason, String SQLState) {
		super(reason, SQLState);
		// TODO Auto-generated constructor stub
	}

	public BlowReletionalException(String reason, Throwable cause) {
		super(reason, cause);
		// TODO Auto-generated constructor stub
	}

	public BlowReletionalException(String reason) {
		super(reason);
		// TODO Auto-generated constructor stub
	}

	public BlowReletionalException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
