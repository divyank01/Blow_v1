package com.sales.core.helper;

import java.util.Map;

public class CachedQuery {

	private Map<String, Object> cachedParams;
	private String cachedQuery;
	
	public CachedQuery(Map<String, Object> cachedParams, String cachedQuery) {
		super();
		this.cachedParams = cachedParams;
		this.cachedQuery = cachedQuery;
	}
	public Map<String, Object> getCachedParams() {
		return cachedParams;
	}
	public void setCachedParams(Map<String, Object> cachedParams) {
		this.cachedParams = cachedParams;
	}
	public String getCachedQuery() {
		return cachedQuery;
	}
	public void setCachedQuery(String cachedQuery) {
		this.cachedQuery = cachedQuery;
	}
}
