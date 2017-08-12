package com.customer.pojo;

import com.sales.blow.annotations.BlowId;
import com.sales.blow.annotations.BlowProperty;
import com.sales.blow.annotations.BlowSchema;


@BlowSchema(schemaName="PersonalDetails")
public class PersonalDetails {
	@BlowId(generated=true,seq="person_det_seq")
	@BlowProperty(columnName="ID",length=5)
	private int id;
	@BlowProperty(columnName="nationality",length=20)
	private String nationality;
	@BlowProperty(columnName="motherTongue",length=20)
	private String motherTongue;
	@BlowProperty(columnName="CUST_ID",length=5)
	private int custId;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	public String getMotherTongue() {
		return motherTongue;
	}
	public void setMotherTongue(String motherTongue) {
		this.motherTongue = motherTongue;
	}
	public int getCustId() {
		return custId;
	}
	public void setCustId(int custId) {
		this.custId = custId;
	}
	 
	
}
