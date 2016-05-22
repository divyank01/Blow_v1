package com.sales.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.sales.blow.comparator.DependencyMapComparator;
import com.sales.blow.exceptions.BlownException;
import com.sales.constants.BlowConstatnts;
import com.sales.constants.BlowParam;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS.Maps;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS.Maps.Attributes;
import com.sales.poolable.parsers.ORM_QUERY_Parser.Queries.MappingObject;
import com.sales.pools.ConnectionPool;
import com.sales.pools.OrmMappingPool;


/**
 * 
 * 
 * @author divyank
 *
 * As name suggests this class will perform querry execution for insert or update
 *
 * Read an awesome post by Brian @ http://brian.pontarelli.com/2011/06/21/jdbc-batch-vs-multi-row-inserts/
 *
 */


public class QuerryExecutor {

	private QuerryBuilder querryBuilder;
	private BlowCoreMapper coreMapper;
	private Map<String, Object> params=new HashMap<String, Object>();
	private static final String LIST_SIZE="incomingListSize";
	private QuerryExecutor(){
		coreMapper=BlowCoreMapper.getInstance();
	}
	protected static QuerryExecutor getExecutor(){
		return new QuerryExecutor();
	}	
	
	private List<String> sortForRelations(Map m,ORM_MAPPINGS mapping){
		List<Maps> list=new ArrayList<Maps>();
		List<String> retVal=new ArrayList<String>();
		Iterator<String> itr=m.keySet().iterator();
		while(itr.hasNext()){
			list.add(mapping.getMapForClass(itr.next()));
		}
		Collections.sort(list, new DependencyMapComparator());
		for(int i=0;i<list.size();i++){
			retVal.add(list.get(i).getClassName());
		}
		System.out.println(retVal);
		return retVal;
	}
	
	protected void executeStatment(String query) throws Exception{
		Connection con=ConnectionPool.getInstance().borrowObject();
		Statement stmt=null;
		try{
			stmt=con.createStatement();
			stmt.execute(query);
		}finally{
			stmt.close();
			ConnectionPool.getInstance().returnObject(con);
		}
	}
	
	
	protected boolean batchInsertOrUpdate(Object obj1,ORM_MAPPINGS mappings) throws Exception{
		Connection con=ConnectionPool.getInstance().borrowObject();
		Map<String, PreparedStatement> m=batchInsertOrUpdate(obj1, mappings, null, con, null, null);
		Iterator<String> iter=sortForRelations(m, mappings).iterator();
		while(iter.hasNext()){
			String ps=iter.next();
			System.out.println(ps);
			PreparedStatement stmt=m.get(ps);
			stmt.close();
		}
		ConnectionPool.getInstance().returnObject(con);
		return false;
	}
	
	private Map<String, PreparedStatement> batchInsertOrUpdate(Object obj1,ORM_MAPPINGS mappings,QuerryBuilder builder,Connection con,Map map,Map m)throws Exception{
		if(m==null)
			m=new HashMap<Object, Object>();
		Map<String, PreparedStatement> sqlMap=map;
		if(builder==null)
			builder=QuerryBuilder.newInstance();
		if(map==null)
			sqlMap=new HashMap<String, PreparedStatement>();
		if(obj1 instanceof List){
			List list=(List)obj1;
			params.put(LIST_SIZE, list.size());
			for(int i=0;i<list.size();i++){
				Object obj=list.get(i);
				if(!m.containsKey(obj)){
					m.put(obj, null);
					if(!sqlMap.containsKey(obj.getClass().getCanonicalName())){
						PreparedStatement sql=con.prepareStatement(builder.getInsertQuerry(mappings, obj,(Integer)params.get(LIST_SIZE)));
						sqlMap.put(obj.getClass().getCanonicalName(), sql);
					}
					Maps dMap=mappings.getMapForClass(obj.getClass().getCanonicalName());
					Map<String, Attributes> aMap=dMap.getAttributeMap();
					PreparedStatement stmt=sqlMap.get(obj.getClass().getCanonicalName());
					Iterator<String> itr=aMap.keySet().iterator();
					int count=1;
					while(itr.hasNext()){
						String attr=itr.next();
						Object ob1=obj.getClass().getMethod(getterForField(aMap.get(attr).getName()), null).invoke(obj, null);
						if(!aMap.get(attr).isFk() && !aMap.get(attr).isGenerated()){
							stmt.setObject(count, ob1);
							count++;
						}
						if(aMap.get(attr).isFk() && !m.containsKey(ob1)){
							m.put(ob1, null);
							batchInsertOrUpdate(ob1, mappings, builder, con, sqlMap,m);
						}
					}
					stmt.addBatch();
				}
			}
		}else{
			if(!sqlMap.containsKey(obj1.getClass().getCanonicalName())){
				PreparedStatement sql=con.prepareStatement(builder.getInsertQuerry(mappings, obj1,(Integer)params.get(LIST_SIZE)));
				sqlMap.put(obj1.getClass().getCanonicalName(), sql);
			}
			Maps dMap=mappings.getMapForClass(obj1.getClass().getCanonicalName());
			Map<String, Attributes> aMap=dMap.getAttributeMap();
			PreparedStatement stmt=sqlMap.get(obj1.getClass().getCanonicalName());
			Iterator<String> itr=aMap.keySet().iterator();
			int count=1;
			while(itr.hasNext()){
				String attr=itr.next();
				Object ob1=obj1.getClass().getMethod(getterForField(aMap.get(attr).getName()), null).invoke(obj1, null);
				if(!aMap.get(attr).isFk() && !aMap.get(attr).isGenerated()){
					stmt.setObject(count, ob1);
					count++;
				}
				if(aMap.get(attr).isFk() && !m.containsKey(ob1)){
					m.put(ob1, null);
					batchInsertOrUpdate(ob1, mappings, builder, con, sqlMap,m);
				}
			}
			stmt.addBatch();
		}
		return sqlMap;
	}
	
	protected boolean executeInsertOrUpdate(Object obj,ORM_MAPPINGS mappings,Map m) throws Exception{
		m.put(obj, null);
		Connection con=null;
		con=ConnectionPool.getInstance().borrowObject();
		querryBuilder=QuerryBuilder.newInstance();
		PreparedStatement stmt=null;
		if(countOfRecord(con, mappings, obj)==0){
			/*
			 * insert code will go here
			 */
			stmt=querryBuilder.createInsertQuerry(mappings, obj,con);
		}else{
			/*
			 * update code will go here
			 */
			stmt=querryBuilder.createUpadteQuerry(mappings, obj,con);		
		}
		stmt.execute();
		stmt.close();
		
		
		Map<String, Attributes> aMap=mappings.getMapForClass(obj.getClass().getCanonicalName()).getAttributeMap();
		Iterator<String> iter=aMap.keySet().iterator();
		while (iter.hasNext()) {
			String attr=iter.next();
			if(aMap.get(attr).isFk()){
				Object ob1=obj.getClass().getMethod(getterForField(aMap.get(attr).getName()), null).invoke(obj, null);
				if (ob1 instanceof Collection<?>){
					Iterator it=((Collection)ob1).iterator();
					while(it.hasNext()){
						executeInsertOrUpdate(it.next(), mappings, m);
					}
				}
				else{
					if(ob1!=null && !m.containsKey(ob1))
						executeInsertOrUpdate(ob1, mappings, m);
				}
			}
		}
		
		con.commit();
		ConnectionPool.getInstance().returnObject(con);
		return false;
	}
	
	private int countOfRecord(Connection con,ORM_MAPPINGS mappings,Object obj) throws Exception{
		ResultSet rs=querryBuilder.getCountForPk(mappings, obj, con).executeQuery();
		int retVal=0;
		while(rs.next()){
			retVal=rs.getInt(1);
		}
		rs.close();
		return retVal;
	}
	
	private String getterForField(String field) {
		String retVal=(String.valueOf(field.charAt(0)).toUpperCase())+field.substring(1);
		return BlowConstatnts.GET+retVal;
	}

	protected Object retriveSingleRecord(String sql,BlowParam blowParam,ORM_MAPPINGS mappings,Object t, Map<String, Object> params) throws Exception{
		Connection con=null;
		Object retval=null;
			con=ConnectionPool.getInstance().borrowObject();
			System.out.println(sql.toString());
			ResultSet rs=con
			.prepareStatement(sql.toString())
			.executeQuery();
			int counter=0;
			while(rs.next()){
				if(counter>0 && retval!=null){
					retval=coreMapper.processObjectForCordinality(retval, mappings, rs,params);
				}if(retval==null)
					retval=extractResltSet(rs,true,null,blowParam,mappings,t);
				else
					retval=extractResltSet(rs,false,retval,blowParam,mappings,t);
				counter++;
			}
			if(counter>1){
				rs.close();
				ConnectionPool.getInstance().returnObject(con);
				throw new BlownException("multiple records found");
			}
			rs.close();
			ConnectionPool.getInstance().returnObject(con);
		return retval;
	}
	
	protected List<Object> retriveMultipleRecord(String sql,BlowParam blowParam,ORM_MAPPINGS mappings,Object t) throws Exception{
		Connection con=null;
		List<Object> retval=new ArrayList<Object>();
		con=ConnectionPool.getInstance().borrowObject();
		System.out.println(sql.toString());
		ResultSet rs=con.prepareStatement(sql.toString()).executeQuery();
		while(rs.next()){
			Object ob=null;
			ob=extractResltSet(rs,true,null,blowParam,mappings,t);
			if(!retval.contains(ob))
				retval.add(ob);
		}
		rs.close();
		ConnectionPool.getInstance().returnObject(con);
		return retval;
	}
		
	private Object extractResltSet(ResultSet rs,boolean flag,Object preObject,BlowParam blowParam,ORM_MAPPINGS mappings,Object t) throws Exception {
		return coreMapper.mapPersistaceToObj(rs,mappings.getMaps().get(t),mappings,blowParam,preObject,flag);
	}
	
	protected Object runSql(String sql,MappingObject mappingObject,String type) throws Exception{
		Connection con=null;
		Object retval=null;
		con=ConnectionPool.getInstance().borrowObject();
		System.out.println(sql.toString());
		ResultSet rs=con.prepareStatement(sql.toString()).executeQuery();
		retval=coreMapper.mapPersistanceObject(rs, mappingObject);
		rs.close();
		ConnectionPool.getInstance().returnObject(con);
		return retval;
	}
}
