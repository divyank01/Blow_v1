package com.sales.core;

import java.util.List;

import com.sales.blow.exceptions.BlownException;
import com.sales.constants.BlowParam;

public interface Basis<T,U> {

	public Basis<T, U> propEquals(String prop,Object value)throws Exception;
	
	public Basis<T, U> whereClause(String prop,String value)throws Exception;
	
	public Basis<T, U> asc(String prop)throws Exception;
	
	public Basis<T, U> setAlias(String prop)throws Exception;
	
	public boolean updateEntity()throws Exception;
	
	public void commitCharge()throws Exception;
	
	public T retrieveOne()throws Exception;
	
	public List<T> retrieveMany(U u)throws Exception;
	
	public Basis<T, U> prop(BlowParam param,String prop,Object value)throws Exception;
	
	public Basis<T, U> fetchMode(BlowParam param)throws Exception;
}
