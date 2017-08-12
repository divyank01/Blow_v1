package com.sales.pojo;

import com.sales.blow.annotations.BlowId;
import com.sales.blow.annotations.BlowProperty;
import com.sales.blow.annotations.BlowSchema;
import com.sales.blow.annotations.MapSuperClass;

@MapSuperClass
@BlowSchema(schemaName="ELECTRONIC_PRODUCT_DETAILS")
public class ElectronicProductDetails extends ProductDetails{
	
	@BlowId(generated=true,seq="EPD_SEQ")
	@BlowProperty(columnName="ID")
	private Integer id;
	@BlowProperty(columnName="ELE_DESCRIPTION")
	private String description;
	@BlowProperty(columnName="TECH_DETAILS")
	private String techDetails;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTechDetails() {
		return techDetails;
	}
	public void setTechDetails(String techDetails) {
		this.techDetails = techDetails;
	}
}
