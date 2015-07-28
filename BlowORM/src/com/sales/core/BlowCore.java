package com.sales.core;

public final class BlowCore<T> {

	private static BlowCore core;
	
	private BLowContext context=null;
	
	static{
		if(core==null){
			core=new BlowCore();
			core.context=new BlowContextImpl();
		}
	}
	
	public static BlowCore getInstance(){
		return core;
	}
	
	private BlowCore(){}

	public BLowContext<T> getContext() {
		return context;
	}
}
