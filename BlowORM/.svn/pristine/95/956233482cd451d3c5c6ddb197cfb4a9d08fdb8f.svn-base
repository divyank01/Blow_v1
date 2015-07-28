package com.sales.core;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sales.constants.BlowParam;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS.Maps;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS.Maps.Attributes;
import com.sales.utils.BlowCoreUtils;


public class BlowCoreMapper {

	private BlowCoreMapper(){}

	private static BlowCoreMapper mapper;

	private ORM_MAPPINGS mappings=null;

	protected static BlowCoreMapper getInstance(){
		if(mapper==null)
			mapper=new BlowCoreMapper();
		return mapper;
	}

	/**
	 * 
	 * @param rs
	 * @param maps ORM_MAPPINGS.Maps
	 * @return mapped java type
	 * @throws Exception
	 */
	protected Object mapPersistaceToObj(ResultSet rs, Maps maps,ORM_MAPPINGS mappings,BlowParam blowParam,Object prevObj,boolean reqToSet) throws Exception{
		Object obj=null;
		if(prevObj!=null)
			obj=prevObj;
		else
			obj=Class.forName(maps.getClassName()).newInstance();
		Method[] methods=obj.getClass().getMethods();
		Map<String, Attributes> map=maps.getAttributeMap();
		this.mappings=mappings;
		for(Method method:methods){
			if(method.getName().startsWith("set")){
				Iterator<String> iter=map.keySet().iterator();
				while(iter.hasNext()){
					String m=iter.next();
					if(method.getName().equalsIgnoreCase("set"+m)){
						if(!map.get(m).isFk() && map.get(m).getColName()!=null && reqToSet){
							String val=map.get(m).getName();
							Object[] objs= mapTypes(rs.getObject((val.length()<10?
									val:val.substring(0, 10))+"_"+maps.getIndex()),method.getParameterTypes()[0]);
							if(objs!=null)
								method.invoke(obj, objs);
						}
						else{
							if(blowParam!=null && blowParam.equals(BlowParam.LAZY)){

							}else{
								Iterator<String> dependents=maps.getDependentClasses().iterator();
								Class paramClass=method.getParameterTypes()[0];
								while(dependents.hasNext()){
									String m1=dependents.next();									
									if(m1.equalsIgnoreCase(paramClass.getCanonicalName()) && reqToSet)
										method.invoke(obj,mapDependentsToPersistaceObj(rs, maps,m1,obj,null,true));
								}
								if(paramClass.isArray() || paramClass.getCanonicalName().equalsIgnoreCase(List.class.getCanonicalName())){
									Object lst=obj.getClass().getMethod("get"+BlowCoreUtils._FCFieldName(m), null).invoke(obj, null);
									Object ob2=mapDependentsToPersistaceObj(rs, maps, map.get(m).getClassName(), obj,null,true);
									if(lst==null){
										if(ob2!=null){
											List list=(List) Class.forName("java.util.ArrayList").newInstance();									
											list.add(ob2);
											method.invoke(obj,list);
										}
									}else{
										List list=(List)lst;
										if(!list.contains(ob2) && ob2!=null)
											list.add(ob2);
									}
								}	
							}
						}
					}
				}

			}
		}
		return obj;
	}



	private Object mapDependentsToPersistaceObj(ResultSet rs, Maps maps,String m,Object supOb,Object prevObj,boolean reqToSet) throws Exception{
		Iterator<String> it=maps.getDependentClassMap().get(m).getAttributeMap()
		.keySet().iterator();
		Object dep=null;
		if(prevObj==null)
		dep=Class.forName(m).newInstance();
		else
			dep=prevObj;
		Method[] depMeths=dep.getClass().getMethods();
		int counter=0;
		while(it.hasNext()){
			String prop=it.next();
			for(Method meth:depMeths){						
				if(meth.getName().startsWith("set") && meth.getName().equalsIgnoreCase("set"+prop)){
					if(!maps.getClassName().equalsIgnoreCase(maps.getDependentClassMap().get(m).getAttributeMap().get(prop).getClassName())){

					}
					if(maps.getClassName().equalsIgnoreCase(maps.getDependentClassMap().get(m).getAttributeMap().get(prop).getClassName())){
						/*
						 * mapping reference for one-2-one mappings
						 */
						if(!(meth.getParameterTypes()[0].isArray() || meth.getParameterTypes()[0].getCanonicalName().equals("java.util.List")))
							meth.invoke(dep,supOb);
						else{

							//changes starts
							System.out.println("pappu");
							Class paramClass=meth.getParameterTypes()[0];
							if(paramClass.isArray() || paramClass.getCanonicalName().equalsIgnoreCase(List.class.getCanonicalName())){
								Object lst=dep.getClass().getMethod("get"+BlowCoreUtils._FCFieldName(prop), null).invoke(dep, null);
								Object ob2=mapDependentsToPersistaceObj(rs 
										,mappings.getMaps().get(m)
										,maps.getDependentClassMap().get(m).getAttributeMap().get(prop).getClassName()
										,dep
										,null
										,true);
								if(lst==null){
									if(ob2!=null){
										List list=(List) Class.forName("java.util.ArrayList").newInstance();									
										list.add(ob2);
										meth.invoke(dep,list);
									}
								}else{
									List list=(List)lst;
									if(!list.contains(ob2) && ob2!=null)
										list.add(ob2);
								}
							}
							//changes ends
						}
					}else{
						String temp=meth.getParameterTypes()[0].getCanonicalName();
						if(mappings.getMaps().containsKey(temp) && maps.getDependentClassMap().get(m).haveDependents()){
							/*
							 * check if temp is a mapped class and m(current class) have dependents
							 */
							meth.invoke(dep,mapDependentsToPersistaceObj(rs, mappings.getMaps().get(m),temp, dep,null,true));
						}
						else{
							//System.out.println("champa");
							Class paramClass=meth.getParameterTypes()[0];
							if(paramClass.isArray() || paramClass.getCanonicalName().equalsIgnoreCase(List.class.getCanonicalName())){
								Object lst=dep.getClass().getMethod("get"+BlowCoreUtils._FCFieldName(prop), null).invoke(dep, null);
								Object ob2=mapDependentsToPersistaceObj(rs
										, mappings.getMaps().get(m)
										, maps.getDependentClassMap().get(m).getAttributeMap().get(prop).getClassName()
										, dep
										,null
										,true);
								if(lst==null){
									if(ob2!=null){
										List list=(List) Class.forName("java.util.ArrayList").newInstance();									
										list.add(ob2);
										meth.invoke(dep,list);
									}
								}else{
									List list=(List)lst;
									if(!list.contains(ob2) && ob2!=null)
										list.add(ob2);
								}
							}
							else{
								String propName=maps.getDependentClassMap().get(m).getAttributeMap().get(prop).getName();
								Object[] val=mapTypes(rs.getObject(
										(propName.length()<10?propName:propName.substring(0, 10))
										+"_"+maps.getDependentClassMap().get(m).getIndex()),
										meth.getParameterTypes()[0]);
								if(val!=null){
									counter++;
									meth.invoke(dep,val);
								}
							}
						}
					}
				}
			}
		}
		if(counter==0)
			dep=null;
		return dep;
	}
	@SuppressWarnings("unchecked")
	private Object[] mapTypes(Object object, Class cls){
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
	}

	protected Object processObjectForCordinality(Object obj,ORM_MAPPINGS mappings,ResultSet rs) throws Exception{
		Maps map=mappings.getMapForClass(obj.getClass().getCanonicalName());
		String pkFieldName=map.getPkAttr().getName();
		Object pkFieldValue=obj.getClass().getMethod("get"+BlowCoreUtils._FCFieldName(pkFieldName), null).invoke(obj, null);
		if(rs.getObject(map.getqMap().get(map.getSchemaName()+"."+map.getPkAttr().getColName())).toString().equals(pkFieldValue.toString())){		
			//System.out.println("yoo!!");
			return obj;
		}
		return null;
	}
}
