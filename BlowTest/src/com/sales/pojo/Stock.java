package com.sales.pojo;

import com.sales.blow.annotations.BlowId;
import com.sales.blow.annotations.BlowProperty;
import com.sales.blow.annotations.BlowSchema;
import com.sales.blow.annotations.One2One;

@BlowSchema(schemaName="STOCK")
public class Stock {

	@BlowId
	@BlowProperty(columnName="ID")
	private int id;
	@BlowProperty(columnName="PROD_ID")
	private int productId;
	@BlowProperty(columnName="LIVE_STOCK")
	private double liveStockCount;
	@BlowProperty(columnName="LOC_ID")
	private int locId;
	@One2One(fk="ID",isReferenced=false)
	private Prodcty product;
	@One2One(fk="STOCK_ID",isReferenced=true)
	private StockMappings mappings;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public double getLiveStockCount() {
		return liveStockCount;
	}
	public void setLiveStockCount(double liveStockCount) {
		this.liveStockCount = liveStockCount;
	}
	public int getLocId() {
		return locId;
	}
	public void setLocId(int locId) {
		this.locId = locId;
	}
	public Prodcty getProduct() {
		return product;
	}
	public void setProduct(Prodcty product) {
		this.product = product;
	}
	public StockMappings getMappings() {
		return mappings;
	}
	public void setMappings(StockMappings mappings) {
		this.mappings = mappings;
	}
	
	
}
