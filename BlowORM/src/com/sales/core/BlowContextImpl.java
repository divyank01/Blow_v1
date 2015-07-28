/**
 * 
 */
package com.sales.core;


/**
 * @author black
 *
 */
public final class BlowContextImpl<T> implements BLowContext<T>{
	
	protected BlowContextImpl(){}
	//@Override
	public Basis<T, T> getBasis(Class<T> clazz) throws Exception{
		// TODO Auto-generated method stub
		return new BLowBasisImpl<T,T>((T) ((Class)clazz).getCanonicalName());
	}

	@Override
	public void closeSession() {
		
	}
	@Override
	public boolean saveOrUpdateEntity(T t)throws Exception {
		// TODO Auto-generated method stub
		return new BLowBasisImpl(t.getClass()).saveEntity(t);
	}	

}
