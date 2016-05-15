package com.sales.constants;

public class SQLTypes {

	public static String get(String javaType){
		if(javaType.equalsIgnoreCase("string") || javaType.equalsIgnoreCase("java.lang.String"))
			return "VARCHAR2";
		if(javaType.equalsIgnoreCase("int") || javaType.equalsIgnoreCase("double") || javaType.equalsIgnoreCase("long") || javaType.equalsIgnoreCase("java.lang.Double") || javaType.equalsIgnoreCase("java.lang.Long"))
			return "NUMBER";
		return null;
	}
}
