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
package com.sales.core;

import static com.sales.core.helper.LoggingHelper.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sales.blow.comparator.DependencyMapComparator;
import com.sales.blow.exceptions.BlownException;
import com.sales.blow.exceptions.EX;
import com.sales.constants.BlowConstatnts;
import com.sales.constants.BlowParam;
import com.sales.core.helper.SessionContainer;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS.Maps;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS.Maps.Attributes;
import com.sales.poolable.parsers.ORM_QUERY_Parser.Queries.MappingObject;
import com.sales.poolable.parsers.ORM_QUERY_Parser.Queries.Query;
import com.sales.pools.ConnectionPool;
import com.sales.pools.ObjectPool;
import com.sales.processes.QueryProcessor;

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



public class QueryExecutor {

	private QueryBuilder querryBuilder;
	private BlowCoreMapper coreMapper;
	private Map<String, Object> params=new HashMap<String, Object>();
	private static final String LIST_SIZE="incomingListSize";
	
	private QueryExecutor(){
		coreMapper=BlowCoreMapper.getInstance();
	}
	protected static QueryExecutor getExecutor(){
		return new QueryExecutor();
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
		//System.out.println(retVal);
		return retVal;
	}
	
	protected void executeStatment(String query) throws Exception{
		Connection con=ObjectPool.getConnection();
		Statement stmt=null;
		try{
			log(query);
			stmt=con.createStatement();
			stmt.execute(query);
		}finally{
			stmt.close();
			ObjectPool.submit(con);
		}
	}
	
	
	protected boolean batchInsertOrUpdate(Object obj1,ORM_MAPPINGS mappings) throws Exception{
		Connection con=ObjectPool.getConnection();
		Map<String, PreparedStatement> m=batchInsertOrUpdate(obj1, mappings, null, con, null, null);
		Iterator<String> iter=sortForRelations(m, mappings).iterator();
		while(iter.hasNext()){
			String ps=iter.next();
			System.out.println(ps);
			PreparedStatement stmt=m.get(ps);
			stmt.close();
		}
		ObjectPool.submit(con);
		return false;
	}
	
	private Map<String, PreparedStatement> batchInsertOrUpdate(Object obj1,ORM_MAPPINGS mappings,QueryBuilder builder,Connection con,Map map,Map m)throws Exception{
		if(m==null)
			m=new HashMap<Object, Object>();
		Map<String, PreparedStatement> sqlMap=map;
		if(builder==null)
			builder=QueryBuilder.newInstance();
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
	
	protected boolean executeInsertOrUpdate(Object obj,ORM_MAPPINGS mappings,Map m,SessionContainer container) throws Exception{
		m.put(obj, null);
		Connection con=null;
		con=container.getConnection();
		querryBuilder=QueryBuilder.newInstance();
		PreparedStatement stmt=null;
		try{
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
							executeInsertOrUpdate(it.next(), mappings, m,container);
						}
					}
					else{
						if(ob1!=null && !m.containsKey(ob1))
							executeInsertOrUpdate(ob1, mappings, m,container);
					}
				}
			}
		}catch(Exception ex){
			log(EX.M9+container.getSessionId());
			container.getConnection().rollback();
			throw new BlownException(ex);
		}finally{
			stmt.close();
		}
		return false;
	}
	
	private int countOfRecord(Connection con,ORM_MAPPINGS mappings,Object obj) throws Exception{
		PreparedStatement ps=querryBuilder.getCountForPk(mappings, obj, con);
		ResultSet rs=ps.executeQuery();
		int retVal=0;
		while(rs.next()){
			retVal=rs.getInt(1);
		}
		rs.close();
		ps.close();
		return retVal;
	}
	
	private String getterForField(String field) {
		String retVal=(String.valueOf(field.charAt(0)).toUpperCase())+field.substring(1);
		return BlowConstatnts.GET+retVal;
	}

	protected Object retriveSingleRecord(String sql,BlowParam blowParam,ORM_MAPPINGS mappings,Object t, Map<String, Object> params,SessionContainer session) throws Exception{
		Connection con=null;
		Object retval=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		try{
			con=session.getConnection();
			log(sql.toString());
			//System.out.println(sql.toString());
			if(con!=null){
				session.getQueries().put(sql, session.getSessionId());
				ps=con.prepareStatement(sql.toString());
				ps=QueryProcessor.processQuery(ps, params);
				rs=ps.executeQuery();
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
					//throw new BlownException("multiple records found");
				}
			}
		}catch(Exception ex){
			if(session.getConnection()!=null)
				session.getConnection().rollback();
			throw new BlownException(ex);
		}finally{
			if(ps!=null)
				ps.close();
			if(rs!=null)
				rs.close();
		}
		return retval;
	}
	
	
	protected List<Object> retriveMultipleRecord(String sql,BlowParam blowParam,ORM_MAPPINGS mappings,Map<String, Object> params,Object t,SessionContainer session) throws Exception{
		Connection con=session.getConnection();;
		List<Object> retval=new ArrayList<Object>();
		log(sql.toString());
		//System.out.println(sql.toString());
		session.getQueries().put(sql, session.getSessionId());
		PreparedStatement ps=null;
		ResultSet rs=null;
		try{
			ps=con.prepareStatement(sql.toString());
			ps=QueryProcessor.processQuery(ps, params);
			rs=ps.executeQuery();
			int counter=0;
			int pos=-1;
			Object ob=null;
			while(rs.next()){
				/*ob=extractResltSet(rs,true,null,blowParam,mappings,t);
			if(!retval.contains(ob)){
				retval.add(ob);
			}*/
				boolean flag=coreMapper.newObjectFromRS(rs, mappings.getMaps().get(t), ob);
				if(counter>0 && ob!=null){
					ob=coreMapper.processObjectForCordinality(ob, mappings, rs,this.params);
				}
				if(ob==null)
					ob=extractResltSet(rs,flag,null,blowParam,mappings,t);
				else
					ob=extractResltSet(rs,flag,ob,blowParam,mappings,t);
				counter++;
				if(!coreMapper.contains(mappings,ob,retval)){
					retval.add(ob);
				}
				if(coreMapper.contains(mappings,ob,retval)){
					pos=coreMapper.getPostionFromList(mappings,ob,retval);
					retval.remove(pos);
					retval.add(ob);
				}
			}

		}finally{
			if(ps!=null)
				ps.close();
			if(rs!=null)
				rs.close();
		}
		return retval;
	}
		
	private Object extractResltSet(ResultSet rs,boolean flag,Object preObject,BlowParam blowParam,ORM_MAPPINGS mappings,Object t) throws Exception {
		return coreMapper.mapPersistaceToObj(rs,mappings.getMaps().get(t),mappings,blowParam,preObject,flag);
	}
	
	protected Object runSql(Query query,Map input,SessionContainer session) throws Exception{
		Connection con=null;
		Object retval=null;
		ResultSet rs=null;
		PreparedStatement ps=null;
		try{
			if(query.getMappingObject()==null)
				throw new BlownException(EX.M23+query.getMappingObjName());
			String sql=query.getContent().trim();
			querryBuilder=QueryBuilder.newInstance();
			con=session.getConnection();
			session.getQueries().put(sql, session.getSessionId());
			sql=querryBuilder.processQuery(query, input);
			log(sql.toString());
			ps=con.prepareStatement(sql.toString());
			rs=ps.executeQuery();
			retval=coreMapper.mapPersistanceObject(rs, query.getMappingObject());
		}catch(Exception e){
			if(rs!=null)
				rs.close();
			if(ps!=null)
				ps.close();
			e.printStackTrace();
			throw new BlownException(e.getMessage());
		}finally{
			if(rs!=null)
				rs.close();
			if(ps!=null)
				ps.close();
		}
		return retval;
	}
}
