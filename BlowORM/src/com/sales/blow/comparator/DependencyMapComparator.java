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
package com.sales.blow.comparator;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS.Maps;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS.Maps.Attributes;

public class DependencyMapComparator implements Comparator<Maps> {

	@Override
	public int compare(Maps o1, Maps o2) {
		Map<String, Attributes> map=o1.getFkAttr();
		Iterator<String> itr=map.keySet().iterator();
		boolean flag=false;
		while (itr.hasNext()) {
			String key = (String) itr.next();
			if(map.get(key).isReferenced() && map.get(key).getClassName().equals(o2.getClassName())){
				flag=true;
			}
		}
		if(flag)
			return -1;
		else
			return 1;
	}

}
