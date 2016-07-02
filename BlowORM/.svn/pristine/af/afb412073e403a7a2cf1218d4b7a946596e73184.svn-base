package com.sales.blow.logging;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LoggingFormatter extends Formatter {

	@Override
	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();
        sb.append(record.getLevel()).append(": BLOW: ");
        sb.append(record.getMessage()).append('\n');
        record=null;
        return sb.toString();
	}

}
