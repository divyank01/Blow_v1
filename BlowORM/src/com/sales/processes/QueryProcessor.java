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
package com.sales.processes;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import com.sales.constants.BlowConstatnts;
import com.sales.constants.BlowParam;
import com.sales.core.helper.PropParam;

public class QueryProcessor {

	public static PreparedStatement processQuery(PreparedStatement ps,Map<String, Object> map) throws SQLException{
		Iterator<String> keys=map.keySet().iterator();
		while(keys.hasNext()){
			String key=keys.next();
			processParam(map,key,ps);
		}
		return ps;
	}
	
	private static void processParam(Map<String, Object> map,String key,PreparedStatement sql) throws SQLException{
		PropParam param=(PropParam)map.get(key);
		if(param.getParam().equals(BlowParam.LIKE_AROUND)){
			sql.setObject(((PropParam)map.get(key)).getIndex(), BlowConstatnts.LIKE+((PropParam)map.get(key)).getValue()+BlowConstatnts.LIKE);
		}
		else if(param.getParam().equals(BlowParam.LIKE_FRONT)){
			sql.setObject(((PropParam)map.get(key)).getIndex(), BlowConstatnts.LIKE+((PropParam)map.get(key)).getValue());
			
		}
		else if(param.getParam().equals(BlowParam.LIKE_END)){
			sql.setObject(((PropParam)map.get(key)).getIndex(), ((PropParam)map.get(key)).getValue()+BlowConstatnts.LIKE);
		}
		else{
			sql.setObject(((PropParam)map.get(key)).getIndex(), ((PropParam)map.get(key)).getValue());
		}
	}
}
