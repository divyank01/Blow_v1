package com.sales.pojos;

import com.sales.blow.annotations.BlowProperty;
import com.sales.blow.annotations.BlowId;
import com.sales.blow.annotations.One2One;
import com.sales.pojo.Prodcty;
import com.sales.blow.annotations.BlowSchema;

@BlowSchema(schemaName="ELECTRONIC_PRODUCT_DETAILS")
public class ElectronicProductDetails{

	@One2One(fk="ID",isReferenced=false)
	private Prodcty product;

	@BlowId
	private int id;

	@BlowProperty(columnName="TECH_DETAILS")
	private String techDetails;

	@BlowProperty(columnName="PRICE")
	private int price;

	@BlowProperty(columnName="COLOR")
	private String color;

	@BlowProperty(columnName="ELE_DESCRIPTION")
	private String description;

	@BlowProperty(columnName="PROD_ID")
	private int prodId;

	@BlowProperty(columnName="BRAND")
	private String brand;

	@BlowProperty(columnName="MATERIAL")
	private String material;
	public Prodcty getProduct(){
		return this.product;
	}
	public void setProduct(Prodcty product){
		this.product=product;
	}
	public int getId(){
		return this.id;
	}
	public void setId(int id){
		this.id=id;
	}
	public String getTechDetails(){
		return this.techDetails;
	}
	public void setTechDetails(String techDetails){
		this.techDetails=techDetails;
	}
	public int getPrice(){
		return this.price;
	}
	public void setPrice(int price){
		this.price=price;
	}
	public String getColor(){
		return this.color;
	}
	public void setColor(String color){
		this.color=color;
	}
	public String getDescription(){
		return this.description;
	}
	public void setDescription(String description){
		this.description=description;
	}
	public int getProdId(){
		return this.prodId;
	}
	public void setProdId(int prodId){
		this.prodId=prodId;
	}
	public String getBrand(){
		return this.brand;
	}
	public void setBrand(String brand){
		this.brand=brand;
	}
	public String getMaterial(){
		return this.material;
	}
	public void setMaterial(String material){
		this.material=material;
	}

}