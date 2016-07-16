package com.sales.core.helper;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.sales.blow.logging.LoggingFormatter;
import com.sales.blow.logging.LoggingHandler;
import com.sales.poolable.parsers.ORM_CONFIG_Parser;
import com.sales.pools.OrmConfigParserPool;

public class LoggingHelper {

	private LoggingHelper(){}
	
	//private static LoggingHelper logger;
	
	private static boolean doLog;
	
	private static Logger LOGGER=Logger.getLogger("com.sales.core.QuerryBuilder");
	
	private static LoggingHandler handler= new LoggingHandler();
	
	static{
		try {
			LOGGER=Logger.getLogger("com.sales.core.QuerryBuilder");
			for(Handler h:LOGGER.getHandlers()){
				LOGGER.removeHandler(h);
			}
			LOGGER.setUseParentHandlers(false);
			LOGGER.addHandler(handler);
			handler.setFormatter(new LoggingFormatter());
			ORM_CONFIG_Parser parser=OrmConfigParserPool.getInstance().borrowObject();
			doLog=parser.getOrm_config().isLoggingEnabled();
			OrmConfigParserPool.getInstance().returnObject(parser);
			handler.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void log(String value){
		if(doLog)
			LOGGER.log(Level.INFO, value);
	}
	
	
	
}
