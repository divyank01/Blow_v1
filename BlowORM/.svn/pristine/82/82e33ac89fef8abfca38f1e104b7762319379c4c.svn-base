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
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sales.blow.exceptions.BlownException;
import com.sales.constants.ConfigConstants;



/**
 * 
 * @author Divyank Sharma
 * Parser for orm configurations.
 *
 */
public final class ORM_CONFIG_Parser {
	
	private static final String CONFIG_FILE_NAME = "BLOW-ORM-CONFIG.xml";

	private ORM_CONFIG_Parser(){}
	
	private ORM_CONFIG orm_config;
	
	private static ORM_CONFIG_Parser config;
	
	public static ORM_CONFIG_Parser getInstance(){
		return config;
	}
	
	static{
		if(config==null){
			config=new ORM_CONFIG_Parser();
			try {
				config.loadConfig();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loadConfig() throws Exception{
		String pwd = null;
		String userName = null;
		String url = null;
		boolean genSchema=false;
		List<String> mappings=new ArrayList<String>();
		List<String> packagesToScan=new ArrayList<String>();
		List<String> schemas=new ArrayList<String>();
		List<String> queries=new ArrayList<String>();
		String driver=null;
		boolean logging=false;
		boolean useAnnotations=false;
		Document doc=DocumentBuilderFactory.
						newInstance().
						newDocumentBuilder().
						parse(Thread.currentThread().
								getContextClassLoader()
						        .getResourceAsStream(CONFIG_FILE_NAME));
		NodeList nl=doc.getChildNodes().item(0).getChildNodes();

		for(int i=0;i<nl.getLength();i++){
			Node node=nl.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE){
				if(node.getNodeName().equalsIgnoreCase(ConfigConstants.URL))
					url=node.getTextContent();
				if(node.getNodeName().equalsIgnoreCase(ConfigConstants.DRIVER))
					driver=node.getTextContent();
				if(node.getNodeName().equalsIgnoreCase(ConfigConstants.PASSWORD))
					pwd=node.getTextContent();
				if(node.getNodeName().equalsIgnoreCase(ConfigConstants.USERNAME))
					userName=node.getTextContent();
				if(node.getNodeName().equalsIgnoreCase(ConfigConstants.MAPPINGS)){
					mappings.add(node.getAttributes().getNamedItem(ConfigConstants.MAPPINGS_FILE).getNodeValue());
				}
				if(node.getNodeName().equalsIgnoreCase(ConfigConstants.SCHEMAS)){
					StringTokenizer tokens=new StringTokenizer(node.getTextContent(), ",");
					while(tokens.hasMoreElements())
						schemas.add(tokens.nextToken());
				}
				if(node.getNodeName().equalsIgnoreCase(ConfigConstants.ANNOTATIONS)){
					useAnnotations=Boolean.valueOf(node.getAttributes().getNamedItem(ConfigConstants.ANNOTATION_USE).getNodeValue());
					if(useAnnotations){
						getAnnotations(packagesToScan,node);
					}
				}
				if(node.getNodeName().equalsIgnoreCase(ConfigConstants.GEN_SCHEMA)){
					genSchema=Boolean.valueOf(node.getTextContent());
				}
				if(node.getNodeName().equalsIgnoreCase(ConfigConstants.QUERIES)){
					NodeList nodeList=node.getChildNodes();
					for(int j=0;j<nodeList.getLength();j++){
						if(nodeList.item(j).getNodeName().equalsIgnoreCase(ConfigConstants.QUERY)){
							queries.add(nodeList.item(j).getAttributes().getNamedItem(ConfigConstants.Q_FILE).getNodeValue());
						}
					}
				}
				if(node.getNodeName().equalsIgnoreCase(ConfigConstants.SQL_LOGGING)){
					logging=Boolean.valueOf(node.getTextContent());
				}
			}
		}
		if(userName==null||pwd==null||url==null||driver==null){
			throw new BlownException("Not enough information to connect to databse");
		}
		orm_config=new ORM_CONFIG(userName, pwd, url,useAnnotations,packagesToScan,mappings,schemas,queries);
		orm_config.setGenSchema(genSchema);
		orm_config.setDriver(driver);
		orm_config.setLoggingEnabled(logging);
	}
	
	private void getAnnotations(List<String> packagesToScan,Node nod) {
		NodeList nl=nod.getChildNodes();
		for(int i=0;i<nl.getLength();i++){
			Node node=nl.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE){
				if(node.getNodeName().equalsIgnoreCase(ConfigConstants.PACKAGE_SCAN)){
					packagesToScan.add(node.getTextContent().trim());
				}
			}
		}
	}

	@Deprecated
	public static final void excute(){
		try {
			config.loadConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public final class ORM_CONFIG{
		private String userName;
		private String pwd;
		private String url;
		private String driver;
		private boolean genSchema;
		private boolean useAnnotations;
		private List<String> packagesToScan;
		private List<String> mappingFiles;
		private List<String> schemas;
		private List<String> queries;
		private boolean loggingEnabled;
		public ORM_CONFIG(String userName, String pwd, String url,boolean useAnnotations, List<String> packagesToScan,List<String> mappingFiles,List<String> schemas,List<String> queries) {
			super();
			this.userName = userName;
			this.pwd = pwd;
			this.url = url;
			this.useAnnotations = useAnnotations;
			this.packagesToScan = packagesToScan;
			this.mappingFiles = mappingFiles;
			this.schemas = schemas;
			this.queries = queries;
		}
		public String getUserName() {
			return userName;
		}
		public String getPwd() {
			return pwd;
		}
		public String getUrl() {
			return url;
		}
		public boolean isUseAnnotations() {
			return useAnnotations;
		}
		public List<String> getPackagesToScan() {
			return packagesToScan;
		}
		public List<String> getMappingFiles() {
			return mappingFiles;
		}
		public List<String> getSchemas() {
			return schemas;
		}
		public void setSchemas(List<String> schemas) {
			this.schemas = schemas;
		}
		public boolean isGenSchema() {
			return genSchema;
		}
		public void setGenSchema(boolean genSchema) {
			this.genSchema = genSchema;
		}
		public List<String> getQueries() {
			return queries;
		}
		public String getDriver() {
			return driver;
		}
		public void setDriver(String driver) {
			this.driver = driver;
		}
		public boolean isLoggingEnabled() {
			return loggingEnabled;
		}
		protected void setLoggingEnabled(boolean loggingEnabled) {
			this.loggingEnabled = loggingEnabled;
		}
		
		
	}
	public ORM_CONFIG getOrm_config() {
		return orm_config;
	}
}
