package com.customer.pojo;

import com.sales.blow.annotations.BlowId;
import com.sales.blow.annotations.BlowProperty;
import com.sales.blow.annotations.BlowSchema;


@BlowSchema(schemaName="OCCUPATION")
public class Occupation {
	@BlowId
	@BlowProperty(columnName="ID" , length=5)
	private int id;
	@BlowProperty(columnName="designation" , length=50)
	private String designation;
	@BlowProperty(columnName="department", length=50)
	private String department;
	@BlowProperty(columnName="CUSTomer_ID",length=5)
	private int custId;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public int getCustId() {
		return custId;
	}
	public void setCustId(int custId) {
		this.custId = custId;
	}
}
