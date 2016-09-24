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
package com.sales.pools;

import com.sales.poolable.parsers.ORM_CONFIG_Parser;
import com.sales.poolables.factories.OrmConfigParserFactory;

public class OrmConfigParserPool extends SimpleObjectPool<ORM_CONFIG_Parser, String> {

	private static OrmConfigParserPool pool;
	
	static{
		if(pool==null)
			pool=new OrmConfigParserPool(new OrmConfigParserFactory());
	}
	
	private OrmConfigParserPool(BlowFactory<ORM_CONFIG_Parser> factory){
		super(factory);
	}
	
	protected static OrmConfigParserPool getInstance(){
		return pool;
	}
	
}
