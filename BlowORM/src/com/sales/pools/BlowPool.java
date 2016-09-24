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
package com.sales.pools;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import com.sales.blow.exceptions.EX;
import com.sales.blow.exceptions.PoolingException;
import com.sales.poolable.parsers.ORM_CONFIG_Parser;

public class BlowPool<T> {

	private long expire = 3000000L;
	
	private BlowFactory<T> factory;
	
	private Map<T,Long> locked=new Hashtable<T,Long>();
	
	private Map<T,Long> unLocked=new Hashtable<T, Long>();
	
	protected BlowPool(BlowFactory<T> factory){
		this.factory=factory;
	}
	
	protected synchronized T borrowObject() throws Exception {
		long current = System.currentTimeMillis();
		Iterator<T> keys=unLocked.keySet().iterator();
		if(!unLocked.keySet().isEmpty()){
			while(keys.hasNext()){
				T t=keys.next();
				if(current-unLocked.get(t)>expire){
					unLocked.remove(t);
					factory.kill(t);
					t=null;
				}
				if(current-unLocked.get(t)<expire){
					//printPools(1);
					unLocked.remove(t);
					locked.put(t, current);
					return t;
				}
			}
		}
		T t=factory.spawn();
		if(t!=null)
			locked.put(t, current);
		else
			throw new PoolingException(EX.M21);
		//printPools(2);
		return t;
	}
	
	protected synchronized void returnObject(T t) throws Exception {
		long current = System.currentTimeMillis();
		if(locked.containsKey(t) && current-locked.get(t)>expire){
			factory.kill(t);
		}else{
			unLocked.put(t, locked.containsKey(t)?locked.get(t):current);
			if(locked.containsKey(t))
				locked.remove(t);		
		}
		//printPools(3);
	}
	
	public synchronized void invalidateObject(T t) throws Exception {
		factory.kill(t);
	}
	
	private void printPools(int m){
		System.out.println("locked========>"+locked+"\t\t"+m);
		System.out.println("unlocked========>"+unLocked+"\t\t"+m);
	}
	
	public void printPoolSize(){
		System.out.println("locked :"+locked.size()+" entities found.");
		System.out.println("unlocked :"+unLocked.size()+" entities found.");
	}
}
