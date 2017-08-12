package com.sales.pojo;

import java.util.List;

import com.sales.blow.annotations.BlowId;
import com.sales.blow.annotations.BlowProperty;
import com.sales.blow.annotations.BlowSchema;
import com.sales.blow.annotations.One2Many;
import com.sales.blow.annotations.One2One;

@BlowSchema(schemaName="PRODUCT")
public class Prodcty {
	
	@BlowId(generated=true,seq="product_seq")
	@BlowProperty(columnName="ID")
	public int id;
	@BlowProperty(columnName="NAME",length=50)
	private String name;
	@BlowProperty(columnName="CAT_ID")
	private long catId;
	@One2One(fk="PROD_ID",isReferenced=true)
	private ProductDetails details;
	@One2One(fk="PROD_ID",isReferenced=true)
	private ElectronicProductDetails epd;
	@One2One(fk="PROD_ID",isReferenced=true)
	private Stock stock;
	@One2Many(collectionType="java.util.List",isReferenced=true,fk="PROD_ID",type="com.sales.pojo.Stock")
	private List<Stock> stocks;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getCatId() {
		return catId;
	}
	public void setCatId(long price) {
		this.catId = price;
	}
	public ProductDetails getDetails() {
		return details;
	}
	public void setDetails(ProductDetails details) {
		this.details = details;
	}
	public ElectronicProductDetails getEpd() {
		return epd;
	}
	public void setEpd(ElectronicProductDetails epd) {
		this.epd = epd;
	}
	public Stock getStock() {
		return stock;
	}
	public void setStock(Stock stock) {
		this.stock = stock;
	}
	public List<Stock> getStocks() {
		return stocks;
	}
	public void setStocks(List<Stock> stocks) {
		this.stocks = stocks;
	}
	
}
