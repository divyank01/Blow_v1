package com.sales.core;


import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sales.blow.exceptions.BlownException;
import com.sales.constants.BlowParam;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS;
import com.sales.pools.ConnectionPool;
import com.sales.pools.OrmMappingPool;

@SuppressWarnings("unchecked")
public class BLowBasisImpl<T, U> implements Basis<T, U> {

	private T t;
	private StringBuffer sql;
	private Map<String, Object> params=new HashMap<String, Object>();
	private ORM_MAPPINGS mappings=null;
	private ORM_MAPPINGS_Parser parser;
	private boolean useJoin;
	private QuerryBuilder querryBuilder;
	private BlowCoreMapper coreMapper;
	private BlowParam blowParam;
	protected BLowBasisImpl(T claz)throws Exception{
		t=claz;
		sql=new StringBuffer();
		querryBuilder=QuerryBuilder.newInstance();
		parser=OrmMappingPool.getInstance().borrowObject();
		mappings=parser.getOrm_Mappings();
		OrmMappingPool.getInstance().returnObject(parser);
		coreMapper=BlowCoreMapper.getInstance();
		if(mappings.getMaps().get(t)!=null && mappings.getMaps().get(t).haveDependents());
			useJoin=true;
	}


	@Override
	public Basis<T, U> asc(String prop) throws Exception{
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void commitCharge() throws Exception{
		// TODO Auto-generated method stub

	}

	@Override
	public Basis<T, U> propEquals(String prop, Object value) throws Exception{
		if(prop!=null&&!prop.equals(""))
			params.put(prop, value);
		querryBuilder.param=BlowParam.EQ;
		return this;
	}

	@Override
	public List<T> retrieveMany(U u) throws Exception{
		Connection con=null;
		List<T> retval=new ArrayList<T>();

		con=ConnectionPool.newInstance().borrowObject();
		querryBuilder.processParams(mappings, sql, params, (String)t, useJoin,blowParam);
		System.out.println(sql.toString());
		ResultSet rs=con.prepareStatement(sql.toString()).executeQuery();
		while(rs.next()){
			retval.add(extractResltSet(rs,true,null));
		}
		rs.close();
		ConnectionPool.newInstance().returnObject(con);
		OrmMappingPool.getInstance().returnObject(parser);
		return retval;
	}

	@Override
	public T retrieveOne() throws Exception{
		Connection con=null;
		T retval=null;
			if(params.isEmpty())
				throw new BlownException("parameters not set for retriving one record");
			con=ConnectionPool.newInstance().borrowObject();
			querryBuilder.processParams(mappings, sql, params, (String)t, useJoin,blowParam);
			System.out.println(sql.toString());
			ResultSet rs=con
			.prepareStatement(sql.toString())
			.executeQuery();
			int counter=0;
			while(rs.next()){
				if(counter>0 && retval!=null){
					retval=(T)coreMapper.processObjectForCordinality(retval, mappings, rs);
				}if(retval==null)
					retval=extractResltSet(rs,true,null);
				else
					retval=extractResltSet(rs,false,retval);
				counter++;
			}
			if(counter>1){
				rs.close();
				ConnectionPool.newInstance().returnObject(con);
				OrmMappingPool.getInstance().returnObject(parser);
				//throw new BlownException("multiple records found");
			}
			rs.close();
			ConnectionPool.newInstance().returnObject(con);
			OrmMappingPool.getInstance().returnObject(parser);
		return retval;
	}


	private T extractResltSet(ResultSet rs,boolean flag,Object preObject) throws Exception {
		return (T)coreMapper.mapPersistaceToObj(rs,mappings.getMaps().get(this.t),mappings,blowParam,preObject,flag);
	}



	protected boolean saveEntity(T t) throws Exception{
		QuerryExecutor executor=QuerryExecutor.getExecutor();
		try {
			executor.executeInsertOrUpdate(t, mappings,new HashMap());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean updateEntity() throws Exception{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Basis<T, U> whereClause(String prop, String value)throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Basis<T, U> setAlias(String prop) throws Exception{
		// TODO Auto-generated method stub
		return this;
	}


	@Override
	public Basis<T, U> prop(BlowParam param,String prop, Object value) {
		switch(param){
		case ALL:

			break;

		case EQ:

			break;

		case GT:

			break;

		case GT_EQ:

			break;

		case LT:

			break;

		case LT_EQ:

			break;
		default:
			break;
		}
		return this;
	}


	@Override
	public Basis<T, U> fetchMode(BlowParam param) throws BlownException {
		if(!(param.equals(BlowParam.EAGER)||param.equals(BlowParam.LAZY)))
			throw new BlownException("Invalid fetch mode : Valid modes are LAZY or EAGER");
		blowParam=param;
		return this;
	}


}
