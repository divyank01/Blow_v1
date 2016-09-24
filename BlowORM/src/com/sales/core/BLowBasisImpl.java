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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sales.blow.exceptions.BlownException;
import com.sales.blow.exceptions.EX;
import com.sales.constants.BlowParam;
import com.sales.core.helper.CachedQuery;
import com.sales.core.helper.PropParam;
import com.sales.core.helper.SessionContainer;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS;
import com.sales.pools.OrmMappingPool;

@SuppressWarnings("unchecked")
public class BLowBasisImpl<T, U extends SessionContainer> implements Basis<T, U> {

	private T t;
	private StringBuffer sql;
	private Map<String, Object> params=new HashMap<String, Object>();
	private ORM_MAPPINGS mappings=null;
	private ORM_MAPPINGS_Parser parser;
	private boolean useJoin;
	private QueryBuilder queryBuilder;
	private BlowParam blowParam;
	private SessionContainer container;
	
	private static final String retOne="R1";
	private static final String retMany="RM";
	private static final String del="D";
	private static final String update="U";
	
	protected BLowBasisImpl(T claz,U container)throws Exception{
		t=claz;
		this.container=container;
		sql=new StringBuffer();
		queryBuilder=QueryBuilder.newInstance();
		parser=OrmMappingPool.getInstance().borrowObject();
		mappings=parser.getOrm_Mappings();		
		OrmMappingPool.getInstance().returnObject(parser);
		if(mappings.getMaps().get(t)!=null && mappings.getMaps().get(t).haveDependents());
			useJoin=true;
	}

	@Override
	public void commitCharge() throws Exception{
		

	}

	@Override
	public Basis<T, U> propEquals(String prop, Object value) throws Exception{
		if(prop!=null&&!prop.equals(""))
			params.put(prop, new PropParam(prop, BlowParam.EQ, value));
		return this;
	}

	@Override
	public List<T> retrieveMany(U u) throws Exception{
		List<T> retval=null;
		String qId=calculateQryId(retMany);
		String querry=null;
		if(QueryBuilder.getQuerryCache().containsKey(qId)){
			querry=((CachedQuery)QueryBuilder.getQuerryCache().get(qId)).getCachedQuery();
			queryBuilder.mapParamIndexes(((CachedQuery)QueryBuilder.getQuerryCache().get(qId)).getCachedParams(), params);
		}else{
			queryBuilder.processParams(mappings, sql, params, (String)t, useJoin,blowParam);
			QueryBuilder.getQuerryCache().put(qId, new CachedQuery(params, sql.toString()));
			querry=sql.toString();
		}
		retval=(List<T>)getExecutor().retriveMultipleRecord(querry, blowParam, mappings,params, t,this.container);
		return retval;
	}

	@Override
	public T retrieveOne() throws Exception{
		T retval=null;
		if(params.isEmpty())
			throw new BlownException(EX.M5);
		String qId=calculateQryId(retOne);
		String querry=null;
		if(QueryBuilder.getQuerryCache().containsKey(qId)){
			querry=((CachedQuery)QueryBuilder.getQuerryCache().get(qId)).getCachedQuery();
			queryBuilder.mapParamIndexes(((CachedQuery)QueryBuilder.getQuerryCache().get(qId)).getCachedParams(), params);
		}else{
			queryBuilder.processParams(mappings, sql, params, (String)t, useJoin,blowParam);
			QueryBuilder.getQuerryCache().put(qId, new CachedQuery(params, sql.toString()));
			querry=sql.toString();
		}	
		retval=(T)getExecutor().retriveSingleRecord(querry, blowParam, mappings, t,params,this.container);
		return retval;
	}


	protected boolean saveEntity(T t) throws Exception{
		QueryExecutor executor=QueryExecutor.getExecutor();
		try {
			if(t instanceof List)
				executor.batchInsertOrUpdate(t, mappings);
			else
				executor.executeInsertOrUpdate(t, mappings,new HashMap(),this.container);
		} catch (Exception e) {
			throw new BlownException(e.getMessage());
		}
		return false;
	}

	@Override
	public boolean updateEntity(long sessionId) throws Exception{
		return false;
	}

	@Override
	public Basis<T, U> prop(BlowParam param,String prop, Object value) {
		this.params.put(prop, new PropParam(prop, param, value));
		return this;
	}


	@Override
	public Basis<T, U> fetchMode(BlowParam param) throws BlownException {
		if(!(param.equals(BlowParam.EAGER)||param.equals(BlowParam.LAZY)))
			throw new BlownException(EX.M6);
		blowParam=param;
		return this;
	}
	
	private QueryExecutor getExecutor(){
		return QueryExecutor.getExecutor();
	}
	
	private String calculateQryId(String func){
		StringBuilder id=null;
		if(params!=null){
			id=new StringBuilder();
			Iterator<String> itr=params.keySet().iterator();
			id.append(t+"+");
			id.append(func+"+");
			id.append(blowParam!=null?(blowParam.toString()+"+"):"");
			while(itr.hasNext()){
				String key=itr.next();
				id.append(key+"+");
				id.append(((PropParam)params.get(key)).getParam().toString()+"+");
			}
			return id.toString();
		}
		return null;
	}

	@Override
	public Basis<T, U> order(String prop, BlowParam blowParam) throws Exception {
		return null;
	}

	@Override
	public Basis<T, U> groupBy(String prop, BlowParam blowParam)
			throws Exception {
		return null;
	}

	@Override
	public Basis<T, U> having(String prop, BlowParam blowParam)throws Exception {
		return null;
	}

	@Override
	public void remove(Object obj) throws Exception {
		
		if(obj==null)
			throw new BlownException(EX.M7);
		List<String> queries=new ArrayList<String>();
		queryBuilder.deleteEntity(mappings,mappings.getMapForClass(obj.getClass().getCanonicalName()),params,queries,container,true);
		for(String s:queries){
			System.out.println(s);
		}
	}
}