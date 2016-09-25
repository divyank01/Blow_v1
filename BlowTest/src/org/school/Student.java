package org.school;

/**
* This File is generated by Blow-orm according to the mapping provided
* Modification in this file can lead to data curruption
*/

import com.sales.blow.annotations.BlowId;
import com.sales.blow.annotations.BlowProperty;
import com.sales.blow.annotations.BlowSchema;

@BlowSchema(schemaName="STUDENT")
public class Student{

	@BlowId
	@BlowProperty(columnName="id")
	private int id;

	@BlowProperty(columnName="L_NAME")
	private String lastName;

	@BlowProperty(columnName="AGE")
	private int age;

	@BlowProperty(columnName="F_NAME")
	private String firstName;
	public int getId(){
		return this.id;
	}
	public void setId(int id){
		this.id=id;
	}
	public String getLastName(){
		return this.lastName;
	}
	public void setLastName(String lastName){
		this.lastName=lastName;
	}
	public int getAge(){
		return this.age;
	}
	public void setAge(int age){
		this.age=age;
	}
	public String getFirstName(){
		return this.firstName;
	}
	public void setFirstName(String firstName){
		this.firstName=firstName;
	}

}