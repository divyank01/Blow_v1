package com.sales.poolable.parsers;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sales.constants.ConfigConstants;

public final class ORM_CONFIG_Parser {
	
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
		
		Document doc=DocumentBuilderFactory.
						newInstance().
						newDocumentBuilder().
						parse(Thread.currentThread().
								getContextClassLoader()
						        .getResourceAsStream("BLOW-ORM-CONFIG.xml"));
		NodeList nl=doc.getChildNodes().item(0).getChildNodes();

		for(int i=0;i<nl.getLength();i++){
			Node node=nl.item(i);
			if(node.getNodeType()==Node.ELEMENT_NODE){
				if(node.getNodeName().equalsIgnoreCase(ConfigConstants.URL))
					url=node.getTextContent();
				if(node.getNodeName().equalsIgnoreCase(ConfigConstants.PASSWORD))
					pwd=node.getTextContent();
				if(node.getNodeName().equalsIgnoreCase(ConfigConstants.USERNAME))
					userName=node.getTextContent();
				if(node.getNodeName().equalsIgnoreCase(ConfigConstants.MAPPINGS)){
					loadMappings(node.getAttributes().getNamedItem(ConfigConstants.MAPPINGS_FILE).getNodeValue());
				}
					
			}
		}
		orm_config=new ORM_CONFIG(userName, pwd, url);
	}
	
	private void loadMappings(String fileLoc) throws Exception{
		Document doc=DocumentBuilderFactory.
		newInstance().
		newDocumentBuilder().
		parse(Thread.currentThread().
				getContextClassLoader()
		        .getResourceAsStream(fileLoc));
		NodeList mappings=doc.getChildNodes().item(0).getChildNodes();
		for(int i=0;i<mappings.getLength();i++){
			
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
		
		
		public ORM_CONFIG(String userName, String pwd, String url) {
			super();
			this.userName = userName;
			this.pwd = pwd;
			this.url = url;
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
		private ORM_CONFIG(){}
		
		
	}
	public ORM_CONFIG getOrm_config() {
		return orm_config;
	}
}
