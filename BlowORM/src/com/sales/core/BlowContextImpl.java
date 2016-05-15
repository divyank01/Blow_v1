/**
 * 
 */
package com.sales.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.sales.blow.exceptions.BlownException;

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
		if(t instanceof Collection){
			if(t instanceof List){
				List entities =(List)t;
				if(!entities.isEmpty()){
					return new BLowBasisImpl(entities.get(0).getClass()).saveEntity(t);
				}else
					throw new BlownException("Unable to save collection: collection size "+entities.size());
			}else
				throw new BlownException("Unable to save collection. Collection type: "+t.getClass());
		}else{
			return new BLowBasisImpl(t.getClass()).saveEntity(t);
		}
		
	}
	
	@Override
	public Object getSQLResult(String id, Map input) {
		
		try {
			return StoredQueryHandler.getHandler().getObjectFromSQL(id, input);
		} catch (Exception e) {
			e=new BlownException(e);
			e.printStackTrace();
			return null;
		}
	}	

}
