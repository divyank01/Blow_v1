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

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sales.blow.exceptions.BlownException;
import com.sales.blow.exceptions.EX;
import com.sales.core.helper.SessionContainer;
import com.sales.pools.ObjectPool;

import static com.sales.core.helper.LoggingHelper.log;

/**
 * @author black
 *
 */
public final class BlowContextImpl<T> implements BlowContext<T>{
	
	private SessionContainer session=null;
	
	private static Map<Long,SessionContainer> activeSessions=new ConcurrentHashMap<Long, SessionContainer>();
	
	protected BlowContextImpl(){}
	
	@Override
	public Basis<T> getBasis(Class<T> clazz) throws Exception{
		try{
			if(session==null)
				throw new BlownException(EX.M1);
			return new BlowBasisImpl((T) ((Class)clazz).getCanonicalName(),this.session);
		}catch (Exception e) {
			throw new BlownException(e.getMessage());
		}
	}

	@Override
	public void closeSession() throws Exception {
		if(this.session!=null && this.session.getConnection()!=null){
			BlowContextImpl.activeSessions.remove(this.getSessionId());
			this.session.getConnection().commit();
			ObjectPool.submit(this.session.getConnection());
			this.session=null;
		}
		else
			throw new BlownException(EX.M2);
	}
	
	@Override
	public boolean saveOrUpdateEntity(T t)throws Exception {
			if(session==null)
				throw new BlownException(EX.M1);
			if(t instanceof Collection){
				if(t instanceof List){
					List entities =(List)t;
					if(!entities.isEmpty()){
						return new BlowBasisImpl(entities.get(0).getClass(),this.session).saveEntity(t);
					}else
						throw new BlownException(EX.M3+entities.size());
				}else
					throw new BlownException(EX.M4+t.getClass());
			}else{
				return new BlowBasisImpl(t.getClass(),this.session).saveEntity(t);
			}
	}
	
	@Override
	public Object getSQLResult(String id, Map input) throws BlownException {
		if(session==null)
			throw new BlownException(EX.M1);
		try {
			return StoredQueryHandler.getHandler().getObjectFromSQL(id, input,this.session);
		} catch (Exception e) {
			e=new BlownException(e);
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public long openSession() throws SQLException {
		this.session=new SessionContainer();
		try {
			this.session.setSessionId(Math.round(Math.random()*100000000000L));
			this.session.setConnection(ObjectPool.getConnection());
			BlowContextImpl.activeSessions.put(this.getSessionId(), this.session);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.session.getSessionId();
	}
	@Override
	public void rollback() throws BlownException,SQLException {
		if(session==null)
			throw new BlownException(EX.M1);
		this.session.getConnection().rollback();
		this.session.getConnection().commit();
		log(EX.M9+session.getSessionId());
	}
	
	@Override
	public void rollback(long sessionId) throws BlownException,SQLException {
		SessionContainer ses=activeSessions.get(sessionId);
		if(ses==null)
			throw new BlownException(EX.M24);
		ses.getConnection().rollback();
		//ses.getConnection().commit();
	}
	
	@Override
	public long getSessionId() throws BlownException {
		if(session==null)
			throw new BlownException(EX.M1);
		return this.session.getSessionId();
	}

	@Override
	public void delete(T t) throws Exception{
		getBasis((Class<T>)t.getClass()).remove(t);;
		
	}

	@Override
	public void commit(long sessionId) throws Exception {
		if(activeSessions.get(sessionId)!=null)
		activeSessions.get(sessionId).getConnection().commit();
		else 
			throw new BlownException(EX.M24);
	}	

}
