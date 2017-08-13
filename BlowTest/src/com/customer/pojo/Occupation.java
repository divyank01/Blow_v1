package com.customer.pojo;

import com.sales.blow.annotations.BlowId;
import com.sales.blow.annotations.BlowProperty;
import com.sales.blow.annotations.BlowSchema;
import com.sales.blow.annotations.One2One;


@BlowSchema(schemaName="OCCUPATION")
public class Occupation {
	@BlowId(generated=true,seq="occ_seq")
	@BlowProperty(columnName="ID" , length=5)
	private int id;
	@BlowProperty(columnName="designation" , length=50)
	private String designation;
	@BlowProperty(columnName="department", length=50)
	private String department;
	//@BlowProperty(columnName="CUSTomer_ID",length=5)
	private int custId;
	@One2One(fk="CUSTomer_ID",isReferenced=false)
	private Customer customer;
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
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
