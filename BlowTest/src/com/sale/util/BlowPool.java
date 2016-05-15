package com.sale.util;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class BlowPool<T> {

	private long expire = 300000L;
	
	private BlowFactory<T> factory;
	
	private Map<T,Long> locked=new Hashtable<T,Long>();
	
	private Map<T,Long> unLocked=new Hashtable<T, Long>();
	
	protected BlowPool(BlowFactory factory){
		this.factory=factory;
	}
	
	public synchronized T loan(){
		long current = System.currentTimeMillis();
		Iterator<T> keys=unLocked.keySet().iterator();
		while(keys.hasNext()){
			T t=keys.next();
			if(current-unLocked.get(t)>expire){
				factory.kill(t);
				unLocked.remove(t);
				t=null;
			}
			if(current-unLocked.get(t)<expire){
				unLocked.remove(t);
				locked.put(t, current);
				return t;
			}
		}
		T t=factory.spawn();
		locked.put(t, current);
		return t;
	}
	
	public synchronized void submit(T t){
		long current = System.currentTimeMillis();
		if(current-locked.get(t)>expire){
			factory.kill(t);
		}else{
			unLocked.put(t, locked.get(t));
			locked.remove(t);		
		}
	}
	
}
