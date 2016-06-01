package com.sales.core;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.sales.blow.exceptions.BlownException;
import com.sales.constants.BlowConstatnts;
import com.sales.constants.BlowParam;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS.Maps;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS.Maps.Attributes;
import com.sales.poolable.parsers.ORM_QUERY_Parser.Queries.MappingObject;
import com.sales.utils.BlowCoreUtils;

/**
 * This class will handle mapping process for different scenarios.
 * 
 * 
 * @author Divyank Sharma
 *
 */
public class BlowCoreMapper {

	private BlowCoreMapper(){}

	private static BlowCoreMapper mapper;

	private ORM_MAPPINGS mappings=null;

	protected static BlowCoreMapper getInstance(){
		if(mapper==null)
			mapper=new BlowCoreMapper();
		return mapper;
	}
	
	
	
	protected boolean newObjectFromRS(ResultSet rs, Maps maps,Object prevObj)throws Exception{
		boolean isNewObject=true;
		if(prevObj!=null){
			Attributes attr=mappings.getMapForClass(prevObj.getClass().getCanonicalName()).getPkAttr();
			String val=attr.getName();
			String s=(val.length()<10?val:val.substring(0, 10))+"_"+maps.getIndex();
			Method m=prevObj.getClass().getMethod("get"+BlowCoreUtils._FCFieldName(attr.getName()), null);
			if(mapTypes(rs.getObject(s), m.invoke(prevObj, null).getClass())[0].equals(m.invoke(prevObj, null))){
				isNewObject=false;
			}
		}
		return isNewObject;
	}
	
	/**
	 * It will map objects from resultsets.
	 * 
	 * @param rs
	 * @param maps ORM_MAPPINGS.Maps
	 * @return mapped java type
	 * @throws Exception
	 */
	protected Object mapPersistaceToObj(ResultSet rs, Maps maps,ORM_MAPPINGS mappings,BlowParam blowParam,Object prevObj,boolean reqToSet) throws Exception{
		Object obj=null;
		boolean isNewObject=newObjectFromRS(rs, maps, prevObj);
		if(prevObj!=null && !isNewObject)
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
										if(ob2!=null && !contains(mappings,ob2,list))
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
	
	/**
	 * It will map associated objects to class. 
	 * 
	 * @param rs
	 * @param maps
	 * @param m
	 * @param supOb
	 * @param prevObj
	 * @param reqToSet
	 * @return
	 * @throws Exception
	 */

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
			/*
			 * check if pk of dependent is null if yes return null;
			 */
			if(maps.getDependentClassMap().get(m).getAttributeMap().get(prop).isPk()){
				String val=maps.getDependentClassMap().get(m).getAttributeMap().get(prop).getName();
				String s=(val.length()<10?val:val.substring(0, 10))+"_"+maps.getDependentClassMap().get(m).getIndex();
				if(rs.getObject(s)==null){
					return null;
				}
			}
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
									if(ob2!=null && !list.contains(ob2))
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
	
	/**
	 * Lets map sql types to java types. After this they will be set in POJOs.
	 * @param object
	 * @param cls
	 * @return
	 */
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
		if(object instanceof String || object instanceof Blob){
			retval=new Object[1];
			retval[0]=object;
		}
		if(object instanceof Date){
			retval=new Object[1];
			retval[0]=new java.util.Date(((Date)object).getTime());
		}
		if(object==null){
			retval=new Object[1];
			if(cls.isPrimitive()){
				if(cls.getName().equalsIgnoreCase(int.class.getName()))
					retval[0]=0;
				if(cls.getName().equalsIgnoreCase(long.class.getName()))
					retval[0]=0;
				if(cls.getName().equalsIgnoreCase(float.class.getName()))
					retval[0]=0;
				if(cls.getName().equalsIgnoreCase(double.class.getName()))
					retval[0]=0;
			}
			if(!cls.isPrimitive()){
				if(cls.getName().equalsIgnoreCase(Integer.class.getName()))
					retval[0]=0;
				if(cls.getName().equalsIgnoreCase(Long.class.getName()))
					retval[0]=0;
				if(cls.getName().equalsIgnoreCase(Float.class.getName()))
					retval[0]=0;
				if(cls.getName().equalsIgnoreCase(Double.class.getName()))
					retval[0]=0;
				if(cls.getName().equalsIgnoreCase(String.class.getName())||cls.getName().equalsIgnoreCase(Blob.class.getName())){
					retval[0]=null;
				}
				if(cls.getName().equalsIgnoreCase(Date.class.getName())){
					retval[0]=null;
				}
			}
		}
		return retval;
	}

	private boolean equals(Object obj1, Object obj2){
		boolean retval=false;
		if(obj1.getClass().getName().equalsIgnoreCase(int.class.getName()))
			return ((Integer)obj1).intValue()==((Integer)obj2).intValue();
		if(obj1.getClass().getName().equalsIgnoreCase(long.class.getName()))
			return ((Long)obj1).longValue()==((Long)obj2).longValue();
		if(obj1.getClass().getName().equalsIgnoreCase(float.class.getName()))
			return ((Float)obj1).floatValue()==((Float)obj2).floatValue();
		if(obj1.getClass().getName().equalsIgnoreCase(double.class.getName()))
			return ((Double)obj1).doubleValue()==((Double)obj2).doubleValue();

		if(obj1.getClass().getName().equalsIgnoreCase(Integer.class.getName()))
			return ((Integer)obj1).intValue()==((Integer)obj2).intValue();
		if(obj1.getClass().getName().equalsIgnoreCase(Long.class.getName()))
			return ((Long)obj1).longValue()==((Long)obj2).longValue();
		if(obj1.getClass().getName().equalsIgnoreCase(Float.class.getName()))
			return ((Float)obj1).floatValue()==((Float)obj2).floatValue();
		if(obj1.getClass().getName().equalsIgnoreCase(Double.class.getName()))
			return ((Double)obj1).doubleValue()==((Double)obj2).doubleValue();
		if(obj1 instanceof String || obj1 instanceof Blob){
			return obj1.equals(obj2);
		}
		return retval;
	}

	/**
	 * this is just a way to bypass contains method It will loop bt
	 * contains loops anyway so m gonna give it a try.
	 * And its not easy to reflect .equals and dont wanna enforce user to override
	 * on the basis on PK
	 * 
	 * @return if list contains obj or not
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	protected boolean contains(ORM_MAPPINGS mappings ,Object newObj,List lst) throws Exception{
		String getter="get"+BlowCoreUtils._FCFieldName(mappings.getMapForClass(newObj.getClass().getCanonicalName()).getPkAttr().getName()).trim();
		Object obj=newObj.getClass().getMethod(getter, new Class[0]).invoke(newObj, null);		
		for(Object o:lst){
			if(equals(newObj.getClass().getMethod(getter, new Class[0]).invoke(newObj, null),o.getClass().getMethod(getter, new Class[0]).invoke(o, null))){
				return true;
			}
		}
		return false;
	}
	
	
	
	protected int getPostionFromList(ORM_MAPPINGS mappings ,Object newObj,List lst) throws Exception{
		String getter="get"+BlowCoreUtils._FCFieldName(mappings.getMapForClass(newObj.getClass().getCanonicalName()).getPkAttr().getName()).trim();
		Object obj=newObj.getClass().getMethod(getter, new Class[0]).invoke(newObj, null);		
		int i=0;
		for(Object o:lst){
			if(equals(newObj.getClass().getMethod(getter, new Class[0]).invoke(newObj, null),o.getClass().getMethod(getter, new Class[0]).invoke(o, null))){
				return i;
			}
			i++;
		}
		throw new BlownException("Failed to map list properly");
	}
	
	
	/**
	 * 
	 * @param obj
	 * @param mappings
	 * @param rs
	 * @param params
	 * @return object for further modification or null if no modification is required
	 * @throws Exception
	 * 
	 * Checks if further modification on the object is required it will check in resultset row if the provided
	 * params for retrival are same with previous rows.
	 * 
	 */
	protected Object processObjectForCordinality(Object obj,ORM_MAPPINGS mappings,ResultSet rs, Map<String, Object> params) throws Exception{
		Maps map=mappings.getMapForClass(obj.getClass().getCanonicalName());
		Iterator<String> itr=params.keySet().iterator();
		boolean isAllPropEqual=true;
		while(itr.hasNext()){
			String param=itr.next();
			if(param.contains(BlowConstatnts.DOT)){
				StringTokenizer tokens=new StringTokenizer(param, BlowConstatnts.DOT);
				StringBuffer buff=new StringBuffer();
				String columnName=getColNameForRSfromParam(mappings, map, buff, tokens);
				/*
				 * find better solution for this array
				 */
				String[] className=new String[1];
				Object val=getValueForParamFromObject(obj, mappings, map, new StringTokenizer(param, BlowConstatnts.DOT),className);
				if(!rs.getObject(mappings.getMapForClass(className[0]).getqMap().get(columnName)).toString().equals(val.toString())){
					isAllPropEqual=false;
				}
			}else{
				String columnName=map.getSchemaName()+BlowConstatnts.DOT+map.getAttributeMap().get(param).getColName();
				Object val=obj.getClass().getMethod(BlowConstatnts.GET+BlowCoreUtils._FCFieldName(param), null).invoke(obj, null);
				if(!rs.getObject(map.getqMap().get(columnName)).toString().equals(val.toString())){
					isAllPropEqual=false;
				}
			}
		}
		if(isAllPropEqual)
			return obj;
		return null;
	}

	public String getColNameForRSfromParam(ORM_MAPPINGS mappings,Maps map,StringBuffer buff,StringTokenizer tokens){
		String token=tokens.nextToken();		
		/*
		 * check if token is a dependent complex type
		 */
		if(map.getAttributeMap().get(token).isFk()){
			if(tokens.countTokens()==1)
				buff.append(map.getDependentClassMap().get(map.getAttributeMap().get(token).getClassName()).getSchemaName());
			map=mappings.getMapForClass(map.getAttributeMap().get(token).getClassName());
			if(tokens.hasMoreTokens())
				getColNameForRSfromParam(mappings, map, buff, tokens);
		}else{
			buff.append(BlowConstatnts.DOT+map.getAttributeMap().get(token).getColName());
		}
		return buff.toString();
	}
	
	public Object getValueForParamFromObject(Object obj,ORM_MAPPINGS mappings,Maps map,StringTokenizer tokens,String[] className)throws Exception{		
		Object retVal=null;
		String token=tokens.nextToken();		
		/*
		 * check if token is a dependent complex type
		 */
		if(map.getAttributeMap().get(token).isFk()){
			obj=obj.getClass().getMethod(BlowConstatnts.GET+BlowCoreUtils._FCFieldName(token), null).invoke(obj, null);
			className[0]=map.getAttributeMap().get(token).getClassName();
			map=mappings.getMapForClass(map.getAttributeMap().get(token).getClassName());
			if(tokens.hasMoreTokens())
				retVal=getValueForParamFromObject(obj,mappings, map,tokens,className);
		}else{
			return obj.getClass().getMethod(BlowConstatnts.GET+BlowCoreUtils._FCFieldName(token), null).invoke(obj, null);
		}
		return retVal;
	}
	
	/**
	 * 
	 * It maps result set to into object based on mappingObject for saved queries.
	 * 
	 * @param rs
	 * @param mappingObject
	 * @return
	 * @throws Exception
	 */
	public Object mapPersistanceObject(ResultSet rs,MappingObject mappingObject) throws Exception{
		if(rs!=null){
			int count=0;
			Object temp=null;
			Object list=null;
			boolean isList=false;
			while(rs.next()){
				Object obj=Class.forName(mappingObject.getClassName()).newInstance();
				Iterator<String> itr=mappingObject.getProperties().keySet().iterator();
				while(itr.hasNext()){
					String s=itr.next().trim();
					for(Method m:obj.getClass().getMethods()){
						if(m.getName().equals(BlowConstatnts.SET+BlowCoreUtils._FCFieldName(s))){
							m.invoke(obj, mapTypes(rs.getObject(mappingObject.getProperties().get(s)),m.getParameterTypes()[0]));
							break;
						}
					}
				}
				if(temp==null)
					temp=obj;
				if(count>0){
					if(list==null){
						isList=true;
						list = new ArrayList<Object>();
						((List)list).add(temp);
					}
					((List)list).add(obj);
				}
				count++;
			}
			if(isList)
				return list;
			else
				return temp;
		}else{
			throw new BlownException();
		}
	}
}
