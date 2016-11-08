package com.sale.util;

import com.sales.core.BlowContext;
import com.sales.core.Basis;
import com.sales.core.BlowCore;
import com.sales.pojo.Prodcty;

public class BlowUtils {

	private static BlowContext context;
	static{
		Prodcty p=new Prodcty();
		context=BlowCore.getInstance().getContext();
	}
	
	public static Basis getBasis(Class clazz){
		try {
			return context.getBasis(clazz);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public static BlowContext getContext(){
		return context;
	}
	
}
