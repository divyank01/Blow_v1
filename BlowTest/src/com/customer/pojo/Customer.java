package com.customer.pojo;

import java.util.List;

import com.sales.blow.annotations.BlowId;
import com.sales.blow.annotations.BlowProperty;
import com.sales.blow.annotations.BlowSchema;
import com.sales.blow.annotations.One2Many;
import com.sales.blow.annotations.One2One;

@BlowSchema(schemaName="CUSTOMER")
public class Customer {
	@BlowId(generated=true,seq="cust_seq")
	@BlowProperty(columnName="ID",length=5)
	private int id;
	@BlowProperty(columnName="first_name",length=50)
	private String firstName;
	@BlowProperty(columnName="last_name",length=50)
	private String lastName;
	@BlowProperty(columnName="middle_name",length=50)
	private String middleName;
	@BlowProperty(columnName="gender",length=5)
	private String gender;
	@One2Many(collectionType="java.util.List",isReferenced=true,fk="cust_id",type="com.customer.pojo.Address")
	private List<Address> address;
	@One2Many(collectionType="java.util.List",isReferenced=true,fk="customer_id",type="com.customer.pojo.Contact")
	private List<Contact> contacts;
	@One2One(fk="CUSTomer_ID",isReferenced=true)
	private Occupation occupation;
	@One2One(fk="CUST_ID",isReferenced=true)
	private PersonalDetails details;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public List<Address> getAddress() {
		return address;
	}
	public void setAddress(List<Address> address) {
		this.address = address;
	}
	public List<Contact> getContacts() {
		return contacts;
	}
	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}
	public Occupation getOccupation() {
		return occupation;
	}
	public void setOccupation(Occupation occupation) {
		this.occupation = occupation;
	}
	public PersonalDetails getDetails() {
		return details;
	}
	public void setDetails(PersonalDetails details) {
		this.details = details;
	}
	
	
	
}
