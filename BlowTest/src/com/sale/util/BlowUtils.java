package com.sale.util;

import com.sales.core.BlowContext;
import com.sales.core.Basis;
import com.sales.core.BlowCore;

public class BlowUtils {

	private static BlowContext context;
	static{
		context=BlowCore.getInstance().getContext();
	}
	
	public static Basis getBasis(Class clazz){
		try {
			return context.getBasis(clazz);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public static BlowContext getContext(){
		return context;
	}
	
}
