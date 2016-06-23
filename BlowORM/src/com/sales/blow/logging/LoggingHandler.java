package com.sales.blow.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LoggingHandler extends ConsoleHandler {

	{
		super.setFormatter(new LoggingFormatter());
	}
	
}
