package com.sale.util;

import com.sales.core.BLowContext;
import com.sales.core.Basis;
import com.sales.core.BlowCore;

public class BlowUtils {

	private static BLowContext context;
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
	public static BLowContext getContext(){
		return context;
	}
	
}
