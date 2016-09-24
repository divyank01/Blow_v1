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

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sales.blow.annotations.BlowId;
import com.sales.blow.annotations.BlowProperty;
import com.sales.blow.annotations.BlowSchema;
import com.sales.blow.annotations.One2Many;
import com.sales.blow.annotations.One2One;
import com.sales.blow.exceptions.BlownException;
import com.sales.blow.exceptions.EX;
import com.sales.blow.exceptions.MappingsException;
import com.sales.constants.ConfigConstants;
import com.sales.core.DatabaseStateManager;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.DataBaseInfo.Table.Column;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS.Maps;
import com.sales.pools.ConnectionPool;
import com.sales.pools.OrmConfigParserPool;

import static com.sales.core.helper.LoggingHelper.log;
/**
 * 
 * @author Divyank Sharma
 * 
 * Its a poolable parser for the mappings.All the mappings/annotations will be parsed
 * and mappings will be loaded
 */
@SuppressWarnings("unchecked")
public class ORM_MAPPINGS_Parser {

	private ORM_MAPPINGS_Parser(){}

	private ORM_MAPPINGS orm_mapping;

	private int mappingIndex;

	private boolean useAnnotaion;

	private static ORM_MAPPINGS_Parser config;

	private ORM_CONFIG_Parser.ORM_CONFIG ormConfig;
	
	private static DataBaseInfo dataBaseInfo;
	
	private DatabaseStateManager stateManager;
	
	public synchronized static ORM_MAPPINGS_Parser getInstance(){
		return config;
	}

	static {
		log("Loading BLOW-ORM");
		if(config==null){
			config=new ORM_MAPPINGS_Parser();
			try {
				config.loadConfig();
			} catch (Exception e) {
				e.printStackTrace();
				BlownException ex=new BlownException(EX.M16+e.getMessage());
				ex.setStackTrace(e.getStackTrace());
				ex.printStackTrace();
			}
		}
	}

	private void loadConfig() throws Exception{					
		ORM_CONFIG_Parser configParser=OrmConfigParserPool.getInstance().borrowObject();
		ormConfig=configParser.getOrm_config();
		useAnnotaion=ormConfig.isUseAnnotations();
		if(useAnnotaion){
			List<String> annotationPackages=ormConfig.getPackagesToScan();
			for(int i=0;i<annotationPackages.size();i++){
				String pack=annotationPackages.get(i);
				File packageFile=new File(Thread.currentThread().
								getContextClassLoader().getResource(pack.replaceAll("\\.", Character.toString(File.separatorChar)).trim()).getFile());
				loadAnnotations(packageFile,pack);
			}
		}else{
			List<String> mappings=ormConfig.getMappingFiles();
			for(int i=0;i<mappings.size();i++){
				loadMappings(mappings.get(i));
			}
		}
		loadDependancies();
		if(ormConfig.isGenSchema()){
			if(dataBaseInfo==null){
				dataBaseInfo=DataBaseInfo.getInstance();
				dataBaseInfo.loadDataBaseInfo(config.ormConfig.getSchemas());
			}
			stateManager=DatabaseStateManager.getInstance();
			stateManager.syncSchema(orm_mapping, dataBaseInfo);
		}
		OrmConfigParserPool.getInstance().returnObject(configParser);
	}
	
	private String getPackageName(String filename){
		return filename.substring(0, filename.lastIndexOf("."));
	}
	
	private void loadAnnotations(File file,String packageName) throws MappingsException {
		for(File f:file.listFiles()){
			if(!f.isHidden() && f.getName().endsWith(".class")){
				try {
					Class clzz=Class.forName(packageName+"."+(f.getName().replaceAll("\\.class", "")));
					_loadAnnotations(clzz);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

	}


	private void _loadAnnotations(Class clzz) throws MappingsException {
		if(orm_mapping==null)
			orm_mapping=new ORM_MAPPINGS();	
		mappingIndex++;
		ORM_MAPPINGS.Maps orm_maps=orm_mapping.new Maps();
		if(clzz.isAnnotationPresent(BlowSchema.class)){
			BlowSchema ob=(BlowSchema) clzz.getAnnotation(BlowSchema.class);
			orm_maps.setClassName(clzz.getCanonicalName());
			orm_maps.setSchemaName(ob.schemaName());
		}
		doMapFieldsForAnnotations(clzz,orm_maps);
		if(clzz.getSuperclass()!=null)
			doMapFieldsForAnnotations(clzz.getSuperclass(),orm_maps);
		if(orm_maps.getPkAttr()==null)
			throw new MappingsException(EX.M17+orm_maps.className);

		orm_maps.setIndex(mappingIndex);
		orm_mapping.getMaps().put(orm_maps.getClassName(), orm_maps);
	}




	private void doMapFieldsForAnnotations(Class clzz, Maps orm_maps) throws MappingsException {
		for(Field field:clzz.getDeclaredFields()){//iteratings mapping 
			if(field.isAnnotationPresent(BlowProperty.class)){
				BlowProperty blowProp=(BlowProperty)field.getAnnotation(BlowProperty.class);				
				Maps.Attributes attr =  orm_maps.new Attributes();
				orm_maps.getAttributeMap().put(field.getName(),attr);
				attr.setColName(blowProp.columnName());
				attr.setLength(blowProp.length());
				attr.setName(field.getName());
				attr.setType(field.getType().getCanonicalName());
				if(field.isAnnotationPresent(BlowId.class)){
					BlowId prop=(BlowId)field.getAnnotation(BlowId.class);
					attr.setPk(true);
					orm_maps.setPkAttr(attr);
					attr.setGenerated(prop.generated());
					attr.setSeqName(prop.seq());
				}
				orm_maps.qMap.put(orm_maps.getSchemaName()+"."+blowProp.columnName(), 
						(field.getName().length()<10?
								field.getName():
									(field.getName().substring(0, 10)))+"_"+mappingIndex);
				/*orm_maps.qMap.put(attr.getName(), 
						(field.getName().length()<10?
								field.getName():
									(field.getName().substring(0, 10)))+"_"+mappingIndex);*/

			}
			if(field.isAnnotationPresent(One2One.class)){
				One2One one2OneProp=(One2One)field.getAnnotation(One2One.class);
				Maps.Attributes attr =  orm_maps.new Attributes();
				attr.setColName(one2OneProp.fk());
				String propName=field.getName();
				attr.setName(propName);
				attr.setAlias(propName.length()<10?propName+"_"+mappingIndex:propName.substring(0, 10)+"_"+mappingIndex);
				attr.setFk(true);
				attr.setClassName(field.getType().getCanonicalName());
				attr.setSupplimentryClass(orm_maps.getClassName());
				if(one2OneProp.fk()==null){
					throw new MappingsException(EX.M18+orm_maps.className);
				}
				attr.setReferenced(one2OneProp.isReferenced());
				orm_maps.getAttributeMap().put(propName,attr);
				orm_maps.getFkAttr().put(field.getType().getCanonicalName(), attr);
				orm_maps.getDependentClasses().add(field.getType().getCanonicalName());
			}
			if(field.isAnnotationPresent(One2Many.class)){
				//do one to many for annotations
				Maps.Attributes attr =  orm_maps.new Attributes();
				One2Many one2Many=(One2Many)field.getAnnotation(One2Many.class);
				attr.setColName(one2Many.fk());
				attr.setCollectionType(one2Many.collectionType());
				String propName=field.getName();
				attr.setName(propName);
				attr.setAlias(propName.length()<10?propName+"_"+mappingIndex:propName.substring(0, 10)+"_"+mappingIndex);
				attr.setFk(true);
				attr.setClassName(one2Many.type());
				attr.setSupplimentryClass(orm_maps.getClassName());
				attr.setRelation('M');
				if(one2Many.fk()==null){
					throw new MappingsException(EX.M19+orm_maps.className);
				}

				orm_maps.getAttributeMap().put(propName,attr);
				orm_maps.getFkAttr().put(attr.getClassName(), attr);
				if(!orm_maps.getDependentClasses().contains(one2Many.type()))
					orm_maps.getDependentClasses().add(one2Many.type());
			}
		}
	}

	protected void loadMappings(String fileLoc) throws Exception{
		Document doc=DocumentBuilderFactory.
				newInstance().
				newDocumentBuilder().
				parse(Thread.currentThread().
						getContextClassLoader()
						.getResourceAsStream(fileLoc));
		NodeList mappings=doc.getChildNodes().item(0).getChildNodes();//main node
		if(orm_mapping==null)
			orm_mapping=new ORM_MAPPINGS();	
		for(int i=0;i<mappings.getLength();i++){//iteratings mapping 			
			if(mappings.item(i).getNodeType()==Node.ELEMENT_NODE){
				mappingIndex++;
				ORM_MAPPINGS.Maps orm_maps=orm_mapping.new Maps();
				NodeList mapping=mappings.item(i).getChildNodes();
				for(int j=0;j<mapping.getLength();j++){
					if(mapping.item(j).getNodeType()==Node.ELEMENT_NODE){
						if(mapping.item(j).getNodeName().equalsIgnoreCase(ConfigConstants.CLASS))
							if(orm_mapping.maps.containsKey(mapping.item(j).getTextContent())){
								throw new MappingsException("Duplicate mappings found for"+mapping.item(j).getTextContent());
							}else
								orm_maps.setClassName(mapping.item(j).getTextContent());
						if(mapping.item(j).getNodeName().equalsIgnoreCase(ConfigConstants.RDBMS))
							orm_maps.setSchemaName(mapping.item(j).getTextContent());
						if(mapping.item(j).getNodeName().equalsIgnoreCase(ConfigConstants.MAP)){
							mapAttributes(mapping.item(j),orm_maps,mappingIndex);
						}

					}
				}
				orm_maps.setIndex(mappingIndex);
				orm_mapping.getMaps().put(orm_maps.getClassName(), orm_maps);
			}
		}
	}

	private void loadDependancies() throws Exception {
		Iterator<String> iter=orm_mapping.getMaps().keySet().iterator();
		while(iter.hasNext()){
			String clsToCheck=iter.next();
			Maps mapOfClass=orm_mapping.getMaps().get(clsToCheck);
			for(String s:mapOfClass.getDependentClasses())
				mapOfClass.getDependentClassMap().put(s, orm_mapping.getMapForClass(s));
		}
	}

	private void mapAttributes(Node item, Maps ormMaps,int indexCount) throws Exception {
		NodeList mapEntries=item.getChildNodes();
		boolean isPkSet=false;
		for(int i=0;i<mapEntries.getLength();i++){
			NamedNodeMap nodeMap=mapEntries.item(i).getAttributes();
			if(mapEntries.item(i).getNodeType()==Node.ELEMENT_NODE && mapEntries.item(i).getNodeName().equalsIgnoreCase(ConfigConstants.PROPERTY)){
				Maps.Attributes attr =  ormMaps.new Attributes();
				ormMaps.getAttributeMap().put(nodeMap.getNamedItem(ConfigConstants.NAME).getNodeValue(),attr);
				attr.setColName(nodeMap.getNamedItem(ConfigConstants.COL_NAME).getNodeValue());
				attr.setName(nodeMap.getNamedItem(ConfigConstants.NAME).getNodeValue());
				attr.setType(nodeMap.getNamedItem(ConfigConstants.TYPE).getNodeValue());
				attr.setRelation('N');
				if(nodeMap.getNamedItem(ConfigConstants.LEN)!=null){
					attr.setLength(Integer.valueOf(nodeMap.getNamedItem(ConfigConstants.LEN).getNodeValue()));
				}
				if(nodeMap.getNamedItem(ConfigConstants.GENERATED)!=null){
					boolean isGenerated=Boolean.valueOf(nodeMap.getNamedItem(ConfigConstants.GENERATED).getNodeValue());
					attr.setGenerated(isGenerated);
					if(isGenerated){
						attr.setSeqName(nodeMap.getNamedItem(ConfigConstants.SEQ).getNodeValue());
					}
				}
				if(nodeMap.getNamedItem(ConfigConstants.PK)!=null){
					attr.setPk(true);
					ormMaps.setPkAttr(attr);
					isPkSet=true;
				}
				String nameVal=nodeMap.getNamedItem(ConfigConstants.NAME).getNodeValue();
				ormMaps.qMap.put(ormMaps.getSchemaName()+"."+mapEntries.item(i).getAttributes().getNamedItem(ConfigConstants.COL_NAME).getNodeValue(), 
						(nameVal.length()<10?nameVal:nameVal.substring(0, 10))+"_"+indexCount);
				/*ormMaps.qMap.put(attr.getName(), 
						(nameVal.length()<10?nameVal:nameVal.substring(0, 10))+"_"+indexCount);*/
			}
			if(mapEntries.item(i).getNodeType()==Node.ELEMENT_NODE && mapEntries.item(i).getNodeName().equalsIgnoreCase(ConfigConstants.ONE_2_ONE)){
				Maps.Attributes attr =  ormMaps.new Attributes();
				attr.setColName(nodeMap.getNamedItem(ConfigConstants.FK).getNodeValue());
				String propName=nodeMap.getNamedItem(ConfigConstants.NAME).getNodeValue();
				if(nodeMap.getNamedItem(ConfigConstants.FK_REF)!=null){
					attr.setReferenced(Boolean.valueOf(nodeMap.getNamedItem(ConfigConstants.FK_REF).getNodeValue()));
				}else
					attr.setReferenced(false);
				attr.setName(propName);
				attr.setAlias(propName.length()<10?propName+"_"+indexCount:propName.substring(0, 10)+"_"+indexCount);
				attr.setFk(true);
				attr.setClassName(nodeMap.getNamedItem(ConfigConstants.REF_CLASS).getNodeValue());
				attr.setSupplimentryClass(ormMaps.getClassName());
				if(nodeMap.getNamedItem(ConfigConstants.FK)==null){
					throw new MappingsException(EX.M18+ormMaps.className);
				}
				if(nodeMap.getNamedItem(ConfigConstants.CASCADE)!=null){
					attr.setCascade(nodeMap.getNamedItem(ConfigConstants.CASCADE).getNodeValue());
					ormMaps.getCascades().add(propName);
				}
				attr.setRelation('O');
				ormMaps.getAttributeMap().put(nodeMap.getNamedItem(ConfigConstants.NAME).getNodeValue(),attr);
				ormMaps.getFkAttr().put(nodeMap.getNamedItem(ConfigConstants.REF_CLASS).getNodeValue(), attr);
				ormMaps.getDependentClasses().add(nodeMap.getNamedItem(ConfigConstants.REF_CLASS).getNodeValue());
			}
			if(mapEntries.item(i).getNodeType()==Node.ELEMENT_NODE && mapEntries.item(i).getNodeName().equalsIgnoreCase(ConfigConstants.ONE_2_MANY)){
				Maps.Attributes attr =  ormMaps.new Attributes();
				attr.setColName(nodeMap.getNamedItem(ConfigConstants.FK).getNodeValue());
				attr.setCollectionType(nodeMap.getNamedItem("collectionType").getNodeValue());
				String propName=nodeMap.getNamedItem(ConfigConstants.NAME).getNodeValue();
				attr.setName(propName);
				attr.setAlias(propName.length()<10?propName+"_"+indexCount:propName.substring(0, 10)+"_"+indexCount);
				attr.setFk(true);
				attr.setClassName(nodeMap.getNamedItem(ConfigConstants.REF_CLASS).getNodeValue());
				attr.setSupplimentryClass(ormMaps.getClassName());
				attr.setRelation('M');
				if(nodeMap.getNamedItem(ConfigConstants.FK)==null){
					throw new MappingsException(EX.M19+ormMaps.className);
				}
				if(nodeMap.getNamedItem(ConfigConstants.CASCADE)!=null){
					attr.setCascade(nodeMap.getNamedItem(ConfigConstants.CASCADE).getNodeValue());
					ormMaps.getCascades().add(propName);
				}
				if(nodeMap.getNamedItem(ConfigConstants.FK_REF)!=null){
					attr.setReferenced(Boolean.valueOf(nodeMap.getNamedItem(ConfigConstants.FK_REF).getNodeValue()));
				}else
					attr.setReferenced(false);
				ormMaps.getAttributeMap().put(nodeMap.getNamedItem(ConfigConstants.NAME).getNodeValue(),attr);
				ormMaps.getFkAttr().put(nodeMap.getNamedItem(ConfigConstants.REF_CLASS).getNodeValue(), attr);
				if(!ormMaps.getDependentClasses().contains(nodeMap.getNamedItem(ConfigConstants.REF_CLASS).getNodeValue()))
					ormMaps.getDependentClasses().add(nodeMap.getNamedItem(ConfigConstants.REF_CLASS).getNodeValue());
			}
		}
		if(!isPkSet)
			throw new MappingsException(EX.M17+ormMaps.className);
	}

	/**
	 * 
	 * @author Divyank
	 * 
	 */
	@Deprecated
	public static final void excute(){
		try {
			config.loadConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static final class DataBaseInfo{
		
		private static DataBaseInfo _dataBaseInfo;
		
		private DataBaseInfo(){}
		
		static{
			if(_dataBaseInfo==null)
				_dataBaseInfo=new DataBaseInfo();
		}
		protected static DataBaseInfo getInstance(){
			return DataBaseInfo._dataBaseInfo;
		}
		
		private Map<String,Table> tables=new HashMap<String, Table>();
		
		private List<String> sequences=new ArrayList<String>();
		
		protected void loadDataBaseInfo(List<String> schemas)throws Exception{
			Connection con=ConnectionPool.getInstance().borrowObject();
			DatabaseMetaData meta=con.getMetaData();
			ResultSet _columns=null;
			ResultSet _seq=null;
			try{
				for(String schema:schemas){
					_columns=meta.getColumns(null,schema,null,null);
					while(_columns.next()){
						String tableName=_columns.getString(3).toUpperCase();
						String columnName=_columns.getString(4).toUpperCase();
						int columnNo=_columns.getInt(17);
						int size = _columns.getInt("COLUMN_SIZE");
						if(!tables.containsKey(tableName)){
							Table tab=new Table();
							tab.setTableName(tableName);
							Column column = tab.new Column();
							column.setColumnName(columnName);
							column.setColumnNo(columnNo);
							column.setLength(size);
							tab.getColumns().put(columnName, column);
							tables.put(tableName, tab);
						}else{
							Table tab=tables.get(tableName);
							Column column = tab.new Column();
							column.setColumnName(columnName);
							column.setColumnNo(columnNo);
							column.setLength(size);
							tab.getColumns().put(columnName, column);
							tables.put(tableName, tab);
						}
					}
					String[] seq= {"SEQUENCE"};
					_seq=meta.getTables(null,schema,null,seq);
					while(_seq.next()){
						sequences.add(_seq.getString(3).toUpperCase());
					}
				}
			}finally{
				_seq.close();
				_columns.close();
				ConnectionPool.getInstance().returnObject(con);
			}
		}
		
		public final class Table{
			private Map<String,Column> columns=new HashMap<String, Column>();
			private String tableName;
			public final class Column{
				private String columnName;
				private int columnNo;
				private String type;
				private int length;
				public String getColumnName() {
					return columnName;
				}
				public void setColumnName(String columnName) {
					this.columnName = columnName;
				}
				public int getColumnNo() {
					return columnNo;
				}
				public void setColumnNo(int columnNo) {
					this.columnNo = columnNo;
				}
				public String getType() {
					return type;
				}
				public void setType(String type) {
					this.type = type;
				}
				public int getLength() {
					return length;
				}
				public void setLength(int length) {
					this.length = length;
				}
			}
			public Map<String, Column> getColumns() {
				return columns;
			}
			public void setColumns(Map<String, Column> columns) {
				this.columns = columns;
			}
			public String getTableName() {
				return tableName;
			}
			public void setTableName(String tableName) {
				this.tableName = tableName;
			}
		}

		public Map<String, Table> getTables() {
			return tables;
		}

		public List<String> getSequences() {
			return sequences;
		}
	}

	/**
	 * 
	 * @author Divyank
	 *
	 *
	 */
	public final class ORM_MAPPINGS{
		private Map<String, Maps> maps=new HashMap<String, Maps>();
		public Map<String, Maps> getMaps() {
			return maps;
		}
		public ORM_MAPPINGS(){

		}

		public Maps getMapForClass(String canonicalName){
			return maps.get(canonicalName);
		}

		public Maps getMapForSchemaName(String schemaName){
			Iterator<String> itr=maps.keySet().iterator();
			Maps map=null;
			while(itr.hasNext()){
				String classNames=itr.next();
				if(maps.get(classNames).getSchemaName().equalsIgnoreCase(schemaName)){
					map=maps.get(classNames);
					break;
				}
			}
			return map;
		}
		public DataBaseInfo getDataBaseInfo() {
			return dataBaseInfo;
		}
				/**
		 * 
		 * @author Divyank Sharma
		 * 
		 * It will represent map for the classes
		 *
		 */
		public final class Maps{
			private String className;
			private String schemaName;
			private int index;
			private Map<String, Attributes> attributeMap=new HashMap<String, Attributes>();
			private Map<String,Maps> dependentClassMap=new HashMap<String,Maps>();
			private List<String> dependentClasses=new ArrayList<String>();
			private boolean dependenciesSatisfied;
			private Map<String, String> qMap=new HashMap<String, String>();
			private Attributes pkAttr;
			private Map<String, Attributes> fkAttr=new HashMap<String, Attributes>();
			private List<String> cascades=new ArrayList<String>();
			public boolean haveDependents(){
				if(!fkAttr.isEmpty())
					return true;
				return false;
			}

			public String getClassName() {
				return className;
			}
			public String getSchemaName() {
				return schemaName;
			}
			public Map<String, Attributes> getAttributeMap() {
				return attributeMap;
			}
			protected void setClassName(String className) {
				this.className = className;
			}
			protected void setSchemaName(String schemaName) {
				this.schemaName = schemaName;
			}
			protected void setAttributeMap(Map<String, Attributes> attributeMap) {
				this.attributeMap = attributeMap;
			}
			public Map<String, Maps> getDependentClassMap() {
				return dependentClassMap;
			}
			protected void setDependentClassMap(Map<String, Maps> dependentClassMap) {
				this.dependentClassMap = dependentClassMap;
			}
			public boolean isDependenciesSatisfied() {
				return dependenciesSatisfied;
			}
			protected void setDependenciesSatisfied(boolean dependenciesSatisfied) {
				this.dependenciesSatisfied = dependenciesSatisfied;
			}
			public List<String> getDependentClasses() {
				return dependentClasses;
			}
			protected void setDependentClasses(List<String> dependentClasses) {
				this.dependentClasses = dependentClasses;
			}
			public class Attributes{
				private String name;
				private String colName;
				private boolean isPk;
				private boolean isFk;
				private String alias;
				private String className;
				private String supplimentryClass;
				private String type;
				private String collectionType;
				private char relation;
				private boolean generated;
				private String seqName;
				private boolean isReferenced;
				private int length;
				private String cascade;
				public String getName() {
					return name;
				}
				public void setName(String name) {
					this.name = name;
				}
				public String getColName() {
					return colName;
				}
				public void setColName(String colName) {
					this.colName = colName;
				}
				public boolean isPk() {
					return isPk;
				}
				public void setPk(boolean isPk) {
					this.isPk = isPk;
				}
				public boolean isFk() {
					return isFk;
				}
				public void setFk(boolean isFk) {
					this.isFk = isFk;
				}
				public String getAlias() {
					return alias;
				}
				public void setAlias(String alias) {
					this.alias = alias;
				}
				public String getClassName() {
					return className;
				}
				public void setClassName(String className) {
					this.className = className;
				}
				public String getSupplimentryClass() {
					return supplimentryClass;
				}
				public void setSupplimentryClass(String supplimentryClass) {
					this.supplimentryClass = supplimentryClass;
				}
				public String getType() {
					return type;
				}
				public void setType(String type) {
					this.type = type;
				}
				public void setCollectionType(String collectionType) {
					this.collectionType = collectionType;
				}
				public String getCollectionType() {
					return collectionType;
				}
				public char getRelation() {
					return relation;
				}
				public void setRelation(char relation) {
					this.relation = relation;
				}
				public boolean isGenerated() {
					return generated;
				}
				public void setGenerated(boolean generated) {
					this.generated = generated;
				}
				public String getSeqName() {
					return seqName;
				}
				public void setSeqName(String seqName) {
					this.seqName = seqName;
				}
				public boolean isReferenced() {
					return isReferenced;
				}
				public void setReferenced(boolean isReferenced) {
					this.isReferenced = isReferenced;
				}
				public int getLength() {
					return length;
				}
				public void setLength(int length) {
					this.length = length;
				}
				public String getCascade() {
					return cascade;
				}
				public void setCascade(String cascade) {
					this.cascade = cascade;
				}
			}
			public Attributes getPkAttr() {
				return pkAttr;
			}
			public void setPkAttr(Attributes pkAttr) {
				this.pkAttr = pkAttr;
			}
			public Map<String,Attributes> getFkAttr() {
				return fkAttr;
			}
			public void setFkAttr(Map<String,Attributes> fkAttr) {
				this.fkAttr = fkAttr;
			}

			public Map<String, String> getqMap() {
				return qMap;
			}

			public void setqMap(Map<String, String> qMap) {
				this.qMap = qMap;
			}

			public int getIndex() {
				return index;
			}

			public void setIndex(int index) {
				this.index = index;
			}

			public List<String> getCascades() {
				return cascades;
			}

			public void setCascades(List<String> cascades) {
				this.cascades = cascades;
			}

		}
	}
	public ORM_MAPPINGS getOrm_Mappings() {
		return orm_mapping;
	}

}
