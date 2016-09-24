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
