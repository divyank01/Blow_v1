/**
  *  BLOW-ORM is an open source ORM for java and its currently under development.
  *
  *  Copyright (C) 2016  @author Divyank Sharma
  *
  *  This program is free software: you can redistribute it and/or modify
  *  it under the terms of the GNU General Public License as published by
  *  the Free Software Foundation, either version 3 of the License, or
  *  (at your option) any later version.
  *
  *  This program is distributed in the hope that it will be useful,
  *  but WITHOUT ANY WARRANTY; without even the implied warranty of
  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  *  GNU General Public License for more details.
  *
  *  You should have received a copy of the GNU General Public License
  *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  *  
  *  
  *  In Addition to it if you find any bugs or encounter any issue you need to notify me.
  *  I appreciate any suggestions to improve it.
  *  @mailto: divyank01@gmail.com
  */
package com.sales.blow.exceptions;

public interface EX {

	String M1="Trying to get basis on closed session, open session first";
	String M2="Session already closed";
	String M3="Unable to save collection: collection size ";
	String M4="Unable to save collection. Collection type: ";
	String M5="parameters not set for retriving one record";
	String M6="Invalid fetch mode : Valid modes are LAZY or EAGER";
	String M7="trying to delete null object";
	String M8="Query id not found in the mappings";
	String M9="rolling back current session, session_id:";
	String M10="invalid querry type";
	String M11="Mapping not found for class: ";
	String M12="Either propety not present or not complex type";
	String M13="invalid attribute value for condition";
	String M14="Failed to map list properly";
	String M15="Not enough information to connect to databse";
	String M16="failed to initailize Blow  :";
	String M17="PK Required for ";
	String M18="Fk required for one-2-one mappings in class ";
	String M19="Fk required for one-2-many mappings in class ";
	String M20="Class name is not mapped for mapping objects.";
	String M21="Cannot pool null value object";
	/*String M22="Session already closed";
	String M23="Session already closed";
	String M24="Session already closed";
	String M25="Session already closed";
	String M26="Session already closed";
	String M27="Session already closed";
	String M28="Session already closed";*/
}
