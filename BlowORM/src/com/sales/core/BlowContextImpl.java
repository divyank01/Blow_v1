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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sales.blow.exceptions.BlownException;
import com.sales.core.helper.SessionContainer;
import com.sales.pools.ConnectionPool;

/**
 * @author black
 *
 */
public final class BlowContextImpl<T> implements BLowContext<T>{
	
	private SessionContainer session=null;
	
	private static Map<Long,SessionContainer> activeSessions=new HashMap<Long, SessionContainer>();
	
	protected BlowContextImpl(){}
	
	@Override
	public Basis<T, T> getBasis(Class<T> clazz) throws Exception{
		if(session==null)
			throw new BlownException("Trying to get basis on closed session, open session first");
		return new BLowBasisImpl<T,T>((T) ((Class)clazz).getCanonicalName(),this.session);
	}

	@Override
	public void closeSession() throws Exception {
		if(this.session!=null && this.session.getConnection()!=null){
			BlowContextImpl.activeSessions.remove(this.getSessionId());
			this.session.getConnection().commit();
			ConnectionPool.getInstance().returnObject(this.session.getConnection());
			this.session=null;
		}
		else
			throw new BlownException("Session already closed");
	}
	
	@Override
	public boolean saveOrUpdateEntity(T t)throws Exception {
			if(session==null)
				throw new BlownException("Trying to get basis on closed session, open session first");
			if(t instanceof Collection){
				if(t instanceof List){
					List entities =(List)t;
					if(!entities.isEmpty()){
						return new BLowBasisImpl(entities.get(0).getClass(),this.session).saveEntity(t);
					}else
						throw new BlownException("Unable to save collection: collection size "+entities.size());
				}else
					throw new BlownException("Unable to save collection. Collection type: "+t.getClass());
			}else{
				return new BLowBasisImpl(t.getClass(),this.session).saveEntity(t);
			}
	}
	
	@Override
	public Object getSQLResult(String id, Map input) throws BlownException {
		if(session==null)
			throw new BlownException("Trying to get basis on closed session, open session first");
		try {
			return StoredQueryHandler.getHandler().getObjectFromSQL(id, input,this.session);
		} catch (Exception e) {
			e=new BlownException(e);
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public void openSession() throws SQLException {
		this.session=new SessionContainer();
		try {
			this.session.setSessionId(Math.round(Math.random()*100000000000L));
			this.session.setConnection(ConnectionPool.getInstance().borrowObject());
			BlowContextImpl.activeSessions.put(this.getSessionId(), this.session);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void rollback() throws BlownException,SQLException {
		if(session==null)
			throw new BlownException("Trying to get basis on closed session, open session first");
		this.session.getConnection().rollback();
		this.session.getConnection().commit();
	}
	@Override
	public long getSessionId() throws BlownException {
		if(session==null)
			throw new BlownException("Trying to get basis on closed session, open session first");
		return this.session.getSessionId();
	}	

}
