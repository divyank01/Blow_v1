package com.sales.core;


public interface BLowContext<T> {

	public Basis<T, T> getBasis(Class<T> clazz)throws Exception;
	public void closeSession();
	public boolean saveOrUpdateEntity(T t)throws Exception;
}
