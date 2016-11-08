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
package com.sales.core;

import static com.sales.core.helper.LoggingHelper.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.sales.blow.exceptions.BlownException;
import com.sales.blow.exceptions.EX;
import com.sales.constants.BlowConstatnts;
import com.sales.constants.BlowParam;
import com.sales.constants.BlowParams;
import com.sales.constants.SQLTypes;
import com.sales.core.helper.PropParam;
import com.sales.core.helper.SessionContainer;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS.Maps;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS.Maps.Attributes;
import com.sales.poolable.parsers.ORM_QUERY_Parser.Queries.Query;
import com.sales.poolable.parsers.ORM_QUERY_Parser.Queries.Query.Condition;

public class QueryBuilder {

	private QueryBuilder(){}
	private static QueryBuilder bildr;
	protected String propBasis;
	private static final String cDelim="~@~";
	private static final String hash="#";
	private static final String nxtLine="\n";
	private static final String tab="\t";
	private static Map<String,Object> querryCache=new HashMap<String, Object>();
	protected static QueryBuilder newInstance(){
		bildr=new QueryBuilder();
		return bildr;
	}
	/*
	 * changes required for complex primary key
	 */
	protected PreparedStatement getCountForPk(ORM_MAPPINGS mappings,Object obj,Connection con) throws Exception{
		StringBuffer buffer=new StringBuffer();
		buffer.append("SELECT COUNT(*) AS count FROM ");
		Maps dMap=mappings.getMapForClass(obj.getClass().getCanonicalName());
		buffer.append(dMap.getSchemaName());
		buffer.append(" WHERE ");
		buffer.append(dMap.getPkAttr().getColName()+"=?");
		log(buffer.toString());
		Object ob1=obj.getClass().getMethod(getterForField(dMap.getPkAttr().getName()), null).invoke(obj, null);
		PreparedStatement smt=con.prepareStatement(buffer.toString());
		smt.setObject(1, ob1);
		return smt;
	}

	protected String getOracleImplForBatchInsertQuery(ORM_MAPPINGS mappings,Object obj,StringBuilder sql){
		if(sql==null){
			sql=new StringBuilder();
			sql.append("INSERT ALL ");
		}
		StringBuilder qMarks=new StringBuilder();
		Maps dMap=mappings.getMapForClass(obj.getClass().getCanonicalName());
		Map<String, Attributes> aMap=dMap.getAttributeMap();
		Iterator<String> itr=aMap.keySet().iterator();
		sql.append("INTO "+dMap.getSchemaName()+" (");
		qMarks.append(" (");
		while(itr.hasNext()){
			String key=itr.next();
			if(!aMap.get(key).isFk()){
				//its normal for this object not foreign.
				sql.append(aMap.get(key).getColName());
				if(aMap.get(key).isGenerated()){
					qMarks.append(aMap.get(key).getSeqName()+".nextval");
				}else{
					qMarks.append("?");
				}
				if(itr.hasNext()){
					qMarks.append(",");
					sql.append(",");
				}
			}else{
				//map the nextval here
				if(!aMap.get(key).isReferenced()){
					String seqName =mappings.getMapForClass(aMap.get(key).getClassName()).getPkAttr().getSeqName();
					if(itr.hasNext())
						qMarks.append(seqName+".nextval,");
					else
						qMarks.append(seqName+".nextval");
				}
			}
		}
		return sql.append(")"+qMarks.toString()).toString();
	}
	
	/**
	 * Generates insert query for mapping
	 * @param mappings
	 * @param obj
	 * @param valueCount
	 * @return
	 */
	protected String getInsertQuerry(ORM_MAPPINGS mappings,Object obj,int valueCount){
		Maps dMap=mappings.getMapForClass(obj.getClass().getCanonicalName());
		StringBuilder sql=new StringBuilder();
		StringBuilder qMarks=new StringBuilder();
		sql.append("INSERT INTO ");
		sql.append(dMap.getSchemaName());
		sql.append(" (");
		qMarks.append("(");
		Map<String, Attributes> aMap=dMap.getAttributeMap();
		Iterator<String> iter=aMap.keySet().iterator();
		while(iter.hasNext()){
			String attr=iter.next();
			if(!aMap.get(attr).isFk()){
				sql.append(aMap.get(attr).getColName());
				if(aMap.get(attr).isGenerated()){
					qMarks.append(aMap.get(attr).getSeqName()+".nextval");
				}else{
					qMarks.append("?");
				}
				if(iter.hasNext()){
					qMarks.append(",");
					sql.append(",");
				}
			}else{
				if(!aMap.get(attr).isReferenced()){
					sql.append(aMap.get(attr).getColName());
					Attributes pk=mappings.getMapForClass(aMap.get(attr).getClassName()).getPkAttr();
					if(pk.isGenerated()){
						qMarks.append(pk.getSeqName()+".currval");
					}
					if(iter.hasNext()){
						qMarks.append(",");
						sql.append(",");
					}
				}
			}
		}
		if(qMarks.charAt(qMarks.length()-1)==','){
			qMarks.deleteCharAt(qMarks.length()-1);
		}
		if(sql.charAt(sql.length()-1)==','){
			sql.deleteCharAt(sql.length()-1);
		}
		qMarks.append(")");
		sql.append(")");
		sql.append(" VALUES ");
		/*
		 * this is only valid for mysql
		 * 
		 * for oracle insert all should be used
		 * 
		 * ************IMP*************
		 * It might not be the right place to mention this comment
		 * Following is really interesting
		 * Only inster all is not goin to cut it we need
		 * batch of insert allcheck out following link
		 * http://brianbontrager.blogspot.in/2013/05/of-sequences-insert-all-and-prepared.html
		 * 
		 */
		for(int i=0;i<valueCount;i++){
			sql.append(qMarks.toString());
			if(valueCount-1!=i)
				sql.append(",");
		}
		return sql.append("").toString();
	}
	/*
	 * changes required for complex primary key
	 */
	protected String getUpdateQuerry(Object obj,ORM_MAPPINGS mappings,int valueCount){
		Maps dMap=mappings.getMapForClass(obj.getClass().getCanonicalName());
		StringBuilder sql=new StringBuilder();
		StringBuilder qMarks=new StringBuilder();
		sql.append("UPDATE ");
		sql.append(dMap.getSchemaName());
		sql.append(" SET ");
		qMarks.append("(");
		Map<String, Attributes> aMap=dMap.getAttributeMap();
		Iterator<String> iter=aMap.keySet().iterator();
		while(iter.hasNext()){
			String attr=iter.next();
			if(!aMap.get(attr).isFk()){
				sql.append(aMap.get(attr).getColName()+"=? ");
				qMarks.append("?");
				if(iter.hasNext()){
					qMarks.append(",");
					sql.append(",");
				}
			}
		}
		if(sql.charAt(sql.length()-1)==','){
			sql.deleteCharAt(sql.length()-1);
		}
		qMarks.append(")");
		sql.append(" WHERE ");
		return sql.append(dMap.getPkAttr().getColName()+"="+"?").toString();
	}

	private String getQuerry(Object obj,ORM_MAPPINGS mappings,int type) throws Exception{
		String retval=null;
		switch(type){
		case 1:
			retval= getInsertQuerry(mappings, obj,1);
			break;
		case 2:
			retval= getUpdateQuerry(obj, mappings,1);
			break;
		default:
			throw new BlownException(EX.M10);
		}
		return retval;
	}

	protected PreparedStatement createInsertQuerry(ORM_MAPPINGS mappings,Object obj,Connection con) throws Exception{
		PreparedStatement stmt=null;
		String sql=null;
		if(obj instanceof List){
			List items=(List)obj;
			//Object dummy=items.get(0);
			sql=getQuerry(obj, mappings, 1);
			stmt=con.prepareStatement(sql);
			for(Object ob:items){
				Maps dMap=mappings.getMapForClass(obj.getClass().getCanonicalName());
				Map<String, Attributes> aMap=dMap.getAttributeMap();
				Iterator<String> itr=aMap.keySet().iterator();
				int count=1;
				while(itr.hasNext()){
					String attr=itr.next();
					if(!aMap.get(attr).isFk() && !aMap.get(attr).isGenerated()){
						Object ob1=ob.getClass().getMethod(getterForField(aMap.get(attr).getName()), null).invoke(ob, null);
						stmt.setObject(count, ob1);
						stmt.addBatch();
						count++;
					}
				}
			}
		}else{
			Maps dMap=mappings.getMapForClass(obj.getClass().getCanonicalName());
			Map<String, Attributes> aMap=dMap.getAttributeMap();
			sql=getQuerry(obj, mappings, 1);
			stmt=con.prepareStatement(sql);
			Iterator<String> itr=aMap.keySet().iterator();
			int count=1;
			while(itr.hasNext()){
				String attr=itr.next();
				if(!aMap.get(attr).isFk() && !aMap.get(attr).isGenerated()){
					Object ob1=obj.getClass().getMethod(getterForField(aMap.get(attr).getName()), null).invoke(obj, null);
					stmt.setObject(count, ob1);
					count++;
				}
			}
		}
		log(sql);
		return stmt;
	}

	protected PreparedStatement createUpadteQuerry(ORM_MAPPINGS mappings,Object obj,Connection con) throws Exception{
		Maps dMap=mappings.getMapForClass(obj.getClass().getCanonicalName());
		Map<String, Attributes> aMap=dMap.getAttributeMap();
		String sql=getQuerry(obj, mappings, 2);
		PreparedStatement stmt=con.prepareStatement(sql.toString());
		Iterator<String> itr=aMap.keySet().iterator();
		int count=1;
		while(itr.hasNext()){
			String attr=itr.next();
			if(!aMap.get(attr).isFk()){
				Object ob1=obj.getClass().getMethod(getterForField(aMap.get(attr).getName()), null).invoke(obj, null);
				stmt.setObject(count, ob1);
				count++;
			}
		}
		Object ob1=obj.getClass().getMethod(getterForField(dMap.getPkAttr().getName()), null).invoke(obj, null);
		if(ob1==null)
			throw new BlownException("PK attribute "+dMap.getPkAttr().getName()+" is null for "+dMap.getSchemaName());
		else
			stmt.setObject(count, ob1);
		log(sql.toString());
		return stmt;
	}

	private String getterForField(String field) {
		String retVal=(String.valueOf(field.charAt(0)).toUpperCase())+field.substring(1);
		return "get"+retVal;
	}

	protected void processParams(ORM_MAPPINGS mappings,StringBuffer sql,Map<String, ?> params2,String t,boolean useJoin,BlowParam blowParam) throws Exception {
		int index=0;
		sql.append(BlowConstatnts.SELECT);
		StringBuilder build=new StringBuilder();
		if(mappings.getMaps().get(t)==null){
			throw new BlownException(EX.M11+t);
		}
		if(blowParam!=null && blowParam.equals(BlowParam.LAZY))
			makeMapForAlieses(mappings.getMaps().get(t).getqMap(),build,true,mappings,t,null);
		else
			makeMapForAlieses(mappings.getMaps().get(t).getqMap(),build,false,mappings,t,null);
		String select=build.toString();
		sql.append(select.substring(0, select.length()-1)+BlowConstatnts.FROM+mappings.getMaps().get(t).getSchemaName());
		if(!params2.isEmpty()){
			Iterator<String> iter=params2.keySet().iterator();
			boolean check=true;
			if(blowParam!=null && blowParam.equals(BlowParam.LAZY)){

			}
			else{
				Iterator<String> itr=mappings.getMaps().get(t).getDependentClasses().iterator();
				while(itr.hasNext()){
					String dClz=itr.next();
					makeLOJs(mappings,t,dClz,sql,false);
				}
			}
			sql.append(BlowConstatnts.WHERE);
			int count=0;
			while(iter.hasNext()){
				String prop=iter.next();
				PropParam propParam=(PropParam)params2.get(prop);
				propParam.setIndex(++index);
				if(useJoin && check){				
					if(prop.contains(BlowConstatnts.DOT)){
						StringTokenizer tokens=new StringTokenizer(prop, BlowConstatnts.DOT);
						String finalSchema="";
						Map<String, Attributes> dMap=mappings.getMapForClass(t).getAttributeMap();
						while(tokens.hasMoreElements()){
							String token=tokens.nextToken();
							if(dMap.containsKey(token)){
								if(dMap.get(token).isFk()){
									finalSchema=mappings.getMapForClass(dMap.get(token).getClassName())
											.getSchemaName();
									dMap=mappings.getMapForClass(dMap.get(token).getClassName()).getAttributeMap();
								}else{
									if(count>0)
										sql.append(BlowConstatnts.AND);
									sql.append(finalSchema+BlowConstatnts.DOT+dMap.get(token).getColName());
									processParam(propParam, sql);
								}
							}else{
								throw new BlownException(EX.M12);
							}
						}
					}else{
						Attributes attr=mappings.getMaps().get(t).getAttributeMap().get(prop);
						if(attr==null)
							throw new BlownException("Property "+prop+" not present/mapped in class: "+t);
						if(attr.isFk())
							throw new BlownException("Property "+prop+" is complex type in class: "+t);
						if(count>0)
							sql.append(BlowConstatnts.AND);
						sql.append(BlowConstatnts.SPACE);
						sql.append(mappings.getMaps().get(t).getSchemaName());
						sql.append(BlowConstatnts.DOT);
						sql.append(attr.getColName());
						processParam(propParam, sql);
					}
				}
				else{
					sql.append(BlowConstatnts.AND);
					sql.append(BlowConstatnts.SPACE);
					sql.append(mappings.getMaps().get(t).getSchemaName());
					sql.append(BlowConstatnts.DOT);
					sql.append(mappings.getMaps().get(t).getAttributeMap().get(prop).getColName());
					processParam(propParam, sql);
				}
				count++;
			}
		}else{
			if(blowParam!=null && blowParam.equals(BlowParam.LAZY)){

			}
			else{
				Iterator<String> itr=mappings.getMaps().get(t).getDependentClasses().iterator();
				while(itr.hasNext()){
					makeLOJs(mappings, t, itr.next(), sql, false);
				}
			}
		}
	}

	private void processParam(PropParam param,StringBuffer sql){
		if(param.getParam().equals(BlowParam.EQ)){
			sql.append(BlowConstatnts.EQ);
			sql.append("?");
		}
		if(param.getParam().equals(BlowParam.GT)){
			sql.append(BlowConstatnts.GT);
			sql.append("?");
		}
		if(param.getParam().equals(BlowParam.GT_EQ)){
			sql.append(BlowConstatnts.GT_EQ);
			sql.append("?");
		}
		if(param.getParam().equals(BlowParam.LT)){
			sql.append(BlowConstatnts.LT);
			sql.append("?");
		}
		if(param.getParam().equals(BlowParam.LT_EQ)){
			sql.append(BlowConstatnts.LT_EQ);
			sql.append("?");
		}
		if(param.getParam().equals(BlowParam.LIKE_AROUND)){
			sql.append(BlowConstatnts._LIKE);
			sql.append("?");
		}
		if(param.getParam().equals(BlowParam.LIKE_FRONT)){
			sql.append(BlowConstatnts._LIKE);
			sql.append("?");
		}
		if(param.getParam().equals(BlowParam.LIKE_END)){
			sql.append(BlowConstatnts._LIKE);
			sql.append("?");
		}
	}
	
	
	/**
	 * Makes left outer join for associative attribute.
	 * P.S.:think twice before changing this function.
	 * @param mappings
	 * @param oldClass
	 * @param newClass
	 * @param sql
	 * @param flag
	 */
	private void makeLOJs(ORM_MAPPINGS mappings, String oldClass, String newClass,StringBuffer sql,boolean flag) {
		if(mappings.getMapForClass(newClass).getFkAttr().get(oldClass)!=null 
				&& mappings.getMapForClass(newClass).getFkAttr().get(oldClass).isReferenced() 
				&& !mappings.getMapForClass(oldClass).getFkAttr().get(newClass).isReferenced()){
			//reverse one to one mapping
			sql.append(BlowConstatnts.LOJ);
			sql.append(mappings.getMapForClass(newClass).getSchemaName());
			sql.append(BlowConstatnts.ON+mappings.getMaps().get(oldClass).getSchemaName());
			sql.append(BlowConstatnts.DOT);
			sql.append(mappings.getMapForClass(newClass).getFkAttr().get(oldClass).getColName());
			sql.append(BlowConstatnts.EQ);
			sql.append(mappings.getMapForClass(newClass).getSchemaName());
			sql.append(BlowConstatnts.DOT);
			sql.append(mappings.getMaps().get(newClass).getPkAttr().getColName());
		}else{
			sql.append(BlowConstatnts.LOJ);
			sql.append(mappings.getMapForClass(newClass).getSchemaName());
			sql.append(BlowConstatnts.ON+mappings.getMaps().get(oldClass).getSchemaName());
			sql.append(BlowConstatnts.DOT);
			sql.append(mappings.getMapForClass(oldClass).getPkAttr().getColName());
			sql.append(BlowConstatnts.EQ);
			sql.append(mappings.getMapForClass(newClass).getSchemaName());
			sql.append(BlowConstatnts.DOT);
			sql.append(mappings.getMaps().get(oldClass).getFkAttr().get(newClass).getColName());
		}
		if(mappings.getMapForClass(newClass).haveDependents()){
			for(String clz:mappings.getMapForClass(newClass).getDependentClasses()){
				if(!clz.equalsIgnoreCase(oldClass)){
					makeLOJs(mappings, newClass, clz, sql, true);
				}
			}
		}
	}
	private StringBuilder makeMapForAlieses(Map m,StringBuilder build,boolean isDone,ORM_MAPPINGS mappings,String t,String prevClass) throws BlownException {
		Iterator<String> itr=m.keySet().iterator();
		while(itr.hasNext()){
			String str=(String)itr.next();
			build.append(str+" as "+m.get(str)+",");
		}
		if(!isDone){
			Iterator<String> iter=mappings.getMaps().get(t).getDependentClassMap().keySet().iterator();
			while(iter.hasNext()){
				String str=(String)iter.next();
				if(mappings.getMaps().get(t).getDependentClassMap().get(str)!=null){
					Maps dependentMap=mappings.getMaps().get(t).getDependentClassMap().get(str);
					if(!str.equalsIgnoreCase(prevClass)){
						if(mappings.getMaps().get(t).getDependentClassMap().get(str).haveDependents())
							makeMapForAlieses(dependentMap.getqMap(),build,false,mappings,dependentMap.getClassName(),t);
						else
							makeMapForAlieses(dependentMap.getqMap(),build,true,mappings,dependentMap.getClassName(),t);
					}
				}else
					throw new BlownException(EX.M11+str);
			}
		}
		return build;
	}
	
	/**
	 * Creates a table for given map
	 * @param maps
	 * @return
	 */
	protected String createTableForClass(Maps maps){
		StringBuilder sql=new StringBuilder("CREATE TABLE ");
		sql.append(maps.getSchemaName());
		sql.append(BlowConstatnts.SPACE).append(BlowConstatnts.L_BRCKT);
		Iterator<String> itr=maps.getAttributeMap().keySet().iterator();
		while (itr.hasNext()) {
			String prop = itr.next();
			Attributes attr=maps.getAttributeMap().get(prop);
			if(!attr.isFk() && !attr.isReferenced()){
				sql.append(attr.getColName())
				.append(BlowConstatnts.SPACE)
				.append(SQLTypes.get(attr.getType()))
				.append(BlowConstatnts.L_BRCKT)
				.append(attr.getLength())
				.append(BlowConstatnts.R_BRCKT)
				.append(BlowConstatnts.SPACE)
				.append(BlowConstatnts.COMMA);
			}else if(attr.isReferenced()){
				//do nothing
			}else{
				
			}
		}
		sql.append(BlowConstatnts.PK)
			.append(BlowConstatnts.L_BRCKT)
			.append(maps.getPkAttr().getColName())
			.append(BlowConstatnts.R_BRCKT)
			.append(BlowConstatnts.R_BRCKT);
		return sql.toString();
	}
	
	/**
	 * Creates a sequence query for given name
	 * @param maps
	 * @return
	 */
	protected String createSequence(String seqName){
		return "CREATE SEQUENCE "+seqName+" START WITH 1 INCREMENT BY 1 CACHE 10";
	}
	
	/**
	 * Alter the given table and add provided attribute.
	 * @param attr
	 * @param tableName
	 * @return
	 */
	protected String alterAddColumn(Attributes attr, String tableName){
		StringBuilder sql=new StringBuilder("ALTER TABLE ");
		sql.append(tableName)
			.append(BlowConstatnts.SPACE)
			.append(BlowConstatnts.ADD)
			.append(BlowConstatnts.SPACE)
			.append(attr.getColName())
			.append(BlowConstatnts.SPACE)
			.append(SQLTypes.get(attr.getType()))
			.append(BlowConstatnts.L_BRCKT)
			.append(attr.getLength())
			.append(BlowConstatnts.R_BRCKT);
		return sql.toString();
	}
	
	/**
	 * Modify column in given table
	 * @param attr
	 * @param tableName
	 * @return
	 */
	protected String modifyColumn(Attributes attr, String tableName){
		StringBuilder sql=new StringBuilder("ALTER TABLE ");
		sql.append(tableName)
			.append(BlowConstatnts.SPACE)
			.append(BlowConstatnts.MODIFY)
			.append(BlowConstatnts.SPACE)
			.append(attr.getColName())
			.append(BlowConstatnts.SPACE)
			.append(SQLTypes.get(attr.getType()))
			.append(BlowConstatnts.L_BRCKT)
			.append(attr.getLength())
			.append(BlowConstatnts.R_BRCKT);
		return sql.toString();
	}
	
	protected static Map<String, Object> getQuerryCache(){
		return querryCache;
	}

	protected String processQuery(Query query,Map input) throws Exception{
		String sql=query.getContent().trim();
		for(Condition condition:query.getConditions()){
			if(input==null){
				throw new BlownException(EX.M22);
			}
			if(condition.getProp()!=null && condition.getProp().contains(".")){
				StringTokenizer splits=new StringTokenizer(condition.getProp(),".");
				String tkn=splits.nextToken();
				boolean isValidCondition=validateCondition(condition, getValueForToken(splits,input.get(tkn)));
				int otherId=condition.getId()+1;
				if(input.get(tkn)==null)
					throw new BlownException("Invalid input in query map '"+tkn+"' is not valid input.");
				if(isValidCondition && condition.getType().equals("WHEN")){
					sql=sql.replace(cDelim+condition.getId()+cDelim,condition.getContent().trim());
					sql=sql.replace(cDelim+otherId+cDelim,"");
				}else if(!isValidCondition && condition.getType().equals("WHEN")){
					sql=sql.replace(cDelim+condition.getId()+cDelim,"");
					sql=sql.replace(cDelim+otherId+cDelim,query.getConditionById(otherId).getContent().trim());
				}else if(!isValidCondition && condition.getType().equals("IF")){
					sql=sql.replace(cDelim+condition.getId()+cDelim,"");
				}else if(isValidCondition && condition.getType().equals("IF")){
					sql=sql.replace(cDelim+condition.getId()+cDelim,condition.getContent().trim());
				}
			}else{
				if(!condition.getType().equalsIgnoreCase("otherwise"))
					if(validateCondition(condition, input.get(condition.getProp()).toString()))
						sql=sql.replace(cDelim+condition.getId()+cDelim, condition.getContent().trim());
			}
		}
		query.setContent(sql.replaceAll(nxtLine, " ").replaceAll(tab, " "));
		sql=processQueryForTokens(query, input);
		return sql;
	}
	private boolean validateCondition(Condition condition,String value) throws BlownException{
		try{
			if(condition.getType().equalsIgnoreCase("otherwise"))
				return false;
			if(condition.getOperator().equals("NOTNULL")){
				return value!=null?(value.trim().equals("NULL")?false:true):false;
			}
			if(condition.getOperator().equals("GT")){
				if(!isNAN(condition.getValue()))
					if(getIntVal(value)>getIntVal(condition.getValue())){
						return true;
					}
			}
			if(condition.getOperator().equals("LT")){
				if(!isNAN(condition.getValue()))
					if(getIntVal(value)<getIntVal(condition.getValue())){
						return true;
					}
			}
			if(condition.getOperator().equals("GTEQ")){
				if(!isNAN(condition.getValue()))
					if(getIntVal(value)>getIntVal(condition.getValue())){
						return true;
					}
			}
			if(condition.getOperator().equals("LTEQ")){
				if(!isNAN(condition.getValue()))
					if(getIntVal(value)<getIntVal(condition.getValue())){
						return true;
					}
			}
			if(condition.getOperator().equals("EQ")){
				if(isNAN(condition.getValue())){
					if(value.equals(condition.getValue()))
						return true;
				}else
					if(getIntVal(value)==getIntVal(condition.getValue())){
						return true;
					}
			}
			if(condition.getOperator().equals("NOTEQ")){
				if(isNAN(condition.getValue())){
					if(value.equals(condition.getValue()))
						return false;
				}else
					if(getIntVal(value)!=getIntVal(condition.getValue())){
						return true;
					}
			}
		}catch(Exception e){
			throw new BlownException(EX.M13+condition.getProp()+" and value "+value);
		}
		return false;
	}

	private int getIntVal(String value){
		return Integer.valueOf(value);
	}
	
	private boolean isNAN(String val){
		try{
			Integer.valueOf(val);
			return false;
		}catch(Exception e){
			return true;
		}
	}
	
	private String processQueryForTokens(Query query,Map input) throws Exception{
		String sql=query.getContent().trim();
		if(sql.indexOf(hash)>0){
			for(String s:getTokens(sql,1)){
				if(s.contains(".")){
					StringTokenizer splits=new StringTokenizer(s,".");
					String tkn=splits.nextToken();
					if(input.get(tkn)==null)
						throw new BlownException("Invalid input in query map '"+tkn+"' is not valid input.");
					sql=sql.replace(hash+s+hash, getValueForToken(splits,input.get(tkn)));
				}else{
					if(input.get(s)==null)
						throw new BlownException("Invalid input in query map '"+s+"' is not valid input.");
					sql=sql.replace(hash+s+hash, input.get(s).toString());
				}
			}
		}
		return sql;
	}
	
	private String getValueForToken(StringTokenizer splits,Object obj) throws Exception {
		while(splits.hasMoreElements()){
			if(obj==null)
				return " NULL";
			obj=obj.getClass().getMethod(getterForField(splits.nextToken()), null).invoke(obj, null);
		}
		return obj.toString().trim();
	}
	
	private List<String> getTokens(String input,int type){
		String[] arr=null;
		if(type==1)
			arr=input.split(hash);
		else
			arr=input.split(cDelim);
		List<String> tokens=new ArrayList<String>();
		for(int i=0;i<arr.length;i++){
			if(i%2!=0)
				tokens.add(arr[i]);
		}
		return tokens;
	}
	
	protected void mapParamIndexes(Map<String, Object> oldMap,Map<String, Object> newMap){
		Iterator<String> itr=oldMap.keySet().iterator();
		while(itr.hasNext()){
			String key=itr.next();
			((PropParam)newMap.get(key)).setIndex(((PropParam)oldMap.get(key)).getIndex());
		}
	}
	protected void deleteEntity(ORM_MAPPINGS mappings, Maps map,Map<String, Object> params,List<String> queries, SessionContainer container,boolean processParam) throws BlownException {
		if(map.getCascades().isEmpty())
			delete(map, queries,params,processParam);
		else{
			for(String cascade:map.getCascades()){
				String type=map.getAttributeMap().get(cascade).getClassName();
				deleteEntity(mappings,mappings.getMapForClass(type),params,queries,container,false);
				delete(map, queries,params,processParam);
			}
		}
	}
	
	private void delete(Maps map,List<String> queries,Map<String, Object> params,boolean processParam) throws BlownException{
		StringBuffer sb=new StringBuffer("delete from ");
		sb.append(map.getSchemaName()+" where ");
		if(params.isEmpty() || !processParam){
			sb.append(map.getPkAttr().getColName()+"=?");
		}else{
			Iterator<String> itr=params.keySet().iterator();
			while(itr.hasNext()){
				PropParam pp=(PropParam)params.get(itr.next());
				if(map.getAttributeMap().get(pp.getProperty())==null)
					throw new BlownException("invalid attribute name "+pp.getProperty());
				sb.append(map.getAttributeMap().get(pp.getProperty()).getColName());
				processParam(pp, sb);
				if(itr.hasNext())
					sb.append(" AND ");
			}
		}
		if(!queries.contains(sb.toString()))
			queries.add(sb.toString());
	}
}