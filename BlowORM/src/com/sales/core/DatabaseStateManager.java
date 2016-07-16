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

import java.util.Iterator;

import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.DataBaseInfo;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.DataBaseInfo.Table;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.DataBaseInfo.Table.Column;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS.Maps;
import com.sales.poolable.parsers.ORM_MAPPINGS_Parser.ORM_MAPPINGS.Maps.Attributes;

public class DatabaseStateManager {

	private static DatabaseStateManager manager;
	
	private QuerryBuilder builder;
	private QuerryExecutor executor;
	private DatabaseStateManager(){}
	
	public static DatabaseStateManager getInstance(){
		if(manager==null)
			manager=new DatabaseStateManager();
		return manager;
	}
	/**
	 * Sync up the database structure with mappings
	 * create or alter tables and creates sequences
	 * which are required
	 * @param mappings
	 * @param info
	 * @throws Exception
	 */
	public void syncSchema(ORM_MAPPINGS mappings,DataBaseInfo info) throws Exception {
		Iterator<String> itr=mappings.getMaps().keySet().iterator();
		builder=QuerryBuilder.newInstance();
		executor=QuerryExecutor.getExecutor();
		String className;
		String schema;
		String query;
		Maps map;
		while(itr.hasNext()){
			className=itr.next();
			map=mappings.getMapForClass(className);
			schema=map.getSchemaName();
			Table table=info.getTables().get(schema.toUpperCase());
			if(table==null){
				/*
				 * create table-----full-------
				 */
				query=builder.createTableForClass(map);
				log(query);
				executor.executeStatment(query);
			}else if(table!=null){
			   /*
			    * check if all column are present
				* create function maps r better then for loop
				*/
				performTablesForModification(table, map);
			}
			/*
			 * check for the sequences
			 */
			Iterator<String> attrs=map.getAttributeMap().keySet().iterator();
			while(attrs.hasNext()){
				String attr=attrs.next();
				if(map.getAttributeMap().get(attr).isGenerated()){
					if(!info.getSequences().contains(map.getAttributeMap().get(attr).getSeqName().toUpperCase())){
						String seq=builder.createSequence(map.getAttributeMap().get(attr).getSeqName());
						log(seq);
						executor.executeStatment(seq);
					}
				}
			}
		}
		
	}
	
	private void performTablesForModification(Table table,Maps maps) throws Exception{
		Iterator<String> itr=maps.getAttributeMap().keySet().iterator();
		String query;
		while (itr.hasNext()) {
			String prop = itr.next();
			Attributes attr=maps.getAttributeMap().get(prop);
			if(!attr.isFk() && !attr.isReferenced()){
				Column col=table.getColumns().get(attr.getColName().toUpperCase());
				if(col==null){
					//alter table
					query=builder.alterAddColumn(attr,maps.getSchemaName());
					log(query);
					executor.executeStatment(query);
				}else{
					if(attr.getLength()>0 && attr.getLength()!=col.getLength()){
						//modify column
						query=builder.modifyColumn(attr,maps.getSchemaName());
						log(query);
						executor.executeStatment(query);
					}
				}
			}
		}
	}
	
}
