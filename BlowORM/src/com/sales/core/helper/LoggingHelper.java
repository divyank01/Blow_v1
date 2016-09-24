/**
  *  BLOW-ORM is an open source ORM for java and its currently under development.
  *
  *  Copyright (C) 2016  @author Divyank Sharma
  *
  *  This program is free software: you can redistribute it and/or modify
  *  it under the terms of the GNU General Public License as published by
  *  the Free Software Foundation, either version 3 of the License, or
  *  (at your option) any later version.
  *
  *  This program is distributed in the hope that it will be useful,
  *  but WITHOUT ANY WARRANTY; without even the implied warranty of
  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  *  GNU General Public License for more details.
  *
  *  You should have received a copy of the GNU General Public License
  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  *  
  *  
  *  In Addition to it if you find any bugs or encounter any issue you need to notify me.
  *  I appreciate any suggestions to improve it.
  *  @mailto: divyank01@gmail.com
  */
package com.sales.core.helper;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.sales.blow.logging.LoggingFormatter;
import com.sales.blow.logging.LoggingHandler;
import com.sales.poolable.parsers.ORM_CONFIG_Parser;
import com.sales.pools.ObjectPool;
import com.sales.pools.OrmConfigParserPool;

public class LoggingHelper {

	private LoggingHelper(){}
	
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
			ORM_CONFIG_Parser parser=ObjectPool.getConfig();
			doLog=parser.getOrm_config().isLoggingEnabled();
			ObjectPool.submit(parser);
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
