package com.sales.utils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Map;

import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS.Maps;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS.Maps.Attributes;
import com.sales.pools.OrmMappingPool;

public abstract class BlowCoreUtils {

	
	/*public static ORM_MAPPINGS mappings;
	static{
		try {
			mappings=OrmMappingPool.getInstance().borrowObject().getOrm_Mappings();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	*//**
	 * 
	 * @param rs ResultSet
	 * @param maps ORM_MAPPINGS.Maps
	 * @return mapped java type
	 * @throws Exception
	 *//*
	public static Object mapPersistaceToObj(ResultSet rs, Maps maps) throws Exception{
		Object obj=Class.forName(maps.getClassName()).newInstance();
		Method[] methods=obj.getClass().getMethods();
		Map<String, Attributes> map=maps.getAttributeMap();

		//while(rs.next()){
			for(Method method:methods){
				if(method.getName().startsWith("set")){
					Iterator<String> iter=map.keySet().iterator();
					while(iter.hasNext()){
						String m=iter.next();
						if(method.getName().equalsIgnoreCase("set"+m)){
							if(!map.get(m).isFk() && map.get(m).getColName()!=null){
								Object[] objs= mapTypes(rs.getObject(map.get(m).getName()+"_"+maps.getIndex()),method.getParameterTypes()[0]);
								method.invoke(obj, objs);
							}
							else{
								Iterator<String> dependents=maps.getDependentClasses().iterator();
								while(dependents.hasNext()){
									String m1=dependents.next();
									if(m1.equalsIgnoreCase(method.getParameterTypes()[0].getCanonicalName()))
										method.invoke(obj,mapDependentsToPersistaceObj(rs, maps,m1));
								}	
							}
						}
					}

				}
			//}
		}
		return obj;
	}



	private static Object mapDependentsToPersistaceObj(ResultSet rs, Maps maps,String m) throws Exception{
		Iterator<String> it=maps.getDependentClassMap().get(m).getAttributeMap().keySet().iterator();
		Object dep=Class.forName(m).newInstance();
		Method[] depMeths=dep.getClass().getMethods();
		int counter=0;
		while(it.hasNext()){
			String prop=it.next();
			for(Method meth:depMeths){						
				if(meth.getName().startsWith("set") && meth.getName().equalsIgnoreCase("set"+prop)){
						Object[] val=mapTypes(rs.getObject(maps.getDependentClassMap().get(m).getAttributeMap().get(prop).getName()
								+"_"+maps.getDependentClassMap().get(m).getIndex()),
								meth.getParameterTypes()[0]);
						if(val!=null){
							counter++;
							meth.invoke(dep,val);
						}
				}
			}
		}
		if(counter==0)
			dep=null;
		return dep;
	}

	private static Object[] mapTypes(Object object, Class cls){
		Object[] retval=null;
		if(object instanceof BigDecimal){
			retval=new Object[1];
			if(cls.isPrimitive()){
				if(cls.getName().equalsIgnoreCase(int.class.getName()))
					retval[0]=((BigDecimal)object).intValue();
				if(cls.getName().equalsIgnoreCase(long.class.getName()))
					retval[0]=((BigDecimal)object).longValue();
				if(cls.getName().equalsIgnoreCase(float.class.getName()))
					retval[0]=((BigDecimal)object).floatValue();
				if(cls.getName().equalsIgnoreCase(double.class.getName()))
					retval[0]=((BigDecimal)object).doubleValue();
			}
			if(!cls.isPrimitive()){
				if(cls.getName().equalsIgnoreCase(Integer.class.getName()))
					retval[0]=((BigDecimal)object).intValue();
				if(cls.getName().equalsIgnoreCase(Long.class.getName()))
					retval[0]=((BigDecimal)object).longValue();
				if(cls.getName().equalsIgnoreCase(Float.class.getName()))
					retval[0]=((BigDecimal)object).floatValue();
				if(cls.getName().equalsIgnoreCase(Double.class.getName()))
					retval[0]=((BigDecimal)object).doubleValue();
			}
		}
		if(object instanceof String){
			retval=new Object[1];
			retval[0]=object;
		}
		return retval;
	}*/
	
	public static String _FCFieldName(String a){
		String suffix=a.substring(1);
		String prefix=a.substring(0, 1).toUpperCase();
		return prefix+suffix;
	}
}
