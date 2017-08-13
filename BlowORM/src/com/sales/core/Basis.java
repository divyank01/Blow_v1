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

import java.util.List;

import com.sales.blow.exceptions.BlownException;
import com.sales.constants.BlowParam;

public interface Basis<T> {

	public Basis<T> propEquals(String prop,Object value)throws Exception;
	
	//public Basis<T, U> whereClause(String prop,String value)throws Exception;
	
	public Basis<T> order(String prop,BlowParam blowParam)throws Exception;
	
	public Basis<T> groupBy(String prop,BlowParam blowParam)throws Exception;
	
	public Basis<T> having(String prop,BlowParam blowParam)throws Exception;
	
	//public Basis<T, U> setAlias(String prop)throws Exception;
	
	public boolean updateEntity(long sessionId)throws Exception;
	
	public void commitCharge()throws Exception;
	
	public T retrieveOne()throws Exception;
	
	public List<T> retrieveMany()throws Exception;
	
	public Basis<T> prop(BlowParam param,String prop,Object value)throws Exception;
	
	public Basis<T> fetchMode(BlowParam param)throws Exception;
	
	public void remove(Object obj)throws Exception;
}
