package com.sales.processes;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS.Maps;

public class MappingLoader implements Runnable {

	private ORM_MAPPINGS orm_mapping;
	private String fileLoc;

	public MappingLoader(ORM_MAPPINGS ormMapping, String fileLoc) {
		this.orm_mapping=ormMapping;
		this.fileLoc=fileLoc;
	}

	@Override
	public void run() {
		Document doc;
		try {
			doc = DocumentBuilderFactory.
			newInstance().
			newDocumentBuilder().
			parse(Thread.currentThread().
					getContextClassLoader()
					.getResourceAsStream(fileLoc));

			NodeList mappings=doc.getChildNodes().item(0).getChildNodes();//main node
			if(orm_mapping==null)
			//	orm_mapping=new ORM_MAPPINGS();	
			for(int i=0;i<mappings.getLength();i++){//iteratings mapping 
				if(mappings.item(i).getNodeType()==Node.ELEMENT_NODE){			
					ORM_MAPPINGS.Maps orm_maps=orm_mapping.new Maps();
					NodeList mapping=mappings.item(i).getChildNodes();
					for(int j=0;j<mapping.getLength();j++){
						if(mapping.item(j).getNodeType()==Node.ELEMENT_NODE){
							if(mapping.item(j).getNodeName().equalsIgnoreCase("MAP:Class"))
							//	orm_maps.setClassName(mapping.item(j).getTextContent());
							if(mapping.item(j).getNodeName().equalsIgnoreCase("MAP:RDBSchema"))
							//	orm_maps.setSchemaName(mapping.item(j).getTextContent());
							if(mapping.item(j).getNodeName().equalsIgnoreCase("MAP:Map")){
								mapAttributes(mapping.item(j),orm_maps);
							}
						}
					}
					orm_mapping.getMaps().put(orm_maps.getClassName(), orm_maps);
				}
			}
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void mapAttributes(Node item, Maps ormMaps) {
		NodeList mapEntries=item.getChildNodes();
		for(int i=0;i<mapEntries.getLength();i++){
			if(mapEntries.item(i).getNodeType()==Node.ELEMENT_NODE && mapEntries.item(i).getNodeName().equalsIgnoreCase("MAP:Property")){
				/*ormMaps.getAttributeMap().put(mapEntries.item(i).getAttributes().getNamedItem("name").getNodeValue(),
						mapEntries.item(i).getAttributes().getNamedItem("colName").getNodeValue());*/
			}
			if(mapEntries.item(i).getNodeType()==Node.ELEMENT_NODE && mapEntries.item(i).getNodeName().equalsIgnoreCase("MAP:one-2-one")){
				ormMaps.getDependentClassMap().put(mapEntries.item(i).getAttributes().getNamedItem("ref-class").getNodeValue()
						, orm_mapping.getMaps().get(mapEntries.item(i).getAttributes().getNamedItem("ref-class").getNodeValue()));
			}
		}
	}
}
