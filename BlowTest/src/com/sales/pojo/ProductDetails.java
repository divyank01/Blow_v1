package com.sales.pojo;

import com.sales.blow.annotations.BlowId;
import com.sales.blow.annotations.BlowProperty;
import com.sales.blow.annotations.BlowSchema;
import com.sales.blow.annotations.One2One;

@BlowSchema(schemaName="PRODUCT_DETAILS")
public class ProductDetails {

	@BlowId
	@BlowProperty(columnName="ID")
	private Integer id;
	@BlowProperty(columnName="PROD_ID")
	private Integer prodId;
	@BlowProperty(columnName="BRAND")
	private String brand;
	//@BlowProperty(columnName="PROD_SIZE")
	private String productSize;
	@BlowProperty(columnName="COLOR")
	private String color;
	@BlowProperty(columnName="MATERIAL")
	private String material;
	@BlowProperty(columnName="PRICE")
	private Integer price;
	@One2One(fk="PROD_ID")
	private Prodcty product;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getProdId() {
		return prodId;
	}
	public void setProdId(Integer prodId) {
		this.prodId = prodId;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getProductSize() {
		return productSize;
	}
	public void setProductSize(String productSize) {
		this.productSize = productSize;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public Prodcty getProduct() {
		return product;
	}
	public void setProduct(Prodcty product) {
		this.product = product;
	}
	
	
	
}
