package com.sale.util;

public abstract class BlowFactory<T> {

	protected abstract void kill(T t);
	
	protected abstract T spawn();
	
}
