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

import java.util.Map;


public interface BlowContext<T> {

	/**
	 * Basis for data modification.
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public Basis<T, T> getBasis(Class<T> clazz)throws Exception;
	
	/**
	 * This will close the session and commit underlying connection and finish session's life.
	 * @throws Exception
	 */
	public void closeSession()throws Exception;
	/**
	 * Open new session 
	 * @return session id of the new session created.
	 * @throws Exception
	 */
	public long openSession()throws Exception;
	public void rollback()throws Exception;
	public boolean saveOrUpdateEntity(T t)throws Exception;
	public Object getSQLResult(String id,Map map)throws Exception;
	public void delete(T t)throws Exception;
	public long getSessionId()throws Exception;
	/**
	 * rollback the session with given session id
	 * @param sessionId
	 * @throws Exception
	 */
	public void rollback(long sessionId) throws Exception;
	/**
	 * Commit the session with given session id
	 * @param sessionId
	 * @throws Exception
	 */
	public void commit(long sessionId)throws Exception;
}
