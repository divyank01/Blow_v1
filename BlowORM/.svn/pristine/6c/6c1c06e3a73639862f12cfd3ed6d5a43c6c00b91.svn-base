package com.sales.core;

import java.util.Map;


public interface BLowContext<T> {

	public Basis<T, T> getBasis(Class<T> clazz)throws Exception;
	public void closeSession();
	public boolean saveOrUpdateEntity(T t)throws Exception;
	public Object getSQLResult(String id,Map map);
}
