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
package com.sales.poolable.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sales.blow.exceptions.BlownException;
import com.sales.blow.exceptions.EX;
import com.sales.constants.ConfigConstants;
import com.sales.poolable.parsers.ORM_QUERY_Parser.Queries.Query.Condition;
import com.sales.pools.ObjectPool;
import com.sales.pools.OrmConfigParserPool;

public class ORM_QUERY_Parser {
	
	private static final String cDelim="~@~";
	
	private static final String iDelim="#@#";
	
	
	private ORM_QUERY_Parser(){}
	
	private static ORM_QUERY_Parser parser;
	
	private ORM_QUERY_Parser.Queries queries=new Queries();
	
	
	
	static{
		parser=new ORM_QUERY_Parser();
		try {
			parser.load();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static ORM_QUERY_Parser getInstance(){
		if(parser==null)
			parser=new ORM_QUERY_Parser();
		return parser;
	}
	
	private void load() throws Exception{
		ORM_CONFIG_Parser config_Parser=ObjectPool.getConfig();
		List<String> qList = config_Parser.getOrm_config().getQueries();
		for (int i = 0; i < qList.size(); i++) {
			_load(qList.get(i));
		}
		queries.shuffle();
		ObjectPool.submit(config_Parser);
	}
	
	
	private void _load(String string) throws Exception {
		Document document=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(Thread.currentThread().
				getContextClassLoader()
				.getResourceAsStream(string));
		Node node=document.getFirstChild();
		NodeList nodeList=node.getChildNodes();
		int cntr=0;
		for(int i=0;i<nodeList.getLength();i++){
			Node nodes=nodeList.item(i);
			if(nodes.getNodeName().equalsIgnoreCase(ConfigConstants.Q_INCLUDE)){
				queries.getIncludes().put(nodes.getAttributes().getNamedItem("name").getNodeValue(), nodes.getTextContent());
			}
			if(nodes.getNodeName().equalsIgnoreCase(ConfigConstants.QUERY)){
				NodeList nList=nodes.getChildNodes();
				StringBuilder builder=new StringBuilder();
				Queries.Query qry=queries.new Query();
				for(int j=0;j<nList.getLength();j++){
					Node nod=nList.item(j);
					if(nod.getNodeType()==3){
						builder.append(nod.getTextContent().trim());
					}
					if(nod.getNodeType()==1 && nod.getNodeName().equals("Include")){
						builder.append(iDelim).append(nod.getAttributes().getNamedItem("ref").getNodeValue()).append(iDelim);
						qry.getIncludes().add(nod.getAttributes().getNamedItem("ref").getNodeValue());
					}
					if(nod.getNodeType()==1 && nod.getNodeName().equals("condition")){
						NodeList nl=nod.getChildNodes();
						for(int k=0;k<nl.getLength();k++){
							Node nod1=nl.item(k);
							if(nod1.getNodeType()==1 && nod1.getNodeName().equals("NotNull")){
								builder.append(cDelim).append(cntr).append(cDelim);
								Condition condition=qry.new Condition();
								condition.setId(cntr);
								condition.setOperator("NOTNULL");
								condition.setProp(nod1.getAttributes().getNamedItem("prop").getNodeValue());
								condition.setType("NOT_NULL");
								condition.setContent(nod1.getTextContent());
								qry.getConditions().add(condition);
								cntr++;
							}
							if(nod1.getNodeType()==1 && nod1.getNodeName().equals("when")){
								builder.append(cDelim).append(cntr).append(cDelim);
								Condition condition=qry.new Condition();
								condition.setOperator(nod1.getAttributes().getNamedItem("operator").getNodeValue());
								condition.setId(cntr);
								condition.setProp(nod1.getAttributes().getNamedItem("prop").getNodeValue());
								condition.setType("WHEN");
								condition.setContent(nod1.getTextContent());
								condition.setValue(nod1.getAttributes().getNamedItem("value").getNodeValue());
								qry.getConditions().add(condition);
								cntr++;
							}
							if(nod1.getNodeType()==1 && nod1.getNodeName().equals("otherwise")){
								builder.append(cDelim).append(cntr).append(cDelim);
								Condition condition=qry.new Condition();
								condition.setId(cntr);
								condition.setType("OTHERWISE");
								condition.setWhen(qry.getConditions().get(qry.getConditions().size()-1));
								condition.setContent(nod1.getTextContent());
								qry.getConditions().add(condition);
								cntr++;
							}
							if(nod1.getNodeType()==1 && nod1.getNodeName().equals("if")){
								builder.append(cDelim).append(cntr).append(cDelim);
								Condition condition=qry.new Condition();
								condition.setId(cntr);
								condition.setOperator(nod1.getAttributes().getNamedItem("operator").getNodeValue());
								condition.setProp(nod1.getAttributes().getNamedItem("prop").getNodeValue());
								condition.setType("IF");
								condition.setValue(nod1.getAttributes().getNamedItem("value").getNodeValue());
								condition.setContent(nod1.getTextContent());
								qry.getConditions().add(condition);
								cntr++;
							}
						}
					}
				}
				qry.setContent(builder.toString());
				qry.setClassName(nodes.getAttributes().getNamedItem("class").getNodeValue());
				qry.setMappingObjName(nodes.getAttributes().getNamedItem("mapping-object").getNodeValue());
				queries.getQueries().put(nodes.getAttributes().getNamedItem("name").getNodeValue(), qry);
			}
			if(nodes.getNodeName().equalsIgnoreCase(ConfigConstants.Q_OBJECT)){
				NodeList nList=nodes.getChildNodes();
				Queries.MappingObject object=queries.new MappingObject();
				//object.setProperties(new HashMap<String, String>());
				String classname=nodes.getAttributes().getNamedItem("className").getNodeValue();
				if(classname.isEmpty())
					throw new BlownException(EX.M20);
				object.setClassName(classname);
				object.setName(nodes.getAttributes().getNamedItem("name").getNodeValue());
				for(int j=0;j<nList.getLength();j++){
					if(nList.item(j).getNodeName().equalsIgnoreCase(ConfigConstants.Q_PROP)){
						if(nList.item(j).getAttributes().getNamedItem("column")!=null)
							object.getProperties().put(nList.item(j).getAttributes().getNamedItem("attr").getNodeValue(),
									nList.item(j).getAttributes().getNamedItem("column").getNodeValue());
						else{
							Queries.MappingObject obj=queries.new MappingObject();
							obj.setName(nList.item(j).getAttributes().getNamedItem("mapping-object").getNodeValue());
							object.getProperties().put(nList.item(j).getAttributes().getNamedItem("attr").getNodeValue(),
									obj);
							
						}
					}
				}
				queries.getMappingObjects().put(nodes.getAttributes().getNamedItem("name").getNodeValue(), object);
			}
		}
	}


	public final class Queries{
		private Map<String, String> includes=new HashMap<String, String>();
		private Map<String, Query> queries=new HashMap<String, Query>();
		private Map<String, MappingObject> mappingObjects=new HashMap<String, MappingObject>();
		public class MappingObject{
			private Map<String,Object> properties=new HashMap<String, Object>();
			private String className;
			private String name;
			public Map<String, Object> getProperties() {
				return properties;
			}

			public void setProperties(Map<String, Object> properties) {
				this.properties = properties;
			}

			public String getClassName() {
				return className;
			}

			public void setClassName(String className) {
				this.className = className;
			}

			public String getName() {
				return this.name;
			}

			public void setName(String name) {
				this.name = name;
			}
			
			public int hashCode(){
				return className.hashCode()+name.hashCode();
			}
			
			
		}
		
		public class Query{
			private List<String> includes=new ArrayList<String>();
			private List<Condition> conditions=new ArrayList<Condition>();
			private String content;
			private String className;
			private MappingObject mappingObject;
			private String mappingObjName;
			public List<String> getIncludes() {
				return includes;
			}
			public void setIncludes(List<String> includes) {
				this.includes = includes;
			}
			public String getContent() {
				return content;
			}
			public void setContent(String content) {
				this.content = content;
			}
			public String getClassName() {
				return className;
			}
			public void setClassName(String className) {
				this.className = className;
			}
			public MappingObject getMappingObject() {
				return mappingObject;
			}
			public void setMappingObject(MappingObject mappingObject) {
				this.mappingObject = mappingObject;
			}
			public String getMappingObjName() {
				return mappingObjName;
			}
			public void setMappingObjName(String mappingObjName) {
				this.mappingObjName = mappingObjName;
			}
			public List<Condition> getConditions() {
				return conditions;
			}
			public void setConditions(List<Condition> conditions) {
				this.conditions = conditions;
			}
			
			public Condition getConditionById(int id){
				Condition c=null;
				if(id<conditions.size()-1)
					c=conditions.get(id);
				if(c!=null && c.getId()==id)
					return c;
				else{
					for(Condition condi:conditions){
						if(condi.getId()==id)
							return condi;
					}
				}
				return c;
			}
			
			public class Condition{
				private String type;
				private String value;
				private String operator;
				private String prop;
				private int id;
				private String content;
				private boolean isWhen;
				private Condition when;
				public String getValue() {
					return value;
				}
				public void setValue(String value) {
					this.value = value;
				}
				public String getOperator() {
					return operator;
				}
				public void setOperator(String operator) {
					this.operator = operator;
				}
				public String getProp() {
					return prop;
				}
				public void setProp(String prop) {
					this.prop = prop;
				}
				public String getType() {
					return type;
				}
				public void setType(String type) {
					this.type = type;
				}
				public int getId() {
					return id;
				}
				public void setId(int id) {
					this.id = id;
				}
				public String getContent() {
					return content;
				}
				public void setContent(String content) {
					this.content = content;
				}
				public boolean isWhen() {
					return isWhen;
				}
				public void setWhen(boolean isWhen) {
					this.isWhen = isWhen;
				}
				public Condition getWhen() {
					return when;
				}
				public void setWhen(Condition when) {
					this.when = when;
				}
			}
		}
		
		public Map<String, String> getIncludes() {
			return includes;
		}
		public Map<String, Query> getQueries() {
			return queries;
		}
		protected void shuffle(){
			Iterator<String> itr=queries.keySet().iterator();
			while(itr.hasNext()){
				String qName=itr.next();
				Query qry=queries.get(qName);
				qry.setMappingObject(parser.queries.getMappingObjects().get(qry.getMappingObjName()));
				Iterator<String> it=getMappingObjects().keySet().iterator();
				while(it.hasNext()){
					MappingObject mObject=getMappingObjects().get(it.next());
					Iterator<String> inIt=mObject.getProperties().keySet().iterator();
					while(inIt.hasNext()){
						String propName=inIt.next(); 
						Object ob=mObject.getProperties().get(propName);
						if(ob instanceof MappingObject){
							mObject.getProperties().put(propName, getMappingObjects().get(((MappingObject)ob).getName()));
						}
					}
				}
				List<String> inclds= qry.getIncludes();
				for(int i=0;i<inclds.size();i++){
					if(qry.getContent()!=null && qry.getContent().indexOf(iDelim)>=0){
						qry.setContent(processQuery(qry.getContent()));
					}
				}
			}
		}
		
		private String processQuery(String input){
			StringBuilder builder=new StringBuilder();			
			int startIndex =input.indexOf(iDelim);
			String start=input.substring(0,startIndex);
			int lastIndex=startIndex+3+input.substring(startIndex+3).indexOf(iDelim)+3;
			String retVal=builder.append(start)
								 .append(" ")
								 .append(includes.get(input.substring(startIndex, lastIndex).replaceAll(iDelim, "")))
								 .append(" ")
								 .append(input.substring(lastIndex,input.length())).toString();
			if(retVal.indexOf(iDelim)>=0)
				processQuery(retVal);
			return retVal;
		}
		public Map<String, MappingObject> getMappingObjects() {
			return mappingObjects;
		}
		public void setMappingObjects(Map<String, MappingObject> mappingObjects) {
			this.mappingObjects = mappingObjects;
		}
	}


	public ORM_QUERY_Parser.Queries.Query getQueries(String id) {
		return queries.getQueries().get(id);
	}
	
}
