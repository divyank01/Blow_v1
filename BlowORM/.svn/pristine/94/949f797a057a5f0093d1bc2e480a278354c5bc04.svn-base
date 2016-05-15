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
